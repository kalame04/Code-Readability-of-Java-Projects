/*
 * Created on 02 December 2006, 22:37
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
import java.io.File;
import javax.swing.*;
import javax.swing.event.*;

public class SettingsForm extends javax.swing.JFrame {
    
    /** Creates new form SettingsForm */
    public SettingsForm() {
        initComponents();
    }
    
    private void updateComponents(){
        mplayerpathField.setText(Settings.Mplayerpath);
        mencoderpathField.setText(Settings.Mencoderpath);
        mkvmergepathField.setText(Settings.Mkvmergepath);        
        mp4boxpathField.setText(Settings.MP4Boxpath);
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        mplayerpathField = new javax.swing.JTextField();
        mencoderpathField = new javax.swing.JTextField();
        mkvmergepathField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        mp4boxpathField = new javax.swing.JTextField();
        getmplayerpathButton = new javax.swing.JButton();
        getmencoderpathButton = new javax.swing.JButton();
        getmkvmergepathButton = new javax.swing.JButton();
        getmp4boxpathButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        savesettingsasButton = new javax.swing.JButton();
        savesettingsButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        jLabel2.setText("Mplayer:");

        jLabel3.setText("Mencoder:");

        jLabel4.setText("Mkvmerge:");

        mplayerpathField.setText(Settings.Mplayerpath);

        mencoderpathField.setText(Settings.Mencoderpath);

        mkvmergepathField.setText(Settings.Mkvmergepath);

        jLabel5.setText("MP4box:");

        mp4boxpathField.setText(Settings.MP4Boxpath);

        getmplayerpathButton.setText("find");
        getmplayerpathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getmplayerpathButtonActionPerformed(evt);
            }
        });

        getmencoderpathButton.setText("find");
        getmencoderpathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getmencoderpathButtonActionPerformed(evt);
            }
        });

        getmkvmergepathButton.setText("find");
        getmkvmergepathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getmkvmergepathButtonActionPerformed(evt);
            }
        });

        getmp4boxpathButton.setText("find");
        getmp4boxpathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getmp4boxpathButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel2)
                    .add(jLabel3)
                    .add(jLabel4)
                    .add(jLabel5))
                .add(22, 22, 22)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(mp4boxpathField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                    .add(mkvmergepathField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                    .add(mencoderpathField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                    .add(mplayerpathField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(getmp4boxpathButton, 0, 0, Short.MAX_VALUE)
                    .add(getmkvmergepathButton, 0, 0, Short.MAX_VALUE)
                    .add(getmencoderpathButton, 0, 0, Short.MAX_VALUE)
                    .add(getmplayerpathButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, Short.MAX_VALUE))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(41, 41, 41)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(mplayerpathField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(getmplayerpathButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(mencoderpathField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(getmencoderpathButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(mkvmergepathField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(getmkvmergepathButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(mp4boxpathField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(getmp4boxpathButton))
                .addContainerGap(122, Short.MAX_VALUE))
        );
        jTabbedPane1.addTab("Paths", jPanel1);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 442, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 273, Short.MAX_VALUE)
        );
        jTabbedPane1.addTab("lavc", jPanel2);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 442, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 273, Short.MAX_VALUE)
        );
        jTabbedPane1.addTab("x264", jPanel3);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
                .addContainerGap())
        );
        jTabbedPane1.addTab("view file", jPanel4);

        jLabel1.setText("Set default parameters here.");

        jLabel6.setText("Only paths are currently saved to config file.");

        savesettingsasButton.setText("Save Settings As..");

        savesettingsButton.setText("Save Current as Default");
        savesettingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                savesettingsButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 164, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 46, Short.MAX_VALUE)
                        .add(jLabel6)
                        .add(32, 32, 32))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(savesettingsButton)
                        .add(34, 34, 34)
                        .add(savesettingsasButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 158, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(48, 48, 48))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jLabel6))
                .add(18, 18, 18)
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(savesettingsButton)
                    .add(savesettingsasButton))
                .add(18, 18, 18))
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void getmplayerpathButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getmplayerpathButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        //chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Select MPlayer Path");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            System.out.println("CurrentDirectory(): " +  chooser.getCurrentDirectory());
            Settings.Mplayerpath =  chooser.getSelectedFile().getPath();
        } else {
            System.out.println("No Selection ");
        }
        updateComponents();
    }//GEN-LAST:event_getmplayerpathButtonActionPerformed

    private void getmencoderpathButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getmencoderpathButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Mencoder Path");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            System.out.println("CurrentDirectory(): " +  chooser.getCurrentDirectory());
            Settings.Mencoderpath =  chooser.getSelectedFile().getPath();
        } else {
            System.out.println("No Selection ");
        }
        updateComponents();
    }//GEN-LAST:event_getmencoderpathButtonActionPerformed

    private void getmkvmergepathButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getmkvmergepathButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Mkvmerge Path");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            System.out.println("CurrentDirectory(): " +  chooser.getCurrentDirectory());
            Settings.Mkvmergepath =  chooser.getSelectedFile().getPath();
        } else {
            System.out.println("No Selection ");
        }
        updateComponents();
    }//GEN-LAST:event_getmkvmergepathButtonActionPerformed

    private void getmp4boxpathButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getmp4boxpathButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select MP4Box Path");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            System.out.println("CurrentDirectory(): " +  chooser.getCurrentDirectory());
            Settings.MP4Boxpath =  chooser.getSelectedFile().getPath();
        } else {
            System.out.println("No Selection ");
        }
        updateComponents();
    }//GEN-LAST:event_getmp4boxpathButtonActionPerformed
    
    private void savesettingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savesettingsButtonActionPerformed
        Settings.saveSettingstoFile(null);
    }//GEN-LAST:event_savesettingsButtonActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SettingsForm().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton getmencoderpathButton;
    private javax.swing.JButton getmkvmergepathButton;
    private javax.swing.JButton getmp4boxpathButton;
    private javax.swing.JButton getmplayerpathButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField mencoderpathField;
    private javax.swing.JTextField mkvmergepathField;
    private javax.swing.JTextField mp4boxpathField;
    private javax.swing.JTextField mplayerpathField;
    private javax.swing.JButton savesettingsButton;
    private javax.swing.JButton savesettingsasButton;
    // End of variables declaration//GEN-END:variables
    
}
