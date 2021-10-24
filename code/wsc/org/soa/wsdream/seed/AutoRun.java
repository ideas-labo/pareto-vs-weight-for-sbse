package org.soa.wsdream.seed;

import java.util.HashMap;
import java.util.Map;

import org.femosaa.core.EAConfigure;
import org.femosaa.core.SASAlgorithmAdaptor;
import org.femosaa.seed.NewSeeder;
import org.femosaa.seed.NewSeeder.SeedingStrategy;
import org.femosaa.util.Logger;

/**
 * 
 * @author tao
 *
 *
 * IBEA max number of eval to have only seeds
 * 50
max eval ao_seed: 1100
max eval so_seed: 1200
max eval h_seed: 700
max eval r_seed: 800


SOSeed-70: 800
HSeed-90: 400
SOSeed-90: 500
AOSeed-30: 1000
RSeed-90: 600
HSeed-10: 1200
AOSeed-90: 700
HSeed-30: 700
AOSeed-70: 900
RSeed-70: 600
SOSeed-30: 1000
HSeed-70: 600
AOSeed-10: 900
RSeed-10: 1000
SOSeed-10: 1200
RSeed-30: 900


100 AS

max eval ao_seed: 900
max eval so_seed: 1000
max eval h_seed: 500
max eval r_seed: 500

SOSeed-70: 400
HSeed-90: 400
SOSeed-90: 300
AOSeed-30: 800
RSeed-90: 500
HSeed-10: 700
AOSeed-90: 600
HSeed-30: 600
AOSeed-70: 700
RSeed-70: 500
SOSeed-30: 500
HSeed-70: 500
AOSeed-10: 900
RSeed-10: 700
SOSeed-10: 700
RSeed-30: 600


 * 
 * 
 * MOEAD max number of eval to have only seeds
 * 50
 * max eval ao_seed: 4780 0
max eval so_seed: 4860 0
max eval h_seed: 4860 0
max eval r_seed: 4760

SOSeed-70: 4080
HSeed-90: 2960
SOSeed-90: 3860
AOSeed-30: 4440 0
RSeed-90: 4780
HSeed-10: 0
AOSeed-90: 4400
HSeed-30: 4300 0
AOSeed-70: 4560
RSeed-70: 4480
SOSeed-30: 0
HSeed-70: 3400
AOSeed-10: 0
RSeed-10: 0
SOSeed-10: 0
RSeed-30: 0



100AS


max eval ao_seed: 9780
max eval so_seed: 25700
max eval h_seed: 7320
max eval r_seed: 18720/7440

SOSeed-70: 2380
HSeed-90: 1980
SOSeed-90: 5980
AOSeed-30: 3320
RSeed-90: 480
HSeed-10: 22160
AOSeed-90: 12880
HSeed-30: 19900
AOSeed-70: 9740
RSeed-70: 16400
SOSeed-30: 12260
HSeed-70: 1360
AOSeed-10: 21040
RSeed-10: 22420
SOSeed-10: 15900
RSeed-30: 8760

NSGAII

R-Seed:600

100AS

max eval ao_seed: 1000
max eval so_seed: 900
max eval h_seed: 300
max eval r_seed: 300

SOSeed-70: 400
HSeed-90: 200
SOSeed-90: 300
AOSeed-30: 600
RSeed-90: 200
HSeed-10: 400
AOSeed-90: 600
HSeed-30: 300
AOSeed-70: 500
RSeed-70: 200
SOSeed-30: 400
HSeed-70: 200
AOSeed-10: 600
RSeed-10: 400
SOSeed-10: 500
RSeed-30: 300
 */
public class AutoRun {

	
	public static void main(String[] arg) {
		
	    //run_50("nsgaii");
		//run_others("nsgaii");
		WSSimulator.main_test();
		run_crossover_rate("ibea");
		run_crossover_rate("moead");
	}
	
	
	private static void run_others(String alg){
		WSSimulator.main_test();
		Map<String, Integer> map = new HashMap<String, Integer>();
		
		
		if("ibea".equals(alg)) {
			WSSimulator.MOEA = 2;
		} else if ("moead".equals(alg)) {
			WSSimulator.MOEA = 0;
		} else {
			WSSimulator.MOEA = 1;
		}
		
		int [] no_seed = new int[] {10,30,70,90};
		for (int n : no_seed) {
			/*NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.AO_Seed);
			NewSeeder.getInstance().no_of_seed = n;
			EAConfigure.getInstance().setupWSConfiguration();
			Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/"+n+"/AOSeed/";
			map.put("AOSeed-"+n,WSSimulator.main_run());
			
			NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.SO_Seed);
			NewSeeder.getInstance().no_of_seed = n;
			EAConfigure.getInstance().setupWSConfiguration();
			Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/"+n+"/SOSeed/";
			map.put("SOSeed-"+n,WSSimulator.main_run());
			
			NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.H_Seed);
			NewSeeder.getInstance().no_of_seed = n;
			EAConfigure.getInstance().setupWSConfiguration();
			Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/"+n+"/HSeed/";
			map.put("HSeed-"+n,WSSimulator.main_run());*/
			
			NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.R_Seed);
			NewSeeder.getInstance().no_of_seed = n;
			EAConfigure.getInstance().setupWSConfiguration();
			Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/"+n+"/RSeed/";
			map.put("RSeed-"+n,WSSimulator.main_run());
		}
		
	
		for (Map.Entry<String, Integer> s : map.entrySet()) {
			System.out.print(s.getKey() + ": " + s.getValue() + "\n");
		}
	}
	
	private static void run_crossover_rate(String alg){
		//WSSimulator.main_test();
		int ao_seed,so_seed,h_seed,r_seed;
		
		if("ibea".equals(alg)) {
			WSSimulator.MOEA = 2;
		} else if ("moead".equals(alg)) {
			WSSimulator.MOEA = 0;
		} else {
			WSSimulator.MOEA = 1;
		}
		
		//double [] rate = new double[] {0.3,0.6};
		double [] rate = new double[] {0.2,0.5}; //for 100AS
		String s = "";
		for (double n : rate) {
		s = String.valueOf(n);
		if(n==0.2) {
			s = "0.3";
		} else if(n==0.5) {
			s = "0.6";
		}
			
		/*NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.AO_Seed);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfiguration(n);
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/AOSeed"+s+"/";
		ao_seed = WSSimulator.main_run();
		
		NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.SO_Seed);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfiguration(n);
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/SOSeed"+s+"/";
		so_seed = WSSimulator.main_run();
		
		NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.H_Seed);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfiguration(n);
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/HSeed"+s+"/";
		h_seed = WSSimulator.main_run();
		
		NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.R_Seed);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfiguration(n);
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/RSeed"+s+"/";
		r_seed = WSSimulator.main_run();
		
		NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.NONE);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfiguration(n);
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/NONE+h"+s+"/";
		WSSimulator.main_run();
		
		NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.NONE);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfiguration(n);
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/NONE+r"+s+"/";
		WSSimulator.main_run();
		
		NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.NONE);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfiguration(n);
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/NONE+ao"+s+"/";
		SASAlgorithmAdaptor.seed_time = 412;
		WSSimulator.main_run();
		SASAlgorithmAdaptor.seed_time = -1;

		
		NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.NONE);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfiguration(n);
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/NONE+so"+s+"/";
		SASAlgorithmAdaptor.seed_time = 1257;
		WSSimulator.main_run();
		SASAlgorithmAdaptor.seed_time = -1;*/
		
		NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.NONE);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfiguration(n);
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/NONE+ao"+s+"/";
		SASAlgorithmAdaptor.seed_time = 4912;
		WSSimulator.main_run();
		SASAlgorithmAdaptor.seed_time = -1;

		
		NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.NONE);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfiguration(n);
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/NONE+so"+s+"/";
		SASAlgorithmAdaptor.seed_time = 3824;
		WSSimulator.main_run();
		SASAlgorithmAdaptor.seed_time = -1;
		
	
		
		}
		
		NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.NONE);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfigurationOnlyMutation();
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/NONE+ao-m"+s+"/";
		SASAlgorithmAdaptor.seed_time = 4912;
		WSSimulator.main_run();
		SASAlgorithmAdaptor.seed_time = -1;
		
		
		NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.NONE);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfigurationOnlyMutation();
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/NONE+so-m"+s+"/";
		SASAlgorithmAdaptor.seed_time = 3824;
		WSSimulator.main_run();
		SASAlgorithmAdaptor.seed_time = -1;
		
		
		
	}
	
	private static void run_50(String alg){
		
		WSSimulator.main_test();
		int ao_seed,so_seed,h_seed,r_seed;
		
		if("ibea".equals(alg)) {
			WSSimulator.MOEA = 2;
		} else if ("moead".equals(alg)) {
			WSSimulator.MOEA = 0;
		} else {
			WSSimulator.MOEA = 1;
		}
		
		/*NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.AO_Seed);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfiguration();
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/AOSeed/";
		ao_seed = WSSimulator.main_run();
		
		NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.SO_Seed);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfiguration();
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/SOSeed/";
		so_seed = WSSimulator.main_run();
		
		NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.H_Seed);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfiguration();
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/HSeed/";
		h_seed = WSSimulator.main_run();*/
		/*
		NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.R_Seed);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfiguration();
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/RSeed/";
		r_seed = WSSimulator.main_run();*/
		
		/*NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.NONE);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfiguration();
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/NONE+h/";
		WSSimulator.main_run();
		
		NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.NONE);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfiguration();
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/NONE+r/";
		WSSimulator.main_run();
		
		NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.NONE);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfiguration();
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/NONE+ao/";
		SASAlgorithmAdaptor.seed_time = 412;
		WSSimulator.main_run();
		SASAlgorithmAdaptor.seed_time = -1;

		
		NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.NONE);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfiguration();
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/NONE+so/";
		SASAlgorithmAdaptor.seed_time = 1257;
		WSSimulator.main_run();
		SASAlgorithmAdaptor.seed_time = -1;*/
		
		
		/*********only mutation****/
		
		/*NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.AO_Seed);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfigurationOnlyMutation();
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/AOSeed-m/";
		ao_seed = WSSimulator.main_run();
		
		NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.SO_Seed);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfigurationOnlyMutation();
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/SOSeed-m/";
		so_seed = WSSimulator.main_run();
		
		NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.H_Seed);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfigurationOnlyMutation();
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/HSeed-m/";
		h_seed = WSSimulator.main_run();*/
		
	
		/*NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.R_Seed);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfigurationOnlyMutation();
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/RSeed-m/";
		r_seed = WSSimulator.main_run();*/
		
		/*NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.NONE);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfigurationOnlyMutation();
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/NONE/";
		WSSimulator.main_run();
		
		NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.NONE);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfigurationOnlyMutation();
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/NONE+ao-m/";
		SASAlgorithmAdaptor.seed_time = 412;
		WSSimulator.main_run();
		SASAlgorithmAdaptor.seed_time = -1;

		
		NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.NONE);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfigurationOnlyMutation();
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/NONE+so-m/";
		SASAlgorithmAdaptor.seed_time = 1257;
		WSSimulator.main_run();
		SASAlgorithmAdaptor.seed_time = -1;*/
		
		/***********different delta**********/
		
		/*NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.H_Seed);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfiguration();
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/HSeed-30/";
		WSSimulator.prefix = "/Users/tao/research/experiments-data/seed/prerun-30pdiff/";
		WSSimulator.main_run();
		
		NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.H_Seed);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfiguration();
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/HSeed-50/";
		WSSimulator.prefix = "/Users/tao/research/experiments-data/seed/prerun-50pdiff/";
		WSSimulator.main_run();
		
		NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.H_Seed);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfiguration();
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/HSeed-70/";
		WSSimulator.prefix = "/Users/tao/research/experiments-data/seed/prerun-70pdiff/";
		WSSimulator.main_run();
		
		NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.H_Seed);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfiguration();
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/HSeed-90/";
		WSSimulator.prefix = "/Users/tao/research/experiments-data/seed/prerun-90pdiff/";
		WSSimulator.main_run();*/
		
		/*NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.R_Seed);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfiguration();
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/RSeed-30/";
		WSSimulator.prefix = "/Users/tao/research/experiments-data/seed/prerun-30pdiff/";
		WSSimulator.main_run();
		
		NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.R_Seed);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfiguration();
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/RSeed-50/";
		WSSimulator.prefix = "/Users/tao/research/experiments-data/seed/prerun-50pdiff/";
		WSSimulator.main_run();
		
		NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.R_Seed);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfiguration();
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/RSeed-70/";
		WSSimulator.prefix = "/Users/tao/research/experiments-data/seed/prerun-70pdiff/";
		WSSimulator.main_run();
		
		NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.R_Seed);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfiguration();
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/RSeed-90/";
		WSSimulator.prefix = "/Users/tao/research/experiments-data/seed/prerun-90pdiff/";
		WSSimulator.main_run();*/
		
	
		
		NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.NONE);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfiguration();
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/NONE+ao/";
		SASAlgorithmAdaptor.seed_time = 4912;
		WSSimulator.main_run();
		SASAlgorithmAdaptor.seed_time = -1;
		
		
		/*NewSeeder.getInstance().setSeedingStrategy(SeedingStrategy.NONE);
		NewSeeder.getInstance().no_of_seed = 50;
		EAConfigure.getInstance().setupWSConfiguration();
		Logger.prefix =  "/Users/tao/research/monitor/ws-soa/sas/"+alg+"/50/NONE+so/";
		SASAlgorithmAdaptor.seed_time = 3824;
		WSSimulator.main_run();
		SASAlgorithmAdaptor.seed_time = -1;*/

	
		
		/*System.out.print("max eval ao_seed: "  + ao_seed + 
				"\nmax eval so_seed: "  + so_seed + 
				"\nmax eval h_seed: "  + h_seed 
				"\nmax eval r_seed: "  + r_seed);+*/ 
	}
	
}
