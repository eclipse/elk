/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.test.framework.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.core.util.Pair;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

/**
 * This class contains utility methods useful for other classes of the test framework.
 */
public final class TestUtil {

    /**
     * Private constructor to avoid instantiation.
     */
    private TestUtil() {
    }

    /**
     * Returns the values of all fields in the test class annotated with the specified annotation.
     * 
     * @param testClass
     *            the test class to look through.
     * @param annotation
     *            the annotation the fields should be loaded for.
     * @return a list of values.
     */
    public static List<Object> loadAnnotatedFields(final TestClass testClass,
            final Class<? extends Annotation> annotation) {
        
        List<FrameworkField> annotatedFields = testClass.getAnnotatedFields(annotation);
        List<Object> result = new ArrayList<>();
        
        for (FrameworkField frameworkField : annotatedFields) {
            Constructor<?> constr = testClass.getOnlyConstructor();
            Class<?> clazz = constr.getClass();
            try {
                result.add(frameworkField.get(clazz));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        
        return result;
    }

    /**
     * Looks for fields in the test class annotated with the specified annotation and returns a list of their values
     * together with the name of the field.
     * 
     * @param testClass
     *            the test class to look through.
     * @param annotation
     *            the annotation the fields should be loaded for.
     * @return a list of pairs of field values and field names.
     */
    public static List<Pair<Object, String>> loadAnnotatedFieldsWithNames(final TestClass testClass,
            final Class<? extends Annotation> annotation) {
        
        List<FrameworkField> annotatedFields = testClass.getAnnotatedFields(annotation);
        List<Pair<Object, String>> result = new ArrayList<>();
        
        for (FrameworkField frameworkField : annotatedFields) {
            Constructor<?> constr = testClass.getOnlyConstructor();
            Class<?> clazz = constr.getClass();
            String name = testClass.getJavaClass().getName() + "-" + frameworkField.getName();
            
            try {
                result.add(Pair.of(
                        frameworkField.get(clazz),
                        name));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        
        return result;
    }

    /**
     * Looks for methods in the given class annotated with the given annotation, executes them, and returns a list of
     * their return values.
     * 
     * @param testClass
     *            the test class to look through.
     * @param annotation
     *            the annotation methods to be executed should be annotated with.
     * @return return values obtained by executing the methods.
     */
    public static List<Object> executeAnnotatedMethods(final TestClass testClass,
            final Class<? extends Annotation> annotation) {
        
        List<FrameworkMethod> annotMeth = testClass.getAnnotatedMethods(annotation);
        List<Object> result = new ArrayList<>();
        Constructor<?> constr = testClass.getOnlyConstructor();
        
        Object test = null;
        try {
            test = constr.newInstance();
            
            for (FrameworkMethod frameworkMethod : annotMeth) {
                try {
                    result.add(frameworkMethod.invokeExplosively(test));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            
        } catch (InstantiationException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
        } catch (InvocationTargetException e1) {
            e1.printStackTrace();
        }
        
        return result;
    }

    /**
     * Looks for methods in the given class annotated with the given annotation, executes them, and returns a list of
     * their return values and their names.
     * 
     * @param testClass
     *            the test class to look through.
     * @param annotation
     *            the annotation methods to be executed should be annotated with.
     * @return pairs of return values obtained by executing the methods and the method names.
     */
    public static List<Pair<Object, String>> executeAnnotatedMethodsWithName(final TestClass testClass,
            final Class<? extends Annotation> annotation) {
        
        List<FrameworkMethod> annotMeth = testClass.getAnnotatedMethods(annotation);
        List<Pair<Object, String>> result = new ArrayList<>();
        Constructor<?> constr = testClass.getOnlyConstructor();
        
        Object test = null;
        try {
            test = constr.newInstance();
            
            for (FrameworkMethod frameworkMethod : annotMeth) {
                String name = testClass.getJavaClass().getName() + "-" + frameworkMethod.getName();
                
                try {
                    result.add(Pair.of(
                            frameworkMethod.invokeExplosively(test),
                            name));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            
        } catch (InstantiationException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
        } catch (InvocationTargetException e1) {
            e1.printStackTrace();
        }
        
        return result;
    }

    /**
     * Returns a basic graph with three connected nodes.
     */
    public static ElkNode buildBasicGraph() {
        ElkNode layoutGraph = ElkGraphUtil.createGraph();
        ElkNode node1 = ElkGraphUtil.createNode(layoutGraph);
        ElkNode node2 = ElkGraphUtil.createNode(layoutGraph);
        ElkNode node3 = ElkGraphUtil.createNode(layoutGraph);

        ElkGraphUtil.createSimpleEdge(node1, node3);
        ElkGraphUtil.createSimpleEdge(node2, node3);

        // layout options
        for (ElkNode node : layoutGraph.getChildren()) {
            // suppress the checkstyle warnings, because the meaning is clear
            // SUPPRESS CHECKSTYLE NEXT 2 MagicNumber
            node.setWidth(50.0);
            node.setHeight(50.0);
        }
        
        return layoutGraph;
    }

}
