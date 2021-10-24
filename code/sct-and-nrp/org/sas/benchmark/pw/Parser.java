package org.sas.benchmark.pw;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
/**
 * 
 * For comparing pareto and weight
 * 
 * 
 * 0,1,0,0,1,3,0.6,23,3,250,100,4,250,23,40,0,1.4,47.112,7711.976945
 * 1:1:0:1:1:1:1:1:0:1:0:0:0:1:0:0:0:1:1:1:0:1:0:1:0:0:1:1:0:0:1:0:0:1:1:0:1:1:0:1:0:1:1:1:1:1:1:1:1:1:1:0:1:9:5:2:4:2:29
 * 0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:0:4:8:6:3:5:7:8
 * @author tao
 *
 */
public class Parser {
 
	
	
	
	//public static String[] keepZero = {"BDBCAll","BDBJAll","X264All"};
	// two objectives
	public static HashMap<String, Double> map1 = new HashMap<String, Double>();
	public static HashMap<String, Double> map2 = new HashMap<String, Double>();
	public static HashMap<String, Double> map3 = new HashMap<String, Double>();
	public static String selected = "SS-K";
	//x264 Best 244.23Worst 821.963
	// sql Best 12.513Worst 16.851
    public static void main( String[] args )
    {
    	
    	if(selected.equals("hsqldb") || selected.equals("vp8")) {
    		read(selected,true);
    	} else {
    		read(selected,false);
    	}
    	//readBoost("xgboost4096");
    }
    
    public static void readBoost(String name){
    	// We only need to get rid of the mandatory one or those that do not change at all.
    	ArrayList<ArrayList<Double>> list = new ArrayList<ArrayList<Double>>();
    	String[] names = null;
    	double time = 0.0;
    	try {
			BufferedReader reader = new BufferedReader(new FileReader("/Users/"+System.getProperty("user.name")+"/research/experiments-data/public-data/performance/flash-data/Flash-MultiConfig/Data/"+name+".csv"));
			String line = null; 
			
		
			int o = 0;
			while ((line = reader.readLine()) != null) {
				
				if(line.contains("$") || o==0) {
					String[] dd = line.split(",");
					names = dd;
					for(String s : dd) {
						System.out.print("\"" + s + "\",\n");
							
					}
					o++;
					continue;
				}
				String r = "";
				String[] data = line.split(",");
				
				for(int i = 0; i < data.length - 2; i++) {
					///r += r.equals("")? data[i] : ":" + data[i];
					if(list.size() <= i) {
						list.add(new ArrayList<Double>());
					}
					
					ArrayList<Double> subList = list.get(i);
					if(!subList.contains(Double.parseDouble(data[i]))) {
						subList.add(Double.parseDouble(data[i]));
					}
					
				}
				
				
			}
			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		HashSet<Integer> set = new HashSet<Integer>();
		
		
		for(int i = 0; i < list.size(); i++) {
			ArrayList<Double> subList = list.get(i);
			// means it cannot be changed and has no variability
			if (subList.size() == 1) {
				set.add(i);
			} else {
				double[] d = new double[subList.size()];
				for(int j = 0; j < subList.size(); j++) {
					d[j] = subList.get(j);
				}
				
				
				Arrays.sort(d);
				
				subList.clear();
				for(int j = 0; j < d.length; j++) {
					subList.add((Double)d[j]);
					System.out.print("Oringal index: " + i + "=" + d[j] + "\n");
				}
				
				
			}
		}
		
		for(int i = 0; i < list.size(); i++) {
			if(!set.contains(i)) {
			System.out.print("<item name=\""+ names[i] +"\" provision=\"0\" constraint=\"-1\" differences=\"1\" pre_to_max=\"0.7\" pre_of_max=\"0.1\" min=\"0\" max=\""+(list.get(i).size()-1)+"\" price_per_unit=\"0.5\"  />\n");
			}
		
		}
		
		for(int i = 0; i < list.size(); i++) {
			if(!set.contains(i)) {
			if(list.get(i).size() <= 2) {
				System.out.print("<feature name=\""+names[i]+"\" type=\"categorical\" optional=\"true\"/>\n");
			} else {
				System.out.print("<feature name=\""+names[i]+"\" type=\"numeric\" range=\"0 "+(list.get(i).size()-1)+"\" gap=\"1\" />\n");
			}
			}
		}
		
		System.out.print("Unchanged ones: " + set.toString() + "\n");
		//if (1==1)return;
		
		HashSet<String> print_out = new HashSet<String>();
		List<Double> o1 = new ArrayList<Double>();
		List<Double> o2 = new ArrayList<Double>();
    	try {
    		BufferedReader reader = new BufferedReader(new FileReader("/Users/"+System.getProperty("user.name")+"/research/experiments-data/public-data/performance/flash-data/Flash-MultiConfig/Data/"+name+".csv"));
			String line = null; 
			int o = 0;
			
			while ((line = reader.readLine()) != null) {
				
				if(line.contains("$") || o==0) {
					o++;
					continue;
				}
				String r = "";
				String[] data = line.split(",");
				
				for(int i = 0; i < data.length - 2; i++) {
					
					if(!set.contains(i)) {
						ArrayList<Double> subList = list.get(i);
						int v = subList.indexOf(Double.parseDouble(data[i]));
						r += r.equals("")? v : ":" + v;
					}
					
					
					
				}
				
				
				if(map1.containsKey(r)) {
					System.out.print(line + " : " + r+ ", current "  +map1.get(r) +" duplicate\n");
				}
			
				
				double v1 = "nan".equals(data[data.length-2]) ? 0.0 : Double.valueOf(data[data.length-2]);
				double v2 = "nan".equals(data[data.length-1]) ? 0.0 : Double.valueOf(data[data.length-1]);
				
				
				if(v1 == 0|| v2 == 0) {
					continue;
				}
				
				if(v1 < 0) {
					v1 = Math.abs(v1);
				}
				
				if(v2 < 0) {
					v2 = Math.abs(v2);
				}
						
				map1.put(r, v1);
				map2.put(r, v2);
		
				System.out.print(/*line + " : " + */r + "=" + map1.get(r)+ " and " + map2.get(r) +"\n");
				//System.out.print("("+Math.log10((1.0/map1.get(r)))+ "," + Math.log10(map2.get(r)) +")\n");
				if(!"nan".equals(data[data.length-1])) {
				  v1 = 1.0/v1;	
				  time += Double.valueOf(data[data.length-1]);
				  o1.add(v1);
				  o2.add(v2);
				  print_out.add("("+v1+","+v2+")");
				}
				
				
				
			}
			
			System.out.print(map1.size() + "\n");
			System.out.print(print_out.size() + "\n");
			System.out.print("Mean runtime: " + time/map1.size() + "\n");
			
			
			
			//getSeeds();
			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

    
    public static void read(String name, boolean more){
    	// We only need to get rid of the mandatory one or those that do not change at all.
    	ArrayList<ArrayList<Double>> list = new ArrayList<ArrayList<Double>>();
    	String[] names = null;
    	double time = 0.0;
    	try {
			BufferedReader reader = new BufferedReader(new FileReader("/Users/"+System.getProperty("user.name")+"/research/experiments-data/public-data/performance/flash-data/Flash-MultiConfig/Data/"+name+".csv"));
			String line = null; 
			
		
			
			while ((line = reader.readLine()) != null) {
				
				if(line.contains("$")) {
					String[] dd = line.split(",");
					names = dd;
					for(String s : dd) {
						System.out.print("\"" + s + "\",\n");
							
					}
					continue;
				}
				String r = "";
				String[] data = line.split(",");
				int n = more? 3 : 2;
				for(int i = 0; i < data.length - n; i++) {
					///r += r.equals("")? data[i] : ":" + data[i];
					if(list.size() <= i) {
						list.add(new ArrayList<Double>());
					}
					
					ArrayList<Double> subList = list.get(i);
					if(!subList.contains(Double.parseDouble(data[i]))) {
						subList.add(Double.parseDouble(data[i]));
					}
					
				}
				
				
			}
			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		HashSet<Integer> set = new HashSet<Integer>();
		
		
		for(int i = 0; i < list.size(); i++) {
			ArrayList<Double> subList = list.get(i);
			// means it cannot be changed and has no variability
			if (subList.size() == 1) {
				set.add(i);
			} else {
				double[] d = new double[subList.size()];
				for(int j = 0; j < subList.size(); j++) {
					d[j] = subList.get(j);
				}
				
				
				Arrays.sort(d);
				
				subList.clear();
				for(int j = 0; j < d.length; j++) {
					subList.add((Double)d[j]);
					System.out.print("Oringal index: " + i + "=" + d[j] + "\n");
				}
				
				
			}
		}
		
		for(int i = 0; i < list.size(); i++) {
			if(!set.contains(i)) {
			System.out.print("<item name=\""+ names[i] +"\" provision=\"0\" constraint=\"-1\" differences=\"1\" pre_to_max=\"0.7\" pre_of_max=\"0.1\" min=\"0\" max=\""+(list.get(i).size()-1)+"\" price_per_unit=\"0.5\"  />\n");
			}
		
		}
		
		for(int i = 0; i < list.size(); i++) {
			if(!set.contains(i)) {
			if(list.get(i).size() <= 2) {
				System.out.print("<feature name=\""+names[i]+"\" type=\"categorical\" optional=\"true\"/> />\n");
			} else {
				System.out.print("<feature name=\""+names[i]+"\" type=\"numeric\" range=\"0 "+(list.get(i).size()-1)+"\" gap=\"1\" />\n");
			}
			}
		}
		
		System.out.print("Unchanged ones: " + set.toString() + "\n");
		//if (1==1)return;
		
		double min1 = Double.MAX_VALUE;
		double max1 = Double.MIN_VALUE;
		
		double min2 = Double.MAX_VALUE;
		double max2 = Double.MIN_VALUE;
		
		
		double min3 = Double.MAX_VALUE;
		double max3 = Double.MIN_VALUE;
    	try {
    		BufferedReader reader = new BufferedReader(new FileReader("/Users/"+System.getProperty("user.name")+"/research/experiments-data/public-data/performance/flash-data/Flash-MultiConfig/Data/"+name+".csv"));
			String line = null; 
			
			
			while ((line = reader.readLine()) != null) {
				
				if(line.contains("$")) {
					continue;
				}
				String r = "";
				String[] data = line.split(",");
				int n = more? 3 : 2;
				for(int i = 0; i < data.length - n; i++) {
					
					if(!set.contains(i)) {
						ArrayList<Double> subList = list.get(i);
						int v = subList.indexOf(Double.parseDouble(data[i]));
						r += r.equals("")? v : ":" + v;
					}
					
					
					
				}
				
				
				if(map1.containsKey(r)) {
					System.out.print(line + " : " + r+ ", current "  +map1.get(r) +" duplicate\n");
				}
				
			
				if(more) {
					map1.put(r, Double.valueOf(data[data.length-3]));
					map2.put(r, Double.valueOf(data[data.length-2]));
					map3.put(r, Double.valueOf(data[data.length-1]));
					System.out.print(/*line + " : " + */r + "=" + map1.get(r)+ " and " + map2.get(r)+ " and " + map3.get(r) +"\n");
				} else {
					map1.put(r, Double.valueOf(data[data.length-2]));
					map2.put(r, Double.valueOf(data[data.length-1]));
					System.out.print(/*line + " : " + */r + "=" + map1.get(r)+ " and " + map2.get(r) +"\n");
				}
		
				
				
				if(map1.get(r) < min1) {
					min1 = map1.get(r);
				}
				

				if(map1.get(r) > max1) {
					max1 = map1.get(r);
				}
				
				if(map2.get(r) < min2) {
					min2 = map2.get(r);
				}
				

				if(map2.get(r) > max2) {
					max2 = map2.get(r);
				}
				
				if(more) {
					if(map3.get(r) < min3) {
						min3 = map3.get(r);
					}
					

					if(map3.get(r) > max3) {
						max3 = map3.get(r);
					}
				}
				
				if(more) {
					if(selected.equals("hsqldb")) {
						time += Double.valueOf(data[data.length-2]);
					} else {
						time += Double.valueOf(data[data.length-3]);
					}
					
				} else {
					time += Double.valueOf(data[data.length-1]);
				}
				
			}
			
			System.out.print(map1.size() + "\n");
			System.out.print("Mean runtime: " + time/map1.size() + "\n");
			if(more) {
				System.out.print("{" + min1 + "," + max1 + "},{"+ min2 + "," + max2 + "},{"+ min3 + "," + max3 + "}\n");
			} else {
				System.out.print("{" + -1.0*max1 + "," + -1.0*min1 + "},{"+ min2 + "," + max2 + "}\n");
			}
			
			//System.out.print("{" + min1 + "," + max1 + "},{"+ min2 + "," + max2 + "}\n");
			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
    public static void validateUnchanged(){
    	
    	
    }
    
	public static void validate(){
		try {
			BufferedReader reader = new BufferedReader(new FileReader("/Users/"+System.getProperty("user.name")+"/research/experiments-data/fuzzy-requirement/single-objective-dataset/"+selected+".csv"));
			String line = null; 
			
			int[] store = null;
			int total = 0;
			while ((line = reader.readLine()) != null) {
				
				if(line.startsWith("$")) {
					String[] d = line.split(",");
					for (int i = 0; i < d.length; i++) {
						//System.out.print("\""+d[i].substring(1) + "\",\n");
					}
					
					continue;
				}
				
				String[] data = line.split(",");
				
				if(store == null) {
					store = new int[data.length - 1];
					for(int i = 0; i < store.length; i++) {
						store[i] = 0;
					}
				}
				
				for(int i = 0; i < store.length; i++) {
					
					if(data[i].equals("1")) {
						store[i] += 1;
					} 
				}
				
				total++;
		
			}
			
			String r = "";
			for(int i = 0; i < store.length; i++) {
				
				if(store[i] == total) { 
					r += i + ",";
				}
			}
			
			System.out.print(r);
			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	private static void normalize(){
		double max =  17.894581279143072;
		double v = 4.1823277703510335;
		double min = 0;
		
		v = (v - min) / (max - min);
		
		System.out.print((0.3 * v) + 1.2);
		
		/**
		 *17.894581279143072
10.953841910378587
4.819035135705402
4.1823277703510335
1.0097075186941624
		 */
	}
	
	/**
	 * apache=0.08888888888888889;0.36666666666666664;0.6444444444444445;
	 * bdbc=0.011525532255482631;0.11996467982050739;0.37815312640389964;
	 * bdbj=0.025053422739665463;0.15032053643799279;0.5187532237860143;
	 * llvm=0.290950744558992;0.43413516609392905;0.7205040091638032;
	 * x264=0.26962281884538364;0.6158034940015544;0.9619841691577251;
	 * sql=0.11226371599815588;0.45804518211157225;0.6885661595205165;
	 */
	private static void run_normalize(){
		String[] a = new String[]{"13.0", "14.5", "15.5"};
		double max = 16.851;
		
		double min = 12.513;
		
		for (String s : a) {
			
			double v = Double.parseDouble(s);
			v = (v - min) / (max - min);
			
			System.out.print(v+";");
		}
		
	}
}
