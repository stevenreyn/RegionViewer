RegionViewer
============

Allows you to export a region (graph of objects on a JVM) to several different 
types of graph files. The most useful type of output graph file is 
[GraphViz format](http://www.graphviz.org/). These files have a .dot extension. 
Using GraphViz tools,you can easily convert the dot file to many different formats, 
such as SVG.

RegionViewer uses some heuristics to decide what details to show and which to
suppress. It is smart enough to detect and avoid loops. An example output
looks like the following.

![Example export of two Java Strings] (images/strings.dot.svg)

This above example shows the export from two Java Strings. The Strings were 
defined like this:

    String brother = "brother";
    String the = brother.substring(3, 6);
    
The RegionViewer was given the two object references, brother and the. It first
followed all the objects reachable from brother and colored those nodes
redish-orange, and then followed the objects reachable from the and colored those
nodes yellowish. Any object previously visited was not exported a second time.

This example shows Java's immutable String objects exhibiting structural sharing.
The code used to generate this graph is in the RegionViewer project in the file
acceptanceTest/net/slreynolds/ds/SecondTest.java. You can examine this Java file
for simple examples that show you how to use the RegionViewer.

Some other examples are the following.

![Example export of a Java ArrayList] (images/alist.dot.svg)

This above example shows the export from a Java ArrayList that contains two objects.

![Example export of a Java LinkedList] (images/llist.dot.svg)

This above example shows the export from a Java ArrayList that contains two objects.

![Example export of a Scala HashMap] (images/hmap-ops.dot.svg)

This above example shows the export from a Scala HashMap and some togther maps derived
from the orignal.

![Example export of a Scala HashMap] (images/hmap-ops_simple.dot.svg)

This above example shows the same scenario as above but using the simpler GraphViz
exporter. This exporter suppresses all primitive fields and therefore only shows
objects and their relationships.

