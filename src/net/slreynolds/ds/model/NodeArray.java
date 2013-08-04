
package net.slreynolds.ds.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
final public class NodeArray extends GraphPoint {

    private List<GraphPoint> _elements;
    private ArrayType _type;
    
    public enum ArrayType { NODE }

    public NodeArray(NamedID ID, String name, ArrayType atype, int length, int generation) {
        super(ID, name, generation);
        if (length < 0) {
            throw new IllegalArgumentException("length must be non-negative");
        }
        _elements = new ArrayList<GraphPoint>(length);
        _type = atype;
        switch (atype) {
            case NODE:
                for (int i = 0; i < length; i++) {
                    Node p = new Node(NamedIDGenerator.next(),generation);
                    p.setArrayParent(this);
                    p.putAttr(Named.ARRAY_INDEX, i);
                    _elements.add(p);
                }
                break;
            default:
                throw new IllegalStateException("Didn't implement that array type yet: " + atype);
        }

    }

    public ArrayType getArrayType() { return _type; }
    
    public List<GraphPoint> getElements() {
        return Collections.unmodifiableList(_elements);
    }

    public GraphPoint get(int i) {
        return _elements.get(i);
    }
    
    public int getLength() {
        return _elements.size();
    }
    
    @Override
    public NodeArray putAttr(String key, Object value) {
        return (NodeArray) super.putAttr(key, value);
    }

    @Override
    public NodeArray removeAttr(String key) {
        return (NodeArray) super.removeAttr(key);
    }
    
    @Override
    final public boolean isNode() {return false;}
    @Override
    final public boolean isArray() { return true; }
    
    @Override
    public String toString() {
        return "NodeArray [ # elements=" + _elements.size() + ", " + get(0).toString() + "]";
    }
    
    // -------------- Generated Code ------------------

    // TODO generate hashcode and equals again

    
}
