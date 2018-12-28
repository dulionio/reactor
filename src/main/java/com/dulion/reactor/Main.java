package com.dulion.reactor;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dulion.reactor.message.MessageGenerator;
import com.dulion.reactor.message.MessageLogger;

import reactor.core.publisher.Flux;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {
        LOG.info("Starting reactive application");

        MessageGenerator generator = new MessageGenerator();
        MessageLogger logger = new MessageLogger();

        Flux.generate(generator)
            .delayElements(Duration.ofSeconds(1))
            .take(10)
            .subscribe(logger);

        LOG.info("Sleeping...");
        Thread.sleep(12000);
        LOG.info("Exiting reactive application");
    }

}
