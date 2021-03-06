package org.kcl.nestor.agent.functions;

import jason.asSemantics.Agent;
import jason.asSyntax.Literal;

import java.util.List;

public interface BeliefUpdateFunction {
	public void updateBeliefs(Agent agent, List<Literal> percepts);
}
