/**
 * 
 */
package org.kcl.nestor.mot.impl;

import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.LogicalFormula;
import jason.asSyntax.Trigger;
import jason.bb.BeliefBase;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.kcl.nestor.mot.GoalGenerationFunction;

/**
 * @author Felipe Rech Meneguzzi
 *
 */
public class GoalGenerationFunctionImpl extends MotivationFunction implements GoalGenerationFunction {
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(GoalGenerationFunction.class.getName());
	
	protected Hashtable<LogicalFormula,Trigger> goals;
	
	public GoalGenerationFunctionImpl() {
		this.goals = new Hashtable<LogicalFormula,Trigger>();
	}
	
	@SuppressWarnings("unused")
	private void addTestValues() {
		Trigger t = Trigger.parseTrigger("+!finish(block1)");
		goals.put(Literal.LTrue, t);
	}

	/* (non-Javadoc)
	 * @see org.soton.peleus.mot.GoalGenerationFunction#generateGoals(jason.bb.BeliefBase)
	 */
	public List<Trigger> generateGoals(BeliefBase beliefBase) {
		logger.fine("Generating goals");
		List<Trigger> list = new ArrayList<Trigger>();
		Unifier unifier = null;
		for (LogicalFormula formula : goals.keySet()) {
			Iterator<Unifier> unifiers = logicalConsequence(formula, beliefBase);
			if(unifiers.hasNext()) {
				unifier = unifiers.next();
				Trigger trigger = (Trigger) goals.get(formula).clone();
				unifier.apply(trigger.getLiteral());
				list.add(trigger);
			}
			/*if(formula.equals(Literal.LTrue) || 
				(unifier = getUnifier(formula, beliefBase)) != null) {
				Trigger trigger = (Trigger) goals.get(formula).clone();
				unifier.apply(trigger.getLiteral());
				list.add(trigger);
			}*/
		}
		return list;
	}
	
	public Unifier getUnifier(Literal literal, BeliefBase beliefBase) {
		Iterator<Literal> iter = beliefBase.getRelevant(literal);
		Unifier unifier = new Unifier();
		if(iter != null) {
			for(;iter.hasNext();) {
				Literal l = iter.next();
				l = (Literal) l.clone();
				l.clearAnnots();
				if(unifier.unifies(l, literal))
					return unifier;
			}
		}
		
		return null;
	}

	public void addBeliefToGoalMapping(LogicalFormula formula, Trigger trigger) {
		this.goals.put(formula, trigger);
		
	}

	public void removeBeliefToGoalMapping(LogicalFormula formula) {
		this.goals.remove(formula);
	}

	public void removeGoal(Trigger trigger) {
		for (Iterator<Trigger> iter = this.goals.values().iterator(); iter.hasNext();) {
			Trigger t = iter.next();
			if(t.equals(trigger)) {
				iter.remove();
			}
		}
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("GoalGeneration ");
		sb.append(IntensityUpdateFunctionImpl.class.getName());
		sb.append("{");
		
		for (LogicalFormula formula : goals.keySet()) {
			sb.append(System.getProperty("line.separator"));
			sb.append("   ");
			sb.append(formula);
			sb.append(" -> ");
			sb.append(goals.get(formula));
		}
		
		sb.append(System.getProperty("line.separator"));
		sb.append("   }");
		return sb.toString();
	}
}
