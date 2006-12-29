package org.kcl.nestor.mot.log;

import jason.asSyntax.Trigger;

import java.util.List;

/**
 * A class for logging motivation dynamics and results.
 * @author Felipe Meneguzzi
 *
 */
public interface MotivationLog {

	public void addMotivationLevel(int motivationLevel);

	public void addTriggeredGoals(List<Trigger> goals);

	public void addMitigationPoint(int mitigationLevel);

	public void addThresholdExceededEvent();
}