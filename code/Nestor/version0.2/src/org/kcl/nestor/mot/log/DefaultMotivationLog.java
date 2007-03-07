package org.kcl.nestor.mot.log;

import jason.asSyntax.Trigger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.kcl.nestor.mot.Motivation;

/**
 * The default implementation of the motivation log
 * @author Felipe Meneguzzi
 * TODO Add a graph writing module
 */
public class DefaultMotivationLog implements MotivationLog {
	
	protected List<Integer> motivationLevels;
	
	protected List<Integer> thresholdExceededEvents;
	
	protected HashMap<Integer, List<Trigger>> generatedGoals;
	
	protected HashMap<Integer, Integer> mitigationPoints;
	
	protected Motivation motivation; 
	
	public DefaultMotivationLog(Motivation motivation) {
		this.motivationLevels = new ArrayList<Integer>();
		this.generatedGoals = new HashMap<Integer, List<Trigger>>();
		this.mitigationPoints = new HashMap<Integer, Integer>();
		this.thresholdExceededEvents = new ArrayList<Integer>();
		this.motivation = motivation;
	}

	/* (non-Javadoc)
	 * @see org.kcl.nestor.mot.log.MotivationLog#addMotivationLevel(int)
	 */
	public void addMotivationLevel(int motivationLevel) {
		this.motivationLevels.add(motivationLevel);
	}
	
	/* (non-Javadoc)
	 * @see org.kcl.nestor.mot.log.MotivationLog#addTriggeredGoals(java.util.List)
	 */
	public void addTriggeredGoals(List<Trigger> goals) {
		this.generatedGoals.put(motivationLevels.size()-1, goals);
	}
	
	/* (non-Javadoc)
	 * @see org.kcl.nestor.mot.log.MotivationLog#addMitigationPoint(int)
	 */
	public void addMitigationPoint(int mitigationLevel) {
		this.mitigationPoints.put(motivationLevels.size()-1, mitigationLevel);
	}
	
	public void addThresholdExceededEvent() {
		this.thresholdExceededEvents.add(motivationLevels.size()-1);
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("Motivation: "+motivation.getMotivationName()+" - Threshold: "+motivation.getMotivationThreshold()+System.getProperty("line.separator"));
		for (int i = 0; i < motivationLevels.size(); i++) {
			buffer.append(i + " " + motivationLevels.get(i));
			if(thresholdExceededEvents.contains(i)){
				buffer.append(" * ");
			}
			if(mitigationPoints.containsKey(i)) {
				buffer.append(" -> "+mitigationPoints.get(i));
			}
			if(generatedGoals.containsKey(i)) {
				buffer.append(" ("+generatedGoals.get(i).toString()+")");
			}
			buffer.append(System.getProperty("line.separator"));
		}
		
		return buffer.toString();
	}
	
	public static class DefaultMotivationLogFactory extends MotivationLogFactory {

		@Override
		public MotivationLog newMotivationLog(Motivation motivation) {
			return new DefaultMotivationLog(motivation);
		}
		
	}
}
