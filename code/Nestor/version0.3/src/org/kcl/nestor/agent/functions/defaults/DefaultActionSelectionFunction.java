package org.kcl.nestor.agent.functions.defaults;

import jason.asSemantics.ActionExec;
import jason.asSemantics.Agent;

import java.util.List;

import org.kcl.nestor.agent.functions.ActionSelectionFunction;

public class DefaultActionSelectionFunction implements ActionSelectionFunction {

	public ActionExec selectAction(Agent agent, List<ActionExec> actList) {
		//make sure the selected Action is removed from actList
        return actList.remove(0);
	}

}
