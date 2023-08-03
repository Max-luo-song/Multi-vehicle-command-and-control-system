package com.example.myapplication.message;

// 订阅话题/cmd_vel
import com.example.myapplication.structure.Vector3;
@MessageType(string = "geometry_msgs/Twist")
public class Move extends Message {
    public Vector3 linear;    // 包含linear.x   linear.y  linear.z
    public Vector3 angular;   // 包含angular.x  angular.y   angular.z
}
