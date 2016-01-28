/*******************************************************************************
 * Copyright (c) 2011, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.alg.graphviz.dot.formatting;

import org.eclipse.elk.alg.graphviz.dot.services.GraphvizDotGrammarAccess;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.formatting.impl.AbstractDeclarativeFormatter;
import org.eclipse.xtext.formatting.impl.FormattingConfig;
import org.eclipse.xtext.util.Pair;


/**
 * This class contains custom formatting description.
 * 
 * @author msp
 * @kieler.design proposed by msp
 * @kieler.rating proposed yellow by msp
 */
public class GraphvizDotFormatter extends AbstractDeclarativeFormatter {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureFormatting(final FormattingConfig c) {
        GraphvizDotGrammarAccess f = (GraphvizDotGrammarAccess) getGrammarAccess();
        // SUPPRESS CHECKSTYLE NEXT MagicNumber
        c.setAutoLinewrap(255);
        
        for (Pair<Keyword, Keyword> pair : f.findKeywordPairs("{", "}")) {
            c.setIndentation(pair.getFirst(), pair.getSecond());
            c.setLinewrap(1).after(pair.getFirst());
            c.setLinewrap(1).before(pair.getSecond());
            c.setLinewrap(1).after(pair.getSecond());
        }
        
        for (Keyword semicolon : f.findKeywords(";")) {
            c.setNoLinewrap().before(semicolon);
            c.setNoSpace().before(semicolon);
        }
        
        for (Pair<Keyword, Keyword> pair : f.findKeywordPairs("[", "]")) {
            c.setNoSpace().after(pair.getFirst());
            c.setNoSpace().before(pair.getSecond());
        }
        
        for (Keyword equal : f.findKeywords("=")) {
            c.setNoSpace().before(equal);
            c.setNoSpace().after(equal);
        }

        for (Keyword comma : f.findKeywords(",")) {
            c.setNoLinewrap().before(comma);
            c.setNoSpace().before(comma);
        }
        
        c.setLinewrap(1).before(f.getStatementRule());
            
        c.setLinewrap(0, 1, 2).before(f.getSL_COMMENTRule());
        c.setLinewrap(0, 1, 2).before(f.getML_COMMENTRule());
        c.setLinewrap(0, 1, 1).after(f.getML_COMMENTRule());
    }
    
}
