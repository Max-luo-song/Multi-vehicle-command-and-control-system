/*
	Description: subscribe the goal data and navigation to the position.
*/

#include <ros/ros.h>
#include <move_base_msgs/MoveBaseAction.h>
#include <actionlib/client/simple_action_client.h>
#include "subscribe_goal_jd/Subscribe_goal_jd.h"
#include <thread>
#include <std_msgs/Int32.h>

typedef actionlib::SimpleActionClient<move_base_msgs::MoveBaseAction> MoveBaseClient;

ros::Publisher action_state_number_pub;
std_msgs::Int32 action_state_number_msg;


void GoalCallback(const subscribe_goal_jd::Subscribe_goal_jd& msg) {
    action_state_number_msg.data = 4;
    int target_point_num = msg.target_point_number;
    //tell the action client that we want to spin a thread by default
    MoveBaseClient ac("move_base", true);
    //wait for the action server to come up
    while(!ac.waitForServer(ros::Duration(5.0))){
        ROS_INFO("Waiting for the move_base action server to come up");
    }
    int task_state = 0; // if task_state = 1:failed taskstate = 0:success
    // 使用前两个室内点作为前往的关键点，第三个室内点为目标点，后两个室内点为返回的关键点，返回点为返回的目标
    move_base_msgs::MoveBaseGoal goal;  //用来传递我们导航去的目标信息
    for (int i=0; i<target_point_num; i++) {
        goal.target_pose.header.frame_id = "map";
        goal.target_pose.header.stamp = ros::Time::now();
        
        goal.target_pose.pose.position.x = msg.position_x[i];
        goal.target_pose.pose.position.y = msg.position_y[i];
        goal.target_pose.pose.position.z = 0.0;
        goal.target_pose.pose.orientation.x = 0.0;
        goal.target_pose.pose.orientation.y = 0.0;
        goal.target_pose.pose.orientation.z = msg.orientation_z[i];
        goal.target_pose.pose.orientation.w = msg.orientation_w[i];
	
        ac.sendGoal(goal);
        
        ac.waitForResult();
        
        if(ac.getState() == actionlib::SimpleClientGoalState::SUCCEEDED) {
            ROS_INFO("Goal  Mission complete!");
	    }
        else {
            ROS_INFO("Goal  Mission failed ...");
	        task_state = 1;
	    }
    }
    if (task_state == 0) {
	    ROS_INFO("Finish all goals!!!");
        action_state_number_msg.data = 3;
    }
    else {
	    ROS_INFO("There are some tasks failed!!!");
    }
    
}

void publishThread() {
    ros::NodeHandle nh;
    action_state_number_pub = nh.advertise<std_msgs::Int32>("/action_state_number", 1000);
    action_state_number_msg.data = 0;
    ros::Rate loop_rate(10);
    while(ros::ok()) {
        action_state_number_pub.publish(action_state_number_msg);
	    loop_rate.sleep();
    }
}

void subscribeThread() {
    ros::NodeHandle nh;
    // 创建一个Subscriber，订阅名为/subscribe_goal_jd，注册回调函数GoalCallback
    ros::Subscriber goal_sub = nh.subscribe("/subscribe_goal_jd", 10, GoalCallback);
    ROS_INFO("Subscribe /subscribe_goal_jd successfully!");
    ros::spin();
}

int main(int argc, char** argv){
    ros::init(argc, argv, "subscribe_goal_jd");
    ROS_INFO("node subscribe_goal_jd launch successfully!!!");
    // 创建两个线程，一个用于发布，一个用于订阅
    std::thread publishThreadObj(publishThread);
    std::thread subscribeThreadObj(subscribeThread);
    // 等待线程完成
    publishThreadObj.join();
    subscribeThreadObj.join();

    return 0;

}
