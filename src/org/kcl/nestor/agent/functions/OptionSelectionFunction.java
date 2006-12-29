package org.kcl.nestor.agent.functions;

import jason.asSemantics.Agent;
import jason.asSemantics.Option;

import java.util.List;

public interface OptionSelectionFunction<A extends Agent> {
	public Option selectOption(A agent, List<Option> options);
}
