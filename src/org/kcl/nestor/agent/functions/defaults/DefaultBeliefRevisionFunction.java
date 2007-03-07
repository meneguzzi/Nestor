package org.kcl.nestor.agent.functions.defaults;

import jason.asSemantics.Agent;
import jason.asSemantics.Intention;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kcl.nestor.agent.functions.BeliefRevisionFunction;

public class DefaultBeliefRevisionFunction implements BeliefRevisionFunction {
	private static final Logger logger = Logger.getLogger(Agent.class.getName());

	@SuppressWarnings("unchecked")
	public List<Literal>[] reviseBeliefs(Agent agent, Literal beliefToAdd,
			Literal beliefToDel, Intention i) {
//		 This class does not implement belief revision! It
        // is supposed that a subclass will do it.
        // It simply add/del the belief.

        List<Literal>[] result = new List[2];
        result[0] = Collections.emptyList();
        result[1] = Collections.emptyList();

        boolean changed = false; // if the BB is changed

        if (beliefToAdd != null) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("adding " + beliefToAdd);
            }
            if (agent.getBB().add(beliefToAdd)) {
                result[0] = Collections.singletonList(beliefToAdd);
                changed = true;
            }
        }

        if (beliefToDel != null) {
            Unifier u = null;
            try {
                u = i.peek().getUnif(); // get from current intention
            } catch (Exception e) {
                u = new Unifier();
            }

            if (logger.isLoggable(Level.FINE)) {
                logger.fine("doing brf for " + beliefToDel + " in BB="
                        + agent.believes(beliefToDel, u));
            }
            
            if (agent.believes(beliefToDel, u)) {
            	Literal inBB = agent.findBel(beliefToDel, u);
                // lInBB is l unified in BB
                // we can not use l for delBel in case l is g(_,_)
                if (beliefToDel.hasAnnot()) {
                    // if belief has annots, use them
                    inBB = (Literal) inBB.clone();
                    inBB.clearAnnots();
                    inBB.addAnnots(beliefToDel.getAnnots());
                }

                if (agent.getBB().remove(inBB)) {
                    if (logger.isLoggable(Level.FINE))
                        logger.fine("Removed:" + inBB);
                    result[1] = Collections.singletonList(inBB);
                    changed = true;
                }
            }
        }
        if (changed) {
            return result;
        } else {
            return null;
        }
	}

}
