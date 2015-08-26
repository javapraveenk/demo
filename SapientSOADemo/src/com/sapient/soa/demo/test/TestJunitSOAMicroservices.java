package com.sapient.soa.demo.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.curator.test.TestingServer;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.grizzly2.servlet.GrizzlyWebContainerFactory;
import org.glassfish.jersey.test.spi.TestContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.WebApplicationInitializer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.sapient.soa.demo.rest.resource.PriceApplicationInitializer;
import com.sapient.soa.demo.rest.resource.ProductsApplicationInitializer;
import com.sapient.soa.demo.vo.Product;
import com.sapient.soa.demo.vo.ProductResult;


public class TestJunitSOAMicroservices {
  private TestingServer testServer;
  private ProductsServer longRunningProductsServer;

  

  private static final int MAX_SERVERS = 2;
 
  @Before
  public void setUp() throws Exception {
  File tempDir = Files.createTempDir();	
	//  File tempDir = new File("C://testDir");
	  testServer = new TestingServer(2181,tempDir);  
  }  

  @After
  public void tearDown() throws IOException {
    testServer.close();   
    if (longRunningProductsServer != null) {
    	longRunningProductsServer.interrupt();
    }

  }

  @Test
  public void integration() throws Exception {
    CyclicBarrier barrier = new CyclicBarrier(MAX_SERVERS + 1);
    
   /* int cnt = 1;
    for (Integer port : ports()) {
      new ProductsServer(port, barrier).start();
      if(cnt == 1){
    	  Thread.sleep(10000);
      }else{
    	  Thread.sleep(20000);
      }
      cnt++;
    }   
    */
    longRunningProductsServer = new ProductsServer(9210, 500000L, null);
    longRunningProductsServer.start();
    
 //  barrier.await();
    
    ProductsClient productsClient = new ProductsClientImpl(testServer.getConnectString());
    List<ProductResult> productResults = Lists.newArrayList();
    
   
    
    
    int i = 1;
    int maxProducts = 1000;
    
    while (i < maxProducts) {
      try {
        Product product = new Product(Integer.valueOf(i).toString(), "productContent" + i,"product" + i,"type"+i,Long.valueOf(10000+i));
        ProductResult result = productsClient.add(product);
        result.setProduct(product);
        productResults.add(result);      
        i++;
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }   
    
    writeProductDataInExcel(productResults);
    
    Set<String> usedInstances = productsClient.getServiceInstancesUsed();
    for (Iterator iterator = usedInstances.iterator(); iterator.hasNext();) {
		String strng = (String) iterator.next();
		System.out.println("Instance url:"+strng);
		
	}
    
 //   assertEquals(MAX_SERVERS +1, productsClient.getServiceInstancesUsed().size());
    
    Product p = productsClient.get(Long.valueOf(530));
    assertEquals(Long.valueOf(10530),p .getProductPrice());
   
    
  }
  
  
  private static void writeProductDataInExcel(List<ProductResult> result){
	  HSSFWorkbook workbook = new HSSFWorkbook();
	  HSSFSheet sheet = workbook.createSheet("productData");
	  int rownum = 0;
	  for (Iterator iterator = result.iterator(); iterator.hasNext();) {
		  ProductResult productResult = (ProductResult) iterator.next();
			Long productId = productResult.getProductId();
			Long price = productResult.getProduct().getProductPrice();
	      Row row = sheet.createRow(rownum++);
	      int cellnum = 0;
	          Cell cell = row.createCell(0);
	          if(productId instanceof Long)
	              cell.setCellValue(productId);
	          
	          Cell cell1 = row.createCell(1);
	          if(price instanceof Long)
	              cell1.setCellValue(price);
	  }
	   
	  try {
	      FileOutputStream out = 
	              new FileOutputStream(new File("testProductData.xls"));
	      workbook.write(out);
	      out.close();
	      System.out.println("Excel written successfully..");
	       
	  } catch (FileNotFoundException e) {
	      e.printStackTrace();
	  } catch (IOException e) {
	      e.printStackTrace();
	  }
  }
  
  
 
  
  

  
      
  private static Set<Integer> ports() {

    Set<Integer> ports = Sets.newHashSet();

    while (ports.size() < MAX_SERVERS) {
      int port = ThreadLocalRandom.current().nextInt(9091, 9200);
      ports.add(port);
    }

    return ports;
  }

  private static class ProductsServer extends Thread {
    private static final Logger LOG = Logger.getLogger(ProductsServer.class);
    private int port;
    private long sleepTime;
    private final CyclicBarrier barrier;
   
    public ProductsServer(int port, long sleepTime, CyclicBarrier barrier) {
      this.port = port;    
      this.sleepTime = sleepTime;
      this.barrier = barrier;
    }
    
    public ProductsServer(int port, CyclicBarrier barrier) {
      this(port, -1, barrier);
    }

    public void run() {
      TestContainer container = null;
      try {
        // Set the property of port
        System.setProperty("port", String.valueOf(port));
        
        // Local URL
        URI uri = new URI("http://0.0.0.0:" + port + "/");
        
        // Start container
        container = new SimpleTestContainer(uri, new ProductsApplicationInitializer());
        container.start();
        
        if (barrier != null) {
          barrier.await();
        }
        
        // Sleep for provided time before dying
        long serverSleepTime = sleepTime == -1 ? ThreadLocalRandom.current().nextLong(5000, 20000)
          : sleepTime;
        Thread.sleep(serverSleepTime);
      }
      catch (InterruptedException e) {
        LOG.info("Interrupted Server");
      }
      catch (Exception e) {
        LOG.error("WTF", e);
      }
      finally {
        if (container != null) {
          container.stop();
        }
        LOG.info("Server on Port:" + port + " exiting");
      }
    }
  }
  

  
  

  private static final class SimpleTestContainer implements TestContainer {
    private HttpServer server;

    private final URI baseUri;

    private final WebApplicationInitializer initializer;

    public SimpleTestContainer(URI baseUri, WebApplicationInitializer initializer) {
      this.baseUri = baseUri;
      this.initializer = initializer;
    }

    @Override
    public URI getBaseUri() {
      return baseUri;
    }

    @Override
    public ClientConfig getClientConfig() {
      return null;
    }

    @Override
    public void start() {
      try {
        server = GrizzlyWebContainerFactory.create(baseUri);
        WebappContext context = new WebappContext("something", "/");
        initializer.onStartup(context);
        context.deploy(server);
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }

    @Override
    public void stop() {
      server.shutdown();
    }
  }
}
