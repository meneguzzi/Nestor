package org.kcl.nestor.agent.functions;

import jason.asSemantics.Agent;
import jason.asSemantics.Message;

import java.util.Queue;

public interface MessageSelectionFunction {
	public Message selectMessage(Agent agent, Queue<Message> msgList);
}
