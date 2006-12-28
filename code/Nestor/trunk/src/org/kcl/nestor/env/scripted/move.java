package org.kcl.nestor.env.scripted;

import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Term;

import java.util.ArrayList;
import java.util.List;

import org.kcl.nestor.env.action.ExternalAction;

public class move implements ExternalAction<ScriptedEnvironment> {
	public static final String FUNCTOR="move";

	public List<Literal> consequences(ScriptedEnvironment env, String agName,
			Term... terms) {
		ArrayList<Literal> consequences = new ArrayList<Literal>(4);
		List<Literal> percepts = env.getPercepts("");
		
		logger.info("Moving "+agName+" from "+terms[0]+" to "+terms[1]);
		Literal precond0 = Literal.parseLiteral("at("+terms[0]+")");
		precond0.setNegated(false);
		Literal effect0 = Literal.parseLiteral("at("+terms[1]+")");
		
		Literal batt = env.findLiteralByFunctor("batt", percepts);
		Literal resBatt = updateBattery(batt);
		batt = new Literal(false, batt);
		
		consequences.add(precond0);
		consequences.add(effect0);
		consequences.add(batt);
		consequences.add(resBatt);
		
		return consequences;
	}

	public boolean execute(ScriptedEnvironment env, String agName, Term... terms) {
		List<Literal> percepts = env.getPercepts("");
		
		logger.info("Moving "+agName+" from "+terms[0]+" to "+terms[1]);
		Literal precond0 = Literal.parseLiteral("at("+terms[0]+")");
		Literal effect0 = Literal.parseLiteral("at("+terms[1]+")");
		
		/*if(findMatchingLiteral(precond0, percepts) == null)
			return false;*/
		
		env.removePercept(precond0);
		env.addPercept(effect0);
		
		Literal batt = env.findLiteralByFunctor("batt", percepts);
		if(batt != null) {
			env.removePercept(batt);
			env.addPercept(updateBattery(batt));
		}
		
		return true;
	}

	public String getFunctor() {
		return FUNCTOR;
	}
	
	public Literal updateBattery(Literal battLiteral) {
		Term t0 = battLiteral.getTerm(0);
		Literal retLiteral = battLiteral;
		if(t0.isNumeric()) {
			NumberTerm term = (NumberTerm) t0;
			int battCondition = (int) term.solve();
			battCondition--;
			retLiteral = Literal.parseLiteral("batt("+battCondition+")");
			//logger.info("Batt is "+battCondition);
		}
		
		return retLiteral;
	}

}
