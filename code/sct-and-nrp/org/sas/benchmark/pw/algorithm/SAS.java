package org.sas.benchmark.pw.algorithm;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.encodings.solutionType.BinarySolutionType;
import jmetal.encodings.solutionType.IntSolutionType;
import jmetal.encodings.variable.Int;
import jmetal.util.JMException;

import org.sas.benchmark.pw.nrp.Parser;
import org.ssase.util.Repository;




public class SAS extends Problem {

	public static final boolean isTest = false;
	public static final long TIME_THRESHOLD = 40000;
	
	/**
	 * Constructor
	 */
//	public SAS(String solutionType) throws ClassNotFoundException {
//		this(solutionType, 10);
//	}

	/**
	 * vars [number of variables][upper and lower bounds]
	 * 
	 * Create a new instance of problem for SAS
	 */
	public SAS( int vars, int numberOfObjectives_, int numberOfConstraints_) throws ClassNotFoundException {
		numberOfVariables_ = vars;
		this.numberOfObjectives_ = numberOfObjectives_;
		this.numberOfConstraints_ = numberOfConstraints_;
		problemName_ = "SAS";

		upperLimitSAS_ = new int[numberOfVariables_];
		lowerLimitSAS_ = new int[numberOfVariables_];
//
		double[] l = new double[vars];
		double[] u = new double[vars];
		for (int i = 0; i < vars; i++) {
			l[i] = 0;
			u[i] = 1;
		}
	
		this.lowerLimit_ = l;
		this.upperLimit_ = u;

		
		solutionType_ = new IntSolutionType(this);
	
	}
	
	/**
	 * Evaluate a solution: this is for PW
	 */
	public void evaluate(Solution solution) throws JMException {
		
		double v1 = 0.0;
		double v2 = 0.0;
		for (int i = 0; i < solution.getDecisionVariables().length; i++) {
			v1 += solution.getDecisionVariables()[i].getValue() * Parser.map.get(i).getProfit();
			v2 += solution.getDecisionVariables()[i].getValue() * Parser.map.get(i).getCost();
		}
		
		v2 = v1 == 0? Double.MAX_VALUE/100 : v2;
		v1 = v1 == 0? Double.MAX_VALUE/100 : 1.0/v1;//v1 == 0? Double.MAX_VALUE/100 : v1 * 1.0/v1;
		
		
		double v3 = Parser.getCostSTD(solution);
		v3 = v1 == 0 || v3 == 0? Double.MAX_VALUE/100 : v3;
		
		if(Parser.factory != null) {
			Parser.factory.updateNormalization(v1, 0);
			Parser.factory.updateNormalization(v2, 1);
			//Parser.factory.updateNormalization(v3, 2);
		}
		
		solution.setObjective(0, v1);
		solution.setObjective(1, v2);
		//solution.setObjective(2, v3);
		//solution.setObjective(2, v2);
	}
	
	public void evaluate1(Solution solution) throws JMException {
		
		double v1 = 0.0;
		double v2 = 0.0;
		for (int i = 0; i < solution.getDecisionVariables().length; i++) {					
			v2 += solution.getDecisionVariables()[i].getValue() * Parser.map.get(i).getCost();
		}
		
		for (int i = 0; i < Parser.c.size(); i++) {	
			Integer[] r = Parser.c.get(i);
			boolean sat = true;
			for (int j = 0; j < r.length; j++) {	
				if(solution.getDecisionVariables()[r[j]-1].getValue() == 0.0) {
					sat = false;
					break;
				}
			}
			
			if(sat) {
				v1 += Parser.p.get(i);
			}
		}
		
		v1 = v1 == 0? Double.MAX_VALUE/100 : 1.0/v1;
		
		if(Parser.factory != null) {
			Parser.factory.updateNormalization(v1, 0);
			Parser.factory.updateNormalization(v2, 1);
		}
		solution.setObjective(0, v1);
		solution.setObjective(1, v2);
	}
}