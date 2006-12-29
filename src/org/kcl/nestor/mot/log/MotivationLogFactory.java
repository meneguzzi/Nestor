package org.kcl.nestor.mot.log;

import org.kcl.nestor.mot.Motivation;

/**
 * A centralized factory class for motivation log instances, to allow easy modification
 * of the behaviour of logging.
 * 
 * @author Felipe Meneguzzi
 *
 */
public abstract class MotivationLogFactory {
	private static MotivationLogFactory factorySingleton = null;
	
	private static boolean loggingEnabled = false;
	
	/**
	 * Returns the singleton instance of the <code>MotivationLogFactory</code> class.
	 * @return
	 */
	public static MotivationLogFactory getInstance() {
		if(factorySingleton == null) {
			if(loggingEnabled) {
				factorySingleton = new DefaultMotivationLog.DefaultMotivationLogFactory();
			} else {
				factorySingleton = new EmptyMotivationLog.EmptyMotivationLogFactory();
			}
		}
		
		return factorySingleton;
	}
	
	/**
	 * Sets whether or not future instances of <code>MotivationLog</code> will enable logging.
	 * Invoking this method after <code>MotivationLog</code> instances have been created will
	 * not change their behaviour.
	 * 
	 * @param enabled
	 */
	public static void setLogging(boolean enabled) {
		if(enabled != loggingEnabled) {
			loggingEnabled = enabled;
			factorySingleton = null;
		}
	}
	
	/**
	 * Creates an instance of <code>MotivationLog</code> to keep track of motivation
	 * events for the supplied <code>Motivation</code>.
	 * 
	 * @param motivation The motivation for which events will be logged.
	 * @return
	 */
	public abstract MotivationLog newMotivationLog(Motivation motivation);
}
