package com.arena.core;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// @RestController tells Spring: "this class handles incoming HTTP requests,
// and every method's return value should be written directly into the HTTP
// response body (as JSON, if it's an object) rather than treated as the name
// of an HTML view to render."
//
// @Component-family annotations like this one are what @ComponentScan (see
// ArenaCoreApplication) picks up automatically at startup — Spring creates
// ONE instance of this class (a "bean") and manages its lifecycle for you.
// You never write "new PingController()" anywhere yourself.
@RestController
public class PingController {

    // @GetMapping("/api/ping") wires this method to handle
    // GET http://localhost:8080/api/ping
    @GetMapping("/api/ping")
    public String ping() {
        return "pong from arena-core";
    }
}
