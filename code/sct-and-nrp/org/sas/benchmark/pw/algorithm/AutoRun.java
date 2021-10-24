package org.sas.benchmark.pw.algorithm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.femosaa.core.EAConfigure;
import org.femosaa.core.SASAlgorithmAdaptor;
import org.sas.benchmark.pw.Data.Pack;
import org.sas.benchmark.pw.nrp.Parser;
import org.ssase.requirement.froas.RequirementPrimitive;
import org.ssase.requirement.froas.RequirementProposition;
import org.ssase.util.Repository;

public class AutoRun {
	/*
	 * new double[] { 500, 1000, 1500, 2000, 2500 }; new double[] { 0.1, 0.8, 5,
	 * 15, 40}; new double[] { 2000, 3300, 5000, 10000, 17000}; new double[] {
	 * 180, 220, 230, 250, 280}; new double[] { 11, 13, 14.5, 15.5, 18}; new
	 * double[] { 230, 400, 600, 800, 1000};
	 * 
	 * 
	 * Generating new solutions 300ms
	 * non dominated sorting 63ms
	 * crowding distance 44ms
	 * 
	 * nrp-e-3, nrp-m-3, nrp-g-1
	 * 
	 */
	// private static double[] ds = new double[] { 2000, 3300, 5000, 10000,
	// 17000};
	private static String[] weights = new String[] { "0.1-0.9", "0.2-0.8",
			"0.3-0.7", "0.4-0.6", "0.5-0.5", "0.6-0.4", "0.7-0.3", "0.8-0.2",
			"0.9-0.1" };
	//private static String[] weights = new String[] {"0.1-0.8-0.1", "0.8-0.1-0.1",  "0.1-0.1-0.8", "0.33-0.33-0.33" };
	
	private static String[] single_algs = new String[] {"sa", "ga", "hc", "rs" };
	private static String[] multi_algs = new String[] { "nsgaii"/*,"moead""nsgaii"*/ };
	public static String benchmark = "nrp-m3";
	static boolean run_moea = true;
	private static int[] rand = new int[]{143,123,47,139,46,91,102,138,117,78,56,140};
	
	private static String[] configs = new String[] { "1", "2", "3", "4", "5", "6", "7" };
	public static String config = "";//"" by default
	
	private static String fp = "sas";
	
	static String budget = "";
	
	static String pre = "";
	private static String[] b = new String[] { "nrp-e1", "nrp-e2", "nrp-e3", "nrp-e4",
		"nrp-g1", "nrp-g2", "nrp-g3", "nrp-g4",
		"nrp-m1", "nrp-m2", "nrp-m3", "nrp-m4"};
	
	
	private static Map<String, Double[][]> map = new HashMap<String, Double[][]> ();
	private static Map<String, Integer> time_budget = new HashMap<String, Integer> ();	
	/**
	 * Need to change depending on whether evaluation or time is used as the budget.
	 */
	static {
		// all g4 may need to rerun for time, mark at 3 Jan, 21:00
		map.put("nrp-e1", new Double[][] {{ 0.0016099451404118672,0.90476190476},{1.0,552.0}});
		map.put("nrp-e2", new Double[][] {{ 0.0021711727232110586,0.96551724137 },{1.0,484.0}});		
		map.put("nrp-e3", new Double[][] {{ 0.005015379606728326,0.5909090909 },{1.0,182.0}});
		map.put("nrp-e4", new Double[][] {{ 0.0021796610293579946,0.95 },{1.0,504.0}});
		map.put("nrp-g1", new Double[][] {{ 0.004831921507204577,0.66666666666 },{3.0,236.0}});		
		map.put("nrp-g2", new Double[][] {{ 0.003075696704024126,1.44444444444 },{1.0,466.0}});				
		map.put("nrp-g3", new Double[][] {{ 0.001989890839887389,0.63636363636 },{1.0,477.0}});		
		map.put("nrp-g4", new Double[][] {{ 0.002030103988533906,0.72},{1.0,621.0}});			
		map.put("nrp-m1", new Double[][] {{ 0.0016745151814322342,1.05555555556 },{1.0,475.0}});	
		map.put("nrp-m2", new Double[][] {{ 0.0035200773869786387,1.38095238095 },{1.0,314.0}});		
		map.put("nrp-m3", new Double[][] {{ 0.0029908099074387657,0.58333333333 },{1.0,229.0}});			
		map.put("nrp-m4", new Double[][] {{ 0.0015896308562909735,0.85},{1.0,551.0}});
		
		map.put("nrp-e-3", new Double[][] {{ 0.005015379606728326,0.5909090909 },{1.0,182.0},{1.3877787807814457E-17,0.2154827675923337}});
		map.put("nrp-g-3", new Double[][] {{ 0.004831921507204577,0.66666666666 },{3.0,236.0},{1.3877787807814457E-17,0.47058823529411764}});
		map.put("nrp-m-3", new Double[][] {{ 0.0029908099074387657,0.58333333333 },{1.0,229.0},{2.7755575615628914E-17,0.15877132402714708}});
		/*map.put("nrp-e1", new Double[][] {{ -621.1391772915772,-1.1052631578970638},{1.0,552.0}});
		map.put("nrp-e2", new Double[][] {{ -460.58058362167003,-1.035714285724273 },{1.0,484.0}});		
		map.put("nrp-e3", new Double[][] {{ -199.38670218670214,-1.692307692333728 },{1.0,182.0}});
		map.put("nrp-e4", new Double[][] {{ -458.78693362451116,-1.0526315789473684 },{1.0,504.0}});
		map.put("nrp-g1", new Double[][] {{ -206.95700427023954,-1.500000000015 },{3.0,236.0}});		
		map.put("nrp-g2", new Double[][] {{ -325.1295872872112,-0.6923076923098225 },{1.0,466.0}});				
		map.put("nrp-g3", new Double[][] {{ -502.54012931512943,-1.5714285714375509 },{1.0,477.0}});		
		map.put("nrp-g4", new Double[][] {{ -492.58560430797286,-1.3888888888888888},{1.0,621.0}});			
		map.put("nrp-m1", new Double[][] {{ -597.1877777451306,-0.9473684210486426},{1.0,475.0}});	
		map.put("nrp-m2", new Double[][] {{ -284.08466350744703,-0.7241379310357313},{1.0,314.0}});		
		map.put("nrp-m3", new Double[][] {{ -334.3575924075924,-1.7142857142955101 },{1.0,229.0}});			
		map.put("nrp-m4", new Double[][] {{ -629.0768677787639,-1.1764705882352942},{1.0,551.0}});*/
		
		
		/*System.out.print((-1.0/map.get("nrp-e1")[0][0])+","+(-1.0/map.get("nrp-e1")[0][1]) + "\n");
		System.out.print((-1.0/map.get("nrp-e2")[0][0])+","+(-1.0/map.get("nrp-e2")[0][1]) + "\n");
		System.out.print((-1.0/map.get("nrp-e3")[0][0])+","+(-1.0/map.get("nrp-e3")[0][1]) + "\n");
		System.out.print((-1.0/map.get("nrp-e4")[0][0])+","+(-1.0/map.get("nrp-e4")[0][1]) + "\n");
		System.out.print((-1.0/map.get("nrp-g1")[0][0])+","+(-1.0/map.get("nrp-g1")[0][1]) + "\n");
		System.out.print((-1.0/map.get("nrp-g2")[0][0])+","+(-1.0/map.get("nrp-g2")[0][1]) + "\n");
		System.out.print((-1.0/map.get("nrp-g3")[0][0])+","+(-1.0/map.get("nrp-g3")[0][1]) + "\n");
		System.out.print((-1.0/map.get("nrp-g4")[0][0])+","+(-1.0/map.get("nrp-g4")[0][1]) + "\n");
		System.out.print((-1.0/map.get("nrp-m1")[0][0])+","+(-1.0/map.get("nrp-m1")[0][1]) + "\n");
		System.out.print((-1.0/map.get("nrp-m2")[0][0])+","+(-1.0/map.get("nrp-m2")[0][1]) + "\n");
		System.out.print((-1.0/map.get("nrp-m3")[0][0])+","+(-1.0/map.get("nrp-m3")[0][1]) + "\n");
		System.out.print((-1.0/map.get("nrp-m4")[0][0])+","+(-1.0/map.get("nrp-m4")[0][1]) + "\n");*/
		// eval
		
		/*map.put("nrp-e1", new Double[][] {{ 0.0016215998664796294,0.025070635520713116},{7.0,535.0}});
		map.put("nrp-e2", new Double[][] {{ 0.002206986602146856,0.031128789188922104 },{6.0,449.0}});
		map.put("nrp-e3", new Double[][] {{ 0.005015379606728326,0.07320506804317223 },{1.0,182.0}});
		map.put("nrp-e4", new Double[][] {{ 0.0021796610293579946,0.03627384579317355 },{8.0,504.0}});
		map.put("nrp-g1", new Double[][] {{ 0.004831921507204577,0.25 },{3.0,236.0}});
		map.put("nrp-g2", new Double[][] {{ 0.0031370849960010807,0.3333333333333333 },{1.0,423.0}});
		map.put("nrp-g3", new Double[][] {{ 0.001996132689558199,0.03741314804917157 },{5.0,471.0}});
		map.put("nrp-g4", new Double[][] {{ 0.002030103988533906,0.015761624197845915},{16.0,621.0}});
		map.put("nrp-m1", new Double[][] {{ 0.0016745151814322342,0.04198740377886634 },{2.0,416.0}});
		map.put("nrp-m2", new Double[][] {{ 0.003557411364418617,0.24705221785513756 },{1.0,299.0}});
		map.put("nrp-m3", new Double[][] {{ 0.0029908099074387657,0.052143684820393964 },{1.0,229.0}});
		map.put("nrp-m4", new Double[][] {{ 0.0016073672339561344,0.014429568248672094},{12.0,524.0}});
		
		// time
		
		
		map.put("nrp-e1", new Double[][] {{ 0.0016298903966350863,0.025070635520713116},{7.0,524.0}});
		map.put("nrp-e2", new Double[][] {{ 0.0021968960133047174,0.031128789188922104 },{6.0,457.0}});
		map.put("nrp-e3", new Double[][] {{ 0.005015379606728326,0.07320506804317223 },{1.0,182.0}});
		map.put("nrp-e4", new Double[][] {{ 0.0021851042139296385,0.03627384579317355 },{8.0,499.0}});
		map.put("nrp-g1", new Double[][] {{ 0.004831921507204577,0.25 },{3.0,236.0}});
		map.put("nrp-g2", new Double[][] {{ 0.0031370849960010807,0.3333333333333333 },{1.0,423.0}});
		map.put("nrp-g3", new Double[][] {{ 0.001996132689558199,0.03741314804917157 },{5.0,471.0}});
		map.put("nrp-g4", new Double[][] {{ 0.002030103988533906,0.015761624197845915},{16.0,621.0}});
		map.put("nrp-m1", new Double[][] {{ 0.0016745151814322342,0.04198740377886634 },{2.0,404.0}});
		map.put("nrp-m2", new Double[][] {{ 0.003557411364418617,0.24705221785513756 },{1.0,299.0}});
		map.put("nrp-m3", new Double[][] {{ 0.0029908099074387657,0.052143684820393964 },{1.0,229.0}});
		map.put("nrp-m4", new Double[][] {{ 0.0016073672339561344,0.014429568248672094},{12.0,529.0}});*/
		
		
		//map.put("nrp-e-3", new Double[][] {{ 0.005015379606728326,0.07320506804317223 },{1.0,182.0},{1.3877787807814457E-17,0.2154827675923337}});

		//map.put("nrp-m-3", new Double[][] {{ 0.0029908099074387657,0.052143684820393964 },{1.0,229.0},{2.7755575615628914E-17,0.16666666666666669}});
		//map.put("nrp-g-3", new Double[][] {{ 0.004831921507204577,0.25 },{3.0,236.0},{1.3877787807814457E-17,0.47058823529411764}});
		
		// ga*1, hc*2, sa*2, rs*4 -> time
		
		time_budget.put("nrp-e1", 500);
		time_budget.put("nrp-e2", 600);
		time_budget.put("nrp-e3", 800);
		time_budget.put("nrp-e4", 900);
		time_budget.put("nrp-g1", 700);
		time_budget.put("nrp-g2", 900);
		time_budget.put("nrp-g3", 900);
		time_budget.put("nrp-g4", 800);
		time_budget.put("nrp-m1", 800);
		time_budget.put("nrp-m2", 800);
		time_budget.put("nrp-m3", 800);
		time_budget.put("nrp-m4", 800);
	}
	
	public static void main(String[] args) {
		
		budget = "eval";//"eval" "time"
		
		for (int i = 0; i < b.length; i++) {
			if(!b[i].equals("nrp-m2")) {
				//continue;
			}
			Parser.selected = b[i];
			Parser.t_size = rand[i];
			main();
			//findExtreme();
		}
		/*int k = 0;
		Parser.selected = b[k];
		Parser.t_size = rand[k];
		main();*/
		//findExtreme();
	}

	public static void main() {
		//Parser.selected = benchmark;
		Simulator.setup();	Simulator.setup();
		SASAlgorithmAdaptor.logGenerationOfObjectiveValue = 1000;
		if(budget.equals("time")) {
			SASAlgorithmAdaptor.logGenerationOfObjectiveValue = 5000;
		}
		SASAlgorithmAdaptor.logGenerationOfObjectiveValue = -1;
		File f = new File("/Users/"+System.getProperty("user.name")+"/research/monitor/"+pre+"ws-soa/"+fp);

		try {
			if (f.exists()) {
				delete(f);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(budget.equals("eval")) {
			if(run_moea) {
		    run_MOEA();
		    if(1==1) return;
			}
		}
		

		for (String p : weights) {

			String[] s = p.split("-");
			double[] w = new double[s.length];
			for (int i = 0; i < s.length; i++) {
				w[i] = Double.parseDouble(s[i]);
			}

			for (String alg : single_algs) {
				
				if(budget.equals("time")) {
					if(alg.equals("ga")) {
						EAConfigure.getInstance().setupNRPConfigurationOnRun(time_budget.get(Parser.selected));
					} else if(alg.equals("hc")) {
						EAConfigure.getInstance().setupNRPConfigurationOnRun(time_budget.get(Parser.selected) * 2);
					} else if(alg.equals("sa")) {
						EAConfigure.getInstance().setupNRPConfigurationOnRun(time_budget.get(Parser.selected) * 2);
					} else if(alg.equals("rs")) {
						EAConfigure.getInstance().setupNRPConfigurationOnRun(time_budget.get(Parser.selected) * 4);
					}
				}

				for (int k = 0; k<4;k++) {
					
					// We have already run the ga with dynamic update one
					/*if(k == 1 || k == 0) {
						continue;
					}*/
					
					if(k == 0 || k == 1) {
						//continue;
					}


					//String normalization = k == 0? "" : "fixed-bounds";
					//Simulator.fixed_bounds = k == 0? null : convert(map.get(Parser.selected));
					
					String normalization = "";
					Simulator.fixed_bounds = null;
					
					if(k == 0) {
						normalization = "";
						Simulator.fixed_bounds = null;
					} else if(k == 1) {
						normalization = "fixed-bounds";
						if(benchmark.equals("nrp-e-3") || benchmark.equals("nrp-g-3") || benchmark.equals("nrp-m-3")) {
							Simulator.fixed_bounds = convert(map.get(benchmark));
						} else {
							Simulator.fixed_bounds = convert(map.get(Parser.selected));
						}
							
						
					} else if(k == 2) {
						normalization = "ratio";
						if(benchmark.equals("nrp-e-3") || benchmark.equals("nrp-g-3") || benchmark.equals("nrp-m-3")) {
							Simulator.fixed_bounds = new double[][]{{0,0},{0,0},{0,0}};
						} else {
							Simulator.fixed_bounds = new double[][]{{0,0},{0,0}};
						}
					} else if(k == 3) {
						normalization = "none";
						if(benchmark.equals("nrp-e-3") || benchmark.equals("nrp-g-3") || benchmark.equals("nrp-m-3")) {
							Simulator.fixed_bounds = new double[][]{{-1,-1},{-1,-1},{-1,-1}};
						} else {
							Simulator.fixed_bounds = new double[][]{{-1,-1},{-1,-1}};
						}
					}
					
					
					Simulator.alg = alg;
					Simulator.weights = w;

					Simulator.main_test();

					File source = new File("/Users/"+System.getProperty("user.name")+"/research/monitor/"+pre+"ws-soa/" + fp);
					File r = new File("/Users/"+System.getProperty("user.name")+"/research/experiments-data/w-vs-p/"+budget+"-nrp/" + p + "/" + Parser.selected
							+ "/" + alg + "/" + normalization + "/sas");
					File dest = new File("/Users/"+System.getProperty("user.name")+"/research/experiments-data/w-vs-p/"+budget+"-nrp/" + p + "/" + Parser.selected
							+ "/" + alg + "/" + normalization + "/sas");

					if (r.exists()) {
						System.out.print("Remove " + r + "\n");
						try {
							delete(r);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					if (!dest.exists()) {
						dest.mkdirs();
					}

					try {
						copyFolder(source, dest);
						if (source.exists()) {
							System.out.print("Remove " + source + "\n");
							delete(source);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					System.out.print("End of " + "/Users/"+System.getProperty("user.name")+"/research/experiments-data/w-vs-p/"+budget+"-nrp/" + p + "/"
							+ Parser.selected + "/" + alg + "/" + normalization + "\n");
					// try {
					// Thread.sleep((long)2000);
					// } catch (InterruptedException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }

				}

			}
		}

	}
	
	public static void findExtreme() {
		
		Simulator.setup();
		
		weights = new String[] {"0.0-0.0-1.0"};
		single_algs = new String[] {"ga"};
		for (String p : weights) {

			String[] s = p.split("-");
			double[] w = new double[s.length];
			for (int i = 0; i < s.length; i++) {
				w[i] = Double.parseDouble(s[i]);
			}

			for (String alg : single_algs) {
				
				if(budget.equals("time")) {
					if(alg.equals("ga")) {
						EAConfigure.getInstance().setupNRPConfigurationOnRun(time_budget.get(Parser.selected));
					} else if(alg.equals("hc")) {
						EAConfigure.getInstance().setupNRPConfigurationOnRun(time_budget.get(Parser.selected) * 2);
					} else if(alg.equals("sa")) {
						EAConfigure.getInstance().setupNRPConfigurationOnRun(time_budget.get(Parser.selected) * 2);
					} else if(alg.equals("rs")) {
						EAConfigure.getInstance().setupNRPConfigurationOnRun(time_budget.get(Parser.selected) * 4);
					}
				}

				for (int k = 3; k<4;k++) {
					
					// We have already run the ga with dynamic update one
					if(k != 3) {
						continue;
					}


					//String normalization = k == 0? "" : "fixed-bounds";
					//Simulator.fixed_bounds = k == 0? null : convert(map.get(Parser.selected));
					
					String normalization = "";
					Simulator.fixed_bounds = null;
					
					if(k == 0) {
						normalization = "";
						Simulator.fixed_bounds = null;
					} else if(k == 1) {
						normalization = "fixed-bounds";
						Simulator.fixed_bounds = convert(map.get(Parser.selected));
					} else if(k == 2) {
						normalization = "ratio";
						if(benchmark.equals("nrp-e-3") || benchmark.equals("nrp-g-3") || benchmark.equals("nrp-m-3")) {
							Simulator.fixed_bounds = new double[][]{{0,0},{0,0},{0,0}};
						} else {
							Simulator.fixed_bounds = new double[][]{{0,0},{0,0}};
						}
					} else if(k == 3) {
						normalization = "none";
						if(benchmark.equals("nrp-e-3") || benchmark.equals("nrp-g-3") || benchmark.equals("nrp-m-3")) {
							Simulator.fixed_bounds = new double[][]{{-1,-1},{-1,-1},{-1,-1}};
						} else {
							Simulator.fixed_bounds = new double[][]{{-1,-1},{-1,-1}};
						}
					}
					
					
					Simulator.alg = alg;
					Simulator.weights = w;

					Simulator.main_test();


				}

			}
		}
	}

	public static void run_MOEA() {
		for (String alg : multi_algs) {
			Simulator.alg = alg;

			Simulator.main_test();

			File source = new File("/Users/"+System.getProperty("user.name")+"/research/monitor/"+pre+"ws-soa/"+fp);
			File r =null;
			File dest =null;
			if(benchmark.equals("nrp-e-3") || benchmark.equals("nrp-g-3") || benchmark.equals("nrp-m-3")) {
				 r = new File(
						"/Users/"+System.getProperty("user.name")+"/research/experiments-data/w-vs-p/eval-nrp/"
								+ "/" + benchmark+ "/" + alg + config + "/" + "/sas");
				 dest = new File(
						"/Users/"+System.getProperty("user.name")+"/research/experiments-data/w-vs-p/eval-nrp/"
								+ "/" + benchmark+ "/" + alg + config + "/" + "/sas");
			} else {
				 r = new File(
						"/Users/"+System.getProperty("user.name")+"/research/experiments-data/w-vs-p/eval-nrp/"
								+ "/" + Parser.selected + "/" + alg + config + "/" + "/sas");
				 dest = new File(
						"/Users/"+System.getProperty("user.name")+"/research/experiments-data/w-vs-p/eval-nrp/"
								+ "/" + Parser.selected + "/" + alg + config + "/" + "/sas");
			}
			
		

			if (r.exists()) {
				System.out.print("Remove " + r + "\n");
				try {
					delete(r);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (!dest.exists()) {
				dest.mkdirs();
			}

			try {
				copyFolder(source, dest);
				if (source.exists()) {
					System.out.print("Remove " + source + "\n");
					delete(source);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out
					.print("End of "
							+ "/Users/"+System.getProperty("user.name")+"/research/experiments-data/w-vs-p/eval-nrp/"
							+ "/" + Parser.selected + "/" + alg + "/" + "\n");

		}
		File f = new File("/Users/"+System.getProperty("user.name")+"/research/monitor/"+pre+"ws-soa/"+fp);

		try {
			if (f.exists()) {
				delete(f);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void copyFolder(File src, File dest) throws IOException {

		if (src.isDirectory()) {

			// if directory not exists, create it
			if (!dest.exists()) {
				dest.mkdir();
				System.out.println("Directory copied from " + src + "  to "
						+ dest);
			}

			// list all the directory contents
			String files[] = src.list();

			for (String file : files) {
				// construct the src and dest file structure
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				// recursive copy
				copyFolder(srcFile, destFile);
			}

		} else {
			// if file, then copy it
			// Use bytes stream to support all file types
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);

			byte[] buffer = new byte[1024];

			int length;
			// copy the file content in bytes
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}

			in.close();
			out.close();
			System.out.println("File copied from " + src + " to " + dest);
		}

	}

	public static void delete(File file) throws IOException {

		if (file.isDirectory()) {

			// directory is empty, then delete it
			if (file.list().length == 0) {

				file.delete();
				// System.out.println("Directory is deleted : "
				// + file.getAbsolutePath());

			} else {

				// list all the directory contents
				String files[] = file.list();

				for (String temp : files) {
					// construct the file structure
					File fileDelete = new File(file, temp);

					// recursive delete
					delete(fileDelete);
				}

				// check the directory again, if empty then delete it
				if (file.list().length == 0) {
					file.delete();
					// System.out.println("Directory is deleted : "
					// + file.getAbsolutePath());
				}
			}

		} else {
			// if file, then delete it
			file.delete();
			// System.out.println("File is deleted : " +
			// file.getAbsolutePath());
		}
	}
	
	public static double[][] convert(Double[][] d) {
		double[][] r = new double[d.length][];
		for (int i = 0; i < d.length;i++) {
			if(benchmark.equals("nrp-e-3") || benchmark.equals("nrp-g-3") || benchmark.equals("nrp-m-3")) {
				r[i] = new double[] {d[i][0],d[i][1]};
			} else {
				r[i] = new double[] {d[i][0],d[i][1]};
			}
			
		}
		return r;
		
	}
}
