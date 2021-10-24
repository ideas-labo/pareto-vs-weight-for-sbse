//  MOEAD_STM.java
//
//  Author:
//       Ke Li <keli.genius@gmail.com>
//       <http://www.cs.bham.ac.uk/~likw/>
//  
//	Reference:
//  	 K. Li, Q. Zhang, S. Kwong, M. Li and R. Wang, "Stable Matching-Based Selection in Evolutionary Multiobjective Optimization",
//		 IEEE Transactions on Evolutionary Computation, 18(6): 909-923, 2014. DOI: 10.1109/TEVC.2013.2293776
//
//  Copyright (c) 2016 Ke Li
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
import jmetal.util.PseudoRandom;
import jmetal.util.Ranking;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

import org.femosaa.core.EAConfigure;
import org.femosaa.core.SASAlgorithmAdaptor;
import org.femosaa.core.SASSolution;
import org.femosaa.core.SASSolutionInstantiator;
import org.femosaa.seed.Seeder;
import org.ssase.util.Repository;

/**
 
 * @author ke, tao
 * 
 */

public class MOEAD extends Algorithm {

	private int populationSize_;
	
	private SASSolutionInstantiator factory = null;
	private Seeder seeder = null;
	// population repository
	private SolutionSet population_;
	private SolutionSet currentOffspring_;
	private SolutionSet union_;

	// ideal point
	double[] z_;

	// nadir point
	double[] nz_;

	// weight vectors
	double[][] lambda_;

	// neighborhood size
	int T_;

	// neighborhood structure
	int[][] neighborhood_;

	// probability that parent solutions are selected from neighborhood
	double delta_;

	String functionType_;
	int evaluations_;

	Operator crossover_;
	Operator mutation_;

	String dataDirectory_;

  	/**
  	 * Constructor
  	 * @param problem Problem to solve
  	 */
	private MOEAD(Problem problem) {
		super(problem);
		functionType_ = "Norm_ITCH";
	}

  	/**
  	 * Constructor
  	 * @param problem Problem to solve
  	 */
	public MOEAD(Problem problem, SASSolutionInstantiator factory) {
		super(problem);
        this.factory = factory;
		functionType_ = "Norm_ITCH";
		//functionType_ = "Norm_TCH";
	}
	
	public SolutionSet execute() throws JMException, ClassNotFoundException {
		
	
		
		int type = 1;
		int maxEvaluations;
		
		// knee point which might be used as the output
		//Solution kneeIndividual = factory.getSolution(problem_);
		if(getInputParameter("seeder") != null) {
			seeder = (Seeder)getInputParameter("seeder");
		}
		evaluations_    = 0;
		dataDirectory_  = this.getInputParameter("dataDirectory").toString();
		maxEvaluations  = ((Integer) this.getInputParameter("maxEvaluations")).intValue();
		populationSize_ = ((Integer) this.getInputParameter("populationSize")).intValue();
		int measurement = 0;

		population_  = new SolutionSet(populationSize_);
		functionType_ = "AGG";
		T_ 	   = populationSize_/2;//2
		delta_ = 0.9;//0.9
		
		if(populationSize_ < T_) {
			T_ = populationSize_/2;
		}

		z_ 			  = new double[problem_.getNumberOfObjectives()];
	    nz_ 		  = new double[problem_.getNumberOfObjectives()];
	    lambda_ 	  = Repository.lambda_;//new double[populationSize_][problem_.getNumberOfObjectives()];
	    neighborhood_ = new int[populationSize_][T_];

		crossover_ = operators_.get("crossover");
		mutation_  = operators_.get("mutation");

		/* STEP 1. INITIALIZATION */
		// STEP 1.1. compute Euclidean distances between weight vectors and find T
		//initUniformWeight();
		initNeighborhood();

		// STEP 1.2. initialize population
		if (seeder != null) {
			seeder.seeding(population_, factory, problem_, populationSize_);
			evaluations_ += populationSize_;
			measurement += factory.record(population_);
		} else {
		    //initPopulation();
			  for (int i = 0; i < populationSize_; i++) {
					Solution newSolution = new Solution(problem_);
					problem_.evaluate(newSolution);
					problem_.evaluateConstraints(newSolution);
					evaluations_++;
					population_.add(newSolution);
			     
			    }
		}
		
		
		
		SolutionSet old_population = new SolutionSet(populationSize_);
		if(SASAlgorithmAdaptor.isFuzzy) {
			old_population = population_;
			population_ = factory.fuzzilize(population_);
		}
		
		// STEP 1.3. initialize the ideal and nadir points
		initIdealPoint();
		initNadirPoint();
		long time = Long.MAX_VALUE;
		
		
		/* STEP 2. UPDATE */
		do {
			int[] permutation = new int[populationSize_];
			Utils.randomPermutation(permutation, populationSize_);
			currentOffspring_   = new SolutionSet(populationSize_);//new SolutionSet(2 * populationSize_);
			
			for (int i = 0; i < populationSize_/2; i++) {
			    int n = permutation[i]; // or int n = i;
				// int n = i ; // or int n = i;
				double rnd = PseudoRandom.randDouble();

				// STEP 2.1. Mating selection based on probability
				if (rnd < delta_)
					type = 1; // neighborhood
				else
					type = 2; // whole population
				Solution[] children;
				Solution[] parents = new Solution[2];
				Vector<Integer> p = new Vector<Integer>();
				
				parents = matingSelection(p, n, 2, type);

				// STEP 2.2. Reproduction
				children = (Solution[]) crossover_.execute(parents);
				mutation_.execute(children[0]);
				mutation_.execute(children[1]);
				// evaluation
				problem_.evaluate(children[0]);
				problem_.evaluate(children[1]);
				//measurement += factory.record(children[0]);
				if(EAConfigure.getInstance().measurement == measurement) {
					break;
				}
				problem_.evaluate(children[1]);
				//measurement += factory.record(children[1]);
				if(EAConfigure.getInstance().measurement == measurement) {
					break;
				}
				evaluations_++;
				evaluations_++;
				// STEP 2.3. update the ideal and nadir points
				
				if(!SASAlgorithmAdaptor.isFuzzy) {
					updateReference(children[0]);
					updateNadirPoint(children[0]);
					
					updateReference(children[1]);
					updateNadirPoint(children[1]);
				}
				
				

				// add into the offspring population
				currentOffspring_.add(children[0]);
				currentOffspring_.add(children[1]);
				
				//if(((SASSolution)parents[0]).isFromInValid || ((SASSolution)parents[1]).isFromInValid) {
				//	((SASSolution)children[0]).isFromInValid = true;
				//	((SASSolution)children[1]).isFromInValid = true;
				//}
			} // for
			
			// Combine the parent and the current offspring populations
			//union_ = ((SolutionSet) population_).union(currentOffspring_);
			
			if(SASAlgorithmAdaptor.isFuzzy) {
				SolutionSet union = ((SolutionSet) old_population).union(currentOffspring_);
				SolutionSet old_union = union;
				if(SASAlgorithmAdaptor.isBoundNormalizationForTarget) {
					((SASSolution)old_population.get(0)).resetNormalizationBounds(0);
					((SASSolution)old_population.get(0)).resetNormalizationBounds(1);
					/*for(int i = 0; i < union.size(); i++) {
						((SASSolution)union.get(i)).updateNormalizationBounds(new double[] {union.get(i).getObjective(0),
								union.get(i).getObjective(1)});
					}*/
					
					for(int i = 0; i < old_population.size(); i++) {
						((SASSolution)old_population.get(i)).updateNormalizationBounds(new double[] {old_population.get(i).getObjective(0),
								old_population.get(i).getObjective(1)});
					}
				}
				union = factory.fuzzilize(union);
				
				for(int i = 0; i < union.size(); i++) {
					// update with the fuzzy values
					updateReference(union.get(i));
					updateNadirPoint(union.get(i));
				}
				
				old_population.clear();
				population_.clear();
				//System.out.print("fuzzy----\n");
				for (int i = 0; i < populationSize_; i++) {
					population_.add(union.get(i));
					//System.out.print(population_.get(i).getObjective(0) + " : " + population_.get(i).getObjective(1)+"\n");
				}
				
				for (int i = 0; i < union.size() - populationSize_; i++) {
					this.updateNeighborhood(union.get(populationSize_+i), i, type);
					//this.updateNeighborhood(union.get(populationSize_+i*2), i, type);
					//this.updateNeighborhood(union.get(populationSize_+i*2+1), i, type);
				}
				
				for (int i = 0; i < population_.size(); i++) {
					/*System.out.print("***\n");
					System.out.print("fuzzy value = " + population_.get(i).getObjective(0) + ":" + population_.get(i).getObjective(1) + "\n");
					System.out.print("orignal value = " + factory.defuzzilize(population_.get(i), old_union).getObjective(0) + ":" + factory.defuzzilize(population_.get(i), old_union).getObjective(1) + "\n");
					System.out.print("***\n");*/
					old_population.add(factory.defuzzilize(population_.get(i), old_union));
					//System.out.print("original: " + old_population.get(i).getObjective(0) + " : " + old_population.get(i).getObjective(1)+"\n");
					//System.out.print("fuzzy: " + population_.get(i).getObjective(0) + " : " + population_.get(i).getObjective(1)+"\n");
					
				}
				//System.out.print("----\n");
				

				
			} else {
				/*for (int i = 0; i < populationSize_/2; i++) {
					this.updateNeighborhood(currentOffspring_.get(i*2), i, type);
					this.updateNeighborhood(currentOffspring_.get(i*2+1), i, type);
				}*/
				for (int i = 0; i < populationSize_; i++) {
					if(i >= currentOffspring_.size()) {
						break;
					}
					this.updateNeighborhood(currentOffspring_.get(i), i, type);
				}
			}
			
			
			

			// selection process
			//selection();
			if(SASAlgorithmAdaptor.isLogTheEvalNeededToRemiveNonSeed) {
				org.femosaa.util.Logger.printMarkedSolution(population_, evaluations_);
			}
			/*if(SASAlgorithmAdaptor.logGenerationOfObjectiveValue > 0&& evaluations_%SASAlgorithmAdaptor.logGenerationOfObjectiveValue == 0) {
				org.femosaa.util.Logger.logSolutionSetWithGeneration(population_, "SolutionSetWithGen.rtf", 
						evaluations_);
			}*/
			if(evaluations_ >= maxEvaluations && time == Long.MAX_VALUE) {
				time = System.currentTimeMillis();
			}
			
			
			
			
			if(SASAlgorithmAdaptor.logGenerationOfObjectiveValue > 0 && evaluations_%SASAlgorithmAdaptor.logGenerationOfObjectiveValue == 0) {
				if(SASAlgorithmAdaptor.isFuzzy) {
					org.femosaa.util.Logger.logSolutionSetWithGeneration(old_population, "SolutionSetWithGen.rtf", 
							evaluations_ );
				} else {
					org.femosaa.util.Logger.logSolutionSetWithGeneration(population_, "SolutionSetWithGen.rtf", 
							evaluations_ );
				}
				
				//org.femosaa.util.Logger.logSolutionSetValuesWithGen(population, "SolutionSetValuesWithGen.rtf", 
						//evaluations );
			}
			
			if (SASAlgorithmAdaptor.logMeasurementOfObjectiveValue) {
				if(SASAlgorithmAdaptor.isFuzzy) {
					org.femosaa.util.Logger.logSolutionSetWithGeneration(old_population, "SolutionSetWithMeasurement.rtf", 
							measurement );
				} else {
					org.femosaa.util.Logger.logSolutionSetWithGeneration(population_, "SolutionSetWithMeasurement.rtf", 
							measurement );
				}
				
			}
			
			if(EAConfigure.getInstance().measurement == measurement) {
				break;
			}
		
		} while (evaluations_ <= maxEvaluations|| (evaluations_ >= maxEvaluations && (System.currentTimeMillis() - time) < -1 ));
		
		// find the knee point
//		kneeIndividual = kneeSelection();
//		
//		for (int i = 0; i < problem_.getNumberOfObjectives(); i++)
//			System.out.print(kneeIndividual.getObjective(i) + "\n");
//		
//		if(isFindKnee) {
//			population_.clear();
//			population_.add(kneeIndividual);
//		}
		if(SASAlgorithmAdaptor.isFuzzy) {
			population_ = old_population;
			org.femosaa.util.Logger.logFinalEvaluation("FinalEvaluationCount.rtf", evaluations_);
		}
		
		/*if (SASAlgorithmAdaptor.logMeasurementOfObjectiveValue) {
			org.femosaa.util.Logger.logSolutionSetWithGeneration(population_, "SolutionSetWithMeasurement.rtf", 
					measurement );
		}*/
		
		return population_;

//		Ranking ranking = new Ranking(population_);
//		return ranking.getSubfront(0);
		//return population_;
	}
	
	public SolutionSet doRanking(SolutionSet population){
		Ranking ranking = new Ranking(population);
		return ranking.getSubfront(0);
	}
	
	protected  void updateNeighborhood(Solution individual, int subProblemId, int neighborType) {
	    int size;
	    int time;

	    time = 0;

	    if (neighborType == 1) {
	      size = neighborhood_[subProblemId].length;
	    } else {
	      size = population_.size();
	    }
	    int[] perm = new int[size];

	    Utils.randomPermutation(perm, size);
	    int maximumNumberOfReplacedSolutions = size;
	    for (int i = 0; i < size; i++) {
	      int k;
	      if (neighborType == 1) {
	        k = neighborhood_[subProblemId][perm[i]];
	      } else {
	        k = perm[i];
	      }
	      double f1, f2;

	      f1 = fitnessFunction(population_.get(k), lambda_[k]);
	      f2 = fitnessFunction(individual, lambda_[k]);
	      
	      //System.out.print("f1: " + f1 + ", f2: " + f2 + "\n");

	      if (f2 < f1) {
	        population_.replace(k, new Solution(individual));
	        time++;
	      }

	   
	      
	      if (time >= maximumNumberOfReplacedSolutions) {
	        return;
	      }
	    }
	  }
	
	/**
  	 * Select the next parent population, based on the stable matching criteria
  	 */
	public void selection() {

		int[] idx = new int[populationSize_];
		double[] nicheCount = new double[populationSize_];

		int[][]    solPref   = new int[union_.size()][];
		double[][] solMatrix = new double[union_.size()][];
		double[][] distMatrix    = new double[union_.size()][];
		double[][] fitnessMatrix = new double[union_.size()][];

		for (int i = 0; i < union_.size(); i++) {
			solPref[i]   = new int[populationSize_];
			solMatrix[i] = new double[populationSize_];
			distMatrix[i]    = new double[populationSize_];
			fitnessMatrix[i] = new double[populationSize_];
		}
		int[][]    subpPref   = new int[populationSize_][];
		double[][] subpMatrix = new double[populationSize_][];
		for (int i = 0; i < populationSize_; i++) {
			subpPref[i]   = new int[union_.size()];
			subpMatrix[i] = new double[union_.size()];
		}

		// Calculate the preference values of solution matrix
		for (int i = 0; i < union_.size(); i++) {
			int minIndex = 0;
			for (int j = 0; j < populationSize_; j++) {
				fitnessMatrix[i][j] = fitnessFunction(union_.get(i), lambda_[j]);
				distMatrix[i][j]  	= calculateDistance(union_.get(i), lambda_[j]);
				if (distMatrix[i][j] < distMatrix[i][minIndex])
					minIndex = j;
			}
			nicheCount[minIndex] = nicheCount[minIndex] + 1;
		}

		// calculate the preference values of subproblem matrix and solution matrix
		for (int i = 0; i < union_.size(); i++) {
			for (int j = 0; j < populationSize_; j++) {
				subpMatrix[j][i] = fitnessFunction(union_.get(i), lambda_[j]);
				solMatrix[i][j] = distMatrix[i][j] + nicheCount[j];
			}
		}

		// sort the preference value matrix to get the preference rank matrix
		for (int i = 0; i < populationSize_; i++) {
			for (int j = 0; j < union_.size(); j++)
				subpPref[i][j] = j;
			Utils.QuickSort(subpMatrix[i], subpPref[i], 0, union_.size() - 1);
		}
		for (int i = 0; i < union_.size(); i++) {
			for (int j = 0; j < populationSize_; j++)
				solPref[i][j] = j;
			Utils.QuickSort(solMatrix[i], solPref[i], 0, populationSize_ - 1);
		}

		idx = stableMatching(subpPref, solPref, populationSize_, union_.size());

		for (int i = 0; i < populationSize_; i++)
			population_.replace(i, new Solution(union_.get(idx[i])));
	}
  
  	/**
	 * Return the stable matching between 'subproblems' and 'solutions'
	 * ('subproblems' propose first). It is worth noting that the number of
	 * solutions is larger than that of the subproblems.
	 * 
	 * @param manPref
	 * @param womanPref
	 * @param menSize
	 * @param womenSize
	 * @return
	 */
	public int[] stableMatching(int[][] manPref, int[][] womanPref, int menSize, int womenSize) {
		
		// Indicates the mating status
		int[] statusMan   = new int[menSize];
		int[] statusWoman = new int[womenSize];

		final int NOT_ENGAGED = -1;
		for (int i = 0; i < womenSize; i++)
			statusWoman[i] = NOT_ENGAGED;

		// List of men that are not currently engaged.
		LinkedList<Integer> freeMen = new LinkedList<Integer>();
		for (int i = 0; i < menSize; i++)
			freeMen.add(i);

		// next[i] is the next woman to whom i has not yet proposed.
		int[] next = new int[womenSize];

		while (!freeMen.isEmpty()) {
			int m = freeMen.remove();
			int w = manPref[m][next[m]];
			next[m]++;
			if (statusWoman[w] == NOT_ENGAGED) {
				statusMan[m]   = w;
				statusWoman[w] = m;
			} else {
				int m1 = statusWoman[w];
				if (prefers(m, m1, womanPref[w], menSize)) {
					statusMan[m]   = w;
					statusWoman[w] = m;
					freeMen.add(m1);
				} else {
					freeMen.add(m);
				}
			}
		}
		
		return statusMan;
	}
	
  	/**
  	 * Returns true in case that a given woman prefers x to y.
  	 * @param x
  	 * @param y
  	 * @param womanPref
  	 * @return
  	 */
	public boolean prefers(int x, int y, int[] womanPref, int size) {
		
		for (int i = 0; i < size; i++) {
			int pref = womanPref[i];
			if (pref == x)
				return true;
			if (pref == y)
				return false;
		}
		// this should never happen.
		System.out.println("Error in womanPref list!");
		return false;
	}
	
	/**
	 * Calculate the perpendicular distance between the solution and reference
	 * line
	 * 
	 * @param individual
	 * @param lambda
	 * @return
	 */
	public double calculateDistance(Solution individual, double[] lambda) {
		double scale;
		double distance;

		double[] vecInd  = new double[problem_.getNumberOfObjectives()];
		double[] vecProj = new double[problem_.getNumberOfObjectives()];
		
		// vecInd has been normalized to the range [0,1]
		for (int i = 0; i < problem_.getNumberOfObjectives(); i++)
			vecInd[i] = nz_[i] != z_[i]? (individual.getObjective(i) - z_[i]) / (nz_[i] - z_[i]) : 
				(individual.getObjective(i)- z_[i]) / (nz_[i]);

		scale = innerproduct(vecInd, lambda) / innerproduct(lambda, lambda);
		for (int i = 0; i < problem_.getNumberOfObjectives(); i++)
			vecProj[i] = vecInd[i] - scale * lambda[i];

		distance = norm_vector(vecProj);
		
		return distance;
	}
	
	/**
	 * Calculate the distance between 'individual' and a weight vector
	 * 
	 * @param individual
	 * @param lambda
	 * @return
	 */
	public double calculateDistance2(Solution individual, double[] lambda) {

		double distance;
		double distanceSum = 0.0;

		double[] vecInd  	   = new double[problem_.getNumberOfObjectives()];
		double[] normalizedObj = new double[problem_.getNumberOfObjectives()];

		for (int i = 0; i < problem_.getNumberOfObjectives(); i++)
			distanceSum += individual.getObjective(i);
		for (int i = 0; i < problem_.getNumberOfObjectives(); i++)
			normalizedObj[i] = individual.getObjective(i) / distanceSum;
		for (int i = 0; i < problem_.getNumberOfObjectives(); i++)
			vecInd[i] = normalizedObj[i] - lambda[i];

		distance = norm_vector(vecInd);

		return distance;
	}
	
	/**
	 * Calculate the perpendicular distance between the solution and reference line (PBI style)
	 * 
	 * @param individual
	 * @param lambda
	 * @return
	 */
	public double calculateDistance3(Solution individual, double[] lambda) {

		double distance;
		
		// normalize the weight vector (line segment)
		double nd = norm_vector(lambda);
		for (int i = 0; i < problem_.getNumberOfObjectives(); i++)
			lambda[i] = lambda[i] / nd;

		double[] realA = new double[problem_.getNumberOfObjectives()];
		double[] realB = new double[problem_.getNumberOfObjectives()];

		// difference between current point and reference point
		for (int i = 0; i < problem_.getNumberOfObjectives(); i++)
			realA[i] = nz_[i] != z_[i]? (individual.getObjective(i) - z_[i]) / (nz_[i] - z_[i]) :
				(individual.getObjective(i) - z_[i]) / (nz_[i]);

		// distance along the line segment
		double d1 = Math.abs(innerproduct(realA, lambda));

		// distance to the line segment
		for (int i = 0; i < problem_.getNumberOfObjectives(); i++)
			realB[i] = (realA[i] - (z_[i] + d1 * lambda[i]));
		distance = norm_vector(realB);

		return distance;
	}
	/**
	 * Initialize the weight vectors for subproblems (We only use the data that are already available)
	 */
	public void initUniformWeight() {
		String dataFileName;
		dataFileName = "W" + problem_.getNumberOfObjectives() + "D_" + populationSize_ + ".dat";

		try {
			// Open the file
			FileInputStream fis = new FileInputStream(dataDirectory_ + "/"
					+ dataFileName);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);

			int i = 0;
			int j = 0;
			String aux = br.readLine();
			while (aux != null) {
				StringTokenizer st = new StringTokenizer(aux);
				j = 0;
				while (st.hasMoreTokens()) {
					double value = (new Double(st.nextToken())).doubleValue();
					lambda_[i][j] = value;
					j++;
				}
				aux = br.readLine();
				i++;
			}
			br.close();
		} catch (Exception e) {
			System.out
					.println("initUniformWeight: failed when reading for file: "
							+ dataDirectory_ + "/" + dataFileName);
			e.printStackTrace();
		}
	} // initUniformWeight

	/**
	 * Compute the utility of subproblems
	 */
	public void comp_utility() {
		
		double f1, f2, uti, delta;
		
		for (int i = 0; i < populationSize_; i++) {
			f1    = fitnessFunction(population_.get(i), lambda_[i]);
			//f2 	  = fitnessFunction(savedValues_[i], lambda_[i]);
			
			//delta = f2 - f1;
//			if (delta > 0.001)
//				utility_[i] = 1.0;
//			else {
//				uti 		= (0.95 + (0.05 * delta / 0.001)) * utility_[i];
//				utility_[i] = uti < 1.0 ? uti : 1.0;
//			}
//			savedValues_[i] = factory.getSolution(population_.get(i));
		}
	}

	/**
	 * Initialize the neighborhood of subproblems
	 */
	public void initNeighborhood() {
		double[] x = new double[populationSize_];
		int[] idx = new int[populationSize_];

		for (int i = 0; i < populationSize_; i++) {
			// calculate the distances based on weight vectors
			for (int j = 0; j < populationSize_; j++) {
				x[j] = Utils.distVector(lambda_[i], lambda_[j]);
				idx[j] = j;
			}

			// find 'niche' nearest neighboring subproblems
			Utils.minFastSort(x, idx, populationSize_, T_);

			for (int k = 0; k < T_; k++) {
				neighborhood_[i][k] = idx[k];
			}
		}
	}

  /**
   * Initialize the population
   * @throws JMException
   * @throws ClassNotFoundException
   */
  public void initPopulation() throws JMException, ClassNotFoundException {
	  for (int i = 0; i < populationSize_; i++) {
      Solution newSolution = new Solution(problem_);

      problem_.evaluate(newSolution);
      evaluations_++;
      population_.add(newSolution) ;
      //measurement += factory.record(newSolution);
      //savedValues_[i] = factory.getSolution(newSolution);
    }
  }

	public void initPopulation_Read() throws JMException, ClassNotFoundException {
		String popFileName = "FUN1.dat";

		try {
			// Open the file
			FileInputStream fis = new FileInputStream(dataDirectory_ + "/"
					+ popFileName);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);

			int i = 0;
			int j = 0;
			String aux = br.readLine();
			while (aux != null) {
				StringTokenizer st = new StringTokenizer(aux);
				j = 0;
				Solution newSolution = new Solution(problem_);
				while (st.hasMoreTokens()) {
					double value = (new Double(st.nextToken())).doubleValue();
					newSolution.setObjective(j, value);
					j++;
				}
				population_.add(newSolution) ;
				//savedValues_[i] = factory.getSolution(newSolution);
				aux = br.readLine();
				i++;
			}
			br.close();
		} catch (Exception e) {
			System.out
					.println("initUniformWeight: failed when reading for file: "
							+ dataDirectory_ + "/" + popFileName);
			e.printStackTrace();
		}
	}

  	/**
  	 * Initialize the ideal point
	 * @throws JMException
	 * @throws ClassNotFoundException
	 */
	void initIdealPoint() throws JMException, ClassNotFoundException {
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
		for (int i = 0; i < problem_.getNumberOfObjectives(); i++)
			nz_[i] = -1.0e+30;

		for (int i = 0; i < populationSize_; i++)
			updateNadirPoint(population_.get(i));
	}

	/**
  	 * Mating selection is used to select the mating parents for offspring generation
  	 * @param list : the set of the indexes of selected mating parents
  	 * @param cid  : the id of current subproblem
  	 * @param size : the number of selected mating parents
  	 * @param type : 1 - neighborhood; otherwise - whole population
  	 */
	public Solution[] matingSelection(Vector<Integer> list, int cid, int size, int type) {
		
		int ss, r, p;
		
		Solution[] parents = new Solution[2];
		
		ss = neighborhood_[cid].length;
		while (list.size() < size) {
			if (type == 1) {
				r = PseudoRandom.randInt(0, ss - 1);
				p = neighborhood_[cid][r];
			} else {
				p = PseudoRandom.randInt(0, populationSize_ - 1);
			}
			boolean flag = true;
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i) == p) // p is in the list
				{
					flag = false;
					break;
				}
			}

			if (flag) {
				list.addElement(p);
			}
		}
		parents[0] = population_.get(list.get(0));
		parents[1] = population_.get(list.get(1));
		
		return parents;
	} // matingSelection

	public List<Integer> tour_selection(int depth) {

		int i2, s2;
		int threshold;
		
		// selection based on utility
		List<Integer> selected  = new ArrayList<Integer>();
		List<Integer> candidate = new ArrayList<Integer>();

		for (int i = 0; i < problem_.getNumberOfObjectives(); i++)
			selected.add(i);

		// set of unselected weights
		for (int i = problem_.getNumberOfObjectives(); i < populationSize_; i++)
			candidate.add(i);

		threshold = (int) (populationSize_ / 5);
		while (selected.size() < threshold) {
			int best_idd = (int) (PseudoRandom.randDouble() * candidate.size());
			int best_sub = candidate.get(best_idd);
			
			for (int i = 1; i < depth; i++) {
				i2 = (int) (PseudoRandom.randDouble() * candidate.size());
				s2 = candidate.get(i2);
//				if (utility_[s2] > utility_[best_sub]) {
//					best_idd = i2;
//					best_sub = s2;
//				}
			}
			selected.add(best_sub);
			candidate.remove(best_idd);
		}
		return selected;
	}


   	/**
   	 * Update the ideal point, it is just an approximation with the best value for each objective
   	 * @param individual
   	 */
	void updateReference(Solution individual) {
		for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
			// update dynamically even for normalized solutions, as sometime, e.g., in MMO, this does not have to be 0-1
			/*if(SASAlgorithmAdaptor.isFuzzy) {
				z_[i] = 0.0;
				continue;
			}*/
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
		for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
			/*if(SASAlgorithmAdaptor.isFuzzy) {
				nz_[i] = 1.0;
				continue;
			}*/
			if (individual.getObjective(i) != Double.MAX_VALUE/100 && individual.getObjective(i) > nz_[i])
				nz_[i] = individual.getObjective(i);
		}
	}
	
	
	/**
	 * Calculate the dot product of two vectors
	 * @param vec1
	 * @param vec2
	 * @return
	 */
	public double innerproduct(double[] vec1, double[] vec2) {
		double sum = 0;
		
		for (int i = 0; i < vec1.length; i++)
			sum += vec1[i] * vec2[i];
		
		return sum;
	}

	/**
	 * Calculate the norm of the vector
	 * @param z
	 * @return
	 */
	public double norm_vector(double[] z) {
		double sum = 0;
		
		for (int i = 0; i < problem_.getNumberOfObjectives(); i++)
			sum += z[i] * z[i];
		
		return Math.sqrt(sum);
	}

	/**
	 * Calculate the fitness value of a given individual, based on the specific scalarizing function
	 * @param individual
	 * @param lambda
	 * @return
	 */
	double fitnessFunction(Solution individual, double[] lambda) {
		double fitness;
		fitness = 0.0;

		if (functionType_.equals("ITCH")) {
			double maxFun = -1.0e+30;

			for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
				double d = individual.getObjective(i);
				if(individual.getObjective(i) == Double.MAX_VALUE/100) {
					d = nz_[i];
				}
				double diff = Math.abs(d- z_[i]);

				double feval;
				if (lambda[i] == 0) {
					feval = diff / 0.000001;
				} else {
					feval = diff / lambda[i];
				}
				if (feval > maxFun) {
					maxFun = feval;
				}
			}
			fitness = maxFun;
		} else if (functionType_.equals("Norm_ITCH"))  {
			double maxFun = -1.0e+30;
			double[] normalized_obj = new double[problem_.getNumberOfObjectives()];
			
			// normalization
			for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
				if(individual.getObjective(i) == Double.MAX_VALUE/100) {
					normalized_obj[i] = 1.0;
				} else { 
				normalized_obj[i] = nz_[i] != z_[i]? Math.abs((individual.getObjective(i) - z_[i]) / (nz_[i] - z_[i])) :
					Math.abs((individual.getObjective(i)- z_[i]) / (nz_[i]));
			   }
		    }
			for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
				double diff = normalized_obj[i];//Math.abs(individual.getObjective(i));
				
				double feval;
				if (lambda[i] == 0)
					feval = diff / 0.000001;
				else
					feval = diff / lambda[i];
				if (feval > maxFun)
					maxFun = feval;
			}
			fitness = maxFun;
		} else if (functionType_.equals("TCH")) {			
			double max_fun = -1.0e+30;
			
			for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
				double d = individual.getObjective(i);
				if(individual.getObjective(i) == Double.MAX_VALUE/100) {
					d = nz_[i];
				}
				double diff = (d - z_[i]);
				double feval;
				if (lambda[i] == 0)
					feval = 0.000001 * diff;
				else
					feval = diff * lambda[i];
				if (feval > max_fun)
					max_fun = feval;
			}
			fitness = max_fun;
		} else if (functionType_.equals("Norm_TCH")) {
			double max_fun = -1.0e+30;
			double[] normalized_obj = new double[problem_.getNumberOfObjectives()];
			
			// normalization
			for (int i = 0; i < problem_.getNumberOfObjectives(); i++)  {
				if(individual.getObjective(i) == Double.MAX_VALUE/100) {
					normalized_obj[i] = 1.0;
				} else {
				 normalized_obj[i] = (individual.getObjective(i) - z_[i]) / (nz_[i] - z_[i]);
			    }
			}
			for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
				double diff = individual.getObjective(i) == Double.MAX_VALUE/100? Math.abs(nz_[i]) : Math.abs(individual.getObjective(i));
				double feval;
				if (lambda[i] == 0)
					feval = 0.000001 * diff;
				else
					feval = diff * lambda[i];
				if (feval > max_fun)
					max_fun = feval;
			}
			
			fitness = max_fun;
		} else if (functionType_.equals("PBI"))
		{
			double theta; // penalty parameter
			theta = 5.0;

			// normalize the weight vector (line segment)
			double nd = norm_vector(lambda);
			for (int i = 0; i < problem_.getNumberOfObjectives(); i++)
				lambda[i] = lambda[i] / nd;

			double[] realA = new double[problem_.getNumberOfObjectives()];
			double[] realB = new double[problem_.getNumberOfObjectives()];

			// difference beween current point and reference point
			for (int n = 0; n < problem_.getNumberOfObjectives(); n++)
				realA[n] = individual.getObjective(n) == Double.MAX_VALUE/100? (nz_[n] - z_[n]) : (individual.getObjective(n) - z_[n]);

			// distance along the line segment
			double d1 = Math.abs(innerproduct(realA, lambda));

			// distance to the line segment
			for (int n = 0; n < problem_.getNumberOfObjectives(); n++)
				realB[n] = individual.getObjective(n) == Double.MAX_VALUE/100? (nz_[n]  - (z_[n] + d1
						* lambda[n])) : (individual.getObjective(n) - (z_[n] + d1
						* lambda[n]));
			double d2 = norm_vector(realB);

			fitness = d1 + theta * d2;
		} else if (functionType_.equals("AGG"))
		{
			double sum = 0.0;
		      for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
		    	double a = nz_[i] != z_[i]? Math.abs((individual.getObjective(i) - z_[i]) / (nz_[i] - z_[i])) :
					Math.abs((individual.getObjective(i)- z_[i]) / (nz_[i]));
		        sum += (lambda[i]) * a;
		      }

		      fitness = sum;
		}else {
		
			System.out.println("MOEAD.fitnessFunction: unknown type "
					+ functionType_);
			System.exit(-1);
		}
		return fitness;
	}
//	double fitnessFunction(Solution individual, double[] lambda) {
//		double fitness;
//		fitness = 0.0;
//
//		if (functionType_.equals("ITCH")) {
//			double maxFun = -1.0e+30;
//
//			for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
//				double diff = Math.abs(individual.getObjective(i) - z_[i]);
//
//				double feval;
//				if (lambda[i] == 0) {
//					feval = diff / 0.000001;
//				} else {
//					feval = diff / lambda[i];
//				}
//				if (feval > maxFun) {
//					maxFun = feval;
//				}
//			}
//			fitness = maxFun;
//		} else if (functionType_.equals("Norm_ITCH"))  {
//			double maxFun = -1.0e+30;
//			double[] normalized_obj = new double[problem_.getNumberOfObjectives()];
//			
//			// normalization
//			for (int i = 0; i < problem_.getNumberOfObjectives(); i++) 
//				normalized_obj[i] = nz_[i] != z_[i]? Math.abs((individual.getObjective(i) - z_[i]) / (nz_[i] - z_[i])) :
//					Math.abs((individual.getObjective(i)- z_[i]) / (nz_[i]));
//			
//			for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
//				double diff = normalized_obj[i];//Math.abs(individual.getObjective(i));
//				
//				double feval;
//				if (lambda[i] == 0)
//					feval = diff / 0.000001;
//				else
//					feval = diff / lambda[i];
//				if (feval > maxFun)
//					maxFun = feval;
//			}
//			fitness = maxFun;
//		} else if (functionType_.equals("TCH")) {			
//			double max_fun = -1.0e+30;
//			
//			for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
//				double diff = (individual.getObjective(i) - z_[i]);
//				double feval;
//				if (lambda[i] == 0)
//					feval = 0.000001 * diff;
//				else
//					feval = diff * lambda[i];
//				if (feval > max_fun)
//					max_fun = feval;
//			}
//			fitness = max_fun;
//		} else if (functionType_.equals("Norm_TCH")) {
//			double max_fun = -1.0e+30;
//			double[] normalized_obj = new double[problem_.getNumberOfObjectives()];
//			
//			// normalization
//			for (int i = 0; i < problem_.getNumberOfObjectives(); i++) 
//				normalized_obj[i] = (individual.getObjective(i) - z_[i]) / (nz_[i] - z_[i]);
//			
//			for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
//				double diff = Math.abs(individual.getObjective(i));
//				double feval;
//				if (lambda[i] == 0)
//					feval = 0.000001 * diff;
//				else
//					feval = diff * lambda[i];
//				if (feval > max_fun)
//					max_fun = feval;
//			}
//			
//			fitness = max_fun;
//		} else if (functionType_.equals("PBI"))
//		{
//			double theta; // penalty parameter
//			theta = 5.0;
//
//			// normalize the weight vector (line segment)
//			double nd = norm_vector(lambda);
//			for (int i = 0; i < problem_.getNumberOfObjectives(); i++)
//				lambda[i] = lambda[i] / nd;
//
//			double[] realA = new double[problem_.getNumberOfObjectives()];
//			double[] realB = new double[problem_.getNumberOfObjectives()];
//
//			// difference beween current point and reference point
//			for (int n = 0; n < problem_.getNumberOfObjectives(); n++)
//				realA[n] = (individual.getObjective(n) - z_[n]);
//
//			// distance along the line segment
//			double d1 = Math.abs(innerproduct(realA, lambda));
//
//			// distance to the line segment
//			for (int n = 0; n < problem_.getNumberOfObjectives(); n++)
//				realB[n] = (individual.getObjective(n) - (z_[n] + d1
//						* lambda[n]));
//			double d2 = norm_vector(realB);
//
//			fitness = d1 + theta * d2;
//		} else {
//			System.out.println("MOEAD.fitnessFunction: unknown type "
//					+ functionType_);
//			System.exit(-1);
//		}
//		return fitness;
//	}
	
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

} // moea/D-STM