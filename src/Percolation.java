import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    private boolean[][] gridOpen = null;
    private WeightedQuickUnionUF WQF;
    private int N, count;

    // create N-by-N grid, with all sites blocked
    public Percolation(int N) {
        gridOpen = new boolean[N][N];
        if (N <= 0) {
            throw new java.lang.IllegalArgumentException();
        }
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                gridOpen[i][j] = false; // close grid .
            }
        }
    }

    boolean isOpen(int i, int j) {
        int row = i - 1;
        int col = j - 1;
        if (row < 0 || row > N || col < 0 || col > N) {
            throw new IndexOutOfBoundsException();
        }
        return gridOpen[row][col];
    }

    void open(int i, int j) {
        int row = i - 1;
        int col = j - 1;
        if (row < 0 || row > N || col < 0 || col > N) {
            throw new IndexOutOfBoundsException("Illegal parameter value.");
        }
        gridOpen[row][col] = true; // open it. 
        count++;

        if (row == 0) { //top row
            WQF.union(0, N * row + col);
        }
        if (row == N - 1) { //bottom row
            WQF.union((N * N) + 1, N * row + col);
        }
        if (i > 1 && isOpen(row, j)) { // top 
            WQF.union((N * row + col), (N * (row - 1) + col));
        }

        if (i < N && isOpen(i + 1, j)) {    // bottom
            WQF.union(N * row + col, N * i + col);
        }

        if (j > 1 && isOpen(i, j - 1)) { // left 
            WQF.union(N * row + col, N * row + (col - 1));
        }

        if (j < N && isOpen(i, j + 1)) { // right. 
            WQF.union(N * row + col, N * row + j);
        }
    }

    boolean isFull(int i, int j) {
        int row = i - 1;
        int col = j - 1;
        if (row < 0 || row > N || col < 0 || col > N) {
            throw new IndexOutOfBoundsException("Illegal parameter value.");
        }
        for (int m = 0; m < N; m++) {
            if (isOpen(i, j) && WQF.connected(m, N * row + col)) {
                return true;
            }
        }
        return false;

    }

// does the system percolate?
    public boolean percolates() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (isOpen(1, i + 1) && isOpen(N, j + 1) && WQF.connected(i, (N - 1) * N + j)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {

        final int T = 1000;
        final int N = 10;

        Percolation perc = new Percolation(N);

        boolean percolates = perc.percolates();
        System.out.println("percolates is " + percolates);
        int row, col, ct;
        double sum = 0.0;
        for (int i = 0; i < T; i++) {
            ct = 0;
            perc = new Percolation(N);
            while (!perc.percolates()) {
                row = StdRandom.uniform(perc.getN()) + 1;
                col = StdRandom.uniform(perc.getN()) + 1;
                if (perc.isFull(row, col)) {
                    perc.open(row, col);
                    ct++;
                }
            }
            sum += ct;
        }
        System.out.println("After " + T + " attempts,the average number of sites opened was %.2f" + sum / T + " or %.2f"+ ((sum / T) / (N * N)) * 100);
    }

    public static void show(boolean[][] a, boolean which) {
        int N = a.length;
        StdDraw.setXscale(-1, N);
        StdDraw.setYscale(-1, N);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (a[i][j] == which) {
                    StdDraw.filledSquare(j, N - i - 1, .5);
                }
            }
        }
    }

    public static boolean[][] random(int N, double p) {
        boolean[][] a = new boolean[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                a[i][j] = StdRandom.bernoulli(p);
            }
        }
        return a;
    }

    public static boolean[][] flow(boolean[][] open) {
        int N = open.length;
        boolean[][] full = new boolean[N][N];
        for (int j = 0; j < N; j++) {
            flow(open, full, 0, j);
        }
        return full;
    }

    public static void flow(boolean[][] open, boolean[][] full, int i, int j) {
        int N = open.length;

        // base cases
        if (i < 0 || i >= N) {
            return;    // invalid row
        }
        if (j < 0 || j >= N) {
            return;    // invalid column
        }
        if (!open[i][j]) {
            return;        // not an open site
        }
        if (full[i][j]) {
            return;         // already marked as full
        }
        // mark i-j as full
        full[i][j] = true;

        flow(open, full, i + 1, j);   // down
        flow(open, full, i, j + 1);   // right
        flow(open, full, i, j - 1);   // left
        flow(open, full, i - 1, j);   // up
    }

    private int getN() {
        return N;
    }
}
