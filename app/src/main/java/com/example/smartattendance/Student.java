package com.example.smartattendance;

public class Student {
    String SubjectCode;
    String RollNumber;
    int Column, Row;
    String Owner;
    Student(String received){
        /*Form at of received : SubjectCOde_RollNumber_Column_Row_Owner Owner here is the person marking the attendance for the rollnumber */
        String []splits = received.split("_");
        this.SubjectCode = splits[0];
        this.RollNumber = splits[1];
        this.Row = Integer.parseInt(splits[2]);
        this.Column = Integer.parseInt(splits[3]);
        this.Owner = splits[4];
    }
    boolean equals(Student student){
        if(this == student){
            return true;
        }

        if(this.RollNumber.compareTo(student.RollNumber) == 0){
            return true;
        }
        return false;
    }
}
