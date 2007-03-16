package org.kcl.nestor.mot.gui;

import jason.asSyntax.Literal;
import jason.asSyntax.Trigger;

import java.util.Iterator;
import java.util.List;

import org.kcl.nestor.mot.MotivatedAgent;
import org.kcl.nestor.mot.Motivation;

public class MotivatedAgentWrapper extends MotivatedAgent {
	
	protected MotivationsWindow motivationsWindow;
	
	public MotivatedAgentWrapper() {
		super();
	}
	
	@Override
	public void readMotivations(String file) throws Exception {
		this.motivationsWindow = new MotivationsWindow();
		super.readMotivations(file);
		for (Iterator iter = this.motivations.iterator(); iter.hasNext();) {
			Motivation motivation = (Motivation) iter.next();
			motivationsWindow.addMotivation(motivation);
		}
		motivationsWindow.updateData();
		motivationsWindow.setVisible(true);
	}
	
	@Override
	public void buf(List<Literal> percepts) {
		super.buf(percepts);
		motivationsWindow.updateData();
	}
	
	@Override
	public void addMotivatedGoal(Trigger trigger, Motivation motivation) {
		super.addMotivatedGoal(trigger, motivation);
		motivationsWindow.addMotivatedGoal(trigger, motivation);
	}
	
	@Override
	public Motivation removePendingMotivatedGoal(Trigger trigger) {
		motivationsWindow.removeMotivatedGoal(trigger);
		return super.removePendingMotivatedGoal(trigger);
	}
}
