import org.assertj.core.api.Assertions;
import org.athenian.RosBridge;
import org.athenian.RosClient;
import org.athenian.common.Utils;
import org.athenian.core.RosBridgeOptions;
import org.athenian.core.RosClientOptions;
import org.athenian.core.TwistDataStream;
import org.athenian.grpc.TwistData;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.concurrent.TimeUnit.SECONDS;

public class BridgeTest {

  private static final Logger     logger     = LoggerFactory.getLogger(BridgeTest.class);
  private static final AtomicLong COUNTER    = new AtomicLong();
  private static final String[]   EMPTY_ARGV = {};

  private static RosBridge ROSBRIDGE = null;

  @BeforeClass
  public static void setUp() {
    final RosBridgeOptions options = new RosBridgeOptions(EMPTY_ARGV);
    ROSBRIDGE = new RosBridge(options.getPort(), null, data -> COUNTER.incrementAndGet());
    ROSBRIDGE.startAsync();
  }

  @AfterClass
  public static void takeDown()
      throws TimeoutException {
    ROSBRIDGE.stopAsync();
    ROSBRIDGE.awaitTerminated(5, SECONDS);
  }

  @Test
  public void blockingTest() {
    final RosClientOptions options = new RosClientOptions(EMPTY_ARGV);
    final RosClient rosClient = new RosClient(options, null);
    long start = COUNTER.get();
    final int count = 1000;
    for (int i = 0; i < count; i++) {
      TwistData data = TwistData.newBuilder()
                                .setLinearX(i)
                                .setLinearY(i + 1)
                                .setLinearZ(i + 2)
                                .setAngularX(i + 3)
                                .setAngularY(i + 4)
                                .setAngularZ(i + 5)
                                .build();
      rosClient.writeData(data);
    }

    Utils.sleepForSecs(3);
    Assertions.assertThat(start + count == COUNTER.get());
  }

  @Test
  public void streamingTest() {
    final RosClientOptions options = new RosClientOptions(EMPTY_ARGV);
    final RosClient rosClient = new RosClient(options, null);
    long start = COUNTER.get();
    final int count = 1000;

    try (final TwistDataStream stream = rosClient.newTwistDataStream()) {
      for (int i = 0; i < count; i++) {
        TwistData data = TwistData.newBuilder()
                                  .setLinearX(i)
                                  .setLinearY(i + 1)
                                  .setLinearZ(i + 2)
                                  .setAngularX(i + 3)
                                  .setAngularY(i + 4)
                                  .setAngularZ(i + 5)
                                  .build();
        stream.writeData(data);
      }
    }

    Utils.sleepForSecs(3);
    Assertions.assertThat(start + count == COUNTER.get());
  }
}
