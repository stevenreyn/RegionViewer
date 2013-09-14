package net.slreynolds.ds.model;

import static org.junit.Assert.*;

import java.util.HashMap;

import net.slreynolds.ds.ObjectSaver;
import net.slreynolds.ds.model.Graph;
import net.slreynolds.ds.model.GraphPoint;
import net.slreynolds.ds.model.Named;

import org.junit.Test;


public class TestModel {

	public static class A {
		private final B _b;
		public A(B b) {
			_b = b;
		}
	}
	
	public static class B {
		private final C _c;
		public B(C c) {
			_c = c;
		}
	}
	
	public static class C {
		private final int _i;
		public C(int i) {
			_i = i;
		}
	}
	
	public static class DummyClass {
		private int one;
		private double two;
		private char three;
		private float four;
		public DummyClass(int one, double two, char three, float four) {
			this.one = one;
			this.two = two;
			this.three = three;
			this.four = four;
		}
	}
	
	@Test
	public void testABCScenario() {
		C c = new C(2);
		B b = new B(c);
		A a = new A(b);
		ExporterStub exporter = new ExporterStub();
		ObjectSaver saver = new ObjectSaver(exporter);
		saver.save(new Object[]{a},new String[]{"a"},new HashMap<String,Object>());
		Graph g = exporter.getGraph();
		assertEquals("num graph points",4,g.getGraphPoints().size());
		
		GraphPoint gp = g.getPrimaryGraphPoint();
		assertTrue("primary point isa symbol",gp.hasAttr(Named.SYMBOL));
		assertTrue("primary point isa symbol",(Boolean)gp.getAttr(Named.SYMBOL));
		assertEquals("primary has one link",1,gp.getNeighbors().size());
		
		GraphPoint gp_a = gp.getNeighbors().get(0).getTo();
		assertEquals("a has one link",1,gp_a.getNeighbors().size());
		
		GraphPoint gp_b = gp_a.getNeighbors().get(0).getTo();
		assertEquals("b has one link",1,gp_b.getNeighbors().size());
		
		GraphPoint gp_c = gp_b.getNeighbors().get(0).getTo();
		assertEquals("c has no links",0,gp_c.getNeighbors().size());
		
	}
	
	@Test
	public void testFields() {
		DummyClass dummy = new DummyClass(1,2.0,'c',4.0f);
		ExporterStub exporter = new ExporterStub();
		ObjectSaver saver = new ObjectSaver(exporter);
		saver.save(new Object[]{dummy},new String[]{"um"},new HashMap<String,Object>());
		Graph g = exporter.getGraph();
		
        assertEquals("num graph points",2,g.getGraphPoints().size());
		
		GraphPoint gp = g.getPrimaryGraphPoint();
		assertTrue("primary point isa symbol",gp.hasAttr(Named.SYMBOL));
		assertTrue("primary point isa symbol",(Boolean)gp.getAttr(Named.SYMBOL));
		assertEquals("primary has one link",1,gp.getNeighbors().size());
		
		GraphPoint gp_dummy = gp.getNeighbors().get(0).getTo();
		assertEquals("dummy has no links",0,gp_dummy.getNeighbors().size());
		assertEquals("dummy.one",1,gp_dummy.getAttr("one"));
		assertEquals("dummy.two",2.0,gp_dummy.getAttr("two"));
		assertEquals("dummy.three",'c',gp_dummy.getAttr("three"));
		assertEquals("dummy.four",4.0f,gp_dummy.getAttr("four"));
	}
}
