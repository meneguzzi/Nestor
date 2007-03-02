package org.kcl.nestor.mot.functions;

import jason.asSemantics.Agent;
import jason.asSemantics.Event;
import jason.asSyntax.Trigger;

import java.util.Queue;
import java.util.logging.Logger;

import org.kcl.nestor.agent.functions.EventSelectionFunction;
import org.kcl.nestor.mot.MotivatedAgent;
import org.kcl.nestor.mot.Motivation;

public class MotivatedEventSelection implements EventSelectionFunction<MotivatedAgent> {
	private Logger logger = Logger.getLogger(Agent.class.getName());

	public Event selectEvent(MotivatedAgent agent, Queue<Event> events) {
		Event event = events.poll();
		//If the event is a deletion of an achievement goal, we have plan failure
		//And we should remove the pending motivated goals, if any existed
		if(!event.getTrigger().isAddition() && event.getTrigger().isAchvGoal()) {
			Trigger trigger = event.getTrigger();
			//If the inverse of this deletion is in a pending motivated goal,
			Trigger oppositeTrigger = new Trigger(!trigger.isAddition(), trigger.getGoal(), trigger.getLiteral());
			logger.info("Event "+event+" generated. Plan to achieve "+oppositeTrigger+" failed.");
			//Then we must remove it from the list of pending motivated goals
			if(agent.isPendingMotivatedGoal(oppositeTrigger)) {
				Motivation motivation = agent.removePendingMotivatedGoal(trigger);
				logger.info("Removing motivation "+motivation+" from pending motivations");
				//and remove its associated motivation from the pending ones,
				//so that the agent can be aware that this motivation can no longer me mitigated
				agent.removePendingMotivation(motivation);
			}
		}
		return event;
	}

}
