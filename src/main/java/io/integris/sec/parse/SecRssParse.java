package io.integris.sec.parse;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import io.integris.sec.message.bean.Message;

public class SecRssParse {

    private final XMLInputFactory _factory;

    public SecRssParse() {
        _factory = XMLInputFactory.newInstance();
    }

    public Publisher<Message> publish(Message source) {
        return sink -> publish(source, sink);
    }

    private void publish(Message source, Subscriber<? super Message> sink) {
        try {
            new FeedParser(_factory.createXMLStreamReader(source.getInputStream()), source, sink).parse();
            sink.onComplete();
        } catch (XMLStreamException ex) {
            sink.onError(ex);
        }
    }

    @FunctionalInterface
    private interface ParseMethod {
        boolean parse() throws XMLStreamException;
    }

    private class FeedParser {

        private static final String EDGAR_NS = "http://www.sec.gov/Archives/edgar";
        private static final String RSS_EL = "rss";
        private static final String CHANNEL_EL = "channel";
        private static final String ITEM_EL = "item";
        private static final String XBRL_FILING_EL = "xbrlFiling";
        private static final String XBRL_FILES_EL = "xbrlFiles";
        private static final String XBRL_FILE_EL = "xbrlFile";
        private static final String TYPE_AT = "type";
        private static final String URL_AT = "url";
        private static final String DESCRIPTION_AT = "description";

        private final XMLStreamReader _reader;
        private final Message _source;
        private final Subscriber<? super Message> _sink;

        public FeedParser(XMLStreamReader reader, Message source, Subscriber<? super Message> sink) {
            _reader = reader;
            _source = source;
            _sink = sink;
        }

        public void parse() throws XMLStreamException {
            parse(this::parseDocument);
        }

        private boolean parseDocument() throws XMLStreamException {
            switch (_reader.getEventType()) {
            case XMLStreamConstants.END_DOCUMENT:
                return false;

            case XMLStreamConstants.START_ELEMENT:
                if (isRssElement(RSS_EL)) {
                    parse(this::parseRss);
                } else {
                    // Skip document
                    return false;
                }
            }

            return true;
        }

        private boolean parseRss() throws XMLStreamException {
            switch (_reader.getEventType()) {
            case XMLStreamConstants.END_ELEMENT:
                return !isRssElement(RSS_EL);

            case XMLStreamConstants.START_ELEMENT:
                if (isRssElement(CHANNEL_EL)) {
                    parse(this::parseChannel);
                }
            }

            return true;
        }

        private boolean parseChannel() throws XMLStreamException {
            switch (_reader.getEventType()) {
            case XMLStreamConstants.END_ELEMENT:
                return !isRssElement(CHANNEL_EL);

            case XMLStreamConstants.START_ELEMENT:
                if (isRssElement(ITEM_EL)) {
                    parse(this::parseItem);
                }
            }

            return true;
        }

        private boolean parseItem() throws XMLStreamException {
            switch (_reader.getEventType()) {
            case XMLStreamConstants.END_ELEMENT:
                return !isRssElement(ITEM_EL);

            case XMLStreamConstants.START_ELEMENT:
                if (isEdgarElement(XBRL_FILING_EL)) {
                    parse(this::parseFiling);
                }
            }

            return true;
        }

        private boolean parseFiling() throws XMLStreamException {
            switch (_reader.getEventType()) {
            case XMLStreamConstants.END_ELEMENT:
                return !isRssElement(XBRL_FILING_EL);

            case XMLStreamConstants.START_ELEMENT:
                if (isEdgarElement(XBRL_FILES_EL)) {
                    parse(this::parseFiles);
                }
            }

            return true;
        }

        private boolean parseFiles() throws XMLStreamException {
            switch (_reader.getEventType()) {
            case XMLStreamConstants.END_ELEMENT:
                return !isRssElement(XBRL_FILES_EL);

            case XMLStreamConstants.START_ELEMENT:
                if (isEdgarElement(XBRL_FILE_EL)) {
                    Message outbound = new Message(_source);
                    outbound.setType(getEdgarAtribute(TYPE_AT));
                    outbound.setUrl(getEdgarAtribute(URL_AT));
                    outbound.setDescription(getEdgarAtribute(DESCRIPTION_AT));
                    _sink.onNext(outbound);
                }
            }

            return true;
        }

        private void parse(ParseMethod method) throws XMLStreamException {
            while (method.parse() && _reader.hasNext()) {
                _reader.next();
            }
        }

        private String getEdgarAtribute(String localName) {
            return _reader.getAttributeValue(EDGAR_NS, localName);
        }

        private boolean isRssElement(String localName) {
            String namespace = _reader.getNamespaceURI();
            return localName.equals(_reader.getLocalName());
        }

        private boolean isEdgarElement(String localName) {
            return EDGAR_NS.equals(_reader.getNamespaceURI())
                    && localName.equals(_reader.getLocalName());
        }
    }
}
