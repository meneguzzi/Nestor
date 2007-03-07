package org.kcl.nestor.mot.log;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.kcl.nestor.mot.MotivatedAgent;
import org.kcl.nestor.mot.Motivation;

import jason.architecture.AgArch;
import jason.asSemantics.Agent;

/**
 * An agent architecture specialized for logging an agent's motivational dynamics. 
 * The static block in this class ensures that logging will be enabled by default if
 * this agent architecture is used.
 * 
 * @author Felipe Meneguzzi
 *
 */
public class LoggingAgArch extends AgArch {
	/**
	 * This static block changes the automatic behaviour of the MotivationLogFactory to
	 * allow the creation of logging for all agents.
	 * TODO Again this is probably not the best method to do this, and I shall change it.
	 */
	static {
		MotivationLogFactory.setLogging(true);
	}
	
	@Override
	public void stopAg() {
		super.stopAg();
		Agent agent = this.getTS().getAg();
		if(agent instanceof MotivatedAgent) {
			MotivatedAgent motivatedAgent = (MotivatedAgent) agent;
			List<Motivation> motivations = motivatedAgent.getMotivations();
			for (Iterator i = motivations.iterator(); i.hasNext();) {
				Motivation motivation = (Motivation) i.next();
				try {
					FileWriter writer = new FileWriter(motivation.getMotivationName()+".log");
					writer.write(motivation.getMotivationLog().toString());
					writer.flush();
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
