/**
 * 
 */
package org.kcl.nestor.mot;

import jason.JasonException;
import jason.asSemantics.Event;
import jason.asSemantics.Intention;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Trigger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.kcl.nestor.agent.ModularAgent;
import org.kcl.nestor.agent.functions.defaults.DefaultActionSelectionFunction;
import org.kcl.nestor.agent.functions.defaults.DefaultBeliefRevisionFunction;
import org.kcl.nestor.agent.functions.defaults.DefaultIntentionSelectionFunction;
import org.kcl.nestor.agent.functions.defaults.DefaultMessageSelectionFunction;
import org.kcl.nestor.agent.functions.defaults.DefaultOptionSelectionFunction;
import org.kcl.nestor.mot.functions.MotivatedBeliefUpdate;
import org.kcl.nestor.mot.functions.MotivatedEventSelection;
import org.kcl.nestor.mot.parser.MotivationParser;

/** 
 * This class represents a motivated agent, capable of generating goals autonomously
 * while responding to a model of motivational intensity.
 * 
 * @author Felipe Rech Meneguzzi
 */
public class MotivatedAgent extends ModularAgent {
	
	/**
	 * 
	 */
	protected List<Motivation> motivations;
	
	/**
	 * The goals associated with pending motivations
	 */
	protected Hashtable<Trigger, Motivation> pendingMotivatedGoals;

	/**
	 * The list of motivations that have been triggered
	 */
	protected List<Motivation> pendingMotivations;
	
	/**
	 * The unifiers created when a motivation is triggered
	 */
	protected HashMap<Motivation, Unifier> triggeredMotivationUnifiers;
	
	/**
	 * An intention used solely for the addition of motivated Goals
	 */
	protected Intention motivatedIntention;
	
	public MotivatedAgent() {
		this.motivations = new ArrayList<Motivation>();
		this.motivatedIntention = new Intention();
		
		this.pendingMotivations = new ArrayList<Motivation>();
		this.pendingMotivatedGoals = new Hashtable<Trigger, Motivation>();
		this.triggeredMotivationUnifiers = new HashMap<Motivation, Unifier>();
		
		//TODO Review the configuration of the functions 
		//TODO and generalize its instantiation process
		
		this.beliefUpdateFunction = new MotivatedBeliefUpdate();
		//this.optionSelectionFunction = new MotivatedOptionSelectionV2();
		//this.intentionSelectionFunction = new MotivatedIntentionSelection();
		this.eventSelectionFunction = new MotivatedEventSelection();
		
		//Default function implementations
		this.actionSelectionFunction = new DefaultActionSelectionFunction();
		this.beliefRevisionFunction = new DefaultBeliefRevisionFunction();
		this.messageSelectionFunction = new DefaultMessageSelectionFunction();
		
		this.optionSelectionFunction = new DefaultOptionSelectionFunction();
		this.intentionSelectionFunction = new DefaultIntentionSelectionFunction();
	}
	
	@Override
	public void initAg(String asSrc) throws JasonException {
		super.initAg(asSrc);
		String motivationsFile = ts.getSettings().getUserParameter("motivations");
		motivationsFile = (motivationsFile != null ? motivationsFile : "motivations.mot");
		
//		XXX Review this
		try {
			motivationsFile = motivationsFile.replaceAll("\"", " ").trim();
			logger.info("Reading "+motivationsFile);
			readMotivations(motivationsFile);
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
	 * Returns a reference to the named motivation from this agent if this agent,
	 * has one such motivation, otherwise returns null.
	 * @param motivationName The name of a motivation.
	 * @return The named motivation, if the agent has one such motivation, or null otherwise.
	 */
	public Motivation getMotivation(String motivationName) {
		for (Iterator<Motivation> iter = motivations.iterator(); iter.hasNext();) {
			Motivation motivation = iter.next();
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
	 * Returns the motivations which have been triggered but have not yet been satisfied.
	 *  
	 * @return the triggeredMotivations
	 */
	public List<Motivation> getPendingMotivations() {
		return pendingMotivations;
	}
	
	/**
	 * Removes all pending motivations from the agent
	 * @param motivation
	 */
	public void removePendingMotivation(Motivation motivation) {
		//removes a pending motivation, as well as the list of goals associated
		//to this motivation
		this.pendingMotivations.remove(motivation);
		this.triggeredMotivationUnifiers.remove(motivation);
		for(Iterator<Trigger> i = pendingMotivatedGoals.keySet().iterator() ; i.hasNext(); ){
			Trigger pendTrigger = i.next();
			if(pendingMotivatedGoals.get(pendTrigger) == motivation) {
				i.remove();
			}
		}
	}

	/**
	 * Posts a motivation-generated goal to the agent's events, while keeping
	 * a reference to the posted trigger for future removal.
	 * @param trigger    The trigger/goal to be added
	 * @param motivation The motivation associated with the added goal
	 * @param unifier 	 The unifier used in the generation of the added goal
	 */
	public void addMotivatedGoal(Trigger trigger, Motivation motivation, Unifier unifier) {
		this.pendingMotivatedGoals.put(trigger, motivation);
		this.triggeredMotivationUnifiers.put(motivation, unifier);
		//this.fTS.updateEvents(new Event(trigger, motivatedIntention));
		this.ts.updateEvents(new Event(trigger, Intention.EmptyInt));
	}
	
	/**
	 * Returns the unifier associated with a pending motivation.
	 * @param motivation The motivation whose triggering unifier is desired.
	 * @return The unifier associated with the specified motivation.
	 */
	public Unifier getMotivationUnifier(Motivation motivation) {
		return this.triggeredMotivationUnifiers.get(motivation);
	}
	
	/**
	 * Checks whether the pending goals are still being pursued
	 *
	 */
	public void checkPendingGoals() {
		for(Iterator<Trigger> i = pendingMotivatedGoals.keySet().iterator() ; i.hasNext(); ){
			Trigger pendTrigger = i.next();
			
			for(Iterator<Intention> j = getTS().getC().getIntentions().iterator(); j.hasNext();) {
				Intention intention = j.next();
				if(intention.getIMs().size() > 0) {
					Trigger trigger = (Trigger) intention.getIMs().get(0).getTrigger().clone();
					Unifier unif = intention.getIMs().get(0).getUnif();
					trigger.getLiteral().apply(unif);
					if(pendTrigger.equals(trigger))
						break; // If the trigger is still part of an intention, it is still pending
				}
			}
			logger.info("Intention to "+pendTrigger+" is no longer being pursued");
			Motivation motivation = this.pendingMotivatedGoals.get(pendTrigger);
			i.remove();
			if(!pendingMotivatedGoals.containsValue(motivation)) {
				this.pendingMotivations.remove(motivation);
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
}
