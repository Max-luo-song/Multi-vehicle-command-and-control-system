export DISPLAY=:0
#echo "123456" | sudo -S chmod a+rw /dev/ttyUSB0
#sudo chmod a+rw /dev/ttyACM0
source ~/cv_bridge/install/setup.bash
if [ -n "$1" ]
then
    roslaunch publish_pose total.launch address:=$1 port:=$2 map_file:=$HOME/map/$3.yaml
else
    echo "You need two param:first is IP, second is port, third is map_file"
fi
