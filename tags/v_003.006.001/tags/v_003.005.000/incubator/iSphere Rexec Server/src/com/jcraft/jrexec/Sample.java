package com.jcraft.jrexec;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.io.InputStream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

class Sample implements ActionListener {
    JRexec jrexec = null;

    JTextField name = new JTextField(8);
    JTextField host = new JTextField(20);
    JPasswordField passwd = new JPasswordField(8);
    JTextField command = new JTextField(20);
    JButton rexec = new JButton("REXEC");
    JButton kill = new JButton("SigKILL");

    Sample() {
        JFrame jframe = new JFrame();
        Container cpane = jframe.getContentPane();
        cpane.setLayout(new GridLayout(0, 1));

        JPanel jpanel = null;

        jpanel = new JPanel();
        jpanel.setBorder(BorderFactory.createTitledBorder("Name"));
        jpanel.setLayout(new BorderLayout());
        name.setText("RADDATZ");
        name.setMinimumSize(new Dimension(50, 25));
        name.setEditable(true);
        jpanel.add(name, "Center");
        cpane.add(jpanel);

        jpanel = new JPanel();
        jpanel.setBorder(BorderFactory.createTitledBorder("Host"));
        jpanel.setLayout(new BorderLayout());
        host.setText("localhost");
        host.setText("DEV530.RZKH.DE");
        host.setText("ghentw.gfd.de");
        host.setMinimumSize(new Dimension(50, 25));
        host.setEditable(true);
        jpanel.add(host, "Center");
        cpane.add(jpanel);

        jpanel = new JPanel();
        jpanel.setBorder(BorderFactory.createTitledBorder("Password"));
        jpanel.setLayout(new BorderLayout());
        passwd.setText("myS3cr3t");
        passwd.setText("JUST4YOU");
        passwd.setMinimumSize(new Dimension(50, 25));
        passwd.setEditable(true);
        jpanel.add(passwd, "Center");
        cpane.add(jpanel);

        jpanel = new JPanel();
        jpanel.setBorder(BorderFactory.createTitledBorder("Command"));
        jpanel.setLayout(new BorderLayout());
        command.setText("cmd /C dir"); // cmdx /C start foobaa.txt
        command.setText("DSPMSG MSGQ(RADDATZ) OUTPUT(*PRINT)");
        command.setMinimumSize(new Dimension(50, 25));
        command.setEditable(true);
        jpanel.add(command, "Center");
        command.addActionListener(this);
        cpane.add(jpanel);

        jpanel = new JPanel();
        jpanel.add(rexec, "West");
        rexec.addActionListener(this);
        jpanel.add(kill, "East");
        kill.addActionListener(this);
        cpane.add(jpanel);

        jframe.pack();
        jframe.setVisible(true);
    }

    public static void main(String[] arg) {
        Sample Sample = new Sample();
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (e.getSource() == kill) {
            if (jrexec == null) return;
            try {
                jrexec.sendSignal(9);
                jrexec.close();
            } catch (Exception ee) {
                // System.out.println("ee: "+ee);
                jrexec = null;
            }
        }
        if (e.getSource() != rexec && e.getSource() != command) {
            return;
        }
        if (name.getText().length() == 0 || host.getText().length() == 0 || passwd.getText().length() == 0 || command.getText().length() == 0
            || jrexec != null) {
            return;
        }
        try {
            jrexec = new JRexec(name.getText(), host.getText(), passwd.getText(), command.getText(), true);
            Spawn spawn = new Spawn(this);
            spawn.start();
        } catch (Exception ee) {
            // System.out.println("ee: "+ee);
            jrexec = null;
        }
    }

    class Spawn extends Thread {
        Sample sample = null;

        Spawn(Sample sample) {
            this.sample = sample;
        }

        public void run() {
            if (sample.jrexec == null) return;
            try {
                InputStream in = jrexec.getResult();
                jrexec.doit();

                System.out.println("Input stream: " + in);

                byte[] b = new byte[1];
                int count = in.read(b, 0, b.length); // success or fail ??
                System.out.println("Gelesen: " + count);

                byte[] buf = new byte[1024];
                while (true) {
                    int len = in.read(buf, 0, buf.length);
                    if (len == -1) break;
                    System.out.print(new String(buf, 0, len));
                }

                InputStream error = jrexec.getAuxInputStream();
                if (error != null) {
                    while (true) {
                        int len = error.read(buf, 0, buf.length);
                        if (len == -1) break;
                        System.err.print(new String(buf, 0, len));
                    }
                    sample.jrexec = null;
                }
            } catch (Exception e) {
                System.err.println("spawn: " + e);
                sample.jrexec = null;
            }
        }
    }
}
