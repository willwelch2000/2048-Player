import java.util.Random;

public class Play2048 {
    private int[][] board = new int[4][4]; //Row, column--rows go down, columns go right

    public Play2048() {
        addTile();
        addTile();
    }

    public Play2048(Play2048 other) {
        //Creates object, copying another
        int[][] otherBoard = other.board;
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                board[i][j] = otherBoard[i][j];
    }

    public Play2048(int[][] otherBoard) {
        //Creates object based on an input matrix
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                board[i][j] = otherBoard[i][j];
    }

    public boolean addTile() {
        //Adds a tile randomly; returns false if impossible
        boolean filled = true;
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                if (board[i][j] == 0)
                    filled = false;
        if (filled)
            return false;

        Random random = new Random();
        int locationChoice = random.nextInt(16);
        while (board[locationChoice / 4][locationChoice % 4] != 0)
            locationChoice = random.nextInt(16);

        int numberChoice = Math.random() < 0.9 ? 2 : 4;

        board[locationChoice / 4][locationChoice % 4] = numberChoice;

        return true;
    }

    public boolean empty(int[] space) {
        //Returns true if inputed space is empty
        return board[space[0]][space[1]] == 0;
    }

    public boolean equals(Play2048 other) {
        //Returns true if this game is the same as another
        int[][] otherBoard = other.board;
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                if (board[i][j] != otherBoard[i][j])
                    return false;
        return true;
    }

    public boolean isPossibleMove(int direction) {
        Play2048 copy = new Play2048(this);
        copy.action(direction);
        return !equals(copy);
    }

    public boolean over() {
        //Returns true if game is over
        Play2048 copy = new Play2048(this);
        copy.left();
        if (!equals(copy))
            return false;
        copy.right();
        if (!equals(copy))
            return false;
        copy.up();
        if (!equals(copy))
            return false;
        copy.down();
        if (!equals(copy))
            return false;
        return true;
    }

    public void action(int direction, boolean addATile) {
        //Performs action, maybe adds tile
        Play2048 copy = new Play2048(this);
        if (over())
            return;
        if (direction == 0)
            up();
        else if (direction == 1)
            right();
        else if (direction == 2)
            down();
        else if (direction == 3)
            left();
        if ((!equals(copy) && addATile) || direction == -1)
            addTile();
    }

    public void action(int direction) {
        //Performs action and assumes that it should add a tile
        action(direction, true);
    }

    private void left() {
        //Moves to the left
        boolean[][] combined = new boolean[4][4];
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                combined[i][j] = false;

        //Column 0

        //Column 1
        for (int i = 0; i < 4; i++) {
            if (board[i][1] == 0)
                continue;
            if (board[i][0] == 0) {
                board[i][0] = board[i][1];
                board[i][1] = 0;
            }
            else if (board[i][0] == board[i][1] && !combined[i][0]) {
                board[i][0] *= 2;
                board[i][1] = 0;
                combined[i][0] = true;
            }
        }

        //Column 2
        for (int i = 0; i < 4; i++) {
            if (board[i][2] == 0)
                continue;
            if (board[i][1] == 0) {
                board[i][1] = board[i][2];
                board[i][2] = 0;
                if (board[i][0] == 0) {
                    board[i][0] = board[i][1];
                    board[i][1] = 0;
                }
                else if (board[i][0] == board[i][1] && !combined[i][0]) {
                    board[i][0] *= 2;
                    board[i][1] = 0;
                    combined[i][0] = true;
                }
            }
            else if (board[i][1] == board[i][2] && !combined[i][1]) {
                board[i][1] *= 2;
                board[i][2] = 0;
                combined[i][1] = true;
            }
        }

        //Column 3
        for (int i = 0; i < 4; i++) {
            if (board[i][3] == 0)
                continue;
            if (board[i][2] == 0) {
                board[i][2] = board[i][3];
                board[i][3] = 0;
                if (board[i][1] == 0) {
                    board[i][1] = board[i][2];
                    board[i][2] = 0;
                    if (board[i][0] == 0) {
                        board[i][0] = board[i][1];
                        board[i][1] = 0;
                    }
                    else if (board[i][0] == board[i][1] && !combined[i][0]) {
                        board[i][0] *= 2;
                        board[i][1] = 0;
                        combined[i][0] = true;
                    }
                }
                else if (board[i][1] == board[i][2] && !combined[i][1]) {
                    board[i][1] *= 2;
                    board[i][2] = 0;
                    combined[i][1] = true;
                }
            }
            else if (board[i][2] == board[i][3] && !combined[i][2]) {
                board[i][2] *= 2;
                board[i][3] = 0;
                combined[i][2] = true;
            }
        }
    }

    private void right() {
        //Moves right
        boolean[][] combined = new boolean[4][4];
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                combined[i][j] = false;

        //Column 3

        //Column 2
        for (int i = 0; i < 4; i++) {
            if (board[i][2] == 0)
                continue;
            if (board[i][3] == 0) {
                board[i][3] = board[i][2];
                board[i][2] = 0;
            }
            else if (board[i][3] == board[i][2] && !combined[i][3]) {
                board[i][3] *= 2;
                board[i][2] = 0;
                combined[i][3] = true;
            }
        }

        //Column 1
        for (int i = 0; i < 4; i++) {
            if (board[i][1] == 0)
                continue;
            if (board[i][2] == 0) {
                board[i][2] = board[i][1];
                board[i][1] = 0;
                if (board[i][3] == 0) {
                    board[i][3] = board[i][2];
                    board[i][2] = 0;
                }
                else if (board[i][3] == board[i][2] && !combined[i][3]) {
                    board[i][3] *= 2;
                    board[i][2] = 0;
                    combined[i][3] = true;
                }
            }
            else if (board[i][2] == board[i][1] && !combined[i][2]) {
                board[i][2] *= 2;
                board[i][1] = 0;
                combined[i][2] = true;
            }
        }

        //Column 0
        for (int i = 0; i < 4; i++) {
            if (board[i][0] == 0)
                continue;
            if (board[i][1] == 0) {
                board[i][1] = board[i][0];
                board[i][0] = 0;
                if (board[i][2] == 0) {
                    board[i][2] = board[i][1];
                    board[i][1] = 0;
                    if (board[i][3] == 0) {
                        board[i][3] = board[i][2];
                        board[i][2] = 0;
                    }
                    else if (board[i][3] == board[i][2] && !combined[i][3]) {
                        board[i][3] *= 2;
                        board[i][2] = 0;
                        combined[i][3] = true;
                    }
                }
                else if (board[i][2] == board[i][1] && !combined[i][2]) {
                    board[i][2] *= 2;
                    board[i][1] = 0;
                    combined[i][2] = true;
                }
            }
            else if (board[i][1] == board[i][0] && !combined[i][1]) {
                board[i][1] *= 2;
                board[i][0] = 0;
                combined[i][1] = true;
            }
        }
    }

    private void up() {
        //Moves up
        boolean[][] combined = new boolean[4][4];
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                combined[i][j] = false;

        //Row 0

        //Row 1
        for (int i = 0; i < 4; i++) {
            if (board[1][i] == 0)
                continue;
            if (board[0][i] == 0) {
                board[0][i] = board[1][i];
                board[1][i] = 0;
            }
            else if (board[0][i] == board[1][i] && !combined[0][i]) {
                board[0][i] *= 2;
                board[1][i] = 0;
                combined[0][i] = true;
            }
        }

        //Row 2
        for (int i = 0; i < 4; i++) {
            if (board[2][i] == 0)
                continue;
            if (board[1][i] == 0) {
                board[1][i] = board[2][i];
                board[2][i] = 0;
                if (board[0][i] == 0) {
                    board[0][i] = board[1][i];
                    board[1][i] = 0;
                }
                else if (board[0][i] == board[1][i] && !combined[0][i]) {
                    board[0][i] *= 2;
                    board[1][i] = 0;
                    combined[0][i] = true;
                }
            }
            else if (board[1][i] == board[2][i] && !combined[1][i]) {
                board[1][i] *= 2;
                board[2][i] = 0;
                combined[1][i] = true;
            }
        }

        //Row 3
        for (int i = 0; i < 4; i++) {
            if (board[3][i] == 0)
                continue;
            if (board[2][i] == 0) {
                board[2][i] = board[3][i];
                board[3][i] = 0;
                if (board[1][i] == 0) {
                    board[1][i] = board[2][i];
                    board[2][i] = 0;
                    if (board[0][i] == 0) {
                        board[0][i] = board[1][i];
                        board[1][i] = 0;
                    }
                    else if (board[0][i] == board[1][i] && !combined[0][i]) {
                        board[0][i] *= 2;
                        board[1][i] = 0;
                        combined[0][i] = true;
                    }
                }
                else if (board[1][i] == board[2][i] && !combined[1][i]) {
                    board[1][i] *= 2;
                    board[2][i] = 0;
                    combined[1][i] = true;
                }
            }
            else if (board[2][i] == board[3][i] && !combined[2][i]) {
                board[2][i] *= 2;
                board[3][i] = 0;
                combined[2][i] = true;
            }
        }
    }

    private void down() {
        //Moves down
        boolean[][] combined = new boolean[4][4];
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                combined[i][j] = false;

        //Row 3

        //Row 2
        for (int i = 0; i < 4; i++) {
            if (board[2][i] == 0)
                continue;
            if (board[3][i] == 0) {
                board[3][i] = board[2][i];
                board[2][i] = 0;
            }
            else if (board[3][i] == board[2][i] && !combined[3][i]) {
                board[3][i] *= 2;
                board[2][i] = 0;
                combined[3][i] = true;
            }
        }

        //Row 1
        for (int i = 0; i < 4; i++) {
            if (board[1][i] == 0)
                continue;
            if (board[2][i] == 0) {
                board[2][i] = board[1][i];
                board[1][i] = 0;
                if (board[3][i] == 0) {
                    board[3][i] = board[2][i];
                    board[2][i] = 0;
                }
                else if (board[3][i] == board[2][i] && !combined[3][i]) {
                    board[3][i] *= 2;
                    board[2][i] = 0;
                    combined[3][i] = true;
                }
            }
            else if (board[2][i] == board[1][i] && !combined[2][i]) {
                board[2][i] *= 2;
                board[1][i] = 0;
                combined[2][i] = true;
            }
        }

        //Row 0
        for (int i = 0; i < 4; i++) {
            if (board[0][i] == 0)
                continue;
            if (board[1][i] == 0) {
                board[1][i] = board[0][i];
                board[0][i] = 0;
                if (board[2][i] == 0) {
                    board[2][i] = board[1][i];
                    board[1][i] = 0;
                    if (board[3][i] == 0) {
                        board[3][i] = board[2][i];
                        board[2][i] = 0;
                    }
                    else if (board[3][i] == board[2][i] && !combined[3][i]) {
                        board[3][i] *= 2;
                        board[2][i] = 0;
                        combined[3][i] = true;
                    }
                }
                else if (board[2][i] == board[1][i] && !combined[2][i]) {
                    board[2][i] *= 2;
                    board[1][i] = 0;
                    combined[2][i] = true;
                }
            }
            else if (board[1][i] == board[0][i] && !combined[1][i]) {
                board[1][i] *= 2;
                board[0][i] = 0;
                combined[1][i] = true;
            }
        }
    }

    public int emptySpaces() {
        //Returns number of empty spaces
        int count = 0;
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                if (board[i][j] == 0)
                    count++;
        return count;
    }

    public int highestNumber() {
        //Returns highest number on board
        int highestNumber = 0;
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                if (board[i][j] > highestNumber)
                    highestNumber = board[i][j];
        return highestNumber;
    }

    public int[][] findLocation(int number) {
        //Returns an array of the locations with the desired number
        int count = 0;
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                if (board[i][j] == number)
                    count++;
        int[][] list = new int[count][2];
        count = 0;
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                if (board[i][j] == number) {
                    list[count][0] = i;
                    list[count][1] = j;
                    count++;
                }
        return list;
    }

    public int[][] getBoard() {
        //Returns board as a matrix
        int[][] toReturn = new int[4][4];
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                toReturn[i][j] = board[i][j];
        return toReturn;
    }

    public String toString() {
        //Returns game as a string
        String toReturn = "+------+------+------+------+\n";
        for (int i = 0; i < 4; i++) {
            toReturn += "|      |      |      |      |\n|";
            for (int j = 0; j < 4; j++)
                if (board[i][j] == 0)
                    toReturn += "      |";
                else
                    toReturn += String.format("%6d", board[i][j]) + "|";
            toReturn += "\n|      |      |      |      |\n+------+------+------+------+\n";
        }
        return toReturn;
    }
}