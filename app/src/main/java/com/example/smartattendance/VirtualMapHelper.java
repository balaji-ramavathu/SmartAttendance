package com.example.smartattendance;

import java.util.*;
import static java.lang.Math.max;

public class VirtualMapHelper {
    ArrayList<ArrayList <ArrayList<Student > > > VMap;
    /*
    VMap : List of Columns
    Columns : List of benches
    bench : List of Students
    */
    ArrayList<Integer> ColumnWidth;
    int maxRow;
    int maxColumn,maxSeatCount;
    VirtualMapHelper(){
        this.VMap = new ArrayList<ArrayList <ArrayList<Student > > > ();
        this.ColumnWidth = new ArrayList<Integer>();
        this.maxRow = 0;
        this.maxColumn = 0;
    }
    public void Update(ArrayList<String> data){
        ArrayList<Student> students = new ArrayList<Student>();
        for(String _str : data){
            Student student = new Student(_str);
            students.add(student);
        }
        setLimits(students);
        extendSizes();
        for(Student _student : students) {
            int flag = 0;
            for(int i = 0; i < VMap.get(_student.Column - 1).get(_student.Row - 1).size(); ++i) {
                if (VMap.get(_student.Column - 1).get(_student.Row - 1).get(i).equals(_student)) {
                    flag = 1;
                    break;
                }
            }
            if(flag == 0) {
                VMap.get(_student.Column - 1).get(_student.Row - 1).add(_student);
                ColumnWidth.set(_student.Column - 1, max(ColumnWidth.get(_student.Column - 1), VMap.get(_student.Column - 1).get(_student.Row - 1).size()));
            }
        }
    }
    private void setLimits(ArrayList<Student> students){
        for(Student _student : students){
            maxColumn = max(maxColumn, _student.Column);
            maxRow = max(maxRow, _student.Row);
        }
    }
    private void extendSizes(){
        for(int i = VMap.size(); i < maxColumn; ++i) {
            VMap.add(new ArrayList<ArrayList<Student>>());
            ColumnWidth.add(0);
        }
        for(int i = 0; i < VMap.size(); ++i){
            for(int j = VMap.get(i).size(); j < maxRow; ++j) {
                VMap.get(i).add(new ArrayList<Student>());

            }
        }
    }
    public int getColumnWidthSize() {
        return ColumnWidth.size();
    }

    public ArrayList<ArrayList<ArrayList<Student>>> getVMap() {
        return VMap;
    }

    public void setVMap(ArrayList<ArrayList<ArrayList<Student>>> VMap) {
        this.VMap = VMap;
    }

    public ArrayList<Integer> getColumnWidth() {
        return ColumnWidth;
    }

    public void setColumnWidth(ArrayList<Integer> columnWidth) {
        ColumnWidth = columnWidth;
    }

    public int getMaxRow() {
        return maxRow;
    }

    public void setMaxRow(int maxRow) {
        this.maxRow = maxRow;
    }

    public int getMaxColumn() {
        return maxColumn;
    }

    public void setMaxColumn(int maxColumn) {
        this.maxColumn = maxColumn;
    }
}
