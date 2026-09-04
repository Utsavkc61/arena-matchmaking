package com.arena.core;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
public class MatchmakingController {
    private final MatchmakingQueue matchmakingQueue;
    private final PlayerRepository playerRepository;

    public MatchmakingController(MatchmakingQueue matchmakingQueue, PlayerRepository playerRepository){
        this.matchmakingQueue=matchmakingQueue;
        this.playerRepository=playerRepository;
    }

    @PostMapping("/api/queue/join/{playerId}")
    public String addPlayer(@PathVariable Long playerId) {
        Player player = playerRepository.findById(playerId)
            .orElseThrow(() -> new PlayerNotFoundException(playerId));
        
        matchmakingQueue.addPlayer(player);

        return player.getName()+ " has joined";
    }

    @PostMapping("/api/queue/leave/{playerId}")
    public String removePlayer(@PathVariable Long playerId) {
        matchmakingQueue.removePlayer(playerId);
        return playerId + " has been removed";
    }

}
