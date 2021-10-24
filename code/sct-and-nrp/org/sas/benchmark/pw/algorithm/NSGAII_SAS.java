//  NSGAII.java
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

import java.util.Iterator;

import org.femosaa.core.SASAlgorithmAdaptor;
import org.femosaa.core.SASSolutionInstantiator;
import org.femosaa.seed.Seeder;

import jmetal.core.*;
import jmetal.util.comparators.CrowdingComparator;
import jmetal.util.*;

/**
 * 
 * @author keli, taochen
 *
 */
public class NSGAII_SAS extends Algorithm {

	private Seeder seeder = null;
	SolutionSet population_;

	SASSolutionInstantiator factory;
	
	/**
	 * Constructor
	 * @param problem Problem to solve
	 */
	public NSGAII_SAS(Problem problem) {
		super (problem) ;
	} // NSGAII
	
	public NSGAII_SAS(Problem problem, SASSolutionInstantiator factory) {
		super(problem);
        this.factory = factory;
	}

  

	/**   
	 * Runs the NSGA-II algorithm.
	 * @return a <code>SolutionSet</code> that is a set of non dominated solutions
	 * as a result of the algorithm execution
	 * @throws JMException 
	 */
	public SolutionSet execute() throws JMException, ClassNotFoundException {
		
		
		
		int type;
		
		int populationSize;
		int maxEvaluations;
		int evaluations;

		int requiredEvaluations; // Use in the example of use of the
		// indicators object (see below)

		// knee point which might be used as the output
	
		if(getInputParameter("seeder") != null) {
			seeder = (Seeder)getInputParameter("seeder");
		}
		SolutionSet population;
		SolutionSet offspringPopulation;
		SolutionSet union;

		Operator mutationOperator;
		Operator crossoverOperator;
		Operator selectionOperator;

		Distance distance = new Distance();

		//Read the parameters
		populationSize = ((Integer) getInputParameter("populationSize")).intValue();
		maxEvaluations = ((Integer) getInputParameter("maxEvaluations")).intValue();

		//Initialize the variables
		population = new SolutionSet(populationSize);
		evaluations = 0;

		requiredEvaluations = 0;

		//Read the operators
		mutationOperator  = operators_.get("mutation");
		crossoverOperator = operators_.get("crossover");
		selectionOperator = operators_.get("selection");

	
		for (int i = 0; i < populationSize; i++) {
			Solution newSolution = new Solution(problem_);
			problem_.evaluate(newSolution);
			problem_.evaluateConstraints(newSolution);
			evaluations++;
			population.add(newSolution);
		} //for    
		
		
		SolutionSet old_population = new SolutionSet(populationSize);
	
		if(SASAlgorithmAdaptor.isFuzzy) {
			old_population = population;
			population = factory.fuzzilize(population);
		}
	      
		if (SASAlgorithmAdaptor.logGenerationOfObjectiveValue > 0) {
			org.femosaa.util.Logger.logSolutionSetWithGeneration(population,
					"InitialSolutionSet.rtf", 0);
		}

	
		
		long time = Long.MAX_VALUE;
		
		// Generations 
		while (evaluations < maxEvaluations || (evaluations >= maxEvaluations && (System.currentTimeMillis() - time) < SASAlgorithmAdaptor.seed_time )) {
			//System.out.print("no" + evaluations + "***eval\n");
		
//
//			Iterator itr = population.iterator();
//			double no = 0.0;
//			while(itr.hasNext()) {
//				Solution s = (Solution)itr.next();
//				if(((SASSolution)s).isFromInValid) {
//					no++;
//				}
//			}
//			System.out.print("("+evaluations+","+(no/population.size()) + ")\n");
			// Create the offSpring solutionSet      
			offspringPopulation = new SolutionSet(populationSize);
			Solution[] parents = new Solution[2];		
			//for (int i = 0; i < (populationSize / 2); i++) {
			while (offspringPopulation.size() < populationSize) {
				int c = 2;
				if (evaluations < maxEvaluations || (evaluations >= maxEvaluations)) {
					Solution[] offSpring = null;
					//obtain parents			
				
				
					parents[0] = (Solution) selectionOperator.execute(population);
					parents[1] = (Solution) selectionOperator.execute(population);
				
					//offSpring  = new Solution[2];
					//offSpring[0] = factory.getSolution(parents[0]);
					//offSpring[1] = factory.getSolution(parents[1]);
				
					offSpring = (Solution[]) crossoverOperator.execute(parents);
					
					
					mutationOperator.execute(offSpring[0]);
					mutationOperator.execute(offSpring[1]);
					
					//long test_time = System.currentTimeMillis();
					problem_.evaluate(offSpring[0]);
					problem_.evaluateConstraints(offSpring[0]);
					problem_.evaluate(offSpring[1]);
					problem_.evaluateConstraints(offSpring[1]);
					//System.out.print("Evaluation time: " + (System.currentTimeMillis()-test_time) + "\n");
					if(c == 0) {
						continue;
					}
					if(offspringPopulation.size() >= populationSize) {
						break;
					}
					offspringPopulation.add(offSpring[0]);
					evaluations++;
					c--;
					if(c == 0) {
						continue;
					}
					if(offspringPopulation.size() >= populationSize) {
						break;
					}
					offspringPopulation.add(offSpring[1]);
					evaluations++;
				
					
				} // if                            
			} // for
			
			SolutionSet old_union = null;
			// Create the solutionSet union of solutionSet and offSpring			
			if(SASAlgorithmAdaptor.isFuzzy) {			
				union = ((SolutionSet) old_population).union(offspringPopulation);
				old_union = union;
				union = factory.fuzzilize(union);
			} else {
				union = ((SolutionSet) population).union(offspringPopulation);
			}
			// Create the solutionSet union of solutionSet and offSpring
			//union = ((SolutionSet) population).union(offspringPopulation);
			
			// Ranking the union
			Ranking ranking = new Ranking(union);

			int remain = populationSize;
			int index = 0;
			SolutionSet front = null;
			population.clear();
			old_population.clear();
			
			// Obtain the next front
			front = ranking.getSubfront(index);
			
			while ((remain > 0) && (remain >= front.size())) {
				//Assign crowding distance to individuals
				distance.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
				//Add the individuals of this front
				for (int k = 0; k < front.size(); k++) {
					population.add(front.get(k));
					if(SASAlgorithmAdaptor.isFuzzy) {
						old_population.add(factory.defuzzilize(front.get(k), old_union));
					}
				} // for

				//Decrement remain
				remain = remain - front.size();

				//Obtain the next front
				index++;
				if (remain > 0) {
					front = ranking.getSubfront(index);
				} // if        
			} // while

			// Remain is less than front(index).size, insert only the best one
			if (remain > 0) {  // front contains individuals to insert                        
				distance.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
				front.sort(new CrowdingComparator());
				for (int k = 0; k < remain; k++) {
					population.add(front.get(k));
					if(SASAlgorithmAdaptor.isFuzzy) {
						old_population.add(factory.defuzzilize(front.get(k), old_union));
					}
				} // for

				remain = 0;
			} // if                               
		
			if(SASAlgorithmAdaptor.isLogTheEvalNeededToRemiveNonSeed) {
				org.femosaa.util.Logger.printMarkedSolution(population, evaluations);
			}
		
			
			if(SASAlgorithmAdaptor.logGenerationOfObjectiveValue > 0 && evaluations%SASAlgorithmAdaptor.logGenerationOfObjectiveValue == 0) {
				org.femosaa.util.Logger.logSolutionSetWithGeneration(population, "SolutionSetWithGen.rtf", 
						evaluations );
				//org.femosaa.util.Logger.logSolutionSetValuesWithGen(population, "SolutionSetValuesWithGen.rtf", 
						//evaluations );
			}
			
			if(SASAlgorithmAdaptor.isLogDiscardedSolutions) {
				SolutionSet p = new SolutionSet(union.size() - population.size());
			
				for (int k = 0; k < union.size(); k++) {
					boolean has = false;
					for (int i = 0; i < population.size(); i++) {
						if(union.get(k).equals(population.get(i))){
							has = true;
							break;
						}
					}
					
					if(!has) {
						p.add(union.get(k));
					}
				}
				
				org.femosaa.util.Logger.logSolutionSetWithGeneration(p, "DiscardSolutionSetWithGen.rtf", 
						evaluations );
				//org.femosaa.util.Logger.logSolutionSetValuesWithGen(p, "DiscardSolutionSetValuesWithGen.rtf", 
						//evaluations );
				
			}
			
			if(evaluations >= maxEvaluations && time == Long.MAX_VALUE) {
				time = System.currentTimeMillis();
			}
			
			
			if(factory instanceof org.sas.benchmark.sm.nrp.Factory) {
				((org.sas.benchmark.sm.nrp.Factory)factory).map.clear();
				
			}
		} // while
//		Iterator itr = population.iterator();
//		double no = 0.0;
//		while(itr.hasNext()) {
//			Solution s = (Solution)itr.next();
//			if(((SASSolution)s).isFromInValid) {
//				no++;
//			}
//		}
//		System.out.print("("+evaluations+","+(no/population.size()) + ")\n");
		if(SASAlgorithmAdaptor.isFuzzy) {
			population = old_population;
		}
		// Return as output parameter the required evaluations
		setOutputParameter("evaluations", requiredEvaluations);
		population_ = population;
		
		// Return the first non-dominated front
//		Ranking ranking = new Ranking(population);
//		return ranking.getSubfront(0);
		return population;
	} // execute
	
	
	public SolutionSet doRanking(SolutionSet population){
		Ranking ranking = new Ranking(population);
		return ranking.getSubfront(0);
	}
	
	/**
	 * This is used to find the knee point from a set of solutions
	 * 
	 * @param population
	 * @return
	 */
	public Solution kneeSelection(SolutionSet population_) {		
		int[] max_idx    = new int[problem_.getNumberOfObjectives()];
		double[] max_obj = new double[problem_.getNumberOfObjectives()];
		int populationSize_ = population_.size();
		// finding the extreme solution for f1
		for (int i = 0; i < populationSize_; i++) {
			for (int j = 0; j < problem_.getNumberOfObjectives(); j++) {
				// search the extreme solution for f1
				if (population_.get(i).getObjective(j) > max_obj[j]) {
					max_idx[j] = i;
					max_obj[j] = population_.get(i).getObjective(j);
				}
			}
		}

		if (max_idx[0] == max_idx[1])
			System.out.println("Watch out! Two equal extreme solutions cannot happen!");
		
		int maxIdx;
		double maxDist;
		double temp1 = (population_.get(max_idx[1]).getObjective(0) - population_.get(max_idx[0]).getObjective(0)) * 
				(population_.get(max_idx[0]).getObjective(1) - population_.get(0).getObjective(1)) - 
				(population_.get(max_idx[0]).getObjective(0) - population_.get(0).getObjective(0)) * 
				(population_.get(max_idx[1]).getObjective(1) - population_.get(max_idx[0]).getObjective(1));
		double temp2 = Math.pow(population_.get(max_idx[1]).getObjective(0) - population_.get(max_idx[0]).getObjective(0), 2.0) + 
				Math.pow(population_.get(max_idx[1]).getObjective(1) - population_.get(max_idx[0]).getObjective(1), 2.0);
		double constant = Math.sqrt(temp2);
		double tempDist = Math.abs(temp1) / constant;
		maxIdx  = 0;
		maxDist = tempDist;
		for (int i = 1; i < populationSize_; i++) {
			temp1 = (population_.get(max_idx[1]).getObjective(0) - population_.get(max_idx[0]).getObjective(0)) *
					(population_.get(max_idx[0]).getObjective(1) - population_.get(i).getObjective(1)) - 
					(population_.get(max_idx[0]).getObjective(0) - population_.get(i).getObjective(0)) * 
					(population_.get(max_idx[1]).getObjective(1) - population_.get(max_idx[0]).getObjective(1));
			tempDist = Math.abs(temp1) / constant;
			if (tempDist > maxDist) {
				maxIdx  = i;
				maxDist = tempDist;
			}
		}
		
		return population_.get(maxIdx);
	}
} // NSGA-II
