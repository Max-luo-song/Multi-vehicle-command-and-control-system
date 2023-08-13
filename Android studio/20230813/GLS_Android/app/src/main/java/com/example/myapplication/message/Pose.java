package com.example.myapplication.message;

// 订阅话题/publish_pose
// 位姿message类型，前三个对应/odom的position变量，后四个对应/odom的orientation变量
// 本项目中只使用position_x，position_y表示位置，orientation_z，orientation_w表示方向
// 其余四个变量初始化为0，不使用
@MessageType(string = "publish_pose/Publish_pose")
public class Pose extends Message {
    public double position_x;
    public double position_y;
    public double position_z;
    public double orientation_x;
    public double orientation_y;
    public double orientation_z;
    public double orientation_w;

    public Pose() {

    }
    // 构造函数：快捷键alt+Insert
    public Pose(double position_x, double position_y, double position_z, double orientation_x, double orientation_y, double orientation_z, double orientation_w) {
        this.position_x = position_x;
        this.position_y = position_y;
        this.position_z = position_z;
        this.orientation_x = orientation_x;
        this.orientation_y = orientation_y;
        this.orientation_z = orientation_z;
        this.orientation_w = orientation_w;
    }
}
