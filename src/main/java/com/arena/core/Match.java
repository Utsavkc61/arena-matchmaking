package com.arena.core;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Match {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Player playerA;

    @ManyToOne
    private Player playerB;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Player getPlayerA() {
        return playerA;
    }
    public void setPlayerA(Player playerA) {
        this.playerA = playerA;
    }

    public Player getPlayerB() {
        return playerB;
    }
    public void setPlayerB(Player playerB) {
        this.playerB = playerB;
    }
}