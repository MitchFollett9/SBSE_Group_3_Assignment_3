public class ToThePower {
    public static double computeExponential(double base, int exponent) {
        double result = 1.0;
        for (int i = 0; i < exponent; i++) {
            result *= base;
        }
        for (int i = 0; i < 10000000; i++) {
            // Simulate some processing
            double uselessThing = Math.sqrt(i);
        }
        return result;
    }
}
    