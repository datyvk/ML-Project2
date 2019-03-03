package opt.test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import dist.DiscreteDependencyTree;
import dist.DiscretePermutationDistribution;
import dist.DiscreteUniformDistribution;
import dist.Distribution;

import opt.*;
import opt.example.*;
import opt.ga.CrossoverFunction;
import opt.ga.SwapMutation;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.StandardGeneticAlgorithm;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.FixedIterationTrainer;
import shared.writer.CSVWriter;

/**
 * 
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class TravelingSalesmanTest {
    /**
     * The n value
     */
    private static final int N = 50;

    /**
     * The test main
     *
     * @param args ignored
     */
    public static void main(String[] args) throws IOException {
        Random random = new Random();
        // create the random points
        double[][] points = new double[N][2];
        for (int i = 0; i < points.length; i++) {
            points[i][0] = random.nextDouble();
            points[i][1] = random.nextDouble();
        }
        // for rhc, sa, and ga we use a permutation based encoding
        TravelingSalesmanEvaluationFunction ef = new TravelingSalesmanRouteEvaluationFunction(points);
        Distribution odd = new DiscretePermutationDistribution(N);
        NeighborFunction nf = new SwapNeighbor();
        MutationFunction mf = new SwapMutation();
        CrossoverFunction cf = new TravelingSalesmanCrossOver(ef);
        HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
        GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
        double start, end;
        start = System.nanoTime();
        RandomizedHillClimbing rhc = new RandomizedHillClimbing(hcp);
        FixedIterationTrainer fit = new FixedIterationTrainer(rhc, 20000);
        fit.printer("rhc");
        System.out.println(ef.value(rhc.getOptimal()));
        end = System.nanoTime();
        System.out.println((end - start) / Math.pow(10, 9));

        start = System.nanoTime();
        SimulatedAnnealing sa = new SimulatedAnnealing(8500, .2, hcp);
        fit = new FixedIterationTrainer(sa, 20000);
        fit.printer("sa");
        System.out.println(ef.value(sa.getOptimal()));
        end = System.nanoTime();
        System.out.println((end - start) / Math.pow(10, 9));

        start = System.nanoTime();
        StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(200, 75, 15, gap);
        fit = new FixedIterationTrainer(ga, 20000);
        fit.printer("ga");
        System.out.println(ef.value(ga.getOptimal()));
        end = System.nanoTime();
        System.out.println((end - start) / Math.pow(10, 9));

        // for mimic we use a sort encoding
        ef = new TravelingSalesmanSortEvaluationFunction(points);
        int[] ranges = new int[N];
        Arrays.fill(ranges, N);
        odd = new  DiscreteUniformDistribution(ranges);
        Distribution df = new DiscreteDependencyTree(.1, ranges);
        ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);

        start = System.nanoTime();
        MIMIC mimic = new MIMIC(450, 3, pop);
        fit = new FixedIterationTrainer(mimic, 20000);
        fit.printer("MIMIC");
        System.out.println(ef.value(mimic.getOptimal()));
        end = System.nanoTime();
        System.out.println((end - start) / Math.pow(10, 9));


    }
}
