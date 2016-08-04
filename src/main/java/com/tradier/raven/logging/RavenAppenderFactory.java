package com.tradier.raven.logging;

import static com.google.common.base.Preconditions.checkNotNull;

import com.getsentry.raven.logback.SentryAppender;
import io.dropwizard.logging.AbstractAppenderFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import io.dropwizard.logging.async.AsyncAppenderFactory;
import io.dropwizard.logging.filter.LevelFilterFactory;
import io.dropwizard.logging.layout.LayoutFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;



@JsonTypeName("raven")
public class RavenAppenderFactory extends AbstractAppenderFactory {

    @JsonProperty
    private String dsn = null;

    @JsonProperty
    private String tags = null;

    @JsonProperty
    private String release;

    @JsonProperty
    private String environment;

    public String getDsn() {
        return dsn;
    }

    public void setDsn(String dsn) {
        this.dsn = dsn;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public void addDroppingRavenLoggingFilter(Appender<ILoggingEvent> appender) {
        Filter<ILoggingEvent> filter = new DroppingRavenLoggingFilter();
        filter.start();
        appender.addFilter(filter);
    }

    @Override
    public Appender build(LoggerContext context, String applicationName, LayoutFactory layoutFactory, 
            LevelFilterFactory levelFilterFactory, AsyncAppenderFactory asyncAppenderFactory) {
        checkNotNull(context);

        final SentryAppender appender = new SentryAppender();
        appender.setName("dropwizard-raven");
        appender.setContext(context);
        appender.setDsn(dsn);
        if (release != null) {
            appender.setRelease(release);
        }
        if (environment != null) {
            appender.setEnvironment(environment);
        }
        if (tags != null) {
            appender.setTags(tags);
        }

        appender.start();
        appender.addFilter(levelFilterFactory.build(threshold));
        Appender<ILoggingEvent> asyncAppender = wrapAsync(appender, asyncAppenderFactory, context);
        addDroppingRavenLoggingFilter(asyncAppender);

        return asyncAppender;
    }

    public static class DroppingRavenLoggingFilter extends Filter<ILoggingEvent> {

        @Override
        public FilterReply decide(ILoggingEvent event) {
            if (event.getLoggerName().startsWith("com.getsentry.raven")) {
                return FilterReply.DENY;
            } else {
                return FilterReply.ACCEPT;
            }
        }
    }
}
