
package net.slreynolds.ds.util;

import net.slreynolds.ds.model.GraphPoint;
import net.slreynolds.ds.model.Named;

/**
 *
 */
public class NodeUtil {
    public static void addSystemHash(GraphPoint n, Object src) {
        int hash = System.identityHashCode(src);
        n.putAttr(Named.SYSTEM_HASH, hash);
    }
}
