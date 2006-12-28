package org.kcl.nestor.mot.functions;

import jason.asSemantics.Agent;
import jason.asSemantics.Intention;
import jason.asSyntax.Literal;

import java.util.List;

public interface BeliefRevisionFunction {
	public List<Literal>[] reviseBeliefs(Agent agent, Literal beliefToAdd, 
										Literal beliefToDel, Intention i);
}
