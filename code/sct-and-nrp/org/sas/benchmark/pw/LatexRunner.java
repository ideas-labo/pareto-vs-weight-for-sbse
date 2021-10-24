package org.sas.benchmark.pw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;

import org.sas.benchmark.pw.Data.Pack;

public class LatexRunner {

	//static String prefix = "/Users/tao/research/potential-publications/w-vs-p/supplementary/materials/";
	static String prefix = "/Users/"+System.getProperty("user.name")+"/research/potential-publications/w-vs-p/TSE/tables/embedding/";
	static String prefix_pdf = "/Users/"+System.getProperty("user.name")+"/research/potential-publications/w-vs-p/TSE/tables/embedding/figs/pdf/";
	// static String f = "w-vs-p.tex";
	/**
	 * @param args
	 */
	// pdflatex -synctex=1 -interaction=nonstopmode --shell-escape w-vs-p.tex
	public static void run(String f) {
		ProcessBuilder processBuilder = new ProcessBuilder();
		// Windows
		processBuilder.command("/Library/TeX/texbin/pdflatex", "-synctex=1",
				"-interaction=nonstopmode", "--shell-escape",
				"-output-directory=" + prefix_pdf, prefix + f + ".tex");

		try {

			Process process = processBuilder.start();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					process.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null) {
				//System.out.println(line);
			}

			int exitCode = process.waitFor();
			System.out.println("\nExited with error code : " + exitCode);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public static void copyFolder(String f) throws IOException {

		File src = new File(prefix + f + ".pdf");
		File dest = new File(
				"/Users/tao/research/potential-publications/w-vs-p/supplementary/pdf/"
						+ f + ".pdf");
		if (src.isDirectory()) {

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
	
	public static void start_norm(String data,String p) {
		String main = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(prefix + "norm-error-bar.tex"));
			String line = null;


			while ((line = reader.readLine()) != null) {
				main += line + "\n";
			}
			reader.close();
			
			
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		main = main.replace("[java_filling_data]", data +"\n");
		
		//System.out.print(main);
		
		try {
			File f = new File(prefix + "figs/norm-error-bar-"+p+".tex");
			if(f.exists()) {
				f.delete();
			}
		
			
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(prefix + "figs/norm-error-bar-"+p+".tex", false));		
			
			bw.write(main);
			bw.flush();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		run("figs/norm-error-bar-"+p);
	}
	
	public static void start_quality(String data,String p) {
		String main = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(prefix + "quality-error-bar.tex"));
			String line = null;


			while ((line = reader.readLine()) != null) {
				main += line + "\n";
			}
			reader.close();
			
			
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		main = main.replace("[java_filling_data]", data +"\n");
		
		//System.out.print(main);
		
		try {
			File f = new File(prefix + "figs/quality-error-bar-"+p+".tex");
			if(f.exists()) {
				f.delete();
			}
		
			
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(prefix + "figs/quality-error-bar-"+p+".tex", false));		
			
			bw.write(main);
			bw.flush();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		run("figs/quality-error-bar-"+p);
	}


	public static void generateFile() {

		String eval = "";
		//String time = "\begin{figure*}[t!]\n" + "\\centering\n";
		for (Pack p : Data.packs) {

			for (int i = 0; i < Data.weights.length; i ++) {
				String s = Data.weights[i];

				if (i % 3 == 0) {
					eval += "\\begin{figure*}[h]\n" + "\\centering\n";
				}
				
				eval +=  "\\begin{subfigure}[h]{0.3\\textwidth}\n" +
					"\\includegraphics[width=\\textwidth]{pdf/{"+Data.nameMap.get(p.getBenchmark())+"="+s+"=eval}.pdf}\n" + 
			      "\\subcaption{\\textsc{"+Data.nameMap.get(p.getBenchmark()) + "}, [" + s.split("-")[0] + "," + s.split("-")[1] + "]" +"}\n" + 
			      "\\end{subfigure}\n" + ((i + 1) % 3 == 0 ? "" :  "~\n");
			   
				if ((i+1) % 3 == 0) {
					eval +=   "\\caption{Convergence under equal number of evaluations for \\textsc{" + Data.nameMap.get(p.getBenchmark()) + "}.}\n" +
					"\\end{figure*}\n";
					//eval +=   "\\caption{Convergence under equal running time for \\textsc{" + Data.nameMap.get(p.getBenchmark()) + "}.}\n" +
					//"\\end{figure*}\n";
				}
			}

		}
		
		//eval +=   "\\caption{Convergence under equal number of evaluations.}\n" +
		//"\\end{figure*}\n";
		
		System.out.print(eval);
		
		//"\\caption{Convergence under equal running time.}\n"
	}
	
	public static void main (String[] args) {
		 generateFile();
	}
	
	

}
