package org.kcl.nestor.agent;

import jason.asSemantics.ActionExec;
import jason.asSemantics.Agent;
import jason.asSemantics.Event;
import jason.asSemantics.Intention;
import jason.asSemantics.Message;
import jason.asSemantics.Option;
import jason.asSyntax.Literal;

import java.util.List;
import java.util.Queue;
import java.util.logging.Logger;

import org.kcl.nestor.agent.functions.ActionSelectionFunction;
import org.kcl.nestor.agent.functions.BeliefRevisionFunction;
import org.kcl.nestor.agent.functions.BeliefUpdateFunction;
import org.kcl.nestor.agent.functions.EventSelectionFunction;
import org.kcl.nestor.agent.functions.IntentionSelectionFunction;
import org.kcl.nestor.agent.functions.MessageSelectionFunction;
import org.kcl.nestor.agent.functions.OptionSelectionFunction;
import org.kcl.nestor.agent.functions.defaults.DefaultActionSelectionFunction;
import org.kcl.nestor.agent.functions.defaults.DefaultBeliefRevisionFunction;
import org.kcl.nestor.agent.functions.defaults.DefaultBeliefUpdateFunction;
import org.kcl.nestor.agent.functions.defaults.DefaultEventSelectionFunction;
import org.kcl.nestor.agent.functions.defaults.DefaultIntentionSelectionFunction;
import org.kcl.nestor.agent.functions.defaults.DefaultMessageSelectionFunction;
import org.kcl.nestor.agent.functions.defaults.DefaultOptionSelectionFunction;

/**
 * A Modular Agent class extending Jason's basic agent class.
 * @author Felipe Meneguzzi
 *
 */
public class ModularAgent extends Agent {
	
	protected static final Logger logger = Logger.getLogger(Agent.class.getName());
	
	protected ActionSelectionFunction actionSelectionFunction = null;
	protected BeliefRevisionFunction beliefRevisionFunction = null;
	protected BeliefUpdateFunction beliefUpdateFunction = null;
	protected EventSelectionFunction eventSelectionFunction = null;
	protected IntentionSelectionFunction intentionSelectionFunction = null;
	protected MessageSelectionFunction messageSelectionFunction = null;
	protected OptionSelectionFunction optionSelectionFunction = null;
	
	public ModularAgent() {
		//Default function implementations
		this.actionSelectionFunction = new DefaultActionSelectionFunction();
		this.beliefRevisionFunction = new DefaultBeliefRevisionFunction();
		this.beliefUpdateFunction = new DefaultBeliefUpdateFunction();
		this.eventSelectionFunction = new DefaultEventSelectionFunction();
		this.intentionSelectionFunction = new DefaultIntentionSelectionFunction();
		this.messageSelectionFunction = new DefaultMessageSelectionFunction();
		this.optionSelectionFunction = new DefaultOptionSelectionFunction();
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
	
	@SuppressWarnings("unchecked")
	@Override
	public Option selectOption(List<Option> options) {
		return optionSelectionFunction.selectOption(this,options);
	}
	
	@Override
	public ActionExec selectAction(List<ActionExec> actList) {
		return this.actionSelectionFunction.selectAction(this, actList);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Event selectEvent(Queue<Event> events) {
		return this.eventSelectionFunction.selectEvent(this, events);
	}
	
	@Override
	public Message selectMessage(Queue<Message> msgList) {
		return this.messageSelectionFunction.selectMessage(this, msgList);
	}
}
