package com.arena.core;

import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
public class MatchmakingQueue {

    private final Map<Long, Player> waitingPlayers = new ConcurrentHashMap<>();

    public void addPlayer(Player player) {
        waitingPlayers.put(player.getId(), player);
    }

    public void removePlayer(Long playerId) {
        waitingPlayers.remove(playerId);
    }

    public Map<Long, Player> getWaitingPlayers() {
        return waitingPlayers;
    }
    public Player pollAnyPlayer() {
    for (Long playerId : waitingPlayers.keySet()) {
        Player removed = waitingPlayers.remove(playerId);
        if (removed != null) {
            return removed;
        }
    }
    return null;
}
}