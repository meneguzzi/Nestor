package org.kcl.nestor.mot.bb;

import jason.asSyntax.Literal;
import jason.bb.BeliefBase;
import jason.bb.DefaultBeliefBase;

import java.util.Iterator;


/**
 * A temporary BeliefBase intented to make predictions about events on another
 * belief base. This class can be reset to the same state as the original belief
 * base used in its instantiation.
 * 
 * @author meneguzzi
 *
 */
public class PredictiveBeliefBase extends DefaultBeliefBase {
	protected BeliefBase originalBeliefBase = null;
	
	public PredictiveBeliefBase() {
		this(null);
	}
	
	public PredictiveBeliefBase(BeliefBase base) {
		super();
		this.originalBeliefBase = base;
		this.reset();
	}
	
	public void reset() {
		if(this.size() > 0){
			for (Iterator<Literal> i = this.getAll(); i.hasNext();) {
				Literal literal = i.next();
				this.remove(literal);
			}
		}
		
		for(Iterator<Literal> i = originalBeliefBase.getAll(); i.hasNext(); ){
			this.add(i.next());
		}
	}
	
	public BeliefBase clone() {
		PredictiveBeliefBase beliefBase = new PredictiveBeliefBase(this.originalBeliefBase);
		
		for (Iterator<Literal> i = super.getAll(); i.hasNext();) {
			Literal l	= i.next();
			beliefBase.add(l);
		}
		
		return beliefBase;
	}
}
