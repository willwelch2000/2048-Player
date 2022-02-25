
class Move extends Goal {
    // This gets a specific number to a specific space

    public Move(Play2048 game, int number, int[] desiredLocation, boolean forgetIfNotEasy) {
        super(game, 4, number, desiredLocation, forgetIfNotEasy);
        addSecondary();
    }

    public void finalAction() { // Do best move
        game.action(Strategy2048.bestMove(game, desiredBoard(), 4));
    }

    public boolean completed() {
        // If row doesn't matter
        if (desiredLocation[0] == -1) {
            for (int i = 0; i < 4; i++)
                if (board[i][desiredLocation[1]] == number)
                    return true;
            return false;
        }

        // If column doesn't matter
        if (desiredLocation[1] == -1) {
            for (int i = 0; i < 4; i++)
                if (board[desiredLocation[0]][i] == number)
                    return true;
            return false;
        }

        // Otherwise
        return board[desiredLocation[0]][desiredLocation[1]] == number;
    }

    public int[][] fixedBoard() {
        //Just return desiredBoard
        return desiredBoard();
    }

    public int[][] desiredBoard() {
        int[][] toReturn;
        if (desiredLocation[0] == -1) {
            // Row doesn't matter--we need 1 of these in the right column
            toReturn = new int[][] { { -1, -1, -1, -1 }, { -1, -1, -1, -1 }, { -1, -1, -1, -1 }, { -1, -1, -1, -1 },
                    { -1, -1, -1, -1 }, { -1, -1, -1, -1 } };
            toReturn[5][desiredLocation[1]] = 10 + number;
        } else if (desiredLocation[1] == -1) {
            // Column doesn't matter--we need 1 of these in the right row
            toReturn = new int[][] { { -1, -1, -1, -1 }, { -1, -1, -1, -1 }, { -1, -1, -1, -1 }, { -1, -1, -1, -1 },
                    { -1, -1, -1, -1 }, { -1, -1, -1, -1 } };
            toReturn[4][desiredLocation[0]] = 10 + number;
        } else {
            toReturn = new int[][] { { -1, -1, -1, -1 }, { -1, -1, -1, -1 }, { -1, -1, -1, -1 },
                    { -1, -1, -1, -1 } };
            toReturn[desiredLocation[0]][desiredLocation[1]] = number;
        }
        return toReturn;
    }

    public void reevaluate() {
        //Major note--to replace/add secondaries, just clear all of them--at the end, it will add secondaries if empty

        // If number doesn't exist, forget this goal
        boolean numberExists = false;
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                if (board[i][j] == number)
                    numberExists = true;
        if (!numberExists)
            forget = true;

        if (forget)
            return;

        // Forgotten secondary goals (only look at first goal)--delete them--will add new ones at end
        if (secondaryGoals.size() > 0 && secondaryGoals.get(0).getForget())
            while (!secondaryGoals.isEmpty())
                secondaryGoals.remove(0);

        // If necessary, add a secondary goal
        if (!forget && secondaryGoals.size() == 0)
            addSecondary();
    }

    public String description() {
        String toReturn = "Move: " + desiredLocation[0] + ", " + desiredLocation[1] + ": " + number;
        return toReturn;
    }

    protected void addSecondary() {
        //Adjusts secondaries--should be called with no secondaries--should clear before calling

        // If this isn't in right row or column, add secondary task to get one of those right
        boolean rightRow = false, rightColumn = false;
        int[][] locations = game.findLocation(number);
        for (int i = 0; i < locations.length; i++) {
            if (locations[i][0] == desiredLocation[0])
                rightRow = true;
            if (locations[i][1] == desiredLocation[1])
                rightColumn = true;
        }
        if (desiredLocation[0] == -1)
            rightRow = true;
        if (desiredLocation[1] == -1)
            rightColumn = true;
        if (!rightRow && !rightColumn && secondaryGoals.size() == 0)
            // CHANGE LATER--Come up with better algorithm for choosing to do row or column first
            secondaryGoals.add(new Move(game, number, new int[] { desiredLocation[0], -1 }, false));
    }
}