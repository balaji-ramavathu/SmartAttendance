package com.iiita.smartattendance;

public class Student {
    String SubjectCode;
    String RollNumber;
    int Column, Row;
    String Owner;
    Student(String SubjectCode, String Rollnumber, int Row, int Column, String Owner){
        this.SubjectCode = SubjectCode;
        this.RollNumber = Rollnumber;
        this.Row = Row;
        this.Column = Column;
        this.Owner = Owner;
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
