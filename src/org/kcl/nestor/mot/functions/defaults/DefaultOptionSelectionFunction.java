package org.kcl.nestor.mot.functions.defaults;

import jason.asSemantics.Agent;
import jason.asSemantics.Option;

import java.util.List;

import org.kcl.nestor.mot.functions.OptionSelectionFunction;

public class DefaultOptionSelectionFunction implements OptionSelectionFunction<Agent> {
	
	public Option selectOption(Agent agent, List<Option> options) {
		if (options.size() > 0) {
            return options.remove(0);
        } else {
            return null;
        }
	}

}
