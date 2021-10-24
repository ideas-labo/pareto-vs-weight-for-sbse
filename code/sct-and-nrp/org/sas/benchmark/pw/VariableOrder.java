package org.sas.benchmark.pw;

import java.util.ArrayList;
import java.util.List;



public class VariableOrder {

	private static List<String> SS_N = new ArrayList<String>();/*diffcult*/
	private static List<String> SS_O = new ArrayList<String>();/*diffcult*/
	private static List<String> SS_M = new ArrayList<String>();/*p=100,g=100*,s*/
	private static List<String> SS_K = new ArrayList<String>();/*p=50,g=30,s*/
	private static List<String> SS_J = new ArrayList<String>();/*p=50,g=30,s*/
	private static List<String> SS_L = new ArrayList<String>();/*p=20,g=25,s*/
	private static List<String> SS_I = new ArrayList<String>();/*p=20,g=25,s*/
	private static List<String> SS_A = new ArrayList<String>();/*p=20,g=25,s*/
	private static List<String> SS_C = new ArrayList<String>();/*p=20,g=25,s*/
	private static List<String> SS_E = new ArrayList<String>();/*p=20,g=15,s*/
	private static List<String> xgboost4096 = new ArrayList<String>();/*p=100,g=100,s*/
	private static List<String> vp8 = new ArrayList<String>();/*p=100,g=100,s*/
	private static List<String> hsqldb = new ArrayList<String>();/*p=100,g=100,s*/
	
	static {
		// The excluded ones are still here but they do not affect the order
		String[] array = new String[]{
				"no_mbtree",
				"no_asm",
				"no_cabac",
				"no_scenecut",
				"aq_strength",
				"bframes",
				"qcomp",
				"qp",
				"ref",
				"rc_lookahead",
				"b_bias",
				"threads",
				"keyint",
				"crf",
				"scenecut",
				"seek",
				"ipratio"			
		};
		
		attach(SS_N, array);
		
		array = new String[]{
				"extrema",
				"enabledOptimizations",
				"disabledOptimizations",
				"ls",
				"dcr",
				"cf",
				"lir",
				"inl",
				"lur",
				"wlur",
				"prfunr",
				"lus",
				"cse",
				"dfr",
				"wlt",
				"wlf",
				"awlf",
				"ivecyc",
				"ive",
				"ivesli",
				"wlflt",
				"ae",
				"dl",
				"rco",
				"uip",
				"dr",
				"ipc",
				"wlpg",
				"cp",
				"vp",
				"srf",
				"phm",
				"dpa",
				"msca",
				"wls",
				"as",
				"wlsimp",
				"cwle",
				"lro",
				"lao",
				"pra",
				"rnb",
				"rip",
				"sde",
				"wlprop",
				"saa",
				"cyc",
				"scyc",
				"saacyc",
				"wlsd",
				"cts",
				"ucts",
				"maxoptcyc",
				"maxlur",
				"maxwlur",
				"maxprfur",
				"maxae",
				"initmheap",
				"initwheap"
				
		};
		
		attach(SS_O, array);
		
		array = new String[]{
				"F",
				"smoother",
				"colorGS",
				"relaxParameter",
				"V",
				"Jacobi",
				"line",
				"zebraLine",
				"cycle",
				"alpha",
				"beta",
				"preSmoothing",
				"postSmoothing"
				
		};
		
		attach(SS_M, array);
		
		array = new String[]{
				"spouts",
				"max_spout",
				"spout_wait",
				"spliters",
				"counters",
				"netty_min_wait"
				
		};
		
		attach(SS_K, array);
		
		
		array = new String[]{
				"spouts",
				"max_spout",
				"sorters",
				"emit_freq",
				"chunk_size",
				"message_size"
				
		};
		
		attach(SS_J, array);
		
		array = new String[]{
				"a",
				"b",
				"c",
				"d",
				"e",
				"f",
				"g",
				"h",
				"i",
				"j",
				"k"
				
		};
		
		attach(SS_L, array);
		
		array = new String[]{
				"spout",
				"split",
				"count",
				"buffer-size",
				"heap"
		};
		
		attach(SS_I, array);
		
	    array = new String[]{
	    		"spout_wait",
	    		"spliters",
	    		"counters"
		};
		
		attach(SS_A, array);
		
		
	    array = new String[]{
	    		"spout_wait",
	    		"spliters",
	    		"counters"
		};
		
		attach(SS_C, array);
		
	    array = new String[]{
	    		"max_spout",
	    		"spliters",
	    		"counters"
		};
		
		attach(SS_E, array);
		
		   
	    array = new String[]{
	    		"﻿vm_type",
	    		"min_child_weight",
	    		"nthread",
	    		"n_estimators",
	    		"max_depth",
	    		"learning_rate",
	    		"max_delta_step",
	    		"subsample",
	    		"colsample_bytree",
	    		"lambda",
	    		"alpha",
	    		"scale_pos_weight",
	    		"colsample_bylevel"
			};
			
	    attach(xgboost4096, array);
	    

	 	array = new String[]{
	 			"compressed_script",
	 			"encryption",
	 			"crypt_aes",
	 			"crypt_blowfish",
	 			"transaction_control",
	 			"txc_mvlocks",
	 			"txc_mvcc",
	 			"txc_locks",
	 			"table_type",
	 			"memory_tables",
	 			"cached_tables",
	 			"small_cache",
	 			"large_cache",
	 			"logging",
	 			"detailed_logging",
	 			"no_write_delay",
	 			"small_log"
		};
		
        attach(hsqldb, array);
        
        array = new String[]{
	    		"twoPass",
	    		"quality",
	    		"bestQuality",
	    		"goodQuality",
	    		"rtQuality",
	    		"constantBitrate",
	    		"autoAltRef",
	    		"noAltRef",
	    		"allowResize",
	    		"threads",
	    		"tokenParts",
	    		"arnrMaxFrames",
	    		"arnrStrength"
			};
			
	    attach(vp8, array);
	}
	
	
	
	public static List<String> getList(){
		

		if("SS-N".equals(Parser.selected)) {
			return SS_N;
		} else if("SS-O".equals(Parser.selected)) {
			return SS_O;
		} else if("SS-M".equals(Parser.selected)) {
			return SS_M;
		} else if("SS-K".equals(Parser.selected)) {
			return SS_K;
		} else if("SS-J".equals(Parser.selected)) {
			return SS_J;
		} else if("SS-L".equals(Parser.selected)) {
			return SS_L;
		} else if("SS-I".equals(Parser.selected)) {
			return SS_I;
		} else if("SS-A".equals(Parser.selected)) {
			return SS_A;
		} else if("SS-C".equals(Parser.selected)) {
			return SS_C;
		} else if("SS-E".equals(Parser.selected)) {
			return SS_E;
		} else if("xgboost4096".equals(Parser.selected)) {
			return xgboost4096;
		} else if("hsqldb".equals(Parser.selected)) {
			return hsqldb;
		} else if("vp8".equals(Parser.selected)) {
			return vp8;
		}

		return null;
	}
	
	private static void attach(List<String> list, String[] array){
		for (int i = 0; i < array.length; i++) {
			list.add(array[i]);
		}
		
	}
	
	public static void main(String[] arg) {
		for (int i = 0; i < SS_N.size(); i++) {
			//System.out.print(" <feature name=\""+X264.get(i)+"\" type=\"categorical\" optional=\"true\"/>\n");
			System.out.print("<item name=\""+SS_N.get(i)+"\" provision=\"0\" constraint=\"-1\" differences=\"1\" pre_to_max=\"0.7\" pre_of_max=\"0.1\" min=\"0\" max=\"1\" price_per_unit=\"0.5\"  />\n");
		}
	}
}
