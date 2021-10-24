package org.sas.benchmark.pw.nrp;

import java.util.HashMap;

import org.ssase.model.Delegate;

public class BenchmarkDelegate implements Delegate{

	
	private int obj_index = 0;
	
	
	
	public BenchmarkDelegate(int obj_index) {
		super();
		this.obj_index = obj_index;
	}



	@Override
	public double predict(double[] xValue) {
		double value = 0.0;

		// 0 is profit
		// xValue is binary
		//long time = System.currentTimeMillis();
		if(obj_index == 0) {
			
			for (int i = 0; i < xValue.length; i++) {
				value += xValue[i] * Parser.map.get(i).getProfit();
			}
			
			value = 1.0/value;
			
		} else if(obj_index == 1) {
			
			for (int i = 0; i < xValue.length; i++) {
				value += xValue[i] * Parser.map.get(i).getCost();
			}
		}
		//System.out.print("Evaluation time: " + (System.currentTimeMillis()-time) + "\n");
		return value*100;
	}

}
