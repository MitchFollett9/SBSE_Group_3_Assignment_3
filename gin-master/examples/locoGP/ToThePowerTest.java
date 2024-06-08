
import static org.junit.Assert.*;

public class ToThePowerTest {
    private void testExponential(int a, int b, int result) {
        double sortingAttempt = ToThePower.computeExponential(a, b);
        assertEquals((int)sortingAttempt, result);
    }

    @org.junit.Test
    public void test1() throws Exception {
        testExponential(2, 2, 4);
    }

    @org.junit.Test
    public void test2() throws Exception {
        testExponential(3, 2, 9);
    }

    @org.junit.Test
    public void test3() throws Exception {
        testExponential(2, 18, 262144);
    }

    @org.junit.Test
    public void test4() throws Exception {
        testExponential(3, 14, 4782969);
    }

    @org.junit.Test
    public void test5() throws Exception {
        testExponential(60, 3, 216000);
    }

}
