package com.jhooq.demo.Jhooqdockerdemo;
import java.io.File;
// Exit Spring app
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
// Exit Spring App
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Bean;
// START manual intrumentation
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.ObservableDoubleGauge;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.GlobalOpenTelemetry;
// END manual instrumentation

@RestController
public class JhooqDockerDemoController {
    /* START of Manula intrumentation */
    /* Adding two custom metrics
     * app_requests_total counts number of request recieved. Uses Long Counter, sync
     * app_free_mem displays free RAM. Uses guage double, async */
    private static final Meter meter = GlobalOpenTelemetry.getMeter("jhooqservice");
    private static final LongCounter jhRequestsCounter =     meter
          .counterBuilder("app_requests_total")
          .setDescription("Counts ad requests by request and response type")
          .build();  
    private static final ObservableDoubleGauge diskSpace = meter.gaugeBuilder("app_free_mem").setDescription("Host free memory")
	   .setUnit("MB")
	   .buildWithCallback(measurement -> {
		   measurement.record(getFreeMem(), Attributes.of(AttributeKey.stringKey("service"), "jhooq"));
	   });
    /* END of manual intrumentation */
    String jobLengthEnv = System.getenv("JOB_LENGTH");
    int jobLength = Integer.parseInt(jobLengthEnv);

    // Add seprate counter for internal handling 
    private static final AtomicInteger requestCount = new AtomicInteger(0);
//    private static ConfigurableApplicationContext ctx;

    @GetMapping("/hello")
    public String hello() {
	System.out.println("Entering handler");
	/* Increment request counter whenever there is a new GET REquest */
	jhRequestsCounter.add(1, Attributes.of(AttributeKey.stringKey("method"), "GET"));
	// Exit Springboot after serving 100 request
        int count = requestCount.incrementAndGet();
	System.out.println("Value of Counter is " + count );
	if (count >= jobLength) {
	   System.out.println("Attempting to exist the application after  " + count );
//	   SpringApplication.exit(ctx, () -> 0);
	   System.out.println("Spring App exited");
	   System.exit(0);
	}
	// End of application logic to exit after 100 requests
        return "Docker Demo - Hello Jhooq Service Manual Instrumentation";
    }
    
    /* get current disk space */
    /* No longer used since free memory statistics is acquired */
    public static double getDiskSpace() {
	File file = new File(System.getProperty("user.dir"));
	long freeSpace = file.getFreeSpace();
	double freeSpaceInMB = (double) freeSpace / (1024 *1024);
	return freeSpaceInMB;
    }

    /* get free memory */
    public static double getFreeMem() {
	 Runtime gfg = Runtime.getRuntime();
	 double memory = (double) gfg.freeMemory();
	 return memory;
    }


}
