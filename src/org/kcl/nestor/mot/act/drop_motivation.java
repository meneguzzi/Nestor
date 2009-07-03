package org.kcl.nestor.mot.act;

import java.util.Iterator;

import org.kcl.nestor.mot.MotivatedAgent;
import org.kcl.nestor.mot.Motivation;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.StringTerm;
import jason.asSyntax.Term;

/**
 * 
  <p>Internal action: <b><code>.drop_motivation</code></b>.
  
  <p>Description: an internal action that drops the named motivation,
  removing all associated goals.
  
  <p>Parameters:<ul>
  
  <li>+ motivation (number): the motivation whose goals are to be dropped.<br/>
  
  </ul>
  
  <p>Examples:<ul> 

  <li> <code>.drop_motivation(navigate)</code>: drops the goals generated 
  by the motivation to navigate.</li>

  </ul>
 * @author Felipe Meneguzzi
 *
 */
public class drop_motivation extends DefaultInternalAction {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		if(args.length != 1) {
			ts.getAg().getLogger().warning("Wrong number of arguments for drop_motivation");
			return false;
		}
		
		if(!args[0].isString()) {
			ts.getAg().getLogger().warning("Wrong type of argument for drop_motivation");
		}
		
		if(ts.getAg() instanceof MotivatedAgent) {
			StringTerm term = (StringTerm) args[0];
			String motivationName = term.getString();
			MotivatedAgent agent = (MotivatedAgent) ts.getAg();
			Motivation motivationToDrop = null;
			
			//To find the motivation we want to drop, we iterate the pending motivations
			for (Iterator<Motivation> i = agent.getPendingMotivations().iterator(); i.hasNext();) {
				Motivation motivation = i.next();
				if(motivation.getMotivationName().equals(motivationName)) {
					//if we find it, we have to keep it in a temporary variable
					//to avoid a concurrent modification exception.
					motivationToDrop = motivation;
					break;
				}
			}
			
			//If we found the motivation we wanted to drop, do it using the
			//appropriate method
			if(motivationToDrop != null) {
				agent.getLogger().info("Dropping motivation "+motivationName);
				agent.removePendingMotivation(motivationToDrop);
			}
			
			return true;
		} else {
			ts.getAg().getLogger().warning("Only MotivatedAgent(S) can drop_motivation");
		}
		
		return false;
	}	
}
