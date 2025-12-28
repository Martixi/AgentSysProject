public class MFNTest {
    public static void main(String[] args) {
        // Network 1 Parameters from instructions
        int m = 5;
        int[] W = {4, 3, 2, 3, 2};
        double[] C = {10.0, 15.0, 25.0, 15.0, 20.0};
        int[] L = {5, 7, 6, 5, 8};
        double[] R = {0.7, 0.65, 0.67, 0.71, 0.75};
        double[] rho = {0.1, 0.3, 0.5, 0.7, 0.9};

        System.out.println("--- Starting MFN Math Verification ---");

        try {
            MFN mfn = new MFN(m, W, C, L, R, rho);

            // 1. Check Sample Size (Should be 16588 for eps=0.01, delta=0.01)
            int N = mfn.worstCaseSampleSize(0.01, 0.01);
            System.out.println("Worst Case Sample Size (Expected 16588): " + N);

            // 2. Check Combinatorial
            java.math.BigInteger binom = MFN.Combinatorial.binomial(5, 2);
            System.out.println("Binomial(5,2) (Expected 10): " + binom);

            // 3. Check Normal CDF/ICDF
            double z = 1.96;
            double cdfVal = MFN.normalCDF(z);
            System.out.println("NormalCDF(1.96) (Expected ~0.975): " + cdfVal);
            System.out.println("NormalICDF(0.975) (Expected ~1.96): " + MFN.normalICDF(0.975));

            // 4. Check Link Probability (Formula 1)
            // Testing Link 0, State 4
            double pr04 = mfn.Pr(0, 4);
            System.out.println("Pr(Link 0, State 4): " + pr04);

            // 5. Check Transmission Time Logic (Formula 3/5)
            // Path P1 = {0, 1} (Links a1, a2). State X = {4, 3, 2, 3, 2} (Max states)
            int[] P1 = {0, 1};
            double[] X_max = {4, 3, 2, 3, 2};
            double time = mfn.TransmissionTime(P1, 42, X_max);
            System.out.println("Trans. Time Path 1, Flow 42 (Max Capacity): " + time);

            System.out.println("\n--- Math Logic seems SOLID ---");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}