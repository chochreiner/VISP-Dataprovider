package vispDataProvider.generationPattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vispDataProvider.entities.GenerationState;

public class CustomizablePattern implements GenerationPattern{

	private enum MODE {BURST, FREQUENCY, TOKENS};
	
	private static MODE mode = MODE.TOKENS;
	private Long frequencyChangeInterval;

	// the generator is executed every 100ms
	private static int[] burstAmounts = {1, 5, 10, 5, 1};
//	private static int[] amounts = {10, 50, 100, 50, 10};
	private static int[] frequency = {10, 2, 1, 2, 10};
// 	private static int[] tokens = {1,5,10,15,20,25,30,35,40,45,50};
	private static int[] tokens = {10,20,30,40,50};

	private static int index = 0;
	private static int indexTokens = 0;
	private static long tokenRecharge = 0;
	private static int currentTokens = 10;

	private static int wait = 0;
    private static final Logger LOG = LoggerFactory.getLogger(CustomizablePattern.class);
    
    public CustomizablePattern(long frequencyChangeIntervalInSecs) {
    	this.frequencyChangeInterval = frequencyChangeIntervalInSecs * 1000;
	}
	
    public GenerationState iterate(GenerationState state) {

    	updateGeneratorState(state);
    	
    	switch (mode){
    	case BURST:
    		return generateBurst(state);
    	case FREQUENCY:
    		return generateSingleTupleWithFrequency(state);
    	case TOKENS:
    		return generateSingleTupleWithTokens(state);
    	default: 
    		return generateBurst(state);    			
    	}
        
    }
    
    
    private GenerationState generateSingleTupleWithTokens(GenerationState state) {
    	
    	if (currentTokens >= 0){
    		currentTokens = currentTokens - 1;
    		
        	state.setAmount(1);
            state.setIteration(state.getIteration() + 1);
    	
    	} else {
    		
    		state.setAmount(0);
    		state.setIteration(state.getIteration() + 1);
    	
    	}	

        return state;
        
    }
 
    /* Generate a tuple every frequency[i] invocation of the scheduler 
     * i changes every frequencyChangeInterval secs */
    private GenerationState generateSingleTupleWithFrequency(GenerationState state) {
        
    	if (wait < 1){
    		wait = frequency[index] - 1;
    		
        	state.setAmount(1);
            state.setIteration(state.getIteration() + 1);
    	} else {
    		wait--;

    		state.setAmount(0);
            state.setIteration(state.getIteration() + 1);
    	}

        return state;
        
    }
    private GenerationState generateBurst(GenerationState state) {
        
    	state.setAmount(burstAmounts[index]);
        state.setIteration(state.getIteration() + 1);

        return state;
        
    }
    
    private void updateGeneratorState(GenerationState state){
    	
    	if (state == null || state.getTime() == null || state.getTime().equals("")){
    		state.setTime(Long.toString(System.currentTimeMillis()));
    		index = 0;
    		return;
    	}

    	long lastFrequencyChange = Long.valueOf(state.getTime()).longValue();
    	long now = System.currentTimeMillis();

    	if (now > tokenRecharge + 1000){
    		currentTokens = tokens[indexTokens];
    		tokenRecharge = now;
    	}

    	if (now > lastFrequencyChange + frequencyChangeInterval){
    		/* Time to change the generation pattern */
    		index = (index + 1) % burstAmounts.length; 
    		indexTokens = (indexTokens + 1) % tokens.length;
    		state.setTime(Long.toString(now));
    		
    		wait = 0;    		
    		currentTokens = tokens[indexTokens];
    		
    		switch (mode){
        	case BURST:
    			LOG.info("State set to " + index + ", thus producing " + burstAmounts[index] + " tuples each 100ms");
        	case FREQUENCY:
    			LOG.info("State set to " + index + ", thus producing a tuple every " + frequency[index] + " x 100ms");
        	case TOKENS:
    			LOG.info("State set to " + indexTokens + ", thus producing " + tokens[indexTokens] + " tuples/s (every 20ms generate if tokens are available)");
        	}

    	}
    	
    }
    
}
