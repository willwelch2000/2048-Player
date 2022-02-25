import java.util.*;

public class Strategy2048 {
    private static Play2048 game = new Play2048(/*new int[][] {{64, 4, 0, 0}, {128, 8, 0, 0}, {256, 2, 0, 0}, {512, 16, 0, 0}}*/);
    private static Goal goal = new Get(game, 8, new int[] {3, 0}, new int[][] {});

    public static void main(String[] args) throws InterruptedException {
        System.out.println(game);
        System.out.println(goal);
        Thread.sleep(1000);
        Scanner in = new Scanner(System.in);
        while (!game.over()) {
            goal.action();

            if (goal.completed()) {
                goal = new Get(game, 2*game.highestNumber(), new int[] {3, 0}, new int[][] {});
            }

            if (goal.getForget()) {
                goal = new MoveHighest(game);
            }

            System.out.println(game);
            System.out.println(goal);
            Thread.sleep(500);
        }
        System.out.println(game.highestNumber());
        in.close();
    }

    // General functions to be used by multiple goals

    public static int bestMove(Play2048 game, int[][] desired, int cycles) {
        // Figures out the move that is most likely for it to be possible to reach the desired state after cycles number of moves

        double highestPercentage = 0;
        int bestMove = 0;
        int[] movePriority = new int[] {2, 3, 0, 1}; //In order of most likely what we need
        for (int move = 0; move < 4; move++) {
            Play2048 copy = new Play2048(game);
            copy.action(movePriority[move], false);
            double percentage = percentageOfSuccess(copy, desired, cycles - 1, !copy.equals(game));
            if (percentage > highestPercentage) {
                highestPercentage = percentage;
                bestMove = movePriority[move];
            }
            if (percentage == 1.0)
                return movePriority[move];
        }
        if (highestPercentage <= .1) {
            for (int i = 0; i < 4; i++)
                if (game.isPossibleMove(movePriority[i]))
                    return movePriority[i];
            Random random = new Random();
            return random.nextInt(4);
        } else
            return bestMove;
    }

    public static boolean combinable(int[][] gameBoard, int[] toLocation, int[] fromLocation, int[][] keepLocations) {
        //Tests if it would be easy to double toLocation if fromLocation were the same number

        gameBoard[fromLocation[0]][fromLocation[1]] = gameBoard[toLocation[0]][toLocation[1]];
        int[][] desiredBoard = new int[][] { { -1, -1, -1, -1 }, { -1, -1, -1, -1 }, { -1, -1, -1, -1 },
            { -1, -1, -1, -1 } };

        desiredBoard[fromLocation[0]][fromLocation[1]] *= 2;

        for (int i = 0; i < keepLocations.length; i++)
            desiredBoard[keepLocations[i][0]][keepLocations[i][1]] = gameBoard[keepLocations[i][0]][keepLocations[i][1]];

        Play2048 testGame = new Play2048(gameBoard);

        return easyToSolve(testGame, desiredBoard);
    }

    public static boolean easyToSolve(Play2048 game, int[][] desiredBoard) {
        //Says if the percentage of success is > 90% within 4 moves

        Play2048 copy = new Play2048(game);
        copy.action(Strategy2048.bestMove(copy, desiredBoard, 4), false);
        double percentage = Strategy2048.percentageOfSuccess(copy, desiredBoard, 3, !copy.equals(game));
        return percentage > .9;
    }

    public static boolean filled(Play2048 game, boolean startFromRight, int finalColumn) {
        Play2048 copy = new Play2048(game);
        Fill fill = new Fill(copy, startFromRight, finalColumn, new int[][] {});
        return fill.completed();
    }

    public static boolean equalsDesired(int[][] gameBoard, int[][] desiredBoard) {
        //Says if gameBoard matches desiredBoard
        // -1=anything
        // 1=anything besides 0
        // Rows 4 and 5 (optional)--signify rows/columns overall
        // Row 6 (optional)--signifies which columns should be filled (1 means filled)

        //Row 6 (can be 4 if 4/5 are omitted)
        if (desiredBoard.length == 5 || desiredBoard.length == 7) {
            int[] columnsToBeFilled = desiredBoard[desiredBoard.length - 1];
            boolean startFromRight;
            int finalColumn;
            if (columnsToBeFilled[0] == 1) {
                startFromRight = false;
                finalColumn = 3;
                for (int col = 1; col < 4; col++)
                    if (columnsToBeFilled[col] == 0) {
                        finalColumn = col - 1;
                        break;
                    }
            } else {
                startFromRight = true;
                finalColumn = 0;
                for (int col = 2; col > -1; col--)
                    if (columnsToBeFilled[col] == 0) {
                        finalColumn = col + 1;
                        break;
                    }
            }
            if (!Strategy2048.filled(game, startFromRight, finalColumn))
                return false;
        }

        //Rows 4 and 5
        if (desiredBoard.length > 5) {
            // Manage overall rows
            for (int row = 0; row < 4; row++) {
                int encodeRow = desiredBoard[4][row];
                if (encodeRow == -1)
                    continue;
                boolean negative = encodeRow < 0;
                if (negative)
                    encodeRow *= -1;
                int desiredNumber = 0;
                int desiredOccurrences = 0;
                for (int i = 1; i < 5; i++) {
                    if (isPowerOfTwo(encodeRow - 10 * i)) {
                        desiredOccurrences = i;
                        desiredNumber = encodeRow - 10 * i;
                        if (negative)
                            desiredNumber *= -1;
                        break;
                    }
                }
                int occurrences = 0;
                for (int j = 0; j < 4; j++)
                    if (spaceEqualsDesired(gameBoard[row][j], desiredNumber))
                        occurrences++;
                if (occurrences < desiredOccurrences)
                    return false;
            }

            // Manage overall columns
            for (int column = 0; column < 4; column++) {
                int encodeColumn = desiredBoard[5][column];
                if (encodeColumn == -1)
                    continue;
                boolean negative = encodeColumn < 0;
                if (negative)
                    encodeColumn *= -1;
                int desiredNumber = 0;
                int desiredOccurrences = 0;
                for (int i = 1; i < 5; i++) {
                    if (isPowerOfTwo(encodeColumn - 10 * i)) {
                        desiredOccurrences = i;
                        desiredNumber = encodeColumn - 10 * i;
                        if (negative)
                            desiredNumber *= -1;
                        break;
                    }
                }
                int occurrences = 0;
                for (int j = 0; j < 4; j++)
                    if (spaceEqualsDesired(gameBoard[j][column], desiredNumber))
                        occurrences++;
                if (occurrences < desiredOccurrences)
                    return false;
            }
        }

        // Individual pieces
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                if (!spaceEqualsDesired(gameBoard[i][j], desiredBoard[i][j]))
                    return false;
        return true;
    }

    public static boolean lockedInPlaceVertically(int[][] gameBoard, int[] location) {
        //Returns true if a space is either on the edge or in the middle but locked in by a lower edge space

        int row = location[0];
        int column = location[1];
        int currentValue = gameBoard[row][column];
        if (row == 0 || row == 3)
            return true;
        else if (row == 1)
            if (gameBoard[0][column] != 0 && gameBoard[0][column] != currentValue)
                return true;
            else
                return false;
        else //Row 2
            if (gameBoard[3][column] != 0 && gameBoard[3][column] != currentValue)
                return true;
            else
                return false;

    }

    public static int numberOfSmallerSurroundingSpaces(int[][] gameBoard, int[] location, List<int[]> repeatSpaces) {
        //Says if a space on the board is locked in by larger squares
        int row = location[0];
        int column = location[1];
        int currentValue = gameBoard[location[0]][location[1]];

        //Create list of all spaces with a lower value than this--exclude repeat spaces--if one of them is a 0, replace it with the current value--blank space shouldn't hamper
        List<int[]> smallerAdjacentSpaces = new ArrayList<>();
        if (row > 0 && gameBoard[row - 1][column] <= currentValue) {
            smallerAdjacentSpaces.add(new int[] {row - 1, column});
            if (gameBoard[row - 1][column] == 0)
                gameBoard[row - 1][column] = currentValue;
        }
        if (row < 3 && gameBoard[row + 1][column] <= currentValue) {
            smallerAdjacentSpaces.add(new int[] {row + 1, column});
            if (gameBoard[row + 1][column] == 0)
                gameBoard[row + 1][column] = currentValue;
        }
        if (column > 0 && gameBoard[row][column - 1] <= currentValue) {
            smallerAdjacentSpaces.add(new int[] {row, column - 1});
            if (gameBoard[row][column - 1] == 0)
                gameBoard[row][column - 1] = currentValue;
        }
        if (column < 3 && gameBoard[row][column + 1] <= currentValue) {
            smallerAdjacentSpaces.add(new int[] {row, column + 1});
            if (gameBoard[row][column + 1] == 0)
                gameBoard[row][column + 1] = currentValue;
        }

        //Get rid of any spaces that are the same location as a repeat space
        List<int[]> toRemove = new ArrayList<>();
        for (int i = 0; i < smallerAdjacentSpaces.size(); i++)
            for (int j = 0; j < repeatSpaces.size(); j++)
                if (smallerAdjacentSpaces.get(i)[0] == repeatSpaces.get(j)[0] && smallerAdjacentSpaces.get(i)[1] == repeatSpaces.get(j)[1])
                    toRemove.add(smallerAdjacentSpaces.get(i));
        smallerAdjacentSpaces.removeAll(toRemove);

        int smallerSurroundingNumber = smallerAdjacentSpaces.size();

        //Add old repeat spaces, this space, and all of the smaller surrounding spaces to repeatSpaces
        repeatSpaces.add(location);
        repeatSpaces.addAll(smallerAdjacentSpaces);

        //Add number (recursive) of all smaller adjacents to the overall number
        for (int i = 0; i < smallerAdjacentSpaces.size(); i++)
            smallerSurroundingNumber += numberOfSmallerSurroundingSpaces(gameBoard, smallerAdjacentSpaces.get(i), repeatSpaces);

        return smallerSurroundingNumber;
    }

    public static double percentageOfSuccess(Play2048 testGame, int[][] desired, int cyclesLeft, boolean hasChanged) {
        //Gives likelihood of reaching desired board, considering the random adding of tiles, within cyclesLeft number of moves

        // Requires that the random tile hasn't yet been added--this will add it--this must be called in the middle of a move (before the random tile)
        // Has a boolean of if the board has changed or not--usually make this argument !copy.equals(testGame)

        //If we have reached success--100%
        if (equalsDesired(testGame.getBoard(), desired))
            return 1;

        //If the game is over, if the cycles are out, or if nothing has changed--0%
        if (testGame.over() || cyclesLeft == 0 || !hasChanged)
            return 0;

        // Otherwise check all 4 moves (this is where we do run multiple tests) and return highest (recursive)
        double highestPercentage = 0;
        int[] movePriority = new int[] {2, 3, 0, 1}; //In order of most likely what we need
        for (int move = 0; move < 4; move++) {
            double percentage = 0;
            for (int cycle = 0; cycle < 14; cycle++) {
                //Perform the random tile generation from last move, and then do a move without the random tile being added
                Play2048 copy = new Play2048(testGame);
                copy.addTile(); // If we shouldn't add tile, it will return 0 above, so here should always add tile
                copy.action(movePriority[move], false);
                // Here, I'm thinking about subtracting one from the total amount of cycles if the move does nothing--divide by that number after this loop
                percentage += .99*percentageOfSuccess(copy, desired, cyclesLeft - 1, !copy.equals(testGame)) / 14; //The .99 helps make sure we do the move that takes the least amount of moves to get there in bestMove
            }
            if (percentage > highestPercentage)
                highestPercentage = percentage;
        }
        return highestPercentage;
    }

    public static boolean workingDown(Play2048 game, int column) {
        //Says if a given column is working down as opposed to up

        int[][] board = game.getBoard();

        // Find largest space in column
        int largestSpaceValue = -1;
        int largestSpaceRow = -1;
        for (int i = 0; i < 4; i++)
            if (board[i][column] > largestSpaceValue) {
                largestSpaceValue = board[i][column];
                largestSpaceRow = i;
            }

        if (largestSpaceRow < 2)
            return true;
        return false;
    }

    private static boolean isPowerOfTwo(int n) {
        if (n <= 0)
            return false;

        while (n > 2) {
            int t = n >> 1;
            int c = t << 1;

            if (n - c != 0)
                return false;

            n = n >> 1;
        }

        return true;
    }

    private static boolean spaceEqualsDesired(int spaceNumber, int desiredNumber) {
        if (desiredNumber == -1)
            return true;
        else if (desiredNumber == 1)
            if (spaceNumber != 0)
                return true;
            else
                return false;
        else if (desiredNumber < 0)
            return spaceNumber != -1 * desiredNumber;
        else
            return spaceNumber == desiredNumber;
    }
}