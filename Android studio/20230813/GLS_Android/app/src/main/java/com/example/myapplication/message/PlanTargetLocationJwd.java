package com.example.myapplication.message;
import java.util.Arrays;

// 发布话题/subscribe_gaol
@MessageType(string = "subscribe_goal_jwd/Subscribe_goal_jwd")
public class PlanTargetLocationJwd extends Message {
    public double[] position_x; // 经度——使用
    public double[] position_y; // 纬度——使用
    public double[] position_z; // 0.0——不使用
    public double[] orientation_x;  // 0.0——不使用
    public double[] orientation_y;  // 0.0——不使用
    public double[] orientation_z;  // 使用
    public double[] orientation_w;  // 使用

    public int target_point_number;



    public PlanTargetLocationJwd() {
        // position初始化，初始化为0
        position_x = new double[12];
        Arrays.fill(position_x,0);
        position_y = new double[12];
        Arrays.fill(position_y,0);
        position_z = new double[12];
        Arrays.fill(position_z,0);
        // orientation初始化，初始化为0
        orientation_x = new double[12];
        Arrays.fill(orientation_x,0);
        orientation_y = new double[12];
        Arrays.fill(orientation_y,0);
        orientation_z = new double[12];
        Arrays.fill(orientation_z,0);
        orientation_w = new double[12];
        Arrays.fill(orientation_w,0);
    }
}