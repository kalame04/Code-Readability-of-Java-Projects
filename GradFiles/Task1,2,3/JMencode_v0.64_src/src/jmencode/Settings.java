/*
 * Settings.java
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
 * This class stores all the static variables for default and
 * current parameters related to encoding and muxing.
 */

public class Settings {
    
    public static String version = "0.64";
    public static String cfgpath = "jmencode.cfg";
    public static String osname = System.getProperty("os.name");
    public static String[] codecslist = { "x264", "lavc", "copy"};
    public static String[] fourcclist = { "auto", "XVID", "DIVX", "DIVX5", "H264","FLV1"};
    public static String[] acodecslist = {"mp3", "aac", "copy", "wav"};
    public static String[] abitratelist = {"96", "128", "160", "192"};
    public static String[] containerlist = {"mp4", "mkv", "avi", "flv"};   //for main video setup
    public static String[] muxerlist = {"mp4", "mkv"};      //excludes avi, for muxer form only
    public static String[] languagelist = {"English", "German", "French"};
    public static String[] scalerlist = {"fast bilinear", "bilinear", "bicubic",
    "nearest", "gauss", "lanczos"};
    public static String[] noframeslist = {"20", "50", "100", "200", "500"};
    public static String[] aspratiolist = {"auto", "ignore", "4:3",  "1.66:1", "1.85:1", "2.40:1"};
    public static String[] DARlist = {"auto", "1:1", "16:9"};
    public static String[] fitsizeslist = {"700MB (CD)", "650MB (CD)", "1400MB (2xCD)", "250MB", "100MB"};
    public static Integer[] titlelist = new Integer[]{1};
    
    public static String fileinputname, 
            fileoutputname = "output",
            audiofileoutput,
            dvddev = "",
            container = "mp4",
            audcodec = "mp3",
            codec = "x264",
            lavccodec="mpeg4",
            fourcc = "auto",
            starttime = "00:00:00", stoptime = "00:00:00";
    public static boolean usediscinput = true,
            resize = false,
            crop = false,
            selecttime = false,
            usemod16 = true,    //enforce mod 16 scaling, better for x264?
            domuxing = true,
            titlechanged = true,    //if new title is loaded, this is true
            dovolumegain = false,
            unsharp = false;
    public static int dvdtitle = 1, audtrack = 0, prevframes = 5, stepsize = 5,
            length = 60; //length of segment to encode in seconds
    public static float prevstarttime = 100, //start time of preview
            targetsize = 700;
    
    //general mencoder options
    public static int scalew = 0, scaleh = 0,
            sws = 2,
            //general encoding options
            bitrate = 700,
            audbitrate = 128,
            audquality = 60,
            volgain = 1,
            passes = 2,
            //options specific to x264
            qp = 26,        //This selects the quantizer to use for P-frames. default is 26
            crf = 30,       //Enables constant quality mode, and selects the quality
            subq = 4,
            trellis = 0,
            
            //filter options
            unsharpval = 1;
    
    public static String LAMEpath = "C:\\program files\\megui\\tools\\lame\\lame.exe";
    public static String Mencoderpath = "C:\\Program Files\\MeGUI\\tools\\mencoder\\mencoder.exe";
//    public static String Mencoderpath = "C:\\Program Files\\mplayer\\mencoder.exe";
    public static String Mplayerpath = "C:\\Program Files\\mplayer\\mplayer.exe";
    public static String Mkvmergepath = "C:\\Program Files\\MeGUI\\tools\\mkvmerge\\mkvmerge.exe";
    public static String MP4Boxpath = "C:\\Program Files\\MeGUI\\tools\\mp4box\\MP4Box.exe";
    
    public static String allapps = LAMEpath + "," + Mencoderpath + "," + Mplayerpath;
    public static String[] externalappslist = { LAMEpath, Mencoderpath, Mplayerpath };
    
    public static String muxCommand = Mkvmergepath + "";
    
    public static String currentinfo = "";  //current process status given in stdout, if running
    
    final static Field[] fields = Settings.class.getDeclaredFields();
    final static Object[] fieldsobj = new Object[fields.length];
    
    /** Creates a new instance of ExternalAppList */
    public Settings() {
        
    }
    
    /** Set commands without file paths, as these are not usually needed
     * in linux systems. Same for Mac OS? */
    public static void resetPaths(){
//         Possible return values are the same as the Platform property,
//         and include "WinXP", "Win2000", "WinME", "Windows Server 2003",
//         "WinCE", "Windows Vista", "MacOSX", "Linux", "SunOS", and "IRIX64".
        
        if (osname.matches("Linux") ){
            System.out.println("setting linux pathnames..");
            LAMEpath = "lame";
            Mencoderpath = "mencoder";
            Mplayerpath = "mplayer";
            Mkvmergepath = "mkvmerge";
            MP4Boxpath = "MP4Box";
        } else if (osname.matches("Mac OS X")){
            System.out.println("setting OS X pathnames..");
            LAMEpath = "/usr/local/bin/lame";
            Mencoderpath = "/usr/local/bin/mencoder";
            Mplayerpath = "/usr/local/bin/mplayer";
            Mkvmergepath = "/usr/local/bin/mkvmerge";
            MP4Boxpath = "/usr/local/bin/MP4Box";            
            
        }
    }
    
    /**
     * Saves the settings file, by writing out all the fields and their values to
     * a text file.
     */
    public static void saveSettingstoFile(String filename){
        File file;
        BufferedReader reader;
        String line;
        
        PrintWriter writer;
        String[] l_Names = new String[fields.length];
        
        try{
            if (filename == null){
                writer = new PrintWriter(new FileWriter(cfgpath));
            } else{
                writer = new PrintWriter(new FileWriter(filename));
            }
            Settings set = new Settings();
            line = set.getfields();
            System.out.println(line);
            writer.println(line);
            
            writer.close();
        } catch (IOException ioe) {
            System.out.println("I/O Exception in file writing: "+ioe); }
    }
    
    /**
     * Opens the settings file, if it exists and sets the parameters as required.
     * On first run of the program there may be no settings file and defaults are used.
     */
    public static void loadSettingsfromFile(String path){
        File file;
        BufferedReader reader;
        String line;
        
        //read header
        try{
            if (path == null){
                file = new File(cfgpath);
                reader = new BufferedReader(new FileReader(file));
            } else{
                file = new File(path);
                reader = new BufferedReader(new FileReader(file));
            }
            //reader.readLine();reader.readLine();
            while ( (line = reader.readLine()) != null){
                try {
                    
                    StringTokenizer st;
                    st = new StringTokenizer(line, " ");
                    String variable = st.nextToken().replace(":","");
                    String value = st.nextToken();
                    System.out.print("variable="+variable + "  ");
                    System.out.println("val="+value);
                    //assign runtime variables based on settings file values read
                    //very inefficient way to do it, but what the hell..
                    if (variable.matches("Mplayerpath")){
                        while (st.hasMoreTokens()){
                            value = value + " " + st.nextToken();
                        }
                        Mplayerpath = value;
                        System.out.println("Mplayerpath="+Mplayerpath+" ");
                    } else if (variable.matches("Mencoderpath")) {
                        while (st.hasMoreTokens()){
                            value = value + " " + st.nextToken();
                        }
                        Mencoderpath = value;
                        System.out.println("Mencoderpath="+Mencoderpath+" ");
                    } else if (variable.matches("Mkvmergepath")) {
                        while (st.hasMoreTokens()){
                            value = value + " " + st.nextToken();
                        }
                        Mkvmergepath = value;
                        System.out.println("Mkvmergepath="+Mkvmergepath+" ");
                        Mkvmergepath = value;
                    } else if (variable.matches("MP4Boxpath")) {
                        while (st.hasMoreTokens()){
                            value = value + " " + st.nextToken();
                        }
                        MP4Boxpath = value;
                        System.out.println("MP4Boxpath="+MP4Boxpath+" ");
                        MP4Boxpath = value;
                    }
                } catch (NoSuchElementException e ) {
                }
                
            }
            reader.close();
        }  catch (IOException ioe) { 
            System.out.println("No config file present. Using default values. "); 
        }
        
    }
    
    /**
     * Represent this object as a String.
     *
     * Here, a generic implementation uses reflection to print
     * names and values of all fields <em>declared in this class</em>. Note that
     * superclass fields are left out of this implementation.
     *
     */
    public String getfields() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        
        result.append( this.getClass().getName() );
        result.append( " Object {" );
        result.append(newLine);
        
        //determine fields declared in this class only (no fields of superclass)
        Field[] fields = this.getClass().getDeclaredFields();
        
        //print field names paired with their values
        for ( Field field : fields  ) {
            result.append("  ");
            try {
                result.append( field.getName() );
                result.append(": ");
                //requires access to private field:
                result.append( field.get(this) );
            } catch ( IllegalAccessException ex ) {
                System.out.println(ex);
            }
            result.append(newLine);
        }
        result.append("}");
        
        return result.toString();
    }
    
    /** Calculates video bitrate for preset target file size */
    public static void CalculateBitrate(int length){
        
        //(Size - (Audio x Length )) / Length = Video bitrate
        bitrate = Math.round(((( targetsize * 1024 * 8 )/1000)*1024 -
                (audbitrate*length))/ (length) );
        if (bitrate > 2000){
            bitrate = 2000;
        }
        System.out.println("BITRATE = "+ bitrate);
    }
    
    /**Find nearest lower mod 16 value to input */
    public static int nextlowerMod16(int val){
        int r = val%16;
        int n = val;
        if (r != 0){
            n = val - Math.abs(r);
        }
        //System.out.println(n);
        return n;
    }
    
    /**Find nearest higher mod 16 value to input */
    public static int nexthigherMod16(int val){
        int r = val%16;
        int n = val;
        if (r != 0){
            n = val + (16-Math.abs(r));
        }
        //System.out.println(n);
        return n;
    }
    
    /**Find nearest mod 16 value to input */
    public static int nearestMod16(int val){
        int r = val%16; int n;
        int upper = 0, lower = 0;
        if (r != 0){
            lower = val - Math.abs(r);
            upper = val + (16-Math.abs(r));
        }
        if ((upper - val) < (val - lower)){
            n = upper;
        } else {
            n = lower;
        }
        System.out.println(n);
        return n;
    }
    
    /**Set scale values according to aspect ratio and if
     * needed, based on mod 16 calculation*/
    public static void setScaleValues(int oldval){
        //if not mod 16, then change to nearest value
        System.out.println(usemod16);
        System.out.println(scalew % 16);
        System.out.println(oldval);
        if (usemod16 == true & scalew % 16 != 0 ){
            if (oldval < scalew){
                scalew = nexthigherMod16(scalew);
            } else {
                scalew = nextlowerMod16(scalew);
            }
        }
        if (VideoTitle.targetaspectratio != 0){
            scaleh = (int) Math.round(scalew / VideoTitle.targetaspectratio );
            if (usemod16 == true & scaleh % 16 != 0 ){
                scaleh = nearestMod16(scaleh);
            }
        }
        //find aspect ratio to be used with mod 16 and get an error from target
        VideoTitle.actualaspectratio = VideoTitle.round((double)scalew/scaleh, 2);
        VideoTitle.getscaleError();
        
        System.out.println(scalew);
        System.out.println(scaleh);
        System.out.println(VideoTitle.error);
    }
    
    /**
     * Parse 'mplayer -identify' output. This will result in the creation of
     * dvd title objects within the VideoTitle class if needed. There may be a
     * better way to get the video info, but the only way at the moment is to parse
     * the mplayer output.
     */
    public static void parseVideoInfo(String line){
        
        StringTokenizer matcher = new StringTokenizer(line , "=");
        matcher.nextToken();
        //number of current title being parsed, incremented when a new
        //ID_DVD_CURRENT_TITLE value is parsed
        int curraudid=1;
        
        if (line.startsWith("ID_DVD_TITLES")){
            VideoTitle.notitles = Math.round(Float.parseFloat(matcher.nextToken()));
//            System.out.println("titles="+VideoTitle.notitles);
        } else if (line.startsWith("ID_DVD_CURRENT_TITLE")){ //number of this title
            VideoTitle.id = Math.round(Float.parseFloat(matcher.nextToken()));
        } else if (line.startsWith("ID_VIDEO_WIDTH")){
            VideoTitle.inputw = Integer.parseInt(matcher.nextToken());
//            System.out.println("W="+VideoTitle.inputw);
        } else if (line.startsWith("ID_VIDEO_HEIGHT")){
            VideoTitle.inputh = Integer.parseInt(matcher.nextToken());
//            System.out.println("H="+VideoTitle.inputh);
        } else if (line.startsWith("ID_VIDEO_FPS")){
            VideoTitle.fps = Float.parseFloat(matcher.nextToken());
//            System.out.println("fps="+VideoTitle.fps);
        } else if (line.startsWith("ID_LENGTH")){
            VideoTitle.videolength = Math.round(Float.parseFloat(matcher.nextToken()));
//            System.out.println("length="+VideoTitle.videolength);
            
            //parse audio streams info and add the audio tracks
        } else if (line.startsWith("audio stream")){
            if (Settings.titlechanged == true){
                matcher = new StringTokenizer(line , ": ");
                matcher.nextToken(); matcher.nextToken();
                curraudid = Integer.parseInt(matcher.nextToken());
                matcher.nextToken();
                String format = matcher.nextToken();
                matcher.nextToken(); matcher.nextToken();
                String lang = matcher.nextToken();
                matcher.nextToken();
                String l2 = matcher.nextToken();
                matcher = new StringTokenizer(l2 , ".");
                int aid = Integer.parseInt(matcher.nextToken());
                VideoTitle.addAudioTrack(aid, lang, format);
            }
        } else if (line.startsWith("AVI file format")){
            VideoTitle.type = "avi";
        } else if (line.startsWith("ID_VIDEO_FORMAT")){
            VideoTitle.vidformat = matcher.nextToken();
        }
        
    }
    
    
    public  static void main(String args[]) {
//        Settings set = new Settings();
//        String s = set.getfields();
//        System.out.println(s);
//        saveSettingstoFile(null);
//        loadSettingsfromFile(null);
        
//
        String line;
        File file;
        BufferedReader reader;
        
        try{
            file = new File("mplayerout.txt");
            reader = new BufferedReader(new FileReader(file));
            
            while ( (line = reader.readLine()) != null){
                try {
                    parseVideoInfo(line);
                    
                } catch (NoSuchElementException e ) {}
            }
            reader.close();
        }  catch (IOException ioe) { System.out.println("I/O Exception "+ioe); }
        
        String s = VideoTitle.print();
//        System.out.println(s);
        String type = VideoTitle.getaudFormat(0); //get format
        System.out.println(type);
        
    }
    
}
