package org.kcl.nestor.env.scripted;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.kcl.nestor.env.scripted");
		//$JUnit-BEGIN$
		suite.addTestSuite(ScriptedEnvironmentActionsTest.class);
		//$JUnit-END$
		return suite;
	}

}
