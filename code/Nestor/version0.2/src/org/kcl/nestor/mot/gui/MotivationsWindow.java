package org.kcl.nestor.mot.gui;

import jason.asSyntax.Trigger;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.kcl.nestor.mot.Motivation;

public class MotivationsWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;
	
	private List<MotivationPanel> motivationPanels;  //  @jve:decl-index=0:
	
	private List<MotivatedGoalPanel> motivatedGoalPanels;  //  @jve:decl-index=0:

	private JScrollPane jScrollPane = null;

	private JPanel jMotivationPanels = null;

	private JPanel jMotivationIntensitiesPanel = null;

	private JPanel jMotivatedGoalsPanel = null;

	private JScrollPane jScrollPane1 = null;

	private JPanel jGoalPanels = null;

	/**
	 * This is the default constructor
	 */
	public MotivationsWindow() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(494, 200);
		this.setContentPane(getJContentPane());
		this.setTitle("Motivations");
		
		motivationPanels = new Vector<MotivationPanel>();
		motivatedGoalPanels = new Vector<MotivatedGoalPanel>();
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridLayout gridLayout2 = new GridLayout();
			gridLayout2.setRows(1);
			gridLayout2.setColumns(2);
			jContentPane = new JPanel();
			jContentPane.setLayout(gridLayout2);
			jContentPane.add(getJMotivationIntensitiesPanel(), null);
			jContentPane.add(getJMotivatedGoalsPanel(), null);
		}
		return jContentPane;
	}

	public void updateData() {
		for (Iterator iter = motivationPanels.iterator(); iter.hasNext();) {
			MotivationPanel panel = (MotivationPanel) iter.next();
			panel.updateData();
		}
		this.repaint();
	}
	
	public void addMotivation(Motivation motivation) {
		MotivationPanel panel = new MotivationPanel();
		panel.setMotivation(motivation);
		this.motivationPanels.add(panel);
		this.getJMotivationPanels().add(panel);
	}
	
	public void removeMotivation(Motivation motivation) {
		for (Iterator iter = motivationPanels.iterator(); iter.hasNext();) {
			MotivationPanel panel = (MotivationPanel) iter.next();
			if(panel.getMotivation() == motivation) {
				this.jContentPane.remove(panel);
			}
		}
	}
	
	public void addMotivatedGoal(Trigger trigger, Motivation motivation) {
		MotivatedGoalPanel motivatedGoalPanel = new MotivatedGoalPanel();
		motivatedGoalPanel.setTriggerMotivationPair(trigger, motivation);
		this.getJGoalPanels().add(motivatedGoalPanel);
		this.motivatedGoalPanels.add(motivatedGoalPanel);
		this.repaint();
	}
	
	public void removeMotivatedGoal(Trigger trigger) {
		for (Iterator<MotivatedGoalPanel> i = motivatedGoalPanels.iterator(); i.hasNext();) {
			MotivatedGoalPanel panel = i.next();
			if(panel.getTrigger() == trigger) {
				i.remove();
				this.getJGoalPanels().remove(panel);
			}
		}
		this.repaint();
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJMotivationPanels());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jMotivationPanels	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJMotivationPanels() {
		if (jMotivationPanels == null) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(10);
			gridLayout.setColumns(1);
			jMotivationPanels = new JPanel();
			jMotivationPanels.setLayout(gridLayout);
		}
		return jMotivationPanels;
	}

	/**
	 * This method initializes jMotivationIntensitiesPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJMotivationIntensitiesPanel() {
		if (jMotivationIntensitiesPanel == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.weighty = 1.0;
			gridBagConstraints.weightx = 1.0;
			jMotivationIntensitiesPanel = new JPanel();
			jMotivationIntensitiesPanel.setLayout(new GridBagLayout());
			jMotivationIntensitiesPanel.add(getJScrollPane(), gridBagConstraints);
		}
		return jMotivationIntensitiesPanel;
	}

	/**
	 * This method initializes jMotivatedGoalsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJMotivatedGoalsPanel() {
		if (jMotivatedGoalsPanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.weighty = 1.0;
			gridBagConstraints1.weightx = 1.0;
			jMotivatedGoalsPanel = new JPanel();
			jMotivatedGoalsPanel.setLayout(new GridBagLayout());
			jMotivatedGoalsPanel.add(getJScrollPane1(), gridBagConstraints1);
		}
		return jMotivatedGoalsPanel;
	}

	/**
	 * This method initializes jScrollPane1	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setViewportView(getJGoalPanels());
		}
		return jScrollPane1;
	}

	/**
	 * This method initializes jGoalPanels	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJGoalPanels() {
		if (jGoalPanels == null) {
			GridLayout gridLayout1 = new GridLayout();
			gridLayout1.setRows(10);
			gridLayout1.setColumns(1);
			jGoalPanels = new JPanel();
			jGoalPanels.setLayout(gridLayout1);
		}
		return jGoalPanels;
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
