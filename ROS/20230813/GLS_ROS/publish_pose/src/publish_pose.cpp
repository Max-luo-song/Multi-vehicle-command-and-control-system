/*
    程序描述：ROS程序，功能是订阅/odom话题，并pose到/nav_pose话题
            消息类型nav_msgs/Odometry
*/
#include <ros/ros.h>
#include <geometry_msgs/Twist.h>
#include <nav_msgs/Odometry.h>
#include "publish_pose/Publish_pose.h"
#include <sys/time.h>
#include <unistd.h>

nav_msgs::Odometry odom_data;
publish_pose::Publish_pose pose_msg;
ros::Publisher pose_pub;
struct timeval time1;

int number = 0;

// 接收到订阅的消息后，会进入消息回调函数
void OdomCallback(const nav_msgs::Odometry& msg)
{
    pose_msg.position_x = msg.pose.pose.position.x;
    pose_msg.position_y = msg.pose.pose.position.y;
    pose_msg.position_z = 0.0;
    pose_msg.orientation_x = 0.0;
    pose_msg.orientation_y = 0.0;
    pose_msg.orientation_z = msg.pose.pose.orientation.z;
    pose_msg.orientation_w = msg.pose.pose.orientation.w;
    struct timeval new_time;
    gettimeofday(&new_time, NULL);
    if ((new_time.tv_sec*1000 + new_time.tv_usec/1000) - (time1.tv_sec*1000 + time1.tv_usec/1000) >= 100) {
	    pose_pub.publish(pose_msg);
        time1.tv_sec = new_time.tv_sec;
	    time1.tv_usec = new_time.tv_usec;
    }
    
}

int main(int argc, char **argv)
{
    // 初始化ROS节点
    ros::init(argc, argv, "publish_pose");
    // 创建节点句柄
    ros::NodeHandle n;
    // 创建一个Subscriber，订阅名为/odom，注册回调函数OdomCallback
    ros::Subscriber Odom_sub = n.subscribe("/odom", 10, OdomCallback);

    // 创建一个Publisher，发布名为/cmd_vel的topic，消息类型为geometry_msgs::Twist，队列长度10
    pose_pub = n.advertise<publish_pose::Publish_pose>("/publish_pose", 10);
    
    ROS_INFO("Topic /publish_pose publish successfully!");
    gettimeofday(&time1, NULL);
    // call the callback
    ros::spin();
    return 0;
}
