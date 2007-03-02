package org.kcl.nestor.env.scripted;

import jason.asSyntax.Literal;
import jason.asSyntax.Term;

import java.util.ArrayList;
import java.util.List;

import org.kcl.nestor.env.action.ExternalAction;

public class charge implements ExternalAction<ScriptedEnvironment> {
	
	public static final String FUNCTOR="charge";

	public List<Literal> consequences(ScriptedEnvironment env, String agName, Term... terms) {
		List<Literal> percepts = env.getPercepts("johnny");
		List<Literal> consequences = new ArrayList<Literal>(2);
		
		Literal batt = env.findLiteralByFunctor("batt", percepts);
		batt = new Literal(false, batt);
		Literal newBatt = Literal.parseLiteral("batt(10)");
		
		consequences.add(batt);
		consequences.add(newBatt);
		
		return consequences;
	}

	public boolean execute(ScriptedEnvironment env, String agName, Term... terms) {
		List<Literal> percepts = env.getPercepts("johnny");
		
		//Literal precond0 = Literal.parseLiteral("at(A)");
		/*if(findMatchingLiteral(precond0, percepts) == null)
			return false;*/
		
		Literal batt = env.findLiteralByFunctor("batt", percepts);
		env.removePercept(agName, batt);
		//environment.addPercept(Literal.parseLiteral("batt(full)"));
		env.addPercept(Literal.parseLiteral("batt(10)"));
		logger.info("Charging "+agName);
		return true;
	}

	public String getFunctor() {
		return FUNCTOR;
	}

}
