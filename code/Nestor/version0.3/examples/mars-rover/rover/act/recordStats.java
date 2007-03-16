package rover.act;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Term;

import java.io.File;
import java.io.FileWriter;
import java.util.logging.Logger;

public class recordStats extends DefaultInternalAction {
	private static final Logger logger = Logger.getLogger(DefaultInternalAction.class.getName());
	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args)
			throws Exception {
		if(args.length < 5 ) {
			logger.info("Wrong number of arguments for stats.");
			return false;
		}
		
		NumberTerm arg0 = (NumberTerm) args[0];
		NumberTerm arg1 = (NumberTerm) args[1];
		NumberTerm arg2 = (NumberTerm) args[2];
		NumberTerm arg3 = (NumberTerm) args[3];
		
		int waypoints = (int) arg0.solve();
		int distance = (int) arg1.solve();
		int wastedDistance = (int) arg2.solve();
		int chargeDistance = (int) arg3.solve();
		
		String filename = args[4].toString();
		filename = filename.replaceAll("\"", "");
		//filename = filename + waypoints + ".txt";
		filename = filename + ".txt";
		
		//System.out.println("Will write to file" + filename);
		File file = new File(filename);
		FileWriter writer = new FileWriter(file, true);
		writer.write(""+waypoints+" "+distance+" "+wastedDistance+" "+chargeDistance+System.getProperty("line.separator"));
		writer.close();
		
		return true;
	}
}
