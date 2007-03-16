package org.kcl.nestor.mot.predictor;

import jason.JasonException;
import jason.asSemantics.Agent;
import jason.asSemantics.Option;
import jason.asSemantics.Unifier;
import jason.asSyntax.BodyLiteral;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import jason.asSyntax.Trigger;
import jason.bb.BeliefBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * A MockUp version of an AgentSpeak agent, which is used to make predictions of
 * how a certain agent will behave, but without actually affecting the
 * environment.
 * 
 * @author meneguzz
 * 
 */
public class MockUpAgent extends Agent {
	protected static final Logger logger = Logger.getLogger(MockUpAgent.class
			.getName());

	protected Agent agent;

	protected PredictiveBeliefBase beliefBase;

	// XXX Perhaps make this parameterizable at a later date
	private static final String ignorableInternalActions[] = { ".wait" };

	static {
		Arrays.sort(ignorableInternalActions);
	}

	public MockUpAgent(Agent agent) {
		this.agent = agent;
		this.beliefBase = new PredictiveBeliefBase(agent.getBB());
	}

	/**
	 * 
	 * @param actionName
	 * @return
	 */
	public boolean ignoreInternalAction(String actionName) {
		return (Arrays.binarySearch(ignorableInternalActions, actionName) >= 0);
	}

	/**
	 * Return the expected consequences of a plan step in terms of belief
	 * additions and belief deletions, if the step is successful, otherwise the
	 * list will contain the default <code>false</code> Literal.
	 * 
	 * @param bodyLiteral
	 *            A <code>BodyLiteral</code> representing the plan step whose
	 *            consequences are to be inferred.
	 * @param unif
	 *            TODO
	 * @return A <code>List</code> containing the beliefs modifications that
	 *         should result from the action execution, if the step is
	 *         successful, otherwise the list will contain the default
	 *         <code>false</code> Literal.
	 */
	public List<Literal> getConsequences(BodyLiteral bodyLiteral, Unifier unif) {
		logger.fine("Getting consequences for " + bodyLiteral);
		List<Literal> consequences = new ArrayList<Literal>();
		// unif = (Unifier) unif.clone();
		Literal lit = (Literal) bodyLiteral.getLiteralFormula().clone();
		lit.apply(unif);

		switch (bodyLiteral.getType()) {
		case delAddBel:
			Literal del = (Literal) lit.clone();
			del.makeVarsAnnon();
			Unifier delUnifier = new Unifier();
			if (believes(del, delUnifier)) {
				del.apply(delUnifier);
				del.setNegated(false);
				consequences.add(del);
			}
			break;
		case delBel:
			lit.setNegated(false);
		case addBel:
			consequences.add(lit);
			break;
		case achieveNF:
		case achieve:
			// "execute" new plan in our little sandbox
			lit.makeVarsAnnon();
			Trigger trigger = new Trigger(false, Trigger.TEAchvG, lit);
			consequences.addAll(getConsequences(trigger));
			// logger.fine("Ignoring subgoal "+bodyLiteral);
			break;
		case action:
			// TODO get the consequences from the simulator
			break;
		case internalAction:
			if (!ignoreInternalAction(lit.getFunctor())) {
				try {
					Term[] current = lit.getTermsArray();
					Term[] clone = new Term[current.length];
					for (int i = 0; i < clone.length; i++) {
						clone[i] = (Term) current[i].clone();
					}

					// calls execute
					Object oresult;

					oresult = this.getIA(lit)
							.execute(this.getTS(), unif, clone);
					if (oresult instanceof Boolean && (Boolean) oresult) {
						// return LogExpr.createUnifIterator(un);
					} else if (oresult instanceof Iterator) {
						// return ((Iterator<Unifier>)oresult);
					} else {
						consequences.clear();
						consequences.add(Literal.LFalse);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		case constraint:
			// TODO find out what this means
			break;
		case test:
			if (!this.believes(lit, unif)) {
				lit.makeVarsAnnon();
				Trigger trig = new Trigger(false, Trigger.TETestG, lit);
				consequences.addAll(getConsequences(trig));
			}
			break;
		default:
			break;
		}

		return consequences;
	}

	public List<Literal> getConsequences(Trigger trigger) {
		List<Literal> consequences = new ArrayList<Literal>();
		try {
			List<Option> relevant = this.agent.getTS().relevantPlans(trigger);
			List<Option> applicable = this.agent.getTS().applicablePlans(
					relevant);
			if (applicable.size() > 0) {
				Option option = applicable.get(0);
				consequences.addAll(getConsequences(option));
			} else {
				consequences.add(Literal.LFalse);
			}
		} catch (JasonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			consequences.clear();
			consequences.add(Literal.LFalse);
		}
		return consequences;
	}

	public List<Literal> getConsequences(Option option) {
		List<Literal> consequences = new ArrayList<Literal>();
		option = (Option) option.clone();

		for (Iterator<BodyLiteral> i = option.getPlan().getBody().iterator(); i
				.hasNext();) {
			BodyLiteral literal = i.next();
			List<Literal> stepConsequences = getConsequences(literal, option.getUnif());
			// if this plan would fail, then its consequences are a simple
			// failure
			if (consequences.size() == 1
					&& consequences.get(0) == Literal.LFalse) {
				consequences.clear();
				consequences.add(Literal.LFalse);
				break;
			} else {
				// otherwise, add the consequences of this step to the global
				// consequences
				//consequences.addAll(stepConsequences);
				this.beliefBase.applyBeliefs(stepConsequences);
			}
		}
		if(consequences.size() != 1) {
			consequences = this.beliefBase.getDelta();
		}
		logger.info("Consequences of plan: " + option + " are " + consequences);
		return consequences;
	}
	
	/* Initial version of this method
	public List<Literal> getConsequences(Option option) {
		List<Literal> consequences = new ArrayList<Literal>();
		option = (Option) option.clone();

		for (Iterator<BodyLiteral> i = option.getPlan().getBody().iterator(); i
				.hasNext();) {
			BodyLiteral literal = i.next();
			List<Literal> stepConsequences = getConsequences(literal, option
					.getUnif());
			// if this plan would fail, then its consequences are a simple
			// failure
			if (consequences.size() == 1
					&& consequences.get(0) == Literal.LFalse) {
				consequences.clear();
				consequences.add(Literal.LFalse);
				break;
			} else {
				// otherwise, add the consequences of this step to the global
				// consequences
				consequences.addAll(stepConsequences);
			}
		}
		logger.info("Consequences of plan: " + option + " are " + consequences);
		return consequences;
	}
	*/

	public BeliefBase getBB() {
		return this.beliefBase;
	}

	public boolean addBel(Literal bel) {
		return this.beliefBase.add(bel);
	}

	public boolean delBel(Literal bel) {
		return this.beliefBase.remove(bel);
	}

	public void reset() {
		this.beliefBase.reset();
	}
}
