package org.kcl.nestor.env.scripted;

import jason.asSyntax.Literal;
import jason.asSyntax.Structure;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

public class ScriptedEnvironmentActionsTest extends TestCase {
	
	protected ScriptedEnvironment environment;
	protected ScriptedEnvironmentActions actions;
	
	protected static final String AGENT_NAME = "TestAgent";

	protected void setUp() throws Exception {
		//super.setUp();
		environment = new ScriptedEnvironment();
		actions = new ScriptedEnvironmentActions(environment,"act");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testExecuteAction() {
		environment.addPercept(Literal.parseLiteral("at(a)"));
		environment.addPercept(Literal.parseLiteral("batt(10)"));
		Structure moveAction = Structure.parse("move(a,b)");
		boolean success = actions.executeAction(AGENT_NAME, moveAction);
		if(!success) {
			fail("Failed to execute action: "+moveAction);
		} else {
			List<Literal> percepts = environment.getPercepts(AGENT_NAME);
			boolean foundA = false;
			boolean foundB = false;
			Literal literalA = Literal.parseLiteral("at(a)");
			Literal literalB = Literal.parseLiteral("at(b)");
			for (Iterator<Literal> iter = percepts.iterator(); iter.hasNext();) {
				Literal literal = iter.next();
				if(literal.equals(literalA)) {
					foundA = true;
				} else if (literal.equals(literalB)) {
					foundB = true;
				}
			}
			if(foundA || !foundB) {
				fail("Failed to execute action: "+moveAction+" environment not modified.");
			}
		}
	}

	public void testUpdateBattery() {
		Literal battLiteral = Literal.parseLiteral("batt(10)");
		Literal battLiteralExpected = Literal.parseLiteral("batt(9)");
		
		battLiteral = actions.updateBattery(battLiteral);
		
		if(!battLiteral.equals(battLiteralExpected)) {
			fail("Failure to update battery, expecting "+battLiteralExpected+", but got"+battLiteral);
		}
	}

}
