package com.challenge.android.radio_t.parser;

import com.challenge.android.radio_t.model.Channel;
import com.challenge.android.radio_t.model.PodcastItem;
import com.novoda.sax.Element;
import com.novoda.sax.ElementListener;
import com.novoda.sexp.finder.ElementFinder;
import com.novoda.sexp.finder.ElementFinderFactory;
import com.novoda.sexp.marshaller.AttributeMarshaller;
import com.novoda.sexp.parser.ParseWatcher;
import com.novoda.sexp.parser.Parser;

import org.xml.sax.Attributes;

import java.util.ArrayList;
import java.util.List;

public class PodcastChannelParser implements Parser<Channel> {
    private static final String TAG_ITEM = "item";
    private static final String TAG_TITLE = "title";
    private static final String TAG_LINK = "link";
    private static final String TAG_THUMBNAIL = "thumbnail";
    private static final String TAG_KEYWORDS = "keywords";
    private static final String TAG_MEDIA_NAMESPACE = "http://search.yahoo.com/mrss/";
    private static final String TAG_IMAGE_ATTR = "url";

    private final ElementFinder<PodcastItem> podcastItemFinder;
    private final ElementFinder<String> titleFinder;
    private final ElementFinder<String> linkFinder;
    private final ElementFinder<String> thumbnailFinder;
    private final ElementFinder<String> keywordsFinder;

    private ParseWatcher<Channel> listener;
    private ChannelHolder channelHolder;

    public PodcastChannelParser(ElementFinderFactory factory) {
        this.podcastItemFinder = factory.getListElementFinder(new PodcastItemParser(factory), parseWatcher);
        this.titleFinder = factory.getStringFinder();
        this.linkFinder = factory.getStringFinder();
        this.thumbnailFinder = factory.getAttributeFinder(new StringAttributeMarshaller(), TAG_IMAGE_ATTR);
        this.keywordsFinder = factory.getStringFinder();
    }

    private final ParseWatcher<PodcastItem> parseWatcher = new ParseWatcher<PodcastItem>() {
        @Override
        public void onParsed(PodcastItem item) {
            channelHolder.podcastItemList.add(item);
        }
    };

    @Override
    public void parse(Element element, final ParseWatcher<Channel> listener) {
        this.listener = listener;
        element.setElementListener(channelParseListener);
        podcastItemFinder.find(element, TAG_ITEM);
        titleFinder.find(element, TAG_TITLE);
        linkFinder.find(element, TAG_LINK);
        thumbnailFinder.find(element, TAG_MEDIA_NAMESPACE, TAG_THUMBNAIL);
        keywordsFinder.find(element, TAG_MEDIA_NAMESPACE, TAG_KEYWORDS);
    }

    private final ElementListener channelParseListener = new ElementListener() {
        @Override
        public void start(Attributes attributes) {
            channelHolder = new ChannelHolder();
        }

        @Override
        public void end() {
            channelHolder.title = titleFinder.getResultOrThrow();
            channelHolder.link = linkFinder.getResultOrThrow();
            channelHolder.thumbnail = thumbnailFinder.getResultOrThrow();
            channelHolder.keywords = keywordsFinder.getResultOrThrow();

            listener.onParsed(channelHolder.asChannel());
        }
    };

    private static class ChannelHolder {
        public String title;
        public String link;
        public String thumbnail;
        public String keywords;
        public List<PodcastItem> podcastItemList;

        public ChannelHolder() {
            podcastItemList = new ArrayList<>();
        }

        public Channel asChannel() {
            return new Channel(title, link, thumbnail, keywords, podcastItemList);
        }
    }

    private static class StringAttributeMarshaller implements AttributeMarshaller<String> {
        @Override
        public String marshall(String... input) {
            if (input.length > 0) return input[0];
            else return null;
        }
    }
}
