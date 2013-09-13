package net.slreynolds.ds.export;

import net.slreynolds.ds.model.GraphPoint;
import net.slreynolds.ds.model.Named;
import net.slreynolds.ds.model.Node;
import net.slreynolds.ds.model.NodeArray;

class ExporterUtils {

    static String name(String path) {
        int dotIdx = path.lastIndexOf('.');
        int slashIdx = Math.max(path.lastIndexOf('/'),path.lastIndexOf('\\'));
        if (dotIdx < 0) {
            if (slashIdx < 0) {
                return path;
            }
            return path.substring(slashIdx+1,path.length());
        }
        assert(dotIdx >= 0);
        if (slashIdx < 0) {
            return path.substring(0,dotIdx);
        }
        assert(slashIdx >= 0);
        if (slashIdx < dotIdx) {
            return path.substring(slashIdx+1,dotIdx);
        }
        return path.substring(slashIdx+1,path.length());
    }
    

    
    static String encloseInQuotes(String arg) {
        return '"' + arg + '"';
    }
    

    static boolean isArrayNode(GraphPoint gp) {
    	if (gp instanceof Node) {
    		Node node = (Node)gp;
    		return (node.getArrayParent() != null);
    	}
    	return false;
    }
    
    static String id(GraphPoint gp) {
    	if (isArrayNode(gp)) {
    		Node node = (Node)gp;
    		// TODO code too complicated here, Graph should be simplified
    		NodeArray parent = node.getArrayParent();
    		
    		return String.format("%s:e%d", id(parent),
    					gp.getAttr(Named.ARRAY_INDEX));
    		
    	}
    	return String.format("%d", gp.getID());
    }

    static String getIndent(int level) {
        final String indent_1 = "   ";
        switch (level) {
            case 0:
                return "";
            case 1:
                return indent_1;
            default:
                return indent_1 + getIndent(level-1); // TODO kinda slow
        }
    }
}
