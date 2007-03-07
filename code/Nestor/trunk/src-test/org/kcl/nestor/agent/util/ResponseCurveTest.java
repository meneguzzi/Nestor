package org.kcl.nestor.agent.util;

import java.awt.geom.Point2D;

import junit.framework.TestCase;

public class ResponseCurveTest extends TestCase {
	
	Point2D.Double samples[] = {new Point2D.Double(20.0,2.0), 
								new Point2D.Double(30.0,40.0),
								new Point2D.Double(50.0,60.0),
								new Point2D.Double(70.0,100.0)};

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testResponseCurve() {		
		ResponseCurve curve = new ResponseCurve(samples, 2.0);
		if(curve == null) {
			fail();
		}
	}

	public void testGetResponse() {
		ResponseCurve curve = new ResponseCurve(samples, 2.0);
		double inputs[] = {1.0, 10.0, 90.0, 200.0};
		for (int i = 0; i < inputs.length; i++) {
			double response = curve.getResponse(inputs[i]);
			System.out.println("Input "+inputs[i]+" = "+response);
		}
	}

}
