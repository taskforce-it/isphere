/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rexec.internal;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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
public class RexecServer {

    private boolean isEnabled = false;

    private final int listenerPort = 512;

    public static void main(String[] args) {

        RexecServer server = new RexecServer();
        server.run();
    }

    /**
     * Constructs a new RexecServer object.
     */
    public RexecServer() {
        isEnabled = true;
    }

    /**
     * Starts the Rexec server.
     */
    private void run() {

        if (!isEnabled) {
            return;
        }

        ServerSocket server = null;
        try {
            server = new ServerSocket(listenerPort);
            while (isEnabled) {
                waitForIncomingConnections(server);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeSocket(server);
        }
    }

    /**
     * Waits for incoming requests. Starts a new handler thread for each
     * incoming reqtest.
     * 
     * @throws IOException
     */
    private void waitForIncomingConnections(ServerSocket server) throws IOException {

        Socket client = server.accept();
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
    private void closeSocket(ServerSocket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                // ignore exceptions on close
            }
        }
    }

}
