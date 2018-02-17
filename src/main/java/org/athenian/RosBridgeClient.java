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

import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.athenian.common.Utils;
import org.athenian.core.RosBridgeClientOptions;
import org.athenian.core.TwistValueStream;
import org.athenian.grpc.CommandValue;
import org.athenian.grpc.EncoderValue;
import org.athenian.grpc.RosBridgeServiceGrpc.RosBridgeServiceBlockingStub;
import org.athenian.grpc.RosBridgeServiceGrpc.RosBridgeServiceStub;
import org.athenian.grpc.TwistValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

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

  public static void main(final String[] argv)
      throws InterruptedException {
    final RosBridgeClient client = RosBridgeClient.newClient(new RosBridgeClientOptions(argv));

    for (int i = 0; i < REPEAT; i++) {
      for (int j = 0; j < COUNT; j++) {
        TwistValue value = TwistValue.newBuilder()
                                     .setLinearX(j)
                                     .setLinearY(j + 1)
                                     .setLinearZ(j + 2)
                                     .setAngularX(j + 3)
                                     .setAngularY(j + 4)
                                     .setAngularZ(j + 5)
                                     .build();
        client.writeTwistValue(value);
      }

      try (final TwistValueStream stream = client.newTwistValueStream()) {
        for (int j = 0; j < COUNT; j++) {
          TwistValue value = TwistValue.newBuilder()
                                       .setLinearX(j)
                                       .setLinearY(j + 1)
                                       .setLinearZ(j + 2)
                                       .setAngularX(j + 3)
                                       .setAngularY(j + 4)
                                       .setAngularZ(j + 5)
                                       .build();
          stream.writeTwistValue(value);
        }
      }
      Utils.sleepForSecs(1);

      final Iterator<EncoderValue> encoder_iter = client.encoderValues("wheel1");
      while (encoder_iter.hasNext()) {
        EncoderValue encoderValue = encoder_iter.next();
        logger.info("Read encoder value: " + encoderValue.getValue());
      }

      final CountDownLatch encoderLatch =
          client.encoderValues("wheel1",
                               encoderValue -> logger.info("Read encoder value: " + encoderValue.getValue()));
      encoderLatch.await();
      Utils.sleepForSecs(1);

      final Iterator<CommandValue> iter = client.commandValues();
      while (iter.hasNext()) {
        CommandValue commandValue = iter.next();
        logger.info("Read command: " + commandValue.getCommand());
        logger.info("Read command arg: " + commandValue.getCommandArg());
      }

      final CountDownLatch commandLatch =
          client.commandValues(
              commandValue -> {
                logger.info("Read command: " + commandValue.getCommand());
                logger.info("Read command arg: " + commandValue.getCommandArg());
              });
      commandLatch.await();
      Utils.sleepForSecs(1);
    }
  }

  public void writeTwistValue(final TwistValue value) {
    this.getBlockingStub().writeTwistValue(value);
  }

  public Iterator<EncoderValue> encoderValues(final String encoderName) {
    final StringValue encoderDesc = StringValue.newBuilder().setValue(encoderName).build();
    return this.getBlockingStub().readEncoderValues(encoderDesc);
  }

  public CountDownLatch encoderValues(final String encoderName,
                                      final Consumer<EncoderValue> onMessageAction) {
    final CountDownLatch completedLatch = new CountDownLatch(1);
    final StringValue encoderDesc = StringValue.newBuilder().setValue(encoderName).build();
    final StreamObserver<EncoderValue> observer = new StreamObserver<EncoderValue>() {
      @Override
      public void onNext(EncoderValue value) {
        if (onMessageAction != null)
          onMessageAction.accept(value);
      }

      @Override
      public void onError(Throwable t) {
        final Status status = Status.fromThrowable(t);
        if (status != Status.CANCELLED)
          logger.error("Error in asyncEncoderValues(): {}", status);
        this.onCompleted();
      }

      @Override
      public void onCompleted() {
        completedLatch.countDown();
      }
    };
    this.getNonBlockingStub().readEncoderValues(encoderDesc, observer);
    return completedLatch;
  }

  public Iterator<CommandValue> commandValues() {
    return this.getBlockingStub().readCommandValues(Empty.getDefaultInstance());
  }

  public CountDownLatch commandValues(final Consumer<CommandValue> onMessageAction) {
    final CountDownLatch completedLatch = new CountDownLatch(1);
    final StreamObserver<CommandValue> observer = new StreamObserver<CommandValue>() {
      @Override
      public void onNext(CommandValue value) {
        if (onMessageAction != null)
          onMessageAction.accept(value);
      }

      @Override
      public void onError(Throwable t) {
        final Status status = Status.fromThrowable(t);
        if (status != Status.CANCELLED)
          logger.error("Error in asyncEncoderValues(): {}", status);
        this.onCompleted();
      }

      @Override
      public void onCompleted() {
        completedLatch.countDown();
      }
    };
    this.getNonBlockingStub().readCommandValues(Empty.getDefaultInstance(), observer);
    return completedLatch;
  }

  public TwistValueStream newTwistValueStream() {
    return new TwistValueStream(this.getNonBlockingStub());
  }

  private ManagedChannel getChannel() { return this.channelRef.get(); }

  private RosBridgeServiceBlockingStub getBlockingStub() { return this.blockingStubRef.get(); }

  private RosBridgeServiceStub getNonBlockingStub() { return this.asyncStubRef.get(); }
}
