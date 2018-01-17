import logging
import time

from rosbridge_client import RosBridgeClient

logger = logging.getLogger(__name__)

if __name__ == "__main__":

    bridge = RosBridgeClient("localhost:50051")

    count = 10000


    def twist_gen(cnt):
        for i in range(cnt):
            twist_data = RosBridgeClient.newTwistData(i, i + 2)
            # print("Streaming data")
            yield twist_data


    start1 = time.time()
    bridge.stream_twist(twist_gen(count))
    end1 = time.time()
    print("Stream elapsed: " + str(end1 - start1))

    start2 = time.time()
    for i in range(count):
        twist_data = RosBridgeClient.newTwistData(i, i + 3)
        # print("Sending data")
        bridge.report_twist(twist_data)
    end2 = time.time()
    print("Nonstream elapsed: " + str(end2 - start2))

    time.sleep(10)
