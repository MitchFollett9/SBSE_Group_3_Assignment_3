package gin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Simple local search.
 */
public class LocalSearch {

    private static final int seed = 5678;
    private static final int NUM_STEPS = 1000;
    private static final int WARMUP_REPS = 10;

    // private int numberOfRemovals = 0;

    protected SourceFile sourceFile;
    protected TestRunner testRunner;
    protected Random rng;

    /**
     * Main method. Take a source code filename, instantiate a search instance and execute the search.
     * @param args A single source code filename, .java
     */
    public static void main(String[] args) {

        if (args.length == 0) {

            System.out.println("Please specify a source file to optimise.");

        } else {

            String sourceFilename = args[0];
            System.out.println("Optimising source file: " + sourceFilename + "\n");

            LocalSearch localSearch = new LocalSearch(sourceFilename);
            System.out.println("initial search");
            SearchReturn sr = localSearch.search();
            
            System.out.println("after final search " + sr.betterPatch.index);
            System.out.println("after final search patch " + sr.betterPatch.patch.toString());

        }

    }

    /**
     * Constructor: Create a sourceFile and a testRunner object based on the input filename.
     *              Initialise the RNG.
     * @param sourceFilename
     */
    public LocalSearch(String sourceFilename) {

        this.sourceFile = new SourceFile(sourceFilename);  // just parses the code and counts statements etc.
        this.testRunner = new TestRunner(this.sourceFile); // Utility class for running junits
        this.rng = new Random(); // use seed if we want same results each time

    }

    /**
     * Actual LocalSearch.
     * @return
     */
    private SearchReturn search() {

        // start with the empty patch
        Patch bestPatch = new Patch(sourceFile);
        double bestTime = 10000000; // testRunner.test(bestPatch, WARMUP_REPS).executionTime;
        double origTime = bestTime;
        int bestStep = 0;

        ArrayList<PatchWIthIndex> passingPatches = new ArrayList<>();
        PatchWIthIndex betterPatch;
        int sameStep = 0;
        
        System.out.println("Initial execution time: " + bestTime + " (ns) \n");

        for (int step = 1; step <= NUM_STEPS; step++) {

            System.out.print("Step " + step + " ");

            Patch neighbour = neighbour(bestPatch, rng);

            // System.out.print(neighbour);

            TestRunner.TestResult testResult = testRunner.test(neighbour);

            if (!testResult.patchSuccess) {
                System.out.println("Patch invalid");
                // failedChanges.add(neighbour.toString());
            }

            if (!testResult.compiled) {
                System.out.println("Failed to compile");
                // failedChanges.add(neighbour.toString());
                continue;
            }

            if (!testResult.junitResult.wasSuccessful()) {
                System.out.println("Failed to pass all tests");
                // failedChanges.add(neighbour.toString());
                continue;
            }

            passingPatches.add(new PatchWIthIndex(step, neighbour));

            if (testResult.executionTime < bestTime) {
                bestPatch = neighbour;
                bestTime = testResult.executionTime;
                bestStep = step;
                System.out.println("*** New best *** Time: " + bestTime + "(ns)");
                // failedChanges.clear();
            } else {
                System.out.println("Time: " + testResult.executionTime);
            }

        }
        System.out.println("\nBest patch found: " + bestPatch);
        System.out.println("Found at step: " + bestStep);
        System.out.println("Best execution time: " + bestTime + " (ns) ");
        System.out.println("Speedup (%): " + (origTime - bestTime)/origTime);
        bestPatch.writePatchedSourceToFile(sourceFile.getFilename() + ".optimised");

        SourceFile checkFile = bestPatch.apply();

        betterPatch = new PatchWIthIndex(0, bestPatch);

        System.out.println("print is equal working" + bestPatch.apply().isEqual(bestPatch.apply()));

        for (PatchWIthIndex str : passingPatches) {
            if (str.patch.apply().isEqual(checkFile)) {
                betterPatch = str;
                break;
            }
        }

        /*
         * 
         * Heres my thoughts on how to do this
         * 1. save an array of all of the changes that produced a best time
         * 2. save an array of all of the changes that didnt change anything, maybe only one of each type of change
         * 3. at the end delete all from the didnt improve list that appear in the best list
         * 4. there is your list of things that never improved it
         * 5. kind of looks like they want you to run search on the final one after this, so i guess run it twice?
         * 
         * 
         */
        // for (int i = 0; i < bestPatch.edits.size(); i++) {
        //     System.out.println(bestPatch.edits.get(i).toString());
        // }
        

        return new SearchReturn(betterPatch, bestPatch);

    }


    /**
     * Generate a neighbouring patch, by either deleting a randomly chosen edit, or adding a new random edit
     * @param patch Generate a neighbour of this patch.
     * @return A neighbouring patch.
     */
    public Patch neighbour(Patch patch, Random rng) {

        Patch neighbour = patch.clone();

            if (neighbour.size() > 0 && rng.nextFloat() > 0.5) {
                neighbour.remove(rng.nextInt(neighbour.size()));
            } else {
                neighbour.addRandomEdit(rng);
            }

        return neighbour;

    }


}
