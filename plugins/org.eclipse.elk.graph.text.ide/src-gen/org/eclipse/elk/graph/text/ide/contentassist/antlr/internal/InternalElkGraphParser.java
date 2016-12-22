package org.eclipse.elk.graph.text.ide.contentassist.antlr.internal;

import java.io.InputStream;
import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.ide.editor.contentassist.antlr.internal.AbstractInternalContentAssistParser;
import org.eclipse.xtext.ide.editor.contentassist.antlr.internal.DFA;
import org.eclipse.elk.graph.text.services.ElkGraphGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

/*******************************************************************************
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
@SuppressWarnings("all")
public class InternalElkGraphParser extends AbstractInternalContentAssistParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_SIGNED_INT", "RULE_FLOAT", "RULE_ID", "RULE_STRING", "RULE_INT", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'true'", "'false'", "'graph'", "'node'", "'{'", "'}'", "'label'", "':'", "'port'", "'layout'", "'['", "']'", "'position'", "','", "'size'", "'edge'", "'->'", "'incoming'", "'outgoing'", "'start'", "'end'", "'bends'", "'|'", "'section'", "'.'"
    };
    public static final int RULE_STRING=7;
    public static final int RULE_SL_COMMENT=10;
    public static final int T__19=19;
    public static final int T__15=15;
    public static final int T__37=37;
    public static final int T__16=16;
    public static final int T__17=17;
    public static final int T__18=18;
    public static final int T__33=33;
    public static final int T__34=34;
    public static final int T__13=13;
    public static final int T__35=35;
    public static final int T__14=14;
    public static final int T__36=36;
    public static final int EOF=-1;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int RULE_ID=6;
    public static final int RULE_WS=11;
    public static final int RULE_SIGNED_INT=4;
    public static final int RULE_ANY_OTHER=12;
    public static final int T__26=26;
    public static final int T__27=27;
    public static final int T__28=28;
    public static final int RULE_INT=8;
    public static final int T__29=29;
    public static final int T__22=22;
    public static final int RULE_ML_COMMENT=9;
    public static final int T__23=23;
    public static final int RULE_FLOAT=5;
    public static final int T__24=24;
    public static final int T__25=25;
    public static final int T__20=20;
    public static final int T__21=21;

    // delegates
    // delegators


        public InternalElkGraphParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public InternalElkGraphParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return InternalElkGraphParser.tokenNames; }
    public String getGrammarFileName() { return "InternalElkGraph.g"; }


    	private ElkGraphGrammarAccess grammarAccess;

    	public void setGrammarAccess(ElkGraphGrammarAccess grammarAccess) {
    		this.grammarAccess = grammarAccess;
    	}

    	@Override
    	protected Grammar getGrammar() {
    		return grammarAccess.getGrammar();
    	}

    	@Override
    	protected String getValueForTokenName(String tokenName) {
    		return tokenName;
    	}



    // $ANTLR start "entryRuleRootNode"
    // InternalElkGraph.g:57:1: entryRuleRootNode : ruleRootNode EOF ;
    public final void entryRuleRootNode() throws RecognitionException {
        try {
            // InternalElkGraph.g:58:1: ( ruleRootNode EOF )
            // InternalElkGraph.g:59:1: ruleRootNode EOF
            {
             before(grammarAccess.getRootNodeRule()); 
            pushFollow(FOLLOW_1);
            ruleRootNode();

            state._fsp--;

             after(grammarAccess.getRootNodeRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleRootNode"


    // $ANTLR start "ruleRootNode"
    // InternalElkGraph.g:66:1: ruleRootNode : ( ( rule__RootNode__Group__0 ) ) ;
    public final void ruleRootNode() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:70:2: ( ( ( rule__RootNode__Group__0 ) ) )
            // InternalElkGraph.g:71:2: ( ( rule__RootNode__Group__0 ) )
            {
            // InternalElkGraph.g:71:2: ( ( rule__RootNode__Group__0 ) )
            // InternalElkGraph.g:72:3: ( rule__RootNode__Group__0 )
            {
             before(grammarAccess.getRootNodeAccess().getGroup()); 
            // InternalElkGraph.g:73:3: ( rule__RootNode__Group__0 )
            // InternalElkGraph.g:73:4: rule__RootNode__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__RootNode__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getRootNodeAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleRootNode"


    // $ANTLR start "entryRuleElkNode"
    // InternalElkGraph.g:82:1: entryRuleElkNode : ruleElkNode EOF ;
    public final void entryRuleElkNode() throws RecognitionException {
        try {
            // InternalElkGraph.g:83:1: ( ruleElkNode EOF )
            // InternalElkGraph.g:84:1: ruleElkNode EOF
            {
             before(grammarAccess.getElkNodeRule()); 
            pushFollow(FOLLOW_1);
            ruleElkNode();

            state._fsp--;

             after(grammarAccess.getElkNodeRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleElkNode"


    // $ANTLR start "ruleElkNode"
    // InternalElkGraph.g:91:1: ruleElkNode : ( ( rule__ElkNode__Group__0 ) ) ;
    public final void ruleElkNode() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:95:2: ( ( ( rule__ElkNode__Group__0 ) ) )
            // InternalElkGraph.g:96:2: ( ( rule__ElkNode__Group__0 ) )
            {
            // InternalElkGraph.g:96:2: ( ( rule__ElkNode__Group__0 ) )
            // InternalElkGraph.g:97:3: ( rule__ElkNode__Group__0 )
            {
             before(grammarAccess.getElkNodeAccess().getGroup()); 
            // InternalElkGraph.g:98:3: ( rule__ElkNode__Group__0 )
            // InternalElkGraph.g:98:4: rule__ElkNode__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__ElkNode__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getElkNodeAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleElkNode"


    // $ANTLR start "entryRuleElkLabel"
    // InternalElkGraph.g:107:1: entryRuleElkLabel : ruleElkLabel EOF ;
    public final void entryRuleElkLabel() throws RecognitionException {
        try {
            // InternalElkGraph.g:108:1: ( ruleElkLabel EOF )
            // InternalElkGraph.g:109:1: ruleElkLabel EOF
            {
             before(grammarAccess.getElkLabelRule()); 
            pushFollow(FOLLOW_1);
            ruleElkLabel();

            state._fsp--;

             after(grammarAccess.getElkLabelRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleElkLabel"


    // $ANTLR start "ruleElkLabel"
    // InternalElkGraph.g:116:1: ruleElkLabel : ( ( rule__ElkLabel__Group__0 ) ) ;
    public final void ruleElkLabel() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:120:2: ( ( ( rule__ElkLabel__Group__0 ) ) )
            // InternalElkGraph.g:121:2: ( ( rule__ElkLabel__Group__0 ) )
            {
            // InternalElkGraph.g:121:2: ( ( rule__ElkLabel__Group__0 ) )
            // InternalElkGraph.g:122:3: ( rule__ElkLabel__Group__0 )
            {
             before(grammarAccess.getElkLabelAccess().getGroup()); 
            // InternalElkGraph.g:123:3: ( rule__ElkLabel__Group__0 )
            // InternalElkGraph.g:123:4: rule__ElkLabel__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__ElkLabel__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getElkLabelAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleElkLabel"


    // $ANTLR start "entryRuleElkPort"
    // InternalElkGraph.g:132:1: entryRuleElkPort : ruleElkPort EOF ;
    public final void entryRuleElkPort() throws RecognitionException {
        try {
            // InternalElkGraph.g:133:1: ( ruleElkPort EOF )
            // InternalElkGraph.g:134:1: ruleElkPort EOF
            {
             before(grammarAccess.getElkPortRule()); 
            pushFollow(FOLLOW_1);
            ruleElkPort();

            state._fsp--;

             after(grammarAccess.getElkPortRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleElkPort"


    // $ANTLR start "ruleElkPort"
    // InternalElkGraph.g:141:1: ruleElkPort : ( ( rule__ElkPort__Group__0 ) ) ;
    public final void ruleElkPort() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:145:2: ( ( ( rule__ElkPort__Group__0 ) ) )
            // InternalElkGraph.g:146:2: ( ( rule__ElkPort__Group__0 ) )
            {
            // InternalElkGraph.g:146:2: ( ( rule__ElkPort__Group__0 ) )
            // InternalElkGraph.g:147:3: ( rule__ElkPort__Group__0 )
            {
             before(grammarAccess.getElkPortAccess().getGroup()); 
            // InternalElkGraph.g:148:3: ( rule__ElkPort__Group__0 )
            // InternalElkGraph.g:148:4: rule__ElkPort__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__ElkPort__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getElkPortAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleElkPort"


    // $ANTLR start "ruleShapeLayout"
    // InternalElkGraph.g:158:1: ruleShapeLayout : ( ( rule__ShapeLayout__Group__0 ) ) ;
    public final void ruleShapeLayout() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:162:2: ( ( ( rule__ShapeLayout__Group__0 ) ) )
            // InternalElkGraph.g:163:2: ( ( rule__ShapeLayout__Group__0 ) )
            {
            // InternalElkGraph.g:163:2: ( ( rule__ShapeLayout__Group__0 ) )
            // InternalElkGraph.g:164:3: ( rule__ShapeLayout__Group__0 )
            {
             before(grammarAccess.getShapeLayoutAccess().getGroup()); 
            // InternalElkGraph.g:165:3: ( rule__ShapeLayout__Group__0 )
            // InternalElkGraph.g:165:4: rule__ShapeLayout__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__ShapeLayout__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getShapeLayoutAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleShapeLayout"


    // $ANTLR start "entryRuleElkEdge"
    // InternalElkGraph.g:174:1: entryRuleElkEdge : ruleElkEdge EOF ;
    public final void entryRuleElkEdge() throws RecognitionException {
        try {
            // InternalElkGraph.g:175:1: ( ruleElkEdge EOF )
            // InternalElkGraph.g:176:1: ruleElkEdge EOF
            {
             before(grammarAccess.getElkEdgeRule()); 
            pushFollow(FOLLOW_1);
            ruleElkEdge();

            state._fsp--;

             after(grammarAccess.getElkEdgeRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleElkEdge"


    // $ANTLR start "ruleElkEdge"
    // InternalElkGraph.g:183:1: ruleElkEdge : ( ( rule__ElkEdge__Group__0 ) ) ;
    public final void ruleElkEdge() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:187:2: ( ( ( rule__ElkEdge__Group__0 ) ) )
            // InternalElkGraph.g:188:2: ( ( rule__ElkEdge__Group__0 ) )
            {
            // InternalElkGraph.g:188:2: ( ( rule__ElkEdge__Group__0 ) )
            // InternalElkGraph.g:189:3: ( rule__ElkEdge__Group__0 )
            {
             before(grammarAccess.getElkEdgeAccess().getGroup()); 
            // InternalElkGraph.g:190:3: ( rule__ElkEdge__Group__0 )
            // InternalElkGraph.g:190:4: rule__ElkEdge__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdge__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getElkEdgeAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleElkEdge"


    // $ANTLR start "ruleEdgeLayout"
    // InternalElkGraph.g:200:1: ruleEdgeLayout : ( ( rule__EdgeLayout__Group__0 ) ) ;
    public final void ruleEdgeLayout() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:204:2: ( ( ( rule__EdgeLayout__Group__0 ) ) )
            // InternalElkGraph.g:205:2: ( ( rule__EdgeLayout__Group__0 ) )
            {
            // InternalElkGraph.g:205:2: ( ( rule__EdgeLayout__Group__0 ) )
            // InternalElkGraph.g:206:3: ( rule__EdgeLayout__Group__0 )
            {
             before(grammarAccess.getEdgeLayoutAccess().getGroup()); 
            // InternalElkGraph.g:207:3: ( rule__EdgeLayout__Group__0 )
            // InternalElkGraph.g:207:4: rule__EdgeLayout__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__EdgeLayout__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getEdgeLayoutAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleEdgeLayout"


    // $ANTLR start "entryRuleElkSingleEdgeSection"
    // InternalElkGraph.g:216:1: entryRuleElkSingleEdgeSection : ruleElkSingleEdgeSection EOF ;
    public final void entryRuleElkSingleEdgeSection() throws RecognitionException {
        try {
            // InternalElkGraph.g:217:1: ( ruleElkSingleEdgeSection EOF )
            // InternalElkGraph.g:218:1: ruleElkSingleEdgeSection EOF
            {
             before(grammarAccess.getElkSingleEdgeSectionRule()); 
            pushFollow(FOLLOW_1);
            ruleElkSingleEdgeSection();

            state._fsp--;

             after(grammarAccess.getElkSingleEdgeSectionRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleElkSingleEdgeSection"


    // $ANTLR start "ruleElkSingleEdgeSection"
    // InternalElkGraph.g:225:1: ruleElkSingleEdgeSection : ( ( rule__ElkSingleEdgeSection__Group__0 ) ) ;
    public final void ruleElkSingleEdgeSection() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:229:2: ( ( ( rule__ElkSingleEdgeSection__Group__0 ) ) )
            // InternalElkGraph.g:230:2: ( ( rule__ElkSingleEdgeSection__Group__0 ) )
            {
            // InternalElkGraph.g:230:2: ( ( rule__ElkSingleEdgeSection__Group__0 ) )
            // InternalElkGraph.g:231:3: ( rule__ElkSingleEdgeSection__Group__0 )
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getGroup()); 
            // InternalElkGraph.g:232:3: ( rule__ElkSingleEdgeSection__Group__0 )
            // InternalElkGraph.g:232:4: rule__ElkSingleEdgeSection__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getElkSingleEdgeSectionAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleElkSingleEdgeSection"


    // $ANTLR start "entryRuleElkEdgeSection"
    // InternalElkGraph.g:241:1: entryRuleElkEdgeSection : ruleElkEdgeSection EOF ;
    public final void entryRuleElkEdgeSection() throws RecognitionException {
        try {
            // InternalElkGraph.g:242:1: ( ruleElkEdgeSection EOF )
            // InternalElkGraph.g:243:1: ruleElkEdgeSection EOF
            {
             before(grammarAccess.getElkEdgeSectionRule()); 
            pushFollow(FOLLOW_1);
            ruleElkEdgeSection();

            state._fsp--;

             after(grammarAccess.getElkEdgeSectionRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleElkEdgeSection"


    // $ANTLR start "ruleElkEdgeSection"
    // InternalElkGraph.g:250:1: ruleElkEdgeSection : ( ( rule__ElkEdgeSection__Group__0 ) ) ;
    public final void ruleElkEdgeSection() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:254:2: ( ( ( rule__ElkEdgeSection__Group__0 ) ) )
            // InternalElkGraph.g:255:2: ( ( rule__ElkEdgeSection__Group__0 ) )
            {
            // InternalElkGraph.g:255:2: ( ( rule__ElkEdgeSection__Group__0 ) )
            // InternalElkGraph.g:256:3: ( rule__ElkEdgeSection__Group__0 )
            {
             before(grammarAccess.getElkEdgeSectionAccess().getGroup()); 
            // InternalElkGraph.g:257:3: ( rule__ElkEdgeSection__Group__0 )
            // InternalElkGraph.g:257:4: rule__ElkEdgeSection__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getElkEdgeSectionAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleElkEdgeSection"


    // $ANTLR start "entryRuleElkBendPoint"
    // InternalElkGraph.g:266:1: entryRuleElkBendPoint : ruleElkBendPoint EOF ;
    public final void entryRuleElkBendPoint() throws RecognitionException {
        try {
            // InternalElkGraph.g:267:1: ( ruleElkBendPoint EOF )
            // InternalElkGraph.g:268:1: ruleElkBendPoint EOF
            {
             before(grammarAccess.getElkBendPointRule()); 
            pushFollow(FOLLOW_1);
            ruleElkBendPoint();

            state._fsp--;

             after(grammarAccess.getElkBendPointRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleElkBendPoint"


    // $ANTLR start "ruleElkBendPoint"
    // InternalElkGraph.g:275:1: ruleElkBendPoint : ( ( rule__ElkBendPoint__Group__0 ) ) ;
    public final void ruleElkBendPoint() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:279:2: ( ( ( rule__ElkBendPoint__Group__0 ) ) )
            // InternalElkGraph.g:280:2: ( ( rule__ElkBendPoint__Group__0 ) )
            {
            // InternalElkGraph.g:280:2: ( ( rule__ElkBendPoint__Group__0 ) )
            // InternalElkGraph.g:281:3: ( rule__ElkBendPoint__Group__0 )
            {
             before(grammarAccess.getElkBendPointAccess().getGroup()); 
            // InternalElkGraph.g:282:3: ( rule__ElkBendPoint__Group__0 )
            // InternalElkGraph.g:282:4: rule__ElkBendPoint__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__ElkBendPoint__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getElkBendPointAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleElkBendPoint"


    // $ANTLR start "entryRuleProperty"
    // InternalElkGraph.g:291:1: entryRuleProperty : ruleProperty EOF ;
    public final void entryRuleProperty() throws RecognitionException {
        try {
            // InternalElkGraph.g:292:1: ( ruleProperty EOF )
            // InternalElkGraph.g:293:1: ruleProperty EOF
            {
             before(grammarAccess.getPropertyRule()); 
            pushFollow(FOLLOW_1);
            ruleProperty();

            state._fsp--;

             after(grammarAccess.getPropertyRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleProperty"


    // $ANTLR start "ruleProperty"
    // InternalElkGraph.g:300:1: ruleProperty : ( ( rule__Property__Group__0 ) ) ;
    public final void ruleProperty() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:304:2: ( ( ( rule__Property__Group__0 ) ) )
            // InternalElkGraph.g:305:2: ( ( rule__Property__Group__0 ) )
            {
            // InternalElkGraph.g:305:2: ( ( rule__Property__Group__0 ) )
            // InternalElkGraph.g:306:3: ( rule__Property__Group__0 )
            {
             before(grammarAccess.getPropertyAccess().getGroup()); 
            // InternalElkGraph.g:307:3: ( rule__Property__Group__0 )
            // InternalElkGraph.g:307:4: rule__Property__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__Property__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getPropertyAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleProperty"


    // $ANTLR start "entryRuleQualifiedId"
    // InternalElkGraph.g:316:1: entryRuleQualifiedId : ruleQualifiedId EOF ;
    public final void entryRuleQualifiedId() throws RecognitionException {
        try {
            // InternalElkGraph.g:317:1: ( ruleQualifiedId EOF )
            // InternalElkGraph.g:318:1: ruleQualifiedId EOF
            {
             before(grammarAccess.getQualifiedIdRule()); 
            pushFollow(FOLLOW_1);
            ruleQualifiedId();

            state._fsp--;

             after(grammarAccess.getQualifiedIdRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleQualifiedId"


    // $ANTLR start "ruleQualifiedId"
    // InternalElkGraph.g:325:1: ruleQualifiedId : ( ( rule__QualifiedId__Group__0 ) ) ;
    public final void ruleQualifiedId() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:329:2: ( ( ( rule__QualifiedId__Group__0 ) ) )
            // InternalElkGraph.g:330:2: ( ( rule__QualifiedId__Group__0 ) )
            {
            // InternalElkGraph.g:330:2: ( ( rule__QualifiedId__Group__0 ) )
            // InternalElkGraph.g:331:3: ( rule__QualifiedId__Group__0 )
            {
             before(grammarAccess.getQualifiedIdAccess().getGroup()); 
            // InternalElkGraph.g:332:3: ( rule__QualifiedId__Group__0 )
            // InternalElkGraph.g:332:4: rule__QualifiedId__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__QualifiedId__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getQualifiedIdAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleQualifiedId"


    // $ANTLR start "entryRuleBoolean"
    // InternalElkGraph.g:341:1: entryRuleBoolean : ruleBoolean EOF ;
    public final void entryRuleBoolean() throws RecognitionException {
        try {
            // InternalElkGraph.g:342:1: ( ruleBoolean EOF )
            // InternalElkGraph.g:343:1: ruleBoolean EOF
            {
             before(grammarAccess.getBooleanRule()); 
            pushFollow(FOLLOW_1);
            ruleBoolean();

            state._fsp--;

             after(grammarAccess.getBooleanRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleBoolean"


    // $ANTLR start "ruleBoolean"
    // InternalElkGraph.g:350:1: ruleBoolean : ( ( rule__Boolean__Alternatives ) ) ;
    public final void ruleBoolean() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:354:2: ( ( ( rule__Boolean__Alternatives ) ) )
            // InternalElkGraph.g:355:2: ( ( rule__Boolean__Alternatives ) )
            {
            // InternalElkGraph.g:355:2: ( ( rule__Boolean__Alternatives ) )
            // InternalElkGraph.g:356:3: ( rule__Boolean__Alternatives )
            {
             before(grammarAccess.getBooleanAccess().getAlternatives()); 
            // InternalElkGraph.g:357:3: ( rule__Boolean__Alternatives )
            // InternalElkGraph.g:357:4: rule__Boolean__Alternatives
            {
            pushFollow(FOLLOW_2);
            rule__Boolean__Alternatives();

            state._fsp--;


            }

             after(grammarAccess.getBooleanAccess().getAlternatives()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleBoolean"


    // $ANTLR start "entryRuleNumber"
    // InternalElkGraph.g:366:1: entryRuleNumber : ruleNumber EOF ;
    public final void entryRuleNumber() throws RecognitionException {
        try {
            // InternalElkGraph.g:367:1: ( ruleNumber EOF )
            // InternalElkGraph.g:368:1: ruleNumber EOF
            {
             before(grammarAccess.getNumberRule()); 
            pushFollow(FOLLOW_1);
            ruleNumber();

            state._fsp--;

             after(grammarAccess.getNumberRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "entryRuleNumber"


    // $ANTLR start "ruleNumber"
    // InternalElkGraph.g:375:1: ruleNumber : ( ( rule__Number__Alternatives ) ) ;
    public final void ruleNumber() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:379:2: ( ( ( rule__Number__Alternatives ) ) )
            // InternalElkGraph.g:380:2: ( ( rule__Number__Alternatives ) )
            {
            // InternalElkGraph.g:380:2: ( ( rule__Number__Alternatives ) )
            // InternalElkGraph.g:381:3: ( rule__Number__Alternatives )
            {
             before(grammarAccess.getNumberAccess().getAlternatives()); 
            // InternalElkGraph.g:382:3: ( rule__Number__Alternatives )
            // InternalElkGraph.g:382:4: rule__Number__Alternatives
            {
            pushFollow(FOLLOW_2);
            rule__Number__Alternatives();

            state._fsp--;


            }

             after(grammarAccess.getNumberAccess().getAlternatives()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "ruleNumber"


    // $ANTLR start "entryRulePropertyKey"
    // InternalElkGraph.g:391:1: entryRulePropertyKey : rulePropertyKey EOF ;
    public final void entryRulePropertyKey() throws RecognitionException {
         
        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalElkGraph.g:395:1: ( rulePropertyKey EOF )
            // InternalElkGraph.g:396:1: rulePropertyKey EOF
            {
             before(grammarAccess.getPropertyKeyRule()); 
            pushFollow(FOLLOW_1);
            rulePropertyKey();

            state._fsp--;

             after(grammarAccess.getPropertyKeyRule()); 
            match(input,EOF,FOLLOW_2); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	myHiddenTokenState.restore();

        }
        return ;
    }
    // $ANTLR end "entryRulePropertyKey"


    // $ANTLR start "rulePropertyKey"
    // InternalElkGraph.g:406:1: rulePropertyKey : ( ( rule__PropertyKey__Group__0 ) ) ;
    public final void rulePropertyKey() throws RecognitionException {

        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:411:2: ( ( ( rule__PropertyKey__Group__0 ) ) )
            // InternalElkGraph.g:412:2: ( ( rule__PropertyKey__Group__0 ) )
            {
            // InternalElkGraph.g:412:2: ( ( rule__PropertyKey__Group__0 ) )
            // InternalElkGraph.g:413:3: ( rule__PropertyKey__Group__0 )
            {
             before(grammarAccess.getPropertyKeyAccess().getGroup()); 
            // InternalElkGraph.g:414:3: ( rule__PropertyKey__Group__0 )
            // InternalElkGraph.g:414:4: rule__PropertyKey__Group__0
            {
            pushFollow(FOLLOW_2);
            rule__PropertyKey__Group__0();

            state._fsp--;


            }

             after(grammarAccess.getPropertyKeyAccess().getGroup()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);
            	myHiddenTokenState.restore();

        }
        return ;
    }
    // $ANTLR end "rulePropertyKey"


    // $ANTLR start "rule__RootNode__Alternatives_3"
    // InternalElkGraph.g:423:1: rule__RootNode__Alternatives_3 : ( ( ( rule__RootNode__ChildrenAssignment_3_0 ) ) | ( ( rule__RootNode__ContainedEdgesAssignment_3_1 ) ) | ( ( rule__RootNode__PortsAssignment_3_2 ) ) | ( ( rule__RootNode__LabelsAssignment_3_3 ) ) );
    public final void rule__RootNode__Alternatives_3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:427:1: ( ( ( rule__RootNode__ChildrenAssignment_3_0 ) ) | ( ( rule__RootNode__ContainedEdgesAssignment_3_1 ) ) | ( ( rule__RootNode__PortsAssignment_3_2 ) ) | ( ( rule__RootNode__LabelsAssignment_3_3 ) ) )
            int alt1=4;
            switch ( input.LA(1) ) {
            case 16:
                {
                alt1=1;
                }
                break;
            case 28:
                {
                alt1=2;
                }
                break;
            case 21:
                {
                alt1=3;
                }
                break;
            case 19:
                {
                alt1=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // InternalElkGraph.g:428:2: ( ( rule__RootNode__ChildrenAssignment_3_0 ) )
                    {
                    // InternalElkGraph.g:428:2: ( ( rule__RootNode__ChildrenAssignment_3_0 ) )
                    // InternalElkGraph.g:429:3: ( rule__RootNode__ChildrenAssignment_3_0 )
                    {
                     before(grammarAccess.getRootNodeAccess().getChildrenAssignment_3_0()); 
                    // InternalElkGraph.g:430:3: ( rule__RootNode__ChildrenAssignment_3_0 )
                    // InternalElkGraph.g:430:4: rule__RootNode__ChildrenAssignment_3_0
                    {
                    pushFollow(FOLLOW_2);
                    rule__RootNode__ChildrenAssignment_3_0();

                    state._fsp--;


                    }

                     after(grammarAccess.getRootNodeAccess().getChildrenAssignment_3_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalElkGraph.g:434:2: ( ( rule__RootNode__ContainedEdgesAssignment_3_1 ) )
                    {
                    // InternalElkGraph.g:434:2: ( ( rule__RootNode__ContainedEdgesAssignment_3_1 ) )
                    // InternalElkGraph.g:435:3: ( rule__RootNode__ContainedEdgesAssignment_3_1 )
                    {
                     before(grammarAccess.getRootNodeAccess().getContainedEdgesAssignment_3_1()); 
                    // InternalElkGraph.g:436:3: ( rule__RootNode__ContainedEdgesAssignment_3_1 )
                    // InternalElkGraph.g:436:4: rule__RootNode__ContainedEdgesAssignment_3_1
                    {
                    pushFollow(FOLLOW_2);
                    rule__RootNode__ContainedEdgesAssignment_3_1();

                    state._fsp--;


                    }

                     after(grammarAccess.getRootNodeAccess().getContainedEdgesAssignment_3_1()); 

                    }


                    }
                    break;
                case 3 :
                    // InternalElkGraph.g:440:2: ( ( rule__RootNode__PortsAssignment_3_2 ) )
                    {
                    // InternalElkGraph.g:440:2: ( ( rule__RootNode__PortsAssignment_3_2 ) )
                    // InternalElkGraph.g:441:3: ( rule__RootNode__PortsAssignment_3_2 )
                    {
                     before(grammarAccess.getRootNodeAccess().getPortsAssignment_3_2()); 
                    // InternalElkGraph.g:442:3: ( rule__RootNode__PortsAssignment_3_2 )
                    // InternalElkGraph.g:442:4: rule__RootNode__PortsAssignment_3_2
                    {
                    pushFollow(FOLLOW_2);
                    rule__RootNode__PortsAssignment_3_2();

                    state._fsp--;


                    }

                     after(grammarAccess.getRootNodeAccess().getPortsAssignment_3_2()); 

                    }


                    }
                    break;
                case 4 :
                    // InternalElkGraph.g:446:2: ( ( rule__RootNode__LabelsAssignment_3_3 ) )
                    {
                    // InternalElkGraph.g:446:2: ( ( rule__RootNode__LabelsAssignment_3_3 ) )
                    // InternalElkGraph.g:447:3: ( rule__RootNode__LabelsAssignment_3_3 )
                    {
                     before(grammarAccess.getRootNodeAccess().getLabelsAssignment_3_3()); 
                    // InternalElkGraph.g:448:3: ( rule__RootNode__LabelsAssignment_3_3 )
                    // InternalElkGraph.g:448:4: rule__RootNode__LabelsAssignment_3_3
                    {
                    pushFollow(FOLLOW_2);
                    rule__RootNode__LabelsAssignment_3_3();

                    state._fsp--;


                    }

                     after(grammarAccess.getRootNodeAccess().getLabelsAssignment_3_3()); 

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RootNode__Alternatives_3"


    // $ANTLR start "rule__ElkNode__Alternatives_2_3"
    // InternalElkGraph.g:456:1: rule__ElkNode__Alternatives_2_3 : ( ( ( rule__ElkNode__ChildrenAssignment_2_3_0 ) ) | ( ( rule__ElkNode__ContainedEdgesAssignment_2_3_1 ) ) | ( ( rule__ElkNode__PortsAssignment_2_3_2 ) ) | ( ( rule__ElkNode__LabelsAssignment_2_3_3 ) ) );
    public final void rule__ElkNode__Alternatives_2_3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:460:1: ( ( ( rule__ElkNode__ChildrenAssignment_2_3_0 ) ) | ( ( rule__ElkNode__ContainedEdgesAssignment_2_3_1 ) ) | ( ( rule__ElkNode__PortsAssignment_2_3_2 ) ) | ( ( rule__ElkNode__LabelsAssignment_2_3_3 ) ) )
            int alt2=4;
            switch ( input.LA(1) ) {
            case 16:
                {
                alt2=1;
                }
                break;
            case 28:
                {
                alt2=2;
                }
                break;
            case 21:
                {
                alt2=3;
                }
                break;
            case 19:
                {
                alt2=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }

            switch (alt2) {
                case 1 :
                    // InternalElkGraph.g:461:2: ( ( rule__ElkNode__ChildrenAssignment_2_3_0 ) )
                    {
                    // InternalElkGraph.g:461:2: ( ( rule__ElkNode__ChildrenAssignment_2_3_0 ) )
                    // InternalElkGraph.g:462:3: ( rule__ElkNode__ChildrenAssignment_2_3_0 )
                    {
                     before(grammarAccess.getElkNodeAccess().getChildrenAssignment_2_3_0()); 
                    // InternalElkGraph.g:463:3: ( rule__ElkNode__ChildrenAssignment_2_3_0 )
                    // InternalElkGraph.g:463:4: rule__ElkNode__ChildrenAssignment_2_3_0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkNode__ChildrenAssignment_2_3_0();

                    state._fsp--;


                    }

                     after(grammarAccess.getElkNodeAccess().getChildrenAssignment_2_3_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalElkGraph.g:467:2: ( ( rule__ElkNode__ContainedEdgesAssignment_2_3_1 ) )
                    {
                    // InternalElkGraph.g:467:2: ( ( rule__ElkNode__ContainedEdgesAssignment_2_3_1 ) )
                    // InternalElkGraph.g:468:3: ( rule__ElkNode__ContainedEdgesAssignment_2_3_1 )
                    {
                     before(grammarAccess.getElkNodeAccess().getContainedEdgesAssignment_2_3_1()); 
                    // InternalElkGraph.g:469:3: ( rule__ElkNode__ContainedEdgesAssignment_2_3_1 )
                    // InternalElkGraph.g:469:4: rule__ElkNode__ContainedEdgesAssignment_2_3_1
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkNode__ContainedEdgesAssignment_2_3_1();

                    state._fsp--;


                    }

                     after(grammarAccess.getElkNodeAccess().getContainedEdgesAssignment_2_3_1()); 

                    }


                    }
                    break;
                case 3 :
                    // InternalElkGraph.g:473:2: ( ( rule__ElkNode__PortsAssignment_2_3_2 ) )
                    {
                    // InternalElkGraph.g:473:2: ( ( rule__ElkNode__PortsAssignment_2_3_2 ) )
                    // InternalElkGraph.g:474:3: ( rule__ElkNode__PortsAssignment_2_3_2 )
                    {
                     before(grammarAccess.getElkNodeAccess().getPortsAssignment_2_3_2()); 
                    // InternalElkGraph.g:475:3: ( rule__ElkNode__PortsAssignment_2_3_2 )
                    // InternalElkGraph.g:475:4: rule__ElkNode__PortsAssignment_2_3_2
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkNode__PortsAssignment_2_3_2();

                    state._fsp--;


                    }

                     after(grammarAccess.getElkNodeAccess().getPortsAssignment_2_3_2()); 

                    }


                    }
                    break;
                case 4 :
                    // InternalElkGraph.g:479:2: ( ( rule__ElkNode__LabelsAssignment_2_3_3 ) )
                    {
                    // InternalElkGraph.g:479:2: ( ( rule__ElkNode__LabelsAssignment_2_3_3 ) )
                    // InternalElkGraph.g:480:3: ( rule__ElkNode__LabelsAssignment_2_3_3 )
                    {
                     before(grammarAccess.getElkNodeAccess().getLabelsAssignment_2_3_3()); 
                    // InternalElkGraph.g:481:3: ( rule__ElkNode__LabelsAssignment_2_3_3 )
                    // InternalElkGraph.g:481:4: rule__ElkNode__LabelsAssignment_2_3_3
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkNode__LabelsAssignment_2_3_3();

                    state._fsp--;


                    }

                     after(grammarAccess.getElkNodeAccess().getLabelsAssignment_2_3_3()); 

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkNode__Alternatives_2_3"


    // $ANTLR start "rule__EdgeLayout__Alternatives_2"
    // InternalElkGraph.g:489:1: rule__EdgeLayout__Alternatives_2 : ( ( ( rule__EdgeLayout__SectionsAssignment_2_0 ) ) | ( ( ( rule__EdgeLayout__SectionsAssignment_2_1 ) ) ( ( rule__EdgeLayout__SectionsAssignment_2_1 )* ) ) );
    public final void rule__EdgeLayout__Alternatives_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:493:1: ( ( ( rule__EdgeLayout__SectionsAssignment_2_0 ) ) | ( ( ( rule__EdgeLayout__SectionsAssignment_2_1 ) ) ( ( rule__EdgeLayout__SectionsAssignment_2_1 )* ) ) )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==EOF||LA4_0==24||(LA4_0>=30 && LA4_0<=34)) ) {
                alt4=1;
            }
            else if ( (LA4_0==36) ) {
                alt4=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // InternalElkGraph.g:494:2: ( ( rule__EdgeLayout__SectionsAssignment_2_0 ) )
                    {
                    // InternalElkGraph.g:494:2: ( ( rule__EdgeLayout__SectionsAssignment_2_0 ) )
                    // InternalElkGraph.g:495:3: ( rule__EdgeLayout__SectionsAssignment_2_0 )
                    {
                     before(grammarAccess.getEdgeLayoutAccess().getSectionsAssignment_2_0()); 
                    // InternalElkGraph.g:496:3: ( rule__EdgeLayout__SectionsAssignment_2_0 )
                    // InternalElkGraph.g:496:4: rule__EdgeLayout__SectionsAssignment_2_0
                    {
                    pushFollow(FOLLOW_2);
                    rule__EdgeLayout__SectionsAssignment_2_0();

                    state._fsp--;


                    }

                     after(grammarAccess.getEdgeLayoutAccess().getSectionsAssignment_2_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalElkGraph.g:500:2: ( ( ( rule__EdgeLayout__SectionsAssignment_2_1 ) ) ( ( rule__EdgeLayout__SectionsAssignment_2_1 )* ) )
                    {
                    // InternalElkGraph.g:500:2: ( ( ( rule__EdgeLayout__SectionsAssignment_2_1 ) ) ( ( rule__EdgeLayout__SectionsAssignment_2_1 )* ) )
                    // InternalElkGraph.g:501:3: ( ( rule__EdgeLayout__SectionsAssignment_2_1 ) ) ( ( rule__EdgeLayout__SectionsAssignment_2_1 )* )
                    {
                    // InternalElkGraph.g:501:3: ( ( rule__EdgeLayout__SectionsAssignment_2_1 ) )
                    // InternalElkGraph.g:502:4: ( rule__EdgeLayout__SectionsAssignment_2_1 )
                    {
                     before(grammarAccess.getEdgeLayoutAccess().getSectionsAssignment_2_1()); 
                    // InternalElkGraph.g:503:4: ( rule__EdgeLayout__SectionsAssignment_2_1 )
                    // InternalElkGraph.g:503:5: rule__EdgeLayout__SectionsAssignment_2_1
                    {
                    pushFollow(FOLLOW_3);
                    rule__EdgeLayout__SectionsAssignment_2_1();

                    state._fsp--;


                    }

                     after(grammarAccess.getEdgeLayoutAccess().getSectionsAssignment_2_1()); 

                    }

                    // InternalElkGraph.g:506:3: ( ( rule__EdgeLayout__SectionsAssignment_2_1 )* )
                    // InternalElkGraph.g:507:4: ( rule__EdgeLayout__SectionsAssignment_2_1 )*
                    {
                     before(grammarAccess.getEdgeLayoutAccess().getSectionsAssignment_2_1()); 
                    // InternalElkGraph.g:508:4: ( rule__EdgeLayout__SectionsAssignment_2_1 )*
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( (LA3_0==36) ) {
                            alt3=1;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // InternalElkGraph.g:508:5: rule__EdgeLayout__SectionsAssignment_2_1
                    	    {
                    	    pushFollow(FOLLOW_3);
                    	    rule__EdgeLayout__SectionsAssignment_2_1();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop3;
                        }
                    } while (true);

                     after(grammarAccess.getEdgeLayoutAccess().getSectionsAssignment_2_1()); 

                    }


                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeLayout__Alternatives_2"


    // $ANTLR start "rule__Property__Alternatives_2"
    // InternalElkGraph.g:517:1: rule__Property__Alternatives_2 : ( ( ( rule__Property__ValueAssignment_2_0 ) ) | ( ( rule__Property__ValueAssignment_2_1 ) ) | ( ( rule__Property__ValueAssignment_2_2 ) ) | ( ( rule__Property__ValueAssignment_2_3 ) ) | ( ( rule__Property__ValueAssignment_2_4 ) ) );
    public final void rule__Property__Alternatives_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:521:1: ( ( ( rule__Property__ValueAssignment_2_0 ) ) | ( ( rule__Property__ValueAssignment_2_1 ) ) | ( ( rule__Property__ValueAssignment_2_2 ) ) | ( ( rule__Property__ValueAssignment_2_3 ) ) | ( ( rule__Property__ValueAssignment_2_4 ) ) )
            int alt5=5;
            switch ( input.LA(1) ) {
            case RULE_STRING:
                {
                alt5=1;
                }
                break;
            case RULE_ID:
                {
                alt5=2;
                }
                break;
            case 13:
            case 14:
                {
                alt5=3;
                }
                break;
            case RULE_SIGNED_INT:
                {
                alt5=4;
                }
                break;
            case RULE_FLOAT:
                {
                alt5=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }

            switch (alt5) {
                case 1 :
                    // InternalElkGraph.g:522:2: ( ( rule__Property__ValueAssignment_2_0 ) )
                    {
                    // InternalElkGraph.g:522:2: ( ( rule__Property__ValueAssignment_2_0 ) )
                    // InternalElkGraph.g:523:3: ( rule__Property__ValueAssignment_2_0 )
                    {
                     before(grammarAccess.getPropertyAccess().getValueAssignment_2_0()); 
                    // InternalElkGraph.g:524:3: ( rule__Property__ValueAssignment_2_0 )
                    // InternalElkGraph.g:524:4: rule__Property__ValueAssignment_2_0
                    {
                    pushFollow(FOLLOW_2);
                    rule__Property__ValueAssignment_2_0();

                    state._fsp--;


                    }

                     after(grammarAccess.getPropertyAccess().getValueAssignment_2_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalElkGraph.g:528:2: ( ( rule__Property__ValueAssignment_2_1 ) )
                    {
                    // InternalElkGraph.g:528:2: ( ( rule__Property__ValueAssignment_2_1 ) )
                    // InternalElkGraph.g:529:3: ( rule__Property__ValueAssignment_2_1 )
                    {
                     before(grammarAccess.getPropertyAccess().getValueAssignment_2_1()); 
                    // InternalElkGraph.g:530:3: ( rule__Property__ValueAssignment_2_1 )
                    // InternalElkGraph.g:530:4: rule__Property__ValueAssignment_2_1
                    {
                    pushFollow(FOLLOW_2);
                    rule__Property__ValueAssignment_2_1();

                    state._fsp--;


                    }

                     after(grammarAccess.getPropertyAccess().getValueAssignment_2_1()); 

                    }


                    }
                    break;
                case 3 :
                    // InternalElkGraph.g:534:2: ( ( rule__Property__ValueAssignment_2_2 ) )
                    {
                    // InternalElkGraph.g:534:2: ( ( rule__Property__ValueAssignment_2_2 ) )
                    // InternalElkGraph.g:535:3: ( rule__Property__ValueAssignment_2_2 )
                    {
                     before(grammarAccess.getPropertyAccess().getValueAssignment_2_2()); 
                    // InternalElkGraph.g:536:3: ( rule__Property__ValueAssignment_2_2 )
                    // InternalElkGraph.g:536:4: rule__Property__ValueAssignment_2_2
                    {
                    pushFollow(FOLLOW_2);
                    rule__Property__ValueAssignment_2_2();

                    state._fsp--;


                    }

                     after(grammarAccess.getPropertyAccess().getValueAssignment_2_2()); 

                    }


                    }
                    break;
                case 4 :
                    // InternalElkGraph.g:540:2: ( ( rule__Property__ValueAssignment_2_3 ) )
                    {
                    // InternalElkGraph.g:540:2: ( ( rule__Property__ValueAssignment_2_3 ) )
                    // InternalElkGraph.g:541:3: ( rule__Property__ValueAssignment_2_3 )
                    {
                     before(grammarAccess.getPropertyAccess().getValueAssignment_2_3()); 
                    // InternalElkGraph.g:542:3: ( rule__Property__ValueAssignment_2_3 )
                    // InternalElkGraph.g:542:4: rule__Property__ValueAssignment_2_3
                    {
                    pushFollow(FOLLOW_2);
                    rule__Property__ValueAssignment_2_3();

                    state._fsp--;


                    }

                     after(grammarAccess.getPropertyAccess().getValueAssignment_2_3()); 

                    }


                    }
                    break;
                case 5 :
                    // InternalElkGraph.g:546:2: ( ( rule__Property__ValueAssignment_2_4 ) )
                    {
                    // InternalElkGraph.g:546:2: ( ( rule__Property__ValueAssignment_2_4 ) )
                    // InternalElkGraph.g:547:3: ( rule__Property__ValueAssignment_2_4 )
                    {
                     before(grammarAccess.getPropertyAccess().getValueAssignment_2_4()); 
                    // InternalElkGraph.g:548:3: ( rule__Property__ValueAssignment_2_4 )
                    // InternalElkGraph.g:548:4: rule__Property__ValueAssignment_2_4
                    {
                    pushFollow(FOLLOW_2);
                    rule__Property__ValueAssignment_2_4();

                    state._fsp--;


                    }

                     after(grammarAccess.getPropertyAccess().getValueAssignment_2_4()); 

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Property__Alternatives_2"


    // $ANTLR start "rule__Boolean__Alternatives"
    // InternalElkGraph.g:556:1: rule__Boolean__Alternatives : ( ( 'true' ) | ( 'false' ) );
    public final void rule__Boolean__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:560:1: ( ( 'true' ) | ( 'false' ) )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==13) ) {
                alt6=1;
            }
            else if ( (LA6_0==14) ) {
                alt6=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // InternalElkGraph.g:561:2: ( 'true' )
                    {
                    // InternalElkGraph.g:561:2: ( 'true' )
                    // InternalElkGraph.g:562:3: 'true'
                    {
                     before(grammarAccess.getBooleanAccess().getTrueKeyword_0()); 
                    match(input,13,FOLLOW_2); 
                     after(grammarAccess.getBooleanAccess().getTrueKeyword_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalElkGraph.g:567:2: ( 'false' )
                    {
                    // InternalElkGraph.g:567:2: ( 'false' )
                    // InternalElkGraph.g:568:3: 'false'
                    {
                     before(grammarAccess.getBooleanAccess().getFalseKeyword_1()); 
                    match(input,14,FOLLOW_2); 
                     after(grammarAccess.getBooleanAccess().getFalseKeyword_1()); 

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Boolean__Alternatives"


    // $ANTLR start "rule__Number__Alternatives"
    // InternalElkGraph.g:577:1: rule__Number__Alternatives : ( ( RULE_SIGNED_INT ) | ( RULE_FLOAT ) );
    public final void rule__Number__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:581:1: ( ( RULE_SIGNED_INT ) | ( RULE_FLOAT ) )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==RULE_SIGNED_INT) ) {
                alt7=1;
            }
            else if ( (LA7_0==RULE_FLOAT) ) {
                alt7=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // InternalElkGraph.g:582:2: ( RULE_SIGNED_INT )
                    {
                    // InternalElkGraph.g:582:2: ( RULE_SIGNED_INT )
                    // InternalElkGraph.g:583:3: RULE_SIGNED_INT
                    {
                     before(grammarAccess.getNumberAccess().getSIGNED_INTTerminalRuleCall_0()); 
                    match(input,RULE_SIGNED_INT,FOLLOW_2); 
                     after(grammarAccess.getNumberAccess().getSIGNED_INTTerminalRuleCall_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalElkGraph.g:588:2: ( RULE_FLOAT )
                    {
                    // InternalElkGraph.g:588:2: ( RULE_FLOAT )
                    // InternalElkGraph.g:589:3: RULE_FLOAT
                    {
                     before(grammarAccess.getNumberAccess().getFLOATTerminalRuleCall_1()); 
                    match(input,RULE_FLOAT,FOLLOW_2); 
                     after(grammarAccess.getNumberAccess().getFLOATTerminalRuleCall_1()); 

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Number__Alternatives"


    // $ANTLR start "rule__RootNode__Group__0"
    // InternalElkGraph.g:598:1: rule__RootNode__Group__0 : rule__RootNode__Group__0__Impl rule__RootNode__Group__1 ;
    public final void rule__RootNode__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:602:1: ( rule__RootNode__Group__0__Impl rule__RootNode__Group__1 )
            // InternalElkGraph.g:603:2: rule__RootNode__Group__0__Impl rule__RootNode__Group__1
            {
            pushFollow(FOLLOW_4);
            rule__RootNode__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__RootNode__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RootNode__Group__0"


    // $ANTLR start "rule__RootNode__Group__0__Impl"
    // InternalElkGraph.g:610:1: rule__RootNode__Group__0__Impl : ( () ) ;
    public final void rule__RootNode__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:614:1: ( ( () ) )
            // InternalElkGraph.g:615:1: ( () )
            {
            // InternalElkGraph.g:615:1: ( () )
            // InternalElkGraph.g:616:2: ()
            {
             before(grammarAccess.getRootNodeAccess().getElkNodeAction_0()); 
            // InternalElkGraph.g:617:2: ()
            // InternalElkGraph.g:617:3: 
            {
            }

             after(grammarAccess.getRootNodeAccess().getElkNodeAction_0()); 

            }


            }

        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RootNode__Group__0__Impl"


    // $ANTLR start "rule__RootNode__Group__1"
    // InternalElkGraph.g:625:1: rule__RootNode__Group__1 : rule__RootNode__Group__1__Impl rule__RootNode__Group__2 ;
    public final void rule__RootNode__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:629:1: ( rule__RootNode__Group__1__Impl rule__RootNode__Group__2 )
            // InternalElkGraph.g:630:2: rule__RootNode__Group__1__Impl rule__RootNode__Group__2
            {
            pushFollow(FOLLOW_4);
            rule__RootNode__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__RootNode__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RootNode__Group__1"


    // $ANTLR start "rule__RootNode__Group__1__Impl"
    // InternalElkGraph.g:637:1: rule__RootNode__Group__1__Impl : ( ( rule__RootNode__Group_1__0 )? ) ;
    public final void rule__RootNode__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:641:1: ( ( ( rule__RootNode__Group_1__0 )? ) )
            // InternalElkGraph.g:642:1: ( ( rule__RootNode__Group_1__0 )? )
            {
            // InternalElkGraph.g:642:1: ( ( rule__RootNode__Group_1__0 )? )
            // InternalElkGraph.g:643:2: ( rule__RootNode__Group_1__0 )?
            {
             before(grammarAccess.getRootNodeAccess().getGroup_1()); 
            // InternalElkGraph.g:644:2: ( rule__RootNode__Group_1__0 )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==15) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // InternalElkGraph.g:644:3: rule__RootNode__Group_1__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__RootNode__Group_1__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getRootNodeAccess().getGroup_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RootNode__Group__1__Impl"


    // $ANTLR start "rule__RootNode__Group__2"
    // InternalElkGraph.g:652:1: rule__RootNode__Group__2 : rule__RootNode__Group__2__Impl rule__RootNode__Group__3 ;
    public final void rule__RootNode__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:656:1: ( rule__RootNode__Group__2__Impl rule__RootNode__Group__3 )
            // InternalElkGraph.g:657:2: rule__RootNode__Group__2__Impl rule__RootNode__Group__3
            {
            pushFollow(FOLLOW_4);
            rule__RootNode__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__RootNode__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RootNode__Group__2"


    // $ANTLR start "rule__RootNode__Group__2__Impl"
    // InternalElkGraph.g:664:1: rule__RootNode__Group__2__Impl : ( ( rule__RootNode__PropertiesAssignment_2 )* ) ;
    public final void rule__RootNode__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:668:1: ( ( ( rule__RootNode__PropertiesAssignment_2 )* ) )
            // InternalElkGraph.g:669:1: ( ( rule__RootNode__PropertiesAssignment_2 )* )
            {
            // InternalElkGraph.g:669:1: ( ( rule__RootNode__PropertiesAssignment_2 )* )
            // InternalElkGraph.g:670:2: ( rule__RootNode__PropertiesAssignment_2 )*
            {
             before(grammarAccess.getRootNodeAccess().getPropertiesAssignment_2()); 
            // InternalElkGraph.g:671:2: ( rule__RootNode__PropertiesAssignment_2 )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==RULE_ID) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // InternalElkGraph.g:671:3: rule__RootNode__PropertiesAssignment_2
            	    {
            	    pushFollow(FOLLOW_5);
            	    rule__RootNode__PropertiesAssignment_2();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);

             after(grammarAccess.getRootNodeAccess().getPropertiesAssignment_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RootNode__Group__2__Impl"


    // $ANTLR start "rule__RootNode__Group__3"
    // InternalElkGraph.g:679:1: rule__RootNode__Group__3 : rule__RootNode__Group__3__Impl ;
    public final void rule__RootNode__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:683:1: ( rule__RootNode__Group__3__Impl )
            // InternalElkGraph.g:684:2: rule__RootNode__Group__3__Impl
            {
            pushFollow(FOLLOW_2);
            rule__RootNode__Group__3__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RootNode__Group__3"


    // $ANTLR start "rule__RootNode__Group__3__Impl"
    // InternalElkGraph.g:690:1: rule__RootNode__Group__3__Impl : ( ( rule__RootNode__Alternatives_3 )* ) ;
    public final void rule__RootNode__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:694:1: ( ( ( rule__RootNode__Alternatives_3 )* ) )
            // InternalElkGraph.g:695:1: ( ( rule__RootNode__Alternatives_3 )* )
            {
            // InternalElkGraph.g:695:1: ( ( rule__RootNode__Alternatives_3 )* )
            // InternalElkGraph.g:696:2: ( rule__RootNode__Alternatives_3 )*
            {
             before(grammarAccess.getRootNodeAccess().getAlternatives_3()); 
            // InternalElkGraph.g:697:2: ( rule__RootNode__Alternatives_3 )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==16||LA10_0==19||LA10_0==21||LA10_0==28) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // InternalElkGraph.g:697:3: rule__RootNode__Alternatives_3
            	    {
            	    pushFollow(FOLLOW_6);
            	    rule__RootNode__Alternatives_3();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);

             after(grammarAccess.getRootNodeAccess().getAlternatives_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RootNode__Group__3__Impl"


    // $ANTLR start "rule__RootNode__Group_1__0"
    // InternalElkGraph.g:706:1: rule__RootNode__Group_1__0 : rule__RootNode__Group_1__0__Impl rule__RootNode__Group_1__1 ;
    public final void rule__RootNode__Group_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:710:1: ( rule__RootNode__Group_1__0__Impl rule__RootNode__Group_1__1 )
            // InternalElkGraph.g:711:2: rule__RootNode__Group_1__0__Impl rule__RootNode__Group_1__1
            {
            pushFollow(FOLLOW_7);
            rule__RootNode__Group_1__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__RootNode__Group_1__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RootNode__Group_1__0"


    // $ANTLR start "rule__RootNode__Group_1__0__Impl"
    // InternalElkGraph.g:718:1: rule__RootNode__Group_1__0__Impl : ( 'graph' ) ;
    public final void rule__RootNode__Group_1__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:722:1: ( ( 'graph' ) )
            // InternalElkGraph.g:723:1: ( 'graph' )
            {
            // InternalElkGraph.g:723:1: ( 'graph' )
            // InternalElkGraph.g:724:2: 'graph'
            {
             before(grammarAccess.getRootNodeAccess().getGraphKeyword_1_0()); 
            match(input,15,FOLLOW_2); 
             after(grammarAccess.getRootNodeAccess().getGraphKeyword_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RootNode__Group_1__0__Impl"


    // $ANTLR start "rule__RootNode__Group_1__1"
    // InternalElkGraph.g:733:1: rule__RootNode__Group_1__1 : rule__RootNode__Group_1__1__Impl ;
    public final void rule__RootNode__Group_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:737:1: ( rule__RootNode__Group_1__1__Impl )
            // InternalElkGraph.g:738:2: rule__RootNode__Group_1__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__RootNode__Group_1__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RootNode__Group_1__1"


    // $ANTLR start "rule__RootNode__Group_1__1__Impl"
    // InternalElkGraph.g:744:1: rule__RootNode__Group_1__1__Impl : ( ( rule__RootNode__IdentifierAssignment_1_1 ) ) ;
    public final void rule__RootNode__Group_1__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:748:1: ( ( ( rule__RootNode__IdentifierAssignment_1_1 ) ) )
            // InternalElkGraph.g:749:1: ( ( rule__RootNode__IdentifierAssignment_1_1 ) )
            {
            // InternalElkGraph.g:749:1: ( ( rule__RootNode__IdentifierAssignment_1_1 ) )
            // InternalElkGraph.g:750:2: ( rule__RootNode__IdentifierAssignment_1_1 )
            {
             before(grammarAccess.getRootNodeAccess().getIdentifierAssignment_1_1()); 
            // InternalElkGraph.g:751:2: ( rule__RootNode__IdentifierAssignment_1_1 )
            // InternalElkGraph.g:751:3: rule__RootNode__IdentifierAssignment_1_1
            {
            pushFollow(FOLLOW_2);
            rule__RootNode__IdentifierAssignment_1_1();

            state._fsp--;


            }

             after(grammarAccess.getRootNodeAccess().getIdentifierAssignment_1_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RootNode__Group_1__1__Impl"


    // $ANTLR start "rule__ElkNode__Group__0"
    // InternalElkGraph.g:760:1: rule__ElkNode__Group__0 : rule__ElkNode__Group__0__Impl rule__ElkNode__Group__1 ;
    public final void rule__ElkNode__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:764:1: ( rule__ElkNode__Group__0__Impl rule__ElkNode__Group__1 )
            // InternalElkGraph.g:765:2: rule__ElkNode__Group__0__Impl rule__ElkNode__Group__1
            {
            pushFollow(FOLLOW_7);
            rule__ElkNode__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkNode__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkNode__Group__0"


    // $ANTLR start "rule__ElkNode__Group__0__Impl"
    // InternalElkGraph.g:772:1: rule__ElkNode__Group__0__Impl : ( 'node' ) ;
    public final void rule__ElkNode__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:776:1: ( ( 'node' ) )
            // InternalElkGraph.g:777:1: ( 'node' )
            {
            // InternalElkGraph.g:777:1: ( 'node' )
            // InternalElkGraph.g:778:2: 'node'
            {
             before(grammarAccess.getElkNodeAccess().getNodeKeyword_0()); 
            match(input,16,FOLLOW_2); 
             after(grammarAccess.getElkNodeAccess().getNodeKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkNode__Group__0__Impl"


    // $ANTLR start "rule__ElkNode__Group__1"
    // InternalElkGraph.g:787:1: rule__ElkNode__Group__1 : rule__ElkNode__Group__1__Impl rule__ElkNode__Group__2 ;
    public final void rule__ElkNode__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:791:1: ( rule__ElkNode__Group__1__Impl rule__ElkNode__Group__2 )
            // InternalElkGraph.g:792:2: rule__ElkNode__Group__1__Impl rule__ElkNode__Group__2
            {
            pushFollow(FOLLOW_8);
            rule__ElkNode__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkNode__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkNode__Group__1"


    // $ANTLR start "rule__ElkNode__Group__1__Impl"
    // InternalElkGraph.g:799:1: rule__ElkNode__Group__1__Impl : ( ( rule__ElkNode__IdentifierAssignment_1 ) ) ;
    public final void rule__ElkNode__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:803:1: ( ( ( rule__ElkNode__IdentifierAssignment_1 ) ) )
            // InternalElkGraph.g:804:1: ( ( rule__ElkNode__IdentifierAssignment_1 ) )
            {
            // InternalElkGraph.g:804:1: ( ( rule__ElkNode__IdentifierAssignment_1 ) )
            // InternalElkGraph.g:805:2: ( rule__ElkNode__IdentifierAssignment_1 )
            {
             before(grammarAccess.getElkNodeAccess().getIdentifierAssignment_1()); 
            // InternalElkGraph.g:806:2: ( rule__ElkNode__IdentifierAssignment_1 )
            // InternalElkGraph.g:806:3: rule__ElkNode__IdentifierAssignment_1
            {
            pushFollow(FOLLOW_2);
            rule__ElkNode__IdentifierAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getElkNodeAccess().getIdentifierAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkNode__Group__1__Impl"


    // $ANTLR start "rule__ElkNode__Group__2"
    // InternalElkGraph.g:814:1: rule__ElkNode__Group__2 : rule__ElkNode__Group__2__Impl ;
    public final void rule__ElkNode__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:818:1: ( rule__ElkNode__Group__2__Impl )
            // InternalElkGraph.g:819:2: rule__ElkNode__Group__2__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkNode__Group__2__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkNode__Group__2"


    // $ANTLR start "rule__ElkNode__Group__2__Impl"
    // InternalElkGraph.g:825:1: rule__ElkNode__Group__2__Impl : ( ( rule__ElkNode__Group_2__0 )? ) ;
    public final void rule__ElkNode__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:829:1: ( ( ( rule__ElkNode__Group_2__0 )? ) )
            // InternalElkGraph.g:830:1: ( ( rule__ElkNode__Group_2__0 )? )
            {
            // InternalElkGraph.g:830:1: ( ( rule__ElkNode__Group_2__0 )? )
            // InternalElkGraph.g:831:2: ( rule__ElkNode__Group_2__0 )?
            {
             before(grammarAccess.getElkNodeAccess().getGroup_2()); 
            // InternalElkGraph.g:832:2: ( rule__ElkNode__Group_2__0 )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==17) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // InternalElkGraph.g:832:3: rule__ElkNode__Group_2__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkNode__Group_2__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getElkNodeAccess().getGroup_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkNode__Group__2__Impl"


    // $ANTLR start "rule__ElkNode__Group_2__0"
    // InternalElkGraph.g:841:1: rule__ElkNode__Group_2__0 : rule__ElkNode__Group_2__0__Impl rule__ElkNode__Group_2__1 ;
    public final void rule__ElkNode__Group_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:845:1: ( rule__ElkNode__Group_2__0__Impl rule__ElkNode__Group_2__1 )
            // InternalElkGraph.g:846:2: rule__ElkNode__Group_2__0__Impl rule__ElkNode__Group_2__1
            {
            pushFollow(FOLLOW_9);
            rule__ElkNode__Group_2__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkNode__Group_2__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkNode__Group_2__0"


    // $ANTLR start "rule__ElkNode__Group_2__0__Impl"
    // InternalElkGraph.g:853:1: rule__ElkNode__Group_2__0__Impl : ( '{' ) ;
    public final void rule__ElkNode__Group_2__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:857:1: ( ( '{' ) )
            // InternalElkGraph.g:858:1: ( '{' )
            {
            // InternalElkGraph.g:858:1: ( '{' )
            // InternalElkGraph.g:859:2: '{'
            {
             before(grammarAccess.getElkNodeAccess().getLeftCurlyBracketKeyword_2_0()); 
            match(input,17,FOLLOW_2); 
             after(grammarAccess.getElkNodeAccess().getLeftCurlyBracketKeyword_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkNode__Group_2__0__Impl"


    // $ANTLR start "rule__ElkNode__Group_2__1"
    // InternalElkGraph.g:868:1: rule__ElkNode__Group_2__1 : rule__ElkNode__Group_2__1__Impl rule__ElkNode__Group_2__2 ;
    public final void rule__ElkNode__Group_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:872:1: ( rule__ElkNode__Group_2__1__Impl rule__ElkNode__Group_2__2 )
            // InternalElkGraph.g:873:2: rule__ElkNode__Group_2__1__Impl rule__ElkNode__Group_2__2
            {
            pushFollow(FOLLOW_9);
            rule__ElkNode__Group_2__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkNode__Group_2__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkNode__Group_2__1"


    // $ANTLR start "rule__ElkNode__Group_2__1__Impl"
    // InternalElkGraph.g:880:1: rule__ElkNode__Group_2__1__Impl : ( ( ruleShapeLayout )? ) ;
    public final void rule__ElkNode__Group_2__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:884:1: ( ( ( ruleShapeLayout )? ) )
            // InternalElkGraph.g:885:1: ( ( ruleShapeLayout )? )
            {
            // InternalElkGraph.g:885:1: ( ( ruleShapeLayout )? )
            // InternalElkGraph.g:886:2: ( ruleShapeLayout )?
            {
             before(grammarAccess.getElkNodeAccess().getShapeLayoutParserRuleCall_2_1()); 
            // InternalElkGraph.g:887:2: ( ruleShapeLayout )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==22) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // InternalElkGraph.g:887:3: ruleShapeLayout
                    {
                    pushFollow(FOLLOW_2);
                    ruleShapeLayout();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getElkNodeAccess().getShapeLayoutParserRuleCall_2_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkNode__Group_2__1__Impl"


    // $ANTLR start "rule__ElkNode__Group_2__2"
    // InternalElkGraph.g:895:1: rule__ElkNode__Group_2__2 : rule__ElkNode__Group_2__2__Impl rule__ElkNode__Group_2__3 ;
    public final void rule__ElkNode__Group_2__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:899:1: ( rule__ElkNode__Group_2__2__Impl rule__ElkNode__Group_2__3 )
            // InternalElkGraph.g:900:2: rule__ElkNode__Group_2__2__Impl rule__ElkNode__Group_2__3
            {
            pushFollow(FOLLOW_9);
            rule__ElkNode__Group_2__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkNode__Group_2__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkNode__Group_2__2"


    // $ANTLR start "rule__ElkNode__Group_2__2__Impl"
    // InternalElkGraph.g:907:1: rule__ElkNode__Group_2__2__Impl : ( ( rule__ElkNode__PropertiesAssignment_2_2 )* ) ;
    public final void rule__ElkNode__Group_2__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:911:1: ( ( ( rule__ElkNode__PropertiesAssignment_2_2 )* ) )
            // InternalElkGraph.g:912:1: ( ( rule__ElkNode__PropertiesAssignment_2_2 )* )
            {
            // InternalElkGraph.g:912:1: ( ( rule__ElkNode__PropertiesAssignment_2_2 )* )
            // InternalElkGraph.g:913:2: ( rule__ElkNode__PropertiesAssignment_2_2 )*
            {
             before(grammarAccess.getElkNodeAccess().getPropertiesAssignment_2_2()); 
            // InternalElkGraph.g:914:2: ( rule__ElkNode__PropertiesAssignment_2_2 )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0==RULE_ID) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // InternalElkGraph.g:914:3: rule__ElkNode__PropertiesAssignment_2_2
            	    {
            	    pushFollow(FOLLOW_5);
            	    rule__ElkNode__PropertiesAssignment_2_2();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);

             after(grammarAccess.getElkNodeAccess().getPropertiesAssignment_2_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkNode__Group_2__2__Impl"


    // $ANTLR start "rule__ElkNode__Group_2__3"
    // InternalElkGraph.g:922:1: rule__ElkNode__Group_2__3 : rule__ElkNode__Group_2__3__Impl rule__ElkNode__Group_2__4 ;
    public final void rule__ElkNode__Group_2__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:926:1: ( rule__ElkNode__Group_2__3__Impl rule__ElkNode__Group_2__4 )
            // InternalElkGraph.g:927:2: rule__ElkNode__Group_2__3__Impl rule__ElkNode__Group_2__4
            {
            pushFollow(FOLLOW_9);
            rule__ElkNode__Group_2__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkNode__Group_2__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkNode__Group_2__3"


    // $ANTLR start "rule__ElkNode__Group_2__3__Impl"
    // InternalElkGraph.g:934:1: rule__ElkNode__Group_2__3__Impl : ( ( rule__ElkNode__Alternatives_2_3 )* ) ;
    public final void rule__ElkNode__Group_2__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:938:1: ( ( ( rule__ElkNode__Alternatives_2_3 )* ) )
            // InternalElkGraph.g:939:1: ( ( rule__ElkNode__Alternatives_2_3 )* )
            {
            // InternalElkGraph.g:939:1: ( ( rule__ElkNode__Alternatives_2_3 )* )
            // InternalElkGraph.g:940:2: ( rule__ElkNode__Alternatives_2_3 )*
            {
             before(grammarAccess.getElkNodeAccess().getAlternatives_2_3()); 
            // InternalElkGraph.g:941:2: ( rule__ElkNode__Alternatives_2_3 )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0==16||LA14_0==19||LA14_0==21||LA14_0==28) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // InternalElkGraph.g:941:3: rule__ElkNode__Alternatives_2_3
            	    {
            	    pushFollow(FOLLOW_6);
            	    rule__ElkNode__Alternatives_2_3();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);

             after(grammarAccess.getElkNodeAccess().getAlternatives_2_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkNode__Group_2__3__Impl"


    // $ANTLR start "rule__ElkNode__Group_2__4"
    // InternalElkGraph.g:949:1: rule__ElkNode__Group_2__4 : rule__ElkNode__Group_2__4__Impl ;
    public final void rule__ElkNode__Group_2__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:953:1: ( rule__ElkNode__Group_2__4__Impl )
            // InternalElkGraph.g:954:2: rule__ElkNode__Group_2__4__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkNode__Group_2__4__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkNode__Group_2__4"


    // $ANTLR start "rule__ElkNode__Group_2__4__Impl"
    // InternalElkGraph.g:960:1: rule__ElkNode__Group_2__4__Impl : ( '}' ) ;
    public final void rule__ElkNode__Group_2__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:964:1: ( ( '}' ) )
            // InternalElkGraph.g:965:1: ( '}' )
            {
            // InternalElkGraph.g:965:1: ( '}' )
            // InternalElkGraph.g:966:2: '}'
            {
             before(grammarAccess.getElkNodeAccess().getRightCurlyBracketKeyword_2_4()); 
            match(input,18,FOLLOW_2); 
             after(grammarAccess.getElkNodeAccess().getRightCurlyBracketKeyword_2_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkNode__Group_2__4__Impl"


    // $ANTLR start "rule__ElkLabel__Group__0"
    // InternalElkGraph.g:976:1: rule__ElkLabel__Group__0 : rule__ElkLabel__Group__0__Impl rule__ElkLabel__Group__1 ;
    public final void rule__ElkLabel__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:980:1: ( rule__ElkLabel__Group__0__Impl rule__ElkLabel__Group__1 )
            // InternalElkGraph.g:981:2: rule__ElkLabel__Group__0__Impl rule__ElkLabel__Group__1
            {
            pushFollow(FOLLOW_10);
            rule__ElkLabel__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkLabel__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkLabel__Group__0"


    // $ANTLR start "rule__ElkLabel__Group__0__Impl"
    // InternalElkGraph.g:988:1: rule__ElkLabel__Group__0__Impl : ( 'label' ) ;
    public final void rule__ElkLabel__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:992:1: ( ( 'label' ) )
            // InternalElkGraph.g:993:1: ( 'label' )
            {
            // InternalElkGraph.g:993:1: ( 'label' )
            // InternalElkGraph.g:994:2: 'label'
            {
             before(grammarAccess.getElkLabelAccess().getLabelKeyword_0()); 
            match(input,19,FOLLOW_2); 
             after(grammarAccess.getElkLabelAccess().getLabelKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkLabel__Group__0__Impl"


    // $ANTLR start "rule__ElkLabel__Group__1"
    // InternalElkGraph.g:1003:1: rule__ElkLabel__Group__1 : rule__ElkLabel__Group__1__Impl rule__ElkLabel__Group__2 ;
    public final void rule__ElkLabel__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1007:1: ( rule__ElkLabel__Group__1__Impl rule__ElkLabel__Group__2 )
            // InternalElkGraph.g:1008:2: rule__ElkLabel__Group__1__Impl rule__ElkLabel__Group__2
            {
            pushFollow(FOLLOW_10);
            rule__ElkLabel__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkLabel__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkLabel__Group__1"


    // $ANTLR start "rule__ElkLabel__Group__1__Impl"
    // InternalElkGraph.g:1015:1: rule__ElkLabel__Group__1__Impl : ( ( rule__ElkLabel__Group_1__0 )? ) ;
    public final void rule__ElkLabel__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1019:1: ( ( ( rule__ElkLabel__Group_1__0 )? ) )
            // InternalElkGraph.g:1020:1: ( ( rule__ElkLabel__Group_1__0 )? )
            {
            // InternalElkGraph.g:1020:1: ( ( rule__ElkLabel__Group_1__0 )? )
            // InternalElkGraph.g:1021:2: ( rule__ElkLabel__Group_1__0 )?
            {
             before(grammarAccess.getElkLabelAccess().getGroup_1()); 
            // InternalElkGraph.g:1022:2: ( rule__ElkLabel__Group_1__0 )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==RULE_ID) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // InternalElkGraph.g:1022:3: rule__ElkLabel__Group_1__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkLabel__Group_1__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getElkLabelAccess().getGroup_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkLabel__Group__1__Impl"


    // $ANTLR start "rule__ElkLabel__Group__2"
    // InternalElkGraph.g:1030:1: rule__ElkLabel__Group__2 : rule__ElkLabel__Group__2__Impl rule__ElkLabel__Group__3 ;
    public final void rule__ElkLabel__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1034:1: ( rule__ElkLabel__Group__2__Impl rule__ElkLabel__Group__3 )
            // InternalElkGraph.g:1035:2: rule__ElkLabel__Group__2__Impl rule__ElkLabel__Group__3
            {
            pushFollow(FOLLOW_8);
            rule__ElkLabel__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkLabel__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkLabel__Group__2"


    // $ANTLR start "rule__ElkLabel__Group__2__Impl"
    // InternalElkGraph.g:1042:1: rule__ElkLabel__Group__2__Impl : ( ( rule__ElkLabel__TextAssignment_2 ) ) ;
    public final void rule__ElkLabel__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1046:1: ( ( ( rule__ElkLabel__TextAssignment_2 ) ) )
            // InternalElkGraph.g:1047:1: ( ( rule__ElkLabel__TextAssignment_2 ) )
            {
            // InternalElkGraph.g:1047:1: ( ( rule__ElkLabel__TextAssignment_2 ) )
            // InternalElkGraph.g:1048:2: ( rule__ElkLabel__TextAssignment_2 )
            {
             before(grammarAccess.getElkLabelAccess().getTextAssignment_2()); 
            // InternalElkGraph.g:1049:2: ( rule__ElkLabel__TextAssignment_2 )
            // InternalElkGraph.g:1049:3: rule__ElkLabel__TextAssignment_2
            {
            pushFollow(FOLLOW_2);
            rule__ElkLabel__TextAssignment_2();

            state._fsp--;


            }

             after(grammarAccess.getElkLabelAccess().getTextAssignment_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkLabel__Group__2__Impl"


    // $ANTLR start "rule__ElkLabel__Group__3"
    // InternalElkGraph.g:1057:1: rule__ElkLabel__Group__3 : rule__ElkLabel__Group__3__Impl ;
    public final void rule__ElkLabel__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1061:1: ( rule__ElkLabel__Group__3__Impl )
            // InternalElkGraph.g:1062:2: rule__ElkLabel__Group__3__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkLabel__Group__3__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkLabel__Group__3"


    // $ANTLR start "rule__ElkLabel__Group__3__Impl"
    // InternalElkGraph.g:1068:1: rule__ElkLabel__Group__3__Impl : ( ( rule__ElkLabel__Group_3__0 )? ) ;
    public final void rule__ElkLabel__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1072:1: ( ( ( rule__ElkLabel__Group_3__0 )? ) )
            // InternalElkGraph.g:1073:1: ( ( rule__ElkLabel__Group_3__0 )? )
            {
            // InternalElkGraph.g:1073:1: ( ( rule__ElkLabel__Group_3__0 )? )
            // InternalElkGraph.g:1074:2: ( rule__ElkLabel__Group_3__0 )?
            {
             before(grammarAccess.getElkLabelAccess().getGroup_3()); 
            // InternalElkGraph.g:1075:2: ( rule__ElkLabel__Group_3__0 )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==17) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // InternalElkGraph.g:1075:3: rule__ElkLabel__Group_3__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkLabel__Group_3__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getElkLabelAccess().getGroup_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkLabel__Group__3__Impl"


    // $ANTLR start "rule__ElkLabel__Group_1__0"
    // InternalElkGraph.g:1084:1: rule__ElkLabel__Group_1__0 : rule__ElkLabel__Group_1__0__Impl rule__ElkLabel__Group_1__1 ;
    public final void rule__ElkLabel__Group_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1088:1: ( rule__ElkLabel__Group_1__0__Impl rule__ElkLabel__Group_1__1 )
            // InternalElkGraph.g:1089:2: rule__ElkLabel__Group_1__0__Impl rule__ElkLabel__Group_1__1
            {
            pushFollow(FOLLOW_11);
            rule__ElkLabel__Group_1__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkLabel__Group_1__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkLabel__Group_1__0"


    // $ANTLR start "rule__ElkLabel__Group_1__0__Impl"
    // InternalElkGraph.g:1096:1: rule__ElkLabel__Group_1__0__Impl : ( ( rule__ElkLabel__IdentifierAssignment_1_0 ) ) ;
    public final void rule__ElkLabel__Group_1__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1100:1: ( ( ( rule__ElkLabel__IdentifierAssignment_1_0 ) ) )
            // InternalElkGraph.g:1101:1: ( ( rule__ElkLabel__IdentifierAssignment_1_0 ) )
            {
            // InternalElkGraph.g:1101:1: ( ( rule__ElkLabel__IdentifierAssignment_1_0 ) )
            // InternalElkGraph.g:1102:2: ( rule__ElkLabel__IdentifierAssignment_1_0 )
            {
             before(grammarAccess.getElkLabelAccess().getIdentifierAssignment_1_0()); 
            // InternalElkGraph.g:1103:2: ( rule__ElkLabel__IdentifierAssignment_1_0 )
            // InternalElkGraph.g:1103:3: rule__ElkLabel__IdentifierAssignment_1_0
            {
            pushFollow(FOLLOW_2);
            rule__ElkLabel__IdentifierAssignment_1_0();

            state._fsp--;


            }

             after(grammarAccess.getElkLabelAccess().getIdentifierAssignment_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkLabel__Group_1__0__Impl"


    // $ANTLR start "rule__ElkLabel__Group_1__1"
    // InternalElkGraph.g:1111:1: rule__ElkLabel__Group_1__1 : rule__ElkLabel__Group_1__1__Impl ;
    public final void rule__ElkLabel__Group_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1115:1: ( rule__ElkLabel__Group_1__1__Impl )
            // InternalElkGraph.g:1116:2: rule__ElkLabel__Group_1__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkLabel__Group_1__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkLabel__Group_1__1"


    // $ANTLR start "rule__ElkLabel__Group_1__1__Impl"
    // InternalElkGraph.g:1122:1: rule__ElkLabel__Group_1__1__Impl : ( ':' ) ;
    public final void rule__ElkLabel__Group_1__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1126:1: ( ( ':' ) )
            // InternalElkGraph.g:1127:1: ( ':' )
            {
            // InternalElkGraph.g:1127:1: ( ':' )
            // InternalElkGraph.g:1128:2: ':'
            {
             before(grammarAccess.getElkLabelAccess().getColonKeyword_1_1()); 
            match(input,20,FOLLOW_2); 
             after(grammarAccess.getElkLabelAccess().getColonKeyword_1_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkLabel__Group_1__1__Impl"


    // $ANTLR start "rule__ElkLabel__Group_3__0"
    // InternalElkGraph.g:1138:1: rule__ElkLabel__Group_3__0 : rule__ElkLabel__Group_3__0__Impl rule__ElkLabel__Group_3__1 ;
    public final void rule__ElkLabel__Group_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1142:1: ( rule__ElkLabel__Group_3__0__Impl rule__ElkLabel__Group_3__1 )
            // InternalElkGraph.g:1143:2: rule__ElkLabel__Group_3__0__Impl rule__ElkLabel__Group_3__1
            {
            pushFollow(FOLLOW_9);
            rule__ElkLabel__Group_3__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkLabel__Group_3__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkLabel__Group_3__0"


    // $ANTLR start "rule__ElkLabel__Group_3__0__Impl"
    // InternalElkGraph.g:1150:1: rule__ElkLabel__Group_3__0__Impl : ( '{' ) ;
    public final void rule__ElkLabel__Group_3__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1154:1: ( ( '{' ) )
            // InternalElkGraph.g:1155:1: ( '{' )
            {
            // InternalElkGraph.g:1155:1: ( '{' )
            // InternalElkGraph.g:1156:2: '{'
            {
             before(grammarAccess.getElkLabelAccess().getLeftCurlyBracketKeyword_3_0()); 
            match(input,17,FOLLOW_2); 
             after(grammarAccess.getElkLabelAccess().getLeftCurlyBracketKeyword_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkLabel__Group_3__0__Impl"


    // $ANTLR start "rule__ElkLabel__Group_3__1"
    // InternalElkGraph.g:1165:1: rule__ElkLabel__Group_3__1 : rule__ElkLabel__Group_3__1__Impl rule__ElkLabel__Group_3__2 ;
    public final void rule__ElkLabel__Group_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1169:1: ( rule__ElkLabel__Group_3__1__Impl rule__ElkLabel__Group_3__2 )
            // InternalElkGraph.g:1170:2: rule__ElkLabel__Group_3__1__Impl rule__ElkLabel__Group_3__2
            {
            pushFollow(FOLLOW_9);
            rule__ElkLabel__Group_3__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkLabel__Group_3__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkLabel__Group_3__1"


    // $ANTLR start "rule__ElkLabel__Group_3__1__Impl"
    // InternalElkGraph.g:1177:1: rule__ElkLabel__Group_3__1__Impl : ( ( ruleShapeLayout )? ) ;
    public final void rule__ElkLabel__Group_3__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1181:1: ( ( ( ruleShapeLayout )? ) )
            // InternalElkGraph.g:1182:1: ( ( ruleShapeLayout )? )
            {
            // InternalElkGraph.g:1182:1: ( ( ruleShapeLayout )? )
            // InternalElkGraph.g:1183:2: ( ruleShapeLayout )?
            {
             before(grammarAccess.getElkLabelAccess().getShapeLayoutParserRuleCall_3_1()); 
            // InternalElkGraph.g:1184:2: ( ruleShapeLayout )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==22) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // InternalElkGraph.g:1184:3: ruleShapeLayout
                    {
                    pushFollow(FOLLOW_2);
                    ruleShapeLayout();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getElkLabelAccess().getShapeLayoutParserRuleCall_3_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkLabel__Group_3__1__Impl"


    // $ANTLR start "rule__ElkLabel__Group_3__2"
    // InternalElkGraph.g:1192:1: rule__ElkLabel__Group_3__2 : rule__ElkLabel__Group_3__2__Impl rule__ElkLabel__Group_3__3 ;
    public final void rule__ElkLabel__Group_3__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1196:1: ( rule__ElkLabel__Group_3__2__Impl rule__ElkLabel__Group_3__3 )
            // InternalElkGraph.g:1197:2: rule__ElkLabel__Group_3__2__Impl rule__ElkLabel__Group_3__3
            {
            pushFollow(FOLLOW_9);
            rule__ElkLabel__Group_3__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkLabel__Group_3__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkLabel__Group_3__2"


    // $ANTLR start "rule__ElkLabel__Group_3__2__Impl"
    // InternalElkGraph.g:1204:1: rule__ElkLabel__Group_3__2__Impl : ( ( rule__ElkLabel__PropertiesAssignment_3_2 )* ) ;
    public final void rule__ElkLabel__Group_3__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1208:1: ( ( ( rule__ElkLabel__PropertiesAssignment_3_2 )* ) )
            // InternalElkGraph.g:1209:1: ( ( rule__ElkLabel__PropertiesAssignment_3_2 )* )
            {
            // InternalElkGraph.g:1209:1: ( ( rule__ElkLabel__PropertiesAssignment_3_2 )* )
            // InternalElkGraph.g:1210:2: ( rule__ElkLabel__PropertiesAssignment_3_2 )*
            {
             before(grammarAccess.getElkLabelAccess().getPropertiesAssignment_3_2()); 
            // InternalElkGraph.g:1211:2: ( rule__ElkLabel__PropertiesAssignment_3_2 )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==RULE_ID) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // InternalElkGraph.g:1211:3: rule__ElkLabel__PropertiesAssignment_3_2
            	    {
            	    pushFollow(FOLLOW_5);
            	    rule__ElkLabel__PropertiesAssignment_3_2();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop18;
                }
            } while (true);

             after(grammarAccess.getElkLabelAccess().getPropertiesAssignment_3_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkLabel__Group_3__2__Impl"


    // $ANTLR start "rule__ElkLabel__Group_3__3"
    // InternalElkGraph.g:1219:1: rule__ElkLabel__Group_3__3 : rule__ElkLabel__Group_3__3__Impl rule__ElkLabel__Group_3__4 ;
    public final void rule__ElkLabel__Group_3__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1223:1: ( rule__ElkLabel__Group_3__3__Impl rule__ElkLabel__Group_3__4 )
            // InternalElkGraph.g:1224:2: rule__ElkLabel__Group_3__3__Impl rule__ElkLabel__Group_3__4
            {
            pushFollow(FOLLOW_9);
            rule__ElkLabel__Group_3__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkLabel__Group_3__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkLabel__Group_3__3"


    // $ANTLR start "rule__ElkLabel__Group_3__3__Impl"
    // InternalElkGraph.g:1231:1: rule__ElkLabel__Group_3__3__Impl : ( ( rule__ElkLabel__LabelsAssignment_3_3 )* ) ;
    public final void rule__ElkLabel__Group_3__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1235:1: ( ( ( rule__ElkLabel__LabelsAssignment_3_3 )* ) )
            // InternalElkGraph.g:1236:1: ( ( rule__ElkLabel__LabelsAssignment_3_3 )* )
            {
            // InternalElkGraph.g:1236:1: ( ( rule__ElkLabel__LabelsAssignment_3_3 )* )
            // InternalElkGraph.g:1237:2: ( rule__ElkLabel__LabelsAssignment_3_3 )*
            {
             before(grammarAccess.getElkLabelAccess().getLabelsAssignment_3_3()); 
            // InternalElkGraph.g:1238:2: ( rule__ElkLabel__LabelsAssignment_3_3 )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==19) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // InternalElkGraph.g:1238:3: rule__ElkLabel__LabelsAssignment_3_3
            	    {
            	    pushFollow(FOLLOW_6);
            	    rule__ElkLabel__LabelsAssignment_3_3();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop19;
                }
            } while (true);

             after(grammarAccess.getElkLabelAccess().getLabelsAssignment_3_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkLabel__Group_3__3__Impl"


    // $ANTLR start "rule__ElkLabel__Group_3__4"
    // InternalElkGraph.g:1246:1: rule__ElkLabel__Group_3__4 : rule__ElkLabel__Group_3__4__Impl ;
    public final void rule__ElkLabel__Group_3__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1250:1: ( rule__ElkLabel__Group_3__4__Impl )
            // InternalElkGraph.g:1251:2: rule__ElkLabel__Group_3__4__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkLabel__Group_3__4__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkLabel__Group_3__4"


    // $ANTLR start "rule__ElkLabel__Group_3__4__Impl"
    // InternalElkGraph.g:1257:1: rule__ElkLabel__Group_3__4__Impl : ( '}' ) ;
    public final void rule__ElkLabel__Group_3__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1261:1: ( ( '}' ) )
            // InternalElkGraph.g:1262:1: ( '}' )
            {
            // InternalElkGraph.g:1262:1: ( '}' )
            // InternalElkGraph.g:1263:2: '}'
            {
             before(grammarAccess.getElkLabelAccess().getRightCurlyBracketKeyword_3_4()); 
            match(input,18,FOLLOW_2); 
             after(grammarAccess.getElkLabelAccess().getRightCurlyBracketKeyword_3_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkLabel__Group_3__4__Impl"


    // $ANTLR start "rule__ElkPort__Group__0"
    // InternalElkGraph.g:1273:1: rule__ElkPort__Group__0 : rule__ElkPort__Group__0__Impl rule__ElkPort__Group__1 ;
    public final void rule__ElkPort__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1277:1: ( rule__ElkPort__Group__0__Impl rule__ElkPort__Group__1 )
            // InternalElkGraph.g:1278:2: rule__ElkPort__Group__0__Impl rule__ElkPort__Group__1
            {
            pushFollow(FOLLOW_7);
            rule__ElkPort__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkPort__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkPort__Group__0"


    // $ANTLR start "rule__ElkPort__Group__0__Impl"
    // InternalElkGraph.g:1285:1: rule__ElkPort__Group__0__Impl : ( 'port' ) ;
    public final void rule__ElkPort__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1289:1: ( ( 'port' ) )
            // InternalElkGraph.g:1290:1: ( 'port' )
            {
            // InternalElkGraph.g:1290:1: ( 'port' )
            // InternalElkGraph.g:1291:2: 'port'
            {
             before(grammarAccess.getElkPortAccess().getPortKeyword_0()); 
            match(input,21,FOLLOW_2); 
             after(grammarAccess.getElkPortAccess().getPortKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkPort__Group__0__Impl"


    // $ANTLR start "rule__ElkPort__Group__1"
    // InternalElkGraph.g:1300:1: rule__ElkPort__Group__1 : rule__ElkPort__Group__1__Impl rule__ElkPort__Group__2 ;
    public final void rule__ElkPort__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1304:1: ( rule__ElkPort__Group__1__Impl rule__ElkPort__Group__2 )
            // InternalElkGraph.g:1305:2: rule__ElkPort__Group__1__Impl rule__ElkPort__Group__2
            {
            pushFollow(FOLLOW_8);
            rule__ElkPort__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkPort__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkPort__Group__1"


    // $ANTLR start "rule__ElkPort__Group__1__Impl"
    // InternalElkGraph.g:1312:1: rule__ElkPort__Group__1__Impl : ( ( rule__ElkPort__IdentifierAssignment_1 ) ) ;
    public final void rule__ElkPort__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1316:1: ( ( ( rule__ElkPort__IdentifierAssignment_1 ) ) )
            // InternalElkGraph.g:1317:1: ( ( rule__ElkPort__IdentifierAssignment_1 ) )
            {
            // InternalElkGraph.g:1317:1: ( ( rule__ElkPort__IdentifierAssignment_1 ) )
            // InternalElkGraph.g:1318:2: ( rule__ElkPort__IdentifierAssignment_1 )
            {
             before(grammarAccess.getElkPortAccess().getIdentifierAssignment_1()); 
            // InternalElkGraph.g:1319:2: ( rule__ElkPort__IdentifierAssignment_1 )
            // InternalElkGraph.g:1319:3: rule__ElkPort__IdentifierAssignment_1
            {
            pushFollow(FOLLOW_2);
            rule__ElkPort__IdentifierAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getElkPortAccess().getIdentifierAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkPort__Group__1__Impl"


    // $ANTLR start "rule__ElkPort__Group__2"
    // InternalElkGraph.g:1327:1: rule__ElkPort__Group__2 : rule__ElkPort__Group__2__Impl ;
    public final void rule__ElkPort__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1331:1: ( rule__ElkPort__Group__2__Impl )
            // InternalElkGraph.g:1332:2: rule__ElkPort__Group__2__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkPort__Group__2__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkPort__Group__2"


    // $ANTLR start "rule__ElkPort__Group__2__Impl"
    // InternalElkGraph.g:1338:1: rule__ElkPort__Group__2__Impl : ( ( rule__ElkPort__Group_2__0 )? ) ;
    public final void rule__ElkPort__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1342:1: ( ( ( rule__ElkPort__Group_2__0 )? ) )
            // InternalElkGraph.g:1343:1: ( ( rule__ElkPort__Group_2__0 )? )
            {
            // InternalElkGraph.g:1343:1: ( ( rule__ElkPort__Group_2__0 )? )
            // InternalElkGraph.g:1344:2: ( rule__ElkPort__Group_2__0 )?
            {
             before(grammarAccess.getElkPortAccess().getGroup_2()); 
            // InternalElkGraph.g:1345:2: ( rule__ElkPort__Group_2__0 )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==17) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // InternalElkGraph.g:1345:3: rule__ElkPort__Group_2__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkPort__Group_2__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getElkPortAccess().getGroup_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkPort__Group__2__Impl"


    // $ANTLR start "rule__ElkPort__Group_2__0"
    // InternalElkGraph.g:1354:1: rule__ElkPort__Group_2__0 : rule__ElkPort__Group_2__0__Impl rule__ElkPort__Group_2__1 ;
    public final void rule__ElkPort__Group_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1358:1: ( rule__ElkPort__Group_2__0__Impl rule__ElkPort__Group_2__1 )
            // InternalElkGraph.g:1359:2: rule__ElkPort__Group_2__0__Impl rule__ElkPort__Group_2__1
            {
            pushFollow(FOLLOW_9);
            rule__ElkPort__Group_2__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkPort__Group_2__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkPort__Group_2__0"


    // $ANTLR start "rule__ElkPort__Group_2__0__Impl"
    // InternalElkGraph.g:1366:1: rule__ElkPort__Group_2__0__Impl : ( '{' ) ;
    public final void rule__ElkPort__Group_2__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1370:1: ( ( '{' ) )
            // InternalElkGraph.g:1371:1: ( '{' )
            {
            // InternalElkGraph.g:1371:1: ( '{' )
            // InternalElkGraph.g:1372:2: '{'
            {
             before(grammarAccess.getElkPortAccess().getLeftCurlyBracketKeyword_2_0()); 
            match(input,17,FOLLOW_2); 
             after(grammarAccess.getElkPortAccess().getLeftCurlyBracketKeyword_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkPort__Group_2__0__Impl"


    // $ANTLR start "rule__ElkPort__Group_2__1"
    // InternalElkGraph.g:1381:1: rule__ElkPort__Group_2__1 : rule__ElkPort__Group_2__1__Impl rule__ElkPort__Group_2__2 ;
    public final void rule__ElkPort__Group_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1385:1: ( rule__ElkPort__Group_2__1__Impl rule__ElkPort__Group_2__2 )
            // InternalElkGraph.g:1386:2: rule__ElkPort__Group_2__1__Impl rule__ElkPort__Group_2__2
            {
            pushFollow(FOLLOW_9);
            rule__ElkPort__Group_2__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkPort__Group_2__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkPort__Group_2__1"


    // $ANTLR start "rule__ElkPort__Group_2__1__Impl"
    // InternalElkGraph.g:1393:1: rule__ElkPort__Group_2__1__Impl : ( ( ruleShapeLayout )? ) ;
    public final void rule__ElkPort__Group_2__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1397:1: ( ( ( ruleShapeLayout )? ) )
            // InternalElkGraph.g:1398:1: ( ( ruleShapeLayout )? )
            {
            // InternalElkGraph.g:1398:1: ( ( ruleShapeLayout )? )
            // InternalElkGraph.g:1399:2: ( ruleShapeLayout )?
            {
             before(grammarAccess.getElkPortAccess().getShapeLayoutParserRuleCall_2_1()); 
            // InternalElkGraph.g:1400:2: ( ruleShapeLayout )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==22) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // InternalElkGraph.g:1400:3: ruleShapeLayout
                    {
                    pushFollow(FOLLOW_2);
                    ruleShapeLayout();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getElkPortAccess().getShapeLayoutParserRuleCall_2_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkPort__Group_2__1__Impl"


    // $ANTLR start "rule__ElkPort__Group_2__2"
    // InternalElkGraph.g:1408:1: rule__ElkPort__Group_2__2 : rule__ElkPort__Group_2__2__Impl rule__ElkPort__Group_2__3 ;
    public final void rule__ElkPort__Group_2__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1412:1: ( rule__ElkPort__Group_2__2__Impl rule__ElkPort__Group_2__3 )
            // InternalElkGraph.g:1413:2: rule__ElkPort__Group_2__2__Impl rule__ElkPort__Group_2__3
            {
            pushFollow(FOLLOW_9);
            rule__ElkPort__Group_2__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkPort__Group_2__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkPort__Group_2__2"


    // $ANTLR start "rule__ElkPort__Group_2__2__Impl"
    // InternalElkGraph.g:1420:1: rule__ElkPort__Group_2__2__Impl : ( ( rule__ElkPort__PropertiesAssignment_2_2 )* ) ;
    public final void rule__ElkPort__Group_2__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1424:1: ( ( ( rule__ElkPort__PropertiesAssignment_2_2 )* ) )
            // InternalElkGraph.g:1425:1: ( ( rule__ElkPort__PropertiesAssignment_2_2 )* )
            {
            // InternalElkGraph.g:1425:1: ( ( rule__ElkPort__PropertiesAssignment_2_2 )* )
            // InternalElkGraph.g:1426:2: ( rule__ElkPort__PropertiesAssignment_2_2 )*
            {
             before(grammarAccess.getElkPortAccess().getPropertiesAssignment_2_2()); 
            // InternalElkGraph.g:1427:2: ( rule__ElkPort__PropertiesAssignment_2_2 )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==RULE_ID) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // InternalElkGraph.g:1427:3: rule__ElkPort__PropertiesAssignment_2_2
            	    {
            	    pushFollow(FOLLOW_5);
            	    rule__ElkPort__PropertiesAssignment_2_2();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop22;
                }
            } while (true);

             after(grammarAccess.getElkPortAccess().getPropertiesAssignment_2_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkPort__Group_2__2__Impl"


    // $ANTLR start "rule__ElkPort__Group_2__3"
    // InternalElkGraph.g:1435:1: rule__ElkPort__Group_2__3 : rule__ElkPort__Group_2__3__Impl rule__ElkPort__Group_2__4 ;
    public final void rule__ElkPort__Group_2__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1439:1: ( rule__ElkPort__Group_2__3__Impl rule__ElkPort__Group_2__4 )
            // InternalElkGraph.g:1440:2: rule__ElkPort__Group_2__3__Impl rule__ElkPort__Group_2__4
            {
            pushFollow(FOLLOW_9);
            rule__ElkPort__Group_2__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkPort__Group_2__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkPort__Group_2__3"


    // $ANTLR start "rule__ElkPort__Group_2__3__Impl"
    // InternalElkGraph.g:1447:1: rule__ElkPort__Group_2__3__Impl : ( ( rule__ElkPort__LabelsAssignment_2_3 )* ) ;
    public final void rule__ElkPort__Group_2__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1451:1: ( ( ( rule__ElkPort__LabelsAssignment_2_3 )* ) )
            // InternalElkGraph.g:1452:1: ( ( rule__ElkPort__LabelsAssignment_2_3 )* )
            {
            // InternalElkGraph.g:1452:1: ( ( rule__ElkPort__LabelsAssignment_2_3 )* )
            // InternalElkGraph.g:1453:2: ( rule__ElkPort__LabelsAssignment_2_3 )*
            {
             before(grammarAccess.getElkPortAccess().getLabelsAssignment_2_3()); 
            // InternalElkGraph.g:1454:2: ( rule__ElkPort__LabelsAssignment_2_3 )*
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( (LA23_0==19) ) {
                    alt23=1;
                }


                switch (alt23) {
            	case 1 :
            	    // InternalElkGraph.g:1454:3: rule__ElkPort__LabelsAssignment_2_3
            	    {
            	    pushFollow(FOLLOW_6);
            	    rule__ElkPort__LabelsAssignment_2_3();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop23;
                }
            } while (true);

             after(grammarAccess.getElkPortAccess().getLabelsAssignment_2_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkPort__Group_2__3__Impl"


    // $ANTLR start "rule__ElkPort__Group_2__4"
    // InternalElkGraph.g:1462:1: rule__ElkPort__Group_2__4 : rule__ElkPort__Group_2__4__Impl ;
    public final void rule__ElkPort__Group_2__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1466:1: ( rule__ElkPort__Group_2__4__Impl )
            // InternalElkGraph.g:1467:2: rule__ElkPort__Group_2__4__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkPort__Group_2__4__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkPort__Group_2__4"


    // $ANTLR start "rule__ElkPort__Group_2__4__Impl"
    // InternalElkGraph.g:1473:1: rule__ElkPort__Group_2__4__Impl : ( '}' ) ;
    public final void rule__ElkPort__Group_2__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1477:1: ( ( '}' ) )
            // InternalElkGraph.g:1478:1: ( '}' )
            {
            // InternalElkGraph.g:1478:1: ( '}' )
            // InternalElkGraph.g:1479:2: '}'
            {
             before(grammarAccess.getElkPortAccess().getRightCurlyBracketKeyword_2_4()); 
            match(input,18,FOLLOW_2); 
             after(grammarAccess.getElkPortAccess().getRightCurlyBracketKeyword_2_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkPort__Group_2__4__Impl"


    // $ANTLR start "rule__ShapeLayout__Group__0"
    // InternalElkGraph.g:1489:1: rule__ShapeLayout__Group__0 : rule__ShapeLayout__Group__0__Impl rule__ShapeLayout__Group__1 ;
    public final void rule__ShapeLayout__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1493:1: ( rule__ShapeLayout__Group__0__Impl rule__ShapeLayout__Group__1 )
            // InternalElkGraph.g:1494:2: rule__ShapeLayout__Group__0__Impl rule__ShapeLayout__Group__1
            {
            pushFollow(FOLLOW_12);
            rule__ShapeLayout__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ShapeLayout__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__Group__0"


    // $ANTLR start "rule__ShapeLayout__Group__0__Impl"
    // InternalElkGraph.g:1501:1: rule__ShapeLayout__Group__0__Impl : ( 'layout' ) ;
    public final void rule__ShapeLayout__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1505:1: ( ( 'layout' ) )
            // InternalElkGraph.g:1506:1: ( 'layout' )
            {
            // InternalElkGraph.g:1506:1: ( 'layout' )
            // InternalElkGraph.g:1507:2: 'layout'
            {
             before(grammarAccess.getShapeLayoutAccess().getLayoutKeyword_0()); 
            match(input,22,FOLLOW_2); 
             after(grammarAccess.getShapeLayoutAccess().getLayoutKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__Group__0__Impl"


    // $ANTLR start "rule__ShapeLayout__Group__1"
    // InternalElkGraph.g:1516:1: rule__ShapeLayout__Group__1 : rule__ShapeLayout__Group__1__Impl rule__ShapeLayout__Group__2 ;
    public final void rule__ShapeLayout__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1520:1: ( rule__ShapeLayout__Group__1__Impl rule__ShapeLayout__Group__2 )
            // InternalElkGraph.g:1521:2: rule__ShapeLayout__Group__1__Impl rule__ShapeLayout__Group__2
            {
            pushFollow(FOLLOW_13);
            rule__ShapeLayout__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ShapeLayout__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__Group__1"


    // $ANTLR start "rule__ShapeLayout__Group__1__Impl"
    // InternalElkGraph.g:1528:1: rule__ShapeLayout__Group__1__Impl : ( '[' ) ;
    public final void rule__ShapeLayout__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1532:1: ( ( '[' ) )
            // InternalElkGraph.g:1533:1: ( '[' )
            {
            // InternalElkGraph.g:1533:1: ( '[' )
            // InternalElkGraph.g:1534:2: '['
            {
             before(grammarAccess.getShapeLayoutAccess().getLeftSquareBracketKeyword_1()); 
            match(input,23,FOLLOW_2); 
             after(grammarAccess.getShapeLayoutAccess().getLeftSquareBracketKeyword_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__Group__1__Impl"


    // $ANTLR start "rule__ShapeLayout__Group__2"
    // InternalElkGraph.g:1543:1: rule__ShapeLayout__Group__2 : rule__ShapeLayout__Group__2__Impl rule__ShapeLayout__Group__3 ;
    public final void rule__ShapeLayout__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1547:1: ( rule__ShapeLayout__Group__2__Impl rule__ShapeLayout__Group__3 )
            // InternalElkGraph.g:1548:2: rule__ShapeLayout__Group__2__Impl rule__ShapeLayout__Group__3
            {
            pushFollow(FOLLOW_14);
            rule__ShapeLayout__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ShapeLayout__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__Group__2"


    // $ANTLR start "rule__ShapeLayout__Group__2__Impl"
    // InternalElkGraph.g:1555:1: rule__ShapeLayout__Group__2__Impl : ( ( rule__ShapeLayout__UnorderedGroup_2 ) ) ;
    public final void rule__ShapeLayout__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1559:1: ( ( ( rule__ShapeLayout__UnorderedGroup_2 ) ) )
            // InternalElkGraph.g:1560:1: ( ( rule__ShapeLayout__UnorderedGroup_2 ) )
            {
            // InternalElkGraph.g:1560:1: ( ( rule__ShapeLayout__UnorderedGroup_2 ) )
            // InternalElkGraph.g:1561:2: ( rule__ShapeLayout__UnorderedGroup_2 )
            {
             before(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2()); 
            // InternalElkGraph.g:1562:2: ( rule__ShapeLayout__UnorderedGroup_2 )
            // InternalElkGraph.g:1562:3: rule__ShapeLayout__UnorderedGroup_2
            {
            pushFollow(FOLLOW_2);
            rule__ShapeLayout__UnorderedGroup_2();

            state._fsp--;


            }

             after(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__Group__2__Impl"


    // $ANTLR start "rule__ShapeLayout__Group__3"
    // InternalElkGraph.g:1570:1: rule__ShapeLayout__Group__3 : rule__ShapeLayout__Group__3__Impl ;
    public final void rule__ShapeLayout__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1574:1: ( rule__ShapeLayout__Group__3__Impl )
            // InternalElkGraph.g:1575:2: rule__ShapeLayout__Group__3__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ShapeLayout__Group__3__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__Group__3"


    // $ANTLR start "rule__ShapeLayout__Group__3__Impl"
    // InternalElkGraph.g:1581:1: rule__ShapeLayout__Group__3__Impl : ( ']' ) ;
    public final void rule__ShapeLayout__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1585:1: ( ( ']' ) )
            // InternalElkGraph.g:1586:1: ( ']' )
            {
            // InternalElkGraph.g:1586:1: ( ']' )
            // InternalElkGraph.g:1587:2: ']'
            {
             before(grammarAccess.getShapeLayoutAccess().getRightSquareBracketKeyword_3()); 
            match(input,24,FOLLOW_2); 
             after(grammarAccess.getShapeLayoutAccess().getRightSquareBracketKeyword_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__Group__3__Impl"


    // $ANTLR start "rule__ShapeLayout__Group_2_0__0"
    // InternalElkGraph.g:1597:1: rule__ShapeLayout__Group_2_0__0 : rule__ShapeLayout__Group_2_0__0__Impl rule__ShapeLayout__Group_2_0__1 ;
    public final void rule__ShapeLayout__Group_2_0__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1601:1: ( rule__ShapeLayout__Group_2_0__0__Impl rule__ShapeLayout__Group_2_0__1 )
            // InternalElkGraph.g:1602:2: rule__ShapeLayout__Group_2_0__0__Impl rule__ShapeLayout__Group_2_0__1
            {
            pushFollow(FOLLOW_11);
            rule__ShapeLayout__Group_2_0__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ShapeLayout__Group_2_0__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__Group_2_0__0"


    // $ANTLR start "rule__ShapeLayout__Group_2_0__0__Impl"
    // InternalElkGraph.g:1609:1: rule__ShapeLayout__Group_2_0__0__Impl : ( 'position' ) ;
    public final void rule__ShapeLayout__Group_2_0__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1613:1: ( ( 'position' ) )
            // InternalElkGraph.g:1614:1: ( 'position' )
            {
            // InternalElkGraph.g:1614:1: ( 'position' )
            // InternalElkGraph.g:1615:2: 'position'
            {
             before(grammarAccess.getShapeLayoutAccess().getPositionKeyword_2_0_0()); 
            match(input,25,FOLLOW_2); 
             after(grammarAccess.getShapeLayoutAccess().getPositionKeyword_2_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__Group_2_0__0__Impl"


    // $ANTLR start "rule__ShapeLayout__Group_2_0__1"
    // InternalElkGraph.g:1624:1: rule__ShapeLayout__Group_2_0__1 : rule__ShapeLayout__Group_2_0__1__Impl rule__ShapeLayout__Group_2_0__2 ;
    public final void rule__ShapeLayout__Group_2_0__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1628:1: ( rule__ShapeLayout__Group_2_0__1__Impl rule__ShapeLayout__Group_2_0__2 )
            // InternalElkGraph.g:1629:2: rule__ShapeLayout__Group_2_0__1__Impl rule__ShapeLayout__Group_2_0__2
            {
            pushFollow(FOLLOW_15);
            rule__ShapeLayout__Group_2_0__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ShapeLayout__Group_2_0__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__Group_2_0__1"


    // $ANTLR start "rule__ShapeLayout__Group_2_0__1__Impl"
    // InternalElkGraph.g:1636:1: rule__ShapeLayout__Group_2_0__1__Impl : ( ':' ) ;
    public final void rule__ShapeLayout__Group_2_0__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1640:1: ( ( ':' ) )
            // InternalElkGraph.g:1641:1: ( ':' )
            {
            // InternalElkGraph.g:1641:1: ( ':' )
            // InternalElkGraph.g:1642:2: ':'
            {
             before(grammarAccess.getShapeLayoutAccess().getColonKeyword_2_0_1()); 
            match(input,20,FOLLOW_2); 
             after(grammarAccess.getShapeLayoutAccess().getColonKeyword_2_0_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__Group_2_0__1__Impl"


    // $ANTLR start "rule__ShapeLayout__Group_2_0__2"
    // InternalElkGraph.g:1651:1: rule__ShapeLayout__Group_2_0__2 : rule__ShapeLayout__Group_2_0__2__Impl rule__ShapeLayout__Group_2_0__3 ;
    public final void rule__ShapeLayout__Group_2_0__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1655:1: ( rule__ShapeLayout__Group_2_0__2__Impl rule__ShapeLayout__Group_2_0__3 )
            // InternalElkGraph.g:1656:2: rule__ShapeLayout__Group_2_0__2__Impl rule__ShapeLayout__Group_2_0__3
            {
            pushFollow(FOLLOW_16);
            rule__ShapeLayout__Group_2_0__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ShapeLayout__Group_2_0__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__Group_2_0__2"


    // $ANTLR start "rule__ShapeLayout__Group_2_0__2__Impl"
    // InternalElkGraph.g:1663:1: rule__ShapeLayout__Group_2_0__2__Impl : ( ( rule__ShapeLayout__XAssignment_2_0_2 ) ) ;
    public final void rule__ShapeLayout__Group_2_0__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1667:1: ( ( ( rule__ShapeLayout__XAssignment_2_0_2 ) ) )
            // InternalElkGraph.g:1668:1: ( ( rule__ShapeLayout__XAssignment_2_0_2 ) )
            {
            // InternalElkGraph.g:1668:1: ( ( rule__ShapeLayout__XAssignment_2_0_2 ) )
            // InternalElkGraph.g:1669:2: ( rule__ShapeLayout__XAssignment_2_0_2 )
            {
             before(grammarAccess.getShapeLayoutAccess().getXAssignment_2_0_2()); 
            // InternalElkGraph.g:1670:2: ( rule__ShapeLayout__XAssignment_2_0_2 )
            // InternalElkGraph.g:1670:3: rule__ShapeLayout__XAssignment_2_0_2
            {
            pushFollow(FOLLOW_2);
            rule__ShapeLayout__XAssignment_2_0_2();

            state._fsp--;


            }

             after(grammarAccess.getShapeLayoutAccess().getXAssignment_2_0_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__Group_2_0__2__Impl"


    // $ANTLR start "rule__ShapeLayout__Group_2_0__3"
    // InternalElkGraph.g:1678:1: rule__ShapeLayout__Group_2_0__3 : rule__ShapeLayout__Group_2_0__3__Impl rule__ShapeLayout__Group_2_0__4 ;
    public final void rule__ShapeLayout__Group_2_0__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1682:1: ( rule__ShapeLayout__Group_2_0__3__Impl rule__ShapeLayout__Group_2_0__4 )
            // InternalElkGraph.g:1683:2: rule__ShapeLayout__Group_2_0__3__Impl rule__ShapeLayout__Group_2_0__4
            {
            pushFollow(FOLLOW_15);
            rule__ShapeLayout__Group_2_0__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ShapeLayout__Group_2_0__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__Group_2_0__3"


    // $ANTLR start "rule__ShapeLayout__Group_2_0__3__Impl"
    // InternalElkGraph.g:1690:1: rule__ShapeLayout__Group_2_0__3__Impl : ( ',' ) ;
    public final void rule__ShapeLayout__Group_2_0__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1694:1: ( ( ',' ) )
            // InternalElkGraph.g:1695:1: ( ',' )
            {
            // InternalElkGraph.g:1695:1: ( ',' )
            // InternalElkGraph.g:1696:2: ','
            {
             before(grammarAccess.getShapeLayoutAccess().getCommaKeyword_2_0_3()); 
            match(input,26,FOLLOW_2); 
             after(grammarAccess.getShapeLayoutAccess().getCommaKeyword_2_0_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__Group_2_0__3__Impl"


    // $ANTLR start "rule__ShapeLayout__Group_2_0__4"
    // InternalElkGraph.g:1705:1: rule__ShapeLayout__Group_2_0__4 : rule__ShapeLayout__Group_2_0__4__Impl ;
    public final void rule__ShapeLayout__Group_2_0__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1709:1: ( rule__ShapeLayout__Group_2_0__4__Impl )
            // InternalElkGraph.g:1710:2: rule__ShapeLayout__Group_2_0__4__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ShapeLayout__Group_2_0__4__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__Group_2_0__4"


    // $ANTLR start "rule__ShapeLayout__Group_2_0__4__Impl"
    // InternalElkGraph.g:1716:1: rule__ShapeLayout__Group_2_0__4__Impl : ( ( rule__ShapeLayout__YAssignment_2_0_4 ) ) ;
    public final void rule__ShapeLayout__Group_2_0__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1720:1: ( ( ( rule__ShapeLayout__YAssignment_2_0_4 ) ) )
            // InternalElkGraph.g:1721:1: ( ( rule__ShapeLayout__YAssignment_2_0_4 ) )
            {
            // InternalElkGraph.g:1721:1: ( ( rule__ShapeLayout__YAssignment_2_0_4 ) )
            // InternalElkGraph.g:1722:2: ( rule__ShapeLayout__YAssignment_2_0_4 )
            {
             before(grammarAccess.getShapeLayoutAccess().getYAssignment_2_0_4()); 
            // InternalElkGraph.g:1723:2: ( rule__ShapeLayout__YAssignment_2_0_4 )
            // InternalElkGraph.g:1723:3: rule__ShapeLayout__YAssignment_2_0_4
            {
            pushFollow(FOLLOW_2);
            rule__ShapeLayout__YAssignment_2_0_4();

            state._fsp--;


            }

             after(grammarAccess.getShapeLayoutAccess().getYAssignment_2_0_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__Group_2_0__4__Impl"


    // $ANTLR start "rule__ShapeLayout__Group_2_1__0"
    // InternalElkGraph.g:1732:1: rule__ShapeLayout__Group_2_1__0 : rule__ShapeLayout__Group_2_1__0__Impl rule__ShapeLayout__Group_2_1__1 ;
    public final void rule__ShapeLayout__Group_2_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1736:1: ( rule__ShapeLayout__Group_2_1__0__Impl rule__ShapeLayout__Group_2_1__1 )
            // InternalElkGraph.g:1737:2: rule__ShapeLayout__Group_2_1__0__Impl rule__ShapeLayout__Group_2_1__1
            {
            pushFollow(FOLLOW_11);
            rule__ShapeLayout__Group_2_1__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ShapeLayout__Group_2_1__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__Group_2_1__0"


    // $ANTLR start "rule__ShapeLayout__Group_2_1__0__Impl"
    // InternalElkGraph.g:1744:1: rule__ShapeLayout__Group_2_1__0__Impl : ( 'size' ) ;
    public final void rule__ShapeLayout__Group_2_1__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1748:1: ( ( 'size' ) )
            // InternalElkGraph.g:1749:1: ( 'size' )
            {
            // InternalElkGraph.g:1749:1: ( 'size' )
            // InternalElkGraph.g:1750:2: 'size'
            {
             before(grammarAccess.getShapeLayoutAccess().getSizeKeyword_2_1_0()); 
            match(input,27,FOLLOW_2); 
             after(grammarAccess.getShapeLayoutAccess().getSizeKeyword_2_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__Group_2_1__0__Impl"


    // $ANTLR start "rule__ShapeLayout__Group_2_1__1"
    // InternalElkGraph.g:1759:1: rule__ShapeLayout__Group_2_1__1 : rule__ShapeLayout__Group_2_1__1__Impl rule__ShapeLayout__Group_2_1__2 ;
    public final void rule__ShapeLayout__Group_2_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1763:1: ( rule__ShapeLayout__Group_2_1__1__Impl rule__ShapeLayout__Group_2_1__2 )
            // InternalElkGraph.g:1764:2: rule__ShapeLayout__Group_2_1__1__Impl rule__ShapeLayout__Group_2_1__2
            {
            pushFollow(FOLLOW_15);
            rule__ShapeLayout__Group_2_1__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ShapeLayout__Group_2_1__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__Group_2_1__1"


    // $ANTLR start "rule__ShapeLayout__Group_2_1__1__Impl"
    // InternalElkGraph.g:1771:1: rule__ShapeLayout__Group_2_1__1__Impl : ( ':' ) ;
    public final void rule__ShapeLayout__Group_2_1__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1775:1: ( ( ':' ) )
            // InternalElkGraph.g:1776:1: ( ':' )
            {
            // InternalElkGraph.g:1776:1: ( ':' )
            // InternalElkGraph.g:1777:2: ':'
            {
             before(grammarAccess.getShapeLayoutAccess().getColonKeyword_2_1_1()); 
            match(input,20,FOLLOW_2); 
             after(grammarAccess.getShapeLayoutAccess().getColonKeyword_2_1_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__Group_2_1__1__Impl"


    // $ANTLR start "rule__ShapeLayout__Group_2_1__2"
    // InternalElkGraph.g:1786:1: rule__ShapeLayout__Group_2_1__2 : rule__ShapeLayout__Group_2_1__2__Impl rule__ShapeLayout__Group_2_1__3 ;
    public final void rule__ShapeLayout__Group_2_1__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1790:1: ( rule__ShapeLayout__Group_2_1__2__Impl rule__ShapeLayout__Group_2_1__3 )
            // InternalElkGraph.g:1791:2: rule__ShapeLayout__Group_2_1__2__Impl rule__ShapeLayout__Group_2_1__3
            {
            pushFollow(FOLLOW_16);
            rule__ShapeLayout__Group_2_1__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ShapeLayout__Group_2_1__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__Group_2_1__2"


    // $ANTLR start "rule__ShapeLayout__Group_2_1__2__Impl"
    // InternalElkGraph.g:1798:1: rule__ShapeLayout__Group_2_1__2__Impl : ( ( rule__ShapeLayout__WidthAssignment_2_1_2 ) ) ;
    public final void rule__ShapeLayout__Group_2_1__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1802:1: ( ( ( rule__ShapeLayout__WidthAssignment_2_1_2 ) ) )
            // InternalElkGraph.g:1803:1: ( ( rule__ShapeLayout__WidthAssignment_2_1_2 ) )
            {
            // InternalElkGraph.g:1803:1: ( ( rule__ShapeLayout__WidthAssignment_2_1_2 ) )
            // InternalElkGraph.g:1804:2: ( rule__ShapeLayout__WidthAssignment_2_1_2 )
            {
             before(grammarAccess.getShapeLayoutAccess().getWidthAssignment_2_1_2()); 
            // InternalElkGraph.g:1805:2: ( rule__ShapeLayout__WidthAssignment_2_1_2 )
            // InternalElkGraph.g:1805:3: rule__ShapeLayout__WidthAssignment_2_1_2
            {
            pushFollow(FOLLOW_2);
            rule__ShapeLayout__WidthAssignment_2_1_2();

            state._fsp--;


            }

             after(grammarAccess.getShapeLayoutAccess().getWidthAssignment_2_1_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__Group_2_1__2__Impl"


    // $ANTLR start "rule__ShapeLayout__Group_2_1__3"
    // InternalElkGraph.g:1813:1: rule__ShapeLayout__Group_2_1__3 : rule__ShapeLayout__Group_2_1__3__Impl rule__ShapeLayout__Group_2_1__4 ;
    public final void rule__ShapeLayout__Group_2_1__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1817:1: ( rule__ShapeLayout__Group_2_1__3__Impl rule__ShapeLayout__Group_2_1__4 )
            // InternalElkGraph.g:1818:2: rule__ShapeLayout__Group_2_1__3__Impl rule__ShapeLayout__Group_2_1__4
            {
            pushFollow(FOLLOW_15);
            rule__ShapeLayout__Group_2_1__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ShapeLayout__Group_2_1__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__Group_2_1__3"


    // $ANTLR start "rule__ShapeLayout__Group_2_1__3__Impl"
    // InternalElkGraph.g:1825:1: rule__ShapeLayout__Group_2_1__3__Impl : ( ',' ) ;
    public final void rule__ShapeLayout__Group_2_1__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1829:1: ( ( ',' ) )
            // InternalElkGraph.g:1830:1: ( ',' )
            {
            // InternalElkGraph.g:1830:1: ( ',' )
            // InternalElkGraph.g:1831:2: ','
            {
             before(grammarAccess.getShapeLayoutAccess().getCommaKeyword_2_1_3()); 
            match(input,26,FOLLOW_2); 
             after(grammarAccess.getShapeLayoutAccess().getCommaKeyword_2_1_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__Group_2_1__3__Impl"


    // $ANTLR start "rule__ShapeLayout__Group_2_1__4"
    // InternalElkGraph.g:1840:1: rule__ShapeLayout__Group_2_1__4 : rule__ShapeLayout__Group_2_1__4__Impl ;
    public final void rule__ShapeLayout__Group_2_1__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1844:1: ( rule__ShapeLayout__Group_2_1__4__Impl )
            // InternalElkGraph.g:1845:2: rule__ShapeLayout__Group_2_1__4__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ShapeLayout__Group_2_1__4__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__Group_2_1__4"


    // $ANTLR start "rule__ShapeLayout__Group_2_1__4__Impl"
    // InternalElkGraph.g:1851:1: rule__ShapeLayout__Group_2_1__4__Impl : ( ( rule__ShapeLayout__HeightAssignment_2_1_4 ) ) ;
    public final void rule__ShapeLayout__Group_2_1__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1855:1: ( ( ( rule__ShapeLayout__HeightAssignment_2_1_4 ) ) )
            // InternalElkGraph.g:1856:1: ( ( rule__ShapeLayout__HeightAssignment_2_1_4 ) )
            {
            // InternalElkGraph.g:1856:1: ( ( rule__ShapeLayout__HeightAssignment_2_1_4 ) )
            // InternalElkGraph.g:1857:2: ( rule__ShapeLayout__HeightAssignment_2_1_4 )
            {
             before(grammarAccess.getShapeLayoutAccess().getHeightAssignment_2_1_4()); 
            // InternalElkGraph.g:1858:2: ( rule__ShapeLayout__HeightAssignment_2_1_4 )
            // InternalElkGraph.g:1858:3: rule__ShapeLayout__HeightAssignment_2_1_4
            {
            pushFollow(FOLLOW_2);
            rule__ShapeLayout__HeightAssignment_2_1_4();

            state._fsp--;


            }

             after(grammarAccess.getShapeLayoutAccess().getHeightAssignment_2_1_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__Group_2_1__4__Impl"


    // $ANTLR start "rule__ElkEdge__Group__0"
    // InternalElkGraph.g:1867:1: rule__ElkEdge__Group__0 : rule__ElkEdge__Group__0__Impl rule__ElkEdge__Group__1 ;
    public final void rule__ElkEdge__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1871:1: ( rule__ElkEdge__Group__0__Impl rule__ElkEdge__Group__1 )
            // InternalElkGraph.g:1872:2: rule__ElkEdge__Group__0__Impl rule__ElkEdge__Group__1
            {
            pushFollow(FOLLOW_7);
            rule__ElkEdge__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdge__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group__0"


    // $ANTLR start "rule__ElkEdge__Group__0__Impl"
    // InternalElkGraph.g:1879:1: rule__ElkEdge__Group__0__Impl : ( 'edge' ) ;
    public final void rule__ElkEdge__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1883:1: ( ( 'edge' ) )
            // InternalElkGraph.g:1884:1: ( 'edge' )
            {
            // InternalElkGraph.g:1884:1: ( 'edge' )
            // InternalElkGraph.g:1885:2: 'edge'
            {
             before(grammarAccess.getElkEdgeAccess().getEdgeKeyword_0()); 
            match(input,28,FOLLOW_2); 
             after(grammarAccess.getElkEdgeAccess().getEdgeKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group__0__Impl"


    // $ANTLR start "rule__ElkEdge__Group__1"
    // InternalElkGraph.g:1894:1: rule__ElkEdge__Group__1 : rule__ElkEdge__Group__1__Impl rule__ElkEdge__Group__2 ;
    public final void rule__ElkEdge__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1898:1: ( rule__ElkEdge__Group__1__Impl rule__ElkEdge__Group__2 )
            // InternalElkGraph.g:1899:2: rule__ElkEdge__Group__1__Impl rule__ElkEdge__Group__2
            {
            pushFollow(FOLLOW_7);
            rule__ElkEdge__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdge__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group__1"


    // $ANTLR start "rule__ElkEdge__Group__1__Impl"
    // InternalElkGraph.g:1906:1: rule__ElkEdge__Group__1__Impl : ( ( rule__ElkEdge__Group_1__0 )? ) ;
    public final void rule__ElkEdge__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1910:1: ( ( ( rule__ElkEdge__Group_1__0 )? ) )
            // InternalElkGraph.g:1911:1: ( ( rule__ElkEdge__Group_1__0 )? )
            {
            // InternalElkGraph.g:1911:1: ( ( rule__ElkEdge__Group_1__0 )? )
            // InternalElkGraph.g:1912:2: ( rule__ElkEdge__Group_1__0 )?
            {
             before(grammarAccess.getElkEdgeAccess().getGroup_1()); 
            // InternalElkGraph.g:1913:2: ( rule__ElkEdge__Group_1__0 )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==RULE_ID) ) {
                int LA24_1 = input.LA(2);

                if ( (LA24_1==20) ) {
                    alt24=1;
                }
            }
            switch (alt24) {
                case 1 :
                    // InternalElkGraph.g:1913:3: rule__ElkEdge__Group_1__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkEdge__Group_1__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getElkEdgeAccess().getGroup_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group__1__Impl"


    // $ANTLR start "rule__ElkEdge__Group__2"
    // InternalElkGraph.g:1921:1: rule__ElkEdge__Group__2 : rule__ElkEdge__Group__2__Impl rule__ElkEdge__Group__3 ;
    public final void rule__ElkEdge__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1925:1: ( rule__ElkEdge__Group__2__Impl rule__ElkEdge__Group__3 )
            // InternalElkGraph.g:1926:2: rule__ElkEdge__Group__2__Impl rule__ElkEdge__Group__3
            {
            pushFollow(FOLLOW_17);
            rule__ElkEdge__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdge__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group__2"


    // $ANTLR start "rule__ElkEdge__Group__2__Impl"
    // InternalElkGraph.g:1933:1: rule__ElkEdge__Group__2__Impl : ( ( rule__ElkEdge__SourcesAssignment_2 ) ) ;
    public final void rule__ElkEdge__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1937:1: ( ( ( rule__ElkEdge__SourcesAssignment_2 ) ) )
            // InternalElkGraph.g:1938:1: ( ( rule__ElkEdge__SourcesAssignment_2 ) )
            {
            // InternalElkGraph.g:1938:1: ( ( rule__ElkEdge__SourcesAssignment_2 ) )
            // InternalElkGraph.g:1939:2: ( rule__ElkEdge__SourcesAssignment_2 )
            {
             before(grammarAccess.getElkEdgeAccess().getSourcesAssignment_2()); 
            // InternalElkGraph.g:1940:2: ( rule__ElkEdge__SourcesAssignment_2 )
            // InternalElkGraph.g:1940:3: rule__ElkEdge__SourcesAssignment_2
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdge__SourcesAssignment_2();

            state._fsp--;


            }

             after(grammarAccess.getElkEdgeAccess().getSourcesAssignment_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group__2__Impl"


    // $ANTLR start "rule__ElkEdge__Group__3"
    // InternalElkGraph.g:1948:1: rule__ElkEdge__Group__3 : rule__ElkEdge__Group__3__Impl rule__ElkEdge__Group__4 ;
    public final void rule__ElkEdge__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1952:1: ( rule__ElkEdge__Group__3__Impl rule__ElkEdge__Group__4 )
            // InternalElkGraph.g:1953:2: rule__ElkEdge__Group__3__Impl rule__ElkEdge__Group__4
            {
            pushFollow(FOLLOW_17);
            rule__ElkEdge__Group__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdge__Group__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group__3"


    // $ANTLR start "rule__ElkEdge__Group__3__Impl"
    // InternalElkGraph.g:1960:1: rule__ElkEdge__Group__3__Impl : ( ( rule__ElkEdge__Group_3__0 )* ) ;
    public final void rule__ElkEdge__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1964:1: ( ( ( rule__ElkEdge__Group_3__0 )* ) )
            // InternalElkGraph.g:1965:1: ( ( rule__ElkEdge__Group_3__0 )* )
            {
            // InternalElkGraph.g:1965:1: ( ( rule__ElkEdge__Group_3__0 )* )
            // InternalElkGraph.g:1966:2: ( rule__ElkEdge__Group_3__0 )*
            {
             before(grammarAccess.getElkEdgeAccess().getGroup_3()); 
            // InternalElkGraph.g:1967:2: ( rule__ElkEdge__Group_3__0 )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0==26) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // InternalElkGraph.g:1967:3: rule__ElkEdge__Group_3__0
            	    {
            	    pushFollow(FOLLOW_18);
            	    rule__ElkEdge__Group_3__0();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop25;
                }
            } while (true);

             after(grammarAccess.getElkEdgeAccess().getGroup_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group__3__Impl"


    // $ANTLR start "rule__ElkEdge__Group__4"
    // InternalElkGraph.g:1975:1: rule__ElkEdge__Group__4 : rule__ElkEdge__Group__4__Impl rule__ElkEdge__Group__5 ;
    public final void rule__ElkEdge__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1979:1: ( rule__ElkEdge__Group__4__Impl rule__ElkEdge__Group__5 )
            // InternalElkGraph.g:1980:2: rule__ElkEdge__Group__4__Impl rule__ElkEdge__Group__5
            {
            pushFollow(FOLLOW_7);
            rule__ElkEdge__Group__4__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdge__Group__5();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group__4"


    // $ANTLR start "rule__ElkEdge__Group__4__Impl"
    // InternalElkGraph.g:1987:1: rule__ElkEdge__Group__4__Impl : ( '->' ) ;
    public final void rule__ElkEdge__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1991:1: ( ( '->' ) )
            // InternalElkGraph.g:1992:1: ( '->' )
            {
            // InternalElkGraph.g:1992:1: ( '->' )
            // InternalElkGraph.g:1993:2: '->'
            {
             before(grammarAccess.getElkEdgeAccess().getHyphenMinusGreaterThanSignKeyword_4()); 
            match(input,29,FOLLOW_2); 
             after(grammarAccess.getElkEdgeAccess().getHyphenMinusGreaterThanSignKeyword_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group__4__Impl"


    // $ANTLR start "rule__ElkEdge__Group__5"
    // InternalElkGraph.g:2002:1: rule__ElkEdge__Group__5 : rule__ElkEdge__Group__5__Impl rule__ElkEdge__Group__6 ;
    public final void rule__ElkEdge__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2006:1: ( rule__ElkEdge__Group__5__Impl rule__ElkEdge__Group__6 )
            // InternalElkGraph.g:2007:2: rule__ElkEdge__Group__5__Impl rule__ElkEdge__Group__6
            {
            pushFollow(FOLLOW_19);
            rule__ElkEdge__Group__5__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdge__Group__6();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group__5"


    // $ANTLR start "rule__ElkEdge__Group__5__Impl"
    // InternalElkGraph.g:2014:1: rule__ElkEdge__Group__5__Impl : ( ( rule__ElkEdge__TargetsAssignment_5 ) ) ;
    public final void rule__ElkEdge__Group__5__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2018:1: ( ( ( rule__ElkEdge__TargetsAssignment_5 ) ) )
            // InternalElkGraph.g:2019:1: ( ( rule__ElkEdge__TargetsAssignment_5 ) )
            {
            // InternalElkGraph.g:2019:1: ( ( rule__ElkEdge__TargetsAssignment_5 ) )
            // InternalElkGraph.g:2020:2: ( rule__ElkEdge__TargetsAssignment_5 )
            {
             before(grammarAccess.getElkEdgeAccess().getTargetsAssignment_5()); 
            // InternalElkGraph.g:2021:2: ( rule__ElkEdge__TargetsAssignment_5 )
            // InternalElkGraph.g:2021:3: rule__ElkEdge__TargetsAssignment_5
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdge__TargetsAssignment_5();

            state._fsp--;


            }

             after(grammarAccess.getElkEdgeAccess().getTargetsAssignment_5()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group__5__Impl"


    // $ANTLR start "rule__ElkEdge__Group__6"
    // InternalElkGraph.g:2029:1: rule__ElkEdge__Group__6 : rule__ElkEdge__Group__6__Impl rule__ElkEdge__Group__7 ;
    public final void rule__ElkEdge__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2033:1: ( rule__ElkEdge__Group__6__Impl rule__ElkEdge__Group__7 )
            // InternalElkGraph.g:2034:2: rule__ElkEdge__Group__6__Impl rule__ElkEdge__Group__7
            {
            pushFollow(FOLLOW_19);
            rule__ElkEdge__Group__6__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdge__Group__7();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group__6"


    // $ANTLR start "rule__ElkEdge__Group__6__Impl"
    // InternalElkGraph.g:2041:1: rule__ElkEdge__Group__6__Impl : ( ( rule__ElkEdge__Group_6__0 )* ) ;
    public final void rule__ElkEdge__Group__6__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2045:1: ( ( ( rule__ElkEdge__Group_6__0 )* ) )
            // InternalElkGraph.g:2046:1: ( ( rule__ElkEdge__Group_6__0 )* )
            {
            // InternalElkGraph.g:2046:1: ( ( rule__ElkEdge__Group_6__0 )* )
            // InternalElkGraph.g:2047:2: ( rule__ElkEdge__Group_6__0 )*
            {
             before(grammarAccess.getElkEdgeAccess().getGroup_6()); 
            // InternalElkGraph.g:2048:2: ( rule__ElkEdge__Group_6__0 )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( (LA26_0==26) ) {
                    alt26=1;
                }


                switch (alt26) {
            	case 1 :
            	    // InternalElkGraph.g:2048:3: rule__ElkEdge__Group_6__0
            	    {
            	    pushFollow(FOLLOW_18);
            	    rule__ElkEdge__Group_6__0();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop26;
                }
            } while (true);

             after(grammarAccess.getElkEdgeAccess().getGroup_6()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group__6__Impl"


    // $ANTLR start "rule__ElkEdge__Group__7"
    // InternalElkGraph.g:2056:1: rule__ElkEdge__Group__7 : rule__ElkEdge__Group__7__Impl ;
    public final void rule__ElkEdge__Group__7() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2060:1: ( rule__ElkEdge__Group__7__Impl )
            // InternalElkGraph.g:2061:2: rule__ElkEdge__Group__7__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdge__Group__7__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group__7"


    // $ANTLR start "rule__ElkEdge__Group__7__Impl"
    // InternalElkGraph.g:2067:1: rule__ElkEdge__Group__7__Impl : ( ( rule__ElkEdge__Group_7__0 )? ) ;
    public final void rule__ElkEdge__Group__7__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2071:1: ( ( ( rule__ElkEdge__Group_7__0 )? ) )
            // InternalElkGraph.g:2072:1: ( ( rule__ElkEdge__Group_7__0 )? )
            {
            // InternalElkGraph.g:2072:1: ( ( rule__ElkEdge__Group_7__0 )? )
            // InternalElkGraph.g:2073:2: ( rule__ElkEdge__Group_7__0 )?
            {
             before(grammarAccess.getElkEdgeAccess().getGroup_7()); 
            // InternalElkGraph.g:2074:2: ( rule__ElkEdge__Group_7__0 )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==17) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // InternalElkGraph.g:2074:3: rule__ElkEdge__Group_7__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkEdge__Group_7__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getElkEdgeAccess().getGroup_7()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group__7__Impl"


    // $ANTLR start "rule__ElkEdge__Group_1__0"
    // InternalElkGraph.g:2083:1: rule__ElkEdge__Group_1__0 : rule__ElkEdge__Group_1__0__Impl rule__ElkEdge__Group_1__1 ;
    public final void rule__ElkEdge__Group_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2087:1: ( rule__ElkEdge__Group_1__0__Impl rule__ElkEdge__Group_1__1 )
            // InternalElkGraph.g:2088:2: rule__ElkEdge__Group_1__0__Impl rule__ElkEdge__Group_1__1
            {
            pushFollow(FOLLOW_11);
            rule__ElkEdge__Group_1__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdge__Group_1__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group_1__0"


    // $ANTLR start "rule__ElkEdge__Group_1__0__Impl"
    // InternalElkGraph.g:2095:1: rule__ElkEdge__Group_1__0__Impl : ( ( rule__ElkEdge__IdentifierAssignment_1_0 ) ) ;
    public final void rule__ElkEdge__Group_1__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2099:1: ( ( ( rule__ElkEdge__IdentifierAssignment_1_0 ) ) )
            // InternalElkGraph.g:2100:1: ( ( rule__ElkEdge__IdentifierAssignment_1_0 ) )
            {
            // InternalElkGraph.g:2100:1: ( ( rule__ElkEdge__IdentifierAssignment_1_0 ) )
            // InternalElkGraph.g:2101:2: ( rule__ElkEdge__IdentifierAssignment_1_0 )
            {
             before(grammarAccess.getElkEdgeAccess().getIdentifierAssignment_1_0()); 
            // InternalElkGraph.g:2102:2: ( rule__ElkEdge__IdentifierAssignment_1_0 )
            // InternalElkGraph.g:2102:3: rule__ElkEdge__IdentifierAssignment_1_0
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdge__IdentifierAssignment_1_0();

            state._fsp--;


            }

             after(grammarAccess.getElkEdgeAccess().getIdentifierAssignment_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group_1__0__Impl"


    // $ANTLR start "rule__ElkEdge__Group_1__1"
    // InternalElkGraph.g:2110:1: rule__ElkEdge__Group_1__1 : rule__ElkEdge__Group_1__1__Impl ;
    public final void rule__ElkEdge__Group_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2114:1: ( rule__ElkEdge__Group_1__1__Impl )
            // InternalElkGraph.g:2115:2: rule__ElkEdge__Group_1__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdge__Group_1__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group_1__1"


    // $ANTLR start "rule__ElkEdge__Group_1__1__Impl"
    // InternalElkGraph.g:2121:1: rule__ElkEdge__Group_1__1__Impl : ( ':' ) ;
    public final void rule__ElkEdge__Group_1__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2125:1: ( ( ':' ) )
            // InternalElkGraph.g:2126:1: ( ':' )
            {
            // InternalElkGraph.g:2126:1: ( ':' )
            // InternalElkGraph.g:2127:2: ':'
            {
             before(grammarAccess.getElkEdgeAccess().getColonKeyword_1_1()); 
            match(input,20,FOLLOW_2); 
             after(grammarAccess.getElkEdgeAccess().getColonKeyword_1_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group_1__1__Impl"


    // $ANTLR start "rule__ElkEdge__Group_3__0"
    // InternalElkGraph.g:2137:1: rule__ElkEdge__Group_3__0 : rule__ElkEdge__Group_3__0__Impl rule__ElkEdge__Group_3__1 ;
    public final void rule__ElkEdge__Group_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2141:1: ( rule__ElkEdge__Group_3__0__Impl rule__ElkEdge__Group_3__1 )
            // InternalElkGraph.g:2142:2: rule__ElkEdge__Group_3__0__Impl rule__ElkEdge__Group_3__1
            {
            pushFollow(FOLLOW_7);
            rule__ElkEdge__Group_3__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdge__Group_3__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group_3__0"


    // $ANTLR start "rule__ElkEdge__Group_3__0__Impl"
    // InternalElkGraph.g:2149:1: rule__ElkEdge__Group_3__0__Impl : ( ',' ) ;
    public final void rule__ElkEdge__Group_3__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2153:1: ( ( ',' ) )
            // InternalElkGraph.g:2154:1: ( ',' )
            {
            // InternalElkGraph.g:2154:1: ( ',' )
            // InternalElkGraph.g:2155:2: ','
            {
             before(grammarAccess.getElkEdgeAccess().getCommaKeyword_3_0()); 
            match(input,26,FOLLOW_2); 
             after(grammarAccess.getElkEdgeAccess().getCommaKeyword_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group_3__0__Impl"


    // $ANTLR start "rule__ElkEdge__Group_3__1"
    // InternalElkGraph.g:2164:1: rule__ElkEdge__Group_3__1 : rule__ElkEdge__Group_3__1__Impl ;
    public final void rule__ElkEdge__Group_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2168:1: ( rule__ElkEdge__Group_3__1__Impl )
            // InternalElkGraph.g:2169:2: rule__ElkEdge__Group_3__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdge__Group_3__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group_3__1"


    // $ANTLR start "rule__ElkEdge__Group_3__1__Impl"
    // InternalElkGraph.g:2175:1: rule__ElkEdge__Group_3__1__Impl : ( ( rule__ElkEdge__SourcesAssignment_3_1 ) ) ;
    public final void rule__ElkEdge__Group_3__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2179:1: ( ( ( rule__ElkEdge__SourcesAssignment_3_1 ) ) )
            // InternalElkGraph.g:2180:1: ( ( rule__ElkEdge__SourcesAssignment_3_1 ) )
            {
            // InternalElkGraph.g:2180:1: ( ( rule__ElkEdge__SourcesAssignment_3_1 ) )
            // InternalElkGraph.g:2181:2: ( rule__ElkEdge__SourcesAssignment_3_1 )
            {
             before(grammarAccess.getElkEdgeAccess().getSourcesAssignment_3_1()); 
            // InternalElkGraph.g:2182:2: ( rule__ElkEdge__SourcesAssignment_3_1 )
            // InternalElkGraph.g:2182:3: rule__ElkEdge__SourcesAssignment_3_1
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdge__SourcesAssignment_3_1();

            state._fsp--;


            }

             after(grammarAccess.getElkEdgeAccess().getSourcesAssignment_3_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group_3__1__Impl"


    // $ANTLR start "rule__ElkEdge__Group_6__0"
    // InternalElkGraph.g:2191:1: rule__ElkEdge__Group_6__0 : rule__ElkEdge__Group_6__0__Impl rule__ElkEdge__Group_6__1 ;
    public final void rule__ElkEdge__Group_6__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2195:1: ( rule__ElkEdge__Group_6__0__Impl rule__ElkEdge__Group_6__1 )
            // InternalElkGraph.g:2196:2: rule__ElkEdge__Group_6__0__Impl rule__ElkEdge__Group_6__1
            {
            pushFollow(FOLLOW_7);
            rule__ElkEdge__Group_6__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdge__Group_6__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group_6__0"


    // $ANTLR start "rule__ElkEdge__Group_6__0__Impl"
    // InternalElkGraph.g:2203:1: rule__ElkEdge__Group_6__0__Impl : ( ',' ) ;
    public final void rule__ElkEdge__Group_6__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2207:1: ( ( ',' ) )
            // InternalElkGraph.g:2208:1: ( ',' )
            {
            // InternalElkGraph.g:2208:1: ( ',' )
            // InternalElkGraph.g:2209:2: ','
            {
             before(grammarAccess.getElkEdgeAccess().getCommaKeyword_6_0()); 
            match(input,26,FOLLOW_2); 
             after(grammarAccess.getElkEdgeAccess().getCommaKeyword_6_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group_6__0__Impl"


    // $ANTLR start "rule__ElkEdge__Group_6__1"
    // InternalElkGraph.g:2218:1: rule__ElkEdge__Group_6__1 : rule__ElkEdge__Group_6__1__Impl ;
    public final void rule__ElkEdge__Group_6__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2222:1: ( rule__ElkEdge__Group_6__1__Impl )
            // InternalElkGraph.g:2223:2: rule__ElkEdge__Group_6__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdge__Group_6__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group_6__1"


    // $ANTLR start "rule__ElkEdge__Group_6__1__Impl"
    // InternalElkGraph.g:2229:1: rule__ElkEdge__Group_6__1__Impl : ( ( rule__ElkEdge__TargetsAssignment_6_1 ) ) ;
    public final void rule__ElkEdge__Group_6__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2233:1: ( ( ( rule__ElkEdge__TargetsAssignment_6_1 ) ) )
            // InternalElkGraph.g:2234:1: ( ( rule__ElkEdge__TargetsAssignment_6_1 ) )
            {
            // InternalElkGraph.g:2234:1: ( ( rule__ElkEdge__TargetsAssignment_6_1 ) )
            // InternalElkGraph.g:2235:2: ( rule__ElkEdge__TargetsAssignment_6_1 )
            {
             before(grammarAccess.getElkEdgeAccess().getTargetsAssignment_6_1()); 
            // InternalElkGraph.g:2236:2: ( rule__ElkEdge__TargetsAssignment_6_1 )
            // InternalElkGraph.g:2236:3: rule__ElkEdge__TargetsAssignment_6_1
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdge__TargetsAssignment_6_1();

            state._fsp--;


            }

             after(grammarAccess.getElkEdgeAccess().getTargetsAssignment_6_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group_6__1__Impl"


    // $ANTLR start "rule__ElkEdge__Group_7__0"
    // InternalElkGraph.g:2245:1: rule__ElkEdge__Group_7__0 : rule__ElkEdge__Group_7__0__Impl rule__ElkEdge__Group_7__1 ;
    public final void rule__ElkEdge__Group_7__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2249:1: ( rule__ElkEdge__Group_7__0__Impl rule__ElkEdge__Group_7__1 )
            // InternalElkGraph.g:2250:2: rule__ElkEdge__Group_7__0__Impl rule__ElkEdge__Group_7__1
            {
            pushFollow(FOLLOW_9);
            rule__ElkEdge__Group_7__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdge__Group_7__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group_7__0"


    // $ANTLR start "rule__ElkEdge__Group_7__0__Impl"
    // InternalElkGraph.g:2257:1: rule__ElkEdge__Group_7__0__Impl : ( '{' ) ;
    public final void rule__ElkEdge__Group_7__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2261:1: ( ( '{' ) )
            // InternalElkGraph.g:2262:1: ( '{' )
            {
            // InternalElkGraph.g:2262:1: ( '{' )
            // InternalElkGraph.g:2263:2: '{'
            {
             before(grammarAccess.getElkEdgeAccess().getLeftCurlyBracketKeyword_7_0()); 
            match(input,17,FOLLOW_2); 
             after(grammarAccess.getElkEdgeAccess().getLeftCurlyBracketKeyword_7_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group_7__0__Impl"


    // $ANTLR start "rule__ElkEdge__Group_7__1"
    // InternalElkGraph.g:2272:1: rule__ElkEdge__Group_7__1 : rule__ElkEdge__Group_7__1__Impl rule__ElkEdge__Group_7__2 ;
    public final void rule__ElkEdge__Group_7__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2276:1: ( rule__ElkEdge__Group_7__1__Impl rule__ElkEdge__Group_7__2 )
            // InternalElkGraph.g:2277:2: rule__ElkEdge__Group_7__1__Impl rule__ElkEdge__Group_7__2
            {
            pushFollow(FOLLOW_9);
            rule__ElkEdge__Group_7__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdge__Group_7__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group_7__1"


    // $ANTLR start "rule__ElkEdge__Group_7__1__Impl"
    // InternalElkGraph.g:2284:1: rule__ElkEdge__Group_7__1__Impl : ( ( ruleEdgeLayout )? ) ;
    public final void rule__ElkEdge__Group_7__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2288:1: ( ( ( ruleEdgeLayout )? ) )
            // InternalElkGraph.g:2289:1: ( ( ruleEdgeLayout )? )
            {
            // InternalElkGraph.g:2289:1: ( ( ruleEdgeLayout )? )
            // InternalElkGraph.g:2290:2: ( ruleEdgeLayout )?
            {
             before(grammarAccess.getElkEdgeAccess().getEdgeLayoutParserRuleCall_7_1()); 
            // InternalElkGraph.g:2291:2: ( ruleEdgeLayout )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==22) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // InternalElkGraph.g:2291:3: ruleEdgeLayout
                    {
                    pushFollow(FOLLOW_2);
                    ruleEdgeLayout();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getElkEdgeAccess().getEdgeLayoutParserRuleCall_7_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group_7__1__Impl"


    // $ANTLR start "rule__ElkEdge__Group_7__2"
    // InternalElkGraph.g:2299:1: rule__ElkEdge__Group_7__2 : rule__ElkEdge__Group_7__2__Impl rule__ElkEdge__Group_7__3 ;
    public final void rule__ElkEdge__Group_7__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2303:1: ( rule__ElkEdge__Group_7__2__Impl rule__ElkEdge__Group_7__3 )
            // InternalElkGraph.g:2304:2: rule__ElkEdge__Group_7__2__Impl rule__ElkEdge__Group_7__3
            {
            pushFollow(FOLLOW_9);
            rule__ElkEdge__Group_7__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdge__Group_7__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group_7__2"


    // $ANTLR start "rule__ElkEdge__Group_7__2__Impl"
    // InternalElkGraph.g:2311:1: rule__ElkEdge__Group_7__2__Impl : ( ( rule__ElkEdge__PropertiesAssignment_7_2 )* ) ;
    public final void rule__ElkEdge__Group_7__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2315:1: ( ( ( rule__ElkEdge__PropertiesAssignment_7_2 )* ) )
            // InternalElkGraph.g:2316:1: ( ( rule__ElkEdge__PropertiesAssignment_7_2 )* )
            {
            // InternalElkGraph.g:2316:1: ( ( rule__ElkEdge__PropertiesAssignment_7_2 )* )
            // InternalElkGraph.g:2317:2: ( rule__ElkEdge__PropertiesAssignment_7_2 )*
            {
             before(grammarAccess.getElkEdgeAccess().getPropertiesAssignment_7_2()); 
            // InternalElkGraph.g:2318:2: ( rule__ElkEdge__PropertiesAssignment_7_2 )*
            loop29:
            do {
                int alt29=2;
                int LA29_0 = input.LA(1);

                if ( (LA29_0==RULE_ID) ) {
                    alt29=1;
                }


                switch (alt29) {
            	case 1 :
            	    // InternalElkGraph.g:2318:3: rule__ElkEdge__PropertiesAssignment_7_2
            	    {
            	    pushFollow(FOLLOW_5);
            	    rule__ElkEdge__PropertiesAssignment_7_2();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop29;
                }
            } while (true);

             after(grammarAccess.getElkEdgeAccess().getPropertiesAssignment_7_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group_7__2__Impl"


    // $ANTLR start "rule__ElkEdge__Group_7__3"
    // InternalElkGraph.g:2326:1: rule__ElkEdge__Group_7__3 : rule__ElkEdge__Group_7__3__Impl rule__ElkEdge__Group_7__4 ;
    public final void rule__ElkEdge__Group_7__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2330:1: ( rule__ElkEdge__Group_7__3__Impl rule__ElkEdge__Group_7__4 )
            // InternalElkGraph.g:2331:2: rule__ElkEdge__Group_7__3__Impl rule__ElkEdge__Group_7__4
            {
            pushFollow(FOLLOW_9);
            rule__ElkEdge__Group_7__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdge__Group_7__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group_7__3"


    // $ANTLR start "rule__ElkEdge__Group_7__3__Impl"
    // InternalElkGraph.g:2338:1: rule__ElkEdge__Group_7__3__Impl : ( ( rule__ElkEdge__LabelsAssignment_7_3 )* ) ;
    public final void rule__ElkEdge__Group_7__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2342:1: ( ( ( rule__ElkEdge__LabelsAssignment_7_3 )* ) )
            // InternalElkGraph.g:2343:1: ( ( rule__ElkEdge__LabelsAssignment_7_3 )* )
            {
            // InternalElkGraph.g:2343:1: ( ( rule__ElkEdge__LabelsAssignment_7_3 )* )
            // InternalElkGraph.g:2344:2: ( rule__ElkEdge__LabelsAssignment_7_3 )*
            {
             before(grammarAccess.getElkEdgeAccess().getLabelsAssignment_7_3()); 
            // InternalElkGraph.g:2345:2: ( rule__ElkEdge__LabelsAssignment_7_3 )*
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( (LA30_0==19) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // InternalElkGraph.g:2345:3: rule__ElkEdge__LabelsAssignment_7_3
            	    {
            	    pushFollow(FOLLOW_6);
            	    rule__ElkEdge__LabelsAssignment_7_3();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop30;
                }
            } while (true);

             after(grammarAccess.getElkEdgeAccess().getLabelsAssignment_7_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group_7__3__Impl"


    // $ANTLR start "rule__ElkEdge__Group_7__4"
    // InternalElkGraph.g:2353:1: rule__ElkEdge__Group_7__4 : rule__ElkEdge__Group_7__4__Impl ;
    public final void rule__ElkEdge__Group_7__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2357:1: ( rule__ElkEdge__Group_7__4__Impl )
            // InternalElkGraph.g:2358:2: rule__ElkEdge__Group_7__4__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdge__Group_7__4__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group_7__4"


    // $ANTLR start "rule__ElkEdge__Group_7__4__Impl"
    // InternalElkGraph.g:2364:1: rule__ElkEdge__Group_7__4__Impl : ( '}' ) ;
    public final void rule__ElkEdge__Group_7__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2368:1: ( ( '}' ) )
            // InternalElkGraph.g:2369:1: ( '}' )
            {
            // InternalElkGraph.g:2369:1: ( '}' )
            // InternalElkGraph.g:2370:2: '}'
            {
             before(grammarAccess.getElkEdgeAccess().getRightCurlyBracketKeyword_7_4()); 
            match(input,18,FOLLOW_2); 
             after(grammarAccess.getElkEdgeAccess().getRightCurlyBracketKeyword_7_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__Group_7__4__Impl"


    // $ANTLR start "rule__EdgeLayout__Group__0"
    // InternalElkGraph.g:2380:1: rule__EdgeLayout__Group__0 : rule__EdgeLayout__Group__0__Impl rule__EdgeLayout__Group__1 ;
    public final void rule__EdgeLayout__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2384:1: ( rule__EdgeLayout__Group__0__Impl rule__EdgeLayout__Group__1 )
            // InternalElkGraph.g:2385:2: rule__EdgeLayout__Group__0__Impl rule__EdgeLayout__Group__1
            {
            pushFollow(FOLLOW_12);
            rule__EdgeLayout__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__EdgeLayout__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeLayout__Group__0"


    // $ANTLR start "rule__EdgeLayout__Group__0__Impl"
    // InternalElkGraph.g:2392:1: rule__EdgeLayout__Group__0__Impl : ( 'layout' ) ;
    public final void rule__EdgeLayout__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2396:1: ( ( 'layout' ) )
            // InternalElkGraph.g:2397:1: ( 'layout' )
            {
            // InternalElkGraph.g:2397:1: ( 'layout' )
            // InternalElkGraph.g:2398:2: 'layout'
            {
             before(grammarAccess.getEdgeLayoutAccess().getLayoutKeyword_0()); 
            match(input,22,FOLLOW_2); 
             after(grammarAccess.getEdgeLayoutAccess().getLayoutKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeLayout__Group__0__Impl"


    // $ANTLR start "rule__EdgeLayout__Group__1"
    // InternalElkGraph.g:2407:1: rule__EdgeLayout__Group__1 : rule__EdgeLayout__Group__1__Impl rule__EdgeLayout__Group__2 ;
    public final void rule__EdgeLayout__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2411:1: ( rule__EdgeLayout__Group__1__Impl rule__EdgeLayout__Group__2 )
            // InternalElkGraph.g:2412:2: rule__EdgeLayout__Group__1__Impl rule__EdgeLayout__Group__2
            {
            pushFollow(FOLLOW_20);
            rule__EdgeLayout__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__EdgeLayout__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeLayout__Group__1"


    // $ANTLR start "rule__EdgeLayout__Group__1__Impl"
    // InternalElkGraph.g:2419:1: rule__EdgeLayout__Group__1__Impl : ( '[' ) ;
    public final void rule__EdgeLayout__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2423:1: ( ( '[' ) )
            // InternalElkGraph.g:2424:1: ( '[' )
            {
            // InternalElkGraph.g:2424:1: ( '[' )
            // InternalElkGraph.g:2425:2: '['
            {
             before(grammarAccess.getEdgeLayoutAccess().getLeftSquareBracketKeyword_1()); 
            match(input,23,FOLLOW_2); 
             after(grammarAccess.getEdgeLayoutAccess().getLeftSquareBracketKeyword_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeLayout__Group__1__Impl"


    // $ANTLR start "rule__EdgeLayout__Group__2"
    // InternalElkGraph.g:2434:1: rule__EdgeLayout__Group__2 : rule__EdgeLayout__Group__2__Impl rule__EdgeLayout__Group__3 ;
    public final void rule__EdgeLayout__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2438:1: ( rule__EdgeLayout__Group__2__Impl rule__EdgeLayout__Group__3 )
            // InternalElkGraph.g:2439:2: rule__EdgeLayout__Group__2__Impl rule__EdgeLayout__Group__3
            {
            pushFollow(FOLLOW_14);
            rule__EdgeLayout__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__EdgeLayout__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeLayout__Group__2"


    // $ANTLR start "rule__EdgeLayout__Group__2__Impl"
    // InternalElkGraph.g:2446:1: rule__EdgeLayout__Group__2__Impl : ( ( rule__EdgeLayout__Alternatives_2 ) ) ;
    public final void rule__EdgeLayout__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2450:1: ( ( ( rule__EdgeLayout__Alternatives_2 ) ) )
            // InternalElkGraph.g:2451:1: ( ( rule__EdgeLayout__Alternatives_2 ) )
            {
            // InternalElkGraph.g:2451:1: ( ( rule__EdgeLayout__Alternatives_2 ) )
            // InternalElkGraph.g:2452:2: ( rule__EdgeLayout__Alternatives_2 )
            {
             before(grammarAccess.getEdgeLayoutAccess().getAlternatives_2()); 
            // InternalElkGraph.g:2453:2: ( rule__EdgeLayout__Alternatives_2 )
            // InternalElkGraph.g:2453:3: rule__EdgeLayout__Alternatives_2
            {
            pushFollow(FOLLOW_2);
            rule__EdgeLayout__Alternatives_2();

            state._fsp--;


            }

             after(grammarAccess.getEdgeLayoutAccess().getAlternatives_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeLayout__Group__2__Impl"


    // $ANTLR start "rule__EdgeLayout__Group__3"
    // InternalElkGraph.g:2461:1: rule__EdgeLayout__Group__3 : rule__EdgeLayout__Group__3__Impl ;
    public final void rule__EdgeLayout__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2465:1: ( rule__EdgeLayout__Group__3__Impl )
            // InternalElkGraph.g:2466:2: rule__EdgeLayout__Group__3__Impl
            {
            pushFollow(FOLLOW_2);
            rule__EdgeLayout__Group__3__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeLayout__Group__3"


    // $ANTLR start "rule__EdgeLayout__Group__3__Impl"
    // InternalElkGraph.g:2472:1: rule__EdgeLayout__Group__3__Impl : ( ']' ) ;
    public final void rule__EdgeLayout__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2476:1: ( ( ']' ) )
            // InternalElkGraph.g:2477:1: ( ']' )
            {
            // InternalElkGraph.g:2477:1: ( ']' )
            // InternalElkGraph.g:2478:2: ']'
            {
             before(grammarAccess.getEdgeLayoutAccess().getRightSquareBracketKeyword_3()); 
            match(input,24,FOLLOW_2); 
             after(grammarAccess.getEdgeLayoutAccess().getRightSquareBracketKeyword_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeLayout__Group__3__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group__0"
    // InternalElkGraph.g:2488:1: rule__ElkSingleEdgeSection__Group__0 : rule__ElkSingleEdgeSection__Group__0__Impl rule__ElkSingleEdgeSection__Group__1 ;
    public final void rule__ElkSingleEdgeSection__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2492:1: ( rule__ElkSingleEdgeSection__Group__0__Impl rule__ElkSingleEdgeSection__Group__1 )
            // InternalElkGraph.g:2493:2: rule__ElkSingleEdgeSection__Group__0__Impl rule__ElkSingleEdgeSection__Group__1
            {
            pushFollow(FOLLOW_21);
            rule__ElkSingleEdgeSection__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group__0"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group__0__Impl"
    // InternalElkGraph.g:2500:1: rule__ElkSingleEdgeSection__Group__0__Impl : ( () ) ;
    public final void rule__ElkSingleEdgeSection__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2504:1: ( ( () ) )
            // InternalElkGraph.g:2505:1: ( () )
            {
            // InternalElkGraph.g:2505:1: ( () )
            // InternalElkGraph.g:2506:2: ()
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getElkEdgeSectionAction_0()); 
            // InternalElkGraph.g:2507:2: ()
            // InternalElkGraph.g:2507:3: 
            {
            }

             after(grammarAccess.getElkSingleEdgeSectionAccess().getElkEdgeSectionAction_0()); 

            }


            }

        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group__0__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group__1"
    // InternalElkGraph.g:2515:1: rule__ElkSingleEdgeSection__Group__1 : rule__ElkSingleEdgeSection__Group__1__Impl ;
    public final void rule__ElkSingleEdgeSection__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2519:1: ( rule__ElkSingleEdgeSection__Group__1__Impl )
            // InternalElkGraph.g:2520:2: rule__ElkSingleEdgeSection__Group__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group__1"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group__1__Impl"
    // InternalElkGraph.g:2526:1: rule__ElkSingleEdgeSection__Group__1__Impl : ( ( rule__ElkSingleEdgeSection__UnorderedGroup_1 ) ) ;
    public final void rule__ElkSingleEdgeSection__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2530:1: ( ( ( rule__ElkSingleEdgeSection__UnorderedGroup_1 ) ) )
            // InternalElkGraph.g:2531:1: ( ( rule__ElkSingleEdgeSection__UnorderedGroup_1 ) )
            {
            // InternalElkGraph.g:2531:1: ( ( rule__ElkSingleEdgeSection__UnorderedGroup_1 ) )
            // InternalElkGraph.g:2532:2: ( rule__ElkSingleEdgeSection__UnorderedGroup_1 )
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1()); 
            // InternalElkGraph.g:2533:2: ( rule__ElkSingleEdgeSection__UnorderedGroup_1 )
            // InternalElkGraph.g:2533:3: rule__ElkSingleEdgeSection__UnorderedGroup_1
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__UnorderedGroup_1();

            state._fsp--;


            }

             after(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group__1__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0__0"
    // InternalElkGraph.g:2542:1: rule__ElkSingleEdgeSection__Group_1_0__0 : rule__ElkSingleEdgeSection__Group_1_0__0__Impl rule__ElkSingleEdgeSection__Group_1_0__1 ;
    public final void rule__ElkSingleEdgeSection__Group_1_0__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2546:1: ( rule__ElkSingleEdgeSection__Group_1_0__0__Impl rule__ElkSingleEdgeSection__Group_1_0__1 )
            // InternalElkGraph.g:2547:2: rule__ElkSingleEdgeSection__Group_1_0__0__Impl rule__ElkSingleEdgeSection__Group_1_0__1
            {
            pushFollow(FOLLOW_11);
            rule__ElkSingleEdgeSection__Group_1_0__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_0__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0__0"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0__0__Impl"
    // InternalElkGraph.g:2554:1: rule__ElkSingleEdgeSection__Group_1_0__0__Impl : ( 'incoming' ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_0__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2558:1: ( ( 'incoming' ) )
            // InternalElkGraph.g:2559:1: ( 'incoming' )
            {
            // InternalElkGraph.g:2559:1: ( 'incoming' )
            // InternalElkGraph.g:2560:2: 'incoming'
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getIncomingKeyword_1_0_0()); 
            match(input,30,FOLLOW_2); 
             after(grammarAccess.getElkSingleEdgeSectionAccess().getIncomingKeyword_1_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0__0__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0__1"
    // InternalElkGraph.g:2569:1: rule__ElkSingleEdgeSection__Group_1_0__1 : rule__ElkSingleEdgeSection__Group_1_0__1__Impl rule__ElkSingleEdgeSection__Group_1_0__2 ;
    public final void rule__ElkSingleEdgeSection__Group_1_0__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2573:1: ( rule__ElkSingleEdgeSection__Group_1_0__1__Impl rule__ElkSingleEdgeSection__Group_1_0__2 )
            // InternalElkGraph.g:2574:2: rule__ElkSingleEdgeSection__Group_1_0__1__Impl rule__ElkSingleEdgeSection__Group_1_0__2
            {
            pushFollow(FOLLOW_7);
            rule__ElkSingleEdgeSection__Group_1_0__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_0__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0__1"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0__1__Impl"
    // InternalElkGraph.g:2581:1: rule__ElkSingleEdgeSection__Group_1_0__1__Impl : ( ':' ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_0__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2585:1: ( ( ':' ) )
            // InternalElkGraph.g:2586:1: ( ':' )
            {
            // InternalElkGraph.g:2586:1: ( ':' )
            // InternalElkGraph.g:2587:2: ':'
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getColonKeyword_1_0_1()); 
            match(input,20,FOLLOW_2); 
             after(grammarAccess.getElkSingleEdgeSectionAccess().getColonKeyword_1_0_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0__1__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0__2"
    // InternalElkGraph.g:2596:1: rule__ElkSingleEdgeSection__Group_1_0__2 : rule__ElkSingleEdgeSection__Group_1_0__2__Impl ;
    public final void rule__ElkSingleEdgeSection__Group_1_0__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2600:1: ( rule__ElkSingleEdgeSection__Group_1_0__2__Impl )
            // InternalElkGraph.g:2601:2: rule__ElkSingleEdgeSection__Group_1_0__2__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_0__2__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0__2"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0__2__Impl"
    // InternalElkGraph.g:2607:1: rule__ElkSingleEdgeSection__Group_1_0__2__Impl : ( ( rule__ElkSingleEdgeSection__IncomingShapeAssignment_1_0_2 ) ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_0__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2611:1: ( ( ( rule__ElkSingleEdgeSection__IncomingShapeAssignment_1_0_2 ) ) )
            // InternalElkGraph.g:2612:1: ( ( rule__ElkSingleEdgeSection__IncomingShapeAssignment_1_0_2 ) )
            {
            // InternalElkGraph.g:2612:1: ( ( rule__ElkSingleEdgeSection__IncomingShapeAssignment_1_0_2 ) )
            // InternalElkGraph.g:2613:2: ( rule__ElkSingleEdgeSection__IncomingShapeAssignment_1_0_2 )
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getIncomingShapeAssignment_1_0_2()); 
            // InternalElkGraph.g:2614:2: ( rule__ElkSingleEdgeSection__IncomingShapeAssignment_1_0_2 )
            // InternalElkGraph.g:2614:3: rule__ElkSingleEdgeSection__IncomingShapeAssignment_1_0_2
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__IncomingShapeAssignment_1_0_2();

            state._fsp--;


            }

             after(grammarAccess.getElkSingleEdgeSectionAccess().getIncomingShapeAssignment_1_0_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0__2__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_1__0"
    // InternalElkGraph.g:2623:1: rule__ElkSingleEdgeSection__Group_1_1__0 : rule__ElkSingleEdgeSection__Group_1_1__0__Impl rule__ElkSingleEdgeSection__Group_1_1__1 ;
    public final void rule__ElkSingleEdgeSection__Group_1_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2627:1: ( rule__ElkSingleEdgeSection__Group_1_1__0__Impl rule__ElkSingleEdgeSection__Group_1_1__1 )
            // InternalElkGraph.g:2628:2: rule__ElkSingleEdgeSection__Group_1_1__0__Impl rule__ElkSingleEdgeSection__Group_1_1__1
            {
            pushFollow(FOLLOW_11);
            rule__ElkSingleEdgeSection__Group_1_1__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_1__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_1__0"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_1__0__Impl"
    // InternalElkGraph.g:2635:1: rule__ElkSingleEdgeSection__Group_1_1__0__Impl : ( 'outgoing' ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_1__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2639:1: ( ( 'outgoing' ) )
            // InternalElkGraph.g:2640:1: ( 'outgoing' )
            {
            // InternalElkGraph.g:2640:1: ( 'outgoing' )
            // InternalElkGraph.g:2641:2: 'outgoing'
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getOutgoingKeyword_1_1_0()); 
            match(input,31,FOLLOW_2); 
             after(grammarAccess.getElkSingleEdgeSectionAccess().getOutgoingKeyword_1_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_1__0__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_1__1"
    // InternalElkGraph.g:2650:1: rule__ElkSingleEdgeSection__Group_1_1__1 : rule__ElkSingleEdgeSection__Group_1_1__1__Impl rule__ElkSingleEdgeSection__Group_1_1__2 ;
    public final void rule__ElkSingleEdgeSection__Group_1_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2654:1: ( rule__ElkSingleEdgeSection__Group_1_1__1__Impl rule__ElkSingleEdgeSection__Group_1_1__2 )
            // InternalElkGraph.g:2655:2: rule__ElkSingleEdgeSection__Group_1_1__1__Impl rule__ElkSingleEdgeSection__Group_1_1__2
            {
            pushFollow(FOLLOW_7);
            rule__ElkSingleEdgeSection__Group_1_1__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_1__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_1__1"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_1__1__Impl"
    // InternalElkGraph.g:2662:1: rule__ElkSingleEdgeSection__Group_1_1__1__Impl : ( ':' ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_1__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2666:1: ( ( ':' ) )
            // InternalElkGraph.g:2667:1: ( ':' )
            {
            // InternalElkGraph.g:2667:1: ( ':' )
            // InternalElkGraph.g:2668:2: ':'
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getColonKeyword_1_1_1()); 
            match(input,20,FOLLOW_2); 
             after(grammarAccess.getElkSingleEdgeSectionAccess().getColonKeyword_1_1_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_1__1__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_1__2"
    // InternalElkGraph.g:2677:1: rule__ElkSingleEdgeSection__Group_1_1__2 : rule__ElkSingleEdgeSection__Group_1_1__2__Impl ;
    public final void rule__ElkSingleEdgeSection__Group_1_1__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2681:1: ( rule__ElkSingleEdgeSection__Group_1_1__2__Impl )
            // InternalElkGraph.g:2682:2: rule__ElkSingleEdgeSection__Group_1_1__2__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_1__2__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_1__2"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_1__2__Impl"
    // InternalElkGraph.g:2688:1: rule__ElkSingleEdgeSection__Group_1_1__2__Impl : ( ( rule__ElkSingleEdgeSection__OutgoingShapeAssignment_1_1_2 ) ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_1__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2692:1: ( ( ( rule__ElkSingleEdgeSection__OutgoingShapeAssignment_1_1_2 ) ) )
            // InternalElkGraph.g:2693:1: ( ( rule__ElkSingleEdgeSection__OutgoingShapeAssignment_1_1_2 ) )
            {
            // InternalElkGraph.g:2693:1: ( ( rule__ElkSingleEdgeSection__OutgoingShapeAssignment_1_1_2 ) )
            // InternalElkGraph.g:2694:2: ( rule__ElkSingleEdgeSection__OutgoingShapeAssignment_1_1_2 )
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getOutgoingShapeAssignment_1_1_2()); 
            // InternalElkGraph.g:2695:2: ( rule__ElkSingleEdgeSection__OutgoingShapeAssignment_1_1_2 )
            // InternalElkGraph.g:2695:3: rule__ElkSingleEdgeSection__OutgoingShapeAssignment_1_1_2
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__OutgoingShapeAssignment_1_1_2();

            state._fsp--;


            }

             after(grammarAccess.getElkSingleEdgeSectionAccess().getOutgoingShapeAssignment_1_1_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_1__2__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_2__0"
    // InternalElkGraph.g:2704:1: rule__ElkSingleEdgeSection__Group_1_2__0 : rule__ElkSingleEdgeSection__Group_1_2__0__Impl rule__ElkSingleEdgeSection__Group_1_2__1 ;
    public final void rule__ElkSingleEdgeSection__Group_1_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2708:1: ( rule__ElkSingleEdgeSection__Group_1_2__0__Impl rule__ElkSingleEdgeSection__Group_1_2__1 )
            // InternalElkGraph.g:2709:2: rule__ElkSingleEdgeSection__Group_1_2__0__Impl rule__ElkSingleEdgeSection__Group_1_2__1
            {
            pushFollow(FOLLOW_11);
            rule__ElkSingleEdgeSection__Group_1_2__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_2__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_2__0"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_2__0__Impl"
    // InternalElkGraph.g:2716:1: rule__ElkSingleEdgeSection__Group_1_2__0__Impl : ( 'start' ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_2__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2720:1: ( ( 'start' ) )
            // InternalElkGraph.g:2721:1: ( 'start' )
            {
            // InternalElkGraph.g:2721:1: ( 'start' )
            // InternalElkGraph.g:2722:2: 'start'
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getStartKeyword_1_2_0()); 
            match(input,32,FOLLOW_2); 
             after(grammarAccess.getElkSingleEdgeSectionAccess().getStartKeyword_1_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_2__0__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_2__1"
    // InternalElkGraph.g:2731:1: rule__ElkSingleEdgeSection__Group_1_2__1 : rule__ElkSingleEdgeSection__Group_1_2__1__Impl rule__ElkSingleEdgeSection__Group_1_2__2 ;
    public final void rule__ElkSingleEdgeSection__Group_1_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2735:1: ( rule__ElkSingleEdgeSection__Group_1_2__1__Impl rule__ElkSingleEdgeSection__Group_1_2__2 )
            // InternalElkGraph.g:2736:2: rule__ElkSingleEdgeSection__Group_1_2__1__Impl rule__ElkSingleEdgeSection__Group_1_2__2
            {
            pushFollow(FOLLOW_15);
            rule__ElkSingleEdgeSection__Group_1_2__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_2__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_2__1"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_2__1__Impl"
    // InternalElkGraph.g:2743:1: rule__ElkSingleEdgeSection__Group_1_2__1__Impl : ( ':' ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_2__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2747:1: ( ( ':' ) )
            // InternalElkGraph.g:2748:1: ( ':' )
            {
            // InternalElkGraph.g:2748:1: ( ':' )
            // InternalElkGraph.g:2749:2: ':'
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getColonKeyword_1_2_1()); 
            match(input,20,FOLLOW_2); 
             after(grammarAccess.getElkSingleEdgeSectionAccess().getColonKeyword_1_2_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_2__1__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_2__2"
    // InternalElkGraph.g:2758:1: rule__ElkSingleEdgeSection__Group_1_2__2 : rule__ElkSingleEdgeSection__Group_1_2__2__Impl rule__ElkSingleEdgeSection__Group_1_2__3 ;
    public final void rule__ElkSingleEdgeSection__Group_1_2__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2762:1: ( rule__ElkSingleEdgeSection__Group_1_2__2__Impl rule__ElkSingleEdgeSection__Group_1_2__3 )
            // InternalElkGraph.g:2763:2: rule__ElkSingleEdgeSection__Group_1_2__2__Impl rule__ElkSingleEdgeSection__Group_1_2__3
            {
            pushFollow(FOLLOW_16);
            rule__ElkSingleEdgeSection__Group_1_2__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_2__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_2__2"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_2__2__Impl"
    // InternalElkGraph.g:2770:1: rule__ElkSingleEdgeSection__Group_1_2__2__Impl : ( ( rule__ElkSingleEdgeSection__StartXAssignment_1_2_2 ) ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_2__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2774:1: ( ( ( rule__ElkSingleEdgeSection__StartXAssignment_1_2_2 ) ) )
            // InternalElkGraph.g:2775:1: ( ( rule__ElkSingleEdgeSection__StartXAssignment_1_2_2 ) )
            {
            // InternalElkGraph.g:2775:1: ( ( rule__ElkSingleEdgeSection__StartXAssignment_1_2_2 ) )
            // InternalElkGraph.g:2776:2: ( rule__ElkSingleEdgeSection__StartXAssignment_1_2_2 )
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getStartXAssignment_1_2_2()); 
            // InternalElkGraph.g:2777:2: ( rule__ElkSingleEdgeSection__StartXAssignment_1_2_2 )
            // InternalElkGraph.g:2777:3: rule__ElkSingleEdgeSection__StartXAssignment_1_2_2
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__StartXAssignment_1_2_2();

            state._fsp--;


            }

             after(grammarAccess.getElkSingleEdgeSectionAccess().getStartXAssignment_1_2_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_2__2__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_2__3"
    // InternalElkGraph.g:2785:1: rule__ElkSingleEdgeSection__Group_1_2__3 : rule__ElkSingleEdgeSection__Group_1_2__3__Impl rule__ElkSingleEdgeSection__Group_1_2__4 ;
    public final void rule__ElkSingleEdgeSection__Group_1_2__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2789:1: ( rule__ElkSingleEdgeSection__Group_1_2__3__Impl rule__ElkSingleEdgeSection__Group_1_2__4 )
            // InternalElkGraph.g:2790:2: rule__ElkSingleEdgeSection__Group_1_2__3__Impl rule__ElkSingleEdgeSection__Group_1_2__4
            {
            pushFollow(FOLLOW_15);
            rule__ElkSingleEdgeSection__Group_1_2__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_2__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_2__3"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_2__3__Impl"
    // InternalElkGraph.g:2797:1: rule__ElkSingleEdgeSection__Group_1_2__3__Impl : ( ',' ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_2__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2801:1: ( ( ',' ) )
            // InternalElkGraph.g:2802:1: ( ',' )
            {
            // InternalElkGraph.g:2802:1: ( ',' )
            // InternalElkGraph.g:2803:2: ','
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getCommaKeyword_1_2_3()); 
            match(input,26,FOLLOW_2); 
             after(grammarAccess.getElkSingleEdgeSectionAccess().getCommaKeyword_1_2_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_2__3__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_2__4"
    // InternalElkGraph.g:2812:1: rule__ElkSingleEdgeSection__Group_1_2__4 : rule__ElkSingleEdgeSection__Group_1_2__4__Impl ;
    public final void rule__ElkSingleEdgeSection__Group_1_2__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2816:1: ( rule__ElkSingleEdgeSection__Group_1_2__4__Impl )
            // InternalElkGraph.g:2817:2: rule__ElkSingleEdgeSection__Group_1_2__4__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_2__4__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_2__4"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_2__4__Impl"
    // InternalElkGraph.g:2823:1: rule__ElkSingleEdgeSection__Group_1_2__4__Impl : ( ( rule__ElkSingleEdgeSection__StartYAssignment_1_2_4 ) ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_2__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2827:1: ( ( ( rule__ElkSingleEdgeSection__StartYAssignment_1_2_4 ) ) )
            // InternalElkGraph.g:2828:1: ( ( rule__ElkSingleEdgeSection__StartYAssignment_1_2_4 ) )
            {
            // InternalElkGraph.g:2828:1: ( ( rule__ElkSingleEdgeSection__StartYAssignment_1_2_4 ) )
            // InternalElkGraph.g:2829:2: ( rule__ElkSingleEdgeSection__StartYAssignment_1_2_4 )
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getStartYAssignment_1_2_4()); 
            // InternalElkGraph.g:2830:2: ( rule__ElkSingleEdgeSection__StartYAssignment_1_2_4 )
            // InternalElkGraph.g:2830:3: rule__ElkSingleEdgeSection__StartYAssignment_1_2_4
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__StartYAssignment_1_2_4();

            state._fsp--;


            }

             after(grammarAccess.getElkSingleEdgeSectionAccess().getStartYAssignment_1_2_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_2__4__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_3__0"
    // InternalElkGraph.g:2839:1: rule__ElkSingleEdgeSection__Group_1_3__0 : rule__ElkSingleEdgeSection__Group_1_3__0__Impl rule__ElkSingleEdgeSection__Group_1_3__1 ;
    public final void rule__ElkSingleEdgeSection__Group_1_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2843:1: ( rule__ElkSingleEdgeSection__Group_1_3__0__Impl rule__ElkSingleEdgeSection__Group_1_3__1 )
            // InternalElkGraph.g:2844:2: rule__ElkSingleEdgeSection__Group_1_3__0__Impl rule__ElkSingleEdgeSection__Group_1_3__1
            {
            pushFollow(FOLLOW_11);
            rule__ElkSingleEdgeSection__Group_1_3__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_3__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_3__0"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_3__0__Impl"
    // InternalElkGraph.g:2851:1: rule__ElkSingleEdgeSection__Group_1_3__0__Impl : ( 'end' ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_3__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2855:1: ( ( 'end' ) )
            // InternalElkGraph.g:2856:1: ( 'end' )
            {
            // InternalElkGraph.g:2856:1: ( 'end' )
            // InternalElkGraph.g:2857:2: 'end'
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getEndKeyword_1_3_0()); 
            match(input,33,FOLLOW_2); 
             after(grammarAccess.getElkSingleEdgeSectionAccess().getEndKeyword_1_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_3__0__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_3__1"
    // InternalElkGraph.g:2866:1: rule__ElkSingleEdgeSection__Group_1_3__1 : rule__ElkSingleEdgeSection__Group_1_3__1__Impl rule__ElkSingleEdgeSection__Group_1_3__2 ;
    public final void rule__ElkSingleEdgeSection__Group_1_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2870:1: ( rule__ElkSingleEdgeSection__Group_1_3__1__Impl rule__ElkSingleEdgeSection__Group_1_3__2 )
            // InternalElkGraph.g:2871:2: rule__ElkSingleEdgeSection__Group_1_3__1__Impl rule__ElkSingleEdgeSection__Group_1_3__2
            {
            pushFollow(FOLLOW_15);
            rule__ElkSingleEdgeSection__Group_1_3__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_3__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_3__1"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_3__1__Impl"
    // InternalElkGraph.g:2878:1: rule__ElkSingleEdgeSection__Group_1_3__1__Impl : ( ':' ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_3__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2882:1: ( ( ':' ) )
            // InternalElkGraph.g:2883:1: ( ':' )
            {
            // InternalElkGraph.g:2883:1: ( ':' )
            // InternalElkGraph.g:2884:2: ':'
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getColonKeyword_1_3_1()); 
            match(input,20,FOLLOW_2); 
             after(grammarAccess.getElkSingleEdgeSectionAccess().getColonKeyword_1_3_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_3__1__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_3__2"
    // InternalElkGraph.g:2893:1: rule__ElkSingleEdgeSection__Group_1_3__2 : rule__ElkSingleEdgeSection__Group_1_3__2__Impl rule__ElkSingleEdgeSection__Group_1_3__3 ;
    public final void rule__ElkSingleEdgeSection__Group_1_3__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2897:1: ( rule__ElkSingleEdgeSection__Group_1_3__2__Impl rule__ElkSingleEdgeSection__Group_1_3__3 )
            // InternalElkGraph.g:2898:2: rule__ElkSingleEdgeSection__Group_1_3__2__Impl rule__ElkSingleEdgeSection__Group_1_3__3
            {
            pushFollow(FOLLOW_16);
            rule__ElkSingleEdgeSection__Group_1_3__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_3__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_3__2"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_3__2__Impl"
    // InternalElkGraph.g:2905:1: rule__ElkSingleEdgeSection__Group_1_3__2__Impl : ( ( rule__ElkSingleEdgeSection__EndXAssignment_1_3_2 ) ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_3__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2909:1: ( ( ( rule__ElkSingleEdgeSection__EndXAssignment_1_3_2 ) ) )
            // InternalElkGraph.g:2910:1: ( ( rule__ElkSingleEdgeSection__EndXAssignment_1_3_2 ) )
            {
            // InternalElkGraph.g:2910:1: ( ( rule__ElkSingleEdgeSection__EndXAssignment_1_3_2 ) )
            // InternalElkGraph.g:2911:2: ( rule__ElkSingleEdgeSection__EndXAssignment_1_3_2 )
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getEndXAssignment_1_3_2()); 
            // InternalElkGraph.g:2912:2: ( rule__ElkSingleEdgeSection__EndXAssignment_1_3_2 )
            // InternalElkGraph.g:2912:3: rule__ElkSingleEdgeSection__EndXAssignment_1_3_2
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__EndXAssignment_1_3_2();

            state._fsp--;


            }

             after(grammarAccess.getElkSingleEdgeSectionAccess().getEndXAssignment_1_3_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_3__2__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_3__3"
    // InternalElkGraph.g:2920:1: rule__ElkSingleEdgeSection__Group_1_3__3 : rule__ElkSingleEdgeSection__Group_1_3__3__Impl rule__ElkSingleEdgeSection__Group_1_3__4 ;
    public final void rule__ElkSingleEdgeSection__Group_1_3__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2924:1: ( rule__ElkSingleEdgeSection__Group_1_3__3__Impl rule__ElkSingleEdgeSection__Group_1_3__4 )
            // InternalElkGraph.g:2925:2: rule__ElkSingleEdgeSection__Group_1_3__3__Impl rule__ElkSingleEdgeSection__Group_1_3__4
            {
            pushFollow(FOLLOW_15);
            rule__ElkSingleEdgeSection__Group_1_3__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_3__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_3__3"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_3__3__Impl"
    // InternalElkGraph.g:2932:1: rule__ElkSingleEdgeSection__Group_1_3__3__Impl : ( ',' ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_3__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2936:1: ( ( ',' ) )
            // InternalElkGraph.g:2937:1: ( ',' )
            {
            // InternalElkGraph.g:2937:1: ( ',' )
            // InternalElkGraph.g:2938:2: ','
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getCommaKeyword_1_3_3()); 
            match(input,26,FOLLOW_2); 
             after(grammarAccess.getElkSingleEdgeSectionAccess().getCommaKeyword_1_3_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_3__3__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_3__4"
    // InternalElkGraph.g:2947:1: rule__ElkSingleEdgeSection__Group_1_3__4 : rule__ElkSingleEdgeSection__Group_1_3__4__Impl ;
    public final void rule__ElkSingleEdgeSection__Group_1_3__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2951:1: ( rule__ElkSingleEdgeSection__Group_1_3__4__Impl )
            // InternalElkGraph.g:2952:2: rule__ElkSingleEdgeSection__Group_1_3__4__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_3__4__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_3__4"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_3__4__Impl"
    // InternalElkGraph.g:2958:1: rule__ElkSingleEdgeSection__Group_1_3__4__Impl : ( ( rule__ElkSingleEdgeSection__EndYAssignment_1_3_4 ) ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_3__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2962:1: ( ( ( rule__ElkSingleEdgeSection__EndYAssignment_1_3_4 ) ) )
            // InternalElkGraph.g:2963:1: ( ( rule__ElkSingleEdgeSection__EndYAssignment_1_3_4 ) )
            {
            // InternalElkGraph.g:2963:1: ( ( rule__ElkSingleEdgeSection__EndYAssignment_1_3_4 ) )
            // InternalElkGraph.g:2964:2: ( rule__ElkSingleEdgeSection__EndYAssignment_1_3_4 )
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getEndYAssignment_1_3_4()); 
            // InternalElkGraph.g:2965:2: ( rule__ElkSingleEdgeSection__EndYAssignment_1_3_4 )
            // InternalElkGraph.g:2965:3: rule__ElkSingleEdgeSection__EndYAssignment_1_3_4
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__EndYAssignment_1_3_4();

            state._fsp--;


            }

             after(grammarAccess.getElkSingleEdgeSectionAccess().getEndYAssignment_1_3_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_3__4__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_4__0"
    // InternalElkGraph.g:2974:1: rule__ElkSingleEdgeSection__Group_1_4__0 : rule__ElkSingleEdgeSection__Group_1_4__0__Impl rule__ElkSingleEdgeSection__Group_1_4__1 ;
    public final void rule__ElkSingleEdgeSection__Group_1_4__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2978:1: ( rule__ElkSingleEdgeSection__Group_1_4__0__Impl rule__ElkSingleEdgeSection__Group_1_4__1 )
            // InternalElkGraph.g:2979:2: rule__ElkSingleEdgeSection__Group_1_4__0__Impl rule__ElkSingleEdgeSection__Group_1_4__1
            {
            pushFollow(FOLLOW_11);
            rule__ElkSingleEdgeSection__Group_1_4__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_4__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_4__0"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_4__0__Impl"
    // InternalElkGraph.g:2986:1: rule__ElkSingleEdgeSection__Group_1_4__0__Impl : ( 'bends' ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_4__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2990:1: ( ( 'bends' ) )
            // InternalElkGraph.g:2991:1: ( 'bends' )
            {
            // InternalElkGraph.g:2991:1: ( 'bends' )
            // InternalElkGraph.g:2992:2: 'bends'
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getBendsKeyword_1_4_0()); 
            match(input,34,FOLLOW_2); 
             after(grammarAccess.getElkSingleEdgeSectionAccess().getBendsKeyword_1_4_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_4__0__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_4__1"
    // InternalElkGraph.g:3001:1: rule__ElkSingleEdgeSection__Group_1_4__1 : rule__ElkSingleEdgeSection__Group_1_4__1__Impl rule__ElkSingleEdgeSection__Group_1_4__2 ;
    public final void rule__ElkSingleEdgeSection__Group_1_4__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3005:1: ( rule__ElkSingleEdgeSection__Group_1_4__1__Impl rule__ElkSingleEdgeSection__Group_1_4__2 )
            // InternalElkGraph.g:3006:2: rule__ElkSingleEdgeSection__Group_1_4__1__Impl rule__ElkSingleEdgeSection__Group_1_4__2
            {
            pushFollow(FOLLOW_15);
            rule__ElkSingleEdgeSection__Group_1_4__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_4__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_4__1"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_4__1__Impl"
    // InternalElkGraph.g:3013:1: rule__ElkSingleEdgeSection__Group_1_4__1__Impl : ( ':' ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_4__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3017:1: ( ( ':' ) )
            // InternalElkGraph.g:3018:1: ( ':' )
            {
            // InternalElkGraph.g:3018:1: ( ':' )
            // InternalElkGraph.g:3019:2: ':'
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getColonKeyword_1_4_1()); 
            match(input,20,FOLLOW_2); 
             after(grammarAccess.getElkSingleEdgeSectionAccess().getColonKeyword_1_4_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_4__1__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_4__2"
    // InternalElkGraph.g:3028:1: rule__ElkSingleEdgeSection__Group_1_4__2 : rule__ElkSingleEdgeSection__Group_1_4__2__Impl rule__ElkSingleEdgeSection__Group_1_4__3 ;
    public final void rule__ElkSingleEdgeSection__Group_1_4__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3032:1: ( rule__ElkSingleEdgeSection__Group_1_4__2__Impl rule__ElkSingleEdgeSection__Group_1_4__3 )
            // InternalElkGraph.g:3033:2: rule__ElkSingleEdgeSection__Group_1_4__2__Impl rule__ElkSingleEdgeSection__Group_1_4__3
            {
            pushFollow(FOLLOW_22);
            rule__ElkSingleEdgeSection__Group_1_4__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_4__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_4__2"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_4__2__Impl"
    // InternalElkGraph.g:3040:1: rule__ElkSingleEdgeSection__Group_1_4__2__Impl : ( ( rule__ElkSingleEdgeSection__BendPointsAssignment_1_4_2 ) ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_4__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3044:1: ( ( ( rule__ElkSingleEdgeSection__BendPointsAssignment_1_4_2 ) ) )
            // InternalElkGraph.g:3045:1: ( ( rule__ElkSingleEdgeSection__BendPointsAssignment_1_4_2 ) )
            {
            // InternalElkGraph.g:3045:1: ( ( rule__ElkSingleEdgeSection__BendPointsAssignment_1_4_2 ) )
            // InternalElkGraph.g:3046:2: ( rule__ElkSingleEdgeSection__BendPointsAssignment_1_4_2 )
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getBendPointsAssignment_1_4_2()); 
            // InternalElkGraph.g:3047:2: ( rule__ElkSingleEdgeSection__BendPointsAssignment_1_4_2 )
            // InternalElkGraph.g:3047:3: rule__ElkSingleEdgeSection__BendPointsAssignment_1_4_2
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__BendPointsAssignment_1_4_2();

            state._fsp--;


            }

             after(grammarAccess.getElkSingleEdgeSectionAccess().getBendPointsAssignment_1_4_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_4__2__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_4__3"
    // InternalElkGraph.g:3055:1: rule__ElkSingleEdgeSection__Group_1_4__3 : rule__ElkSingleEdgeSection__Group_1_4__3__Impl ;
    public final void rule__ElkSingleEdgeSection__Group_1_4__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3059:1: ( rule__ElkSingleEdgeSection__Group_1_4__3__Impl )
            // InternalElkGraph.g:3060:2: rule__ElkSingleEdgeSection__Group_1_4__3__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_4__3__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_4__3"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_4__3__Impl"
    // InternalElkGraph.g:3066:1: rule__ElkSingleEdgeSection__Group_1_4__3__Impl : ( ( rule__ElkSingleEdgeSection__Group_1_4_3__0 )* ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_4__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3070:1: ( ( ( rule__ElkSingleEdgeSection__Group_1_4_3__0 )* ) )
            // InternalElkGraph.g:3071:1: ( ( rule__ElkSingleEdgeSection__Group_1_4_3__0 )* )
            {
            // InternalElkGraph.g:3071:1: ( ( rule__ElkSingleEdgeSection__Group_1_4_3__0 )* )
            // InternalElkGraph.g:3072:2: ( rule__ElkSingleEdgeSection__Group_1_4_3__0 )*
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getGroup_1_4_3()); 
            // InternalElkGraph.g:3073:2: ( rule__ElkSingleEdgeSection__Group_1_4_3__0 )*
            loop31:
            do {
                int alt31=2;
                int LA31_0 = input.LA(1);

                if ( (LA31_0==35) ) {
                    alt31=1;
                }


                switch (alt31) {
            	case 1 :
            	    // InternalElkGraph.g:3073:3: rule__ElkSingleEdgeSection__Group_1_4_3__0
            	    {
            	    pushFollow(FOLLOW_23);
            	    rule__ElkSingleEdgeSection__Group_1_4_3__0();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop31;
                }
            } while (true);

             after(grammarAccess.getElkSingleEdgeSectionAccess().getGroup_1_4_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_4__3__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_4_3__0"
    // InternalElkGraph.g:3082:1: rule__ElkSingleEdgeSection__Group_1_4_3__0 : rule__ElkSingleEdgeSection__Group_1_4_3__0__Impl rule__ElkSingleEdgeSection__Group_1_4_3__1 ;
    public final void rule__ElkSingleEdgeSection__Group_1_4_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3086:1: ( rule__ElkSingleEdgeSection__Group_1_4_3__0__Impl rule__ElkSingleEdgeSection__Group_1_4_3__1 )
            // InternalElkGraph.g:3087:2: rule__ElkSingleEdgeSection__Group_1_4_3__0__Impl rule__ElkSingleEdgeSection__Group_1_4_3__1
            {
            pushFollow(FOLLOW_15);
            rule__ElkSingleEdgeSection__Group_1_4_3__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_4_3__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_4_3__0"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_4_3__0__Impl"
    // InternalElkGraph.g:3094:1: rule__ElkSingleEdgeSection__Group_1_4_3__0__Impl : ( '|' ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_4_3__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3098:1: ( ( '|' ) )
            // InternalElkGraph.g:3099:1: ( '|' )
            {
            // InternalElkGraph.g:3099:1: ( '|' )
            // InternalElkGraph.g:3100:2: '|'
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getVerticalLineKeyword_1_4_3_0()); 
            match(input,35,FOLLOW_2); 
             after(grammarAccess.getElkSingleEdgeSectionAccess().getVerticalLineKeyword_1_4_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_4_3__0__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_4_3__1"
    // InternalElkGraph.g:3109:1: rule__ElkSingleEdgeSection__Group_1_4_3__1 : rule__ElkSingleEdgeSection__Group_1_4_3__1__Impl ;
    public final void rule__ElkSingleEdgeSection__Group_1_4_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3113:1: ( rule__ElkSingleEdgeSection__Group_1_4_3__1__Impl )
            // InternalElkGraph.g:3114:2: rule__ElkSingleEdgeSection__Group_1_4_3__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_4_3__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_4_3__1"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_4_3__1__Impl"
    // InternalElkGraph.g:3120:1: rule__ElkSingleEdgeSection__Group_1_4_3__1__Impl : ( ( rule__ElkSingleEdgeSection__BendPointsAssignment_1_4_3_1 ) ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_4_3__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3124:1: ( ( ( rule__ElkSingleEdgeSection__BendPointsAssignment_1_4_3_1 ) ) )
            // InternalElkGraph.g:3125:1: ( ( rule__ElkSingleEdgeSection__BendPointsAssignment_1_4_3_1 ) )
            {
            // InternalElkGraph.g:3125:1: ( ( rule__ElkSingleEdgeSection__BendPointsAssignment_1_4_3_1 ) )
            // InternalElkGraph.g:3126:2: ( rule__ElkSingleEdgeSection__BendPointsAssignment_1_4_3_1 )
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getBendPointsAssignment_1_4_3_1()); 
            // InternalElkGraph.g:3127:2: ( rule__ElkSingleEdgeSection__BendPointsAssignment_1_4_3_1 )
            // InternalElkGraph.g:3127:3: rule__ElkSingleEdgeSection__BendPointsAssignment_1_4_3_1
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__BendPointsAssignment_1_4_3_1();

            state._fsp--;


            }

             after(grammarAccess.getElkSingleEdgeSectionAccess().getBendPointsAssignment_1_4_3_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_4_3__1__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group__0"
    // InternalElkGraph.g:3136:1: rule__ElkEdgeSection__Group__0 : rule__ElkEdgeSection__Group__0__Impl rule__ElkEdgeSection__Group__1 ;
    public final void rule__ElkEdgeSection__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3140:1: ( rule__ElkEdgeSection__Group__0__Impl rule__ElkEdgeSection__Group__1 )
            // InternalElkGraph.g:3141:2: rule__ElkEdgeSection__Group__0__Impl rule__ElkEdgeSection__Group__1
            {
            pushFollow(FOLLOW_7);
            rule__ElkEdgeSection__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group__0"


    // $ANTLR start "rule__ElkEdgeSection__Group__0__Impl"
    // InternalElkGraph.g:3148:1: rule__ElkEdgeSection__Group__0__Impl : ( 'section' ) ;
    public final void rule__ElkEdgeSection__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3152:1: ( ( 'section' ) )
            // InternalElkGraph.g:3153:1: ( 'section' )
            {
            // InternalElkGraph.g:3153:1: ( 'section' )
            // InternalElkGraph.g:3154:2: 'section'
            {
             before(grammarAccess.getElkEdgeSectionAccess().getSectionKeyword_0()); 
            match(input,36,FOLLOW_2); 
             after(grammarAccess.getElkEdgeSectionAccess().getSectionKeyword_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group__0__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group__1"
    // InternalElkGraph.g:3163:1: rule__ElkEdgeSection__Group__1 : rule__ElkEdgeSection__Group__1__Impl rule__ElkEdgeSection__Group__2 ;
    public final void rule__ElkEdgeSection__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3167:1: ( rule__ElkEdgeSection__Group__1__Impl rule__ElkEdgeSection__Group__2 )
            // InternalElkGraph.g:3168:2: rule__ElkEdgeSection__Group__1__Impl rule__ElkEdgeSection__Group__2
            {
            pushFollow(FOLLOW_24);
            rule__ElkEdgeSection__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group__1"


    // $ANTLR start "rule__ElkEdgeSection__Group__1__Impl"
    // InternalElkGraph.g:3175:1: rule__ElkEdgeSection__Group__1__Impl : ( ( rule__ElkEdgeSection__IdentifierAssignment_1 ) ) ;
    public final void rule__ElkEdgeSection__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3179:1: ( ( ( rule__ElkEdgeSection__IdentifierAssignment_1 ) ) )
            // InternalElkGraph.g:3180:1: ( ( rule__ElkEdgeSection__IdentifierAssignment_1 ) )
            {
            // InternalElkGraph.g:3180:1: ( ( rule__ElkEdgeSection__IdentifierAssignment_1 ) )
            // InternalElkGraph.g:3181:2: ( rule__ElkEdgeSection__IdentifierAssignment_1 )
            {
             before(grammarAccess.getElkEdgeSectionAccess().getIdentifierAssignment_1()); 
            // InternalElkGraph.g:3182:2: ( rule__ElkEdgeSection__IdentifierAssignment_1 )
            // InternalElkGraph.g:3182:3: rule__ElkEdgeSection__IdentifierAssignment_1
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__IdentifierAssignment_1();

            state._fsp--;


            }

             after(grammarAccess.getElkEdgeSectionAccess().getIdentifierAssignment_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group__1__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group__2"
    // InternalElkGraph.g:3190:1: rule__ElkEdgeSection__Group__2 : rule__ElkEdgeSection__Group__2__Impl rule__ElkEdgeSection__Group__3 ;
    public final void rule__ElkEdgeSection__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3194:1: ( rule__ElkEdgeSection__Group__2__Impl rule__ElkEdgeSection__Group__3 )
            // InternalElkGraph.g:3195:2: rule__ElkEdgeSection__Group__2__Impl rule__ElkEdgeSection__Group__3
            {
            pushFollow(FOLLOW_24);
            rule__ElkEdgeSection__Group__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group__2"


    // $ANTLR start "rule__ElkEdgeSection__Group__2__Impl"
    // InternalElkGraph.g:3202:1: rule__ElkEdgeSection__Group__2__Impl : ( ( rule__ElkEdgeSection__Group_2__0 )? ) ;
    public final void rule__ElkEdgeSection__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3206:1: ( ( ( rule__ElkEdgeSection__Group_2__0 )? ) )
            // InternalElkGraph.g:3207:1: ( ( rule__ElkEdgeSection__Group_2__0 )? )
            {
            // InternalElkGraph.g:3207:1: ( ( rule__ElkEdgeSection__Group_2__0 )? )
            // InternalElkGraph.g:3208:2: ( rule__ElkEdgeSection__Group_2__0 )?
            {
             before(grammarAccess.getElkEdgeSectionAccess().getGroup_2()); 
            // InternalElkGraph.g:3209:2: ( rule__ElkEdgeSection__Group_2__0 )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==29) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // InternalElkGraph.g:3209:3: rule__ElkEdgeSection__Group_2__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkEdgeSection__Group_2__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getElkEdgeSectionAccess().getGroup_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group__2__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group__3"
    // InternalElkGraph.g:3217:1: rule__ElkEdgeSection__Group__3 : rule__ElkEdgeSection__Group__3__Impl rule__ElkEdgeSection__Group__4 ;
    public final void rule__ElkEdgeSection__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3221:1: ( rule__ElkEdgeSection__Group__3__Impl rule__ElkEdgeSection__Group__4 )
            // InternalElkGraph.g:3222:2: rule__ElkEdgeSection__Group__3__Impl rule__ElkEdgeSection__Group__4
            {
            pushFollow(FOLLOW_21);
            rule__ElkEdgeSection__Group__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group__3"


    // $ANTLR start "rule__ElkEdgeSection__Group__3__Impl"
    // InternalElkGraph.g:3229:1: rule__ElkEdgeSection__Group__3__Impl : ( '[' ) ;
    public final void rule__ElkEdgeSection__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3233:1: ( ( '[' ) )
            // InternalElkGraph.g:3234:1: ( '[' )
            {
            // InternalElkGraph.g:3234:1: ( '[' )
            // InternalElkGraph.g:3235:2: '['
            {
             before(grammarAccess.getElkEdgeSectionAccess().getLeftSquareBracketKeyword_3()); 
            match(input,23,FOLLOW_2); 
             after(grammarAccess.getElkEdgeSectionAccess().getLeftSquareBracketKeyword_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group__3__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group__4"
    // InternalElkGraph.g:3244:1: rule__ElkEdgeSection__Group__4 : rule__ElkEdgeSection__Group__4__Impl rule__ElkEdgeSection__Group__5 ;
    public final void rule__ElkEdgeSection__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3248:1: ( rule__ElkEdgeSection__Group__4__Impl rule__ElkEdgeSection__Group__5 )
            // InternalElkGraph.g:3249:2: rule__ElkEdgeSection__Group__4__Impl rule__ElkEdgeSection__Group__5
            {
            pushFollow(FOLLOW_14);
            rule__ElkEdgeSection__Group__4__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group__5();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group__4"


    // $ANTLR start "rule__ElkEdgeSection__Group__4__Impl"
    // InternalElkGraph.g:3256:1: rule__ElkEdgeSection__Group__4__Impl : ( ( rule__ElkEdgeSection__UnorderedGroup_4 ) ) ;
    public final void rule__ElkEdgeSection__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3260:1: ( ( ( rule__ElkEdgeSection__UnorderedGroup_4 ) ) )
            // InternalElkGraph.g:3261:1: ( ( rule__ElkEdgeSection__UnorderedGroup_4 ) )
            {
            // InternalElkGraph.g:3261:1: ( ( rule__ElkEdgeSection__UnorderedGroup_4 ) )
            // InternalElkGraph.g:3262:2: ( rule__ElkEdgeSection__UnorderedGroup_4 )
            {
             before(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4()); 
            // InternalElkGraph.g:3263:2: ( rule__ElkEdgeSection__UnorderedGroup_4 )
            // InternalElkGraph.g:3263:3: rule__ElkEdgeSection__UnorderedGroup_4
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__UnorderedGroup_4();

            state._fsp--;


            }

             after(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group__4__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group__5"
    // InternalElkGraph.g:3271:1: rule__ElkEdgeSection__Group__5 : rule__ElkEdgeSection__Group__5__Impl ;
    public final void rule__ElkEdgeSection__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3275:1: ( rule__ElkEdgeSection__Group__5__Impl )
            // InternalElkGraph.g:3276:2: rule__ElkEdgeSection__Group__5__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group__5__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group__5"


    // $ANTLR start "rule__ElkEdgeSection__Group__5__Impl"
    // InternalElkGraph.g:3282:1: rule__ElkEdgeSection__Group__5__Impl : ( ']' ) ;
    public final void rule__ElkEdgeSection__Group__5__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3286:1: ( ( ']' ) )
            // InternalElkGraph.g:3287:1: ( ']' )
            {
            // InternalElkGraph.g:3287:1: ( ']' )
            // InternalElkGraph.g:3288:2: ']'
            {
             before(grammarAccess.getElkEdgeSectionAccess().getRightSquareBracketKeyword_5()); 
            match(input,24,FOLLOW_2); 
             after(grammarAccess.getElkEdgeSectionAccess().getRightSquareBracketKeyword_5()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group__5__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_2__0"
    // InternalElkGraph.g:3298:1: rule__ElkEdgeSection__Group_2__0 : rule__ElkEdgeSection__Group_2__0__Impl rule__ElkEdgeSection__Group_2__1 ;
    public final void rule__ElkEdgeSection__Group_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3302:1: ( rule__ElkEdgeSection__Group_2__0__Impl rule__ElkEdgeSection__Group_2__1 )
            // InternalElkGraph.g:3303:2: rule__ElkEdgeSection__Group_2__0__Impl rule__ElkEdgeSection__Group_2__1
            {
            pushFollow(FOLLOW_7);
            rule__ElkEdgeSection__Group_2__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_2__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_2__0"


    // $ANTLR start "rule__ElkEdgeSection__Group_2__0__Impl"
    // InternalElkGraph.g:3310:1: rule__ElkEdgeSection__Group_2__0__Impl : ( '->' ) ;
    public final void rule__ElkEdgeSection__Group_2__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3314:1: ( ( '->' ) )
            // InternalElkGraph.g:3315:1: ( '->' )
            {
            // InternalElkGraph.g:3315:1: ( '->' )
            // InternalElkGraph.g:3316:2: '->'
            {
             before(grammarAccess.getElkEdgeSectionAccess().getHyphenMinusGreaterThanSignKeyword_2_0()); 
            match(input,29,FOLLOW_2); 
             after(grammarAccess.getElkEdgeSectionAccess().getHyphenMinusGreaterThanSignKeyword_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_2__0__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_2__1"
    // InternalElkGraph.g:3325:1: rule__ElkEdgeSection__Group_2__1 : rule__ElkEdgeSection__Group_2__1__Impl rule__ElkEdgeSection__Group_2__2 ;
    public final void rule__ElkEdgeSection__Group_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3329:1: ( rule__ElkEdgeSection__Group_2__1__Impl rule__ElkEdgeSection__Group_2__2 )
            // InternalElkGraph.g:3330:2: rule__ElkEdgeSection__Group_2__1__Impl rule__ElkEdgeSection__Group_2__2
            {
            pushFollow(FOLLOW_16);
            rule__ElkEdgeSection__Group_2__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_2__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_2__1"


    // $ANTLR start "rule__ElkEdgeSection__Group_2__1__Impl"
    // InternalElkGraph.g:3337:1: rule__ElkEdgeSection__Group_2__1__Impl : ( ( rule__ElkEdgeSection__OutgoingSectionsAssignment_2_1 ) ) ;
    public final void rule__ElkEdgeSection__Group_2__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3341:1: ( ( ( rule__ElkEdgeSection__OutgoingSectionsAssignment_2_1 ) ) )
            // InternalElkGraph.g:3342:1: ( ( rule__ElkEdgeSection__OutgoingSectionsAssignment_2_1 ) )
            {
            // InternalElkGraph.g:3342:1: ( ( rule__ElkEdgeSection__OutgoingSectionsAssignment_2_1 ) )
            // InternalElkGraph.g:3343:2: ( rule__ElkEdgeSection__OutgoingSectionsAssignment_2_1 )
            {
             before(grammarAccess.getElkEdgeSectionAccess().getOutgoingSectionsAssignment_2_1()); 
            // InternalElkGraph.g:3344:2: ( rule__ElkEdgeSection__OutgoingSectionsAssignment_2_1 )
            // InternalElkGraph.g:3344:3: rule__ElkEdgeSection__OutgoingSectionsAssignment_2_1
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__OutgoingSectionsAssignment_2_1();

            state._fsp--;


            }

             after(grammarAccess.getElkEdgeSectionAccess().getOutgoingSectionsAssignment_2_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_2__1__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_2__2"
    // InternalElkGraph.g:3352:1: rule__ElkEdgeSection__Group_2__2 : rule__ElkEdgeSection__Group_2__2__Impl ;
    public final void rule__ElkEdgeSection__Group_2__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3356:1: ( rule__ElkEdgeSection__Group_2__2__Impl )
            // InternalElkGraph.g:3357:2: rule__ElkEdgeSection__Group_2__2__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_2__2__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_2__2"


    // $ANTLR start "rule__ElkEdgeSection__Group_2__2__Impl"
    // InternalElkGraph.g:3363:1: rule__ElkEdgeSection__Group_2__2__Impl : ( ( rule__ElkEdgeSection__Group_2_2__0 )* ) ;
    public final void rule__ElkEdgeSection__Group_2__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3367:1: ( ( ( rule__ElkEdgeSection__Group_2_2__0 )* ) )
            // InternalElkGraph.g:3368:1: ( ( rule__ElkEdgeSection__Group_2_2__0 )* )
            {
            // InternalElkGraph.g:3368:1: ( ( rule__ElkEdgeSection__Group_2_2__0 )* )
            // InternalElkGraph.g:3369:2: ( rule__ElkEdgeSection__Group_2_2__0 )*
            {
             before(grammarAccess.getElkEdgeSectionAccess().getGroup_2_2()); 
            // InternalElkGraph.g:3370:2: ( rule__ElkEdgeSection__Group_2_2__0 )*
            loop33:
            do {
                int alt33=2;
                int LA33_0 = input.LA(1);

                if ( (LA33_0==26) ) {
                    alt33=1;
                }


                switch (alt33) {
            	case 1 :
            	    // InternalElkGraph.g:3370:3: rule__ElkEdgeSection__Group_2_2__0
            	    {
            	    pushFollow(FOLLOW_18);
            	    rule__ElkEdgeSection__Group_2_2__0();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop33;
                }
            } while (true);

             after(grammarAccess.getElkEdgeSectionAccess().getGroup_2_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_2__2__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_2_2__0"
    // InternalElkGraph.g:3379:1: rule__ElkEdgeSection__Group_2_2__0 : rule__ElkEdgeSection__Group_2_2__0__Impl rule__ElkEdgeSection__Group_2_2__1 ;
    public final void rule__ElkEdgeSection__Group_2_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3383:1: ( rule__ElkEdgeSection__Group_2_2__0__Impl rule__ElkEdgeSection__Group_2_2__1 )
            // InternalElkGraph.g:3384:2: rule__ElkEdgeSection__Group_2_2__0__Impl rule__ElkEdgeSection__Group_2_2__1
            {
            pushFollow(FOLLOW_7);
            rule__ElkEdgeSection__Group_2_2__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_2_2__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_2_2__0"


    // $ANTLR start "rule__ElkEdgeSection__Group_2_2__0__Impl"
    // InternalElkGraph.g:3391:1: rule__ElkEdgeSection__Group_2_2__0__Impl : ( ',' ) ;
    public final void rule__ElkEdgeSection__Group_2_2__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3395:1: ( ( ',' ) )
            // InternalElkGraph.g:3396:1: ( ',' )
            {
            // InternalElkGraph.g:3396:1: ( ',' )
            // InternalElkGraph.g:3397:2: ','
            {
             before(grammarAccess.getElkEdgeSectionAccess().getCommaKeyword_2_2_0()); 
            match(input,26,FOLLOW_2); 
             after(grammarAccess.getElkEdgeSectionAccess().getCommaKeyword_2_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_2_2__0__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_2_2__1"
    // InternalElkGraph.g:3406:1: rule__ElkEdgeSection__Group_2_2__1 : rule__ElkEdgeSection__Group_2_2__1__Impl ;
    public final void rule__ElkEdgeSection__Group_2_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3410:1: ( rule__ElkEdgeSection__Group_2_2__1__Impl )
            // InternalElkGraph.g:3411:2: rule__ElkEdgeSection__Group_2_2__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_2_2__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_2_2__1"


    // $ANTLR start "rule__ElkEdgeSection__Group_2_2__1__Impl"
    // InternalElkGraph.g:3417:1: rule__ElkEdgeSection__Group_2_2__1__Impl : ( ( rule__ElkEdgeSection__OutgoingSectionsAssignment_2_2_1 ) ) ;
    public final void rule__ElkEdgeSection__Group_2_2__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3421:1: ( ( ( rule__ElkEdgeSection__OutgoingSectionsAssignment_2_2_1 ) ) )
            // InternalElkGraph.g:3422:1: ( ( rule__ElkEdgeSection__OutgoingSectionsAssignment_2_2_1 ) )
            {
            // InternalElkGraph.g:3422:1: ( ( rule__ElkEdgeSection__OutgoingSectionsAssignment_2_2_1 ) )
            // InternalElkGraph.g:3423:2: ( rule__ElkEdgeSection__OutgoingSectionsAssignment_2_2_1 )
            {
             before(grammarAccess.getElkEdgeSectionAccess().getOutgoingSectionsAssignment_2_2_1()); 
            // InternalElkGraph.g:3424:2: ( rule__ElkEdgeSection__OutgoingSectionsAssignment_2_2_1 )
            // InternalElkGraph.g:3424:3: rule__ElkEdgeSection__OutgoingSectionsAssignment_2_2_1
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__OutgoingSectionsAssignment_2_2_1();

            state._fsp--;


            }

             after(grammarAccess.getElkEdgeSectionAccess().getOutgoingSectionsAssignment_2_2_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_2_2__1__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0__0"
    // InternalElkGraph.g:3433:1: rule__ElkEdgeSection__Group_4_0__0 : rule__ElkEdgeSection__Group_4_0__0__Impl rule__ElkEdgeSection__Group_4_0__1 ;
    public final void rule__ElkEdgeSection__Group_4_0__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3437:1: ( rule__ElkEdgeSection__Group_4_0__0__Impl rule__ElkEdgeSection__Group_4_0__1 )
            // InternalElkGraph.g:3438:2: rule__ElkEdgeSection__Group_4_0__0__Impl rule__ElkEdgeSection__Group_4_0__1
            {
            pushFollow(FOLLOW_11);
            rule__ElkEdgeSection__Group_4_0__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_0__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0__0"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0__0__Impl"
    // InternalElkGraph.g:3445:1: rule__ElkEdgeSection__Group_4_0__0__Impl : ( 'incoming' ) ;
    public final void rule__ElkEdgeSection__Group_4_0__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3449:1: ( ( 'incoming' ) )
            // InternalElkGraph.g:3450:1: ( 'incoming' )
            {
            // InternalElkGraph.g:3450:1: ( 'incoming' )
            // InternalElkGraph.g:3451:2: 'incoming'
            {
             before(grammarAccess.getElkEdgeSectionAccess().getIncomingKeyword_4_0_0()); 
            match(input,30,FOLLOW_2); 
             after(grammarAccess.getElkEdgeSectionAccess().getIncomingKeyword_4_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0__0__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0__1"
    // InternalElkGraph.g:3460:1: rule__ElkEdgeSection__Group_4_0__1 : rule__ElkEdgeSection__Group_4_0__1__Impl rule__ElkEdgeSection__Group_4_0__2 ;
    public final void rule__ElkEdgeSection__Group_4_0__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3464:1: ( rule__ElkEdgeSection__Group_4_0__1__Impl rule__ElkEdgeSection__Group_4_0__2 )
            // InternalElkGraph.g:3465:2: rule__ElkEdgeSection__Group_4_0__1__Impl rule__ElkEdgeSection__Group_4_0__2
            {
            pushFollow(FOLLOW_7);
            rule__ElkEdgeSection__Group_4_0__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_0__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0__1"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0__1__Impl"
    // InternalElkGraph.g:3472:1: rule__ElkEdgeSection__Group_4_0__1__Impl : ( ':' ) ;
    public final void rule__ElkEdgeSection__Group_4_0__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3476:1: ( ( ':' ) )
            // InternalElkGraph.g:3477:1: ( ':' )
            {
            // InternalElkGraph.g:3477:1: ( ':' )
            // InternalElkGraph.g:3478:2: ':'
            {
             before(grammarAccess.getElkEdgeSectionAccess().getColonKeyword_4_0_1()); 
            match(input,20,FOLLOW_2); 
             after(grammarAccess.getElkEdgeSectionAccess().getColonKeyword_4_0_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0__1__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0__2"
    // InternalElkGraph.g:3487:1: rule__ElkEdgeSection__Group_4_0__2 : rule__ElkEdgeSection__Group_4_0__2__Impl ;
    public final void rule__ElkEdgeSection__Group_4_0__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3491:1: ( rule__ElkEdgeSection__Group_4_0__2__Impl )
            // InternalElkGraph.g:3492:2: rule__ElkEdgeSection__Group_4_0__2__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_0__2__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0__2"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0__2__Impl"
    // InternalElkGraph.g:3498:1: rule__ElkEdgeSection__Group_4_0__2__Impl : ( ( rule__ElkEdgeSection__IncomingShapeAssignment_4_0_2 ) ) ;
    public final void rule__ElkEdgeSection__Group_4_0__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3502:1: ( ( ( rule__ElkEdgeSection__IncomingShapeAssignment_4_0_2 ) ) )
            // InternalElkGraph.g:3503:1: ( ( rule__ElkEdgeSection__IncomingShapeAssignment_4_0_2 ) )
            {
            // InternalElkGraph.g:3503:1: ( ( rule__ElkEdgeSection__IncomingShapeAssignment_4_0_2 ) )
            // InternalElkGraph.g:3504:2: ( rule__ElkEdgeSection__IncomingShapeAssignment_4_0_2 )
            {
             before(grammarAccess.getElkEdgeSectionAccess().getIncomingShapeAssignment_4_0_2()); 
            // InternalElkGraph.g:3505:2: ( rule__ElkEdgeSection__IncomingShapeAssignment_4_0_2 )
            // InternalElkGraph.g:3505:3: rule__ElkEdgeSection__IncomingShapeAssignment_4_0_2
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__IncomingShapeAssignment_4_0_2();

            state._fsp--;


            }

             after(grammarAccess.getElkEdgeSectionAccess().getIncomingShapeAssignment_4_0_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0__2__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_1__0"
    // InternalElkGraph.g:3514:1: rule__ElkEdgeSection__Group_4_1__0 : rule__ElkEdgeSection__Group_4_1__0__Impl rule__ElkEdgeSection__Group_4_1__1 ;
    public final void rule__ElkEdgeSection__Group_4_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3518:1: ( rule__ElkEdgeSection__Group_4_1__0__Impl rule__ElkEdgeSection__Group_4_1__1 )
            // InternalElkGraph.g:3519:2: rule__ElkEdgeSection__Group_4_1__0__Impl rule__ElkEdgeSection__Group_4_1__1
            {
            pushFollow(FOLLOW_11);
            rule__ElkEdgeSection__Group_4_1__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_1__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_1__0"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_1__0__Impl"
    // InternalElkGraph.g:3526:1: rule__ElkEdgeSection__Group_4_1__0__Impl : ( 'outgoing' ) ;
    public final void rule__ElkEdgeSection__Group_4_1__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3530:1: ( ( 'outgoing' ) )
            // InternalElkGraph.g:3531:1: ( 'outgoing' )
            {
            // InternalElkGraph.g:3531:1: ( 'outgoing' )
            // InternalElkGraph.g:3532:2: 'outgoing'
            {
             before(grammarAccess.getElkEdgeSectionAccess().getOutgoingKeyword_4_1_0()); 
            match(input,31,FOLLOW_2); 
             after(grammarAccess.getElkEdgeSectionAccess().getOutgoingKeyword_4_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_1__0__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_1__1"
    // InternalElkGraph.g:3541:1: rule__ElkEdgeSection__Group_4_1__1 : rule__ElkEdgeSection__Group_4_1__1__Impl rule__ElkEdgeSection__Group_4_1__2 ;
    public final void rule__ElkEdgeSection__Group_4_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3545:1: ( rule__ElkEdgeSection__Group_4_1__1__Impl rule__ElkEdgeSection__Group_4_1__2 )
            // InternalElkGraph.g:3546:2: rule__ElkEdgeSection__Group_4_1__1__Impl rule__ElkEdgeSection__Group_4_1__2
            {
            pushFollow(FOLLOW_7);
            rule__ElkEdgeSection__Group_4_1__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_1__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_1__1"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_1__1__Impl"
    // InternalElkGraph.g:3553:1: rule__ElkEdgeSection__Group_4_1__1__Impl : ( ':' ) ;
    public final void rule__ElkEdgeSection__Group_4_1__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3557:1: ( ( ':' ) )
            // InternalElkGraph.g:3558:1: ( ':' )
            {
            // InternalElkGraph.g:3558:1: ( ':' )
            // InternalElkGraph.g:3559:2: ':'
            {
             before(grammarAccess.getElkEdgeSectionAccess().getColonKeyword_4_1_1()); 
            match(input,20,FOLLOW_2); 
             after(grammarAccess.getElkEdgeSectionAccess().getColonKeyword_4_1_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_1__1__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_1__2"
    // InternalElkGraph.g:3568:1: rule__ElkEdgeSection__Group_4_1__2 : rule__ElkEdgeSection__Group_4_1__2__Impl ;
    public final void rule__ElkEdgeSection__Group_4_1__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3572:1: ( rule__ElkEdgeSection__Group_4_1__2__Impl )
            // InternalElkGraph.g:3573:2: rule__ElkEdgeSection__Group_4_1__2__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_1__2__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_1__2"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_1__2__Impl"
    // InternalElkGraph.g:3579:1: rule__ElkEdgeSection__Group_4_1__2__Impl : ( ( rule__ElkEdgeSection__OutgoingShapeAssignment_4_1_2 ) ) ;
    public final void rule__ElkEdgeSection__Group_4_1__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3583:1: ( ( ( rule__ElkEdgeSection__OutgoingShapeAssignment_4_1_2 ) ) )
            // InternalElkGraph.g:3584:1: ( ( rule__ElkEdgeSection__OutgoingShapeAssignment_4_1_2 ) )
            {
            // InternalElkGraph.g:3584:1: ( ( rule__ElkEdgeSection__OutgoingShapeAssignment_4_1_2 ) )
            // InternalElkGraph.g:3585:2: ( rule__ElkEdgeSection__OutgoingShapeAssignment_4_1_2 )
            {
             before(grammarAccess.getElkEdgeSectionAccess().getOutgoingShapeAssignment_4_1_2()); 
            // InternalElkGraph.g:3586:2: ( rule__ElkEdgeSection__OutgoingShapeAssignment_4_1_2 )
            // InternalElkGraph.g:3586:3: rule__ElkEdgeSection__OutgoingShapeAssignment_4_1_2
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__OutgoingShapeAssignment_4_1_2();

            state._fsp--;


            }

             after(grammarAccess.getElkEdgeSectionAccess().getOutgoingShapeAssignment_4_1_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_1__2__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_2__0"
    // InternalElkGraph.g:3595:1: rule__ElkEdgeSection__Group_4_2__0 : rule__ElkEdgeSection__Group_4_2__0__Impl rule__ElkEdgeSection__Group_4_2__1 ;
    public final void rule__ElkEdgeSection__Group_4_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3599:1: ( rule__ElkEdgeSection__Group_4_2__0__Impl rule__ElkEdgeSection__Group_4_2__1 )
            // InternalElkGraph.g:3600:2: rule__ElkEdgeSection__Group_4_2__0__Impl rule__ElkEdgeSection__Group_4_2__1
            {
            pushFollow(FOLLOW_11);
            rule__ElkEdgeSection__Group_4_2__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_2__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_2__0"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_2__0__Impl"
    // InternalElkGraph.g:3607:1: rule__ElkEdgeSection__Group_4_2__0__Impl : ( 'start' ) ;
    public final void rule__ElkEdgeSection__Group_4_2__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3611:1: ( ( 'start' ) )
            // InternalElkGraph.g:3612:1: ( 'start' )
            {
            // InternalElkGraph.g:3612:1: ( 'start' )
            // InternalElkGraph.g:3613:2: 'start'
            {
             before(grammarAccess.getElkEdgeSectionAccess().getStartKeyword_4_2_0()); 
            match(input,32,FOLLOW_2); 
             after(grammarAccess.getElkEdgeSectionAccess().getStartKeyword_4_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_2__0__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_2__1"
    // InternalElkGraph.g:3622:1: rule__ElkEdgeSection__Group_4_2__1 : rule__ElkEdgeSection__Group_4_2__1__Impl rule__ElkEdgeSection__Group_4_2__2 ;
    public final void rule__ElkEdgeSection__Group_4_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3626:1: ( rule__ElkEdgeSection__Group_4_2__1__Impl rule__ElkEdgeSection__Group_4_2__2 )
            // InternalElkGraph.g:3627:2: rule__ElkEdgeSection__Group_4_2__1__Impl rule__ElkEdgeSection__Group_4_2__2
            {
            pushFollow(FOLLOW_15);
            rule__ElkEdgeSection__Group_4_2__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_2__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_2__1"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_2__1__Impl"
    // InternalElkGraph.g:3634:1: rule__ElkEdgeSection__Group_4_2__1__Impl : ( ':' ) ;
    public final void rule__ElkEdgeSection__Group_4_2__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3638:1: ( ( ':' ) )
            // InternalElkGraph.g:3639:1: ( ':' )
            {
            // InternalElkGraph.g:3639:1: ( ':' )
            // InternalElkGraph.g:3640:2: ':'
            {
             before(grammarAccess.getElkEdgeSectionAccess().getColonKeyword_4_2_1()); 
            match(input,20,FOLLOW_2); 
             after(grammarAccess.getElkEdgeSectionAccess().getColonKeyword_4_2_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_2__1__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_2__2"
    // InternalElkGraph.g:3649:1: rule__ElkEdgeSection__Group_4_2__2 : rule__ElkEdgeSection__Group_4_2__2__Impl rule__ElkEdgeSection__Group_4_2__3 ;
    public final void rule__ElkEdgeSection__Group_4_2__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3653:1: ( rule__ElkEdgeSection__Group_4_2__2__Impl rule__ElkEdgeSection__Group_4_2__3 )
            // InternalElkGraph.g:3654:2: rule__ElkEdgeSection__Group_4_2__2__Impl rule__ElkEdgeSection__Group_4_2__3
            {
            pushFollow(FOLLOW_16);
            rule__ElkEdgeSection__Group_4_2__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_2__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_2__2"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_2__2__Impl"
    // InternalElkGraph.g:3661:1: rule__ElkEdgeSection__Group_4_2__2__Impl : ( ( rule__ElkEdgeSection__StartXAssignment_4_2_2 ) ) ;
    public final void rule__ElkEdgeSection__Group_4_2__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3665:1: ( ( ( rule__ElkEdgeSection__StartXAssignment_4_2_2 ) ) )
            // InternalElkGraph.g:3666:1: ( ( rule__ElkEdgeSection__StartXAssignment_4_2_2 ) )
            {
            // InternalElkGraph.g:3666:1: ( ( rule__ElkEdgeSection__StartXAssignment_4_2_2 ) )
            // InternalElkGraph.g:3667:2: ( rule__ElkEdgeSection__StartXAssignment_4_2_2 )
            {
             before(grammarAccess.getElkEdgeSectionAccess().getStartXAssignment_4_2_2()); 
            // InternalElkGraph.g:3668:2: ( rule__ElkEdgeSection__StartXAssignment_4_2_2 )
            // InternalElkGraph.g:3668:3: rule__ElkEdgeSection__StartXAssignment_4_2_2
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__StartXAssignment_4_2_2();

            state._fsp--;


            }

             after(grammarAccess.getElkEdgeSectionAccess().getStartXAssignment_4_2_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_2__2__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_2__3"
    // InternalElkGraph.g:3676:1: rule__ElkEdgeSection__Group_4_2__3 : rule__ElkEdgeSection__Group_4_2__3__Impl rule__ElkEdgeSection__Group_4_2__4 ;
    public final void rule__ElkEdgeSection__Group_4_2__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3680:1: ( rule__ElkEdgeSection__Group_4_2__3__Impl rule__ElkEdgeSection__Group_4_2__4 )
            // InternalElkGraph.g:3681:2: rule__ElkEdgeSection__Group_4_2__3__Impl rule__ElkEdgeSection__Group_4_2__4
            {
            pushFollow(FOLLOW_15);
            rule__ElkEdgeSection__Group_4_2__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_2__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_2__3"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_2__3__Impl"
    // InternalElkGraph.g:3688:1: rule__ElkEdgeSection__Group_4_2__3__Impl : ( ',' ) ;
    public final void rule__ElkEdgeSection__Group_4_2__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3692:1: ( ( ',' ) )
            // InternalElkGraph.g:3693:1: ( ',' )
            {
            // InternalElkGraph.g:3693:1: ( ',' )
            // InternalElkGraph.g:3694:2: ','
            {
             before(grammarAccess.getElkEdgeSectionAccess().getCommaKeyword_4_2_3()); 
            match(input,26,FOLLOW_2); 
             after(grammarAccess.getElkEdgeSectionAccess().getCommaKeyword_4_2_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_2__3__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_2__4"
    // InternalElkGraph.g:3703:1: rule__ElkEdgeSection__Group_4_2__4 : rule__ElkEdgeSection__Group_4_2__4__Impl ;
    public final void rule__ElkEdgeSection__Group_4_2__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3707:1: ( rule__ElkEdgeSection__Group_4_2__4__Impl )
            // InternalElkGraph.g:3708:2: rule__ElkEdgeSection__Group_4_2__4__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_2__4__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_2__4"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_2__4__Impl"
    // InternalElkGraph.g:3714:1: rule__ElkEdgeSection__Group_4_2__4__Impl : ( ( rule__ElkEdgeSection__StartYAssignment_4_2_4 ) ) ;
    public final void rule__ElkEdgeSection__Group_4_2__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3718:1: ( ( ( rule__ElkEdgeSection__StartYAssignment_4_2_4 ) ) )
            // InternalElkGraph.g:3719:1: ( ( rule__ElkEdgeSection__StartYAssignment_4_2_4 ) )
            {
            // InternalElkGraph.g:3719:1: ( ( rule__ElkEdgeSection__StartYAssignment_4_2_4 ) )
            // InternalElkGraph.g:3720:2: ( rule__ElkEdgeSection__StartYAssignment_4_2_4 )
            {
             before(grammarAccess.getElkEdgeSectionAccess().getStartYAssignment_4_2_4()); 
            // InternalElkGraph.g:3721:2: ( rule__ElkEdgeSection__StartYAssignment_4_2_4 )
            // InternalElkGraph.g:3721:3: rule__ElkEdgeSection__StartYAssignment_4_2_4
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__StartYAssignment_4_2_4();

            state._fsp--;


            }

             after(grammarAccess.getElkEdgeSectionAccess().getStartYAssignment_4_2_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_2__4__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_3__0"
    // InternalElkGraph.g:3730:1: rule__ElkEdgeSection__Group_4_3__0 : rule__ElkEdgeSection__Group_4_3__0__Impl rule__ElkEdgeSection__Group_4_3__1 ;
    public final void rule__ElkEdgeSection__Group_4_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3734:1: ( rule__ElkEdgeSection__Group_4_3__0__Impl rule__ElkEdgeSection__Group_4_3__1 )
            // InternalElkGraph.g:3735:2: rule__ElkEdgeSection__Group_4_3__0__Impl rule__ElkEdgeSection__Group_4_3__1
            {
            pushFollow(FOLLOW_11);
            rule__ElkEdgeSection__Group_4_3__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_3__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_3__0"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_3__0__Impl"
    // InternalElkGraph.g:3742:1: rule__ElkEdgeSection__Group_4_3__0__Impl : ( 'end' ) ;
    public final void rule__ElkEdgeSection__Group_4_3__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3746:1: ( ( 'end' ) )
            // InternalElkGraph.g:3747:1: ( 'end' )
            {
            // InternalElkGraph.g:3747:1: ( 'end' )
            // InternalElkGraph.g:3748:2: 'end'
            {
             before(grammarAccess.getElkEdgeSectionAccess().getEndKeyword_4_3_0()); 
            match(input,33,FOLLOW_2); 
             after(grammarAccess.getElkEdgeSectionAccess().getEndKeyword_4_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_3__0__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_3__1"
    // InternalElkGraph.g:3757:1: rule__ElkEdgeSection__Group_4_3__1 : rule__ElkEdgeSection__Group_4_3__1__Impl rule__ElkEdgeSection__Group_4_3__2 ;
    public final void rule__ElkEdgeSection__Group_4_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3761:1: ( rule__ElkEdgeSection__Group_4_3__1__Impl rule__ElkEdgeSection__Group_4_3__2 )
            // InternalElkGraph.g:3762:2: rule__ElkEdgeSection__Group_4_3__1__Impl rule__ElkEdgeSection__Group_4_3__2
            {
            pushFollow(FOLLOW_15);
            rule__ElkEdgeSection__Group_4_3__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_3__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_3__1"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_3__1__Impl"
    // InternalElkGraph.g:3769:1: rule__ElkEdgeSection__Group_4_3__1__Impl : ( ':' ) ;
    public final void rule__ElkEdgeSection__Group_4_3__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3773:1: ( ( ':' ) )
            // InternalElkGraph.g:3774:1: ( ':' )
            {
            // InternalElkGraph.g:3774:1: ( ':' )
            // InternalElkGraph.g:3775:2: ':'
            {
             before(grammarAccess.getElkEdgeSectionAccess().getColonKeyword_4_3_1()); 
            match(input,20,FOLLOW_2); 
             after(grammarAccess.getElkEdgeSectionAccess().getColonKeyword_4_3_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_3__1__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_3__2"
    // InternalElkGraph.g:3784:1: rule__ElkEdgeSection__Group_4_3__2 : rule__ElkEdgeSection__Group_4_3__2__Impl rule__ElkEdgeSection__Group_4_3__3 ;
    public final void rule__ElkEdgeSection__Group_4_3__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3788:1: ( rule__ElkEdgeSection__Group_4_3__2__Impl rule__ElkEdgeSection__Group_4_3__3 )
            // InternalElkGraph.g:3789:2: rule__ElkEdgeSection__Group_4_3__2__Impl rule__ElkEdgeSection__Group_4_3__3
            {
            pushFollow(FOLLOW_16);
            rule__ElkEdgeSection__Group_4_3__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_3__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_3__2"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_3__2__Impl"
    // InternalElkGraph.g:3796:1: rule__ElkEdgeSection__Group_4_3__2__Impl : ( ( rule__ElkEdgeSection__EndXAssignment_4_3_2 ) ) ;
    public final void rule__ElkEdgeSection__Group_4_3__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3800:1: ( ( ( rule__ElkEdgeSection__EndXAssignment_4_3_2 ) ) )
            // InternalElkGraph.g:3801:1: ( ( rule__ElkEdgeSection__EndXAssignment_4_3_2 ) )
            {
            // InternalElkGraph.g:3801:1: ( ( rule__ElkEdgeSection__EndXAssignment_4_3_2 ) )
            // InternalElkGraph.g:3802:2: ( rule__ElkEdgeSection__EndXAssignment_4_3_2 )
            {
             before(grammarAccess.getElkEdgeSectionAccess().getEndXAssignment_4_3_2()); 
            // InternalElkGraph.g:3803:2: ( rule__ElkEdgeSection__EndXAssignment_4_3_2 )
            // InternalElkGraph.g:3803:3: rule__ElkEdgeSection__EndXAssignment_4_3_2
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__EndXAssignment_4_3_2();

            state._fsp--;


            }

             after(grammarAccess.getElkEdgeSectionAccess().getEndXAssignment_4_3_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_3__2__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_3__3"
    // InternalElkGraph.g:3811:1: rule__ElkEdgeSection__Group_4_3__3 : rule__ElkEdgeSection__Group_4_3__3__Impl rule__ElkEdgeSection__Group_4_3__4 ;
    public final void rule__ElkEdgeSection__Group_4_3__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3815:1: ( rule__ElkEdgeSection__Group_4_3__3__Impl rule__ElkEdgeSection__Group_4_3__4 )
            // InternalElkGraph.g:3816:2: rule__ElkEdgeSection__Group_4_3__3__Impl rule__ElkEdgeSection__Group_4_3__4
            {
            pushFollow(FOLLOW_15);
            rule__ElkEdgeSection__Group_4_3__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_3__4();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_3__3"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_3__3__Impl"
    // InternalElkGraph.g:3823:1: rule__ElkEdgeSection__Group_4_3__3__Impl : ( ',' ) ;
    public final void rule__ElkEdgeSection__Group_4_3__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3827:1: ( ( ',' ) )
            // InternalElkGraph.g:3828:1: ( ',' )
            {
            // InternalElkGraph.g:3828:1: ( ',' )
            // InternalElkGraph.g:3829:2: ','
            {
             before(grammarAccess.getElkEdgeSectionAccess().getCommaKeyword_4_3_3()); 
            match(input,26,FOLLOW_2); 
             after(grammarAccess.getElkEdgeSectionAccess().getCommaKeyword_4_3_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_3__3__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_3__4"
    // InternalElkGraph.g:3838:1: rule__ElkEdgeSection__Group_4_3__4 : rule__ElkEdgeSection__Group_4_3__4__Impl ;
    public final void rule__ElkEdgeSection__Group_4_3__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3842:1: ( rule__ElkEdgeSection__Group_4_3__4__Impl )
            // InternalElkGraph.g:3843:2: rule__ElkEdgeSection__Group_4_3__4__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_3__4__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_3__4"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_3__4__Impl"
    // InternalElkGraph.g:3849:1: rule__ElkEdgeSection__Group_4_3__4__Impl : ( ( rule__ElkEdgeSection__EndYAssignment_4_3_4 ) ) ;
    public final void rule__ElkEdgeSection__Group_4_3__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3853:1: ( ( ( rule__ElkEdgeSection__EndYAssignment_4_3_4 ) ) )
            // InternalElkGraph.g:3854:1: ( ( rule__ElkEdgeSection__EndYAssignment_4_3_4 ) )
            {
            // InternalElkGraph.g:3854:1: ( ( rule__ElkEdgeSection__EndYAssignment_4_3_4 ) )
            // InternalElkGraph.g:3855:2: ( rule__ElkEdgeSection__EndYAssignment_4_3_4 )
            {
             before(grammarAccess.getElkEdgeSectionAccess().getEndYAssignment_4_3_4()); 
            // InternalElkGraph.g:3856:2: ( rule__ElkEdgeSection__EndYAssignment_4_3_4 )
            // InternalElkGraph.g:3856:3: rule__ElkEdgeSection__EndYAssignment_4_3_4
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__EndYAssignment_4_3_4();

            state._fsp--;


            }

             after(grammarAccess.getElkEdgeSectionAccess().getEndYAssignment_4_3_4()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_3__4__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_4__0"
    // InternalElkGraph.g:3865:1: rule__ElkEdgeSection__Group_4_4__0 : rule__ElkEdgeSection__Group_4_4__0__Impl rule__ElkEdgeSection__Group_4_4__1 ;
    public final void rule__ElkEdgeSection__Group_4_4__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3869:1: ( rule__ElkEdgeSection__Group_4_4__0__Impl rule__ElkEdgeSection__Group_4_4__1 )
            // InternalElkGraph.g:3870:2: rule__ElkEdgeSection__Group_4_4__0__Impl rule__ElkEdgeSection__Group_4_4__1
            {
            pushFollow(FOLLOW_11);
            rule__ElkEdgeSection__Group_4_4__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_4__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_4__0"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_4__0__Impl"
    // InternalElkGraph.g:3877:1: rule__ElkEdgeSection__Group_4_4__0__Impl : ( 'bends' ) ;
    public final void rule__ElkEdgeSection__Group_4_4__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3881:1: ( ( 'bends' ) )
            // InternalElkGraph.g:3882:1: ( 'bends' )
            {
            // InternalElkGraph.g:3882:1: ( 'bends' )
            // InternalElkGraph.g:3883:2: 'bends'
            {
             before(grammarAccess.getElkEdgeSectionAccess().getBendsKeyword_4_4_0()); 
            match(input,34,FOLLOW_2); 
             after(grammarAccess.getElkEdgeSectionAccess().getBendsKeyword_4_4_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_4__0__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_4__1"
    // InternalElkGraph.g:3892:1: rule__ElkEdgeSection__Group_4_4__1 : rule__ElkEdgeSection__Group_4_4__1__Impl rule__ElkEdgeSection__Group_4_4__2 ;
    public final void rule__ElkEdgeSection__Group_4_4__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3896:1: ( rule__ElkEdgeSection__Group_4_4__1__Impl rule__ElkEdgeSection__Group_4_4__2 )
            // InternalElkGraph.g:3897:2: rule__ElkEdgeSection__Group_4_4__1__Impl rule__ElkEdgeSection__Group_4_4__2
            {
            pushFollow(FOLLOW_15);
            rule__ElkEdgeSection__Group_4_4__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_4__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_4__1"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_4__1__Impl"
    // InternalElkGraph.g:3904:1: rule__ElkEdgeSection__Group_4_4__1__Impl : ( ':' ) ;
    public final void rule__ElkEdgeSection__Group_4_4__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3908:1: ( ( ':' ) )
            // InternalElkGraph.g:3909:1: ( ':' )
            {
            // InternalElkGraph.g:3909:1: ( ':' )
            // InternalElkGraph.g:3910:2: ':'
            {
             before(grammarAccess.getElkEdgeSectionAccess().getColonKeyword_4_4_1()); 
            match(input,20,FOLLOW_2); 
             after(grammarAccess.getElkEdgeSectionAccess().getColonKeyword_4_4_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_4__1__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_4__2"
    // InternalElkGraph.g:3919:1: rule__ElkEdgeSection__Group_4_4__2 : rule__ElkEdgeSection__Group_4_4__2__Impl rule__ElkEdgeSection__Group_4_4__3 ;
    public final void rule__ElkEdgeSection__Group_4_4__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3923:1: ( rule__ElkEdgeSection__Group_4_4__2__Impl rule__ElkEdgeSection__Group_4_4__3 )
            // InternalElkGraph.g:3924:2: rule__ElkEdgeSection__Group_4_4__2__Impl rule__ElkEdgeSection__Group_4_4__3
            {
            pushFollow(FOLLOW_22);
            rule__ElkEdgeSection__Group_4_4__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_4__3();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_4__2"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_4__2__Impl"
    // InternalElkGraph.g:3931:1: rule__ElkEdgeSection__Group_4_4__2__Impl : ( ( rule__ElkEdgeSection__BendPointsAssignment_4_4_2 ) ) ;
    public final void rule__ElkEdgeSection__Group_4_4__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3935:1: ( ( ( rule__ElkEdgeSection__BendPointsAssignment_4_4_2 ) ) )
            // InternalElkGraph.g:3936:1: ( ( rule__ElkEdgeSection__BendPointsAssignment_4_4_2 ) )
            {
            // InternalElkGraph.g:3936:1: ( ( rule__ElkEdgeSection__BendPointsAssignment_4_4_2 ) )
            // InternalElkGraph.g:3937:2: ( rule__ElkEdgeSection__BendPointsAssignment_4_4_2 )
            {
             before(grammarAccess.getElkEdgeSectionAccess().getBendPointsAssignment_4_4_2()); 
            // InternalElkGraph.g:3938:2: ( rule__ElkEdgeSection__BendPointsAssignment_4_4_2 )
            // InternalElkGraph.g:3938:3: rule__ElkEdgeSection__BendPointsAssignment_4_4_2
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__BendPointsAssignment_4_4_2();

            state._fsp--;


            }

             after(grammarAccess.getElkEdgeSectionAccess().getBendPointsAssignment_4_4_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_4__2__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_4__3"
    // InternalElkGraph.g:3946:1: rule__ElkEdgeSection__Group_4_4__3 : rule__ElkEdgeSection__Group_4_4__3__Impl ;
    public final void rule__ElkEdgeSection__Group_4_4__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3950:1: ( rule__ElkEdgeSection__Group_4_4__3__Impl )
            // InternalElkGraph.g:3951:2: rule__ElkEdgeSection__Group_4_4__3__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_4__3__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_4__3"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_4__3__Impl"
    // InternalElkGraph.g:3957:1: rule__ElkEdgeSection__Group_4_4__3__Impl : ( ( rule__ElkEdgeSection__Group_4_4_3__0 )* ) ;
    public final void rule__ElkEdgeSection__Group_4_4__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3961:1: ( ( ( rule__ElkEdgeSection__Group_4_4_3__0 )* ) )
            // InternalElkGraph.g:3962:1: ( ( rule__ElkEdgeSection__Group_4_4_3__0 )* )
            {
            // InternalElkGraph.g:3962:1: ( ( rule__ElkEdgeSection__Group_4_4_3__0 )* )
            // InternalElkGraph.g:3963:2: ( rule__ElkEdgeSection__Group_4_4_3__0 )*
            {
             before(grammarAccess.getElkEdgeSectionAccess().getGroup_4_4_3()); 
            // InternalElkGraph.g:3964:2: ( rule__ElkEdgeSection__Group_4_4_3__0 )*
            loop34:
            do {
                int alt34=2;
                int LA34_0 = input.LA(1);

                if ( (LA34_0==35) ) {
                    alt34=1;
                }


                switch (alt34) {
            	case 1 :
            	    // InternalElkGraph.g:3964:3: rule__ElkEdgeSection__Group_4_4_3__0
            	    {
            	    pushFollow(FOLLOW_23);
            	    rule__ElkEdgeSection__Group_4_4_3__0();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop34;
                }
            } while (true);

             after(grammarAccess.getElkEdgeSectionAccess().getGroup_4_4_3()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_4__3__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_4_3__0"
    // InternalElkGraph.g:3973:1: rule__ElkEdgeSection__Group_4_4_3__0 : rule__ElkEdgeSection__Group_4_4_3__0__Impl rule__ElkEdgeSection__Group_4_4_3__1 ;
    public final void rule__ElkEdgeSection__Group_4_4_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3977:1: ( rule__ElkEdgeSection__Group_4_4_3__0__Impl rule__ElkEdgeSection__Group_4_4_3__1 )
            // InternalElkGraph.g:3978:2: rule__ElkEdgeSection__Group_4_4_3__0__Impl rule__ElkEdgeSection__Group_4_4_3__1
            {
            pushFollow(FOLLOW_15);
            rule__ElkEdgeSection__Group_4_4_3__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_4_3__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_4_3__0"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_4_3__0__Impl"
    // InternalElkGraph.g:3985:1: rule__ElkEdgeSection__Group_4_4_3__0__Impl : ( '|' ) ;
    public final void rule__ElkEdgeSection__Group_4_4_3__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3989:1: ( ( '|' ) )
            // InternalElkGraph.g:3990:1: ( '|' )
            {
            // InternalElkGraph.g:3990:1: ( '|' )
            // InternalElkGraph.g:3991:2: '|'
            {
             before(grammarAccess.getElkEdgeSectionAccess().getVerticalLineKeyword_4_4_3_0()); 
            match(input,35,FOLLOW_2); 
             after(grammarAccess.getElkEdgeSectionAccess().getVerticalLineKeyword_4_4_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_4_3__0__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_4_3__1"
    // InternalElkGraph.g:4000:1: rule__ElkEdgeSection__Group_4_4_3__1 : rule__ElkEdgeSection__Group_4_4_3__1__Impl ;
    public final void rule__ElkEdgeSection__Group_4_4_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4004:1: ( rule__ElkEdgeSection__Group_4_4_3__1__Impl )
            // InternalElkGraph.g:4005:2: rule__ElkEdgeSection__Group_4_4_3__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_4_3__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_4_3__1"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_4_3__1__Impl"
    // InternalElkGraph.g:4011:1: rule__ElkEdgeSection__Group_4_4_3__1__Impl : ( ( rule__ElkEdgeSection__BendPointsAssignment_4_4_3_1 ) ) ;
    public final void rule__ElkEdgeSection__Group_4_4_3__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4015:1: ( ( ( rule__ElkEdgeSection__BendPointsAssignment_4_4_3_1 ) ) )
            // InternalElkGraph.g:4016:1: ( ( rule__ElkEdgeSection__BendPointsAssignment_4_4_3_1 ) )
            {
            // InternalElkGraph.g:4016:1: ( ( rule__ElkEdgeSection__BendPointsAssignment_4_4_3_1 ) )
            // InternalElkGraph.g:4017:2: ( rule__ElkEdgeSection__BendPointsAssignment_4_4_3_1 )
            {
             before(grammarAccess.getElkEdgeSectionAccess().getBendPointsAssignment_4_4_3_1()); 
            // InternalElkGraph.g:4018:2: ( rule__ElkEdgeSection__BendPointsAssignment_4_4_3_1 )
            // InternalElkGraph.g:4018:3: rule__ElkEdgeSection__BendPointsAssignment_4_4_3_1
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__BendPointsAssignment_4_4_3_1();

            state._fsp--;


            }

             after(grammarAccess.getElkEdgeSectionAccess().getBendPointsAssignment_4_4_3_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__Group_4_4_3__1__Impl"


    // $ANTLR start "rule__ElkBendPoint__Group__0"
    // InternalElkGraph.g:4027:1: rule__ElkBendPoint__Group__0 : rule__ElkBendPoint__Group__0__Impl rule__ElkBendPoint__Group__1 ;
    public final void rule__ElkBendPoint__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4031:1: ( rule__ElkBendPoint__Group__0__Impl rule__ElkBendPoint__Group__1 )
            // InternalElkGraph.g:4032:2: rule__ElkBendPoint__Group__0__Impl rule__ElkBendPoint__Group__1
            {
            pushFollow(FOLLOW_16);
            rule__ElkBendPoint__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkBendPoint__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkBendPoint__Group__0"


    // $ANTLR start "rule__ElkBendPoint__Group__0__Impl"
    // InternalElkGraph.g:4039:1: rule__ElkBendPoint__Group__0__Impl : ( ( rule__ElkBendPoint__XAssignment_0 ) ) ;
    public final void rule__ElkBendPoint__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4043:1: ( ( ( rule__ElkBendPoint__XAssignment_0 ) ) )
            // InternalElkGraph.g:4044:1: ( ( rule__ElkBendPoint__XAssignment_0 ) )
            {
            // InternalElkGraph.g:4044:1: ( ( rule__ElkBendPoint__XAssignment_0 ) )
            // InternalElkGraph.g:4045:2: ( rule__ElkBendPoint__XAssignment_0 )
            {
             before(grammarAccess.getElkBendPointAccess().getXAssignment_0()); 
            // InternalElkGraph.g:4046:2: ( rule__ElkBendPoint__XAssignment_0 )
            // InternalElkGraph.g:4046:3: rule__ElkBendPoint__XAssignment_0
            {
            pushFollow(FOLLOW_2);
            rule__ElkBendPoint__XAssignment_0();

            state._fsp--;


            }

             after(grammarAccess.getElkBendPointAccess().getXAssignment_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkBendPoint__Group__0__Impl"


    // $ANTLR start "rule__ElkBendPoint__Group__1"
    // InternalElkGraph.g:4054:1: rule__ElkBendPoint__Group__1 : rule__ElkBendPoint__Group__1__Impl rule__ElkBendPoint__Group__2 ;
    public final void rule__ElkBendPoint__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4058:1: ( rule__ElkBendPoint__Group__1__Impl rule__ElkBendPoint__Group__2 )
            // InternalElkGraph.g:4059:2: rule__ElkBendPoint__Group__1__Impl rule__ElkBendPoint__Group__2
            {
            pushFollow(FOLLOW_15);
            rule__ElkBendPoint__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkBendPoint__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkBendPoint__Group__1"


    // $ANTLR start "rule__ElkBendPoint__Group__1__Impl"
    // InternalElkGraph.g:4066:1: rule__ElkBendPoint__Group__1__Impl : ( ',' ) ;
    public final void rule__ElkBendPoint__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4070:1: ( ( ',' ) )
            // InternalElkGraph.g:4071:1: ( ',' )
            {
            // InternalElkGraph.g:4071:1: ( ',' )
            // InternalElkGraph.g:4072:2: ','
            {
             before(grammarAccess.getElkBendPointAccess().getCommaKeyword_1()); 
            match(input,26,FOLLOW_2); 
             after(grammarAccess.getElkBendPointAccess().getCommaKeyword_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkBendPoint__Group__1__Impl"


    // $ANTLR start "rule__ElkBendPoint__Group__2"
    // InternalElkGraph.g:4081:1: rule__ElkBendPoint__Group__2 : rule__ElkBendPoint__Group__2__Impl ;
    public final void rule__ElkBendPoint__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4085:1: ( rule__ElkBendPoint__Group__2__Impl )
            // InternalElkGraph.g:4086:2: rule__ElkBendPoint__Group__2__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkBendPoint__Group__2__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkBendPoint__Group__2"


    // $ANTLR start "rule__ElkBendPoint__Group__2__Impl"
    // InternalElkGraph.g:4092:1: rule__ElkBendPoint__Group__2__Impl : ( ( rule__ElkBendPoint__YAssignment_2 ) ) ;
    public final void rule__ElkBendPoint__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4096:1: ( ( ( rule__ElkBendPoint__YAssignment_2 ) ) )
            // InternalElkGraph.g:4097:1: ( ( rule__ElkBendPoint__YAssignment_2 ) )
            {
            // InternalElkGraph.g:4097:1: ( ( rule__ElkBendPoint__YAssignment_2 ) )
            // InternalElkGraph.g:4098:2: ( rule__ElkBendPoint__YAssignment_2 )
            {
             before(grammarAccess.getElkBendPointAccess().getYAssignment_2()); 
            // InternalElkGraph.g:4099:2: ( rule__ElkBendPoint__YAssignment_2 )
            // InternalElkGraph.g:4099:3: rule__ElkBendPoint__YAssignment_2
            {
            pushFollow(FOLLOW_2);
            rule__ElkBendPoint__YAssignment_2();

            state._fsp--;


            }

             after(grammarAccess.getElkBendPointAccess().getYAssignment_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkBendPoint__Group__2__Impl"


    // $ANTLR start "rule__Property__Group__0"
    // InternalElkGraph.g:4108:1: rule__Property__Group__0 : rule__Property__Group__0__Impl rule__Property__Group__1 ;
    public final void rule__Property__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4112:1: ( rule__Property__Group__0__Impl rule__Property__Group__1 )
            // InternalElkGraph.g:4113:2: rule__Property__Group__0__Impl rule__Property__Group__1
            {
            pushFollow(FOLLOW_11);
            rule__Property__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Property__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Property__Group__0"


    // $ANTLR start "rule__Property__Group__0__Impl"
    // InternalElkGraph.g:4120:1: rule__Property__Group__0__Impl : ( ( rule__Property__KeyAssignment_0 ) ) ;
    public final void rule__Property__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4124:1: ( ( ( rule__Property__KeyAssignment_0 ) ) )
            // InternalElkGraph.g:4125:1: ( ( rule__Property__KeyAssignment_0 ) )
            {
            // InternalElkGraph.g:4125:1: ( ( rule__Property__KeyAssignment_0 ) )
            // InternalElkGraph.g:4126:2: ( rule__Property__KeyAssignment_0 )
            {
             before(grammarAccess.getPropertyAccess().getKeyAssignment_0()); 
            // InternalElkGraph.g:4127:2: ( rule__Property__KeyAssignment_0 )
            // InternalElkGraph.g:4127:3: rule__Property__KeyAssignment_0
            {
            pushFollow(FOLLOW_2);
            rule__Property__KeyAssignment_0();

            state._fsp--;


            }

             after(grammarAccess.getPropertyAccess().getKeyAssignment_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Property__Group__0__Impl"


    // $ANTLR start "rule__Property__Group__1"
    // InternalElkGraph.g:4135:1: rule__Property__Group__1 : rule__Property__Group__1__Impl rule__Property__Group__2 ;
    public final void rule__Property__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4139:1: ( rule__Property__Group__1__Impl rule__Property__Group__2 )
            // InternalElkGraph.g:4140:2: rule__Property__Group__1__Impl rule__Property__Group__2
            {
            pushFollow(FOLLOW_25);
            rule__Property__Group__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__Property__Group__2();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Property__Group__1"


    // $ANTLR start "rule__Property__Group__1__Impl"
    // InternalElkGraph.g:4147:1: rule__Property__Group__1__Impl : ( ':' ) ;
    public final void rule__Property__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4151:1: ( ( ':' ) )
            // InternalElkGraph.g:4152:1: ( ':' )
            {
            // InternalElkGraph.g:4152:1: ( ':' )
            // InternalElkGraph.g:4153:2: ':'
            {
             before(grammarAccess.getPropertyAccess().getColonKeyword_1()); 
            match(input,20,FOLLOW_2); 
             after(grammarAccess.getPropertyAccess().getColonKeyword_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Property__Group__1__Impl"


    // $ANTLR start "rule__Property__Group__2"
    // InternalElkGraph.g:4162:1: rule__Property__Group__2 : rule__Property__Group__2__Impl ;
    public final void rule__Property__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4166:1: ( rule__Property__Group__2__Impl )
            // InternalElkGraph.g:4167:2: rule__Property__Group__2__Impl
            {
            pushFollow(FOLLOW_2);
            rule__Property__Group__2__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Property__Group__2"


    // $ANTLR start "rule__Property__Group__2__Impl"
    // InternalElkGraph.g:4173:1: rule__Property__Group__2__Impl : ( ( rule__Property__Alternatives_2 ) ) ;
    public final void rule__Property__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4177:1: ( ( ( rule__Property__Alternatives_2 ) ) )
            // InternalElkGraph.g:4178:1: ( ( rule__Property__Alternatives_2 ) )
            {
            // InternalElkGraph.g:4178:1: ( ( rule__Property__Alternatives_2 ) )
            // InternalElkGraph.g:4179:2: ( rule__Property__Alternatives_2 )
            {
             before(grammarAccess.getPropertyAccess().getAlternatives_2()); 
            // InternalElkGraph.g:4180:2: ( rule__Property__Alternatives_2 )
            // InternalElkGraph.g:4180:3: rule__Property__Alternatives_2
            {
            pushFollow(FOLLOW_2);
            rule__Property__Alternatives_2();

            state._fsp--;


            }

             after(grammarAccess.getPropertyAccess().getAlternatives_2()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Property__Group__2__Impl"


    // $ANTLR start "rule__QualifiedId__Group__0"
    // InternalElkGraph.g:4189:1: rule__QualifiedId__Group__0 : rule__QualifiedId__Group__0__Impl rule__QualifiedId__Group__1 ;
    public final void rule__QualifiedId__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4193:1: ( rule__QualifiedId__Group__0__Impl rule__QualifiedId__Group__1 )
            // InternalElkGraph.g:4194:2: rule__QualifiedId__Group__0__Impl rule__QualifiedId__Group__1
            {
            pushFollow(FOLLOW_26);
            rule__QualifiedId__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__QualifiedId__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__QualifiedId__Group__0"


    // $ANTLR start "rule__QualifiedId__Group__0__Impl"
    // InternalElkGraph.g:4201:1: rule__QualifiedId__Group__0__Impl : ( RULE_ID ) ;
    public final void rule__QualifiedId__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4205:1: ( ( RULE_ID ) )
            // InternalElkGraph.g:4206:1: ( RULE_ID )
            {
            // InternalElkGraph.g:4206:1: ( RULE_ID )
            // InternalElkGraph.g:4207:2: RULE_ID
            {
             before(grammarAccess.getQualifiedIdAccess().getIDTerminalRuleCall_0()); 
            match(input,RULE_ID,FOLLOW_2); 
             after(grammarAccess.getQualifiedIdAccess().getIDTerminalRuleCall_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__QualifiedId__Group__0__Impl"


    // $ANTLR start "rule__QualifiedId__Group__1"
    // InternalElkGraph.g:4216:1: rule__QualifiedId__Group__1 : rule__QualifiedId__Group__1__Impl ;
    public final void rule__QualifiedId__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4220:1: ( rule__QualifiedId__Group__1__Impl )
            // InternalElkGraph.g:4221:2: rule__QualifiedId__Group__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__QualifiedId__Group__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__QualifiedId__Group__1"


    // $ANTLR start "rule__QualifiedId__Group__1__Impl"
    // InternalElkGraph.g:4227:1: rule__QualifiedId__Group__1__Impl : ( ( rule__QualifiedId__Group_1__0 )* ) ;
    public final void rule__QualifiedId__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4231:1: ( ( ( rule__QualifiedId__Group_1__0 )* ) )
            // InternalElkGraph.g:4232:1: ( ( rule__QualifiedId__Group_1__0 )* )
            {
            // InternalElkGraph.g:4232:1: ( ( rule__QualifiedId__Group_1__0 )* )
            // InternalElkGraph.g:4233:2: ( rule__QualifiedId__Group_1__0 )*
            {
             before(grammarAccess.getQualifiedIdAccess().getGroup_1()); 
            // InternalElkGraph.g:4234:2: ( rule__QualifiedId__Group_1__0 )*
            loop35:
            do {
                int alt35=2;
                int LA35_0 = input.LA(1);

                if ( (LA35_0==37) ) {
                    alt35=1;
                }


                switch (alt35) {
            	case 1 :
            	    // InternalElkGraph.g:4234:3: rule__QualifiedId__Group_1__0
            	    {
            	    pushFollow(FOLLOW_27);
            	    rule__QualifiedId__Group_1__0();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop35;
                }
            } while (true);

             after(grammarAccess.getQualifiedIdAccess().getGroup_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__QualifiedId__Group__1__Impl"


    // $ANTLR start "rule__QualifiedId__Group_1__0"
    // InternalElkGraph.g:4243:1: rule__QualifiedId__Group_1__0 : rule__QualifiedId__Group_1__0__Impl rule__QualifiedId__Group_1__1 ;
    public final void rule__QualifiedId__Group_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4247:1: ( rule__QualifiedId__Group_1__0__Impl rule__QualifiedId__Group_1__1 )
            // InternalElkGraph.g:4248:2: rule__QualifiedId__Group_1__0__Impl rule__QualifiedId__Group_1__1
            {
            pushFollow(FOLLOW_7);
            rule__QualifiedId__Group_1__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__QualifiedId__Group_1__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__QualifiedId__Group_1__0"


    // $ANTLR start "rule__QualifiedId__Group_1__0__Impl"
    // InternalElkGraph.g:4255:1: rule__QualifiedId__Group_1__0__Impl : ( '.' ) ;
    public final void rule__QualifiedId__Group_1__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4259:1: ( ( '.' ) )
            // InternalElkGraph.g:4260:1: ( '.' )
            {
            // InternalElkGraph.g:4260:1: ( '.' )
            // InternalElkGraph.g:4261:2: '.'
            {
             before(grammarAccess.getQualifiedIdAccess().getFullStopKeyword_1_0()); 
            match(input,37,FOLLOW_2); 
             after(grammarAccess.getQualifiedIdAccess().getFullStopKeyword_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__QualifiedId__Group_1__0__Impl"


    // $ANTLR start "rule__QualifiedId__Group_1__1"
    // InternalElkGraph.g:4270:1: rule__QualifiedId__Group_1__1 : rule__QualifiedId__Group_1__1__Impl ;
    public final void rule__QualifiedId__Group_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4274:1: ( rule__QualifiedId__Group_1__1__Impl )
            // InternalElkGraph.g:4275:2: rule__QualifiedId__Group_1__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__QualifiedId__Group_1__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__QualifiedId__Group_1__1"


    // $ANTLR start "rule__QualifiedId__Group_1__1__Impl"
    // InternalElkGraph.g:4281:1: rule__QualifiedId__Group_1__1__Impl : ( RULE_ID ) ;
    public final void rule__QualifiedId__Group_1__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4285:1: ( ( RULE_ID ) )
            // InternalElkGraph.g:4286:1: ( RULE_ID )
            {
            // InternalElkGraph.g:4286:1: ( RULE_ID )
            // InternalElkGraph.g:4287:2: RULE_ID
            {
             before(grammarAccess.getQualifiedIdAccess().getIDTerminalRuleCall_1_1()); 
            match(input,RULE_ID,FOLLOW_2); 
             after(grammarAccess.getQualifiedIdAccess().getIDTerminalRuleCall_1_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__QualifiedId__Group_1__1__Impl"


    // $ANTLR start "rule__PropertyKey__Group__0"
    // InternalElkGraph.g:4297:1: rule__PropertyKey__Group__0 : rule__PropertyKey__Group__0__Impl rule__PropertyKey__Group__1 ;
    public final void rule__PropertyKey__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4301:1: ( rule__PropertyKey__Group__0__Impl rule__PropertyKey__Group__1 )
            // InternalElkGraph.g:4302:2: rule__PropertyKey__Group__0__Impl rule__PropertyKey__Group__1
            {
            pushFollow(FOLLOW_26);
            rule__PropertyKey__Group__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__PropertyKey__Group__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PropertyKey__Group__0"


    // $ANTLR start "rule__PropertyKey__Group__0__Impl"
    // InternalElkGraph.g:4309:1: rule__PropertyKey__Group__0__Impl : ( RULE_ID ) ;
    public final void rule__PropertyKey__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4313:1: ( ( RULE_ID ) )
            // InternalElkGraph.g:4314:1: ( RULE_ID )
            {
            // InternalElkGraph.g:4314:1: ( RULE_ID )
            // InternalElkGraph.g:4315:2: RULE_ID
            {
             before(grammarAccess.getPropertyKeyAccess().getIDTerminalRuleCall_0()); 
            match(input,RULE_ID,FOLLOW_2); 
             after(grammarAccess.getPropertyKeyAccess().getIDTerminalRuleCall_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PropertyKey__Group__0__Impl"


    // $ANTLR start "rule__PropertyKey__Group__1"
    // InternalElkGraph.g:4324:1: rule__PropertyKey__Group__1 : rule__PropertyKey__Group__1__Impl ;
    public final void rule__PropertyKey__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4328:1: ( rule__PropertyKey__Group__1__Impl )
            // InternalElkGraph.g:4329:2: rule__PropertyKey__Group__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__PropertyKey__Group__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PropertyKey__Group__1"


    // $ANTLR start "rule__PropertyKey__Group__1__Impl"
    // InternalElkGraph.g:4335:1: rule__PropertyKey__Group__1__Impl : ( ( rule__PropertyKey__Group_1__0 )* ) ;
    public final void rule__PropertyKey__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4339:1: ( ( ( rule__PropertyKey__Group_1__0 )* ) )
            // InternalElkGraph.g:4340:1: ( ( rule__PropertyKey__Group_1__0 )* )
            {
            // InternalElkGraph.g:4340:1: ( ( rule__PropertyKey__Group_1__0 )* )
            // InternalElkGraph.g:4341:2: ( rule__PropertyKey__Group_1__0 )*
            {
             before(grammarAccess.getPropertyKeyAccess().getGroup_1()); 
            // InternalElkGraph.g:4342:2: ( rule__PropertyKey__Group_1__0 )*
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);

                if ( (LA36_0==37) ) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // InternalElkGraph.g:4342:3: rule__PropertyKey__Group_1__0
            	    {
            	    pushFollow(FOLLOW_27);
            	    rule__PropertyKey__Group_1__0();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop36;
                }
            } while (true);

             after(grammarAccess.getPropertyKeyAccess().getGroup_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PropertyKey__Group__1__Impl"


    // $ANTLR start "rule__PropertyKey__Group_1__0"
    // InternalElkGraph.g:4351:1: rule__PropertyKey__Group_1__0 : rule__PropertyKey__Group_1__0__Impl rule__PropertyKey__Group_1__1 ;
    public final void rule__PropertyKey__Group_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4355:1: ( rule__PropertyKey__Group_1__0__Impl rule__PropertyKey__Group_1__1 )
            // InternalElkGraph.g:4356:2: rule__PropertyKey__Group_1__0__Impl rule__PropertyKey__Group_1__1
            {
            pushFollow(FOLLOW_7);
            rule__PropertyKey__Group_1__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__PropertyKey__Group_1__1();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PropertyKey__Group_1__0"


    // $ANTLR start "rule__PropertyKey__Group_1__0__Impl"
    // InternalElkGraph.g:4363:1: rule__PropertyKey__Group_1__0__Impl : ( '.' ) ;
    public final void rule__PropertyKey__Group_1__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4367:1: ( ( '.' ) )
            // InternalElkGraph.g:4368:1: ( '.' )
            {
            // InternalElkGraph.g:4368:1: ( '.' )
            // InternalElkGraph.g:4369:2: '.'
            {
             before(grammarAccess.getPropertyKeyAccess().getFullStopKeyword_1_0()); 
            match(input,37,FOLLOW_2); 
             after(grammarAccess.getPropertyKeyAccess().getFullStopKeyword_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PropertyKey__Group_1__0__Impl"


    // $ANTLR start "rule__PropertyKey__Group_1__1"
    // InternalElkGraph.g:4378:1: rule__PropertyKey__Group_1__1 : rule__PropertyKey__Group_1__1__Impl ;
    public final void rule__PropertyKey__Group_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4382:1: ( rule__PropertyKey__Group_1__1__Impl )
            // InternalElkGraph.g:4383:2: rule__PropertyKey__Group_1__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__PropertyKey__Group_1__1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PropertyKey__Group_1__1"


    // $ANTLR start "rule__PropertyKey__Group_1__1__Impl"
    // InternalElkGraph.g:4389:1: rule__PropertyKey__Group_1__1__Impl : ( RULE_ID ) ;
    public final void rule__PropertyKey__Group_1__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4393:1: ( ( RULE_ID ) )
            // InternalElkGraph.g:4394:1: ( RULE_ID )
            {
            // InternalElkGraph.g:4394:1: ( RULE_ID )
            // InternalElkGraph.g:4395:2: RULE_ID
            {
             before(grammarAccess.getPropertyKeyAccess().getIDTerminalRuleCall_1_1()); 
            match(input,RULE_ID,FOLLOW_2); 
             after(grammarAccess.getPropertyKeyAccess().getIDTerminalRuleCall_1_1()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__PropertyKey__Group_1__1__Impl"


    // $ANTLR start "rule__ShapeLayout__UnorderedGroup_2"
    // InternalElkGraph.g:4405:1: rule__ShapeLayout__UnorderedGroup_2 : ( rule__ShapeLayout__UnorderedGroup_2__0 )? ;
    public final void rule__ShapeLayout__UnorderedGroup_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        		getUnorderedGroupHelper().enter(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2());
        	
        try {
            // InternalElkGraph.g:4410:1: ( ( rule__ShapeLayout__UnorderedGroup_2__0 )? )
            // InternalElkGraph.g:4411:2: ( rule__ShapeLayout__UnorderedGroup_2__0 )?
            {
            // InternalElkGraph.g:4411:2: ( rule__ShapeLayout__UnorderedGroup_2__0 )?
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( LA37_0 == 25 && getUnorderedGroupHelper().canSelect(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2(), 0) ) {
                alt37=1;
            }
            else if ( LA37_0 == 27 && getUnorderedGroupHelper().canSelect(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2(), 1) ) {
                alt37=1;
            }
            switch (alt37) {
                case 1 :
                    // InternalElkGraph.g:4411:2: rule__ShapeLayout__UnorderedGroup_2__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ShapeLayout__UnorderedGroup_2__0();

                    state._fsp--;


                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	getUnorderedGroupHelper().leave(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2());
            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__UnorderedGroup_2"


    // $ANTLR start "rule__ShapeLayout__UnorderedGroup_2__Impl"
    // InternalElkGraph.g:4419:1: rule__ShapeLayout__UnorderedGroup_2__Impl : ( ({...}? => ( ( ( rule__ShapeLayout__Group_2_0__0 ) ) ) ) | ({...}? => ( ( ( rule__ShapeLayout__Group_2_1__0 ) ) ) ) ) ;
    public final void rule__ShapeLayout__UnorderedGroup_2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        		boolean selected = false;
        	
        try {
            // InternalElkGraph.g:4424:1: ( ( ({...}? => ( ( ( rule__ShapeLayout__Group_2_0__0 ) ) ) ) | ({...}? => ( ( ( rule__ShapeLayout__Group_2_1__0 ) ) ) ) ) )
            // InternalElkGraph.g:4425:3: ( ({...}? => ( ( ( rule__ShapeLayout__Group_2_0__0 ) ) ) ) | ({...}? => ( ( ( rule__ShapeLayout__Group_2_1__0 ) ) ) ) )
            {
            // InternalElkGraph.g:4425:3: ( ({...}? => ( ( ( rule__ShapeLayout__Group_2_0__0 ) ) ) ) | ({...}? => ( ( ( rule__ShapeLayout__Group_2_1__0 ) ) ) ) )
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( LA38_0 == 25 && getUnorderedGroupHelper().canSelect(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2(), 0) ) {
                alt38=1;
            }
            else if ( LA38_0 == 27 && getUnorderedGroupHelper().canSelect(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2(), 1) ) {
                alt38=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 38, 0, input);

                throw nvae;
            }
            switch (alt38) {
                case 1 :
                    // InternalElkGraph.g:4426:3: ({...}? => ( ( ( rule__ShapeLayout__Group_2_0__0 ) ) ) )
                    {
                    // InternalElkGraph.g:4426:3: ({...}? => ( ( ( rule__ShapeLayout__Group_2_0__0 ) ) ) )
                    // InternalElkGraph.g:4427:4: {...}? => ( ( ( rule__ShapeLayout__Group_2_0__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2(), 0) ) {
                        throw new FailedPredicateException(input, "rule__ShapeLayout__UnorderedGroup_2__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2(), 0)");
                    }
                    // InternalElkGraph.g:4427:107: ( ( ( rule__ShapeLayout__Group_2_0__0 ) ) )
                    // InternalElkGraph.g:4428:5: ( ( rule__ShapeLayout__Group_2_0__0 ) )
                    {

                    					getUnorderedGroupHelper().select(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2(), 0);
                    				

                    					selected = true;
                    				
                    // InternalElkGraph.g:4434:5: ( ( rule__ShapeLayout__Group_2_0__0 ) )
                    // InternalElkGraph.g:4435:6: ( rule__ShapeLayout__Group_2_0__0 )
                    {
                     before(grammarAccess.getShapeLayoutAccess().getGroup_2_0()); 
                    // InternalElkGraph.g:4436:6: ( rule__ShapeLayout__Group_2_0__0 )
                    // InternalElkGraph.g:4436:7: rule__ShapeLayout__Group_2_0__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ShapeLayout__Group_2_0__0();

                    state._fsp--;


                    }

                     after(grammarAccess.getShapeLayoutAccess().getGroup_2_0()); 

                    }


                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalElkGraph.g:4441:3: ({...}? => ( ( ( rule__ShapeLayout__Group_2_1__0 ) ) ) )
                    {
                    // InternalElkGraph.g:4441:3: ({...}? => ( ( ( rule__ShapeLayout__Group_2_1__0 ) ) ) )
                    // InternalElkGraph.g:4442:4: {...}? => ( ( ( rule__ShapeLayout__Group_2_1__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2(), 1) ) {
                        throw new FailedPredicateException(input, "rule__ShapeLayout__UnorderedGroup_2__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2(), 1)");
                    }
                    // InternalElkGraph.g:4442:107: ( ( ( rule__ShapeLayout__Group_2_1__0 ) ) )
                    // InternalElkGraph.g:4443:5: ( ( rule__ShapeLayout__Group_2_1__0 ) )
                    {

                    					getUnorderedGroupHelper().select(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2(), 1);
                    				

                    					selected = true;
                    				
                    // InternalElkGraph.g:4449:5: ( ( rule__ShapeLayout__Group_2_1__0 ) )
                    // InternalElkGraph.g:4450:6: ( rule__ShapeLayout__Group_2_1__0 )
                    {
                     before(grammarAccess.getShapeLayoutAccess().getGroup_2_1()); 
                    // InternalElkGraph.g:4451:6: ( rule__ShapeLayout__Group_2_1__0 )
                    // InternalElkGraph.g:4451:7: rule__ShapeLayout__Group_2_1__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ShapeLayout__Group_2_1__0();

                    state._fsp--;


                    }

                     after(grammarAccess.getShapeLayoutAccess().getGroup_2_1()); 

                    }


                    }


                    }


                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	if (selected)
            		getUnorderedGroupHelper().returnFromSelection(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2());
            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__UnorderedGroup_2__Impl"


    // $ANTLR start "rule__ShapeLayout__UnorderedGroup_2__0"
    // InternalElkGraph.g:4464:1: rule__ShapeLayout__UnorderedGroup_2__0 : rule__ShapeLayout__UnorderedGroup_2__Impl ( rule__ShapeLayout__UnorderedGroup_2__1 )? ;
    public final void rule__ShapeLayout__UnorderedGroup_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4468:1: ( rule__ShapeLayout__UnorderedGroup_2__Impl ( rule__ShapeLayout__UnorderedGroup_2__1 )? )
            // InternalElkGraph.g:4469:2: rule__ShapeLayout__UnorderedGroup_2__Impl ( rule__ShapeLayout__UnorderedGroup_2__1 )?
            {
            pushFollow(FOLLOW_28);
            rule__ShapeLayout__UnorderedGroup_2__Impl();

            state._fsp--;

            // InternalElkGraph.g:4470:2: ( rule__ShapeLayout__UnorderedGroup_2__1 )?
            int alt39=2;
            int LA39_0 = input.LA(1);

            if ( LA39_0 == 25 && getUnorderedGroupHelper().canSelect(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2(), 0) ) {
                alt39=1;
            }
            else if ( LA39_0 == 27 && getUnorderedGroupHelper().canSelect(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2(), 1) ) {
                alt39=1;
            }
            switch (alt39) {
                case 1 :
                    // InternalElkGraph.g:4470:2: rule__ShapeLayout__UnorderedGroup_2__1
                    {
                    pushFollow(FOLLOW_2);
                    rule__ShapeLayout__UnorderedGroup_2__1();

                    state._fsp--;


                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__UnorderedGroup_2__0"


    // $ANTLR start "rule__ShapeLayout__UnorderedGroup_2__1"
    // InternalElkGraph.g:4476:1: rule__ShapeLayout__UnorderedGroup_2__1 : rule__ShapeLayout__UnorderedGroup_2__Impl ;
    public final void rule__ShapeLayout__UnorderedGroup_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4480:1: ( rule__ShapeLayout__UnorderedGroup_2__Impl )
            // InternalElkGraph.g:4481:2: rule__ShapeLayout__UnorderedGroup_2__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ShapeLayout__UnorderedGroup_2__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__UnorderedGroup_2__1"


    // $ANTLR start "rule__ElkSingleEdgeSection__UnorderedGroup_1"
    // InternalElkGraph.g:4488:1: rule__ElkSingleEdgeSection__UnorderedGroup_1 : ( rule__ElkSingleEdgeSection__UnorderedGroup_1__0 )? ;
    public final void rule__ElkSingleEdgeSection__UnorderedGroup_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        		getUnorderedGroupHelper().enter(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1());
        	
        try {
            // InternalElkGraph.g:4493:1: ( ( rule__ElkSingleEdgeSection__UnorderedGroup_1__0 )? )
            // InternalElkGraph.g:4494:2: ( rule__ElkSingleEdgeSection__UnorderedGroup_1__0 )?
            {
            // InternalElkGraph.g:4494:2: ( rule__ElkSingleEdgeSection__UnorderedGroup_1__0 )?
            int alt40=2;
            int LA40_0 = input.LA(1);

            if ( LA40_0 == 30 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 0) ) {
                alt40=1;
            }
            else if ( LA40_0 == 31 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 1) ) {
                alt40=1;
            }
            else if ( LA40_0 == 32 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 2) ) {
                alt40=1;
            }
            else if ( LA40_0 == 33 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 3) ) {
                alt40=1;
            }
            else if ( LA40_0 == 34 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 4) ) {
                alt40=1;
            }
            switch (alt40) {
                case 1 :
                    // InternalElkGraph.g:4494:2: rule__ElkSingleEdgeSection__UnorderedGroup_1__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkSingleEdgeSection__UnorderedGroup_1__0();

                    state._fsp--;


                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	getUnorderedGroupHelper().leave(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1());
            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__UnorderedGroup_1"


    // $ANTLR start "rule__ElkSingleEdgeSection__UnorderedGroup_1__Impl"
    // InternalElkGraph.g:4502:1: rule__ElkSingleEdgeSection__UnorderedGroup_1__Impl : ( ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_1__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_2__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_3__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_4__0 ) ) ) ) ) ;
    public final void rule__ElkSingleEdgeSection__UnorderedGroup_1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        		boolean selected = false;
        	
        try {
            // InternalElkGraph.g:4507:1: ( ( ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_1__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_2__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_3__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_4__0 ) ) ) ) ) )
            // InternalElkGraph.g:4508:3: ( ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_1__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_2__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_3__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_4__0 ) ) ) ) )
            {
            // InternalElkGraph.g:4508:3: ( ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_1__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_2__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_3__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_4__0 ) ) ) ) )
            int alt41=5;
            int LA41_0 = input.LA(1);

            if ( LA41_0 == 30 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 0) ) {
                alt41=1;
            }
            else if ( LA41_0 == 31 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 1) ) {
                alt41=2;
            }
            else if ( LA41_0 == 32 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 2) ) {
                alt41=3;
            }
            else if ( LA41_0 == 33 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 3) ) {
                alt41=4;
            }
            else if ( LA41_0 == 34 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 4) ) {
                alt41=5;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 41, 0, input);

                throw nvae;
            }
            switch (alt41) {
                case 1 :
                    // InternalElkGraph.g:4509:3: ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0__0 ) ) ) )
                    {
                    // InternalElkGraph.g:4509:3: ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0__0 ) ) ) )
                    // InternalElkGraph.g:4510:4: {...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 0) ) {
                        throw new FailedPredicateException(input, "rule__ElkSingleEdgeSection__UnorderedGroup_1__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 0)");
                    }
                    // InternalElkGraph.g:4510:116: ( ( ( rule__ElkSingleEdgeSection__Group_1_0__0 ) ) )
                    // InternalElkGraph.g:4511:5: ( ( rule__ElkSingleEdgeSection__Group_1_0__0 ) )
                    {

                    					getUnorderedGroupHelper().select(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 0);
                    				

                    					selected = true;
                    				
                    // InternalElkGraph.g:4517:5: ( ( rule__ElkSingleEdgeSection__Group_1_0__0 ) )
                    // InternalElkGraph.g:4518:6: ( rule__ElkSingleEdgeSection__Group_1_0__0 )
                    {
                     before(grammarAccess.getElkSingleEdgeSectionAccess().getGroup_1_0()); 
                    // InternalElkGraph.g:4519:6: ( rule__ElkSingleEdgeSection__Group_1_0__0 )
                    // InternalElkGraph.g:4519:7: rule__ElkSingleEdgeSection__Group_1_0__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkSingleEdgeSection__Group_1_0__0();

                    state._fsp--;


                    }

                     after(grammarAccess.getElkSingleEdgeSectionAccess().getGroup_1_0()); 

                    }


                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalElkGraph.g:4524:3: ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_1__0 ) ) ) )
                    {
                    // InternalElkGraph.g:4524:3: ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_1__0 ) ) ) )
                    // InternalElkGraph.g:4525:4: {...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_1__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 1) ) {
                        throw new FailedPredicateException(input, "rule__ElkSingleEdgeSection__UnorderedGroup_1__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 1)");
                    }
                    // InternalElkGraph.g:4525:116: ( ( ( rule__ElkSingleEdgeSection__Group_1_1__0 ) ) )
                    // InternalElkGraph.g:4526:5: ( ( rule__ElkSingleEdgeSection__Group_1_1__0 ) )
                    {

                    					getUnorderedGroupHelper().select(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 1);
                    				

                    					selected = true;
                    				
                    // InternalElkGraph.g:4532:5: ( ( rule__ElkSingleEdgeSection__Group_1_1__0 ) )
                    // InternalElkGraph.g:4533:6: ( rule__ElkSingleEdgeSection__Group_1_1__0 )
                    {
                     before(grammarAccess.getElkSingleEdgeSectionAccess().getGroup_1_1()); 
                    // InternalElkGraph.g:4534:6: ( rule__ElkSingleEdgeSection__Group_1_1__0 )
                    // InternalElkGraph.g:4534:7: rule__ElkSingleEdgeSection__Group_1_1__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkSingleEdgeSection__Group_1_1__0();

                    state._fsp--;


                    }

                     after(grammarAccess.getElkSingleEdgeSectionAccess().getGroup_1_1()); 

                    }


                    }


                    }


                    }
                    break;
                case 3 :
                    // InternalElkGraph.g:4539:3: ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_2__0 ) ) ) )
                    {
                    // InternalElkGraph.g:4539:3: ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_2__0 ) ) ) )
                    // InternalElkGraph.g:4540:4: {...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_2__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 2) ) {
                        throw new FailedPredicateException(input, "rule__ElkSingleEdgeSection__UnorderedGroup_1__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 2)");
                    }
                    // InternalElkGraph.g:4540:116: ( ( ( rule__ElkSingleEdgeSection__Group_1_2__0 ) ) )
                    // InternalElkGraph.g:4541:5: ( ( rule__ElkSingleEdgeSection__Group_1_2__0 ) )
                    {

                    					getUnorderedGroupHelper().select(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 2);
                    				

                    					selected = true;
                    				
                    // InternalElkGraph.g:4547:5: ( ( rule__ElkSingleEdgeSection__Group_1_2__0 ) )
                    // InternalElkGraph.g:4548:6: ( rule__ElkSingleEdgeSection__Group_1_2__0 )
                    {
                     before(grammarAccess.getElkSingleEdgeSectionAccess().getGroup_1_2()); 
                    // InternalElkGraph.g:4549:6: ( rule__ElkSingleEdgeSection__Group_1_2__0 )
                    // InternalElkGraph.g:4549:7: rule__ElkSingleEdgeSection__Group_1_2__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkSingleEdgeSection__Group_1_2__0();

                    state._fsp--;


                    }

                     after(grammarAccess.getElkSingleEdgeSectionAccess().getGroup_1_2()); 

                    }


                    }


                    }


                    }
                    break;
                case 4 :
                    // InternalElkGraph.g:4554:3: ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_3__0 ) ) ) )
                    {
                    // InternalElkGraph.g:4554:3: ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_3__0 ) ) ) )
                    // InternalElkGraph.g:4555:4: {...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_3__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 3) ) {
                        throw new FailedPredicateException(input, "rule__ElkSingleEdgeSection__UnorderedGroup_1__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 3)");
                    }
                    // InternalElkGraph.g:4555:116: ( ( ( rule__ElkSingleEdgeSection__Group_1_3__0 ) ) )
                    // InternalElkGraph.g:4556:5: ( ( rule__ElkSingleEdgeSection__Group_1_3__0 ) )
                    {

                    					getUnorderedGroupHelper().select(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 3);
                    				

                    					selected = true;
                    				
                    // InternalElkGraph.g:4562:5: ( ( rule__ElkSingleEdgeSection__Group_1_3__0 ) )
                    // InternalElkGraph.g:4563:6: ( rule__ElkSingleEdgeSection__Group_1_3__0 )
                    {
                     before(grammarAccess.getElkSingleEdgeSectionAccess().getGroup_1_3()); 
                    // InternalElkGraph.g:4564:6: ( rule__ElkSingleEdgeSection__Group_1_3__0 )
                    // InternalElkGraph.g:4564:7: rule__ElkSingleEdgeSection__Group_1_3__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkSingleEdgeSection__Group_1_3__0();

                    state._fsp--;


                    }

                     after(grammarAccess.getElkSingleEdgeSectionAccess().getGroup_1_3()); 

                    }


                    }


                    }


                    }
                    break;
                case 5 :
                    // InternalElkGraph.g:4569:3: ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_4__0 ) ) ) )
                    {
                    // InternalElkGraph.g:4569:3: ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_4__0 ) ) ) )
                    // InternalElkGraph.g:4570:4: {...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_4__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 4) ) {
                        throw new FailedPredicateException(input, "rule__ElkSingleEdgeSection__UnorderedGroup_1__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 4)");
                    }
                    // InternalElkGraph.g:4570:116: ( ( ( rule__ElkSingleEdgeSection__Group_1_4__0 ) ) )
                    // InternalElkGraph.g:4571:5: ( ( rule__ElkSingleEdgeSection__Group_1_4__0 ) )
                    {

                    					getUnorderedGroupHelper().select(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 4);
                    				

                    					selected = true;
                    				
                    // InternalElkGraph.g:4577:5: ( ( rule__ElkSingleEdgeSection__Group_1_4__0 ) )
                    // InternalElkGraph.g:4578:6: ( rule__ElkSingleEdgeSection__Group_1_4__0 )
                    {
                     before(grammarAccess.getElkSingleEdgeSectionAccess().getGroup_1_4()); 
                    // InternalElkGraph.g:4579:6: ( rule__ElkSingleEdgeSection__Group_1_4__0 )
                    // InternalElkGraph.g:4579:7: rule__ElkSingleEdgeSection__Group_1_4__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkSingleEdgeSection__Group_1_4__0();

                    state._fsp--;


                    }

                     after(grammarAccess.getElkSingleEdgeSectionAccess().getGroup_1_4()); 

                    }


                    }


                    }


                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	if (selected)
            		getUnorderedGroupHelper().returnFromSelection(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1());
            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__UnorderedGroup_1__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__UnorderedGroup_1__0"
    // InternalElkGraph.g:4592:1: rule__ElkSingleEdgeSection__UnorderedGroup_1__0 : rule__ElkSingleEdgeSection__UnorderedGroup_1__Impl ( rule__ElkSingleEdgeSection__UnorderedGroup_1__1 )? ;
    public final void rule__ElkSingleEdgeSection__UnorderedGroup_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4596:1: ( rule__ElkSingleEdgeSection__UnorderedGroup_1__Impl ( rule__ElkSingleEdgeSection__UnorderedGroup_1__1 )? )
            // InternalElkGraph.g:4597:2: rule__ElkSingleEdgeSection__UnorderedGroup_1__Impl ( rule__ElkSingleEdgeSection__UnorderedGroup_1__1 )?
            {
            pushFollow(FOLLOW_29);
            rule__ElkSingleEdgeSection__UnorderedGroup_1__Impl();

            state._fsp--;

            // InternalElkGraph.g:4598:2: ( rule__ElkSingleEdgeSection__UnorderedGroup_1__1 )?
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( LA42_0 == 30 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 0) ) {
                alt42=1;
            }
            else if ( LA42_0 == 31 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 1) ) {
                alt42=1;
            }
            else if ( LA42_0 == 32 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 2) ) {
                alt42=1;
            }
            else if ( LA42_0 == 33 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 3) ) {
                alt42=1;
            }
            else if ( LA42_0 == 34 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 4) ) {
                alt42=1;
            }
            switch (alt42) {
                case 1 :
                    // InternalElkGraph.g:4598:2: rule__ElkSingleEdgeSection__UnorderedGroup_1__1
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkSingleEdgeSection__UnorderedGroup_1__1();

                    state._fsp--;


                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__UnorderedGroup_1__0"


    // $ANTLR start "rule__ElkSingleEdgeSection__UnorderedGroup_1__1"
    // InternalElkGraph.g:4604:1: rule__ElkSingleEdgeSection__UnorderedGroup_1__1 : rule__ElkSingleEdgeSection__UnorderedGroup_1__Impl ( rule__ElkSingleEdgeSection__UnorderedGroup_1__2 )? ;
    public final void rule__ElkSingleEdgeSection__UnorderedGroup_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4608:1: ( rule__ElkSingleEdgeSection__UnorderedGroup_1__Impl ( rule__ElkSingleEdgeSection__UnorderedGroup_1__2 )? )
            // InternalElkGraph.g:4609:2: rule__ElkSingleEdgeSection__UnorderedGroup_1__Impl ( rule__ElkSingleEdgeSection__UnorderedGroup_1__2 )?
            {
            pushFollow(FOLLOW_29);
            rule__ElkSingleEdgeSection__UnorderedGroup_1__Impl();

            state._fsp--;

            // InternalElkGraph.g:4610:2: ( rule__ElkSingleEdgeSection__UnorderedGroup_1__2 )?
            int alt43=2;
            int LA43_0 = input.LA(1);

            if ( LA43_0 == 30 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 0) ) {
                alt43=1;
            }
            else if ( LA43_0 == 31 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 1) ) {
                alt43=1;
            }
            else if ( LA43_0 == 32 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 2) ) {
                alt43=1;
            }
            else if ( LA43_0 == 33 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 3) ) {
                alt43=1;
            }
            else if ( LA43_0 == 34 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 4) ) {
                alt43=1;
            }
            switch (alt43) {
                case 1 :
                    // InternalElkGraph.g:4610:2: rule__ElkSingleEdgeSection__UnorderedGroup_1__2
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkSingleEdgeSection__UnorderedGroup_1__2();

                    state._fsp--;


                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__UnorderedGroup_1__1"


    // $ANTLR start "rule__ElkSingleEdgeSection__UnorderedGroup_1__2"
    // InternalElkGraph.g:4616:1: rule__ElkSingleEdgeSection__UnorderedGroup_1__2 : rule__ElkSingleEdgeSection__UnorderedGroup_1__Impl ( rule__ElkSingleEdgeSection__UnorderedGroup_1__3 )? ;
    public final void rule__ElkSingleEdgeSection__UnorderedGroup_1__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4620:1: ( rule__ElkSingleEdgeSection__UnorderedGroup_1__Impl ( rule__ElkSingleEdgeSection__UnorderedGroup_1__3 )? )
            // InternalElkGraph.g:4621:2: rule__ElkSingleEdgeSection__UnorderedGroup_1__Impl ( rule__ElkSingleEdgeSection__UnorderedGroup_1__3 )?
            {
            pushFollow(FOLLOW_29);
            rule__ElkSingleEdgeSection__UnorderedGroup_1__Impl();

            state._fsp--;

            // InternalElkGraph.g:4622:2: ( rule__ElkSingleEdgeSection__UnorderedGroup_1__3 )?
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( LA44_0 == 30 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 0) ) {
                alt44=1;
            }
            else if ( LA44_0 == 31 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 1) ) {
                alt44=1;
            }
            else if ( LA44_0 == 32 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 2) ) {
                alt44=1;
            }
            else if ( LA44_0 == 33 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 3) ) {
                alt44=1;
            }
            else if ( LA44_0 == 34 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 4) ) {
                alt44=1;
            }
            switch (alt44) {
                case 1 :
                    // InternalElkGraph.g:4622:2: rule__ElkSingleEdgeSection__UnorderedGroup_1__3
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkSingleEdgeSection__UnorderedGroup_1__3();

                    state._fsp--;


                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__UnorderedGroup_1__2"


    // $ANTLR start "rule__ElkSingleEdgeSection__UnorderedGroup_1__3"
    // InternalElkGraph.g:4628:1: rule__ElkSingleEdgeSection__UnorderedGroup_1__3 : rule__ElkSingleEdgeSection__UnorderedGroup_1__Impl ( rule__ElkSingleEdgeSection__UnorderedGroup_1__4 )? ;
    public final void rule__ElkSingleEdgeSection__UnorderedGroup_1__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4632:1: ( rule__ElkSingleEdgeSection__UnorderedGroup_1__Impl ( rule__ElkSingleEdgeSection__UnorderedGroup_1__4 )? )
            // InternalElkGraph.g:4633:2: rule__ElkSingleEdgeSection__UnorderedGroup_1__Impl ( rule__ElkSingleEdgeSection__UnorderedGroup_1__4 )?
            {
            pushFollow(FOLLOW_29);
            rule__ElkSingleEdgeSection__UnorderedGroup_1__Impl();

            state._fsp--;

            // InternalElkGraph.g:4634:2: ( rule__ElkSingleEdgeSection__UnorderedGroup_1__4 )?
            int alt45=2;
            int LA45_0 = input.LA(1);

            if ( LA45_0 == 30 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 0) ) {
                alt45=1;
            }
            else if ( LA45_0 == 31 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 1) ) {
                alt45=1;
            }
            else if ( LA45_0 == 32 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 2) ) {
                alt45=1;
            }
            else if ( LA45_0 == 33 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 3) ) {
                alt45=1;
            }
            else if ( LA45_0 == 34 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1(), 4) ) {
                alt45=1;
            }
            switch (alt45) {
                case 1 :
                    // InternalElkGraph.g:4634:2: rule__ElkSingleEdgeSection__UnorderedGroup_1__4
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkSingleEdgeSection__UnorderedGroup_1__4();

                    state._fsp--;


                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__UnorderedGroup_1__3"


    // $ANTLR start "rule__ElkSingleEdgeSection__UnorderedGroup_1__4"
    // InternalElkGraph.g:4640:1: rule__ElkSingleEdgeSection__UnorderedGroup_1__4 : rule__ElkSingleEdgeSection__UnorderedGroup_1__Impl ;
    public final void rule__ElkSingleEdgeSection__UnorderedGroup_1__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4644:1: ( rule__ElkSingleEdgeSection__UnorderedGroup_1__Impl )
            // InternalElkGraph.g:4645:2: rule__ElkSingleEdgeSection__UnorderedGroup_1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__UnorderedGroup_1__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__UnorderedGroup_1__4"


    // $ANTLR start "rule__ElkEdgeSection__UnorderedGroup_4"
    // InternalElkGraph.g:4652:1: rule__ElkEdgeSection__UnorderedGroup_4 : ( rule__ElkEdgeSection__UnorderedGroup_4__0 )? ;
    public final void rule__ElkEdgeSection__UnorderedGroup_4() throws RecognitionException {

        		int stackSize = keepStackSize();
        		getUnorderedGroupHelper().enter(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4());
        	
        try {
            // InternalElkGraph.g:4657:1: ( ( rule__ElkEdgeSection__UnorderedGroup_4__0 )? )
            // InternalElkGraph.g:4658:2: ( rule__ElkEdgeSection__UnorderedGroup_4__0 )?
            {
            // InternalElkGraph.g:4658:2: ( rule__ElkEdgeSection__UnorderedGroup_4__0 )?
            int alt46=2;
            int LA46_0 = input.LA(1);

            if ( LA46_0 == 30 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 0) ) {
                alt46=1;
            }
            else if ( LA46_0 == 31 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 1) ) {
                alt46=1;
            }
            else if ( LA46_0 == 32 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 2) ) {
                alt46=1;
            }
            else if ( LA46_0 == 33 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 3) ) {
                alt46=1;
            }
            else if ( LA46_0 == 34 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 4) ) {
                alt46=1;
            }
            switch (alt46) {
                case 1 :
                    // InternalElkGraph.g:4658:2: rule__ElkEdgeSection__UnorderedGroup_4__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkEdgeSection__UnorderedGroup_4__0();

                    state._fsp--;


                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	getUnorderedGroupHelper().leave(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4());
            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__UnorderedGroup_4"


    // $ANTLR start "rule__ElkEdgeSection__UnorderedGroup_4__Impl"
    // InternalElkGraph.g:4666:1: rule__ElkEdgeSection__UnorderedGroup_4__Impl : ( ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_0__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_1__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_2__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_3__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_4__0 ) ) ) ) ) ;
    public final void rule__ElkEdgeSection__UnorderedGroup_4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        		boolean selected = false;
        	
        try {
            // InternalElkGraph.g:4671:1: ( ( ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_0__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_1__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_2__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_3__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_4__0 ) ) ) ) ) )
            // InternalElkGraph.g:4672:3: ( ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_0__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_1__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_2__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_3__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_4__0 ) ) ) ) )
            {
            // InternalElkGraph.g:4672:3: ( ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_0__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_1__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_2__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_3__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_4__0 ) ) ) ) )
            int alt47=5;
            int LA47_0 = input.LA(1);

            if ( LA47_0 == 30 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 0) ) {
                alt47=1;
            }
            else if ( LA47_0 == 31 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 1) ) {
                alt47=2;
            }
            else if ( LA47_0 == 32 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 2) ) {
                alt47=3;
            }
            else if ( LA47_0 == 33 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 3) ) {
                alt47=4;
            }
            else if ( LA47_0 == 34 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 4) ) {
                alt47=5;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 47, 0, input);

                throw nvae;
            }
            switch (alt47) {
                case 1 :
                    // InternalElkGraph.g:4673:3: ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_0__0 ) ) ) )
                    {
                    // InternalElkGraph.g:4673:3: ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_0__0 ) ) ) )
                    // InternalElkGraph.g:4674:4: {...}? => ( ( ( rule__ElkEdgeSection__Group_4_0__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 0) ) {
                        throw new FailedPredicateException(input, "rule__ElkEdgeSection__UnorderedGroup_4__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 0)");
                    }
                    // InternalElkGraph.g:4674:110: ( ( ( rule__ElkEdgeSection__Group_4_0__0 ) ) )
                    // InternalElkGraph.g:4675:5: ( ( rule__ElkEdgeSection__Group_4_0__0 ) )
                    {

                    					getUnorderedGroupHelper().select(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 0);
                    				

                    					selected = true;
                    				
                    // InternalElkGraph.g:4681:5: ( ( rule__ElkEdgeSection__Group_4_0__0 ) )
                    // InternalElkGraph.g:4682:6: ( rule__ElkEdgeSection__Group_4_0__0 )
                    {
                     before(grammarAccess.getElkEdgeSectionAccess().getGroup_4_0()); 
                    // InternalElkGraph.g:4683:6: ( rule__ElkEdgeSection__Group_4_0__0 )
                    // InternalElkGraph.g:4683:7: rule__ElkEdgeSection__Group_4_0__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkEdgeSection__Group_4_0__0();

                    state._fsp--;


                    }

                     after(grammarAccess.getElkEdgeSectionAccess().getGroup_4_0()); 

                    }


                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalElkGraph.g:4688:3: ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_1__0 ) ) ) )
                    {
                    // InternalElkGraph.g:4688:3: ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_1__0 ) ) ) )
                    // InternalElkGraph.g:4689:4: {...}? => ( ( ( rule__ElkEdgeSection__Group_4_1__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 1) ) {
                        throw new FailedPredicateException(input, "rule__ElkEdgeSection__UnorderedGroup_4__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 1)");
                    }
                    // InternalElkGraph.g:4689:110: ( ( ( rule__ElkEdgeSection__Group_4_1__0 ) ) )
                    // InternalElkGraph.g:4690:5: ( ( rule__ElkEdgeSection__Group_4_1__0 ) )
                    {

                    					getUnorderedGroupHelper().select(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 1);
                    				

                    					selected = true;
                    				
                    // InternalElkGraph.g:4696:5: ( ( rule__ElkEdgeSection__Group_4_1__0 ) )
                    // InternalElkGraph.g:4697:6: ( rule__ElkEdgeSection__Group_4_1__0 )
                    {
                     before(grammarAccess.getElkEdgeSectionAccess().getGroup_4_1()); 
                    // InternalElkGraph.g:4698:6: ( rule__ElkEdgeSection__Group_4_1__0 )
                    // InternalElkGraph.g:4698:7: rule__ElkEdgeSection__Group_4_1__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkEdgeSection__Group_4_1__0();

                    state._fsp--;


                    }

                     after(grammarAccess.getElkEdgeSectionAccess().getGroup_4_1()); 

                    }


                    }


                    }


                    }
                    break;
                case 3 :
                    // InternalElkGraph.g:4703:3: ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_2__0 ) ) ) )
                    {
                    // InternalElkGraph.g:4703:3: ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_2__0 ) ) ) )
                    // InternalElkGraph.g:4704:4: {...}? => ( ( ( rule__ElkEdgeSection__Group_4_2__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 2) ) {
                        throw new FailedPredicateException(input, "rule__ElkEdgeSection__UnorderedGroup_4__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 2)");
                    }
                    // InternalElkGraph.g:4704:110: ( ( ( rule__ElkEdgeSection__Group_4_2__0 ) ) )
                    // InternalElkGraph.g:4705:5: ( ( rule__ElkEdgeSection__Group_4_2__0 ) )
                    {

                    					getUnorderedGroupHelper().select(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 2);
                    				

                    					selected = true;
                    				
                    // InternalElkGraph.g:4711:5: ( ( rule__ElkEdgeSection__Group_4_2__0 ) )
                    // InternalElkGraph.g:4712:6: ( rule__ElkEdgeSection__Group_4_2__0 )
                    {
                     before(grammarAccess.getElkEdgeSectionAccess().getGroup_4_2()); 
                    // InternalElkGraph.g:4713:6: ( rule__ElkEdgeSection__Group_4_2__0 )
                    // InternalElkGraph.g:4713:7: rule__ElkEdgeSection__Group_4_2__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkEdgeSection__Group_4_2__0();

                    state._fsp--;


                    }

                     after(grammarAccess.getElkEdgeSectionAccess().getGroup_4_2()); 

                    }


                    }


                    }


                    }
                    break;
                case 4 :
                    // InternalElkGraph.g:4718:3: ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_3__0 ) ) ) )
                    {
                    // InternalElkGraph.g:4718:3: ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_3__0 ) ) ) )
                    // InternalElkGraph.g:4719:4: {...}? => ( ( ( rule__ElkEdgeSection__Group_4_3__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 3) ) {
                        throw new FailedPredicateException(input, "rule__ElkEdgeSection__UnorderedGroup_4__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 3)");
                    }
                    // InternalElkGraph.g:4719:110: ( ( ( rule__ElkEdgeSection__Group_4_3__0 ) ) )
                    // InternalElkGraph.g:4720:5: ( ( rule__ElkEdgeSection__Group_4_3__0 ) )
                    {

                    					getUnorderedGroupHelper().select(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 3);
                    				

                    					selected = true;
                    				
                    // InternalElkGraph.g:4726:5: ( ( rule__ElkEdgeSection__Group_4_3__0 ) )
                    // InternalElkGraph.g:4727:6: ( rule__ElkEdgeSection__Group_4_3__0 )
                    {
                     before(grammarAccess.getElkEdgeSectionAccess().getGroup_4_3()); 
                    // InternalElkGraph.g:4728:6: ( rule__ElkEdgeSection__Group_4_3__0 )
                    // InternalElkGraph.g:4728:7: rule__ElkEdgeSection__Group_4_3__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkEdgeSection__Group_4_3__0();

                    state._fsp--;


                    }

                     after(grammarAccess.getElkEdgeSectionAccess().getGroup_4_3()); 

                    }


                    }


                    }


                    }
                    break;
                case 5 :
                    // InternalElkGraph.g:4733:3: ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_4__0 ) ) ) )
                    {
                    // InternalElkGraph.g:4733:3: ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_4__0 ) ) ) )
                    // InternalElkGraph.g:4734:4: {...}? => ( ( ( rule__ElkEdgeSection__Group_4_4__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 4) ) {
                        throw new FailedPredicateException(input, "rule__ElkEdgeSection__UnorderedGroup_4__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 4)");
                    }
                    // InternalElkGraph.g:4734:110: ( ( ( rule__ElkEdgeSection__Group_4_4__0 ) ) )
                    // InternalElkGraph.g:4735:5: ( ( rule__ElkEdgeSection__Group_4_4__0 ) )
                    {

                    					getUnorderedGroupHelper().select(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 4);
                    				

                    					selected = true;
                    				
                    // InternalElkGraph.g:4741:5: ( ( rule__ElkEdgeSection__Group_4_4__0 ) )
                    // InternalElkGraph.g:4742:6: ( rule__ElkEdgeSection__Group_4_4__0 )
                    {
                     before(grammarAccess.getElkEdgeSectionAccess().getGroup_4_4()); 
                    // InternalElkGraph.g:4743:6: ( rule__ElkEdgeSection__Group_4_4__0 )
                    // InternalElkGraph.g:4743:7: rule__ElkEdgeSection__Group_4_4__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkEdgeSection__Group_4_4__0();

                    state._fsp--;


                    }

                     after(grammarAccess.getElkEdgeSectionAccess().getGroup_4_4()); 

                    }


                    }


                    }


                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	if (selected)
            		getUnorderedGroupHelper().returnFromSelection(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4());
            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__UnorderedGroup_4__Impl"


    // $ANTLR start "rule__ElkEdgeSection__UnorderedGroup_4__0"
    // InternalElkGraph.g:4756:1: rule__ElkEdgeSection__UnorderedGroup_4__0 : rule__ElkEdgeSection__UnorderedGroup_4__Impl ( rule__ElkEdgeSection__UnorderedGroup_4__1 )? ;
    public final void rule__ElkEdgeSection__UnorderedGroup_4__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4760:1: ( rule__ElkEdgeSection__UnorderedGroup_4__Impl ( rule__ElkEdgeSection__UnorderedGroup_4__1 )? )
            // InternalElkGraph.g:4761:2: rule__ElkEdgeSection__UnorderedGroup_4__Impl ( rule__ElkEdgeSection__UnorderedGroup_4__1 )?
            {
            pushFollow(FOLLOW_29);
            rule__ElkEdgeSection__UnorderedGroup_4__Impl();

            state._fsp--;

            // InternalElkGraph.g:4762:2: ( rule__ElkEdgeSection__UnorderedGroup_4__1 )?
            int alt48=2;
            int LA48_0 = input.LA(1);

            if ( LA48_0 == 30 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 0) ) {
                alt48=1;
            }
            else if ( LA48_0 == 31 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 1) ) {
                alt48=1;
            }
            else if ( LA48_0 == 32 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 2) ) {
                alt48=1;
            }
            else if ( LA48_0 == 33 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 3) ) {
                alt48=1;
            }
            else if ( LA48_0 == 34 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 4) ) {
                alt48=1;
            }
            switch (alt48) {
                case 1 :
                    // InternalElkGraph.g:4762:2: rule__ElkEdgeSection__UnorderedGroup_4__1
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkEdgeSection__UnorderedGroup_4__1();

                    state._fsp--;


                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__UnorderedGroup_4__0"


    // $ANTLR start "rule__ElkEdgeSection__UnorderedGroup_4__1"
    // InternalElkGraph.g:4768:1: rule__ElkEdgeSection__UnorderedGroup_4__1 : rule__ElkEdgeSection__UnorderedGroup_4__Impl ( rule__ElkEdgeSection__UnorderedGroup_4__2 )? ;
    public final void rule__ElkEdgeSection__UnorderedGroup_4__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4772:1: ( rule__ElkEdgeSection__UnorderedGroup_4__Impl ( rule__ElkEdgeSection__UnorderedGroup_4__2 )? )
            // InternalElkGraph.g:4773:2: rule__ElkEdgeSection__UnorderedGroup_4__Impl ( rule__ElkEdgeSection__UnorderedGroup_4__2 )?
            {
            pushFollow(FOLLOW_29);
            rule__ElkEdgeSection__UnorderedGroup_4__Impl();

            state._fsp--;

            // InternalElkGraph.g:4774:2: ( rule__ElkEdgeSection__UnorderedGroup_4__2 )?
            int alt49=2;
            int LA49_0 = input.LA(1);

            if ( LA49_0 == 30 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 0) ) {
                alt49=1;
            }
            else if ( LA49_0 == 31 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 1) ) {
                alt49=1;
            }
            else if ( LA49_0 == 32 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 2) ) {
                alt49=1;
            }
            else if ( LA49_0 == 33 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 3) ) {
                alt49=1;
            }
            else if ( LA49_0 == 34 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 4) ) {
                alt49=1;
            }
            switch (alt49) {
                case 1 :
                    // InternalElkGraph.g:4774:2: rule__ElkEdgeSection__UnorderedGroup_4__2
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkEdgeSection__UnorderedGroup_4__2();

                    state._fsp--;


                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__UnorderedGroup_4__1"


    // $ANTLR start "rule__ElkEdgeSection__UnorderedGroup_4__2"
    // InternalElkGraph.g:4780:1: rule__ElkEdgeSection__UnorderedGroup_4__2 : rule__ElkEdgeSection__UnorderedGroup_4__Impl ( rule__ElkEdgeSection__UnorderedGroup_4__3 )? ;
    public final void rule__ElkEdgeSection__UnorderedGroup_4__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4784:1: ( rule__ElkEdgeSection__UnorderedGroup_4__Impl ( rule__ElkEdgeSection__UnorderedGroup_4__3 )? )
            // InternalElkGraph.g:4785:2: rule__ElkEdgeSection__UnorderedGroup_4__Impl ( rule__ElkEdgeSection__UnorderedGroup_4__3 )?
            {
            pushFollow(FOLLOW_29);
            rule__ElkEdgeSection__UnorderedGroup_4__Impl();

            state._fsp--;

            // InternalElkGraph.g:4786:2: ( rule__ElkEdgeSection__UnorderedGroup_4__3 )?
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( LA50_0 == 30 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 0) ) {
                alt50=1;
            }
            else if ( LA50_0 == 31 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 1) ) {
                alt50=1;
            }
            else if ( LA50_0 == 32 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 2) ) {
                alt50=1;
            }
            else if ( LA50_0 == 33 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 3) ) {
                alt50=1;
            }
            else if ( LA50_0 == 34 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 4) ) {
                alt50=1;
            }
            switch (alt50) {
                case 1 :
                    // InternalElkGraph.g:4786:2: rule__ElkEdgeSection__UnorderedGroup_4__3
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkEdgeSection__UnorderedGroup_4__3();

                    state._fsp--;


                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__UnorderedGroup_4__2"


    // $ANTLR start "rule__ElkEdgeSection__UnorderedGroup_4__3"
    // InternalElkGraph.g:4792:1: rule__ElkEdgeSection__UnorderedGroup_4__3 : rule__ElkEdgeSection__UnorderedGroup_4__Impl ( rule__ElkEdgeSection__UnorderedGroup_4__4 )? ;
    public final void rule__ElkEdgeSection__UnorderedGroup_4__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4796:1: ( rule__ElkEdgeSection__UnorderedGroup_4__Impl ( rule__ElkEdgeSection__UnorderedGroup_4__4 )? )
            // InternalElkGraph.g:4797:2: rule__ElkEdgeSection__UnorderedGroup_4__Impl ( rule__ElkEdgeSection__UnorderedGroup_4__4 )?
            {
            pushFollow(FOLLOW_29);
            rule__ElkEdgeSection__UnorderedGroup_4__Impl();

            state._fsp--;

            // InternalElkGraph.g:4798:2: ( rule__ElkEdgeSection__UnorderedGroup_4__4 )?
            int alt51=2;
            int LA51_0 = input.LA(1);

            if ( LA51_0 == 30 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 0) ) {
                alt51=1;
            }
            else if ( LA51_0 == 31 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 1) ) {
                alt51=1;
            }
            else if ( LA51_0 == 32 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 2) ) {
                alt51=1;
            }
            else if ( LA51_0 == 33 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 3) ) {
                alt51=1;
            }
            else if ( LA51_0 == 34 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4(), 4) ) {
                alt51=1;
            }
            switch (alt51) {
                case 1 :
                    // InternalElkGraph.g:4798:2: rule__ElkEdgeSection__UnorderedGroup_4__4
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkEdgeSection__UnorderedGroup_4__4();

                    state._fsp--;


                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__UnorderedGroup_4__3"


    // $ANTLR start "rule__ElkEdgeSection__UnorderedGroup_4__4"
    // InternalElkGraph.g:4804:1: rule__ElkEdgeSection__UnorderedGroup_4__4 : rule__ElkEdgeSection__UnorderedGroup_4__Impl ;
    public final void rule__ElkEdgeSection__UnorderedGroup_4__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4808:1: ( rule__ElkEdgeSection__UnorderedGroup_4__Impl )
            // InternalElkGraph.g:4809:2: rule__ElkEdgeSection__UnorderedGroup_4__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__UnorderedGroup_4__Impl();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__UnorderedGroup_4__4"


    // $ANTLR start "rule__RootNode__IdentifierAssignment_1_1"
    // InternalElkGraph.g:4816:1: rule__RootNode__IdentifierAssignment_1_1 : ( RULE_ID ) ;
    public final void rule__RootNode__IdentifierAssignment_1_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4820:1: ( ( RULE_ID ) )
            // InternalElkGraph.g:4821:2: ( RULE_ID )
            {
            // InternalElkGraph.g:4821:2: ( RULE_ID )
            // InternalElkGraph.g:4822:3: RULE_ID
            {
             before(grammarAccess.getRootNodeAccess().getIdentifierIDTerminalRuleCall_1_1_0()); 
            match(input,RULE_ID,FOLLOW_2); 
             after(grammarAccess.getRootNodeAccess().getIdentifierIDTerminalRuleCall_1_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RootNode__IdentifierAssignment_1_1"


    // $ANTLR start "rule__RootNode__PropertiesAssignment_2"
    // InternalElkGraph.g:4831:1: rule__RootNode__PropertiesAssignment_2 : ( ruleProperty ) ;
    public final void rule__RootNode__PropertiesAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4835:1: ( ( ruleProperty ) )
            // InternalElkGraph.g:4836:2: ( ruleProperty )
            {
            // InternalElkGraph.g:4836:2: ( ruleProperty )
            // InternalElkGraph.g:4837:3: ruleProperty
            {
             before(grammarAccess.getRootNodeAccess().getPropertiesPropertyParserRuleCall_2_0()); 
            pushFollow(FOLLOW_2);
            ruleProperty();

            state._fsp--;

             after(grammarAccess.getRootNodeAccess().getPropertiesPropertyParserRuleCall_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RootNode__PropertiesAssignment_2"


    // $ANTLR start "rule__RootNode__ChildrenAssignment_3_0"
    // InternalElkGraph.g:4846:1: rule__RootNode__ChildrenAssignment_3_0 : ( ruleElkNode ) ;
    public final void rule__RootNode__ChildrenAssignment_3_0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4850:1: ( ( ruleElkNode ) )
            // InternalElkGraph.g:4851:2: ( ruleElkNode )
            {
            // InternalElkGraph.g:4851:2: ( ruleElkNode )
            // InternalElkGraph.g:4852:3: ruleElkNode
            {
             before(grammarAccess.getRootNodeAccess().getChildrenElkNodeParserRuleCall_3_0_0()); 
            pushFollow(FOLLOW_2);
            ruleElkNode();

            state._fsp--;

             after(grammarAccess.getRootNodeAccess().getChildrenElkNodeParserRuleCall_3_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RootNode__ChildrenAssignment_3_0"


    // $ANTLR start "rule__RootNode__ContainedEdgesAssignment_3_1"
    // InternalElkGraph.g:4861:1: rule__RootNode__ContainedEdgesAssignment_3_1 : ( ruleElkEdge ) ;
    public final void rule__RootNode__ContainedEdgesAssignment_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4865:1: ( ( ruleElkEdge ) )
            // InternalElkGraph.g:4866:2: ( ruleElkEdge )
            {
            // InternalElkGraph.g:4866:2: ( ruleElkEdge )
            // InternalElkGraph.g:4867:3: ruleElkEdge
            {
             before(grammarAccess.getRootNodeAccess().getContainedEdgesElkEdgeParserRuleCall_3_1_0()); 
            pushFollow(FOLLOW_2);
            ruleElkEdge();

            state._fsp--;

             after(grammarAccess.getRootNodeAccess().getContainedEdgesElkEdgeParserRuleCall_3_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RootNode__ContainedEdgesAssignment_3_1"


    // $ANTLR start "rule__RootNode__PortsAssignment_3_2"
    // InternalElkGraph.g:4876:1: rule__RootNode__PortsAssignment_3_2 : ( ruleElkPort ) ;
    public final void rule__RootNode__PortsAssignment_3_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4880:1: ( ( ruleElkPort ) )
            // InternalElkGraph.g:4881:2: ( ruleElkPort )
            {
            // InternalElkGraph.g:4881:2: ( ruleElkPort )
            // InternalElkGraph.g:4882:3: ruleElkPort
            {
             before(grammarAccess.getRootNodeAccess().getPortsElkPortParserRuleCall_3_2_0()); 
            pushFollow(FOLLOW_2);
            ruleElkPort();

            state._fsp--;

             after(grammarAccess.getRootNodeAccess().getPortsElkPortParserRuleCall_3_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RootNode__PortsAssignment_3_2"


    // $ANTLR start "rule__RootNode__LabelsAssignment_3_3"
    // InternalElkGraph.g:4891:1: rule__RootNode__LabelsAssignment_3_3 : ( ruleElkLabel ) ;
    public final void rule__RootNode__LabelsAssignment_3_3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4895:1: ( ( ruleElkLabel ) )
            // InternalElkGraph.g:4896:2: ( ruleElkLabel )
            {
            // InternalElkGraph.g:4896:2: ( ruleElkLabel )
            // InternalElkGraph.g:4897:3: ruleElkLabel
            {
             before(grammarAccess.getRootNodeAccess().getLabelsElkLabelParserRuleCall_3_3_0()); 
            pushFollow(FOLLOW_2);
            ruleElkLabel();

            state._fsp--;

             after(grammarAccess.getRootNodeAccess().getLabelsElkLabelParserRuleCall_3_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__RootNode__LabelsAssignment_3_3"


    // $ANTLR start "rule__ElkNode__IdentifierAssignment_1"
    // InternalElkGraph.g:4906:1: rule__ElkNode__IdentifierAssignment_1 : ( RULE_ID ) ;
    public final void rule__ElkNode__IdentifierAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4910:1: ( ( RULE_ID ) )
            // InternalElkGraph.g:4911:2: ( RULE_ID )
            {
            // InternalElkGraph.g:4911:2: ( RULE_ID )
            // InternalElkGraph.g:4912:3: RULE_ID
            {
             before(grammarAccess.getElkNodeAccess().getIdentifierIDTerminalRuleCall_1_0()); 
            match(input,RULE_ID,FOLLOW_2); 
             after(grammarAccess.getElkNodeAccess().getIdentifierIDTerminalRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkNode__IdentifierAssignment_1"


    // $ANTLR start "rule__ElkNode__PropertiesAssignment_2_2"
    // InternalElkGraph.g:4921:1: rule__ElkNode__PropertiesAssignment_2_2 : ( ruleProperty ) ;
    public final void rule__ElkNode__PropertiesAssignment_2_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4925:1: ( ( ruleProperty ) )
            // InternalElkGraph.g:4926:2: ( ruleProperty )
            {
            // InternalElkGraph.g:4926:2: ( ruleProperty )
            // InternalElkGraph.g:4927:3: ruleProperty
            {
             before(grammarAccess.getElkNodeAccess().getPropertiesPropertyParserRuleCall_2_2_0()); 
            pushFollow(FOLLOW_2);
            ruleProperty();

            state._fsp--;

             after(grammarAccess.getElkNodeAccess().getPropertiesPropertyParserRuleCall_2_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkNode__PropertiesAssignment_2_2"


    // $ANTLR start "rule__ElkNode__ChildrenAssignment_2_3_0"
    // InternalElkGraph.g:4936:1: rule__ElkNode__ChildrenAssignment_2_3_0 : ( ruleElkNode ) ;
    public final void rule__ElkNode__ChildrenAssignment_2_3_0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4940:1: ( ( ruleElkNode ) )
            // InternalElkGraph.g:4941:2: ( ruleElkNode )
            {
            // InternalElkGraph.g:4941:2: ( ruleElkNode )
            // InternalElkGraph.g:4942:3: ruleElkNode
            {
             before(grammarAccess.getElkNodeAccess().getChildrenElkNodeParserRuleCall_2_3_0_0()); 
            pushFollow(FOLLOW_2);
            ruleElkNode();

            state._fsp--;

             after(grammarAccess.getElkNodeAccess().getChildrenElkNodeParserRuleCall_2_3_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkNode__ChildrenAssignment_2_3_0"


    // $ANTLR start "rule__ElkNode__ContainedEdgesAssignment_2_3_1"
    // InternalElkGraph.g:4951:1: rule__ElkNode__ContainedEdgesAssignment_2_3_1 : ( ruleElkEdge ) ;
    public final void rule__ElkNode__ContainedEdgesAssignment_2_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4955:1: ( ( ruleElkEdge ) )
            // InternalElkGraph.g:4956:2: ( ruleElkEdge )
            {
            // InternalElkGraph.g:4956:2: ( ruleElkEdge )
            // InternalElkGraph.g:4957:3: ruleElkEdge
            {
             before(grammarAccess.getElkNodeAccess().getContainedEdgesElkEdgeParserRuleCall_2_3_1_0()); 
            pushFollow(FOLLOW_2);
            ruleElkEdge();

            state._fsp--;

             after(grammarAccess.getElkNodeAccess().getContainedEdgesElkEdgeParserRuleCall_2_3_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkNode__ContainedEdgesAssignment_2_3_1"


    // $ANTLR start "rule__ElkNode__PortsAssignment_2_3_2"
    // InternalElkGraph.g:4966:1: rule__ElkNode__PortsAssignment_2_3_2 : ( ruleElkPort ) ;
    public final void rule__ElkNode__PortsAssignment_2_3_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4970:1: ( ( ruleElkPort ) )
            // InternalElkGraph.g:4971:2: ( ruleElkPort )
            {
            // InternalElkGraph.g:4971:2: ( ruleElkPort )
            // InternalElkGraph.g:4972:3: ruleElkPort
            {
             before(grammarAccess.getElkNodeAccess().getPortsElkPortParserRuleCall_2_3_2_0()); 
            pushFollow(FOLLOW_2);
            ruleElkPort();

            state._fsp--;

             after(grammarAccess.getElkNodeAccess().getPortsElkPortParserRuleCall_2_3_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkNode__PortsAssignment_2_3_2"


    // $ANTLR start "rule__ElkNode__LabelsAssignment_2_3_3"
    // InternalElkGraph.g:4981:1: rule__ElkNode__LabelsAssignment_2_3_3 : ( ruleElkLabel ) ;
    public final void rule__ElkNode__LabelsAssignment_2_3_3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4985:1: ( ( ruleElkLabel ) )
            // InternalElkGraph.g:4986:2: ( ruleElkLabel )
            {
            // InternalElkGraph.g:4986:2: ( ruleElkLabel )
            // InternalElkGraph.g:4987:3: ruleElkLabel
            {
             before(grammarAccess.getElkNodeAccess().getLabelsElkLabelParserRuleCall_2_3_3_0()); 
            pushFollow(FOLLOW_2);
            ruleElkLabel();

            state._fsp--;

             after(grammarAccess.getElkNodeAccess().getLabelsElkLabelParserRuleCall_2_3_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkNode__LabelsAssignment_2_3_3"


    // $ANTLR start "rule__ElkLabel__IdentifierAssignment_1_0"
    // InternalElkGraph.g:4996:1: rule__ElkLabel__IdentifierAssignment_1_0 : ( RULE_ID ) ;
    public final void rule__ElkLabel__IdentifierAssignment_1_0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5000:1: ( ( RULE_ID ) )
            // InternalElkGraph.g:5001:2: ( RULE_ID )
            {
            // InternalElkGraph.g:5001:2: ( RULE_ID )
            // InternalElkGraph.g:5002:3: RULE_ID
            {
             before(grammarAccess.getElkLabelAccess().getIdentifierIDTerminalRuleCall_1_0_0()); 
            match(input,RULE_ID,FOLLOW_2); 
             after(grammarAccess.getElkLabelAccess().getIdentifierIDTerminalRuleCall_1_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkLabel__IdentifierAssignment_1_0"


    // $ANTLR start "rule__ElkLabel__TextAssignment_2"
    // InternalElkGraph.g:5011:1: rule__ElkLabel__TextAssignment_2 : ( RULE_STRING ) ;
    public final void rule__ElkLabel__TextAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5015:1: ( ( RULE_STRING ) )
            // InternalElkGraph.g:5016:2: ( RULE_STRING )
            {
            // InternalElkGraph.g:5016:2: ( RULE_STRING )
            // InternalElkGraph.g:5017:3: RULE_STRING
            {
             before(grammarAccess.getElkLabelAccess().getTextSTRINGTerminalRuleCall_2_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getElkLabelAccess().getTextSTRINGTerminalRuleCall_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkLabel__TextAssignment_2"


    // $ANTLR start "rule__ElkLabel__PropertiesAssignment_3_2"
    // InternalElkGraph.g:5026:1: rule__ElkLabel__PropertiesAssignment_3_2 : ( ruleProperty ) ;
    public final void rule__ElkLabel__PropertiesAssignment_3_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5030:1: ( ( ruleProperty ) )
            // InternalElkGraph.g:5031:2: ( ruleProperty )
            {
            // InternalElkGraph.g:5031:2: ( ruleProperty )
            // InternalElkGraph.g:5032:3: ruleProperty
            {
             before(grammarAccess.getElkLabelAccess().getPropertiesPropertyParserRuleCall_3_2_0()); 
            pushFollow(FOLLOW_2);
            ruleProperty();

            state._fsp--;

             after(grammarAccess.getElkLabelAccess().getPropertiesPropertyParserRuleCall_3_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkLabel__PropertiesAssignment_3_2"


    // $ANTLR start "rule__ElkLabel__LabelsAssignment_3_3"
    // InternalElkGraph.g:5041:1: rule__ElkLabel__LabelsAssignment_3_3 : ( ruleElkLabel ) ;
    public final void rule__ElkLabel__LabelsAssignment_3_3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5045:1: ( ( ruleElkLabel ) )
            // InternalElkGraph.g:5046:2: ( ruleElkLabel )
            {
            // InternalElkGraph.g:5046:2: ( ruleElkLabel )
            // InternalElkGraph.g:5047:3: ruleElkLabel
            {
             before(grammarAccess.getElkLabelAccess().getLabelsElkLabelParserRuleCall_3_3_0()); 
            pushFollow(FOLLOW_2);
            ruleElkLabel();

            state._fsp--;

             after(grammarAccess.getElkLabelAccess().getLabelsElkLabelParserRuleCall_3_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkLabel__LabelsAssignment_3_3"


    // $ANTLR start "rule__ElkPort__IdentifierAssignment_1"
    // InternalElkGraph.g:5056:1: rule__ElkPort__IdentifierAssignment_1 : ( RULE_ID ) ;
    public final void rule__ElkPort__IdentifierAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5060:1: ( ( RULE_ID ) )
            // InternalElkGraph.g:5061:2: ( RULE_ID )
            {
            // InternalElkGraph.g:5061:2: ( RULE_ID )
            // InternalElkGraph.g:5062:3: RULE_ID
            {
             before(grammarAccess.getElkPortAccess().getIdentifierIDTerminalRuleCall_1_0()); 
            match(input,RULE_ID,FOLLOW_2); 
             after(grammarAccess.getElkPortAccess().getIdentifierIDTerminalRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkPort__IdentifierAssignment_1"


    // $ANTLR start "rule__ElkPort__PropertiesAssignment_2_2"
    // InternalElkGraph.g:5071:1: rule__ElkPort__PropertiesAssignment_2_2 : ( ruleProperty ) ;
    public final void rule__ElkPort__PropertiesAssignment_2_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5075:1: ( ( ruleProperty ) )
            // InternalElkGraph.g:5076:2: ( ruleProperty )
            {
            // InternalElkGraph.g:5076:2: ( ruleProperty )
            // InternalElkGraph.g:5077:3: ruleProperty
            {
             before(grammarAccess.getElkPortAccess().getPropertiesPropertyParserRuleCall_2_2_0()); 
            pushFollow(FOLLOW_2);
            ruleProperty();

            state._fsp--;

             after(grammarAccess.getElkPortAccess().getPropertiesPropertyParserRuleCall_2_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkPort__PropertiesAssignment_2_2"


    // $ANTLR start "rule__ElkPort__LabelsAssignment_2_3"
    // InternalElkGraph.g:5086:1: rule__ElkPort__LabelsAssignment_2_3 : ( ruleElkLabel ) ;
    public final void rule__ElkPort__LabelsAssignment_2_3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5090:1: ( ( ruleElkLabel ) )
            // InternalElkGraph.g:5091:2: ( ruleElkLabel )
            {
            // InternalElkGraph.g:5091:2: ( ruleElkLabel )
            // InternalElkGraph.g:5092:3: ruleElkLabel
            {
             before(grammarAccess.getElkPortAccess().getLabelsElkLabelParserRuleCall_2_3_0()); 
            pushFollow(FOLLOW_2);
            ruleElkLabel();

            state._fsp--;

             after(grammarAccess.getElkPortAccess().getLabelsElkLabelParserRuleCall_2_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkPort__LabelsAssignment_2_3"


    // $ANTLR start "rule__ShapeLayout__XAssignment_2_0_2"
    // InternalElkGraph.g:5101:1: rule__ShapeLayout__XAssignment_2_0_2 : ( ruleNumber ) ;
    public final void rule__ShapeLayout__XAssignment_2_0_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5105:1: ( ( ruleNumber ) )
            // InternalElkGraph.g:5106:2: ( ruleNumber )
            {
            // InternalElkGraph.g:5106:2: ( ruleNumber )
            // InternalElkGraph.g:5107:3: ruleNumber
            {
             before(grammarAccess.getShapeLayoutAccess().getXNumberParserRuleCall_2_0_2_0()); 
            pushFollow(FOLLOW_2);
            ruleNumber();

            state._fsp--;

             after(grammarAccess.getShapeLayoutAccess().getXNumberParserRuleCall_2_0_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__XAssignment_2_0_2"


    // $ANTLR start "rule__ShapeLayout__YAssignment_2_0_4"
    // InternalElkGraph.g:5116:1: rule__ShapeLayout__YAssignment_2_0_4 : ( ruleNumber ) ;
    public final void rule__ShapeLayout__YAssignment_2_0_4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5120:1: ( ( ruleNumber ) )
            // InternalElkGraph.g:5121:2: ( ruleNumber )
            {
            // InternalElkGraph.g:5121:2: ( ruleNumber )
            // InternalElkGraph.g:5122:3: ruleNumber
            {
             before(grammarAccess.getShapeLayoutAccess().getYNumberParserRuleCall_2_0_4_0()); 
            pushFollow(FOLLOW_2);
            ruleNumber();

            state._fsp--;

             after(grammarAccess.getShapeLayoutAccess().getYNumberParserRuleCall_2_0_4_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__YAssignment_2_0_4"


    // $ANTLR start "rule__ShapeLayout__WidthAssignment_2_1_2"
    // InternalElkGraph.g:5131:1: rule__ShapeLayout__WidthAssignment_2_1_2 : ( ruleNumber ) ;
    public final void rule__ShapeLayout__WidthAssignment_2_1_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5135:1: ( ( ruleNumber ) )
            // InternalElkGraph.g:5136:2: ( ruleNumber )
            {
            // InternalElkGraph.g:5136:2: ( ruleNumber )
            // InternalElkGraph.g:5137:3: ruleNumber
            {
             before(grammarAccess.getShapeLayoutAccess().getWidthNumberParserRuleCall_2_1_2_0()); 
            pushFollow(FOLLOW_2);
            ruleNumber();

            state._fsp--;

             after(grammarAccess.getShapeLayoutAccess().getWidthNumberParserRuleCall_2_1_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__WidthAssignment_2_1_2"


    // $ANTLR start "rule__ShapeLayout__HeightAssignment_2_1_4"
    // InternalElkGraph.g:5146:1: rule__ShapeLayout__HeightAssignment_2_1_4 : ( ruleNumber ) ;
    public final void rule__ShapeLayout__HeightAssignment_2_1_4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5150:1: ( ( ruleNumber ) )
            // InternalElkGraph.g:5151:2: ( ruleNumber )
            {
            // InternalElkGraph.g:5151:2: ( ruleNumber )
            // InternalElkGraph.g:5152:3: ruleNumber
            {
             before(grammarAccess.getShapeLayoutAccess().getHeightNumberParserRuleCall_2_1_4_0()); 
            pushFollow(FOLLOW_2);
            ruleNumber();

            state._fsp--;

             after(grammarAccess.getShapeLayoutAccess().getHeightNumberParserRuleCall_2_1_4_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ShapeLayout__HeightAssignment_2_1_4"


    // $ANTLR start "rule__ElkEdge__IdentifierAssignment_1_0"
    // InternalElkGraph.g:5161:1: rule__ElkEdge__IdentifierAssignment_1_0 : ( RULE_ID ) ;
    public final void rule__ElkEdge__IdentifierAssignment_1_0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5165:1: ( ( RULE_ID ) )
            // InternalElkGraph.g:5166:2: ( RULE_ID )
            {
            // InternalElkGraph.g:5166:2: ( RULE_ID )
            // InternalElkGraph.g:5167:3: RULE_ID
            {
             before(grammarAccess.getElkEdgeAccess().getIdentifierIDTerminalRuleCall_1_0_0()); 
            match(input,RULE_ID,FOLLOW_2); 
             after(grammarAccess.getElkEdgeAccess().getIdentifierIDTerminalRuleCall_1_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__IdentifierAssignment_1_0"


    // $ANTLR start "rule__ElkEdge__SourcesAssignment_2"
    // InternalElkGraph.g:5176:1: rule__ElkEdge__SourcesAssignment_2 : ( ( ruleQualifiedId ) ) ;
    public final void rule__ElkEdge__SourcesAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5180:1: ( ( ( ruleQualifiedId ) ) )
            // InternalElkGraph.g:5181:2: ( ( ruleQualifiedId ) )
            {
            // InternalElkGraph.g:5181:2: ( ( ruleQualifiedId ) )
            // InternalElkGraph.g:5182:3: ( ruleQualifiedId )
            {
             before(grammarAccess.getElkEdgeAccess().getSourcesElkConnectableShapeCrossReference_2_0()); 
            // InternalElkGraph.g:5183:3: ( ruleQualifiedId )
            // InternalElkGraph.g:5184:4: ruleQualifiedId
            {
             before(grammarAccess.getElkEdgeAccess().getSourcesElkConnectableShapeQualifiedIdParserRuleCall_2_0_1()); 
            pushFollow(FOLLOW_2);
            ruleQualifiedId();

            state._fsp--;

             after(grammarAccess.getElkEdgeAccess().getSourcesElkConnectableShapeQualifiedIdParserRuleCall_2_0_1()); 

            }

             after(grammarAccess.getElkEdgeAccess().getSourcesElkConnectableShapeCrossReference_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__SourcesAssignment_2"


    // $ANTLR start "rule__ElkEdge__SourcesAssignment_3_1"
    // InternalElkGraph.g:5195:1: rule__ElkEdge__SourcesAssignment_3_1 : ( ( ruleQualifiedId ) ) ;
    public final void rule__ElkEdge__SourcesAssignment_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5199:1: ( ( ( ruleQualifiedId ) ) )
            // InternalElkGraph.g:5200:2: ( ( ruleQualifiedId ) )
            {
            // InternalElkGraph.g:5200:2: ( ( ruleQualifiedId ) )
            // InternalElkGraph.g:5201:3: ( ruleQualifiedId )
            {
             before(grammarAccess.getElkEdgeAccess().getSourcesElkConnectableShapeCrossReference_3_1_0()); 
            // InternalElkGraph.g:5202:3: ( ruleQualifiedId )
            // InternalElkGraph.g:5203:4: ruleQualifiedId
            {
             before(grammarAccess.getElkEdgeAccess().getSourcesElkConnectableShapeQualifiedIdParserRuleCall_3_1_0_1()); 
            pushFollow(FOLLOW_2);
            ruleQualifiedId();

            state._fsp--;

             after(grammarAccess.getElkEdgeAccess().getSourcesElkConnectableShapeQualifiedIdParserRuleCall_3_1_0_1()); 

            }

             after(grammarAccess.getElkEdgeAccess().getSourcesElkConnectableShapeCrossReference_3_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__SourcesAssignment_3_1"


    // $ANTLR start "rule__ElkEdge__TargetsAssignment_5"
    // InternalElkGraph.g:5214:1: rule__ElkEdge__TargetsAssignment_5 : ( ( ruleQualifiedId ) ) ;
    public final void rule__ElkEdge__TargetsAssignment_5() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5218:1: ( ( ( ruleQualifiedId ) ) )
            // InternalElkGraph.g:5219:2: ( ( ruleQualifiedId ) )
            {
            // InternalElkGraph.g:5219:2: ( ( ruleQualifiedId ) )
            // InternalElkGraph.g:5220:3: ( ruleQualifiedId )
            {
             before(grammarAccess.getElkEdgeAccess().getTargetsElkConnectableShapeCrossReference_5_0()); 
            // InternalElkGraph.g:5221:3: ( ruleQualifiedId )
            // InternalElkGraph.g:5222:4: ruleQualifiedId
            {
             before(grammarAccess.getElkEdgeAccess().getTargetsElkConnectableShapeQualifiedIdParserRuleCall_5_0_1()); 
            pushFollow(FOLLOW_2);
            ruleQualifiedId();

            state._fsp--;

             after(grammarAccess.getElkEdgeAccess().getTargetsElkConnectableShapeQualifiedIdParserRuleCall_5_0_1()); 

            }

             after(grammarAccess.getElkEdgeAccess().getTargetsElkConnectableShapeCrossReference_5_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__TargetsAssignment_5"


    // $ANTLR start "rule__ElkEdge__TargetsAssignment_6_1"
    // InternalElkGraph.g:5233:1: rule__ElkEdge__TargetsAssignment_6_1 : ( ( ruleQualifiedId ) ) ;
    public final void rule__ElkEdge__TargetsAssignment_6_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5237:1: ( ( ( ruleQualifiedId ) ) )
            // InternalElkGraph.g:5238:2: ( ( ruleQualifiedId ) )
            {
            // InternalElkGraph.g:5238:2: ( ( ruleQualifiedId ) )
            // InternalElkGraph.g:5239:3: ( ruleQualifiedId )
            {
             before(grammarAccess.getElkEdgeAccess().getTargetsElkConnectableShapeCrossReference_6_1_0()); 
            // InternalElkGraph.g:5240:3: ( ruleQualifiedId )
            // InternalElkGraph.g:5241:4: ruleQualifiedId
            {
             before(grammarAccess.getElkEdgeAccess().getTargetsElkConnectableShapeQualifiedIdParserRuleCall_6_1_0_1()); 
            pushFollow(FOLLOW_2);
            ruleQualifiedId();

            state._fsp--;

             after(grammarAccess.getElkEdgeAccess().getTargetsElkConnectableShapeQualifiedIdParserRuleCall_6_1_0_1()); 

            }

             after(grammarAccess.getElkEdgeAccess().getTargetsElkConnectableShapeCrossReference_6_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__TargetsAssignment_6_1"


    // $ANTLR start "rule__ElkEdge__PropertiesAssignment_7_2"
    // InternalElkGraph.g:5252:1: rule__ElkEdge__PropertiesAssignment_7_2 : ( ruleProperty ) ;
    public final void rule__ElkEdge__PropertiesAssignment_7_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5256:1: ( ( ruleProperty ) )
            // InternalElkGraph.g:5257:2: ( ruleProperty )
            {
            // InternalElkGraph.g:5257:2: ( ruleProperty )
            // InternalElkGraph.g:5258:3: ruleProperty
            {
             before(grammarAccess.getElkEdgeAccess().getPropertiesPropertyParserRuleCall_7_2_0()); 
            pushFollow(FOLLOW_2);
            ruleProperty();

            state._fsp--;

             after(grammarAccess.getElkEdgeAccess().getPropertiesPropertyParserRuleCall_7_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__PropertiesAssignment_7_2"


    // $ANTLR start "rule__ElkEdge__LabelsAssignment_7_3"
    // InternalElkGraph.g:5267:1: rule__ElkEdge__LabelsAssignment_7_3 : ( ruleElkLabel ) ;
    public final void rule__ElkEdge__LabelsAssignment_7_3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5271:1: ( ( ruleElkLabel ) )
            // InternalElkGraph.g:5272:2: ( ruleElkLabel )
            {
            // InternalElkGraph.g:5272:2: ( ruleElkLabel )
            // InternalElkGraph.g:5273:3: ruleElkLabel
            {
             before(grammarAccess.getElkEdgeAccess().getLabelsElkLabelParserRuleCall_7_3_0()); 
            pushFollow(FOLLOW_2);
            ruleElkLabel();

            state._fsp--;

             after(grammarAccess.getElkEdgeAccess().getLabelsElkLabelParserRuleCall_7_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdge__LabelsAssignment_7_3"


    // $ANTLR start "rule__EdgeLayout__SectionsAssignment_2_0"
    // InternalElkGraph.g:5282:1: rule__EdgeLayout__SectionsAssignment_2_0 : ( ruleElkSingleEdgeSection ) ;
    public final void rule__EdgeLayout__SectionsAssignment_2_0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5286:1: ( ( ruleElkSingleEdgeSection ) )
            // InternalElkGraph.g:5287:2: ( ruleElkSingleEdgeSection )
            {
            // InternalElkGraph.g:5287:2: ( ruleElkSingleEdgeSection )
            // InternalElkGraph.g:5288:3: ruleElkSingleEdgeSection
            {
             before(grammarAccess.getEdgeLayoutAccess().getSectionsElkSingleEdgeSectionParserRuleCall_2_0_0()); 
            pushFollow(FOLLOW_2);
            ruleElkSingleEdgeSection();

            state._fsp--;

             after(grammarAccess.getEdgeLayoutAccess().getSectionsElkSingleEdgeSectionParserRuleCall_2_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeLayout__SectionsAssignment_2_0"


    // $ANTLR start "rule__EdgeLayout__SectionsAssignment_2_1"
    // InternalElkGraph.g:5297:1: rule__EdgeLayout__SectionsAssignment_2_1 : ( ruleElkEdgeSection ) ;
    public final void rule__EdgeLayout__SectionsAssignment_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5301:1: ( ( ruleElkEdgeSection ) )
            // InternalElkGraph.g:5302:2: ( ruleElkEdgeSection )
            {
            // InternalElkGraph.g:5302:2: ( ruleElkEdgeSection )
            // InternalElkGraph.g:5303:3: ruleElkEdgeSection
            {
             before(grammarAccess.getEdgeLayoutAccess().getSectionsElkEdgeSectionParserRuleCall_2_1_0()); 
            pushFollow(FOLLOW_2);
            ruleElkEdgeSection();

            state._fsp--;

             after(grammarAccess.getEdgeLayoutAccess().getSectionsElkEdgeSectionParserRuleCall_2_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__EdgeLayout__SectionsAssignment_2_1"


    // $ANTLR start "rule__ElkSingleEdgeSection__IncomingShapeAssignment_1_0_2"
    // InternalElkGraph.g:5312:1: rule__ElkSingleEdgeSection__IncomingShapeAssignment_1_0_2 : ( ( ruleQualifiedId ) ) ;
    public final void rule__ElkSingleEdgeSection__IncomingShapeAssignment_1_0_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5316:1: ( ( ( ruleQualifiedId ) ) )
            // InternalElkGraph.g:5317:2: ( ( ruleQualifiedId ) )
            {
            // InternalElkGraph.g:5317:2: ( ( ruleQualifiedId ) )
            // InternalElkGraph.g:5318:3: ( ruleQualifiedId )
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getIncomingShapeElkConnectableShapeCrossReference_1_0_2_0()); 
            // InternalElkGraph.g:5319:3: ( ruleQualifiedId )
            // InternalElkGraph.g:5320:4: ruleQualifiedId
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getIncomingShapeElkConnectableShapeQualifiedIdParserRuleCall_1_0_2_0_1()); 
            pushFollow(FOLLOW_2);
            ruleQualifiedId();

            state._fsp--;

             after(grammarAccess.getElkSingleEdgeSectionAccess().getIncomingShapeElkConnectableShapeQualifiedIdParserRuleCall_1_0_2_0_1()); 

            }

             after(grammarAccess.getElkSingleEdgeSectionAccess().getIncomingShapeElkConnectableShapeCrossReference_1_0_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__IncomingShapeAssignment_1_0_2"


    // $ANTLR start "rule__ElkSingleEdgeSection__OutgoingShapeAssignment_1_1_2"
    // InternalElkGraph.g:5331:1: rule__ElkSingleEdgeSection__OutgoingShapeAssignment_1_1_2 : ( ( ruleQualifiedId ) ) ;
    public final void rule__ElkSingleEdgeSection__OutgoingShapeAssignment_1_1_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5335:1: ( ( ( ruleQualifiedId ) ) )
            // InternalElkGraph.g:5336:2: ( ( ruleQualifiedId ) )
            {
            // InternalElkGraph.g:5336:2: ( ( ruleQualifiedId ) )
            // InternalElkGraph.g:5337:3: ( ruleQualifiedId )
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getOutgoingShapeElkConnectableShapeCrossReference_1_1_2_0()); 
            // InternalElkGraph.g:5338:3: ( ruleQualifiedId )
            // InternalElkGraph.g:5339:4: ruleQualifiedId
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getOutgoingShapeElkConnectableShapeQualifiedIdParserRuleCall_1_1_2_0_1()); 
            pushFollow(FOLLOW_2);
            ruleQualifiedId();

            state._fsp--;

             after(grammarAccess.getElkSingleEdgeSectionAccess().getOutgoingShapeElkConnectableShapeQualifiedIdParserRuleCall_1_1_2_0_1()); 

            }

             after(grammarAccess.getElkSingleEdgeSectionAccess().getOutgoingShapeElkConnectableShapeCrossReference_1_1_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__OutgoingShapeAssignment_1_1_2"


    // $ANTLR start "rule__ElkSingleEdgeSection__StartXAssignment_1_2_2"
    // InternalElkGraph.g:5350:1: rule__ElkSingleEdgeSection__StartXAssignment_1_2_2 : ( ruleNumber ) ;
    public final void rule__ElkSingleEdgeSection__StartXAssignment_1_2_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5354:1: ( ( ruleNumber ) )
            // InternalElkGraph.g:5355:2: ( ruleNumber )
            {
            // InternalElkGraph.g:5355:2: ( ruleNumber )
            // InternalElkGraph.g:5356:3: ruleNumber
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getStartXNumberParserRuleCall_1_2_2_0()); 
            pushFollow(FOLLOW_2);
            ruleNumber();

            state._fsp--;

             after(grammarAccess.getElkSingleEdgeSectionAccess().getStartXNumberParserRuleCall_1_2_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__StartXAssignment_1_2_2"


    // $ANTLR start "rule__ElkSingleEdgeSection__StartYAssignment_1_2_4"
    // InternalElkGraph.g:5365:1: rule__ElkSingleEdgeSection__StartYAssignment_1_2_4 : ( ruleNumber ) ;
    public final void rule__ElkSingleEdgeSection__StartYAssignment_1_2_4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5369:1: ( ( ruleNumber ) )
            // InternalElkGraph.g:5370:2: ( ruleNumber )
            {
            // InternalElkGraph.g:5370:2: ( ruleNumber )
            // InternalElkGraph.g:5371:3: ruleNumber
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getStartYNumberParserRuleCall_1_2_4_0()); 
            pushFollow(FOLLOW_2);
            ruleNumber();

            state._fsp--;

             after(grammarAccess.getElkSingleEdgeSectionAccess().getStartYNumberParserRuleCall_1_2_4_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__StartYAssignment_1_2_4"


    // $ANTLR start "rule__ElkSingleEdgeSection__EndXAssignment_1_3_2"
    // InternalElkGraph.g:5380:1: rule__ElkSingleEdgeSection__EndXAssignment_1_3_2 : ( ruleNumber ) ;
    public final void rule__ElkSingleEdgeSection__EndXAssignment_1_3_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5384:1: ( ( ruleNumber ) )
            // InternalElkGraph.g:5385:2: ( ruleNumber )
            {
            // InternalElkGraph.g:5385:2: ( ruleNumber )
            // InternalElkGraph.g:5386:3: ruleNumber
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getEndXNumberParserRuleCall_1_3_2_0()); 
            pushFollow(FOLLOW_2);
            ruleNumber();

            state._fsp--;

             after(grammarAccess.getElkSingleEdgeSectionAccess().getEndXNumberParserRuleCall_1_3_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__EndXAssignment_1_3_2"


    // $ANTLR start "rule__ElkSingleEdgeSection__EndYAssignment_1_3_4"
    // InternalElkGraph.g:5395:1: rule__ElkSingleEdgeSection__EndYAssignment_1_3_4 : ( ruleNumber ) ;
    public final void rule__ElkSingleEdgeSection__EndYAssignment_1_3_4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5399:1: ( ( ruleNumber ) )
            // InternalElkGraph.g:5400:2: ( ruleNumber )
            {
            // InternalElkGraph.g:5400:2: ( ruleNumber )
            // InternalElkGraph.g:5401:3: ruleNumber
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getEndYNumberParserRuleCall_1_3_4_0()); 
            pushFollow(FOLLOW_2);
            ruleNumber();

            state._fsp--;

             after(grammarAccess.getElkSingleEdgeSectionAccess().getEndYNumberParserRuleCall_1_3_4_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__EndYAssignment_1_3_4"


    // $ANTLR start "rule__ElkSingleEdgeSection__BendPointsAssignment_1_4_2"
    // InternalElkGraph.g:5410:1: rule__ElkSingleEdgeSection__BendPointsAssignment_1_4_2 : ( ruleElkBendPoint ) ;
    public final void rule__ElkSingleEdgeSection__BendPointsAssignment_1_4_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5414:1: ( ( ruleElkBendPoint ) )
            // InternalElkGraph.g:5415:2: ( ruleElkBendPoint )
            {
            // InternalElkGraph.g:5415:2: ( ruleElkBendPoint )
            // InternalElkGraph.g:5416:3: ruleElkBendPoint
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getBendPointsElkBendPointParserRuleCall_1_4_2_0()); 
            pushFollow(FOLLOW_2);
            ruleElkBendPoint();

            state._fsp--;

             after(grammarAccess.getElkSingleEdgeSectionAccess().getBendPointsElkBendPointParserRuleCall_1_4_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__BendPointsAssignment_1_4_2"


    // $ANTLR start "rule__ElkSingleEdgeSection__BendPointsAssignment_1_4_3_1"
    // InternalElkGraph.g:5425:1: rule__ElkSingleEdgeSection__BendPointsAssignment_1_4_3_1 : ( ruleElkBendPoint ) ;
    public final void rule__ElkSingleEdgeSection__BendPointsAssignment_1_4_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5429:1: ( ( ruleElkBendPoint ) )
            // InternalElkGraph.g:5430:2: ( ruleElkBendPoint )
            {
            // InternalElkGraph.g:5430:2: ( ruleElkBendPoint )
            // InternalElkGraph.g:5431:3: ruleElkBendPoint
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getBendPointsElkBendPointParserRuleCall_1_4_3_1_0()); 
            pushFollow(FOLLOW_2);
            ruleElkBendPoint();

            state._fsp--;

             after(grammarAccess.getElkSingleEdgeSectionAccess().getBendPointsElkBendPointParserRuleCall_1_4_3_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__BendPointsAssignment_1_4_3_1"


    // $ANTLR start "rule__ElkEdgeSection__IdentifierAssignment_1"
    // InternalElkGraph.g:5440:1: rule__ElkEdgeSection__IdentifierAssignment_1 : ( RULE_ID ) ;
    public final void rule__ElkEdgeSection__IdentifierAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5444:1: ( ( RULE_ID ) )
            // InternalElkGraph.g:5445:2: ( RULE_ID )
            {
            // InternalElkGraph.g:5445:2: ( RULE_ID )
            // InternalElkGraph.g:5446:3: RULE_ID
            {
             before(grammarAccess.getElkEdgeSectionAccess().getIdentifierIDTerminalRuleCall_1_0()); 
            match(input,RULE_ID,FOLLOW_2); 
             after(grammarAccess.getElkEdgeSectionAccess().getIdentifierIDTerminalRuleCall_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__IdentifierAssignment_1"


    // $ANTLR start "rule__ElkEdgeSection__OutgoingSectionsAssignment_2_1"
    // InternalElkGraph.g:5455:1: rule__ElkEdgeSection__OutgoingSectionsAssignment_2_1 : ( ( RULE_ID ) ) ;
    public final void rule__ElkEdgeSection__OutgoingSectionsAssignment_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5459:1: ( ( ( RULE_ID ) ) )
            // InternalElkGraph.g:5460:2: ( ( RULE_ID ) )
            {
            // InternalElkGraph.g:5460:2: ( ( RULE_ID ) )
            // InternalElkGraph.g:5461:3: ( RULE_ID )
            {
             before(grammarAccess.getElkEdgeSectionAccess().getOutgoingSectionsElkEdgeSectionCrossReference_2_1_0()); 
            // InternalElkGraph.g:5462:3: ( RULE_ID )
            // InternalElkGraph.g:5463:4: RULE_ID
            {
             before(grammarAccess.getElkEdgeSectionAccess().getOutgoingSectionsElkEdgeSectionIDTerminalRuleCall_2_1_0_1()); 
            match(input,RULE_ID,FOLLOW_2); 
             after(grammarAccess.getElkEdgeSectionAccess().getOutgoingSectionsElkEdgeSectionIDTerminalRuleCall_2_1_0_1()); 

            }

             after(grammarAccess.getElkEdgeSectionAccess().getOutgoingSectionsElkEdgeSectionCrossReference_2_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__OutgoingSectionsAssignment_2_1"


    // $ANTLR start "rule__ElkEdgeSection__OutgoingSectionsAssignment_2_2_1"
    // InternalElkGraph.g:5474:1: rule__ElkEdgeSection__OutgoingSectionsAssignment_2_2_1 : ( ( RULE_ID ) ) ;
    public final void rule__ElkEdgeSection__OutgoingSectionsAssignment_2_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5478:1: ( ( ( RULE_ID ) ) )
            // InternalElkGraph.g:5479:2: ( ( RULE_ID ) )
            {
            // InternalElkGraph.g:5479:2: ( ( RULE_ID ) )
            // InternalElkGraph.g:5480:3: ( RULE_ID )
            {
             before(grammarAccess.getElkEdgeSectionAccess().getOutgoingSectionsElkEdgeSectionCrossReference_2_2_1_0()); 
            // InternalElkGraph.g:5481:3: ( RULE_ID )
            // InternalElkGraph.g:5482:4: RULE_ID
            {
             before(grammarAccess.getElkEdgeSectionAccess().getOutgoingSectionsElkEdgeSectionIDTerminalRuleCall_2_2_1_0_1()); 
            match(input,RULE_ID,FOLLOW_2); 
             after(grammarAccess.getElkEdgeSectionAccess().getOutgoingSectionsElkEdgeSectionIDTerminalRuleCall_2_2_1_0_1()); 

            }

             after(grammarAccess.getElkEdgeSectionAccess().getOutgoingSectionsElkEdgeSectionCrossReference_2_2_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__OutgoingSectionsAssignment_2_2_1"


    // $ANTLR start "rule__ElkEdgeSection__IncomingShapeAssignment_4_0_2"
    // InternalElkGraph.g:5493:1: rule__ElkEdgeSection__IncomingShapeAssignment_4_0_2 : ( ( ruleQualifiedId ) ) ;
    public final void rule__ElkEdgeSection__IncomingShapeAssignment_4_0_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5497:1: ( ( ( ruleQualifiedId ) ) )
            // InternalElkGraph.g:5498:2: ( ( ruleQualifiedId ) )
            {
            // InternalElkGraph.g:5498:2: ( ( ruleQualifiedId ) )
            // InternalElkGraph.g:5499:3: ( ruleQualifiedId )
            {
             before(grammarAccess.getElkEdgeSectionAccess().getIncomingShapeElkConnectableShapeCrossReference_4_0_2_0()); 
            // InternalElkGraph.g:5500:3: ( ruleQualifiedId )
            // InternalElkGraph.g:5501:4: ruleQualifiedId
            {
             before(grammarAccess.getElkEdgeSectionAccess().getIncomingShapeElkConnectableShapeQualifiedIdParserRuleCall_4_0_2_0_1()); 
            pushFollow(FOLLOW_2);
            ruleQualifiedId();

            state._fsp--;

             after(grammarAccess.getElkEdgeSectionAccess().getIncomingShapeElkConnectableShapeQualifiedIdParserRuleCall_4_0_2_0_1()); 

            }

             after(grammarAccess.getElkEdgeSectionAccess().getIncomingShapeElkConnectableShapeCrossReference_4_0_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__IncomingShapeAssignment_4_0_2"


    // $ANTLR start "rule__ElkEdgeSection__OutgoingShapeAssignment_4_1_2"
    // InternalElkGraph.g:5512:1: rule__ElkEdgeSection__OutgoingShapeAssignment_4_1_2 : ( ( ruleQualifiedId ) ) ;
    public final void rule__ElkEdgeSection__OutgoingShapeAssignment_4_1_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5516:1: ( ( ( ruleQualifiedId ) ) )
            // InternalElkGraph.g:5517:2: ( ( ruleQualifiedId ) )
            {
            // InternalElkGraph.g:5517:2: ( ( ruleQualifiedId ) )
            // InternalElkGraph.g:5518:3: ( ruleQualifiedId )
            {
             before(grammarAccess.getElkEdgeSectionAccess().getOutgoingShapeElkConnectableShapeCrossReference_4_1_2_0()); 
            // InternalElkGraph.g:5519:3: ( ruleQualifiedId )
            // InternalElkGraph.g:5520:4: ruleQualifiedId
            {
             before(grammarAccess.getElkEdgeSectionAccess().getOutgoingShapeElkConnectableShapeQualifiedIdParserRuleCall_4_1_2_0_1()); 
            pushFollow(FOLLOW_2);
            ruleQualifiedId();

            state._fsp--;

             after(grammarAccess.getElkEdgeSectionAccess().getOutgoingShapeElkConnectableShapeQualifiedIdParserRuleCall_4_1_2_0_1()); 

            }

             after(grammarAccess.getElkEdgeSectionAccess().getOutgoingShapeElkConnectableShapeCrossReference_4_1_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__OutgoingShapeAssignment_4_1_2"


    // $ANTLR start "rule__ElkEdgeSection__StartXAssignment_4_2_2"
    // InternalElkGraph.g:5531:1: rule__ElkEdgeSection__StartXAssignment_4_2_2 : ( ruleNumber ) ;
    public final void rule__ElkEdgeSection__StartXAssignment_4_2_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5535:1: ( ( ruleNumber ) )
            // InternalElkGraph.g:5536:2: ( ruleNumber )
            {
            // InternalElkGraph.g:5536:2: ( ruleNumber )
            // InternalElkGraph.g:5537:3: ruleNumber
            {
             before(grammarAccess.getElkEdgeSectionAccess().getStartXNumberParserRuleCall_4_2_2_0()); 
            pushFollow(FOLLOW_2);
            ruleNumber();

            state._fsp--;

             after(grammarAccess.getElkEdgeSectionAccess().getStartXNumberParserRuleCall_4_2_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__StartXAssignment_4_2_2"


    // $ANTLR start "rule__ElkEdgeSection__StartYAssignment_4_2_4"
    // InternalElkGraph.g:5546:1: rule__ElkEdgeSection__StartYAssignment_4_2_4 : ( ruleNumber ) ;
    public final void rule__ElkEdgeSection__StartYAssignment_4_2_4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5550:1: ( ( ruleNumber ) )
            // InternalElkGraph.g:5551:2: ( ruleNumber )
            {
            // InternalElkGraph.g:5551:2: ( ruleNumber )
            // InternalElkGraph.g:5552:3: ruleNumber
            {
             before(grammarAccess.getElkEdgeSectionAccess().getStartYNumberParserRuleCall_4_2_4_0()); 
            pushFollow(FOLLOW_2);
            ruleNumber();

            state._fsp--;

             after(grammarAccess.getElkEdgeSectionAccess().getStartYNumberParserRuleCall_4_2_4_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__StartYAssignment_4_2_4"


    // $ANTLR start "rule__ElkEdgeSection__EndXAssignment_4_3_2"
    // InternalElkGraph.g:5561:1: rule__ElkEdgeSection__EndXAssignment_4_3_2 : ( ruleNumber ) ;
    public final void rule__ElkEdgeSection__EndXAssignment_4_3_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5565:1: ( ( ruleNumber ) )
            // InternalElkGraph.g:5566:2: ( ruleNumber )
            {
            // InternalElkGraph.g:5566:2: ( ruleNumber )
            // InternalElkGraph.g:5567:3: ruleNumber
            {
             before(grammarAccess.getElkEdgeSectionAccess().getEndXNumberParserRuleCall_4_3_2_0()); 
            pushFollow(FOLLOW_2);
            ruleNumber();

            state._fsp--;

             after(grammarAccess.getElkEdgeSectionAccess().getEndXNumberParserRuleCall_4_3_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__EndXAssignment_4_3_2"


    // $ANTLR start "rule__ElkEdgeSection__EndYAssignment_4_3_4"
    // InternalElkGraph.g:5576:1: rule__ElkEdgeSection__EndYAssignment_4_3_4 : ( ruleNumber ) ;
    public final void rule__ElkEdgeSection__EndYAssignment_4_3_4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5580:1: ( ( ruleNumber ) )
            // InternalElkGraph.g:5581:2: ( ruleNumber )
            {
            // InternalElkGraph.g:5581:2: ( ruleNumber )
            // InternalElkGraph.g:5582:3: ruleNumber
            {
             before(grammarAccess.getElkEdgeSectionAccess().getEndYNumberParserRuleCall_4_3_4_0()); 
            pushFollow(FOLLOW_2);
            ruleNumber();

            state._fsp--;

             after(grammarAccess.getElkEdgeSectionAccess().getEndYNumberParserRuleCall_4_3_4_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__EndYAssignment_4_3_4"


    // $ANTLR start "rule__ElkEdgeSection__BendPointsAssignment_4_4_2"
    // InternalElkGraph.g:5591:1: rule__ElkEdgeSection__BendPointsAssignment_4_4_2 : ( ruleElkBendPoint ) ;
    public final void rule__ElkEdgeSection__BendPointsAssignment_4_4_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5595:1: ( ( ruleElkBendPoint ) )
            // InternalElkGraph.g:5596:2: ( ruleElkBendPoint )
            {
            // InternalElkGraph.g:5596:2: ( ruleElkBendPoint )
            // InternalElkGraph.g:5597:3: ruleElkBendPoint
            {
             before(grammarAccess.getElkEdgeSectionAccess().getBendPointsElkBendPointParserRuleCall_4_4_2_0()); 
            pushFollow(FOLLOW_2);
            ruleElkBendPoint();

            state._fsp--;

             after(grammarAccess.getElkEdgeSectionAccess().getBendPointsElkBendPointParserRuleCall_4_4_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__BendPointsAssignment_4_4_2"


    // $ANTLR start "rule__ElkEdgeSection__BendPointsAssignment_4_4_3_1"
    // InternalElkGraph.g:5606:1: rule__ElkEdgeSection__BendPointsAssignment_4_4_3_1 : ( ruleElkBendPoint ) ;
    public final void rule__ElkEdgeSection__BendPointsAssignment_4_4_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5610:1: ( ( ruleElkBendPoint ) )
            // InternalElkGraph.g:5611:2: ( ruleElkBendPoint )
            {
            // InternalElkGraph.g:5611:2: ( ruleElkBendPoint )
            // InternalElkGraph.g:5612:3: ruleElkBendPoint
            {
             before(grammarAccess.getElkEdgeSectionAccess().getBendPointsElkBendPointParserRuleCall_4_4_3_1_0()); 
            pushFollow(FOLLOW_2);
            ruleElkBendPoint();

            state._fsp--;

             after(grammarAccess.getElkEdgeSectionAccess().getBendPointsElkBendPointParserRuleCall_4_4_3_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__BendPointsAssignment_4_4_3_1"


    // $ANTLR start "rule__ElkBendPoint__XAssignment_0"
    // InternalElkGraph.g:5621:1: rule__ElkBendPoint__XAssignment_0 : ( ruleNumber ) ;
    public final void rule__ElkBendPoint__XAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5625:1: ( ( ruleNumber ) )
            // InternalElkGraph.g:5626:2: ( ruleNumber )
            {
            // InternalElkGraph.g:5626:2: ( ruleNumber )
            // InternalElkGraph.g:5627:3: ruleNumber
            {
             before(grammarAccess.getElkBendPointAccess().getXNumberParserRuleCall_0_0()); 
            pushFollow(FOLLOW_2);
            ruleNumber();

            state._fsp--;

             after(grammarAccess.getElkBendPointAccess().getXNumberParserRuleCall_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkBendPoint__XAssignment_0"


    // $ANTLR start "rule__ElkBendPoint__YAssignment_2"
    // InternalElkGraph.g:5636:1: rule__ElkBendPoint__YAssignment_2 : ( ruleNumber ) ;
    public final void rule__ElkBendPoint__YAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5640:1: ( ( ruleNumber ) )
            // InternalElkGraph.g:5641:2: ( ruleNumber )
            {
            // InternalElkGraph.g:5641:2: ( ruleNumber )
            // InternalElkGraph.g:5642:3: ruleNumber
            {
             before(grammarAccess.getElkBendPointAccess().getYNumberParserRuleCall_2_0()); 
            pushFollow(FOLLOW_2);
            ruleNumber();

            state._fsp--;

             after(grammarAccess.getElkBendPointAccess().getYNumberParserRuleCall_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkBendPoint__YAssignment_2"


    // $ANTLR start "rule__Property__KeyAssignment_0"
    // InternalElkGraph.g:5651:1: rule__Property__KeyAssignment_0 : ( rulePropertyKey ) ;
    public final void rule__Property__KeyAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5655:1: ( ( rulePropertyKey ) )
            // InternalElkGraph.g:5656:2: ( rulePropertyKey )
            {
            // InternalElkGraph.g:5656:2: ( rulePropertyKey )
            // InternalElkGraph.g:5657:3: rulePropertyKey
            {
             before(grammarAccess.getPropertyAccess().getKeyPropertyKeyParserRuleCall_0_0()); 
            pushFollow(FOLLOW_2);
            rulePropertyKey();

            state._fsp--;

             after(grammarAccess.getPropertyAccess().getKeyPropertyKeyParserRuleCall_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Property__KeyAssignment_0"


    // $ANTLR start "rule__Property__ValueAssignment_2_0"
    // InternalElkGraph.g:5666:1: rule__Property__ValueAssignment_2_0 : ( RULE_STRING ) ;
    public final void rule__Property__ValueAssignment_2_0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5670:1: ( ( RULE_STRING ) )
            // InternalElkGraph.g:5671:2: ( RULE_STRING )
            {
            // InternalElkGraph.g:5671:2: ( RULE_STRING )
            // InternalElkGraph.g:5672:3: RULE_STRING
            {
             before(grammarAccess.getPropertyAccess().getValueSTRINGTerminalRuleCall_2_0_0()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getPropertyAccess().getValueSTRINGTerminalRuleCall_2_0_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Property__ValueAssignment_2_0"


    // $ANTLR start "rule__Property__ValueAssignment_2_1"
    // InternalElkGraph.g:5681:1: rule__Property__ValueAssignment_2_1 : ( ruleQualifiedId ) ;
    public final void rule__Property__ValueAssignment_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5685:1: ( ( ruleQualifiedId ) )
            // InternalElkGraph.g:5686:2: ( ruleQualifiedId )
            {
            // InternalElkGraph.g:5686:2: ( ruleQualifiedId )
            // InternalElkGraph.g:5687:3: ruleQualifiedId
            {
             before(grammarAccess.getPropertyAccess().getValueQualifiedIdParserRuleCall_2_1_0()); 
            pushFollow(FOLLOW_2);
            ruleQualifiedId();

            state._fsp--;

             after(grammarAccess.getPropertyAccess().getValueQualifiedIdParserRuleCall_2_1_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Property__ValueAssignment_2_1"


    // $ANTLR start "rule__Property__ValueAssignment_2_2"
    // InternalElkGraph.g:5696:1: rule__Property__ValueAssignment_2_2 : ( ruleBoolean ) ;
    public final void rule__Property__ValueAssignment_2_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5700:1: ( ( ruleBoolean ) )
            // InternalElkGraph.g:5701:2: ( ruleBoolean )
            {
            // InternalElkGraph.g:5701:2: ( ruleBoolean )
            // InternalElkGraph.g:5702:3: ruleBoolean
            {
             before(grammarAccess.getPropertyAccess().getValueBooleanParserRuleCall_2_2_0()); 
            pushFollow(FOLLOW_2);
            ruleBoolean();

            state._fsp--;

             after(grammarAccess.getPropertyAccess().getValueBooleanParserRuleCall_2_2_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Property__ValueAssignment_2_2"


    // $ANTLR start "rule__Property__ValueAssignment_2_3"
    // InternalElkGraph.g:5711:1: rule__Property__ValueAssignment_2_3 : ( RULE_SIGNED_INT ) ;
    public final void rule__Property__ValueAssignment_2_3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5715:1: ( ( RULE_SIGNED_INT ) )
            // InternalElkGraph.g:5716:2: ( RULE_SIGNED_INT )
            {
            // InternalElkGraph.g:5716:2: ( RULE_SIGNED_INT )
            // InternalElkGraph.g:5717:3: RULE_SIGNED_INT
            {
             before(grammarAccess.getPropertyAccess().getValueSIGNED_INTTerminalRuleCall_2_3_0()); 
            match(input,RULE_SIGNED_INT,FOLLOW_2); 
             after(grammarAccess.getPropertyAccess().getValueSIGNED_INTTerminalRuleCall_2_3_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Property__ValueAssignment_2_3"


    // $ANTLR start "rule__Property__ValueAssignment_2_4"
    // InternalElkGraph.g:5726:1: rule__Property__ValueAssignment_2_4 : ( RULE_FLOAT ) ;
    public final void rule__Property__ValueAssignment_2_4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5730:1: ( ( RULE_FLOAT ) )
            // InternalElkGraph.g:5731:2: ( RULE_FLOAT )
            {
            // InternalElkGraph.g:5731:2: ( RULE_FLOAT )
            // InternalElkGraph.g:5732:3: RULE_FLOAT
            {
             before(grammarAccess.getPropertyAccess().getValueFLOATTerminalRuleCall_2_4_0()); 
            match(input,RULE_FLOAT,FOLLOW_2); 
             after(grammarAccess.getPropertyAccess().getValueFLOATTerminalRuleCall_2_4_0()); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {

            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__Property__ValueAssignment_2_4"

    // Delegated rules


 

    public static final BitSet FOLLOW_1 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_2 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_3 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_4 = new BitSet(new long[]{0x0000000010298040L});
    public static final BitSet FOLLOW_5 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_6 = new BitSet(new long[]{0x0000000010290002L});
    public static final BitSet FOLLOW_7 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_8 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_9 = new BitSet(new long[]{0x00000000106D0040L});
    public static final BitSet FOLLOW_10 = new BitSet(new long[]{0x00000000000000C0L});
    public static final BitSet FOLLOW_11 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_12 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_13 = new BitSet(new long[]{0x000000000A000000L});
    public static final BitSet FOLLOW_14 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_15 = new BitSet(new long[]{0x0000000000000030L});
    public static final BitSet FOLLOW_16 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_17 = new BitSet(new long[]{0x0000000024000000L});
    public static final BitSet FOLLOW_18 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_19 = new BitSet(new long[]{0x0000000004020000L});
    public static final BitSet FOLLOW_20 = new BitSet(new long[]{0x00000017C0000000L});
    public static final BitSet FOLLOW_21 = new BitSet(new long[]{0x00000007C0000000L});
    public static final BitSet FOLLOW_22 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_23 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_24 = new BitSet(new long[]{0x0000000020800000L});
    public static final BitSet FOLLOW_25 = new BitSet(new long[]{0x00000000000060F0L});
    public static final BitSet FOLLOW_26 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_27 = new BitSet(new long[]{0x0000002000000002L});
    public static final BitSet FOLLOW_28 = new BitSet(new long[]{0x000000000A000002L});
    public static final BitSet FOLLOW_29 = new BitSet(new long[]{0x00000007C0000002L});

}
