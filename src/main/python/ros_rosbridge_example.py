import logging
import rospy
from arc852.utils import setup_logging
from geometry_msgs.msg import Twist

from rosbridge_client import RosBridgeClient

logger = logging.getLogger(__name__)
setup_logging()


def main():
    # Define callback for Twist msgs
    def onTwistMsg(msg):
        global bridge
        twist_data = RosBridgeClient.newTwistData(msg.data.linear.x, msg.data.angular.z)
        bridge.stream_twist(twist_data)

    # Connect to bridge
    bridge = RosBridgeClient(hostname="localhost:50051",
                             log_info=rospy.loginfo,
                             log_debug=rospy.logdebug,
                             log_error=rospy.legerr)

    # Subscribe to the /cmd_vel topic
    rospy.init_node('ros_rosbridge_example')
    rospy.Subscriber('cmd_vel', Twist, onTwistMsg)
    rospy.loginfo("Listening to /cmd_vel...")

    # Wait for msgs
    rospy.spin()


if __name__ == "__main__":
    main()
