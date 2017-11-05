package org.athenian;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Strings.isNullOrEmpty;


public class RosBridge {

  private static final Logger logger = LoggerFactory.getLogger(RosBridge.class);

  private final RosBridgeService grpcService;

  public RosBridge(final int proxyPort,
                   final String inProcessServerName) {
    this.grpcService = isNullOrEmpty(inProcessServerName) ? RosBridgeService.create(this, proxyPort)
                                                          : RosBridgeService.create(this, inProcessServerName);
  }
}
