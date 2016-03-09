/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rexec.internal;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import biz.isphere.rexec.preferences.Preferences;

/**
 * This class implements a simple Rexec server, listening on port 512 for
 * incoming requests. Use RUNRMTCMD to send requests from an IBM i.
 * <p>
 * Example:
 * 
 * <pre>
 * RUNRMTCMD CMD('cmd /C "start c:\Temp\readme.txt"')   
 *   RMTLOCNAME('10.115.11.228' *IP)
 * </pre>
 */
public class RexecServer extends Thread {

    private int listenerPort;

    private ServerSocket serverSocket;

    // public static void main(String[] args) {
    //
    // RexecServer server = new RexecServer();
    // server.run();
    // }

    /**
     * Constructs a new RexecServer object.
     */
    public RexecServer() {
        super();

        listenerPort = Preferences.getInstance().getListenerPort();
        setName("Rexec-Server: " + listenerPort);
    }

    /**
     * Starts the Rexec server.
     */
    public void run() {

        serverSocket = null;
        try {
            serverSocket = new ServerSocket(listenerPort);
            while (serverSocket != null) {
                waitForIncomingConnections();
            }
        } catch (IOException e) {
            // ignore exception on close
        } finally {
            closeSocket();
        }
    }

    public void shutdown() {
        closeSocket();
    }

    /**
     * Waits for incoming requests. Starts a new handler thread for each
     * incoming reqtest.
     * 
     * @throws IOException
     */
    private void waitForIncomingConnections() throws IOException {

        Socket client = serverSocket.accept();
        if (client != null) {
            RexecRequestHandler handler = new RexecRequestHandler(client);
            handler.start();
        }
    }

    /**
     * Closes a given socket.
     * 
     * @param socket - socket that is closed
     */
    private void closeSocket() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
                serverSocket = null;
            } catch (IOException e) {
                // ignore exceptions on close
            }
        }
    }

}
