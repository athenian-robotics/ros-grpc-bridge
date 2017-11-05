package org.athenian.common;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

public abstract class GenericService
    extends AbstractExecutionThreadService
    implements Closeable {

  private static final Logger logger = LoggerFactory.getLogger(GenericService.class);

  private final List<Service> services = Lists.newArrayList(this);

  private ServiceManager serviceManager = null;

  protected GenericService() {
    this.addListener(new GenericServiceListener(this), MoreExecutors.directExecutor());
  }

  public void init() {
    this.serviceManager = new ServiceManager(this.services);
    this.serviceManager.addListener(this.newListener());
  }

  @Override
  protected void startUp()
      throws Exception {
    super.startUp();
    Runtime.getRuntime().addShutdownHook(Utils.shutDownHookAction(this));
  }

  @Override
  public void close()
      throws IOException {
    this.stopAsync();
  }

  protected void addService(final Service service) { this.services.add(service); }

  protected void addServices(final Service service, final Service... services) {
    this.services.addAll(Lists.asList(service, services));
  }

  protected ServiceManager.Listener newListener() {
    final String serviceName = this.getClass().getSimpleName();
    return new ServiceManager.Listener() {
      @Override
      public void healthy() { logger.info("All {} services healthy", serviceName); }

      @Override
      public void stopped() { logger.info("All {} services stopped", serviceName); }

      @Override
      public void failure(final Service service) { logger.info("{} service failed: {}", serviceName, service); }
    };
  }

}
