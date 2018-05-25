package game;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import validators.Validators;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Paul on 5/19/2018.
 */
public class Game {
    private static final String FILENAME = "E:\\Java_Projects\\GameTheory_Dizertatie2\\game.txt";
    private static final List<Player> playersList = Lists.newArrayList();
    private static final List<Fraction> probabilities_P_Product = Lists.newArrayList();
    private static final List<Fraction> probabilities_PSTAR_Product = Lists.newArrayList();
    //payoffs read from file
    private static List<String> payoffLines = Lists.newArrayList();
    //all strategy combinations
    private static Set<List<String>> allStrategyCombinations = Sets.newLinkedHashSet();
    //contains payoffs for each player and strategy combination
    private static Map<PlayerStrategyCombinationKey, Integer> payoffsForEachPlayerAndStrategyCombination = Maps.newLinkedHashMap();
    //true is payoffs are random, false if payoffs are read from file
    private static boolean randomPayoffs = false;

    public static void main(String[] args) {
        String line = null;
        try (BufferedReader br = new BufferedReader(new FileReader(FILENAME))) {
            while (!(line = br.readLine()).isEmpty()) {
                Player player = createPlayer(line);
                playersList.add(player);
            }

            br.readLine();

            System.out.println("Use random payoffs (1) or payoffs from file (2)?");
            Scanner payoffScannner = new Scanner(System.in);
            randomPayoffs = payoffScannner.nextInt() == 1;

            if (!randomPayoffs) {
                for (String line2 = br.readLine(); line2 != null; line2 = br.readLine())
                    payoffLines.add(line2);
            }

        } catch (IOException e) {
            System.out.println(e);
        }

        System.out.println("Game:");
        playersList.forEach(player -> {
            String playerName = player.getName();
            System.out.println(playerName + " " + player.getStrategyProbabilityMapP());

            List<Fraction> probabilities = Player.getProbabilityList(player.getStrategyProbabilityMapP());
            Validators.valideProbabilitiesForEachPlayer(playerName, probabilities);
        });

        allStrategyCombinations = computeAllStrategyCombinationsSet();

        if (randomPayoffs) {
            saveRandomPayoffsForEachPlayerAndStrategyProfile(allStrategyCombinations);
        } else {
            Validators.validatePayoffLines(payoffLines, allStrategyCombinations);
            payoffsForEachPlayerAndStrategyCombination = savePayoffsFromFileForPlayerAndStrategyCombination(
                    allStrategyCombinations,
                    payoffLines
            );
        }
        printGameByStrategyCombination();

        computePstarStrategyProfile();
        playersList.forEach(
                player -> {
                    System.out.println(player.getName() + "\n"
                            + "PstrategyProfile: " + player.getStrategyProbabilityMapP() + "\n"
                            + "PstarStrategyProfile: " + player.getStrategyProbabilityMapPstar());
                }
        );

        computeExpectedUtilities();

//        System.out.println("---- Find payoffs for player and strategy combination ---- ");
//        getPayoffs();
    }

    private static void computePstarStrategyProfile() {
        System.out.println("---Create P* strategy profile:---");

        for (Player player : playersList) {
            System.out.println(player.getName());
            Map<String, Fraction> strategyProbabilityMapPstar = Maps.newLinkedHashMap();
            List<Fraction> probabilities = Lists.newArrayList();
            for (String strategy : player.getStrategyProbabilityMapP().keySet()) {
                System.out.println("Enter probability for strategy " + strategy);

                Scanner probabilityScanner = new Scanner(System.in);
                String probabilityString = probabilityScanner.next();

                player.getProbabilitiesStringList().add(probabilityString);
                Fraction probability = getProbabilityFromString(probabilityString);
                strategyProbabilityMapPstar.put(strategy, probability);

                probabilities.add(probability);
            }
            Validators.valideProbabilitiesForEachPlayer(player.getName(), probabilities);
            player.setStrategyProbabilityMapPstar(strategyProbabilityMapPstar);
            System.out.println(player.getStrategyProbabilityMapPstar());
        }
    }

    private static void computeExpectedUtilities() {
        System.out.println("\n---Expected utilities:----------------");
        computeProbabilityProductForEachStrategyProfile();
        System.out.println(probabilities_P_Product);
        System.out.println(probabilities_PSTAR_Product);
        playersList.forEach(player -> {
            Fraction utilityP = computeUtilityForPlayer(player.getName(), probabilities_P_Product);
            Fraction utilityPstar = computeUtilityForPlayer(player.getName(), probabilities_PSTAR_Product);
            player.setUtilityPstar(utilityP);
            player.setUtilityPstar(utilityPstar);
            System.out.println(player.getName()
                    + "\n - expectedUtilityP = " + utilityP.toString() + "\n"
                    + " - expectedUtilityPstar = " + utilityPstar.toString()
            );
        });
    }

    private static Player createPlayer(String line) {
        Player player = new Player();
        String[] splittedLine = line.split("\\|");

        //get player name from position 0 and remove the space before |
        player.setName(splittedLine[0].substring(0, splittedLine[0].length() - 1));

        //get player strategies, pos 1, and probability, pos2, for each strategy
        String strategies = splittedLine[1].substring(1, splittedLine[1].length() - 1);
        String probabilities = splittedLine[2].substring(1, splittedLine[2].length());

        Map<String, Fraction> strategyProbabilityMap = Maps.newLinkedHashMap();
        List<String> probabilitiesStringList = Lists.newArrayList();
        setStrategiesAndProbabilities(
                strategies,
                probabilities,
                strategyProbabilityMap,
                probabilitiesStringList
        );

        player.setStrategyProbabilityMapP(strategyProbabilityMap);
        player.setProbabilitiesStringList(probabilitiesStringList);
        return player;
    }

    public static void setStrategiesAndProbabilities(
            String strategies,
            String probabilities,
            Map<String, Fraction> strategyProbabilityMap,
            List<String> probabilitiesString) {

        String[] strategiesSplitted = strategies.split(",");
        String[] probabilitiesSplittedAsStrings = probabilities.split(",");

        List<Fraction> probabilityList = convertProbabilityStringToProbabilityList(probabilitiesSplittedAsStrings);

        if (strategiesSplitted.length == probabilitiesSplittedAsStrings.length) {
            for (int i = 0; i < strategiesSplitted.length; i++) {
                strategyProbabilityMap.put(strategiesSplitted[i], probabilityList.get(i));
                probabilitiesString.add(probabilitiesSplittedAsStrings[i]);
            }
        } else {
            throw new IllegalStateException(
                    "Nr of strategies must be equal to nr of probabilities for probailityLine: " + probabilities
            );
        }
    }

    public static List<Fraction> convertProbabilityStringToProbabilityList(String[] probabilities) {
        List<Fraction> probabilityList = Lists.newArrayList();
        for (String prob : probabilities) {
            Fraction probability = getProbabilityFromString(prob);
            probabilityList.add(probability);
        }
        return probabilityList;
    }

    private static Fraction getProbabilityFromString(String prob) {
        if (!prob.contains("/")) {
            throw new RuntimeException("Probabilities must be of from a/b");
        }

        String[] splittedProbabilityString = prob.split("\\/");
        Fraction probability;
        if (splittedProbabilityString[0].equals("0")) {
            probability = Fraction.ONE.ZERO;
        } else {
            probability = new Fraction(Integer.parseInt(splittedProbabilityString[0]),
                    Integer.parseInt(splittedProbabilityString[1]));
        }
        return probability;
    }

    public static Set<List<String>> computeAllStrategyCombinationsSet() {
        List<Set<String>> allStrategyCombinations = playersList.stream()
                .map(player -> player.getStrategyProbabilityMapP().keySet())
                .collect(Collectors.toList());

        return Sets.cartesianProduct(allStrategyCombinations);
    }

    private static void printGameByStrategyCombination() {
        System.out.println("---------------------------");
        for (List<String> strategyCombination : allStrategyCombinations) {
            System.out.println("Strategy combination: " + strategyCombination);
            for (Player player : playersList) {
                Integer payoff = payoffsForEachPlayerAndStrategyCombination.get(new PlayerStrategyCombinationKey(player.getName(), strategyCombination));
                System.out.println("\t" + player.getName() + " = " + payoff);
            }
        }
    }

    public static Map<PlayerStrategyCombinationKey, Integer> savePayoffsFromFileForPlayerAndStrategyCombination(Set<List<String>> allStrategyCombinations, List<String> payoffList) {
        int strategyCombinationCounter = 0;
        for (List<String> strategyCombination : allStrategyCombinations) {
            List<Integer> strategyCombinationPayoff = getStrategyCombinationPayoff(strategyCombinationCounter, payoffList, strategyCombination);
            for (int i = 0; i < playersList.size(); i++) {
                PlayerStrategyCombinationKey playerStrategyCombinationKey = new PlayerStrategyCombinationKey(playersList.get(i).getName(), strategyCombination);
                payoffsForEachPlayerAndStrategyCombination.put(playerStrategyCombinationKey, strategyCombinationPayoff.get(i));
            }
            strategyCombinationCounter++;
        }

        return payoffsForEachPlayerAndStrategyCombination;
    }

    public static void saveRandomPayoffsForEachPlayerAndStrategyProfile(Set<List<String>> allStrategyProfilesSet) {
        for (List<String> strategyProfile : allStrategyProfilesSet) {
            for (Player player : playersList) {
                Random random = new Random();
                int payoff = random.nextInt(10) + 1;
                PlayerStrategyCombinationKey playerStrategyProfileKey = new PlayerStrategyCombinationKey(player.getName(), strategyProfile);
                payoffsForEachPlayerAndStrategyCombination.put(playerStrategyProfileKey, payoff);
            }
        }
    }

    //use only for constructing the game
    public static List<Integer> getStrategyCombinationPayoff(int strategyCombinationCounter, List<String> payoffList, List<String> strategy) {
        String[] payoffsString = payoffList.get(strategyCombinationCounter).split(",");
        List<Integer> strategyCombinationPayoff = Arrays.stream(payoffsString).map(
                payoffString -> Integer.parseInt(payoffString)).collect(Collectors.toList()
        );
        Validators.validateStrategyCombinationPayoff(strategyCombinationPayoff, strategy, playersList);

        return strategyCombinationPayoff;
    }

    //use this for searching payoffs for given playe and strategy combination
    private static void getPayoffs() {
        String exit = "0";
        while (exit.equals("0")) {

            System.out.println("Payoff is: " + getPayoffForPlayerAndStrategyCombination(payoffsForEachPlayerAndStrategyCombination));

            System.out.println("Exit(1) or continue(0)?");
            Scanner exitScanner = new Scanner(System.in);
            exit = exitScanner.nextLine();
        }
    }

    public static Integer getPayoffForPlayerAndStrategyCombination(Map<PlayerStrategyCombinationKey, Integer> payoffsPlayersStrategyProfilesMap) {
        System.out.println("Give player: ");
        Scanner playerScanner = new Scanner(System.in);
        String player = playerScanner.next();
        List<String> playerNames = playersList.stream().map(Player::getName)
                .collect(Collectors.toList());
        if (!playerNames.contains(player)) {
            throw new RuntimeException(player + " is not in players list.");
        }

        System.out.println("Give strategy: ");
        Scanner strategyCombinationScanner = new Scanner(System.in);
        String strategyCombinationString = strategyCombinationScanner.next();
        System.out.println(strategyCombinationString);

        List<String> strategyCombination = Arrays.asList(strategyCombinationString.split(","));
        Validators.validateStrategyCombination(strategyCombination, playersList);

        PlayerStrategyCombinationKey playerStrategyProfileKey = new PlayerStrategyCombinationKey(
                player,
                strategyCombination
        );

        return payoffsPlayersStrategyProfilesMap.get(playerStrategyProfileKey);
    }

    private static Fraction computeUtilityForPlayer(String playerName, List<Fraction> probabilitiesProduct) {
        Fraction finalPayoff = Fraction.ZERO;
        PlayerStrategyCombinationKey key = new PlayerStrategyCombinationKey(playerName, null);
        int i = 0;
        for (List<String> strategyProfile : allStrategyCombinations) {
            key.setStrategyCombination(strategyProfile);
            Integer payoff = payoffsForEachPlayerAndStrategyCombination.get(key);
            Fraction playerProbability = probabilitiesProduct.get(i);
            i++;
            Fraction probabilityTimesPayoff = Fraction.multiplyTwoFractions(
                    new Fraction(payoff, 1),
                    playerProbability
            );
//            System.out.println(probabilityTimesPayoff);
            finalPayoff = Fraction.addTwoFractions(
                    finalPayoff,
                    probabilityTimesPayoff
            );
        }
        return finalPayoff;
    }

    public static void computeProbabilityProductForEachStrategyProfile() {
        for (List<String> strategyCombination : allStrategyCombinations) {
            Fraction product_P = Fraction.ONE;
            Fraction product_PSTAR = Fraction.ONE;
            for (int i = 0; i < playersList.size(); i++) {
                Player player = playersList.get(i);
                Fraction probability_P = player.getStrategyProbabilityMapP().get(strategyCombination.get(i));
                Fraction probability_PSTAR = player.getStrategyProbabilityMapPstar().get(strategyCombination.get(i));
                product_P = Fraction.multiplyTwoFractions(product_P, probability_P);
                product_PSTAR = Fraction.multiplyTwoFractions(product_PSTAR, probability_PSTAR);
            }
            probabilities_P_Product.add(product_P);
            probabilities_PSTAR_Product.add(product_PSTAR);
        }
    }
}
