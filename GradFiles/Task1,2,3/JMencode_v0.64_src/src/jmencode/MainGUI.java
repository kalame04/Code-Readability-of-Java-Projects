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

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.text.*;
import java.io.File;

/**
 * This is a GUI for setting Mencoder parameters in order to
 * encode and mux a video input.
 * The main controls are displayed in a tabbed panel, with a
 * preview of the commands to be run in another jpanel below this.
 * @author  Damien Farrell
 */

public class MainGUI extends JFrame {
    private ExecProgram executor;
    public static  MuxerForm muxpanel;
    public String path, args, audioargs, videoargs, input, output, //audoutput,
            extractcomm, command1, command2, extension, FOURCC;
    private static final String sp = " ";
    private SwingWorker worker;
    Font theMainFont, smallFont;
    static JFrame lnfframe;
    static LogPanel log;
    static SettingsForm setupform;
    static BitrateCalcForm bcalcform;
    static RescaleOptionsForm prevsetupform;
    static MplayerController mplayercontrol;
    
    
    /** Creates new form */
    public MainGUI() {
        Settings.loadSettingsfromFile(null);
        System.out.println(Settings.osname);
        Settings.resetPaths();
        path = Settings.Mencoderpath;
        output = "";
        input = "";
        System.out.println("Settings.fileinputname="+Settings.fileinputname);
        System.out.println("Settings.fileoutputname="+Settings.fileoutputname);
        if (Settings.codec == "x264"){
            extension = ".264";
        } else{
            extension = ".m4v";
        }
        log = new LogPanel();
        muxpanel = new MuxerForm();
        //create the switcher here, it is static so only one instance allowed
        lnfframe = LNFSetter.createLNFSwitcher(this);
        //same for settings form
        setupform = new SettingsForm();
        bcalcform = new BitrateCalcForm();
        prevsetupform = new RescaleOptionsForm(this);
        mplayercontrol = new MplayerController();
        initComponents();
        updateCommand();
        updateComponents();
        
    }
    
    /**Updates command line and GUI components when alterations made */
    public void update(){
        updateCommand();
        updateComponents();
    }
    
    /**Build encode command for video, based on pass and whether it is for avi
     * or mp4/mkv muxing */
    private void dovideoArgs(int pass){
        if (Settings.domuxing == false){
            videoargs = "-sws" + sp + Settings.sws + sp + "-ovc" + sp
                    + (Settings.codec == "lavc" ? "lavc -lavcopts vcodec=" + Settings.lavccodec + ":vbitrate="
                    + Settings.bitrate + ":vhq:vpass=" + pass +     //-of lavf               
                     (Settings.fourcc == "FLV1" ? " -lavfopts i_certify_that_my_video_stream_does_not_use_b_frames " : "") +
                        (Settings.fourcc != "auto" ? " -ffourcc " + Settings.fourcc : "") :
                      (Settings.codec == "x264" ? "x264 -x264encopts bitrate="
                    + Settings.bitrate + ":subq=4:i4x4" + ":pass=" + pass +
                     (Settings.fourcc != "auto" ? " -ffourcc " + Settings.fourcc : "") :
                            "copy")) + sp
                    + (Settings.crop | Settings.resize | Settings.unsharp ? "-vf " : "")
                    + (Settings.crop ? "crop="
                    + VideoTitle.cropw+":"+VideoTitle.croph+":"+VideoTitle.cropx+":"+VideoTitle.cropy+"," : "")
                    + (Settings.resize ? "scale="+Settings.scalew+":"+Settings.scaleh : "")
                    + (Settings.unsharp ? ",unsharp=l3x3:1:c3x3:1": "") + sp
                    
                    + (Settings.selecttime ? "-ss " + Settings.starttime + sp + "-endpos " + Settings.length : "")
                    + sp;
        } else if (Settings.domuxing == true){
            if (Settings.passes == 1){
                videoargs = "-ovc" + sp + (pass==1 ? "frameno" : (
                        (Settings.codec == "lavc" ? "lavc -lavcopts vcodec=" + Settings.lavccodec + ":vbitrate="
                        + Settings.bitrate + ":vhq:vpass=1" +
                            (Settings.fourcc != "auto" ? " -ffourcc " + Settings.fourcc : "") :
                        (Settings.codec == "x264" ? "x264 -x264encopts bitrate="
                        + Settings.bitrate + ":subq=4:i4x4" + ":pass=1" +
                        (Settings.fourcc != "auto" ? " -ffourcc " + Settings.fourcc : "") :
                                "copy")) + sp
                        + (Settings.crop | Settings.resize | Settings.unsharp ? "-vf " : "")
                        + (Settings.crop ? "crop="
                        + VideoTitle.cropw+":"+VideoTitle.croph+":"+VideoTitle.cropx+":"+VideoTitle.cropy+"," : "")
                        + (Settings.resize ? "scale="+Settings.scalew+":"+Settings.scaleh+"," : "") + sp )
                        + (Settings.unsharp ? "unsharp=l3x3:1:c3x3:1": "") + sp )                        
                        
                        + sp + (Settings.selecttime ? "-ss " + Settings.starttime + sp + "-endpos " + Settings.length : "")
                        + sp + (pass==2 ? "-of rawvideo"  : "-of rawaudio" )
                        + sp + "-sws" + sp + Settings.sws + sp;
            }
            if (Settings.passes == 2){
                videoargs = "-ovc" + sp
                        + (Settings.codec == "lavc" ? "lavc -lavcopts vcodec=" + Settings.lavccodec + ":vbitrate="
                        + Settings.bitrate + ":vhq:vpass=" + pass +
                            (Settings.fourcc != "auto" ? " -ffourcc " + Settings.fourcc : "") :
                          (Settings.codec == "x264" ? "x264 -x264encopts bitrate="
                        + Settings.bitrate + ":subq=4:i4x4" + ":pass=" + pass +
                        (Settings.fourcc != "auto" ? " -ffourcc " + Settings.fourcc : "") :
                                "copy")) + sp
                        + (Settings.crop | Settings.resize | Settings.unsharp ? "-vf " : "")
                        + (Settings.crop ? "crop="
                        + VideoTitle.cropw+":"+VideoTitle.croph+":"+VideoTitle.cropx+":"+VideoTitle.cropy+"," : "")
                        + (Settings.resize ? "scale="+Settings.scalew+":"+Settings.scaleh : "")
                        + (Settings.unsharp ? ",unsharp=l3x3:1:c3x3:1": "") + sp
                        
                        + (Settings.selecttime ? "-ss " + Settings.starttime + sp + "-endpos " + Settings.length : "") + sp
                        + (pass==2 ? "-of rawvideo"  : "-of rawaudio" )
                        + sp + "-sws" + sp + Settings.sws + sp;
            }
        }
    }
    
    /**Generates arguments for mencoder audio extraction */
    private void doaudioArgs(int pass){
        if (Settings.audcodec == "copy"){
            audioargs = "-oac" + sp + "copy" + sp;
        } else if (Settings.audcodec == "aac"){
            audioargs = "-oac" + sp + "faac -faacopts object=0:tns:quality=" + Settings.audquality + sp;
        } else if(Settings.audcodec == "mp3"){
            audioargs = "-oac" + sp + "mp3lame -lameopts abr:br=" + Settings.audbitrate + sp;
        } else if (Settings.audcodec == "wav") {
            audioargs = "-oac" + sp + "pcm" + sp;
        }
        if (Settings.audtrack != 0 ) audioargs += "-aid"+ sp + Settings.audtrack + sp;
        if (Settings.dovolumegain == true) audioargs += "-af volume=" + Settings.volgain + sp;
    }
    
    //deals with changes to command and parameters chosen by user and creates the new command(s)
    //code is rather convoluted and needs to be simplified..
    private void updateCommand(){
        //if no muxing extension is avi container
        if (Settings.usediscinput == true){
            input = "dvd://" + Settings.dvdtitle + " -dvd-device " + Settings.dvddev  + sp ;
        }
        else {
            input = Settings.fileinputname;
            if ((Settings.fileoutputname == "output") && (input != null)){
                Settings.fileoutputname = input; 
            }
            System.out.println("Settings.fileinputname="+Settings.fileinputname);
            System.out.println("Settings.fileoutputname="+Settings.fileoutputname);
        }
 
        output = Settings.fileoutputname + (Settings.domuxing ? extension : "." + Settings.container);     
        //System.out.println(Settings.fileinputname);
        
        Settings.audiofileoutput = Settings.fileoutputname + "." + Settings.audcodec;
        
        if (Settings.domuxing == true){
//            //extract audio - not really needed if processing sound anyway in video enc??
//            extractcomm = Settings.Mplayerpath + sp +
//                    "-dumpaudio" + sp + input + sp + "-dumpfile" + sp + Settings.audiofileoutput ;
//            //or convert to wav if needs to be recompressed
//            extractcomm = Settings.Mplayerpath + sp +
//                    input  + sp + "-ao pcm:file=" + Settings.audiofileoutput +sp + "-vo null";
            
            //must set encoder to process audio even when you only need the video
            //if two passes do rawaudio in first pass and rawvideo in second pass, with one
            //pass u need to dump the audio with mplayer first as above.
            
            doaudioArgs(1);
            
            //convert video only
            dovideoArgs(1);
            args = audioargs + videoargs;
            //uses the audio file as output IF two passes
            command1 = path + sp + args + input + sp + "-o" + sp + "\"" + Settings.audiofileoutput + "\"";
            
            //must still deal with audio, but discard it..
            audioargs = "-oac" + sp + "copy" + sp;
            //even for one pass, the command must be executed twice to do the audio
            //seperately for muxing
            dovideoArgs(2);
            args = audioargs + videoargs;
            command2 = path + sp + args + input + sp + "-o" + sp + "\"" + output + "\"";
            
            //mux them
            muxerpanel.updateCommandfromSettings(); //update muxer settings if not avi
            
        }
        //otherwise just normally create an avi as Mencoder is designed to do
        else{
            //do audio codec arguments first
            if (Settings.passes == 1){
                doaudioArgs(1);
            } else { audioargs = "-nosound" + sp;}
            //put everything together
            dovideoArgs(1);
            args = audioargs + videoargs;
            command1 = path + sp + args + input + sp + "-o" + sp + "\"" + output + "\"";
            if (Settings.passes == 2){
                doaudioArgs(1);
                dovideoArgs(2);
            }
            args = audioargs + videoargs;
            command2 = path + sp + args + input + sp + "-o" + sp + "\"" + output + "\"";
        }
    }
    
    private void updatefromArgsField(){
        command1 = path  + sp + args + input + sp + "-o" + sp + "\"" + output + "\"";
    }
    
    /**updates command display text area*/
    private void updateCommandDisplay(){
        commanddisplayarea.setText("");
        commanddisplayarea.append(command1); commanddisplayarea.append("\n\n");
        if (Settings.passes == 2 | Settings.domuxing == true){
            commanddisplayarea.append(command2); commanddisplayarea.append("\n\n");
        }
        if (Settings.domuxing == true){
            commanddisplayarea.append(muxerpanel.muxCommand1);
            commanddisplayarea.append("\n\n");
            if (Settings.container == "mkv"){
                commanddisplayarea.append(muxerpanel.muxCommand2);
            }
        }
    }
    
    /** Updates GUI components after certain command line and user input changes */
    private void updateComponents(){
        argsfield.setText(args);
        fileinputfield.setText(Settings.fileinputname);
        getdiscpathField.setText(Settings.dvddev);
        outputfield.setText(Settings.fileoutputname);
        updateCommandDisplay();
        muxerpanel.update();
        if (Settings.usemod16 == false & Settings.resize == true){
            ScaleHeightSpinner.setEnabled(true);
        } else {
            ScaleHeightSpinner.setEnabled(false);
        }
        if (VideoTitle.targetaspectratio != 0){
            targetaspectlabel.setText(Double.toString(VideoTitle.targetaspectratio)+":1");
            actualaspectlabel.setText(Double.toString(VideoTitle.actualaspectratio)+":1");
        }
        
        if (VideoTitle.error > 0.01){
            aspecterrorLabel.setText(Double.toString(VideoTitle.error)+"%");
        } else {
            aspecterrorLabel.setText("N/A");
        }
        
    }
    
    /** Reset relevant gui widgets when new video is loaded */
    private void resetComponents(){
        resetCrop();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        jButton2 = new javax.swing.JButton();
        confirmDialog = new javax.swing.JDialog();
        confirmokButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        openprevwindowButton = new javax.swing.JButton();
        framebackButton = new javax.swing.JButton();
        frameforwardButton = new javax.swing.JButton();
        cropfailedDialog = new javax.swing.JDialog();
        jLabel6 = new javax.swing.JLabel();
        cropfaileddialogOK = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        argsfield = new javax.swing.JTextField();
        fileinputfield = new javax.swing.JTextField();
        GoButton = new javax.swing.JButton();
        CancelButton = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        outputfield = new javax.swing.JTextField();
        progresslabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        commanddisplayarea = new javax.swing.JTextArea();
        getinputfilebutton = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel10 = new javax.swing.JPanel();
        videoInfoPanel = new jmencode.VideoInfoForm();
        jPanel2 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        audcodecfield = new javax.swing.JComboBox();
        audcodecfield = new JComboBox(Settings.acodecslist);
        jPanel6 = new javax.swing.JPanel();
        audbitrateComboBox = new javax.swing.JComboBox();
        audbitrateComboBox = new JComboBox(Settings.abitratelist);
        audQualityField = new javax.swing.JSpinner();
        BitrateLabel = new javax.swing.JLabel();
        QualityLabel = new javax.swing.JLabel();
        volgainComboBox = new javax.swing.JComboBox();
        Integer[] vlist = new Integer[]{1,2,3,4,5,6,7,8,9,10};
        volgainComboBox = new JComboBox(vlist);
        usevolGainCheckBox = new javax.swing.JCheckBox();
        extractAudioButton = new javax.swing.JButton();
        extractasWAVButton = new javax.swing.JButton();
        audtracknumField = new javax.swing.JComboBox();
        Integer[] alist = new Integer[]{1};
        audtracknumField = new JComboBox(alist);
        jLabel15 = new javax.swing.JLabel();
        previewCurrentTrackButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        codecfield = new javax.swing.JComboBox();
        codecfield = new JComboBox(Settings.codecslist);
        jLabel4 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        bitratefield = new javax.swing.JTextField();
        onePassButton = new javax.swing.JRadioButton();
        twoPassButton = new javax.swing.JRadioButton();
        startLabel = new javax.swing.JLabel();
        seglengthLabel = new javax.swing.JLabel();
        selecttimeCheckBox = new javax.swing.JCheckBox();
        starttimeSpinner = new javax.swing.JSpinner();
        Calendar cal = new GregorianCalendar(2000, Calendar.JANUARY, 01);
        Date date = cal.getTime();
        SpinnerDateModel sm = new SpinnerDateModel(date, null, null, Calendar.SECOND );
        starttimeSpinner = new JSpinner(sm);
        JSpinner.DateEditor de = new JSpinner.DateEditor(starttimeSpinner, "HH:mm:ss");
        starttimeSpinner.setEditor(de);

        scalequalityfield = new javax.swing.JComboBox();
        scalequalityfield = new JComboBox(Settings.scalerlist);

        jLabel10 = new javax.swing.JLabel();
        previewVideoButton = new javax.swing.JButton();
        noframesComboBox = new javax.swing.JComboBox();
        noframesComboBox = new JComboBox(Settings.noframeslist);
        jLabel21 = new javax.swing.JLabel();
        playagainButton = new javax.swing.JButton();
        aspectratiofield = new javax.swing.JComboBox();
        aspectratiofield = new JComboBox(Settings.aspratiolist);
        jLabel11 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        fittoSizeComboBox = new javax.swing.JComboBox();
        fittoSizeComboBox = new JComboBox(Settings.fitsizeslist);
        segmentlengthfield = new javax.swing.JTextField();
        segmentlengthfield.setText(Integer.toString(Settings.length));
        inputDARfield = new javax.swing.JComboBox();
        inputDARfield = new JComboBox(Settings.DARlist);
        jLabel12 = new javax.swing.JLabel();
        fitbitratevalueLabel = new javax.swing.JLabel();
        bitrateLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        constantbitrateButton = new javax.swing.JRadioButton();
        fittoCDButton = new javax.swing.JRadioButton();
        vidqualityLabel = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        useBframesCheckBox = new javax.swing.JCheckBox();
        useBframesSpinner = new javax.swing.JSpinner();
        containertypeComboBox = new javax.swing.JComboBox();
        containertypeComboBox = new JComboBox(Settings.containerlist);
        jLabel14 = new javax.swing.JLabel();
        fourccfield = new javax.swing.JComboBox();
        fourccfield = new JComboBox(Settings.fourcclist);
        jLabel27 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        resizeCheckBox = new javax.swing.JCheckBox();
        ScaleWidthSpinner = new javax.swing.JSpinner();
        resetsizeButton = new javax.swing.JButton();
        ScaleHeightSpinner = new javax.swing.JSpinner();
        refreshprevButton = new javax.swing.JButton();
        cropdetectButton = new javax.swing.JButton();
        cropySpinner = new javax.swing.JSpinner();
        cropxSpinner = new javax.swing.JSpinner();
        crophSpinner = new javax.swing.JSpinner();
        jLabel16 = new javax.swing.JLabel();
        cropCheckBox = new javax.swing.JCheckBox();
        cropwSpinner = new javax.swing.JSpinner();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        autoScaleButton = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        previewPanel = new jmencode.ImgPreviewPanel();
        jLabel7 = new javax.swing.JLabel();
        previewPanel1 = new jmencode.ImgPreviewPanel();
        jLabel20 = new javax.swing.JLabel();
        vidpreviewSlider = new javax.swing.JSlider();
        jPanel11 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        targetaspectlabel = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        actualaspectlabel = new javax.swing.JLabel();
        aspecterrorLabel = new javax.swing.JLabel();
        rescaleOptionsButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        unsharpBox = new javax.swing.JCheckBox();
        jPanel5 = new javax.swing.JPanel();
        muxerpanel = new jmencode.MuxerForm();
        getoutputfilebutton = new javax.swing.JButton();
        discinputField = new javax.swing.JTextField();
        setDiscInputButton = new javax.swing.JRadioButton();
        setfileInputButton = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        getdiscpathField = new javax.swing.JTextField();
        getdiscpathbutton = new javax.swing.JButton();
        procProgressBar = new javax.swing.JProgressBar();
        dvdtitleField = new javax.swing.JComboBox();
        dvdtitleField = new JComboBox(Settings.titlelist);
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        viewlogmenuItem = new javax.swing.JMenuItem();
        playmediaMenuItem = new javax.swing.JMenuItem();
        quitMenuItem = new javax.swing.JMenuItem();
        toolsMenu = new javax.swing.JMenu();
        MuxerMenuItem = new javax.swing.JMenuItem();
        bitratecalcMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        settingsMenuItem = new javax.swing.JMenuItem();
        setLNFMenuItem = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();

        jButton2.setText("jButton2");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        confirmDialog.setTitle("Confirm Action");
        confirmDialog.setAlwaysOnTop(true);
        confirmDialog.setModal(true);
        confirmDialog.setResizable(false);
        confirmokButton.setText("OK");
        confirmokButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmokButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jLabel13.setText("Are you sure you want to Cancel?");

        org.jdesktop.layout.GroupLayout confirmDialogLayout = new org.jdesktop.layout.GroupLayout(confirmDialog.getContentPane());
        confirmDialog.getContentPane().setLayout(confirmDialogLayout);
        confirmDialogLayout.setHorizontalGroup(
            confirmDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(confirmDialogLayout.createSequentialGroup()
                .add(40, 40, 40)
                .add(confirmDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(confirmDialogLayout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(confirmokButton)
                        .add(25, 25, 25)
                        .add(cancelButton))
                    .add(jLabel13))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        confirmDialogLayout.setVerticalGroup(
            confirmDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, confirmDialogLayout.createSequentialGroup()
                .add(24, 24, 24)
                .add(jLabel13)
                .add(15, 15, 15)
                .add(confirmDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(confirmokButton)
                    .add(cancelButton))
                .addContainerGap())
        );
        openprevwindowButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jmencode/images/openwindow.png")));
        openprevwindowButton.setToolTipText("Open preview in a window");
        openprevwindowButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        openprevwindowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openprevwindowButtonActionPerformed(evt);
            }
        });

        framebackButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jmencode/images/slower.png")));
        framebackButton.setToolTipText("Preview frame backward");
        framebackButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        framebackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                framebackButtonActionPerformed(evt);
            }
        });

        frameforwardButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jmencode/images/faster.png")));
        frameforwardButton.setToolTipText("Preview frame forward");
        frameforwardButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        frameforwardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frameforwardButtonActionPerformed(evt);
            }
        });

        cropfailedDialog.setAlwaysOnTop(true);
        cropfailedDialog.setResizable(false);
        jLabel6.setText("MPlayer failed to correctly detect the crop values.");

        cropfaileddialogOK.setText("OK");
        cropfaileddialogOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cropfaileddialogOKActionPerformed(evt);
            }
        });

        jLabel22.setText("Try manual entry or using the mouse to select a");

        jLabel24.setText("rectangle of the region to crop in the input preview.");

        org.jdesktop.layout.GroupLayout cropfailedDialogLayout = new org.jdesktop.layout.GroupLayout(cropfailedDialog.getContentPane());
        cropfailedDialog.getContentPane().setLayout(cropfailedDialogLayout);
        cropfailedDialogLayout.setHorizontalGroup(
            cropfailedDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cropfailedDialogLayout.createSequentialGroup()
                .add(cropfailedDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cropfailedDialogLayout.createSequentialGroup()
                        .add(40, 40, 40)
                        .add(cropfailedDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
                            .add(jLabel22, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
                            .add(jLabel24, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .add(cropfailedDialogLayout.createSequentialGroup()
                        .add(116, 116, 116)
                        .add(cropfaileddialogOK, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 77, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(35, Short.MAX_VALUE))
        );
        cropfailedDialogLayout.setVerticalGroup(
            cropfailedDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cropfailedDialogLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel6)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel22)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel24)
                .add(16, 16, 16)
                .add(cropfaileddialogOK)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("JMEncode");
        jLabel2.setText("Manual Edit:");

        argsfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                argsfieldActionPerformed(evt);
            }
        });

        fileinputfield.setEnabled(false);
        fileinputfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileinputfieldActionPerformed(evt);
            }
        });

        GoButton.setText("Encode");
        GoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GoButtonActionPerformed(evt);
            }
        });

        CancelButton.setText("Cancel");
        CancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelButtonActionPerformed(evt);
            }
        });

        jLabel5.setText("Output Name:");

        outputfield.setText(Settings.fileoutputname);
        outputfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputfieldActionPerformed(evt);
            }
        });
        outputfield.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                outputfieldKeyReleased(evt);
            }
        });

        progresslabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        progresslabel.setForeground(new java.awt.Color(204, 0, 51));
        progresslabel.setText("The following command(s) will be run. Check here before execution...");

        jScrollPane1.setMaximumSize(new java.awt.Dimension(10767, 10767));
        commanddisplayarea.setColumns(20);
        commanddisplayarea.setEditable(false);
        commanddisplayarea.setLineWrap(true);
        commanddisplayarea.setRows(10);
        commanddisplayarea.setToolTipText("shows final command to be run");
        commanddisplayarea.setWrapStyleWord(true);
        commanddisplayarea.setMaximumSize(new java.awt.Dimension(2147483647, 4500));
        jScrollPane1.setViewportView(commanddisplayarea);

        getinputfilebutton.setText("open");
        getinputfilebutton.setToolTipText("open file");
        getinputfilebutton.setEnabled(false);
        getinputfilebutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getinputfilebuttonActionPerformed(evt);
            }
        });

        jTabbedPane1.setMinimumSize(new java.awt.Dimension(500, 626));
        videoInfoPanel.setFont(new java.awt.Font("Arial", 0, 10));

        org.jdesktop.layout.GroupLayout jPanel10Layout = new org.jdesktop.layout.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel10Layout.createSequentialGroup()
                .add(28, 28, 28)
                .add(videoInfoPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(183, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .add(videoInfoPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
                .addContainerGap())
        );
        jTabbedPane1.addTab("Video Info", jPanel10);

        jLabel9.setText("Codec:");

        audcodecfield.setToolTipText("Choose audio compression type");
        audcodecfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                audcodecfieldActionPerformed(evt);
            }
        });

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Options"));
        audbitrateComboBox.setToolTipText("Sets video bitrate");
        audbitrateComboBox.setSelectedItem(Integer.toString(Settings.audbitrate));
        if (Settings.audcodec == "copy"){
            audbitrateComboBox.setEnabled(false);
        }else{
            audbitrateComboBox.setEnabled(true);
        }
        audbitrateComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                audbitrateComboBoxActionPerformed(evt);
            }
        });

        audQualityField.setValue(Settings.audquality);
        if (Settings.audcodec == "aac"){
            audQualityField.setEnabled(true);
        } else{
            audQualityField.setEnabled(false);
        }

        audQualityField.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                audQualityFieldStateChanged(evt);
            }
        });

        BitrateLabel.setText("Bitrate:");

        QualityLabel.setText("Quality:");

        volgainComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                volgainComboBoxActionPerformed(evt);
            }
        });

        usevolGainCheckBox.setText("Volume Gain:");
        usevolGainCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        usevolGainCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        usevolGainCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usevolGainCheckBoxActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel6Layout.createSequentialGroup()
                        .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(BitrateLabel)
                            .add(QualityLabel))
                        .add(21, 21, 21)
                        .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(audQualityField)
                            .add(audbitrateComboBox, 0, 62, Short.MAX_VALUE)))
                    .add(jPanel6Layout.createSequentialGroup()
                        .add(usevolGainCheckBox)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(volgainComboBox, 0, 43, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                .addContainerGap(217, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(audbitrateComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(BitrateLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(audQualityField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(QualityLabel))
                .add(25, 25, 25)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(usevolGainCheckBox)
                    .add(volgainComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(179, Short.MAX_VALUE))
        );

        extractAudioButton.setText("Extract Audio Now");
        extractAudioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                extractAudioButtonActionPerformed(evt);
            }
        });

        extractasWAVButton.setText("Extract to WAV");
        extractasWAVButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                extractasWAVButtonActionPerformed(evt);
            }
        });

        audtracknumField.setToolTipText("Audio tracks list for current title (if dvd)");
        audtracknumField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                audtracknumFieldActionPerformed(evt);
            }
        });

        jLabel15.setText("Track:");

        previewCurrentTrackButton.setText("Preview Current Track");
        previewCurrentTrackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previewCurrentTrackButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(extractasWAVButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(previewCurrentTrackButton, 0, 0, Short.MAX_VALUE)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel9)
                            .add(jLabel15))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(audtracknumField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(audcodecfield, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(extractAudioButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(33, 33, 33)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel9)
                            .add(audcodecfield, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(audtracknumField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel15))
                        .add(91, 91, 91)
                        .add(extractAudioButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(extractasWAVButton)
                        .add(18, 18, 18)
                        .add(previewCurrentTrackButton))
                    .add(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jTabbedPane1.addTab("Audio", jPanel2);

        codecfield.setToolTipText("Choose video compression type");
        codecfield.setSelectedItem(Settings.codec);
        codecfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                codecfieldActionPerformed(evt);
            }
        });

        jLabel4.setText("Codec:");

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Options"));
        bitratefield.setToolTipText("Enter bitrate here (VBR)");
        bitratefield.setText(Integer.toString(Settings.bitrate));
        bitratefield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bitratefieldActionPerformed(evt);
            }
        });
        bitratefield.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                bitratefieldFocusLost(evt);
            }
        });

        buttonGroup1.add(onePassButton);
        onePassButton.setText("1-pass");
        onePassButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        onePassButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        onePassButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onePassButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(twoPassButton);
        twoPassButton.setSelected(true);
        twoPassButton.setText("2-pass");
        twoPassButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        twoPassButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        twoPassButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                twoPassButtonActionPerformed(evt);
            }
        });

        startLabel.setText("Start");
        startLabel.setEnabled(false);

        seglengthLabel.setText("Length of segment:");
        seglengthLabel.setEnabled(false);

        selecttimeCheckBox.setText("Select Time");
        selecttimeCheckBox.setToolTipText("Select Start Time and duration. Not working for certain file types");
        selecttimeCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        selecttimeCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        selecttimeCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selecttimeCheckBoxActionPerformed(evt);
            }
        });

        starttimeSpinner.setToolTipText("Start time in hr:mm:ss");
        starttimeSpinner.setEnabled(false);
        starttimeSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                starttimeSpinnerStateChanged(evt);
            }
        });

        scalequalityfield.setSelectedItem("bicubic");
        scalequalityfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scalequalityfieldActionPerformed(evt);
            }
        });

        jLabel10.setText("Scale Quality:");

        previewVideoButton.setText("Preview Clip");
        previewVideoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previewVideoButtonActionPerformed(evt);
            }
        });

        noframesComboBox.setSelectedItem(Integer.toString(Settings.prevframes));
        noframesComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noframesComboBoxActionPerformed(evt);
            }
        });

        jLabel21.setText("No. Frames to use:");

        playagainButton.setText("Play Again");
        playagainButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playagainButtonActionPerformed(evt);
            }
        });

        aspectratiofield.setToolTipText("Aspect ratio of source. Auto tries to estimate it using crop values.");
        aspectratiofield.setSelectedItem(Double.toString(VideoTitle.targetaspectratio));
        aspectratiofield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aspectratiofieldActionPerformed(evt);
            }
        });

        jLabel11.setText("Source Aspect Ratio:");

        fittoSizeComboBox.setToolTipText("Preset Values for target file size");
        fittoSizeComboBox.setEnabled(false);
        fittoSizeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fittoSizeComboBoxActionPerformed(evt);
            }
        });

        segmentlengthfield.setToolTipText("Enter time in seconds.");
        segmentlengthfield.setEnabled(false);
        segmentlengthfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                segmentlengthfieldActionPerformed(evt);
            }
        });
        segmentlengthfield.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                segmentlengthfieldFocusLost(evt);
            }
        });

        inputDARfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputDARfieldActionPerformed(evt);
            }
        });

        jLabel12.setText("Input DAR:");

        fitbitratevalueLabel.setText("N/A");
        fitbitratevalueLabel.setEnabled(false);

        bitrateLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        bitrateLabel.setText("Bitrate:");
        bitrateLabel.setEnabled(false);

        buttonGroup4.add(constantbitrateButton);
        constantbitrateButton.setSelected(true);
        constantbitrateButton.setText("Custom Bitrate");
        constantbitrateButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        constantbitrateButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        constantbitrateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                constantbitrateButtonActionPerformed(evt);
            }
        });

        buttonGroup4.add(fittoCDButton);
        fittoCDButton.setText("Fit Size:");
        fittoCDButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        fittoCDButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        fittoCDButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fittoCDButtonActionPerformed(evt);
            }
        });

        vidqualityLabel.setForeground(new java.awt.Color(153, 0, 0));
        vidqualityLabel.setText("N/A");

        jLabel26.setText("Quality:");

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 426, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jSeparator3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 417, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(previewVideoButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 91, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(playagainButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 83, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel21)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(noframesComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 64, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(constantbitrateButton)
                            .add(fittoCDButton))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(bitratefield, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 68, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(fittoSizeComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 89, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel3Layout.createSequentialGroup()
                                .add(24, 24, 24)
                                .add(onePassButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(twoPassButton))
                            .add(jPanel3Layout.createSequentialGroup()
                                .add(13, 13, 13)
                                .add(bitrateLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(fitbitratevalueLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 56, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(14, 14, 14)
                                .add(jLabel26)
                                .add(6, 6, 6)
                                .add(vidqualityLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 52, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jPanel3Layout.createSequentialGroup()
                                .add(jLabel10)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(scalequalityfield, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 83, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(23, 23, 23)
                                .add(jLabel11))
                            .add(jLabel12))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(inputDARfield, 0, 58, Short.MAX_VALUE)
                            .add(aspectratiofield, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 58, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(selecttimeCheckBox)
                        .add(20, 20, 20)
                        .add(startLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(starttimeSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 88, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(seglengthLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(segmentlengthfield, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 54, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(constantbitrateButton)
                    .add(bitratefield, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(onePassButton)
                    .add(twoPassButton))
                .add(15, 15, 15)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(fittoCDButton)
                    .add(fittoSizeComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(bitrateLabel)
                    .add(fitbitratevalueLabel)
                    .add(jLabel26)
                    .add(vidqualityLabel))
                .add(15, 15, 15)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(14, 14, 14)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE, false)
                    .add(selecttimeCheckBox)
                    .add(startLabel)
                    .add(starttimeSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(seglengthLabel)
                    .add(segmentlengthfield, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(27, 27, 27)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(scalequalityfield, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel11)
                    .add(aspectratiofield, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel10))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE, false)
                    .add(inputDARfield, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel12))
                .add(16, 16, 16)
                .add(jSeparator3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel21)
                    .add(noframesComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(previewVideoButton)
                    .add(playagainButton))
                .addContainerGap())
        );

        jButton1.setText("Bitrate Calc");
        jButton1.setToolTipText("Bitrate calculator");
        jButton1.setEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        useBframesCheckBox.setText("Use B-frames");
        useBframesCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        useBframesCheckBox.setEnabled(false);
        useBframesCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        if (Settings.codec == "x264"){
            useBframesCheckBox.setEnabled(true);
        } else{
            useBframesCheckBox.setEnabled(false);
        }

        useBframesSpinner.setEnabled(false);
        if (Settings.codec == "x264"){
            useBframesSpinner.setEnabled(true);
        } else{
            useBframesSpinner.setEnabled(false);
        }

        containertypeComboBox.setToolTipText("Changing this forces changes in codec and fourcc fields to show you the recommended settings");
        containertypeComboBox.setSelectedItem(Settings.container);
        containertypeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                containertypeComboBoxActionPerformed(evt);
            }
        });

        jLabel14.setText("Container:");

        fourccfield.setToolTipText("Do not change this unless you know what you are doing.");
        fourccfield.setSelectedItem(Settings.codec);
        fourccfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fourccfieldActionPerformed(evt);
            }
        });

        jLabel27.setText("fourCC:");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel14)
                    .add(containertypeComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 67, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel4)
                            .add(jLabel27))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(fourccfield, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(codecfield, 0, 79, Short.MAX_VALUE)))
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jButton1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1Layout.createSequentialGroup()
                            .add(useBframesCheckBox)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(useBframesSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(74, 74, 74))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(28, 28, 28)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(codecfield, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(fourccfield, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel27))
                .add(16, 16, 16)
                .add(jButton1)
                .add(27, 27, 27)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(useBframesCheckBox)
                    .add(useBframesSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(24, 24, 24)
                .add(jLabel14)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(containertypeComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(96, 96, 96))
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(30, 30, 30))
        );
        jTabbedPane1.addTab("Video", jPanel1);

        jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        resizeCheckBox.setText("Resize:");
        resizeCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        resizeCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        if (Settings.resize == true){
            resizeCheckBox.setSelected(true);
        } else{
            resizeCheckBox.setSelected(false);
        }
        resizeCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resizeCheckBoxActionPerformed(evt);
            }
        });

        ScaleHeightSpinner.setValue(Settings.scaleh);
        if (Settings.resize == true){
            ScaleHeightSpinner.setEnabled(true);
        } else{
            ScaleHeightSpinner.setEnabled(false);
        }
        ScaleWidthSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ScaleWidthSpinnerStateChanged(evt);
            }
        });

        resetsizeButton.setText("Reset");
        resetsizeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetsizeButtonActionPerformed(evt);
            }
        });

        ScaleWidthSpinner.setValue(Settings.scalew);
        if (Settings.resize == true){
            ScaleWidthSpinner.setEnabled(true);
        } else{
            ScaleWidthSpinner.setEnabled(false);
        }
        ScaleHeightSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ScaleHeightSpinnerStateChanged(evt);
            }
        });

        refreshprevButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jmencode/images/ok.png")));
        refreshprevButton.setToolTipText("Redo preview with current crop and resize");
        refreshprevButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        refreshprevButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshprevButtonActionPerformed(evt);
            }
        });

        cropdetectButton.setText("Detect Crop");
        cropdetectButton.setToolTipText("Use MPlayer to detect the crop. ");
        cropdetectButton.setEnabled(false);
        cropdetectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cropdetectButtonActionPerformed(evt);
            }
        });

        cropySpinner.setEnabled(false);
        cropySpinner.setValue(VideoTitle.cropy);
        cropySpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cropySpinnerStateChanged(evt);
            }
        });

        cropxSpinner.setEnabled(false);
        cropxSpinner.setValue(VideoTitle.cropx);
        cropxSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cropxSpinnerStateChanged(evt);
            }
        });

        crophSpinner.setEnabled(false);
        crophSpinner.setValue(VideoTitle.croph);
        crophSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                crophSpinnerStateChanged(evt);
            }
        });

        jLabel16.setText("w");

        cropCheckBox.setText("Crop:");
        cropCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cropCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cropCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cropCheckBoxActionPerformed(evt);
            }
        });

        cropwSpinner.setEnabled(false);
        cropwSpinner.setValue(VideoTitle.cropw);
        cropwSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cropwSpinnerStateChanged(evt);
            }
        });

        jLabel17.setText("h");

        jLabel18.setText("x");

        jLabel19.setText("y");

        autoScaleButton.setText("Auto Scale");
        autoScaleButton.setToolTipText("Use this to recalculate the scale based on current aspect ratio and DAR");
        autoScaleButton.setEnabled(false);
        autoScaleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoScaleButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(jPanel8Layout.createSequentialGroup()
                        .add(cropCheckBox)
                        .add(15, 15, 15)
                        .add(jLabel16)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cropwSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 42, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel17)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(crophSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 42, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel18)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cropxSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel19)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cropySpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cropdetectButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 91, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel8Layout.createSequentialGroup()
                        .add(resizeCheckBox)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(ScaleWidthSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(ScaleHeightSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 63, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(autoScaleButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(resetsizeButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 77, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(14, 14, 14)
                .add(refreshprevButton)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel8Layout.createSequentialGroup()
                        .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel8Layout.createSequentialGroup()
                                .add(5, 5, 5)
                                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(resizeCheckBox)
                                    .add(ScaleWidthSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(ScaleHeightSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                            .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(resetsizeButton)
                                .add(autoScaleButton)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 9, Short.MAX_VALUE)
                        .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(crophSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(cropxSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(cropwSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(cropySpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel16)
                            .add(jLabel17)
                            .add(jLabel18)
                            .add(jLabel19)
                            .add(cropCheckBox)
                            .add(cropdetectButton)))
                    .add(refreshprevButton))
                .addContainerGap())
        );

        previewPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        previewPanel.setToolTipText("Can manually set crop here by dragging mouse. Crop box must be ticked");
        previewPanel.setDoubleBuffered(false);
        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel7.setForeground(new java.awt.Color(255, 255, 102));
        jLabel7.setText("Input");

        org.jdesktop.layout.GroupLayout previewPanelLayout = new org.jdesktop.layout.GroupLayout(previewPanel);
        previewPanel.setLayout(previewPanelLayout);
        previewPanelLayout.setHorizontalGroup(
            previewPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(previewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel7)
                .addContainerGap(237, Short.MAX_VALUE))
        );
        previewPanelLayout.setVerticalGroup(
            previewPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(previewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel7)
                .addContainerGap(143, Short.MAX_VALUE))
        );

        previewPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        previewPanel1.setDoubleBuffered(false);
        previewPanel1.selectionenabled = false;
        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel20.setForeground(new java.awt.Color(255, 255, 51));
        jLabel20.setText("Output");

        org.jdesktop.layout.GroupLayout previewPanel1Layout = new org.jdesktop.layout.GroupLayout(previewPanel1);
        previewPanel1.setLayout(previewPanel1Layout);
        previewPanel1Layout.setHorizontalGroup(
            previewPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(previewPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel20)
                .addContainerGap(263, Short.MAX_VALUE))
        );
        previewPanel1Layout.setVerticalGroup(
            previewPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(previewPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(144, Short.MAX_VALUE))
        );

        vidpreviewSlider.setValue(0);
        vidpreviewSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                vidpreviewSliderMouseReleased(evt);
            }
        });

        jPanel11.setToolTipText("These values apply when resize is checked");
        jLabel23.setForeground(new java.awt.Color(153, 0, 0));
        jLabel23.setText("Target Aspect Ratio:");

        jLabel25.setForeground(new java.awt.Color(204, 0, 0));
        jLabel25.setText("Actual Aspect Ratio:");

        targetaspectlabel.setText("Auto");

        jLabel31.setText("Error:");

        actualaspectlabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        actualaspectlabel.setText("N/A");

        aspecterrorLabel.setText("N/A");

        org.jdesktop.layout.GroupLayout jPanel11Layout = new org.jdesktop.layout.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel11Layout.createSequentialGroup()
                        .add(jLabel23)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(targetaspectlabel)
                        .add(31, 31, 31)
                        .add(jLabel31)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(aspecterrorLabel))
                    .add(jPanel11Layout.createSequentialGroup()
                        .add(jLabel25)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(actualaspectlabel)))
                .addContainerGap(100, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel11Layout.createSequentialGroup()
                .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel31)
                    .add(targetaspectlabel)
                    .add(jLabel23)
                    .add(aspecterrorLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel25)
                    .add(actualaspectlabel)))
        );

        org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(vidpreviewSlider, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                    .add(previewPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(25, 25, 25)
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(previewPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel11, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(previewPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(previewPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel9Layout.createSequentialGroup()
                        .add(2, 2, 2)
                        .add(vidpreviewSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel9Layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        rescaleOptionsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jmencode/images/previewstill.png")));
        rescaleOptionsButton.setToolTipText("Rescaling Options");
        rescaleOptionsButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        rescaleOptionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rescaleOptionsButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel7Layout.createSequentialGroup()
                        .add(29, 29, 29)
                        .add(jPanel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(rescaleOptionsButton))
                    .add(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel7Layout.createSequentialGroup()
                .add(jPanel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel7Layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel7Layout.createSequentialGroup()
                        .add(18, 18, 18)
                        .add(rescaleOptionsButton)))
                .add(8, 8, 8))
        );
        jTabbedPane1.addTab("Rescale & Crop", jPanel7);

        jLabel1.setText("not yet implemented");

        unsharpBox.setText("unsharp mask");
        unsharpBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        unsharpBox.setEnabled(false);
        unsharpBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        unsharpBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unsharpBoxActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel12Layout = new org.jdesktop.layout.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel12Layout.createSequentialGroup()
                .add(45, 45, 45)
                .add(unsharpBox)
                .addContainerGap(173, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .add(unsharpBox)
                .addContainerGap(276, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .add(21, 21, 21)
                .add(jPanel12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(74, 74, 74)
                .add(jLabel1)
                .addContainerGap(179, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(74, 74, 74)
                        .add(jLabel1))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel12, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jTabbedPane1.addTab("Filters", jPanel4);

        jPanel5.setAutoscrolls(true);

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(23, 23, 23)
                .add(muxerpanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)
                .add(45, 45, 45))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(muxerpanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(287, 287, 287))
        );
        jTabbedPane1.addTab("Container", jPanel5);

        getoutputfilebutton.setText("Output File");
        getoutputfilebutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getoutputfilebuttonActionPerformed(evt);
            }
        });

        discinputField.setText("dvd://");

        buttonGroup2.add(setDiscInputButton);
        setDiscInputButton.setSelected(true);
        setDiscInputButton.setText("Disc Input:");
        setDiscInputButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setDiscInputButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        setDiscInputButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setDiscInputButtonActionPerformed(evt);
            }
        });
        setDiscInputButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                setDiscInputButtonMouseReleased(evt);
            }
        });

        buttonGroup2.add(setfileInputButton);
        setfileInputButton.setText("Single File Input:");
        setfileInputButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setfileInputButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        setfileInputButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setfileInputButtonActionPerformed(evt);
            }
        });
        setfileInputButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                setfileInputButtonMouseReleased(evt);
            }
        });

        jLabel3.setText("DVD Title:");

        jLabel8.setText("Path:");

        getdiscpathField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getdiscpathFieldActionPerformed(evt);
            }
        });

        getdiscpathbutton.setText("Open folder");
        getdiscpathbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getdiscpathbuttonActionPerformed(evt);
            }
        });

        procProgressBar.setToolTipText("Current Progress");
        procProgressBar.setStringPainted(true);

        dvdtitleField.setEnabled(false);
        dvdtitleField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dvdtitleFieldActionPerformed(evt);
            }
        });

        fileMenu.setText("File");
        viewlogmenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jmencode/images/open.png")));
        viewlogmenuItem.setText("View Log");
        viewlogmenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewlogmenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(viewlogmenuItem);

        playmediaMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jmencode/images/open.png")));
        playmediaMenuItem.setText("Play Media");
        playmediaMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playmediaMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(playmediaMenuItem);

        quitMenuItem.setText("Quit");
        quitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(quitMenuItem);

        jMenuBar1.add(fileMenu);

        toolsMenu.setText("Tools");
        MuxerMenuItem.setText("Muxer");
        MuxerMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MuxerMenuItemActionPerformed(evt);
            }
        });

        toolsMenu.add(MuxerMenuItem);

        bitratecalcMenuItem.setText("Bitrate Calc");
        bitratecalcMenuItem.setEnabled(false);
        bitratecalcMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bitratecalcMenuItemActionPerformed(evt);
            }
        });

        toolsMenu.add(bitratecalcMenuItem);

        toolsMenu.add(jSeparator2);

        settingsMenuItem.setText("Settings");
        settingsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsMenuItemActionPerformed(evt);
            }
        });

        toolsMenu.add(settingsMenuItem);

        setLNFMenuItem.setText("Set LNF");
        setLNFMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setLNFMenuItemActionPerformed(evt);
            }
        });

        toolsMenu.add(setLNFMenuItem);

        jMenuBar1.add(toolsMenu);

        jMenu1.setText("Help");
        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });

        jMenu1.add(aboutMenuItem);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 678, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(setfileInputButton)
                            .add(setDiscInputButton))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(discinputField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 49, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel3)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(dvdtitleField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel8)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(getdiscpathField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE))
                            .add(fileinputfield, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 535, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(getdiscpathbutton, 0, 0, Short.MAX_VALUE)
                            .add(getinputfilebutton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, Short.MAX_VALUE))
                        .add(13, 13, 13))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(progresslabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 399, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 279, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jLabel5)
                                    .add(jLabel2))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(layout.createSequentialGroup()
                                        .add(outputfield, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 557, Short.MAX_VALUE)
                                        .add(15, 15, 15)
                                        .add(getoutputfilebutton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 34, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, argsfield, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 606, Short.MAX_VALUE)))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(procProgressBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
                                .add(90, 90, 90)
                                .add(GoButton)
                                .add(14, 14, 14)
                                .add(CancelButton)
                                .add(76, 76, 76)))
                        .addContainerGap())
                    .add(layout.createSequentialGroup()
                        .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 678, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(setDiscInputButton)
                    .add(discinputField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3)
                    .add(jLabel8)
                    .add(getdiscpathField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(getdiscpathbutton)
                    .add(dvdtitleField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(setfileInputButton)
                    .add(getinputfilebutton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(fileinputfield, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 352, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(argsfield, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(getoutputfilebutton)
                    .add(jLabel5)
                    .add(outputfield, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(progresslabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(GoButton)
                    .add(CancelButton)
                    .add(procProgressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void outputfieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_outputfieldKeyReleased
        Settings.fileoutputname = outputfield.getText();
        update();            
    }//GEN-LAST:event_outputfieldKeyReleased

    private void setDiscInputButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_setDiscInputButtonMouseReleased
        Settings.usediscinput = true;
        VideoTitle.type = "dvd";
        discinputField.setEnabled(true);
        dvdtitleField.setEnabled(true);
        getdiscpathbutton.setEnabled(true);
        getdiscpathField.setEnabled(true);
        fileinputfield.setEnabled(false);
        getinputfilebutton.setEnabled(false);
        //update();
        
    }//GEN-LAST:event_setDiscInputButtonMouseReleased

    private void setfileInputButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_setfileInputButtonMouseReleased
        Settings.usediscinput = false;
        System.out.println("Using file input");
        discinputField.setEnabled(false);
        dvdtitleField.setEnabled(false);
        getdiscpathbutton.setEnabled(false);
        getdiscpathField.setEnabled(false);
        fileinputfield.setEnabled(true);
        getinputfilebutton.setEnabled(true);
        if (Settings.fileinputname!=null){
            input = Settings.fileinputname;
            update();
        }
        
    }//GEN-LAST:event_setfileInputButtonMouseReleased
    
    private void fourccfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fourccfieldActionPerformed
        Settings.fourcc = (String)fourccfield.getSelectedItem();
        //should do validation here.
        if (Settings.fourcc == "FLV1"){
            Settings.lavccodec = "flv";
        } else{
            Settings.lavccodec = "mpeg4";
        }
                        
        update();        
        
    }//GEN-LAST:event_fourccfieldActionPerformed
    
    private void volgainComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_volgainComboBoxActionPerformed
        Settings.volgain = (Integer)volgainComboBox.getSelectedItem();
        update();
    }//GEN-LAST:event_volgainComboBoxActionPerformed
    
    private void usevolGainCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usevolGainCheckBoxActionPerformed
        if (usevolGainCheckBox.isSelected() == true ) {
            Settings.dovolumegain = true;
        } else{
            Settings.dovolumegain = false;
        }
        update();
        
    }//GEN-LAST:event_usevolGainCheckBoxActionPerformed
    
    private void unsharpBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unsharpBoxActionPerformed
        if (unsharpBox.isSelected() == true ) {
            Settings.unsharp = true;
        } else{
            Settings.unsharp = false;
        }
        update();
    }//GEN-LAST:event_unsharpBoxActionPerformed
    
    private void previewCurrentTrackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previewCurrentTrackButtonActionPerformed
        
        extractAudio("play");
    }//GEN-LAST:event_previewCurrentTrackButtonActionPerformed
    
    /**Get the audio track to encode from the videotitle aid value using the
     *combobox list as an index to the track vector */
    private void audtracknumFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_audtracknumFieldActionPerformed
        try {
            int atrack = (Integer)audtracknumField.getSelectedItem();
            Settings.audtrack = VideoTitle.getAid(atrack-1);
            update();
        } catch(NullPointerException n) {
        }
    }//GEN-LAST:event_audtracknumFieldActionPerformed
    
    private void cropfaileddialogOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cropfaileddialogOKActionPerformed
        cropfailedDialog.setVisible(false);
    }//GEN-LAST:event_cropfaileddialogOKActionPerformed
    
    private void fittoCDButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fittoCDButtonActionPerformed
        bitratefield.setEnabled(false);
        fittoSizeComboBox.setEnabled(true);
        bitrateLabel.setEnabled(true);
        fitbitratevalueLabel.setEnabled(true);
        Settings.CalculateBitrate(VideoTitle.videolength);
        fitbitratevalueLabel.setText(Integer.toString(Settings.bitrate));
        updateQualityLabel();
        update();
        
    }//GEN-LAST:event_fittoCDButtonActionPerformed
    
    private void constantbitrateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_constantbitrateButtonActionPerformed
        bitratefield.setEnabled(true);
        fittoSizeComboBox.setEnabled(false);
        bitrateLabel.setEnabled(false);
        fitbitratevalueLabel.setEnabled(false);
        Settings.bitrate = Integer.parseInt(bitratefield.getText());
        updateQualityLabel();
        update();
    }//GEN-LAST:event_constantbitrateButtonActionPerformed
    
    private void dvdtitleFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dvdtitleFieldActionPerformed
        try {
            Settings.dvdtitle = (Integer)dvdtitleField.getSelectedItem();
            VideoTitle.resetInfo();
            Settings.titlechanged = true;
            update();
            getVideoInfo(true, true, true);
        } catch(NullPointerException n) {
        }
    }//GEN-LAST:event_dvdtitleFieldActionPerformed
    
    /** Rescale the slider range to reflect the new video length */
    private void setscalevidSlider(){
//        vidpreviewSlider = new JSlider();
        vidpreviewSlider.setMinimum(0);
        vidpreviewSlider.setMaximum(VideoTitle.videolength);
        System.out.println(vidpreviewSlider.getMaximum());
        vidpreviewSlider.repaint();
        
    }
    
    /** Reset the movie preview slider to zero */
    private void resetvidSlider(){
        vidpreviewSlider.setValue(0);
    }
    
    private void setvidSlider(int value){
        vidpreviewSlider.setValue(value);
    }
    
    private void vidpreviewSliderMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_vidpreviewSliderMouseReleased
        
        int value = vidpreviewSlider.getValue();
        System.out.println("SLIDERVAL="+value);
        Settings.prevstarttime = (float) value;
        previewVideo("input", "still");
        
    }//GEN-LAST:event_vidpreviewSliderMouseReleased
    
    private void autoScaleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoScaleButtonActionPerformed
        
        if ((String)aspectratiofield.getSelectedItem() == "auto"){
            if (VideoTitle.cropdetectable == false){
                getVideoInfo(false, true, false);    //don't try to detect crop
            } else{
                getVideoInfo(false, true, true);
            }
        }
        
    }//GEN-LAST:event_autoScaleButtonActionPerformed
    
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        confirmDialog.setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed
    
    private void confirmokButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmokButtonActionPerformed
        executor.Stop();  confirmDialog.setVisible(false);
    }//GEN-LAST:event_confirmokButtonActionPerformed
    
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed
    
    private void inputDARfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputDARfieldActionPerformed
        String item = (String)inputDARfield.getSelectedItem();
        if (item == "1:1"){
            VideoTitle.inputDAR = 1;
        } else if (item == "16:9"){
            VideoTitle.inputDAR = 1.77;    //anamorphic
        } else if (item == "auto"){
            
        }
        VideoTitle.findAspectRatio();
        Settings.scaleh = (int) Math.round(Settings.scalew / VideoTitle.targetaspectratio);
        ScaleHeightSpinner.setValue(Settings.scaleh);
        targetaspectlabel.setText(Double.toString(VideoTitle.targetaspectratio)+":1");
    }//GEN-LAST:event_inputDARfieldActionPerformed
    
    private void segmentlengthfieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_segmentlengthfieldFocusLost
        Settings.length = Integer.parseInt(segmentlengthfield.getText());
        update();
    }//GEN-LAST:event_segmentlengthfieldFocusLost
    
    private void segmentlengthfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_segmentlengthfieldActionPerformed
        Settings.length = Integer.parseInt(segmentlengthfield.getText());
        update();
        
    }//GEN-LAST:event_segmentlengthfieldActionPerformed
    
    private void updateQualityLabel(){
        if (Settings.codec == "x264"){
            if (Settings.bitrate <= 400){
                vidqualityLabel.setText("poor");
            } else if (Settings.bitrate > 400 & Settings.bitrate <= 600){
                vidqualityLabel.setText("fair");
            } else if (Settings.bitrate > 600 & Settings.bitrate <= 800){
                vidqualityLabel.setText("good");
            } else if (Settings.bitrate > 800 & Settings.bitrate <= 1000){
                vidqualityLabel.setText("v good");
            } else {
                vidqualityLabel.setText("excellent");
            }
        } else if (Settings.codec == "lavc"){
            if (Settings.bitrate <= 600){
                vidqualityLabel.setText("poor");
            } else if (Settings.bitrate > 600 & Settings.bitrate <= 800){
                vidqualityLabel.setText("fair");
            } else if (Settings.bitrate > 800 & Settings.bitrate <= 1000){
                vidqualityLabel.setText("good");
            } else if (Settings.bitrate > 1000 & Settings.bitrate <= 1400){
                vidqualityLabel.setText("v good");
            } else {
                vidqualityLabel.setText("excellent");
            }
        }
    }
    
    private void fittoSizeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fittoSizeComboBoxActionPerformed
        
        String ch = (String) fittoSizeComboBox.getSelectedItem();
        if (ch == "700MB (CD)"){
            Settings.targetsize = 700;
        } else if (ch == "650MB (CD)"){
            Settings.targetsize = 650;
        } else if (ch == "1400MB (2xCD)"){
            Settings.targetsize = 1400;
        } else if (ch == "100MB"){
            Settings.targetsize = 100;
        } else if (ch == "250MB"){
            Settings.targetsize = 250;
        }
        Settings.CalculateBitrate(VideoTitle.videolength);
        fitbitratevalueLabel.setText(Integer.toString(Settings.bitrate));
        updateQualityLabel();
        update();
        
    }//GEN-LAST:event_fittoSizeComboBoxActionPerformed
    
    private void resetsizeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetsizeButtonActionPerformed
        //VideoTitle.targetaspectratio = VideoTitle.inputw/VideoTitle.inputh;
        Settings.scalew = VideoTitle.inputw;  Settings.scaleh = VideoTitle.inputh;
        ScaleWidthSpinner.setValue(Settings.scalew);
        ScaleHeightSpinner.setValue(Settings.scaleh);
    }//GEN-LAST:event_resetsizeButtonActionPerformed
    
    private void aspectratiofieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aspectratiofieldActionPerformed
        String item = (String)aspectratiofield.getSelectedItem();
        if (item == "auto"){
            VideoTitle.findAspectRatio();
        } else if (item == "4:3"){
            VideoTitle.targetaspectratio = 1.33;
        } else if (item == "1.66"){
            VideoTitle.targetaspectratio = 1.66;
        } else if (item == "1.85:1"){
            VideoTitle.targetaspectratio = 1.85;
        } else if (item == "2.40:1"){
            VideoTitle.targetaspectratio = 2.4;
        }
        
        if (Settings.resize == true){
            Settings.scaleh = (int) Math.round(Settings.scalew / VideoTitle.targetaspectratio);
            ScaleHeightSpinner.setValue(Settings.scaleh);
        }
        targetaspectlabel.setText(Double.toString(VideoTitle.targetaspectratio)+":1");
        
    }//GEN-LAST:event_aspectratiofieldActionPerformed
    
    private void playagainButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playagainButtonActionPerformed
        String  playoutputcomm = Settings.Mplayerpath + " -slave" + sp + "prev.tst";
        executor.ExecuteM(playoutputcomm);
        
    }//GEN-LAST:event_playagainButtonActionPerformed
    
    private void noframesComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noframesComboBoxActionPerformed
        Settings.prevframes = Integer.parseInt((String)noframesComboBox.getSelectedItem());
    }//GEN-LAST:event_noframesComboBoxActionPerformed
    
    private void frameforwardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_frameforwardButtonActionPerformed
        Settings.prevstarttime += Settings.stepsize;
        previewVideo("output", "still");
    }//GEN-LAST:event_frameforwardButtonActionPerformed
    
    private void framebackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_framebackButtonActionPerformed
        
        if (Settings.prevstarttime > 0) {
            Settings.prevstarttime -= Settings.stepsize;
            previewVideo("output", "still");
        }
    }//GEN-LAST:event_framebackButtonActionPerformed
    
    private void rescaleOptionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rescaleOptionsButtonActionPerformed
        prevsetupform.setVisible(true);
        prevsetupform.pack();
        //previewVideo("input", "still");
        
    }//GEN-LAST:event_rescaleOptionsButtonActionPerformed
    
    private void cropCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cropCheckBoxActionPerformed
        setCropTrue();
        this.previewPanel.setRectfromCropSettings();
    }//GEN-LAST:event_cropCheckBoxActionPerformed
    
    private void cropySpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cropySpinnerStateChanged
        VideoTitle.cropy = (Integer)cropySpinner.getValue();
        update();
        previewPanel.setRectfromCropSettings();
    }//GEN-LAST:event_cropySpinnerStateChanged
    
    private void cropxSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cropxSpinnerStateChanged
        VideoTitle.cropx = (Integer)cropxSpinner.getValue();
        update();
        previewPanel.setRectfromCropSettings();
    }//GEN-LAST:event_cropxSpinnerStateChanged
    
    private void refreshprevButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshprevButtonActionPerformed
//        if (Settings.crop == true){
//            previewPanel.setCrop();
//        }
        refreshprevButton.setEnabled(false);
        previewVideo("output", "still");
    }//GEN-LAST:event_refreshprevButtonActionPerformed
    
    private void crophSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_crophSpinnerStateChanged
        VideoTitle.croph = (Integer)crophSpinner.getValue();
        update();
        previewPanel.setRectfromCropSettings();
    }//GEN-LAST:event_crophSpinnerStateChanged
    
    private void cropwSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cropwSpinnerStateChanged
        VideoTitle.cropw = (Integer)cropwSpinner.getValue();
        update();
        previewPanel.setRectfromCropSettings();
        
    }//GEN-LAST:event_cropwSpinnerStateChanged
    
    private void openprevwindowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openprevwindowButtonActionPerformed
        JFrame prevframe = new JFrame("Preview");
        ImgPreviewPanel prevpanel = new ImgPreviewPanel();
        //prevpanel.loadImage("00000001.png");
        prevframe.add(prevpanel);
        prevframe.setSize(prevpanel.getSize());
        prevframe.setMinimumSize(prevpanel.getSize());
        prevframe.setVisible(true);
        prevframe.pack();
        prevpanel.doImageRescale();
        
    }//GEN-LAST:event_openprevwindowButtonActionPerformed
    
    /**encode a small very sample either resized and cropped  */
    private String makePreviewClip(String option){
        String makepreviewcomm;
        
        String previewargs = "-nosound -ss" + sp + Settings.prevstarttime + " -frames"
                + sp + Settings.prevframes + " -of rawvideo";
        String cropresizeargs =  " -sws" + sp + Settings.sws + sp
                + "-ovc" + sp
                + (option == "input" ? "copy" : ((Settings.codec == "lavc" ? "lavc -lavcopts vcodec=mpeg4:vbitrate="
                + Settings.bitrate + ":vhq -ffourcc XVID:vpass=1" :
                    "x264 -x264encopts bitrate=" + Settings.bitrate +
                ":subq=4:i4x4" + ":pass=1") + sp
                + (Settings.crop || Settings.resize ? "-vf " : "")
                + (Settings.crop ? "crop="
                + VideoTitle.cropw+":"+VideoTitle.croph+":"+VideoTitle.cropx+":"+VideoTitle.cropy+"," : "")
                + (Settings.resize ? "scale="+Settings.scalew+":"+Settings.scaleh : ""))) + sp;
        
        makepreviewcomm = path + sp + previewargs + cropresizeargs + sp + input + sp + "-o" + sp + "prev.tst";
        
        return makepreviewcomm;
    }
    
    
    /**creates the commands required to play current preview current input
     * settings*/
    public void previewVideo(final String choice, final String type ){
        
        final String makeinputpreviewcomm =  makePreviewClip("input");
        final String makeoutputpreviewcomm =  makePreviewClip("output");
        final String playinputcomm = Settings.Mplayerpath + " -slave" + sp + input;
        final String playoutputcomm = Settings.Mplayerpath + " -slave" + sp + "prev.tst";
        
        //create picture preview of original
//        final String makeinputpreviewcomm = Settings.Mplayerpath
//                + " -ss " + sp + Settings.prevstarttime + " -frames 2 -nosound" + sp
//                + " -vo png" + sp + input;
        
        
        //create picture preview of output preview clip
        final String previewoutputcomm = Settings.Mplayerpath
                + " -frames 1 -nosound" + sp
                + " -vo png" + sp + "prev.tst";
        
        worker = new SwingWorker() {
            public Object construct() {
                try{
                    String comm = "";
                    executor = new ExecProgram();
                    if (choice == "input"){
                        System.out.println(makeinputpreviewcomm);
                        progresslabel.setText("Creating short preview...");
                        String[] comms = new String[2];
                        if (type == "still") {
                            comms[0] = makeinputpreviewcomm;
                            comms[1] = previewoutputcomm;
                            executor.ExecuteM(comms);
                        }
                        
//                        comm = makeinputpreviewcomm;
//                        executor.ExecuteM(comm);
                        
                        
                    } else if (choice == "output"){
                        System.out.println(makeoutputpreviewcomm);
                        progresslabel.setText("Creating short preview...");
                        String[] comms = new String[2];
                        if (type == "still") {
                            comms[0] = makeoutputpreviewcomm;
                            comms[1] = previewoutputcomm;
                            executor.ExecuteM(comms);
                        }
                        if (type == "clip") {
                            comms[0] = makeoutputpreviewcomm;
                            comms[1] = playoutputcomm;
                            executor.ExecuteM(comms);
                        }
                    }
//                    if (choice == "clip") executor.showGUI();
                    
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                return  progresslabel;
            }
            public void finished() {
                if (executor.getexitValue() == 0){
                    progresslabel.setText("Preview done");
                    if (type == "still"){
                        if (choice == "input"){
                            previewPanel.loadImage( "00000001.png");
                        } else if  (choice == "output"){
                            previewPanel1.loadImage("00000001.png");
                            refreshprevButton.setEnabled(true);
                        }
                    } else if (type == "clip"){
                        
                    }
                } else{
                    progresslabel.setText("Stopped");
                }
            }
        }; worker.start();
    }
    
    
    private void previewVideoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previewVideoButtonActionPerformed
        previewVideo("output", "clip");
    }//GEN-LAST:event_previewVideoButtonActionPerformed
    
    private void bitratefieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_bitratefieldFocusLost
        Settings.bitrate = Integer.parseInt(bitratefield.getText());
        update();
    }//GEN-LAST:event_bitratefieldFocusLost
    
    private void audQualityFieldStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_audQualityFieldStateChanged
        Settings.audquality = (Integer)audQualityField.getValue();
        update();
        
    }//GEN-LAST:event_audQualityFieldStateChanged
    
    private void bitratecalcMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bitratecalcMenuItemActionPerformed
        bcalcform.setVisible(true);
    }//GEN-LAST:event_bitratecalcMenuItemActionPerformed
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        bcalcform.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed
    
    private void extractAudio(final String option){
        if (option == "copy"){
            String format = VideoTitle.getaudFormat(Settings.audtrack); //get format
            System.out.println(format);
            extractcomm = Settings.Mplayerpath + sp +
                    "-dumpaudio" + sp + input + sp + "-dumpfile" + sp + "audtrack_" + Settings.audtrack
                    + "." + format;
        } else if (option == "wav"){
            extractcomm = Settings.Mplayerpath + sp +
                    input  + sp + "-ao pcm:file=" + "audtrack_" + Settings.audtrack + ".wav" + sp + "-vo null";
        } else if (option == "play"){
            extractcomm = Settings.Mplayerpath + sp + "-vo null" + sp + input;
//             mplayercontrol.playMedia(input, " -aid" + sp + Settings.audtrack + sp + "-vo null");
        }
        
        if (VideoTitle.numaud > 1 ) extractcomm += " -aid"+ sp + Settings.audtrack + sp;
        worker = new SwingWorker() {
            public Object construct() {
                try{
                    if (option == "copy"){
                        progresslabel.setText("extracting audio as is...");
                    } else if (option == "wav"){
                        progresslabel.setText("extracting audio as wav...");
                    } else if (option == "play"){
                        progresslabel.setText("previewing audio...");
                    }
                    
                    executor = new ExecProgram();
                    System.out.println(extractcomm);
                    executor.ExecuteM(extractcomm);
                    
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
                log.load("log.txt");
            }
        }; worker.start();
    }
    
    
    private void extractAudioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_extractAudioButtonActionPerformed
        extractAudio("copy");
    }//GEN-LAST:event_extractAudioButtonActionPerformed
    
    private void extractasWAVButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_extractasWAVButtonActionPerformed
        extractAudio("wav");
    }//GEN-LAST:event_extractasWAVButtonActionPerformed
    
    
    private void containertypeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_containertypeComboBoxActionPerformed
        Settings.container = (String)containertypeComboBox.getSelectedItem();
        
        if (Settings.container.matches("avi") || Settings.container.matches("flv") ){
            Settings.domuxing = false;
            codecfield.setSelectedItem("lavc");
            if (Settings.container.matches("flv")){
                fourccfield.setSelectedItem("FLV1");
            }
            else if (Settings.container.matches("avi")){
                fourccfield.setSelectedItem("auto");
            }
        } else {
            Settings.domuxing = true;
        }
        updateCommand();
        updateComponents();
    }//GEN-LAST:event_containertypeComboBoxActionPerformed
    
    private void scalequalityfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scalequalityfieldActionPerformed
        if (scalequalityfield.getSelectedItem() == "fast bilinear"){
            Settings.sws = 0;
        } else if (scalequalityfield.getSelectedItem() == "bilinear"){
            Settings.sws = 1;
        } else if(scalequalityfield.getSelectedItem() == "bicubic"){
            Settings.sws = 2;
        } else if(scalequalityfield.getSelectedItem() == "nearest"){
            Settings.sws = 4;
        } else if(scalequalityfield.getSelectedItem() == "gauss"){
            Settings.sws = 7;
        } else if(scalequalityfield.getSelectedItem() == "lanczos"){
            Settings.sws = 9;
        }
        
        update();
    }//GEN-LAST:event_scalequalityfieldActionPerformed
    
    private void getoutputfilebuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getoutputfilebuttonActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Select Output file");
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            Settings.fileoutputname = chooser.getSelectedFile().getPath();
            System.out.println("Settings.fileoutputname"+Settings.fileoutputname);            
            update();
            System.out.println("Settings.fileoutputname"+Settings.fileoutputname);
        } else {
            System.out.println("No Selection ");
        }                

        
    }//GEN-LAST:event_getoutputfilebuttonActionPerformed
    
    private void settingsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsMenuItemActionPerformed
        
//        SettingsForm setupform = new SettingsForm();
        setupform.setVisible(true);
    }//GEN-LAST:event_settingsMenuItemActionPerformed
    
    private void MuxerMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MuxerMenuItemActionPerformed
        
        JFrame frame = new JFrame();
        muxpanel.enableMuxButtons();
        frame.add(muxpanel);
        frame.pack();
        frame.setVisible(true);
        
    }//GEN-LAST:event_MuxerMenuItemActionPerformed
    
    private void audbitrateComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_audbitrateComboBoxActionPerformed
        Settings.audbitrate = Integer.parseInt((String)audbitrateComboBox.getSelectedItem());
        Settings.CalculateBitrate(VideoTitle.videolength);
        fitbitratevalueLabel.setText(Integer.toString(Settings.bitrate));
        update();
    }//GEN-LAST:event_audbitrateComboBoxActionPerformed
    
    private void setLNFMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setLNFMenuItemActionPerformed
        
//        frame = LNFSetter.createLNFSwitcher(this);
        lnfframe.pack();
        lnfframe.setVisible(true);
        
    }//GEN-LAST:event_setLNFMenuItemActionPerformed
    
    private void starttimeSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_starttimeSpinnerStateChanged
        Object value = (Date)starttimeSpinner.getValue();
        SimpleDateFormat formatter; Locale currentLocale = new Locale("en","US");
        formatter = new SimpleDateFormat("HH:mm:ss", currentLocale);
        Settings.starttime = formatter.format(value);
        System.out.println(Settings.starttime);
        update();
    }//GEN-LAST:event_starttimeSpinnerStateChanged
    
    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        
        Frame splashFrame = null;
        java.net.URL imageURL = MainGUI.class.getResource("images/"+"splash.png");
        if (imageURL != null) {
            splashFrame = SplashWindow.splash(
                    Toolkit.getDefaultToolkit().createImage(imageURL)
                    );
        } else {
            
            System.err.println("Splash image not found");
        }
        
    }//GEN-LAST:event_aboutMenuItemActionPerformed
    
    private void quitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitMenuItemActionPerformed
        quit();
    }//GEN-LAST:event_quitMenuItemActionPerformed
    
    private void selecttimeCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selecttimeCheckBoxActionPerformed
        if (selecttimeCheckBox.isSelected() == true ) {
            Settings.selecttime = true;
            starttimeSpinner.setEnabled(true);
            //stoptimeSpinner.setEnabled(true);
            segmentlengthfield.setEnabled(true);
            seglengthLabel.setEnabled(true);
            startLabel.setEnabled(true);
        } else{
            Settings.selecttime = false;
            starttimeSpinner.setEnabled(false);
            //stoptimeSpinner.setEnabled(false);
            segmentlengthfield.setEnabled(false);
            seglengthLabel.setEnabled(false);
            startLabel.setEnabled(false);
        }
        update();
        
    }//GEN-LAST:event_selecttimeCheckBoxActionPerformed
    
    private void playmediaMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playmediaMenuItemActionPerformed
        
        String media = "";
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Select Directory");
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            media = chooser.getSelectedFile().getPath();
        } else {
            System.out.println("No Selection ");
        }
        
        mplayercontrol.playMedia(media, "");
        
    }//GEN-LAST:event_playmediaMenuItemActionPerformed
    
    private void viewlogmenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewlogmenuItemActionPerformed
        
        log.setVisible(true);
    }//GEN-LAST:event_viewlogmenuItemActionPerformed
    
    public static void resetCrop(){
        cropwSpinner.setValue(0);
        crophSpinner.setValue(0);
        cropxSpinner.setValue(0);
        cropySpinner.setValue(0);
    }
    
    public static void updateCrop(){
        cropwSpinner.setValue(VideoTitle.cropw);
        crophSpinner.setValue(VideoTitle.croph);
        cropxSpinner.setValue(VideoTitle.cropx);
        cropySpinner.setValue(VideoTitle.cropy);
    }
    
    private void getVideoInfo(final boolean identify, final boolean getaspect,
            final boolean cropdetect){
        
        worker = new SwingWorker() {
            public Object construct() {
                String comm = Settings.Mplayerpath + sp
                        +  (identify == true ? "-identify  " : sp)
                        + "-vo null -nosound -ss" + sp + Settings.prevstarttime
                        + " -frames 20" + sp
                        + (cropdetect == true ? "-vf cropdetect " : sp)
                        + input;
                try{
                    if (identify == true){
                        progresslabel.setText("getting info...");
                    } else if (cropdetect == true){
                        progresslabel.setText("detecting crop data...");
                    }
                    executor = new ExecProgram();
                    System.out.println(comm);
                    executor.ExecuteM(comm);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                return  progresslabel;
            }
            public void finished() {
                if (executor.getexitValue() == 0){
                    if (identify == true){  //done on first open of file
                        previewVideo("input", "still");
                        setvidSlider((int)Settings.prevstarttime);
                        Settings.scalew = VideoTitle.inputw;
                        Settings.scaleh = VideoTitle.inputh;
                        ScaleWidthSpinner.setValue(Settings.scalew);
                        
                        //recreate titles combobox to reflect number of titles
                        //bit tricky! Have to avoid forcing an actionperformed event..
                        //do not remove all items at once, do it one by one when needed
                        
                        //if no. items in list is smaller than titles, add items
                        int numitems = dvdtitleField.getItemCount();
                        if (VideoTitle.notitles > numitems){
                            dvdtitleField.setEnabled(true);
                            for (int i = numitems; i<VideoTitle.notitles; i++){
                                dvdtitleField.addItem(i+1);
                            }
                        } //otherwise remove extra items in list
                        else if (VideoTitle.notitles < numitems) {
                            dvdtitleField.setEnabled(true);
                            for (int i = numitems; i>VideoTitle.notitles; i--){
                                dvdtitleField.removeItemAt(i-1);
                            }
                        }
                        //only do audio list if new title
                        if (Settings.titlechanged == true){
                            //do the same for the audio tracks combobox
                            numitems = audtracknumField.getItemCount();
                            if (VideoTitle.numaud > numitems){
                                audtracknumField.setEnabled(true);
                                for (int i = numitems; i<VideoTitle.numaud; i++){
                                    audtracknumField.addItem(i+1);
                                }
                            } //otherwise remove extra items in list
                            else if (VideoTitle.numaud < numitems) {
                                audtracknumField.setEnabled(true);
                                for (int i = numitems; i>VideoTitle.numaud; i--){
                                    audtracknumField.removeItemAt(i-1);
                                }
                            }
                        }
                        
                    }
                    if (getaspect == true){
                        if (Settings.scalew == 0) Settings.scalew = VideoTitle.inputw;
                        //Try to estimate the aspect ratio of the source from the crop values
                        VideoTitle.findAspectRatio();
                        if (Settings.scalew > 720) Settings.scalew = 720; //prevent high values
                        //Settings.scaleh = (int) Math.round(Settings.scalew / VideoTitle.targetaspectratio);
                        int oldval = Settings.scalew ;
                        Settings.setScaleValues(oldval);    //will set values according to mod 16 if set true
                        ScaleWidthSpinner.setValue(Settings.scalew);
                        ScaleHeightSpinner.setValue(Settings.scaleh);
                        
                    }
                    if (cropdetect == true | VideoTitle.cropdetectable == true) {
                        updateCrop();
                        
                        if (VideoTitle.cropdetectable == false){
                            cropfailedDialog.pack();
                            cropfailedDialog.setVisible(true);
                        } else {
                            previewPanel.setRectfromCropSettings();
                        }
                    }
                    setscalevidSlider();   //resets the preview slider to the right scale
                    videoInfoPanel.displayInfo();
                    update();
                    Settings.titlechanged = false;
                    progresslabel.setText("Finished");
                } else{
                    progresslabel.setText("Stopped");
                }
            }
        }; worker.start();
    }
    
    //captures 20 frames of the movie to get crop
    private void cropdetectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cropdetectButtonActionPerformed
        
//        cropdetect(false);
        getVideoInfo(false, false, true);
        
    }//GEN-LAST:event_cropdetectButtonActionPerformed
    
    private void audcodecfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_audcodecfieldActionPerformed
        Settings.audcodec = (String)audcodecfield.getSelectedItem();
        if (Settings.audcodec == "copy" || Settings.audcodec == "wav"){
            audbitrateComboBox.setEnabled(false);
            audQualityField.setEnabled(false);
            BitrateLabel.setEnabled(false);
            QualityLabel.setEnabled(false);
        } else if (Settings.audcodec == "mp3"){
            audbitrateComboBox.setEnabled(true);
            audQualityField.setEnabled(false);
            BitrateLabel.setEnabled(true);
        } else if(Settings.audcodec == "aac"){
            audbitrateComboBox.setEnabled(false);
            audQualityField.setEnabled(true);
            QualityLabel.setEnabled(true);
        }
        update();
    }//GEN-LAST:event_audcodecfieldActionPerformed
    
    private void setfileInputButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setfileInputButtonActionPerformed
        Settings.usediscinput = false;
        System.out.println("Using file input");
        discinputField.setEnabled(false);
        dvdtitleField.setEnabled(false);
        getdiscpathbutton.setEnabled(false);
        getdiscpathField.setEnabled(false);
        fileinputfield.setEnabled(true);
        getinputfilebutton.setEnabled(true);
        input = Settings.fileinputname;
        update();
        
    }//GEN-LAST:event_setfileInputButtonActionPerformed
    
    private void setDiscInputButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setDiscInputButtonActionPerformed
        Settings.usediscinput = true;
        VideoTitle.type = "dvd";
        discinputField.setEnabled(true);
        dvdtitleField.setEnabled(true);
        getdiscpathbutton.setEnabled(true);
        getdiscpathField.setEnabled(true);
        fileinputfield.setEnabled(false);
        getinputfilebutton.setEnabled(false);
        update();
        
    }//GEN-LAST:event_setDiscInputButtonActionPerformed
    
    private void getdiscpathbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getdiscpathbuttonActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select DVD Directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            System.out.println("CurrentDirectory(): " +  chooser.getCurrentDirectory());
            Settings.dvddev = chooser.getSelectedFile().getPath();
            VideoTitle.resetInfo();
            Settings.titlechanged = true;
            update();
            getVideoInfo(true, true, true);
        } else {
            System.out.println("No Selection ");
        }
        
        
    }//GEN-LAST:event_getdiscpathbuttonActionPerformed
    
    private void getdiscpathFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getdiscpathFieldActionPerformed
        Settings.dvddev = (String)getdiscpathField.getText();
        VideoTitle.resetInfo();
        Settings.titlechanged = true;
        update();
        getVideoInfo(true, true, true);
        
    }//GEN-LAST:event_getdiscpathFieldActionPerformed
    
    
    private void resizeCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resizeCheckBoxActionPerformed
        if (resizeCheckBox.isSelected() == false ) {
            Settings.resize = false;
            ScaleWidthSpinner.setEnabled(false);
            ScaleHeightSpinner.setEnabled(false);
            autoScaleButton.setEnabled(false);
        } else {
            Settings.resize = true;
            ScaleWidthSpinner.setEnabled(true);
            autoScaleButton.setEnabled(true);
        }
        update();
    }//GEN-LAST:event_resizeCheckBoxActionPerformed
    
    private void setCropTrue(){
        if (cropCheckBox.isSelected() == true) {
            Settings.crop = true;
            cropwSpinner.setEnabled(true);
            crophSpinner.setEnabled(true);
            cropxSpinner.setEnabled(true);
            cropySpinner.setEnabled(true);
            cropdetectButton.setEnabled(true);
            
        }  if (cropCheckBox.isSelected() == false) {
            Settings.crop = false;
            cropwSpinner.setEnabled(false);
            crophSpinner.setEnabled(false);
            cropxSpinner.setEnabled(false);
            cropySpinner.setEnabled(false);
            cropdetectButton.setEnabled(false);
        }
        update();
    }
    
    /**only do this if mod 16 is false */
    private void ScaleHeightSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ScaleHeightSpinnerStateChanged
        
        if (Settings.usemod16 == false){
            Settings.scaleh = (Integer)ScaleHeightSpinner.getValue();
            if (VideoTitle.targetaspectratio != 0){
                Settings.scalew = (int) Math.round(Settings.scaleh * VideoTitle.targetaspectratio );
            }
            ScaleWidthSpinner.setValue(Settings.scalew);
        }
        
        update();
    }//GEN-LAST:event_ScaleHeightSpinnerStateChanged
    
    private void ScaleWidthSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ScaleWidthSpinnerStateChanged
        int oldval = Settings.scalew ;
        Settings.scalew = (Integer)ScaleWidthSpinner.getValue();
        Settings.setScaleValues(oldval);
        ScaleWidthSpinner.setValue(Settings.scalew);
        ScaleHeightSpinner.setValue(Settings.scaleh);
//        actualaspectlabel.setText(Double.toString(VideoTitle.actualaspectratio)+":1");
//        if (VideoTitle.error > 0.001){
//            aspecterrorLabel.setText(Double.toString(VideoTitle.error)+"%");
//        } else {
//            aspecterrorLabel.setText("N/A");
//        }
        update();
    }//GEN-LAST:event_ScaleWidthSpinnerStateChanged
    
    private void twoPassButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_twoPassButtonActionPerformed
        Settings.passes = 2;
        update();
    }//GEN-LAST:event_twoPassButtonActionPerformed
    
    private void onePassButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onePassButtonActionPerformed
        Settings.passes = 1;
        update();
    }//GEN-LAST:event_onePassButtonActionPerformed
    
    private void bitratefieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bitratefieldActionPerformed
        Settings.bitrate = Integer.parseInt(bitratefield.getText());
        updateQualityLabel();
        update();
    }//GEN-LAST:event_bitratefieldActionPerformed
    
    private void codecfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_codecfieldActionPerformed
        Settings.codec = (String)codecfield.getSelectedItem();
        if (Settings.codec == "x264"){
            fourccfield.setSelectedItem("auto");
            fourccfield.setEnabled(false);
            extension = ".264";
            useBframesCheckBox.setEnabled(true);
            useBframesSpinner.setEnabled(true);
        } else{
           fourccfield.setEnabled(true);
            extension = ".m4v";
            useBframesCheckBox.setEnabled(false);
            useBframesSpinner.setEnabled(false);
        }
        updateQualityLabel();
        update();
    }//GEN-LAST:event_codecfieldActionPerformed
    
    /**Executes encoding commands in a Swingworker object */
    public void doEncode(){
        
        worker = new SwingWorker() {
            public Object construct() {
                try{
                    progresslabel.setText("running...");
                    executor = new ExecProgram();
                    
                    //execute the series of commands by passing them as a string array
                    String[] comms;
                    String[] info; //corresponding info for each step, for progress display
                    if ( Settings.domuxing == true){
                        if (Settings.container == "mp4"){
                            comms = new String[3];
                            comms[0] = command1;
                            comms[1] = command2;
                            comms[2] = muxerpanel.muxCommand1;
                            System.out.println(comms[0]);
                            System.out.println(comms[1]);
                            System.out.println(comms[2]);
                            info = new String[3];
                            info[0] = "Doing first pass..";
                            info[1] = "Doing second pass..";
                            info[2] = "Muxing to mp4..";
                            
                        } else {    //if (Settings.container == "mkv"){
                            comms = new String[4];
                            comms[0] = command1;
                            comms[1] = command2;
                            comms[2] = muxerpanel.muxCommand1;
                            comms[3] = muxerpanel.muxCommand2;
                            System.out.println(comms[0]);
                            System.out.println(comms[1]);
                            System.out.println(comms[2]);
                            System.out.println(comms[3]);
                            info = new String[4];
                            info[0] = "Doing first pass..";
                            info[1] = "Doing second pass..";
                            info[2] = "Muxing to mp4..";
                            info[3] = "Muxing to mkv..";
                        }
                        
                    } else if (Settings.passes == 1 & Settings.domuxing == false){
                        comms = new String[1];
                        comms[0] = command1;
                        System.out.println(comms[0]);
                        info = new String[1];
                        info[0] = "Single Pass. Doing audio and video..";
                    } else {
                        comms = new String[2];
                        comms[0] = command1;
                        comms[1] = command2;
                        System.out.println(comms[0]);
                        System.out.println(comms[1]);
                        info = new String[2];
                        info[0] = "Doing first pass..";
                        info[1] = "Doing second pass..";
                    }
                    executor.ExecuteM(comms, info);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                return progresslabel;
            }
            public void finished() {
                if (executor.getexitValue() == 0){
                    progresslabel.setText("Finished");
                } else{
                    progresslabel.setText("Stopped");
                } GoButton.setEnabled(true);
                log.load("log.txt");
            }
        }; worker.start();
        
        
    }
    
    private void GoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GoButtonActionPerformed
        doEncode();
        GoButton.setEnabled(false);
    }//GEN-LAST:event_GoButtonActionPerformed
    
    private void CancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelButtonActionPerformed
        confirmDialog.pack();
        confirmDialog.setVisible(true);
        
    }//GEN-LAST:event_CancelButtonActionPerformed
    
    private void getinputfilebuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getinputfilebuttonActionPerformed
        Settings.usediscinput=false;
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Select Input File");
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            Settings.fileinputname = chooser.getSelectedFile().getPath();
            VideoTitle.resetInfo();
            Settings.titlechanged = true;
            update();
            getVideoInfo(true, true, true);
        } else {
            System.out.println("No Selection ");
        }
//        update();
        
        
    }//GEN-LAST:event_getinputfilebuttonActionPerformed
    
    private void argsfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_argsfieldActionPerformed
        args = argsfield.getText();
        updatefromArgsField();
        updateComponents();
    }//GEN-LAST:event_argsfieldActionPerformed
    
    private void fileinputfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileinputfieldActionPerformed
        input = fileinputfield.getText();
        VideoTitle.resetInfo();
        Settings.titlechanged = true;
        update();
        getVideoInfo(true, true, true);
    }//GEN-LAST:event_fileinputfieldActionPerformed
    
    private void outputfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outputfieldActionPerformed
        output = outputfield.getText();
        update();

    }//GEN-LAST:event_outputfieldActionPerformed
    
    //Quit the application.
    protected void quit() {
        System.exit(0);
    }
    
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
    
    /**
     * @param args the path line arguments
     */
    public static void main(String args[]) {
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            //UIManager.setLookAndFeel(laf);
        } catch (Exception e) { }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainGUI().setVisible(true);
            }
        });
        setDefaultLookAndFeelDecorated(true);
        //if (splashFrame != null) splashFrame.dispose();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel BitrateLabel;
    private javax.swing.JButton CancelButton;
    private javax.swing.JButton GoButton;
    private javax.swing.JMenuItem MuxerMenuItem;
    private javax.swing.JLabel QualityLabel;
    private javax.swing.JSpinner ScaleHeightSpinner;
    private javax.swing.JSpinner ScaleWidthSpinner;
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JLabel actualaspectlabel;
    private javax.swing.JTextField argsfield;
    private javax.swing.JLabel aspecterrorLabel;
    private javax.swing.JComboBox aspectratiofield;
    private javax.swing.JSpinner audQualityField;
    private javax.swing.JComboBox audbitrateComboBox;
    private javax.swing.JComboBox audcodecfield;
    private javax.swing.JComboBox audtracknumField;
    private javax.swing.JButton autoScaleButton;
    private javax.swing.JLabel bitrateLabel;
    private javax.swing.JMenuItem bitratecalcMenuItem;
    private javax.swing.JTextField bitratefield;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.JButton cancelButton;
    private javax.swing.JComboBox codecfield;
    private javax.swing.JTextArea commanddisplayarea;
    private javax.swing.JDialog confirmDialog;
    private javax.swing.JButton confirmokButton;
    private javax.swing.JRadioButton constantbitrateButton;
    private javax.swing.JComboBox containertypeComboBox;
    private static javax.swing.JCheckBox cropCheckBox;
    private javax.swing.JButton cropdetectButton;
    private javax.swing.JDialog cropfailedDialog;
    private javax.swing.JButton cropfaileddialogOK;
    private static javax.swing.JSpinner crophSpinner;
    private static javax.swing.JSpinner cropwSpinner;
    private static javax.swing.JSpinner cropxSpinner;
    private static javax.swing.JSpinner cropySpinner;
    private javax.swing.JTextField discinputField;
    private javax.swing.JComboBox dvdtitleField;
    private javax.swing.JButton extractAudioButton;
    private javax.swing.JButton extractasWAVButton;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JTextField fileinputfield;
    private javax.swing.JLabel fitbitratevalueLabel;
    private javax.swing.JRadioButton fittoCDButton;
    private javax.swing.JComboBox fittoSizeComboBox;
    private javax.swing.JComboBox fourccfield;
    private javax.swing.JButton framebackButton;
    private javax.swing.JButton frameforwardButton;
    private javax.swing.JTextField getdiscpathField;
    private javax.swing.JButton getdiscpathbutton;
    private javax.swing.JButton getinputfilebutton;
    private javax.swing.JButton getoutputfilebutton;
    private javax.swing.JComboBox inputDARfield;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private jmencode.MuxerForm muxerpanel;
    private javax.swing.JComboBox noframesComboBox;
    private javax.swing.JRadioButton onePassButton;
    private javax.swing.JButton openprevwindowButton;
    private javax.swing.JTextField outputfield;
    private javax.swing.JButton playagainButton;
    private javax.swing.JMenuItem playmediaMenuItem;
    private javax.swing.JButton previewCurrentTrackButton;
    private jmencode.ImgPreviewPanel previewPanel;
    private jmencode.ImgPreviewPanel previewPanel1;
    private javax.swing.JButton previewVideoButton;
    public static javax.swing.JProgressBar procProgressBar;
    public static javax.swing.JLabel progresslabel;
    private javax.swing.JMenuItem quitMenuItem;
    private javax.swing.JButton refreshprevButton;
    private javax.swing.JButton rescaleOptionsButton;
    private javax.swing.JButton resetsizeButton;
    private javax.swing.JCheckBox resizeCheckBox;
    private javax.swing.JComboBox scalequalityfield;
    private javax.swing.JLabel seglengthLabel;
    private javax.swing.JTextField segmentlengthfield;
    private javax.swing.JCheckBox selecttimeCheckBox;
    private javax.swing.JRadioButton setDiscInputButton;
    private javax.swing.JMenuItem setLNFMenuItem;
    private javax.swing.JRadioButton setfileInputButton;
    private javax.swing.JMenuItem settingsMenuItem;
    private javax.swing.JLabel startLabel;
    private javax.swing.JSpinner starttimeSpinner;
    private javax.swing.JLabel targetaspectlabel;
    private javax.swing.JMenu toolsMenu;
    private javax.swing.JRadioButton twoPassButton;
    private javax.swing.JCheckBox unsharpBox;
    private javax.swing.JCheckBox useBframesCheckBox;
    private javax.swing.JSpinner useBframesSpinner;
    private javax.swing.JCheckBox usevolGainCheckBox;
    private jmencode.VideoInfoForm videoInfoPanel;
    private javax.swing.JSlider vidpreviewSlider;
    private javax.swing.JLabel vidqualityLabel;
    private javax.swing.JMenuItem viewlogmenuItem;
    private javax.swing.JComboBox volgainComboBox;
    // End of variables declaration//GEN-END:variables
    
}
