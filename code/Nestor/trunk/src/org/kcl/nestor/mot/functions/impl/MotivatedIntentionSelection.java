package org.kcl.nestor.mot.functions.impl;

import jason.asSemantics.Agent;
import jason.asSemantics.IntendedMeans;
import jason.asSemantics.Intention;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Term;

import java.util.Iterator;
import java.util.Queue;
import java.util.logging.Logger;

import org.kcl.nestor.mot.MotivatedAgent;
import org.kcl.nestor.mot.Motivation;
import org.kcl.nestor.mot.functions.IntentionSelectionFunction;

public class MotivatedIntentionSelection implements IntentionSelectionFunction {
	protected static final Logger logger = Logger.getLogger(IntentionSelectionFunction.class.getName());

	public Intention selectIntention(Queue<Intention> intentions) {
		return intentions.poll();
	}

	public Intention selectIntention(Agent agent, Queue<Intention> intentions) {
		//logger.info("Selecting intention...");
		MotivatedAgent motivatedAgent = null;
		if(agent instanceof MotivatedAgent) {
			motivatedAgent = (MotivatedAgent) agent;
		} else {
			return this.selectIntention(intentions);
		}
		
		Intention mostMotivatedIntention = null;
		int higherIntensity = 0;
		for (Iterator iter = intentions.iterator(); iter.hasNext();) {
			Intention intention = (Intention) iter.next();
			if(intention.isFinished()) {
				logger.info("Returning finished intention.");
				return intention;
			}
			IntendedMeans intendedMeans = intention.get(0);
			ListTerm annots = intendedMeans.getTrigger().getLiteral().getAnnots();
			for (Iterator iterator = annots.iterator(); iterator.hasNext();) {
				Term term = (Term) iterator.next();
				Motivation motivation = motivatedAgent.getMotivation(term.toString());
				if(motivation != null && motivation.getMotivationIntensity() > higherIntensity) {
					logger.info("Most motivated intention is "+motivation.getMotivationName());
					mostMotivatedIntention = intention;
					higherIntensity = motivation.getMotivationIntensity();
				}
			}
		}
		if(mostMotivatedIntention != null) {
			return mostMotivatedIntention;
		} else {
			logger.info("No motivated intention found.");
			return this.selectIntention(intentions);
		}
	}

}
