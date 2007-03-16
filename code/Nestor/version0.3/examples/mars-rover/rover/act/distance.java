package rover.act;

import java.util.logging.Logger;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Term;

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
