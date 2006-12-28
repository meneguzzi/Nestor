/**
 * 
 */
package org.kcl.nestor.mot;

import jason.asSemantics.ActionExec;
import jason.asSemantics.Agent;
import jason.asSemantics.Event;
import jason.asSemantics.Intention;
import jason.asSemantics.Message;
import jason.asSemantics.Option;
import jason.asSyntax.Literal;
import jason.asSyntax.Trigger;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.logging.Logger;

import org.kcl.nestor.mot.functions.ActionSelectionFunction;
import org.kcl.nestor.mot.functions.BeliefRevisionFunction;
import org.kcl.nestor.mot.functions.BeliefUpdateFunction;
import org.kcl.nestor.mot.functions.EventSelectionFunction;
import org.kcl.nestor.mot.functions.IntentionSelectionFunction;
import org.kcl.nestor.mot.functions.MessageSelectionFunction;
import org.kcl.nestor.mot.functions.OptionSelectionFunction;
import org.kcl.nestor.mot.functions.defaults.DefaultActionSelectionFunction;
import org.kcl.nestor.mot.functions.defaults.DefaultBeliefRevisionFunction;
import org.kcl.nestor.mot.functions.defaults.DefaultMessageSelectionFunction;
import org.kcl.nestor.mot.functions.impl.MotivatedBeliefUpdate;
import org.kcl.nestor.mot.functions.impl.MotivatedEventSelection;
import org.kcl.nestor.mot.functions.impl.MotivatedIntentionSelection;
import org.kcl.nestor.mot.functions.impl.MotivatedOptionSelection;
import org.kcl.nestor.mot.parser.MotivationParser;

/** 
 * This class represents a motivated agent, capable of generating goals autonomously
 * while responding to a model of motivational intensity.
 * 
 * @author Felipe Rech Meneguzzi
 */
public class MotivatedAgent extends Agent {
	
	protected static final Logger logger = Logger.getLogger(Agent.class.getName());
	
	protected ActionSelectionFunction actionSelectionFunction = null;
	protected BeliefRevisionFunction beliefRevisionFunction = null;
	protected BeliefUpdateFunction beliefUpdateFunction = null;
	protected EventSelectionFunction<MotivatedAgent> eventSelectionFunction = null;
	protected IntentionSelectionFunction intentionSelectionFunction = null;
	protected MessageSelectionFunction messageSelectionFunction = null;
	protected OptionSelectionFunction<MotivatedAgent> optionSelectionFunction = null;
	
	/**
	 * 
	 */
	protected List<Motivation> motivations;
	
	/**
	 * 
	 */
	protected Hashtable<Trigger, Motivation> pendingMotivatedGoals;
	
	/**
	 * An intention used solely for the addition of motivated Goals
	 */
	protected Intention motivatedIntention;

	/**
	 * The list of motivations that have been triggered
	 */
	protected List<Motivation> pendingMotivations;
	
	public MotivatedAgent() {
		this.motivations = new ArrayList<Motivation>();
		this.motivatedIntention = new Intention();
		
		this.pendingMotivations = new ArrayList<Motivation>();
		this.pendingMotivatedGoals = new Hashtable<Trigger, Motivation>();
		
		//TODO Review the configuration of the functions 
		//TODO and generalize its instantiation process
		
		this.beliefUpdateFunction = new MotivatedBeliefUpdate();
		this.optionSelectionFunction = new MotivatedOptionSelection();
		this.intentionSelectionFunction = new MotivatedIntentionSelection();
		this.eventSelectionFunction = new MotivatedEventSelection();
		
		//Default function implementations
		this.actionSelectionFunction = new DefaultActionSelectionFunction();
		this.beliefRevisionFunction = new DefaultBeliefRevisionFunction();
		this.messageSelectionFunction = new DefaultMessageSelectionFunction();
		
		//XXX Review this
		try {
			readMotivations("motivations.mot");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads the agent's motivations from the file named in the supplied string.
	 * @param filename The name of a file containing the description of motivational functions
	 * @throws Exception
	 */
	public void readMotivations(String filename) throws Exception {
		this.motivations.addAll(MotivationParser.parseFile(filename));
	}

	/**
	 * Posts a motivation-generated goal to the agent's events, while keeping
	 * a reference to the posted trigger for future removal.
	 * @param trigger
	 * @param motivation TODO
	 */
	public void addMotivatedGoal(Trigger trigger, Motivation motivation) {
		this.pendingMotivatedGoals.put(trigger, motivation);
		this.fTS.updateEvents(new Event(trigger, motivatedIntention));
	}
	
	/**
	 * Returns a reference to the named motivation from this agent if this agent,
	 * has one such motivation, otherwise returns null.
	 * @param motivationName The name of a motivation.
	 * @return The named motivation, if the agent has one such motivation, or null otherwise.
	 */
	public Motivation getMotivation(String motivationName) {
		for (Iterator iter = motivations.iterator(); iter.hasNext();) {
			Motivation motivation = (Motivation) iter.next();
			if(motivation.getMotivationName().equals(motivationName)) {
				return motivation;
			}
		}
		return null;
	}
	
	/**
	 * Returns all of the motivations from this agent.
	 * @return A <code>List</code> containing this agent's motivations. 
	 */
	public List<Motivation> getMotivations() {
		return motivations;
	}
	
	/**
	 * @return the triggeredMotivations
	 */
	public List<Motivation> getPendingMotivations() {
		return pendingMotivations;
	}
	
	public void removePendingMotivation(Motivation motivation) {
		//removes a pending motivation, as well as the list of goals associated
		//to this motivation
		this.pendingMotivations.remove(motivation);
		for (Trigger pendTrigger : pendingMotivatedGoals.keySet()) {
			if(pendingMotivatedGoals.get(pendTrigger) == motivation) {
				//pendingMotivatedGoals.remove(pendTrigger);
				//to maintain compatibility with subclasses
				this.removePendingMotivatedGoal(pendTrigger);
			}
		}
	}
	
	/**
	 * Returns whether or not the supplied trigger is a pending motivated goal.
	 * 
	 * @param trigger The trigger representing the pending motivated goal.
	 * @return		  Whether or not the supplied trigger is a motivaded goal pending accomplishment.
	 */
	public boolean isPendingMotivatedGoal(Trigger trigger) {
		return this.pendingMotivatedGoals.containsKey(trigger);
	}
	
	/**
	 * Removes the supplied goal/trigger from the list of pending ones, and
	 * returns the motivation associated with that goal.
	 * 
	 * @param trigger
	 * @return
	 */
	public Motivation removePendingMotivatedGoal(Trigger trigger) {
		return this.pendingMotivatedGoals.remove(trigger);
	}
	
	//AgentSpeak functions, and the corresponding calls to delegate classes

	@Override
	public void buf(List<Literal> percepts) {
		beliefUpdateFunction.updateBeliefs(this, percepts);
	}
	
	@Override
	public List<Literal>[] brf(Literal beliefToAdd, Literal beliefToDel, Intention i) {
		return this.beliefRevisionFunction.reviseBeliefs(this, beliefToAdd, beliefToDel, i);
	}
	
	@Override
	public Intention selectIntention(Queue<Intention> intentions) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.intentionSelectionFunction.selectIntention(this, intentions);
	}
	
	@Override
	public Option selectOption(List<Option> options) {
		return optionSelectionFunction.selectOption(this,options);
	}
	
	@Override
	public ActionExec selectAction(List<ActionExec> actList) {
		return this.actionSelectionFunction.selectAction(this, actList);
	}
	
	@Override
	public Event selectEvent(Queue<Event> events) {
		return this.eventSelectionFunction.selectEvent(this, events);
	}
	
	@Override
	public Message selectMessage(Queue<Message> msgList) {
		return this.messageSelectionFunction.selectMessage(this, msgList);
	}
}
