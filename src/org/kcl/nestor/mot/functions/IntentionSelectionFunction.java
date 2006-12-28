package org.kcl.nestor.mot.functions;

import jason.asSemantics.Agent;
import jason.asSemantics.Intention;

import java.util.Queue;

/**
 * A strategy pattern used by a <code>MotivatedAgent</code> to delegate
 * the responsibility of selecting an intention.
 * 
 * @author meneguzz
 *
 */
public interface IntentionSelectionFunction {
	/**
	 * An intention selection function that uses data from the agent to select the next
	 * intention to be carried out.
	 * @param agent
	 * @param intentions
	 * @return
	 */
	public Intention selectIntention(Agent agent, Queue<Intention> intentions);
}
