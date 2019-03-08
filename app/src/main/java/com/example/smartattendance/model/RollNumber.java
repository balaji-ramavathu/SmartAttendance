package com.example.smartattendance.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity(indexes = {@Index(value = "roll_number", unique = true)})
public class RollNumber {
    @Id
    private String roll_number;
    @Property
    private String suffix;
    @Property
    private String number;

    @Generated(hash = 108983703)
    public RollNumber(String roll_number, String suffix, String number) {
        this.roll_number = roll_number;
        this.suffix = suffix;
        this.number = number;
    }

    @Generated(hash = 525988730)
    public RollNumber() {
    }

    public String getRoll_number() {
        return this.roll_number;
    }

    public void setRoll_number(String roll_number) {
        this.roll_number = roll_number;
    }


    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
