
class OneMove extends Goal {

    int direction;
    boolean hasDoneMove = false;

    public OneMove(Play2048 game, int direction) {
        super(game, 5, -1, new int[] {}, false);
        this.direction = direction;
    }

    public void finalAction() {
        game.action(direction);
        hasDoneMove = true;
    }

    public boolean completed() {
        return hasDoneMove;
    }

    public int[][] fixedBoard() {
        return new int[][] {{-1, -1, -1, -1}, {-1, -1, -1, -1}, {-1, -1, -1, -1}, {-1, -1, -1, -1}};
    }

    public int[][] desiredBoard() {
        return new int[][] {};
    }

    public void reevaluate() {
        // Nothing here
    }

    public String description() {
        String toReturn = "OneMove: ";
        if (direction == 0)
            toReturn += "Up";
        else if (direction == 1)
            toReturn += "Right";
        else if (direction == 2)
            toReturn += "Down";
        else if (direction == 3)
            toReturn += "Left";
        return toReturn;
    }
}