package com.example.myapplication.message;

// 订阅话题/action_state
@MessageType(string = "subscribe_goal/Action_state_number")
public class ActionStateNumber extends Message {
    public int action_state;
    public int action_number;
    public ActionStateNumber() {
        action_state = -1;
        action_number = -1;
    }
}
