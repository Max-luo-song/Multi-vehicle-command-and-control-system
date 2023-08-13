package com.example.myapplication.message;

// 订阅话题/publish_pose
// 位姿message类型，前三个对应/odom的position变量，后四个对应/odom的orientation变量
// 本项目中只使用position_x，position_y表示位置，orientation_z，orientation_w表示方向
// 其余四个变量初始化为0，不使用
@MessageType(string = "initpose_pub/Initpose_pub")
public class InitPose extends Message {
    public double x;
    public double y;

    public InitPose() {

    }
    // 构造函数：快捷键alt+Insert
    public InitPose(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
