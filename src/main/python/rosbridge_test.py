import logging
import time
from arc852.utils import setup_logging

from rosbridge_client import RosBridgeClient

logger = logging.getLogger(__name__)
setup_logging()

def main():
    bridge = RosBridgeClient("localhost:50051")

    count = 100

    # Non-streaming version
    start1 = time.time()
    for i in range(count):
        twist_data = RosBridgeClient.newTwistData(i, i + 3)
        bridge.write_twist(twist_data)
    end1 = time.time()
    logger.info("Non-streaming elapsed: " + str(end1 - start1))

    # Streaming version
    def twist_gen(cnt):
        for i in range(cnt):
            twist_data = RosBridgeClient.newTwistData(i, i + 2)
            yield twist_data

    start2 = time.time()
    bridge.stream_twist(twist_gen(count))
    end2 = time.time()
    logger.info("Streaming elapsed: " + str(end2 - start2))

    time.sleep(2)

    # Read streming encoder values
    for data in bridge.read_encoder("wheel2"):
        logger.info("Read encoder value: " + str(data.value))


if __name__ == "__main__":
    main()
