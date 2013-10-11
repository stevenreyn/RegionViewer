package net.slreynolds.ds.model;

import static org.junit.Assert.*;

import java.io.File;
import java.util.HashMap;

import net.slreynolds.ds.ObjectSaver;
import net.slreynolds.ds.export.ExporterOptions;
import net.slreynolds.ds.export.GraphVizExporter;
import net.slreynolds.ds.export.SimpleGraphVizExporter;
import net.slreynolds.ds.model.Graph;
import net.slreynolds.ds.model.GraphPoint;
import net.slreynolds.ds.model.Named;

import org.junit.Test;


public class TestArrays {


	
	public static class C {
		@SuppressWarnings("unused")
		private final int _i;
		public C(int i) {
			_i = i;
		}
	}
	
	@Test
	public void testObjectArray() {
		C c0 = new C(0);
		C c1 = new C(1);
		C c2 = new C(2);
		C[] myobjarray = new C[]{c0,c1,c2};
		ExporterStub exporter = new ExporterStub();
		ObjectSaver stubSaver = new ObjectSaver(exporter);
		HashMap<String,Object> options = new HashMap<String,Object>();
		stubSaver.save(new Object[]{myobjarray},new String[]{"myobjarray"},options);
		Graph g = exporter.getGraph();
		assertEquals("num graph points",5,g.getGraphPoints().size());
		
		GraphPoint gp = g.getPrimaryGraphPoint();
		assertTrue("primary point isa symbol",gp.hasAttr(Named.SYMBOL));
		assertTrue("primary point isa symbol",(Boolean)gp.getAttr(Named.SYMBOL));
		assertEquals("primary has one link",1,gp.getNeighbors().size());
		
		String dir = "test_output/";
		ObjectSaver gvizSaver = new ObjectSaver(new GraphVizExporter());
		new File(dir+"myobjarray.dot").delete();
		options.put(ExporterOptions.OUTPUT_PATH, dir+"myobjarray.dot");
		gvizSaver.save(new Object[]{myobjarray},new String[]{"myobjarray"},options);
		
		ObjectSaver simpleGvizSaver = new ObjectSaver(new SimpleGraphVizExporter());
		new File(dir+"myobjarray_simple.dot").delete();
		options.put(ExporterOptions.OUTPUT_PATH, dir+"myobjarray_simple.dot");
		simpleGvizSaver.save(new Object[]{myobjarray},new String[]{"myobjarray"},options);
	}
	
	@Test
	public void testPrimitiveArray() {
		
		int[] myintarray = new int[]{0,1,2};
		ExporterStub exporter = new ExporterStub();
		ObjectSaver stubSaver = new ObjectSaver(exporter);
		HashMap<String,Object> options = new HashMap<String,Object>();
		stubSaver.save(new Object[]{myintarray},new String[]{"myarray"},options);
		Graph g = exporter.getGraph();
		assertEquals("num graph points",2,g.getGraphPoints().size());
		
		GraphPoint gp = g.getPrimaryGraphPoint();
		assertTrue("primary point isa symbol",gp.hasAttr(Named.SYMBOL));
		assertTrue("primary point isa symbol",(Boolean)gp.getAttr(Named.SYMBOL));
		assertEquals("primary has one link",1,gp.getNeighbors().size());
		
		String dir = "test_output/";
		ObjectSaver gvizSaver = new ObjectSaver(new GraphVizExporter());
		new File(dir+"myintarray.dot").delete();
		options.put(ExporterOptions.OUTPUT_PATH, dir+"myintarray.dot");
		gvizSaver.save(new Object[]{myintarray},new String[]{"myintarray"},options);
		
		ObjectSaver simpleGvizSaver = new ObjectSaver(new SimpleGraphVizExporter());
		new File(dir+"myintarray_simple.dot").delete();
		options.put(ExporterOptions.OUTPUT_PATH, dir+"myintarray_simple.dot");
		simpleGvizSaver.save(new Object[]{myintarray},new String[]{"myintarray"},options);
	}	
	
}
