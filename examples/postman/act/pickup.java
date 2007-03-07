package act;

import jason.asSyntax.Literal;
import jason.asSyntax.Term;

import java.util.ArrayList;
import java.util.List;

import org.kcl.nestor.env.action.ExternalAction;
import org.kcl.nestor.env.scripted.ScriptedEnvironment;

public class pickup implements ExternalAction<ScriptedEnvironment> {
	public static final String FUNCTOR="pickup";

	public List<Literal> consequences(ScriptedEnvironment env, String agName,
			Term... terms) {
		List<Literal> percepts = env.getPercepts("johnny");
		List<Literal> consequences = new ArrayList<Literal>(1);
		
		Literal prot = Literal.parseLiteral("over("+terms[0]+", A)");
		Literal remove = env.findMatchingLiteral(prot, percepts);
		if(remove != null) {
			remove = new Literal(false, remove);
			consequences.add(remove);
			consequences.add(Literal.parseLiteral("held("+terms[0]+")"));
		} else {
			consequences.add(Literal.LFalse);
		}
		
		return consequences;
	}

	public boolean execute(ScriptedEnvironment env, String agName,
			Term... terms) {
		boolean success = true;
		List<Literal> percepts = env.getPercepts("johnny");
		
		Literal prot = Literal.parseLiteral("over("+terms[0]+", A)");
		Literal remove = env.findMatchingLiteral(prot, percepts);
		if(remove != null) {
			env.removePercept(remove);
			env.addPercept(Literal.parseLiteral("held("+terms[0]+")"));
			logger.info(agName+" picking up "+terms[0]+" from "+remove.getTerm(1));
		} else {
			success = false;
		}
		return success;
	}

	public String getFunctor() {
		return FUNCTOR;
	}

}
