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

            Set<String> ss = new HashSet<>();

            LocalSearch localSearch = new LocalSearch(sourceFilename);
            System.out.println("initial search");
            SearchReturn sr = localSearch.search(ss);
            LocalSearch localSearch2 = new LocalSearch(sourceFilename);
            System.out.println("after initial search");
            localSearch2.search(sr.stringSet);
            System.out.println("after final search");

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
    private SearchReturn search(Set<String> ss) {

        // start with the empty patch
        Patch bestPatch = new Patch(sourceFile);
        double bestTime = testRunner.test(bestPatch, WARMUP_REPS).executionTime;
        double origTime = bestTime;
        int bestStep = 0;

        Set<String> failedChanges = new HashSet<>();
        Set<String> successfullChanges = new HashSet<>();
        
        System.out.println("Initial execution time: " + bestTime + " (ns) \n");

        for (int step = 1; step <= NUM_STEPS; step++) {

            System.out.print("Step " + step + " ");

            Patch neighbour = neighbour(bestPatch, rng, ss);

            System.out.print(neighbour);

            TestRunner.TestResult testResult = testRunner.test(neighbour);

            if (!testResult.patchSuccess) {
                System.out.println("Patch invalid");
                failedChanges.add(bestPatch.toString() + neighbour.toString());
                continue;
            }

            if (!testResult.compiled) {
                System.out.println("Failed to compile");
                failedChanges.add(bestPatch.toString() + neighbour.toString());
                continue;
            }

            if (!testResult.junitResult.wasSuccessful()) {
                System.out.println("Failed to pass all tests");
                failedChanges.add(bestPatch.toString() + neighbour.toString());
                continue;
            }
            successfullChanges.add(bestPatch.toString() + neighbour.toString());

            if (testResult.executionTime < bestTime) {
                bestPatch = neighbour;
                bestTime = testResult.executionTime;
                bestStep = step;
                System.out.println("*** New best *** Time: " + bestTime + "(ns)");
            } else {
                System.out.println("Time: " + testResult.executionTime);
            }

        }

        System.out.println("Failed changes");
        for (String str : failedChanges) {
            System.out.println(str);
        }
        System.out.println("Success changes");
        for (String str : successfullChanges) {
            System.out.println(str);
        }
        failedChanges.removeIf(successfullChanges::contains);
        
        System.out.println("Failed changes after patch");
        for (String str : failedChanges) {
            System.out.println(str);
        }
        System.out.println("\nBest patch found: " + bestPatch);
        System.out.println("Found at step: " + bestStep);
        System.out.println("Best execution time: " + bestTime + " (ns) ");
        System.out.println("Speedup (%): " + (origTime - bestTime)/origTime);
        bestPatch.writePatchedSourceToFile(sourceFile.getFilename() + ".optimised");

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
        

        return new SearchReturn(failedChanges, bestPatch);

    }


    /**
     * Generate a neighbouring patch, by either deleting a randomly chosen edit, or adding a new random edit
     * @param patch Generate a neighbour of this patch.
     * @return A neighbouring patch.
     */
    public Patch neighbour(Patch patch, Random rng, Set<String> ss) {

        Patch neighbour = patch.clone();

            if (neighbour.size() > 0 && rng.nextFloat() > 0.5) {
                neighbour.remove(rng.nextInt(neighbour.size()));
            } else {
                neighbour.addRandomEdit(rng);
            }
            if (ss.contains(patch.toString() + neighbour.toString()))
            {
                System.out.println("ss contains it" + neighbour.toString());
                return patch;
            }

        return neighbour;

    }


}
