package com.arena.core;

import java.util.concurrent.CompletableFuture;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;

@Component
public class MatchmakingEngine {

    private final MatchmakingQueue matchmakingQueue;
    private final MatchRepository matchRepository;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    public MatchmakingEngine(MatchmakingQueue matchmakingQueue, MatchRepository matchRepository, NotificationService notificationService, SimpMessagingTemplate messagingTemplate) {
        this.matchmakingQueue = matchmakingQueue;
        this.matchRepository = matchRepository;
        this.notificationService = notificationService;
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(fixedRate = 5000)
    public void pairWaitingPlayers() {
        while (true) { 
            Player playerA = matchmakingQueue.pollAnyPlayer();
            if(playerA == null){
                break; //nobody waiting nothing to do
            }
            Player playerB = matchmakingQueue.pollAnyPlayer();
            if(playerB == null){
                matchmakingQueue.addPlayer(playerA);
                break; //nobody waiting nothing to do
            }
            Match match = new Match();
            match.setPlayerA(playerA);
            match.setPlayerB(playerB);
            matchRepository.save(match);

            MatchFoundEvent event = new MatchFoundEvent(match.getId(), playerA.getName(), playerB.getName());
            messagingTemplate.convertAndSend("/topic/matches", event);

            CompletableFuture.runAsync(() -> notificationService.notifyPlayers(playerA, playerB));

            System.out.println("Matched: " + playerA.getName() + " vs " + playerB.getName());
        }
        
    }
}