import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import memory.*;

public class SharedVectorTest {

    @Test
    public void testAdd() {
        double[] data1 = {1.0, 2.0, 3.0};
        double[] data2 = {4.0, 5.0, 6.0};
        SharedVector v1 = new SharedVector(data1, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(data2, VectorOrientation.ROW_MAJOR);
        
        v1.add(v2);
        
        assertEquals(5.0, v1.get(0), 0.001);
        assertEquals(7.0, v1.get(1), 0.001);
        assertEquals(9.0, v1.get(2), 0.001);
    }

    @Test
    public void testAddDifferentLengths() {
        double[] data1 = {1.0, 2.0};
        double[] data2 = {1.0, 2.0, 3.0};
        SharedVector v1 = new SharedVector(data1, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(data2, VectorOrientation.ROW_MAJOR);
        
        assertThrows(Error.class, () -> v1.add(v2));
    }

    @Test
    public void testNegate() {
        double[] data = {1.0, -2.0, 0.0};
        SharedVector v = new SharedVector(data, VectorOrientation.ROW_MAJOR);
        
        v.negate();
        
        assertEquals(-1.0, v.get(0), 0.001);
        assertEquals(2.0, v.get(1), 0.001);
        assertEquals(0.0, v.get(2), 0.001);
    }

    @Test
    public void testDot() {
        double[] data1 = {1.0, 2.0, 3.0};
        double[] data2 = {4.0, 5.0, 6.0};
        SharedVector v1 = new SharedVector(data1, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(data2, VectorOrientation.COLUMN_MAJOR);
        
        double result = v1.dot(v2);
        
        // 1*4 + 2*5 + 3*6 = 4 + 10 + 18 = 32
        assertEquals(32.0, result, 0.001);
    }

    @Test
    public void testDotSameOrientation() {
        double[] data1 = {1.0, 2.0, 3.0};
        double[] data2 = {4.0, 5.0, 6.0};
        SharedVector v1 = new SharedVector(data1, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(data2, VectorOrientation.ROW_MAJOR);
        
        assertThrows(Error.class, () -> v1.dot(v2));
    }

    @Test
    public void testTranspose() {
        double[] data = {1.0, 2.0};
        SharedVector v = new SharedVector(data, VectorOrientation.ROW_MAJOR);
        
        assertEquals(VectorOrientation.ROW_MAJOR, v.getOrientation());
        v.transpose();
        assertEquals(VectorOrientation.COLUMN_MAJOR, v.getOrientation());
        v.transpose();
        assertEquals(VectorOrientation.ROW_MAJOR, v.getOrientation());
    }

    @Test
    public void testVecMatMul() {
        double[] vecData = {1.0, 2.0};
        double[][] matData = {
            {3.0, 4.0},
            {5.0, 6.0}
        };
        SharedVector v = new SharedVector(vecData, VectorOrientation.ROW_MAJOR);
        SharedMatrix m = new SharedMatrix(matData);
        
        v.vecMatMul(m);
        
        // [1 2] * [3 4; 5 6] = [1*3+2*5, 1*4+2*6] = [13, 16]
        assertEquals(13.0, v.get(0), 0.001);
        assertEquals(16.0, v.get(1), 0.001);
    }
}
