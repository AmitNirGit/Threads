import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import parser.*;
import spl.lae.*;
import java.util.List;

public class LinearAlgebraEngineTest {

    @Test
    public void testSimpleAddition() {
        double[][] m1 = {{1.0, 2.0}, {3.0, 4.0}};
        double[][] m2 = {{5.0, 6.0}, {7.0, 8.0}};
        
        ComputationNode n1 = new ComputationNode(m1);
        ComputationNode n2 = new ComputationNode(m2);
        ComputationNode addNode = new ComputationNode("+", List.of(n1, n2));
        
        LinearAlgebraEngine engine = new LinearAlgebraEngine(2);
        ComputationNode resultNode = engine.run(addNode);
        
        double[][] result = resultNode.getMatrix();
        assertEquals(6.0, result[0][0], 0.001);
        assertEquals(8.0, result[0][1], 0.001);
        assertEquals(10.0, result[1][0], 0.001);
        assertEquals(12.0, result[1][1], 0.001);
        
        try {
            engine.shutdown();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testMultiplication() {
        double[][] m1 = {{1.0, 2.0}, {3.0, 4.0}};
        double[][] m2 = {{5.0, 6.0}, {7.0, 8.0}};
        
        ComputationNode n1 = new ComputationNode(m1);
        ComputationNode n2 = new ComputationNode(m2);
        ComputationNode mulNode = new ComputationNode("*", List.of(n1, n2));
        
        LinearAlgebraEngine engine = new LinearAlgebraEngine(2);
        ComputationNode resultNode = engine.run(mulNode);
        
        double[][] result = resultNode.getMatrix();
        // [1 2; 3 4] * [5 6; 7 8] = [1*5+2*7 1*6+2*8; 3*5+4*7 3*6+4*8] = [19 22; 43 50]
        assertEquals(19.0, result[0][0], 0.001);
        assertEquals(22.0, result[0][1], 0.001);
        assertEquals(43.0, result[1][0], 0.001);
        assertEquals(50.0, result[1][1], 0.001);
        
        try {
            engine.shutdown();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testNegate() {
        double[][] m = {{1.0, -2.0}, {0.0, 3.0}};
        ComputationNode n = new ComputationNode(m);
        ComputationNode negNode = new ComputationNode("-", List.of(n));
        
        LinearAlgebraEngine engine = new LinearAlgebraEngine(2);
        ComputationNode resultNode = engine.run(negNode);
        
        double[][] result = resultNode.getMatrix();
        assertEquals(-1.0, result[0][0], 0.001);
        assertEquals(2.0, result[0][1], 0.001);
        assertEquals(0.0, result[1][0], 0.001);
        assertEquals(-3.0, result[1][1], 0.001);
        
        try {
            engine.shutdown();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testTranspose() {
        double[][] m = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
        ComputationNode n = new ComputationNode(m);
        ComputationNode tNode = new ComputationNode("T", List.of(n));
        
        LinearAlgebraEngine engine = new LinearAlgebraEngine(2);
        ComputationNode resultNode = engine.run(tNode);
        
        double[][] result = resultNode.getMatrix();
        assertEquals(3, result.length);
        assertEquals(2, result[0].length);
        assertEquals(1.0, result[0][0], 0.001);
        assertEquals(4.0, result[0][1], 0.001);
        assertEquals(2.0, result[1][0], 0.001);
        assertEquals(5.0, result[1][1], 0.001);
        assertEquals(3.0, result[2][0], 0.001);
        assertEquals(6.0, result[2][1], 0.001);
        
        try {
            engine.shutdown();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
