import java.util.*;
import java.util.concurrent.*;

public class ParallelMatrixMultiplication {

    // =========================================
    // Initialize Matrix with Random Values
    // =========================================
    public static void initializeMatrix(double[][] matrix,
                                        int rows,
                                        int cols) {

        Random rand = new Random();

        for (int i = 0; i < rows; i++) {

            for (int j = 0; j < cols; j++) {

                matrix[i][j] = rand.nextInt(100);
            }
        }
    }

    // =========================================
    // Sequential Matrix Multiplication
    // =========================================
    public static void sequentialMultiply(double[][] a,
                                          double[][] b,
                                          double[][] result,
                                          int m,
                                          int n,
                                          int p) {

        for (int i = 0; i < m; i++) {

            for (int j = 0; j < p; j++) {

                result[i][j] = 0;

                for (int k = 0; k < n; k++) {

                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }
    }

    // =========================================
    // Parallel Matrix Multiplication
    // =========================================
    public static void parallelMultiply(double[][] a,
                                        double[][] b,
                                        double[][] result,
                                        int m,
                                        int n,
                                        int p)
            throws InterruptedException {

        int threads = Runtime.getRuntime().availableProcessors();

        ExecutorService executor =
                Executors.newFixedThreadPool(threads);

        for (int i = 0; i < m; i++) {

            final int row = i;

            executor.execute(() -> {

                for (int j = 0; j < p; j++) {

                    result[row][j] = 0;

                    for (int k = 0; k < n; k++) {

                        result[row][j] +=
                                a[row][k] * b[k][j];
                    }
                }
            });
        }

        executor.shutdown();

        executor.awaitTermination(
                Long.MAX_VALUE,
                TimeUnit.NANOSECONDS
        );
    }

    // =========================================
    // Verify Results
    // =========================================
    public static boolean verifyResults(double[][] seqResult,
                                        double[][] parallelResult,
                                        int rows,
                                        int cols) {

        for (int i = 0; i < rows; i++) {

            for (int j = 0; j < cols; j++) {

                if (Math.abs(seqResult[i][j]
                        - parallelResult[i][j]) > 1e-10) {

                    return false;
                }
            }
        }

        return true;
    }

    // =========================================
    // Main Function
    // =========================================
    public static void main(String[] args)
            throws InterruptedException {

        int[][] dimensions = {
                {100, 100, 100},
                {500, 500, 500},
                {1000, 1000, 1000}
        };

        System.out.println(
                "\nMatrix Multiplication using Multithreading"
        );

        System.out.println(
                "=============================================================="
        );

        System.out.printf(
                "%-20s %-15s %-15s %-15s\n",
                "Dimensions",
                "Sequential",
                "Parallel",
                "Speedup"
        );

        System.out.println(
                "--------------------------------------------------------------"
        );

        for (int[] dim : dimensions) {

            int m = dim[0];
            int n = dim[1];
            int p = dim[2];

            // Create matrices
            double[][] a = new double[m][n];
            double[][] b = new double[n][p];

            double[][] seqResult =
                    new double[m][p];

            double[][] parallelResult =
                    new double[m][p];

            // Initialize matrices
            initializeMatrix(a, m, n);
            initializeMatrix(b, n, p);

            // =================================
            // Sequential Multiplication
            // =================================
            long start = System.nanoTime();

            sequentialMultiply(
                    a,
                    b,
                    seqResult,
                    m,
                    n,
                    p
            );

            long end = System.nanoTime();

            double sequentialTime =
                    (end - start) / 1e6;

            // =================================
            // Parallel Multiplication
            // =================================
            start = System.nanoTime();

            parallelMultiply(
                    a,
                    b,
                    parallelResult,
                    m,
                    n,
                    p
            );

            end = System.nanoTime();

            double parallelTime =
                    (end - start) / 1e6;

            // =================================
            // Speedup Calculation
            // =================================
            double speedup =
                    sequentialTime / parallelTime;

            // =================================
            // Verify Results
            // =================================
            boolean correct =
                    verifyResults(
                            seqResult,
                            parallelResult,
                            m,
                            p
                    );

            // =================================
            // Print Results
            // =================================
            System.out.printf(
                    "%dx%dx%d\t\t%-15.2f %-15.2f %-15.2f",
                    m,
                    n,
                    p,
                    sequentialTime,
                    parallelTime,
                    speedup
            );

            if (!correct) {

                System.out.print(" ERROR!");
            }

            System.out.println();
        }
    }
}