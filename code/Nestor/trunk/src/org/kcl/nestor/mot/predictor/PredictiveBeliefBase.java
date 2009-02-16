package org.kcl.nestor.mot.predictor;

import jason.asSyntax.Literal;
import jason.asSyntax.LiteralImpl;
import jason.bb.BeliefBase;
import jason.bb.DefaultBeliefBase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;


/**
 * A temporary BeliefBase intented to make predictions about events on another
 * belief base. This class can be reset to the same state as the original belief
 * base used in its instantiation.
 * 
 * @author meneguzzi
 *
 */
public class PredictiveBeliefBase extends DefaultBeliefBase {
	protected static final Logger logger = Logger.getLogger(PredictiveBeliefBase.class.getName());
	protected BeliefBase originalBeliefBase;
	
	public PredictiveBeliefBase() {
		this(null);
	}
	
	public PredictiveBeliefBase(BeliefBase originalBeliefBase) {
		super();
		this.originalBeliefBase = originalBeliefBase;
		this.reset();
	}
	
	/**
	 * Returns the differences between this belief base and the 
	 * original one. 
	 * @return
	 */
	public List<Literal> getDelta() {
		List<Literal> delta = new ArrayList<Literal>();
		
		for (Iterator<Literal> i = this.iterator(); i.hasNext();) {
			Literal literal = i.next();
			Literal original = originalBeliefBase.contains(literal);
			if(original == null || !original.equals(literal)) {
				literal = (Literal) literal.clone();
				delta.add(literal);
			}
		}
		
		for(Iterator<Literal> i = originalBeliefBase.iterator(); i.hasNext(); ) {
			Literal literal = i.next();
			Literal original = this.contains(literal);
			if(original == null || !original.equals(literal)) {
				literal = (Literal) literal.clone();
				literal.setNegated(literal.negated());
				delta.add(literal);
			}
		}
		
		return delta;
	}
	
	/**
	 * Applies the list of literals to the predictive belief base,
	 * updating it to maintain consistency.
	 * @param newBeliefs
	 */
	public void applyBeliefs(List<Literal> newBeliefs) {
		for (Literal literal : newBeliefs) {
			Literal existing = this.contains(literal);
			if(existing == null) {
				//If the belief is not on the belief base, we might look for
				//it's negation
				Literal negated = new LiteralImpl(literal.negated(), literal);
				existing = this.contains(literal);
				//If the negated version exists, and the new belief is false,
				//remove the existing one.
				if(existing != null && literal.negated()) {
					remove(existing);
				} else {
					add(literal);
				}
			}
		}
	}
	
	public void reset() {
		if(this.size() > 0){
			for (Iterator<Literal> i = this.iterator(); i.hasNext();) {
				Literal literal = i.next();
				this.remove(literal);
			}
		}
		
		for(Iterator<Literal> i = originalBeliefBase.iterator(); i.hasNext(); ){
			this.add(i.next());
		}
	}
	
	public BeliefBase clone() {
		PredictiveBeliefBase beliefBase = new PredictiveBeliefBase(this.originalBeliefBase);
		
		for (Iterator<Literal> i = super.iterator(); i.hasNext();) {
			Literal l	= i.next();
			beliefBase.add(l);
		}
		
		return beliefBase;
	}
}
