package io.integris.sec;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.integris.sec.message.MessageGenerator;
import io.integris.sec.message.MessageLogger;
import io.integris.sec.rest.SecRssClient;
import reactor.core.publisher.Flux;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {
        LOG.info("Starting reactive application");

        MessageGenerator generator = new MessageGenerator();
        SecRssClient rss = new SecRssClient();
        MessageLogger logger = new MessageLogger();
        CountDownLatch complete = new CountDownLatch(1);

        Flux.generate(generator)
            .delayElements(Duration.ofSeconds(30))
            .take(2)
            .flatMap(rss::publish)
            .doOnComplete(complete::countDown)
            .subscribe(logger);

        LOG.info("Waiting for completion signal...");
        complete.await();
        LOG.info("Exiting reactive application");
    }
}
