package com.arena.core;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record PlayerCreateRequest (@NotBlank(message = "name must not be blank") String name, 
                                    @Min(value=0, message ="rating must not be negative") 
                                    @Max(value=3000, message ="rating must be realistic") int rating){}
    

