package opt.test;

import java.io.IOException;
import java.util.Arrays;

import dist.DiscreteDependencyTree;
import dist.DiscreteUniformDistribution;
import dist.Distribution;

import opt.DiscreteChangeOneNeighbor;
import opt.EvaluationFunction;
import opt.GenericHillClimbingProblem;
import opt.HillClimbingProblem;
import opt.NeighborFunction;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.example.*;
import opt.ga.CrossoverFunction;
import opt.ga.DiscreteChangeOneMutation;
import opt.ga.SingleCrossOver;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.StandardGeneticAlgorithm;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.FixedIterationTrainer;

/**
 * A test using the flip flop evaluation function
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class FlipFlopTest {
    /** The n value */
    private static final int N = 80;

    public static void main(String[] args) throws IOException {
        int[] ranges = new int[N];
        Arrays.fill(ranges, 2);
        EvaluationFunction ef = new FlipFlopEvaluationFunction();
        Distribution odd = new DiscreteUniformDistribution(ranges);
        NeighborFunction nf = new DiscreteChangeOneNeighbor(ranges);
        MutationFunction mf = new DiscreteChangeOneMutation(ranges);
        CrossoverFunction cf = new SingleCrossOver();
        Distribution df = new DiscreteDependencyTree(.1, ranges);
        HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
        GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
        ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);
        double start, end;
        start = System.nanoTime();
        RandomizedHillClimbing rhc = new RandomizedHillClimbing(hcp);      
        FixedIterationTrainer fit = new FixedIterationTrainer(rhc, 20000);
        fit.printer("RHC");
        System.out.println(ef.value(rhc.getOptimal()));
        end = System.nanoTime();
        System.out.println((end - start) / Math.pow(10, 9));

        start = System.nanoTime();
        SimulatedAnnealing sa = new SimulatedAnnealing(6000, .7, hcp);
        fit = new FixedIterationTrainer(sa, 20000);
        fit.printer("SA");
        System.out.println(ef.value(sa.getOptimal()));
        end = System.nanoTime();
        System.out.println((end - start) / Math.pow(10, 9));

        start = System.nanoTime();
        StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(200, 190, 20, gap);
        fit = new FixedIterationTrainer(ga, 20000);
        fit.printer("GA");
        System.out.println(ef.value(ga.getOptimal()));
        end = System.nanoTime();
        System.out.println((end - start) / Math.pow(10, 9));

        start = System.nanoTime();
        MIMIC mimic = new MIMIC(200, 3, pop);
        fit = new FixedIterationTrainer(mimic, 20000);
        fit.printer("MIMIC");
        System.out.println(ef.value(mimic.getOptimal()));
        end = System.nanoTime();
        System.out.println((end - start) / Math.pow(10, 9));
    }
}
