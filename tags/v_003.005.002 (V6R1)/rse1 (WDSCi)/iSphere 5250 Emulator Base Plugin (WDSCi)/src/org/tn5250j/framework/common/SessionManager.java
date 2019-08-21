/*
 * @(#)SessionManager.java
 * Copyright:    Copyright (c) 2001 - 2004
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
package org.tn5250j.framework.common;

import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import org.tn5250j.Session5250;
import org.tn5250j.SessionConfig;
import org.tn5250j.SessionGUI;
import org.tn5250j.TN5250jConstants;
import org.tn5250j.event.SessionConfigEvent;
import org.tn5250j.event.SessionConfigListener;
import org.tn5250j.interfaces.SessionManagerInterface;
import org.tn5250j.settings.ColorProperty;
import org.tn5250j.tools.logging.TN5250jLogFactory;
import org.tn5250j.tools.logging.TN5250jLogger;

/**
 * The SessionManager is the central repository for access to all sessions. The
 * SessionManager contains a list of all Session objects available.
 */
public class SessionManager implements SessionManagerInterface, SessionConfigListener {

    static private Sessions sessions;
    static private Vector configs;

    private TN5250jLogger log = TN5250jLogFactory.getLogger(this.getClass());
    /**
     * A handle to the unique SessionManager class
     */
    static private SessionManager _instance;

    /**
     * The constructor is made protected to allow overriding.
     */
    protected SessionManager() {
        if (_instance == null) {
            // initialize the settings information
            initialize();
            // set our instance to this one.
            _instance = this;
        }
    }

    /**
     * 
     * @return The unique instance of this class.
     */
    static public SessionManager instance() {

        if (_instance == null) {
            _instance = new SessionManager();
        }
        return _instance;

    }

    private void initialize() {
        log.info("New session Manager initialized");
        sessions = new Sessions();
        configs = new Vector(3);

    }

    public Sessions getSessions() {
        return sessions;
    }

    public void closeSession(String sessionName) {

        SessionGUI session = (sessions.item(sessionName)).getGUI();
        if (session != null) closeSession(session);

    }

    public void closeSession(SessionGUI sessionObject) {

        sessionObject.closeDown();
        sessions.removeSession((sessionObject).getSession());

    }

    public Session5250 openSession(Properties sesConnProps, String configurationResource, String sessionName) {
        return this.openSession(sesConnProps, configurationResource, sessionName, "");
    }

    public synchronized Session5250 openSession(Properties sesConnProps, String configurationResource, String sessionName, String sessionTheme) {

        if (sessionName == null)
            sesConnProps.put(TN5250jConstants.SESSION_TERM_NAME, sesConnProps.getProperty(TN5250jConstants.SESSION_HOST));
        else
            sesConnProps.put(TN5250jConstants.SESSION_TERM_NAME, sessionName);

        if (configurationResource == null) configurationResource = "";

        sesConnProps.put(TN5250jConstants.SESSION_CONFIG_RESOURCE, configurationResource);

        Enumeration e = configs.elements();
        SessionConfig useConfig = null;

        while (e.hasMoreElements()) {
            SessionConfig conf = (SessionConfig)e.nextElement();
            if (conf.getSessionName().equals(sessionName) && conf.getSessionTheme().equals(sessionTheme)) {
                useConfig = conf;
            }
        }

        if (useConfig == null) {
            useConfig = new SessionConfig(configurationResource, sessionName, sessionTheme);
            useConfig.addSessionConfigListener(this);
            configs.add(useConfig);
        }

        Session5250 newSession = new Session5250(sesConnProps, configurationResource, sessionName, useConfig);
        sessions.addSession(newSession);
        return newSession;

    }

    /**
     * Propagates the session attributes that have been changed for a session to
     * all remaining sessions with a different theme. Color attributes are not
     * propagated, because these properties are theme specific.
     */
    public void onConfigChanged(SessionConfigEvent changeEvent) {

        if (this.equals(changeEvent.getSource())) {
            return;
        }

        String sessionTheme = changeEvent.getSessionTheme();

        System.out.println("SessionManager.onConfigChanged(): theme=" + sessionTheme);

        String propertyName;
        Object oldValue;
        Object newValue;

        Enumeration e = configs.elements();
        while (e.hasMoreElements()) {
            SessionConfig conf = (SessionConfig)e.nextElement();
            if (!conf.getSessionTheme().equals(sessionTheme)) {

                propertyName = changeEvent.getPropertyName();
                if (!ColorProperty.isColorProperty(propertyName)) {

                    oldValue = changeEvent.getOldValue();
                    newValue = changeEvent.getNewValue();

                    changeEvent = new SessionConfigEvent(this, propertyName, oldValue, newValue, sessionTheme);
                    conf.firePropertyChange(this, propertyName, oldValue, newValue);
                }
            }
        }
    }

    /**
     * Convenience method to add a session that is instatiated outside of
     * SessionManager. An example would be the ProtocolBean.
     * 
     * @param newSession
     */
    public void addSession(Session5250 newSession) {

        sessions.addSession(newSession);
    }
}