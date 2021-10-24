/**
 * NSGAII_SAS_main.java
 * 
 * @author Ke Li <keli.genius@gmail.com>
 * 
 * Copyright (c) 2016 Ke Li
 * 
 * Note: This is a free software developed based on the open source project 
 * jMetal<http://jmetal.sourceforge.net>. The copy right of jMetal belongs to 
 * its original authors, Antonio J. Nebro and Juan J. Durillo. Nevertheless, 
 * this current version can be redistributed and/or modified under the terms of 
 * the GNU Lesser General Public License as published by the Free Software 
 * Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  
 */

package org.sas.benchmark.pw.algorithm;

import jmetal.core.*;
import jmetal.encodings.solutionType.BinarySolutionType;
import jmetal.operators.crossover.*;
import jmetal.operators.mutation.*;
import jmetal.operators.selection.*;
import jmetal.problems.*;
import jmetal.problems.DTLZ.*;
import jmetal.problems.ZDT.*;
import jmetal.problems.cec2009Competition.*;
import jmetal.problems.WFG.*;

import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.femosaa.core.EAConfigure;
import org.femosaa.core.SASSolutionInstantiator;
import org.femosaa.core.SASSolutionType;
import org.sas.benchmark.pw.nrp.Parser;


public class NSGA2_SAS_main {
	public static Logger logger_; // Logger object
	public static FileHandler fileHandler_; // FileHandler object

	protected Problem problem; // The problem to solve
	protected Algorithm algorithm; // The algorithm to use
	Operator crossover; // Crossover operator
	Operator mutation; // Mutation operator
	Operator selection; // Selection operator
	SASSolutionInstantiator factory;
	/**
	 * @param args
	 *            Command line arguments. The first (optional) argument
	 *            specifies the problem to solve.
	 * @throws JMException
	 * @throws IOException
	 * @throws SecurityException
	 *             Usage: three options - jmetal.metaheuristics.moead.MOEAD_main
	 *             - jmetal.metaheuristics.moead.MOEAD_main problemName -
	 *             jmetal.metaheuristics.moead.MOEAD_main problemName
	 *             ParetoFrontFile
	 * @throws ClassNotFoundException
	 */
	
	public void setFactory(SASSolutionInstantiator factory){
		this.factory = factory;
	}
	
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
		
		algorithm = new NSGAII_SAS(problem, factory);

		// Algorithm parameters
		int popsize = EAConfigure.getInstance().pop_size;
		int generations = EAConfigure.getInstance().generation;
		algorithm.setInputParameter("populationSize", popsize);
		algorithm.setInputParameter("maxEvaluations", popsize * generations);
		
		// Crossover operator
		parameters = new HashMap();
		parameters.put("probability", EAConfigure.getInstance().crossover_rate);
		parameters.put("distributionIndex", 20.0);
		// This needs to change in testing.
	
		crossover = CrossoverFactory.getCrossoverOperator(
				"SinglePointCrossover", parameters);
		//SinglePointCrossover UniformCrossoverSAS

		
		
		// Mutation operator
		parameters = new HashMap();
		parameters.put("probability", EAConfigure.getInstance().mutation_rate);
		parameters.put("distributionIndex", 20.0);
		mutation = MutationFactory.getMutationOperator("BitFlipMutation",
				parameters);
		
	

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
		
		String str = "data/NSGAII/SAS";
		
		population.printObjectivesToFile(str + "/results.dat");
		}
		
		
		
		printSolutions(population, numberOfObjectives_);
				
		
		org.femosaa.util.Logger.logSolutionSet(population, "SolutionSet.rtf");
			//org.femosaa.util.Logger.logPercentageOfMarkedSolution(pareto_front, "HowManyFromSeeds.rtf");
		
		//org.femosaa.util.Logger.logSolutionSetValues(population, "SolutionSetValue.rtf");
		
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


} // NSGA2_SAS_main 
