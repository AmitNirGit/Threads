package memory;

public class SharedMatrix {

    private volatile SharedVector[] vectors = {}; // underlying vectors

    public SharedMatrix() {
        this.vectors = new SharedVector[0];
    }

    public SharedMatrix(double[][] matrix) {
        int numOfRows = matrix.length;
        SharedVector newMartix[] = new SharedVector[numOfRows];

        for (int i = 0; i < newMartix.length; i++) {
            newMartix[i] = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);
        }

        this.vectors = newMartix;
    }

    public void loadRowMajor(double[][] matrix) {
        int numOfRows = matrix.length;
        SharedVector newMartix[] = new SharedVector[numOfRows];

        for (int i = 0; i < newMartix.length; i++) {
            newMartix[i] = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);
        }

        this.vectors = newMartix;
    }

    public void loadColumnMajor(double[][] matrix) {
        int numOfRows = matrix.length;
        SharedVector newMartix[] = new SharedVector[numOfRows];

        for (int i = 0; i < newMartix.length; i++) {
            newMartix[i] = new SharedVector(matrix[i], VectorOrientation.COLUMN_MAJOR);
        }

        this.vectors = newMartix;
    }

    public double[][] readRowMajor() {
        if (isEmpty())
            return new double[0][0];

        acquireAllVectorReadLocks(vectors);
        VectorOrientation orientation = getOrientation();
        double[][] martixCopy;
        
        if (orientation == VectorOrientation.ROW_MAJOR) {
            martixCopy = new double[vectors.length][vectors[0].lengthUnsafe()];
            for (int i = 0; i < vectors.length; i++) {
                for (int j = 0; j < vectors[i].lengthUnsafe(); j++) {
                    martixCopy[i][j] = vectors[i].getUnsafe(j);
                }
            }
        } else {

            int numRows = vectors[0].lengthUnsafe();
            int numCols = vectors.length;
            martixCopy = new double[numRows][numCols];
            for (int i = 0; i < numCols; i++) {
                for (int j = 0; j < numRows; j++) {
                    martixCopy[j][i] = vectors[i].getUnsafe(j);
                }
            }
        }

        releaseAllVectorReadLocks(vectors);

        return martixCopy;
    }

    public SharedVector get(int index) {
        if (index < 0 || index >= vectors.length) {
            throw new IndexOutOfBoundsException(
                    "Index " + index + " is out of bounds for matrix of size " + vectors.length);
        }
        return vectors[index];
    }

    public int length() {
        return vectors.length;
    }

    public VectorOrientation getOrientation() {
        SharedVector[] currentVectors = vectors; // Single volatile read - ensures consistency
        if (currentVectors.length == 0)
            return VectorOrientation.ROW_MAJOR;
        SharedVector firstVector = currentVectors[0];
        if (firstVector == null)
            return VectorOrientation.ROW_MAJOR;
        // SharedVector.getOrientation() handles its own locking
        return firstVector.getOrientation();
    }

    private void acquireAllVectorReadLocks(SharedVector[] vecs) {
        for (SharedVector sharedVector : vecs) {
            sharedVector.readLock();
        }
    }

    private void releaseAllVectorReadLocks(SharedVector[] vecs) {
        for (SharedVector sharedVector : vecs) {
            sharedVector.readUnlock();
        }
    }

    private void acquireAllVectorWriteLocks(SharedVector[] vecs) {
        for (SharedVector sharedVector : vecs) {
            sharedVector.writeLock();
        }
    }

    private void releaseAllVectorWriteLocks(SharedVector[] vecs) {
        for (SharedVector sharedVector : vecs) {
            sharedVector.writeUnlock();
        }
    }

    private boolean isEmpty() {
        return vectors.length == 0;
    }

    // please notice matrix is locked!
    private void transposeMatrix() {
        for (SharedVector sharedVector : vectors) {
            sharedVector.transpose();
        }
    }
}
