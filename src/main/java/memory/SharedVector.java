package memory;

import java.util.concurrent.locks.ReadWriteLock;

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

    int lengthUnsafe() {
        return vector.length;
    }

    public int length() {
        readLock();
        int length = vector.length;
        readUnlock();
        return length;
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
        // Ensure consistent lock ordering to prevent deadlock
        if (System.identityHashCode(this) < System.identityHashCode(other)) {
            writeLock();
            other.readLock();
            try {
                if (other.lengthUnsafe() != lengthUnsafe()) {
                    writeUnlock();  
                    other.readUnlock();
                    throw new Error("Vectors have different lengths");
                }
                for (int i = 0; i < vector.length; i++) {
                    vector[i] = vector[i] + other.vector[i];
                }
            } finally {
                other.readUnlock();
                writeUnlock();
            }
        } else {
            other.readLock();
            writeLock();
            try {
                if (other.lengthUnsafe() != lengthUnsafe()) {
                    writeUnlock();  
                    other.readUnlock();
                    throw new Error("Vectors have different lengths");
                }
                for (int i = 0; i < vector.length; i++) {
                    vector[i] = vector[i] + other.vector[i];
                }
            } finally {
                writeUnlock();
                other.readUnlock();
            }
        }
    }

    public void negate() {
        writeLock();
        for (int i = 0; i < vector.length; i++) {
            vector[i] = vector[i] * -1;
        }
        writeUnlock();
    }

    public double dot(SharedVector other) {
        // Ensure consistent lock ordering to prevent deadlock
        if (System.identityHashCode(this) < System.identityHashCode(other)) {
            readLock();
            other.readLock();
            try {
                if (this.orientation == other.orientation) {
                    throw new Error("Invalid vectors, vectors has the same orientation");
                }
                if (this.lengthUnsafe() != other.lengthUnsafe()) {
                    throw new Error("Invalid vectors, vectors has different lengths");
                }
                double result = 0;
                for (int i = 0; i < vector.length; i++) {
                    result = result + vector[i] * other.vector[i];
                }
                return result;
            } finally {
                other.readUnlock();
                readUnlock();
            }
        } else {
            other.readLock();
            readLock();
            try {
                if (this.orientation == other.orientation) {
                    throw new Error("Invalid vectors, vectors has the same orientation");
                }
                if (this.lengthUnsafe() != other.lengthUnsafe()) {
                    throw new Error("Invalid vectors, vectors has different lengths");
                }
                double result = 0;
                for (int i = 0; i < vector.length; i++) {
                    result = result + vector[i] * other.vector[i];
                }
                return result;
            } finally {
                readUnlock();
                other.readUnlock();
            }
        }
    }

    public void vecMatMul(SharedMatrix matrix) {
        double[][] martixCopy = matrix.readRowMajor();
        if (martixCopy.length == 0 || martixCopy[0].length == 0) {
            throw new Error("Cannot multiply with empty matrix");
        }
        if (vector.length != martixCopy.length) {
            throw new Error("Vector length must match number of matrix rows");
        }
        int matrixWidth = martixCopy[0].length;
        writeLock();
        
        double[] newVector = new double[matrixWidth];

        for (int i = 0; i < matrixWidth; i++) {
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
