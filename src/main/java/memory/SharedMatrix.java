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
        
        boolean transposed = false;
        if (getOrientation() == VectorOrientation.COLUMN_MAJOR) {
            acquireAllVectorWriteLocks(vectors);
            transposeMatrix();
            transposed = true;
            releaseAllVectorWriteLocks(vectors);
        }
        
        acquireAllVectorReadLocks(vectors);
        double[][] newMartix = new double[vectors.length][vectors[0].length()];

        for (int i = 0; i < vectors.length; i++) {
            for (int j = 0; j < vectors[i].length(); j++) {
                newMartix[i][j] = vectors[i].getUnsafe(j);
            }
        }
        releaseAllVectorReadLocks(vectors);

        // Transpose back to original orientation if needed
        if (transposed) {
            acquireAllVectorWriteLocks(vectors);
            transposeMatrix();
            releaseAllVectorWriteLocks(vectors);
        }
        
        return newMartix;
    }

    public SharedVector get(int index) {
        return vectors[index];
    }

    public int length() {
        return vectors.length;
    }

    public VectorOrientation getOrientation() {
        if (vectors.length == 0)
            return VectorOrientation.ROW_MAJOR;
        else
            return vectors[0].getOrientation();
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

    public SharedVector[] getVectors(){
        return this.vectors;
    }
}
