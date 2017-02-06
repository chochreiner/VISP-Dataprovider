package vispDataProvider.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.base.Splitter;

/**
 * The ProcessingNodeMonitor collects statistics on the execution of the
 * operator instance (i.e., the processing node).
 *
 */
@Service
public class ProcessingNodeMonitor {

    @Value("${operator.subscribed.operators}")
    protected String subscribedOperators;
    protected List<String> destinations = null;

    private Map<String, Long> emittedMessages;
	
	private Lock emittedMessagesLock;

    protected static final Logger LOG = LoggerFactory.getLogger(ProcessingNodeMonitor.class);

	public ProcessingNodeMonitor() {
		emittedMessages = new HashMap<String, Long>();

		emittedMessagesLock = new ReentrantLock();
	}

	@PostConstruct
    private void computeDestinationOperatorName(){
    	
    	if (destinations != null)
    		return;
    	
    	destinations = new ArrayList<String>();

    	Iterable<String> dwnStr = Splitter.on(',').split(subscribedOperators);
    	if (dwnStr == null)
    		return;
    	
    	Iterator<String> it = dwnStr.iterator();
    	while(it.hasNext()){
    		destinations.add(it.next());
    	}
    	
    	LOG.info("Initialized destinations as " + destinations);
    }

	public void notifyOutgoingMessage() {

//		LOG.info(" + Outgoing Message - Destinations: " + destinations);
		
		if (destinations == null)
			return;
		
		emittedMessagesLock.lock();
		try{

			for (String operatorName : destinations){
				updateOutgoingMessage(operatorName);
			}

		} finally{
			emittedMessagesLock.unlock();
		}

	}

	public void notifyOutgoingMessage(List<String> destinationOperators) {

//		LOG.info(" + Outgoing Message - Destinations: " + destinationOperators);
		
		if (destinationOperators == null)
			return;
		
		emittedMessagesLock.lock();
		try{

			for (String operatorName : destinationOperators){
				updateOutgoingMessage(operatorName);
			}

		} finally{
			emittedMessagesLock.unlock();
		}

	}

	public void notifyOutgoingMessage(String destinationOperatorName) {

		if (destinationOperatorName == null)
			return;		

		emittedMessagesLock.lock();
		try{

			updateOutgoingMessage(destinationOperatorName);

		} finally{
			emittedMessagesLock.unlock();
		}

	}

	private void updateOutgoingMessage(String destinationOperatorName) {

		Long count = emittedMessages.get(destinationOperatorName);

		if (count == null)
			count = new Long(1);
		else
			count = new Long(count.longValue() + 1);

		emittedMessages.put(destinationOperatorName, count);			

	}

	
	public Map<String, Long> getAndResetEmittedMessages(){
		
		Map<String, Long> lastEmittedMessages  = null;
		emittedMessagesLock.lock();
		
		try{
			lastEmittedMessages = emittedMessages;
			emittedMessages = new HashMap<String, Long>();
		}finally{
			emittedMessagesLock.unlock();
		}

		return lastEmittedMessages;

	}
	
}
