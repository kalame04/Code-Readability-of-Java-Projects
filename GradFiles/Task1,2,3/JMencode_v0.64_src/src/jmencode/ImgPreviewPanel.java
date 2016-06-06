/*
 * ImgPreviewPanel.java
 *
 * Created on 06 December 2006, 14:03
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

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.imageio.stream.*;

public class ImgPreviewPanel extends JPanel {
    public BufferedImage preview;
    private int x, y, x1, y1, x2, y2;
    private Point p1, p2;   //to store actual pixel co-ords of current image in panel
    private double imageratio, panelratio;
    public static int cropw, croph, cropx, cropy;
    
    public boolean selectionenabled = true;
    
    int w = this.getWidth();
    int h = this.getHeight();
    Insets insets = getInsets();
    
    /** Creates new form ImgPreviewPanel */
    public ImgPreviewPanel() {
        
        initComponents();
    }
    
    
    /**loads an image from file and puts it into mBufferedImage */
    public void loadImage(String fileName) {
        
        try {
            File file = new File(fileName);
            preview = ImageIO.read(file);
            ColorModel cm = preview.getColorModel();
            int bpp = cm.getPixelSize();
//            String formatName = getFormatName(file);
//            System.out.println("Image is of type "+formatName);
            System.out.println("Depth is "+bpp+" bits");
            System.out.println("Size: "+preview.getWidth()+"x"+preview.getHeight());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
//        preview = new BufferedImage( image.getWidth(null), image.getHeight(null),
//        				   BufferedImage.TYPE_BYTE_GRAY);
        doImageRescale();
        //repaint();
    }
    
    /**custom paint method that ensures proper display ratio of images*/
    public void paintComponent(Graphics g) {
        //Graphics2D g2 = (Graphics2D)g;
        
        super.paintComponent(g);
        //g2.setRenderingHints(Graphics2D.ANTIALIASING, Graphics2D.ANTIALIAS_ON);
        if (preview == null) return;
        
        //doImageRescale();
        g.drawImage(preview, insets.left, insets.top, w, h, null);
        if (Settings.crop == true && selectionenabled == true){
            g.setColor(Color.yellow);
            if (x1 != x2){
                g.drawRect( Math.min( x1, x2 ), Math.min( y1, y2 ),
                        Math.abs( x1 - x2 ), Math.abs( y1 - y2 ) );
            }
        }
        
    }
    
    /** ensures that the image is displayed with the
     * correct ratio as the internal frame is resized */
    public void doImageRescale(){
        if (preview == null) return;
        double imageratio = (double)preview.getWidth()/ (double)preview.getHeight();
        if (imageratio >= 1) {
            w = this.getWidth();
            h = (int) ((double)w / imageratio);
            insets.top = this.getHeight()/2 - h/2;
            insets.left = getInsets().left;
            if (h > this.getHeight()){
                h = this.getHeight();
                w = (int) ((double)h * imageratio);
                insets.left = this.getWidth()/2 - w/2;
                insets.top = getInsets().top;
            }
        } else if (imageratio < 1){
            h = this.getHeight();
            w = (int) ((double)h * imageratio);
            insets.left = this.getWidth()/2 - w/2;
            insets.top = getInsets().top;
            if (w > this.getWidth()){
                w = this.getWidth();
                h = (int) ((double)w / imageratio);
                insets.top = this.getHeight()/2 - h/2;
                insets.left = getInsets().left;
            }
        }
        repaint();
    }
    
    /** Sets the video data crop values from the ones calculated
     * from the preview image selection. */
    public void setCrop(){
        VideoTitle.cropw = this.cropw;
        VideoTitle.croph = this.croph;
        VideoTitle.cropx = this.cropx;
        VideoTitle.cropy = this.cropy;
    }
    
    /** Calculates crop from current selection rect */
    public void doCropCalc(){
        if (p1.x < 0) p1.x = 0; if (p1.y < 0) p1.y = 0;
        if (p2.x < 0) p2.x = 0; if (p2.y < 0) p2.y = 0;
        cropw = Math.abs(p2.x - p1.x);
        croph = Math.abs(p2.y - p1.y);
        if (p1.x < p2.x) cropx = p1.x; else cropx = p2.x;
        if (p1.y < p2.y) cropy = p1.y; else cropy = p2.y;
        //System.out.println("w="+cropw +",h="+croph + ",x="+cropx +",y="+cropy);
    }
    
    /** Updates the crop rectangle used in the preview panel based on the
     * current crop values. This is called when detect crop is used and
     * thereafter when the panel is resized. */
    public void setRectfromCropSettings(){
        double s = getPixelPanelScale();
        
        try {
            if (panelratio <= 1){
                int f = (int) Math.round(getHeight()/2 -
                        (preview.getHeight()*s)/2 );
                x1 = (int) ((double)VideoTitle.cropx * s );
                y1 = (int) (((double)VideoTitle.cropy * s + f ));
                x2 = (int) ((double)(VideoTitle.cropw + VideoTitle.cropx) * s );
                y2 = (int) (((double)(VideoTitle.croph + VideoTitle.cropy) * s + f ));
            } else {
                int f = (int) Math.round(getWidth()/2 -
                        (preview.getWidth()*s)/2 );
                x1 = (int) (((double)VideoTitle.cropx * s + f ));
                y1 = (int) ((double)VideoTitle.cropy * s );
                x2 = (int) (((double)(VideoTitle.cropw + VideoTitle.cropx) * s + f ));
                y2 = (int) ((double)(VideoTitle.croph + VideoTitle.cropy) * s );
            }
            
            repaint();
        } catch (NullPointerException ne) {};
        
    }
    
    
    /** Gets panel coords to actual image pixel coords scale value.
     * Returns the scaling value as a double. */
    public double getPixelPanelScale(){
        double scale = 1;
        try{
            panelratio = (double)this.getWidth()
            / (double)this.getHeight();
            if (panelratio <= 1){
                scale = (double) this.getWidth() / (double)preview.getWidth();
                
            } else {
                scale = (double) this.getHeight() / (double)preview.getHeight();
            }
            
        } catch (NullPointerException ne) {};
        //System.out.println("SCALE=" +scale);
        return scale;
    }
    
    /** Gets the image pixel coords from the corresponding points
     * in the preview panel, by applying the scaling value. */
    public Point getPixelCoords(int x, int y){
        Point p = new Point(0,0);
        
        double s = getPixelPanelScale();
        //get position of actual image pixels, scaled and offset
        try {
            if (panelratio <= 1){
                p.x = (int)((double)x/s) ;
                p.y = (int) (y/s) - ((int)Math.round(getHeight()/s) -
                        preview.getHeight())/2;
            } else {
                p.y = (int)((double)y/s) ;
                p.x = (int) (x/s) - ((int)Math.round(getWidth()/s) -
                        preview.getWidth())/2;
            }
        } catch (NullPointerException ne) {};
        
        return p;
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBackground(new java.awt.Color(204, 204, 204));
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
            }
        });
        addHierarchyBoundsListener(new java.awt.event.HierarchyBoundsListener() {
            public void ancestorMoved(java.awt.event.HierarchyEvent evt) {
            }
            public void ancestorResized(java.awt.event.HierarchyEvent evt) {
                formAncestorResized(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 528, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 333, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    private void formAncestorResized(java.awt.event.HierarchyEvent evt) {//GEN-FIRST:event_formAncestorResized
        doImageRescale();
        this.setRectfromCropSettings();
        
    }//GEN-LAST:event_formAncestorResized
    
    
    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        // handle mouse drag event
        x2 = evt.getX();
        y2 = evt.getY();
        repaint();
        
        
    }//GEN-LAST:event_formMouseDragged
    
    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
//        x = evt.getX();
//        y = evt.getY();
//        int pxval = 0; double actualval = 0;
//        Point p = new Point(0,0);
//        p = getPixelCoords(x, y);
//        repaint();
    }//GEN-LAST:event_formMouseMoved
    
    private void formMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseReleased
        x2 = evt.getX();
        y2 = evt.getY();
        p1 = getPixelCoords(x1, y1); p2 = getPixelCoords(x2, y2);
        System.out.print(p1+": ");
        System.out.println(p2);
        if (Settings.crop == true && selectionenabled == true ){
            doCropCalc();
            setCrop();
            MainGUI.updateCrop();
        }
    }//GEN-LAST:event_formMouseReleased
    
    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        x1 = evt.getX();
        y1 = evt.getY();
    }//GEN-LAST:event_formMousePressed
    
    public int getPrevWidth(){
        int w = preview.getWidth();
        return w;
    }
    
    public int getPrevHeight(){
        int h = preview.getHeight();
        return h;
    }
    
    /**saves the current bufferedimage to a file*/
    
    public void saveImage(String fileName) {
        try {
            javax.imageio.ImageWriter writer = (javax.imageio.ImageWriter)
            ImageIO.getImageWritersByFormatName("png").next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            ImageTypeSpecifier imTy = param.getDestinationType();
            ImageTypeSpecifier imTySp =
                    ImageTypeSpecifier.createFromRenderedImage(preview);
            param.setDestinationType(imTySp);
            System.out.println("Found writer " + writer);
            File file = new File(fileName+".png");
            ImageOutputStream ios = new FileImageOutputStream(file);
            writer.setOutput(ios);
            writer.write( preview);
            
        } catch (IOException ioe) { ioe.printStackTrace(); }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ImgPreviewPanel().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
}
