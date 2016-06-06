/*
 * Created on 13 November 2006, 00:03
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
import java.lang.reflect.Field;

/**
 * This class stores all the static variables for the
 * current input video. This data is taken by parsing the
 * Mplayer console output using the -identify switch when
 * the file is opened.
 */

public class VideoTitle {
    static int id,
            numaud = 0, numsub = 0; //number of audio and subtitle
    public static int notitles = 1;
    public static String type = "", //whether title is single file or part of dvd
                    vidformat = ""; //video format of input
//    /*for multiple titles in dvds, can use a vector to store them, otherwise
//     *this vector only has one element */
//    public static Vector titles = new Vector(1);
    
    public static double inputDAR = 1,
            targetaspectratio = 0,
            actualaspectratio = 0,
            error = 0;
    public static int
            inputw, inputh, //video dimensions
            cropw = 0, croph = 0, cropx = 0, cropy = 0, //crop data
            videolength;    //time length of input in seconds
    public static float
            fps;    //frames per second of input
    
    //this variable sets whether the crop can be detected
    //on the video or not, if it is false, crop detect is ignored
    public static boolean cropdetectable = true;
    
    
    //stores audio track(s) for the title, may be more than one
    static class Audiotrack{
        int aid;
        String lang,
                format;
        
        public Audiotrack(){
            
        }
        
        public Audiotrack(int aid, String lang, String format){
            this.aid = aid;
            this.lang = lang;
            this.format = format;
        }
        
        /*Output details for the track */
        public String print(){
            String s = "aid: "+ aid
                    + ", Language: "+ lang
                    + ", Format: " + format + "\n";
//            System.out.println(s);
            return s;
        }
    }
    
    //stores subtitle track(s) for the title, may be more than one
    static class Subtrack{
        int id;
        String lang;
        
        public Subtrack(){
            
        }
    }
    
    //stores the audio and subtitle track info in vectors
    public static Vector audtracks = new Vector(1);
    public static Vector subtracks = new Vector(1);
    /** Creates a new instance  */
    public VideoTitle() {
        
    }
    
    
    public void addAudioTrack(Audiotrack a){
        audtracks.addElement(a);
    }
    
    /**Add a new track at index i */
    public static void addAudioTrack(int aid, String aul, String auf){
        Audiotrack a = new Audiotrack(aid, aul, auf);
        audtracks.addElement(a);
        numaud++;
    }
    
    /**Set new data for the audio track at index i */
    public void setAudioTrack(int i, Audiotrack a){
        audtracks.insertElementAt(a, i);
    }
    
    /**Add a new track at index i */
    public void setAudioTrack(int i, int aid, String aul, String auf){
        Audiotrack a = new Audiotrack(aid, aul, auf);
        audtracks.insertElementAt(a, i);
    }
    
    /**Gets the audio track at index i */
    public Audiotrack getAudioTrack(int i){
        Audiotrack a = (Audiotrack) audtracks.elementAt(i);
        return a;
    }
    
    /**Gets the audio track aid value at index i */
    public static int getAid(int i){
        int aid = 0;
        try {
            Audiotrack a = (Audiotrack) audtracks.elementAt(i);
            aid = a.aid;
        }  catch (ArrayIndexOutOfBoundsException ae){};
        return aid;
    }
    
    /**Gets the audio track format value at index i */
    public static String getaudFormat(int i){
        String f = "";
        try {
            Audiotrack a = (Audiotrack) audtracks.elementAt(i);
            f = a.format;
        }  catch (ArrayIndexOutOfBoundsException ae){};
        return f;
    }    
    
    /*Output details for the title  */
    public static String print(){
        String s = (type == "dvd" ? "Titles in Disc: "+notitles : "File is of type " + type)
                + "\n"
                +("-----Title_ID "+id+"-----")+"\n"
                +("Format: "+vidformat)+"\n"
                +("Width: "+inputw)+"\n"
                +("Height: "+inputh)+"\n"
                +("DAR: "+inputDAR)+"\n"
                +("Length (mins): "+videolength/60)+"\n"
                +("FPS: "+fps)+"\n"
                +("Current Crop Data")+"\n"
                +("Crop Width: "+VideoTitle.cropw)+"\n"
                +("Crop Height: "+VideoTitle.croph)+"\n"
                +("Crop X: "+VideoTitle.cropx)+"\n"
                +("Crop y: "+VideoTitle.cropy)+"\n";
        
        if (numaud > 0){
            s = s + ("This title has "+numaud+" audio tracks"+"\n");
            for (int i=0; i < numaud; i++ ){
                Audiotrack a = (Audiotrack) audtracks.elementAt(i);
                String s1 = a.print();
                s = s + s1;
            }
        }
        System.out.println(s);
        return s;
    }
    
    
//    /**Add at least one element to the titles list */
//    public static void createTitle(){
//        Title t = new Title();
//        titles.addElement(t);
//    }
//
//    /**Get the title at index i */
//    public static Title getTitle(int i){
//        Title t = (Title) titles.elementAt(i);
//        return t;
//    }
//
//    /**Sets the title at index i */
//    public static void setTitle(int i, Title t){
//        titles.insertElementAt(t, i);
//    }
//
//    /**Create a new title with input values */
//    public static void addTitle( int i, int audid, String aulang, String aufmt ){
//        Title t = new Title(i, aulang, aufmt);
//        titles.addElement(t);
//    }
//
    /**Get error between target aspect ratio and mod 16 value in percent*/
    public static void getscaleError(){
        if (targetaspectratio > 1){
            error = round(Math.abs((actualaspectratio - targetaspectratio)/targetaspectratio)*100,2);
        }
        if (error < 0.01){
            error = 0;
        }
    }
    
    public static void resetInfo(){
        type = "";
        vidformat = "";
        cropdetectable = true;
        cropw = croph = cropx = cropy = 0;
        inputw = inputh = 0;
        fps = 25;
        videolength = 0;
        notitles = 1;
        audtracks.clear();
        subtracks.clear();
        numaud = 0;
        id = 1;
    }
    
    /**Try to estimate the target aspect ratio from the crop values
     * and original video aspect ratio and also the input DAR*/
    public static void findAspectRatio(){
        targetaspectratio = Math.abs(round(((double)inputh/(double)croph) * inputDAR, 2));
        System.out.println("TARGET ASPECT RATIO= "+targetaspectratio);
    }
    
    /**rounds a double down to the specified number of decimal places*/
    public static double round(double a, int b) {
        double tempA = a * Math.pow(10, b);
        long tempB = (long) tempA;
        int diff = (int) (10 * (tempA - tempB));
        if (diff >= 5) {
            ++tempA;
            tempB = (long) tempA;
        }
        tempA = tempB / Math.pow(10, b);
        return tempA;
    }
    
    
    public static void main(String args[]) {
        
        VideoTitle info = new VideoTitle();
        
        
    }
    
}
