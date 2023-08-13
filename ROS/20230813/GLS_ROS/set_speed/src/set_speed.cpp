/*
    作者：NUAA平行智能项目组(组内成员：郭洛松，蔡庚沅)
	程序说明：本程序使用双线程同时订阅发布,用于转换速度
        线程1(发布线程)：发布/cmd_vel
        线程2(订阅线程)：订阅/set_speed
*/

#include <ros/ros.h>
#include <thread>
#include <unistd.h>
#include <ros/ros.h>
#include <geometry_msgs/Twist.h>
#include <thread>


int tag = 0;
double linear_x = 999;
double angular_z = 999;

// 接收到订阅的消息后，会进入消息回调函数
void TwistCallback(const geometry_msgs::Twist& msg)
{
    tag = 1;
    linear_x = msg.linear.x;
    angular_z = msg.angular.z;
    return ;
}

// function：发布线程，但速度数据有效时，循环向cmd_vel发布数据
void publishThread() {
    ros::NodeHandle n;
    ros::Publisher Twist_pub = n.advertise<geometry_msgs::Twist>("/cmd_vel", 10);
    geometry_msgs::Twist vel_msg;
    while (1) {
        if (linear_x != 999 || angular_z != 999) {
            // 设置循环的频率
            ros::Rate loop_rate(10);
            while (ros::ok()) {
                vel_msg.linear.x = linear_x;
                vel_msg.angular.z = angular_z;
                Twist_pub.publish(vel_msg);
                loop_rate.sleep();     
            }
        }
    }
    return ;
}

void SubscribeThread() {
    ros::NodeHandle n;
    ros::Subscriber Twist_sub = n.subscribe("/set_speed", 10, TwistCallback);
    ROS_INFO("node set_speed launch successfully!!!");
    ros::spin();
}

int main(int argc, char** argv){
    ros::init(argc, argv, "set_speed");
    // 创建节点句柄
    // 再创建线程，用于发布
    std::thread publishThreadObj(publishThread);
    std::thread SubscribeThreadObj(SubscribeThread);
    // 等待线程完成
    publishThreadObj.join();
    SubscribeThreadObj.join();
    return 0;
}
