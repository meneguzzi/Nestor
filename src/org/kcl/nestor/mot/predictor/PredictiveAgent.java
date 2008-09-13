package org.kcl.nestor.mot.predictor;

import jason.JasonException;
import jason.RevisionFailedException;
import jason.architecture.AgArch;
import jason.asSemantics.Agent;
import jason.asSemantics.Circumstance;
import jason.asSemantics.InternalAction;
import jason.asSemantics.Option;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.BodyLiteral;
import jason.asSyntax.Literal;
import jason.asSyntax.PlanBody;
import jason.asSyntax.PlanBodyImpl;
import jason.asSyntax.PlanLibrary;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import jason.asSyntax.Trigger;
import jason.bb.BeliefBase;
import jason.runtime.Settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * An agent class intended to simulate the execution of plans and subplans
 * without modifying the base agent.
 * 
 * @author meneguzz
 *
 */
public class PredictiveAgent extends Agent {
	protected static final Term sourceSelf = Structure.parse("source(self)");
	protected static final Term sourcePercept = Structure.parse("source(percept)");
	protected static final Term sourceAny = Structure.parse("_");
	
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
		this.bb = predictiveBeliefBase;
		this.pl = (PlanLibrary) agent.getPL().clone();
		AgArch agArch = new SimpleAgArch();
		this.ts = new TransitionSystem(this, new Circumstance(), new Settings(), agArch);
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
	public InternalAction getIA(String action) throws ClassNotFoundException, InstantiationException, IllegalAccessException  {
		return baseAgent.getIA(action);
	}
	
	public List<Literal> getConsequences(PlanBody planBody, Unifier unifier) {
		List<Literal> consequences = new ArrayList<Literal>();
		try {
			if(this.executeStep(planBody, unifier)) {
				consequences = this.getDelta();
			} else {
				consequences.add(Literal.LFalse);
			}
		} catch (RevisionFailedException e) {
			e.printStackTrace();
			consequences.add(Literal.LFalse);
		} 
		return consequences;
	}
	
	public boolean executeOption(Option option) {
		//We don't want to affect the original option
		option = (Option) option.clone();
		
		//Remove all percepts
		/*for(Iterator<Literal> i = this.fBB.getPercepts(); i.hasNext(); ) {
			Literal l = i.next();
			l.clearAnnots();
			l.addAnnot(BeliefBase.TPercept);
			this.fBB.remove(l);
		}*/
		
		for(PlanBody planBody : (PlanBodyImpl)option.getPlan().getBody()) {
			try {
				if(!executeStep(planBody, option.getUnifier())) {
					return false;
				}
			} catch (RevisionFailedException e) {
				e.printStackTrace();
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
	 * @param planBody
	 * @param unifier
	 * @return
	 * @throws RevisionFailedException 
	 */
	@SuppressWarnings("unchecked")
	public boolean executeStep(PlanBody planBody, Unifier unifier) throws RevisionFailedException {
		Literal lit = (Literal) planBody.getBodyTerm().clone();
		lit.addAnnot(PredictiveAgent.sourceSelf);
		//lit.addAnnot(PredictiveAgent.sourcePercept);
		lit.apply(unifier);
		
		switch (planBody.getBodyType()) {
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
			Trigger trigger = new Trigger(Trigger.TEOperator.add, Trigger.TEType.achieve, lit);
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
					Object oresult = this.getIA(lit.toString()).execute(getTS(), unifier, clone);
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
				Trigger trig = new Trigger(Trigger.TEOperator.add, Trigger.TEType.test, lit);
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
