/**
 * GP_SAS_main.java
 * 
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
import jmetal.metaheuristics.nsgaII.NSGAII_SAS;
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
import org.femosaa.core.SASAlgorithmAdaptor;

public class RS_SAS_main {
	public static Logger logger_; // Logger object
	public static FileHandler fileHandler_; // FileHandler object

	Problem problem; // The problem to solve
	Algorithm algorithm; // The algorithm to use
	Operator crossover; // Crossover operator
	Operator mutation; // Mutation operator
	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public SolutionSet findParetoFront(double[] weights, double[][] fixed_bounds, int vars,  int numberOfObjectives_, int numberOfConstraints_) throws JMException,
			SecurityException, IOException, ClassNotFoundException {
	
		HashMap parameters; // Operator parameters
		Operator selection;

		// Logger object and file to store log messages
		if(SAS.isTest) { 
		logger_ = Configuration.logger_;
		fileHandler_ = new FileHandler("MOEAD.log");
		logger_.addHandler(fileHandler_);
		}
		problem = new SAS(vars, numberOfObjectives_, numberOfConstraints_);

		algorithm = new RS_SAS(problem, weights, fixed_bounds);
	
		// Algorithm parameters
		int popsize = 100;
		int factor = 10;
		algorithm.setInputParameter("populationSize", EAConfigure.getInstance().pop_size);
		algorithm.setInputParameter("maxEvaluations", EAConfigure.getInstance().pop_size * EAConfigure.getInstance().generation);
		//algorithm.setInputParameter("weights", factory.getWeights());
	
		// Crossover operator
		parameters = new HashMap();
		parameters.put("probability", EAConfigure.getInstance().crossover_rate);
		parameters.put("distributionIndex", 20.0);
		// This needs to change in testing.
		
		crossover = CrossoverFactory.getCrossoverOperator("SinglePointCrossover", parameters);
		//SinglePointCrossover UniformCrossoverSAS

		// Mutation operator
		parameters = new HashMap();
		parameters.put("probability", EAConfigure.getInstance().mutation_rate);
		parameters.put("distributionIndex", 20.0);
		mutation = MutationFactory.getMutationOperator("BitFlipMutation", parameters);

		selection = SelectionFactory.getSelectionOperator("BinaryTournamentSAS", parameters);

		algorithm.addOperator("crossover", crossover);
		algorithm.addOperator("mutation", mutation);
		algorithm.addOperator("selection", selection);
		
		long initTime = System.currentTimeMillis();
		
		SolutionSet population = algorithm.execute();
		long estimatedTime = System.currentTimeMillis() - initTime;
		if(SAS.isTest) { 
		logger_.setLevel(Level.CONFIG);
		logger_.log(Level.CONFIG, "Total execution time: " + estimatedTime + "ms");
		
		String str = "data/SGA/SAS";
		
		Utils.deleteFolder(new File(str+ "/results.dat"));
		Utils.createFolder(str);
		
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
    }


} // MOEAD_main
