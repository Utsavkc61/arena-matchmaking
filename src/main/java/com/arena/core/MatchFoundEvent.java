package com.arena.core;

public record MatchFoundEvent(Long matchId, String playerAName, String playerBName) {}