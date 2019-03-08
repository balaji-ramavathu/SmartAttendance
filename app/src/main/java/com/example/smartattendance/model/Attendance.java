package com.example.smartattendance.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;

@Entity(indexes = {@Index(value = "courseId,date", unique = true)})
public class Attendance {
    @Property(nameInDb = "courseId")
    private String courseId;
    @Property(nameInDb = "date")
    private String date;
    @Generated(hash = 1578813368)
    public Attendance(String courseId, String date) {
        this.courseId = courseId;
        this.date = date;
    }
    @Generated(hash = 812698609)
    public Attendance() {
    }
    public String getCourseId() {
        return this.courseId;
    }
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
    public String getDate() {
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }

}
