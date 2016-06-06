/*
 * Created on 13 November 2006, 18:54
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
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
/**
 * Panel to allow setting of the look and feel.
 * @author  Damien Farrell
 */

class LNFSetter implements ActionListener {
    final static String PREFERREDLOOKANDFEELNAME = "javax.swing.plaf.metal.MetalLookAndFeel";
    protected static String curLF = PREFERREDLOOKANDFEELNAME;
    protected static JRadioButton previousButton;
    public static JFrame theFrame;
    public static MainGUI parent;
    
    /* Class to set the Look and Feel on a frame */
    
    String theLNFName;
    
    JRadioButton thisButton;
    
    public LNFSetter (){
        theFrame.setResizable(false);
    }
    
    /** Called to setup for button handling */
    LNFSetter(String lnfName, JRadioButton me) {
        theLNFName = lnfName;
        thisButton = me;
    }
    
    /** Called when the button actually gets pressed. */
    public void actionPerformed(ActionEvent e) {
        try {
            UIManager.setLookAndFeel(theLNFName);
            SwingUtilities.updateComponentTreeUI(theFrame);
            SwingUtilities.updateComponentTreeUI(parent);
            SwingUtilities.updateComponentTreeUI(parent.log);
            SwingUtilities.updateComponentTreeUI(parent.setupform);
            SwingUtilities.updateComponentTreeUI(parent.prevsetupform);
            SwingUtilities.updateComponentTreeUI(parent.mplayercontrol);
            SwingUtilities.updateComponentTreeUI(parent.bcalcform);
            SwingUtilities.updateComponentTreeUI(parent.muxpanel);
            theFrame.pack();
            
        } catch (Exception evt) {
            JOptionPane.showMessageDialog(null,
                    "setLookAndFeel didn't work: " + evt, "UI Failure",
                    JOptionPane.INFORMATION_MESSAGE);
            previousButton.setSelected(true); // reset the GUI to agree
        }
        previousButton = thisButton;
    }
    
    public static JFrame createLNFSwitcher(MainGUI p) {
        parent = p;
        Container cp;
        /** Start with the Java look-and-feel, if possible */
        
        theFrame = new JFrame("Look & Feel Switcher");
        theFrame.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        
        cp = theFrame.getContentPane();
        cp.setLayout(new FlowLayout());
        
        ButtonGroup bg = new ButtonGroup();
        JRadioButton bJava = new JRadioButton("Java");
        bJava.addActionListener(new LNFSetter(
                "javax.swing.plaf.metal.MetalLookAndFeel", bJava));
        bg.add(bJava);
        cp.add(bJava);
        
        JRadioButton bMSW = new JRadioButton("MS-Windows");
        bMSW.addActionListener(new LNFSetter(
                "com.sun.java.swing.plaf.windows.WindowsLookAndFeel", bMSW));
        bg.add(bMSW);
        cp.add(bMSW);
        
//        JRadioButton bMotif = new JRadioButton("Motif");
//        bMotif.addActionListener(new LNFSetter(
//                "com.sun.java.swing.plaf.motif.MotifLookAndFeel", bMotif));
//        bg.add(bMotif);
//        cp.add(bMotif);
        
        String defaultLookAndFeel = UIManager.getSystemLookAndFeelClassName();
        // System.out.println(defaultLookAndFeel);
        JRadioButton bDefault = new JRadioButton("Default");
        bDefault.addActionListener(new LNFSetter(defaultLookAndFeel, bDefault));
        bg.add(bDefault);
        cp.add(bDefault);
        
        (previousButton = bDefault).setSelected(true);
        return(theFrame);
    }
    
}
