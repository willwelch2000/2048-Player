class Fill extends Goal {
    //Fills columns so that you can swipe up without consequence

    private boolean startFromRight; //True = get far left column first, false = right
    private int finalColumn;
    private int[][] keepLocations;
    private int[] keepValues;

    public Fill(Play2048 game, boolean startFromRight, int finalColumn, int[][] keepLocations) {
        super(game, 2, -1, new int[] {}, false);
        this.startFromRight = startFromRight;
        this.finalColumn = finalColumn;
        this.keepLocations = keepLocations;
        keepValues = new int[keepLocations.length];
        for (int i = 0; i < keepLocations.length; i++)
            keepValues[i] = board[keepLocations[i][0]][keepLocations[i][1]];
    }

    public void finalAction() {
        //currentColumn is the column we most need to fill
        int currentColumn = finalColumn;
        if (startFromRight) {
            for (int col = finalColumn + 1; col <= 3; col++)
                if(!columnFilled(col))
                    currentColumn = col;
        } else
            for (int col = finalColumn - 1; col >= 0; col--)
                if(!columnFilled(col))
                    currentColumn = col;

        int[] movePriority = {startFromRight? 1 : 3, Strategy2048.workingDown(game, currentColumn) ? 0 : 2, Strategy2048.workingDown(game, currentColumn) ? 2 : 0, startFromRight? 3 : 1};

        for (int move = 0; move < 4; move ++) {
            if (game.isPossibleMove(movePriority[move])) {
                game.action(movePriority[move]);
                return;
            }
        }
    }

    public boolean completed() {
        //Doesn't care about keep locations

        // Go down with copy
        Play2048 copy = new Play2048(game);
        copy.action(2, false);
        int[][] copyBoard = copy.getBoard();

        //Check every column that should be filled for empty space
        if (startFromRight) {
            for (int col = finalColumn; col <= 3; col++)
                for (int row = 0; row < 4; row++)
                    if (copyBoard[row][col] == 0)
                        return false;
        } else
            for (int col = 0; col <= finalColumn; col++)
                for (int row = 0; row < 4; row++)
                    if (copyBoard[row][col] == 0)
                        return false;

        //Create board to compare to at end
        int[][] compareBoard = new int[][] {{-1, -1, -1, -1}, {-1, -1, -1, -1}, {-1, -1, -1, -1}, {-1, -1, -1, -1}};
        if (startFromRight)
            for (int col = finalColumn + 1; col <= 3; col++)
                for (int row = 0; row < 4; row++)
                    compareBoard[row][col] = copyBoard[row][col];
        else
            for (int col = 0; col <= finalColumn - 1; col++)
                for (int row = 0; row < 4; row++)
                    compareBoard[row][col] = copyBoard[row][col];
        
        //Go left/right on new copy
        copy = new Play2048(game);
        int preferredMove = startFromRight? 1 : 3;
        copy.action(preferredMove, false);
        copyBoard = copy.getBoard();

        //If any involved column except final column has changed, false (compare = original but with some -1s)
        if (!Strategy2048.equalsDesired(copyBoard, compareBoard))
            return false;

        return true;
    }

    public int[][] fixedBoard() {
        int[][] toReturn = new int[][] { { -1, -1, -1, -1 }, { -1, -1, -1, -1 }, { -1, -1, -1, -1 },
                { -1, -1, -1, -1 } };
        // Adjust for keep locations
        for (int i = 0; i < keepLocations.length; i++)
            toReturn[keepLocations[i][0]][keepLocations[i][1]] = keepValues[i];
        return toReturn;
    }

    public int[][] desiredBoard(){
        //Not necessary
        return new int[][] {};
    }

    public void reevaluate() {
        for (int i = 0; i < keepLocations.length; i++)
            if (board[keepLocations[i][0]][keepLocations[i][1]] != keepValues[i])
                forget = true;
    }

    public String description() {
        String toReturn = "Fill: Start from " + (startFromRight? "right" : "left") + "; End column " + finalColumn + ":";
        toReturn += " Keep: ";
        for (int i = 0; i < keepLocations.length; i++)
            toReturn += keepLocations[i][0] + ", " + keepLocations[i][1] + "; ";
        return toReturn;
    }

    private boolean columnFilled(int column) {
        // Go down with copy
        Play2048 copy = new Play2048(game);
        copy.action(2, false);
        int[][] copyBoard = copy.getBoard();

        //Check every column that should be filled for empty space
        for (int row = 0; row < 4; row++)
            if (copyBoard[row][column] == 0)
                return false;
        return true;
    }
}