/**
 * @file MovingAverage.java
 * @Author Sarthak Bansal
 * @date July 25, 2018
 * @brief Entity for strategies
 * 
 * Calculates moving average based on prices/period specification
 * Used by two moving average strategies
 *  
 */

package StrategyAlgos;

import java.util.LinkedList;
import java.util.Queue;

public class MovingAverage {
	private final Queue<Double> Dataset = new LinkedList<Double>();
	private int period;
	private double sum;

	public MovingAverage(int period) {
		this.period = period;
		sum = 0;
	}

	public void addData(double num) {
		sum += num;
		Dataset.add(num);
		if (Dataset.size() > period) {
			sum -= Dataset.remove();
		}
	}

	public double getMean() {
		return (sum / period);
	}

	public double getStd() {
		double mean = sum / period;
		double numi = 0;
		double num = 0;

		for (double i : Dataset) {
			numi = Math.pow((i - mean), 2);
			num += numi;
		}
		return Math.sqrt(num / Dataset.size());
	}

}
