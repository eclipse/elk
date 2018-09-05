/*******************************************************************************
 * Copyright (c) 2018 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.alg.test.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.eclipse.elk.alg.layered.graph.LGraph;
import org.eclipse.elk.core.alg.ILayoutProcessor;

/**
 * This annotation can be used as class or as method annotation. If it is used as class annotation all the methods in
 * the class not annotated with this annotation or the {@link RunBeforeProcessor} annotation are executed after the
 * specified processor. Used as method annotation just the annotated method is executed after the processor. The
 * {@link #onRoot()} annotation can just be used, if it is supported by the layout algorithm. This annotation can be
 * used more then one time per class or method.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Inherited
@Repeatable(RunAfterProcessors.class)
public @interface RunAfterProcessor {
    
    /** The processor the graph should be executed after. */
    Class<? extends ILayoutProcessor<LGraph>> processor();

    /** Whether the test should be executed just on the root graph. */
    boolean onRoot() default false;
    
}
