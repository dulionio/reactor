package io.integris.sec.message.bean;

import java.io.InputStream;

public class Message {

    private final long _sequence;

    private final long _timestamp;

    private InputStream _inputStream;

    private String _type;

    private String _url;

    private String _description;

    public Message(long sequence, long timestamp) {
        _sequence = sequence;
        _timestamp = timestamp;
    }

    public Message(Message source) {
        _sequence = source._sequence;
        _timestamp = source._timestamp;
    }

    public long getSequence() {
        return _sequence;
    }

    public long getTimestamp() {
        return _timestamp;
    }

    public InputStream getInputStream() {
        return _inputStream;
    }

    public String getType() {
        return _type;
    }

    public String getDescription() {
        return _description;
    }

    public String getUrl() {
        return _url;
    }

    public Message setInputStream(InputStream inputStream) {
        _inputStream = inputStream;
        return this;
    }

    public Message setType(String type) {
        _type = type;
        return this;
    }

    public Message setUrl(String url) {
        _url = url;
        return this;
    }

    public Message setDescription(String description) {
        _description = description;
        return this;
    }

    @Override
    public String toString() {
        return "Message {"
                + " sequence: " + _sequence
                + ", timestamp: " + _timestamp
                + " }";
    }
}
