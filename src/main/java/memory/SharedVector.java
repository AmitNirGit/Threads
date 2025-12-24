package memory;

import java.util.concurrent.locks.ReadWriteLock;

import parser.OutputWriter;

public class SharedVector {

    private double[] vector;
    private VectorOrientation orientation;
    private ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();

    public SharedVector(double[] vector, VectorOrientation orientation) {
        this.vector = vector;
        this.orientation = orientation;
    }

    public double get(int index) {
        readLock();
        double val = vector[index];
        readUnlock();
        return val;
    }

    // Get value without locking
    double getUnsafe(int index) {
        return vector[index];
    }

    public int length() {
        return vector.length;
    }

    public VectorOrientation getOrientation() {
        readLock();
        VectorOrientation val = orientation;
        readUnlock();
        return val;
    }

    public void writeLock() {
        lock.writeLock().lock();
    }

    public void writeUnlock() {
        lock.writeLock().unlock();
    }

    public void readLock() {
        lock.readLock().lock();
    }

    public void readUnlock() {
        lock.readLock().unlock();
    }

    public void transpose() {
        writeLock();
        if (orientation == VectorOrientation.ROW_MAJOR) {
            orientation = VectorOrientation.COLUMN_MAJOR;
        } else {
            orientation = VectorOrientation.ROW_MAJOR;
        }
        writeUnlock();
    }

    public void add(SharedVector other) {
        writeLock();
        other.readLock();
        if (other.length() != length()) {
            throw new Error("error");
        }
        for (int i = 0; i < vector.length; i++) {
            vector[i] = vector[i] + other.vector[i];
        }
        writeUnlock();
        other.readUnlock();
    }

    public void negate() {
        writeLock();
        for (int i = 0; i < vector.length; i++) {
            vector[i] = vector[i] * -1;
        }
        writeUnlock();
    }

    public double dot(SharedVector other) {
        readLock();
        other.readLock();
        if (this.orientation == other.orientation) {
            throw new Error("Invalid vectors, vectors has the same orientation");
        }
        if (this.length() != other.length()) {
            throw new Error("Invalid vectors, vectors has different lengths");
        }
        double result = 0;
        for (int i = 0; i < vector.length; i++) {
            result = result + vector[i] * other.vector[i];
        }
        readUnlock();
        other.readUnlock();
        return result;
    }

    public void vecMatMul(SharedMatrix matrix) {
        writeLock();
        double[][] martixCopy = matrix.readRowMajor();
        if (martixCopy.length == 0 || martixCopy[0].length == 0) {
            writeUnlock();
            throw new Error("Cannot multiply with empty matrix");
        }
        if (vector.length != martixCopy.length) {
            writeUnlock();
            throw new Error("Vector length must match number of matrix rows");
        }
        
        double[] newVector = new double[martixCopy[0].length];

        for (int i = 0; i < martixCopy[0].length; i++) {
            double sum = 0;
            for (int j = 0; j < vector.length; j++) {
                sum = sum + vector[j] * martixCopy[j][i];
            }
            newVector[i] = sum;
        }
        this.vector = newVector;
        writeUnlock();
    }
}
