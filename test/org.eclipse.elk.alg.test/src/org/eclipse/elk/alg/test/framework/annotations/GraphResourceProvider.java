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

import org.eclipse.elk.alg.test.framework.io.AbstractResourcePath;

/**
 * Specifies the location of graphs that should be imported and laid out as a list of {@link AbstractResourcePath}
 * instances. This annotation can be used either with fields of type {@code List<AbstractResourcePath>} or with methods
 * which return such a list.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@Inherited
public @interface GraphResourceProvider {
    
}
