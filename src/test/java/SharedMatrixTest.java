import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import memory.*;

public class SharedMatrixTest {

    @Test
    public void testReadRowMajorRowMajor() {
        double[][] data = {
            {1.0, 2.0},
            {3.0, 4.0}
        };
        SharedMatrix m = new SharedMatrix(data);
        
        double[][] result = m.readRowMajor();
        
        assertArrayEquals(data[0], result[0], 0.001);
        assertArrayEquals(data[1], result[1], 0.001);
    }

    @Test
    public void testReadRowMajorColumnMajor() {
        double[][] data = {
            {1.0, 2.0},
            {3.0, 4.0}
        };
        SharedMatrix m = new SharedMatrix();
        m.loadColumnMajor(data);

        double[][] result = m.readRowMajor();
        
        assertEquals(1.0, result[0][0], 0.001);
        assertEquals(3.0, result[0][1], 0.001);
        assertEquals(2.0, result[1][0], 0.001);
        assertEquals(4.0, result[1][1], 0.001);
    }

    @Test
    public void testGetAndLength() {
        double[][] data = {
            {1.0, 2.0, 3.0},
            {4.0, 5.0, 6.0}
        };
        SharedMatrix m = new SharedMatrix(data);
        
        assertEquals(2, m.length());
        assertEquals(3.0, m.get(0).get(2), 0.001);
        assertEquals(4.0, m.get(1).get(0), 0.001);
    }

    @Test
    public void testOutOfBounds() {
        double[][] data = {{1.0}};
        SharedMatrix m = new SharedMatrix(data);
        
        assertThrows(IndexOutOfBoundsException.class, () -> m.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> m.get(1));
    }

    @Test
    public void testIsEmpty() {
        SharedMatrix m = new SharedMatrix();
        // Since length() is public but isEmpty() is private, we test length()
        assertEquals(0, m.length());
        assertArrayEquals(new double[0][0], m.readRowMajor());
    }
}
