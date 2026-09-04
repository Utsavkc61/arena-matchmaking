package com.arena.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


// @SpringBootApplication is actually THREE annotations combined into one:
//   @Configuration     - this class can define beans
//   @EnableAutoConfiguration - Spring Boot's "guess sensible defaults based
//                              on what's on the classpath" magic (e.g. it
//                              sees spring-boot-starter-web on the classpath
//                              and auto-configures an embedded Tomcat server
//                              for you, no XML, no manual setup)
//   @ComponentScan     - Spring will scan this package (and sub-packages)
//                        for @Component/@Service/@Repository/@RestController
//                        classes and register them as beans automatically
@SpringBootApplication
@EnableScheduling
public class ArenaCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArenaCoreApplication.class, args);
    }
}
