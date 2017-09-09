package evodef;

import evogame.Mutator;
import ga.SimpleRMHC;
import plot.LinePlot;
import utilities.StatSummary;

import java.util.ArrayList;

/**
 *  Ultra-simple class for simple comparison of FHT versus
 *  proper evaluation for different size of problem, and for
 *  different problems.
 *
 *  Each time we will pick a single algorithm to evaluate, at
 *  least to begin with.
 */

public class TestFHT {
    public static void main(String[] args) {

        int nSamples = 5;
        SimpleRMHC rmhc = new SimpleRMHC(nSamples);

        // now perform the evaluation with and without FHT
        // in order to do this we need access to the solution

        int nDims = 100;

        // make this 2 for bit strings
        int mValues = 2;

        double noise = 1.0;

        NoisySolutionEvaluator evaluator = new EvalMaxM(nDims, mValues, noise);
        int nReps = 100;

        int nFitnessEvals = 100000;

        TestFHT testFHT = new TestFHT(evaluator, nFitnessEvals);
        Mutator.defaultPointProb = 1;
        Mutator.flipAtLeastOneValue = false;

        System.out.println("Problem: " + evaluator.getClass().getSimpleName());
        System.out.println("N Fitness Evals: " + nFitnessEvals);
        System.out.println("Noise: " + noise);
        System.out.println("N Dimensions: " + nDims);
        System.out.println("n Resamples: " + nSamples);
        System.out.println("Algorithm: " + rmhc.getClass().getSimpleName());
        System.out.println("Flip at least one? " + Mutator.flipAtLeastOneValue);
        System.out.println("Mutation probability: " + Mutator.defaultPointProb);

        for (int i=0; i<nReps; i++) {
            // System.out.println("Trial: " + i);
            testFHT.runTrial(rmhc);
//            System.out.println(testFHT.nTrueOpt);
//            System.out.println(testFHT.trueFit);
//            System.out.println(testFHT.nFalseOpt);
//            System.out.println();
        }

        System.out.println("Final results:");
        System.out.println(testFHT.trueFit);
        System.out.println(testFHT.nTrueOpt);
        System.out.println(testFHT.nFalseOpt);

    }

    public TestFHT(NoisySolutionEvaluator evaluator, int nFitnessEvals) {
        this.evaluator = evaluator;
        this.nFitnessEvals = nFitnessEvals;
        resetStats();
    }

    public void resetStats() {
        trueFit = new StatSummary("true fitness stats");
        nTrueOpt = new StatSummary("n true optima");
        nFalseOpt = new StatSummary("FHT optima");
    }

    NoisySolutionEvaluator evaluator;
    int nFitnessEvals;
    StatSummary trueFit;
    StatSummary nTrueOpt;
    StatSummary nFalseOpt;

    public ArrayList<Double> runTrial(EvoAlg ea) {

        evaluator.reset();

        int[] solution = ea.runTrial(evaluator, nFitnessEvals);
        // System.out.println("Solution: " + Arrays.toString(solution) + " : " + solutionEvaluator.trueFitness(solution));
        trueFit.add(evaluator.trueFitness(solution));

        // linePlots.add(new LinePlot().setData(solutionEvaluator.logger().fa).setColor(lineColor));

        // ok, instead look at the true fitnesses of the evaluated solutions

        ArrayList<Double> noiseFree = new ArrayList<>();
        // System.out.println("Best yet solutions length: " + solutionEvaluator.logger().bestYetSolutions.size());
        for (int[] p : evaluator.logger().bestYetSolutions) {
            noiseFree.add(evaluator.trueFitness(p));
        }

        if (evaluator.isOptimal(solution)) {
            nTrueOpt.add(1);
        } else {
            nTrueOpt.add(0);
        }
        if (evaluator.logger().firstHit != null) {
            nFalseOpt.add(1);
        } else {
            nFalseOpt.add(0);
        }
        return noiseFree;
    }




}
