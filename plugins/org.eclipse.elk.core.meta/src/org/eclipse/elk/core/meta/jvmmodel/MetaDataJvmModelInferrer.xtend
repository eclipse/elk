/*******************************************************************************
 * Copyright (c) 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.meta.jvmmodel

import com.google.common.collect.Iterables
import com.google.inject.Inject
import java.util.EnumSet
import java.util.LinkedList
import org.eclipse.elk.core.data.ILayoutMetaDataProvider
import org.eclipse.elk.core.data.LayoutAlgorithmData
import org.eclipse.elk.core.data.LayoutCategoryData
import org.eclipse.elk.core.data.LayoutOptionData
import org.eclipse.elk.core.data.LayoutOptionData.Type
import org.eclipse.elk.core.meta.metaData.MdAlgorithm
import org.eclipse.elk.core.meta.metaData.MdBundle
import org.eclipse.elk.core.meta.metaData.MdBundleMember
import org.eclipse.elk.core.meta.metaData.MdCategory
import org.eclipse.elk.core.meta.metaData.MdGroup
import org.eclipse.elk.core.meta.metaData.MdModel
import org.eclipse.elk.core.meta.metaData.MdProperty
import org.eclipse.elk.core.meta.metaData.MdPropertyDependency
import org.eclipse.elk.core.meta.metaData.MdPropertySupport
import org.eclipse.elk.core.options.GraphFeature
import org.eclipse.elk.core.util.AlgorithmFactory
import org.eclipse.elk.core.util.IDataObject
import org.eclipse.elk.graph.properties.IProperty
import org.eclipse.elk.graph.properties.Property
import org.eclipse.xtend2.lib.StringConcatenationClient
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.common.types.JvmEnumerationType
import org.eclipse.xtext.common.types.JvmGenericType
import org.eclipse.xtext.common.types.JvmParameterizedTypeReference
import org.eclipse.xtext.common.types.JvmPrimitiveType
import org.eclipse.xtext.common.types.JvmTypeReference
import org.eclipse.xtext.common.types.JvmVisibility
import org.eclipse.xtext.common.types.util.Primitives
import org.eclipse.xtext.util.Strings
import org.eclipse.xtext.xbase.jvmmodel.AbstractModelInferrer
import org.eclipse.xtext.xbase.jvmmodel.IJvmDeclaredTypeAcceptor
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder

/**
 * Infers a JVM model from the source model. 
 *
 * <p>The JVM model should contain all elements that would appear in the Java code which is generated from
 * the source model. Other models link against the JVM model rather than the source model.</p>     
 */
class MetaDataJvmModelInferrer extends AbstractModelInferrer {

    /**
     * Convenience API to build and initialize JVM types and their members.
     */
    @Inject extension JvmTypesBuilder
    
    @Inject extension Primitives

    /**
     * The dispatch method {@code infer} is called for each instance of the
     * given element's type that is contained in a resource.
     * 
     * @param model
     *            the model to create one or more {@link JvmDeclaredType declared types} from.
     * @param acceptor
     *            each created {@link JvmDeclaredType type} without a container should be passed to the acceptor
     *            in order to get attached to the current resource. The acceptor's
     *            {@link IJvmDeclaredTypeAcceptor#accept(org.eclipse.xtext.common.types.JvmDeclaredType) accept(..)}
     *            method takes the constructed empty type for the pre-indexing phase. This one is further
     *            initialized in the indexing phase using passed closure.
     * @param isPreIndexingPhase
     *            whether the method is called in a pre-indexing phase, i.e.
     *            when the global index is not yet fully updated. You must not
     *            rely on linking using the index if isPreIndexingPhase is
     *            <code>true</code>.
     */
    def dispatch void infer(MdModel model, IJvmDeclaredTypeAcceptor acceptor, boolean isPreIndexingPhase) {
        if (model.name === null || model.bundle === null) {
            return
        }
        val bundle = model.bundle
        acceptor.accept(bundle.toClass(bundle.qualifiedTargetClass)) [
            superTypes += typeRef(ILayoutMetaDataProvider)
            fileHeader = model.documentation
            documentation = bundle.documentation
            
            // 1. Public constants for all declared properties
            for (property : bundle.members.allPropertyDefinitions) {
                val constant = property.toPropertyConstant
                if (property.defaultValue !== null)
                    members += property.toPropertyDefault
                members += constant
            }
            // 2. Private constants for required values of option dependencies
            for (property : bundle.members.allPropertyDefinitions) {
                for (dependency : property.dependencies) {
                    if (dependency.value !== null)
                        members += dependency.toDependencyValue
                }
            }
            // 3. Private constants for default option values of algorithms
            for (algorithm : bundle.members.filter(MdAlgorithm)) {
                for (support : algorithm.supportedOptions) {
                    if (support.value !== null)
                        members += support.toSupportDefault
                    if (support.duplicated)
                        members += support.toSupportDuplicatedConstant
                }
            }
            // 4. Implementation of ILayoutMetaDataProvider#apply(Registry)
            members += bundle.toMethod('apply', typeRef(void)) [
                parameters += bundle.toParameter('registry', typeRef(ILayoutMetaDataProvider.Registry))
                body = '''
                    «registerLayoutOptions(bundle)»
                    «registerLayoutCategories(bundle)»
                    «registerLayoutAlgorithms(bundle)»
                '''
            ]
            
        ]
    }
    
    private def Iterable<MdProperty> getAllPropertyDefinitions(Iterable<? extends MdBundleMember> elements) {
        Iterables.concat(
            elements.filter(MdProperty),
            elements.filter(MdGroup)
                    .map[it.children.getAllPropertyDefinitions].flatten)
    }
    
    private def String getQualifiedTargetClass(MdBundle bundle) {
        val model = bundle.eContainer as MdModel
        val bundleClass = bundle.targetClass ?: 'Metadata'
        return model.name + '.' + bundleClass
    }
    
    private def toPropertyConstant(MdProperty property) {
        return property.toField(
            property.constantName,
            typeRef(IProperty, property.type ?: typeRef(Object))
        ) [
            visibility = JvmVisibility.PUBLIC
            static = true
            final = true
            deprecated = property.deprecated
            initializer = '''
                new «Property»<«property.type.asWrapperTypeIfPrimitive ?: typeRef(Object)»>(
                        «property.qualifiedName.toCodeString»«IF property.defaultValue !== null»,
                        «property.defaultConstantName»«ENDIF»)«»'''
            documentation = property.description.trimLines
        ]
    }
    
    private def toPropertyDefault(MdProperty property) {
        val propertyType = property.type.cloneWithProxies ?: typeRef(Object)
        return property.toField(property.defaultConstantName, propertyType) [
            visibility = JvmVisibility.PRIVATE
            static = true
            final = true
            initializer = property.defaultValue
            documentation = '''Default value for {@link #«property.constantName»}.'''
        ]
    }
    
    private def toDependencyValue(MdPropertyDependency dependency) {
        val source = dependency.eContainer as MdProperty
        val propertyType = dependency.target.type.cloneWithProxies ?: typeRef(Object)
        return dependency.toField(dependency.dependencyConstantName, propertyType) [
            visibility = JvmVisibility.PRIVATE
            static = true
            final = true
            initializer = dependency.value
            documentation = '''Required value for dependency between {@link #«source.constantName»} and {@link #«dependency.target.constantName»}.'''
        ]
    }
    
    private def toSupportDefault(MdPropertySupport support) {
        val algorithm = support.eContainer as MdAlgorithm
        val propertyType = support.property.type.cloneWithProxies ?: typeRef(Object)
        return support.toField(support.supportConstantName, propertyType) [
            visibility = JvmVisibility.PRIVATE
            static = true
            final = true
            initializer = support.value
            documentation = '''Default value for {@link #«support.property.constantName»} with algorithm "«algorithm.label ?: algorithm.name»".'''
        ]
    }
    
    private def toSupportDuplicatedConstant(MdPropertySupport support) {
        return support.toField(
            support.property.constantName,
            typeRef(IProperty, support.property.type ?: typeRef(Object))
        ) [
            visibility = JvmVisibility.PUBLIC
            static = true
            final = true
            deprecated = support.property.deprecated
            initializer = '''
                new «Property»<«support.property.type.asWrapperTypeIfPrimitive ?: typeRef(Object)»>(
                        «typeRef(support.property.bundle.qualifiedTargetClass)».«support.property.constantName»,
                        «IF support.value === null»null«ELSE»«support.supportConstantName»«ENDIF»)'''
            documentation = '''Overridden value for «support.property.label ?: support.property.name».'''
        ]
    }
    
    private def StringConcatenationClient registerLayoutOptions(MdBundle bundle) '''
        «FOR property : bundle.members.getAllPropertyDefinitions»
            registry.register(new «LayoutOptionData»(
                «property.qualifiedName.toCodeString»,
                «property.groups.map[name].join('.').toCodeString»,
                «(property.label ?: property.name).shrinkWhiteSpace.toCodeString»,
                «property.description.shrinkWhiteSpace.toCodeString»,
                «IF property.defaultValue === null»
                    null,
                «ELSE»
                    «property.defaultConstantName»,
                «ENDIF»
                «LayoutOptionData».Type.«property.optionType»,
                «property.optionTypeClass».class,
                «IF property.targets.empty»
                    null,
                «ELSE»
                    «EnumSet».of(«FOR t : property.targets SEPARATOR ', '»«LayoutOptionData».Target.«t.toString.toUpperCase»«ENDFOR»),
                «ENDIF»
                «IF property.programmatic || property.output || property.global»
                    «LayoutOptionData».Visibility.HIDDEN
                «ELSEIF property.advanced»
                    «LayoutOptionData».Visibility.ADVANCED
                «ELSE»
                    «LayoutOptionData».Visibility.VISIBLE
                «ENDIF»
                «IF !property.legacyIds.empty»
                    «property.legacyIds.map[', "' + it + '"'].join»
                «ENDIF»
            ));
            «FOR dependency : property.dependencies»
                registry.addDependency(
                    «property.qualifiedName.toCodeString»,
                    «dependency.target.qualifiedName.toCodeString»,
                    «IF dependency.value === null»
                        null
                    «ELSE»
                        «dependency.dependencyConstantName»
                    «ENDIF»
                );
            «ENDFOR»
        «ENDFOR»
    '''
    
    private def StringConcatenationClient registerLayoutCategories(MdBundle bundle) '''
        «FOR category : bundle.members.filter(MdCategory)»
            registry.register(new «LayoutCategoryData»(
                «category.qualifiedName.toCodeString»,
                «(category.label ?: category.name).shrinkWhiteSpace.toCodeString»,
                «category.description.shrinkWhiteSpace.toCodeString»
            ));
        «ENDFOR»
    '''
    
    private def StringConcatenationClient registerLayoutAlgorithms(MdBundle bundle) '''
        «FOR algorithm : bundle.members.filter(MdAlgorithm)»
            registry.register(new «LayoutAlgorithmData»(
                «algorithm.qualifiedName.toCodeString»,
                «(algorithm.label ?: algorithm.name).shrinkWhiteSpace.toCodeString»,
                «algorithm.description.shrinkWhiteSpace.toCodeString»,
                new «AlgorithmFactory»(«algorithm.provider».class, "«algorithm.parameter»"),
                «algorithm.category?.qualifiedName.toCodeString»,
                «algorithm.bundle?.label.toCodeString»,
                «algorithm.previewImage.toCodeString»,
                «IF algorithm.supportedFeatures.empty»
                    null
                «ELSE»
                    «EnumSet».of(«FOR f : algorithm.supportedFeatures SEPARATOR ', '»«GraphFeature».«f.toString.toUpperCase»«ENDFOR»)
                «ENDIF»
            ));
            «FOR support : algorithm.supportedOptions»
                registry.addOptionSupport(
                    «algorithm.qualifiedName.toCodeString»,
                    «support.property.qualifiedName.toCodeString»,
                    «IF support.value === null»
                        null
                    «ELSE»
                        «support.supportConstantName»
                    «ENDIF»
                );
            «ENDFOR»
        «ENDFOR»
    '''
    
    private def Iterable<MdGroup> getGroups(MdBundleMember member) {
        val groups = new LinkedList
        var group = member.eContainer
        while (group instanceof MdGroup) {
            groups.addFirst(group)
            group = group.eContainer
        }
        groups
    }
    
    private def Type getOptionType(MdProperty property) {
        val jvmType = property.type?.type
        switch jvmType {
            
            JvmPrimitiveType: switch jvmType.identifier {
                case boolean.name:  return Type.BOOLEAN
                case int.name:      return Type.INT
                case float.name:    return Type.FLOAT
                // TODO it may be better to prevent double from the start
                case double.name:   return Type.FLOAT
            } 
            
            JvmGenericType: switch jvmType.identifier {
                case Boolean.canonicalName:   return Type.BOOLEAN
                case Integer.canonicalName:   return Type.INT
                case Float.canonicalName:     return Type.FLOAT
                case Double.canonicalName:    return Type.FLOAT
                case String.canonicalName:    return Type.STRING
                case EnumSet.canonicalName:   return Type.ENUMSET
                case jvmType.hasSupertype(IDataObject): return Type.OBJECT
            }
            
            JvmEnumerationType: return Type.ENUM
            
        }
        return Type.UNDEFINED;
    }
    
    private def boolean hasSupertype(JvmDeclaredType type, Class<?> superType) {
        if (type.superTypes.findFirst[ t | t.qualifiedName == superType.canonicalName] != null) {
            return true;
        } else {
            return type.superTypes
                        .map[rt | rt.type]
                        .filter(JvmDeclaredType)
                        .findFirst[t | t.hasSupertype(superType) ] != null
        }
    }    
    
    private def JvmTypeReference getOptionTypeClass(MdProperty property) {
        switch property.type?.type?.identifier {
            case null:
                typeRef(Void)
            case Double.canonicalName:
                typeRef(Float)
            case EnumSet.canonicalName: {
                val outer = property.type as JvmParameterizedTypeReference
                typeRef(outer.arguments.head?.type)    
            }
            default:
                typeRef(property.type?.type)
        }
    }
    
    private def String getQualifiedName(MdBundleMember member) {
        val bundle = member.bundle
        val model = bundle.eContainer as MdModel
        var prefix = bundle.idPrefix ?: model.name
        // if the last part of the idPrefix would be repeated by the 
        // member's name, we skip it
        if (member.name == prefix.substring(prefix.lastIndexOf('.') + 1, prefix.length)) {
           prefix = prefix.substring(0, prefix.lastIndexOf('.')) 
        }
        return prefix
               + (if (member.groups.empty) '' else '.')
               + member.groups.map[it.name].join('.') 
               + '.' + member.name
    }
    
    private def MdBundle getBundle(MdBundleMember member) {
        var parent = member.eContainer
        while (!(parent instanceof MdBundle)) {
            parent = parent.eContainer
        }
        return parent as MdBundle
    }
    
    private def String getConstantName(MdBundleMember member) {
        val name = member.name
        if (name !== null) {
            val result = new StringBuilder
            result.append(member.groups.map[it.name.toUpperCaseWithUnderscores].join('_'))
            if (result.length > 0) result.append('_')
            result.append(name.toUpperCaseWithUnderscores)
            return result.toString
        }
    }
    
    private def String toUpperCaseWithUnderscores(String str) {
        val result = new StringBuilder
        for (var i = 0; i < str.length; i++) {
            val c = str.charAt(i)
            if (Character.isUpperCase(c) && i > 0)
                result.append('_')
            result.append(Character.toUpperCase(c))
        }
        return result.toString
    }
    
    private def getDefaultConstantName(MdProperty property) {
        property.constantName + '_DEFAULT'
    }
    
    private def getDependencyConstantName(MdPropertyDependency dependency) {
        val property = dependency.eContainer as MdProperty
        property.constantName + '_DEP_' + dependency.target.constantName
    }
    
    private def getSupportConstantName(MdPropertySupport support) {
        val algorithm = support.eContainer as MdAlgorithm
        algorithm.constantName + '_SUP_' + support.property.constantName
    }
    
    private def String toCodeString(String s) {
        if (s === null)
            return 'null'
        else
            return '''"«Strings.convertToJavaString(s)»"'''
    }
    
    private def String shrinkWhiteSpace(String s) {
        if (s === null)
            return null
        val result = new StringBuilder
        var shrink = true
        for (var i = 0; i < s.length; i++) {
            val c = s.charAt(i)
            if (Character.isWhitespace(c)) {
                if (!shrink)
                    result.append(' ')
                shrink = true
            } else {
                result.append(c)
                shrink = false
            }
        }
        if (result.length > 0 && Character.isWhitespace(result.charAt(result.length - 1)))
            result.deleteCharAt(result.length - 1)
        return result.toString
    }
    
    private def String trimLines(String s) {
        if (s === null)
            null
        else
            s.split('\r?\n').map[trim].join('\n')
    }
    
}
