import edu.princeton.cs.algs4.Stopwatch;

import java.util.Random;

/**
 * Get the percolation statistics.
 */
public class PercolationStats {

    private double[] fracs = null;
    private double mean = 0.0;
    private double standardError = 0.0;

    // perform T independent computational experiments on an N-by-N grid
    public PercolationStats(int N, int T) {
        if (N <= 0 || T <= 0) {
            throw new java.lang.IllegalArgumentException();
        }

        fracs = new double[T];
        Random random = new Random();
        int[][] locs = new int[2][N];

        for (int i = 0; i < N; i++) {
            locs[0][i] = i + 1;
            locs[1][i] = i + 1;
        }

        // random shuffle. 
        for (int i = 0; i < N; i++) {
            int x = random.nextInt(N);
            int temp = locs[0][x];
            locs[0][x] = locs[0][N - x - 1];
            locs[0][N - x - 1] = temp;
            x = random.nextInt(N);
            temp = locs[1][x];
            locs[1][x] = locs[1][N - x - 1];
            locs[1][N - x - 1] = temp;
        }

        // do a number of experiments. 
        for (int cnt = 0; cnt < T; cnt++) {
            int openCnt = 0;
            Percolation percol = new Percolation(N);
            for (int i = 0; i < N; i++) {

                if (percol.isOpen(locs[0][i], locs[1][i])) {
                    percol.open(locs[0][i], locs[1][i]);
                    openCnt++;
                }
                if (percol.percolates()) {
                    break;
                }
            }
            fracs[cnt] = (double) openCnt / N / N;
            mean += fracs[cnt] / cnt;
        }

        // standard error.
        for (int i = 0; i < T; i++) {
            standardError += (fracs[i] - mean) * (fracs[i] - mean) / (T - 1 + 0.0001);
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        return mean;
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        if (standardError == 0.0) {
            return standardError + 0.001;
        } else {
            return standardError;
        }
    }

    public static void main(String[] args) {
        int N = Integer.parseInt(args[0]);
        int T = Integer.parseInt(args[1]);

        Stopwatch sw = new Stopwatch();

        PercolationStats ps = new PercolationStats(N, T);

        double t = sw.elapsedTime();

        // 95% confidence interval of mean estimation. 
        double lower = ps.mean() - 1.96 * Math.sqrt(ps.stddev()) / Math.sqrt(T);
        double upper = ps.mean() + 1.96 * Math.sqrt(ps.stddev()) / Math.sqrt(T);

        System.out.println("time used               = " + t + "s");
        System.out.println("mean                    = " + ps.mean());
        System.out.println("stddev                  = " + ps.stddev());
        System.out.println("95% confidence interval = " + lower + "," + upper);
    }
}
