package org.kcl.nestor.mot.predictor;

import jason.architecture.AgArch;
import jason.asSemantics.ActionExec;
import jason.asSemantics.TransitionSystem;
import jason.asSyntax.Literal;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleAgArch extends AgArch {
	private static Logger logger = Logger.getLogger(SimpleAgArch.class.getName());

    /*public SimpleJasonAgent() {
        // set up the Jason agent
        try {
            Agent ag = new Agent();
            setTS(ag.initAg(this, new DefaultBeliefBase(), "demo.asl", new Settings()));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Init error", e);
        }
    }*/

    public void run() {
        try {
            while (isRunning()) {
                // calls the Jason engine to perform one reasoning cycle
                logger.fine("Reasoning....");
                getTS().reasoningCycle();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Run error", e);
        }
    }

    // this method just add some perception for the agent
    @Override
    public List<Literal> perceive() {
        List<Literal> l = new ArrayList<Literal>();
        //l.add(Literal.parseLiteral("x(10)"));
        return l;
    }

    // this method get the agent actions
    @Override
    public void act(ActionExec action, List<ActionExec> feedback) {
        //logger.info("Agent " + getAgName() + " is doing: " + action.getActionTerm());
        // set that the execution was ok
        action.setResult(true);
        feedback.add(action);
    }
    
    @Override
    public String getAgName() {
    	return "DummyAgent";
    }

    @Override
    public boolean canSleep() {
        return true;
    }

    @Override
    public boolean isRunning() {
        return true;
    }

    // Not used methods
    // This simple agent does not need messages/control/...
    @Override
    public void sendMsg(jason.asSemantics.Message m) throws Exception {
    }

    @Override
    public void broadcast(jason.asSemantics.Message m) throws Exception {
    }

    @Override
    public void checkMail() {
    }
}
