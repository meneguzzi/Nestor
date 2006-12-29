package org.kcl.nestor.mot.log;

import jason.asSyntax.Trigger;

import java.util.List;

import org.kcl.nestor.mot.Motivation;
/**
 * An empty implementation of the motivation log interface to be used
 * when no logging is required.
 * @author Felipe Meneguzzi
 *
 */
public class EmptyMotivationLog implements MotivationLog {
	
	public EmptyMotivationLog() {
		// TODO Auto-generated constructor stub
	}

	public void addMitigationPoint(int mitigationLevel) {
		// TODO Auto-generated method stub

	}

	public void addMotivationLevel(int motivationLevel) {
		// TODO Auto-generated method stub

	}

	public void addTriggeredGoals(List<Trigger> goals) {
		// TODO Auto-generated method stub

	}
	
	public void addThresholdExceededEvent() {
		// TODO Auto-generated method stub
	}
	
	public String toString() {
		return "";
	}

	public static class EmptyMotivationLogFactory extends MotivationLogFactory {

		@Override
		public MotivationLog newMotivationLog(Motivation motivation) {
			return new EmptyMotivationLog();
		}
		
	}
}
