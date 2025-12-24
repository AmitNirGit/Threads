package spl.lae;

import java.io.IOException;
import java.text.ParseException;

import parser.*;

public class Main {
    public static void main(String[] args) throws IOException {
        int numOfThreads = Integer.parseInt(args[0]);
        System.out.println("number of threads " + numOfThreads);

        String inputPath = args[1];
        String outputPath = args[2];

        InputParser parser = new InputParser();
        LinearAlgebraEngine engine = new LinearAlgebraEngine(numOfThreads);

        try {
            ComputationNode root = parser.parse(inputPath);
            ComputationNode result = engine.run(root);
            OutputWriter.write(result.getMatrix(), outputPath);
            System.out.println(engine.getWorkerReport());
        } catch (ParseException e) {
            OutputWriter.write("Error: " + e.getMessage(), outputPath);
        } finally {
            try {
                engine.shutdown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}