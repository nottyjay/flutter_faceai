package com.alphay.flutter.plugin.faceai.flutter_faceai.bean;

public class CamaraArgs {

    private String name;
    private String key;
    private Integer degree = 0;
    private Boolean horizontalMirror = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getDegree() {
        return degree;
    }

    public void setDegree(Integer degree) {
        this.degree = degree;
    }

    public Boolean isHorizontalMirror() {
        return horizontalMirror;
    }

    public void setHorizontalMirror(Boolean horizontalMirror) {
        this.horizontalMirror = horizontalMirror;
    }
}
