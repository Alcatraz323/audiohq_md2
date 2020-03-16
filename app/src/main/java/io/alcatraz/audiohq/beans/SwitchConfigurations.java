package io.alcatraz.audiohq.beans;

public class SwitchConfigurations {
    private String raw;
    private int nativecode;
    private boolean defaultsilent;

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public int getNativecode() {
        return nativecode;
    }

    public void setNativecode(int nativecode) {
        this.nativecode = nativecode;
    }

    public boolean isDefaultsilent() {
        return defaultsilent;
    }

    public void setDefaultsilent(boolean defaultsilent) {
        this.defaultsilent = defaultsilent;
    }
}
