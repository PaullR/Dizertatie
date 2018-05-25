package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Paul on 5/19/2018.
 */
public class Player {

    private String name;

    //probabilities for each strategy as strings
    private List<String> probabilitiesStringList;

    //key : strategy, value: probability
    private Map<String, Fraction> strategyProbabilityMapP;
    private Map<String, Fraction> strategyProbabilityMapPstar;

    private Fraction utilityP;
    private Fraction utilityPstar;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getProbabilitiesStringList() {
        return probabilitiesStringList;
    }

    public void setProbabilitiesStringList(List<String> probabilitiesStringList) {
        this.probabilitiesStringList = probabilitiesStringList;
    }

    public Map<String, Fraction> getStrategyProbabilityMapP() {
        return strategyProbabilityMapP;
    }

    public void setStrategyProbabilityMapP(Map<String, Fraction> strategyProbabilityMapP) {
        this.strategyProbabilityMapP = strategyProbabilityMapP;
    }

    public Fraction getUtilityP() {
        return utilityP;
    }

    public void setUtilityP(Fraction utilityP) {
        this.utilityP = utilityP;
    }

    public Map<String, Fraction> getStrategyProbabilityMapPstar() {
        return strategyProbabilityMapPstar;
    }

    public void setStrategyProbabilityMapPstar(Map<String, Fraction> strategyProbabilityMapPstar) {
        this.strategyProbabilityMapPstar = strategyProbabilityMapPstar;
    }

    public Fraction getUtilityPstar() {
        return utilityPstar;
    }

    public void setUtilityPstar(Fraction utilityPstar) {
        this.utilityPstar = utilityPstar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        if (!name.equals(player.name)) return false;
        if (!probabilitiesStringList.equals(player.probabilitiesStringList)) return false;
        if (!strategyProbabilityMapP.equals(player.strategyProbabilityMapP)) return false;
        return utilityP.equals(player.utilityP);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + probabilitiesStringList.hashCode();
        result = 31 * result + strategyProbabilityMapP.hashCode();
        return result;
    }

    public static Set<String> getStrategyList(Map<String, Fraction> strategyProbabiliyMap) {
        return strategyProbabiliyMap.keySet();
    }

    public static List<Fraction> getProbabilityList(Map<String, Fraction> strategyProbabiliyMap) {
        return new ArrayList<>(strategyProbabiliyMap.values());
    }

    public static Player getPlayerByName(String playerName, List<Player> players) {
        return players.stream()
                .filter(player -> playerName.equals(player.getName()))
                .findAny()
                .orElse(null);
    }
}
