/*
 * Created on 11 November 2006, 12:55
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
 
    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package jmencode;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.FlowLayout;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;


public class ExecProgram extends WindowAdapter{
    Process proc, proc2;
    int exitVal;
    PrintWriter writer;
    Thread mplayerThread;
    static BufferedReader stdout, stderr, stdout2, stderr2;
    static Vector<String> outputLines;
    private boolean paused = false;
    private boolean outputAllowed = false;
    private static final String mplayerPath = "C:\\Program Files\\mplayer\\mplayer.exe";
    JFrame frame;
    private static final String[] flags = {"-slave", "-playlist", ""};
    private static final String[] mediaPath = {
        ""
    };
    private static final String space = " ";
    
    //works for lame and other commands
    public void Execute(String command){
        try{
            writer = new PrintWriter(new FileWriter("log.txt"));
            Runtime rt = Runtime.getRuntime();
            proc = rt.exec(command);
            InputStream stderr = proc.getErrorStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            System.out.println("<OUTPUT>");
            while ( (line = br.readLine()) != null){
                System.out.println(line); writer.println(line);
            }
            System.out.println("</OUTPUT>");
            exitVal = proc.waitFor();
            Stop();
            System.out.println("Process exitValue: " + exitVal);
            writer.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    public void ExecuteM(String command) {
        ExecuteM(command, null);
    }
    
   //use for executing multiple commands consecutively, passes commands in an array
    public void ExecuteM(String[] commands) {
        for (int i=0; i<commands.length; i++){
            //System.out.println("DOING PART "+ i);
            ExecuteM(commands[i]);
        }
    }
    
    //use for executing multiple commands consecutively, passes commands in an array
    public void ExecuteM(String[] commands, String[] info) {
        for (int i=0; i<commands.length; i++){
            //System.out.println("DOING PART "+ i);
            ExecuteM(commands[i], info[i]);
        }
    }
    
    /**use for executing mplayer and mencoder and maybe others.. */
    public void ExecuteM(String command, String info) {
        //showGUI();
        MainGUI.procProgressBar.setValue(0);
        try {
            if (info != null){
                MainGUI.progresslabel.setText(info);
            }
            
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
//                        writer.append(outputAllowed ? line : ".");
                        writer.print(line.startsWith("Pos:") ? "": line +"\n");
                        //deals with mencoder or mplayer output to update progress bar
                        if (line.startsWith("Pos:")){
                            int p = MplayerParser.parseEncoderProgress(line);
                            MainGUI.procProgressBar.setValue(p);
                            MainGUI.procProgressBar.repaint();
                        } else if (line.startsWith("[CROP]")){  //only does this if cropdetect is used
                            MplayerParser.parseCropOutput(line);
                        } else if (line.startsWith("Movie-Aspect")){
                            VideoTitle.inputDAR = MplayerParser.parseDARInfo(line);
                            //for info from -identify switch
                        } else if ((line.startsWith("ID_") || line.startsWith("audio stream"))){
                            MplayerParser.parseVideoInfo(line);
                        }
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
    
//      //use for executing multiple commands consecutively, passes commands in an array
//    public void ExecuteM(String command, Process p) {
//            p = this.proc;
//            ExecuteM(command);
//
//    }
    
    public void Stop(){
        proc.destroy();
    }
    
    public int getexitValue(){
        return exitVal;
    }
    
//    public class Reminder {
//        Timer timer;
//
//        public Reminder(int seconds) {
//            timer = new Timer();
//            timer.schedule(new RemindTask(), seconds * 1000);
//        }
//
//        class RemindTask extends TimerTask {
//            public void run() {
//                // System.out.println("Time's up!");
//                timer.cancel(); // Terminate the timer thread
//            }
//        }
//    }
    
    
    // Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createIcon(String imageName) {
        String imgLocation = "images/"
                + imageName
                + ".png";
        java.net.URL imageURL = MainGUI.class.getResource(imgLocation);
        
        if (imageURL == null) {
            // System.err.println("Resource not found: "
            //                  + imgLocation);
            return null;
        } else {
            return new ImageIcon(imageURL);
        }
    }
    
    public static void main(String args[]) {
//        try {
//            String procname = "c:\\program files\\megui\\tools\\lame\\lame.exe";
//            String arguments = "--alt-preset standard";
//            String input = "";
//
//            Runtime rt = Runtime.getRuntime();
//            Process proc = rt.exec(procname + " "+ arguments + " " + input);
//            InputStream stderr = proc.getErrorStream();
//            InputStreamReader isr = new InputStreamReader(stderr);
//            BufferedReader br = new BufferedReader(isr);
//            String line = null;
//            System.out.println("<ERROR>");
//            while ( (line = br.readLine()) != null)
//                System.out.println(line);
//            System.out.println("</ERROR>");
//            int exitVal = proc.waitFor();
//            proc.destroy();
//            System.out.println("Process exitValue: " + exitVal);
//        } catch (Throwable t) {
//            t.printStackTrace();
//        }
    }
}
