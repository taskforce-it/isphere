/*
    JRexec -- REXEC client in pure Java.
    Copyright (C) 1998 JCraft Inc.

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Library General Public
    License as published by the Free Software Foundation; either
    version 2 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Library General Public License for more details.

    You should have received a copy of the GNU Library General Public
    License along with this library; if not, write to the Free
    Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package com.jcraft.jrexec;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class JRexec implements Runnable {

    private static final int REXECPORT = 512;

    private Thread thread = null;

    private InputStream data = null;
    private InputStream in = null;
    private OutputStream out = null;
    private boolean devnull = true;
    private Socket socket = null;

    private Socket auxsocket = null;
    private InputStream auxin = null;
    private OutputStream auxout = null;

    private String user = null;
    private String host = null;
    private String passwd = null;
    private String command = null;

    private boolean auxport = true;

    private java.util.Vector listeners = null;

    public JRexec(String user, String host, String passwd, String command, boolean auxport) throws JRexecException {
        this.user = user;
        this.host = host;
        this.passwd = passwd;
        this.command = command;
        this.auxport = auxport;

        try {
            socket = new Socket(host, REXECPORT);
            socket.setSoTimeout(500);
            socket.setSoLinger(true, 5);
            in = socket.getInputStream();
            out = socket.getOutputStream();
        } catch (java.net.UnknownHostException e) {
            throw new JRexecException(e.toString());
        } catch (java.io.IOException e) {
            throw new JRexecException(e.toString());
        }
    }

    public JRexec(String user, String host, String passwd, String command, boolean auxport, InputStream data) throws JRexecException {
        this(user, host, passwd, command, auxport);
        this.data = data;
    }

    public JRexec(String user, String host, String passwd, String command, boolean auxport, String data) throws JRexecException {
        this(user, host, passwd, command, true);
        this.data = new ByteArrayInputStream(data.getBytes());
    }

    public JRexec(String user, String host, String passwd, String command, InputStream data) throws JRexecException {
        this(user, host, passwd, command, true, data);
    }

    public JRexec(String user, String host, String passwd, String command) throws JRexecException {
        this(user, host, passwd, command, true);
    }

    public JRexec(String user, String host, String passwd, String command, String data) throws JRexecException {
        this(user, host, passwd, command, true, data);
    }

    public synchronized InputStream getResult() {
        if (thread != null || !devnull) return null;
        devnull = false;
        return in;
    }

    public synchronized void doit() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void doit_wait() throws JRexecException {
        if (thread != null) return; // ??

        try {
            write();

            byte[] b = new byte[1];
            in.read(b, 0, 1);

            byte[] buf = new byte[1024];

            if (b[0] == 0) { // success
                while (true) {
                    int len = in.read(buf, 0, buf.length);
                    if (len == -1) break;
                    // System.out.print(new String(buf, 0, len));
                }
            } else { // error message
                StringBuffer sb = null;
                while (true) {
                    int len = in.read(buf, 0, buf.length);
                    if (len == -1) break;
                    // System.out.print(new String(buf, 0, len));
                    if (sb == null) {
                        sb = new StringBuffer();
                    }
                    sb.append(new String(buf, 0, len));
                }
                close();
                if (sb != null) {
                    throw new JRexecException(sb.toString());
                }
            }
        } catch (IOException e) {
            close();
            // System.out.println("IO Error : " + e );
            throw new JRexecException(e.toString());
        }
    }

    public void run() {
        try {
            write();

            if (devnull) {
                byte[] buf = new byte[1024];
                byte[] b = new byte[1];
                in.read(b, 0, 1);

                if (b[0] == 0) { // success
                    while (true) {
                        int len = in.read(buf, 0, buf.length);
                        if (len == -1) break;

                        if (listeners != null) {
                            String s = new String(buf, 0, len);
                            synchronized (listeners) {
                                for (java.util.Enumeration e = listeners.elements(); e.hasMoreElements();) {
                                    ((JRexecListener)(e.nextElement())).output(s);
                                }
                            }
                        }

                        System.out.print(new String(buf, 0, len));
                    }
                } else { // error message
                    while (true) {
                        int len = in.read(buf, 0, buf.length);
                        if (len == -1) break;

                        if (listeners != null) {
                            String s = new String(buf, 0, len);
                            synchronized (listeners) {
                                for (java.util.Enumeration e = listeners.elements(); e.hasMoreElements();) {
                                    ((JRexecListener)(e.nextElement())).error(s);
                                }
                            }
                        }

                        System.out.print(new String(buf, 0, len));
                    }
                }
            }
        } catch (IOException e) {
            close();
            // System.out.println("IO Error : " + e );
        }
    }

    private void write() throws IOException {
        // port
        if (auxport) {
            ServerSocket foo = new ServerSocket(0);
            String snum = (new Integer(foo.getLocalPort())).toString();
            byte[] array = snum.getBytes();

            out.write(array, 0, array.length);
            out.write(0);
            out.flush();

            auxsocket = foo.accept();
            auxin = auxsocket.getInputStream();
            auxout = auxsocket.getOutputStream();

            try {
                foo.close();
            } catch (Exception ee) {
                System.out.println("ee: " + ee);
            }
        } else {
            out.write(0);
        }

        int chop = 16;
        if (user.length() < chop) chop = user.length();
        out.write(user.getBytes(), 0, chop);
        out.write(0);

        chop = 16;
        if (passwd.length() < chop) chop = passwd.length();
        out.write(passwd.getBytes(), 0, chop);
        out.write(0);

        chop = 4096;
        if (command.length() < chop) chop = command.length();
        out.write(command.getBytes(), 0, chop);
        out.write(0);

        if (data != null) {
            byte[] buf = new byte[1024];
            while (true) {
                int len = data.read(buf, 0, buf.length);
                if (len == -1) break;
                out.write(buf, 0, len);
            }
        }
    }

    private synchronized void stop() {
        if (thread != null) {
            thread = null;
        }
    }

    public void close() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        try {
            if (auxport) {
                auxin.close();
                auxout.close();
                auxsocket.close();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void sendSignal(int sig) throws JRexecException {
        try {
            auxout.write(sig);
            auxout.flush();
        } catch (Exception e) {
            throw new JRexecException(e.toString());
        }
    }

    public void sendSIGINT() throws JRexecException {
        sendSignal(2);
    }

    public void sendSIGQUIT() throws JRexecException {
        sendSignal(3);
    }

    public void sendSIGTERM() throws JRexecException {
        sendSignal(15);
    }

    public InputStream getAuxInputStream() {
        return auxin;
    }

    public OutputStream getAuxOutputStream() {
        return auxout;
    }

    public void addListener(JRexecListener foo) {
        if (listeners == null) listeners = new java.util.Vector();
        listeners.addElement(foo);
    }

    public void removeListener(JRexecListener foo) {
        if (listeners == null) return;
        listeners.removeElement(foo);
    }

    /*
     * public static void main(String arg[]){ try { JRexec jrexec=new
     * JRexec("yamanaka", "localhost", "????", "du /"); InputStream
     * in=jrexec.getResult(); jrexec.doit(); byte[] b=new byte[1]; in.read(b, 0,
     * 1); // 0 System.out.println("return: "+b[0]); byte[] buf=new byte[1024];
     * while(true){ int len=in.read(buf, 0, buf.length); if(len==-1) break;
     * System.out.print(new String(buf, 0, len)); } } catch(Exception e) {
     * System.out.println("IO Error : " + e ); } }
     */

    /*
     * public static void main(String arg[]){ try { JRexec jrexec=new
     * JRexec("yamanaka", "localhost", "????", "du /"); try{ jrexec.doit_wait();
     * } catch(JRexecException ee) { System.out.println(ee); } } catch(Exception
     * e) { System.out.println(e); } }
     */

    /*
     * public static void main(String arg[]){ try { JRexec jrexec=new
     * JRexec("yamanaka", "localhost", "????", "du /"); jrexec.addListener( new
     * JRexecListener(){ public void output(String
     * s){System.out.println("output: "+s);} public void error(String
     * s){System.out.println("error: "+s);} } ); jrexec.doit(); }
     * catch(Exception e) { System.out.println("IO Error : " + e ); } }
     */
}
