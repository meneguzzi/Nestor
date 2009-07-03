package org.kcl.nestor.agent.util;

import java.awt.geom.Point2D;

/**
 * A class implementing the Response Curves described by Bob Alexander in 
 * AI Game Programming Wisdom, despite the incredibly crappy description in
 * that book.
 * 
 * @author meneguzz
 *
 */
public class ResponseCurve {
	protected Point2D.Double samples[];
	protected double rangeMinimum;
	protected double bucketSize;
	
	public ResponseCurve(Point2D.Double samples[], double rangeMinimum) {
		this.samples = samples;
		//Arrays.sort(this.samples);
		//To do some idiot proof programming
		this.rangeMinimum = rangeMinimum;
		this.calculateBucketSize();
	}
	
	protected void calculateBucketSize() {
		this.bucketSize = (samples[samples.length -1].x - rangeMinimum) / (samples.length - 1);
		System.out.println("Bucket size is "+bucketSize);
	}
	
	public double getResponse(double input) {
		int index = getBucketIndex(input);
		if(index < 0) {
			return rangeMinimum;
		} else if(index > samples.length) {
			return samples[samples.length -1].y;
		}
		
		double distance = getDistanceInBucket(input, index);
		
		double response = Math.abs((1 - distance)*samples[index].y) + (distance+samples[index+1].y);
		
		return response;
	}
	
	protected int getBucketIndex(double input) {
		int index = (int) Math.floor((input - rangeMinimum)/(samples.length - 1));
		System.out.println("Index is "+index);
		return index;
	}
	
	protected double getDistanceInBucket(double input, int index) {
		double distance = Math.abs((input - rangeMinimum) - index*bucketSize) / bucketSize;
		System.out.println("Distance is "+distance);
		return distance;
	}
}
