package com.tradier.raven.logging;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import io.dropwizard.configuration.ConfigurationException;

import java.io.IOException;

import org.junit.Test;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import io.dropwizard.logging.async.AsyncLoggingEventAppenderFactory;
import io.dropwizard.logging.filter.ThresholdLevelFilterFactory;
import io.dropwizard.logging.layout.DropwizardLayoutFactory;

public class RavenAppenderFactoryTest {

    @Test
    public void hasValidDefaults() throws IOException, ConfigurationException {
        final RavenAppenderFactory factory = new RavenAppenderFactory();

        assertThat("default dsn is unset", factory.getDsn(), nullValue());
        assertThat("default additional fields are empty", factory.getTags(), nullValue());
    }
	
    @Test(expected = NullPointerException.class)
    public void buildRavenAppenderShouldFailWithNullContext() {
        new RavenAppenderFactory().build(null, "", null, null, null);
    }
	
    @Test
    public void buildRavenAppenderShouldWorkWithValidConfiguration() {
        final RavenAppenderFactory raven = new RavenAppenderFactory();
        final String dsn = "https://user:pass@app.getsentry.com/id";

        ThresholdLevelFilterFactory levelFilterFactory = new ThresholdLevelFilterFactory();
        DropwizardLayoutFactory layoutFactory = new DropwizardLayoutFactory();
        AsyncLoggingEventAppenderFactory asyncAppenderFactory = new AsyncLoggingEventAppenderFactory();
        Appender<ILoggingEvent> appender = raven.build(
                new LoggerContext(), dsn, layoutFactory, levelFilterFactory, asyncAppenderFactory);

        assertThat(appender, instanceOf(AsyncAppender.class));
    }

}
