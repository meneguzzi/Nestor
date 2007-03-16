/**
 * 
 */
package org.kcl.nestor.mot;

import jason.asSemantics.Agent;
import jason.asSyntax.LogicalFormula;
import jason.asSyntax.NumberTerm;

/** 
 * A strategy pattern defining the interface for the 
 * intention mitigation function.
 * 
 * @author Felipe Rech Meneguzzi
 */
public interface MitigationFunction {
	
	/**
	 * Adds a mapping from a belief <code>Literal</code> into an
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
	 * The function invoked when the goals associated with this motivation are 
	 * achieved.
	 * @param agent A reference to an agent
	 */
	public int mitigate(Agent agent);
}
