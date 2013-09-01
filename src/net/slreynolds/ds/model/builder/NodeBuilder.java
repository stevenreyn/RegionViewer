
package net.slreynolds.ds.model.builder;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import net.slreynolds.ds.model.BuildException;
import net.slreynolds.ds.model.BuilderOptions;
import net.slreynolds.ds.model.Graph;
import net.slreynolds.ds.model.GraphPoint;
import net.slreynolds.ds.model.Named;
import net.slreynolds.ds.model.NamedIDGenerator;
import net.slreynolds.ds.model.Node;
import net.slreynolds.ds.model.NodeArray;
import net.slreynolds.util.NodeUtil;

/**
 *
 */
public class NodeBuilder  {
  
    
	final public static GraphPoint buildNode(Object o, Graph graph,  GraphBuildContext context, int nestingLevel) throws BuildException {

		if (o == null) {
			System.err.printf("Won't build a null\n");
			return null;
		}
		final Map<String,Object> options = context.getOptions();
		final boolean showSystemHash =
				(options.containsKey(BuilderOptions.SHOW_SYSHASH) ? 
						(Boolean)options.get(BuilderOptions.SHOW_SYSHASH)  : BuilderOptions.DEFAULT_SHOW_SYSHASH );
		final int MAX_NESTING = 
				(options.containsKey(BuilderOptions.MAX_NESTING) ? 
						(Integer)options.get(BuilderOptions.MAX_NESTING)  : BuilderOptions.DEFAULT_MAX_NESTING );
		final int MAX_ARRAY_LENGTH = 
				(options.containsKey(BuilderOptions.MAX_ARRAY_LENGTH) ? 
						(Integer)options.get(BuilderOptions.MAX_ARRAY_LENGTH)  : BuilderOptions.DEFAULT_MAX_ARRAY_LENGTH );
		final int generation = (Integer)options.get(BuilderOptions.GENERATION);
		
		try {
			final String classname = getClassName(o);
			final String packageNameOfInstance = classNameToPackage(o.getClass().getName());
			if (o.getClass().isArray()) {
				int length = getArrayLength(o);
				if (length > MAX_ARRAY_LENGTH) {
					// TODO This message should have a name for the array. Else it is highly ambiguous
					// and confusing to the user.
					System.out.printf("Truncating array type %s of length %d to %d\n",
							classname,length,MAX_ARRAY_LENGTH);
					length = MAX_ARRAY_LENGTH;
				}
				NodeArray array = new NodeArray(NamedIDGenerator.next()," ",NodeArray.ArrayType.NODE,length, generation); 
				array.putAttr(Named.CLASS,classname);
				final boolean inlineValues = shouldInlineArrayValues(o,options);
				for(int i = 0; i < length; i++) {
					Node node = (Node)array.get(i);
					Object val = getArrayValue(o,i);
					if (val == null || inlineValues) {
						node.putAttr(Named.VALUE,val);  
					}
					else if (nestingLevel < MAX_NESTING) {
						enqueueNode(context, nestingLevel, node," ", val);
					}
					else {
						// TODO this log message should really have a name for the object instead of just a type. 
						// this message is really ambiguous!
						String reason = "";
						if (nestingLevel >= MAX_NESTING) {
							reason = String.format("nestingLevel %s exceeds MAX_NESTING %d");
						}
						System.out.printf("Not following array %s:%s because %s.\n",
								o.getClass(),reason);
					}
				}
				if (showSystemHash)
					NodeUtil.addSystemHash(array, o);
				context.addPoint(o,array);
				return array;
			}
			else {
				Node node = new Node(NamedIDGenerator.next(),generation);
				// TODO try to get generic info?
				node.putAttr(Named.CLASS, classname); 
				if (showSystemHash)
					NodeUtil.addSystemHash(node, o);

				Class<?> clazz = o.getClass();
				// Do not follow scala.collection.parallel.*TaskSupport else we
				// get into a big tangle that GraphViz chokes on
				final boolean isTaskSupportInstance = clazz.getName().endsWith("TaskSupport");
				// Also do not follow Class (because it is complicated and not interesting in this context)
				final boolean isClassInstance = clazz.getName().equals("java.lang.Class");
				final boolean shouldFollowReferences = !isTaskSupportInstance && !isClassInstance;
				while (clazz != null && classNameToPackage(clazz.getName()).equals(packageNameOfInstance)) {
					Field[] fields = clazz.getDeclaredFields();

					for (Field field : fields) {
						/* Scala compiler puts some important fields as synthetic,
						 * so don't dare skip them
						 */
						//if (field.isSynthetic())
						//	continue;
						if (shouldSkip(o,field))
							continue;
						field.setAccessible(true);
						final String fieldName = field.getName();
						Object fieldValue = null;
						try {
							fieldValue = field.get(o);
							if (fieldValue == null) continue;
						} 
						catch (IllegalArgumentException e) {
							throw new BuildException("Error accessing field " + classname + "." + fieldName ,e);
						} 
						catch (IllegalAccessException e) {
							throw new BuildException("Error accessing field " + classname + "." + fieldName ,e);	
						}    		

						if (shouldInlineField(o,field,options)) {
							node.putAttr(fieldName,fieldValue);
						}
						else if (nestingLevel < MAX_NESTING && shouldFollowReferences) {
							enqueueNode(context, nestingLevel, node, fieldName, fieldValue);
						}
						else {
							String reason = "";
							if (nestingLevel >= MAX_NESTING) {
								reason = String.format("nestingLevel %s exceeds MAX_NESTING %d");
							}
							if (isTaskSupportInstance) {
								if (reason.length() > 0) {
									reason = String.format("%s and parent object instance of a TaskSupport", reason);
								}
								else {
									reason = "Parent object instance of a TaskSupport";
								}
							}
							if (isClassInstance) {
								if (reason.length() > 0) {
									reason = String.format("%s and parent object instance of a Class", reason);
								}
								else {
									reason = "Parent object instance of a Class";
								}
							}
							System.out.printf("Not following field %s:%s because %s.\n",
									fieldName,fieldValue.getClass(),reason);
						}
					}
					clazz = clazz.getSuperclass();
				}
				context.addPoint(o,node);
				return node;
			}
		}
		catch (BuildException be) {
			be.printStackTrace();
			throw be;
		}
		catch (NullPointerException npe) {
			npe.printStackTrace();
			throw npe;
		}
		catch (Throwable t) {
			t.printStackTrace();
			throw new RuntimeException(t);
		}
	}

	private static void enqueueNode(GraphBuildContext context, int nestingLevel, GraphPoint fromNode,
			final String edgeName, Object value) {
		if (context.hasPoint(value)) {
			final EdgeSuspension edgeSusp = new EdgeSuspension(fromNode,context.getPoint(value),edgeName);
			context.enqueueEdgeToBuild(edgeSusp);
		}
		else {
			final GraphPointSuspension pointSusp = new GraphPointSuspension(nestingLevel+1,value);
			context.enqueuePointToBuild(pointSusp);
			final EdgeSuspension edgeSusp = new EdgeSuspension(fromNode,value,edgeName);
			context.enqueueEdgeToBuild(edgeSusp);
		}
	}
    
    private static int getArrayLength(Object ar) throws BuildException {
    	try {
    		return Array.getLength(ar);
		} 
    	catch (Exception e) {
			throw new BuildException("Error getting length on "+ ar.toString(),e);
		} 
    	
    }
    
    private static Object getArrayValue(Object ar, int i) throws BuildException {
    	try {
    		return Array.get(ar, i);
		} 
    	catch (Exception e) {
			throw new BuildException("Error getting value[" + i + "] on " + ar.toString(),e);
		} 
    }
    
    
    private static boolean shouldInlineField(Object o, Field field, Map<String,Object> options) {
    	return shouldInLineType(field.getType(),options);
    }
    
    private static boolean shouldInlineArrayValues(Object o, Map<String,Object> options) {
    	if (!o.getClass().isArray()) {
    		throw new IllegalArgumentException("first argument to this method must be an array");
    	}
    	Class<?> contentType = o.getClass().getComponentType();
    	
    	if (shouldInLineType(contentType,options))
    		return true;
    	boolean shouldInLine = true;
    	final int N = Array.getLength(o);
    	for (int i = 0; i < N; i++) {
    		Object val = Array.get(o, i);
    		if (val == null) continue;
    		shouldInLine = shouldInLine && shouldInLineType(val.getClass(),options);
    	}
    	return shouldInLine;
    }
    
    private static boolean shouldInLineType(Class<?> clazz,Map<String,Object> options) {
    	if (clazz.equals(String.class)) {
    		boolean inlineString = BuilderOptions.DEFAULT_INLINE_STRINGS;
    		if (options.containsKey(BuilderOptions.INLINE_STRINGS)) {
    			inlineString = (Boolean)options.get(BuilderOptions.INLINE_STRINGS);
    		}
    		return inlineString;
    	}
    	if (clazz.isPrimitive()) return true;
		if (isNumberObject( clazz, options))  {
			boolean inlineNumbers = BuilderOptions.DEFAULT_INLINE_NUMBERS;
			if (options.containsKey(BuilderOptions.INLINE_NUMBERS)) {
				inlineNumbers = (Boolean)options.get(BuilderOptions.INLINE_NUMBERS);
			}
			return inlineNumbers;
		}
    	return false;
    }
    
    private static boolean isNumberObject(Class<?> clazz,Map<String,Object> options) {
    	if (clazz.equals(Byte.class)) return true;
    	if (clazz.equals(Short.class)) return true;
    	if (clazz.equals(Integer.class)) return true;
    	if (clazz.equals(Long.class)) return true; 
    	if (clazz.equals(Float.class)) return true;
    	if (clazz.equals(Double.class)) return true;
    	return false;
    }
    
    private static boolean shouldSkip(Object o,Field field) {
    	if (field.getName().equals("serialVersionUID"))
    		return true;
    	if (field.getName().equals("serialPersistentFields"))
    		return true;
    	if (Modifier.isStatic(field.getModifiers()))
    		return true;
    	return false;
    }
    
    private static String getClassName(Object o) {
    	return o.getClass().getSimpleName(); // TODO fails if inner class
    }
    
    private static String classNameToPackage(String classname) {
    	int lastdot = classname.lastIndexOf('.');
    	if (lastdot < 0) return "no-package";
    	return classname.substring(0,lastdot);
    }

}
