package com.arena.core;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class PlayerController {
    private final PlayerRepository playerRepository;

    public PlayerController(PlayerRepository playerRepository){
        this.playerRepository=playerRepository;
    }
    @PostMapping("/api/players")
    public PlayerResponse createPlayer(@Valid @RequestBody PlayerCreateRequest request){
        Player player = new Player();
        player.setName(request.name());
        player.setRating(request.rating());

        Player saved = playerRepository.save(player);
        return new PlayerResponse (saved.getId(), saved.getName(), saved.getRating());

    }
    @GetMapping("/api/players/{id}")
    public PlayerResponse getPlayer(@PathVariable Long id) {
        Player player = playerRepository.findById(id)
            .orElseThrow(() -> new PlayerNotFoundException(id));

        return new PlayerResponse(player.getId(), player.getName(), player.getRating());
    }
}

