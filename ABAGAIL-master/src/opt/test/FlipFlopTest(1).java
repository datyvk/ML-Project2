package opt.test;

import java.util.Arrays;
import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.io.File;

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

import shared.writer.CSVWriter;

/**
 * A test using the flip flop evaluation function
 * 
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class FlipFlopTest{
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

        RandomizedHillClimbing rhc = new RandomizedHillClimbing(hcp);
        FixedIterationTrainer fit = new FixedIterationTrainer(rhc, 200000);
        fit.train();
        System.out.println(ef.value(rhc.getOptimal()));
        iterateOA(ef, hcp, gap, pop, fit);

        // SimulatedAnnealing sa = new SimulatedAnnealing(100, .95, hcp);
        // fit = new FixedIterationTrainer(sa, 200000);
        // fit.train();
        // System.out.println(ef.value(sa.getOptimal()));

        // StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(200, 100, 20,
        // gap);
        // fit = new FixedIterationTrainer(ga, 1000);
        // fit.train();
        // System.out.println(ef.value(ga.getOptimal()));

        // MIMIC mimic = new MIMIC(200, 5, pop);
        // fit = new FixedIterationTrainer(mimic, 1000);
        // fit.train();
        // System.out.println(ef.value(mimic.getOptimal()));
    }

    public static void iterateOA(EvaluationFunction ef, HillClimbingProblem hcp, GeneticAlgorithmProblem gap,
            ProbabilisticOptimizationProblem pop, FixedIterationTrainer fit) throws IOException {
        CSVWriter file = null;
        File outputFile = null;
        
        //Simulated Annealing Iterator
        double[] saMaxVals = {0,0,0};

        outputFile = new File("SimulatedAnnealingFlipFlop.csv");
        String[] saArr = {"Optimal", "Temp", "CR"};
        file = new CSVWriter(outputFile.getAbsolutePath(), saArr);
        file.open();
        for (int temp = 0; temp < 10000; temp+=500) {
            for (double cr = 0.1; cr < 1; cr+=.1) {
                SimulatedAnnealing sa = new SimulatedAnnealing(temp,cr, hcp);
                fit = new FixedIterationTrainer(sa, 200000);
                fit.train();
                double currOpt = ef.value(sa.getOptimal());
                double[] currVals = {currOpt, temp, cr};
                saMaxVals = currOpt >= saMaxVals[0] ? currVals : saMaxVals;

                for (double val: currVals) {
                    file.write(Double.toString(val));
                }
                    
                file.nextRecord();
            }
        }  
        System.out.printf("Simulated Annealing Max Values: %s\n", Arrays.toString(saMaxVals));
        file.close();

        // Genetic Algorithms iterator
        double[] gaMaxVals = {0,0,0};

        outputFile = new File("GeneticAlgorithmFlipFlop.csv");
        String[] gaArr = {"Optimal", "PopSize", "ToMate", "ToMutate"};
        file = new CSVWriter(outputFile.getAbsolutePath(), gaArr);
        file.open();
        for (int mate = 10; mate <= 200; mate+=20) {
            for (int mut = 5; mut < 50; mut+=5) {
                StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(200, mate, mut, gap);
                fit = new FixedIterationTrainer(ga, 1000);
                fit.train();
                double currOpt = ef.value(ga.getOptimal());
                double[] currVals = {currOpt, 200, mate, mut};
                gaMaxVals = currOpt >= gaMaxVals[0] ? currVals : gaMaxVals;
                for (double val: currVals) {
                    file.write(Double.toString(val));
                }
                    
                file.nextRecord();
            }
        }
        System.out.printf("Gen Alg Max Values: %s\n", Arrays.toString(gaMaxVals));

        file.close();

        // Mimic Iteration
        double[] mimMaxVals = {0,0,0};

        outputFile = new File("MimicAlgorithmFlipFlop.csv");
        String[] mimArr = {"Optimal", "Samples", "ToKeep"};
        file = new CSVWriter(outputFile.getAbsolutePath(), mimArr);
        file.open();
        for (int samp = 50; samp < 500; samp+=50) {
            for (int keep = 1; keep < 10; keep+=1) {
                MIMIC mimic = new MIMIC(samp, keep, pop);
                fit = new FixedIterationTrainer(mimic, 1000);
                fit.train();
                double currOpt = ef.value(mimic.getOptimal());
                double[] currVals = {currOpt, samp, keep};
                mimMaxVals = currOpt >= mimMaxVals[0] ? currVals : mimMaxVals;
                for (double val: currVals) {
                    file.write(Double.toString(val));
                }
                file.nextRecord();
            }
        }        
        System.out.printf("Mim Alg Max Values: %s\n", Arrays.toString(mimMaxVals));

        file.close();
    }
}
// MIMIC mimic = new MIMIC(200, 5, pop);
        // fit = new FixedIterationTrainer(mimic, 1000);
        // fit.train();
        // System.out.println(ef.value(mimic.getOptimal()));
