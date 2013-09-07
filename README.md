# RegionViewer #

## Introduction ##

[RegionViewer](http://github.com/stevenreyn/RegionViewer)
allows you to export a region (graph of objects on a JVM) to several different 
types of graph files. The most useful type of output graph file is 
[GraphViz format](http://www.graphviz.org/). These files have a .dot extension. 
Using GraphViz tools, you can easily convert the dot file to many different formats, 
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
red-orange, and then followed the objects reachable from the and colored those
nodes yellow-green. Any object previously visited was not exported a second time.

This example shows Java's immutable String objects exhibiting structural sharing.
The code used to generate this graph is in the RegionViewer project in the file
acceptanceTest/net/slreynolds/ds/SimplestExample.java. You can read and run this Java file
to see how to use the RegionViewer. Here is the essential part of that code:


        String brother = "brother";
        String the = brother.substring(3, 6);

	HashMap<String,Object> options = new HashMap<String,Object>();
	options.put(ExporterOptions.OUTPUT_PATH, "simplest_strings.dot");
	    
	ObjectSaver gvizSaver = new ObjectSaver(new GraphVizExporter());
	gvizSaver.save(new Object[]{brother,the},
	    		       new String[]{"brother","the"}, 
	    		       options);

        System.out.printf("All done. Convert to SVG using GraphViz command \"dot -O -Tsvg simplest_strings.dot\".\n");

Some other interesting examples are in SecondTest.java from the same directory. 

## More Examples ##

Some other examples are the following.

![Example export of a Java ArrayList] (images/alist.dot.svg)

This above example shows the export from a Java ArrayList that contains two objects.

![Example export of a Java LinkedList] (images/llist.dot.svg)

This above example shows the export from a Java ArrayList that contains two objects.

![Example export of a Scala HashMap] (images/hmap-ops.dot.svg)

This above example shows the export from a Scala HashMap and a map derived
from the orignal by adding a key/value. These last two Scala examples were generated by 
code in the [ScalaRegionExamples](http://github.com/stevenreyn/ScalaRegionExamples) 
project.

![Example export of a Scala HashMap] (images/hmap-ops_simple.dot.svg)

This above example shows the same scenario as above but using the simpler GraphViz
exporter (SimpleGraphVizExporter). This exporter suppresses all primitive fields 
and therefore only shows objects and their relationships.

![Example export of a Clojure HashMap] (images/clj-hash-map-ops.dot.svg)

This above example shows the export from a Clojure HashMap and a map derived
from the orignal by adding a key/value. This Clojure example was generated by 
code in the [ClojureRegionExamples](http://github.com/stevenreyn/ClojureRegionExamples) 
project.


