package com.arena.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;


public interface MatchRepository extends JpaRepository<Match, Long> {
    @Query("SELECT m FROM Match m JOIN FETCH m.playerA JOIN FETCH m.playerB")
    List<Match> findAllWithPlayers();
}