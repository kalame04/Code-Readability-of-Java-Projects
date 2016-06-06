/*
 * MplayerController.java
 * Created on 27 November 2006, 20:30
 * GUI for audio and video encoding using mencoder and mplayer
 * Copyright (C) Damien Farrell
 
    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.
 
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 
 */

package jmencode;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


public class MplayerController extends javax.swing.JFrame {
    
    Process proc;
    SwingWorker worker;
    private ExecProgram executor;
    private static final String sp = " ";
    int exitVal;
    PrintWriter writer;
    Thread mplayerThread;
    static BufferedReader stdout, stderr, stdout2, stderr2;
    static Vector<String> outputLines;
    private boolean paused = false;
    private boolean outputAllowed = false;
    
    /** Creates new form MplayerController */
    public MplayerController() {
        initComponents();
    }
    
    /** Creates new form MplayerController */
    public MplayerController(Process p) {
        p = proc;
        initComponents();
    }
    
    public void playMedia(final String media, final String options){
        this.setVisible(true);
        worker = new SwingWorker() {
            public Object construct() {
                String comm = Settings.Mplayerpath + sp +
                        "-slave" + options + sp + media;
                try{
                    executor = new ExecProgram();
                    System.out.println(comm);
                    ExecuteM(comm);
                    
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                return true;
            }
            public void finished() {
//                if (executor.getexitValue() == 0){
//                    progresslabel.setText("Finished");
//                } else{
//                    progresslabel.setText("Stopped");
//                }
            }
        }; worker.start();
        
    }
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jToolBar1 = new javax.swing.JToolBar();
        pauseButton = new javax.swing.JButton();
        slowButton = new javax.swing.JButton();
        fastButton = new javax.swing.JButton();
        muteButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        movieSlider = new javax.swing.JSlider();

        setResizable(false);
        pauseButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jmencode/images/pause.png")));
        pauseButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        pauseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseButtonActionPerformed(evt);
            }
        });

        jToolBar1.add(pauseButton);

        slowButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jmencode/images/slower.png")));
        slowButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        slowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                slowButtonActionPerformed(evt);
            }
        });

        jToolBar1.add(slowButton);

        fastButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jmencode/images/faster.png")));
        fastButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        fastButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fastButtonActionPerformed(evt);
            }
        });

        jToolBar1.add(fastButton);

        muteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jmencode/images/mute.png")));
        muteButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        muteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                muteButtonActionPerformed(evt);
            }
        });

        jToolBar1.add(muteButton);

        stopButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jmencode/images/stop.png")));
        stopButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });

        jToolBar1.add(stopButton);

        movieSlider.setValue(0);
        movieSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                movieSliderMouseReleased(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(movieSlider, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 42, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(movieSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void movieSliderMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_movieSliderMouseReleased
        
        int value = movieSlider.getValue(); 
        seekto(value);
    }//GEN-LAST:event_movieSliderMouseReleased
    
    public void seekto(int value){
        PrintStream s = new PrintStream(proc.getOutputStream());
        int randomPercent =
                Float.valueOf(""+Math.floor(Math.random()*100)).intValue();
        String command = "seek "+value+" 1\n";
        System.out.print(command);
        s.print(command);
        s.flush();
    }
    
    /**use for executing mplayer and mencoder and maybe others.. */
    public void ExecuteM(String command) {
        //showGUI();
        MainGUI.procProgressBar.setValue(0);
        try {
            proc = Runtime.getRuntime().exec(command);
            
            InputStream is = proc.getInputStream();
            stdout = new BufferedReader(new InputStreamReader(is));
            is = proc.getErrorStream();
            stderr = new BufferedReader(new InputStreamReader(is));
            outputLines = new Vector<String>();
            //this deals with the output from the process in it's own thread
            Thread stdoutThread = new Thread() {
                public void run() {
                    try { writer = new PrintWriter(new FileWriter("log.txt"));
                    int l;
                    String line;
                    for (l = 0; (line = stdout.readLine()) != null;) {
                        if (line.length() > 0) {
                            l++;
                            outputLines.addElement(line);
                            // if (line.matches("Starting playback..."))
                            outputAllowed = true;
                        }
                        System.out.print(outputAllowed ? line +"\n": ".");
                        writer.print(outputAllowed ? line +"\n": ".");
                        
                    }
                    System.out.println("\nRead " + l
                            + " lines from stdout.");
                    stdout.close();
                    writer.close();
                    } catch (IOException ie) {
                        System.out.println("IO exception on stdout: " + ie);
                    }
                }
            };
            stdoutThread.start();
            
            Thread stderrThread = new Thread() {
                public void run() {
                    try {
                        int l;
                        String line;
                        for (l = 0; (line = stderr.readLine()) != null;) {
                            if (line.length() > 0)
                                l++;
                            System.out.print(",");
                        }
                        System.out.println("\nRead " + l
                                + " lines from stderr.");
                        stderr.close();
                    } catch (IOException ie) {
                        System.out.println("IO exception on stderr: " + ie);
                    }
                }
            };
            stderrThread.start();
            
            System.out.println("About to waitfor");
            try {
                exitVal = proc.waitFor();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("Done.");
            
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Exec failed. Exiting");
            System.exit(0);
        }
    }
    
    private void muteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_muteButtonActionPerformed
        PrintStream s = new PrintStream(proc.getOutputStream());
        if (paused) {
            s.print("pausing mute\n");
        } else {
            s.print("mute\n");
        }
        s.flush();
    }//GEN-LAST:event_muteButtonActionPerformed
    
    private void slowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_slowButtonActionPerformed
        PrintStream s = new PrintStream(proc.getOutputStream());
        String command = "speed_incr -1\n";
        s.print(command);
        s.flush();
    }//GEN-LAST:event_slowButtonActionPerformed
    
    private void fastButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fastButtonActionPerformed
        PrintStream s = new PrintStream(proc.getOutputStream());
        String command = "speed_incr 1\n";
        s.print(command);
        s.flush();
    }//GEN-LAST:event_fastButtonActionPerformed
    
    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
        proc.destroy();
    }//GEN-LAST:event_stopButtonActionPerformed
    
    private void pauseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseButtonActionPerformed
        
        PrintStream s = new PrintStream(proc.getOutputStream());
        s.print("pause\n");
        paused = !paused;
        s.flush();
        
    }//GEN-LAST:event_pauseButtonActionPerformed
    
    
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MplayerController().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton fastButton;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JSlider movieSlider;
    private javax.swing.JButton muteButton;
    private javax.swing.JButton pauseButton;
    private javax.swing.JButton slowButton;
    private javax.swing.JButton stopButton;
    // End of variables declaration//GEN-END:variables
    
}
