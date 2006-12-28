package org.kcl.nestor.mot.functions.defaults;

import jason.asSemantics.Agent;
import jason.asSemantics.Intention;

import java.util.Queue;

import org.kcl.nestor.mot.functions.IntentionSelectionFunction;

public class DefaultIntentionSelectionFunction implements
		IntentionSelectionFunction {

	public Intention selectIntention(Agent agent, Queue<Intention> intentions) {
		return intentions.poll();
	}

}
