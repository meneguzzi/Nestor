package org.kcl.nestor.mot.functions.defaults;

import jason.asSemantics.Agent;
import jason.asSemantics.Event;
import jason.asSemantics.Intention;
import jason.asSyntax.Literal;
import jason.asSyntax.Trigger;
import jason.bb.BeliefBase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kcl.nestor.mot.functions.BeliefUpdateFunction;

public class DefaultBeliefUpdateFunction implements BeliefUpdateFunction {
	private static final Logger logger = Logger.getLogger(Agent.class.getName());

	public void updateBeliefs(Agent agent, List<Literal> percepts) {
		if (percepts == null) {
            return;
        }

        // List<Literal> added = new ArrayList<Literal>();
        List<Literal> removed = new ArrayList<Literal>();

        // deleting percepts in the BB that is not perceived anymore
        Iterator<Literal> perceptsInBB = agent.getBB().getPercepts();
        while (perceptsInBB.hasNext()) { // for (int i = 0; i <
            // perceptsInBB.size(); i++) {
            Literal l = perceptsInBB.next(); // get(i);

            // could not use percepts.contains(l), since equalsAsTerm must be
            // used (to ignore annotations)
            boolean wasPerceived = false;
            for (Literal t : percepts) {
                // if percept t is already in BB
                if (l.equalsAsTerm(t) && l.negated() == t.negated()) {
                    wasPerceived = true;
                    break;
                }
            }
            if (!wasPerceived) {
                removed.add(l); // do not remove using the iterator here,
                                // concurrent modification!
            }
        }

        for (Literal lr : removed) {
            if (agent.getBB().remove(lr)) {
                agent.getTS().updateEvents(new Event(new Trigger(Trigger.TEDel,Trigger.TEBel, lr), Intention.EmptyInt));
            }
        }

        // addBel only adds a belief when appropriate
        // checking all percepts for new beliefs
        for (Literal lp : percepts) {
            try {
                lp = (Literal) lp.clone();
                lp.addAnnot(BeliefBase.TPercept);
                if (agent.getBB().add(lp)) {
                    Trigger te = new Trigger(Trigger.TEAdd, Trigger.TEBel, lp);
                    agent.getTS().updateEvents(new Event(te, Intention.EmptyInt));
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error adding percetion " + lp, e);
            }
        }
	}

}
