package org.sas.benchmark.pw;

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
		String v = "";
		for(int i = 0; i < xValue.length; i++) {
			v += v.equals("")? (int)xValue[i] : ":" + (int)xValue[i];
		}
	
		HashMap<String, Double> map = null;
		if(obj_index == 0) {
			map = Parser.map1;
		} else if(obj_index == 1) {
			map = Parser.map2;
		} else if(obj_index == 2) {
			map = Parser.map3;
		}
				
		//System.out.print(v + "***\n");
		if(map.containsKey(v)) {
		//	System.out.print(map.containsKey(v) + ": " + v + "***\n");
		}
		
		if(map.containsKey(v)) {
			double r = map.get(v);
			// Only needed for certain benchmarks
			if(obj_index == 0) {
				//r = 1.0/r;
				//r = -1.0 * r;
				//System.out.print(r + "*****\n");
			}
			
			return r*100;
		} else {
			return Double.MAX_VALUE;
		}
		
	
	}

}
