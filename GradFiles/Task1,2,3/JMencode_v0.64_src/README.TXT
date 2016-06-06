Jmencode

This program is a simple java front-end for the free and very useful MPlayer
software, for the purpose of encoding video. Initially the focus is on
converting DVD into MPEG-4. 
MPlayer and Mencoder are available under the GNU General Public License v2.
Mencoder is primarily used on linux or related unix platforms, but there are
binaries for running on windows also.
There are many freely available frontends for this kind of functionality.
The purpose of this exercise is to create one that try to be as cross-platform
and simple as possible. This means excluding the use if a piece of software
like Avisynth, which in it's current version is only available for windows.
For muxing, both MP4Box part of GPAC (LGPL) and mkvmerge (GPL) are used.
All the above software can be obtained for free.

Usage:

There are several ways to run this software :
The simplest is to just double-click on the file 'jmencode.jar',
if you have windows explorer/linux set up to open the jar file using java
or type the following at the console/terminal window inside this directory:
java -jar jmencode.jar (this assumes that the path to the java jre
is setup. If not, you will have to type the full location of the
java.exe file). Use java -Xmx256m -jar jmencode_v0.1.jar 
to allocate more memory for runtime.

License:

 GUI for audio and video encoding using mencoder and mplayer
 Copyright (C) Damien Farrell
 
    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.
 
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
