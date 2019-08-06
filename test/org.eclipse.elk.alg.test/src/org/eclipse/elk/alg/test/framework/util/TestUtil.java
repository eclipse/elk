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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
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
     * Adds an exception to the list of errors if the given method is not public.
     */
    public static boolean ensurePublic(final FrameworkMethod method, final List<Throwable> errors) {
        if (!method.isPublic()) {
            errors.add(new Exception("Method " + method.getName() + " must be public"));
            return false;
        } else {
            return true;
        }
    }

    /**
     * Adds an exception to the list of errors if the given method has a non-void return type.
     */
    public static boolean ensureReturnsType(final FrameworkMethod method, final List<Throwable> errors,
            final Class<?> expectedType) {

        if (!expectedType.isAssignableFrom(method.getReturnType())) {
            errors.add(new Exception(
                    "Method " + method.getName() + " must have return type " + expectedType.getCanonicalName()));
            return false;
        } else {
            return true;
        }
    }

    /**
     * Adds an exception to the list of errors if the given method does not have parameters that can accept arguments of
     * the given types.
     */
    public static boolean ensureParameters(final FrameworkMethod method, final List<Throwable> errors,
            final Class<?>... expectedTypes) {

        Method javaMethod = method.getMethod();
        boolean checkPassed = true;

        if (javaMethod.getParameterCount() == expectedTypes.length) {
            Class<?>[] parameterTypes = javaMethod.getParameterTypes();

            for (int i = 0; i < expectedTypes.length; i++) {
                if (!parameterTypes[i].isAssignableFrom(expectedTypes[i])) {
                    checkPassed = false;
                }
            }

        } else {
            checkPassed = false;
        }

        if (checkPassed) {
            return true;
        } else {
            errors.add(new Exception("Method " + method.getName() + " must expect parameters of types: "
                    + Arrays.toString(expectedTypes)));
            return false;
        }
    }

    /**
     * Adds exceptions to the list of errors if the given method has annotations of the given annotation types.
     */
    @SafeVarargs
    public static boolean ensureNotAnnotatedWith(final FrameworkMethod method, final List<Throwable> errors,
            final Class<? extends Annotation>... forbidden) {

        boolean success = true;
        
        for (Annotation annotation : method.getAnnotations()) {
            for (Class<? extends Annotation> forbiddenAnnotationType : forbidden) {
                if (forbiddenAnnotationType.equals(annotation.getClass())) {
                    errors.add(new Exception("Method " + method.getName() + " cannot have annotation "
                            + forbiddenAnnotationType.getName()));
                    success = false;
                }
            }
        }

        return success;
    }

    /**
     * Adds an exception to the list of errors if the given class does not have a single public no-parameters
     * constructor.
     */
    public static boolean ensureSinglePublicNoParameterConstructor(final TestClass testClass,
            final List<Throwable> errors) {

        Class<?> javaClass = testClass.getJavaClass();
        Constructor<?>[] constructors = javaClass.getConstructors();

        if (constructors.length != 1 || constructors[0].getParameterCount() != 0) {
            errors.add(new Exception("Test class must have a single public constructor without parameters."));
            return false;
        }

        return true;
    }

    /**
     * Tries to create and return an instance of the test class.
     * 
     * @return an instance of the test class or {@code null} if something went wrong.
     */
    public static Object createTestClassInstance(final TestClass testClass) {
        Constructor<?> constr = testClass.getOnlyConstructor();

        Object test = null;
        try {
            test = constr.newInstance();
        } catch (InstantiationException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
        } catch (InvocationTargetException e1) {
            e1.printStackTrace();
        }

        return test;
    }

    /**
     * Returns the concatenation of the results of {@link #loadAnnotatedFields(TestClass, Class)} and
     * {@link #executeAnnotatedMethods(TestClass, Class)}.
     */
    public static List<Object> loadAnnotatedFieldsAndMethods(final TestClass testClass,
            final Class<? extends Annotation> annotation) {

        List<Object> result = loadAnnotatedFields(testClass, annotation);
        result.addAll(executeAnnotatedMethods(testClass, annotation));
        return result;
    }

    /**
     * Returns the concatenation of the results of {@link #loadAnnotatedFieldsWithNames(TestClass, Class)} and
     * {@link #executeAnnotatedMethodsWithName(TestClass, Class)}.
     */
    public static List<Pair<Object, String>> loadAnnotatedFieldsAndMethodsWithNames(final TestClass testClass,
            final Class<? extends Annotation> annotation) {

        List<Pair<Object, String>> result = loadAnnotatedFieldsWithNames(testClass, annotation);
        result.addAll(executeAnnotatedMethodsWithName(testClass, annotation));
        return result;
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
                result.add(Pair.of(frameworkField.get(clazz), name));
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

        Object test = createTestClassInstance(testClass);
        if (test != null) {
            for (FrameworkMethod frameworkMethod : annotMeth) {
                try {
                    result.add(frameworkMethod.invokeExplosively(test));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
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

        Object test = createTestClassInstance(testClass);
        if (test != null) {
            for (FrameworkMethod frameworkMethod : annotMeth) {
                String name = testClass.getJavaClass().getName() + "-" + frameworkMethod.getName();

                try {
                    result.add(Pair.of(frameworkMethod.invokeExplosively(test), name));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
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
            // Suppress the Checkstyle warnings, because the meaning is clear
            // SUPPRESS CHECKSTYLE NEXT 2 MagicNumber
            node.setWidth(50.0);
            node.setHeight(50.0);
        }

        return layoutGraph;
    }

}
