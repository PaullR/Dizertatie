package validators;

import game.Fraction;
import game.Player;

import java.util.List;
import java.util.Set;

/**
 * Created by Paul on 5/19/2018.
 */
public class Validators {
    public static void validatePayoffLines(List<String> payoffLines, Set<List<String>> allStrategyCombinations) {
        int validation = payoffLines.size() - allStrategyCombinations.size();

        if (validation == 0) {
            return;
        } else if (validation < 0) {
            System.out.println("Not enough payoff lines. There should be " + allStrategyCombinations.size() + " payoff lines");
            System.exit(0);
        } else {
            System.out.println("Too many payoff lines. There should be " + allStrategyCombinations.size() + " payoff lines");
            System.exit(0);
        }
    }

    public static void validateStrategyCombinationPayoff(List<Integer> strategyCombinationPayoff, List<String> strategy, List<Player> playersList) {
        int validation = strategyCombinationPayoff.size() - playersList.size();
        if (validation == 0) {
            return;
        } else if (validation == -1) {
            System.out.println("Not enough payoff values for strategy " + strategy + ". There should be " + playersList.size() + " values(one for each player)");
            System.exit(0);
        } else {
            System.out.println("Too many payoff values for strategy " + strategy + ". There should be " + playersList.size() + " values(one for each player)");
            System.exit(0);
        }
    }

    public static void validateStrategyCombination(List<String> strategy, List<Player> playersList) {
        int validation = strategy.size() - playersList.size();
        if (validation == 0) {
            return;
        } else if (validation == -1) {
            System.out.println("Invalid strategy combination. Too few strategies.There should be " + playersList.size() + " stragies");
            System.exit(0);
        } else {
            System.out.println("Invalid strategy combination. Too many strategies.There should be " + playersList.size() + " stragies");
            System.exit(0);
        }
    }

    public static void valideProbabilitiesForEachPlayer(String playerName, List<Fraction> probabilities) {
        Fraction probabilitiesSum = Fraction.addFractionsList(probabilities);
        if (probabilitiesSum.getNumerator() % probabilitiesSum.getDenominator() > 0) {
            throw new IllegalStateException("Sum of probabilities for " + playerName + " should be 1 or 0, but is " + probabilitiesSum);
        }
    }
}