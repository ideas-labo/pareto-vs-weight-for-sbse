package org.soa.wsdream.seed;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.core.Variable;
import jmetal.encodings.variable.Int;
import jmetal.util.Ranking;

import org.femosaa.core.EAConfigure;
import org.femosaa.core.SAS;
import org.femosaa.core.SASAlgorithmAdaptor;
import org.femosaa.seed.NewSeeder;
import org.femosaa.seed.NewSeeder.SeedingStrategy;
import org.femosaa.util.Logger;
import org.soa.femosaa.AbstractService;
import org.soa.femosaa.ConcreteService;
import org.soa.femosaa.SOADelegate;
import org.soa.femosaa.Workflow;
import org.ssase.Service;
import org.ssase.objective.Objective;
import org.ssase.objective.QualityOfService;
import org.ssase.objective.optimization.femosaa.FEMOSAASolutionInstantiator;
import org.ssase.objective.optimization.femosaa.ibea.IBEAwithKAndDRegion;
import org.ssase.objective.optimization.femosaa.moead.MOEAD_STMwithKAndDRegion;
import org.ssase.objective.optimization.femosaa.nsgaii.NSGAIIwithKAndDRegion;
import org.ssase.objective.optimization.femosaa.nsgaii.NSGAIIwithZeroAndOneRegion;
import org.ssase.objective.optimization.gp.GPRegion;
import org.ssase.primitive.ControlPrimitive;
import org.ssase.primitive.Primitive;
import org.ssase.region.OptimizationType;
import org.ssase.region.Region;
import org.ssase.util.Repository;
import org.ssase.util.Ssascaling;

/**
 * For Seeding work
 * === NSGAII=====
 * AOSeed-10 800
 * AOSeed-30 600
 * AOSeed-50 600
 * AOSeed-70 700
 * AOSeed-90 600
 * 
 * SOSeed-10 900
 * SOSeed-30 700
 * SOSeed-50 600
 * SOSeed-70 500
 * SOSeed-90 500
 * 
 * HSeed-10 700
 * HSeed-30 500
 * HSeed-50 400
 * HSeed-70 400
 * HSeed-90 400
 * 
 * RSeed-10 700
 * RSeed-30 500
 * RSeed-50 500
 * RSeed-70 400
 * RSeed-90 400
 * 
 * 
 * 
 * 
 * Overhead (get the worst possible),
 * H-Seed
 * 5AS1 100=12,500=40,1000=134,2000=133,3000=219
 * 5AS2 100=15,500=40,1000=127,2000=118,3000=211
 * 5AS3 100=13,500=43,1000=124,2000=129,3000=234
 * 
 * 10AS1 100=13,500=176,1000=106,2000=177,3000=435
 * 10AS2 100=17,500=103,1000=100,2000=136,3000=236
 * 10AS3 100=17,500=196,1000=128,2000=108,3000=210
 * 
 * 15AS1 100=13,500=113,1000=70,2000=134,3000=263
 * 15AS2 100=12,500=80,1000=72,2000=129,3000=216
 * 15AS3 100=15,500=109,1000=75,2000=134,3000=334
 * 
 * AO-Seed
 * 
 * 5AS3 1000=197,5000=379,10000=497,20000=610,30000=1196
 * 10AS3 1000=209,5000=412,10000=472,20000=582,30000=780
 * 15AS3 1000=248,5000=466,10000=525,20000=701,30000=855
 * SO-Seed
 * 
 * 5AS3 1000=405,5000=1248,10000=1605,20000=1944,30000=2094
 * 10AS3 1000=444,5000=1257,10000=1491,20000=1668,30000=2004
 * 15AS3 1000=525,5000=1392,10000=1524,20000=1905,30000=2166
 * @author tao
 *
 */
public class WSSimulator {

	static List<Objective> o = new ArrayList<Objective>();
	public static String prefix = "/Users/tao/research/experiments-data/seed/prerun-10pdiff/";
	private static final String setting = "100AS" +
			"/";
	public static int MOEA = 1; //0= moead 1=nsgaii 2=ibea
	
	private static long global_time = 0;
	private static int count = 0;
	static List<ControlPrimitive> cp = null;
	
	static {
		
	}
	// 100AS 1000=385ms AO
	// 100AS 1000=327ms times 3 SO
	
	// 100AS 5000=1010ms AO
	// 100AS 5000=664ms times 3 SO
	
	// 100AS 10000=1784ms AO
	// 100AS 10000=1167ms times 3 SO
	
	// 100AS 20000=3346ms AO
	// 100AS 20000=2035ms times 3 SO
	
	// 100AS 30000=4912ms AO
	// 100AS 30000=2986 + 419 + 419 = 3824 SO
	static String[] remove_strings = new String[]{
		//"sas-rubis_software-Latency", 
		//"sas-rubis_software-Throughput",
		//"sas-rubis_software-Cost"
		};
	
	
	public static void main(String[] arg) {
		NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.AO_Seed);
		NewSeeder.getInstance().no_of_seed = 50; //10, 30, (50), 70, 90
		// double v = 3.0;
		// double d = 1/v;
		// System.out.print(d);
		main_test();
	}

	private static String format(BigDecimal x, int scale) {
		  NumberFormat formatter = new DecimalFormat("0.0E0");
		  formatter.setRoundingMode(RoundingMode.HALF_UP);
		  formatter.setMinimumFractionDigits(scale);
		  return formatter.format(x);
		}
	
	public static void main_test() {

		Ssascaling.activate();
		WSWorkflow workflow = new WSWorkflow();

		//EAConfigure.getInstance().setupWSConfiguration();
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

			// The order does not really matter, as there is a compact function
			public int compare(Object arg0, Object arg1) {
				ControlPrimitive cp1 = (ControlPrimitive) arg0;
				ControlPrimitive cp2 = (ControlPrimitive) arg1;
				int value1 = cp1.getName().length()>3? Integer.parseInt(cp1.getName().substring(2,4)) : Integer.parseInt(String.valueOf(cp1.getName().charAt(2)));
				int value2 = cp2.getName().length()>3? Integer.parseInt(cp2.getName().substring(2,4)) : Integer.parseInt(String.valueOf(cp2.getName().charAt(2)));
				//System.out.print(value1 + "-----------:------------" + value2 + "\n");
				
				if(cp1.getName().equals("AS100")) {
					value1 = 100;
				}
				if(cp2.getName().equals("AS100")) {
					value2 = 100;
				}
				
				return value1 < value2 ? -1 : 1;
			}

		});

		// Assume all objectives have the same order and inputs
		int small = Integer.MAX_VALUE;
		int big = Integer.MIN_VALUE;
		BigDecimal v = new BigDecimal(1);
		for (ControlPrimitive p : cp) {
			System.out.print("*****" + p.getName() + "\n");
			int i = p.getValueVector().length - 1;
			if (i < small) {
				small = i;
			}
			
			if (i > big) {
				big = i;
			}
			
			v = v.multiply(new BigDecimal(i));
		}
		
		
		//System.out.print("small " + small + "\n");
		//System.out.print("big " + big + "\n");
		
		//System.out.print("space " + format(v,2) + "\n");
		
		//if(1==1) return;
		
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

		Repository.initUniformWeight("W3D_105.dat", 105);
		int max_number_of_eval_to_have_only_seed = 0;
		long time = 0; 
		int n = 2;//30 
		for (int i = 0; i < n;/*1*/ i++) {
			long t = System.currentTimeMillis(); 
			//org.femosaa.core.SASSolution.putDependencyChainBack();

//			preRunAOOrSOSeed();
//			testNSGAII();
//			if(1==1) continue;//return;
			
			if(1==1) return;
			switch (NewSeeder.getInstance().getSeedingStrategy()) {
			case AO_Seed:
				AO_Seed();
				break;
			case SO_Seed:
				SO_Seed();
				break;
			case H_Seed:
				H_Seed();
				break;
			case R_Seed:
				R_Seed();
				break;
			case NONE: {
				SASAlgorithmAdaptor.isSeedSolution = false;
				NONE();
				break;
			}

			}
			
			time += System.currentTimeMillis() - t;
			//System.out.print("Number of seeds: " + NewSeeder.getInstance().getSeedsCount() + "\n");
//			testNSGAII();
			NewSeeder.getInstance().reset();
			// cs.change();

			max_number_of_eval_to_have_only_seed = Logger.max_number_of_eval_to_have_only_seed > 
			max_number_of_eval_to_have_only_seed? Logger.max_number_of_eval_to_have_only_seed : max_number_of_eval_to_have_only_seed;
			Logger.max_number_of_eval_to_have_only_seed = 0;
		}
		System.out.print("Max eval needed is: " + max_number_of_eval_to_have_only_seed + "\n");
		System.out.print("Excution time is " + (time)/n);
	}
	
	public static int main_run() {
		int max_number_of_eval_to_have_only_seed = 0;
		long time = 0; 
		int n = 30;//30 
		for (int i = 0; i < n;/*1*/ i++) {
			long t = System.currentTimeMillis(); 
			//org.femosaa.core.SASSolution.putDependencyChainBack();

//			preRunAOOrSOSeed();
//			testNSGAII();
//			if(1==1) continue;//return;
//			
			switch (NewSeeder.getInstance().getSeedingStrategy()) {
			case AO_Seed:
				AO_Seed();
				break;
			case SO_Seed:
				SO_Seed();
				break;
			case H_Seed:
				H_Seed();
				break;
			case R_Seed:
				R_Seed();
				break;
			case NONE: {
				SASAlgorithmAdaptor.isSeedSolution = false;
				NONE();
				break;
			}

			}
			
			time += System.currentTimeMillis() - t;
			//System.out.print("Number of seeds: " + NewSeeder.getInstance().getSeedsCount() + "\n");
//			testNSGAII();
			NewSeeder.getInstance().reset();
			// cs.change();

			max_number_of_eval_to_have_only_seed = Logger.max_number_of_eval_to_have_only_seed > 
			max_number_of_eval_to_have_only_seed? Logger.max_number_of_eval_to_have_only_seed : max_number_of_eval_to_have_only_seed;
			Logger.max_number_of_eval_to_have_only_seed = 0;
		}
		
		return max_number_of_eval_to_have_only_seed;
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

	private static void testNSGAII() {
		double[] r = null;
		Region.selected = OptimizationType.NSGAIIkd;

		System.out
				.print("=============== NSGAIIwithKAndDRegion ===============\n");
		NSGAIIwithKAndDRegion moead = new NSGAIIwithKAndDRegion();
		moead.addObjectives(o);
		long time = System.currentTimeMillis();
		moead.optimize();
		// r = getFitness(moead.optimize());
		org.ssase.util.Logger.logOptimizationTime(null,
				String.valueOf((System.currentTimeMillis() - time)));
		// logData("sas", "Throughput", String.valueOf(r[0]));
		// logData("sas", "Cost", String.valueOf(r[1]));

	}
	
	private static void testIBEA() {
		double[] r = null;
		Region.selected = OptimizationType.IBEAkd;

		System.out
				.print("=============== IBEAwithKAndDRegion ===============\n");
		IBEAwithKAndDRegion moead = new IBEAwithKAndDRegion();
		moead.addObjectives(o);
		long time = System.currentTimeMillis();
		moead.optimize();
		// r = getFitness(moead.optimize());
		org.ssase.util.Logger.logOptimizationTime(null,
				String.valueOf((System.currentTimeMillis() - time)));
		// logData("sas", "Throughput", String.valueOf(r[0]));
		// logData("sas", "Cost", String.valueOf(r[1]));

	}
	
	private static void NONE() {
		switch(MOEA) {
		case 0: 
			testMOEAD();
			break;
		case 1: 
			testNSGAII();
			break;
		case 2: 
			testIBEA();
			break;
		}
	}
	
	private static void AO_Seed() {
		SASAlgorithmAdaptor.isSeedSolution = true;
		readHistoryForSeeding("AO/AOSeed.rtf");
		NONE();
	}
	
	private static void SO_Seed() {
		SASAlgorithmAdaptor.isSeedSolution = true;
		readHistoryForSeeding("SO/SOSeed-L.rtf");
		readHistoryForSeeding("SO/SOSeed-T.rtf");
		readHistoryForSeeding("SO/SOSeed-C.rtf");
		NONE();
		/*
		List<Solution> list = readHistoryForSeeding("SO/SOSeed-L.rtf");
		int count = 0; 
		int size = NewSeeder.getInstance().no_of_seed/3;
		while(count < size){
			for (Solution s : list) {
				
				if(count >= size) {
					break;
				}
				NewSeeder.getInstance().addHistory(s);
				count++;
			}
		}
		
		list = readHistoryForSeeding("SO/SOSeed-T.rtf");
		count = 0; 
		while(count < size){
			for (Solution s : list) {
				
				if(count >= size) {
					break;
				}
				NewSeeder.getInstance().addHistory(s);
				count++;
			}
		}
		
		list = readHistoryForSeeding("SO/SOSeed-C.rtf");
		count = 0; 
		while(count < size){
			for (Solution s : list) {
				
				if(count >= size) {
					break;
				}
				NewSeeder.getInstance().addHistory(s);
				count++;
			}
		}
		NONE();*/
	}
	
	private static void H_Seed() {
		/*
		List<Solution> list = readHistoryForSeeding("HandR/SolutionSetValue.rtf");
		SolutionSet set = new SolutionSet();
		for (Solution s : list) {
			set.add(s);
		}
		
		Ranking ranking = new Ranking(set);
		
		int count = 0; 
		int rank = 0;
		while(count < NewSeeder.getInstance().no_of_seed){
			set = ranking.getSubfront(rank); 
			Iterator itr = set.iterator();
			while(itr.hasNext()) {
				
				if(count >= NewSeeder.getInstance().no_of_seed) {
					break;
				}
				NewSeeder.getInstance().addHistory((Solution)itr.next());
				count++;
			}
			
			rank++;
		}
		NONE();*/
		SASAlgorithmAdaptor.isSeedSolution = true;
		readHistoryForSeeding("HandR/SolutionSetValue.rtf");
		//Collection
		NONE();
	}
	
	private static void R_Seed() {
		SASAlgorithmAdaptor.isSeedSolution = true;
		readHistoryForSeeding("HandR/SolutionSetValue.rtf");
		NONE();
	}
	
	
	private static void preRunAOOrSOSeed() {
		long t = System.currentTimeMillis();
		SASAlgorithmAdaptor.isSeedSolution = false;
		double[] r = null;
		Region.selected = OptimizationType.GP;

		System.out
				.print("=============== GPRegion ===============\n");
		GPRegion moead = new GPRegion();
		moead.addObjectives(o);
		long time = System.currentTimeMillis();
		//moead.optimize();
	
		//System.out.print("Excution time is " + (t*3));
		LinkedHashMap<ControlPrimitive, Double> list = moead.optimize();
	
		if(count != 0)
		global_time += System.currentTimeMillis() - time;
		count++;
		System.out.print("Average pre-opitmization overhead " + (global_time/(count == 1? count : count -1)) + " ms\n");
		System.out.print("pre-opitmization overhead " + (System.currentTimeMillis() - time) + " ms\n");
		logSolutionSetValues(list, "AOSeed.rtf");
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
	
	private static void readHistoryForSeeding(String file){
		
		String path = prefix + setting + file;
		//List<Solution> list = new ArrayList<Solution>();
		FEMOSAASolutionInstantiator init = new FEMOSAASolutionInstantiator(o);
		
		
		int[][] vars = new int[cp.size()][2];
		for (int i = 0; i < cp.size(); i ++) {	
			vars[i][0] = 0;
			vars[i][1] = cp.get(i).getValueVector().length-1;
		}
		Problem problem = null;
		try {
			problem = new SAS("SASSolutionType", init, vars, o.size(), 0);
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String line = null;
			while ((line = reader.readLine()) != null) {
				
				if("".equals(line) || line.startsWith("--")) {
					continue;
				}
				
				String[] data = line.split(",");
		
				
				Variable[] variables = new Variable[cp.size()];
				for (int i = 0; i < cp.size(); i ++) {
					variables[i] = new Int(0, cp.get(i).getValueVector().length-1);	
				}
				Solution solution = init.getSolution(problem, variables);
				//System.out.print(solution.getDecisionVariables().length + "***\n");
				for (int i = 0; i < data.length; i++) {
					double d = Double.parseDouble(data[i]) >= cp.get(i).getValueVector().length? cp.get(i).getValueVector().length-1 :
						Double.parseDouble(data[i]);
					solution.getDecisionVariables()[i]
					                                .setValue(d);
				}
				// Can be commented
				if(org.femosaa.core.SASAlgorithmAdaptor.isLogTheEvalNeededToRemiveNonSeed) {
				    ((org.femosaa.core.SASSolution)solution).isFromInValid = true;
				}
				
				NewSeeder.getInstance().addHistory(solution);
				//list.add(solution);
			}
		
			
			/*for (int i = 0; i < 0; i ++) {
				if(i >= list.size()) {
					NewSeeder.getInstance().addHistory(list.get(i%100));
				} else {
					NewSeeder.getInstance().addHistory(list.get(i));
				}
				
			}*/
			
			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}