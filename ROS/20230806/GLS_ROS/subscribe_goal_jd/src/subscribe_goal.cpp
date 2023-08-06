/*
	Description: subscribe the goal data and navigation to the position.
*/

#include <ros/ros.h>
#include <move_base_msgs/MoveBaseAction.h>
#include <actionlib/client/simple_action_client.h>
#include "subscribe_goal/Subscribe_goal.h"
#include "subscribe_goal/Action_state_number.h"
#include <thread>

typedef actionlib::SimpleActionClient<move_base_msgs::MoveBaseAction> MoveBaseClient;

ros::Publisher action_state_number_pub;
subscribe_goal::Action_state_number action_state_number_msg;


void GoalCallback(const subscribe_goal::Subscribe_goal& msg) {
    action_state_number_msg.action_state = 4;
    action_state_number_msg.action_number = msg.action_number;
    int target_point_num = msg.target_point_number;
    //tell the action client that we want to spin a thread by default
    MoveBaseClient ac("move_base", true);
    //wait for the action server to come up
    while(!ac.waitForServer(ros::Duration(5.0))){
        ROS_INFO("Waiting for the move_base action server to come up");
    }
    // 传过来的点包括12个数据，开始点(1)   返回点(1)   室内点(5)  室外点(5)
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
        action_state_number_msg.action_state = 3;
    }
    else {
	ROS_INFO("There are some tasks failed!!!");
    }
    
}

void publishThread() {
    ros::NodeHandle nh;
    action_state_number_pub = nh.advertise<subscribe_goal::Action_state_number>("/action_state_number", 1000);
    action_state_number_msg.action_state = 0;
    action_state_number_msg.action_number = -1;
    ros::Rate loop_rate(10);
    while(ros::ok()) {
        action_state_number_pub.publish(action_state_number_msg);
	loop_rate.sleep();
    }
}

void subscribeThread() {
    MoveBaseClient ac("move_base", true);
    ros::NodeHandle nh;
    // 创建一个Subscriber，订阅名为/subscribe_goal，注册回调函数GoalCallback
    ros::Subscriber goal_sub = nh.subscribe("/subscribe_goal", 10, GoalCallback);
    ROS_INFO("Subscribe /subscribe_goal successfully!");
    ros::spin();
}

int main(int argc, char** argv){
    ros::init(argc, argv, "subscribe_goal");
    // 创建两个线程，一个用于发布，一个用于订阅
    std::thread publishThreadObj(publishThread);
    std::thread subscribeThreadObj(subscribeThread);
    // 等待线程完成
    publishThreadObj.join();
    subscribeThreadObj.join();

    return 0;

}
