package spl.lae;

import parser.*;
import memory.*;
import scheduling.*;

import java.util.ArrayList;
import java.util.List;

public class LinearAlgebraEngine {

    private SharedMatrix leftMatrix = new SharedMatrix();
    private SharedMatrix rightMatrix = new SharedMatrix();
    private TiredExecutor executor;

    public LinearAlgebraEngine(int numThreads) {
        this.executor = new TiredExecutor(numThreads);
    }

    public ComputationNode run(ComputationNode computationRoot) {
        computationRoot.associativeNesting();
        while (computationRoot.getNodeType() != ComputationNodeType.MATRIX) {
            ComputationNode resolveable = computationRoot.findResolvable();
            loadAndCompute(resolveable);
        }
        return computationRoot;
    }

    public void loadAndCompute(ComputationNode node) {
        ComputationNodeType nodeType = node.getNodeType();
        this.leftMatrix = new SharedMatrix(node.getChildren().get(0).getMatrix());

        if (node.getChildren().size() > 1) {
            this.rightMatrix = new SharedMatrix(node.getChildren().get(1).getMatrix());
        } else {
            this.rightMatrix = null;
        }

        List<Runnable> tasks;
        switch (nodeType) {
            case ComputationNodeType.ADD:
                tasks = createAddTasks();
                break;
            case ComputationNodeType.MULTIPLY:
                tasks = createMultiplyTasks();
                break;
            case ComputationNodeType.NEGATE:
                tasks = createNegateTasks();
                break;
            case ComputationNodeType.TRANSPOSE:
                tasks = createTransposeTasks();
                break;
            default:
                return;
        }

        if (tasks != null) {
            executor.submitAll(tasks);
        }
        // Update the node with the result matrix
        node.resolve(leftMatrix.readRowMajor());
    }

    public List<Runnable> createAddTasks() {
        if (rightMatrix == null || leftMatrix == null) {
            throw new Error("Need at least 2 matrices to add");
        }
        List<Runnable> tasks = new ArrayList<Runnable>();
        for (int i = 0; i < leftMatrix.length(); i++) {
            final int rowIndex = i;
            Runnable task = () -> leftMatrix.get(rowIndex).add(rightMatrix.get(rowIndex));
            tasks.add(task);
        }
        return tasks;
    }

    public List<Runnable> createMultiplyTasks() {
        if (rightMatrix == null || leftMatrix == null) {
            throw new Error("Need at least 2 matrix to multiply");
        }
        List<Runnable> tasks = new ArrayList<Runnable>();
        for (int i = 0; i < leftMatrix.length(); i++) {
            final int rowIndex = i;
            Runnable task = () -> leftMatrix.get(rowIndex).vecMatMul(rightMatrix);
            tasks.add(task);
        }
        return tasks;
    }

    public List<Runnable> createNegateTasks() {
        if (rightMatrix != null) {
            throw new Error("Unary opeartion can get only 1 matrix");
        }
        List<Runnable> tasks = new ArrayList<Runnable>();
        for (int i = 0; i < leftMatrix.length(); i++) {
            final int rowIndex = i;
            Runnable task = () -> leftMatrix.get(rowIndex).negate();
            tasks.add(task);
        }
        return tasks;
    }

    public List<Runnable> createTransposeTasks() {
        if (rightMatrix != null) {
            throw new Error("Unary opeartion can get only 1 matrix");
        }
        List<Runnable> tasks = new ArrayList<Runnable>();
        for (int i = 0; i < leftMatrix.length(); i++) {
            final int rowIndex = i;
            Runnable task = () -> leftMatrix.get(rowIndex).transpose();
            tasks.add(task);
        }
        return tasks;
    }

    public String getWorkerReport() {
        return executor.getWorkerReport();
    }

    public void shutdown() throws InterruptedException {
        executor.shutdown();
    }
}
