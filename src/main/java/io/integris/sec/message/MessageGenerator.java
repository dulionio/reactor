package io.integris.sec.message;

import java.util.function.Consumer;

import io.integris.sec.message.bean.Message;
import reactor.core.publisher.SynchronousSink;

public class MessageGenerator implements Consumer<SynchronousSink<Message>> {

    private long _sequence = 0;

    @Override
    public void accept(SynchronousSink<Message> sink) {
        sink.next(new Message(_sequence++, System.currentTimeMillis()));
    }
}
