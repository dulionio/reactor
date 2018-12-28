package com.dulion.reactor.message;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageLogger implements Subscriber<Message> {

    private static final Logger LOG = LoggerFactory.getLogger(MessageLogger.class);

    @Override
    public void onSubscribe(Subscription subscription) {
        subscription.request(Long.MAX_VALUE);
        LOG.info("On Subscribe: {}", subscription.getClass().getName());
    }

    @Override
    public void onNext(Message next) {
        LOG.info("On Next: {}", next);
    }

    @Override
    public void onError(Throwable throwable) {
        LOG.info("On Error: {} - {}", throwable.getClass().getName(), throwable.getMessage());
    }

    @Override
    public void onComplete() {
        LOG.info("On Complete");
    }

}
