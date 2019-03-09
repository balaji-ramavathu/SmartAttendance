package com.example.smartattendance;

import java.util.ArrayList;

public class dbCourse {
    String courseid, name, spreadsheetID;
    ArrayList <dbRollnumber> rollnumbers;
    dbCourse(String courseid, String name, ArrayList <dbRollnumber> rollnumbers){
        this.courseid = courseid;
        this.name = name;
        this.rollnumbers = rollnumbers;
    }
}
