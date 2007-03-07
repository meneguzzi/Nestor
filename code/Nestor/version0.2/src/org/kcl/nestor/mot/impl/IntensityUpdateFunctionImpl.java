/**
 * 
 */
package org.kcl.nestor.mot.impl;

import jason.asSemantics.Unifier;
import jason.asSyntax.LogicalFormula;
import jason.asSyntax.NumberTerm;
import jason.bb.BeliefBase;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.logging.Logger;

import org.kcl.nestor.mot.IntensityUpdateFunction;

/**
 * An intensity update function that monitors positive and negative beliefs in 
 * the belief base, updating the motivation intensity when certain predicates are
 * believed (positiveBeliefs) or not (negativeBeliefs).
 * 
 * @author Felipe Rech Meneguzzi
 *
 */
public class IntensityUpdateFunctionImpl extends MotivationFunction implements IntensityUpdateFunction {
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(IntensityUpdateFunction.class.getName());
	
	protected Hashtable<LogicalFormula, NumberTerm> beliefCues;
	
	public IntensityUpdateFunctionImpl() {
		beliefCues = new Hashtable<LogicalFormula, NumberTerm>();
		
		//For testing purposes only
		//addTestValues();
	}
	
	@SuppressWarnings("unused")
	private void addTestValues() {
		/*Literal l = Literal.parseLiteral("over(block1,feedBelt)");
		positiveBeliefs.put(l, 1);
		
		negativeBeliefs.put(l, -1);
		
		l = Literal.parseLiteral("finished(block1)");
		positiveBeliefs.put(l, -1);*/
	}

	/* (non-Javadoc)
	 * @see org.soton.peleus.mot.IntensityUpdateFunction#updateIntensity(jason.bb.BeliefBase)
	 */
	public int updateIntensity(BeliefBase beliefBase) {
		int motivationDelta = 0;
		for (LogicalFormula cue : beliefCues.keySet()) {
			//logger.info("Updating intensity regarding "+positiveLiteral);
			LogicalFormula formula = (LogicalFormula) cue.clone();
			Iterator<Unifier> unifiers = logicalConsequence(formula, beliefBase);
			//TODO consider the actual strategy to calculate motivations if more than one
			//TODO unification is possible for a given formula
			if(unifiers.hasNext()) {
				NumberTerm value = (NumberTerm)beliefCues.get(formula).clone();
				unifiers.next().apply(value);
				motivationDelta += value.solve();
			}
			/*if(supportedByBeliefBase(literal, beliefBase)) {
				//logger.info(positiveLiteral+"is supported by the belief base");
				List<Unifier> unifiers = unifies(literal, beliefBase);
				NumberTerm value = beliefCues.get(formula);
				for (Unifier unifier : unifiers) {
					NumberTerm value2 = (NumberTerm) value.clone();
					unifier.apply(value2);
					motivationDelta+=value2.solve();
				}
			}*/
		}
		
		logger.finer("Net motivation is: "+motivationDelta);
		return motivationDelta;
	}

	public void addBeliefToIntegerMapping(LogicalFormula formula, NumberTerm value) {
		this.beliefCues.put(formula, value);
		
	}

	public void removeBeliefToIntegerMapping(LogicalFormula formula) {
		this.beliefCues.remove(formula);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("IntensityUpdate ");
		sb.append(IntensityUpdateFunctionImpl.class.getName());
		sb.append("{");
		
		for (LogicalFormula formula : beliefCues.keySet()) {
			sb.append(System.getProperty("line.separator"));
			sb.append("   ");
			sb.append(formula);
			sb.append(" -> ");
			sb.append(beliefCues.get(formula));
		}
		
		sb.append(System.getProperty("line.separator"));
		sb.append("   }");
		return sb.toString();
	}

}
