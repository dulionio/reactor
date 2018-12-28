package com.dulion.reactor.message;

public class Message {

    private final long _sequence;

    private final long _timestamp;

    public Message(long sequence, long timestamp) {
        _sequence = sequence;
        _timestamp = timestamp;
    }

    public long getSequence() {
        return _sequence;
    }

    public long getTimestamp() {
        return _timestamp;
    }

    @Override
    public String toString() {
        return "Message {"
                + " sequence: " + _sequence
                + ", timestamp: " + _timestamp
                + " }";
    }
}
