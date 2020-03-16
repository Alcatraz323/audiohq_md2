/**
  * Copyright 2019 bejson.com 
  */
package io.alcatraz.audiohq.beans.self;
import java.util.List;

/**
 * Auto-generated: 2019-08-06 10:40:15
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class  SelfConfig {

    private String name;
    private String self;
    private String giturl;
    private String siteurl;
    private int versioncode;
    private String versionname;
    private boolean suspend;
    private List<Updates> updates;
    private ShowOnstart showOnstart;
    public void setName(String name) {
         this.name = name;
     }
     public String getName() {
         return name;
     }

    public void setSelf(String self) {
         this.self = self;
     }
     public String getSelf() {
         return self;
     }

    public void setGiturl(String giturl) {
         this.giturl = giturl;
     }
     public String getGiturl() {
         return giturl;
     }

    public void setSiteurl(String siteurl) {
         this.siteurl = siteurl;
     }
     public String getSiteurl() {
         return siteurl;
     }

    public void setVersioncode(int versioncode) {
         this.versioncode = versioncode;
     }
     public int getVersioncode() {
         return versioncode;
     }

    public void setVersionname(String versionname) {
         this.versionname = versionname;
     }
     public String getVersionname() {
         return versionname;
     }

    public void setSuspend(boolean suspend) {
         this.suspend = suspend;
     }
     public boolean getSuspend() {
         return suspend;
     }

    public void setUpdates(List<Updates> updates) {
         this.updates = updates;
     }
     public List<Updates> getUpdates() {
         return updates;
     }

    public void setShowOnstart(ShowOnstart showOnstart) {
         this.showOnstart = showOnstart;
     }
     public ShowOnstart getShowOnstart() {
         return showOnstart;
     }

     public static SelfConfig getDefaultInstance(){
        return null;
     }

     public static SelfConfig requestForInstance(){
        return null;
     }

     
}