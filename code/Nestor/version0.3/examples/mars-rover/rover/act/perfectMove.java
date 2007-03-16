package rover.act;

import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Term;

import java.util.ArrayList;
import java.util.List;

import org.kcl.nestor.env.action.ExternalAction;
import org.kcl.nestor.env.scripted.ScriptedEnvironment;

public class perfectMove implements ExternalAction<ScriptedEnvironment> {
	public static final String FUNCTOR="perfectMove";

	public List<Literal> consequences(ScriptedEnvironment env, String agName, Term... terms) {
		ArrayList<Literal> consequences = new ArrayList<Literal>(4);
		List<Literal> percepts = env.getPercepts("");
		
		//logger.info("Moving to "+terms[0]+","+terms[1]);
		Literal precond = env.findLiteralByFunctor("at", percepts);
		precond.setNegated(false);
		Literal effect0 = Literal.parseLiteral("at("+terms[0]+","+terms[1]+")");
		
		consequences.add(precond);
		consequences.add(effect0);
		
		return consequences;
	}
	
	public boolean execute(ScriptedEnvironment env, String agName, Term... terms) {
		List<Literal> percepts = env.getPercepts("");
		
		if(terms.length != 2) {
			logger.warning("Wrong number of parameters for move, expected 2, was "+terms.length);
			return false;
		}
		
		//logger.info("Moving to "+terms[0]+","+terms[1]);
		Literal precond = env.findLiteralByFunctor("at", percepts);
		if(terms[0].isNumeric() && terms[1].isNumeric()) {
			NumberTerm term0 = (NumberTerm) terms[0];
			NumberTerm term1 = (NumberTerm) terms[1];
			
			NumberTerm at0 = (NumberTerm) precond.getTerm(0);
			NumberTerm at1 = (NumberTerm) precond.getTerm(1);
			//If the agent tries to move more than one unit of distance, 
			//It is an error
			if( (Math.abs(term0.solve() - at0.solve()) > 1) ||
				(Math.abs(term1.solve() - at1.solve()) > 1)) {
				logger.warning("Tried to move more than 1 unit of distance.");
				return false;
			}
		}
		//If otherwise things went smooth, generate the resulting literals
		//And update the percepts
		Literal effect0 = Literal.parseLiteral("at("+terms[0]+","+terms[1]+")");
		
		boolean ret = true;
		
		ret &= env.removePercept(precond);
		env.addPercept(effect0);
		
		return ret;
	}

	public String getFunctor() {
		return FUNCTOR;
	}

}
