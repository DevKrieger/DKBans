package ch.dkrieger.bansystem.lib.reason;

import ch.dkrieger.bansystem.lib.utils.Duration;

import java.util.List;

public class UnbanReason extends KickReason{

    private Duration maxDuration;
    private int maxPoints;
    private List<Integer> notForBanID;

    public UnbanReason(int id, int points, String name, String display, String permission, boolean hidden, List<String> aliases, Duration maxDuration, int maxPoints, List<Integer> notForBanID) {
        super(id, points, name, display, permission, hidden, aliases);
        this.maxDuration = maxDuration;
        this.maxPoints = maxPoints;
        this.notForBanID = notForBanID;
    }

    public Duration getMaxDuration() {
        return maxDuration;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public List<Integer> getNotForBanID() {
        return notForBanID;
    }
}