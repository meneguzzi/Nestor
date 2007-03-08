package rover.act;

import jason.asSyntax.Literal;
import jason.asSyntax.Term;

import java.util.ArrayList;
import java.util.List;

import org.kcl.nestor.env.action.ExternalAction;
import org.kcl.nestor.env.scripted.ScriptedEnvironment;

public class charge implements ExternalAction<ScriptedEnvironment> {
	public static final String FUNCTOR = "charge";
	
	public static final int chargeAmount = 40;

	public List<Literal> consequences(ScriptedEnvironment env, String agName, Term... terms) {
		ArrayList<Literal> consequences = new ArrayList<Literal>(4);
		List<Literal> percepts = env.getPercepts("");
		
		Literal precond = env.findLiteralByFunctor("battery", percepts);
		precond.setNegated(false);
		Literal effect = Literal.parseLiteral("battery(20)");
		
		consequences.add(precond);
		consequences.add(effect);
		
		return consequences;
	}

	public boolean execute(ScriptedEnvironment env, String agName, Term... terms) {
		List<Literal> percepts = env.getPercepts("");
		
		logger.info("Charging");
		Literal precond = env.findLiteralByFunctor("battery", percepts);
		Literal effect = Literal.parseLiteral("battery("+chargeAmount+")");
		
		env.removePercept(precond);
		env.addPercept(effect);
		
		return true;
	}

	public String getFunctor() {
		return FUNCTOR;
	}

}
