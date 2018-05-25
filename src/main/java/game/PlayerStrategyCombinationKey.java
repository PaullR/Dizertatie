package game;

import java.util.List;

/**
 * Created by Paul on 5/19/2018.
 */
public class PlayerStrategyCombinationKey {
    private String playerName;
    private List<String> strategyCombination;

    public PlayerStrategyCombinationKey(String playerName, List<String> strategyCombination) {
        this.playerName = playerName;
        this.strategyCombination = strategyCombination;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public List<String> getStrategyCombination() {
        return strategyCombination;
    }

    public void setStrategyCombination(List<String> strategyCombination) {
        this.strategyCombination = strategyCombination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayerStrategyCombinationKey that = (PlayerStrategyCombinationKey) o;

        if (!playerName.equals(that.playerName)) return false;
        return strategyCombination.equals(that.strategyCombination);
    }

    @Override
    public int hashCode() {
        int result = playerName.hashCode();
        result = 31 * result + strategyCombination.hashCode();
        return result;
    }
}
