package rover.act;

import java.util.logging.Logger;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Term;

/**
 * 
  <p>Internal action: <b><code>.distance</code></b>.
  
  <p>Description: an internal action that calculates the distance between two coordinates in a
  grid considering only vertical and horizontal movement.
  
  <p>Parameters:<ul>
  
  <li>+ x1 (number): the beginning x coordinate.<br/>
  
  <li>+ y1 (number): the beginning y coordinate.<br/>
  
  <li>+ x2 (number): the final x coordinate.<br/>
  
  <li>+ y2 (number): the final y coordinate.<br/>

  <li>- distance (number): the distance between the two coordinates.<br/>

  </ul>
  
  <p>Examples:<ul> 

  <li> <code>.distance(1,1,2,2,D)</code>: unifies D with the number 2, which is the distance (1,1) and (2,2).</li>

  </ul>
 * @author meneguzz
 *
 */
public class distance extends DefaultInternalAction {
	private static final Logger logger = Logger.getLogger(DefaultInternalAction.class.getName());
	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args)
			throws Exception {
		if(args.length < 5 ) {
			logger.info("Wrong number of arguments for distance.");
			return false;
		}
		
		NumberTerm x1t = (NumberTerm) args[0];
		NumberTerm y1t = (NumberTerm) args[1];
		NumberTerm x2t = (NumberTerm) args[2];
		NumberTerm y2t = (NumberTerm) args[3];
		x1t.apply(un);
		y1t.apply(un);
		x2t.apply(un);
		y2t.apply(un);
		
		int x1 = (int) x1t.solve();
		int y1 = (int) y1t.solve();
		int x2 = (int) x2t.solve();
		int y2 = (int) y2t.solve();
		
		int d = Math.abs(x2-x1) + Math.abs(y2-y1);
		
		NumberTermImpl distance = new NumberTermImpl(d);
		
		return un.unifies(distance, args[4]);
	}
}
