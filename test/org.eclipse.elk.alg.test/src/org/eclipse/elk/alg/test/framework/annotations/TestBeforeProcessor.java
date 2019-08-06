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

import org.eclipse.elk.core.alg.ILayoutProcessor;

/**
 * This annotation can be used as class or as method annotation. If it is used as class annotation all the methods in
 * the class not annotated with this annotation or the RunAfterProcessor annotation are executed before the specified
 * processor. Used as method annotation just the annotated method is executed before the processor. The
 * {@link #onRootOnly()}option can just be used, if it is supported by the layout algorithm. This annotation can be used
 * more then once per class or method.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@Inherited
@Repeatable(TestBeforeProcessors.class)
public @interface TestBeforeProcessor {
    
    /** The processor the graph should be executed before. */
    Class<? extends ILayoutProcessor<?>> value();
    
}
