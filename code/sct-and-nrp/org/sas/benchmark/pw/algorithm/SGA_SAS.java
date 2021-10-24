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

import org.femosaa.core.SASAlgorithmAdaptor;

import jmetal.core.*;
import jmetal.util.comparators.CrowdingComparator;
import jmetal.util.*;

/**
 * 
 * @author keli, taochen
 *
 */
public class SGA_SAS extends Algorithm {


	// ideal point
	double[] z_;

	// nadir point
	double[] nz_;
	
	int populationSize_;
	
	SolutionSet population_;
	
	double[] weights = new double[0];
	
	// This to be used with single objective only and without any fuzzy setting
	double[][] fixed_bounds = null;
	/**
	 * Constructor
	 * @param problem Problem to solve
	 */
	public SGA_SAS(Problem problem, double[] weights , double[][] fixed_bounds) {
		super (problem) ;
		this.weights = weights;
		this.fixed_bounds = fixed_bounds;
	} // NSGAII



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

	
		SolutionSet population;
		SolutionSet offspringPopulation;
		SolutionSet union;

		Operator mutationOperator;
		Operator crossoverOperator;
		Operator selectionOperator;

		Distance distance = new Distance();
		
		if(getInputParameter("fixed_bounds") != null) {
			fixed_bounds = (double[][])getInputParameter("fixed_bounds");
		}

		//Read the parameters
		populationSize = ((Integer) getInputParameter("populationSize")).intValue();
		maxEvaluations = ((Integer) getInputParameter("maxEvaluations")).intValue();
		//weights = (double[])getInputParameter("weights");

		
		//Initialize the variables
		population = new SolutionSet(populationSize);
		populationSize_ = populationSize;
		population_ = population;
		evaluations = 0;

		requiredEvaluations = 0;

		//Read the operators
		mutationOperator  = operators_.get("mutation");
		crossoverOperator = operators_.get("crossover");
		selectionOperator = operators_.get("selection");

		
		z_  = new double[problem_.getNumberOfObjectives()];
	    nz_ = new double[problem_.getNumberOfObjectives()];
		
		
		// Create the initial solutionSet
		
		for (int i = 0; i < populationSize; i++) {
			Solution newSolution = new Solution(problem_);
			problem_.evaluate(newSolution);
			problem_.evaluateConstraints(newSolution);
			evaluations++;
			population.add(newSolution);
		} //for           

		initIdealPoint();
		initNadirPoint();
		
		SolutionSet old_population = new SolutionSet(populationSize);
	
		
		for (int i = 0; i < populationSize; i++) {
			fitnessAssignment(population.get(i));	// assign fitness value to each solution			
		}
		
		if (SASAlgorithmAdaptor.logGenerationOfObjectiveValue > 0) {			
			if(SASAlgorithmAdaptor.isFuzzy) {
				org.femosaa.util.Logger.logSolutionSetWithGenerationAndFuzzyValue(population, old_population,
						"SolutionSetWithGen.rtf", 0);
			} else {
				org.femosaa.util.Logger.logSolutionSetWithGeneration(population, "SolutionSetWithGen.rtf", 
						0);
			}
		} 
		double te = 0.0;
		// Generations 
		while (evaluations < maxEvaluations) {

			// Create the offSpring solutionSet
			offspringPopulation = new SolutionSet(populationSize);
			Solution[] parents = new Solution[2];
			for (int i = 0; i < (populationSize / 2); i++) {
				if (evaluations < maxEvaluations) {
					//obtain parents
					// First round, how to ensure normalization?
					parents[0] = (Solution) selectionOperator.execute(population);
					parents[1] = (Solution) selectionOperator.execute(population);
					Solution[] offSpring = (Solution[]) crossoverOperator.execute(parents);
					mutationOperator.execute(offSpring[0]);
					mutationOperator.execute(offSpring[1]);
					problem_.evaluate(offSpring[0]);
					problem_.evaluateConstraints(offSpring[0]);
					problem_.evaluate(offSpring[1]);
					problem_.evaluateConstraints(offSpring[1]);
					
					if(SASAlgorithmAdaptor.isLogToD && (offSpring[0].getObjective(0) <= SASAlgorithmAdaptor.d || 
							offSpring[1].getObjective(0) <= SASAlgorithmAdaptor.d) && te == 0.0) {
						System.out.print("Found one with " + evaluations + "\n");
						te = evaluations; 
					}
					
					updateReference(offSpring[0]);
					updateNadirPoint(offSpring[0]);
					
					updateReference(offSpring[1]);
					updateNadirPoint(offSpring[1]);
					
//					fitnessAssignment(offSpring[0]);
//					fitnessAssignment(offSpring[1]);
					offspringPopulation.add(offSpring[0]);
					offspringPopulation.add(offSpring[1]);
					evaluations += 2;
				} // if                            
			} // for

			SolutionSet old_union = null;
			// Create the solutionSet union of solutionSet and offSpring			
			if(SASAlgorithmAdaptor.isFuzzy) {			
				union = ((SolutionSet) old_population).union(offspringPopulation);
				old_union = union;
			} else {
				union = ((SolutionSet) population).union(offspringPopulation);
			}
			
			// Environmental selection based on the fitness value of each solution 
			int[] idxArray = new int[union.size()];
			double[] pData = new double[union.size()];
			for (int i = 0; i < union.size(); i++) {	// Is there a faster way?
				fitnessAssignment(union.get(i));
				idxArray[i] = i;
				pData[i]    = union.get(i).getFitness();
			}
			Utils.QuickSort(pData, idxArray, 0, union.size() - 1); 
			
			population.clear();
			for (int i = 0; i < populationSize; i++)
				population.add(union.get(idxArray[i]));
			
		
			
			if(SASAlgorithmAdaptor.logGenerationOfObjectiveValue > 0 && evaluations%SASAlgorithmAdaptor.logGenerationOfObjectiveValue == 0) {
				if(SASAlgorithmAdaptor.isFuzzy) {
					org.femosaa.util.Logger.logSolutionSetWithGenerationAndFuzzyValue(population, old_population, "SolutionSetWithGen.rtf", 
							evaluations );
				} else {
					org.femosaa.util.Logger.logSolutionSetWithGeneration(population, "SolutionSetWithGen.rtf", 
							evaluations );
				}								
			}

		} // while

		if(SASAlgorithmAdaptor.isFuzzy) {
			population = old_population;
		}
		// Return as output parameter the required evaluations
		setOutputParameter("evaluations", requiredEvaluations);
		
		if(SASAlgorithmAdaptor.isLogToD) {
			   System.out.print("Minimum evaluation " + te + "\n");
			   org.femosaa.util.Logger.logFirstTod(te, "FirstToD.rtf");
		}
			
		
		return population;
	} // execute
	
	public SolutionSet doRanking(SolutionSet population){
		SolutionSet set = new SolutionSet(1);
		set.add(population.get(0));
		/*for (int i = 0; i < population.size(); i++) {
		
			//System.out.print("Fitness: " + population.get(i).getFitness()+ ", obj: " + population.get(i).getObjective(0)+ ":"+  population.get(i).getObjective(1)+ "\n");
			Solution cur_solution = population.get(i);
			double cur_fitness = 0.0;
			for (int k = 0; k < problem_.getNumberOfObjectives(); k++) {
				
				if(cur_solution.getObjective(k) == Double.MAX_VALUE/100) {
					cur_fitness += weights[k] * 1.0;
					//System.out.print(cur_fitness + " Find one fitness with MAX_VALUE!\n");
				} else {
				
				cur_fitness += weights[k] * (nz_[k] != z_[k]? ((cur_solution.getObjective(k) - z_[k]) / (nz_[k] - z_[k])) : 
					((cur_solution.getObjective(k) - z_[k]) / (nz_[k])));
			 }
			}
			System.out.print("Fitness: " + cur_fitness+ ", obj: " + population.get(i).getObjective(0)+ ":"+  population.get(i).getObjective(1)+ "\n");
			
		
		}*/
		//System.out.print("weights "+weights[0] + ":"+ weights[1]+"\n");
		//System.out.print(nz_[0]+"-"+z_[0]+"+"+nz_[1]+"-"+z_[1]+"\n");
		return set;
	}
	
	
	/**
	 * This is used to assign fitness value to a solution, according to weighted sum strategy.
	 * @param cur_solution
	 */
	public void fitnessAssignment(Solution cur_solution) {
		double cur_fitness = 0.0;
		// double weight = 1.0 / (double) problem_.getNumberOfObjectives();

		for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {

			if (SASAlgorithmAdaptor.isWeightedSumNormalized) {

				if (fixed_bounds != null) {

					if (fixed_bounds[0][0] == 0 && fixed_bounds[0][1] == 0 && fixed_bounds[1][0] == 0
							&& fixed_bounds[1][1] == 0) {
						if(cur_solution.getObjective(i) < 0) {
							cur_fitness += weights[i] * (cur_solution.getObjective(i)/(cur_solution.getObjective(i)-1));

						} else {
							cur_fitness += weights[i] * (cur_solution.getObjective(i)/(cur_solution.getObjective(i)+1));
						}
					} else if (fixed_bounds[0][0] == -1 && fixed_bounds[0][1] == -1 && fixed_bounds[1][0] == -1
							&& fixed_bounds[1][1] == -1) {
						cur_fitness += weights[i] * cur_solution.getObjective(i);
					} else {
						if (cur_solution.getObjective(i) == Double.MAX_VALUE / 100) {
							cur_fitness += weights[i] * 1.0;
							// System.out.print(cur_fitness + " Find one fitness with MAX_VALUE!\n");
						} else {

							cur_fitness += weights[i]
									* (nz_[i] != z_[i] ? ((cur_solution.getObjective(i) - z_[i]) / (nz_[i] - z_[i]))
											: ((cur_solution.getObjective(i) - z_[i]) / (nz_[i])));
						}
					}

				} else {

					if (cur_solution.getObjective(i) == Double.MAX_VALUE / 100) {
						cur_fitness += weights[i] * 1.0;
						// System.out.print(cur_fitness + " Find one fitness with MAX_VALUE!\n");
					} else {

						cur_fitness += weights[i]
								* (nz_[i] != z_[i] ? ((cur_solution.getObjective(i) - z_[i]) / (nz_[i] - z_[i]))
										: ((cur_solution.getObjective(i) - z_[i]) / (nz_[i])));
					}
				}
			} else {
				cur_fitness += weights[i] * cur_solution.getObjective(i);
			}

		}

		if(Double.isNaN(cur_fitness)) {
			System.out.print("Find one fitness with NaN!\n");
			cur_fitness = 0;//-1.0e+30;
		}
		cur_solution.setFitness(cur_fitness);
	}
	

  	/**
  	 * Initialize the ideal point
	 * @throws JMException
	 * @throws ClassNotFoundException
	 */
	void initIdealPoint() throws JMException, ClassNotFoundException {
		if(SASAlgorithmAdaptor.isFuzzy) {
			for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
				z_[i] = 0;
			}
			return;
		}
		
		if(fixed_bounds != null) {
			for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
				z_[i] = fixed_bounds[i][0];
			}
			return;
		}
		
		for (int i = 0; i < problem_.getNumberOfObjectives(); i++)
			z_[i] = 1.0e+30;

		for (int i = 0; i < populationSize_; i++)
			updateReference(population_.get(i));
	}

	/**
	 * Initialize the nadir point
	 * @throws JMException
	 * @throws ClassNotFoundException
	 */
	void initNadirPoint() throws JMException, ClassNotFoundException {
		if(SASAlgorithmAdaptor.isFuzzy) {
			for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
				nz_[i] = 1;
			}
			return;
		}
		
		if(fixed_bounds != null) {
			for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
				nz_[i] = fixed_bounds[i][1];
			}
			return;
		}
		
		for (int i = 0; i < problem_.getNumberOfObjectives(); i++)
			nz_[i] = -1.0e+30;

		for (int i = 0; i < populationSize_; i++)
			updateNadirPoint(population_.get(i));
	}
	
   	/**
   	 * Update the ideal point, it is just an approximation with the best value for each objective
   	 * @param individual
   	 */
	void updateReference(Solution individual) {
		if(SASAlgorithmAdaptor.isFuzzy) {
			return;
		}
		
		
		if(fixed_bounds != null) {
			return;
		}
		
		for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
			if (individual.getObjective(i) < z_[i])
				z_[i] = individual.getObjective(i);
		}
	}
  
  	/**
  	 * Update the nadir point, it is just an approximation with worst value for each objective
  	 * 
  	 * @param individual
  	 */
	void updateNadirPoint(Solution individual) {
		if(SASAlgorithmAdaptor.isFuzzy) {
			return;
		}
		
		
		if(fixed_bounds != null) {
			return;
		}
		
		
		for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
			if (individual.getObjective(i) != Double.MAX_VALUE/100 && individual.getObjective(i) > nz_[i])
				nz_[i] = individual.getObjective(i);
		}
	}
	
	/**
	 * This is used to find the knee point from a set of solutions
	 * 
	 * @param population
	 * @return
	 */
//	public Solution kneeSelection(SolutionSet population_) {		
//		int[] max_idx    = new int[problem_.getNumberOfObjectives()];
//		double[] max_obj = new double[problem_.getNumberOfObjectives()];
//		int populationSize_ = population_.size();
//		// finding the extreme solution for f1
//		for (int i = 0; i < populationSize_; i++) {
//			for (int j = 0; j < problem_.getNumberOfObjectives(); j++) {
//				// search the extreme solution for f1
//				if (population_.get(i).getObjective(j) > max_obj[j]) {
//					max_idx[j] = i;
//					max_obj[j] = population_.get(i).getObjective(j);
//				}
//			}
//		}
//
//		if (max_idx[0] == max_idx[1])
//			System.out.println("Watch out! Two equal extreme solutions cannot happen!");
//		
//		int maxIdx;
//		double maxDist;
//		double temp1 = (population_.get(max_idx[1]).getObjective(0) - population_.get(max_idx[0]).getObjective(0)) * 
//				(population_.get(max_idx[0]).getObjective(1) - population_.get(0).getObjective(1)) - 
//				(population_.get(max_idx[0]).getObjective(0) - population_.get(0).getObjective(0)) * 
//				(population_.get(max_idx[1]).getObjective(1) - population_.get(max_idx[0]).getObjective(1));
//		double temp2 = Math.pow(population_.get(max_idx[1]).getObjective(0) - population_.get(max_idx[0]).getObjective(0), 2.0) + 
//				Math.pow(population_.get(max_idx[1]).getObjective(1) - population_.get(max_idx[0]).getObjective(1), 2.0);
//		double constant = Math.sqrt(temp2);
//		double tempDist = Math.abs(temp1) / constant;
//		maxIdx  = 0;
//		maxDist = tempDist;
//		for (int i = 1; i < populationSize_; i++) {
//			temp1 = (population_.get(max_idx[1]).getObjective(0) - population_.get(max_idx[0]).getObjective(0)) *
//					(population_.get(max_idx[0]).getObjective(1) - population_.get(i).getObjective(1)) - 
//					(population_.get(max_idx[0]).getObjective(0) - population_.get(i).getObjective(0)) * 
//					(population_.get(max_idx[1]).getObjective(1) - population_.get(max_idx[0]).getObjective(1));
//			tempDist = Math.abs(temp1) / constant;
//			if (tempDist > maxDist) {
//				maxIdx  = i;
//				maxDist = tempDist;
//			}
//		}
//		
//		return population_.get(maxIdx);
//	}
} // NSGA-II
