package org.kcl.nestor.env.scripted;

import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Term;

import java.util.logging.Logger;

import org.kcl.nestor.env.ModularEnvironment;
import org.kcl.nestor.env.action.ExternalAction;

public class ScriptedEnvironmentActions extends ModularEnvironment<ScriptedEnvironment> {
	protected Logger logger = Logger.getLogger(ScriptedEnvironment.class.getName());
	
	public ScriptedEnvironmentActions(ScriptedEnvironment env) {
		super(env);
		addActions();
	}
	
	private void addActions() {
		try {
			this.addExternalAction(this.getClass().getPackage().getName()+".charge");
			this.addExternalAction(this.getClass().getPackage().getName()+".drop");
			this.addExternalAction(this.getClass().getPackage().getName()+".move");
			this.addExternalAction(this.getClass().getPackage().getName()+".pickup");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		ExternalAction<ScriptedEnvironment> action;
//		action = new charge();
//		this.actions.put(action.getFunctor(), action);
//		action = new drop();
//		this.actions.put(action.getFunctor(), action);
//		action = new move();
//		this.actions.put(action.getFunctor(), action);
//		action = new pickup();
//		this.actions.put(action.getFunctor(), action);
	}

	public boolean executeAction(String agName, Term act) {
		boolean success = true;
		
		ExternalAction<ScriptedEnvironment> action = actions.get(act.getFunctor());
		if(action != null) {
			success = action.execute(env, agName, act.getTermsArray());
		} else {
			success = false;
		}
		
		if(!success) {
			logger.info("Action "+act.toString()+" failed");
		}
		return success;
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
