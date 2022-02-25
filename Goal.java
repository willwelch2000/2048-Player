import java.util.*;
abstract class Goal {
    protected Play2048 game;
    protected int type; // 0=Board, 1=Combine, 2=Fill, 3=Get, 4=Move, 5=OneMove
    protected int number;
    protected int[] desiredLocation;
    protected int[][] board;
    protected boolean forget = false; //Means something has gone wrong
    protected boolean tryingToFix = false; //Means it's trying to fix something that has gone wrong--don't forget
    protected boolean secondaryIsFixing = false; //Means that some secondary down the chain is tryingToFix--don't forget
    protected boolean forgetIfNotEasy = false; //Means that this goal is created because it should be easyToSolve--forget if it no longer is
    protected List<Goal> secondaryGoals = new ArrayList<>(); //Goals that must be completed as a smaller part of this one

    public abstract void finalAction(); //Action once secondaries are gone

    public abstract void reevaluate(); //Rethink everything

    public abstract boolean completed();

    public abstract int[][] fixedBoard(); //Board setup that means it isn't messed up

    public abstract int[][] desiredBoard(); //Ideal board setup--not defined for Fill or OneMove

    public abstract String description();

    public Goal(Play2048 game, int type, int number, int[] desiredLocation, boolean forgetIfNotEasy) {
        this.game = game;
        this.type = type;
        this.number = number;
        this.desiredLocation = desiredLocation;
        this.forgetIfNotEasy = forgetIfNotEasy;
        board = game.getBoard();
    }

    public boolean getForget() {
        return forget;
    }

    public boolean tryToFix() {
        //True=fixable. This adds the secondaries to fix unless it already is tryingToFix

        //If it's easy to solve, add a secondary goal to do that move (and remove other ones)
        if (Strategy2048.easyToSolve(game, fixedBoard())) {
            if (!tryingToFix && forget) {
                while (!secondaryGoals.isEmpty())
                    secondaryGoals.remove(0);
                secondaryGoals.add(new Board(game, fixedBoard(), true));
            }
            return true;
        }
        return false;
    }

    public void action() {
        //Go to first secondary goal, if one exists. Then delete it if completed (unless it's trying to fix or a child is--this is because it can be completed when it should be forgotten)
        if (secondaryGoals.size() > 0) {
            secondaryGoals.get(0).action();
            if (secondaryGoals.get(0).completed() && !secondaryGoals.get(0).tryingToFix && !secondaryGoals.get(0).secondaryIsFixing)
                secondaryGoals.remove(0);
        }

        //If no secondary goals, perform action
        else if (!completed())
            finalAction();

        //Update board
        board = game.getBoard();

        //If secondary created for fixing has forgotten, then we can forget this and delete that secondary
        if (tryingToFix && secondaryGoals.size() > 0 && secondaryGoals.get(0).forget) {
            tryingToFix = false;
            forget = true;
            secondaryGoals.remove(0);
        }
        
        //If forgetIfNotEasy and not easy, forget with no exceptions--don't worry about reevaluate or trying to fix
        if (forgetIfNotEasy && !Strategy2048.easyToSolve(game, desiredBoard())) {
            forget = true;
            return;
        }

        if (!completed())
            reevaluate();

        //If child is trying to fix, avoid forgetting and mark secondaryIsFixing
        if (secondaryGoals.size() > 0 && (secondaryGoals.get(0).tryingToFix || secondaryGoals.get(0).secondaryIsFixing)) {
            forget = false;
            secondaryIsFixing = true;
        }
        //Try to fix if necessary (messed up and no secondary fixing)
        if (forget && !secondaryIsFixing) {
            tryingToFix = tryToFix();
            forget = !tryingToFix;
        }

        //Get rid of tryingToFix if fixed
        if (tryingToFix && Strategy2048.equalsDesired(board, fixedBoard()))
            tryingToFix = false;
        //Get rid of secondaryIsFixing if first secondary isn't tryingToFix or secondaryIsFixing--or if no secondaries
        if (secondaryIsFixing && (secondaryGoals.size() == 0 || (!secondaryGoals.get(0).tryingToFix && !secondaryGoals.get(0).secondaryIsFixing)))
            secondaryIsFixing = false;
    }

    public String toString() {
        String toReturn = description() + "; Forget: " + getForget() + "; TryingToFix: " + tryingToFix + "; SecondaryIsFixing: " + secondaryIsFixing + "; ForgetIfNotEasy: " + forgetIfNotEasy;
        if (secondaryGoals.size() == 0)
            return toReturn;
        toReturn += " {\n";
        for (int i = 0; i < secondaryGoals.size(); i++)
            toReturn += secondaryGoals.get(i) + "\n";
        toReturn += "}";
        return toReturn;
    }
}