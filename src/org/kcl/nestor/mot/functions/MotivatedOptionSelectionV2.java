package org.kcl.nestor.mot.functions;

import jason.asSemantics.Agent;
import jason.asSemantics.Option;
import jason.asSemantics.Unifier;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.kcl.nestor.agent.functions.OptionSelectionFunction;
import org.kcl.nestor.mot.MotivatedAgent;
import org.kcl.nestor.mot.Motivation;
import org.kcl.nestor.mot.predictor.DummyAgent;

public class MotivatedOptionSelectionV2 implements OptionSelectionFunction<MotivatedAgent> {

	private static final Logger logger = Logger.getLogger(Agent.class.getName());

	public Option selectOption(MotivatedAgent agent, List<Option> options) {
		//logger.info("Options are: "+options);
		//If we have more than one option, check it
		if (options.size() > 1) {
			int highestMotivationalReward = 0; //By reward we mean we are looking for the 
											   //lowest value
			Option mostMotivated = null;
				
			for (Iterator<Option> iter = options.iterator(); iter.hasNext();) {
				Option option = iter.next();
				DummyAgent dummyAgent = new DummyAgent(agent);
				int motivationalValue = getMotivationalValue(dummyAgent, agent.getMotivations(), option);
				if(motivationalValue < highestMotivationalReward) {
					highestMotivationalReward = motivationalValue;
					mostMotivated = option;
				}
			}
			
			if(options.remove(mostMotivated)) {
				logger.info("Most motivated option is "+mostMotivated);
				return mostMotivated;
			} else {
				logger.info("No motivated option found, returning "+options.get(0));
				return options.remove(0);
			} 
        } else if(options.size() == 1) { //if we have only one option
        	//logger.info("Only one option to carry out goal");
        	return options.remove(0); //No need to ponder which one to take
        } else {
            return null;
        }
	}

	/**
	 * Given a certain <code>BeliefBase</code>, and a certain set of <code>Motivation</code>s,
	 * this method calculates the expected motivational value of executing the supplied 
	 * <code>Plan</code> to the agent's motivational state.
	 * 
	 * @param predictiveAgent			A BeliefBase against which the plan will be executed.
	 * @param motivations	The set of motivations currently being used by the agent.
	 * @param option			The plan for which motivational value is to be calculated.
	 * @return
	 */
	protected int getMotivationalValue(DummyAgent dummyAgent, List<Motivation> motivations, Option option) {
		logger.info("Calculating motivational value for plan "+option);
		int motivationalValue = 0;
		
		if(dummyAgent.executeOption(option)) {
			//logger.info("Original beliefs were "+dummyAgent.getOriginalBB());
			//logger.info("Predicted beliefs are "+dummyAgent.getBB());
			for (Iterator<Motivation> iter = motivations.iterator(); iter.hasNext();) {
				Motivation motivation = iter.next();
				Unifier unif = new Unifier();
				motivationalValue += motivation.getIntensityUpdateFunction().updateIntensity(dummyAgent, unif);
				motivationalValue += motivation.getMitigationFunction().mitigate(dummyAgent, unif);
			} 
		} else {
			return 0;
		}
		
		logger.info("Motivational value is "+motivationalValue);
		return motivationalValue;
	}

}
