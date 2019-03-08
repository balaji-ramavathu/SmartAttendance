package com.example.smartattendance.model;

public class RollLimits {
    String suffix,from,to;

    public RollLimits(String suffix, String from, String to) {
        this.suffix = suffix;
        this.from = from;
        this.to = to;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
