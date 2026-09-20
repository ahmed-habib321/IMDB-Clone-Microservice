package org.example.sharedmodule.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.Marker;

public class OutboxSqlTurboFilter extends TurboFilter {

    private static final String SQL_LOGGER = "org.hibernate.SQL";
    private static final String OUTBOX_TABLE = "imdb_outbox";

    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        if (logger == null || level == null) {
            return FilterReply.NEUTRAL;
        }
        if (!SQL_LOGGER.equals(logger.getName()) || !level.isGreaterOrEqual(Level.DEBUG)) {
            return FilterReply.NEUTRAL;
        }
        if (referencesOutbox(format, params)) {
            return FilterReply.DENY;
        }
        return FilterReply.NEUTRAL;
    }

    private boolean referencesOutbox(String format, Object[] params) {
        if (format != null && format.toLowerCase().contains(OUTBOX_TABLE)) {
            return true;
        }
        if (params != null) {
            for (Object param : params) {
                if (param != null && param.toString().toLowerCase().contains(OUTBOX_TABLE)) {
                    return true;
                }
            }
        }
        return false;
    }
}