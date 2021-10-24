package org.sas.benchmark.pw.algorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import jmetal.util.JMException;

import org.femosaa.core.EAConfigure;
import org.femosaa.core.SASAlgorithmAdaptor;
import org.femosaa.seed.NewSeeder;
import org.femosaa.util.Logger;
import org.sas.benchmark.pw.nrp.Parser;
import org.ssase.Service;
import org.ssase.objective.Objective;
import org.ssase.objective.QualityOfService;
import org.ssase.objective.optimization.femosaa.nsgaii.NSGAIIwithKAndDRegion;
import org.ssase.objective.optimization.gp.GPRegion;
import org.ssase.objective.optimization.hc.HCRegion;
import org.ssase.objective.optimization.rs.RSRegion;
import org.ssase.objective.optimization.sga.SGARegion;
import org.ssase.primitive.ControlPrimitive;
import org.ssase.primitive.Primitive;
import org.ssase.region.OptimizationType;
import org.ssase.region.Region;
import org.ssase.util.Repository;
import org.ssase.util.Ssascaling;

/**
 * 
 *
 */
public class Simulator 
{
	
	static List<Objective> o = new ArrayList<Objective>();
	static List<ControlPrimitive> cp = null;
	//static List<Double> overall = new  ArrayList<Double>();
	public static String alg = "hc";
	public static double[][] fixed_bounds = null;
	
	
	public static double[] weights;
    public static void main( String[] args )
    {
    	setup();
    	main_test();
    }
    
    public static void setup() {
    	//Ssascaling.activate();
		Parser.main(null);

		EAConfigure.getInstance().setupNRPConfiguration();
		//EAConfigure.getInstance().setupNRPConfigurationVariant(Integer.parseInt(AutoRun.config));
		//System.out.print(EAConfigure.getInstance().generation + "*********\n");
		// List<WSAbstractService> as = workflow.all;
		// List<WSConcreteService> exist = new ArrayList<ConcreteService>();
		// for (AbstractService a : as) {
		// exist.addAll(a.getOption());
		// }

		

		
		
		// compact(cp, "CS1", 0);
		// compact(cp, "CS2", 1);
		// compact(cp, "CS3", 2);
		// compact(cp, "CS4", 3);
		// compact(cp, "CS5", 4);

	
	//	if(1==1)
	//		return;
		

		//BenchmarkDelegate qos0 = new BenchmarkDelegate(0);
		//BenchmarkDelegate qos1 = new BenchmarkDelegate(1);
		//BenchmarkDelegate qos1 = new WSSOADelegate(1, workflow);
		//BenchmarkDelegate qos2 = new WSSOADelegate(2, workflow);

		Set<Objective> obj = Repository.getAllObjectives();
		
//		for (Objective ob : obj) {
//			
//			for (String s : remove_strings) {
//				if(s.equals(ob.getName())) {
//					obj.remove(ob);
//				}
//			}
//			
//		}
//		
		
/*		for (Objective ob : obj) {
			if ("sas-rubis_software-profit".equals(ob.getName())) {
				o.add(ob);
			} else if ("sas-rubis_software-cost".equals(ob.getName())) {
				o.add(ob);
			}
		}*/

//		for (Objective ob : obj) {
//			if ("sas-rubis_software-Throughput".equals(ob.getName())) {
//				o.add(ob);
//			}
//		}
//
//		for (Objective ob : obj) {
//			if ("sas-rubis_software-Cost".equals(ob.getName())) {
//				o.add(ob);
//			}
//		}

		for (Objective ob : o) {

		
			
//			else if (qos.getName().equals("sas-rubis_software-Throughput")) {
//				qos.setDelegate(qos1);
//			} else {
//				qos.setDelegate(qos2);
//			}

		}
    }
    
    public static void main_test() {

	

		//Repository.initUniformWeight("W3D_105.dat", 105);
		//int max_number_of_eval_to_have_only_seed = 0;
		long time = 0; 
		int n = 100;//30 
		for (int i = 0; i < n;/*1*/ i++) {
			long t = System.currentTimeMillis(); 
			//org.femosaa.core.SASSolution.putDependencyChainBack();

//			preRunAOOrSOSeed();

			if(alg.equals("ga")) {
				GA(weights, i);
			} else if(alg.equals("hc")) {
				HC(weights, i);
			} else if(alg.equals("rs")) {
				RS(weights, i);
			} else if(alg.equals("sa")) {
				SA(weights, i);
			} else if(alg.equals("nsgaii"))  {
				NSGAII();
			} else if(alg.equals("moead"))  {
				MOEAD();
			}
			//testGA();
			//testHC();
			//testRS();
//			if(1==1) return;
//		
			
			time += System.currentTimeMillis() - t;


			
		}
		
		//for (Double d : overall) {
		//	System.out.print("("+d + ")\n");
		//}
		
	}
  
	private static void GA(double[] weights, int run) {
		double[] r = null;
		Region.selected = OptimizationType.SGA;
		int n = 2;
		if(AutoRun.benchmark.equals("nrp-e-3") || AutoRun.benchmark.equals("nrp-g-3") || AutoRun.benchmark.equals("nrp-m-3")) {
			n = 3;
		}

		System.out
				.print("=============== SGARegion ===============\n");
		System.out
		.print("=============== " + run + ", " + weights[0] + " : " + weights[1] +" ===============\n");
		SGA_SAS_main moead = new SGA_SAS_main();

		long time = System.currentTimeMillis();
		try {
			moead.findParetoFront(weights, fixed_bounds, Parser.map.size(), n, 0);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//overall.add(qos0.predict(x)/100);
		// r = getFitness(moead.optimize());
		org.ssase.util.Logger.logOptimizationTime(null,
				String.valueOf((System.currentTimeMillis() - time)));
		// logData("sas", "Throughput", String.valueOf(r[0]));
		// logData("sas", "Cost", String.valueOf(r[1]));

	}
	
	private static void HC(double[] weights, int run) {
		double[] r = null;
		Region.selected = OptimizationType.HC;
		int n = 2;
		if(AutoRun.benchmark.equals("nrp-e-3") || AutoRun.benchmark.equals("nrp-g-3") || AutoRun.benchmark.equals("nrp-m-3")) {
			n = 3;
		}

		System.out
				.print("=============== HCRegion ===============\n");
		System.out
		.print("=============== " + run + ", " + weights[0] + " : " + weights[1] +" ===============\n");
		HC_SAS_main moead = new HC_SAS_main();

		long time = System.currentTimeMillis();
		try {
			moead.findParetoFront(weights, fixed_bounds, Parser.map.size(), n, 0);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//overall.add(qos0.predict(x)/100);
		// r = getFitness(moead.optimize());
		org.ssase.util.Logger.logOptimizationTime(null,
				String.valueOf((System.currentTimeMillis() - time)));
		// logData("sas", "Throughput", String.valueOf(r[0]));
		// logData("sas", "Cost", String.valueOf(r[1]));

	}
	
	private static void RS(double[] weights, int run) {
		double[] r = null;
		Region.selected = OptimizationType.RS;
		int n = 2;
		if(AutoRun.benchmark.equals("nrp-e-3") || AutoRun.benchmark.equals("nrp-g-3") || AutoRun.benchmark.equals("nrp-m-3")) {
			n = 3;
		}

		System.out
				.print("=============== RSRegion ===============\n");
		System.out
		.print("=============== " + run + ", " + weights[0] + " : " + weights[1] +" ===============\n");
		RS_SAS_main moead = new RS_SAS_main();

		long time = System.currentTimeMillis();
		try {
			moead.findParetoFront(weights, fixed_bounds, Parser.map.size(), n, 0);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//overall.add(qos0.predict(x)/100);
		// r = getFitness(moead.optimize());
		org.ssase.util.Logger.logOptimizationTime(null,
				String.valueOf((System.currentTimeMillis() - time)));
		// logData("sas", "Throughput", String.valueOf(r[0]));
		// logData("sas", "Cost", String.valueOf(r[1]));

	}
	
	private static void SA(double[] weights, int run) {
		double[] r = null;
		Region.selected = OptimizationType.SA;
		int n = 2;
		if(AutoRun.benchmark.equals("nrp-e-3") || AutoRun.benchmark.equals("nrp-g-3") || AutoRun.benchmark.equals("nrp-m-3")) {
			n = 3;
		}

		System.out
				.print("=============== SARegion ===============\n");
		System.out
		.print("=============== " + run + ", " + weights[0] + " : " + weights[1] +" ===============\n");
		SA_SAS_main moead = new SA_SAS_main();

		long time = System.currentTimeMillis();
		try {
			moead.findParetoFront(weights, fixed_bounds, Parser.map.size(), n, 0);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//overall.add(qos0.predict(x)/100);
		// r = getFitness(moead.optimize());
		org.ssase.util.Logger.logOptimizationTime(null,
				String.valueOf((System.currentTimeMillis() - time)));
		// logData("sas", "Throughput", String.valueOf(r[0]));
		// logData("sas", "Cost", String.valueOf(r[1]));

	}
	
	
	private static void NSGAII() {
		double[] r = null;
		Region.selected = OptimizationType.NSGAII;
		
		int n = 2;
		if(AutoRun.benchmark.equals("nrp-e-3") || AutoRun.benchmark.equals("nrp-g-3") || AutoRun.benchmark.equals("nrp-m-3")) {
			n = 3;
		}


		System.out
				.print("=============== NSGAIIRegion ===============\n");
		NSGA2_SAS_main moead = new NSGA2_SAS_main();
		long time = System.currentTimeMillis();
		try {
			moead.findParetoFront(Parser.map.size(), n, 0);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//overall.add(qos0.predict(x)/100);
		// r = getFitness(moead.optimize());
		org.ssase.util.Logger.logOptimizationTime(null,
				String.valueOf((System.currentTimeMillis() - time)));
		// logData("sas", "Throughput", String.valueOf(r[0]));
		// logData("sas", "Cost", String.valueOf(r[1]));

	}


	private static void MOEAD() {
		double[] r = null;
		int no = 2;
		Region.selected = OptimizationType.FEMOSAA;
		if(AutoRun.benchmark.equals("nrp-e-3") || AutoRun.benchmark.equals("nrp-g-3") || AutoRun.benchmark.equals("nrp-m-3")) {
			Repository.initUniformWeight("W3D_"+EAConfigure.getInstance().pop_size+".dat", EAConfigure.getInstance().pop_size);		
			no = 3;
		} else {
			Repository.initUniformWeight(); 
		}
			
		
		System.out
				.print("=============== MOEADRegion ===============\n");
		MOEAD_main moead = new MOEAD_main();
		long time = System.currentTimeMillis();
		try {
			moead.findParetoFront(Parser.map.size(), no, 0);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//overall.add(qos0.predict(x)/100);
		// r = getFitness(moead.optimize());
		org.ssase.util.Logger.logOptimizationTime(null,
				String.valueOf((System.currentTimeMillis() - time)));
		// logData("sas", "Throughput", String.valueOf(r[0]));
		// logData("sas", "Cost", String.valueOf(r[1]));

	}
	
	
	private static void IBEA() {
		double[] r = null;
		Region.selected = OptimizationType.IBEA;

		System.out
				.print("=============== IBEARegion ===============\n");
		IBEA_SAS_main moead = new IBEA_SAS_main();
		long time = System.currentTimeMillis();
		try {
			moead.findParetoFront(Parser.map.size(), 2, 0);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//overall.add(qos0.predict(x)/100);
		// r = getFitness(moead.optimize());
		org.ssase.util.Logger.logOptimizationTime(null,
				String.valueOf((System.currentTimeMillis() - time)));
		// logData("sas", "Throughput", String.valueOf(r[0]));
		// logData("sas", "Cost", String.valueOf(r[1]));

	}
}


/*
 * 
 * */
