import java.util.*;

class Get extends Goal {
    private int[][] keepLocations;
    private int[] keepValues;
    private int currentValue;

    public Get(Play2048 game, int number, int[] desiredLocation, int[][] keepLocations) {
        super(game, 3, number, desiredLocation, false);
        this.keepLocations = keepLocations;
        keepValues = new int[keepLocations.length];
        currentValue = board[desiredLocation[0]][desiredLocation[1]];
        for (int i = 0; i < keepLocations.length; i++)
            keepValues[i] = board[keepLocations[i][0]][keepLocations[i][1]];
        if (!completed())
            addSecondary();
    }

    public void finalAction() {
        //Nothing here because this is made entirely of secondary goals
    }

    public boolean completed() {
        //Doesn't care about keepLocations
        return board[desiredLocation[0]][desiredLocation[1]] >= number;
    }

    public int[][] fixedBoard() {
        int[][] toReturn = new int[][] { { -1, -1, -1, -1 }, { -1, -1, -1, -1 }, { -1, -1, -1, -1 },
                { -1, -1, -1, -1 } };
        //Adjust for keep locations
        for (int i = 0; i < keepLocations.length; i++)
            toReturn[keepLocations[i][0]][keepLocations[i][1]] = keepValues[i];
        //Make current space have current value--doesn't go down
        toReturn[desiredLocation[0]][desiredLocation[1]] = currentValue;
        return toReturn;
    }

    public int[][] desiredBoard() {
        int[][] toReturn;
        toReturn = new int[][] { { -1, -1, -1, -1 }, { -1, -1, -1, -1 }, { -1, -1, -1, -1 },
                { -1, -1, -1, -1 }};
        //Put down number at location
        if (currentValue < 8)
            toReturn[desiredLocation[0]][desiredLocation[1]] = 8;
        else
            toReturn[desiredLocation[0]][desiredLocation[1]] = number;
        // Adjust for keep locations
        for (int i = 0; i < keepLocations.length; i++)
            toReturn[keepLocations[i][0]][keepLocations[i][1]] = keepValues[i];
        return toReturn;
    }

    private int[][] bestBoard() {
        int[][] toReturn;
        //Adjust for staying filled
        if (desiredLocation[1] == 1)
            toReturn = new int[][] { { -1, -1, -1, -1 }, { -1, -1, -1, -1 }, { -1, -1, -1, -1 },
                    { -1, -1, -1, -1 }, {1, 0, 0, 0}};
        else if (desiredLocation[1] > 1)
            toReturn = new int[][] { { -1, -1, -1, -1 }, { -1, -1, -1, -1 }, { -1, -1, -1, -1 },
                    { -1, -1, -1, -1 }, {1, 1, 0, 0}};
        else
            toReturn = new int[][] { { -1, -1, -1, -1 }, { -1, -1, -1, -1 }, { -1, -1, -1, -1 },
                    { -1, -1, -1, -1 }};
        //Put down number at location
        if (currentValue < 8)
            toReturn[desiredLocation[0]][desiredLocation[1]] = 8;
        else
            toReturn[desiredLocation[0]][desiredLocation[1]] = number;
        // Adjust for keep locations
        for (int i = 0; i < keepLocations.length; i++)
            toReturn[keepLocations[i][0]][keepLocations[i][1]] = keepValues[i];
        return toReturn;
    }

    public void reevaluate() {
        //Major note--to replace/add secondaries, just clear all of them--at the end, it will add secondaries if empty

        // Forget if keepLocations messed up
        for (int i = 0; i < keepLocations.length; i++)
            if (board[keepLocations[i][0]][keepLocations[i][1]] != keepValues[i])
                forget = true;
        // Forget if number has gotten lower (moved)
        if (board[desiredLocation[0]][desiredLocation[1]] < currentValue && currentValue >= 8)
            forget = true;
        //Forget if number is too high
        if (board[desiredLocation[0]][desiredLocation[1]] > number)
            forget = true;

        if (forget)
            return;

        // Forgotten secondary goals (only look at first goal)--delete them--will add new ones at end
        if (secondaryGoals.size() > 0 && secondaryGoals.get(0).getForget())
            while (!secondaryGoals.isEmpty())
                secondaryGoals.remove(0);

        //Figure out last secondary in chain (first with finalAction)
        Goal firstActionSecondary = this;
        while (firstActionSecondary.secondaryGoals.size() > 0)
            firstActionSecondary = firstActionSecondary.secondaryGoals.get(0);

        //Planned changes to continue if we've advanced but haven't completed--we only do one or two goals at a time--this updates them if necessary
        if (board[desiredLocation[0]][desiredLocation[1]] > currentValue) {
            currentValue = board[desiredLocation[0]][desiredLocation[1]];
            if (currentValue >= 8 && !(firstActionSecondary.type == 1 && firstActionSecondary.number > 8))
                while (!secondaryGoals.isEmpty())
                    secondaryGoals.remove(0);
        }

        //Easy to advance, but not correct Combine and not fill--reset by clearing
        int highestEasyNumber = highestEasyNumber();
        if (highestEasyNumber > currentValue && !(secondaryGoals.size() == 1 && secondaryGoals.get(0).type == 1 && secondaryGoals.get(0).number == highestEasyNumber) && !(secondaryGoals.size() == 1 && secondaryGoals.get(0).type == 2))
            while (!secondaryGoals.isEmpty())
                secondaryGoals.remove(0);
        //Should be filled, but first secondary with a final action isn't a fill or a combine > 8--reset by clearing
        if (secondaryGoals.size() > 0) {
            if (desiredLocation[1] == 0 && !Strategy2048.filled(game, false, 0) && !(firstActionSecondary.type == 2 || (firstActionSecondary.type == 1 && firstActionSecondary.number > 8)))
                while (!secondaryGoals.isEmpty())
                    secondaryGoals.remove(0);
            else if (desiredLocation[1] == 1 && !Strategy2048.filled(game, false, 1) && !(firstActionSecondary.type == 2 || (firstActionSecondary.type == 1 && firstActionSecondary.number > 8)))
                while (!secondaryGoals.isEmpty())
                    secondaryGoals.remove(0);
            else if (desiredLocation[1] > 1 && !Strategy2048.filled(game, false, 2) && !(firstActionSecondary.type == 2 || (firstActionSecondary.type == 1 && firstActionSecondary.number > 8)))
                while (!secondaryGoals.isEmpty())
                    secondaryGoals.remove(0);
        }

        if (!forget && secondaryGoals.size() == 0)
            addSecondary();
    }

    public String description() {
        String toReturn = "Get: " + desiredLocation[0] + ", " + desiredLocation[1] + ": " + number + " Keep: ";
        for (int i = 0; i < keepLocations.length; i++)
            toReturn += keepLocations[i][0] + ", " + keepLocations[i][1] + "; ";
        return toReturn;
    }

    private void addSecondary() {
        //Adjusts secondaries--must be called with no current secondaries--clear before calling

        //Nothing if completed
        if (completed())
            return;
        
        //If it isn't filled, add goal to fill up the column(s)
        if (desiredLocation[1] == 0 && !Strategy2048.filled(game, false, 0)) {
            secondaryGoals.add(new Fill(game, false, 0, keepLocations));
            return;
        }
        if (desiredLocation[1] == 1 && !Strategy2048.filled(game, false, 1)) {
            secondaryGoals.add(new Fill(game, false, 1, keepLocations));
            return;
        }
        if (desiredLocation[1] > 1 && !Strategy2048.filled(game, false, 2)) {
            secondaryGoals.add(new Fill(game, false, 2, keepLocations));
            return;
        }

        //If the highest number that's easy to get is an increase, set a combine for that
        int highestEasyNumber = highestEasyNumber();
        if (highestEasyNumber > currentValue) {
            secondaryGoals.add(new Combine(game, highestEasyNumber, desiredLocation, keepLocations, true));
            return;
        }

        //Create correct secondaries: get/combine (if >= 8) or combine (if < 8) to double the current space
        if (currentValue >= 8) {
            //Add current space to keepLocations for secondary get
            int[][] newKeepLocations = new int[keepLocations.length + 1][2];
            for (int j = 0; j < keepLocations.length; j++)
                newKeepLocations[j] = keepLocations[j];
            newKeepLocations[keepLocations.length] = desiredLocation;

            secondaryGoals.add(new Get(game, currentValue, nextSpace(), newKeepLocations));
            secondaryGoals.add(new Combine(game, 2*currentValue, desiredLocation, keepLocations, false));
            return;
        }
        secondaryGoals.add(new Combine(game, 8, desiredLocation, keepLocations, false));
    }

    private int highestEasyNumber() {
        //Tries every number, starting at final goal and working our way down, to see if it's easy--returns highest one
        int numberToTry = number;
        int[][] modifiedDesiredBoard = bestBoard();
        while (numberToTry > 8) {
            modifiedDesiredBoard[desiredLocation[0]][desiredLocation[1]] = numberToTry;
            if (Strategy2048.easyToSolve(game, modifiedDesiredBoard) && currentValue >= 8) {
                return numberToTry;
            }
            numberToTry = numberToTry/2;
        }
        return -1;
    }

    public int[] nextSpace() {
        final int firstColumnLimit = 128;
        final int secondColumnLimit = 16;
        final int firstColumnMustGoUp = 1024;
        int row = desiredLocation[0];
        int column = desiredLocation[1];
        int currentValue = board[row][column];
        boolean workingDown = Strategy2048.workingDown(game, column);
        int[][] possibleNextSpaces;

        //Choose list of next spaces
        if (column == 0)
            if (row == 0)
                possibleNextSpaces = new int[][] {{0, 1}, {1, 0}};
            else if (row == 1)
                if (currentValue < firstColumnLimit)
                    possibleNextSpaces = new int[][] {{3, 1}, {0, 0}, {1, 1}, {2, 1}};
                else
                    possibleNextSpaces = new int[][] {{0, 0}, {3, 1}, {1, 1}, {2, 1}};
            else if (row == 2)
                if (currentValue < firstColumnLimit)
                    possibleNextSpaces = new int[][] {{3, 1}, {1, 0}, {2, 1}, {1, 1}};
                else
                    possibleNextSpaces = new int[][] {{1, 0}, {3, 1}, {2, 1}, {1, 1}};
            else
                if (currentValue < firstColumnLimit)
                    possibleNextSpaces = new int[][] {{3, 1}, {2, 0}};
                else
                    if (currentValue >= firstColumnMustGoUp)
                        possibleNextSpaces = new int[][] {{2, 0}};
                    else
                        possibleNextSpaces = new int[][] {{2, 0}, {3, 1}};

        else if (column == 1)
            if (row == 0)
                if (currentValue < secondColumnLimit)
                    if (workingDown)
                        possibleNextSpaces = new int[][] {{0, 2}, {1, 1}};
                    else
                        possibleNextSpaces = new int[][] {{0, 2}};
                else
                    if (workingDown)
                        possibleNextSpaces = new int[][] {{1, 1}, {0, 2}};
                    else
                        possibleNextSpaces = new int[][] {{0, 2}};
            else if (row == 1)
                if (currentValue < secondColumnLimit)
                    if (workingDown)
                        possibleNextSpaces = new int[][] {{3, 2}, {2, 1}, {1, 2}, {2, 2}};
                    else
                        possibleNextSpaces = new int[][] {{3, 2}, {0, 1}, {1, 2}, {2, 2}};
                else
                    if (workingDown)
                        possibleNextSpaces = new int[][] {{2, 1}, {3, 2}, {1, 2}, {2, 2}};
                    else
                        possibleNextSpaces = new int[][] {{0, 1}, {3, 2}, {1, 2}, {2, 2}};
            else if (row == 2)
                if (currentValue < secondColumnLimit)
                    if (workingDown)
                        possibleNextSpaces = new int[][] {{3, 2}, {3, 1}, {2, 2}, {1, 2}};
                    else
                        possibleNextSpaces = new int[][] {{3, 2}, {1, 1}, {2, 2}, {1, 2}};
                else
                    if (workingDown)
                        possibleNextSpaces = new int[][] {{3, 1}, {3, 2}, {2, 2}, {1, 2}};
                    else
                        possibleNextSpaces = new int[][] {{1, 1}, {3, 2}, {2, 2}, {1, 2}};
            else
                if (currentValue < secondColumnLimit)
                    if (workingDown)
                        possibleNextSpaces = new int[][] {{3, 2}};
                    else
                        possibleNextSpaces = new int[][] {{3, 2}, {2, 1}};
                else
                    if (workingDown)
                        possibleNextSpaces = new int[][] {{3, 2}};
                    else
                        possibleNextSpaces = new int[][] {{2, 1}, {3, 2}};

        else if (column == 2)
            if (row == 0)
                if (workingDown)
                    possibleNextSpaces = new int[][] {{1, 2}, {0, 3}};
                else
                    possibleNextSpaces = new int[][] {{0, 3}};
            else if (row == 1)
                if (workingDown)
                    possibleNextSpaces = new int[][] {{2, 2}, {3, 3}, {1, 3}, {2, 3}};
                else
                    possibleNextSpaces = new int[][] {{0, 2}, {3, 3}, {1, 3}, {2, 3}};
            else if (row == 2)
                if (workingDown)
                    possibleNextSpaces = new int[][] {{3, 2}, {3, 3}, {2, 3}, {1, 3}};
                else
                    possibleNextSpaces = new int[][] {{1, 2}, {3, 3}, {2, 3}, {1, 3}};
            else
                if (workingDown)
                    possibleNextSpaces = new int[][] {{3, 3}};
                else
                    possibleNextSpaces = new int[][] {{2, 2}, {3, 3}};

        else
            if (workingDown && !(row == 3))
                possibleNextSpaces = new int[][] {{row + 1, column}};
            else
                possibleNextSpaces = new int[][] {{row - 1, column}};

        //Make a list of the options in decreasing number order--exclude ones that are too high
        List<int[]> largestOptions = new ArrayList<>();
        int numberToTry = currentValue;
        while (numberToTry >= 0) {
            for (int i = 0; i < possibleNextSpaces.length; i++)
                if (board[possibleNextSpaces[i][0]][possibleNextSpaces[i][1]] == numberToTry)
                    largestOptions.add(possibleNextSpaces[i]);
            if (numberToTry == 2)
                numberToTry = 0;
            else if (numberToTry == 0)
                numberToTry = -1;
            else
                numberToTry /= 2;
        }

        //If any is equal to the current space and combinable, choose that one
        for (int i = 0; i < possibleNextSpaces.length; i++) {
            int[] testNextSpace = possibleNextSpaces[i];
            if (board[testNextSpace[0]][testNextSpace[1]] == currentValue && Strategy2048.combinable(board, desiredLocation, testNextSpace, keepLocations)) {
                // System.out.println("equal, combinable");
                return testNextSpace;
            }
        }

        //High (but not too high), not crowded by bigger pieces, locked in place vertically
        for (int i = 0; i < possibleNextSpaces.length; i++) {
            int[] testNextSpace = possibleNextSpaces[i];
            int nextValue = board[testNextSpace[0]][testNextSpace[1]];
            if (nextValue > currentValue/4 && nextValue <= currentValue && Strategy2048.numberOfSmallerSurroundingSpaces(board, testNextSpace, new ArrayList<int[]>()) > 2 && Strategy2048.lockedInPlaceVertically(board, testNextSpace)) {
                // System.out.println("high, not crowded, locked in");
                return testNextSpace;
            }
        }

        //Not too high, not crowded by bigger pieces, locked in place vertically
        for (int i = 0; i < possibleNextSpaces.length; i++) {
            int[] testNextSpace = possibleNextSpaces[i];
            int nextValue = board[testNextSpace[0]][testNextSpace[1]];
            if (nextValue <= currentValue && Strategy2048.numberOfSmallerSurroundingSpaces(board, testNextSpace, new ArrayList<int[]>()) > 2 && Strategy2048.lockedInPlaceVertically(board, testNextSpace)) {
                // System.out.println("not crowded, locked in");
                return testNextSpace;
            }
        }

        //If there's one that isn't too high, return that
        if (largestOptions.size() > 0) {
            // System.out.println("not too high");
            return largestOptions.get(0);
        }
        
        //Default
        return possibleNextSpaces[0];
    }
}