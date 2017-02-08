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
import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.EnumSet
import java.util.LinkedList
import org.eclipse.elk.core.meta.MetaDataRuntimeModule.MelkOutputConfigurationProvider
import org.eclipse.elk.core.meta.metaData.MdAlgorithm
import org.eclipse.elk.core.meta.metaData.MdBundle
import org.eclipse.elk.core.meta.metaData.MdBundleMember
import org.eclipse.elk.core.meta.metaData.MdGroup
import org.eclipse.elk.core.meta.metaData.MdModel
import org.eclipse.elk.core.meta.metaData.MdOption
import org.eclipse.elk.core.options.GraphFeature
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.common.types.JvmEnumerationType
import org.eclipse.xtext.common.types.JvmField
import org.eclipse.xtext.common.types.JvmParameterizedTypeReference
import org.eclipse.xtext.common.types.JvmTypeReference
import org.eclipse.xtext.generator.AbstractFileSystemAccess2
import org.eclipse.xtext.generator.IFileSystemAccess
import org.eclipse.xtext.nodemodel.util.NodeModelUtils
import org.eclipse.xtext.xbase.XExpression
import org.eclipse.xtext.xbase.compiler.JvmModelGenerator

/**
 * Generates documentation from the source model that pertains to a *.melk file.
 * <p>
 * For each algorithm, option and group in the model, a Markdown file is created in the directory specified by
 * the vm argument {@code elk.metadata.documentation.folder}. This path usually points to the docs folder in the
 * git repository for elk, when the Oomph setup appends the vm argument to the eclipse.ini.
 * The docs folder in the master branch will be hosted on a Github page.
 * </p>
 * 
 * @author dag
 */
class MelkDocumentationGenerator extends JvmModelGenerator {
    /** The {@code IFileSystemAccess} used to read additional documentation files stored within
     *  the eclipse project that contains the model. */
    private IFileSystemAccess fsa
    /** The place where the generated documentation is stored. */
    private Path outputPath
    /** The directory containing additional documentation files. */
    private Path documentationFolder
    /** The directory relative to {@code outputPath} where images are stored. */
    private static val IMAGES_OUTPUT_FOLDER = "images"
    
    /**
     * The method {@code internalDoGenerate} is called for each {@link MdModel} derived from a *.melk file.
     * 
     * @param model
     *      the {@link MdModel} that documentation is generated for.
     * @param fsa
     *      the {@code IFileSystemAccess} used to read files
     */
    def dispatch void internalDoGenerate(MdModel model, IFileSystemAccess fsa) {
        this.fsa = fsa;
        //TODO maybe move retrieval of system property into constructor
        
        // The property is set as a vm argument in the eclipse.ini and holds the output folder for documentation.
        val propertyOutputFolder = System.getProperty("elk.metadata.documentation.folder")
        // If the property is not set, don't generate any documentation.
        if (propertyOutputFolder === null) {
            return
        }
        
        outputPath = Paths.get(propertyOutputFolder).resolve("_pages")
        // create directory if necessary
        if (Files.notExists(outputPath)) {
            Files.createDirectories(outputPath)
        }
        
        // If the model has no name or no bundle section, nothing can be generated.
        if (model.name === null || model.bundle === null) {
            return
        }
        
        val bundle = model.bundle
        val members = bundle.members
        
        // The {@code documentationFolder} in the bundle section tells us where to look for additional 
        // documentation files.
        if (bundle.documentationFolder !== null) {
            documentationFolder = Paths.get(bundle.documentationFolder)
        } else {
            // The default folder for additional documentation is a docs folder on project level.
            documentationFolder = Paths.get("docs")
        }

        // write documentation for layout algorithms, groups and options
        for (member : Iterables.concat(members.filter(MdAlgorithm), members.allGroupDefinitions,
            members.allOptionDefinitions)) {
            member.writeDoc
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    // generation functions

    /**
     * The dispatch method {@code generateDoc} is called for each {@link MdBundleMember} that is supposed to be
     * documented.
     * This generates a documentation String in Markdown format.
     * 
     * @param algorithm
     *      the {@link MdAlgorithm} from the {@link MdModel}
     * @return
     *      the Markdown String
     */
    private def dispatch String generateDoc(MdAlgorithm algorithm) {
        // The frontmatter header (between dashes) tells the static-site-generator how to render this document.
        var doc = '''
        ---
        layout: page
        title: «algorithm.label ?: algorithm.name»
        type: algorithm
        ---
        ## «algorithm.label ?: algorithm.name»
        
        '''
        
        if (algorithm.previewImage !== null) {
            val newFileName = algorithm.qualifiedName.replace('.', '-') + "_preview_" 
                                + algorithm.previewImage.substring(algorithm.previewImage.lastIndexOf('/') + 1)
            // copy previewImage into images folder within the rest of the documentation
            algorithm.previewImage.copyImageToOutputPath(newFileName)
            doc += "![](" + IMAGES_OUTPUT_FOLDER + "/" + newFileName + ")\n"
        }
        
        doc += '''
        **Identifier:** «algorithm.qualifiedName»
        **Meta Data Provider:** «algorithm.bundle.targetClass»
        
        '''
        
        if (!algorithm.description.isNullOrEmpty) {
            doc += '''
            ### Description
            
            «algorithm.description.replace('\n', ' ').replace('\t', ' ').replaceAll(" +", " ")»
            
            '''
        }
        
        // If the algorithm belongs to a category, get its description as well.
        if (algorithm.category !== null) {
            doc += '''
            ## Category: «algorithm.category.label ?: algorithm.category.name»
            
            '''
            
            if (!algorithm.category.description.isNullOrEmpty) {
                doc += '''
                «algorithm.category.description.replace('\n', ' ').replace('\t', ' ').replaceAll(" +", " ")»
                
                '''
            }
        }
    
        // table of supported graph features
        if (!algorithm.supportedFeatures.empty) {
            doc += '''
            ## Supported Graph Features
            
            Name | Description
            ----|----
            '''
            for (supportedFeature : algorithm.supportedFeatures) {
                doc += supportedFeature.literal.split("_").map[it.toFirstUpper].join(" ") + 
                        // the description of each feature is retrieved from core.options.GraphFeature
                        " | " + GraphFeature.valueOf(supportedFeature.name()).description + "\n"
            }
            doc += "\n"
        }
        
        if (algorithm.documentation !== null) {
            doc += '''
            ## Additional Documentation
            
            «algorithm.documentation.additionalDocumentation(algorithm.qualifiedName.replace('.', '-'))»
            
            '''
        }
        
        // table of supported options
        if (!algorithm.supportedOptions.empty) {
            doc += '''
            ## Supported Options
            
            Option | Type | Default Value | Identifier
            ----|----|----|----
            '''
            for (supportedOption : algorithm.supportedOptions.sortBy[(it.option.label ?: it.option.name).toLowerCase]) {
                var optionFileName = supportedOption.option.qualifiedName.replace('.', '-')
                // If the supported option specifies additional documentation, it is specific to this algorithm,
                // therefore an alternative file is created for the option documentation that differs from the general
                // documentation.
                if (supportedOption.documentation !== null) {
                    optionFileName = supportedOption.option.qualifiedName.replace('.', '-') + "(" +
                        algorithm.qualifiedName + ")"
                    writeDoc(
                        optionFileName,
                        supportedOption.option.generateDoc(
                            (supportedOption.option.label ?: supportedOption.option.name) + " ("
                                + (algorithm.label ?: algorithm.name) + ")",
                            supportedOption.documentation.additionalDocumentation(optionFileName))
                    )
                }
                
                doc += '''[«supportedOption.option.label ?: supportedOption.option.name»](«optionFileName»)''' +
                ''' | `«supportedOption.option.type?.simpleName/*?.replace("<","&lt;").replace(">","&gt;")*/»`''' +
                ''' | `«supportedOption.value?.text ?: supportedOption.option.defaultValue.text»`''' +
                ''' | «supportedOption.option.qualifiedName.replace(".", "&#8203;.")»
                '''
            }
            doc += "\n"
        }
        
        return doc
    }
    
    /**
     * The dispatch method {@code generateDoc} is called for each {@link MdBundleMember} that is supposed to be
     * documented.
     * This generates a documentation String in Markdown format.
     * 
     * @param option
     *      the {@link MdOption} from the {@link MdModel}
     * @return
     *      the Markdown String
     */
    private def dispatch String generateDoc(MdOption option) {
        generateDoc(option, option.label ?: option.name,
            option.documentation.additionalDocumentation(option.qualifiedName.replace('.', '-')))
    }
    
    /**
     * This method is wrapped by {@link #generateDoc(MdOption)}.
     * Here the title and additional documentation can be specified so that an algorithm that supports this option can 
     * create an alternate version of this documentation, that is specific to the algorithm.
     * 
     * @param option
     *      the {@link MdOption} from the {@link MdModel}
     * @param title
     *      the title for the documentation page
     * @param additionalDoc
     *      a String of additional documentation that will be appended 
     * @return
     *      the Markdown String
     * @see #additionalDocumentation
     */
    private def String generateDoc(MdOption option, String title, String additionalDoc) {
        // The frontmatter header (between dashes) tells the static-site-generator how to render this document.
        var doc = '''
        ---
        layout: page
        title: «title»
        type: option
        ---
        ## «if (option.deprecated) '''~~«title»~~''' else title»
        '''
        
        val kinds = #["deprecated"->option.deprecated, "advanced"->option.advanced, "programmatic"->option.programmatic,
                        "output"->option.output, "global"->option.global].filter[it.value]
        
        doc += '''
        
        ----|----
        «if (!kinds.empty) '''**Type:** | «kinds.map[it.key].join(", ")»'''»
        **Identifier:** | «option.qualifiedName»
        **Meta Data Provider:** | «option.bundle.targetClass»
        **Value Type:** | `«option.type?.identifier»`«if (option.type?.type instanceof JvmEnumerationType) " (Enum)"»
        «if (option.type.possibleValues !== null) "**Possible Values:** | " + 
            option.type.possibleValues.map[
                "`" + it.simpleName + "`"
                + it.annotations?.sortBy[it.annotation?.simpleName]
                        .join(" (", " and ", ")",["*`@" + it.annotation?.simpleName + "`*"])
                ].join("<br>")»
        «if (option.defaultValue !== null) '''**Default Value:** | `«option.defaultValue.text
                    »` (as defined in «option.bundle.idPrefix ?: (option.bundle.eContainer as MdModel).name»)'''»
        «if (option.lowerBound !== null) '''**Lower Bound:** | `«option.lowerBound.text»`'''»
        «if (option.upperBound !== null) '''**Upper Bound:** | `«option.upperBound.text»`'''»
        «if (!option.targets.empty) "**Applies To:** | " + option.targets.map[it.literal].join(", ")»
        «if (!option.legacyIds.empty) "**Legacy Id:** | " + option.legacyIds.join(", ")»
        «if (!option.dependencies.empty) "**Dependencies:** | " + option.dependencies.map["[" + it.target.qualifiedName
                                                  + "](" + it.target.qualifiedName.replace('.', '-') + ")"].join(", ")»
        «if (!option.groups.empty) "**Containing Groups:** | " + option.groups.map["[" + it.name+ "](" + 
                                                                it.qualifiedName.replace('.', '-') + ")"].join(" -> ")»
        «if (option.description !== null) "\n### Description\n\n" + 
                            option.description.replace('\n', ' ').replace('\t', ' ').replaceAll(" +", " ")»
        «if (!additionalDoc.nullOrEmpty) "\n## Additional Documentation\n\n" + additionalDoc»
        '''

        return doc
    }
    
    /**
     * The dispatch method {@code generateDoc} is called for each {@link MdBundleMember} that is supposed to be
     * documented.
     * This generates a documentation String in Markdown format.
     * 
     * @param group
     *      the {@link MdGroup} from the {@link MdModel}
     * @return
     *      the Markdown String
     */
    private def dispatch String generateDoc(MdGroup group) {
        
        var doc = '''
        ---
        layout: page
        title: «group.name»
        type: group
        ---
        ## «group.name»
        
        **Identifier:** «group.qualifiedName»
        «if (!group.children.filter(MdOption).empty) "\n### Options:\n\n" + group.children.filter(MdOption).map[
            "[" + (it.label ?: it.name) + "](" + it.qualifiedName.replace('.', '-') + ")"].join("\n")»
        «if (!group.children.filter(MdGroup).empty) "\n### Subgroups:\n\n" + group.children.filter(MdGroup).map[
            "[" + it.name + "](" + it.qualifiedName.replace('.', '-') + ")"].join("\n")»
        «if (group.documentation !== null) "\n## Additional Documentation\n\n" 
            + group.documentation.additionalDocumentation(group.qualifiedName.replace('.', '-'))»
        '''
        return doc
    }
    
    /** 
     * This is a wrapper for {@link #writeDoc(String, String) writeDoc(String, String)} 
     * that uses the full name of a member as filename.
     * 
     * @param member
     *      the {@link MdBundleMember} that documentation is generated for
     */
    private def void writeDoc(MdBundleMember member) {
        val fileName = member.qualifiedName.replace('.', '-')
        writeDoc(fileName, member.generateDoc)
    }
    
    /**
     * Creates a file in {@code outputPath} to store the generated Markdown in.
     * 
     * @param fileName
     *      name of the file
     * @param documentation
     *      generated markdown string
     */
    private def void writeDoc(String fileName, String documentation) {
        var file = fileName
        // add file extension for Markdown
        if (!file.endsWith(".md")) {
            file += ".md"
        }

        var PrintWriter out
        try {
            out = new PrintWriter(outputPath.resolve(file).toString);
            out.print(documentation)
        } catch (Exception exception) {
            exception.printStackTrace
        } finally {
            out?.close
        }
    }
    
    /**
     * Extracts additional documentation from file or text.
     * A leading {@code @} signals that the following string is supposed to be interpreted as a file name.
     * The file has to be located at the path specified by the {@code documentationFolder} in the bundle section, that
     * defaults to "docs".
     * <p>
     * Images that are linked to in Markdown syntax are copied to the {@code outputPath} and their URLs are adjusted
     * accordingly.
     * </p>
     * 
     * @param documentation
     *      {@code @<file name>} or text in Markdown format
     * @param fileNamePrefix
     *      prefix of the file name for the copy to associate it with the member it belongs to
     * @return
     *      the Markdown String with replaced image references
     */
    private def String additionalDocumentation(String documentation, String fileNamePrefix) {
        if (documentation === null) {
            return ""
        }
        
        var doc = ""
        
        if (!documentation.startsWith("@")) {
            doc = documentation.replace('\n', ' ').replace('\t', ' ').replaceAll(" +", " ")
        } else {
            try {
                // AbstractFileSystemAccess2 needs an OutputConfiguration to work, that is defined in MetaDataRuntimeModule
                doc = (fsa as AbstractFileSystemAccess2).readTextFile(
                    documentationFolder.resolve(documentation.substring(1)).toString,
                    MelkOutputConfigurationProvider.AD_INPUT).toString
            } catch (Exception exception) {
                exception.printStackTrace
            }
        }
        
        // copy all images to the output location and update their URLs in the documentation
            // regex doesn't work here because links may contain parentheses that have to be counted
        var res = ""
        var int i = 0
        var int l = doc.length
        while (i < l) {
            res += doc.charAt(i)
            i++
            // ![ indicates the beginning of an image reference
            if (res.endsWith("![")) {
                // Skip over alt text between braces. We are interested in the path surrounded by parentheses.
                while (i < l && !res.endsWith("](")) {
                    res += doc.charAt(i)
                    i++
                }
                var path = ""
                var int openedParens = 1
                // count parentheses to determine the closing one
                while (i < l && openedParens > 0) {
                    path += doc.charAt(i)
                    i++
                    switch (doc.charAt(i).toString) { // xtend generates a String from ')' not a char
                    	case "(": openedParens++
                    	case ")": openedParens--
                    }
                }
                // remove whitespace and possible title statement from path
                var imgTitle = ""
                path = path.trim
                if (path.endsWith('\"')) {
                    imgTitle = " " + path.substring(path.lastIndexOf('\"', path.length - 2))
                    path = path.substring(0, path.lastIndexOf('\"', path.length - 2)).trim
                }
                // copy file into images folder within the rest of the documentation
                val newFileName = fileNamePrefix + "_" + path.substring(path.lastIndexOf('/') + 1)
                documentationFolder.resolve(path).toString.copyImageToOutputPath(newFileName)
                // replace the URL with the new path
                res += IMAGES_OUTPUT_FOLDER + "/" + newFileName + imgTitle
            }
        }
        
        return res
    }
    
    ////////////////////////////////////////////////////////////////////////////////
    // utility functions
    
    /**
     * Copies images or any file from within the project to the place where the generated documentation is located.
     * 
     * @param path
     *      the path to the image relative to the eclipse project
     * @param newFilename
     *      file name for the copy (should be associable to the issuing member)
     */
    private def copyImageToOutputPath(String path, String newFileName) {
        if (path === null || newFileName === null) {
            return
        }

        try {
            var iStream = (fsa as AbstractFileSystemAccess2).readBinaryFile(path,
                MelkOutputConfigurationProvider.AD_INPUT)
            val imgFolder = outputPath.resolve(IMAGES_OUTPUT_FOLDER)
            // create directory if necessary
            if (Files.notExists(imgFolder)) {
                Files.createDirectory(imgFolder)
            }
            Files.copy(iStream, imgFolder.resolve(newFileName), StandardCopyOption.REPLACE_EXISTING)
        } catch (Exception exception) {
            exception.printStackTrace
        }
    }
    
    // readable text representation of an XExpression
    private def String getText(XExpression exp) {
        if (exp === null) {
            return "<not defined>"
        }
        var text = NodeModelUtils.getNode(exp).text.trim
        // If it's a function name, append parentheses. We just infer that from the lowercaseness of the first letter
        // from the last segment, if there are dot separated segments at all.
        val fName = text.substring(text.lastIndexOf('.') + 1)
        if (Character.isLowerCase(fName.charAt(0)) && fName != text && !fName.endsWith(")")) {
            text += "()"
        }
        return text
    }
    
    // possible values for enumeration types
    private def Iterable<JvmField> getPossibleValues(JvmTypeReference type) {
        if (type !== null) {
            val jvmType = type.type
            if (jvmType instanceof JvmEnumerationType) {
                val dType = jvmType as JvmDeclaredType
                return dType.declaredFields
            // in case of EnumSet the inner type is extracted
            } else if (jvmType.identifier == EnumSet.canonicalName) {
                val dType = (type as JvmParameterizedTypeReference).arguments.head.type as JvmDeclaredType
                return dType.declaredFields
            }
        }
        return null
    }
    
    // iterator for nested groups (flattened)
    private def Iterable<MdGroup> getAllGroupDefinitions(Iterable<? extends MdBundleMember> elements) {
        val groups = elements.filter(MdGroup)
        return Iterables.concat(groups, groups.map[it.children.getAllGroupDefinitions].flatten)
    }
    
    // iterator for nested options (flattened)
    private def Iterable<MdOption> getAllOptionDefinitions(Iterable<? extends MdBundleMember> elements) {
        return Iterables.concat(elements.filter(MdOption),
                                elements.filter(MdGroup).map[it.children.getAllOptionDefinitions].flatten)
    }
    
    // retrieve bundle associated with a member
    private def MdBundle getBundle(MdBundleMember member) {
        var parent = member.eContainer
        while (!(parent instanceof MdBundle)) {
            parent = parent.eContainer
        }
        return parent as MdBundle
    }
    
    // groups containing this member
    private def Iterable<MdGroup> getGroups(MdBundleMember member) {
        val groups = new LinkedList
        var group = member.eContainer
        while (group instanceof MdGroup) {
            groups.addFirst(group) // ordered from outer to inner
            group = group.eContainer
        }
        return groups
    }
    
    // The qualified name of a member consists of the bundles idPrefix, the containing groups and the members name
    // joined by dots.
    private def String getQualifiedName(MdBundleMember member) {
        val bundle = member.bundle
        val model = bundle.eContainer as MdModel
        var prefix = bundle.idPrefix ?: model.name

        if (prefix.endsWith(member.name)) {
           prefix = prefix.substring(0, prefix.lastIndexOf('.')) 
        }
        
        return prefix
               + (if (member.groups.empty) '' else '.')
               + member.groups.map[it.name].join('.') 
               + '.' + member.name
    }
}
