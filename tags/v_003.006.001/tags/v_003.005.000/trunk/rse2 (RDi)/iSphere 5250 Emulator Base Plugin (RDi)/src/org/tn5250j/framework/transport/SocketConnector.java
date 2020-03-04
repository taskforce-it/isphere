/**
 * @(#)SocketConnector.java
 * @author Stephen M. Kennedy
 *
 * Copyright:    Copyright (c) 2001
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
package org.tn5250j.framework.transport;

import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.tn5250j.TN5250jConstants;
import org.tn5250j.tools.logging.TN5250jLogFactory;
import org.tn5250j.tools.logging.TN5250jLogger;

public class SocketConnector {

    private String sslType = null;

    private static TN5250jLogger logger = TN5250jLogFactory.getLogger(SocketConnector.class);

    /**
     * Creates a new instance that creates a plain socket by default.
     */
    public SocketConnector() {
    }

    /**
     * Set the type of SSL connection to use. Specify null or an empty string to
     * use a plain socket.
     * 
     * @param type The SSL connection type
     * @see org.tn5250j.framework.transport.SSLConstants
     */
    public void setSSLType(String type) {
        sslType = type;
    }

    /**
     * Create a new client Socket to the given destination and port. If an SSL
     * socket type has not been specified <i>(by setSSLType(String))</i>, then a
     * plain socket will be created. Otherwise, a new SSL socket of the
     * specified type will be created.
     * 
     * @param destination
     * @param port
     * @return a new client socket, or null if
     */
    public Socket createSocket(String destination, int port) throws Exception {

        Socket socket = null;

        if (sslType == null || sslType.trim().length() == 0 || sslType.toUpperCase().equals(TN5250jConstants.SSL_TYPE_NONE)) {
            logger.info("Creating Plain Socket");

            // Use Socket Constructor!!! SocketFactory for jdk 1.4
            socket = new Socket(destination, port);
        } else {

            // SSL SOCKET

            logger.info("Creating SSL [" + sslType + "] Socket");

            SSLInterface sslIf = null;

            sslIf = createSocketInterface();
            if (sslIf != null) {
                sslIf.init(sslType);
                socket = sslIf.createSSLSocket(destination, port);
            }
        }

        if (socket == null) {
            logger.warn("No socket was created");
        }

        return socket;
    }

    public static String getDefaultSSLProtocol() {

        Set<String> sslProtocols = new HashSet<String>();
        sslProtocols.addAll(Arrays.asList(getSupportedSSLProtocols()));

        for (String sslProtocol : TN5250jConstants.SSL_PROTOCOL_HIERARCHY) {
            if (sslProtocols.contains(sslProtocol)) {
                return sslProtocol;
            }
        }

        return null;
    }

    public static String[] getSupportedSSLProtocols() {

        try {

            SSLInterface sslIf = createSocketInterface();
            return sslIf.getSupportedProtocols();

        } catch (Exception e) {
            logger.error("Failed to retrieve supported SSL protocols. " + "Message is [" + e.getMessage() + "]");
        }

        return null;
    }

    private static SSLInterface createSocketInterface() throws ClassNotFoundException, IllegalAccessException, InstantiationException {

        String sslImplClassName = "org.tn5250j.framework.transport.SSL.SSLImplementation";
        Class<?> c = Class.forName(sslImplClassName);
        SSLInterface sslIf = (SSLInterface)c.newInstance();

        return sslIf;
    }

}