/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rexec.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

import biz.isphere.rexec.Messages;
import biz.isphere.rexec.preferences.Preferences;

/**
 * This class handles the requests that have been received by the Rexec server.
 */
public class RexecRequestHandler extends Thread {

    private static final int BUFFER_SIZE = 4096;

    private static final byte NULL_CHAR = 0;
    private static final byte ERROR_CHAR = 1;
    private static final String EOF = "\n";

    private State state;

    private final Socket client;

    private Socket auxSocket;

    private final boolean waitForAdditionalData = false;
    private boolean captureCommandOutput = true;

    /**
     * Constructs a new Rexec request handler.
     * 
     * @param client - client socket
     */
    public RexecRequestHandler(Socket client) {
        super();

        this.client = client;
        this.auxSocket = null;
        setName("Rexec-Handler: " + client.getInetAddress().getHostAddress());
    }

    /**
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {

        String user = null;
        String password = null;
        String command = null;
        String data = null;

        try {

            String remoteAddr = null;
            int remotePort = -1;

            int clientReadTimeout = Preferences.getInstance().getSocketReadTimeout();
            client.setSoTimeout(clientReadTimeout);
            client.setSoLinger(true, 5);

            InputStream inputStream = client.getInputStream();
            OutputStream outputStream = client.getOutputStream();
            OutputStream errorStream = outputStream;

            state = State.CLIENT_PORT;

            while (state != State.FINISHED) {

                switch (state) {
                case CLIENT_PORT:
                    String portNumber = getNextToken(inputStream);
                    if (isNumeric(portNumber)) {
                        remoteAddr = client.getInetAddress().getHostAddress();
                        remotePort = Integer.parseInt(portNumber);
                        if (remoteAddr != null && remotePort > 0) {
                            auxSocket = new Socket(remoteAddr, remotePort);
                            auxSocket.setSoLinger(true, 5);
                            errorStream = auxSocket.getOutputStream();
                        }
                    }
                    state = State.USER;
                    break;
                case USER:
                    user = getNextToken(inputStream);
                    state = State.PASSWORD;
                    break;
                case PASSWORD:
                    password = getNextToken(inputStream);
                    state = State.COMMAND;
                    break;
                case COMMAND:
                    command = getNextToken(inputStream);
                    if (waitForAdditionalData) {
                        state = State.DATA;
                    } else {
                        state = State.FINISHED;
                    }
                    break;
                case DATA:
                    data = getNextToken(inputStream);
                    state = State.FINISHED;
                    break;
                case FINISHED:
                    break;
                }

            }

            String message = validateUser(user, password);
            if (message != null) {
                failure(outputStream, errorStream, message);
            } else {
                File cmdLog = null;
                try {
                    Process process;
                    if (captureCommandOutput) {
                        cmdLog = File.createTempFile("RexecRequestHandler_", "");
                        process = Runtime.getRuntime().exec(command + " > " + cmdLog);
                    } else {
                        process = Runtime.getRuntime().exec(command);
                    }
                    success(outputStream);

                } catch (Throwable e) {
                    failure(outputStream, errorStream, e.getLocalizedMessage());
                } finally {
                    if (cmdLog != null) {
                        cmdLog.delete();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeSocket(auxSocket);
            closeSocket(client);
        }
    }

    /**
     * @param outputStream
     * @param localizedMessage
     * @throws IOException
     */
    private void failure(OutputStream outputStream, OutputStream errorStream, String message) throws IOException {
        outputStream.write(ERROR_CHAR);
        errorStream.write(message.getBytes());
        errorStream.write(EOF.getBytes());
        errorStream.flush();
    }

    /**
     * @param outputStream
     * @throws IOException
     */
    private void success(OutputStream outputStream) throws IOException {
        outputStream.write(NULL_CHAR);
        outputStream.flush();
    }

    private void returnLogData(OutputStream outputStream, File cmdLog) {

        if (cmdLog == null) {
            return;
        }

        FileInputStream inStream = null;

        try {

            waitForFile(cmdLog);

            byte[] buffer = new byte[4096];
            int count = 0;
            boolean hasData = false;
            inStream = new FileInputStream(cmdLog);
            while ((count = inStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, count);
                hasData = true;
            }

            if (hasData) {
                outputStream.write(EOF.getBytes());
            }

        } catch (Throwable e) {
            // ignore any errors
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e1) {
                    // ignore errors on close
                }
            }
        }
    }

    private void waitForFile(File cmdLog) {
        int timeOut = 1000;
        while ((timeOut > 0) && (timeOut > 0 && (!cmdLog.exists() || cmdLog.length() == 0))) {
            try {
                Thread.sleep(100);
                timeOut = timeOut - 100;
            } catch (InterruptedException e) {
                timeOut = 0;
            }
        }
    }

    /**
     * @param user
     * @param password
     * @return
     * @throws IOException
     */
    private String validateUser(String user, String password) {

        if (user == null && password == null) {
            return null;
        } else if (user == null) {
            return Messages.Login_user_name_is_missing;
        }

        return null;
    }

    /**
     * @param portNumber
     * @return
     */
    private boolean isNumeric(String portNumber) {

        if (portNumber == null) {
            return false;
        }

        try {
            Integer.parseInt(portNumber);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param inBuffer
     * @param inSize
     * @return
     * @throws IOException
     */
    private String getNextToken(InputStream inputStream) throws IOException {

        int offset = 0;
        byte[] tmpBuffer = new byte[BUFFER_SIZE];

        try {

            while (inputStream.read(tmpBuffer, offset, 1) > 0) {
                if (tmpBuffer[offset] == NULL_CHAR) {
                    break;
                } else {
                    offset++;
                }
            }

        } catch (SocketTimeoutException e) {
            // ignore timeout
        }

        byte[] tmpData = new byte[offset];
        System.arraycopy(tmpBuffer, 0, tmpData, 0, tmpData.length);

        String inData = new String(tmpData);

        return inData;
    }

    /**
     * Closes a given socket.
     * 
     * @param socket - socket that is closed
     */
    private void closeSocket(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                // ignore error on close
            }
        }
    }

    private enum State {
        CLIENT_PORT,
        USER,
        PASSWORD,
        COMMAND,
        DATA,
        FINISHED
    }

}
