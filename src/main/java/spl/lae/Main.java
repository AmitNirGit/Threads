package spl.lae;
import java.io.IOException;
import java.text.ParseException;

import parser.*;

public class Main {
    public static void main(String[] args) throws IOException {
        int numOfThreads = Integer.parseInt(args[0]);
        InputParser parser = new InputParser();
        try {
            ComputationNode root = parser.parse(args[1]);
            LinearAlgebraEngine LAE = new LinearAlgebraEngine(numOfThreads);
            OutputWriter.write("Parsing successful", args[2]);
        } catch (ParseException e) {
            OutputWriter.write("Error: " + e.getMessage(), args[2]);
        }
    }
}