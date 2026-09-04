package com.arena.core;

import org.springframework.stereotype.Component;

@Component
public class NotificationService {
    public void notifyPlayers(Player playerA, Player playerB) {
        try {
            Thread.sleep(2000); // pretend this is a slow network call
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Notified " + playerA.getName() + " and " + playerB.getName() + " about their match.");
    }
}
