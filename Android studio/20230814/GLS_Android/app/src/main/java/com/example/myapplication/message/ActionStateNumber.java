package com.example.myapplication.message;

// 订阅话题/action_state
@MessageType(string = "std_msgs/Int32")
public class ActionStateNumber extends Message {
    public int data;

    public ActionStateNumber() {
        data = -1;
    }
}
