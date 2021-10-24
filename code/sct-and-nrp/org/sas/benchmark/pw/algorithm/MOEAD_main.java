package org.sas.benchmark.pw.algorithm;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.femosaa.core.EAConfigure;
import org.femosaa.core.SASAlgorithmAdaptor;
import org.femosaa.core.SASSolutionInstantiator;
import org.femosaa.invalid.SASValidityAndInvalidityCoEvolver;
import org.femosaa.seed.FixedSeeder;
import org.femosaa.seed.NewSeeder;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

public class MOEAD_main {
	public static Logger logger_; // Logger object
	public static FileHandler fileHandler_; // FileHandler object

	protected Problem problem; // The problem to solve
	protected Algorithm algorithm; // The algorithm to use
	Operator crossover; // Crossover operator
	Operator mutation; // Mutation operator
	Operator selection; // Selection operator
	SASSolutionInstantiator factory;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public SolutionSet findParetoFront(int vars,  int numberOfObjectives_, int numberOfConstraints_) throws JMException,
	SecurityException, IOException, ClassNotFoundException {
	
		HashMap parameters; // Operator parameters

		// Logger object and file to store log messages
		if(SAS.isTest) { 
		logger_ = Configuration.logger_;
		fileHandler_ = new FileHandler("MOEAD.log");
		logger_.addHandler(fileHandler_);

		}
		problem = new SAS(vars, numberOfObjectives_, numberOfConstraints_);
		
		//algorithm = new MOEAD_STM_SAS_STATIC(problem, factory);
		algorithm = new MOEAD(problem, factory);

		// Algorithm parameters
	
		algorithm.setInputParameter("populationSize", EAConfigure.getInstance().pop_size);
		algorithm.setInputParameter("maxEvaluations", EAConfigure.getInstance().pop_size * EAConfigure.getInstance().generation);
		
		algorithm.setInputParameter("dataDirectory", System.getProperty("os.name").startsWith("Mac")? "weight" : "/home/tao/weight");
		//algorithm.setInputParameter("lambda", factory.getLambda());
		// Crossover operator
//		int tag = 2;
//		if (tag == 1) {
//			parameters = new HashMap();
//			parameters.put("CR", 0.5);
//			parameters.put("F", 0.5);
//			crossover = CrossoverFactory.getCrossoverOperator(
//					"DifferentialEvolutionCrossover", parameters);
//		} else {
		parameters = new HashMap();
		parameters.put("probability", EAConfigure.getInstance().crossover_rate);
		parameters.put("distributionIndex", 20.0);
		// This needs to change in testing.
		parameters.put("jmetal.metaheuristics.moead.SASSolutionInstantiator",
				factory);
		crossover = CrossoverFactory.getCrossoverOperator(
				"SinglePointCrossover", parameters);
		//SinglePointCrossover UniformCrossoverSAS

		
		
		// Mutation operator
		parameters = new HashMap();
		parameters.put("probability", EAConfigure.getInstance().mutation_rate);
		parameters.put("distributionIndex", 20.0);
		mutation = MutationFactory.getMutationOperator("BitFlipMutation",
				parameters);
		
		
		if(SASAlgorithmAdaptor.isSeedSolution) {
			//algorithm.setInputParameter("seeder", new Seeder(mutation));	
			//algorithm.setInputParameter("seeder", NewSeeder.getInstance(mutation));
			algorithm.setInputParameter("seeder", FixedSeeder.getInstance());	
		}

		algorithm.addOperator("crossover", crossover);
		algorithm.addOperator("mutation", mutation);

		
		
		long initTime = System.currentTimeMillis();
		
		SolutionSet population = algorithm.execute();
		long estimatedTime = System.currentTimeMillis() - initTime;
		if(SAS.isTest) { 
		logger_.setLevel(Level.CONFIG);
		logger_.log(Level.CONFIG, "Total execution time: " + estimatedTime + "ms");
		
		String str = "data/" + problem.getName()
		+ "M" + problem.getNumberOfObjectives() + "/SAS";
		
		Utils.deleteFolder(new File(str+ "/results.dat"));
		Utils.createFolder(str);
		
		population.printObjectivesToFile(str + "/results.dat");
		}
		
	printSolutions(population, numberOfObjectives_);
				
		
		org.femosaa.util.Logger.logSolutionSet(population, "SolutionSet.rtf");
		
		
		return population;

	}

	protected void printSolutions(SolutionSet pareto_front, int numberOfObjectives_) {
		System.out.print("Total number : " + pareto_front.size() + "\n");
		System.out.print("=========== solutions' objective values ===========\n");
		Iterator itr = pareto_front.iterator();
		while(itr.hasNext()) {
			Solution s = (Solution)itr.next();
			String str = "(";
			for(int i = 0; i < numberOfObjectives_; i++) {
				str = (i +1 == numberOfObjectives_)? str + s.getObjective(i) : 
					str + s.getObjective(i) + ",";
			}
			System.out.print(str+ ")\n");
			
		}
		
	
		/*itr = pareto_front.iterator();
		while(itr.hasNext()) {
			String data = "";
			Solution s = (Solution)itr.next();
			for(int i = 0; i < s.numberOfVariables(); i++) {
				try {
					data +=  s.getDecisionVariables()[i].getValue() + (i ==  s.numberOfVariables() - 1? "" : ",");
				} catch (JMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			data += "\n";
			System.out.print(data);
		}*/
    }

	protected Solution findSoleSolutionAfterEvolution(SolutionSet pareto_front) {
		// find the knee point
		Solution kneeIndividual = pareto_front.get(PseudoRandom.randInt(0, pareto_front.size() - 1)); 
		
		//for (int i = 0; i < problem.getNumberOfObjectives(); i++)
		//	System.out.print(kneeIndividual.getObjective(i) + "\n");
		
		
		String str = "data/" +problem.getName()
		+ "M" + problem.getNumberOfObjectives() + "/SAS";
		if(SAS.isTest) 
		Utils.deleteFolder(new File(str+ "/knee_results.dat"));
		SolutionSet set = new SolutionSet(1);
		set.add(kneeIndividual);
		if(SAS.isTest) 
		set.printObjectivesToFile(str + "/knee_results.dat");
		
		return kneeIndividual;
	}
}
