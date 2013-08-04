
package net.slreynolds.ds.model;


/**
 *
 */
final public class Node extends GraphPoint {
    private NodeArray _arrayParent;
 
    public Node(NamedID ID,int generation) {
        super(ID, generation);
    }
    
    public Node(NamedID ID, String name, int generation) {
        super(ID,name,generation);
    }
    
//    public static Node createHiddenNode(int ID, int generation) {
//    	Node n = new Node(generation);
//    	n.putAttr(Named.HIDDEN, true);
//    	return n;
//    }
    
    public static Node createSymbol(NamedID ID, String name, int generation) {
    	Node n = new Node(ID, name, generation);
    	n.putAttr(Named.SYMBOL, true);
    	return n;
    }
    
    @Override
    public Node putAttr(String key, Object value) {
        return (Node) super.putAttr(key, value);
    }

    @Override
    public Node removeAttr(String key) {
        return (Node) super.removeAttr(key);
    }
    
    Node setArrayParent(NodeArray a) { // TODO code too complicated here
        if (_arrayParent != null) {
            throw new IllegalStateException("Node is already in an array");
        }
        _arrayParent = a;
        return this;
    }
    
    public NodeArray getArrayParent() { // TODO code too complicated here
        return _arrayParent;
    }
    
    @Override
    final public boolean isNode() {return true;}
    @Override
    final public boolean isArray() { return false; }
    /**
     * throws unsupported operation exception always
     */
    @Override
    public Node clone()  {
        throw new UnsupportedOperationException();
    }
    
    // ------------- Quasi- generated Code --------------------
    
    @Override
    public String toString() { 
        StringBuffer sb = new StringBuffer("Node{ name=" + getName() + ", ");
        sb.append(attrToString());
        sb.append((_arrayParent == null) ? "" : ", array element}");
        return sb.toString();
    }
    
    // ------------- Generated Code ------------------

    
}
