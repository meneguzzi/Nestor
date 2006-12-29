package org.kcl.nestor.mot.functions;

import jason.asSemantics.Agent;
import jason.asSemantics.Option;
import jason.asSyntax.BodyLiteral;
import jason.asSyntax.Literal;
import jason.asSyntax.Plan;
import jason.bb.BeliefBase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.kcl.nestor.agent.functions.OptionSelectionFunction;
import org.kcl.nestor.mot.MotivatedAgent;
import org.kcl.nestor.mot.Motivation;
import org.kcl.nestor.mot.bb.PredictiveBeliefBase;

public class MotivatedOptionSelection implements OptionSelectionFunction<MotivatedAgent> {
	private static final Logger logger = Logger.getLogger(Agent.class.getName());

	public Option selectOption(MotivatedAgent agent, List<Option> options) {
		logger.info("Options are: "+options);
		
		if (options.size() > 0) {
			PredictiveBeliefBase base = new PredictiveBeliefBase(agent.getBB());
			int highestMotivationalReward = 0; //By reward we mean we are looking for the 
											   //lowest value
			Option mostMotivated = null;
				
			for (Iterator<Option> iter = options.iterator(); iter.hasNext();) {
				Option option = iter.next();
				Plan plan = option.getPlan();
				int motivationalValue = getMotivationalValue(base, agent.getMotivations(), plan);
				if(motivationalValue < highestMotivationalReward) {
					highestMotivationalReward = motivationalValue;
					mostMotivated = option;
				}
				base.reset();
			}
			//get belief base
			//get motivations
			//project each option into the belief base
			if(options.remove(mostMotivated)) {
				logger.info("Most motivated plan is "+mostMotivated);
				return mostMotivated;
			} else {
				return null;
			} 
        } /*else if(options.size() == 0) { //if we have only one option
        	logger.info("Only one option to carry out goal");
        	return options.remove(0); //No need to ponder which one to take
        } */else {
            return null;
        }
	}

	/**
	 * Given a certain <code>BeliefBase</code>, and a certain set of <code>Motivation</code>s,
	 * this method calculates the expected motivational value of executing the supplied 
	 * <code>Plan</code> to the agent's motivational state.
	 * 
	 * @param base			A BeliefBase against which the plan will be executed.
	 * @param motivations	The set of motivations currently being used by the agent.
	 * @param plan			The plan for which motivational value is to be calculated.
	 * @return
	 */
	protected int getMotivationalValue(BeliefBase base, List<Motivation> motivations, Plan plan) {
		logger.info("Calculating motivational value for plan "+plan);
		int motivationalValue = 0;
		//for each step of the plan
		for (Iterator<BodyLiteral> i = plan.getBody().iterator(); i.hasNext();) {
			BodyLiteral literal = i.next();
			List<Literal> consequences = this.getConsequences(literal);
			//if this plan would fail, then its motivational value should be zero 
			if(consequences.size() == 1 && consequences.get(0) == Literal.LFalse) {
				motivationalValue = 0;
				break;
			} else {
				//otherwise, update our simulated beliefbase with the consequences of the
				//plan steps
				for (Iterator<Literal> iter = consequences.iterator(); iter.hasNext();) {
					Literal l = iter.next();
					base.add(l);
				}
				//and calculate the resulting motivational value
				for (Iterator<Motivation> iter = motivations.iterator(); iter.hasNext();) {
					Motivation motivation = iter.next();
					motivationalValue += motivation.getIntensityUpdateFunction().updateIntensity(base);
					motivationalValue += motivation.getMitigationFunction().mitigate(base);
				}
			}
		}
		logger.info("Motivational value is "+motivationalValue);
		return motivationalValue;
	}
	
	/**
	 * Return the expected consequences of a plan step in terms of belief additions
	 * and belief deletions, if the step is successful, otherwise the list will 
	 * contain the default <code>false</code> Literal.
	 * 
	 * @param bodyLiteral A <code>BodyLiteral</code> representing the plan step whose 
	 * 					consequences are to be inferred. 
	 * @return	A <code>List</code> containing the beliefs modifications that should
	 * 			result from the action execution, if the step is successful, otherwise 
	 * 			the list will contain the default <code>false</code> Literal.
	 */
	protected List<Literal> getConsequences(BodyLiteral bodyLiteral) {
		logger.info("Getting consequences for "+bodyLiteral);
		List<Literal> consequences = new ArrayList<Literal>();
		
		switch (bodyLiteral.getType()) {
		case delAddBel:
		case delBel:
		case addBel:
			consequences.add(bodyLiteral.getLiteralFormula());
			break;
		case achieve:
			//execute new plan
			break;
		case achieveNF:
			//TODO find out what achieve new focus means
			break;
		case action:
			//TODO get the consequences from the simulator
			break;
		case internalAction:
			//TODO decide whether or not I should try to find the result of an internal action
			break;
		case constraint:
			//TODO find out what this means
			break;
		case test:
			//TODO perhaps this function should change to include the simulated environment
			break;
		default:
			break;
		}
		
		return consequences;
	}
}
