package org.kcl.nestor.mot.predictor;

import jason.asSemantics.Agent;
import jason.asSemantics.Circumstance;
import jason.asSemantics.Event;
import jason.asSemantics.IntendedMeans;
import jason.asSemantics.Intention;
import jason.asSemantics.InternalAction;
import jason.asSemantics.Option;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.PlanLibrary;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import jason.asSyntax.Trigger;
import jason.bb.BeliefBase;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * A simplified agent class aimed at simulating the behaviour of another agent
 * by following a reasoning cycle disconnected from the environment as well as
 * other agents.
 * 
 * @author Felipe Meneguzzi
 * 
 */
public class DummyAgent extends Agent {
	protected static final Logger logger = Logger.getLogger(DummyAgent.class
			.getName());

	protected PredictiveBeliefBase beliefBase;

	// XXX Perhaps make this parameterizable at a later date
	private static final String ignorableInternalActions[] = { ".wait", ".print" };

	private static final InternalAction noop = new NoopAction();

	static {
		Arrays.sort(ignorableInternalActions);
	}

	public DummyAgent(Agent agent) {
		SimpleAgArch agArch = new SimpleAgArch();

		this.setLogger(agArch);
		logger.setLevel(agent.getTS().getSettings().logLevel());

		this.beliefBase = new PredictiveBeliefBase((BeliefBase) agent.getBB().clone());
		this.bb = beliefBase;

		this.pl = (PlanLibrary) agent.getPL().clone();
		this.aslSource = agent.getASLSrc();

//		this.setTS(new TransitionSystem(this, 
//				(Circumstance) agent.getTS().getC().clone(), 
//				agent.getTS().getSettings(), 
//				agArch));

		this.setTS(new TransitionSystem(this, 
				   new Circumstance(), 
				   agent.getTS().getSettings(), 
				   agArch));
	}

	public BeliefBase getOriginalBB() {
		return this.bb;
	}

	@Override
	public BeliefBase getBB() {
		return beliefBase;
	}

	@Override
	public InternalAction getIA(Structure action) throws Exception {
		// If the interpreter wants an action that is supposed to be ignored,
		// return our empty noop.
		if (!ignoreInternalAction(action.getFunctor())) {
			return super.getIA(action);
		} else {
			return noop;
		}
	}

	/**
	 * 
	 * @param actionName
	 * @return
	 */
	public boolean ignoreInternalAction(String actionName) {
		return (Arrays.binarySearch(ignorableInternalActions, actionName) >= 0);
	}

	public List<Literal> getDelta() {
		return this.beliefBase.getDelta();
	}

	public void reset() {
		this.beliefBase.reset();
	}

	public void removePercepts() {
		//logger.info("Removing percepts");
		for (Iterator<Literal> i = this.getBB().getPercepts(); i.hasNext();) {
			Literal l = i.next();
			l = (Literal) l.clone();
			l.clearAnnots();
			l.addAnnot(BeliefBase.TPercept);
			this.getBB().remove(l);
		}
	}

	/**
	 * Directly executes a certain option using this dummy agent
	 * 
	 * @param option
	 * @return
	 */
	public synchronized boolean executeOption(Option option) {
		logger.info("Simulating plan " + option.getPlan());

		this.removePercepts();

		Intention newIntention = new Intention();
		Trigger trigger = (Trigger) option.getPlan().getTriggerEvent().clone();
		IntendedMeans intendedMeans = new IntendedMeans(
				(Option) option.clone(), trigger);
		newIntention.push(intendedMeans);
		// newIntention.setAtomic(true);
		this.getTS().getC().addIntention(newIntention);
		
		// To check if we had plan failure
		Literal triggerLiteral = (Literal) option.getPlan().getTriggerEvent().getLiteral().clone();
		triggerLiteral.apply((Unifier) option.getUnifier().clone());
		
		
		int numberOfSubgoals = 0;
		while (this.getTS().getC().hasIntention() || 
			   this.getTS().getC().hasEvent() || 
			   this.getTS().getC().hasPendingAction() ||
			   this.getTS().getC().hasPendingIntention()) {
			// while(numberOfCycles-- > 0) {
			/*logger.info("Cycle " + numberOfCycles
					+ ":Simulated intentions are: "
					+ this.getTS().getC().getIntentions());
			logger.info("Cycle " + numberOfCycles
					+ ":Simulated events are: "
					+ this.getTS().getC().getEvents());*/
			this.getTS().reasoningCycle();
			
			//Need to check the events after the reasoning cycle to determine
			//whether or not the original plan failed, and the number of subgoals
			//that were invoked
			for(Iterator<Event> i = this.getTS().getC().getEvents().iterator();
				i.hasNext();) {
				Event e = i.next();
				//If there is a goal event, we increment the number of subgoals
				if(e.getTrigger().isGoal()) {
					numberOfSubgoals++;
				}
				//If the event is a deletion, we must check
				//if it is the deletion of the goal from the
				//option we are trying to simulate, this means
				//that it has failed
				if(!e.getTrigger().isAddition() && e.getTrigger().isGoal()) {
					//e.getTrigger().getLiteral().equalsAsTerm(triggerLiteral);
					//TODO modify this to really compare the triggers
					return false;
				}
			}
		}
		/*logger.info("Finished simulation, delta beliefs are: "
				+ this.beliefBase.getDelta());*/

		return true;
	}

	/**
	 * An internal action that does nothing, to be returned when an action is
	 * expected to be ignored by this agent.
	 * 
	 * @author meneguzz
	 * 
	 */
	public static class NoopAction implements InternalAction {

		public Object execute(TransitionSystem ts, Unifier un, Term[] args)
				throws Exception {
			logger.fine("Noop");
			return true;
		}

		public boolean suspendIntention() {
			// TODO Auto-generated method stub
			return false;
		}

	}
}
