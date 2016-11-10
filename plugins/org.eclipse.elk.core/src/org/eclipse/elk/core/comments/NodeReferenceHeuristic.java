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

import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.elk.core.util.Pair;
import org.eclipse.elk.graph.ElkGraphElement;
import org.eclipse.elk.graph.ElkNode;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * A binary heuristic that checks if node names are referenced in comment texts. This can either be the
 * case or not, hence the term <em>binary heuristic</em>. Indeed, the heuristic will always output
 * either 0 or 1. It will only output 1 if a node's name appears in a comment's text, and if no other
 * node's name appears in it. The following configuration methods have to be called before using this
 * heuristic:
 * <ul>
 *   <li>{@link #withCommentTextProvider(Function)}</li>
 *   <li>{@link #withNodeNameProvider(Function)}</li>
 * </ul>
 * <p>
 * The heuristic can optionally be configured to only consider matches to be matches if the distance
 * between a comment and a matching node does not exceed a configurable threshold.
 * </p>
 * 
 * <p>
 * The heuristic can operate in two modes: a strict and a fuzzy mode.
 * </p>
 * 
 * 
 * <h3>Strict Mode</h3>
 * <p>
 * In strict mode, node names have to appear exactly as they are in the text of a comment, as a
 * separate word.
 * </p>
 * 
 * 
 * <h3>Fuzzy Mode</h3>
 * <p>
 * In fuzzy mode, the heuristic allows the comment text more freedom in mentioning node names. Apart
 * from ignoring case, as in strict mode, we here also allow arbitrary whitespace (including newlines)
 * between two parts of the name. A part is divided from another part either through spaces or through
 * camel case.
 * </p>
 */
public class NodeReferenceHeuristic implements IHeuristic {
    
    /** Function used to retrieve a comment's text. */
    private Function<ElkNode, String> commentTextFunction = null;
    /** Function used to retrieve a node's name. */
    private Function<ElkNode, String> nodeNameFunction = null;
    /** The bounds provider to use. */
    private IBoundsProvider boundsProvider = new ShapeLayoutBoundsProvider();
    /** The maixmum distance attached comments may be away from each other. */
    private double maxDistance = -1;
    /** Whether to use fuzzy mode when looking for occurrences of a node's name in a comment's text. */
    private boolean fuzzy = false;
    /** The comment-node attachments we've found during preprocessing. */
    private Map<ElkNode, ElkNode> foundAttachments = Maps.newHashMap();
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////
    // Configuration
    
    /**
     * Configures the heuristic to use the given function to obtain the text for a comment.
     * 
     * <p>
     * If this method is not called, the heuristic will throw an exception during preprocessing.
     * </p>
     * 
     * @param f
     *            the function to use.
     * @return this object for method chaining.
     */
    public NodeReferenceHeuristic withCommentTextProvider(final Function<ElkNode, String> f) {
        if (f == null) {
            throw new IllegalArgumentException("Comment text function cannot be null.");
        }
        
        commentTextFunction = f;
        
        return this;
    }
    
    /**
     * Configures the heuristic to use the given function to obtain the name of a node.
     * 
     * <p>
     * If this method is not called, the heuristic will throw an exception during preprocessing.
     * </p>
     * 
     * @param f
     *            the function to use.
     * @return this object for method chaining.
     */
    public NodeReferenceHeuristic withNodeNameProvider(final Function<ElkNode, String> f) {
        if (f == null) {
            throw new IllegalArgumentException("Node name function cannot be null.");
        }
        
        nodeNameFunction = f;
        
        return this;
    }
    
    /**
     * Configures the heuristic to be fuzzy when looking for occurrences of node names in a
     * comment's text. If fuzzy mode is off, only exact occurrences will be found.
     * 
     * <p>
     * If this method is not called, the heuristic will default to strict mode.
     * </p>
     * 
     * @return this object for method chaining.
     */
    public NodeReferenceHeuristic withFuzzyMatching() {
        fuzzy = true;
        return this;
    }
    
    /**
     * Configures the heuristic to use the given maximum attachment distance. A comment is only attached
     * to a referenced node if the distance between the two doesn't exceed this distance.
     * 
     * <p>
     * If this method is not called, the heuristic does not impose a distance restriction.
     * </p>
     * 
     * @param distance
     *            the maximum possible distance. Negative values disable the distance restriction.
     * @return this object for method chaining.
     */
    public NodeReferenceHeuristic withMaximumAttachmentDistance(final double distance) {
        this.maxDistance = distance;
        return this;
    }
    
    /**
     * Configures the heuristic to use the given bounds provider to determine the bounds of
     * comments.
     * 
     * <p>
     * If this method is not called, the {@link ShapeLayoutBoundsProvider} is used by default.
     * </p>
     * 
     * @param provider
     *            the bounds provider to use.
     * @return this object for method chaining.
     */
    public NodeReferenceHeuristic withBoundsProvider(final IBoundsProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("Bounds provider must not be null.");
        }
        
        this.boundsProvider = provider;
        return this;
    }
    
    /**
     * Checks whether the current configuration is valid.
     * 
     * @throws IllegalStateException
     *             if the configuration is invalid.
     */
    private void checkConfiguration() {
        if (commentTextFunction == null) {
            throw new IllegalStateException("A comment text function is required.");
        }
        
        if (nodeNameFunction == null) {
            throw new IllegalStateException("A node name function is required.");
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////
    // IHeuristic

    /**
     * {@inheritDoc}
     */
    @Override
    public void preprocess(final ElkNode graph, final boolean includeHierarchy) {
        checkConfiguration();
        
        // Build up maps of comment texts and node names
        List<Pair<ElkNode, String>> commentTexts = Lists.newArrayList();
        List<Pair<ElkNode, String>> nodeNames = Lists.newArrayList();
        
        for (ElkNode node : graph.getChildren()) {
            if (CommentAttacher.isComment(node)) {
                String commentText = commentTextFunction.apply(node);
                
                if (!Strings.isNullOrEmpty(commentText)) {
                    commentTexts.add(Pair.of(node, commentText));
                }
            } else {
                String nodeName = nodeNameFunction.apply(node);
                
                if (!Strings.isNullOrEmpty(nodeName)) {
                    nodeNames.add(Pair.of(node, nodeName));
                }
                
                if (includeHierarchy && !node.getChildren().isEmpty()) {
                    preprocess(node, true);
                }
            }
        }
        
        // Go find matches!
        goFindMatches(commentTexts, nodeNames);
        
        commentTexts = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanup() {
        foundAttachments.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double raw(final ElkNode comment, final ElkGraphElement element) {
        // Simply lookup the comment-node pair in our map
        return foundAttachments.get(comment) == element ? 1 : 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double normalized(final ElkNode comment, final ElkGraphElement element) {
        return raw(comment, element);
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////
    // Matching
    
    /**
     * Runs off and finds matches between comments and nodes. Depending on whether fuzzy mode is on
     * or off, this method uses different kinds of regular expressions to find node names in
     * comments. A node and a comment match if the node's text is contained in the comment's text,
     * and if no other node is mentioned in the comment's text.
     * 
     * <p>
     * Matches are recorded in {@link #foundAttachments}.
     * </p>
     * 
     * @param commentTexts
     *            list of pairs of comments and their text. The text is expected to not be
     *            {@code null}.
     * @param nodeNames
     *            list of pairs of nodes and their names. The name is expected to not be
     *            {@code null}.
     */
    private void goFindMatches(final List<Pair<ElkNode, String>> commentTexts,
            final List<Pair<ElkNode, String>> nodeNames) {
        
        // Produce regular expression patterns for all node names
        List<Pair<ElkNode, Pattern>> nodeRegexps = Lists.newArrayListWithCapacity(nodeNames.size());
        for (Pair<ElkNode, String> nodeNamePair : nodeNames) {
            Pattern regexp = fuzzy
                    ? fuzzyRegexpFor(nodeNamePair.getSecond())
                    : strictRegexpFor(nodeNamePair.getSecond());
            nodeRegexps.add(Pair.of(nodeNamePair.getFirst(), regexp));
        }
        
        // Check each pair of comment and node
        for (Pair<ElkNode, String> commentTextPair : commentTexts) {
            ElkNode foundNode = null;
            
            for (Pair<ElkNode, Pattern> nodeRegexpPair : nodeRegexps) {
                Matcher matcher = nodeRegexpPair.getSecond().matcher(commentTextPair.getSecond());
                
                if (matcher.find()) {
                    // We only want to establish associations if a node is the only one mentioned in a
                    // comment
                    if (foundNode == null) {
                        foundNode = nodeRegexpPair.getFirst();
                    } else {
                        foundNode = null;
                        break;
                    }
                }
            }
            
            // Record the match, if any and if the maximum attachment distance allows
            if (foundNode != null) {
                if (maxDistance < 0) {
                    // There is no distance restriction
                    foundAttachments.put(commentTextPair.getFirst(), foundNode);
                } else {
                    // Calculate the distance between the node and the comment
                    Rectangle2D.Double commentBounds = boundsProvider.boundsFor(
                            commentTextPair.getFirst());
                    Rectangle2D.Double nodeBounds = boundsProvider.boundsFor(foundNode);
                    
                    if (DistanceHeuristic.distance(commentBounds, nodeBounds) <= maxDistance) {
                        foundAttachments.put(commentTextPair.getFirst(), foundNode);
                    }
                }
            }
        }
    }
    
    /**
     * Produces a fuzzy regular expression pattern for the given node name. The pattern matches the
     * following appearances of the node name:
     * <ul>
     *   <li>
     *     a space character in the node name can be represented by one or more whitespace and line
     *     break characters in the comment text.
     *   </li>
     *   <li>
     *     if the node name is camelCased, each upper-case character preceded by a lower-case character
     *     can be prefixed by one or more whitespace and line break characters in the comment text.
     *   </li>
     * </ul>
     * 
     * @param nodeName
     *            the node name.
     * @return regular expression pattern for fuzzy containation.
     */
    private static Pattern fuzzyRegexpFor(final String nodeName) {
        String trimmedNodeName = nodeName.trim();
        StringBuffer regexp = new StringBuffer(nodeName.length() * 2);
        StringBuffer currentSegment = new StringBuffer(nodeName.length());
        
        for (int i = 0; i < trimmedNodeName.length(); i++) {
            char currC = trimmedNodeName.charAt(i);
            
            if (Character.isUpperCase(currC)) {
                // If the previous character was lower-case, end the current segment and insert
                // whitespace placeholders
                if (i > 0 && Character.isLowerCase(trimmedNodeName.charAt(i - 1))) {
                    regexp.append(Pattern.quote(currentSegment.toString()));
                    currentSegment = new StringBuffer(nodeName.length());
                    
                    regexp.append("[\\h\\v]*");
                }
                
                currentSegment.append(currC);
            } else if (Character.isWhitespace(currC)) {
                // The first of a series of whitespace characters must be replaced by whitespace
                // placeholders in the regular expression, and the current segment ends
                if (i > 0 && !Character.isWhitespace(trimmedNodeName.charAt(i - 1))) {
                    regexp.append(Pattern.quote(currentSegment.toString()));
                    currentSegment = new StringBuffer(nodeName.length());
                    
                    regexp.append("[\\h\\v]*");
                }
            } else {
                // It's neither upper-case, nor whitespace, so just add it to the current segment
                currentSegment.append(currC);
            }
        }
        
        regexp.append(Pattern.quote(currentSegment.toString()));
        
        return Pattern.compile("\\b" + regexp.toString() + "\\b",
                Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    }
    
    /**
     * Produces a strict regular expression pattern for the given node name. The pattern matches the
     * node name if it's not part of a longer word.
     * 
     * @param nodeName
     *            the node name.
     * @return regular expression pattern for strict containation.
     */
    private static Pattern strictRegexpFor(final String nodeName) {
        return Pattern.compile("\\b" + Pattern.quote(nodeName) + "\\b", Pattern.DOTALL);
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////
    // Accessors
    
    /**
     * Returns a map that maps comments to nodes they were attached to by this heuristic. The returned
     * map is meaningful only if it is called between calls to {@link #preprocess(ElkNode, boolean)} and
     * {@link #cleanup()}. Comments that are not attached to anything don't appear in the map.
     * 
     * @return mapping of comments to nodes.
     */
    public Map<ElkNode, ElkNode> getAttachments() {
        return foundAttachments;
    }

}
