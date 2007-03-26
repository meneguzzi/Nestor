package org.kcl.nestor.mot.functions;

import jason.asSemantics.Agent;
import jason.asSemantics.Unifier;
import jason.asSyntax.DefaultTerm;
import jason.asSyntax.Literal;
import jason.asSyntax.Trigger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.kcl.nestor.agent.functions.defaults.DefaultBeliefUpdateFunction;
import org.kcl.nestor.mot.MotivatedAgent;
import org.kcl.nestor.mot.Motivation;

public class MotivatedBeliefUpdate extends DefaultBeliefUpdateFunction {
	private Logger logger = Logger.getLogger(Agent.class.getName());
	
	/**
	 * XXX this might have to be cleaned up in the future,
	 * Right now it serves to store the unifiers created when a motivation is triggered
	 */
	protected HashMap<Motivation, Unifier> triggeredMotivationUnifiers;
	
	public MotivatedBeliefUpdate() {
		//Nothing here so far
		triggeredMotivationUnifiers = new HashMap<Motivation, Unifier>();
	}
	
	@Override
	public void updateBeliefs(Agent agent, List<Literal> percepts) {
		super.updateBeliefs(agent, percepts);
		MotivatedAgent motivatedAgent;
		
		if(agent instanceof MotivatedAgent) {
			motivatedAgent = (MotivatedAgent) agent;
		} else {
			return;
		}
		
		logger.fine("Updating Motivations");
		//first remove from the triggered motivations, any motivation whose plan failed
		//TODO must implement a way of removing triggered motivations whose plan failed
		//TODO this is probably the responsability of the event selection function
		
		//Then update motivational intensities
		List<Motivation> newTriggeredMotivations = new ArrayList<Motivation>();
		List<Unifier> newMotivationUnifiers = new ArrayList<Unifier>();
		for (Motivation	motivation : motivatedAgent.getMotivations()) {
			if(motivatedAgent.getPendingMotivations().contains(motivation)) {
				continue;
			}
			logger.fine("Updating Motivation: '"+motivation.getMotivationName()+"'");
			
			Unifier unif = new Unifier();
			boolean thresholdReached = motivation.updateIntensity(agent, unif);
			
			if(thresholdReached && !motivatedAgent.getPendingMotivations().contains(motivation)) {
				logger.info(motivation.getMotivationName()+" threshold reached.");
				//triggeredMotivations.add(motivation);
				newTriggeredMotivations.add(motivation);
				newMotivationUnifiers.add(unif);
			}
		}
		//Then for each triggered motivation, generate goals
		for (Motivation motivation : newTriggeredMotivations) {
			Unifier unif = newMotivationUnifiers.get(newTriggeredMotivations.indexOf(motivation));
			List<Trigger> goals = motivation.generateGoals(agent, unif);
			
			if(goals.size() > 0) {
				motivatedAgent.getPendingMotivations().add(motivation);
				//For each generated "goal", post a new event for the agent
				for (Trigger trigger : goals) {
					//We should avoid adding the exact same goal multiple times
					if(!motivatedAgent.isPendingMotivatedGoal(trigger)) {
						//An annotation is added to the trigger denoting the originating motivation
						//This information is used later on by the intention selection algorithm
						trigger.getLiteral().addAnnot(DefaultTerm.parse(motivation.getMotivationName()));

						logger.info("Adding goal "+trigger.toString());
						motivatedAgent.addMotivatedGoal(trigger, motivation);
						//Store the unifier used so far for the later mitigation
						this.triggeredMotivationUnifiers.put(motivation, unif);
					} else {
						logger.info("Goal "+trigger+" is already being pursued");
					}
				}
			} else {
				logger.info("No goal was generated for motivation "+motivation.getMotivationName());
			}
		}
		//Then mitigate any triggered motivations that might have been satisfied
		for (Iterator<Motivation> iter = motivatedAgent.getPendingMotivations().iterator(); iter.hasNext();) {
			Motivation motivation = iter.next();
			
			// XXX, recover this from the stored motivations
			//Unifier unif = new Unifier();
			Unifier unif = this.triggeredMotivationUnifiers.get(motivation);
			
			if(motivation.mitigate(agent, unif)) {
				logger.info("Motivation "+motivation.getMotivationName()+" mitigated");
				iter.remove();
				motivatedAgent.removePendingMotivation(motivation);
			} /*else {
				logger.info("Motivation "+motivation.getMotivationName()+" not mitigated yet");
			}*/
			//Old way of mitigating the motivation, in which the agent would only
			//believe that the motivation was mitigated if the intensity of the motivation
			//had dropped below the threshold
			/*if(motivation.getMotivationIntensity() < motivation.getMotivationThreshold()) {
				logger.info("Motivation "+motivation.getMotivationName()+" mitigated");
				iter.remove();
			}*/
		}
	}
}
