package org.athenian.core;

import com.beust.jcommander.Parameter;
import com.google.common.collect.Iterables;
import org.athenian.RosBridge;
import org.athenian.common.BaseOptions;

import java.util.Collections;
import java.util.List;

public class RosBridgeOptions
    extends BaseOptions {

  @Parameter(names = {"-p", "--port"}, description = "Listen port")
  private Integer port = 50051;

  public RosBridgeOptions(final List<String> args) {
    this(Iterables.toArray(args != null ? args : Collections.emptyList(), String.class));
  }

  public RosBridgeOptions(final String[] argv) {
    super(RosBridge.class.getSimpleName(), argv);
  }

  public int getPort() { return this.port; }
}