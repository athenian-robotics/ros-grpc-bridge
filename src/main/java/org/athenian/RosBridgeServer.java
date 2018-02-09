/*
 * Copyright © 2018 The Athenian School - http://www.athenian.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.athenian;

import com.google.common.base.MoreObjects;
import org.athenian.common.GenericService;
import org.athenian.common.Utils;
import org.athenian.core.RosBridgeServerOptions;
import org.athenian.core.RosBridgeService;
import org.athenian.grpc.TwistData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

import static com.google.common.base.Strings.isNullOrEmpty;

public class RosBridgeServer
    extends GenericService {

  private static final Logger logger = LoggerFactory.getLogger(RosBridgeServer.class);

  private final int              port;
  private final RosBridgeService grpcService;

  private RosBridgeServer(final int port,
                          final String inProcessName,
                          final Consumer<TwistData> onMessageAction) {
    this.port = port;
    this.grpcService = isNullOrEmpty(inProcessName) ? RosBridgeService.newService(this, port, onMessageAction)
                                                    : RosBridgeService.newService(this, inProcessName, onMessageAction);
    this.init();
  }

  public static RosBridgeServer newServer(final RosBridgeServerOptions options,
                                          final Consumer<TwistData> onMessageAction) {
    options.init();
    return new RosBridgeServer(options.getPort(), null, onMessageAction);
  }

  public static RosBridgeServer newServer(final String inProcessName, final Consumer<TwistData> onMessageAction) {
    return new RosBridgeServer(-1, inProcessName, onMessageAction);
  }

  public static void main(final String[] argv) {
    logger.info(Utils.getBanner("banners/bridge.txt"));
    final RosBridgeServerOptions options = new RosBridgeServerOptions(argv);
    final RosBridgeServer rosBridgeServer = newServer(options,
                                                      data -> logger.info("Got data {}", data));
    rosBridgeServer.startAsync();

    Utils.sleepForSecs(Integer.MAX_VALUE);
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
                      .add("port", this.port)
                      .toString();
  }
}
