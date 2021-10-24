package org.soa.wsdream.wvsp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.femosaa.core.EAConfigure;
import org.femosaa.core.SASAlgorithmAdaptor;
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
	private static String[] weights = new String[] {"0.1-0.9", "0.2-0.8",
			"0.3-0.7", "0.4-0.6", "0.5-0.5","0.6-0.4", "0.7-0.3", "0.8-0.2",
			"0.9-0.1"};
	//private static String[] weights = new String[] {"0.1-0.8-0.1", "0.8-0.1-0.1",  "0.1-0.1-0.8", "0.33-0.33-0.33" };
	
	private static String[] single_algs = new String[] { "ga", "hc", "rs", "sa" };
	private static String[] multi_algs = new String[] { "nsgaii"/*"nsgaii"*/ };
	public static String benchmark = "100AS";
	static boolean run_moea = false;
	
	private static String[] configs = new String[] { "1", "2", "3", "4", "5", "6", "7" };
	public static String config = "";//"" by default
	
	private static Map<String, Double[][]> map = new HashMap<String, Double[][]> ();
	static String budget = "";
	private static Map<String, Integer> time_budget = new HashMap<String, Integer> ();	
	
	
	static String pre = "";
	
	/**
	 * Need to change depending on whether evaluation or time is used as the budget.
	 */
	static {
		
		
		map.put("5AS1", new Double[][] {{ 0.23991508541432882,100000.0 },{7.1508557701761655,511.1274536334115}});
		map.put("5AS2", new Double[][] {{ 0.06609195307339148,9.27873566909 },{7.487333450323162,526.3460401602699}});	
		map.put("5AS3", new Double[][] {{ 0.034537514873149065,20.0 },{7.9644153227693115,561.0950944394882}});		
		map.put("10AS1", new Double[][] {{ 0.23991508541432882,100000.0 },{14.638189220499328,1037.4734937936814}});	
		map.put("10AS2", new Double[][] {{ 0.034537514873149065,20.0 },{14.18903836386653,1007.2092053088193}});	
		map.put("10AS3", new Double[][] {{ 0.0265800348905486,20.0 },{13.003269185118684,1100.0733835030317}});	
		map.put("15AS1", new Double[][] {{ 0.23991508541432882,100000.0 },{22.60260454326864,1598.5685882331695}});		
		map.put("15AS2", new Double[][] {{ 0.11520000737280048,21.5103991379 },{19.2278922262159,1546.187494372363}});		
		map.put("15AS3", new Double[][] {{ 0.4733333235511113,79.2533321667 },{20.587998926257754,1620.7714275417902}});	
		map.put("100AS", new Double[][] {{ 0.23991508541432882,100000.0 },{89.5586797555889,5439.036342296405}});//actually 50AS
		
		// all of these are 5AS3 etc
		map.put("5AS-3", new Double[][]  {{0.087,0.2},{ 0.23991508541432882,100000.0 },{7.9644153227693115,561.0950944394882}});	//100,300
		map.put("10AS-3", new Double[][] {{0.045,0.2},{ 0.0265800348905486,20.0 },{13.003269185118684,1100.0733835030317}});//100,300
		map.put("15AS-3", new Double[][] {{0.142,0.2},{ 0.4733333235511113,79.2533321667 },{20.587998926257754,1620.7714275417902}});//100,300
		
		
		/*map.put("5AS1", new Double[][] {{0.113, 100000.0},{7.1508557701761655,511.1274536334115}});
		map.put("5AS2", new Double[][] {{ 0.023,3.2290000000000005 },{7.487333450323162,526.3460401602699}});	
		map.put("5AS3", new Double[][] {{ -28.954023,-0.05},{7.9644153227693115,561.0950944394882}});		
		map.put("10AS1", new Double[][] {{ -4.1681414, -0.00001 },{14.638189220499328,1037.4734937936814}});	
		map.put("10AS2", new Double[][] {{ 0.034537514873149065,20.0 },{14.18903836386653,1007.2092053088193}});	
		map.put("10AS3", new Double[][] {{ 0.0265800348905486,20.0 },{13.003269185118684,1100.0733835030317}});	
		map.put("15AS1", new Double[][] {{ -4.1681414, -0.00001 },{22.60260454326864,1598.5685882331695}});		
		map.put("15AS2", new Double[][] {{ 0.11520000737280048,21.5103991379 },{19.2278922262159,1546.187494372363}});		
		map.put("15AS3", new Double[][] {{ 0.4733333235511113,79.2533321667 },{20.587998926257754,1620.7714275417902}});	
		map.put("100AS", new Double[][] {{ 0.23991508541432882,100000.0 },{89.5586797555889,5439.036342296405}});//actually 50AS
		
		*/
		/*map.put("5AS1", new Double[][] {{ 0.23991508541432882,20.0 },{7.1508557701761655,100.7522636376875}});
		map.put("5AS2", new Double[][] {{ 0.06609195307339148,9.278735669085547 },{7.487333450323162,125.51896714278436}});
		map.put("5AS3", new Double[][] {{ 0.034537514873149065,20.0 },{7.9644153227693115,116.23523687307376}});
		map.put("10AS1", new Double[][] {{ 0.23991508541432882,20.0 },{14.638189220499328,108.23959708801067}});
		map.put("10AS2", new Double[][] {{ 0.034537514873149065,20.0 },{14.18903836386653,122.45985991417098}});
		map.put("10AS3", new Double[][] {{ 0.0265800348905486,20.0 },{13.003269185118684,133.31684794920926}});
		map.put("15AS1", new Double[][] {{ 0.23991508541432882,20.0 },{22.60260454326864,116.20401241077998}});
		map.put("15AS2", new Double[][] {{ 0.11520000737280048,20.0 },{19.2278922262159,131.55839498723998}});
		map.put("15AS3", new Double[][] {{ 0.4733333235511113,20.0 },{20.587998926257754,127.81119568269564}});
		map.put("100AS", new Double[][] {{ 0.23991508541432882,21.12738917760053 },{89.5586797555889,181.41339171931216}});//actually 50AS
		*/
		
		// time
		
		/*map.put("5AS1", new Double[][] {{ 0.23991508541432882,20.0 },{7.1508557701761655,100.7522636376875}});
		map.put("5AS2", new Double[][] {{ 0.06609195307339148,9.278735669085547 },{7.487333450323162,125.51896714278436}});
		map.put("5AS3", new Double[][] {{ 0.034537514873149065,20.0 },{7.9644153227693115,116.23523687307376}});
		map.put("10AS1", new Double[][] {{ 0.23991508541432882,20.0 },{14.638189220499328,108.23959708801067}});
		map.put("10AS2", new Double[][] {{ 0.034537514873149065,20.0 },{14.18903836386653,122.45985991417098}});
		map.put("10AS3", new Double[][] {{ 0.0265800348905486,20.0 },{13.003269185118684,133.31684794920926}});
		map.put("15AS1", new Double[][] {{ 0.23991508541432882,20.0 },{22.60260454326864,116.20401241077998}});
		map.put("15AS2", new Double[][] {{ 0.11520000737280048,20.0 },{19.2278922262159,131.55839498723998}});
		map.put("15AS3", new Double[][] {{ 0.4733333235511113,20.0 },{20.587998926257754,127.81119568269564}});
		map.put("100AS", new Double[][] {{ 0.23991508541432882,21.12738917760053 },{89.5586797555889,181.41339171931216}});//actually 50AS
		*
		*
		*/
		//map.put("5AS-3", new Double[][]  {{0.087,0.2},{ 0.034537514873149065,20.0 },{7.9644153227693115,116.23523687307376}});	//100,400
		//map.put("10AS-3", new Double[][] {{0.045,0.2},{ 0.0265800348905486,20.0 },{13.003269185118684,133.31684794920926}});//100,400
		//map.put("15AS-3", new Double[][]  {{0.142,0.2},{ 0.4733333235511113,20.0 },{20.587998926257754,127.81119568269564}});//100,400
		
		
		// ga*1, hc*2, sa*2, rs*4 -> time
		
		// every 1000
		time_budget.put("5AS1", 1000);
		time_budget.put("5AS2", 1200);
		time_budget.put("5AS3", 1300);
		time_budget.put("10AS1", 1500);
		time_budget.put("10AS2", 1300);
		time_budget.put("10AS3", 1400);
		time_budget.put("15AS1", 2200);
		time_budget.put("15AS2", 1600);
		time_budget.put("15AS3", 1700);
		time_budget.put("100AS", 8000);
		
		time_budget.put("5AS-3", 1300);
		time_budget.put("10AS-3", 1400);
		time_budget.put("15AS-3", 1700);
		
	}

	public static void main(String[] args) {
		budget = "eval";//"time"
		//findExtreme();
		//if(1==1) return;
		
	
		WSSimulator.setup();

		File f = new File("/Users/"+System.getProperty("user.name")+"/research/monitor/"+pre+"ws-soa/sas");

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
		    if(1==1)return;
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
						EAConfigure.getInstance().setupWSConfigurationOnRun(time_budget.get(benchmark));
					} else if(alg.equals("hc")) {
						EAConfigure.getInstance().setupWSConfigurationOnRun(time_budget.get(benchmark) * 3);
					} else if(alg.equals("sa")) {
						EAConfigure.getInstance().setupWSConfigurationOnRun(time_budget.get(benchmark) * 2);
					} else if(alg.equals("rs")) {
						EAConfigure.getInstance().setupWSConfigurationOnRun(time_budget.get(benchmark) * 2);
					}
				}


				for (int k = 0; k<4;k++) {
					// We have already run the ga with dynamic update one
					if(!"ga".equals(alg)) {
						SASAlgorithmAdaptor.logGenerationOfObjectiveValue = 1000;//100
						//continue;
					} else {
						SASAlgorithmAdaptor.logGenerationOfObjectiveValue = 5000;//1000
					}

					if("ga".equals(alg) && k==0) {
						//continue;
					}
					
					//SASAlgorithmAdaptor.logGenerationOfObjectiveValue = 2000;

					String normalization = "";
					WSSimulator.fixed_bounds = null;
					
					if(k == 0) {
						normalization = "";
						WSSimulator.fixed_bounds = null;
					} else if(k == 1) {
						normalization = "fixed-bounds";
						WSSimulator.fixed_bounds = convert(map.get(benchmark));
					} else if(k == 2) {
						normalization = "ratio";
						if(benchmark.equals("5AS-3") || benchmark.equals("10AS-3") || benchmark.equals("15AS-3")) {
							WSSimulator.fixed_bounds = new double[][]{{0,0},{0,0},{0,0}};
						} else {
							WSSimulator.fixed_bounds = new double[][]{{0,0},{0,0}};
						}
					} else if(k == 3) {
						normalization = "none";
						if(benchmark.equals("5AS-3") || benchmark.equals("10AS-3") || benchmark.equals("15AS-3")) {
							WSSimulator.fixed_bounds = new double[][]{{-1,-1},{-1,-1},{-1,-1}};
						} else {
							WSSimulator.fixed_bounds = new double[][]{{-1,-1},{-1,-1}};
						}
					}

					WSSimulator.alg = alg;
					WSSimulator.weights = w;

					WSSimulator.main_test();

					File source = new File("/Users/"+System.getProperty("user.name")+"/research/monitor/"+pre+"ws-soa/sas");
					File r = new File("/Users/"+System.getProperty("user.name")+"/research/experiments-data/w-vs-p/"+budget+"-services/" + p + "/" + benchmark
							+ "/" + alg + "/" + normalization + "/sas");
					File dest = new File("/Users/"+System.getProperty("user.name")+"/research/experiments-data/w-vs-p/"+budget+"-services/" + p + "/" + benchmark
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

					System.out.print("End of " + "/Users/"+System.getProperty("user.name")+"/research/experiments-data/w-vs-p/"+budget+"-services/" + p + "/"
							+ benchmark + "/" + alg + "/" + normalization + "\n");
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
		WSSimulator.setup();

		//weights = new String[] {"1.0-0.0"};
		weights = new String[] {"1.0-0.0-0.0"};
		single_algs = new String[] {"ga"};
		//run_MOEA();

		//if(1==1)return;
		for (String p : weights) {

			String[] s = p.split("-");
			double[] w = new double[s.length];
			for (int i = 0; i < s.length; i++) {
				w[i] = Double.parseDouble(s[i]);
			}

			for (String alg : single_algs) {
				
				if(budget.equals("time")) {
					if(alg.equals("ga")) {
						EAConfigure.getInstance().setupWSConfigurationOnRun(time_budget.get(benchmark));
					} else if(alg.equals("hc")) {
						EAConfigure.getInstance().setupWSConfigurationOnRun(time_budget.get(benchmark) * 3);
					} else if(alg.equals("sa")) {
						EAConfigure.getInstance().setupWSConfigurationOnRun(time_budget.get(benchmark) * 2);
					} else if(alg.equals("rs")) {
						EAConfigure.getInstance().setupWSConfigurationOnRun(time_budget.get(benchmark) * 2);
					}
				}


				for (int k = 3; k<4;k++) {
				

					String normalization = "";
					WSSimulator.fixed_bounds = null;
					
					if(k == 0) {
						normalization = "";
						WSSimulator.fixed_bounds = null;
					} else if(k == 1) {
						normalization = "fixed-bounds";
						WSSimulator.fixed_bounds = convert(map.get(benchmark));
					} else if(k == 2) {
						normalization = "ratio";
						if(benchmark.equals("5AS-3") || benchmark.equals("10AS-3") || benchmark.equals("15AS-3")) {
							WSSimulator.fixed_bounds = new double[][]{{0,0},{0,0},{0,0}};
						} else {
							WSSimulator.fixed_bounds = new double[][]{{0,0},{0,0}};
						}
					} else if(k == 3) {
						normalization = "none";
						if(benchmark.equals("5AS-3") || benchmark.equals("10AS-3") || benchmark.equals("15AS-3")) {
							WSSimulator.fixed_bounds = new double[][]{{-1,-1},{-1,-1},{-1,-1}};
						} else {
							WSSimulator.fixed_bounds = new double[][]{{-1,-1},{-1,-1}};
						}
					}

					WSSimulator.alg = alg;
					WSSimulator.weights = w;

					WSSimulator.main_test();


				}
			}
		}
	}

	public static void run_MOEA() {
		for (String alg : multi_algs) {
			WSSimulator.alg = alg;

			WSSimulator.main_test();

			File source = new File("/Users/"+System.getProperty("user.name")+"/research/monitor/ws-soa/sas");
			File r = new File(
					"/Users/"+System.getProperty("user.name")+"/research/experiments-data/w-vs-p/"+budget+"-services/"
							+ "/" + benchmark + "/" + alg + config + "/" + "/sas");
			File dest = new File(
					"/Users/"+System.getProperty("user.name")+"/research/experiments-data/w-vs-p/"+budget+"-services/"
							+ "/" + benchmark + "/" + alg + config + "/" + "/sas");

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
							+ "/Users/"+System.getProperty("user.name")+"/research/experiments-data/w-vs-p/"+budget+"-services/"
							+ "/" + benchmark + "/" + alg + "/" + "\n");

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
