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
        double vec = vector[index];
        readUnlock();
        return vec;
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
        // TODO: compute row-vector × matrix
    }
}
