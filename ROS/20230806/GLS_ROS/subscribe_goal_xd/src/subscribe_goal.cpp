/*
    作者：郭洛松
    程序描述：ROS程序，功能是订阅/subscribe_goal话题，并将得到的数据用于导航(相对位置)
            消息类型：geometry_msgs::Twist
*/

#include <ros/ros.h>
#include <move_base_msgs/MoveBaseAction.h>
#include <actionlib/client/simple_action_client.h>
#include <geometry_msgs/Twist.h>
typedef actionlib::SimpleActionClient<move_base_msgs::MoveBaseAction> MoveBaseClient;

move_base_msgs::MoveBaseGoal goal;

void GoalCallback(const geometry_msgs::Twist& msg) {
    //tell the action client that we want to spin a thread by default
    MoveBaseClient ac("move_base", true);
    //wait for the action server to come up
    while(!ac.waitForServer(ros::Duration(5.0))){
        ROS_INFO("Waiting for the move_base action server to come up");
    }
    goal.target_pose.header.frame_id = "base_link";
    goal.target_pose.header.stamp = ros::Time::now();
    goal.target_pose.pose.position.x = msg.linear.x;
    goal.target_pose.pose.position.y = msg.linear.y;
    goal.target_pose.pose.position.z = msg.linear.z;
    goal.target_pose.pose.orientation.x = 0;
    goal.target_pose.pose.orientation.y = 0;
    goal.target_pose.pose.orientation.z = msg.angular.x;
    goal.target_pose.pose.orientation.w = msg.angular.y;
    ROS_INFO("Sending goal");
    ac.sendGoal(goal);
    ac.waitForResult();

    if(ac.getState() == actionlib::SimpleClientGoalState::SUCCEEDED)
        ROS_INFO("Yeah!!! Goal Reach!!!");
    else
        ROS_INFO("Sorry, we Failed!");
}


int main(int argc, char** argv){
    ros::init(argc, argv, "subscribe_goal");
    // 创建节点句柄
    ros::NodeHandle n;
    // 创建一个Subscriber，订阅名为/subscribe_goal，注册回调函数GoalCallback
    ros::Subscriber goal_sub = n.subscribe("/subscribe_goal", 10, GoalCallback);
    ROS_INFO("Subscribe /subscribe_goal successfully!");
    // call the callback
    ros::spin();
    return 0;

}
