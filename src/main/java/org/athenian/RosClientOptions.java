package org.athenian;

import com.beust.jcommander.Parameter;
import com.google.common.collect.Iterables;
import org.athenian.common.BaseOptions;

import java.util.Collections;
import java.util.List;

public class RosClientOptions
    extends BaseOptions {

  @Parameter(names = {"-h", "--hostname"}, description = "Host name")
  private String hostname = "localhost";

  public RosClientOptions(final List<String> args) {
    this(Iterables.toArray(args != null ? args : Collections.emptyList(), String.class));
  }

  public RosClientOptions(final String[] argv) {
    super(RosClient.class.getName(), argv);
  }

  public String getHostname() { return this.hostname; }
}