
package gin;

import java.util.Set;

public class SearchReturn {
    public Set<String> stringSet;
    public Patch patch;

    // Constructor
    public SearchReturn(Set<String> ss, Patch p) {
        stringSet = ss;
        patch = p;
    }
}