package io.alcatraz.audiohq.beans;

public class OverallStatus {
    private String membytes;
    private String playingcount;
    private String pp_count;
    private String pm_count;
    private String wp_count;
    private String wm_count;
    private String version_code;

    public void setMembytes(String membytes) {
        this.membytes = membytes;
    }

    public String getMembytes() {
        return membytes;
    }

    public void setPlayingcount(String playingcount) {
        this.playingcount = playingcount;
    }

    public String getPlayingcount() {
        return playingcount;
    }

    public void setPp_count(String pp_count) {
        this.pp_count = pp_count;
    }

    public String getPp_count() {
        return pp_count;
    }

    public void setPm_count(String pm_count) {
        this.pm_count = pm_count;
    }

    public String getPm_count() {
        return pm_count;
    }

    public void setWp_count(String wp_count) {
        this.wp_count = wp_count;
    }

    public String getWp_count() {
        return wp_count;
    }

    public void setWm_count(String wm_count) {
        this.wm_count = wm_count;
    }

    public String getWm_count() {
        return wm_count;
    }

    public void setVersion_code(String version_code) {
        this.version_code = version_code;
    }
    public String getVersion_code() {
        return version_code;
    }

    public String getProfileCount() {
        int count = Integer.parseInt(pp_count) + Integer.parseInt(pm_count) + Integer.parseInt(wp_count)
                + Integer.parseInt(wm_count);
        return count + "";
    }
}
