package org.kcl.nestor.mot.functions;

import jason.asSemantics.ActionExec;
import jason.asSemantics.Agent;

import java.util.List;

public interface ActionSelectionFunction {
	public ActionExec selectAction(Agent agent, List<ActionExec> actList);
}
