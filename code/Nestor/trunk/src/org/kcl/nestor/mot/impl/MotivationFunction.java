package org.kcl.nestor.mot.impl;

import jason.asSemantics.Agent;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.LogicalFormula;
import jason.bb.BeliefBase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class MotivationFunction {
	
	/**
	 * Tests whether or not the supplied literal is supported by the BeliefBase.
	 * @param literal A literal which is to be tested by the belief base.
	 * @param beliefBase An agent's <code>BeliefBase</code>
	 * @return Whether or not the supplied literal is supported by the BeliefBase
	 */
	public boolean supportedByBeliefBase(Literal literal, BeliefBase beliefBase) {
		return (unifyWithBeliefBase(literal, beliefBase, true) != null);
	}
	
	/**
	 * Tests whether or not the supplied literal unifies with any literal in the BeliefBase,
	 * returning all of the valid unifications.
	 * @param literal A literal which is to be unified with literals in the belief base.
	 * @param beliefBase An agent's <code>BeliefBase</code>
	 * @return All valid unifications found
	 */
	public List<Unifier> unifies(Literal literal, BeliefBase beliefBase) {
		return unifyWithBeliefBase(literal, beliefBase, false);
	}
	
	/**
	 * An adapter method to allow the <code>logicalConsequence</code> method from
	 * <code>LogicalFormula</code> to be invoked without an explicit reference to
	 * an agent object.
	 * 
	 * @param formula The formula whose validity is to be tested
	 * @param base
	 * @return
	 */
	public Iterator<Unifier> logicalConsequence(LogicalFormula formula, BeliefBase base) {
		Iterator<Unifier> unifiers = null;
		DummyAgentAdapter adapter = new DummyAgentAdapter(base);
		Unifier unifier = new  Unifier();
		
		unifiers = formula.logicalConsequence(adapter, unifier);
		
		return unifiers;
	}
	
	private List<Unifier> unifyWithBeliefBase(Literal literal, BeliefBase beliefBase, boolean matchFirst) {
		Iterator<Literal> relevant = beliefBase.getRelevant(literal);
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
	
	/**
	 * An empty agent class used to adapt calls to <code>LogicalFormula</code>.
	 * @author meneguzz
	 *
	 */
	protected class DummyAgentAdapter extends Agent {
		public DummyAgentAdapter(BeliefBase base) {
			this.fBB = base;
		}
	}
}
