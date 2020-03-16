/**
  * Copyright 2020 bejson.com 
  */
package io.alcatraz.audiohq.beans.playing;
import java.util.List;

/**
 * Auto-generated: 2020-03-14 8:9:2
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Processes {

    private String pid;
    private String process;
    private double left;
    private double right;
    private double general;
    private boolean muted;
    private boolean isweakkey;
    private boolean has_profile;
    private int track_count;
    private int active_count;
    private List<Buffers> buffers;
    public void setPid(String pid) {
         this.pid = pid;
     }
     public String getPid() {
         return pid;
     }

    public void setProcess(String process) {
         this.process = process;
     }
     public String getProcess() {
         return process;
     }

    public void setLeft(double left) {
         this.left = left;
     }
     public double getLeft() {
         return left;
     }

    public void setRight(double right) {
         this.right = right;
     }
     public double getRight() {
         return right;
     }

    public void setGeneral(double general) {
         this.general = general;
     }
     public double getGeneral() {
         return general;
     }

    public void setMuted(boolean muted) {
         this.muted = muted;
     }
     public boolean getMuted() {
         return muted;
     }

    public void setIsweakkey(boolean isweakkey) {
         this.isweakkey = isweakkey;
     }
     public boolean getIsweakkey() {
         return isweakkey;
     }

    public void setHas_profile(boolean has_profile) {
         this.has_profile = has_profile;
     }
     public boolean getHas_profile() {
         return has_profile;
     }

    public void setTrack_count(int track_count) {
         this.track_count = track_count;
     }
     public int getTrack_count() {
         return track_count;
     }

    public void setActive_count(int active_count) {
         this.active_count = active_count;
     }
     public int getActive_count() {
         return active_count;
     }

    public void setBuffers(List<Buffers> buffers) {
         this.buffers = buffers;
     }
     public List<Buffers> getBuffers() {
         return buffers;
     }

}