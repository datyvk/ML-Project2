package shared;

import shared.writer.CSVWriter;

import java.io.File;
import java.io.IOException;

/**
 * A fixed iteration trainer
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class FixedIterationTrainer implements Trainer {
    
    /**
     * The inner trainer
     */
    private Trainer trainer;
    
    /**
     * The number of iterations to train
     */
    private int iterations;
    
    /**
     * Make a new fixed iterations trainer
     * @param t the trainer
     * @param iter the number of iterations
     */
    public FixedIterationTrainer(Trainer t, int iter) {
        trainer = t;
        iterations = iter;
    }

    /**
     * @see shared.Trainer#train()
     */
    public double train() {
        double sum = 0;
        for (int i = 0; i < iterations; i++) {
            sum += trainer.train();
        }
        return sum / iterations;
    }
    public double printer(String alg) throws IOException {

        CSVWriter file = null;
        File outputFile = null;
        outputFile = new File("part 2 "+alg+ ".csv");
        String[] saArr = {"iteration", alg};
        file = new CSVWriter(outputFile.getAbsolutePath(), saArr);
        file.open();
        double sum = 0;
        for (int i = 0; i < iterations; i++) {
            sum += trainer.train();
            double fitness = sum / i;
            if (i % 100 == 0 && i > 0) {
                double[] currVal = {i, fitness};
                for (double val: currVal)
                    file.write(Double.toString(val));
                file.nextRecord();
            }

        }
        file.close();
        return sum / iterations;
    }
    

}
