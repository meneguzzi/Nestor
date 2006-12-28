package org.kcl.nestor.env;

import jason.asSyntax.Term;

public interface EnvironmentActions {
	
	public boolean executeAction(String agName, Term act);
}
