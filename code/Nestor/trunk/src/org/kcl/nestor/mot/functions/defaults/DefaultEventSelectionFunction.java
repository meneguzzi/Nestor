package org.kcl.nestor.mot.functions.defaults;

import jason.asSemantics.Agent;
import jason.asSemantics.Event;

import java.util.Queue;

import org.kcl.nestor.mot.functions.EventSelectionFunction;

public class DefaultEventSelectionFunction implements EventSelectionFunction<Agent> {

	public Event selectEvent(Agent agent, Queue<Event> events) {
		//make sure the selected Event is removed from evList
        return events.poll();
	}

}
