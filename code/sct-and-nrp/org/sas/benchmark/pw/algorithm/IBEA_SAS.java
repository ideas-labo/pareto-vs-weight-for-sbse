//  IBEA.java
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

import jmetal.core.*;
import jmetal.util.JMException;
import jmetal.util.Ranking;
import jmetal.util.comparators.DominanceComparator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.femosaa.core.SASAlgorithmAdaptor;
import org.femosaa.core.SASSolution;
import org.femosaa.core.SASSolutionInstantiator;
import org.femosaa.invalid.SASValidityAndInvalidityCoEvolver;
import org.femosaa.seed.Seeder;

/**
 * This class implementing the IBEA algorithm
 */
public class IBEA_SAS extends Algorithm {

	//private SASSolutionInstantiator factory = null;
	private Seeder seeder = null;
	private SASValidityAndInvalidityCoEvolver vandInvCoEvolver = null;
	
	SolutionSet population_;

	/**
	 * Defines the number of tournaments for creating the mating pool
	 */
	public static final int TOURNAMENTS_ROUNDS = 1;

	/**
	 * Stores the value of the indicator between each pair of solutions into the
	 * solution set
	 */
	private List<List<Double>> indicatorValues_;

	/**
   *
   */
	private double maxIndicatorValue_;

	/**
	 * Constructor. Create a new IBEA instance
	 * 
	 * @param problem
	 *            Problem to solve
	 */
	public IBEA_SAS(Problem problem) {
		super(problem);
	} // IBEA 
	
	/**
  	 * Constructor
  	 * @param problem Problem to solve
  	 */
	public IBEA_SAS(Problem problem, SASSolutionInstantiator factory) {
		super(problem);
       // this.factory = factory;
	}

	/**
	 * calculates the hypervolume of that portion of the objective space that is
	 * dominated by individual a but not by individual b
	 */
	double calcHypervolumeIndicator(Solution p_ind_a, Solution p_ind_b, int d,
			double maximumValues[], double minimumValues[]) {
		double a, b, r, max;
		double volume = 0;
		double rho = 2.0;

		r = rho * (maximumValues[d - 1] - minimumValues[d - 1]);
		max = minimumValues[d - 1] + r;

		a = p_ind_a.getObjective(d - 1) == Double.MAX_VALUE/100? maximumValues[d - 1] : p_ind_a.getObjective(d - 1);
		if (p_ind_b == null)
			b = max;
		else
			b = p_ind_b.getObjective(d - 1) == Double.MAX_VALUE/100? maximumValues[d - 1] : p_ind_b.getObjective(d - 1);

		if (d == 1) {
			if (a < b)
				volume = (b - a) / r;
			else
				volume = 0;
		} else {
			if (a < b) {
				volume = calcHypervolumeIndicator(p_ind_a, null, d - 1,
						maximumValues, minimumValues) * (b - a) / r;
				volume += calcHypervolumeIndicator(p_ind_a, p_ind_b, d - 1,
						maximumValues, minimumValues) * (max - b) / r;
			} else {
				volume = calcHypervolumeIndicator(p_ind_a, p_ind_b, d - 1,
						maximumValues, minimumValues) * (max - b) / r;
			}
		}

		return (volume);
	}

	/**
	 * This structure store the indicator values of each pair of elements
	 */
	public void computeIndicatorValuesHD(SolutionSet solutionSet,
			double[] maximumValues, double[] minimumValues) {
		SolutionSet A, B;
		// Initialize the structures
		indicatorValues_ = new ArrayList<List<Double>>();
		maxIndicatorValue_ = -Double.MAX_VALUE;

		for (int j = 0; j < solutionSet.size(); j++) {
			A = new SolutionSet(1);
			A.add(solutionSet.get(j));

			List<Double> aux = new ArrayList<Double>();
			for (int i = 0; i < solutionSet.size(); i++) {
				B = new SolutionSet(1);
				B.add(solutionSet.get(i));

				int flag = (new DominanceComparator()).compare(A.get(0),
						B.get(0));

				double value = 0.0;
				if (flag == -1) {
					value = -calcHypervolumeIndicator(A.get(0), B.get(0),
							problem_.getNumberOfObjectives(), maximumValues,
							minimumValues);
				} else {
					value = calcHypervolumeIndicator(B.get(0), A.get(0),
							problem_.getNumberOfObjectives(), maximumValues,
							minimumValues);
				}
				// double value =
				// epsilon.epsilon(matrixA,matrixB,problem_.getNumberOfObjectives());

				// Update the max value of the indicator
				if (Math.abs(value) > maxIndicatorValue_)
					maxIndicatorValue_ = Math.abs(value);
				aux.add(value);
			}
			indicatorValues_.add(aux);
		}
	} // computeIndicatorValues
	
	/**
	 * Compute the fitness values based on the epsilon-indicator
	 * @param solutionSet
	 * @param maximumValues
	 * @param minimumValues
	 */
	public void computeIndicatorValueEPS(SolutionSet solutionSet,
			double[] maximumValues, double[] minimumValues) {
		Solution A, B;
		
		// Initialize the structures
		indicatorValues_ = new ArrayList<List<Double>>();
		
		for (int i = 0; i < solutionSet.size(); i++) {
			A = new Solution(solutionSet.get(i));

			List<Double> aux = new ArrayList<Double>();
			for (int j = 0; j < solutionSet.size(); j++) {
				B = new Solution(solutionSet.get(j));
				
				double av = A.getObjective(0) == Double.MAX_VALUE/100? maximumValues[0] : A.getObjective(0);
				double bv = B.getObjective(0) == Double.MAX_VALUE/100? maximumValues[0] : B.getObjective(0);
				
				double r = (maximumValues[0] != minimumValues[0])? maximumValues[0] - minimumValues[0] : maximumValues[0];
				double eps = (av - minimumValues[0]) / r - (bv - minimumValues[0]) / r;
				for (int k = 1; k < problem_.getNumberOfObjectives(); k++) {
					double temp_eps;
					
					double aav = A.getObjective(k) == Double.MAX_VALUE/100? maximumValues[k] : A.getObjective(k);
					double bbv = B.getObjective(k) == Double.MAX_VALUE/100? maximumValues[k] : B.getObjective(k);
					
					r = (maximumValues[k] != minimumValues[k])? maximumValues[k] - minimumValues[k] : maximumValues[k];
					temp_eps = (aav - minimumValues[k]) / r - (bbv - minimumValues[k]) / r;
					if (temp_eps > eps)
						eps = temp_eps;
				}
				// Update the max value of the indicator
				if (Math.abs(eps) > maxIndicatorValue_)
					maxIndicatorValue_ = Math.abs(eps);
				aux.add(eps);
			}
			indicatorValues_.add(aux);
		}
	}

	/**
	 * Calculate the fitness for the individual at position pos
	 */
	public void fitness(SolutionSet solutionSet, int pos) {
		double fitness = 0.0;
		double kappa = 0.05;

		for (int i = 0; i < solutionSet.size(); i++) {
			if (i != pos) {
				fitness += Math.exp((-1.0 * indicatorValues_.get(i).get(pos) / maxIndicatorValue_) / kappa);
			}
		}
		solutionSet.get(pos).setFitness(fitness);
	}

	/**
	 * Calculate the fitness for the entire population.
	 **/
	public void calculateFitness(SolutionSet solutionSet) {
		// Obtains the lower and upper bounds of the population
		double[] maximumValues = new double[problem_.getNumberOfObjectives()];
		double[] minimumValues = new double[problem_.getNumberOfObjectives()];

		for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
			maximumValues[i] = -Double.MAX_VALUE; // i.e., the minus maximum value
			minimumValues[i] = Double.MAX_VALUE;  // i.e., the maximum value
		}

		for (int pos = 0; pos < solutionSet.size(); pos++) {
			for (int obj = 0; obj < problem_.getNumberOfObjectives(); obj++) {
				double value = solutionSet.get(pos).getObjective(obj);
				if (value!= Double.MAX_VALUE/100 && value > maximumValues[obj])
					maximumValues[obj] = value;
				if (value < minimumValues[obj])
					minimumValues[obj] = value;
			}
		}

//		computeIndicatorValuesHD(solutionSet, maximumValues, minimumValues);
		computeIndicatorValueEPS(solutionSet, maximumValues, minimumValues);
		for (int pos = 0; pos < solutionSet.size(); pos++) {
			fitness(solutionSet, pos);
		}
	}

	/**
	 * Update the fitness before removing an individual
	 */
	public void removeWorst(SolutionSet solutionSet) {

		// Find the worst;
		double worst = solutionSet.get(0).getFitness();
		int worstIndex = 0;
		double kappa = 0.05;

		for (int i = 1; i < solutionSet.size(); i++) {
			if (solutionSet.get(i).getFitness() > worst) {
				worst = solutionSet.get(i).getFitness();
				worstIndex = i;
			}
		}

		// Update the population
		for (int i = 0; i < solutionSet.size(); i++) {
			if (i != worstIndex) {
				double fitness = solutionSet.get(i).getFitness();
				fitness -= Math.exp((-indicatorValues_.get(worstIndex).get(i) / maxIndicatorValue_) / kappa);
				solutionSet.get(i).setFitness(fitness);
			}
		}

		// remove worst from the indicatorValues list
		indicatorValues_.remove(worstIndex); // Remove its own list
		Iterator<List<Double>> it = indicatorValues_.iterator();
		while (it.hasNext())
			it.next().remove(worstIndex);

		// remove the worst individual from the population
		solutionSet.remove(worstIndex);
	} // removeWorst

	/**
	 * Runs of the IBEA algorithm.
	 * 
	 * @return a <code>SolutionSet</code> that is a set of non dominated
	 *         solutions as a result of the algorithm execution
	 * @throws JMException
	 */
	public SolutionSet execute() throws JMException, ClassNotFoundException {
		
		//if (factory == null) {
			//throw new RuntimeException("No instance of SASSolutionInstantiator found!");
		//}
	
		int type;

		int populationSize;
		int archiveSize;

		int maxEvaluations, evaluations;
		Operator crossoverOperator, mutationOperator, selectionOperator;
		SolutionSet solutionSet, archive, offSpringSolutionSet;

		// knee point which might be used as the output
		//Solution kneeIndividual = factory.getSolution(problem_);
		if(getInputParameter("seeder") != null) {
			seeder = (Seeder)getInputParameter("seeder");
		}
		if(getInputParameter("vandInvCoEvolver") != null) {
		    vandInvCoEvolver = (SASValidityAndInvalidityCoEvolver)getInputParameter("vandInvCoEvolver");
		}
		// Read the params
		populationSize = ((Integer) getInputParameter("populationSize")).intValue();
		archiveSize    = ((Integer) getInputParameter("archiveSize")).intValue();
		maxEvaluations = ((Integer) getInputParameter("maxEvaluations")).intValue();

		// Read the operators
		crossoverOperator = operators_.get("crossover");
		mutationOperator  = operators_.get("mutation");
		selectionOperator = operators_.get("selection");

		// Initialize the variables
		solutionSet = new SolutionSet(populationSize);
		archive = new SolutionSet(archiveSize);
		evaluations = 0;

		// -> Create the initial solutionSet
		Solution newSolution;
		if (seeder != null) {
			//seeder.seeding(solutionSet, factory, problem_, populationSize);
			evaluations += populationSize;
		} else {
			for (int i = 0; i < populationSize; i++) {
			   newSolution = new Solution(problem_);
				problem_.evaluate(newSolution);
				problem_.evaluateConstraints(newSolution);
				evaluations++;
				solutionSet.add(newSolution);
				//newSolution = factory.getSolution(problem_);
				//problem_.evaluate(newSolution);
				//problem_.evaluateConstraints(newSolution);
				//evaluations++;
				//solutionSet.add(newSolution);
			}
		}

		SolutionSet old_population = new SolutionSet(populationSize);
		if(SASAlgorithmAdaptor.isFuzzy) {
			//old_population = solutionSet;
			//solutionSet = factory.fuzzilize(solutionSet);
		}
		
		if(vandInvCoEvolver != null) {
			for (int i = 0; i < populationSize; i++) {
				//newSolution = factory.getSolution(problem_);
				//vandInvCoEvolver.createInitialSolution(newSolution, problem_);
//				if(vandInvCoEvolver.createInitialSolution(newSolution, problem_)){
//					evaluations++;
//					population.add(newSolution);
//				}
				
			} //for  
		}
		
	     
		if (SASAlgorithmAdaptor.logGenerationOfObjectiveValue > 0) {
			org.femosaa.util.Logger.logSolutionSetWithGeneration(solutionSet,
					"InitialSolutionSet.rtf", 0);
		}

		long time = Long.MAX_VALUE;
		while (evaluations < maxEvaluations|| (evaluations >= maxEvaluations && (System.currentTimeMillis() - time) < SASAlgorithmAdaptor.seed_time  )) {
			
			SolutionSet old_union = null;
			SolutionSet union = null;
			// Create the solutionSet union of solutionSet and offSpring			
			if(SASAlgorithmAdaptor.isFuzzy) {			
				union = ((SolutionSet) solutionSet).union(old_population);
				old_union = union;
				//union = factory.fuzzilize(union);
			} else {
				union = ((SolutionSet) solutionSet).union(archive);
			}
			
			old_population.clear();
			calculateFitness(union);
			archive = union;

			while (archive.size() > populationSize) {
				removeWorst(archive);
			}
			
			if(SASAlgorithmAdaptor.isFuzzy) {
				for (int i = 0; i < archive.size(); i++) {
					//old_population.add(factory.defuzzilize(archive.get(i), old_union));
				}				
			}
			
			
			
			
			if(vandInvCoEvolver != null && evaluations > 0) {
				vandInvCoEvolver.doEnvironmentalSelection(archive);
			}
			// Create a new offspringPopulation
			offSpringSolutionSet = new SolutionSet(populationSize);
			Solution[] parents 	 = new Solution[2];
			while (offSpringSolutionSet.size() < populationSize) {
				Solution[] offSpring = null;
				if(vandInvCoEvolver != null) {
					parents[0] = (Solution) vandInvCoEvolver.doMatingSelection(archive);
					parents[1] = (Solution) vandInvCoEvolver.doMatingSelection(archive);
                    offSpring = vandInvCoEvolver.doReproduction(parents, problem_);
					
					for(Solution s : offSpring) {
						if(offSpringSolutionSet.size() >= populationSize) {
							break;
						}
						offSpringSolutionSet.add(s);
						evaluations++;
					}
				} 
				int j = 0;
				do {
					j++;
					parents[0] = (Solution) selectionOperator.execute(archive);
				} while (j < IBEA_SAS.TOURNAMENTS_ROUNDS); // do-while
				int k = 0;
				do {
					k++;
					parents[1] = (Solution) selectionOperator.execute(archive);
				} while (k < IBEA_SAS.TOURNAMENTS_ROUNDS); // do-while

				// make the crossover
				offSpring = (Solution[]) crossoverOperator
						.execute(parents);
				mutationOperator.execute(offSpring[0]);
				problem_.evaluate(offSpring[0]);
				problem_.evaluateConstraints(offSpring[0]);
				if(offSpringSolutionSet.size() >= populationSize) {
					break;
				}
				offSpringSolutionSet.add(offSpring[0]);
				evaluations++;
				
				
				//if(((SASSolution)parents[0]).isFromInValid || ((SASSolution)parents[1]).isFromInValid) {
					//((SASSolution)offSpring[0]).isFromInValid = true;
				//}
				
			} // while
			if(SASAlgorithmAdaptor.isLogTheEvalNeededToRemiveNonSeed) {
				org.femosaa.util.Logger.printMarkedSolution(archive, evaluations);
			}
			// End Create a offSpring solutionSet
			solutionSet = offSpringSolutionSet; 
			if(SASAlgorithmAdaptor.logGenerationOfObjectiveValue > 0 && evaluations%SASAlgorithmAdaptor.logGenerationOfObjectiveValue == 0 
					&& evaluations > 0) {
				org.femosaa.util.Logger.logSolutionSetWithGeneration(archive, "SolutionSetWithGen.rtf", 
						evaluations);
			}
			if(evaluations >= maxEvaluations && time == Long.MAX_VALUE) {
				time = System.currentTimeMillis();
			}
		} // while

		if(SASAlgorithmAdaptor.isFuzzy) {
			archive = old_population;
		}
		// 
//		Ranking ranking = new Ranking(archive);
//		return ranking.getSubfront(0); 
		population_ = archive;
		return archive;
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
} // IBEA_SAS 
