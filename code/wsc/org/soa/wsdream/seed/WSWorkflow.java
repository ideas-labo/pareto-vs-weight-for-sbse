package org.soa.wsdream.seed;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.soa.femosaa.AbstractService;
import org.soa.femosaa.ConcreteService;
import org.soa.femosaa.Connector;


/**
 * 
 * 
 * 
 * 
 * @author tao
 *
 */

public class WSWorkflow {

    public static Map<WSAbstractService, Integer> map = new HashMap<WSAbstractService, Integer>();
    /***
     * For 5 group, start of 0, 5, 10
     * For 10 group, start of 0, 10, 20
     * For 15 group, start of 0, 15, 30
     */
    
	public static int global_number = 0;
	public static int global_number_record = 0;
	//public static Map<WSAbstractService, WSConcreteService> cs_map = new HashMap<WSAbstractService, WSConcreteService>();
	
	
	private static final String prefix = "/Users/"+System.getProperty("user.name")+"/research/experiments-data/seed/services/";
	public static List<WSAbstractService> all = new ArrayList<WSAbstractService> ();
	private static List<Integer> count = new ArrayList<Integer> (); 
	private WSAbstractService entryAbstractService;
	
	public static int total_number_of_as;
	
	static int[] numbers = new int[]{108,110,106,117,123,110,111,106,95,93,111,107,116,122,86};
	static int c = 0;
	
	static int[] numbers_e = new int[]{96,110,106,88,110,119,98,99,100,108,110,115,101,115,123,
		113,123,102,86,122,122,118,87,106,110,112,101,102,119,122,
		108,110,106,117,123,110,111,106,95,93,111,107,116,122,86};
	static int c_e = 0;
	
	static {
		
		
		for (int i = 0; i < 15; i++) {
			count.add(i);
		}
		
	}
	
	
	public static void main(String[] args) {
		new WSWorkflow();
	}
	
	public WSWorkflow(){
		//fiveASSetup3();
		//tenASSetup3();
		//fifteenASSetup3();
		extremeSetup();
		//For FROAS
		//complexSetup();
		
	}
	
	
	public double getObjectiveValues(int i, double[] xValue) {
		
		for (WSAbstractService as : map.keySet()) {
			as.resortSelected(xValue);
		}
		
		return entryAbstractService.getObjectiveValuesPort(i);
	}
	
	private static List<WSConcreteService> generateOptionalCSForExtreme(){
		//Random r = new Random();
		
		if(c_e > 44) {
			c_e = c_e%44 - 1;
		}
		
		if(global_number > 49) {
			global_number = global_number%49 - 1;
		}
		int no = global_number; //count.size() - 1 == 0? 0 : r.nextInt(count.size() - 1);
	
		
		//count.remove(no);
		System.out.print(count.size() + ":" + no + "\n");
		
		
		boolean isCon = true;
		
		
		List<WSConcreteService> list = new ArrayList<WSConcreteService> ();
		
		while (isCon) {
			String path = prefix + "service" + no + ".rtf";
			try {
				BufferedReader reader = new BufferedReader(new FileReader(path));
				String line = null;
				while ((line = reader.readLine()) != null
						&& list.size() != numbers_e[c_e]) {

					if ("".equals(line)) {
						continue;
					}

					list.add(new WSConcreteService(Double.parseDouble(line
							.split(",")[0]),
							Double.parseDouble(line.split(",")[1]), Double
									.parseDouble(line.split(",")[2])));
				}

				reader.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (list.size() == numbers_e[c_e]) {
				isCon = false;
			} else {
				no += 1;
				if(no > 49) {
					no = no%49 - 1;
				}
			}

		}
		System.out.print("Number of CS: " + list.size() + "\n");
		global_number++;
		c_e++;
		return list;
	}
	
	private static List<WSConcreteService> generateOptionalCSForComplex(){
		//Random r = new Random();
		if(global_number > 49) {
			global_number = global_number%49 - 1;
		}
		int no = global_number; //count.size() - 1 == 0? 0 : r.nextInt(count.size() - 1);
	
		
		//count.remove(no);
		System.out.print(count.size() + ":" + no + "\n");
		
		
		boolean isCon = true;
		
		
		List<WSConcreteService> list = new ArrayList<WSConcreteService> ();
		
		while (isCon) {
			String path = prefix + "service" + no + ".rtf";
			try {
				BufferedReader reader = new BufferedReader(new FileReader(path));
				String line = null;
				while ((line = reader.readLine()) != null
						&& list.size() != numbers[c]) {

					if ("".equals(line)) {
						continue;
					}

					list.add(new WSConcreteService(Double.parseDouble(line
							.split(",")[0]),
							Double.parseDouble(line.split(",")[1]), Double
									.parseDouble(line.split(",")[2])));
				}

				reader.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (list.size() == numbers[c]) {
				isCon = false;
			} else {
				no += 1;
				if(no > 49) {
					no = no%49 - 1;
				}
			}

		}
		System.out.print("Number of CS: " + list.size() + "\n");
		global_number++;
		c++;
		return list;
	}
	
	
	private static List<WSConcreteService> generateOptionalCS(){
		Random r = new Random();
		
		int no = global_number; //count.size() - 1 == 0? 0 : r.nextInt(count.size() - 1);
		global_number++;
		//count.remove(no);
//		System.out.print(count.size() + ":" + no + "\n");
//		if(global_number > 49) {
//			global_number = global_number%49 - 1;
//		}
		
		String path = prefix + "service" + no + ".rtf";
		List<WSConcreteService> list = new ArrayList<WSConcreteService> ();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String line = null;
			while ((line = reader.readLine()) != null) {
				
				if("".equals(line)) {
					continue;
				}
				
				list.add(new WSConcreteService(Double.parseDouble(line.split(",")[0]), 
						Double.parseDouble(line.split(",")[1]),
						Double.parseDouble(line.split(",")[2])));
			}
			
			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.print("Number of CS: " + list.size() + "\n");
		return list;
	}
	
	

	private void fiveASSetup1(){
		global_number = 0;
		WSAbstractService as0 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as1 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as2 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as3 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as4 = new WSAbstractService(generateOptionalCS()); 
		
		entryAbstractService = as0;
		
		as0.addAbstractService(as1);
		as0.addAbstractService(as2);
		as0.addConnector(Connector.PARALLEL);
		
		
		
		List<WSAbstractService> otherParallelOrConditional = new ArrayList<WSAbstractService>();
		otherParallelOrConditional.add(as1);
		otherParallelOrConditional.add(as2);
		
		as1.addOtherAbstractService(otherParallelOrConditional);
		as2.addOtherAbstractService(otherParallelOrConditional);
		
		as1.addAbstractService(as3);
		as1.addConnector(Connector.SEQUENTIAL);
		as2.addAbstractService(as3);
		as2.addConnector(Connector.SEQUENTIAL);
		
		as3.addAbstractService(as4);
		as3.addConnector(Connector.SEQUENTIAL);
		
		total_number_of_as = 5;
		all.add(as0);
		all.add(as1);
		all.add(as2);
		all.add(as3);
		all.add(as4);
	}
	
    private void fiveASSetup2(){
    	global_number = 5;
    	WSAbstractService as0 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as1 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as2 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as3 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as4 = new WSAbstractService(generateOptionalCS()); 
		
		entryAbstractService = as0;
		
		as0.addAbstractService(as1);
		as0.addAbstractService(as2);
		as0.addAbstractService(as3);
		as0.addConnector(Connector.PARALLEL);
		
		
		
		List<WSAbstractService> otherParallelOrConditional = new ArrayList<WSAbstractService>();
		otherParallelOrConditional.add(as1);
		otherParallelOrConditional.add(as2);
		otherParallelOrConditional.add(as3);
		
		as1.addOtherAbstractService(otherParallelOrConditional);
		as2.addOtherAbstractService(otherParallelOrConditional);
		as3.addOtherAbstractService(otherParallelOrConditional);
		
		as1.addAbstractService(as4);
		as1.addConnector(Connector.SEQUENTIAL);
		as2.addAbstractService(as4);
		as2.addConnector(Connector.SEQUENTIAL);		
		as3.addAbstractService(as4);
		as3.addConnector(Connector.SEQUENTIAL);
		
		total_number_of_as = 5;
		all.add(as0);
		all.add(as1);
		all.add(as2);
		all.add(as3);
		all.add(as4);
	}
    
    private void fiveASSetup3(){
    	global_number = 10;
    	WSAbstractService as0 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as1 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as2 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as3 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as4 = new WSAbstractService(generateOptionalCS()); 
		
		entryAbstractService = as0;
		
		as0.addAbstractService(as1);
		as0.addConnector(Connector.SEQUENTIAL);
		
		as1.addAbstractService(as2);
		as1.addConnector(Connector.SEQUENTIAL);
		
		as2.addAbstractService(as3);
		as2.addConnector(Connector.SEQUENTIAL);
		
		
		as3.addAbstractService(as4);
		as3.addConnector(Connector.SEQUENTIAL);
		
		total_number_of_as = 5;
		all.add(as0);
		all.add(as1);
		all.add(as2);
		all.add(as3);
		all.add(as4);
	}
    
	private void tenASSetup1(){
		global_number = 0;
		WSAbstractService as0 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as1 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as2 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as3 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as4 = new WSAbstractService(generateOptionalCS());
		WSAbstractService as5 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as6 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as7 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as8 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as9 = new WSAbstractService(generateOptionalCS()); 
		
		entryAbstractService = as0;
		
		as0.addAbstractService(as1);
		as0.addAbstractService(as2);
		as0.addConnector(Connector.PARALLEL);
		
		
		
		List<WSAbstractService> otherParallelOrConditional = new ArrayList<WSAbstractService>();
		otherParallelOrConditional.add(as1);
		otherParallelOrConditional.add(as2);
		
		as1.addOtherAbstractService(otherParallelOrConditional);
		as2.addOtherAbstractService(otherParallelOrConditional);
		
		as1.addAbstractService(as3);
		as1.addConnector(Connector.SEQUENTIAL);
		as2.addAbstractService(as3);
		as2.addConnector(Connector.SEQUENTIAL);
		
		
		as3.addAbstractService(as4);
		as3.addAbstractService(as5);
		as3.addConnector(Connector.PARALLEL);
		
		otherParallelOrConditional = new ArrayList<WSAbstractService>();
		otherParallelOrConditional.add(as4);
		otherParallelOrConditional.add(as5);
		
		as4.addOtherAbstractService(otherParallelOrConditional);
		as5.addOtherAbstractService(otherParallelOrConditional);
		
		as4.addAbstractService(as6);
		as4.addConnector(Connector.SEQUENTIAL);
		as5.addAbstractService(as6);
		as5.addConnector(Connector.SEQUENTIAL);
		
		
		as6.addAbstractService(as7);
		as6.addAbstractService(as8);
		as6.addConnector(Connector.PARALLEL);
		
		otherParallelOrConditional = new ArrayList<WSAbstractService>();
		otherParallelOrConditional.add(as7);
		otherParallelOrConditional.add(as8);
		
		as7.addOtherAbstractService(otherParallelOrConditional);
		as8.addOtherAbstractService(otherParallelOrConditional);
		
		as7.addAbstractService(as9);
		as7.addConnector(Connector.SEQUENTIAL);
		as8.addAbstractService(as9);
		as8.addConnector(Connector.SEQUENTIAL);
		
		total_number_of_as = 10;
		all.add(as0);
		all.add(as1);
		all.add(as2);
		all.add(as3);
		all.add(as4);
		all.add(as5);
		all.add(as6);
		all.add(as7);
		all.add(as8);
		all.add(as9);
		
	}
	
    private void tenASSetup2(){
    	global_number = 10;
    	WSAbstractService as0 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as1 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as2 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as3 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as4 = new WSAbstractService(generateOptionalCS());
		WSAbstractService as5 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as6 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as7 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as8 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as9 = new WSAbstractService(generateOptionalCS()); 
		
		entryAbstractService = as0;
		
		as0.addAbstractService(as1);
		as0.addAbstractService(as2);
		as0.addAbstractService(as3);
		as0.addAbstractService(as4);
		as0.addConnector(Connector.PARALLEL);
		
		
		
		List<WSAbstractService> otherParallelOrConditional = new ArrayList<WSAbstractService>();
		otherParallelOrConditional.add(as1);
		otherParallelOrConditional.add(as2);
		otherParallelOrConditional.add(as3);
		otherParallelOrConditional.add(as4);
		
		as1.addOtherAbstractService(otherParallelOrConditional);
		as2.addOtherAbstractService(otherParallelOrConditional);
		as3.addOtherAbstractService(otherParallelOrConditional);
		as4.addOtherAbstractService(otherParallelOrConditional);
		
		as1.addAbstractService(as5);
		as1.addConnector(Connector.SEQUENTIAL);
		as2.addAbstractService(as5);
		as2.addConnector(Connector.SEQUENTIAL);
		as3.addAbstractService(as5);
		as3.addConnector(Connector.SEQUENTIAL);
		as4.addAbstractService(as5);
		as4.addConnector(Connector.SEQUENTIAL);
		
		
		as5.addAbstractService(as6);
		as5.addAbstractService(as7);
		as5.addAbstractService(as8);
		as5.addAbstractService(as9);
		as5.addConnector(Connector.PARALLEL);
		
		otherParallelOrConditional = new ArrayList<WSAbstractService>();
		otherParallelOrConditional.add(as6);
		otherParallelOrConditional.add(as7);
		otherParallelOrConditional.add(as8);
		otherParallelOrConditional.add(as9);
		
		as6.addOtherAbstractService(otherParallelOrConditional);
		as7.addOtherAbstractService(otherParallelOrConditional);
		as8.addOtherAbstractService(otherParallelOrConditional);
		as9.addOtherAbstractService(otherParallelOrConditional);
		
		total_number_of_as = 10;
		all.add(as0);
		all.add(as1);
		all.add(as2);
		all.add(as3);
		all.add(as4);
		all.add(as5);
		all.add(as6);
		all.add(as7);
		all.add(as8);
		all.add(as9);
	}
    
    private void tenASSetup3(){
    	global_number = 20;
    	WSAbstractService as0 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as1 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as2 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as3 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as4 = new WSAbstractService(generateOptionalCS());
		WSAbstractService as5 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as6 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as7 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as8 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as9 = new WSAbstractService(generateOptionalCS()); 
		
		entryAbstractService = as0;
		
		as0.addAbstractService(as1);
		as0.addAbstractService(as2);
		as0.addAbstractService(as3);
		as0.addAbstractService(as4);
		as0.addAbstractService(as5);
		as0.addAbstractService(as6);
		as0.addAbstractService(as7);
		as0.addAbstractService(as8);
		as0.addConnector(Connector.PARALLEL);
		
		
		
		List<WSAbstractService> otherParallelOrConditional = new ArrayList<WSAbstractService>();
		otherParallelOrConditional.add(as1);
		otherParallelOrConditional.add(as2);
		otherParallelOrConditional.add(as3);
		otherParallelOrConditional.add(as4);
		otherParallelOrConditional.add(as5);
		otherParallelOrConditional.add(as6);
		otherParallelOrConditional.add(as7);
		otherParallelOrConditional.add(as8);
		
		as1.addOtherAbstractService(otherParallelOrConditional);
		as2.addOtherAbstractService(otherParallelOrConditional);
		as3.addOtherAbstractService(otherParallelOrConditional);
		as4.addOtherAbstractService(otherParallelOrConditional);
		as5.addOtherAbstractService(otherParallelOrConditional);
		as6.addOtherAbstractService(otherParallelOrConditional);
		as7.addOtherAbstractService(otherParallelOrConditional);
		as8.addOtherAbstractService(otherParallelOrConditional);
		
		as1.addAbstractService(as9);
		as1.addConnector(Connector.SEQUENTIAL);
		as2.addAbstractService(as9);
		as2.addConnector(Connector.SEQUENTIAL);
		as3.addAbstractService(as9);
		as3.addConnector(Connector.SEQUENTIAL);
		as4.addAbstractService(as9);
		as4.addConnector(Connector.SEQUENTIAL);
		as5.addAbstractService(as9);
		as5.addConnector(Connector.SEQUENTIAL);
		as6.addAbstractService(as9);
		as6.addConnector(Connector.SEQUENTIAL);
		as7.addAbstractService(as9);
		as7.addConnector(Connector.SEQUENTIAL);
		as8.addAbstractService(as9);
		as8.addConnector(Connector.SEQUENTIAL);
		
		total_number_of_as = 10;
		all.add(as0);
		all.add(as1);
		all.add(as2);
		all.add(as3);
		all.add(as4);
		all.add(as5);
		all.add(as6);
		all.add(as7);
		all.add(as8);
		all.add(as9);
	}
    
	private void fifteenASSetup1(){
		global_number = 0;
	 	WSAbstractService as0 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as1 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as2 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as3 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as4 = new WSAbstractService(generateOptionalCS());
		WSAbstractService as5 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as6 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as7 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as8 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as9 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as10 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as11 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as12 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as13 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as14 = new WSAbstractService(generateOptionalCS()); 
		
        entryAbstractService = as0;
		
		as0.addAbstractService(as1);
		as0.addAbstractService(as2);
		as0.addAbstractService(as3);
		as0.addAbstractService(as4);
		as0.addAbstractService(as5);
		as0.addAbstractService(as6);
		as0.addAbstractService(as7);
		as0.addAbstractService(as8);
		as0.addAbstractService(as9);
		as0.addAbstractService(as10);
		as0.addAbstractService(as11);
		as0.addAbstractService(as12);
		as0.addAbstractService(as13);
		as0.addConnector(Connector.PARALLEL);
		
		
		
		List<WSAbstractService> otherParallelOrConditional = new ArrayList<WSAbstractService>();
		otherParallelOrConditional.add(as1);
		otherParallelOrConditional.add(as2);
		otherParallelOrConditional.add(as3);
		otherParallelOrConditional.add(as4);
		otherParallelOrConditional.add(as5);
		otherParallelOrConditional.add(as6);
		otherParallelOrConditional.add(as7);
		otherParallelOrConditional.add(as8);
		otherParallelOrConditional.add(as9);
		otherParallelOrConditional.add(as10);
		otherParallelOrConditional.add(as11);
		otherParallelOrConditional.add(as12);
		otherParallelOrConditional.add(as13);
		
		as1.addOtherAbstractService(otherParallelOrConditional);
		as2.addOtherAbstractService(otherParallelOrConditional);
		as3.addOtherAbstractService(otherParallelOrConditional);
		as4.addOtherAbstractService(otherParallelOrConditional);
		as5.addOtherAbstractService(otherParallelOrConditional);
		as6.addOtherAbstractService(otherParallelOrConditional);
		as7.addOtherAbstractService(otherParallelOrConditional);
		as8.addOtherAbstractService(otherParallelOrConditional);
		as9.addOtherAbstractService(otherParallelOrConditional);
		as10.addOtherAbstractService(otherParallelOrConditional);
		as11.addOtherAbstractService(otherParallelOrConditional);
		as12.addOtherAbstractService(otherParallelOrConditional);
		as13.addOtherAbstractService(otherParallelOrConditional);
		
		as1.addAbstractService(as14);
		as1.addConnector(Connector.SEQUENTIAL);
		as2.addAbstractService(as14);
		as2.addConnector(Connector.SEQUENTIAL);
		as3.addAbstractService(as14);
		as3.addConnector(Connector.SEQUENTIAL);
		as4.addAbstractService(as14);
		as4.addConnector(Connector.SEQUENTIAL);
		as5.addAbstractService(as14);
		as5.addConnector(Connector.SEQUENTIAL);
		as6.addAbstractService(as14);
		as6.addConnector(Connector.SEQUENTIAL);
		as7.addAbstractService(as14);
		as7.addConnector(Connector.SEQUENTIAL);
		as8.addAbstractService(as14);
		as8.addConnector(Connector.SEQUENTIAL);
		as9.addAbstractService(as14);
		as9.addConnector(Connector.SEQUENTIAL);
		as10.addAbstractService(as14);
		as10.addConnector(Connector.SEQUENTIAL);
		as11.addAbstractService(as14);
		as11.addConnector(Connector.SEQUENTIAL);
		as12.addAbstractService(as14);
		as12.addConnector(Connector.SEQUENTIAL);
		as13.addAbstractService(as14);
		as13.addConnector(Connector.SEQUENTIAL);
		
		total_number_of_as = 15;
		all.add(as0);
		all.add(as1);
		all.add(as2);
		all.add(as3);
		all.add(as4);
		all.add(as5);
		all.add(as6);
		all.add(as7);
		all.add(as8);
		all.add(as9);
		all.add(as10);
		all.add(as11);
		all.add(as12);
		all.add(as13);
		all.add(as14);
	}
	
    private void fifteenASSetup2(){
    	global_number = 15;
     	WSAbstractService as0 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as1 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as2 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as3 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as4 = new WSAbstractService(generateOptionalCS());
		WSAbstractService as5 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as6 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as7 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as8 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as9 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as10 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as11 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as12 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as13 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as14 = new WSAbstractService(generateOptionalCS()); 
		
		
        entryAbstractService = as0;
		
		as0.addAbstractService(as1);
		as0.addAbstractService(as2);
		as0.addAbstractService(as3);
		as0.addAbstractService(as4);
		as0.addConnector(Connector.PARALLEL);
		
		
		
		List<WSAbstractService> otherParallelOrConditional = new ArrayList<WSAbstractService>();
		otherParallelOrConditional.add(as1);
		otherParallelOrConditional.add(as2);
		otherParallelOrConditional.add(as3);
		otherParallelOrConditional.add(as4);
		
		as1.addOtherAbstractService(otherParallelOrConditional);
		as2.addOtherAbstractService(otherParallelOrConditional);
		as3.addOtherAbstractService(otherParallelOrConditional);
		as4.addOtherAbstractService(otherParallelOrConditional);
		
		as1.addAbstractService(as5);
		as1.addConnector(Connector.SEQUENTIAL);
		as2.addAbstractService(as5);
		as2.addConnector(Connector.SEQUENTIAL);
		as3.addAbstractService(as5);
		as3.addConnector(Connector.SEQUENTIAL);
		as4.addAbstractService(as5);
		as4.addConnector(Connector.SEQUENTIAL);
		
		
		as5.addAbstractService(as6);
		as5.addAbstractService(as7);
		as5.addAbstractService(as8);
		as5.addAbstractService(as9);
		as5.addConnector(Connector.PARALLEL);
		
		otherParallelOrConditional = new ArrayList<WSAbstractService>();
		otherParallelOrConditional.add(as6);
		otherParallelOrConditional.add(as7);
		otherParallelOrConditional.add(as8);
		otherParallelOrConditional.add(as9);
		
		as6.addOtherAbstractService(otherParallelOrConditional);
		as7.addOtherAbstractService(otherParallelOrConditional);
		as8.addOtherAbstractService(otherParallelOrConditional);
		as9.addOtherAbstractService(otherParallelOrConditional);
		
		as6.addAbstractService(as10);
		as6.addConnector(Connector.SEQUENTIAL);
		as7.addAbstractService(as10);
		as7.addConnector(Connector.SEQUENTIAL);
		as8.addAbstractService(as10);
		as8.addConnector(Connector.SEQUENTIAL);
		as9.addAbstractService(as10);
		as9.addConnector(Connector.SEQUENTIAL);
		
		
		as10.addAbstractService(as11);
		as10.addAbstractService(as12);
		as10.addConnector(Connector.PARALLEL);
		
		otherParallelOrConditional = new ArrayList<WSAbstractService>();
		otherParallelOrConditional.add(as11);
		otherParallelOrConditional.add(as12);
		
		as11.addOtherAbstractService(otherParallelOrConditional);
		as12.addOtherAbstractService(otherParallelOrConditional);
		
		as11.addAbstractService(as13);
		as11.addAbstractService(as14);
		as11.addConnector(Connector.PARALLEL);
		
		as12.addAbstractService(as13);
		as12.addAbstractService(as14);
		as12.addConnector(Connector.PARALLEL);
		
		otherParallelOrConditional = new ArrayList<WSAbstractService>();
		otherParallelOrConditional.add(as13);
		otherParallelOrConditional.add(as14);
		
		as13.addOtherAbstractService(otherParallelOrConditional);
		as14.addOtherAbstractService(otherParallelOrConditional);
		
		total_number_of_as = 15;
		all.add(as0);
		all.add(as1);
		all.add(as2);
		all.add(as3);
		all.add(as4);
		all.add(as5);
		all.add(as6);
		all.add(as7);
		all.add(as8);
		all.add(as9);
		all.add(as10);
		all.add(as11);
		all.add(as12);
		all.add(as13);
		all.add(as14);
		
	}
    
    private void fifteenASSetup3(){
    	global_number = 30;
    	//all.clear();
     	WSAbstractService as0 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as1 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as2 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as3 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as4 = new WSAbstractService(generateOptionalCS());
		WSAbstractService as5 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as6 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as7 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as8 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as9 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as10 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as11 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as12 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as13 = new WSAbstractService(generateOptionalCS()); 
		WSAbstractService as14 = new WSAbstractService(generateOptionalCS()); 
		
		entryAbstractService = as0;
		
		as0.addAbstractService(as1);
		as0.addAbstractService(as2);
		as0.addConnector(Connector.PARALLEL);
		
		List<WSAbstractService> otherParallelOrConditional = new ArrayList<WSAbstractService>();
		otherParallelOrConditional.add(as1);
		otherParallelOrConditional.add(as2);
		
		as1.addOtherAbstractService(otherParallelOrConditional);
		as2.addOtherAbstractService(otherParallelOrConditional);
		
		as1.addAbstractService(as3);
		as1.addAbstractService(as4);
		as1.addConnector(Connector.PARALLEL);
		
		as2.addAbstractService(as3);
		as2.addAbstractService(as4);
		as2.addConnector(Connector.PARALLEL);
		
		otherParallelOrConditional = new ArrayList<WSAbstractService>();
		otherParallelOrConditional.add(as3);
		otherParallelOrConditional.add(as4);
		
		as3.addOtherAbstractService(otherParallelOrConditional);
		as4.addOtherAbstractService(otherParallelOrConditional);
		
		as3.addAbstractService(as5);
		as3.addAbstractService(as6);
		as3.addConnector(Connector.PARALLEL);
		
		as4.addAbstractService(as5);
		as4.addAbstractService(as6);
		as4.addConnector(Connector.PARALLEL);
		
		
		otherParallelOrConditional = new ArrayList<WSAbstractService>();
		otherParallelOrConditional.add(as5);
		otherParallelOrConditional.add(as6);
		
		as5.addOtherAbstractService(otherParallelOrConditional);
		as6.addOtherAbstractService(otherParallelOrConditional);
		
		as5.addAbstractService(as7);
		as5.addAbstractService(as8);
		as5.addConnector(Connector.PARALLEL);
		
		as6.addAbstractService(as7);
		as6.addAbstractService(as8);
		as6.addConnector(Connector.PARALLEL);
		
		
		
		otherParallelOrConditional = new ArrayList<WSAbstractService>();
		otherParallelOrConditional.add(as7);
		otherParallelOrConditional.add(as8);
		
		as7.addOtherAbstractService(otherParallelOrConditional);
		as8.addOtherAbstractService(otherParallelOrConditional);
		
		as7.addAbstractService(as9);
		as7.addAbstractService(as10);
		as7.addConnector(Connector.PARALLEL);
		
		as8.addAbstractService(as9);
		as8.addAbstractService(as10);
		as8.addConnector(Connector.PARALLEL);
		
		

		otherParallelOrConditional = new ArrayList<WSAbstractService>();
		otherParallelOrConditional.add(as9);
		otherParallelOrConditional.add(as10);
		
		as9.addOtherAbstractService(otherParallelOrConditional);
		as10.addOtherAbstractService(otherParallelOrConditional);
		
		as9.addAbstractService(as11);
		as9.addConnector(Connector.SEQUENTIAL);
		as10.addAbstractService(as11);
		as10.addConnector(Connector.SEQUENTIAL);
		
		
		
		as11.addAbstractService(as12);
		as11.addAbstractService(as13);
		as11.addAbstractService(as14);
		as11.addConnector(Connector.PARALLEL);
		
		otherParallelOrConditional = new ArrayList<WSAbstractService>();
		otherParallelOrConditional.add(as12);
		otherParallelOrConditional.add(as13);
		otherParallelOrConditional.add(as14);
		
		as12.addOtherAbstractService(otherParallelOrConditional);
		as13.addOtherAbstractService(otherParallelOrConditional);
		as14.addOtherAbstractService(otherParallelOrConditional);
		
		total_number_of_as = 15;
		all.add(as0);
		all.add(as1);
		all.add(as2);
		all.add(as3);
		all.add(as4);
		all.add(as5);
		all.add(as6);
		all.add(as7);
		all.add(as8);
		all.add(as9);
		all.add(as10);
		all.add(as11);
		all.add(as12);
		all.add(as13);
		all.add(as14);
	}

    
    
    public void complexSetup(){
    	global_number = global_number_record;
    	all.clear();
    	c = 0;
     	WSAbstractService as0 = new WSAbstractService(generateOptionalCSForComplex()); 
		WSAbstractService as1 = new WSAbstractService(generateOptionalCSForComplex()); 
		WSAbstractService as2 = new WSAbstractService(generateOptionalCSForComplex()); 
		WSAbstractService as3 = new WSAbstractService(generateOptionalCSForComplex()); 
		WSAbstractService as4 = new WSAbstractService(generateOptionalCSForComplex());
		WSAbstractService as5 = new WSAbstractService(generateOptionalCSForComplex()); 
		WSAbstractService as6 = new WSAbstractService(generateOptionalCSForComplex()); 
		WSAbstractService as7 = new WSAbstractService(generateOptionalCSForComplex()); 
		WSAbstractService as8 = new WSAbstractService(generateOptionalCSForComplex()); 
		WSAbstractService as9 = new WSAbstractService(generateOptionalCSForComplex()); 
		WSAbstractService as10 = new WSAbstractService(generateOptionalCSForComplex()); 
		WSAbstractService as11 = new WSAbstractService(generateOptionalCSForComplex()); 
		WSAbstractService as12 = new WSAbstractService(generateOptionalCSForComplex()); 
		WSAbstractService as13 = new WSAbstractService(generateOptionalCSForComplex()); 
		WSAbstractService as14 = new WSAbstractService(generateOptionalCSForComplex()); 
		
		entryAbstractService = as0;
		
		as0.addAbstractService(as1);
		as0.addAbstractService(as2);
		as0.addConnector(Connector.PARALLEL);
		
		List<WSAbstractService> otherParallelOrConditional = new ArrayList<WSAbstractService>();
		otherParallelOrConditional.add(as1);
		otherParallelOrConditional.add(as2);
		
		as1.addOtherAbstractService(otherParallelOrConditional);
		as2.addOtherAbstractService(otherParallelOrConditional);
		
		as1.addAbstractService(as3);
		as1.addAbstractService(as4);
		as1.addConnector(Connector.PARALLEL);
		
		as2.addAbstractService(as3);
		as2.addAbstractService(as4);
		as2.addConnector(Connector.PARALLEL);
		
		otherParallelOrConditional = new ArrayList<WSAbstractService>();
		otherParallelOrConditional.add(as3);
		otherParallelOrConditional.add(as4);
		
		as3.addOtherAbstractService(otherParallelOrConditional);
		as4.addOtherAbstractService(otherParallelOrConditional);
		
		as3.addAbstractService(as5);
		as3.addAbstractService(as6);
		as3.addConnector(Connector.PARALLEL);
		
		as4.addAbstractService(as5);
		as4.addAbstractService(as6);
		as4.addConnector(Connector.PARALLEL);
		
		
		otherParallelOrConditional = new ArrayList<WSAbstractService>();
		otherParallelOrConditional.add(as5);
		otherParallelOrConditional.add(as6);
		
		as5.addOtherAbstractService(otherParallelOrConditional);
		as6.addOtherAbstractService(otherParallelOrConditional);
		
		as5.addAbstractService(as7);
		as5.addAbstractService(as8);
		as5.addConnector(Connector.PARALLEL);
		
		as6.addAbstractService(as7);
		as6.addAbstractService(as8);
		as6.addConnector(Connector.PARALLEL);
		
		
		
		otherParallelOrConditional = new ArrayList<WSAbstractService>();
		otherParallelOrConditional.add(as7);
		otherParallelOrConditional.add(as8);
		
		as7.addOtherAbstractService(otherParallelOrConditional);
		as8.addOtherAbstractService(otherParallelOrConditional);
		
		as7.addAbstractService(as9);
		as7.addAbstractService(as10);
		as7.addConnector(Connector.PARALLEL);
		
		as8.addAbstractService(as9);
		as8.addAbstractService(as10);
		as8.addConnector(Connector.PARALLEL);
		
		

		otherParallelOrConditional = new ArrayList<WSAbstractService>();
		otherParallelOrConditional.add(as9);
		otherParallelOrConditional.add(as10);
		
		as9.addOtherAbstractService(otherParallelOrConditional);
		as10.addOtherAbstractService(otherParallelOrConditional);
		
		as9.addAbstractService(as11);
		as9.addConnector(Connector.SEQUENTIAL);
		as10.addAbstractService(as11);
		as10.addConnector(Connector.SEQUENTIAL);
		
		
		
		as11.addAbstractService(as12);
		as11.addAbstractService(as13);
		as11.addAbstractService(as14);
		as11.addConnector(Connector.PARALLEL);
		
		otherParallelOrConditional = new ArrayList<WSAbstractService>();
		otherParallelOrConditional.add(as12);
		otherParallelOrConditional.add(as13);
		otherParallelOrConditional.add(as14);
		
		as12.addOtherAbstractService(otherParallelOrConditional);
		as13.addOtherAbstractService(otherParallelOrConditional);
		as14.addOtherAbstractService(otherParallelOrConditional);
		
		total_number_of_as = 15;
		all.add(as0);
		all.add(as1);
		all.add(as2);
		all.add(as3);
		all.add(as4);
		all.add(as5);
		all.add(as6);
		all.add(as7);
		all.add(as8);
		all.add(as9);
		all.add(as10);
		all.add(as11);
		all.add(as12);
		all.add(as13);
		all.add(as14);
		global_number_record++;
	}
	
    public void extremeSetup(){
    	global_number = global_number_record;
    	all.clear();
    	c_e = 0;
    	
    	
    	for (int i = 0; i < 50/*100*/; i+=5) {
    		
    	
    		
    		WSAbstractService as0 = new WSAbstractService(generateOptionalCSForExtreme()); 
    		WSAbstractService as1 = new WSAbstractService(generateOptionalCSForExtreme()); 
    		WSAbstractService as2 = new WSAbstractService(generateOptionalCSForExtreme()); 
    		WSAbstractService as3 = new WSAbstractService(generateOptionalCSForExtreme()); 
    		WSAbstractService as4 = new WSAbstractService(generateOptionalCSForExtreme());
    		
    		if(i != 0 ) {
    			WSAbstractService pre = all.get(all.size()-1);
    			pre.addAbstractService(as0);
    			pre.addConnector(Connector.SEQUENTIAL);
    		} else {
    			entryAbstractService = as0;
    		}
  		
    		as0.addAbstractService(as1);
    		as0.addAbstractService(as2);
    		as0.addAbstractService(as3);
    		as0.addConnector(Connector.PARALLEL);
    		
    		
    		
    		ArrayList<WSAbstractService> otherParallelOrConditional = new ArrayList<WSAbstractService>();
    		otherParallelOrConditional.add(as1);
    		otherParallelOrConditional.add(as2);
    		otherParallelOrConditional.add(as3);
    		
    		as1.addOtherAbstractService(otherParallelOrConditional);
    		as2.addOtherAbstractService(otherParallelOrConditional);
    		as3.addOtherAbstractService(otherParallelOrConditional);
    		
    		as1.addAbstractService(as4);
    		as1.addConnector(Connector.SEQUENTIAL);
    		as2.addAbstractService(as4);
    		as2.addConnector(Connector.SEQUENTIAL);
    		as3.addAbstractService(as4);
    		as3.addConnector(Connector.SEQUENTIAL);
    		
    		all.add(as0);
    		all.add(as1);
    		all.add(as2);
    		all.add(as3);
    		all.add(as4);
    		
    		global_number+=5;
    	}
    	
    
		total_number_of_as = 50;//50;
    }
    
}
