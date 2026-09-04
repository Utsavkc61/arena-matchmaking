package com.arena.core;

public class PlayerNotFoundException extends DomainException{
    public PlayerNotFoundException(Long id){
        super("Player not found: "+id);
    }
    
}
