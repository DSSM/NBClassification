package de.htwberlin.ai.daweb;

/**
 * @author Sergej Mann <man53r@gmail.com>
 * @version 0.1
 */

public class CalculationData {

	public long count;
	public double condprob;

	public CalculationData() {
		this.count = 0l;
		this.condprob = 0.0f;
	}

	public CalculationData(long count) {
		this.count = count;
		this.condprob = 0.0f;
	}

	/*
	 * private long count; private double condprob; public CalculationData() {
	 * this.count = 1l; this.condprob = 0.0f; } public long getCount() { return
	 * this.count; } public void setCount(long count) { this.count = count; }
	 * public double getCondprob() { return this.condprob; } public void
	 * setCondprob(double condprob) { this.condprob = condprob; }
	 */

	@Override
	public String toString() {
		return "CalculationData{\"count\":" + count + ", \"condprob\":" + condprob + "}";
	}

}
