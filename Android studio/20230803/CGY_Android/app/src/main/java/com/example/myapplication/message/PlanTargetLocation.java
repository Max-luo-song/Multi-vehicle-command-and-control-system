package com.example.myapplication.message;
import java.util.Arrays;

// 发布话题/subscribe_gaol
@MessageType(string = "subscribe_goal/Subscribe_goal")
public class PlanTargetLocation extends Message {
    // 12个点：开始点(1)   返回点(1)   室内点(5)  室外点(5)
    public double[] position_x; // 经度——使用
    public double[] position_y; // 纬度——使用
    public double[] position_z; // 0.0——不使用
    public double[] orientation_x;  // 0.0——不使用
    public double[] orientation_y;  // 0.0——不使用
    public double[] orientation_z;  // 使用
    public double[] orientation_w;  // 使用

    public double action_number;
    public int target_point_number;



    public PlanTargetLocation() {
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
        action_number = -1;
    }
}