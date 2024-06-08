
package gin;

public class SearchReturn {
    public PatchWIthIndex betterPatch;
    public Patch patch;

    // Constructor
    public SearchReturn(PatchWIthIndex ss, Patch p) {
        betterPatch = ss;
        patch = p;
    }
}