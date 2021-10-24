//  IBEA_main.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.sas.benchmark.pw.algorithm;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.BinaryTournament;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.ZDT.*;
import jmetal.problems.DTLZ.*;
import jmetal.problems.WFG.*;
import jmetal.problems.LZ09.*;
import jmetal.problems.cec2009Competition.*;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.comparators.FitnessComparator;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.femosaa.core.EAConfigure;
import org.femosaa.core.SASAlgorithmAdaptor;
import org.femosaa.core.SASProblemFactory;
import org.femosaa.core.SASSolutionInstantiator;
import org.femosaa.invalid.SASValidityAndInvalidityCoEvolver;
import org.femosaa.seed.NewSeeder;

/**
 * Class for configuring and running the DENSEA algorithm
 */
public class IBEA_SAS_main {
	public static Logger logger_; // Logger object
	public static FileHandler fileHandler_; // FileHandler object
	protected Problem problem; // The problem to solve
	protected Algorithm algorithm; // The algorithm to use
	Operator crossover; // Crossover operator
	Operator mutation; // Mutation operator
	Operator selection; // Selection operator

	
		
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public SolutionSet findParetoFront(int vars,  int numberOfObjectives_, int numberOfConstraints_) throws JMException,
			SecurityException, IOException, ClassNotFoundException {
	
		HashMap parameters; // Operator parameters
		Operator selection; // Selection operator

		// Logger object and file to store log messages
		if(SAS.isTest) { 
		logger_ = Configuration.logger_;
		fileHandler_ = new FileHandler("MOEAD.log");
		logger_.addHandler(fileHandler_);
		}
	    problem = new SAS(vars, numberOfObjectives_, numberOfConstraints_);
		
		algorithm = new IBEA_SAS(problem);

		// Algorithm parameters
		int popsize = EAConfigure.getInstance().pop_size;
		int generations = EAConfigure.getInstance().generation;
		algorithm.setInputParameter("archiveSize", popsize*5);
		algorithm.setInputParameter("populationSize", popsize);
		algorithm.setInputParameter("maxEvaluations", popsize * generations);
		
		// Crossover operator
		parameters = new HashMap();
		parameters.put("probability", EAConfigure.getInstance().crossover_rate);
		parameters.put("distributionIndex", 20.0);
		// This needs to change in testing.
		//parameters.put("jmetal.metaheuristics.moead.SASSolutionInstantiator",
			//	factory);

		crossover = CrossoverFactory.getCrossoverOperator(
				"SinglePointCrossover", parameters);
		//SinglePointCrossover UniformCrossoverSAS
		//}
		
		
		//if(SASAlgorithmAdaptor.isPreserveInvalidSolution) {
			//algorithm.setInputParameter("vandInvCoEvolver", new SASValidityAndInvalidityCoEvolver(factory, 0.9, 0.1, 20));
		//}

		// Mutation operator
		parameters = new HashMap();
		parameters.put("probability", EAConfigure.getInstance().mutation_rate);
		parameters.put("distributionIndex", 20.0);
		mutation = MutationFactory.getMutationOperator("BitFlipMutation",
				parameters);

		if(SASAlgorithmAdaptor.isSeedSolution) {
			//algorithm.setInputParameter("seeder", new Seeder(mutation));	
			algorithm.setInputParameter("seeder", NewSeeder.getInstance(mutation));			
		}
		
		selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters);

		algorithm.addOperator("crossover", crossover);
		algorithm.addOperator("mutation", mutation);
		algorithm.addOperator("selection", selection);
		
		
		long initTime = System.currentTimeMillis();
		
		SolutionSet population = algorithm.execute();
		long estimatedTime = System.currentTimeMillis() - initTime;
		if(SAS.isTest) { 
		logger_.setLevel(Level.CONFIG);
		logger_.log(Level.CONFIG, "Total execution time: " + estimatedTime + "ms");
		
		String str = "data/IBEA/SAS";
		
		Utils.deleteFolder(new File(str+ "/results.dat"));
		Utils.createFolder(str);
		
		population.printObjectivesToFile(str + "/results.dat");
		}
		
		org.femosaa.util.Logger.logSolutionSet(population, "SolutionSet.rtf");
		
		return population;

	} 

	

	protected Solution findSoleSolutionAfterEvolution(SolutionSet pareto_front) {
		// find the knee point
		Solution individual = pareto_front.get(PseudoRandom.randInt(0, pareto_front.size() - 1)); 
			
		
		for (int i = 0; i < problem.getNumberOfObjectives(); i++)
			System.out.print(individual.getObjective(i) + "\n");
		
		
		String str = "data/IBEA/SAS";
		if(SAS.isTest) 
		Utils.deleteFolder(new File(str+ "/knee_results.dat"));
		SolutionSet set = new SolutionSet(1);
		set.add(individual);
		if(SAS.isTest) 
		set.printObjectivesToFile(str + "/knee_results.dat");
		
		return individual;
	}

	protected SolutionSet doRanking(SolutionSet population) {
		// TODO Auto-generated method stub
		return ((IBEA_SAS)algorithm).doRanking(population);
	}
	
} // IBEA_main.java
