/**
 * 
 */
package org.kcl.nestor.mot;

import jason.asSemantics.Agent;
import jason.asSemantics.Unifier;
import jason.asSyntax.LogicalFormula;
import jason.asSyntax.NumberTerm;

/** 
 * A strategy pattern defining the interface to the 
 * intensity update function for a given motivation.
 * 
 * @author Felipe Rech Meneguzzi
 */
public interface IntensityUpdateFunction {
	
	/**
	 * Adds a mapping from a belief <code>LogicalFormula</code> into an
	 * integer value used to update the intensity of a given 
	 * motivation. Existing mappings are removed by multiple calls.
	 * 
	 * @param formula
	 * @param value
	 */
	public void addBeliefToIntegerMapping(LogicalFormula formula, NumberTerm value);
	
	/**
	 * Removes the mapping of the supplied <code>Literal</code> from 
	 * this function.
	 * @param formula
	 */
	public void removeBeliefToIntegerMapping(LogicalFormula formula);
	
	/**
	 * A delegate function used by <code>Motivation</code> to calculate
	 * variations to a given motivation.
	 * 
	 * @param agent The agent whose motivations are to be updated
	 * @param unif  The unifier to be used later in the motivation cycle
	 * @return A value to be added to a motivation's intesity.
	 */
	public int updateIntensity(Agent agent, Unifier unif);
}
