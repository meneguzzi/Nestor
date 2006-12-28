package org.kcl.nestor.env.predictor;

import jason.asSyntax.Literal;
import jason.asSyntax.Term;

import java.util.List;

/**
 * A class intended to work as a sandbox environment into which an agent
 * inputs its beliefs and posts actions to determine possible future outcomes.
 * @author meneguzzi
 *
 */
public interface SandboxEnvironment {
	/**
	 * A method used to obtain the consequences of a certain action.
	 * @param action
	 * @return
	 */
	public List<Literal> getConsequences(Term action);
}
