package org.soa.wsdream.wvsp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.core.Variable;
import jmetal.encodings.variable.Int;
import jmetal.util.Ranking;

import org.femosaa.core.EAConfigure;
import org.femosaa.core.SAS;
import org.femosaa.core.SASAlgorithmAdaptor;
import org.femosaa.core.SASVarEntity;
import org.femosaa.seed.NewSeeder;
import org.femosaa.seed.NewSeeder.SeedingStrategy;
import org.femosaa.util.Logger;
import org.soa.femosaa.AbstractService;
import org.soa.femosaa.ConcreteService;
import org.soa.femosaa.SOADelegate;
import org.soa.femosaa.Workflow;
import org.soa.wsdream.seed.WSAbstractService;
import org.soa.wsdream.seed.WSConcreteService;
import org.soa.wsdream.seed.WSWorkflow;
import org.ssase.Service;
import org.ssase.objective.Objective;
import org.ssase.objective.QualityOfService;
import org.ssase.objective.optimization.femosaa.FEMOSAASolutionInstantiator;
import org.ssase.objective.optimization.femosaa.ibea.IBEAwithKAndDRegion;
import org.ssase.objective.optimization.femosaa.moead.MOEADRegion;
import org.ssase.objective.optimization.femosaa.moead.MOEAD_STMwithKAndDRegion;
import org.ssase.objective.optimization.femosaa.nsgaii.NSGAIIwithKAndDRegion;
import org.ssase.objective.optimization.femosaa.nsgaii.NSGAIIwithZeroAndOneRegion;
import org.ssase.objective.optimization.gp.GPRegion;
import org.ssase.objective.optimization.hc.HCRegion;
import org.ssase.objective.optimization.rs.RSRegion;
import org.ssase.objective.optimization.sa.SARegion;
import org.ssase.objective.optimization.sga.SGARegion;
import org.ssase.primitive.ControlPrimitive;
import org.ssase.primitive.Primitive;
import org.ssase.region.OptimizationType;
import org.ssase.region.Region;
import org.ssase.util.Repository;
import org.ssase.util.Ssascaling;

/**
 * 
 * For pareto vs weight work
 *
 */
public class WSSimulator {
	
	
	static List<Objective> o = new ArrayList<Objective>();
	private static final String prefix = "/Users/tao/research/experiments-data/seed/prerun-10pdiff/";
	private static final String setting = "15AS3" +
			"/";
	public static String alg = "nsgaii"; //0= ga 1=nsgaii
	
	private static long global_time = 0;
	private static int count = 0;
	static List<ControlPrimitive> cp = null;
	public static double[] weights;
	public static double[][] fixed_bounds = null;
	
	
	static {
		//NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.AO_Seed);
		//NewSeeder.getInstance().no_of_seed = 0; //10, 30, (50), 70, 90
	}
	
	static String[] remove_strings = new String[]{
		"sas-rubis_software-Latency", 
		//"sas-rubis_software-Throughput",
		//"sas-rubis_software-Cost"
	};
	
	private static void printSurface() {
		//String p = "p6/soa/NSGAII/T-20/sas/sas/NSGAIIkd/";
		String p = "p6/soa/IBEA/T-20/sas/sas/IBEAkd/";
		List<Double> tp = readFile(p+"Throughput.rtf");
		List<Double> cost = readFile(p+"Cost.rtf");
		
		//for (int i = 0; i < cvs.length;i++) {
		//	System.out.print(cost.get(i)  + " " + cvs[i] + " " + (1/tp.get(i))   + "\n");
		//}
	}
	

	private static void printPlot(String p_v, String t) {
		//String p = p_v+"/soa/NSGAII/"+t+"/sas/sas/NSGAIIkd/";
		String p = p_v+"/soa/IBEA/"+t+"/sas/sas/IBEAkd/";
		
		List<Double> tp = readFile(p+"Throughput.rtf");
		List<Double> cost = readFile(p+"Cost.rtf");
		
		for (int i = 0; i < cost.size();i++) {
			System.out.print("("+cost.get(i)  + "," + (tp.get(i))   + ")\n");
		}
	}
	
	private static List<Double> readFile(String path) {
		List<Double> list = new ArrayList<Double> ();
		
		
		File file = new File("/Users/tao/research/experiments-data/fuzzy-requirement/mo-results/"+path);
		
		try {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		while((line = reader.readLine()) != null) {
			list.add(Double.parseDouble(line));
		}
		}catch (Exception e) {
			
		}
		
		return list;
	}
	
	private static void readSingleRunFile() {
		HashSet<String> set = new HashSet<String> ();
		
		
		File file = new File("/Users/tao/research/monitor/ws-soa/sas/SolutionSet.rtf");
		
		try {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		while((line = reader.readLine()) != null) {
			
			if(line.startsWith("-----")) {
				continue;
			}
			
			String d = "(" + line.split(",")[1] + "," + line.split(",")[0]+")";
			
			set.add(d);
		}
		}catch (Exception e) {
			
		}
		
		for (String s : set) {
			System.out.print(s + "\n");
		}
	}

	
	public static void main(String[] arg) {

		// double v = 3.0;
		// double d = 1/v;
		// System.out.print(d);
		main_test();
		
		//readSingleRunFile();
		//printSurface();
		/*printPlot("p1","");
		for (int i = 2; i < 8;i++) {
		    printPlot("p"+i,"T-5");
		    printPlot("p"+i,"T-20");
		}*/
	}

	public static void setup() {
		Ssascaling.activate();
		WSWorkflow workflow = new WSWorkflow();

		EAConfigure.getInstance().setupWSConfiguration();
		//EAConfigure.getInstance().setupFLASHConfigurationVariant(Integer.parseInt(AutoRun.config));
		//EAConfigure.getInstance().setupWSConfigurationOnlyMutation();
		//System.out.print(EAConfigure.getInstance().generation + "*********\n");
		// List<WSAbstractService> as = workflow.all;
		// List<WSConcreteService> exist = new ArrayList<ConcreteService>();
		// for (AbstractService a : as) {
		// exist.addAll(a.getOption());
		// }

	    cp = new ArrayList<ControlPrimitive>();

		Set<ControlPrimitive> set = new HashSet<ControlPrimitive>();
		for (Service s : Repository.getAllServices()) {

			for (Primitive p : s.getPossiblePrimitives()) {
				if (p instanceof ControlPrimitive) {
					set.add((ControlPrimitive) p);
				}
			}

		}

		cp.addAll(set);
		Collections.sort(cp, new Comparator() {

			public int compare(Object arg0, Object arg1) {
				ControlPrimitive cp1 = (ControlPrimitive) arg0;
				ControlPrimitive cp2 = (ControlPrimitive) arg1;
				int value1 = cp1.getName().length()>3? Integer.parseInt(cp1.getName().substring(2,4)) : Integer.parseInt(String.valueOf(cp1.getName().charAt(2)));
				int value2 = cp2.getName().length()>3? Integer.parseInt(cp2.getName().substring(2,4)) : Integer.parseInt(String.valueOf(cp2.getName().charAt(2)));
				
				if(cp1.getName().equals("AS100")) {
					value1 = 100;
				}
				if(cp2.getName().equals("AS100")) {
					value2 = 100;
				}
				//System.out.print(value1 + "-----------:------------" + value2 + "\n");
				return value1 < value2 ? -1 : 1;
			}

		});

		// Assume all objectives have the same order and inputs
		
		for (ControlPrimitive p : cp) {
			System.out.print("*****" + p.getName() + "\n");
		}
		// Region.selected = OptimizationType.FEMOSAA01 ;
		Ssascaling.loadFeatureModel(cp);

		
		
		// compact(cp, "CS1", 0);
		// compact(cp, "CS2", 1);
		// compact(cp, "CS3", 2);
		// compact(cp, "CS4", 3);
		// compact(cp, "CS5", 4);

		for (int i = 0; i < WSWorkflow.total_number_of_as; i++) {
			compact(cp, "AS" + (i + 1), i);
		}

		

		WSSOADelegate qos0 = new WSSOADelegate(0, workflow);
		WSSOADelegate qos1 = new WSSOADelegate(1, workflow);
		WSSOADelegate qos2 = new WSSOADelegate(2, workflow);

		Set<Objective> obj = Repository.getAllObjectives();
		
		for (Objective ob : obj) {
			
			for (String s : remove_strings) {
				if(s.equals(ob.getName())) {
					obj.remove(ob);
				}
			}
			
		}
		
		
		for (Objective ob : obj) {
			if ("sas-rubis_software-Latency".equals(ob.getName())) {
				o.add(ob);
			}
		}

		for (Objective ob : obj) {
			if ("sas-rubis_software-Throughput".equals(ob.getName())) {
				o.add(ob);
			}
		}

		for (Objective ob : obj) {
			if ("sas-rubis_software-Cost".equals(ob.getName())) {
				o.add(ob);
			}
		}

		for (Objective ob : o) {

			QualityOfService qos = (QualityOfService) ob;
			if (qos.getName().equals("sas-rubis_software-Latency")) {
				qos.setDelegate(qos0);
			} else if (qos.getName().equals("sas-rubis_software-Throughput")) {
				qos.setDelegate(qos1);
			} else {
				qos.setDelegate(qos2);
			}

		}
	}
	
	public static void main_test() {

		

		//Repository.initUniformWeight("W3D_105.dat", 105);
		long t = System.currentTimeMillis() ;
		List<Double> cv = new ArrayList<Double>();
		/*for (int i = 0; i < 10; i++) {
		if(i!=9) {
			workflow.complexSetup();
			WSWorkflow.map.clear();
			for (int k = 0; k < WSWorkflow.total_number_of_as; k++) {
				compact(cp, "AS" + (k + 1), k);
			}
			continue;//return;
		}
		}*/
		long time = 0; 
		int n = 100;//100;//30 
		for (int i = 0; i < n;/*1*/ i++) {
			System.out.print("start*****\n");
			//org.femosaa.core.SASSolution.putDependencyChainBack();

//			preRunAOOrSOSeed();
//			testNSGAII();
		
//			
			if(alg.equals("nsgaii")) {
				testNSGAII();
			} else if(alg.equals("ga")) {
				 GA(weights);
			} else if(alg.equals("hc")) {
				HC(weights);
			} else if(alg.equals("rs")) {
				RS(weights);
			} else if(alg.equals("sa")) {
				SA(weights);
			} else if(alg.equals("moead")) {
				MOEAD();
			}
			
			 
			
			
			
			
		}
			time += System.currentTimeMillis() - t;
			//System.out.print("Number of seeds: " + NewSeeder.getInstance().getSeedsCount() + "\n");
//			

			//cv.add(getCV());
			//WSWorkflow.global_number++;
			//******************
			//workflow.complexSetup();
			//WSWorkflow.map.clear();
			//for (int k = 0; k < WSWorkflow.total_number_of_as; k++) {
				//compact(cp, "AS" + (k + 1), k);
			//}
			//******************
		
		
		System.out.print("Excution time is " + (time)/n);
		
		/*for (double c_v : cv) {
			System.out.print(c_v+",\n");
		}*/
		
	}
	
	private static void testMOEAD() {
		double[] r = null;
		Region.selected = OptimizationType.FEMOSAA;

		System.out
				.print("=============== MOEAD_STMwithKAndDRegion ===============\n");
		MOEAD_STMwithKAndDRegion moead = new MOEAD_STMwithKAndDRegion();
		moead.addObjectives(o);
		long time = System.currentTimeMillis();
		moead.optimize();
		// r = getFitness(moead.optimize());
		org.ssase.util.Logger.logOptimizationTime(null,
				String.valueOf((System.currentTimeMillis() - time)));
		// logData("sas", "Throughput", String.valueOf(r[0]));
		// logData("sas", "Cost", String.valueOf(r[1]));

	}
	
	private static void GA(double[] weights) {
		double[] r = null;
		Region.selected = OptimizationType.SGA;

		System.out
				.print("=============== SGARegion ===============\n");
		SGARegion moead = new SGARegion(weights,fixed_bounds);
		moead.addObjectives(o);
		long time = System.currentTimeMillis();
		LinkedHashMap<ControlPrimitive, Double> result = moead.optimize();
		//BenchmarkDelegate qos0 = new BenchmarkDelegate();
		double[] x = new double[result.size()]; 
		int i = 0;
		for (Entry<ControlPrimitive, Double> e : result.entrySet()) {
			x[i] = e.getValue();
			i++;
		}
		
		//overall.add(qos0.predict(x)/100);
		r = getFitness(result);
		org.ssase.util.Logger.logOptimizationTime(null,
				String.valueOf((System.currentTimeMillis() - time)));
		logData("sas", "Throughput", String.valueOf(r[0]));
		logData("sas", "Cost", String.valueOf(r[1]));

	}
	
	private static void HC(double[] weights) {
		double[] r = null;
		Region.selected = OptimizationType.HC;

		System.out
				.print("=============== HCRegion ===============\n");
		HCRegion moead = new HCRegion(weights,fixed_bounds);
		moead.addObjectives(o);
		long time = System.currentTimeMillis();
		LinkedHashMap<ControlPrimitive, Double> result = moead.optimize();
		//BenchmarkDelegate qos0 = new BenchmarkDelegate();
		double[] x = new double[result.size()]; 
		int i = 0;
		for (Entry<ControlPrimitive, Double> e : result.entrySet()) {
			x[i] = e.getValue();
			i++;
		}
		
		//overall.add(qos0.predict(x)/100);
		r = getFitness(result);
		org.ssase.util.Logger.logOptimizationTime(null,
				String.valueOf((System.currentTimeMillis() - time)));
		logData("sas", "Throughput", String.valueOf(r[0]));
		logData("sas", "Cost", String.valueOf(r[1]));

	}
	
	private static void RS(double[] weights) {
		double[] r = null;
		Region.selected = OptimizationType.RS;

		System.out
				.print("=============== RSRegion ===============\n");
		RSRegion moead = new RSRegion(weights,fixed_bounds);
		moead.addObjectives(o);
		long time = System.currentTimeMillis();
		LinkedHashMap<ControlPrimitive, Double> result = moead.optimize();
		//BenchmarkDelegate qos0 = new BenchmarkDelegate();
		double[] x = new double[result.size()]; 
		int i = 0;
		for (Entry<ControlPrimitive, Double> e : result.entrySet()) {
			x[i] = e.getValue();
			i++;
		}
		
		//overall.add(qos0.predict(x)/100);
		r = getFitness(result);
		org.ssase.util.Logger.logOptimizationTime(null,
				String.valueOf((System.currentTimeMillis() - time)));
		logData("sas", "Throughput", String.valueOf(r[0]));
		logData("sas", "Cost", String.valueOf(r[1]));

	}
	
	
	private static void SA(double[] weights) {
		double[] r = null;
		Region.selected = OptimizationType.SA;

		System.out
				.print("=============== SARegion ===============\n");
		SARegion moead = new SARegion(weights,fixed_bounds);
		moead.addObjectives(o);
		long time = System.currentTimeMillis();
		LinkedHashMap<ControlPrimitive, Double> result = moead.optimize();
		//BenchmarkDelegate qos0 = new BenchmarkDelegate();
		double[] x = new double[result.size()]; 
		int i = 0;
		for (Entry<ControlPrimitive, Double> e : result.entrySet()) {
			x[i] = e.getValue();
			i++;
		}
		
		//overall.add(qos0.predict(x)/100);
		r = getFitness(result);
		org.ssase.util.Logger.logOptimizationTime(null,
				String.valueOf((System.currentTimeMillis() - time)));
		logData("sas", "Throughput", String.valueOf(r[0]));
		logData("sas", "Cost", String.valueOf(r[1]));

	}
	

	private static void testNSGAII() {
		double[] r = null;
		Region.selected = OptimizationType.NSGAIIkd;

		System.out
				.print("=============== NSGAIIwithKAndDRegion ===============\n");
		NSGAIIwithKAndDRegion moead = new NSGAIIwithKAndDRegion();
		moead.addObjectives(o);
		long time = System.currentTimeMillis();
		//moead.optimize();
	    r = getFitness(moead.optimize());
		org.ssase.util.Logger.logOptimizationTime(null,
				String.valueOf((System.currentTimeMillis() - time)));
		logData("sas", "Throughput", String.valueOf(r[0]));
		logData("sas", "Cost", String.valueOf(r[1]));

	}
	
	private static void testIBEA() {
		double[] r = null;
		Region.selected = OptimizationType.IBEAkd;

		System.out
				.print("=============== IBEAwithKAndDRegion ===============\n");
		IBEAwithKAndDRegion moead = new IBEAwithKAndDRegion();
		moead.addObjectives(o);
		long time = System.currentTimeMillis();
		//moead.optimize();
		r = getFitness(moead.optimize());
		org.ssase.util.Logger.logOptimizationTime(null,
				String.valueOf((System.currentTimeMillis() - time)));
		 logData("sas", "Throughput", String.valueOf(r[0]));
		 logData("sas", "Cost", String.valueOf(r[1]));

	}
	
	private static void MOEAD() {
		double[] r = null;
		Region.selected = OptimizationType.FEMOSAA;
		if(AutoRun.benchmark.equals("5AS-3") || AutoRun.benchmark.equals("10AS-3") || AutoRun.benchmark.equals("15AS-3")) {
			Repository.initUniformWeight("W3D_"+EAConfigure.getInstance().pop_size+".dat", EAConfigure.getInstance().pop_size);
		}
		
		System.out
				.print("=============== MOEADRegion ===============\n");
		//MOEAD_STMwithKAndDRegion moead = new MOEAD_STMwithKAndDRegion();
		MOEADRegion moead = new MOEADRegion();
		moead.addObjectives(o);
		long time = System.currentTimeMillis();
		LinkedHashMap<ControlPrimitive, Double> result = moead.optimize();
		//BenchmarkDelegate qos0 = new BenchmarkDelegate();
		double[] x = new double[result.size()]; 
		int i = 0;
		for (Entry<ControlPrimitive, Double> e : result.entrySet()) {
			x[i] = e.getValue();
			i++;
		}
		
		//overall.add(qos0.predict(x)/100);
		// r = getFitness(moead.optimize());
		org.ssase.util.Logger.logOptimizationTime(null,
				String.valueOf((System.currentTimeMillis() - time)));
		// logData("sas", "Throughput", String.valueOf(r[0]));
		// logData("sas", "Cost", String.valueOf(r[1]));

	}
	
	

	private static void compact(List<ControlPrimitive> cp, String name,
			int index) {
		int no = -1;
		for (int k = 0; k < cp.size(); k++) {

			if (cp.get(k).getName().equals(name)) {
				no = k;
				break;
			}

		}

		WSWorkflow.map.put(WSWorkflow.all.get(index), no);
	}
	
	private static double getCV(){
		
	
		List<Double> t = new ArrayList<Double>();
		List<Double> c = new ArrayList<Double>();
		for(WSAbstractService was : WSWorkflow.all){
			
			for(WSConcreteService wcs : was.getOption()){
				t.add(wcs.getThroughput());
				c.add(wcs.getCost());
			}
			
		}
		
		double t_std = getStD(t);
		double c_std = getStD(c);
		
		return (t_std+c_std) / 2.0;
		
	}
	
	private static double getStD(List<Double> list){
		double mean = 0.0;
		for (Double d : list) {
			mean += d;
		}
		
		mean = mean / list.size();
		double std = 0.0;
		for (Double d : list) {
			std += Math.pow((d - mean),2);
		}
		
		std = std / list.size();
		std = Math.pow(std, 0.5);
		std = std / mean;
		
		return std;
	}
		
	
	private static synchronized void logSolutionSetValues(LinkedHashMap<ControlPrimitive, Double> list, String name){
		File file = null;
		if(!(file = new File(org.femosaa.util.Logger.prefix)).exists()){
			file.mkdir();
		} 
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(org.femosaa.util.Logger.prefix
					+ name, true));

			String data = "";
				for(int i = 0; i < cp.size(); i++) {
					data +=  list.get(cp.get(i)) - 1.0 /*to change it back to index value*/ + (i ==  cp.size() - 1? "" : ",");
				}
				data += "\n";
			
			
			bw.write(data);
			bw.write("------------------------\n");
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	private static double[] getFitness(LinkedHashMap<ControlPrimitive, Double> result){
		double[] r = new double[2];
		for (Objective obj : Repository.getAllObjectives()) {
			double[] xValue = new double[obj.getPrimitivesInput().size()];
			for (int i = 0; i < xValue.length; i++) {
				
				if (obj.getPrimitivesInput().get(i) instanceof ControlPrimitive) {
					xValue[i] = result.get(obj.getPrimitivesInput().get(i));
				} else {
					xValue[i] = obj.getPrimitivesInput().get(i).getProvision();
				}
				
				 
			}
			
			
			double adapt = obj.predict(xValue);
			
			if(obj.getName().equals("sas-rubis_software-Throughput")) {
				r[0] = 1.0/adapt;
			} else {
				r[1] = adapt;
			}
			
			
			

		
			
			//System.out.print(obj.getName() + " current value: " + obj.getCurrentPrediction() + " - after adaptation: " + adapt + "\n");
			
		
		}
		
		//Seeder.posteriorObjetive(new double[]{1/r[0], r[1]});
		
		return r;
	}
	
	 private static synchronized void logData(String VM_ID, String qos, String data){
			
			if(VM_ID == null) VM_ID = "sas";
			if(QualityOfService.isDelegate()) VM_ID = VM_ID + "/" + Region.selected;
			File file = null;
			if(!(file = new File(Logger.prefix
					+ VM_ID + "/")).exists()){
				file.mkdirs();
			} 
			
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(Logger.prefix
						+ VM_ID + "/" + qos +  ".rtf", true));

				
				//System.out.print(data.toString() + "\n");
				bw.write(data + "\n");
				bw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	

}