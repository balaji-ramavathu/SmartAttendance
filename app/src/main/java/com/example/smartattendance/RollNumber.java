package com.example.smartattendance;

public class RollNumber {
    String Suffix;
    int from;
    int to;

    public String getSuffix() {
        return Suffix;
    }

    public void setSuffix(String suffix) {
        Suffix = suffix;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public RollNumber() {

    }


    public RollNumber(String suffix, int from, int to) {
        Suffix = suffix;
        this.from = from;
        this.to = to;
    }
}
