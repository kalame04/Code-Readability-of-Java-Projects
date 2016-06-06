/*
 * MplayerParser.java
 * Created on 23 December 2006, 22:44
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

public class MplayerParser {
    
    /** Creates a new instance of MplayerParser */
    public MplayerParser() {
        
    }
    
    /**parse the stdout line from the encoder that indicates progress,
     * to get percentage done*/
    public static int parseEncoderProgress(String line){
        int perc= 0;
        if (Settings.selecttime == false){
            //if doing whole video, use percentage to update progress bar
            //String line = "Pos:   2.1s     54f ( 1%) 46.67fps Trem:   0min  17mb  A-V:0.065 [1092:192]";
            StringTokenizer matcher = new StringTokenizer(line , "(");
            matcher.nextToken();
            line = matcher.nextToken();
            matcher = new StringTokenizer(line , "%");
            perc = Integer.parseInt(matcher.nextToken().trim()); //System.out.println(perc);
        } else{
            StringTokenizer matcher = new StringTokenizer(line , ":");
            matcher.nextToken();
            line = matcher.nextToken();
            matcher = new StringTokenizer(line , "s");
            Float time = Float.parseFloat(matcher.nextToken().trim()); //System.out.println(time);
            //System.out.println(Settings.length);
            perc = Math.round(time / Settings.length *100); //System.out.println(perc);
        }
        return perc;
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
    
    /**parse the stdout line from mplayer crop to get w,h,x and y values */
    public static void parseCropOutput(String line){
        //int w,h,x,y=0;
        StringTokenizer matcher = new StringTokenizer(line , "=");
        matcher.nextToken();
        line = matcher.nextToken();
        matcher = new StringTokenizer(line , ":");
        
        VideoTitle.cropw = Integer.parseInt(matcher.nextToken()); //System.out.println(VideoTitle.cropw );
        VideoTitle.croph = Integer.parseInt(matcher.nextToken()); //System.out.println(VideoTitle.croph);
        VideoTitle.cropx = Integer.parseInt(matcher.nextToken()); //System.out.println(x);
        VideoTitle.cropy = Integer.parseInt(matcher.nextToken().replace(").","")) ; //System.out.println(y);
        if (VideoTitle.cropw < 0 | VideoTitle.croph < 0 ){
            VideoTitle.cropdetectable = false;
        } else
            VideoTitle.cropdetectable = true;
    }
    
    public static double parseDARInfo(String line){
        StringTokenizer matcher = new StringTokenizer(line , "Movie-Aspect is ");
//        System.out.println(matcher.nextToken()); System.out.println(matcher.nextToken());
        line = matcher.nextToken();
        matcher = new StringTokenizer(line , ":");
        double DAR = 1.33;
        try {
            String d = matcher.nextToken();
            if (d.contains("und") == false ){
                DAR = Double.parseDouble(d);
            }
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
        System.out.println("DAR="+DAR);
        return DAR;
    }
    
    
}
