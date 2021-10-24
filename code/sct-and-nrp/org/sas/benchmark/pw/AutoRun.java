package org.sas.benchmark.pw;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

//import org.sas.benchmark.pw.algorithm.Simulator;
//import org.sas.benchmark.pw.nrp.Parser;
import org.ssase.requirement.froas.RequirementPrimitive;
import org.ssase.requirement.froas.RequirementProposition;
import org.ssase.util.Repository;

public class AutoRun {
	/*
	 * new double[] { 500, 1000, 1500, 2000, 2500 }; new double[] { 0.1, 0.8, 5,
	 * 15, 40}; new double[] { 2000, 3300, 5000, 10000, 17000}; new double[] {
	 * 180, 220, 230, 250, 280}; new double[] { 11, 13, 14.5, 15.5, 18}; new
	 * double[] { 230, 400, 600, 800, 1000};
	 */
	// private static double[] ds = new double[] { 2000, 3300, 5000, 10000,
	// 17000};
	
	
	///private static String[] weights = new String[] {"0.1-0.9", "0.2-0.8",
	//		"0.3-0.7", "0.4-0.6", "0.5-0.5", "0.6-0.4", "0.7-0.3", "0.8-0.2",
	//		"0.9-0.1" };
	private static String[] weights = new String[] {"0.1-0.8-0.1", "0.8-0.1-0.1",  "0.1-0.1-0.8", "0.33-0.33-0.33" };
	
	private static String[] single_algs = new String[] { "ga", "hc", "rs", "sa" };
	private static String[] multi_algs = new String[] { "moead", "nsgaii" };
	static boolean run_moea = true;
	public static String benchmark = "hsqldb";
	
	private static String[] configs = new String[] { "1", "2", "3", "4", "5", "6", "7" };
	public static String config = "";//"" by default
	
    private static Map<String, Double[][]> map = new HashMap<String, Double[][]> ();
	
    static String budget = "";
    
	/**
	 * Need to change depending on whether evaluation or time is used as the budget.
	 */
	static {
		/**
		 *
		 * mutation 0.01, 0.1
		 * crossover, 0.9, 0.8
		 * population 50% and 100%
		 * 
		 * 0.1, 0.9, 100%
		 * 0.01, 0.9, 100%
		 * 0.1, 0.8, 100%
		 * 0.01, 0.8, 100%
		 * 0.1, 0.9, 50%
		 * 0.01, 0.9, 50%
		 * 0.1, 0.8, 50%
		 * 0.01, 0.8, 50%
		 **/
		/*
		 * map.put("SS-M", new Double[][] {{ 0.001996007984031936,0.33333333333333326 },{32.96,2021.989}});
		map.put("SS-J", new Double[][] {{4.310344827586207E-6,2.6641091219096334E-5},{ 1.9,1213.6 }});
		map.put("SS-K", new Double[][] {{2.878526194588371E-5,3.4410378170056095E-4},{ 3.3172,1130.6 }});
		map.put("SS-L", new Double[][] {{199.68,256.94},{ 11.0,29.0 }});
		map.put("SS-I", new Double[][] {{ 4.8564906998203096E-5,4.8564906998203096E-5 },{47.387,47.387}});//
		map.put("SS-A", new Double[][] {{ 4.333694474539545E-5,4.51997830410414E-5 },{148.88,158.68}});//
		map.put("SS-C", new Double[][] {{8.382229673093043E-5,9.198785760279643E-5},{ 247.45,250.78 }});
		map.put("SS-E", new Double[][] {{1.051591057269649E-5,1.2514704778114283E-4},{ 1.2993999999999999,36603.0 }});
		 * 
		 * */
		
		/*map.put("SS-M", new Double[][] {{0.001996007984031936,0.3333333333333333},{32.96,18842.204}});
		map.put("SS-J", new Double[][] {{4.310344827586207E-6,0.014692486262525345},{1.9,34733.0}});
		map.put("SS-K", new Double[][] {{2.878526194588371E-5,0.013745704467353952},{3.3172,55209.0}});
		map.put("SS-L", new Double[][] {{199.68,270.4},{11.0,29.0}});
		map.put("SS-I", new Double[][] {{4.8564906998203096E-5,4.7120912260861374E-4},{47.387,405.5}});//
		map.put("SS-A", new Double[][] {{4.333694474539545E-5,0.0034654837815359025},{148.88,9421.0}});//
		map.put("SS-C", new Double[][] {{8.382229673093043E-5,0.00823045267489712},{247.45,57645.0}});
		map.put("SS-E", new Double[][] {{1.051591057269649E-5,1.9831039543092847E-4},{1.2994,94553.0}});
		map.put("xgboost4096", new Double[][] {{0.010917431192119686,0.04890410959},{1.03667593,344.8813553}});*/
		
		map.put("SS-M", new Double[][] {{-501.0,-3.0},{32.96,18842.204}});
		map.put("SS-J", new Double[][] {{-232000.0,-68.062},{1.9,34733.0}});
		map.put("SS-K", new Double[][] {{-34740.0,-72.75},{3.3172,55209.0}});
		map.put("SS-L", new Double[][] {{199.68,270.4},{11.0,29.0}});
		map.put("SS-I", new Double[][] {{-20591.0,-2122.2},{47.387,405.5}});//
		map.put("SS-A", new Double[][] {{-23075.0,-288.56},{148.88,9421.0}});//
		map.put("SS-C", new Double[][] {{-11930.0,-121.5},{247.45,57645.0}});
		map.put("SS-E", new Double[][] {{-95094.0,-5042.6},{1.2994,94553.0}});
		map.put("hsqldb", new Double[][] {{6.6166,16.8008},{248.2,520.2},{2.011727,14.174992}});/*30,20*/
		map.put("vp8", new Double[][] {{5183.8,61545.0},{217.6,2217.0},{25.599007,84.642095}});/*50,30*/
		
	}

	public static void main(String[] args) {
		
		
		budget = "eval";//"time"
		//double l = Double.MAX_VALUE/0.001;
		//3.0,48.89200000000001 3.0,33.305
		//System.out.print(l > 1);
		
		//if(1==1) return;
		Parser.selected = benchmark;
		Simulator.setup();

		File f = new File("/Users/"+System.getProperty("user.name")+"/research/monitor/ws-soa/sas");

		try {
			if (f.exists()) {
				delete(f);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(run_moea) {
			run_MOEA();		
			if(1==1) return;
		}
		

		for (String p : weights) {

			String[] s = p.split("-");
			double[] w = new double[s.length];
			for (int i = 0; i < s.length; i++) {
				w[i] = Double.parseDouble(s[i]);
			}

			for (String alg : single_algs) {

			
				for (int k = 0; k<4;k++) {

					// We have already run the ga with dynamic update one
					if(k == 0) {
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
						Simulator.fixed_bounds = convert(map.get(Parser.selected));
					} else if(k == 2) {
						normalization = "ratio";
						if(benchmark.equals("hsqldb") || benchmark.equals("vp8")) {
							Simulator.fixed_bounds = new double[][]{{0,0},{0,0},{0,0}};
						} else {
							Simulator.fixed_bounds = new double[][]{{0,0},{0,0}};
						}
						
					} else if(k == 3) {
						normalization = "none";
						if(benchmark.equals("hsqldb") || benchmark.equals("vp8")) {
							Simulator.fixed_bounds = new double[][]{{-1,-1},{-1,-1},{-1,-1}};
						} else {
							Simulator.fixed_bounds = new double[][]{{-1,-1},{-1,-1}};
						}
					
					}
					
					Simulator.alg = alg;
					Simulator.weights = w;

					Simulator.main_test();

					File source = new File("/Users/"+System.getProperty("user.name")+"/research/monitor/ws-soa/sas");
					File r = new File("/Users/"+System.getProperty("user.name")+"/research/experiments-data/w-vs-p/"+budget+"-configuration-optimization/" + p
							+ "/" + benchmark + "/" + alg + "/" + normalization + "/sas");
					File dest = new File("/Users/"+System.getProperty("user.name")+"/research/experiments-data/w-vs-p/"+budget+"-configuration-optimization/" + p
							+ "/" + benchmark + "/" + alg + "/" + normalization + "/sas");

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
							.print("End of " + "/Users/"+System.getProperty("user.name")+"/research/experiments-data/w-vs-p/"+budget+"-configuration-optimization/"
									+ p + "/" + benchmark + "/" + alg + "/" + normalization +  "\n");
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

	public static void run_MOEA() {
		for (String alg : multi_algs) {
			Simulator.alg = alg;

			Simulator.main_test();

			File source = new File("/Users/"+System.getProperty("user.name")+"/research/monitor/ws-soa/sas");
			File r = new File(
					"/Users/"+System.getProperty("user.name")+"/research/experiments-data/w-vs-p/"+budget+"-configuration-optimization/"
							+ "/" + benchmark + "/" + alg + config + "/" + "/sas");
			File dest = new File(
					"/Users/"+System.getProperty("user.name")+"/research/experiments-data/w-vs-p/"+budget+"-configuration-optimization/"
							+ "/" + benchmark + "/" + alg + config +  "/" + "/sas");

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
							+ "/Users/"+System.getProperty("user.name")+"/research/experiments-data/w-vs-p/"+budget+"-configuration-optimization/"
							+ "/" + benchmark + "/" + alg + config +  "/" + "\n");

		}
		File f = new File("/Users/"+System.getProperty("user.name")+"/research/monitor/ws-soa/sas");

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
			r[i] = new double[] {d[i][0],d[i][1]};
		}
		return r;
		
	}
}
