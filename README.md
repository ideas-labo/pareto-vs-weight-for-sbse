# The Weights can be Harmful: Pareto Search versus Weighted Search in Multi-Objective Search-Based Software Engineering

This repository contains the data and code for the following paper:

> Tao Chen and Miqing Li. 2021. The Weights can be Harmful: Pareto Search versus Weighted Search in Multi-Objective Search-Based Software Engineering. In submission.

## Introduction

In presence of multiple objectives to be optimized in Search-Based Software Engineering (SBSE), Pareto search has been commonly adopted. It searches for a good approximation of the problem's Pareto optimal solutions, from which the stakeholders choose the most preferred solution according to their preferences. However, %in various SBSE cases, when clear preferences of the stakeholders (e.g., a set of weights which reflect relative importance between objectives) are available prior to the search, weighted search is believed to be the first choice since it simplifies the search via converting the original multi-objective problem into a single-objective one and enable the search to focus on what only the stakeholders are interested in. This paper questions such a *"weighted search first"* belief. We show that the weights can, in fact, be harmful to the search process even in the presence of clear preferences. Specifically, we conduct a large scale empirical study which consists of 38 systems/projects from three representative SBSE problems, together with two types of search budget and nine sets of weights, leading to 604 cases of comparisons. Our key finding is that weighted search reaches a certain level of solution quality by consuming relatively less resources at the early stage of the search; however, Pareto search is at the majority of the time (up to 77% of the cases) significantly better than its weighted counterpart, as long as we allow a sufficient, but not unrealistic search budget. This is a beneficial result, as it discovers a potentially new "rule-of-thumb" for the SBSE community: even when clear preferences are available, it is recommended to always consider Pareto search by default for multi-objective SBSE problems provided that solution quality is more important. Weighted search, in contrast, should only be preferred when the resource/search budget is limited, especially for expensive SBSE problems. This, together with other findings and actionable suggestions in the paper, allows us to codify pragmatic and comprehensive guidance on choosing weighted and Pareto search for SBSE under the circumstance that clear preferences are available.

## Code


### Requirements

* Java 1.6+

The [`code`](https://github.com/ideas-labo/pareto-vs-weight-for-sbse/tree/main/code) folder contains all the information about the source code. The program for NRP and SCT are placed in the same folder while that for WSC is placed into a separated one. To run the code, find the [`AutoRun.java`] in the corresponding folder and editing some of the global parameter in the source code to run the relevant experiments.

The [`library`](https://github.com/ideas-labo/pareto-vs-weight-for-sbse/tree/main/library) folder includes all the necessary external libraries for running the code, please include them into the build path when compiling. 
