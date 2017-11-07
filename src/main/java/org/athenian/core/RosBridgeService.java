package org.athenian.core;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.MoreExecutors;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import org.athenian.RosBridge;
import org.athenian.common.GenericServiceListener;
import org.athenian.grpc.TwistData;
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

  private RosBridgeService(final RosBridge rosBridge,
                           final int port,
                           final String serverName,
                           final Consumer<TwistData> action) {
    this.port = port;
    this.serverName = serverName;
    this.inProcessServer = !isNullOrEmpty(serverName);

    final RosBridgeServiceImpl rosBridgeService = new RosBridgeServiceImpl(rosBridge, action);
    this.grpcServer = this.inProcessServer ? InProcessServerBuilder.forName(this.serverName)
                                                                   .addService(rosBridgeService)
                                                                   .build()
                                           : ServerBuilder.forPort(this.port)
                                                          .addService(rosBridgeService)
                                                          .build();
    this.addListener(new GenericServiceListener(this), MoreExecutors.directExecutor());
  }

  public static RosBridgeService create(final RosBridge rosBridge,
                                        final int grpcPort,
                                        final Consumer<TwistData> action) {
    return new RosBridgeService(rosBridge, grpcPort, null, action);
  }

  public static RosBridgeService create(final RosBridge rosBridge,
                                        final String serverName,
                                        final Consumer<TwistData> action) {
    return new RosBridgeService(rosBridge, -1, Preconditions.checkNotNull(serverName), action);
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
