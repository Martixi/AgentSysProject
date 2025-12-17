import java.io.*;
import java.util.*;
import java.math.*;

/*
    Maksymalnie 25 punktow za klase MFN
    do tej pory zdobyte: 14 pkt
 */

public class MFN {

    /*
    In particular, the MFN class should contain
    the fields necessary to implement the task
    1pts DONE
     */

    // variables required in projects instruction
    private int m;                  // the number of links
    private int[] W;                // the component number vector
    private double[] C;             // the component capacity vector
    private int[] L;                // the lead time vector
    private double[] R;             // the component reliability vector
    private double[] rho;           // the vector of the correlation between the faults of the components
    private double[] beta;          // the beta vector
    private ArrayList<int[]> MPs;   // the list of minimal paths

    /*
    The constructor of the MFN class should do the following:
        -check whether the length of vectors W, C, L, R, and rho is equal to m;
        -check whether all values of R and rho are between 0 and 1;
        -create the beta vector if the above-mentioned conditions are satisfied.
    2pts DONE
     */

    public MFN(int m, int[] W, double[] C, int[] L, double[] R, double[] rho) {
        this.m = m;
        // check whether the length of vectors W, C, L, R, and rho is equal to m
        if (W.length != m || C.length != m || L.length != m || R.length != m || rho.length != m) {
            throw new IllegalArgumentException("Wrong parameters: wrong length of one or more vectors");
        }
        // check whether all values of R and rho are between 0 and 1
        for (double r : R){
            if (r < 0 || r > 1) { throw new IllegalArgumentException("Wrong parameters: Reliability should be between 0 and 1"); }
        }

        for (double h : rho){
            if (h < 0 || h > 1){ throw new IllegalArgumentException("Wrong parameters: Correlation should be between 0 and 1"); }
        }

        // everything is fine
        this.W = Arrays.copyOf(W, m);
        this.C = Arrays.copyOf(C, m);
        this.L = Arrays.copyOf(L, m);
        this.R = Arrays.copyOf(R, m);
        this.rho = Arrays.copyOf(rho, m);

        // conditions correct -> create the beta vector
        this.beta = new double[m];

        // formula (2):
        for (int i = 0; i < m; i++) {
            this.beta[i] = 1 + (rho[i]*(1- this.R[i])/this.R[i]);
        }
    }

    /*
    The MFN class should contain the inner class Combinatorial implementing
    methods needed to compute factorial 𝑛! and binomial coefficient (n k )
    2pts
    */
    public static class Combinatorial {

        // factorial 𝑛!
        public static BigInteger factorialBig(int n) {
            if (n < 0) throw new IllegalArgumentException();
            BigInteger result = BigInteger.ONE;
            for (int i = 2; i <= n; i++) {
                result = result.multiply(BigInteger.valueOf(i));
            }
            return result;
        }

        // binomial coefficient
        /*
        here we first do some fractions shortening (skracanie ułamków) in order to save memory
        we implement formula with symmetry optimization: k=min(k, n-k)
        we use min because we can "cross out" from numerator and denominator common ingredients
         */
        public static BigInteger binomial(int n, int k) {
            if (n < 0 || k < 0 || k > n) {
                return BigInteger.ZERO;
            }

            k = Math.min(k, n-k);
            BigInteger result = BigInteger.ONE;

            for (int i =1; i<=k; i++) {
                result = result.multiply(BigInteger.valueOf(n - 1 + 1)).divide(BigInteger.valueOf(i));
            }
            return result;
            }


    /*
    The MFN class should implement methods defined by formula (1), (3) - (5), and (8) from [1]
    5pts
     */

    // formula (1) ->
    public double Pr(int i, int k) {
        // Validate first to avoid IndexOutOfBounds errors
        if (i < 0 || i >= this.m || k < 0 || k > this.W[i]) {
            throw new IllegalArgumentException("Invalid index i or state k.");
        }

        // local variables
        double w_i = this.W[i];
        double r_i = this.R[i];
        double beta_i = this.beta[i];

        // calculation based on Formula (1)
        if (k >= 1) {
            // convert BigInteger result to double for the formula
            double binom = Combinatorial.binomial((int) w_i, k).doubleValue();

            // this throws error -> cannot convert from BigInteger to long: long binomialCoefficient = Combinatorial.binomial(w_i, k);

            double term2 = Math.pow(r_i*beta_i, k);
            double term3 = Math.pow(1-r_i*beta_i, w_i-k);

            return (1.0 / betai) * binomialCoefficient * term2 * term3;
        } else {
            double term1 = Math.pow(1-r_i*beta_i, w_i);
            return 1 - (1 / beta_i)*(1 - term1);
        }
    }

    // sprawdzić logikę czy to się zgadza

        // formula (5) ->
        public double pathCapacity(int[] P, double[] X) {
            double minCap = Double.MAX_VALUE;
            if (X.length != this.m) {
                throw new IllegalArgumentException("The given array length is not equal to the matrix.");
            }
            if (path.length == 0) {
                return 0;
            }
            double result = P[0];

            for (int i : P) {
                if (i < 0 || i >= this.m) {
                    throw new IllegalArgumentException("Invalid link index in the path array.");
                }
                double capacity = X[i];
                if (capacity < result) {
                    result = capacity;
                }
            }

            return result;

        }
    // formula (3) ->
    public double TransmissionTime(int P, double d, double[] X) {
        double pathCapacity = this.pathCapacity(P, X);
        if (pathCapacity <= 0) {
            return Double.POSITIVE_INFINITY;
        } else {
            double leadTime = this.pathLeadTime(P);

            double term2 = d / pathCapacity;
            // what is this
            double term3 = Math.ceil(term2);

            return leadTime + term3;
        }
    }


    // sprawdzić logikę czy to się zgadza
    // formula (4) ->
    public int pathLeadTime(int[] P) {
        int result = 0;

        for (int i : P) {
            if (i < 0 || i >= this.m) {
                throw new IllegalArgumentException("Invalid link index in the path array.");
            }
            result += this.L[i];
        }
        return result;
    }



    // formula (8) ->
    public double MinTransmissionTime(double d, double[] capacityStateVector) {

        double minNetworkTime = Double.POSITIVE_INFINITY;

        for (int[] path : this.MPs) {

            double pathTransmissionTime = this.calculateTransmissionTime(path, d, capacityStateVector);
            if (pathTransmissionTime < minNetworkTime) {
                minNetworkTime = pathTransmissionTime;
            }
        }

        return minNetworkTime;
    }

    /*
    Apart from this, the MFN class should also implement additional methods:
     */

    /*
    void getMPs(String fileName) that reads the file with
    the file name = filename and creates ArrayList<int[]> MPs
    3 pts DONE
     */
    void getMPs (String fileName) {

        this.MPs = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            // Read the file line by line
            while ((line = br.readLine()) != null) {
                line = line.trim();

                // Skip empty lines
                if (line.isEmpty()) continue;

                // Split the line by comma and store tokens
                String[] tokens = line.split(",");

                List<Integer> numbersList = new ArrayList<>();

                for (String token : tokens) {
                    token = token.trim();
                    if (!token.isEmpty()) {
                        try {
                            numbersList.add(Integer.parseInt(token))
                        } catch (NumberFormatException e) {
                            // Handle case where a token is not a valid integer
                            System.err.println("Skipping non-integer token: " + token);
                        }
                    }
                }
                int[] numbers = numbersList.stream().mapToInt(i -> i).toArray();
                if (numbers.length > 0) {
                    this.MPs.add(numbers);
                }
            }
        } catch (FileNotFoundException e) {
            // the file doesn't exist
            System.err.println("Error: File not found");
            e.printStackTrace();
        } catch (IOException e) {
            // I/O errors during reading
            System.err.println("Error reading file");
            e.printStackTrace();
        }
    }

    /*
    double[][] CDF(double[][] arPMF) that creates an array of values of the cumulative
    distribution function based on an array arPMF created by formula
    1 point
     */

    // no fucking idea czy to działa nie rozumiem
    double[][] CDF(double[][] arPMF) {
        double[][] arCDF = new double[arPMF.length][];

        for (int i = 0; i < arPMF.length; i++) {
            // Get the PMF
            double[] pmf_i = arPMF[i];

            if (pmf_i == null || pmf_i.length == 0) {
                // Skip if the PMF is empty or null for this link
                arCDF[i] = new double[0];
                continue;
            }

            double[] cdf_i = new double[pmf_i.length];
            double cumulativeSum = 0.0;

            // Calculate the cumulative sum (the CDF)
            for (int k = 0; k < pmf_i.length; k++) {
                cumulativeSum += pmf_i[k];
                cdf_i[k] = cumulativeSum;
            }
            arCDF[i] = cdf_i;
        }
        return arCDF;
    }

    /*
    static double normalCDF(double z) that computes an approximated value of the
    cumulative distribution function of the standard normal distribution for n=100,
    based on the formula (https://en.wikipedia.org/wiki/Normal_distribution)
    where !! denotes the double factorial and should also be implemented.
    2 pts
     */

    static double normalCDF(double[][] z) {
        final int N = 100;


    }

    // Method to compute the double factorial (n!!)
    public static long doubleFactorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Double factorial is not defined for negative numbers.");
        }
        if (n == 0 || n == 1) {
            return 1;
        }

        long result = 1;

        for (int i = n; i >= 2; i -= 2) {
            result *= i;
        }
        return result;
    }

    /*
    static double normalICDF(double u) that computes an approximated value of the
    quantile function (the inverse of the cumulative distribution function) of the
    standard normal distribution
    IMPORTANT! In order to implement normalICDF, invent your own algorithm that for
    given value u, it determines a real number x such that
    |𝑛𝑜𝑟𝑚𝑎𝑙𝐶𝐷𝐹(𝑥) − 𝑢| ≤ 10^-1
    5 pts
     */

    static double normalICDF(double u) {

    }

    /*
    Based on formula (12b) (from [2] G.S. Fishman – “Monte Carlo, Concepts,
    Algorithms, and Applications” – Springer), implement a function finding
    the worstcase normal sample size
    1 point
     */

    /*
    Based on the inverse CDF method applied to discrete distribution or the Chen and
    Asau Guide Table Method coming from [3] J. E. Gentle – “Random Number
    Generation and Monte Carlo Methods” – Springer (2005), implement method
    double[][] randomSSV(int N, double[][]arCDF), that, for a given integer N and an
    array arCDF of values of the cumulative distribution function, generates N random
    system state vectors (SSVs)
    3 pts
     */

    double[][] randomSSV(int N, double[][]arCDF) {

    }
}