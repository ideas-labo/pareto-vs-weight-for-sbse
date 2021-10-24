package org.sas.benchmark.pw.nrp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.sas.benchmark.sm.nrp.Factory;

import jmetal.core.Solution;
import jmetal.util.JMException;
/**
 * 
 * For comparing pareto and weight
 * 
 * 
 * 0,1,0,0,1,3,0.6,23,3,250,100,4,250,23,40,0,1.4,47.112,7711.976945
 * 0,1,0,0,1,3,0.6,23,3,250,100,4,250,23,40,0,1.4,47.112,9664.754854
 * 
 * @author tao
 *
 */
public class Parser {
 
	public static Factory factory;
	
	public static int t_size = 500;
	
	public static int SH_count = 0;
	//public static List<Tuple> map = new ArrayList<Tuple>();
	public static List<Tuple> map = new ArrayList<Tuple>();
	public static Map<Integer,List<Integer>> user_req = new HashMap<Integer,List<Integer>>();
	public static Map<Integer,Integer> user_total_req = new HashMap<Integer,Integer>();
	public static List<Tuple> sub_map = new ArrayList<Tuple>();
	public static List<Integer[]> c = new ArrayList<Integer[]>();
	public static List<Double> p = new ArrayList<Double>();
	public static String selected = "nrp-g3";
	//x264 Best 244.23Worst 821.963
	// sql Best 12.513Worst 16.851
    public static void main( String[] args )
    {
    	map.clear();
    	c.clear();
    	p.clear();
    	user_req.clear();
    	user_total_req.clear();
    	read(selected);
    	
    	//35,143
    	
    	/*String s = "";
    	for (int i = 0; i < 11;i++) {
    		s += randInt(35,143) + ",";
    	}
    	
    	System.out.print(s);*/
    }
    
    public static double getCostSTD(Solution solution) throws JMException {
    	Map<Integer,Double> user_cost = new HashMap<Integer,Double>();
    	for (int i = 0; i < solution.getDecisionVariables().length; i++) {
    		if(solution.getDecisionVariables()[i].getValue() == 1) {
    			
    			for(int user : user_req.get(i+1)) {
    				if(!user_cost.containsKey(user)) {
    					user_cost.put(user, 0.0);
    				}
    				
    				double c = user_cost.get(user);
    			
    				user_cost.put(user, c + 1.0);
    				//System.out.print(user_cost.get(user) + "***\n");
    			}
    			
    			
    		}
    	}
    	
    	if(user_cost.size() == 0) {
    		return 0;
    	}
    	
    	double[] std = new double[user_cost.size()];
    	int i = 0;
    	for(int user : user_cost.keySet()) {
    		std[i] = user_cost.get(user) / user_total_req.get(user);
    		//System.out.print(user_cost.get(user) + " : " + user_total_req.get(user) + "***\n");
    		//System.out.print(std[i] + "**\n");
    		i++;
    	}
    	//System.out.print(calculateSD(std) + "***\n");
    	
    	return  calculateSD(std);
    }
    
	public static double calculateSD(double numArray[]) {
		double sum = 0.0, standardDeviation = 0.0;
		int length = numArray.length;

		for (double num : numArray) {
			sum += num;
		}

		double mean = sum / length;
		//System.out.print(mean + " : " + length + "***\n");
		for (double num : numArray) {
			standardDeviation += Math.pow(num - mean, 2);
		}

		return Math.sqrt(standardDeviation / length);
	}
    
    public static int randInt(int min, int max) {

        // NOTE: This will (intentionally) not run as written so that folks
        // copy-pasting have to think about how to initialize their
        // Random instance.  Initialization of the Random instance is outside
        // the main scope of the question, but some decent options are to have
        // a field that is initialized once and then re-used as needed or to
        // use ThreadLocalRandom (if using at least Java 1.7).
        // 
        // In particular, do NOT do 'Random rand = new Random()' here or you
        // will get not very good / not very random results.
        Random rand = new java.util.Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    
    public static void read(String name){
    	try {
			BufferedReader reader = new BufferedReader(new FileReader("/Users/"+System.getProperty("user.name")+"/research/experiments-data/public-data/next-release/realistic-nrp/"+name+".txt"));
			String line = null; 
			
		
			int user_count = 0;
			while ((line = reader.readLine()) != null) {
				
				
				if (map.size() == 0) {
					String[] data = line.split(" ");
					
					if(data.length != 1) {
						
						for (int i = 0; i < data.length; i++) {
							Tuple t = new Tuple();
							t.setCost(Double.parseDouble(data[i]));
							map.add(t);
							if(map.size() >= t_size && t_size > 0) {
								break;
							}
						}
						
						
					}
					
					//System.out.print(data.length + "****");
					
				} else {
					
                    String[] data = line.split(" ");
                    //System.out.print(line + "\n");
					if(data.length != 1) {
						//System.out.print(SH_count + ", " + line + "\n");
						double profit = Double.parseDouble(data[0]) / Integer.parseInt(data[1]);
					
						boolean included = false;
						for (int i = 2; i < data.length; i++) {
							
							if(Integer.parseInt(data[i]) > t_size && t_size > 0) {
								continue;
							}
							
							//map.get(Integer.parseInt(data[i]) - 1).setProfit(profit);	
							map.get(Integer.parseInt(data[i]) - 1).setProfit(map.get(Integer.parseInt(data[i]) 
									- 1).getProfit() + profit);
							//if(Integer.parseInt(data[i]) == 56) {
								//System.out.print(map.get(Integer.parseInt(data[i]) 
								//		- 1).getProfit() + "\n");
							//}
							included = true;
							
							
							if(!user_req.containsKey(Integer.parseInt(data[i]))) {
								user_req.put(Integer.parseInt(data[i]), new ArrayList<Integer>());
							}
							
							user_req.get(Integer.parseInt(data[i])).add(user_count);
							user_total_req.put(user_count, data.length-2);
						}
						if(included) {
							SH_count++;
							user_count++;
						}
						//System.out.print(user_count + ", " + line + "\n");
						
					}
					
					
				}
				
				
				
			}
			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		for (Tuple t : map) {
			System.out.print("Profit: " + t.getProfit() + ", Cost: " + t.getCost() + "\n");
		}
		System.out.print("No. of SH: " + SH_count + "\n");
		//System.out.print(user_req.size()+ "\n");
		
		for (int o : user_req.keySet()) {
			if(user_req.get(o).size() == 0) {
				user_req.remove(o);
			}
		}
		
		for (Integer o : user_req.keySet()) {
			System.out.print(o + " : " + user_req.get(o).size()+ "\n");
		}
		
		/*int i = 0;
		for (Tuple t : map) {
			//System.out.print("Profit: " + t.getProfit() + ", Cost: " + t.getCost() + "\n");
			//System.out.print("<item name=\""+ i +"\" provision=\"0\" constraint=\"-1\" differences=\"1\" pre_to_max=\"0.7\" pre_of_max=\"0.1\" min=\"0\" max=\"1\" price_per_unit=\"0.5\"  />\n");
		    i++;
		}
		
		i = 0;
		for (Tuple t : map) {
			//System.out.print("Profit: " + t.getProfit() + ", Cost: " + t.getCost() + "\n");
			//System.out.print("<feature name=\""+i+"\" type=\"categorical\" optional=\"true\"/>\n");
			 i++;
		}*/
		
		System.out.print(map.size());
		
	}
    
    
       public static void read1(String name){
    	try {
			BufferedReader reader = new BufferedReader(new FileReader("/Users/"+System.getProperty("user.name")+"/research/experiments-data/public-data/next-release/realistic-nrp/"+name+".txt"));
			String line = null; 
			
		
			
			while ((line = reader.readLine()) != null) {
				
				
				if (map.size() == 0) {
					String[] data = line.split(" ");
					
					if(data.length != 1) {
						
						for (int i = 0; i < data.length; i++) {
							Tuple t = new Tuple();
							t.setCost(Double.parseDouble(data[i]));
							map.add(t);
						}
						
						
					}
					
					//System.out.print(data.length + "****");
					
				} else {
					
                    String[] data = line.split(" ");
                    //System.out.print(line + "\n");
					if(data.length != 1) {
						double profit = Double.parseDouble(data[0]);
						
						Integer[] r = new Integer[Integer.parseInt(data[1])];
					
						for (int i = 2; i < data.length; i++) {
							r[i - 2] = Integer.parseInt(data[i]);
						}
						p.add(profit);
						c.add(r);
					}
					
					
				}
				
				
				
			}
			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (Tuple t : map) {
			System.out.print("Profit: " + t.getProfit() + ", Cost: " + t.getCost() + "\n");
		}
		
		/*int i = 0;
		for (Tuple t : map) {
			//System.out.print("Profit: " + t.getProfit() + ", Cost: " + t.getCost() + "\n");
			//System.out.print("<item name=\""+ i +"\" provision=\"0\" constraint=\"-1\" differences=\"1\" pre_to_max=\"0.7\" pre_of_max=\"0.1\" min=\"0\" max=\"1\" price_per_unit=\"0.5\"  />\n");
		    i++;
		}
		
		i = 0;
		for (Tuple t : map) {
			//System.out.print("Profit: " + t.getProfit() + ", Cost: " + t.getCost() + "\n");
			//System.out.print("<feature name=\""+i+"\" type=\"categorical\" optional=\"true\"/>\n");
			 i++;
		}*/
		
		System.out.print(map.size());
		
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
	
	
	public static class Tuple {
		private double cost = 0.0;
		private double profit = 0.0;
		
		
		public Tuple() {
			super();
		}
		public void setCost(double cost) {
			this.cost = cost;
		}
		public void setProfit(double profit) {
			this.profit = profit;
		}
		public double getCost() {
			return cost;
		}
		public double getProfit() {
			return profit;
		}
		
		
	}
}
