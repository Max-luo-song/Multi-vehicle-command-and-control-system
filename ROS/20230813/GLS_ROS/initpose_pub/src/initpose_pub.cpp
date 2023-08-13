 /*
    Function:subscribe the /initpose_android, if get message then publish the /initialpose
            /initpose_android消息类型initpose_pub/Initpose_pub
            /initialpose消息类型geometry_msgs/PoseWithCovarianceStamped
*/
#include <ros/ros.h>
#include <geometry_msgs/PoseWithCovarianceStamped.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/time.h>
#include <unistd.h>
#include <iostream>
#include "initpose_pub/Initpose_pub.h"

// function：/initpose_android的回调函数
void Initpose_androidCallback(const initpose_pub::Initpose_pub& msg) {
    ros::NodeHandle n;
    ros::Publisher initpose_pub;
    // 创建一个Publisher，发布名为/cmd_vel的topic，消息类型为geometry_msgs::Twist，队列长度10
    initpose_pub = n.advertise<geometry_msgs::PoseWithCovarianceStamped>("/initialpose", 10);
    geometry_msgs::PoseWithCovarianceStamped initpose_msg;
    initpose_msg.header.frame_id = "map";
    initpose_msg.header.stamp = ros::Time::now();
    initpose_msg.pose.pose.position.x = msg.x;
    initpose_msg.pose.pose.position.y = msg.y;
    initpose_msg.pose.pose.position.z = 0;
    initpose_msg.pose.pose.orientation.x = 0;
    initpose_msg.pose.pose.orientation.y = 0;
    initpose_msg.pose.pose.orientation.z = 0;
    initpose_msg.pose.pose.orientation.w = 1; 
    struct timeval time_old;
    gettimeofday(&time_old, NULL);
    long long int t_old = (long long int)time_old.tv_sec;
    struct timeval time_new;
    // 设置循环的频率
    ros::Rate loop_rate(10);
    initpose_pub.publish(initpose_msg);
    while (ros::ok())
	{
        initpose_pub.publish(initpose_msg);
	    loop_rate.sleep();
        gettimeofday(&time_new, NULL);
        long long int t_new = (long long int)time_new.tv_sec;
        if (t_new - t_old >= 2)
		    break;
	}
    ROS_INFO("位姿初始化成功！！！");
}

int main(int argc, char **argv)
{
    // 初始化ROS节点
    ros::init(argc, argv, "initpose_pub");
    // 创建节点句柄
    ros::NodeHandle n;
    ros::Subscriber Twist_sub = n.subscribe("/initpose_android", 10, Initpose_androidCallback);
    ROS_INFO("/initpose_pub 节点启动成功！！！");
    ros::spin();
    return 0;
}
