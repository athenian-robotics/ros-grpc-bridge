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

package org.athenian.core;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.MoreExecutors;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import org.athenian.RosBridgeServer;
import org.athenian.common.GenericServiceListener;
import org.athenian.grpc.TwistValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.function.Consumer;

import static com.google.common.base.Strings.isNullOrEmpty;

public class RosBridgeService
    extends AbstractIdleService {

  private static final Logger logger = LoggerFactory.getLogger(RosBridgeService.class);

  private final String  serverName;
  private final int     port;
  private final boolean inProcessServer;
  private final Server  grpcServer;

  private RosBridgeService(final RosBridgeServer rosBridgeServer,
                           final int port,
                           final String serverName,
                           final Consumer<TwistValue> onMessageAction) {
    this.port = port;
    this.serverName = serverName;
    this.inProcessServer = !isNullOrEmpty(serverName);

    final RosBridgeServiceImpl rosBridgeService = new RosBridgeServiceImpl(rosBridgeServer, onMessageAction);
    this.grpcServer = this.inProcessServer ? InProcessServerBuilder.forName(this.serverName)
                                                                   .addService(rosBridgeService)
                                                                   .build()
                                           : ServerBuilder.forPort(this.port)
                                                          .addService(rosBridgeService)
                                                          .build();
    this.addListener(new GenericServiceListener(this), MoreExecutors.directExecutor());
  }

  public static RosBridgeService newService(final RosBridgeServer rosBridgeServer,
                                            final int grpcPort,
                                            final Consumer<TwistValue> onMessageAction) {
    return new RosBridgeService(rosBridgeServer, grpcPort, null, onMessageAction);
  }

  public static RosBridgeService newService(final RosBridgeServer rosBridgeServer,
                                            final String serverName,
                                            final Consumer<TwistValue> onMessageAction) {
    return new RosBridgeService(rosBridgeServer, -1, Preconditions.checkNotNull(serverName), onMessageAction);
  }

  @Override
  protected void startUp()
      throws IOException {
    this.grpcServer.start();
  }

  @Override
  protected void shutDown() { this.grpcServer.shutdown(); }

  public int getPort() { return this.grpcServer.getPort(); }

  @Override
  public String toString() {
    final MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper(this);
    if (this.inProcessServer) {
      helper.add("serverType", "InProcess");
      helper.add("serverName", this.serverName);
    }
    else {
      helper.add("serverType", "Netty");
      helper.add("port", this.port);
    }
    return helper.toString();
  }
}
