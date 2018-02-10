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

import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.netty.NettyChannelBuilder;
import org.athenian.common.Utils;
import org.athenian.core.RosBridgeClientOptions;
import org.athenian.core.TwistDataStream;
import org.athenian.grpc.EncoderData;
import org.athenian.grpc.EncoderDesc;
import org.athenian.grpc.RosBridgeServiceGrpc.RosBridgeServiceBlockingStub;
import org.athenian.grpc.RosBridgeServiceGrpc.RosBridgeServiceStub;
import org.athenian.grpc.TwistData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.base.Strings.isNullOrEmpty;
import static io.grpc.ClientInterceptors.intercept;
import static org.athenian.grpc.RosBridgeServiceGrpc.newBlockingStub;
import static org.athenian.grpc.RosBridgeServiceGrpc.newStub;

public class RosBridgeClient {
  private static final Logger logger = LoggerFactory.getLogger(RosBridgeClient.class);
  public static final  int    REPEAT = 100;
  public static final  int    COUNT  = 200;

  private final AtomicReference<ManagedChannel>               channelRef      = new AtomicReference<>();
  private final AtomicReference<RosBridgeServiceBlockingStub> blockingStubRef = new AtomicReference<>();
  private final AtomicReference<RosBridgeServiceStub>         asyncStubRef    = new AtomicReference<>();

  private final String hostname;
  private final int    port;

  private RosBridgeClient(final String hostname, final String inProcessName) {
    if (hostname.contains(":")) {
      String[] vals = hostname.split(":");
      this.hostname = vals[0];
      this.port = Integer.valueOf(vals[1]);
    }
    else {
      this.hostname = hostname;
      this.port = 50051;
    }

    this.channelRef.set(isNullOrEmpty(inProcessName) ? NettyChannelBuilder.forAddress(this.hostname, this.port)
                                                                          .usePlaintext(true)
                                                                          .build()
                                                     : InProcessChannelBuilder.forName(inProcessName)
                                                                              .usePlaintext(true)
                                                                              .build());
    logger.info("Creating gRPC stubs");
    this.blockingStubRef.set(newBlockingStub(intercept(this.getChannel())));
    this.asyncStubRef.set(newStub(intercept(this.getChannel())));
  }

  public static RosBridgeClient newClient() {
    final RosBridgeClientOptions options = new RosBridgeClientOptions(new String[] {});
    return new RosBridgeClient(options.getHostname(), "");
  }

  public static RosBridgeClient newClient(final String inProcessName) {
    final RosBridgeClientOptions options = new RosBridgeClientOptions(new String[] {});
    return new RosBridgeClient(options.getHostname(), inProcessName);
  }

  public static RosBridgeClient newClient(final RosBridgeClientOptions options) {
    return new RosBridgeClient(options.getHostname(), "");
  }

  public static RosBridgeClient newClient(final RosBridgeClientOptions options, final String inProcessName) {
    return new RosBridgeClient(options.getHostname(), inProcessName);
  }

  public static void main(final String[] argv) {
    final RosBridgeClient client = RosBridgeClient.newClient(new RosBridgeClientOptions(argv));

    for (int i = 0; i < REPEAT; i++) {
      for (int j = 0; j < COUNT; j++) {
        TwistData data = TwistData.newBuilder()
                                  .setLinearX(j)
                                  .setLinearY(j + 1)
                                  .setLinearZ(j + 2)
                                  .setAngularX(j + 3)
                                  .setAngularY(j + 4)
                                  .setAngularZ(j + 5)
                                  .build();
        client.writeTwistData(data);
      }

      try (final TwistDataStream stream = client.newTwistDataStream()) {
        for (int j = 0; j < COUNT; j++) {
          TwistData data = TwistData.newBuilder()
                                    .setLinearX(j)
                                    .setLinearY(j + 1)
                                    .setLinearZ(j + 2)
                                    .setAngularX(j + 3)
                                    .setAngularY(j + 4)
                                    .setAngularZ(j + 5)
                                    .build();
          stream.writeTwistData(data);
        }
      }

      Utils.sleepForSecs(1);

      final Iterator<EncoderData> encoderDataIter = client.readEncoderData("wheel1");
      while (encoderDataIter.hasNext()) {
        EncoderData encoderData = encoderDataIter.next();
        logger.info("Read encoder value: " + encoderData.getValue());
      }

      Utils.sleepForSecs(1);
    }
  }

  public void writeTwistData(final TwistData data) {
    this.getBlockingStub().writeTwistData(data);
  }

  public Iterator<EncoderData> readEncoderData(final String encoderName) {
    final EncoderDesc encoderDesc = EncoderDesc.newBuilder().setName(encoderName).build();
    return this.getBlockingStub().readEncoderData(encoderDesc);
  }

  public TwistDataStream newTwistDataStream() {
    return new TwistDataStream(this.getAsyncStub());
  }

  private ManagedChannel getChannel() { return this.channelRef.get(); }

  private RosBridgeServiceBlockingStub getBlockingStub() { return this.blockingStubRef.get(); }

  private RosBridgeServiceStub getAsyncStub() { return this.asyncStubRef.get(); }
}
