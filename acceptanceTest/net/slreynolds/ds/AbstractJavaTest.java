package net.slreynolds.ds;

import java.util.HashMap;


import net.slreynolds.ds.export.ExporterOptions;
import net.slreynolds.ds.export.GraphVizExporter;
import net.slreynolds.ds.export.TulipExporter;
import net.slreynolds.ds.model.BuilderOptions;

public abstract class AbstractJavaTest {

	private final String path;
	  
	protected AbstractJavaTest(String path) {
		this.path = path;
	}
	
	protected void saveToFiles(Object obj, String name) {
		saveToFiles(new Object[]{obj}, new String[]{name}, name);
	}

	protected void saveToFiles(Object[] objs, String[] names, String fileName) {
			    ObjectSaver gvizSaver = new ObjectSaver(new GraphVizExporter());
			    ObjectSaver tulipSaver = new ObjectSaver(new TulipExporter());
			    HashMap<String,Object> options = new HashMap<String,Object>();
			    options.put(BuilderOptions.INLINE_STRINGS,Boolean.FALSE);
			    
			    options.put(ExporterOptions.OUTPUT_PATH, path+'/'+fileName+".dot");
			    gvizSaver.save(objs,names, options);
			    options.put(ExporterOptions.OUTPUT_PATH, path+'/'+fileName+".tlp");
			    tulipSaver.save(objs,names, options);
	}
	

}
