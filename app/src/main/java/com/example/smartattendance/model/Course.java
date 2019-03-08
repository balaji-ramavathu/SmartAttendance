package com.example.smartattendance.model;

import android.widget.ListView;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

import org.greenrobot.greendao.DaoException;


@Entity(indexes = {
        @Index(value = "courseCode", unique = true)
})
public class Course {

    @Id
    private String courseCode;
    @Property(nameInDb = "course_name")
    private String courseName;
    @ToMany(referencedJoinProperty = "roll_number")
    private List<RollNumber> rollNumbers;
    @Property
    private String spreadSheetId;
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 2063667503)
    private transient CourseDao myDao;

    @Generated(hash = 1749939666)
    public Course(String courseCode, String courseName, String spreadSheetId) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.spreadSheetId = spreadSheetId;
    }

    @Generated(hash = 1355838961)
    public Course() {
    }

    public String getCourseCode() {
        return this.courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return this.courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1887390470)
    public List<RollNumber> getRollNumbers() {
        if (rollNumbers == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            RollNumberDao targetDao = daoSession.getRollNumberDao();
            List<RollNumber> rollNumbersNew = targetDao
                    ._queryCourse_RollNumbers(courseCode);
            synchronized (this) {
                if (rollNumbers == null) {
                    rollNumbers = rollNumbersNew;
                }
            }
        }
        return rollNumbers;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 744817641)
    public synchronized void resetRollNumbers() {
        rollNumbers = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    public void setRollNumbers(List<RollNumber> rollNumbers) {
        this.rollNumbers = rollNumbers;
    }

    public String getSpreadSheetId() {
        return this.spreadSheetId;
    }

    public void setSpreadSheetId(String spreadSheetId) {
        this.spreadSheetId = spreadSheetId;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 94420068)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCourseDao() : null;
    }


}
