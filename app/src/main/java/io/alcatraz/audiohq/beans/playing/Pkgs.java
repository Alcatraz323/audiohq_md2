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
public class Pkgs {

    private String pkg;
    private double left;
    private double right;
    private double general;
    private boolean muted;
    private boolean isweakkey;
    private boolean has_profile;
    private boolean has_active_track;
    private List<Processes> processes;

    private boolean sticky = false;
    public void setPkg(String pkg) {
         this.pkg = pkg;
     }
     public String getPkg() {
         return pkg;
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

    public void setHas_active_track(boolean has_active_track) {
         this.has_active_track = has_active_track;
     }
     public boolean getHas_active_track() {
         return has_active_track;
     }

    public void setProcesses(List<Processes> processes) {
         this.processes = processes;
     }
     public List<Processes> getProcesses() {
         return processes;
     }

    public boolean isSticky() {
        return sticky;
    }

    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }
}