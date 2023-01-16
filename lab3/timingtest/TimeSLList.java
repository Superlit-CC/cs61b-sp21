package timingtest;
import com.puppycrawl.tools.checkstyle.api.Check;
import edu.princeton.cs.algs4.Stopwatch;
import org.apache.commons.math3.analysis.function.Add;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        int M = 10000;
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();
        for (int i = 1000; i <= 128000; i *= 2) {
            // 1. Create an SLList.
            SLList<Integer> temp = new SLList<>();
            // 2. Add N items to the SLList.
            for (int j = 0; j < i; j ++ ) {
                temp.addFirst(j);
            }
            // 3. Start the timer.
            Stopwatch sw = new Stopwatch();
            // 4. Perform M getLast operations on the SLList.
            for (int j = 0; j < M; j ++ ) {
                temp.getLast();
            }
            // 5. Check the timer. This gives the total time to complete all M operations.
            double timeInSeconds = sw.elapsedTime();
            Ns.addLast(i);
            times.addLast(timeInSeconds);
            opCounts.addLast(M);
        }
        printTimingTable(Ns, times, opCounts);
    }

}
