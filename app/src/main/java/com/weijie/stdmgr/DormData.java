package com.weijie.stdmgr;



import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by weijie on 2018/7/10.
 */
public class DormData {

    private String roomNo;
    int availiableNumber;
    String [][]array;

    private ArrayList<RoomStudentData> arrayListStud;

       class RoomStudentData {
        String no, name;

        RoomStudentData(String no, String name) {
            this.no = no;
            this.name = name;
        }
    }

    DormData() {
        //init();
    }

    public void read() {
        roomNo = "A3011";
        availiableNumber = 6;
        arrayListStud = new ArrayList<>();
        RoomStudentData roomStudentData1 = new RoomStudentData("1440706001", "李四");
        arrayListStud.add(roomStudentData1);
        RoomStudentData roomStudentData2 = new RoomStudentData("1440706002", "刘建强");
        arrayListStud.add(roomStudentData2);
        RoomStudentData roomStudentData3 = new RoomStudentData("1440706003", "刘信良");
        arrayListStud.add(roomStudentData3);
        RoomStudentData roomStudentData4 = new RoomStudentData("1440706004","黄萍");
        arrayListStud.add(roomStudentData4);
        RoomStudentData roomStudentData5 = new RoomStudentData("1440706005", "万志伟");
        arrayListStud.add(roomStudentData5);
        RoomStudentData roomStudentData6 = new RoomStudentData("1440706006", "林创强");
        arrayListStud.add(roomStudentData6);
    }
    public String getRoomNo(){
        return roomNo;
    }
    public int getAvailiableNumber(){
        return availiableNumber;
    }
    public ArrayList<RoomStudentData> getArrayListStud() {
        return arrayListStud;
    }

}