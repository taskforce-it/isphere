/*
 * @(#)Log4jLogger.java
 * @author  Kenneth J. Pouncey
 *
 * Copyright:    Copyright (c) 2001, 2002, 2003
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software; see the file COPYING.  If not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA
 *
 */
package org.tn5250j.tools.logging;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * An implementation of the TN5250jLogger to provide log4j logger instances.
 */
public class Log4jLogger extends TN5250jLogger {

    private Logger log;

    Log4jLogger() {

    }

    @Override
    public void initialize(final String clazz) {
        log = Logger.getLogger(clazz);
    }

    // printing methods:
    @Override
    public void debug(Object message) {
        log.debug(message);
    }

    @Override
    public void info(Object message) {
        log.info(message);
    }

    @Override
    public void warn(Object message) {
        log.warn(message);

    }

    @Override
    public void warn(Object message, Throwable throw1) {
        log.warn(message, throw1);
    }

    @Override
    public void error(Object message) {
        log.error(message);

    }

    @Override
    public void fatal(Object message) {
        log.fatal(message);

    }

    @Override
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    @Override
    public void setLevel(int newLevel) {

        switch (newLevel) {
        case OFF:
            log.setLevel(Level.OFF);

            break;

        case DEBUG:
            log.setLevel(Level.DEBUG);
            break;

        case INFO:
            log.setLevel(Level.INFO);
            break;

        case WARN:
            log.setLevel(Level.WARN);
            break;

        case ERROR:
            log.setLevel(Level.ERROR);
            break;

        case FATAL:
            log.setLevel(Level.FATAL);
            break;

        }

    }

    @Override
    public int getLevel() {

        switch (log.getLevel().toInt()) {

        case (Priority.DEBUG_INT):
            return DEBUG;

        case (Priority.INFO_INT):
            return INFO;

        case (Priority.WARN_INT):
            return WARN;

        case (Priority.ERROR_INT):
            return ERROR;

        case (Priority.FATAL_INT):
            return FATAL;
        default:
            return WARN;

        }

    }
}