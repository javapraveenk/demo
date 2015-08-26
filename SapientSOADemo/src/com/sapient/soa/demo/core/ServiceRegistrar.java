package com.sapient.soa.demo.core;

import java.io.Closeable;

public interface ServiceRegistrar extends Closeable {
  void registerService() throws Exception;  
}
