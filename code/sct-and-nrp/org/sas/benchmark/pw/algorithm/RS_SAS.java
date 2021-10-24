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

import java.util.Random;

import org.femosaa.core.EAConfigure;
import org.femosaa.core.SASAlgorithmAdaptor;
import org.femosaa.core.SASSolution;
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
public class RS_SAS extends Algorithm {


	// ideal point
	double[] z_;

	// nadir point
	double[] nz_;
	
	int populationSize_;
	private Seeder seeder = null;
	SolutionSet population_;
	double[] weights = new double[0];
	// This to be used with single objective only and without any fuzzy setting
	double[][] fixed_bounds = null;
	/**
	 * Constructor
	 * @param problem Problem to solve
	 */
	public RS_SAS(Problem problem) {
		super (problem) ;
	} // NSGAII


  	/**
  	 * Constructor
  	 * @param problem Problem to solve
  	 */
	public RS_SAS(Problem problem, double[] weights , double[][] fixed_bounds) {
		super (problem) ;
		this.weights = weights;
		this.fixed_bounds = fixed_bounds;
	}

	/**   
	 * Runs the NSGA-II algorithm.
	 * @return a <code>SolutionSet</code> that is a set of non dominated solutions
	 * as a result of the algorithm execution
	 * @throws JMException 
	 */
	public SolutionSet execute() throws JMException, ClassNotFoundException {
		
		
		if (getInputParameter("seeder") != null) {
			seeder = (Seeder) getInputParameter("seeder");
		}
		if(getInputParameter("fixed_bounds") != null) {
			fixed_bounds = (double[][])getInputParameter("fixed_bounds");
		}
		
		int maxEvaluations  = ((Integer) getInputParameter("maxEvaluations")).intValue();
		int evaluations;
		//weights = (double[])getInputParameter("weights");


		z_  = new double[problem_.getNumberOfObjectives()];
	    nz_ = new double[problem_.getNumberOfObjectives()];

		int populationSize = 2;
		//Initialize the variables
		SolutionSet population = new SolutionSet(populationSize);
		//SolutionSet nadir_population = new SolutionSet(populationSize);
		
		evaluations = 0;
		int measurement = 0;
	

		initIdealPoint();
		initNadirPoint();
		// Create the initial solutionSet
		Solution newSolution;
		if (seeder != null) {
		} else {
			// Create the initial solutionSet
			for (int i = 0; i < 1; i++) {
				newSolution = new Solution(problem_);
				problem_.evaluate(newSolution);
				problem_.evaluateConstraints(newSolution);
				evaluations++;
				measurement++;
				population.add(newSolution);
				fitnessAssignment(newSolution);
			} // for
		}     

	
		
		SolutionSet old_population = new SolutionSet(populationSize);
		if(SASAlgorithmAdaptor.isFuzzy) {
		}
		
		for (int i = 0; i < population.size(); i++) {
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
		
		Random rand = new Random();
		double te = 0.0;
		
		// Generations 
		while (evaluations < maxEvaluations) {

		
	
	
			
	
			/**
			 * This is a random search, uniformly searching over the variable vector.
			 */

			Solution nextSolution = new Solution(population.get(0));
			
			//int index = rand.nextInt(nextSolution.getDecisionVariables().length);
			for (int i = 0; i < nextSolution.getDecisionVariables().length; i++) {
				nextSolution.getDecisionVariables()[i].setValue(PseudoRandom.randInt(0, 1));
			}
			
			
			
			
		
			
			problem_.evaluate(nextSolution);
			problem_.evaluateConstraints(nextSolution);
			
			if(SASAlgorithmAdaptor.isLogToD && nextSolution.getObjective(0) <= SASAlgorithmAdaptor.d && te == 0.0) {
				System.out.print("Found one with " + evaluations + "\n");
				te = evaluations; 
			}
			
			
			updateReference(nextSolution);
			updateNadirPoint(nextSolution);
		
			
			
			if(SASAlgorithmAdaptor.isFuzzy) {
			} else {
				population.add(nextSolution);
			}
			
			fitnessAssignment(population.get(0));
			fitnessAssignment(population.get(1));
			
			
			
			
			if(population.get(0).getFitness() < population.get(1).getFitness() ) {
				Solution s = population.get(0);
				population.clear();
				population.add(s);
				if(SASAlgorithmAdaptor.isFuzzy) {
				old_population.remove(1);
				}
				
			} else {
				Solution s = population.get(1);
				population.clear();
				population.add(s);
				if(SASAlgorithmAdaptor.isFuzzy) {
				old_population.remove(0);
				}
			}
			
			evaluations++;
			measurement++;
			if(SASAlgorithmAdaptor.logGenerationOfObjectiveValue > 0 && evaluations%SASAlgorithmAdaptor.logGenerationOfObjectiveValue == 0) {
				if(SASAlgorithmAdaptor.isFuzzy) {
					org.femosaa.util.Logger.logSolutionSetWithGenerationAndFuzzyValue(population, old_population,
							"SolutionSetWithGen.rtf", evaluations);
				} else {
					org.femosaa.util.Logger.logSolutionSetWithGeneration(population, "SolutionSetWithGen.rtf", 
							evaluations);
				}
			}
			
			if (SASAlgorithmAdaptor.logMeasurementOfObjectiveValue) {
				if(SASAlgorithmAdaptor.isFuzzy) {
					org.femosaa.util.Logger.logSolutionSetWithGeneration(old_population, "SolutionSetWithMeasurement.rtf", 
							measurement );
				} else {
					org.femosaa.util.Logger.logSolutionSetWithGeneration(population, "SolutionSetWithMeasurement.rtf", 
							measurement );
				}
				
			}

			//System.out.print("Measurement: " + measurement + "\n");
			//System.out.print("Eval: " + evaluations + "\n");
			
			
			if (EAConfigure.getInstance().measurement == measurement) {
				break;
			}
		
		} // while

		if(SASAlgorithmAdaptor.isFuzzy) {
			population = old_population;
		}
		// Return as output parameter the required evaluations
		//setOutputParameter("evaluations", requiredEvaluations);
		
		if(SASAlgorithmAdaptor.isLogToD) {
			   System.out.print("Minimum evaluation " + te + "\n");
			   org.femosaa.util.Logger.logFirstTod(te, "FirstToD.rtf");
		}
			
		//System.out.print("Measurement: " + measurement + "\n");
		
		return population;
	} // execute
	
	public SolutionSet doRanking(SolutionSet population){
		SolutionSet set = new SolutionSet(1);
		set.add(population.get(0));
		
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
	 * 
	 * @throws JMException
	 * @throws ClassNotFoundException
	 */
	void initIdealPoint() throws JMException, ClassNotFoundException {
		if (SASAlgorithmAdaptor.isFuzzy) {
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
	 * 
	 * @throws JMException
	 * @throws ClassNotFoundException
	 */
	void initNadirPoint() throws JMException, ClassNotFoundException {
		if (SASAlgorithmAdaptor.isFuzzy) {
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
	 * Update the ideal point, it is just an approximation with the best value for
	 * each objective
	 * 
	 * @param individual
	 */
	void updateReference(Solution individual) {
		if (SASAlgorithmAdaptor.isFuzzy) {
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
	 * Update the nadir point, it is just an approximation with worst value for each
	 * objective
	 * 
	 * @param individual
	 */
	void updateNadirPoint(Solution individual) {
		if (SASAlgorithmAdaptor.isFuzzy) {
			return;
		}
		
		if(fixed_bounds != null) {
			return;
		}
		
		for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
			if (individual.getObjective(i) > nz_[i])
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
