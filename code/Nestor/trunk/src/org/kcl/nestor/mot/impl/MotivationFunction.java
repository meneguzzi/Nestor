package org.kcl.nestor.mot.impl;

import jason.asSemantics.Agent;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.LogicalFormula;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class MotivationFunction {
	
	/**
	 * Tests whether or not the supplied literal is supported by the Agent.
	 * @param literal A literal which is to be tested by the belief base.
	 * @param agent An agent's <code>BeliefBase</code>
	 * @return Whether or not the supplied literal is supported by the BeliefBase
	 */
	public boolean supportedByAgent(Literal literal, Agent agent) {
		return (unifyWithBeliefBase(literal, agent, true) != null);
	}
	
	/**
	 * Tests whether or not the supplied literal unifies with any literal in the BeliefBase,
	 * returning all of the valid unifications.
	 * @param literal A literal which is to be unified with literals in the belief base.
	 * @param agent An agent's <code>BeliefBase</code>
	 * @return All valid unifications found
	 */
	public List<Unifier> unifies(Literal literal, Agent agent) {
		return unifyWithBeliefBase(literal, agent, false);
	}
	
	/**
	 * An adapter method to allow the <code>logicalConsequence</code> method from
	 * <code>LogicalFormula</code> to be invoked without an explicit reference to
	 * an agent object.
	 * 
	 * @param formula The formula whose validity is to be tested
	 * @param agent
	 * @return
	 */
	public Iterator<Unifier> logicalConsequence(LogicalFormula formula, Agent agent) {
		Iterator<Unifier> unifiers = null;
		//DummyAgentAdapter adapter = new DummyAgentAdapter(base);
		//MockUpAgent adapter = new MockUpAgent()
		Unifier unifier = new  Unifier();
		
		unifiers = formula.logicalConsequence(agent, unifier);
		
		return unifiers;
	}
	
	private List<Unifier> unifyWithBeliefBase(Literal literal, Agent agent, boolean matchFirst) {
		Iterator<Literal> relevant = agent.getBB().getRelevant(literal);
		if(relevant == null) {
			return null;
		}
		
		List<Unifier> unifiers = null;
		
		while (relevant.hasNext()) {
			Unifier unifier = new Unifier();
			Literal relevantLiteral = (Literal) relevant.next();
			if(unifier.unifies(relevantLiteral, literal)) {
				if(unifiers == null) {
					unifiers = new ArrayList<Unifier>();
				}
				unifiers.add(unifier);
				if(matchFirst)
					break;
			}
		}
		
		return unifiers;
	}
}
