package org.kcl.nestor.mot.functions;

import jason.asSemantics.Agent;
import jason.asSemantics.Option;
import jason.asSemantics.Unifier;
import jason.asSyntax.BodyLiteral;
import jason.asSyntax.Literal;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.kcl.nestor.agent.functions.OptionSelectionFunction;
import org.kcl.nestor.mot.MotivatedAgent;
import org.kcl.nestor.mot.Motivation;
import org.kcl.nestor.mot.predictor.PredictiveAgent;

public class MotivatedOptionSelection implements OptionSelectionFunction<MotivatedAgent> {
	private static final Logger logger = Logger.getLogger(Agent.class.getName());

	public Option selectOption(MotivatedAgent agent, List<Option> options) {
		//logger.info("Options are: "+options);
		//If we have more than one option, check it
		if (options.size() > 1) {
			//MockUpAgent mockUpAgent = new MockUpAgent(agent);
			PredictiveAgent predictiveAgent = new PredictiveAgent(agent);
			int highestMotivationalReward = 0; //By reward we mean we are looking for the 
											   //lowest value
			Option mostMotivated = null;
				
			for (Iterator<Option> iter = options.iterator(); iter.hasNext();) {
				Option option = iter.next();
				int motivationalValue = getMotivationalValue(predictiveAgent, agent.getMotivations(), option);
				if(motivationalValue < highestMotivationalReward) {
					highestMotivationalReward = motivationalValue;
					mostMotivated = option;
				}
				//mockUpAgent.reset();
			}
			//get belief base
			//get motivations
			//project each option into the belief base
			if(options.remove(mostMotivated)) {
				//logger.info("Most motivated option is "+mostMotivated);
				return mostMotivated;
			} else {
				//logger.info("No motivated option found, returning "+options.get(0));
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
	protected int getMotivationalValue(PredictiveAgent predictiveAgent, List<Motivation> motivations, Option option) {
		logger.fine("Calculating motivational value for plan "+option);
		int motivationalValue = 0;
		Unifier unif = option.getUnif();
		//for each step of the plan
		for (Iterator<BodyLiteral> i = option.getPlan().getBody().iterator(); i.hasNext();) {
			BodyLiteral literal = i.next();
			List<Literal> consequences = predictiveAgent.getConsequences(literal, unif);
			//if this plan would fail, then its motivational value should be zero 
			if(consequences.size() == 1 && consequences.get(0) == Literal.LFalse) {
				motivationalValue = 0;
				break;
			} else {
				//otherwise, update our simulated belief base with the consequences of the
				//plan steps
				/*for (Iterator<Literal> iter = consequences.iterator(); iter.hasNext();) {
					Literal l = iter.next();
					predictiveAgent.addBel(l);
				}*/ // this update is now irrelevant, since the simulated agent is already up to date
				//and calculate the resulting motivational value
				for (Iterator<Motivation> iter = motivations.iterator(); iter.hasNext();) {
					Motivation motivation = iter.next();
					motivationalValue += motivation.getIntensityUpdateFunction().updateIntensity(predictiveAgent);
					motivationalValue += motivation.getMitigationFunction().mitigate(predictiveAgent);
				}
			}
		}
		logger.fine("Motivational value is "+motivationalValue);
		return motivationalValue;
	}
	
	
}
