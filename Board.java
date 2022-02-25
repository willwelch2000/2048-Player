class Board extends Goal {
    //Tries to reach a specific board layout

    private int[][] desiredBoard;

    public Board(Play2048 game, int[][] desiredBoard, boolean forgetIfNotEasy) {
        super(game, 0, -1, new int[] {}, forgetIfNotEasy);
        this.desiredBoard = desiredBoard;
    }

    public void finalAction() {
        game.action(Strategy2048.bestMove(game, desiredBoard, 4));
    }

    public boolean completed() {
        return Strategy2048.equalsDesired(board, desiredBoard);
    }

    public int[][] fixedBoard() {
        return desiredBoard;
    }

    public int[][] desiredBoard() {
        return desiredBoard;
    }

    public void reevaluate() {
        if (!Strategy2048.easyToSolve(game, desiredBoard))
            forget = true;
    }

    public String description() {
        return "Board";
    }
}