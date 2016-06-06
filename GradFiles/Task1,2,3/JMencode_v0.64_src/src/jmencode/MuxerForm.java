/*
 * Created on 13 November 2006, 00:51
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
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.text.*;
/**
 * This jpanel form allows entry of muxing parameters.
 * @author  Damien Farrell
 */

public class MuxerForm extends javax.swing.JPanel {
    private JFrame dummy;
    public String muxCommand1, muxCommand2, muxvideoinputname, muxed_videoinputname,
            muxaudioinputname, subsfilename, chapterfilename, muxedvideooutputname,
            extension = "." + Settings.container;
    public boolean auto = true;     //detemines of manual entry allowed or inputs taken from encode
    private static final String sp = " ";
    private ExecProgram executor;
    private SwingWorker worker;
    
    /** Creates new form MkvMuxerForm */
    public MuxerForm() {
        chapterfilename = "";
        muxedvideooutputname = muxvideoinputname;
        muxaudioinputname = "";
        subsfilename = "";
        dummy = new JFrame();
        initComponents();
        if (auto == true){
            automuxButton.setSelected(true); this.disableForm();
        }
        updateComponents();
        updateCommand();
    }
    
    public void update(){
        updateCommand();
        updateComponents();
    }
    
//mkvmerge v1.7.0 ('What Do You Take Me For') built on Apr 28 2006 17:19:57
//'dvdrip.264.mp4': Using the Quicktime/MP4 demultiplexer.
//'dvdrip.264.aac': Using the AAC demultiplexer.
//Warning: 'dvdrip.264.mp4' track 1: The AVC video track is missing the 'CTTS' atom for frame timecode offsets. However, AVC/h.264 allows frames to have more than the traditional one (for P frames) or two (for B frames) references to other frames. The timecodes for such frames will be out-of-order, and the 'CTTS' atom is needed for getting the timecodes right. As it is missing the timecodes for this track might be wrong. You should watch the resulting file and make sure that it looks like you expected it to.
//Track 1 of 'dvdrip.264.mp4': Extracted the aspect ratio information from the MPEG-4 layer 10 (AVC) video data and set the display dimensions to 720/310.
//'dvdrip.264.mp4' track 1: Using the MPEG-4 part 10 (AVC) video output module.
//'dvdrip.264.mp4' track 2: Using the AAC output module.
//Warning: AAC files may contain HE-AAC / AAC+ / SBR AAC audio. This can NOT be detected automatically. Therefore you have to specifiy '--aac-is-sbr 0' manually for this input file if the file actually contains SBR AAC. The file will be muxed in the WRONG way otherwise. Also read mkvmerge's documentation.
//'dvdrip.264.aac' track 0: Using the AAC output module.

        
    /** Updates muxing commmad based on input parameters. */
    private void updateCommand(){
        extension = "." + Settings.container;
        if (extension.matches(".mp4") || extension.matches(".mkv")){
            muxCommand1 = Settings.MP4Boxpath  + " -add" + sp + muxvideoinputname + sp
                    + (chapterfilename != "" ? " -chap" + sp + chapterfilename + sp : "")
                    + "-add" + sp +  "\"" + muxaudioinputname + "\"" + sp// + subsfilename
                    + "\"" + muxedvideooutputname + ".mp4\"";
            if (extension.matches(".mkv")){
                muxCommand2 = Settings.Mkvmergepath + " -o" + sp + muxedvideooutputname + ".mkv"
                        + (chapterfilename != "" ? " --chapters" + sp +  "\"" + chapterfilename + "\"" : "")
                        + sp +  "\""+ muxvideoinputname + ".mp4\"" + sp
                        + muxaudioinputname + sp + "\"" + subsfilename + "\"";
            }
        }  else{
            muxCommand1 = muxCommand2 = "";
            
        }
//        System.out.println(muxCommand); // for debugging
    }
    
    /** Updates mux command using inputs from main GUI */
    public void updateCommandfromSettings(){
        muxvideoinputname = Settings.fileoutputname;
        muxaudioinputname = Settings.audiofileoutput;
        muxedvideooutputname = muxvideoinputname;
        update();
    }
    
    /** Updates GUI widgets after new settings made */
    private void updateComponents(){
        muxvideoinputField.setText(muxvideoinputname);
        muxedinputField.setText(muxed_videoinputname);
        muxaudioinputField.setText(muxaudioinputname);
        muxedoutputField.setText(muxedvideooutputname);
        chapterinputField.setText(chapterfilename);
        subtitleinputField.setText(subsfilename);
    }
    
    public void enableMuxButtons(){
        containertypeComboBox.setEnabled(true);
        goMuxButton.setEnabled(true);
        cancelMuxButton.setEnabled(true);
        stopMuxButton.setEnabled(true);
        containertypeComboBox.setEnabled(true);
    }
    
    public void disableMuxButtons(){
        
        goMuxButton.setEnabled(false);
        cancelMuxButton.setEnabled(false);
        stopMuxButton.setEnabled(false);
        containertypeComboBox.setEnabled(false);
    }
    
    public void enableForm(){
        muxvideoinputField.setEnabled(true);
        muxedinputField.setEnabled(true);
        muxaudioinputField.setEnabled(true);
        getvidinputButton.setEnabled(true);
        audinputButton.setEnabled(true);
        enableMuxButtons();
    }
    
    public void disableForm(){
        muxvideoinputField.setEnabled(false);
        muxedinputField.setEnabled(false);
        muxaudioinputField.setEnabled(false);
        getvidinputButton.setEnabled(false);
        audinputButton.setEnabled(false);
        disableMuxButtons();
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel5 = new javax.swing.JPanel();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        muxvideoinputField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        muxedinputField = new javax.swing.JTextField();
        getvidinputButton = new javax.swing.JButton();
        getmuxedinputButton = new javax.swing.JButton();
        audinputButton = new javax.swing.JButton();
        muxaudioinputField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        audLanguageField = new javax.swing.JComboBox();
        audLanguageField = new JComboBox(Settings.languagelist);
        audtracknameField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        muxedoutputField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        subtitleinputField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        subLanguagefield = new javax.swing.JComboBox();
        subLanguagefield = new JComboBox(Settings.languagelist);
        chapterinputField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        chapterinputButton = new javax.swing.JButton();
        getsubtitlefileButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        goMuxButton = new javax.swing.JButton();
        stopMuxButton = new javax.swing.JButton();
        cancelMuxButton = new javax.swing.JButton();
        progresslabel = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        containertypeComboBox = new javax.swing.JComboBox();
        containertypeComboBox = new JComboBox(Settings.muxerlist);
        automuxButton = new javax.swing.JRadioButton();
        manualmuxButton = new javax.swing.JRadioButton();

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 100, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 100, Short.MAX_VALUE)
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Video & Audio"));
        jLabel1.setText("Video Input:");

        muxvideoinputField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                muxvideoinputFieldActionPerformed(evt);
            }
        });

        jLabel2.setText("Muxed Input:");

        muxedinputField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                muxedinputFieldActionPerformed(evt);
            }
        });

        getvidinputButton.setText("Video Input");
        getvidinputButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getvidinputButtonActionPerformed(evt);
            }
        });

        getmuxedinputButton.setText("Muxed Input");

        audinputButton.setText("Audio Input:");
        audinputButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                audinputButtonActionPerformed(evt);
            }
        });

        muxaudioinputField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                muxaudioinputFieldActionPerformed(evt);
            }
        });

        jLabel3.setText("Audio Input:");

        audLanguageField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                audLanguageFieldActionPerformed(evt);
            }
        });

        audtracknameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                audtracknameFieldActionPerformed(evt);
            }
        });

        jLabel4.setText("Language:");

        jLabel5.setText("Name:");

        muxedoutputField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                muxedoutputFieldActionPerformed(evt);
            }
        });

        jLabel8.setText("Muxed Output:");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(21, 21, 21)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel4)
                            .add(jLabel1)
                            .add(jLabel3)
                            .add(jLabel2))
                        .add(12, 12, 12))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jLabel8)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(audLanguageField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 124, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(13, 13, 13)
                        .add(jLabel5)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(audtracknameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, muxvideoinputField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, muxedinputField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, muxaudioinputField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                    .add(muxedoutputField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, getmuxedinputButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(getvidinputButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 33, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(audinputButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(getvidinputButton)
                    .add(muxvideoinputField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, getmuxedinputButton)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel2)
                            .add(muxedinputField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel3)
                            .add(audinputButton)
                            .add(muxaudioinputField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel4)
                            .add(jLabel5)
                            .add(audtracknameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(audLanguageField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel8)
                    .add(muxedoutputField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel1Layout.linkSize(new java.awt.Component[] {muxaudioinputField, muxedinputField, muxvideoinputField}, org.jdesktop.layout.GroupLayout.VERTICAL);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Subtitles & Chapters"));
        jLabel6.setText("Subtitle file:");

        subtitleinputField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subtitleinputFieldActionPerformed(evt);
            }
        });

        jLabel9.setText("Language:");

        subLanguagefield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subLanguagefieldActionPerformed(evt);
            }
        });

        chapterinputField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chapterinputFieldActionPerformed(evt);
            }
        });

        jLabel7.setText("Chapter file:");

        chapterinputButton.setText("Chapter File");
        chapterinputButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chapterinputButtonActionPerformed(evt);
            }
        });

        getsubtitlefileButton.setText("Subtitle file");
        getsubtitlefileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getsubtitlefileButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel6)
                            .add(jLabel9)
                            .add(jLabel7))
                        .add(20, 20, 20)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel3Layout.createSequentialGroup()
                                .add(subLanguagefield, 0, 129, Short.MAX_VALUE)
                                .add(181, 181, 181))
                            .add(jPanel3Layout.createSequentialGroup()
                                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(chapterinputField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
                                    .add(subtitleinputField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE))
                                .add(41, 41, 41)))
                        .add(0, 0, 0))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(getsubtitlefileButton, 0, 0, Short.MAX_VALUE)
                        .add(chapterinputButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel6)
                            .add(subtitleinputField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel9)
                            .add(subLanguagefield, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 16, Short.MAX_VALUE)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel7)
                            .add(chapterinputField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(getsubtitlefileButton)
                        .add(34, 34, 34)
                        .add(chapterinputButton)))
                .addContainerGap())
        );

        goMuxButton.setText("Mux");
        goMuxButton.setEnabled(false);
        goMuxButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goMuxButtonActionPerformed(evt);
            }
        });

        stopMuxButton.setText("Stop");
        stopMuxButton.setEnabled(false);
        stopMuxButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopMuxButtonActionPerformed(evt);
            }
        });

        cancelMuxButton.setText("Clear");
        cancelMuxButton.setEnabled(false);
        cancelMuxButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelMuxButtonActionPerformed(evt);
            }
        });

        progresslabel.setFont(new java.awt.Font("Tahoma", 1, 12));
        progresslabel.setForeground(new java.awt.Color(153, 51, 0));
        progresslabel.setText("Check data");

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(goMuxButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE)
                    .add(stopMuxButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE)
                    .add(cancelMuxButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE)
                    .add(progresslabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(goMuxButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(stopMuxButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cancelMuxButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(progresslabel)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jLabel11.setText("Container:");

        containertypeComboBox.setEnabled(false);
        containertypeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                containertypeComboBoxActionPerformed(evt);
            }
        });

        buttonGroup1.add(automuxButton);
        automuxButton.setText("Automatic");
        automuxButton.setToolTipText("Gets input data from encode setup");
        automuxButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        automuxButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        automuxButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                automuxButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(manualmuxButton);
        manualmuxButton.setSelected(true);
        manualmuxButton.setText("Manual");
        manualmuxButton.setToolTipText("Allow manual entry of input files. Muxing will not be done automatically after encoding");
        manualmuxButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        manualmuxButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        manualmuxButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manualmuxButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(automuxButton)
                    .add(manualmuxButton)
                    .add(layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel11)
                            .add(containertypeComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 66, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .add(12, 12, 12)
                        .add(jLabel11)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(containertypeComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(automuxButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(manualmuxButton)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
    private void automuxButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_automuxButtonActionPerformed
        auto = true;
        disableForm();
    }//GEN-LAST:event_automuxButtonActionPerformed
    
    private void manualmuxButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manualmuxButtonActionPerformed
        auto = false;
        enableForm();
    }//GEN-LAST:event_manualmuxButtonActionPerformed
    
    private void getsubtitlefileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getsubtitlefileButtonActionPerformed
        FileDialog fd = new FileDialog(dummy);
        fd.setVisible(true);
        if (fd.getFile() == null) return;
        subsfilename = fd.getDirectory() + fd.getFile();
        updateCommand();
        updateComponents();
    }//GEN-LAST:event_getsubtitlefileButtonActionPerformed
    
    
    public void doMuxing(){
        worker = new SwingWorker() {
            public Object construct() {
                try{
                    progresslabel.setText("muxing...");
                    executor = new ExecProgram();
                    System.out.println(muxCommand1);
                    executor.ExecuteM(muxCommand1);
                    
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                return  progresslabel;
            }
            public void finished() {
                if (executor.getexitValue() == 0){
                    progresslabel.setText("Finished");
                } else{
                    progresslabel.setText("Stopped");
                }
            }
        }; worker.start();
        
    }
    
    private void goMuxButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goMuxButtonActionPerformed
        doMuxing();
        
    }//GEN-LAST:event_goMuxButtonActionPerformed
    
    private void stopMuxButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopMuxButtonActionPerformed
        executor.Stop();
    }//GEN-LAST:event_stopMuxButtonActionPerformed
    
    private void containertypeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_containertypeComboBoxActionPerformed
        Settings.container = (String)containertypeComboBox.getSelectedItem();
        updateCommand();
        updateComponents();
    }//GEN-LAST:event_containertypeComboBoxActionPerformed
    
    private void audtracknameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_audtracknameFieldActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_audtracknameFieldActionPerformed
    
    private void chapterinputButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chapterinputButtonActionPerformed
        FileDialog fd = new FileDialog(dummy);
        fd.setVisible(true);
        if (fd.getFile() == null) return;
        chapterfilename = fd.getDirectory() + fd.getFile();
        updateCommand();
        updateComponents();
    }//GEN-LAST:event_chapterinputButtonActionPerformed
    
    private void chapterinputFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chapterinputFieldActionPerformed
        
        chapterfilename = chapterinputField.getText();
        updateCommand();
        updateComponents();
    }//GEN-LAST:event_chapterinputFieldActionPerformed
    
    private void subLanguagefieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subLanguagefieldActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_subLanguagefieldActionPerformed
    
    private void subtitleinputFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subtitleinputFieldActionPerformed
        
        subsfilename = subtitleinputField.getText();
        updateCommand();
        updateComponents();
    }//GEN-LAST:event_subtitleinputFieldActionPerformed
    
    private void getvidinputButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getvidinputButtonActionPerformed
        FileDialog fd = new FileDialog(dummy);
        fd.setVisible(true);
        if (fd.getFile() == null) return;
        muxvideoinputname = fd.getDirectory() + fd.getFile();
        updateCommand();
        updateComponents();
    }//GEN-LAST:event_getvidinputButtonActionPerformed
    
    private void audinputButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_audinputButtonActionPerformed
        FileDialog fd = new FileDialog(dummy);
        fd.setVisible(true);
        if (fd.getFile() == null) return;
        muxaudioinputname = fd.getDirectory() + fd.getFile();
        updateCommand();
        updateComponents();
    }//GEN-LAST:event_audinputButtonActionPerformed
    
    private void muxvideoinputFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_muxvideoinputFieldActionPerformed
        muxvideoinputname = muxvideoinputField.getText();
        updateCommand();
        updateComponents();
    }//GEN-LAST:event_muxvideoinputFieldActionPerformed
    
    private void muxedinputFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_muxedinputFieldActionPerformed
        muxed_videoinputname = muxedinputField.getText();
        updateCommand();
        updateComponents();
    }//GEN-LAST:event_muxedinputFieldActionPerformed
    
    private void muxaudioinputFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_muxaudioinputFieldActionPerformed
        muxaudioinputname =  muxaudioinputField.getText();
        updateCommand();
        updateComponents();
    }//GEN-LAST:event_muxaudioinputFieldActionPerformed
    
    private void audLanguageFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_audLanguageFieldActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_audLanguageFieldActionPerformed
    
    private void muxedoutputFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_muxedoutputFieldActionPerformed
        muxedvideooutputname = muxedoutputField.getText();
        updateCommand();
        updateComponents();
    }//GEN-LAST:event_muxedoutputFieldActionPerformed
    
    private void cancelMuxButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelMuxButtonActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_cancelMuxButtonActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //new MuxerForm().setVisible(true);
                MuxerForm muxpanel = new MuxerForm();
                JFrame frame = new JFrame();
                muxpanel.enableMuxButtons();
                frame.add(muxpanel);
                frame.pack();
                frame.setVisible(true);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox audLanguageField;
    private javax.swing.JButton audinputButton;
    private javax.swing.JTextField audtracknameField;
    private javax.swing.JRadioButton automuxButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton cancelMuxButton;
    private javax.swing.JButton chapterinputButton;
    private javax.swing.JTextField chapterinputField;
    private javax.swing.JComboBox containertypeComboBox;
    private javax.swing.JButton getmuxedinputButton;
    private javax.swing.JButton getsubtitlefileButton;
    private javax.swing.JButton getvidinputButton;
    private javax.swing.JButton goMuxButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JRadioButton manualmuxButton;
    private javax.swing.JTextField muxaudioinputField;
    private javax.swing.JTextField muxedinputField;
    private javax.swing.JTextField muxedoutputField;
    private javax.swing.JTextField muxvideoinputField;
    private javax.swing.JLabel progresslabel;
    private javax.swing.JButton stopMuxButton;
    private javax.swing.JComboBox subLanguagefield;
    private javax.swing.JTextField subtitleinputField;
    // End of variables declaration//GEN-END:variables
    
}
