package com.challenge.android.radio_t.parser;

import com.challenge.android.radio_t.model.Content;
import com.challenge.android.radio_t.model.PodcastItem;
import com.novoda.sax.Element;
import com.novoda.sax.ElementListener;
import com.novoda.sexp.finder.ElementFinder;
import com.novoda.sexp.finder.ElementFinderFactory;
import com.novoda.sexp.marshaller.AttributeMarshaller;
import com.novoda.sexp.parser.ParseWatcher;
import com.novoda.sexp.parser.Parser;

import org.xml.sax.Attributes;

public class PodcastItemParser implements Parser<PodcastItem> {
    private static final String TAG_TITLE = "title";
    private static final String TAG_LINK = "link";
    private static final String TAG_PUB_DATE = "pubDate";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_AUTHOR = "author";
    private static final String TAG_CONTENT = "content";
    private static final String TAG_SUBTITLE = "subtitle";
    private static final String TAG_KEYWORDS = "keywords";
    private static final String TAG_ITUNES_NAMESPACE = "http://www.itunes.com/dtds/podcast-1.0.dtd";
    private static final String TAG_MEDIA_NAMESPACE = "http://search.yahoo.com/mrss/";
    private static final String TAG_ATTR_URL = "url";
    private static final String TAG_ATTR_FILE_SIZE = "fileSize";
    private static final String TAG_ATTR_TYPE = "type";

    private final ElementFinder<String> titleFinder;
    private final ElementFinder<String> linkFinder;
    private final ElementFinder<String> pubDateFinder;
    private final ElementFinder<String> descriptionFinder;
    private final ElementFinder<String> authorFinder;
    private final ElementFinder<String> subtitleFinder;
    private final ElementFinder<String> keywordsFinder;
    private final ElementFinder<Content> contentFinder;

    private ItemHolder itemHolder;
    private ParseWatcher<PodcastItem> listener;

    public PodcastItemParser(ElementFinderFactory factory) {
        titleFinder = factory.getStringFinder();
        linkFinder = factory.getStringFinder();
        pubDateFinder = factory.getStringFinder();
        descriptionFinder = factory.getStringFinder();
        authorFinder = factory.getStringFinder();
        subtitleFinder = factory.getStringFinder();
        keywordsFinder = factory.getStringFinder();
        contentFinder = factory.getAttributeFinder(new StringAttributeMarshaller(),
                TAG_ATTR_URL, TAG_ATTR_FILE_SIZE, TAG_ATTR_TYPE);
    }

    @Override
    public void parse(Element element, final ParseWatcher<PodcastItem> listener) {
        this.listener = listener;
        element.setElementListener(itemParseListener);
        titleFinder.find(element, TAG_TITLE);
        linkFinder.find(element, TAG_LINK);
        pubDateFinder.find(element, TAG_PUB_DATE);
        descriptionFinder.find(element, TAG_DESCRIPTION);
        authorFinder.find(element, TAG_AUTHOR);
        subtitleFinder.find(element, TAG_ITUNES_NAMESPACE, TAG_SUBTITLE);
        keywordsFinder.find(element, TAG_ITUNES_NAMESPACE, TAG_KEYWORDS);
        contentFinder.find(element, TAG_MEDIA_NAMESPACE, TAG_CONTENT);
    }

    private final ElementListener itemParseListener = new ElementListener() {
        @Override
        public void start(Attributes attributes) {
            itemHolder = new ItemHolder();
        }

        @Override
        public void end() {
            itemHolder.title = titleFinder.getResultOrThrow();
            itemHolder.link = linkFinder.getResultOrThrow();
            itemHolder.pubDate = pubDateFinder.getResultOrThrow();
            itemHolder.description = descriptionFinder.getResultOrThrow();
            itemHolder.author = authorFinder.getResultOrThrow();
            itemHolder.content = contentFinder.getResultOrThrow();
            itemHolder.subtitle = subtitleFinder.getResultOrThrow();
            itemHolder.keywords = keywordsFinder.getResultOrThrow();

            listener.onParsed(itemHolder.asItem());
        }
    };

    private static class ItemHolder {
        public String title;
        public String link;
        public String pubDate;
        public String description;
        public String author;
        public Content content;
        public String subtitle;
        public String keywords;

        public PodcastItem asItem() {
            return new PodcastItem(title, link, pubDate, description, author, content, subtitle, keywords);
        }
    }

    private static class StringAttributeMarshaller implements AttributeMarshaller<Content> {
        @Override
        public Content marshall(String... input) {
            String url = input[0];
            long fileSize = 0;
            try {
                fileSize = Long.parseLong(input[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String type = input[2];

            return new Content(url, fileSize, type);
        }
    }
}
