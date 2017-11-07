package org.athenian;

import com.google.common.base.MoreObjects;
import org.athenian.common.GenericService;
import org.athenian.common.Utils;
import org.athenian.core.RosBridgeOptions;
import org.athenian.core.RosBridgeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Strings.isNullOrEmpty;


public class RosBridge
    extends GenericService {

  private static final Logger logger = LoggerFactory.getLogger(RosBridge.class);

  private final RosBridgeService grpcService;

  public RosBridge(final RosBridgeOptions options,
                   final int port,
                   final String inProcessServerName) {
    this.grpcService = isNullOrEmpty(inProcessServerName) ? RosBridgeService.create(this, port)
                                                          : RosBridgeService.create(this, inProcessServerName);
    this.init();
  }

  public static void main(final String[] argv) {
    logger.info(Utils.getBanner("banners/bridge.txt"));
    final RosBridgeOptions options = new RosBridgeOptions(argv);
    final RosBridge rosBridge = new RosBridge(options, options.getPort(), null);
    rosBridge.startAsync();
  }

  @Override
  protected void startUp()
      throws Exception {
    super.startUp();
    this.grpcService.startAsync();
  }

  @Override
  protected void shutDown()
      throws Exception {
    this.grpcService.stopAsync();
    super.shutDown();
  }

  @Override
  protected void run() {
    while (this.isRunning()) {
      Utils.sleepForMillis(500);
    }
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
                      .add("port", this.grpcService.getPort())
                      .toString();
  }
}
