package com.challenge.android.radio_t.parser;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.challenge.android.radio_t.model.Channel;
import com.novoda.sexp.Instigator;
import com.novoda.sexp.RootTag;
import com.novoda.sexp.SimpleEasyXmlParser;
import com.novoda.sexp.SimpleTagInstigator;
import com.novoda.sexp.finder.ElementFinder;
import com.novoda.sexp.finder.ElementFinderFactory;
import com.novoda.sexp.parser.ParseFinishWatcher;

public class RssParser {
    private ElementFinder<Channel> elementFinder;
    private OnRssParsedListener onRssParsedListener;

    public void parse(@NonNull String xml, @Nullable OnRssParsedListener onRssParsedListener) {
        this.onRssParsedListener = onRssParsedListener;

        ElementFinderFactory factory = SimpleEasyXmlParser.getElementFinderFactory();
        elementFinder = factory.getTypeFinder(new PodcastChannelParser(factory));

        Instigator instigator = new PodcastInstigator(elementFinder, finishWatcher);
        SimpleEasyXmlParser.parse(xml, instigator);
    }

    private ParseFinishWatcher finishWatcher = new ParseFinishWatcher() {
        @Override
        public void onFinish() {
            Channel channel = elementFinder.getResult();
            if (onRssParsedListener != null) {
                if (channel == null) onRssParsedListener.onFailed(null);
                else onRssParsedListener.onRssParsed(channel);
            }
        }
    };

    public static class PodcastInstigator extends SimpleTagInstigator {

        public PodcastInstigator(ElementFinder<?> elementFinder, ParseFinishWatcher parseFinishWatcher) {
            super(elementFinder, "channel", parseFinishWatcher);
        }

        @Override
        public RootTag getRootTag() {
            return RootTag.create("rss");
        }
    }

    public interface OnRssParsedListener {
        void onRssParsed(@NonNull Channel channel);

        void onFailed(@Nullable String reason);
    }
}
