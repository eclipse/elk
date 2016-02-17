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
package org.eclipse.elk.core.comments;

import java.util.List;
import java.util.function.Function;

import org.eclipse.elk.graph.KNode;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * Determines if a comment is eligible for attachment based on its size. Use the methods named
 * {@code withXXX} to configure the filter.
 */
public class TextPrefixFilter implements IEligibilityFilter {
    
    /** Function that provides the text for comments. */
    private Function<KNode, String> commentTextProvider = null;
    /** List of prefixes. */
    private List<String> prefixes = Lists.newArrayList();
    /**
     * If {@code true}, a comment that starts with one of the prefixes is not eligible for
     * attachment. If {@code false}, a comment must start with one of the prefixes to be eligible.
     */
    private boolean rejectCommentOnPrefixMatch = true;
    /** Whether to match prefixes in case sensitive mode. */
    private boolean caseSensitive = false;
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////
    // Configuration
    
    /**
     * Configures the filter to use the given function to retrieve the text of a comment.
     * 
     * <p>
     * If this method is not called, the filter will throw an exception during preprocessing.
     * </p>
     * 
     * @param f
     *            function to be used to retrieve comment texts.
     * @return this object for method chaining.
     */
    public TextPrefixFilter withCommentTextProvider(final Function<KNode, String> f) {
        if (f == null) {
            throw new IllegalArgumentException("Comment text function cannot be null.");
        }
        
        commentTextProvider = f;
        
        return this;
    }
    
    /**
     * Configures the filter to require comments to begin with one of the prefixes to be eligible.
     * 
     * <p>
     * If this method is not called, the filter will consider a comment that matches one of the
     * prefixes to not be eligible for attachment.
     * </p>
     * 
     * @return this object for method chaining.
     */
    public TextPrefixFilter withPrefixMatchRequiredForEligibility() {
        rejectCommentOnPrefixMatch = false;
        
        return this;
    }
    
    /**
     * Configures this filter to do the prefix matching in a case-sensitive manner.
     * 
     * <p>
     * If this method is not called, prefix matching is not case-sensitive.
     * </p>
     * 
     * @return this object for method chaining.
     */
    public TextPrefixFilter withCaseSensitiveMatching() {
        caseSensitive = true;
        
        return this;
    }
    
    /**
     * Adds the given prefix to the list of prefixes.
     * 
     * @param prefix
     *            the prefix to add.
     * @return this object for method chaining.
     * @throws IllegalArgumentException
     *             if the prefix is {@code null} or the empty string.
     */
    public TextPrefixFilter addPrefix(final String prefix) {
        if (Strings.isNullOrEmpty(prefix)) {
            throw new IllegalArgumentException("Prefix cannot be null or empty. Wouldn't make sense.");
        }
        
        prefixes.add(prefix);
        
        return this;
    }
    
    /**
     * Checks whether the current configuration is valid.
     * 
     * @throws IllegalStateException
     *             if the configuration is invalid.
     */
    private void checkConfiguration() {
        if (commentTextProvider == null) {
            throw new IllegalStateException("A comment text provider is required.");
        }
        
        if (prefixes.isEmpty()) {
            throw new IllegalStateException("At least one prefix is required.");
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////
    // IEligibilityFilter
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void preprocess(final KNode graph, final boolean includeHierarchy) {
        checkConfiguration();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean eligibleForAttachment(final KNode comment) {
        String commentText = commentTextProvider.apply(comment);

        if (!Strings.isNullOrEmpty(commentText)) {
            for (String prefix : prefixes) {
                // We can only have a match if the comment is as least as long as the prefix
                if (commentText.length() >= prefix.length()) {
                    String commentPrefix = commentText.substring(0, prefix.length());
                    
                    boolean startsWithPrefix = caseSensitive
                            ? commentPrefix.equals(prefix)
                            : commentPrefix.equalsIgnoreCase(prefix);
                    
                    if (startsWithPrefix) {
                        // If we reject a comment once it matches a prefix, we now need to return false.
                        // If we require comments to match a prefix, we now need to return true
                        return !rejectCommentOnPrefixMatch;
                    }
                }
            }
        }

        // If we arrive here, no prefix has matched
        return rejectCommentOnPrefixMatch;
    }

}
