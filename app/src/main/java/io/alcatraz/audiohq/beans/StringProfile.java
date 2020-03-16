package io.alcatraz.audiohq.beans;

public class StringProfile {
    private String raw;
    private float left = 1;
    private float right = 1;
    private float general = 1;
    private boolean control_lr = true;


    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public float getRight() {
        return right;
    }

    public void setRight(float right) {
        this.right = right;
    }

    public float getGeneral() {
        return general;
    }

    public void setGeneral(float general) {
        this.general = general;
    }

    public boolean isControl_lr() {
        return control_lr;
    }

    public void setControl_lr(boolean control_lr) {
        this.control_lr = control_lr;
    }
}
