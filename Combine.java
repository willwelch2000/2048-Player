class Combine extends Goal {
    //This gets a specific number to a specific space while keeping some spaces unchanged

    private int[][] keepLocations;
    private int[] keepValues;

    public Combine(Play2048 game, int number, int[] desiredLocation, int[][] keepLocations, boolean forgetIfNotEasy) {
        super(game, 1, number, desiredLocation, forgetIfNotEasy);
        this.keepLocations = keepLocations;
        keepValues = new int[keepLocations.length];
        for (int i = 0; i < keepLocations.length; i++)
            keepValues[i] = board[keepLocations[i][0]][keepLocations[i][1]];
    }

    public void finalAction() {
        //If we can get to best board, do that
        int[][] bestBoard = bestBoard();
        if (Strategy2048.easyToSolve(game, bestBoard))
            game.action(Strategy2048.bestMove(game, bestBoard, 4));

        //Otherwise, work to desiredBoard
        else
            game.action(Strategy2048.bestMove(game, desiredBoard(), 4));
    }

    public boolean completed() {
        // Doesn't care about keepLocations or filling
        return board[desiredLocation[0]][desiredLocation[1]] == number;
    }

    public int[][] fixedBoard() {
        int[][] toReturn = new int[][] { { -1, -1, -1, -1 }, { -1, -1, -1, -1 }, { -1, -1, -1, -1 },
                { -1, -1, -1, -1 } };
        // Adjust for keep locations
        for (int i = 0; i < keepLocations.length; i++)
            toReturn[keepLocations[i][0]][keepLocations[i][1]] = keepValues[i];
        return toReturn;
    }

    public int[][] desiredBoard() {
        int[][] toReturn = new int[][] { { -1, -1, -1, -1 }, { -1, -1, -1, -1 }, { -1, -1, -1, -1 },
                { -1, -1, -1, -1 } };
        //Put down number at location
        toReturn[desiredLocation[0]][desiredLocation[1]] = number;
        // Adjust for keep locations
        for (int i = 0; i < keepLocations.length; i++)
            toReturn[keepLocations[i][0]][keepLocations[i][1]] = keepValues[i];
        return toReturn;
    }

    public int[][] bestBoard() {
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
        toReturn[desiredLocation[0]][desiredLocation[1]] = number;
        // Adjust for keep locations
        for (int i = 0; i < keepLocations.length; i++)
            toReturn[keepLocations[i][0]][keepLocations[i][1]] = keepValues[i];
        return toReturn;
    }

    public void reevaluate() {
        // Forget if keep locations aren't the same
        for (int i = 0; i < keepLocations.length; i++)
            if (board[keepLocations[i][0]][keepLocations[i][1]] != keepValues[i])
                forget = true;
    }

    public String description() {
        String toReturn = "Combine: " + desiredLocation[0] + ", " + desiredLocation[1] + ": " + number + " Keep: ";
        for (int i = 0; i < keepLocations.length; i++)
            toReturn += keepLocations[i][0] + ", " + keepLocations[i][1] + "; ";
        return toReturn;
    }
}
