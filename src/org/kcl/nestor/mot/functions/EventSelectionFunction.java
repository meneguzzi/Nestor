package org.kcl.nestor.mot.functions;

import jason.asSemantics.Agent;
import jason.asSemantics.Event;

import java.util.Queue;

public interface EventSelectionFunction<A extends Agent> {
	public Event selectEvent(A agent, Queue<Event> events);
}
