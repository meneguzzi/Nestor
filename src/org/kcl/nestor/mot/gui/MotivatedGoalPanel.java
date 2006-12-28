package org.kcl.nestor.mot.gui;

import jason.asSyntax.Trigger;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.kcl.nestor.mot.Motivation;

public class MotivatedGoalPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel jGoalLabel = null;
	private JLabel jGoalLabel2 = null;
	private JLabel jMotivationLabel = null;
	private JLabel jMotivationLabel2 = null;
	
	private Trigger trigger = null;
	private Motivation motivation = null;

	/**
	 * This is the default constructor
	 */
	public MotivatedGoalPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		jMotivationLabel2 = new JLabel();
		jMotivationLabel2.setText("Motivation Value");
		jMotivationLabel = new JLabel();
		jMotivationLabel.setText("Motivation:");
		jGoalLabel2 = new JLabel();
		jGoalLabel2.setText("Goal Value");
		GridLayout gridLayout = new GridLayout();
		gridLayout.setRows(2);
		gridLayout.setColumns(2);
		jGoalLabel = new JLabel();
		jGoalLabel.setText("Goal:");
		this.setLayout(gridLayout);
		this.setSize(300, 113);
		this.add(jGoalLabel, null);
		this.add(jGoalLabel2, null);
		this.add(jMotivationLabel, null);
		this.add(jMotivationLabel2, null);
	}

	public void setTriggerMotivationPair(Trigger trigger, Motivation motivation) {
		this.trigger = trigger;
		this.motivation = motivation;
		this.jGoalLabel2.setText(trigger.getLiteral().toString());
		this.jMotivationLabel2.setText(motivation.getMotivationName());
	}

	/**
	 * @return the motivation
	 */
	public Motivation getMotivation() {
		return motivation;
	}

	/**
	 * @return the trigger
	 */
	public Trigger getTrigger() {
		return trigger;
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
