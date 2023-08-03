package com.example.myapplication.message;

@MessageType(string = "std_msgs/String")
public class YoloList extends Message {
    public String data;

    public YoloList() {
    }

    @Override
    public String toString() {
        return "Yolo_List{" +
                "listStr='" + data + '\'' +
                '}';
    }
}