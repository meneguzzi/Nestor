/**
 * 
 */
package org.kcl.nestor.mot.impl;

import jason.asSemantics.Agent;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.LogicalFormula;
import jason.asSyntax.Trigger;

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
	public List<Trigger> generateGoals(Agent agent, Unifier unif) {
		logger.fine("Generating goals");
		List<Trigger> list = new ArrayList<Trigger>();
		for (LogicalFormula formula : goals.keySet()) {
			Iterator<Unifier> unifiers = formula.logicalConsequence(agent, unif);
			//XXX Previous implementation, in which no unifier was supplied 
			//Iterator<Unifier> unifiers = logicalConsequence(formula, agent);
			
			if(unifiers != null && unifiers.hasNext()) {
				Unifier unifier = unifiers.next();
				Trigger trigger = (Trigger) goals.get(formula).clone();
				
				//Apply the first unifier in order to get the instantiated goal
				trigger.getLiteral().apply(unifier);
				list.add(trigger);
				
				//And compose the unifier to the supplied parameter for the next functions
				unif.compose(unifier);
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
	
	/*
	 * (non-Javadoc)
	 * @see org.kcl.nestor.mot.GoalGenerationFunction#addBeliefToGoalMapping(jason.asSyntax.LogicalFormula, jason.asSyntax.Trigger)
	 */
	public void addBeliefToGoalMapping(LogicalFormula formula, Trigger trigger) {
		this.goals.put(formula, trigger);
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.kcl.nestor.mot.GoalGenerationFunction#removeBeliefToGoalMapping(jason.asSyntax.LogicalFormula)
	 */
	public void removeBeliefToGoalMapping(LogicalFormula formula) {
		this.goals.remove(formula);
	}

	/*
	 * (non-Javadoc)
	 * @see org.kcl.nestor.mot.GoalGenerationFunction#removeGoal(jason.asSyntax.Trigger)
	 */
	public void removeGoal(Trigger trigger) {
		for (Iterator<Trigger> iter = this.goals.values().iterator(); iter.hasNext();) {
			Trigger t = iter.next();
			if(t.equals(trigger)) {
				iter.remove();
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
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
