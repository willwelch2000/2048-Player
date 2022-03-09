import java.util.*;

class MoveHighest extends Move {

    public MoveHighest(Play2048 game) {
        super(game, game.highestNumber(), new int[] { 3, 0 }, false);
    }

    public void reevaluate() {
        // Do all the same things as Move, but check that the number is the highest number--no forget scenario

        // If we have a forgotten secondary goal, get rid of it--the last part will add one if necessary
        List<Goal> toRemove = new ArrayList<>();
        for (int i = 0; i < secondaryGoals.size(); i++)
            if (secondaryGoals.get(i).getForget() && !secondaryGoals.get(i).tryingToFix)
                toRemove.add(secondaryGoals.get(i));
        secondaryGoals.removeAll(toRemove);

        // If number doesn't exist (or if number isn't the highest number), change number and clear secondary goals
        boolean numberExists = false;
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                if (board[i][j] == number)
                    numberExists = true;
        if (!numberExists || number != game.highestNumber()) {
            number = game.highestNumber();
            while (!secondaryGoals.isEmpty())
                secondaryGoals.remove(0);
        }

        if (!forget && secondaryGoals.size() == 0)
            addSecondary();
    }

    public String description() {
        String toReturn = "MoveHighest: " + number;
        return toReturn;
    }
}