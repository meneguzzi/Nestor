package rover.act;

import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Term;

import java.util.ArrayList;
import java.util.List;

import org.kcl.nestor.env.action.ExternalAction;
import org.kcl.nestor.env.scripted.ScriptedEnvironment;

public class move implements ExternalAction<ScriptedEnvironment> {
	public static final String FUNCTOR="move";

	public List<Literal> consequences(ScriptedEnvironment env, String agName, Term... terms) {
		ArrayList<Literal> consequences = new ArrayList<Literal>(4);
		List<Literal> percepts = env.getPercepts("");
		
		//logger.info("Moving to "+terms[0]+","+terms[1]);
		Literal precond = env.findLiteralByFunctor("at", percepts);
		precond.setNegated(false);
		Literal effect0 = Literal.parseLiteral("at("+terms[0]+","+terms[1]+")");
		
		Literal batt = env.findLiteralByFunctor("battery", percepts);
		Literal resBatt;
		try {
			resBatt = updateBattery(batt);
		} catch (Exception e) {
			logger.warning("No recent battery percept.");
			resBatt = batt;
		}
		batt = new Literal(false, batt);
		
		consequences.add(precond);
		consequences.add(effect0);
		consequences.add(batt);
		consequences.add(resBatt);
		
		return consequences;
	}
	
	protected Literal updateBattery(Literal batt) throws Exception {
		Term charge = batt.getTerm(0);
		if(charge.isNumeric()) {
			NumberTerm term = (NumberTerm) charge;
			if(term.solve() <= 0) {
				throw new Exception("Rover battery is empty");			}
			charge = new NumberTermImpl(term.solve() - 1);
		}
		return Literal.parseLiteral("battery("+charge+")");
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
		Literal batt = env.findLiteralByFunctor("battery", percepts);
		Literal resBatt;
		try {
			resBatt = updateBattery(batt);
		} catch (Exception e) {
			logger.warning("Rover battery is empty");
			return false;
		}
		
		boolean ret = true;
		
		ret &= env.removePercept(precond);
		env.addPercept(effect0);
		ret &= env.removePercept(batt);
		env.addPercept(resBatt);
		
		return ret;
	}

	public String getFunctor() {
		return FUNCTOR;
	}

}
