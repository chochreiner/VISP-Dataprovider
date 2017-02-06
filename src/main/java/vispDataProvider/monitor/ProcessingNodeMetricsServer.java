package vispDataProvider.monitor;

import entities.ProcessingNodeMetricsMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * The ProcessingNodeMetricsServer exposes the collected statistics 
 * on the execution of the operator instance (i.e., the processing node).
 *
 */

@RestController
public class ProcessingNodeMetricsServer {

    private String role = "source";
    
    protected static final Logger LOG = LoggerFactory.getLogger(ProcessingNodeMetricsMessage.class);

    @RequestMapping("/metrics")
    public ProcessingNodeMetricsMessage sendAndResetStatistics() {

		ProcessingNodeMonitor procNodeMonitor = new ProcessingNodeMonitor();

    	Map<String, Long> emittedMessages = procNodeMonitor.getAndResetEmittedMessages();
    	Map<String, Long> processedMessages = new HashMap<>();
    	
    	ProcessingNodeMetricsMessage message = new ProcessingNodeMetricsMessage();
    	message.setProcessingNode(role);
    	message.setProcessedMessages(processedMessages);
    	message.setEmittedMessages(emittedMessages);
    	
    	return message; 
    }	
    
}
