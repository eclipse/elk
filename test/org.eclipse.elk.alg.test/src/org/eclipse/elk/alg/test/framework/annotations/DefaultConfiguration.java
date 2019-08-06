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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Instruct the test framework to configure elements of input graphs with default values. The configuration with
 * defaults is done after any {@link Configurator configuration method} has run.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
public @interface DefaultConfiguration {
    
    /** Whether edges should be configured with default values. */
    boolean edges() default true;
    /** Whether nodes should be configured with default values. */
    boolean nodes() default true;
    /** Whether ports should be configured with default values. */
    boolean ports() default true;
    
}
