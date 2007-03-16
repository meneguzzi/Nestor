package org.kcl.nestor.mot.predictor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import jason.JasonException;
import jason.architecture.AgArch;
import jason.architecture.AgArchInfraTier;
import jason.asSemantics.Agent;
import jason.asSemantics.Circumstance;
import jason.asSemantics.InternalAction;
import jason.asSemantics.Option;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.BodyLiteral;
import jason.asSyntax.Literal;
import jason.asSyntax.PlanLibrary;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import jason.asSyntax.Trigger;
import jason.infra.centralised.CentralisedAgArch;
import jason.runtime.Settings;

/**
 * An agent class intended to simulate the execution of plans and subplans
 * without modifying the base agent.
 * 
 * @author meneguzz
 *
 */
public class PredictiveAgent extends Agent {
	protected PredictiveBeliefBase predictiveBeliefBase;
	protected Agent baseAgent;
	
	//XXX Perhaps make this parameterizable at a later date
	private static final String ignorableInternalActions[] = { ".wait" };

	static {
		Arrays.sort(ignorableInternalActions);
	}
	
	public PredictiveAgent(Agent agent) {
		this.baseAgent = agent;
		this.predictiveBeliefBase = new PredictiveBeliefBase(agent.getBB());
		this.fBB = predictiveBeliefBase;
		AgArch agArch = new AgArch();
		CentralisedAgArch infraTier = new CentralisedAgArch();
		infraTier.setAgName("Predictor");
		agArch.setArchInfraTier(infraTier);
		
		this.fTS = new TransitionSystem(this, new Circumstance(), new Settings(), agArch);
	}
	
	/**
	 * 
	 * @param actionName
	 * @return
	 */
	public boolean ignoreInternalAction(String actionName) {
		return (Arrays.binarySearch(ignorableInternalActions, actionName) >= 0);
	}
	
	@Override
	public PlanLibrary getPL() {
		return baseAgent.getPL();
	}
	
	@Override
	public TransitionSystem getTS() {
		return baseAgent.getTS();
	}
	
	@Override
	public InternalAction getIA(Structure action) throws Exception {
		return baseAgent.getIA(action);
	}
	
	public List<Literal> getConsequences(BodyLiteral bodyLiteral, Unifier unifier) {
		List<Literal> consequences = new ArrayList<Literal>();
		if(this.executeStep(bodyLiteral, unifier)) {
			consequences = this.getDelta();
		} else {
			consequences.add(Literal.LFalse);
		} 
		return consequences;
	}
	
	public boolean executeOption(Option option) {
		//We don't want to affect the original option
		option = (Option) option.clone();
		
		for(Iterator<BodyLiteral> i = option.getPlan().getBody().iterator();
			i.hasNext();) {
			BodyLiteral bodyLiteral = i.next();
			if(!executeStep(bodyLiteral, option.getUnif())) {
				return false;
			}
		}
		return true;
	}
	
	public boolean processTrigger(Trigger trigger) {
		try {
			List<Option> relevant = getTS().relevantPlans(trigger);
			List<Option> applicable = getTS().applicablePlans(relevant);
			if (applicable.size() > 0) {
				Option option = applicable.get(0);
				return executeOption(option);
			} else {
				return false;
			}
		} catch (JasonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Executes a single plan-step for an AgentSpeak plan
	 * @param bodyLiteral
	 * @param unifier
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean executeStep(BodyLiteral bodyLiteral, Unifier unifier) {
		Literal lit = bodyLiteral.getLiteralFormula();
		lit = (Literal) lit.clone();
		lit.apply(unifier);
		
		switch (bodyLiteral.getType()) {
		case delAddBel:
			Literal del = (Literal) lit.clone();
			del.makeVarsAnnon();
			Unifier delUnifier = new Unifier();
			if (believes(del, delUnifier)) {
				delBel(del);
			}
			break;
		case delBel:
			delBel(lit);
			break;
		case addBel:
			addBel(lit);
			break;
		case achieveNF:
		case achieve:
			// "execute" new plan in our little sandbox
			lit.makeVarsAnnon();
			Trigger trigger = new Trigger(false, Trigger.TEAchvG, lit);
			return processTrigger(trigger);
			// logger.fine("Ignoring subgoal "+bodyLiteral);
			//break;
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
					Object oresult = this.getIA(lit).execute(getTS(), unifier, clone);
					boolean ok = false;
					ok = oresult instanceof Boolean && (Boolean)oresult;
                    if (!ok && oresult instanceof Iterator) { // ia result is an Iterator
                        Iterator<Unifier> iu = (Iterator<Unifier>)oresult;
                        if (iu.hasNext()) {
                            // change the unifier of the current IM to the first returned by the IA
                            unifier = iu.next(); 
                            ok = true;
                        }
                    }
                    return ok;
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
			if (!this.believes(lit, unifier)) {
				lit.makeVarsAnnon();
				Trigger trig = new Trigger(false, Trigger.TETestG, lit);
				return processTrigger(trig);
			}
			break;
		default:
			break;
		}
		return true;
	}
	
	public List<Literal> getDelta() {
		return this.predictiveBeliefBase.getDelta();
	}
	
	public void reset() {
		this.predictiveBeliefBase.reset();
	}
}
