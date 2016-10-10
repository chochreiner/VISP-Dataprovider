package vispDataProvider.monitor;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import entities.ProcessingNodeMetricsMessage;

/**
 * The ProcessingNodeMetricsServer exposes the collected statistics 
 * on the execution of the operator instance (i.e., the processing node).
 *
 */

@RestController
public class ProcessingNodeMetricsServer {

	@Autowired
	private ProcessingNodeMonitor procNodeMonitor;
	
    @Value("${role}")
    private String role;
    
    protected static final Logger LOG = LoggerFactory.getLogger(ProcessingNodeMetricsMessage.class);
    
    @PostConstruct
    private void helloworld(){
    	
    	LOG.info("hello world");
    	
    }
	
    @RequestMapping("/metrics")
    public ProcessingNodeMetricsMessage sendAndResetStatistics() {

    	Map<String, Long> emittedMessages = procNodeMonitor.getAndResetEmittedMessages();
    	Map<String, Long> processedMessages = new HashMap<String, Long>();
    	
    	ProcessingNodeMetricsMessage message = new ProcessingNodeMetricsMessage();
    	message.setProcessingNode(role);
    	message.setProcessedMessages(processedMessages);
    	message.setEmittedMessages(emittedMessages);
    	
    	return message; 
    }	
    
}
