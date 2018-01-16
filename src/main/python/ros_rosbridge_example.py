import logging
import rospy
from geometry_msgs.msg import Twist

from rosbridge_client import RosBridgeClient

logger = logging.getLogger(__name__)


# Define callback for Twist msgs
def onTwistMsg(msg):
    global bridge
    twist_data = RosBridgeClient.newTwistData(msg.data.linear.x, msg.data.angular.z)
    bridge.stream_twist(twist_data)


if __name__ == "__main__":
    # Connect to bridge
    bridge = RosBridgeClient("localhost:50051")

    # Subscribe to the /cmd_vel topic
    rospy.init_node('ros_rosbridge_example')
    rospy.Subscriber('cmd_vel', Twist, onTwistMsg)
    print("Listening to /cmd_vel...")

    # Wait for msgs
    rospy.spin()
