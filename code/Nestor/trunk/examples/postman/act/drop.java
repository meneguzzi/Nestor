package act;

import jason.asSyntax.Literal;
import jason.asSyntax.LiteralImpl;
import jason.asSyntax.Term;

import java.util.ArrayList;
import java.util.List;

import org.kcl.nestor.env.action.ExternalAction;
import org.kcl.nestor.env.scripted.ScriptedEnvironment;

public class drop implements ExternalAction<ScriptedEnvironment> {
	public static final String FUNCTOR="drop";

	public List<Literal> consequences(ScriptedEnvironment env, String agName,
			Term... terms) {
		List<Literal> percepts = env.getPercepts("johnny");
		List<Literal> consequences = new ArrayList<Literal>(1);
		
		Literal prot = Literal.parseLiteral("held("+terms[0]+")");
		Literal remove = env.findMatchingLiteral(prot, percepts);
		
		if(remove != null) {
			remove = new LiteralImpl(false, remove);
			prot = Literal.parseLiteral("at(A)");
			Literal add = env.findMatchingLiteral(prot, percepts);
			if(add != null) {
				Term t1 = add.getTerm(0);
				add = Literal.parseLiteral("over("+terms[0]+","+t1+")");
				consequences.add(add);
				consequences.add(remove);
			} else {
				consequences.add(Literal.LFalse);
			}
		} else {
			consequences.add(Literal.LFalse);
		}
		
		return consequences;
	}

	public boolean execute(ScriptedEnvironment env, String agName,
			Term... terms) {
		boolean success = true;
		List<Literal> percepts = env.getPercepts("johnny");
		
		Literal prot = Literal.parseLiteral("held("+terms[0]+")");
		Literal remove = env.findMatchingLiteral(prot, percepts);
		if(remove != null) {
			env.removePercept(remove);
		} else {
			success = false;
		}
		
		prot = Literal.parseLiteral("at(A)");
		remove = env.findMatchingLiteral(prot, percepts);
		if(remove != null) {
			Term t1 = remove.getTerm(0);
			env.addPercept(Literal.parseLiteral("over("+terms[0]+","+t1+")"));
			logger.info(agName+" dropping "+terms[0]+" at "+t1);
		} else {
			success = false;
		}
		
		return success;
	}

	public String getFunctor() {
		return FUNCTOR;
	}

}
