/**
 * 
 */
package org.kcl.nestor.mot.impl;

import jason.asSemantics.Agent;
import jason.asSemantics.Unifier;
import jason.asSyntax.LogicalFormula;
import jason.asSyntax.NumberTerm;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.logging.Logger;

import org.kcl.nestor.mot.MitigationFunction;

/**
 * @author Felipe Rech Meneguzzi
 *
 */
public class MitigationFunctionImpl extends MotivationFunction implements MitigationFunction {
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(MotivationFunction.class.getName());
	
	protected Hashtable<LogicalFormula, NumberTerm> mapping;
	
	public MitigationFunctionImpl() {
		this.mapping = new Hashtable<LogicalFormula, NumberTerm>();
	}

	/* (non-Javadoc)
	 * @see org.soton.peleus.mot.MitigationFunction#mitigate(jason.bb.BeliefBase)
	 */
	public int mitigate(Agent agent, Unifier unif) {
		int mitigation = 0;
		
		//logger.info("Belief Base at time of mitigation "+beliefBase);
		
		for (LogicalFormula formula : mapping.keySet()) {
			
			Iterator<Unifier> unifiers = formula.logicalConsequence(agent, unif);
			
			//XXX Previous implementation, in which no unifier was supplied
			//Iterator<Unifier> unifiers = logicalConsequence(formula, agent);
			if(unifiers != null &&  unifiers.hasNext()) {
				logger.fine(formula+" is supported by the belief base");
				NumberTerm value = (NumberTerm) mapping.get(formula).clone();
				
				Unifier unifier = unifiers.next();
				value.apply(unifier);
				
				mitigation += value.solve();
			}
			/*if(supportedByBeliefBase(formula, beliefBase)) {
				logger.info(formula+" is supported by the belief base");
				List<Unifier> unifiers = unifies(formula, beliefBase);
				//logger.info("unifiers: "+unifiers);
				NumberTerm value = mapping.get(formula);
				for (Unifier unifier : unifiers) {
					NumberTerm value2 = (NumberTerm) value.clone();
					unifier.apply(value2);
					mitigation+=value2.solve();
				}
			}*/
		}
		
		return mitigation;
	}

	public void addBeliefToIntegerMapping(LogicalFormula formula, NumberTerm value) {
		this.mapping.put(formula, value);
	}

	public void removeBeliefToIntegerMapping(LogicalFormula formula) {
		this.mapping.remove(formula);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Mitigation ");
		sb.append(IntensityUpdateFunctionImpl.class.getName());
		sb.append("{");
		
		for (LogicalFormula formula : mapping.keySet()) {
			sb.append(System.getProperty("line.separator"));
			sb.append("   ");
			sb.append(formula);
			sb.append(" -> ");
			sb.append(mapping.get(formula));
		}
		
		sb.append(System.getProperty("line.separator"));
		sb.append("   }");
		return sb.toString();
	}

}
