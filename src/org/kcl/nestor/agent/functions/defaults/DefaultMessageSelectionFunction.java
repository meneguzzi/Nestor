package org.kcl.nestor.agent.functions.defaults;

import jason.asSemantics.Agent;
import jason.asSemantics.Message;

import java.util.Queue;

import org.kcl.nestor.agent.functions.MessageSelectionFunction;

public class DefaultMessageSelectionFunction implements
		MessageSelectionFunction {

	public Message selectMessage(Agent agent, Queue<Message> msgList) {
		//make sure the selected Message is removed from msgList
		return msgList.poll();
	}

}
