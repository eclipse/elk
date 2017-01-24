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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_STRING", "RULE_SIGNED_INT", "RULE_FLOAT", "RULE_ID", "RULE_INT", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'true'", "'false'", "'graph'", "'node'", "'{'", "'}'", "'label'", "':'", "'port'", "'layout'", "'['", "']'", "'position'", "','", "'size'", "'edge'", "'->'", "'incoming'", "'outgoing'", "'start'", "'end'", "'bends'", "'|'", "'section'", "'.'"
    };
    public static final int RULE_STRING=4;
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
    public static final int RULE_ID=7;
    public static final int RULE_WS=11;
    public static final int RULE_SIGNED_INT=5;
    public static final int RULE_ANY_OTHER=12;
    public static final int T__26=26;
    public static final int T__27=27;
    public static final int T__28=28;
    public static final int RULE_INT=8;
    public static final int T__29=29;
    public static final int T__22=22;
    public static final int RULE_ML_COMMENT=9;
    public static final int T__23=23;
    public static final int RULE_FLOAT=6;
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


    // $ANTLR start "entryRuleQualifiedId"
    // InternalElkGraph.g:291:1: entryRuleQualifiedId : ruleQualifiedId EOF ;
    public final void entryRuleQualifiedId() throws RecognitionException {
        try {
            // InternalElkGraph.g:292:1: ( ruleQualifiedId EOF )
            // InternalElkGraph.g:293:1: ruleQualifiedId EOF
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
    // InternalElkGraph.g:300:1: ruleQualifiedId : ( ( rule__QualifiedId__Group__0 ) ) ;
    public final void ruleQualifiedId() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:304:2: ( ( ( rule__QualifiedId__Group__0 ) ) )
            // InternalElkGraph.g:305:2: ( ( rule__QualifiedId__Group__0 ) )
            {
            // InternalElkGraph.g:305:2: ( ( rule__QualifiedId__Group__0 ) )
            // InternalElkGraph.g:306:3: ( rule__QualifiedId__Group__0 )
            {
             before(grammarAccess.getQualifiedIdAccess().getGroup()); 
            // InternalElkGraph.g:307:3: ( rule__QualifiedId__Group__0 )
            // InternalElkGraph.g:307:4: rule__QualifiedId__Group__0
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


    // $ANTLR start "entryRuleNumber"
    // InternalElkGraph.g:316:1: entryRuleNumber : ruleNumber EOF ;
    public final void entryRuleNumber() throws RecognitionException {
        try {
            // InternalElkGraph.g:317:1: ( ruleNumber EOF )
            // InternalElkGraph.g:318:1: ruleNumber EOF
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
    // InternalElkGraph.g:325:1: ruleNumber : ( ( rule__Number__Alternatives ) ) ;
    public final void ruleNumber() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:329:2: ( ( ( rule__Number__Alternatives ) ) )
            // InternalElkGraph.g:330:2: ( ( rule__Number__Alternatives ) )
            {
            // InternalElkGraph.g:330:2: ( ( rule__Number__Alternatives ) )
            // InternalElkGraph.g:331:3: ( rule__Number__Alternatives )
            {
             before(grammarAccess.getNumberAccess().getAlternatives()); 
            // InternalElkGraph.g:332:3: ( rule__Number__Alternatives )
            // InternalElkGraph.g:332:4: rule__Number__Alternatives
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


    // $ANTLR start "entryRuleProperty"
    // InternalElkGraph.g:341:1: entryRuleProperty : ruleProperty EOF ;
    public final void entryRuleProperty() throws RecognitionException {
        try {
            // InternalElkGraph.g:342:1: ( ruleProperty EOF )
            // InternalElkGraph.g:343:1: ruleProperty EOF
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
    // InternalElkGraph.g:350:1: ruleProperty : ( ( rule__Property__Group__0 ) ) ;
    public final void ruleProperty() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:354:2: ( ( ( rule__Property__Group__0 ) ) )
            // InternalElkGraph.g:355:2: ( ( rule__Property__Group__0 ) )
            {
            // InternalElkGraph.g:355:2: ( ( rule__Property__Group__0 ) )
            // InternalElkGraph.g:356:3: ( rule__Property__Group__0 )
            {
             before(grammarAccess.getPropertyAccess().getGroup()); 
            // InternalElkGraph.g:357:3: ( rule__Property__Group__0 )
            // InternalElkGraph.g:357:4: rule__Property__Group__0
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


    // $ANTLR start "entryRulePropertyKey"
    // InternalElkGraph.g:366:1: entryRulePropertyKey : rulePropertyKey EOF ;
    public final void entryRulePropertyKey() throws RecognitionException {
         
        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalElkGraph.g:370:1: ( rulePropertyKey EOF )
            // InternalElkGraph.g:371:1: rulePropertyKey EOF
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
    // InternalElkGraph.g:381:1: rulePropertyKey : ( ( rule__PropertyKey__Group__0 ) ) ;
    public final void rulePropertyKey() throws RecognitionException {

        		HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();
        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:386:2: ( ( ( rule__PropertyKey__Group__0 ) ) )
            // InternalElkGraph.g:387:2: ( ( rule__PropertyKey__Group__0 ) )
            {
            // InternalElkGraph.g:387:2: ( ( rule__PropertyKey__Group__0 ) )
            // InternalElkGraph.g:388:3: ( rule__PropertyKey__Group__0 )
            {
             before(grammarAccess.getPropertyKeyAccess().getGroup()); 
            // InternalElkGraph.g:389:3: ( rule__PropertyKey__Group__0 )
            // InternalElkGraph.g:389:4: rule__PropertyKey__Group__0
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


    // $ANTLR start "entryRuleStringValue"
    // InternalElkGraph.g:399:1: entryRuleStringValue : ruleStringValue EOF ;
    public final void entryRuleStringValue() throws RecognitionException {
        try {
            // InternalElkGraph.g:400:1: ( ruleStringValue EOF )
            // InternalElkGraph.g:401:1: ruleStringValue EOF
            {
             before(grammarAccess.getStringValueRule()); 
            pushFollow(FOLLOW_1);
            ruleStringValue();

            state._fsp--;

             after(grammarAccess.getStringValueRule()); 
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
    // $ANTLR end "entryRuleStringValue"


    // $ANTLR start "ruleStringValue"
    // InternalElkGraph.g:408:1: ruleStringValue : ( RULE_STRING ) ;
    public final void ruleStringValue() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:412:2: ( ( RULE_STRING ) )
            // InternalElkGraph.g:413:2: ( RULE_STRING )
            {
            // InternalElkGraph.g:413:2: ( RULE_STRING )
            // InternalElkGraph.g:414:3: RULE_STRING
            {
             before(grammarAccess.getStringValueAccess().getSTRINGTerminalRuleCall()); 
            match(input,RULE_STRING,FOLLOW_2); 
             after(grammarAccess.getStringValueAccess().getSTRINGTerminalRuleCall()); 

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
    // $ANTLR end "ruleStringValue"


    // $ANTLR start "entryRuleQualifiedIdValue"
    // InternalElkGraph.g:424:1: entryRuleQualifiedIdValue : ruleQualifiedIdValue EOF ;
    public final void entryRuleQualifiedIdValue() throws RecognitionException {
        try {
            // InternalElkGraph.g:425:1: ( ruleQualifiedIdValue EOF )
            // InternalElkGraph.g:426:1: ruleQualifiedIdValue EOF
            {
             before(grammarAccess.getQualifiedIdValueRule()); 
            pushFollow(FOLLOW_1);
            ruleQualifiedIdValue();

            state._fsp--;

             after(grammarAccess.getQualifiedIdValueRule()); 
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
    // $ANTLR end "entryRuleQualifiedIdValue"


    // $ANTLR start "ruleQualifiedIdValue"
    // InternalElkGraph.g:433:1: ruleQualifiedIdValue : ( ruleQualifiedId ) ;
    public final void ruleQualifiedIdValue() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:437:2: ( ( ruleQualifiedId ) )
            // InternalElkGraph.g:438:2: ( ruleQualifiedId )
            {
            // InternalElkGraph.g:438:2: ( ruleQualifiedId )
            // InternalElkGraph.g:439:3: ruleQualifiedId
            {
             before(grammarAccess.getQualifiedIdValueAccess().getQualifiedIdParserRuleCall()); 
            pushFollow(FOLLOW_2);
            ruleQualifiedId();

            state._fsp--;

             after(grammarAccess.getQualifiedIdValueAccess().getQualifiedIdParserRuleCall()); 

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
    // $ANTLR end "ruleQualifiedIdValue"


    // $ANTLR start "entryRuleNumberValue"
    // InternalElkGraph.g:449:1: entryRuleNumberValue : ruleNumberValue EOF ;
    public final void entryRuleNumberValue() throws RecognitionException {
        try {
            // InternalElkGraph.g:450:1: ( ruleNumberValue EOF )
            // InternalElkGraph.g:451:1: ruleNumberValue EOF
            {
             before(grammarAccess.getNumberValueRule()); 
            pushFollow(FOLLOW_1);
            ruleNumberValue();

            state._fsp--;

             after(grammarAccess.getNumberValueRule()); 
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
    // $ANTLR end "entryRuleNumberValue"


    // $ANTLR start "ruleNumberValue"
    // InternalElkGraph.g:458:1: ruleNumberValue : ( ( rule__NumberValue__Alternatives ) ) ;
    public final void ruleNumberValue() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:462:2: ( ( ( rule__NumberValue__Alternatives ) ) )
            // InternalElkGraph.g:463:2: ( ( rule__NumberValue__Alternatives ) )
            {
            // InternalElkGraph.g:463:2: ( ( rule__NumberValue__Alternatives ) )
            // InternalElkGraph.g:464:3: ( rule__NumberValue__Alternatives )
            {
             before(grammarAccess.getNumberValueAccess().getAlternatives()); 
            // InternalElkGraph.g:465:3: ( rule__NumberValue__Alternatives )
            // InternalElkGraph.g:465:4: rule__NumberValue__Alternatives
            {
            pushFollow(FOLLOW_2);
            rule__NumberValue__Alternatives();

            state._fsp--;


            }

             after(grammarAccess.getNumberValueAccess().getAlternatives()); 

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
    // $ANTLR end "ruleNumberValue"


    // $ANTLR start "entryRuleBooleanValue"
    // InternalElkGraph.g:474:1: entryRuleBooleanValue : ruleBooleanValue EOF ;
    public final void entryRuleBooleanValue() throws RecognitionException {
        try {
            // InternalElkGraph.g:475:1: ( ruleBooleanValue EOF )
            // InternalElkGraph.g:476:1: ruleBooleanValue EOF
            {
             before(grammarAccess.getBooleanValueRule()); 
            pushFollow(FOLLOW_1);
            ruleBooleanValue();

            state._fsp--;

             after(grammarAccess.getBooleanValueRule()); 
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
    // $ANTLR end "entryRuleBooleanValue"


    // $ANTLR start "ruleBooleanValue"
    // InternalElkGraph.g:483:1: ruleBooleanValue : ( ( rule__BooleanValue__Alternatives ) ) ;
    public final void ruleBooleanValue() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:487:2: ( ( ( rule__BooleanValue__Alternatives ) ) )
            // InternalElkGraph.g:488:2: ( ( rule__BooleanValue__Alternatives ) )
            {
            // InternalElkGraph.g:488:2: ( ( rule__BooleanValue__Alternatives ) )
            // InternalElkGraph.g:489:3: ( rule__BooleanValue__Alternatives )
            {
             before(grammarAccess.getBooleanValueAccess().getAlternatives()); 
            // InternalElkGraph.g:490:3: ( rule__BooleanValue__Alternatives )
            // InternalElkGraph.g:490:4: rule__BooleanValue__Alternatives
            {
            pushFollow(FOLLOW_2);
            rule__BooleanValue__Alternatives();

            state._fsp--;


            }

             after(grammarAccess.getBooleanValueAccess().getAlternatives()); 

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
    // $ANTLR end "ruleBooleanValue"


    // $ANTLR start "rule__RootNode__Alternatives_3"
    // InternalElkGraph.g:498:1: rule__RootNode__Alternatives_3 : ( ( ( rule__RootNode__LabelsAssignment_3_0 ) ) | ( ( rule__RootNode__PortsAssignment_3_1 ) ) | ( ( rule__RootNode__ChildrenAssignment_3_2 ) ) | ( ( rule__RootNode__ContainedEdgesAssignment_3_3 ) ) );
    public final void rule__RootNode__Alternatives_3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:502:1: ( ( ( rule__RootNode__LabelsAssignment_3_0 ) ) | ( ( rule__RootNode__PortsAssignment_3_1 ) ) | ( ( rule__RootNode__ChildrenAssignment_3_2 ) ) | ( ( rule__RootNode__ContainedEdgesAssignment_3_3 ) ) )
            int alt1=4;
            switch ( input.LA(1) ) {
            case 19:
                {
                alt1=1;
                }
                break;
            case 21:
                {
                alt1=2;
                }
                break;
            case 16:
                {
                alt1=3;
                }
                break;
            case 28:
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
                    // InternalElkGraph.g:503:2: ( ( rule__RootNode__LabelsAssignment_3_0 ) )
                    {
                    // InternalElkGraph.g:503:2: ( ( rule__RootNode__LabelsAssignment_3_0 ) )
                    // InternalElkGraph.g:504:3: ( rule__RootNode__LabelsAssignment_3_0 )
                    {
                     before(grammarAccess.getRootNodeAccess().getLabelsAssignment_3_0()); 
                    // InternalElkGraph.g:505:3: ( rule__RootNode__LabelsAssignment_3_0 )
                    // InternalElkGraph.g:505:4: rule__RootNode__LabelsAssignment_3_0
                    {
                    pushFollow(FOLLOW_2);
                    rule__RootNode__LabelsAssignment_3_0();

                    state._fsp--;


                    }

                     after(grammarAccess.getRootNodeAccess().getLabelsAssignment_3_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalElkGraph.g:509:2: ( ( rule__RootNode__PortsAssignment_3_1 ) )
                    {
                    // InternalElkGraph.g:509:2: ( ( rule__RootNode__PortsAssignment_3_1 ) )
                    // InternalElkGraph.g:510:3: ( rule__RootNode__PortsAssignment_3_1 )
                    {
                     before(grammarAccess.getRootNodeAccess().getPortsAssignment_3_1()); 
                    // InternalElkGraph.g:511:3: ( rule__RootNode__PortsAssignment_3_1 )
                    // InternalElkGraph.g:511:4: rule__RootNode__PortsAssignment_3_1
                    {
                    pushFollow(FOLLOW_2);
                    rule__RootNode__PortsAssignment_3_1();

                    state._fsp--;


                    }

                     after(grammarAccess.getRootNodeAccess().getPortsAssignment_3_1()); 

                    }


                    }
                    break;
                case 3 :
                    // InternalElkGraph.g:515:2: ( ( rule__RootNode__ChildrenAssignment_3_2 ) )
                    {
                    // InternalElkGraph.g:515:2: ( ( rule__RootNode__ChildrenAssignment_3_2 ) )
                    // InternalElkGraph.g:516:3: ( rule__RootNode__ChildrenAssignment_3_2 )
                    {
                     before(grammarAccess.getRootNodeAccess().getChildrenAssignment_3_2()); 
                    // InternalElkGraph.g:517:3: ( rule__RootNode__ChildrenAssignment_3_2 )
                    // InternalElkGraph.g:517:4: rule__RootNode__ChildrenAssignment_3_2
                    {
                    pushFollow(FOLLOW_2);
                    rule__RootNode__ChildrenAssignment_3_2();

                    state._fsp--;


                    }

                     after(grammarAccess.getRootNodeAccess().getChildrenAssignment_3_2()); 

                    }


                    }
                    break;
                case 4 :
                    // InternalElkGraph.g:521:2: ( ( rule__RootNode__ContainedEdgesAssignment_3_3 ) )
                    {
                    // InternalElkGraph.g:521:2: ( ( rule__RootNode__ContainedEdgesAssignment_3_3 ) )
                    // InternalElkGraph.g:522:3: ( rule__RootNode__ContainedEdgesAssignment_3_3 )
                    {
                     before(grammarAccess.getRootNodeAccess().getContainedEdgesAssignment_3_3()); 
                    // InternalElkGraph.g:523:3: ( rule__RootNode__ContainedEdgesAssignment_3_3 )
                    // InternalElkGraph.g:523:4: rule__RootNode__ContainedEdgesAssignment_3_3
                    {
                    pushFollow(FOLLOW_2);
                    rule__RootNode__ContainedEdgesAssignment_3_3();

                    state._fsp--;


                    }

                     after(grammarAccess.getRootNodeAccess().getContainedEdgesAssignment_3_3()); 

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
    // InternalElkGraph.g:531:1: rule__ElkNode__Alternatives_2_3 : ( ( ( rule__ElkNode__LabelsAssignment_2_3_0 ) ) | ( ( rule__ElkNode__PortsAssignment_2_3_1 ) ) | ( ( rule__ElkNode__ChildrenAssignment_2_3_2 ) ) | ( ( rule__ElkNode__ContainedEdgesAssignment_2_3_3 ) ) );
    public final void rule__ElkNode__Alternatives_2_3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:535:1: ( ( ( rule__ElkNode__LabelsAssignment_2_3_0 ) ) | ( ( rule__ElkNode__PortsAssignment_2_3_1 ) ) | ( ( rule__ElkNode__ChildrenAssignment_2_3_2 ) ) | ( ( rule__ElkNode__ContainedEdgesAssignment_2_3_3 ) ) )
            int alt2=4;
            switch ( input.LA(1) ) {
            case 19:
                {
                alt2=1;
                }
                break;
            case 21:
                {
                alt2=2;
                }
                break;
            case 16:
                {
                alt2=3;
                }
                break;
            case 28:
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
                    // InternalElkGraph.g:536:2: ( ( rule__ElkNode__LabelsAssignment_2_3_0 ) )
                    {
                    // InternalElkGraph.g:536:2: ( ( rule__ElkNode__LabelsAssignment_2_3_0 ) )
                    // InternalElkGraph.g:537:3: ( rule__ElkNode__LabelsAssignment_2_3_0 )
                    {
                     before(grammarAccess.getElkNodeAccess().getLabelsAssignment_2_3_0()); 
                    // InternalElkGraph.g:538:3: ( rule__ElkNode__LabelsAssignment_2_3_0 )
                    // InternalElkGraph.g:538:4: rule__ElkNode__LabelsAssignment_2_3_0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkNode__LabelsAssignment_2_3_0();

                    state._fsp--;


                    }

                     after(grammarAccess.getElkNodeAccess().getLabelsAssignment_2_3_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalElkGraph.g:542:2: ( ( rule__ElkNode__PortsAssignment_2_3_1 ) )
                    {
                    // InternalElkGraph.g:542:2: ( ( rule__ElkNode__PortsAssignment_2_3_1 ) )
                    // InternalElkGraph.g:543:3: ( rule__ElkNode__PortsAssignment_2_3_1 )
                    {
                     before(grammarAccess.getElkNodeAccess().getPortsAssignment_2_3_1()); 
                    // InternalElkGraph.g:544:3: ( rule__ElkNode__PortsAssignment_2_3_1 )
                    // InternalElkGraph.g:544:4: rule__ElkNode__PortsAssignment_2_3_1
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkNode__PortsAssignment_2_3_1();

                    state._fsp--;


                    }

                     after(grammarAccess.getElkNodeAccess().getPortsAssignment_2_3_1()); 

                    }


                    }
                    break;
                case 3 :
                    // InternalElkGraph.g:548:2: ( ( rule__ElkNode__ChildrenAssignment_2_3_2 ) )
                    {
                    // InternalElkGraph.g:548:2: ( ( rule__ElkNode__ChildrenAssignment_2_3_2 ) )
                    // InternalElkGraph.g:549:3: ( rule__ElkNode__ChildrenAssignment_2_3_2 )
                    {
                     before(grammarAccess.getElkNodeAccess().getChildrenAssignment_2_3_2()); 
                    // InternalElkGraph.g:550:3: ( rule__ElkNode__ChildrenAssignment_2_3_2 )
                    // InternalElkGraph.g:550:4: rule__ElkNode__ChildrenAssignment_2_3_2
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkNode__ChildrenAssignment_2_3_2();

                    state._fsp--;


                    }

                     after(grammarAccess.getElkNodeAccess().getChildrenAssignment_2_3_2()); 

                    }


                    }
                    break;
                case 4 :
                    // InternalElkGraph.g:554:2: ( ( rule__ElkNode__ContainedEdgesAssignment_2_3_3 ) )
                    {
                    // InternalElkGraph.g:554:2: ( ( rule__ElkNode__ContainedEdgesAssignment_2_3_3 ) )
                    // InternalElkGraph.g:555:3: ( rule__ElkNode__ContainedEdgesAssignment_2_3_3 )
                    {
                     before(grammarAccess.getElkNodeAccess().getContainedEdgesAssignment_2_3_3()); 
                    // InternalElkGraph.g:556:3: ( rule__ElkNode__ContainedEdgesAssignment_2_3_3 )
                    // InternalElkGraph.g:556:4: rule__ElkNode__ContainedEdgesAssignment_2_3_3
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkNode__ContainedEdgesAssignment_2_3_3();

                    state._fsp--;


                    }

                     after(grammarAccess.getElkNodeAccess().getContainedEdgesAssignment_2_3_3()); 

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
    // InternalElkGraph.g:564:1: rule__EdgeLayout__Alternatives_2 : ( ( ( rule__EdgeLayout__SectionsAssignment_2_0 ) ) | ( ( ( rule__EdgeLayout__SectionsAssignment_2_1 ) ) ( ( rule__EdgeLayout__SectionsAssignment_2_1 )* ) ) );
    public final void rule__EdgeLayout__Alternatives_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:568:1: ( ( ( rule__EdgeLayout__SectionsAssignment_2_0 ) ) | ( ( ( rule__EdgeLayout__SectionsAssignment_2_1 ) ) ( ( rule__EdgeLayout__SectionsAssignment_2_1 )* ) ) )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==EOF||LA4_0==RULE_ID||LA4_0==24||(LA4_0>=30 && LA4_0<=34)) ) {
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
                    // InternalElkGraph.g:569:2: ( ( rule__EdgeLayout__SectionsAssignment_2_0 ) )
                    {
                    // InternalElkGraph.g:569:2: ( ( rule__EdgeLayout__SectionsAssignment_2_0 ) )
                    // InternalElkGraph.g:570:3: ( rule__EdgeLayout__SectionsAssignment_2_0 )
                    {
                     before(grammarAccess.getEdgeLayoutAccess().getSectionsAssignment_2_0()); 
                    // InternalElkGraph.g:571:3: ( rule__EdgeLayout__SectionsAssignment_2_0 )
                    // InternalElkGraph.g:571:4: rule__EdgeLayout__SectionsAssignment_2_0
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
                    // InternalElkGraph.g:575:2: ( ( ( rule__EdgeLayout__SectionsAssignment_2_1 ) ) ( ( rule__EdgeLayout__SectionsAssignment_2_1 )* ) )
                    {
                    // InternalElkGraph.g:575:2: ( ( ( rule__EdgeLayout__SectionsAssignment_2_1 ) ) ( ( rule__EdgeLayout__SectionsAssignment_2_1 )* ) )
                    // InternalElkGraph.g:576:3: ( ( rule__EdgeLayout__SectionsAssignment_2_1 ) ) ( ( rule__EdgeLayout__SectionsAssignment_2_1 )* )
                    {
                    // InternalElkGraph.g:576:3: ( ( rule__EdgeLayout__SectionsAssignment_2_1 ) )
                    // InternalElkGraph.g:577:4: ( rule__EdgeLayout__SectionsAssignment_2_1 )
                    {
                     before(grammarAccess.getEdgeLayoutAccess().getSectionsAssignment_2_1()); 
                    // InternalElkGraph.g:578:4: ( rule__EdgeLayout__SectionsAssignment_2_1 )
                    // InternalElkGraph.g:578:5: rule__EdgeLayout__SectionsAssignment_2_1
                    {
                    pushFollow(FOLLOW_3);
                    rule__EdgeLayout__SectionsAssignment_2_1();

                    state._fsp--;


                    }

                     after(grammarAccess.getEdgeLayoutAccess().getSectionsAssignment_2_1()); 

                    }

                    // InternalElkGraph.g:581:3: ( ( rule__EdgeLayout__SectionsAssignment_2_1 )* )
                    // InternalElkGraph.g:582:4: ( rule__EdgeLayout__SectionsAssignment_2_1 )*
                    {
                     before(grammarAccess.getEdgeLayoutAccess().getSectionsAssignment_2_1()); 
                    // InternalElkGraph.g:583:4: ( rule__EdgeLayout__SectionsAssignment_2_1 )*
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( (LA3_0==36) ) {
                            alt3=1;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // InternalElkGraph.g:583:5: rule__EdgeLayout__SectionsAssignment_2_1
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


    // $ANTLR start "rule__Number__Alternatives"
    // InternalElkGraph.g:592:1: rule__Number__Alternatives : ( ( RULE_SIGNED_INT ) | ( RULE_FLOAT ) );
    public final void rule__Number__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:596:1: ( ( RULE_SIGNED_INT ) | ( RULE_FLOAT ) )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==RULE_SIGNED_INT) ) {
                alt5=1;
            }
            else if ( (LA5_0==RULE_FLOAT) ) {
                alt5=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // InternalElkGraph.g:597:2: ( RULE_SIGNED_INT )
                    {
                    // InternalElkGraph.g:597:2: ( RULE_SIGNED_INT )
                    // InternalElkGraph.g:598:3: RULE_SIGNED_INT
                    {
                     before(grammarAccess.getNumberAccess().getSIGNED_INTTerminalRuleCall_0()); 
                    match(input,RULE_SIGNED_INT,FOLLOW_2); 
                     after(grammarAccess.getNumberAccess().getSIGNED_INTTerminalRuleCall_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalElkGraph.g:603:2: ( RULE_FLOAT )
                    {
                    // InternalElkGraph.g:603:2: ( RULE_FLOAT )
                    // InternalElkGraph.g:604:3: RULE_FLOAT
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


    // $ANTLR start "rule__Property__Alternatives_2"
    // InternalElkGraph.g:613:1: rule__Property__Alternatives_2 : ( ( ( rule__Property__ValueAssignment_2_0 ) ) | ( ( rule__Property__ValueAssignment_2_1 ) ) | ( ( rule__Property__ValueAssignment_2_2 ) ) | ( ( rule__Property__ValueAssignment_2_3 ) ) );
    public final void rule__Property__Alternatives_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:617:1: ( ( ( rule__Property__ValueAssignment_2_0 ) ) | ( ( rule__Property__ValueAssignment_2_1 ) ) | ( ( rule__Property__ValueAssignment_2_2 ) ) | ( ( rule__Property__ValueAssignment_2_3 ) ) )
            int alt6=4;
            switch ( input.LA(1) ) {
            case RULE_STRING:
                {
                alt6=1;
                }
                break;
            case RULE_ID:
                {
                alt6=2;
                }
                break;
            case RULE_SIGNED_INT:
            case RULE_FLOAT:
                {
                alt6=3;
                }
                break;
            case 13:
            case 14:
                {
                alt6=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }

            switch (alt6) {
                case 1 :
                    // InternalElkGraph.g:618:2: ( ( rule__Property__ValueAssignment_2_0 ) )
                    {
                    // InternalElkGraph.g:618:2: ( ( rule__Property__ValueAssignment_2_0 ) )
                    // InternalElkGraph.g:619:3: ( rule__Property__ValueAssignment_2_0 )
                    {
                     before(grammarAccess.getPropertyAccess().getValueAssignment_2_0()); 
                    // InternalElkGraph.g:620:3: ( rule__Property__ValueAssignment_2_0 )
                    // InternalElkGraph.g:620:4: rule__Property__ValueAssignment_2_0
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
                    // InternalElkGraph.g:624:2: ( ( rule__Property__ValueAssignment_2_1 ) )
                    {
                    // InternalElkGraph.g:624:2: ( ( rule__Property__ValueAssignment_2_1 ) )
                    // InternalElkGraph.g:625:3: ( rule__Property__ValueAssignment_2_1 )
                    {
                     before(grammarAccess.getPropertyAccess().getValueAssignment_2_1()); 
                    // InternalElkGraph.g:626:3: ( rule__Property__ValueAssignment_2_1 )
                    // InternalElkGraph.g:626:4: rule__Property__ValueAssignment_2_1
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
                    // InternalElkGraph.g:630:2: ( ( rule__Property__ValueAssignment_2_2 ) )
                    {
                    // InternalElkGraph.g:630:2: ( ( rule__Property__ValueAssignment_2_2 ) )
                    // InternalElkGraph.g:631:3: ( rule__Property__ValueAssignment_2_2 )
                    {
                     before(grammarAccess.getPropertyAccess().getValueAssignment_2_2()); 
                    // InternalElkGraph.g:632:3: ( rule__Property__ValueAssignment_2_2 )
                    // InternalElkGraph.g:632:4: rule__Property__ValueAssignment_2_2
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
                    // InternalElkGraph.g:636:2: ( ( rule__Property__ValueAssignment_2_3 ) )
                    {
                    // InternalElkGraph.g:636:2: ( ( rule__Property__ValueAssignment_2_3 ) )
                    // InternalElkGraph.g:637:3: ( rule__Property__ValueAssignment_2_3 )
                    {
                     before(grammarAccess.getPropertyAccess().getValueAssignment_2_3()); 
                    // InternalElkGraph.g:638:3: ( rule__Property__ValueAssignment_2_3 )
                    // InternalElkGraph.g:638:4: rule__Property__ValueAssignment_2_3
                    {
                    pushFollow(FOLLOW_2);
                    rule__Property__ValueAssignment_2_3();

                    state._fsp--;


                    }

                     after(grammarAccess.getPropertyAccess().getValueAssignment_2_3()); 

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


    // $ANTLR start "rule__NumberValue__Alternatives"
    // InternalElkGraph.g:646:1: rule__NumberValue__Alternatives : ( ( RULE_SIGNED_INT ) | ( RULE_FLOAT ) );
    public final void rule__NumberValue__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:650:1: ( ( RULE_SIGNED_INT ) | ( RULE_FLOAT ) )
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
                    // InternalElkGraph.g:651:2: ( RULE_SIGNED_INT )
                    {
                    // InternalElkGraph.g:651:2: ( RULE_SIGNED_INT )
                    // InternalElkGraph.g:652:3: RULE_SIGNED_INT
                    {
                     before(grammarAccess.getNumberValueAccess().getSIGNED_INTTerminalRuleCall_0()); 
                    match(input,RULE_SIGNED_INT,FOLLOW_2); 
                     after(grammarAccess.getNumberValueAccess().getSIGNED_INTTerminalRuleCall_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalElkGraph.g:657:2: ( RULE_FLOAT )
                    {
                    // InternalElkGraph.g:657:2: ( RULE_FLOAT )
                    // InternalElkGraph.g:658:3: RULE_FLOAT
                    {
                     before(grammarAccess.getNumberValueAccess().getFLOATTerminalRuleCall_1()); 
                    match(input,RULE_FLOAT,FOLLOW_2); 
                     after(grammarAccess.getNumberValueAccess().getFLOATTerminalRuleCall_1()); 

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
    // $ANTLR end "rule__NumberValue__Alternatives"


    // $ANTLR start "rule__BooleanValue__Alternatives"
    // InternalElkGraph.g:667:1: rule__BooleanValue__Alternatives : ( ( 'true' ) | ( 'false' ) );
    public final void rule__BooleanValue__Alternatives() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:671:1: ( ( 'true' ) | ( 'false' ) )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==13) ) {
                alt8=1;
            }
            else if ( (LA8_0==14) ) {
                alt8=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // InternalElkGraph.g:672:2: ( 'true' )
                    {
                    // InternalElkGraph.g:672:2: ( 'true' )
                    // InternalElkGraph.g:673:3: 'true'
                    {
                     before(grammarAccess.getBooleanValueAccess().getTrueKeyword_0()); 
                    match(input,13,FOLLOW_2); 
                     after(grammarAccess.getBooleanValueAccess().getTrueKeyword_0()); 

                    }


                    }
                    break;
                case 2 :
                    // InternalElkGraph.g:678:2: ( 'false' )
                    {
                    // InternalElkGraph.g:678:2: ( 'false' )
                    // InternalElkGraph.g:679:3: 'false'
                    {
                     before(grammarAccess.getBooleanValueAccess().getFalseKeyword_1()); 
                    match(input,14,FOLLOW_2); 
                     after(grammarAccess.getBooleanValueAccess().getFalseKeyword_1()); 

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
    // $ANTLR end "rule__BooleanValue__Alternatives"


    // $ANTLR start "rule__RootNode__Group__0"
    // InternalElkGraph.g:688:1: rule__RootNode__Group__0 : rule__RootNode__Group__0__Impl rule__RootNode__Group__1 ;
    public final void rule__RootNode__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:692:1: ( rule__RootNode__Group__0__Impl rule__RootNode__Group__1 )
            // InternalElkGraph.g:693:2: rule__RootNode__Group__0__Impl rule__RootNode__Group__1
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
    // InternalElkGraph.g:700:1: rule__RootNode__Group__0__Impl : ( () ) ;
    public final void rule__RootNode__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:704:1: ( ( () ) )
            // InternalElkGraph.g:705:1: ( () )
            {
            // InternalElkGraph.g:705:1: ( () )
            // InternalElkGraph.g:706:2: ()
            {
             before(grammarAccess.getRootNodeAccess().getElkNodeAction_0()); 
            // InternalElkGraph.g:707:2: ()
            // InternalElkGraph.g:707:3: 
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
    // InternalElkGraph.g:715:1: rule__RootNode__Group__1 : rule__RootNode__Group__1__Impl rule__RootNode__Group__2 ;
    public final void rule__RootNode__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:719:1: ( rule__RootNode__Group__1__Impl rule__RootNode__Group__2 )
            // InternalElkGraph.g:720:2: rule__RootNode__Group__1__Impl rule__RootNode__Group__2
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
    // InternalElkGraph.g:727:1: rule__RootNode__Group__1__Impl : ( ( rule__RootNode__Group_1__0 )? ) ;
    public final void rule__RootNode__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:731:1: ( ( ( rule__RootNode__Group_1__0 )? ) )
            // InternalElkGraph.g:732:1: ( ( rule__RootNode__Group_1__0 )? )
            {
            // InternalElkGraph.g:732:1: ( ( rule__RootNode__Group_1__0 )? )
            // InternalElkGraph.g:733:2: ( rule__RootNode__Group_1__0 )?
            {
             before(grammarAccess.getRootNodeAccess().getGroup_1()); 
            // InternalElkGraph.g:734:2: ( rule__RootNode__Group_1__0 )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==15) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // InternalElkGraph.g:734:3: rule__RootNode__Group_1__0
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
    // InternalElkGraph.g:742:1: rule__RootNode__Group__2 : rule__RootNode__Group__2__Impl rule__RootNode__Group__3 ;
    public final void rule__RootNode__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:746:1: ( rule__RootNode__Group__2__Impl rule__RootNode__Group__3 )
            // InternalElkGraph.g:747:2: rule__RootNode__Group__2__Impl rule__RootNode__Group__3
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
    // InternalElkGraph.g:754:1: rule__RootNode__Group__2__Impl : ( ( rule__RootNode__PropertiesAssignment_2 )* ) ;
    public final void rule__RootNode__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:758:1: ( ( ( rule__RootNode__PropertiesAssignment_2 )* ) )
            // InternalElkGraph.g:759:1: ( ( rule__RootNode__PropertiesAssignment_2 )* )
            {
            // InternalElkGraph.g:759:1: ( ( rule__RootNode__PropertiesAssignment_2 )* )
            // InternalElkGraph.g:760:2: ( rule__RootNode__PropertiesAssignment_2 )*
            {
             before(grammarAccess.getRootNodeAccess().getPropertiesAssignment_2()); 
            // InternalElkGraph.g:761:2: ( rule__RootNode__PropertiesAssignment_2 )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==RULE_ID) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // InternalElkGraph.g:761:3: rule__RootNode__PropertiesAssignment_2
            	    {
            	    pushFollow(FOLLOW_5);
            	    rule__RootNode__PropertiesAssignment_2();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop10;
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
    // InternalElkGraph.g:769:1: rule__RootNode__Group__3 : rule__RootNode__Group__3__Impl ;
    public final void rule__RootNode__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:773:1: ( rule__RootNode__Group__3__Impl )
            // InternalElkGraph.g:774:2: rule__RootNode__Group__3__Impl
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
    // InternalElkGraph.g:780:1: rule__RootNode__Group__3__Impl : ( ( rule__RootNode__Alternatives_3 )* ) ;
    public final void rule__RootNode__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:784:1: ( ( ( rule__RootNode__Alternatives_3 )* ) )
            // InternalElkGraph.g:785:1: ( ( rule__RootNode__Alternatives_3 )* )
            {
            // InternalElkGraph.g:785:1: ( ( rule__RootNode__Alternatives_3 )* )
            // InternalElkGraph.g:786:2: ( rule__RootNode__Alternatives_3 )*
            {
             before(grammarAccess.getRootNodeAccess().getAlternatives_3()); 
            // InternalElkGraph.g:787:2: ( rule__RootNode__Alternatives_3 )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0==16||LA11_0==19||LA11_0==21||LA11_0==28) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // InternalElkGraph.g:787:3: rule__RootNode__Alternatives_3
            	    {
            	    pushFollow(FOLLOW_6);
            	    rule__RootNode__Alternatives_3();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop11;
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
    // InternalElkGraph.g:796:1: rule__RootNode__Group_1__0 : rule__RootNode__Group_1__0__Impl rule__RootNode__Group_1__1 ;
    public final void rule__RootNode__Group_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:800:1: ( rule__RootNode__Group_1__0__Impl rule__RootNode__Group_1__1 )
            // InternalElkGraph.g:801:2: rule__RootNode__Group_1__0__Impl rule__RootNode__Group_1__1
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
    // InternalElkGraph.g:808:1: rule__RootNode__Group_1__0__Impl : ( 'graph' ) ;
    public final void rule__RootNode__Group_1__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:812:1: ( ( 'graph' ) )
            // InternalElkGraph.g:813:1: ( 'graph' )
            {
            // InternalElkGraph.g:813:1: ( 'graph' )
            // InternalElkGraph.g:814:2: 'graph'
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
    // InternalElkGraph.g:823:1: rule__RootNode__Group_1__1 : rule__RootNode__Group_1__1__Impl ;
    public final void rule__RootNode__Group_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:827:1: ( rule__RootNode__Group_1__1__Impl )
            // InternalElkGraph.g:828:2: rule__RootNode__Group_1__1__Impl
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
    // InternalElkGraph.g:834:1: rule__RootNode__Group_1__1__Impl : ( ( rule__RootNode__IdentifierAssignment_1_1 ) ) ;
    public final void rule__RootNode__Group_1__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:838:1: ( ( ( rule__RootNode__IdentifierAssignment_1_1 ) ) )
            // InternalElkGraph.g:839:1: ( ( rule__RootNode__IdentifierAssignment_1_1 ) )
            {
            // InternalElkGraph.g:839:1: ( ( rule__RootNode__IdentifierAssignment_1_1 ) )
            // InternalElkGraph.g:840:2: ( rule__RootNode__IdentifierAssignment_1_1 )
            {
             before(grammarAccess.getRootNodeAccess().getIdentifierAssignment_1_1()); 
            // InternalElkGraph.g:841:2: ( rule__RootNode__IdentifierAssignment_1_1 )
            // InternalElkGraph.g:841:3: rule__RootNode__IdentifierAssignment_1_1
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
    // InternalElkGraph.g:850:1: rule__ElkNode__Group__0 : rule__ElkNode__Group__0__Impl rule__ElkNode__Group__1 ;
    public final void rule__ElkNode__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:854:1: ( rule__ElkNode__Group__0__Impl rule__ElkNode__Group__1 )
            // InternalElkGraph.g:855:2: rule__ElkNode__Group__0__Impl rule__ElkNode__Group__1
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
    // InternalElkGraph.g:862:1: rule__ElkNode__Group__0__Impl : ( 'node' ) ;
    public final void rule__ElkNode__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:866:1: ( ( 'node' ) )
            // InternalElkGraph.g:867:1: ( 'node' )
            {
            // InternalElkGraph.g:867:1: ( 'node' )
            // InternalElkGraph.g:868:2: 'node'
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
    // InternalElkGraph.g:877:1: rule__ElkNode__Group__1 : rule__ElkNode__Group__1__Impl rule__ElkNode__Group__2 ;
    public final void rule__ElkNode__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:881:1: ( rule__ElkNode__Group__1__Impl rule__ElkNode__Group__2 )
            // InternalElkGraph.g:882:2: rule__ElkNode__Group__1__Impl rule__ElkNode__Group__2
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
    // InternalElkGraph.g:889:1: rule__ElkNode__Group__1__Impl : ( ( rule__ElkNode__IdentifierAssignment_1 ) ) ;
    public final void rule__ElkNode__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:893:1: ( ( ( rule__ElkNode__IdentifierAssignment_1 ) ) )
            // InternalElkGraph.g:894:1: ( ( rule__ElkNode__IdentifierAssignment_1 ) )
            {
            // InternalElkGraph.g:894:1: ( ( rule__ElkNode__IdentifierAssignment_1 ) )
            // InternalElkGraph.g:895:2: ( rule__ElkNode__IdentifierAssignment_1 )
            {
             before(grammarAccess.getElkNodeAccess().getIdentifierAssignment_1()); 
            // InternalElkGraph.g:896:2: ( rule__ElkNode__IdentifierAssignment_1 )
            // InternalElkGraph.g:896:3: rule__ElkNode__IdentifierAssignment_1
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
    // InternalElkGraph.g:904:1: rule__ElkNode__Group__2 : rule__ElkNode__Group__2__Impl ;
    public final void rule__ElkNode__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:908:1: ( rule__ElkNode__Group__2__Impl )
            // InternalElkGraph.g:909:2: rule__ElkNode__Group__2__Impl
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
    // InternalElkGraph.g:915:1: rule__ElkNode__Group__2__Impl : ( ( rule__ElkNode__Group_2__0 )? ) ;
    public final void rule__ElkNode__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:919:1: ( ( ( rule__ElkNode__Group_2__0 )? ) )
            // InternalElkGraph.g:920:1: ( ( rule__ElkNode__Group_2__0 )? )
            {
            // InternalElkGraph.g:920:1: ( ( rule__ElkNode__Group_2__0 )? )
            // InternalElkGraph.g:921:2: ( rule__ElkNode__Group_2__0 )?
            {
             before(grammarAccess.getElkNodeAccess().getGroup_2()); 
            // InternalElkGraph.g:922:2: ( rule__ElkNode__Group_2__0 )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==17) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // InternalElkGraph.g:922:3: rule__ElkNode__Group_2__0
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
    // InternalElkGraph.g:931:1: rule__ElkNode__Group_2__0 : rule__ElkNode__Group_2__0__Impl rule__ElkNode__Group_2__1 ;
    public final void rule__ElkNode__Group_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:935:1: ( rule__ElkNode__Group_2__0__Impl rule__ElkNode__Group_2__1 )
            // InternalElkGraph.g:936:2: rule__ElkNode__Group_2__0__Impl rule__ElkNode__Group_2__1
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
    // InternalElkGraph.g:943:1: rule__ElkNode__Group_2__0__Impl : ( '{' ) ;
    public final void rule__ElkNode__Group_2__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:947:1: ( ( '{' ) )
            // InternalElkGraph.g:948:1: ( '{' )
            {
            // InternalElkGraph.g:948:1: ( '{' )
            // InternalElkGraph.g:949:2: '{'
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
    // InternalElkGraph.g:958:1: rule__ElkNode__Group_2__1 : rule__ElkNode__Group_2__1__Impl rule__ElkNode__Group_2__2 ;
    public final void rule__ElkNode__Group_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:962:1: ( rule__ElkNode__Group_2__1__Impl rule__ElkNode__Group_2__2 )
            // InternalElkGraph.g:963:2: rule__ElkNode__Group_2__1__Impl rule__ElkNode__Group_2__2
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
    // InternalElkGraph.g:970:1: rule__ElkNode__Group_2__1__Impl : ( ( ruleShapeLayout )? ) ;
    public final void rule__ElkNode__Group_2__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:974:1: ( ( ( ruleShapeLayout )? ) )
            // InternalElkGraph.g:975:1: ( ( ruleShapeLayout )? )
            {
            // InternalElkGraph.g:975:1: ( ( ruleShapeLayout )? )
            // InternalElkGraph.g:976:2: ( ruleShapeLayout )?
            {
             before(grammarAccess.getElkNodeAccess().getShapeLayoutParserRuleCall_2_1()); 
            // InternalElkGraph.g:977:2: ( ruleShapeLayout )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==22) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // InternalElkGraph.g:977:3: ruleShapeLayout
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
    // InternalElkGraph.g:985:1: rule__ElkNode__Group_2__2 : rule__ElkNode__Group_2__2__Impl rule__ElkNode__Group_2__3 ;
    public final void rule__ElkNode__Group_2__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:989:1: ( rule__ElkNode__Group_2__2__Impl rule__ElkNode__Group_2__3 )
            // InternalElkGraph.g:990:2: rule__ElkNode__Group_2__2__Impl rule__ElkNode__Group_2__3
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
    // InternalElkGraph.g:997:1: rule__ElkNode__Group_2__2__Impl : ( ( rule__ElkNode__PropertiesAssignment_2_2 )* ) ;
    public final void rule__ElkNode__Group_2__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1001:1: ( ( ( rule__ElkNode__PropertiesAssignment_2_2 )* ) )
            // InternalElkGraph.g:1002:1: ( ( rule__ElkNode__PropertiesAssignment_2_2 )* )
            {
            // InternalElkGraph.g:1002:1: ( ( rule__ElkNode__PropertiesAssignment_2_2 )* )
            // InternalElkGraph.g:1003:2: ( rule__ElkNode__PropertiesAssignment_2_2 )*
            {
             before(grammarAccess.getElkNodeAccess().getPropertiesAssignment_2_2()); 
            // InternalElkGraph.g:1004:2: ( rule__ElkNode__PropertiesAssignment_2_2 )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0==RULE_ID) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // InternalElkGraph.g:1004:3: rule__ElkNode__PropertiesAssignment_2_2
            	    {
            	    pushFollow(FOLLOW_5);
            	    rule__ElkNode__PropertiesAssignment_2_2();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop14;
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
    // InternalElkGraph.g:1012:1: rule__ElkNode__Group_2__3 : rule__ElkNode__Group_2__3__Impl rule__ElkNode__Group_2__4 ;
    public final void rule__ElkNode__Group_2__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1016:1: ( rule__ElkNode__Group_2__3__Impl rule__ElkNode__Group_2__4 )
            // InternalElkGraph.g:1017:2: rule__ElkNode__Group_2__3__Impl rule__ElkNode__Group_2__4
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
    // InternalElkGraph.g:1024:1: rule__ElkNode__Group_2__3__Impl : ( ( rule__ElkNode__Alternatives_2_3 )* ) ;
    public final void rule__ElkNode__Group_2__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1028:1: ( ( ( rule__ElkNode__Alternatives_2_3 )* ) )
            // InternalElkGraph.g:1029:1: ( ( rule__ElkNode__Alternatives_2_3 )* )
            {
            // InternalElkGraph.g:1029:1: ( ( rule__ElkNode__Alternatives_2_3 )* )
            // InternalElkGraph.g:1030:2: ( rule__ElkNode__Alternatives_2_3 )*
            {
             before(grammarAccess.getElkNodeAccess().getAlternatives_2_3()); 
            // InternalElkGraph.g:1031:2: ( rule__ElkNode__Alternatives_2_3 )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==16||LA15_0==19||LA15_0==21||LA15_0==28) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // InternalElkGraph.g:1031:3: rule__ElkNode__Alternatives_2_3
            	    {
            	    pushFollow(FOLLOW_6);
            	    rule__ElkNode__Alternatives_2_3();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop15;
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
    // InternalElkGraph.g:1039:1: rule__ElkNode__Group_2__4 : rule__ElkNode__Group_2__4__Impl ;
    public final void rule__ElkNode__Group_2__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1043:1: ( rule__ElkNode__Group_2__4__Impl )
            // InternalElkGraph.g:1044:2: rule__ElkNode__Group_2__4__Impl
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
    // InternalElkGraph.g:1050:1: rule__ElkNode__Group_2__4__Impl : ( '}' ) ;
    public final void rule__ElkNode__Group_2__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1054:1: ( ( '}' ) )
            // InternalElkGraph.g:1055:1: ( '}' )
            {
            // InternalElkGraph.g:1055:1: ( '}' )
            // InternalElkGraph.g:1056:2: '}'
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
    // InternalElkGraph.g:1066:1: rule__ElkLabel__Group__0 : rule__ElkLabel__Group__0__Impl rule__ElkLabel__Group__1 ;
    public final void rule__ElkLabel__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1070:1: ( rule__ElkLabel__Group__0__Impl rule__ElkLabel__Group__1 )
            // InternalElkGraph.g:1071:2: rule__ElkLabel__Group__0__Impl rule__ElkLabel__Group__1
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
    // InternalElkGraph.g:1078:1: rule__ElkLabel__Group__0__Impl : ( 'label' ) ;
    public final void rule__ElkLabel__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1082:1: ( ( 'label' ) )
            // InternalElkGraph.g:1083:1: ( 'label' )
            {
            // InternalElkGraph.g:1083:1: ( 'label' )
            // InternalElkGraph.g:1084:2: 'label'
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
    // InternalElkGraph.g:1093:1: rule__ElkLabel__Group__1 : rule__ElkLabel__Group__1__Impl rule__ElkLabel__Group__2 ;
    public final void rule__ElkLabel__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1097:1: ( rule__ElkLabel__Group__1__Impl rule__ElkLabel__Group__2 )
            // InternalElkGraph.g:1098:2: rule__ElkLabel__Group__1__Impl rule__ElkLabel__Group__2
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
    // InternalElkGraph.g:1105:1: rule__ElkLabel__Group__1__Impl : ( ( rule__ElkLabel__Group_1__0 )? ) ;
    public final void rule__ElkLabel__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1109:1: ( ( ( rule__ElkLabel__Group_1__0 )? ) )
            // InternalElkGraph.g:1110:1: ( ( rule__ElkLabel__Group_1__0 )? )
            {
            // InternalElkGraph.g:1110:1: ( ( rule__ElkLabel__Group_1__0 )? )
            // InternalElkGraph.g:1111:2: ( rule__ElkLabel__Group_1__0 )?
            {
             before(grammarAccess.getElkLabelAccess().getGroup_1()); 
            // InternalElkGraph.g:1112:2: ( rule__ElkLabel__Group_1__0 )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==RULE_ID) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // InternalElkGraph.g:1112:3: rule__ElkLabel__Group_1__0
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
    // InternalElkGraph.g:1120:1: rule__ElkLabel__Group__2 : rule__ElkLabel__Group__2__Impl rule__ElkLabel__Group__3 ;
    public final void rule__ElkLabel__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1124:1: ( rule__ElkLabel__Group__2__Impl rule__ElkLabel__Group__3 )
            // InternalElkGraph.g:1125:2: rule__ElkLabel__Group__2__Impl rule__ElkLabel__Group__3
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
    // InternalElkGraph.g:1132:1: rule__ElkLabel__Group__2__Impl : ( ( rule__ElkLabel__TextAssignment_2 ) ) ;
    public final void rule__ElkLabel__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1136:1: ( ( ( rule__ElkLabel__TextAssignment_2 ) ) )
            // InternalElkGraph.g:1137:1: ( ( rule__ElkLabel__TextAssignment_2 ) )
            {
            // InternalElkGraph.g:1137:1: ( ( rule__ElkLabel__TextAssignment_2 ) )
            // InternalElkGraph.g:1138:2: ( rule__ElkLabel__TextAssignment_2 )
            {
             before(grammarAccess.getElkLabelAccess().getTextAssignment_2()); 
            // InternalElkGraph.g:1139:2: ( rule__ElkLabel__TextAssignment_2 )
            // InternalElkGraph.g:1139:3: rule__ElkLabel__TextAssignment_2
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
    // InternalElkGraph.g:1147:1: rule__ElkLabel__Group__3 : rule__ElkLabel__Group__3__Impl ;
    public final void rule__ElkLabel__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1151:1: ( rule__ElkLabel__Group__3__Impl )
            // InternalElkGraph.g:1152:2: rule__ElkLabel__Group__3__Impl
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
    // InternalElkGraph.g:1158:1: rule__ElkLabel__Group__3__Impl : ( ( rule__ElkLabel__Group_3__0 )? ) ;
    public final void rule__ElkLabel__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1162:1: ( ( ( rule__ElkLabel__Group_3__0 )? ) )
            // InternalElkGraph.g:1163:1: ( ( rule__ElkLabel__Group_3__0 )? )
            {
            // InternalElkGraph.g:1163:1: ( ( rule__ElkLabel__Group_3__0 )? )
            // InternalElkGraph.g:1164:2: ( rule__ElkLabel__Group_3__0 )?
            {
             before(grammarAccess.getElkLabelAccess().getGroup_3()); 
            // InternalElkGraph.g:1165:2: ( rule__ElkLabel__Group_3__0 )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==17) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // InternalElkGraph.g:1165:3: rule__ElkLabel__Group_3__0
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
    // InternalElkGraph.g:1174:1: rule__ElkLabel__Group_1__0 : rule__ElkLabel__Group_1__0__Impl rule__ElkLabel__Group_1__1 ;
    public final void rule__ElkLabel__Group_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1178:1: ( rule__ElkLabel__Group_1__0__Impl rule__ElkLabel__Group_1__1 )
            // InternalElkGraph.g:1179:2: rule__ElkLabel__Group_1__0__Impl rule__ElkLabel__Group_1__1
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
    // InternalElkGraph.g:1186:1: rule__ElkLabel__Group_1__0__Impl : ( ( rule__ElkLabel__IdentifierAssignment_1_0 ) ) ;
    public final void rule__ElkLabel__Group_1__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1190:1: ( ( ( rule__ElkLabel__IdentifierAssignment_1_0 ) ) )
            // InternalElkGraph.g:1191:1: ( ( rule__ElkLabel__IdentifierAssignment_1_0 ) )
            {
            // InternalElkGraph.g:1191:1: ( ( rule__ElkLabel__IdentifierAssignment_1_0 ) )
            // InternalElkGraph.g:1192:2: ( rule__ElkLabel__IdentifierAssignment_1_0 )
            {
             before(grammarAccess.getElkLabelAccess().getIdentifierAssignment_1_0()); 
            // InternalElkGraph.g:1193:2: ( rule__ElkLabel__IdentifierAssignment_1_0 )
            // InternalElkGraph.g:1193:3: rule__ElkLabel__IdentifierAssignment_1_0
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
    // InternalElkGraph.g:1201:1: rule__ElkLabel__Group_1__1 : rule__ElkLabel__Group_1__1__Impl ;
    public final void rule__ElkLabel__Group_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1205:1: ( rule__ElkLabel__Group_1__1__Impl )
            // InternalElkGraph.g:1206:2: rule__ElkLabel__Group_1__1__Impl
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
    // InternalElkGraph.g:1212:1: rule__ElkLabel__Group_1__1__Impl : ( ':' ) ;
    public final void rule__ElkLabel__Group_1__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1216:1: ( ( ':' ) )
            // InternalElkGraph.g:1217:1: ( ':' )
            {
            // InternalElkGraph.g:1217:1: ( ':' )
            // InternalElkGraph.g:1218:2: ':'
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
    // InternalElkGraph.g:1228:1: rule__ElkLabel__Group_3__0 : rule__ElkLabel__Group_3__0__Impl rule__ElkLabel__Group_3__1 ;
    public final void rule__ElkLabel__Group_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1232:1: ( rule__ElkLabel__Group_3__0__Impl rule__ElkLabel__Group_3__1 )
            // InternalElkGraph.g:1233:2: rule__ElkLabel__Group_3__0__Impl rule__ElkLabel__Group_3__1
            {
            pushFollow(FOLLOW_12);
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
    // InternalElkGraph.g:1240:1: rule__ElkLabel__Group_3__0__Impl : ( '{' ) ;
    public final void rule__ElkLabel__Group_3__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1244:1: ( ( '{' ) )
            // InternalElkGraph.g:1245:1: ( '{' )
            {
            // InternalElkGraph.g:1245:1: ( '{' )
            // InternalElkGraph.g:1246:2: '{'
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
    // InternalElkGraph.g:1255:1: rule__ElkLabel__Group_3__1 : rule__ElkLabel__Group_3__1__Impl rule__ElkLabel__Group_3__2 ;
    public final void rule__ElkLabel__Group_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1259:1: ( rule__ElkLabel__Group_3__1__Impl rule__ElkLabel__Group_3__2 )
            // InternalElkGraph.g:1260:2: rule__ElkLabel__Group_3__1__Impl rule__ElkLabel__Group_3__2
            {
            pushFollow(FOLLOW_12);
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
    // InternalElkGraph.g:1267:1: rule__ElkLabel__Group_3__1__Impl : ( ( ruleShapeLayout )? ) ;
    public final void rule__ElkLabel__Group_3__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1271:1: ( ( ( ruleShapeLayout )? ) )
            // InternalElkGraph.g:1272:1: ( ( ruleShapeLayout )? )
            {
            // InternalElkGraph.g:1272:1: ( ( ruleShapeLayout )? )
            // InternalElkGraph.g:1273:2: ( ruleShapeLayout )?
            {
             before(grammarAccess.getElkLabelAccess().getShapeLayoutParserRuleCall_3_1()); 
            // InternalElkGraph.g:1274:2: ( ruleShapeLayout )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==22) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // InternalElkGraph.g:1274:3: ruleShapeLayout
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
    // InternalElkGraph.g:1282:1: rule__ElkLabel__Group_3__2 : rule__ElkLabel__Group_3__2__Impl rule__ElkLabel__Group_3__3 ;
    public final void rule__ElkLabel__Group_3__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1286:1: ( rule__ElkLabel__Group_3__2__Impl rule__ElkLabel__Group_3__3 )
            // InternalElkGraph.g:1287:2: rule__ElkLabel__Group_3__2__Impl rule__ElkLabel__Group_3__3
            {
            pushFollow(FOLLOW_12);
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
    // InternalElkGraph.g:1294:1: rule__ElkLabel__Group_3__2__Impl : ( ( rule__ElkLabel__PropertiesAssignment_3_2 )* ) ;
    public final void rule__ElkLabel__Group_3__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1298:1: ( ( ( rule__ElkLabel__PropertiesAssignment_3_2 )* ) )
            // InternalElkGraph.g:1299:1: ( ( rule__ElkLabel__PropertiesAssignment_3_2 )* )
            {
            // InternalElkGraph.g:1299:1: ( ( rule__ElkLabel__PropertiesAssignment_3_2 )* )
            // InternalElkGraph.g:1300:2: ( rule__ElkLabel__PropertiesAssignment_3_2 )*
            {
             before(grammarAccess.getElkLabelAccess().getPropertiesAssignment_3_2()); 
            // InternalElkGraph.g:1301:2: ( rule__ElkLabel__PropertiesAssignment_3_2 )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==RULE_ID) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // InternalElkGraph.g:1301:3: rule__ElkLabel__PropertiesAssignment_3_2
            	    {
            	    pushFollow(FOLLOW_5);
            	    rule__ElkLabel__PropertiesAssignment_3_2();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop19;
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
    // InternalElkGraph.g:1309:1: rule__ElkLabel__Group_3__3 : rule__ElkLabel__Group_3__3__Impl rule__ElkLabel__Group_3__4 ;
    public final void rule__ElkLabel__Group_3__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1313:1: ( rule__ElkLabel__Group_3__3__Impl rule__ElkLabel__Group_3__4 )
            // InternalElkGraph.g:1314:2: rule__ElkLabel__Group_3__3__Impl rule__ElkLabel__Group_3__4
            {
            pushFollow(FOLLOW_12);
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
    // InternalElkGraph.g:1321:1: rule__ElkLabel__Group_3__3__Impl : ( ( rule__ElkLabel__LabelsAssignment_3_3 )* ) ;
    public final void rule__ElkLabel__Group_3__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1325:1: ( ( ( rule__ElkLabel__LabelsAssignment_3_3 )* ) )
            // InternalElkGraph.g:1326:1: ( ( rule__ElkLabel__LabelsAssignment_3_3 )* )
            {
            // InternalElkGraph.g:1326:1: ( ( rule__ElkLabel__LabelsAssignment_3_3 )* )
            // InternalElkGraph.g:1327:2: ( rule__ElkLabel__LabelsAssignment_3_3 )*
            {
             before(grammarAccess.getElkLabelAccess().getLabelsAssignment_3_3()); 
            // InternalElkGraph.g:1328:2: ( rule__ElkLabel__LabelsAssignment_3_3 )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==19) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // InternalElkGraph.g:1328:3: rule__ElkLabel__LabelsAssignment_3_3
            	    {
            	    pushFollow(FOLLOW_13);
            	    rule__ElkLabel__LabelsAssignment_3_3();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop20;
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
    // InternalElkGraph.g:1336:1: rule__ElkLabel__Group_3__4 : rule__ElkLabel__Group_3__4__Impl ;
    public final void rule__ElkLabel__Group_3__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1340:1: ( rule__ElkLabel__Group_3__4__Impl )
            // InternalElkGraph.g:1341:2: rule__ElkLabel__Group_3__4__Impl
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
    // InternalElkGraph.g:1347:1: rule__ElkLabel__Group_3__4__Impl : ( '}' ) ;
    public final void rule__ElkLabel__Group_3__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1351:1: ( ( '}' ) )
            // InternalElkGraph.g:1352:1: ( '}' )
            {
            // InternalElkGraph.g:1352:1: ( '}' )
            // InternalElkGraph.g:1353:2: '}'
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
    // InternalElkGraph.g:1363:1: rule__ElkPort__Group__0 : rule__ElkPort__Group__0__Impl rule__ElkPort__Group__1 ;
    public final void rule__ElkPort__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1367:1: ( rule__ElkPort__Group__0__Impl rule__ElkPort__Group__1 )
            // InternalElkGraph.g:1368:2: rule__ElkPort__Group__0__Impl rule__ElkPort__Group__1
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
    // InternalElkGraph.g:1375:1: rule__ElkPort__Group__0__Impl : ( 'port' ) ;
    public final void rule__ElkPort__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1379:1: ( ( 'port' ) )
            // InternalElkGraph.g:1380:1: ( 'port' )
            {
            // InternalElkGraph.g:1380:1: ( 'port' )
            // InternalElkGraph.g:1381:2: 'port'
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
    // InternalElkGraph.g:1390:1: rule__ElkPort__Group__1 : rule__ElkPort__Group__1__Impl rule__ElkPort__Group__2 ;
    public final void rule__ElkPort__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1394:1: ( rule__ElkPort__Group__1__Impl rule__ElkPort__Group__2 )
            // InternalElkGraph.g:1395:2: rule__ElkPort__Group__1__Impl rule__ElkPort__Group__2
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
    // InternalElkGraph.g:1402:1: rule__ElkPort__Group__1__Impl : ( ( rule__ElkPort__IdentifierAssignment_1 ) ) ;
    public final void rule__ElkPort__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1406:1: ( ( ( rule__ElkPort__IdentifierAssignment_1 ) ) )
            // InternalElkGraph.g:1407:1: ( ( rule__ElkPort__IdentifierAssignment_1 ) )
            {
            // InternalElkGraph.g:1407:1: ( ( rule__ElkPort__IdentifierAssignment_1 ) )
            // InternalElkGraph.g:1408:2: ( rule__ElkPort__IdentifierAssignment_1 )
            {
             before(grammarAccess.getElkPortAccess().getIdentifierAssignment_1()); 
            // InternalElkGraph.g:1409:2: ( rule__ElkPort__IdentifierAssignment_1 )
            // InternalElkGraph.g:1409:3: rule__ElkPort__IdentifierAssignment_1
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
    // InternalElkGraph.g:1417:1: rule__ElkPort__Group__2 : rule__ElkPort__Group__2__Impl ;
    public final void rule__ElkPort__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1421:1: ( rule__ElkPort__Group__2__Impl )
            // InternalElkGraph.g:1422:2: rule__ElkPort__Group__2__Impl
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
    // InternalElkGraph.g:1428:1: rule__ElkPort__Group__2__Impl : ( ( rule__ElkPort__Group_2__0 )? ) ;
    public final void rule__ElkPort__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1432:1: ( ( ( rule__ElkPort__Group_2__0 )? ) )
            // InternalElkGraph.g:1433:1: ( ( rule__ElkPort__Group_2__0 )? )
            {
            // InternalElkGraph.g:1433:1: ( ( rule__ElkPort__Group_2__0 )? )
            // InternalElkGraph.g:1434:2: ( rule__ElkPort__Group_2__0 )?
            {
             before(grammarAccess.getElkPortAccess().getGroup_2()); 
            // InternalElkGraph.g:1435:2: ( rule__ElkPort__Group_2__0 )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==17) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // InternalElkGraph.g:1435:3: rule__ElkPort__Group_2__0
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
    // InternalElkGraph.g:1444:1: rule__ElkPort__Group_2__0 : rule__ElkPort__Group_2__0__Impl rule__ElkPort__Group_2__1 ;
    public final void rule__ElkPort__Group_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1448:1: ( rule__ElkPort__Group_2__0__Impl rule__ElkPort__Group_2__1 )
            // InternalElkGraph.g:1449:2: rule__ElkPort__Group_2__0__Impl rule__ElkPort__Group_2__1
            {
            pushFollow(FOLLOW_12);
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
    // InternalElkGraph.g:1456:1: rule__ElkPort__Group_2__0__Impl : ( '{' ) ;
    public final void rule__ElkPort__Group_2__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1460:1: ( ( '{' ) )
            // InternalElkGraph.g:1461:1: ( '{' )
            {
            // InternalElkGraph.g:1461:1: ( '{' )
            // InternalElkGraph.g:1462:2: '{'
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
    // InternalElkGraph.g:1471:1: rule__ElkPort__Group_2__1 : rule__ElkPort__Group_2__1__Impl rule__ElkPort__Group_2__2 ;
    public final void rule__ElkPort__Group_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1475:1: ( rule__ElkPort__Group_2__1__Impl rule__ElkPort__Group_2__2 )
            // InternalElkGraph.g:1476:2: rule__ElkPort__Group_2__1__Impl rule__ElkPort__Group_2__2
            {
            pushFollow(FOLLOW_12);
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
    // InternalElkGraph.g:1483:1: rule__ElkPort__Group_2__1__Impl : ( ( ruleShapeLayout )? ) ;
    public final void rule__ElkPort__Group_2__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1487:1: ( ( ( ruleShapeLayout )? ) )
            // InternalElkGraph.g:1488:1: ( ( ruleShapeLayout )? )
            {
            // InternalElkGraph.g:1488:1: ( ( ruleShapeLayout )? )
            // InternalElkGraph.g:1489:2: ( ruleShapeLayout )?
            {
             before(grammarAccess.getElkPortAccess().getShapeLayoutParserRuleCall_2_1()); 
            // InternalElkGraph.g:1490:2: ( ruleShapeLayout )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==22) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // InternalElkGraph.g:1490:3: ruleShapeLayout
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
    // InternalElkGraph.g:1498:1: rule__ElkPort__Group_2__2 : rule__ElkPort__Group_2__2__Impl rule__ElkPort__Group_2__3 ;
    public final void rule__ElkPort__Group_2__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1502:1: ( rule__ElkPort__Group_2__2__Impl rule__ElkPort__Group_2__3 )
            // InternalElkGraph.g:1503:2: rule__ElkPort__Group_2__2__Impl rule__ElkPort__Group_2__3
            {
            pushFollow(FOLLOW_12);
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
    // InternalElkGraph.g:1510:1: rule__ElkPort__Group_2__2__Impl : ( ( rule__ElkPort__PropertiesAssignment_2_2 )* ) ;
    public final void rule__ElkPort__Group_2__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1514:1: ( ( ( rule__ElkPort__PropertiesAssignment_2_2 )* ) )
            // InternalElkGraph.g:1515:1: ( ( rule__ElkPort__PropertiesAssignment_2_2 )* )
            {
            // InternalElkGraph.g:1515:1: ( ( rule__ElkPort__PropertiesAssignment_2_2 )* )
            // InternalElkGraph.g:1516:2: ( rule__ElkPort__PropertiesAssignment_2_2 )*
            {
             before(grammarAccess.getElkPortAccess().getPropertiesAssignment_2_2()); 
            // InternalElkGraph.g:1517:2: ( rule__ElkPort__PropertiesAssignment_2_2 )*
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( (LA23_0==RULE_ID) ) {
                    alt23=1;
                }


                switch (alt23) {
            	case 1 :
            	    // InternalElkGraph.g:1517:3: rule__ElkPort__PropertiesAssignment_2_2
            	    {
            	    pushFollow(FOLLOW_5);
            	    rule__ElkPort__PropertiesAssignment_2_2();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop23;
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
    // InternalElkGraph.g:1525:1: rule__ElkPort__Group_2__3 : rule__ElkPort__Group_2__3__Impl rule__ElkPort__Group_2__4 ;
    public final void rule__ElkPort__Group_2__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1529:1: ( rule__ElkPort__Group_2__3__Impl rule__ElkPort__Group_2__4 )
            // InternalElkGraph.g:1530:2: rule__ElkPort__Group_2__3__Impl rule__ElkPort__Group_2__4
            {
            pushFollow(FOLLOW_12);
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
    // InternalElkGraph.g:1537:1: rule__ElkPort__Group_2__3__Impl : ( ( rule__ElkPort__LabelsAssignment_2_3 )* ) ;
    public final void rule__ElkPort__Group_2__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1541:1: ( ( ( rule__ElkPort__LabelsAssignment_2_3 )* ) )
            // InternalElkGraph.g:1542:1: ( ( rule__ElkPort__LabelsAssignment_2_3 )* )
            {
            // InternalElkGraph.g:1542:1: ( ( rule__ElkPort__LabelsAssignment_2_3 )* )
            // InternalElkGraph.g:1543:2: ( rule__ElkPort__LabelsAssignment_2_3 )*
            {
             before(grammarAccess.getElkPortAccess().getLabelsAssignment_2_3()); 
            // InternalElkGraph.g:1544:2: ( rule__ElkPort__LabelsAssignment_2_3 )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( (LA24_0==19) ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // InternalElkGraph.g:1544:3: rule__ElkPort__LabelsAssignment_2_3
            	    {
            	    pushFollow(FOLLOW_13);
            	    rule__ElkPort__LabelsAssignment_2_3();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop24;
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
    // InternalElkGraph.g:1552:1: rule__ElkPort__Group_2__4 : rule__ElkPort__Group_2__4__Impl ;
    public final void rule__ElkPort__Group_2__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1556:1: ( rule__ElkPort__Group_2__4__Impl )
            // InternalElkGraph.g:1557:2: rule__ElkPort__Group_2__4__Impl
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
    // InternalElkGraph.g:1563:1: rule__ElkPort__Group_2__4__Impl : ( '}' ) ;
    public final void rule__ElkPort__Group_2__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1567:1: ( ( '}' ) )
            // InternalElkGraph.g:1568:1: ( '}' )
            {
            // InternalElkGraph.g:1568:1: ( '}' )
            // InternalElkGraph.g:1569:2: '}'
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
    // InternalElkGraph.g:1579:1: rule__ShapeLayout__Group__0 : rule__ShapeLayout__Group__0__Impl rule__ShapeLayout__Group__1 ;
    public final void rule__ShapeLayout__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1583:1: ( rule__ShapeLayout__Group__0__Impl rule__ShapeLayout__Group__1 )
            // InternalElkGraph.g:1584:2: rule__ShapeLayout__Group__0__Impl rule__ShapeLayout__Group__1
            {
            pushFollow(FOLLOW_14);
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
    // InternalElkGraph.g:1591:1: rule__ShapeLayout__Group__0__Impl : ( 'layout' ) ;
    public final void rule__ShapeLayout__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1595:1: ( ( 'layout' ) )
            // InternalElkGraph.g:1596:1: ( 'layout' )
            {
            // InternalElkGraph.g:1596:1: ( 'layout' )
            // InternalElkGraph.g:1597:2: 'layout'
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
    // InternalElkGraph.g:1606:1: rule__ShapeLayout__Group__1 : rule__ShapeLayout__Group__1__Impl rule__ShapeLayout__Group__2 ;
    public final void rule__ShapeLayout__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1610:1: ( rule__ShapeLayout__Group__1__Impl rule__ShapeLayout__Group__2 )
            // InternalElkGraph.g:1611:2: rule__ShapeLayout__Group__1__Impl rule__ShapeLayout__Group__2
            {
            pushFollow(FOLLOW_15);
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
    // InternalElkGraph.g:1618:1: rule__ShapeLayout__Group__1__Impl : ( '[' ) ;
    public final void rule__ShapeLayout__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1622:1: ( ( '[' ) )
            // InternalElkGraph.g:1623:1: ( '[' )
            {
            // InternalElkGraph.g:1623:1: ( '[' )
            // InternalElkGraph.g:1624:2: '['
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
    // InternalElkGraph.g:1633:1: rule__ShapeLayout__Group__2 : rule__ShapeLayout__Group__2__Impl rule__ShapeLayout__Group__3 ;
    public final void rule__ShapeLayout__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1637:1: ( rule__ShapeLayout__Group__2__Impl rule__ShapeLayout__Group__3 )
            // InternalElkGraph.g:1638:2: rule__ShapeLayout__Group__2__Impl rule__ShapeLayout__Group__3
            {
            pushFollow(FOLLOW_16);
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
    // InternalElkGraph.g:1645:1: rule__ShapeLayout__Group__2__Impl : ( ( rule__ShapeLayout__UnorderedGroup_2 ) ) ;
    public final void rule__ShapeLayout__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1649:1: ( ( ( rule__ShapeLayout__UnorderedGroup_2 ) ) )
            // InternalElkGraph.g:1650:1: ( ( rule__ShapeLayout__UnorderedGroup_2 ) )
            {
            // InternalElkGraph.g:1650:1: ( ( rule__ShapeLayout__UnorderedGroup_2 ) )
            // InternalElkGraph.g:1651:2: ( rule__ShapeLayout__UnorderedGroup_2 )
            {
             before(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2()); 
            // InternalElkGraph.g:1652:2: ( rule__ShapeLayout__UnorderedGroup_2 )
            // InternalElkGraph.g:1652:3: rule__ShapeLayout__UnorderedGroup_2
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
    // InternalElkGraph.g:1660:1: rule__ShapeLayout__Group__3 : rule__ShapeLayout__Group__3__Impl ;
    public final void rule__ShapeLayout__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1664:1: ( rule__ShapeLayout__Group__3__Impl )
            // InternalElkGraph.g:1665:2: rule__ShapeLayout__Group__3__Impl
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
    // InternalElkGraph.g:1671:1: rule__ShapeLayout__Group__3__Impl : ( ']' ) ;
    public final void rule__ShapeLayout__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1675:1: ( ( ']' ) )
            // InternalElkGraph.g:1676:1: ( ']' )
            {
            // InternalElkGraph.g:1676:1: ( ']' )
            // InternalElkGraph.g:1677:2: ']'
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
    // InternalElkGraph.g:1687:1: rule__ShapeLayout__Group_2_0__0 : rule__ShapeLayout__Group_2_0__0__Impl rule__ShapeLayout__Group_2_0__1 ;
    public final void rule__ShapeLayout__Group_2_0__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1691:1: ( rule__ShapeLayout__Group_2_0__0__Impl rule__ShapeLayout__Group_2_0__1 )
            // InternalElkGraph.g:1692:2: rule__ShapeLayout__Group_2_0__0__Impl rule__ShapeLayout__Group_2_0__1
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
    // InternalElkGraph.g:1699:1: rule__ShapeLayout__Group_2_0__0__Impl : ( 'position' ) ;
    public final void rule__ShapeLayout__Group_2_0__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1703:1: ( ( 'position' ) )
            // InternalElkGraph.g:1704:1: ( 'position' )
            {
            // InternalElkGraph.g:1704:1: ( 'position' )
            // InternalElkGraph.g:1705:2: 'position'
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
    // InternalElkGraph.g:1714:1: rule__ShapeLayout__Group_2_0__1 : rule__ShapeLayout__Group_2_0__1__Impl rule__ShapeLayout__Group_2_0__2 ;
    public final void rule__ShapeLayout__Group_2_0__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1718:1: ( rule__ShapeLayout__Group_2_0__1__Impl rule__ShapeLayout__Group_2_0__2 )
            // InternalElkGraph.g:1719:2: rule__ShapeLayout__Group_2_0__1__Impl rule__ShapeLayout__Group_2_0__2
            {
            pushFollow(FOLLOW_17);
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
    // InternalElkGraph.g:1726:1: rule__ShapeLayout__Group_2_0__1__Impl : ( ':' ) ;
    public final void rule__ShapeLayout__Group_2_0__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1730:1: ( ( ':' ) )
            // InternalElkGraph.g:1731:1: ( ':' )
            {
            // InternalElkGraph.g:1731:1: ( ':' )
            // InternalElkGraph.g:1732:2: ':'
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
    // InternalElkGraph.g:1741:1: rule__ShapeLayout__Group_2_0__2 : rule__ShapeLayout__Group_2_0__2__Impl rule__ShapeLayout__Group_2_0__3 ;
    public final void rule__ShapeLayout__Group_2_0__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1745:1: ( rule__ShapeLayout__Group_2_0__2__Impl rule__ShapeLayout__Group_2_0__3 )
            // InternalElkGraph.g:1746:2: rule__ShapeLayout__Group_2_0__2__Impl rule__ShapeLayout__Group_2_0__3
            {
            pushFollow(FOLLOW_18);
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
    // InternalElkGraph.g:1753:1: rule__ShapeLayout__Group_2_0__2__Impl : ( ( rule__ShapeLayout__XAssignment_2_0_2 ) ) ;
    public final void rule__ShapeLayout__Group_2_0__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1757:1: ( ( ( rule__ShapeLayout__XAssignment_2_0_2 ) ) )
            // InternalElkGraph.g:1758:1: ( ( rule__ShapeLayout__XAssignment_2_0_2 ) )
            {
            // InternalElkGraph.g:1758:1: ( ( rule__ShapeLayout__XAssignment_2_0_2 ) )
            // InternalElkGraph.g:1759:2: ( rule__ShapeLayout__XAssignment_2_0_2 )
            {
             before(grammarAccess.getShapeLayoutAccess().getXAssignment_2_0_2()); 
            // InternalElkGraph.g:1760:2: ( rule__ShapeLayout__XAssignment_2_0_2 )
            // InternalElkGraph.g:1760:3: rule__ShapeLayout__XAssignment_2_0_2
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
    // InternalElkGraph.g:1768:1: rule__ShapeLayout__Group_2_0__3 : rule__ShapeLayout__Group_2_0__3__Impl rule__ShapeLayout__Group_2_0__4 ;
    public final void rule__ShapeLayout__Group_2_0__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1772:1: ( rule__ShapeLayout__Group_2_0__3__Impl rule__ShapeLayout__Group_2_0__4 )
            // InternalElkGraph.g:1773:2: rule__ShapeLayout__Group_2_0__3__Impl rule__ShapeLayout__Group_2_0__4
            {
            pushFollow(FOLLOW_17);
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
    // InternalElkGraph.g:1780:1: rule__ShapeLayout__Group_2_0__3__Impl : ( ',' ) ;
    public final void rule__ShapeLayout__Group_2_0__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1784:1: ( ( ',' ) )
            // InternalElkGraph.g:1785:1: ( ',' )
            {
            // InternalElkGraph.g:1785:1: ( ',' )
            // InternalElkGraph.g:1786:2: ','
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
    // InternalElkGraph.g:1795:1: rule__ShapeLayout__Group_2_0__4 : rule__ShapeLayout__Group_2_0__4__Impl ;
    public final void rule__ShapeLayout__Group_2_0__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1799:1: ( rule__ShapeLayout__Group_2_0__4__Impl )
            // InternalElkGraph.g:1800:2: rule__ShapeLayout__Group_2_0__4__Impl
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
    // InternalElkGraph.g:1806:1: rule__ShapeLayout__Group_2_0__4__Impl : ( ( rule__ShapeLayout__YAssignment_2_0_4 ) ) ;
    public final void rule__ShapeLayout__Group_2_0__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1810:1: ( ( ( rule__ShapeLayout__YAssignment_2_0_4 ) ) )
            // InternalElkGraph.g:1811:1: ( ( rule__ShapeLayout__YAssignment_2_0_4 ) )
            {
            // InternalElkGraph.g:1811:1: ( ( rule__ShapeLayout__YAssignment_2_0_4 ) )
            // InternalElkGraph.g:1812:2: ( rule__ShapeLayout__YAssignment_2_0_4 )
            {
             before(grammarAccess.getShapeLayoutAccess().getYAssignment_2_0_4()); 
            // InternalElkGraph.g:1813:2: ( rule__ShapeLayout__YAssignment_2_0_4 )
            // InternalElkGraph.g:1813:3: rule__ShapeLayout__YAssignment_2_0_4
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
    // InternalElkGraph.g:1822:1: rule__ShapeLayout__Group_2_1__0 : rule__ShapeLayout__Group_2_1__0__Impl rule__ShapeLayout__Group_2_1__1 ;
    public final void rule__ShapeLayout__Group_2_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1826:1: ( rule__ShapeLayout__Group_2_1__0__Impl rule__ShapeLayout__Group_2_1__1 )
            // InternalElkGraph.g:1827:2: rule__ShapeLayout__Group_2_1__0__Impl rule__ShapeLayout__Group_2_1__1
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
    // InternalElkGraph.g:1834:1: rule__ShapeLayout__Group_2_1__0__Impl : ( 'size' ) ;
    public final void rule__ShapeLayout__Group_2_1__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1838:1: ( ( 'size' ) )
            // InternalElkGraph.g:1839:1: ( 'size' )
            {
            // InternalElkGraph.g:1839:1: ( 'size' )
            // InternalElkGraph.g:1840:2: 'size'
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
    // InternalElkGraph.g:1849:1: rule__ShapeLayout__Group_2_1__1 : rule__ShapeLayout__Group_2_1__1__Impl rule__ShapeLayout__Group_2_1__2 ;
    public final void rule__ShapeLayout__Group_2_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1853:1: ( rule__ShapeLayout__Group_2_1__1__Impl rule__ShapeLayout__Group_2_1__2 )
            // InternalElkGraph.g:1854:2: rule__ShapeLayout__Group_2_1__1__Impl rule__ShapeLayout__Group_2_1__2
            {
            pushFollow(FOLLOW_17);
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
    // InternalElkGraph.g:1861:1: rule__ShapeLayout__Group_2_1__1__Impl : ( ':' ) ;
    public final void rule__ShapeLayout__Group_2_1__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1865:1: ( ( ':' ) )
            // InternalElkGraph.g:1866:1: ( ':' )
            {
            // InternalElkGraph.g:1866:1: ( ':' )
            // InternalElkGraph.g:1867:2: ':'
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
    // InternalElkGraph.g:1876:1: rule__ShapeLayout__Group_2_1__2 : rule__ShapeLayout__Group_2_1__2__Impl rule__ShapeLayout__Group_2_1__3 ;
    public final void rule__ShapeLayout__Group_2_1__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1880:1: ( rule__ShapeLayout__Group_2_1__2__Impl rule__ShapeLayout__Group_2_1__3 )
            // InternalElkGraph.g:1881:2: rule__ShapeLayout__Group_2_1__2__Impl rule__ShapeLayout__Group_2_1__3
            {
            pushFollow(FOLLOW_18);
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
    // InternalElkGraph.g:1888:1: rule__ShapeLayout__Group_2_1__2__Impl : ( ( rule__ShapeLayout__WidthAssignment_2_1_2 ) ) ;
    public final void rule__ShapeLayout__Group_2_1__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1892:1: ( ( ( rule__ShapeLayout__WidthAssignment_2_1_2 ) ) )
            // InternalElkGraph.g:1893:1: ( ( rule__ShapeLayout__WidthAssignment_2_1_2 ) )
            {
            // InternalElkGraph.g:1893:1: ( ( rule__ShapeLayout__WidthAssignment_2_1_2 ) )
            // InternalElkGraph.g:1894:2: ( rule__ShapeLayout__WidthAssignment_2_1_2 )
            {
             before(grammarAccess.getShapeLayoutAccess().getWidthAssignment_2_1_2()); 
            // InternalElkGraph.g:1895:2: ( rule__ShapeLayout__WidthAssignment_2_1_2 )
            // InternalElkGraph.g:1895:3: rule__ShapeLayout__WidthAssignment_2_1_2
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
    // InternalElkGraph.g:1903:1: rule__ShapeLayout__Group_2_1__3 : rule__ShapeLayout__Group_2_1__3__Impl rule__ShapeLayout__Group_2_1__4 ;
    public final void rule__ShapeLayout__Group_2_1__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1907:1: ( rule__ShapeLayout__Group_2_1__3__Impl rule__ShapeLayout__Group_2_1__4 )
            // InternalElkGraph.g:1908:2: rule__ShapeLayout__Group_2_1__3__Impl rule__ShapeLayout__Group_2_1__4
            {
            pushFollow(FOLLOW_17);
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
    // InternalElkGraph.g:1915:1: rule__ShapeLayout__Group_2_1__3__Impl : ( ',' ) ;
    public final void rule__ShapeLayout__Group_2_1__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1919:1: ( ( ',' ) )
            // InternalElkGraph.g:1920:1: ( ',' )
            {
            // InternalElkGraph.g:1920:1: ( ',' )
            // InternalElkGraph.g:1921:2: ','
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
    // InternalElkGraph.g:1930:1: rule__ShapeLayout__Group_2_1__4 : rule__ShapeLayout__Group_2_1__4__Impl ;
    public final void rule__ShapeLayout__Group_2_1__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1934:1: ( rule__ShapeLayout__Group_2_1__4__Impl )
            // InternalElkGraph.g:1935:2: rule__ShapeLayout__Group_2_1__4__Impl
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
    // InternalElkGraph.g:1941:1: rule__ShapeLayout__Group_2_1__4__Impl : ( ( rule__ShapeLayout__HeightAssignment_2_1_4 ) ) ;
    public final void rule__ShapeLayout__Group_2_1__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1945:1: ( ( ( rule__ShapeLayout__HeightAssignment_2_1_4 ) ) )
            // InternalElkGraph.g:1946:1: ( ( rule__ShapeLayout__HeightAssignment_2_1_4 ) )
            {
            // InternalElkGraph.g:1946:1: ( ( rule__ShapeLayout__HeightAssignment_2_1_4 ) )
            // InternalElkGraph.g:1947:2: ( rule__ShapeLayout__HeightAssignment_2_1_4 )
            {
             before(grammarAccess.getShapeLayoutAccess().getHeightAssignment_2_1_4()); 
            // InternalElkGraph.g:1948:2: ( rule__ShapeLayout__HeightAssignment_2_1_4 )
            // InternalElkGraph.g:1948:3: rule__ShapeLayout__HeightAssignment_2_1_4
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
    // InternalElkGraph.g:1957:1: rule__ElkEdge__Group__0 : rule__ElkEdge__Group__0__Impl rule__ElkEdge__Group__1 ;
    public final void rule__ElkEdge__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1961:1: ( rule__ElkEdge__Group__0__Impl rule__ElkEdge__Group__1 )
            // InternalElkGraph.g:1962:2: rule__ElkEdge__Group__0__Impl rule__ElkEdge__Group__1
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
    // InternalElkGraph.g:1969:1: rule__ElkEdge__Group__0__Impl : ( 'edge' ) ;
    public final void rule__ElkEdge__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1973:1: ( ( 'edge' ) )
            // InternalElkGraph.g:1974:1: ( 'edge' )
            {
            // InternalElkGraph.g:1974:1: ( 'edge' )
            // InternalElkGraph.g:1975:2: 'edge'
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
    // InternalElkGraph.g:1984:1: rule__ElkEdge__Group__1 : rule__ElkEdge__Group__1__Impl rule__ElkEdge__Group__2 ;
    public final void rule__ElkEdge__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:1988:1: ( rule__ElkEdge__Group__1__Impl rule__ElkEdge__Group__2 )
            // InternalElkGraph.g:1989:2: rule__ElkEdge__Group__1__Impl rule__ElkEdge__Group__2
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
    // InternalElkGraph.g:1996:1: rule__ElkEdge__Group__1__Impl : ( ( rule__ElkEdge__Group_1__0 )? ) ;
    public final void rule__ElkEdge__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2000:1: ( ( ( rule__ElkEdge__Group_1__0 )? ) )
            // InternalElkGraph.g:2001:1: ( ( rule__ElkEdge__Group_1__0 )? )
            {
            // InternalElkGraph.g:2001:1: ( ( rule__ElkEdge__Group_1__0 )? )
            // InternalElkGraph.g:2002:2: ( rule__ElkEdge__Group_1__0 )?
            {
             before(grammarAccess.getElkEdgeAccess().getGroup_1()); 
            // InternalElkGraph.g:2003:2: ( rule__ElkEdge__Group_1__0 )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==RULE_ID) ) {
                int LA25_1 = input.LA(2);

                if ( (LA25_1==20) ) {
                    alt25=1;
                }
            }
            switch (alt25) {
                case 1 :
                    // InternalElkGraph.g:2003:3: rule__ElkEdge__Group_1__0
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
    // InternalElkGraph.g:2011:1: rule__ElkEdge__Group__2 : rule__ElkEdge__Group__2__Impl rule__ElkEdge__Group__3 ;
    public final void rule__ElkEdge__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2015:1: ( rule__ElkEdge__Group__2__Impl rule__ElkEdge__Group__3 )
            // InternalElkGraph.g:2016:2: rule__ElkEdge__Group__2__Impl rule__ElkEdge__Group__3
            {
            pushFollow(FOLLOW_19);
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
    // InternalElkGraph.g:2023:1: rule__ElkEdge__Group__2__Impl : ( ( rule__ElkEdge__SourcesAssignment_2 ) ) ;
    public final void rule__ElkEdge__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2027:1: ( ( ( rule__ElkEdge__SourcesAssignment_2 ) ) )
            // InternalElkGraph.g:2028:1: ( ( rule__ElkEdge__SourcesAssignment_2 ) )
            {
            // InternalElkGraph.g:2028:1: ( ( rule__ElkEdge__SourcesAssignment_2 ) )
            // InternalElkGraph.g:2029:2: ( rule__ElkEdge__SourcesAssignment_2 )
            {
             before(grammarAccess.getElkEdgeAccess().getSourcesAssignment_2()); 
            // InternalElkGraph.g:2030:2: ( rule__ElkEdge__SourcesAssignment_2 )
            // InternalElkGraph.g:2030:3: rule__ElkEdge__SourcesAssignment_2
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
    // InternalElkGraph.g:2038:1: rule__ElkEdge__Group__3 : rule__ElkEdge__Group__3__Impl rule__ElkEdge__Group__4 ;
    public final void rule__ElkEdge__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2042:1: ( rule__ElkEdge__Group__3__Impl rule__ElkEdge__Group__4 )
            // InternalElkGraph.g:2043:2: rule__ElkEdge__Group__3__Impl rule__ElkEdge__Group__4
            {
            pushFollow(FOLLOW_19);
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
    // InternalElkGraph.g:2050:1: rule__ElkEdge__Group__3__Impl : ( ( rule__ElkEdge__Group_3__0 )* ) ;
    public final void rule__ElkEdge__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2054:1: ( ( ( rule__ElkEdge__Group_3__0 )* ) )
            // InternalElkGraph.g:2055:1: ( ( rule__ElkEdge__Group_3__0 )* )
            {
            // InternalElkGraph.g:2055:1: ( ( rule__ElkEdge__Group_3__0 )* )
            // InternalElkGraph.g:2056:2: ( rule__ElkEdge__Group_3__0 )*
            {
             before(grammarAccess.getElkEdgeAccess().getGroup_3()); 
            // InternalElkGraph.g:2057:2: ( rule__ElkEdge__Group_3__0 )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( (LA26_0==26) ) {
                    alt26=1;
                }


                switch (alt26) {
            	case 1 :
            	    // InternalElkGraph.g:2057:3: rule__ElkEdge__Group_3__0
            	    {
            	    pushFollow(FOLLOW_20);
            	    rule__ElkEdge__Group_3__0();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop26;
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
    // InternalElkGraph.g:2065:1: rule__ElkEdge__Group__4 : rule__ElkEdge__Group__4__Impl rule__ElkEdge__Group__5 ;
    public final void rule__ElkEdge__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2069:1: ( rule__ElkEdge__Group__4__Impl rule__ElkEdge__Group__5 )
            // InternalElkGraph.g:2070:2: rule__ElkEdge__Group__4__Impl rule__ElkEdge__Group__5
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
    // InternalElkGraph.g:2077:1: rule__ElkEdge__Group__4__Impl : ( '->' ) ;
    public final void rule__ElkEdge__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2081:1: ( ( '->' ) )
            // InternalElkGraph.g:2082:1: ( '->' )
            {
            // InternalElkGraph.g:2082:1: ( '->' )
            // InternalElkGraph.g:2083:2: '->'
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
    // InternalElkGraph.g:2092:1: rule__ElkEdge__Group__5 : rule__ElkEdge__Group__5__Impl rule__ElkEdge__Group__6 ;
    public final void rule__ElkEdge__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2096:1: ( rule__ElkEdge__Group__5__Impl rule__ElkEdge__Group__6 )
            // InternalElkGraph.g:2097:2: rule__ElkEdge__Group__5__Impl rule__ElkEdge__Group__6
            {
            pushFollow(FOLLOW_21);
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
    // InternalElkGraph.g:2104:1: rule__ElkEdge__Group__5__Impl : ( ( rule__ElkEdge__TargetsAssignment_5 ) ) ;
    public final void rule__ElkEdge__Group__5__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2108:1: ( ( ( rule__ElkEdge__TargetsAssignment_5 ) ) )
            // InternalElkGraph.g:2109:1: ( ( rule__ElkEdge__TargetsAssignment_5 ) )
            {
            // InternalElkGraph.g:2109:1: ( ( rule__ElkEdge__TargetsAssignment_5 ) )
            // InternalElkGraph.g:2110:2: ( rule__ElkEdge__TargetsAssignment_5 )
            {
             before(grammarAccess.getElkEdgeAccess().getTargetsAssignment_5()); 
            // InternalElkGraph.g:2111:2: ( rule__ElkEdge__TargetsAssignment_5 )
            // InternalElkGraph.g:2111:3: rule__ElkEdge__TargetsAssignment_5
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
    // InternalElkGraph.g:2119:1: rule__ElkEdge__Group__6 : rule__ElkEdge__Group__6__Impl rule__ElkEdge__Group__7 ;
    public final void rule__ElkEdge__Group__6() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2123:1: ( rule__ElkEdge__Group__6__Impl rule__ElkEdge__Group__7 )
            // InternalElkGraph.g:2124:2: rule__ElkEdge__Group__6__Impl rule__ElkEdge__Group__7
            {
            pushFollow(FOLLOW_21);
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
    // InternalElkGraph.g:2131:1: rule__ElkEdge__Group__6__Impl : ( ( rule__ElkEdge__Group_6__0 )* ) ;
    public final void rule__ElkEdge__Group__6__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2135:1: ( ( ( rule__ElkEdge__Group_6__0 )* ) )
            // InternalElkGraph.g:2136:1: ( ( rule__ElkEdge__Group_6__0 )* )
            {
            // InternalElkGraph.g:2136:1: ( ( rule__ElkEdge__Group_6__0 )* )
            // InternalElkGraph.g:2137:2: ( rule__ElkEdge__Group_6__0 )*
            {
             before(grammarAccess.getElkEdgeAccess().getGroup_6()); 
            // InternalElkGraph.g:2138:2: ( rule__ElkEdge__Group_6__0 )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( (LA27_0==26) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // InternalElkGraph.g:2138:3: rule__ElkEdge__Group_6__0
            	    {
            	    pushFollow(FOLLOW_20);
            	    rule__ElkEdge__Group_6__0();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop27;
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
    // InternalElkGraph.g:2146:1: rule__ElkEdge__Group__7 : rule__ElkEdge__Group__7__Impl ;
    public final void rule__ElkEdge__Group__7() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2150:1: ( rule__ElkEdge__Group__7__Impl )
            // InternalElkGraph.g:2151:2: rule__ElkEdge__Group__7__Impl
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
    // InternalElkGraph.g:2157:1: rule__ElkEdge__Group__7__Impl : ( ( rule__ElkEdge__Group_7__0 )? ) ;
    public final void rule__ElkEdge__Group__7__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2161:1: ( ( ( rule__ElkEdge__Group_7__0 )? ) )
            // InternalElkGraph.g:2162:1: ( ( rule__ElkEdge__Group_7__0 )? )
            {
            // InternalElkGraph.g:2162:1: ( ( rule__ElkEdge__Group_7__0 )? )
            // InternalElkGraph.g:2163:2: ( rule__ElkEdge__Group_7__0 )?
            {
             before(grammarAccess.getElkEdgeAccess().getGroup_7()); 
            // InternalElkGraph.g:2164:2: ( rule__ElkEdge__Group_7__0 )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==17) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // InternalElkGraph.g:2164:3: rule__ElkEdge__Group_7__0
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
    // InternalElkGraph.g:2173:1: rule__ElkEdge__Group_1__0 : rule__ElkEdge__Group_1__0__Impl rule__ElkEdge__Group_1__1 ;
    public final void rule__ElkEdge__Group_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2177:1: ( rule__ElkEdge__Group_1__0__Impl rule__ElkEdge__Group_1__1 )
            // InternalElkGraph.g:2178:2: rule__ElkEdge__Group_1__0__Impl rule__ElkEdge__Group_1__1
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
    // InternalElkGraph.g:2185:1: rule__ElkEdge__Group_1__0__Impl : ( ( rule__ElkEdge__IdentifierAssignment_1_0 ) ) ;
    public final void rule__ElkEdge__Group_1__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2189:1: ( ( ( rule__ElkEdge__IdentifierAssignment_1_0 ) ) )
            // InternalElkGraph.g:2190:1: ( ( rule__ElkEdge__IdentifierAssignment_1_0 ) )
            {
            // InternalElkGraph.g:2190:1: ( ( rule__ElkEdge__IdentifierAssignment_1_0 ) )
            // InternalElkGraph.g:2191:2: ( rule__ElkEdge__IdentifierAssignment_1_0 )
            {
             before(grammarAccess.getElkEdgeAccess().getIdentifierAssignment_1_0()); 
            // InternalElkGraph.g:2192:2: ( rule__ElkEdge__IdentifierAssignment_1_0 )
            // InternalElkGraph.g:2192:3: rule__ElkEdge__IdentifierAssignment_1_0
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
    // InternalElkGraph.g:2200:1: rule__ElkEdge__Group_1__1 : rule__ElkEdge__Group_1__1__Impl ;
    public final void rule__ElkEdge__Group_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2204:1: ( rule__ElkEdge__Group_1__1__Impl )
            // InternalElkGraph.g:2205:2: rule__ElkEdge__Group_1__1__Impl
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
    // InternalElkGraph.g:2211:1: rule__ElkEdge__Group_1__1__Impl : ( ':' ) ;
    public final void rule__ElkEdge__Group_1__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2215:1: ( ( ':' ) )
            // InternalElkGraph.g:2216:1: ( ':' )
            {
            // InternalElkGraph.g:2216:1: ( ':' )
            // InternalElkGraph.g:2217:2: ':'
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
    // InternalElkGraph.g:2227:1: rule__ElkEdge__Group_3__0 : rule__ElkEdge__Group_3__0__Impl rule__ElkEdge__Group_3__1 ;
    public final void rule__ElkEdge__Group_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2231:1: ( rule__ElkEdge__Group_3__0__Impl rule__ElkEdge__Group_3__1 )
            // InternalElkGraph.g:2232:2: rule__ElkEdge__Group_3__0__Impl rule__ElkEdge__Group_3__1
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
    // InternalElkGraph.g:2239:1: rule__ElkEdge__Group_3__0__Impl : ( ',' ) ;
    public final void rule__ElkEdge__Group_3__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2243:1: ( ( ',' ) )
            // InternalElkGraph.g:2244:1: ( ',' )
            {
            // InternalElkGraph.g:2244:1: ( ',' )
            // InternalElkGraph.g:2245:2: ','
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
    // InternalElkGraph.g:2254:1: rule__ElkEdge__Group_3__1 : rule__ElkEdge__Group_3__1__Impl ;
    public final void rule__ElkEdge__Group_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2258:1: ( rule__ElkEdge__Group_3__1__Impl )
            // InternalElkGraph.g:2259:2: rule__ElkEdge__Group_3__1__Impl
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
    // InternalElkGraph.g:2265:1: rule__ElkEdge__Group_3__1__Impl : ( ( rule__ElkEdge__SourcesAssignment_3_1 ) ) ;
    public final void rule__ElkEdge__Group_3__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2269:1: ( ( ( rule__ElkEdge__SourcesAssignment_3_1 ) ) )
            // InternalElkGraph.g:2270:1: ( ( rule__ElkEdge__SourcesAssignment_3_1 ) )
            {
            // InternalElkGraph.g:2270:1: ( ( rule__ElkEdge__SourcesAssignment_3_1 ) )
            // InternalElkGraph.g:2271:2: ( rule__ElkEdge__SourcesAssignment_3_1 )
            {
             before(grammarAccess.getElkEdgeAccess().getSourcesAssignment_3_1()); 
            // InternalElkGraph.g:2272:2: ( rule__ElkEdge__SourcesAssignment_3_1 )
            // InternalElkGraph.g:2272:3: rule__ElkEdge__SourcesAssignment_3_1
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
    // InternalElkGraph.g:2281:1: rule__ElkEdge__Group_6__0 : rule__ElkEdge__Group_6__0__Impl rule__ElkEdge__Group_6__1 ;
    public final void rule__ElkEdge__Group_6__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2285:1: ( rule__ElkEdge__Group_6__0__Impl rule__ElkEdge__Group_6__1 )
            // InternalElkGraph.g:2286:2: rule__ElkEdge__Group_6__0__Impl rule__ElkEdge__Group_6__1
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
    // InternalElkGraph.g:2293:1: rule__ElkEdge__Group_6__0__Impl : ( ',' ) ;
    public final void rule__ElkEdge__Group_6__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2297:1: ( ( ',' ) )
            // InternalElkGraph.g:2298:1: ( ',' )
            {
            // InternalElkGraph.g:2298:1: ( ',' )
            // InternalElkGraph.g:2299:2: ','
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
    // InternalElkGraph.g:2308:1: rule__ElkEdge__Group_6__1 : rule__ElkEdge__Group_6__1__Impl ;
    public final void rule__ElkEdge__Group_6__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2312:1: ( rule__ElkEdge__Group_6__1__Impl )
            // InternalElkGraph.g:2313:2: rule__ElkEdge__Group_6__1__Impl
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
    // InternalElkGraph.g:2319:1: rule__ElkEdge__Group_6__1__Impl : ( ( rule__ElkEdge__TargetsAssignment_6_1 ) ) ;
    public final void rule__ElkEdge__Group_6__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2323:1: ( ( ( rule__ElkEdge__TargetsAssignment_6_1 ) ) )
            // InternalElkGraph.g:2324:1: ( ( rule__ElkEdge__TargetsAssignment_6_1 ) )
            {
            // InternalElkGraph.g:2324:1: ( ( rule__ElkEdge__TargetsAssignment_6_1 ) )
            // InternalElkGraph.g:2325:2: ( rule__ElkEdge__TargetsAssignment_6_1 )
            {
             before(grammarAccess.getElkEdgeAccess().getTargetsAssignment_6_1()); 
            // InternalElkGraph.g:2326:2: ( rule__ElkEdge__TargetsAssignment_6_1 )
            // InternalElkGraph.g:2326:3: rule__ElkEdge__TargetsAssignment_6_1
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
    // InternalElkGraph.g:2335:1: rule__ElkEdge__Group_7__0 : rule__ElkEdge__Group_7__0__Impl rule__ElkEdge__Group_7__1 ;
    public final void rule__ElkEdge__Group_7__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2339:1: ( rule__ElkEdge__Group_7__0__Impl rule__ElkEdge__Group_7__1 )
            // InternalElkGraph.g:2340:2: rule__ElkEdge__Group_7__0__Impl rule__ElkEdge__Group_7__1
            {
            pushFollow(FOLLOW_12);
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
    // InternalElkGraph.g:2347:1: rule__ElkEdge__Group_7__0__Impl : ( '{' ) ;
    public final void rule__ElkEdge__Group_7__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2351:1: ( ( '{' ) )
            // InternalElkGraph.g:2352:1: ( '{' )
            {
            // InternalElkGraph.g:2352:1: ( '{' )
            // InternalElkGraph.g:2353:2: '{'
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
    // InternalElkGraph.g:2362:1: rule__ElkEdge__Group_7__1 : rule__ElkEdge__Group_7__1__Impl rule__ElkEdge__Group_7__2 ;
    public final void rule__ElkEdge__Group_7__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2366:1: ( rule__ElkEdge__Group_7__1__Impl rule__ElkEdge__Group_7__2 )
            // InternalElkGraph.g:2367:2: rule__ElkEdge__Group_7__1__Impl rule__ElkEdge__Group_7__2
            {
            pushFollow(FOLLOW_12);
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
    // InternalElkGraph.g:2374:1: rule__ElkEdge__Group_7__1__Impl : ( ( ruleEdgeLayout )? ) ;
    public final void rule__ElkEdge__Group_7__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2378:1: ( ( ( ruleEdgeLayout )? ) )
            // InternalElkGraph.g:2379:1: ( ( ruleEdgeLayout )? )
            {
            // InternalElkGraph.g:2379:1: ( ( ruleEdgeLayout )? )
            // InternalElkGraph.g:2380:2: ( ruleEdgeLayout )?
            {
             before(grammarAccess.getElkEdgeAccess().getEdgeLayoutParserRuleCall_7_1()); 
            // InternalElkGraph.g:2381:2: ( ruleEdgeLayout )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==22) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // InternalElkGraph.g:2381:3: ruleEdgeLayout
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
    // InternalElkGraph.g:2389:1: rule__ElkEdge__Group_7__2 : rule__ElkEdge__Group_7__2__Impl rule__ElkEdge__Group_7__3 ;
    public final void rule__ElkEdge__Group_7__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2393:1: ( rule__ElkEdge__Group_7__2__Impl rule__ElkEdge__Group_7__3 )
            // InternalElkGraph.g:2394:2: rule__ElkEdge__Group_7__2__Impl rule__ElkEdge__Group_7__3
            {
            pushFollow(FOLLOW_12);
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
    // InternalElkGraph.g:2401:1: rule__ElkEdge__Group_7__2__Impl : ( ( rule__ElkEdge__PropertiesAssignment_7_2 )* ) ;
    public final void rule__ElkEdge__Group_7__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2405:1: ( ( ( rule__ElkEdge__PropertiesAssignment_7_2 )* ) )
            // InternalElkGraph.g:2406:1: ( ( rule__ElkEdge__PropertiesAssignment_7_2 )* )
            {
            // InternalElkGraph.g:2406:1: ( ( rule__ElkEdge__PropertiesAssignment_7_2 )* )
            // InternalElkGraph.g:2407:2: ( rule__ElkEdge__PropertiesAssignment_7_2 )*
            {
             before(grammarAccess.getElkEdgeAccess().getPropertiesAssignment_7_2()); 
            // InternalElkGraph.g:2408:2: ( rule__ElkEdge__PropertiesAssignment_7_2 )*
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( (LA30_0==RULE_ID) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // InternalElkGraph.g:2408:3: rule__ElkEdge__PropertiesAssignment_7_2
            	    {
            	    pushFollow(FOLLOW_5);
            	    rule__ElkEdge__PropertiesAssignment_7_2();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop30;
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
    // InternalElkGraph.g:2416:1: rule__ElkEdge__Group_7__3 : rule__ElkEdge__Group_7__3__Impl rule__ElkEdge__Group_7__4 ;
    public final void rule__ElkEdge__Group_7__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2420:1: ( rule__ElkEdge__Group_7__3__Impl rule__ElkEdge__Group_7__4 )
            // InternalElkGraph.g:2421:2: rule__ElkEdge__Group_7__3__Impl rule__ElkEdge__Group_7__4
            {
            pushFollow(FOLLOW_12);
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
    // InternalElkGraph.g:2428:1: rule__ElkEdge__Group_7__3__Impl : ( ( rule__ElkEdge__LabelsAssignment_7_3 )* ) ;
    public final void rule__ElkEdge__Group_7__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2432:1: ( ( ( rule__ElkEdge__LabelsAssignment_7_3 )* ) )
            // InternalElkGraph.g:2433:1: ( ( rule__ElkEdge__LabelsAssignment_7_3 )* )
            {
            // InternalElkGraph.g:2433:1: ( ( rule__ElkEdge__LabelsAssignment_7_3 )* )
            // InternalElkGraph.g:2434:2: ( rule__ElkEdge__LabelsAssignment_7_3 )*
            {
             before(grammarAccess.getElkEdgeAccess().getLabelsAssignment_7_3()); 
            // InternalElkGraph.g:2435:2: ( rule__ElkEdge__LabelsAssignment_7_3 )*
            loop31:
            do {
                int alt31=2;
                int LA31_0 = input.LA(1);

                if ( (LA31_0==19) ) {
                    alt31=1;
                }


                switch (alt31) {
            	case 1 :
            	    // InternalElkGraph.g:2435:3: rule__ElkEdge__LabelsAssignment_7_3
            	    {
            	    pushFollow(FOLLOW_13);
            	    rule__ElkEdge__LabelsAssignment_7_3();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop31;
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
    // InternalElkGraph.g:2443:1: rule__ElkEdge__Group_7__4 : rule__ElkEdge__Group_7__4__Impl ;
    public final void rule__ElkEdge__Group_7__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2447:1: ( rule__ElkEdge__Group_7__4__Impl )
            // InternalElkGraph.g:2448:2: rule__ElkEdge__Group_7__4__Impl
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
    // InternalElkGraph.g:2454:1: rule__ElkEdge__Group_7__4__Impl : ( '}' ) ;
    public final void rule__ElkEdge__Group_7__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2458:1: ( ( '}' ) )
            // InternalElkGraph.g:2459:1: ( '}' )
            {
            // InternalElkGraph.g:2459:1: ( '}' )
            // InternalElkGraph.g:2460:2: '}'
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
    // InternalElkGraph.g:2470:1: rule__EdgeLayout__Group__0 : rule__EdgeLayout__Group__0__Impl rule__EdgeLayout__Group__1 ;
    public final void rule__EdgeLayout__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2474:1: ( rule__EdgeLayout__Group__0__Impl rule__EdgeLayout__Group__1 )
            // InternalElkGraph.g:2475:2: rule__EdgeLayout__Group__0__Impl rule__EdgeLayout__Group__1
            {
            pushFollow(FOLLOW_14);
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
    // InternalElkGraph.g:2482:1: rule__EdgeLayout__Group__0__Impl : ( 'layout' ) ;
    public final void rule__EdgeLayout__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2486:1: ( ( 'layout' ) )
            // InternalElkGraph.g:2487:1: ( 'layout' )
            {
            // InternalElkGraph.g:2487:1: ( 'layout' )
            // InternalElkGraph.g:2488:2: 'layout'
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
    // InternalElkGraph.g:2497:1: rule__EdgeLayout__Group__1 : rule__EdgeLayout__Group__1__Impl rule__EdgeLayout__Group__2 ;
    public final void rule__EdgeLayout__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2501:1: ( rule__EdgeLayout__Group__1__Impl rule__EdgeLayout__Group__2 )
            // InternalElkGraph.g:2502:2: rule__EdgeLayout__Group__1__Impl rule__EdgeLayout__Group__2
            {
            pushFollow(FOLLOW_22);
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
    // InternalElkGraph.g:2509:1: rule__EdgeLayout__Group__1__Impl : ( '[' ) ;
    public final void rule__EdgeLayout__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2513:1: ( ( '[' ) )
            // InternalElkGraph.g:2514:1: ( '[' )
            {
            // InternalElkGraph.g:2514:1: ( '[' )
            // InternalElkGraph.g:2515:2: '['
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
    // InternalElkGraph.g:2524:1: rule__EdgeLayout__Group__2 : rule__EdgeLayout__Group__2__Impl rule__EdgeLayout__Group__3 ;
    public final void rule__EdgeLayout__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2528:1: ( rule__EdgeLayout__Group__2__Impl rule__EdgeLayout__Group__3 )
            // InternalElkGraph.g:2529:2: rule__EdgeLayout__Group__2__Impl rule__EdgeLayout__Group__3
            {
            pushFollow(FOLLOW_16);
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
    // InternalElkGraph.g:2536:1: rule__EdgeLayout__Group__2__Impl : ( ( rule__EdgeLayout__Alternatives_2 ) ) ;
    public final void rule__EdgeLayout__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2540:1: ( ( ( rule__EdgeLayout__Alternatives_2 ) ) )
            // InternalElkGraph.g:2541:1: ( ( rule__EdgeLayout__Alternatives_2 ) )
            {
            // InternalElkGraph.g:2541:1: ( ( rule__EdgeLayout__Alternatives_2 ) )
            // InternalElkGraph.g:2542:2: ( rule__EdgeLayout__Alternatives_2 )
            {
             before(grammarAccess.getEdgeLayoutAccess().getAlternatives_2()); 
            // InternalElkGraph.g:2543:2: ( rule__EdgeLayout__Alternatives_2 )
            // InternalElkGraph.g:2543:3: rule__EdgeLayout__Alternatives_2
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
    // InternalElkGraph.g:2551:1: rule__EdgeLayout__Group__3 : rule__EdgeLayout__Group__3__Impl ;
    public final void rule__EdgeLayout__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2555:1: ( rule__EdgeLayout__Group__3__Impl )
            // InternalElkGraph.g:2556:2: rule__EdgeLayout__Group__3__Impl
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
    // InternalElkGraph.g:2562:1: rule__EdgeLayout__Group__3__Impl : ( ']' ) ;
    public final void rule__EdgeLayout__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2566:1: ( ( ']' ) )
            // InternalElkGraph.g:2567:1: ( ']' )
            {
            // InternalElkGraph.g:2567:1: ( ']' )
            // InternalElkGraph.g:2568:2: ']'
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
    // InternalElkGraph.g:2578:1: rule__ElkSingleEdgeSection__Group__0 : rule__ElkSingleEdgeSection__Group__0__Impl rule__ElkSingleEdgeSection__Group__1 ;
    public final void rule__ElkSingleEdgeSection__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2582:1: ( rule__ElkSingleEdgeSection__Group__0__Impl rule__ElkSingleEdgeSection__Group__1 )
            // InternalElkGraph.g:2583:2: rule__ElkSingleEdgeSection__Group__0__Impl rule__ElkSingleEdgeSection__Group__1
            {
            pushFollow(FOLLOW_23);
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
    // InternalElkGraph.g:2590:1: rule__ElkSingleEdgeSection__Group__0__Impl : ( () ) ;
    public final void rule__ElkSingleEdgeSection__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2594:1: ( ( () ) )
            // InternalElkGraph.g:2595:1: ( () )
            {
            // InternalElkGraph.g:2595:1: ( () )
            // InternalElkGraph.g:2596:2: ()
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getElkEdgeSectionAction_0()); 
            // InternalElkGraph.g:2597:2: ()
            // InternalElkGraph.g:2597:3: 
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
    // InternalElkGraph.g:2605:1: rule__ElkSingleEdgeSection__Group__1 : rule__ElkSingleEdgeSection__Group__1__Impl ;
    public final void rule__ElkSingleEdgeSection__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2609:1: ( rule__ElkSingleEdgeSection__Group__1__Impl )
            // InternalElkGraph.g:2610:2: rule__ElkSingleEdgeSection__Group__1__Impl
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
    // InternalElkGraph.g:2616:1: rule__ElkSingleEdgeSection__Group__1__Impl : ( ( rule__ElkSingleEdgeSection__Group_1__0 ) ) ;
    public final void rule__ElkSingleEdgeSection__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2620:1: ( ( ( rule__ElkSingleEdgeSection__Group_1__0 ) ) )
            // InternalElkGraph.g:2621:1: ( ( rule__ElkSingleEdgeSection__Group_1__0 ) )
            {
            // InternalElkGraph.g:2621:1: ( ( rule__ElkSingleEdgeSection__Group_1__0 ) )
            // InternalElkGraph.g:2622:2: ( rule__ElkSingleEdgeSection__Group_1__0 )
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getGroup_1()); 
            // InternalElkGraph.g:2623:2: ( rule__ElkSingleEdgeSection__Group_1__0 )
            // InternalElkGraph.g:2623:3: rule__ElkSingleEdgeSection__Group_1__0
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1__0();

            state._fsp--;


            }

             after(grammarAccess.getElkSingleEdgeSectionAccess().getGroup_1()); 

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


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1__0"
    // InternalElkGraph.g:2632:1: rule__ElkSingleEdgeSection__Group_1__0 : rule__ElkSingleEdgeSection__Group_1__0__Impl rule__ElkSingleEdgeSection__Group_1__1 ;
    public final void rule__ElkSingleEdgeSection__Group_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2636:1: ( rule__ElkSingleEdgeSection__Group_1__0__Impl rule__ElkSingleEdgeSection__Group_1__1 )
            // InternalElkGraph.g:2637:2: rule__ElkSingleEdgeSection__Group_1__0__Impl rule__ElkSingleEdgeSection__Group_1__1
            {
            pushFollow(FOLLOW_24);
            rule__ElkSingleEdgeSection__Group_1__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1__1();

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1__0"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1__0__Impl"
    // InternalElkGraph.g:2644:1: rule__ElkSingleEdgeSection__Group_1__0__Impl : ( ( rule__ElkSingleEdgeSection__UnorderedGroup_1_0 ) ) ;
    public final void rule__ElkSingleEdgeSection__Group_1__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2648:1: ( ( ( rule__ElkSingleEdgeSection__UnorderedGroup_1_0 ) ) )
            // InternalElkGraph.g:2649:1: ( ( rule__ElkSingleEdgeSection__UnorderedGroup_1_0 ) )
            {
            // InternalElkGraph.g:2649:1: ( ( rule__ElkSingleEdgeSection__UnorderedGroup_1_0 ) )
            // InternalElkGraph.g:2650:2: ( rule__ElkSingleEdgeSection__UnorderedGroup_1_0 )
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0()); 
            // InternalElkGraph.g:2651:2: ( rule__ElkSingleEdgeSection__UnorderedGroup_1_0 )
            // InternalElkGraph.g:2651:3: rule__ElkSingleEdgeSection__UnorderedGroup_1_0
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__UnorderedGroup_1_0();

            state._fsp--;


            }

             after(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0()); 

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1__0__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1__1"
    // InternalElkGraph.g:2659:1: rule__ElkSingleEdgeSection__Group_1__1 : rule__ElkSingleEdgeSection__Group_1__1__Impl rule__ElkSingleEdgeSection__Group_1__2 ;
    public final void rule__ElkSingleEdgeSection__Group_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2663:1: ( rule__ElkSingleEdgeSection__Group_1__1__Impl rule__ElkSingleEdgeSection__Group_1__2 )
            // InternalElkGraph.g:2664:2: rule__ElkSingleEdgeSection__Group_1__1__Impl rule__ElkSingleEdgeSection__Group_1__2
            {
            pushFollow(FOLLOW_24);
            rule__ElkSingleEdgeSection__Group_1__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1__2();

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1__1"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1__1__Impl"
    // InternalElkGraph.g:2671:1: rule__ElkSingleEdgeSection__Group_1__1__Impl : ( ( rule__ElkSingleEdgeSection__Group_1_1__0 )? ) ;
    public final void rule__ElkSingleEdgeSection__Group_1__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2675:1: ( ( ( rule__ElkSingleEdgeSection__Group_1_1__0 )? ) )
            // InternalElkGraph.g:2676:1: ( ( rule__ElkSingleEdgeSection__Group_1_1__0 )? )
            {
            // InternalElkGraph.g:2676:1: ( ( rule__ElkSingleEdgeSection__Group_1_1__0 )? )
            // InternalElkGraph.g:2677:2: ( rule__ElkSingleEdgeSection__Group_1_1__0 )?
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getGroup_1_1()); 
            // InternalElkGraph.g:2678:2: ( rule__ElkSingleEdgeSection__Group_1_1__0 )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==34) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // InternalElkGraph.g:2678:3: rule__ElkSingleEdgeSection__Group_1_1__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkSingleEdgeSection__Group_1_1__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getElkSingleEdgeSectionAccess().getGroup_1_1()); 

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1__1__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1__2"
    // InternalElkGraph.g:2686:1: rule__ElkSingleEdgeSection__Group_1__2 : rule__ElkSingleEdgeSection__Group_1__2__Impl ;
    public final void rule__ElkSingleEdgeSection__Group_1__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2690:1: ( rule__ElkSingleEdgeSection__Group_1__2__Impl )
            // InternalElkGraph.g:2691:2: rule__ElkSingleEdgeSection__Group_1__2__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1__2__Impl();

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1__2"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1__2__Impl"
    // InternalElkGraph.g:2697:1: rule__ElkSingleEdgeSection__Group_1__2__Impl : ( ( rule__ElkSingleEdgeSection__PropertiesAssignment_1_2 )* ) ;
    public final void rule__ElkSingleEdgeSection__Group_1__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2701:1: ( ( ( rule__ElkSingleEdgeSection__PropertiesAssignment_1_2 )* ) )
            // InternalElkGraph.g:2702:1: ( ( rule__ElkSingleEdgeSection__PropertiesAssignment_1_2 )* )
            {
            // InternalElkGraph.g:2702:1: ( ( rule__ElkSingleEdgeSection__PropertiesAssignment_1_2 )* )
            // InternalElkGraph.g:2703:2: ( rule__ElkSingleEdgeSection__PropertiesAssignment_1_2 )*
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getPropertiesAssignment_1_2()); 
            // InternalElkGraph.g:2704:2: ( rule__ElkSingleEdgeSection__PropertiesAssignment_1_2 )*
            loop33:
            do {
                int alt33=2;
                int LA33_0 = input.LA(1);

                if ( (LA33_0==RULE_ID) ) {
                    alt33=1;
                }


                switch (alt33) {
            	case 1 :
            	    // InternalElkGraph.g:2704:3: rule__ElkSingleEdgeSection__PropertiesAssignment_1_2
            	    {
            	    pushFollow(FOLLOW_5);
            	    rule__ElkSingleEdgeSection__PropertiesAssignment_1_2();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop33;
                }
            } while (true);

             after(grammarAccess.getElkSingleEdgeSectionAccess().getPropertiesAssignment_1_2()); 

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1__2__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0_0__0"
    // InternalElkGraph.g:2713:1: rule__ElkSingleEdgeSection__Group_1_0_0__0 : rule__ElkSingleEdgeSection__Group_1_0_0__0__Impl rule__ElkSingleEdgeSection__Group_1_0_0__1 ;
    public final void rule__ElkSingleEdgeSection__Group_1_0_0__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2717:1: ( rule__ElkSingleEdgeSection__Group_1_0_0__0__Impl rule__ElkSingleEdgeSection__Group_1_0_0__1 )
            // InternalElkGraph.g:2718:2: rule__ElkSingleEdgeSection__Group_1_0_0__0__Impl rule__ElkSingleEdgeSection__Group_1_0_0__1
            {
            pushFollow(FOLLOW_11);
            rule__ElkSingleEdgeSection__Group_1_0_0__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_0_0__1();

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0_0__0"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0_0__0__Impl"
    // InternalElkGraph.g:2725:1: rule__ElkSingleEdgeSection__Group_1_0_0__0__Impl : ( 'incoming' ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_0_0__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2729:1: ( ( 'incoming' ) )
            // InternalElkGraph.g:2730:1: ( 'incoming' )
            {
            // InternalElkGraph.g:2730:1: ( 'incoming' )
            // InternalElkGraph.g:2731:2: 'incoming'
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getIncomingKeyword_1_0_0_0()); 
            match(input,30,FOLLOW_2); 
             after(grammarAccess.getElkSingleEdgeSectionAccess().getIncomingKeyword_1_0_0_0()); 

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0_0__0__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0_0__1"
    // InternalElkGraph.g:2740:1: rule__ElkSingleEdgeSection__Group_1_0_0__1 : rule__ElkSingleEdgeSection__Group_1_0_0__1__Impl rule__ElkSingleEdgeSection__Group_1_0_0__2 ;
    public final void rule__ElkSingleEdgeSection__Group_1_0_0__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2744:1: ( rule__ElkSingleEdgeSection__Group_1_0_0__1__Impl rule__ElkSingleEdgeSection__Group_1_0_0__2 )
            // InternalElkGraph.g:2745:2: rule__ElkSingleEdgeSection__Group_1_0_0__1__Impl rule__ElkSingleEdgeSection__Group_1_0_0__2
            {
            pushFollow(FOLLOW_7);
            rule__ElkSingleEdgeSection__Group_1_0_0__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_0_0__2();

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0_0__1"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0_0__1__Impl"
    // InternalElkGraph.g:2752:1: rule__ElkSingleEdgeSection__Group_1_0_0__1__Impl : ( ':' ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_0_0__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2756:1: ( ( ':' ) )
            // InternalElkGraph.g:2757:1: ( ':' )
            {
            // InternalElkGraph.g:2757:1: ( ':' )
            // InternalElkGraph.g:2758:2: ':'
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getColonKeyword_1_0_0_1()); 
            match(input,20,FOLLOW_2); 
             after(grammarAccess.getElkSingleEdgeSectionAccess().getColonKeyword_1_0_0_1()); 

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0_0__1__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0_0__2"
    // InternalElkGraph.g:2767:1: rule__ElkSingleEdgeSection__Group_1_0_0__2 : rule__ElkSingleEdgeSection__Group_1_0_0__2__Impl ;
    public final void rule__ElkSingleEdgeSection__Group_1_0_0__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2771:1: ( rule__ElkSingleEdgeSection__Group_1_0_0__2__Impl )
            // InternalElkGraph.g:2772:2: rule__ElkSingleEdgeSection__Group_1_0_0__2__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_0_0__2__Impl();

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0_0__2"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0_0__2__Impl"
    // InternalElkGraph.g:2778:1: rule__ElkSingleEdgeSection__Group_1_0_0__2__Impl : ( ( rule__ElkSingleEdgeSection__IncomingShapeAssignment_1_0_0_2 ) ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_0_0__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2782:1: ( ( ( rule__ElkSingleEdgeSection__IncomingShapeAssignment_1_0_0_2 ) ) )
            // InternalElkGraph.g:2783:1: ( ( rule__ElkSingleEdgeSection__IncomingShapeAssignment_1_0_0_2 ) )
            {
            // InternalElkGraph.g:2783:1: ( ( rule__ElkSingleEdgeSection__IncomingShapeAssignment_1_0_0_2 ) )
            // InternalElkGraph.g:2784:2: ( rule__ElkSingleEdgeSection__IncomingShapeAssignment_1_0_0_2 )
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getIncomingShapeAssignment_1_0_0_2()); 
            // InternalElkGraph.g:2785:2: ( rule__ElkSingleEdgeSection__IncomingShapeAssignment_1_0_0_2 )
            // InternalElkGraph.g:2785:3: rule__ElkSingleEdgeSection__IncomingShapeAssignment_1_0_0_2
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__IncomingShapeAssignment_1_0_0_2();

            state._fsp--;


            }

             after(grammarAccess.getElkSingleEdgeSectionAccess().getIncomingShapeAssignment_1_0_0_2()); 

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0_0__2__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0_1__0"
    // InternalElkGraph.g:2794:1: rule__ElkSingleEdgeSection__Group_1_0_1__0 : rule__ElkSingleEdgeSection__Group_1_0_1__0__Impl rule__ElkSingleEdgeSection__Group_1_0_1__1 ;
    public final void rule__ElkSingleEdgeSection__Group_1_0_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2798:1: ( rule__ElkSingleEdgeSection__Group_1_0_1__0__Impl rule__ElkSingleEdgeSection__Group_1_0_1__1 )
            // InternalElkGraph.g:2799:2: rule__ElkSingleEdgeSection__Group_1_0_1__0__Impl rule__ElkSingleEdgeSection__Group_1_0_1__1
            {
            pushFollow(FOLLOW_11);
            rule__ElkSingleEdgeSection__Group_1_0_1__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_0_1__1();

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0_1__0"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0_1__0__Impl"
    // InternalElkGraph.g:2806:1: rule__ElkSingleEdgeSection__Group_1_0_1__0__Impl : ( 'outgoing' ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_0_1__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2810:1: ( ( 'outgoing' ) )
            // InternalElkGraph.g:2811:1: ( 'outgoing' )
            {
            // InternalElkGraph.g:2811:1: ( 'outgoing' )
            // InternalElkGraph.g:2812:2: 'outgoing'
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getOutgoingKeyword_1_0_1_0()); 
            match(input,31,FOLLOW_2); 
             after(grammarAccess.getElkSingleEdgeSectionAccess().getOutgoingKeyword_1_0_1_0()); 

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0_1__0__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0_1__1"
    // InternalElkGraph.g:2821:1: rule__ElkSingleEdgeSection__Group_1_0_1__1 : rule__ElkSingleEdgeSection__Group_1_0_1__1__Impl rule__ElkSingleEdgeSection__Group_1_0_1__2 ;
    public final void rule__ElkSingleEdgeSection__Group_1_0_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2825:1: ( rule__ElkSingleEdgeSection__Group_1_0_1__1__Impl rule__ElkSingleEdgeSection__Group_1_0_1__2 )
            // InternalElkGraph.g:2826:2: rule__ElkSingleEdgeSection__Group_1_0_1__1__Impl rule__ElkSingleEdgeSection__Group_1_0_1__2
            {
            pushFollow(FOLLOW_7);
            rule__ElkSingleEdgeSection__Group_1_0_1__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_0_1__2();

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0_1__1"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0_1__1__Impl"
    // InternalElkGraph.g:2833:1: rule__ElkSingleEdgeSection__Group_1_0_1__1__Impl : ( ':' ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_0_1__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2837:1: ( ( ':' ) )
            // InternalElkGraph.g:2838:1: ( ':' )
            {
            // InternalElkGraph.g:2838:1: ( ':' )
            // InternalElkGraph.g:2839:2: ':'
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getColonKeyword_1_0_1_1()); 
            match(input,20,FOLLOW_2); 
             after(grammarAccess.getElkSingleEdgeSectionAccess().getColonKeyword_1_0_1_1()); 

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0_1__1__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0_1__2"
    // InternalElkGraph.g:2848:1: rule__ElkSingleEdgeSection__Group_1_0_1__2 : rule__ElkSingleEdgeSection__Group_1_0_1__2__Impl ;
    public final void rule__ElkSingleEdgeSection__Group_1_0_1__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2852:1: ( rule__ElkSingleEdgeSection__Group_1_0_1__2__Impl )
            // InternalElkGraph.g:2853:2: rule__ElkSingleEdgeSection__Group_1_0_1__2__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_0_1__2__Impl();

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0_1__2"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0_1__2__Impl"
    // InternalElkGraph.g:2859:1: rule__ElkSingleEdgeSection__Group_1_0_1__2__Impl : ( ( rule__ElkSingleEdgeSection__OutgoingShapeAssignment_1_0_1_2 ) ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_0_1__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2863:1: ( ( ( rule__ElkSingleEdgeSection__OutgoingShapeAssignment_1_0_1_2 ) ) )
            // InternalElkGraph.g:2864:1: ( ( rule__ElkSingleEdgeSection__OutgoingShapeAssignment_1_0_1_2 ) )
            {
            // InternalElkGraph.g:2864:1: ( ( rule__ElkSingleEdgeSection__OutgoingShapeAssignment_1_0_1_2 ) )
            // InternalElkGraph.g:2865:2: ( rule__ElkSingleEdgeSection__OutgoingShapeAssignment_1_0_1_2 )
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getOutgoingShapeAssignment_1_0_1_2()); 
            // InternalElkGraph.g:2866:2: ( rule__ElkSingleEdgeSection__OutgoingShapeAssignment_1_0_1_2 )
            // InternalElkGraph.g:2866:3: rule__ElkSingleEdgeSection__OutgoingShapeAssignment_1_0_1_2
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__OutgoingShapeAssignment_1_0_1_2();

            state._fsp--;


            }

             after(grammarAccess.getElkSingleEdgeSectionAccess().getOutgoingShapeAssignment_1_0_1_2()); 

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0_1__2__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0_2__0"
    // InternalElkGraph.g:2875:1: rule__ElkSingleEdgeSection__Group_1_0_2__0 : rule__ElkSingleEdgeSection__Group_1_0_2__0__Impl rule__ElkSingleEdgeSection__Group_1_0_2__1 ;
    public final void rule__ElkSingleEdgeSection__Group_1_0_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2879:1: ( rule__ElkSingleEdgeSection__Group_1_0_2__0__Impl rule__ElkSingleEdgeSection__Group_1_0_2__1 )
            // InternalElkGraph.g:2880:2: rule__ElkSingleEdgeSection__Group_1_0_2__0__Impl rule__ElkSingleEdgeSection__Group_1_0_2__1
            {
            pushFollow(FOLLOW_11);
            rule__ElkSingleEdgeSection__Group_1_0_2__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_0_2__1();

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0_2__0"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0_2__0__Impl"
    // InternalElkGraph.g:2887:1: rule__ElkSingleEdgeSection__Group_1_0_2__0__Impl : ( 'start' ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_0_2__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2891:1: ( ( 'start' ) )
            // InternalElkGraph.g:2892:1: ( 'start' )
            {
            // InternalElkGraph.g:2892:1: ( 'start' )
            // InternalElkGraph.g:2893:2: 'start'
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getStartKeyword_1_0_2_0()); 
            match(input,32,FOLLOW_2); 
             after(grammarAccess.getElkSingleEdgeSectionAccess().getStartKeyword_1_0_2_0()); 

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0_2__0__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0_2__1"
    // InternalElkGraph.g:2902:1: rule__ElkSingleEdgeSection__Group_1_0_2__1 : rule__ElkSingleEdgeSection__Group_1_0_2__1__Impl rule__ElkSingleEdgeSection__Group_1_0_2__2 ;
    public final void rule__ElkSingleEdgeSection__Group_1_0_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2906:1: ( rule__ElkSingleEdgeSection__Group_1_0_2__1__Impl rule__ElkSingleEdgeSection__Group_1_0_2__2 )
            // InternalElkGraph.g:2907:2: rule__ElkSingleEdgeSection__Group_1_0_2__1__Impl rule__ElkSingleEdgeSection__Group_1_0_2__2
            {
            pushFollow(FOLLOW_17);
            rule__ElkSingleEdgeSection__Group_1_0_2__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_0_2__2();

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0_2__1"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0_2__1__Impl"
    // InternalElkGraph.g:2914:1: rule__ElkSingleEdgeSection__Group_1_0_2__1__Impl : ( ':' ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_0_2__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2918:1: ( ( ':' ) )
            // InternalElkGraph.g:2919:1: ( ':' )
            {
            // InternalElkGraph.g:2919:1: ( ':' )
            // InternalElkGraph.g:2920:2: ':'
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getColonKeyword_1_0_2_1()); 
            match(input,20,FOLLOW_2); 
             after(grammarAccess.getElkSingleEdgeSectionAccess().getColonKeyword_1_0_2_1()); 

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0_2__1__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0_2__2"
    // InternalElkGraph.g:2929:1: rule__ElkSingleEdgeSection__Group_1_0_2__2 : rule__ElkSingleEdgeSection__Group_1_0_2__2__Impl rule__ElkSingleEdgeSection__Group_1_0_2__3 ;
    public final void rule__ElkSingleEdgeSection__Group_1_0_2__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2933:1: ( rule__ElkSingleEdgeSection__Group_1_0_2__2__Impl rule__ElkSingleEdgeSection__Group_1_0_2__3 )
            // InternalElkGraph.g:2934:2: rule__ElkSingleEdgeSection__Group_1_0_2__2__Impl rule__ElkSingleEdgeSection__Group_1_0_2__3
            {
            pushFollow(FOLLOW_18);
            rule__ElkSingleEdgeSection__Group_1_0_2__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_0_2__3();

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0_2__2"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0_2__2__Impl"
    // InternalElkGraph.g:2941:1: rule__ElkSingleEdgeSection__Group_1_0_2__2__Impl : ( ( rule__ElkSingleEdgeSection__StartXAssignment_1_0_2_2 ) ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_0_2__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2945:1: ( ( ( rule__ElkSingleEdgeSection__StartXAssignment_1_0_2_2 ) ) )
            // InternalElkGraph.g:2946:1: ( ( rule__ElkSingleEdgeSection__StartXAssignment_1_0_2_2 ) )
            {
            // InternalElkGraph.g:2946:1: ( ( rule__ElkSingleEdgeSection__StartXAssignment_1_0_2_2 ) )
            // InternalElkGraph.g:2947:2: ( rule__ElkSingleEdgeSection__StartXAssignment_1_0_2_2 )
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getStartXAssignment_1_0_2_2()); 
            // InternalElkGraph.g:2948:2: ( rule__ElkSingleEdgeSection__StartXAssignment_1_0_2_2 )
            // InternalElkGraph.g:2948:3: rule__ElkSingleEdgeSection__StartXAssignment_1_0_2_2
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__StartXAssignment_1_0_2_2();

            state._fsp--;


            }

             after(grammarAccess.getElkSingleEdgeSectionAccess().getStartXAssignment_1_0_2_2()); 

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0_2__2__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0_2__3"
    // InternalElkGraph.g:2956:1: rule__ElkSingleEdgeSection__Group_1_0_2__3 : rule__ElkSingleEdgeSection__Group_1_0_2__3__Impl rule__ElkSingleEdgeSection__Group_1_0_2__4 ;
    public final void rule__ElkSingleEdgeSection__Group_1_0_2__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2960:1: ( rule__ElkSingleEdgeSection__Group_1_0_2__3__Impl rule__ElkSingleEdgeSection__Group_1_0_2__4 )
            // InternalElkGraph.g:2961:2: rule__ElkSingleEdgeSection__Group_1_0_2__3__Impl rule__ElkSingleEdgeSection__Group_1_0_2__4
            {
            pushFollow(FOLLOW_17);
            rule__ElkSingleEdgeSection__Group_1_0_2__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_0_2__4();

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0_2__3"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0_2__3__Impl"
    // InternalElkGraph.g:2968:1: rule__ElkSingleEdgeSection__Group_1_0_2__3__Impl : ( ',' ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_0_2__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2972:1: ( ( ',' ) )
            // InternalElkGraph.g:2973:1: ( ',' )
            {
            // InternalElkGraph.g:2973:1: ( ',' )
            // InternalElkGraph.g:2974:2: ','
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getCommaKeyword_1_0_2_3()); 
            match(input,26,FOLLOW_2); 
             after(grammarAccess.getElkSingleEdgeSectionAccess().getCommaKeyword_1_0_2_3()); 

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0_2__3__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0_2__4"
    // InternalElkGraph.g:2983:1: rule__ElkSingleEdgeSection__Group_1_0_2__4 : rule__ElkSingleEdgeSection__Group_1_0_2__4__Impl ;
    public final void rule__ElkSingleEdgeSection__Group_1_0_2__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2987:1: ( rule__ElkSingleEdgeSection__Group_1_0_2__4__Impl )
            // InternalElkGraph.g:2988:2: rule__ElkSingleEdgeSection__Group_1_0_2__4__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_0_2__4__Impl();

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0_2__4"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0_2__4__Impl"
    // InternalElkGraph.g:2994:1: rule__ElkSingleEdgeSection__Group_1_0_2__4__Impl : ( ( rule__ElkSingleEdgeSection__StartYAssignment_1_0_2_4 ) ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_0_2__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:2998:1: ( ( ( rule__ElkSingleEdgeSection__StartYAssignment_1_0_2_4 ) ) )
            // InternalElkGraph.g:2999:1: ( ( rule__ElkSingleEdgeSection__StartYAssignment_1_0_2_4 ) )
            {
            // InternalElkGraph.g:2999:1: ( ( rule__ElkSingleEdgeSection__StartYAssignment_1_0_2_4 ) )
            // InternalElkGraph.g:3000:2: ( rule__ElkSingleEdgeSection__StartYAssignment_1_0_2_4 )
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getStartYAssignment_1_0_2_4()); 
            // InternalElkGraph.g:3001:2: ( rule__ElkSingleEdgeSection__StartYAssignment_1_0_2_4 )
            // InternalElkGraph.g:3001:3: rule__ElkSingleEdgeSection__StartYAssignment_1_0_2_4
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__StartYAssignment_1_0_2_4();

            state._fsp--;


            }

             after(grammarAccess.getElkSingleEdgeSectionAccess().getStartYAssignment_1_0_2_4()); 

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0_2__4__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0_3__0"
    // InternalElkGraph.g:3010:1: rule__ElkSingleEdgeSection__Group_1_0_3__0 : rule__ElkSingleEdgeSection__Group_1_0_3__0__Impl rule__ElkSingleEdgeSection__Group_1_0_3__1 ;
    public final void rule__ElkSingleEdgeSection__Group_1_0_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3014:1: ( rule__ElkSingleEdgeSection__Group_1_0_3__0__Impl rule__ElkSingleEdgeSection__Group_1_0_3__1 )
            // InternalElkGraph.g:3015:2: rule__ElkSingleEdgeSection__Group_1_0_3__0__Impl rule__ElkSingleEdgeSection__Group_1_0_3__1
            {
            pushFollow(FOLLOW_11);
            rule__ElkSingleEdgeSection__Group_1_0_3__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_0_3__1();

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0_3__0"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0_3__0__Impl"
    // InternalElkGraph.g:3022:1: rule__ElkSingleEdgeSection__Group_1_0_3__0__Impl : ( 'end' ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_0_3__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3026:1: ( ( 'end' ) )
            // InternalElkGraph.g:3027:1: ( 'end' )
            {
            // InternalElkGraph.g:3027:1: ( 'end' )
            // InternalElkGraph.g:3028:2: 'end'
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getEndKeyword_1_0_3_0()); 
            match(input,33,FOLLOW_2); 
             after(grammarAccess.getElkSingleEdgeSectionAccess().getEndKeyword_1_0_3_0()); 

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0_3__0__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0_3__1"
    // InternalElkGraph.g:3037:1: rule__ElkSingleEdgeSection__Group_1_0_3__1 : rule__ElkSingleEdgeSection__Group_1_0_3__1__Impl rule__ElkSingleEdgeSection__Group_1_0_3__2 ;
    public final void rule__ElkSingleEdgeSection__Group_1_0_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3041:1: ( rule__ElkSingleEdgeSection__Group_1_0_3__1__Impl rule__ElkSingleEdgeSection__Group_1_0_3__2 )
            // InternalElkGraph.g:3042:2: rule__ElkSingleEdgeSection__Group_1_0_3__1__Impl rule__ElkSingleEdgeSection__Group_1_0_3__2
            {
            pushFollow(FOLLOW_17);
            rule__ElkSingleEdgeSection__Group_1_0_3__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_0_3__2();

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0_3__1"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0_3__1__Impl"
    // InternalElkGraph.g:3049:1: rule__ElkSingleEdgeSection__Group_1_0_3__1__Impl : ( ':' ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_0_3__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3053:1: ( ( ':' ) )
            // InternalElkGraph.g:3054:1: ( ':' )
            {
            // InternalElkGraph.g:3054:1: ( ':' )
            // InternalElkGraph.g:3055:2: ':'
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getColonKeyword_1_0_3_1()); 
            match(input,20,FOLLOW_2); 
             after(grammarAccess.getElkSingleEdgeSectionAccess().getColonKeyword_1_0_3_1()); 

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0_3__1__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0_3__2"
    // InternalElkGraph.g:3064:1: rule__ElkSingleEdgeSection__Group_1_0_3__2 : rule__ElkSingleEdgeSection__Group_1_0_3__2__Impl rule__ElkSingleEdgeSection__Group_1_0_3__3 ;
    public final void rule__ElkSingleEdgeSection__Group_1_0_3__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3068:1: ( rule__ElkSingleEdgeSection__Group_1_0_3__2__Impl rule__ElkSingleEdgeSection__Group_1_0_3__3 )
            // InternalElkGraph.g:3069:2: rule__ElkSingleEdgeSection__Group_1_0_3__2__Impl rule__ElkSingleEdgeSection__Group_1_0_3__3
            {
            pushFollow(FOLLOW_18);
            rule__ElkSingleEdgeSection__Group_1_0_3__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_0_3__3();

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0_3__2"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0_3__2__Impl"
    // InternalElkGraph.g:3076:1: rule__ElkSingleEdgeSection__Group_1_0_3__2__Impl : ( ( rule__ElkSingleEdgeSection__EndXAssignment_1_0_3_2 ) ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_0_3__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3080:1: ( ( ( rule__ElkSingleEdgeSection__EndXAssignment_1_0_3_2 ) ) )
            // InternalElkGraph.g:3081:1: ( ( rule__ElkSingleEdgeSection__EndXAssignment_1_0_3_2 ) )
            {
            // InternalElkGraph.g:3081:1: ( ( rule__ElkSingleEdgeSection__EndXAssignment_1_0_3_2 ) )
            // InternalElkGraph.g:3082:2: ( rule__ElkSingleEdgeSection__EndXAssignment_1_0_3_2 )
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getEndXAssignment_1_0_3_2()); 
            // InternalElkGraph.g:3083:2: ( rule__ElkSingleEdgeSection__EndXAssignment_1_0_3_2 )
            // InternalElkGraph.g:3083:3: rule__ElkSingleEdgeSection__EndXAssignment_1_0_3_2
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__EndXAssignment_1_0_3_2();

            state._fsp--;


            }

             after(grammarAccess.getElkSingleEdgeSectionAccess().getEndXAssignment_1_0_3_2()); 

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0_3__2__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0_3__3"
    // InternalElkGraph.g:3091:1: rule__ElkSingleEdgeSection__Group_1_0_3__3 : rule__ElkSingleEdgeSection__Group_1_0_3__3__Impl rule__ElkSingleEdgeSection__Group_1_0_3__4 ;
    public final void rule__ElkSingleEdgeSection__Group_1_0_3__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3095:1: ( rule__ElkSingleEdgeSection__Group_1_0_3__3__Impl rule__ElkSingleEdgeSection__Group_1_0_3__4 )
            // InternalElkGraph.g:3096:2: rule__ElkSingleEdgeSection__Group_1_0_3__3__Impl rule__ElkSingleEdgeSection__Group_1_0_3__4
            {
            pushFollow(FOLLOW_17);
            rule__ElkSingleEdgeSection__Group_1_0_3__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_0_3__4();

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0_3__3"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0_3__3__Impl"
    // InternalElkGraph.g:3103:1: rule__ElkSingleEdgeSection__Group_1_0_3__3__Impl : ( ',' ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_0_3__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3107:1: ( ( ',' ) )
            // InternalElkGraph.g:3108:1: ( ',' )
            {
            // InternalElkGraph.g:3108:1: ( ',' )
            // InternalElkGraph.g:3109:2: ','
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getCommaKeyword_1_0_3_3()); 
            match(input,26,FOLLOW_2); 
             after(grammarAccess.getElkSingleEdgeSectionAccess().getCommaKeyword_1_0_3_3()); 

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0_3__3__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0_3__4"
    // InternalElkGraph.g:3118:1: rule__ElkSingleEdgeSection__Group_1_0_3__4 : rule__ElkSingleEdgeSection__Group_1_0_3__4__Impl ;
    public final void rule__ElkSingleEdgeSection__Group_1_0_3__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3122:1: ( rule__ElkSingleEdgeSection__Group_1_0_3__4__Impl )
            // InternalElkGraph.g:3123:2: rule__ElkSingleEdgeSection__Group_1_0_3__4__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_0_3__4__Impl();

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0_3__4"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_0_3__4__Impl"
    // InternalElkGraph.g:3129:1: rule__ElkSingleEdgeSection__Group_1_0_3__4__Impl : ( ( rule__ElkSingleEdgeSection__EndYAssignment_1_0_3_4 ) ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_0_3__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3133:1: ( ( ( rule__ElkSingleEdgeSection__EndYAssignment_1_0_3_4 ) ) )
            // InternalElkGraph.g:3134:1: ( ( rule__ElkSingleEdgeSection__EndYAssignment_1_0_3_4 ) )
            {
            // InternalElkGraph.g:3134:1: ( ( rule__ElkSingleEdgeSection__EndYAssignment_1_0_3_4 ) )
            // InternalElkGraph.g:3135:2: ( rule__ElkSingleEdgeSection__EndYAssignment_1_0_3_4 )
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getEndYAssignment_1_0_3_4()); 
            // InternalElkGraph.g:3136:2: ( rule__ElkSingleEdgeSection__EndYAssignment_1_0_3_4 )
            // InternalElkGraph.g:3136:3: rule__ElkSingleEdgeSection__EndYAssignment_1_0_3_4
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__EndYAssignment_1_0_3_4();

            state._fsp--;


            }

             after(grammarAccess.getElkSingleEdgeSectionAccess().getEndYAssignment_1_0_3_4()); 

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_0_3__4__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_1__0"
    // InternalElkGraph.g:3145:1: rule__ElkSingleEdgeSection__Group_1_1__0 : rule__ElkSingleEdgeSection__Group_1_1__0__Impl rule__ElkSingleEdgeSection__Group_1_1__1 ;
    public final void rule__ElkSingleEdgeSection__Group_1_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3149:1: ( rule__ElkSingleEdgeSection__Group_1_1__0__Impl rule__ElkSingleEdgeSection__Group_1_1__1 )
            // InternalElkGraph.g:3150:2: rule__ElkSingleEdgeSection__Group_1_1__0__Impl rule__ElkSingleEdgeSection__Group_1_1__1
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
    // InternalElkGraph.g:3157:1: rule__ElkSingleEdgeSection__Group_1_1__0__Impl : ( 'bends' ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_1__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3161:1: ( ( 'bends' ) )
            // InternalElkGraph.g:3162:1: ( 'bends' )
            {
            // InternalElkGraph.g:3162:1: ( 'bends' )
            // InternalElkGraph.g:3163:2: 'bends'
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getBendsKeyword_1_1_0()); 
            match(input,34,FOLLOW_2); 
             after(grammarAccess.getElkSingleEdgeSectionAccess().getBendsKeyword_1_1_0()); 

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
    // InternalElkGraph.g:3172:1: rule__ElkSingleEdgeSection__Group_1_1__1 : rule__ElkSingleEdgeSection__Group_1_1__1__Impl rule__ElkSingleEdgeSection__Group_1_1__2 ;
    public final void rule__ElkSingleEdgeSection__Group_1_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3176:1: ( rule__ElkSingleEdgeSection__Group_1_1__1__Impl rule__ElkSingleEdgeSection__Group_1_1__2 )
            // InternalElkGraph.g:3177:2: rule__ElkSingleEdgeSection__Group_1_1__1__Impl rule__ElkSingleEdgeSection__Group_1_1__2
            {
            pushFollow(FOLLOW_17);
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
    // InternalElkGraph.g:3184:1: rule__ElkSingleEdgeSection__Group_1_1__1__Impl : ( ':' ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_1__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3188:1: ( ( ':' ) )
            // InternalElkGraph.g:3189:1: ( ':' )
            {
            // InternalElkGraph.g:3189:1: ( ':' )
            // InternalElkGraph.g:3190:2: ':'
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
    // InternalElkGraph.g:3199:1: rule__ElkSingleEdgeSection__Group_1_1__2 : rule__ElkSingleEdgeSection__Group_1_1__2__Impl rule__ElkSingleEdgeSection__Group_1_1__3 ;
    public final void rule__ElkSingleEdgeSection__Group_1_1__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3203:1: ( rule__ElkSingleEdgeSection__Group_1_1__2__Impl rule__ElkSingleEdgeSection__Group_1_1__3 )
            // InternalElkGraph.g:3204:2: rule__ElkSingleEdgeSection__Group_1_1__2__Impl rule__ElkSingleEdgeSection__Group_1_1__3
            {
            pushFollow(FOLLOW_25);
            rule__ElkSingleEdgeSection__Group_1_1__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_1__3();

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
    // InternalElkGraph.g:3211:1: rule__ElkSingleEdgeSection__Group_1_1__2__Impl : ( ( rule__ElkSingleEdgeSection__BendPointsAssignment_1_1_2 ) ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_1__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3215:1: ( ( ( rule__ElkSingleEdgeSection__BendPointsAssignment_1_1_2 ) ) )
            // InternalElkGraph.g:3216:1: ( ( rule__ElkSingleEdgeSection__BendPointsAssignment_1_1_2 ) )
            {
            // InternalElkGraph.g:3216:1: ( ( rule__ElkSingleEdgeSection__BendPointsAssignment_1_1_2 ) )
            // InternalElkGraph.g:3217:2: ( rule__ElkSingleEdgeSection__BendPointsAssignment_1_1_2 )
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getBendPointsAssignment_1_1_2()); 
            // InternalElkGraph.g:3218:2: ( rule__ElkSingleEdgeSection__BendPointsAssignment_1_1_2 )
            // InternalElkGraph.g:3218:3: rule__ElkSingleEdgeSection__BendPointsAssignment_1_1_2
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__BendPointsAssignment_1_1_2();

            state._fsp--;


            }

             after(grammarAccess.getElkSingleEdgeSectionAccess().getBendPointsAssignment_1_1_2()); 

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


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_1__3"
    // InternalElkGraph.g:3226:1: rule__ElkSingleEdgeSection__Group_1_1__3 : rule__ElkSingleEdgeSection__Group_1_1__3__Impl ;
    public final void rule__ElkSingleEdgeSection__Group_1_1__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3230:1: ( rule__ElkSingleEdgeSection__Group_1_1__3__Impl )
            // InternalElkGraph.g:3231:2: rule__ElkSingleEdgeSection__Group_1_1__3__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_1__3__Impl();

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_1__3"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_1__3__Impl"
    // InternalElkGraph.g:3237:1: rule__ElkSingleEdgeSection__Group_1_1__3__Impl : ( ( rule__ElkSingleEdgeSection__Group_1_1_3__0 )* ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_1__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3241:1: ( ( ( rule__ElkSingleEdgeSection__Group_1_1_3__0 )* ) )
            // InternalElkGraph.g:3242:1: ( ( rule__ElkSingleEdgeSection__Group_1_1_3__0 )* )
            {
            // InternalElkGraph.g:3242:1: ( ( rule__ElkSingleEdgeSection__Group_1_1_3__0 )* )
            // InternalElkGraph.g:3243:2: ( rule__ElkSingleEdgeSection__Group_1_1_3__0 )*
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getGroup_1_1_3()); 
            // InternalElkGraph.g:3244:2: ( rule__ElkSingleEdgeSection__Group_1_1_3__0 )*
            loop34:
            do {
                int alt34=2;
                int LA34_0 = input.LA(1);

                if ( (LA34_0==35) ) {
                    alt34=1;
                }


                switch (alt34) {
            	case 1 :
            	    // InternalElkGraph.g:3244:3: rule__ElkSingleEdgeSection__Group_1_1_3__0
            	    {
            	    pushFollow(FOLLOW_26);
            	    rule__ElkSingleEdgeSection__Group_1_1_3__0();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop34;
                }
            } while (true);

             after(grammarAccess.getElkSingleEdgeSectionAccess().getGroup_1_1_3()); 

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_1__3__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_1_3__0"
    // InternalElkGraph.g:3253:1: rule__ElkSingleEdgeSection__Group_1_1_3__0 : rule__ElkSingleEdgeSection__Group_1_1_3__0__Impl rule__ElkSingleEdgeSection__Group_1_1_3__1 ;
    public final void rule__ElkSingleEdgeSection__Group_1_1_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3257:1: ( rule__ElkSingleEdgeSection__Group_1_1_3__0__Impl rule__ElkSingleEdgeSection__Group_1_1_3__1 )
            // InternalElkGraph.g:3258:2: rule__ElkSingleEdgeSection__Group_1_1_3__0__Impl rule__ElkSingleEdgeSection__Group_1_1_3__1
            {
            pushFollow(FOLLOW_17);
            rule__ElkSingleEdgeSection__Group_1_1_3__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_1_3__1();

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_1_3__0"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_1_3__0__Impl"
    // InternalElkGraph.g:3265:1: rule__ElkSingleEdgeSection__Group_1_1_3__0__Impl : ( '|' ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_1_3__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3269:1: ( ( '|' ) )
            // InternalElkGraph.g:3270:1: ( '|' )
            {
            // InternalElkGraph.g:3270:1: ( '|' )
            // InternalElkGraph.g:3271:2: '|'
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getVerticalLineKeyword_1_1_3_0()); 
            match(input,35,FOLLOW_2); 
             after(grammarAccess.getElkSingleEdgeSectionAccess().getVerticalLineKeyword_1_1_3_0()); 

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_1_3__0__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_1_3__1"
    // InternalElkGraph.g:3280:1: rule__ElkSingleEdgeSection__Group_1_1_3__1 : rule__ElkSingleEdgeSection__Group_1_1_3__1__Impl ;
    public final void rule__ElkSingleEdgeSection__Group_1_1_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3284:1: ( rule__ElkSingleEdgeSection__Group_1_1_3__1__Impl )
            // InternalElkGraph.g:3285:2: rule__ElkSingleEdgeSection__Group_1_1_3__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__Group_1_1_3__1__Impl();

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_1_3__1"


    // $ANTLR start "rule__ElkSingleEdgeSection__Group_1_1_3__1__Impl"
    // InternalElkGraph.g:3291:1: rule__ElkSingleEdgeSection__Group_1_1_3__1__Impl : ( ( rule__ElkSingleEdgeSection__BendPointsAssignment_1_1_3_1 ) ) ;
    public final void rule__ElkSingleEdgeSection__Group_1_1_3__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3295:1: ( ( ( rule__ElkSingleEdgeSection__BendPointsAssignment_1_1_3_1 ) ) )
            // InternalElkGraph.g:3296:1: ( ( rule__ElkSingleEdgeSection__BendPointsAssignment_1_1_3_1 ) )
            {
            // InternalElkGraph.g:3296:1: ( ( rule__ElkSingleEdgeSection__BendPointsAssignment_1_1_3_1 ) )
            // InternalElkGraph.g:3297:2: ( rule__ElkSingleEdgeSection__BendPointsAssignment_1_1_3_1 )
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getBendPointsAssignment_1_1_3_1()); 
            // InternalElkGraph.g:3298:2: ( rule__ElkSingleEdgeSection__BendPointsAssignment_1_1_3_1 )
            // InternalElkGraph.g:3298:3: rule__ElkSingleEdgeSection__BendPointsAssignment_1_1_3_1
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__BendPointsAssignment_1_1_3_1();

            state._fsp--;


            }

             after(grammarAccess.getElkSingleEdgeSectionAccess().getBendPointsAssignment_1_1_3_1()); 

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
    // $ANTLR end "rule__ElkSingleEdgeSection__Group_1_1_3__1__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group__0"
    // InternalElkGraph.g:3307:1: rule__ElkEdgeSection__Group__0 : rule__ElkEdgeSection__Group__0__Impl rule__ElkEdgeSection__Group__1 ;
    public final void rule__ElkEdgeSection__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3311:1: ( rule__ElkEdgeSection__Group__0__Impl rule__ElkEdgeSection__Group__1 )
            // InternalElkGraph.g:3312:2: rule__ElkEdgeSection__Group__0__Impl rule__ElkEdgeSection__Group__1
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
    // InternalElkGraph.g:3319:1: rule__ElkEdgeSection__Group__0__Impl : ( 'section' ) ;
    public final void rule__ElkEdgeSection__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3323:1: ( ( 'section' ) )
            // InternalElkGraph.g:3324:1: ( 'section' )
            {
            // InternalElkGraph.g:3324:1: ( 'section' )
            // InternalElkGraph.g:3325:2: 'section'
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
    // InternalElkGraph.g:3334:1: rule__ElkEdgeSection__Group__1 : rule__ElkEdgeSection__Group__1__Impl rule__ElkEdgeSection__Group__2 ;
    public final void rule__ElkEdgeSection__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3338:1: ( rule__ElkEdgeSection__Group__1__Impl rule__ElkEdgeSection__Group__2 )
            // InternalElkGraph.g:3339:2: rule__ElkEdgeSection__Group__1__Impl rule__ElkEdgeSection__Group__2
            {
            pushFollow(FOLLOW_27);
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
    // InternalElkGraph.g:3346:1: rule__ElkEdgeSection__Group__1__Impl : ( ( rule__ElkEdgeSection__IdentifierAssignment_1 ) ) ;
    public final void rule__ElkEdgeSection__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3350:1: ( ( ( rule__ElkEdgeSection__IdentifierAssignment_1 ) ) )
            // InternalElkGraph.g:3351:1: ( ( rule__ElkEdgeSection__IdentifierAssignment_1 ) )
            {
            // InternalElkGraph.g:3351:1: ( ( rule__ElkEdgeSection__IdentifierAssignment_1 ) )
            // InternalElkGraph.g:3352:2: ( rule__ElkEdgeSection__IdentifierAssignment_1 )
            {
             before(grammarAccess.getElkEdgeSectionAccess().getIdentifierAssignment_1()); 
            // InternalElkGraph.g:3353:2: ( rule__ElkEdgeSection__IdentifierAssignment_1 )
            // InternalElkGraph.g:3353:3: rule__ElkEdgeSection__IdentifierAssignment_1
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
    // InternalElkGraph.g:3361:1: rule__ElkEdgeSection__Group__2 : rule__ElkEdgeSection__Group__2__Impl rule__ElkEdgeSection__Group__3 ;
    public final void rule__ElkEdgeSection__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3365:1: ( rule__ElkEdgeSection__Group__2__Impl rule__ElkEdgeSection__Group__3 )
            // InternalElkGraph.g:3366:2: rule__ElkEdgeSection__Group__2__Impl rule__ElkEdgeSection__Group__3
            {
            pushFollow(FOLLOW_27);
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
    // InternalElkGraph.g:3373:1: rule__ElkEdgeSection__Group__2__Impl : ( ( rule__ElkEdgeSection__Group_2__0 )? ) ;
    public final void rule__ElkEdgeSection__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3377:1: ( ( ( rule__ElkEdgeSection__Group_2__0 )? ) )
            // InternalElkGraph.g:3378:1: ( ( rule__ElkEdgeSection__Group_2__0 )? )
            {
            // InternalElkGraph.g:3378:1: ( ( rule__ElkEdgeSection__Group_2__0 )? )
            // InternalElkGraph.g:3379:2: ( rule__ElkEdgeSection__Group_2__0 )?
            {
             before(grammarAccess.getElkEdgeSectionAccess().getGroup_2()); 
            // InternalElkGraph.g:3380:2: ( rule__ElkEdgeSection__Group_2__0 )?
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==29) ) {
                alt35=1;
            }
            switch (alt35) {
                case 1 :
                    // InternalElkGraph.g:3380:3: rule__ElkEdgeSection__Group_2__0
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
    // InternalElkGraph.g:3388:1: rule__ElkEdgeSection__Group__3 : rule__ElkEdgeSection__Group__3__Impl rule__ElkEdgeSection__Group__4 ;
    public final void rule__ElkEdgeSection__Group__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3392:1: ( rule__ElkEdgeSection__Group__3__Impl rule__ElkEdgeSection__Group__4 )
            // InternalElkGraph.g:3393:2: rule__ElkEdgeSection__Group__3__Impl rule__ElkEdgeSection__Group__4
            {
            pushFollow(FOLLOW_23);
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
    // InternalElkGraph.g:3400:1: rule__ElkEdgeSection__Group__3__Impl : ( '[' ) ;
    public final void rule__ElkEdgeSection__Group__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3404:1: ( ( '[' ) )
            // InternalElkGraph.g:3405:1: ( '[' )
            {
            // InternalElkGraph.g:3405:1: ( '[' )
            // InternalElkGraph.g:3406:2: '['
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
    // InternalElkGraph.g:3415:1: rule__ElkEdgeSection__Group__4 : rule__ElkEdgeSection__Group__4__Impl rule__ElkEdgeSection__Group__5 ;
    public final void rule__ElkEdgeSection__Group__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3419:1: ( rule__ElkEdgeSection__Group__4__Impl rule__ElkEdgeSection__Group__5 )
            // InternalElkGraph.g:3420:2: rule__ElkEdgeSection__Group__4__Impl rule__ElkEdgeSection__Group__5
            {
            pushFollow(FOLLOW_16);
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
    // InternalElkGraph.g:3427:1: rule__ElkEdgeSection__Group__4__Impl : ( ( rule__ElkEdgeSection__Group_4__0 ) ) ;
    public final void rule__ElkEdgeSection__Group__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3431:1: ( ( ( rule__ElkEdgeSection__Group_4__0 ) ) )
            // InternalElkGraph.g:3432:1: ( ( rule__ElkEdgeSection__Group_4__0 ) )
            {
            // InternalElkGraph.g:3432:1: ( ( rule__ElkEdgeSection__Group_4__0 ) )
            // InternalElkGraph.g:3433:2: ( rule__ElkEdgeSection__Group_4__0 )
            {
             before(grammarAccess.getElkEdgeSectionAccess().getGroup_4()); 
            // InternalElkGraph.g:3434:2: ( rule__ElkEdgeSection__Group_4__0 )
            // InternalElkGraph.g:3434:3: rule__ElkEdgeSection__Group_4__0
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4__0();

            state._fsp--;


            }

             after(grammarAccess.getElkEdgeSectionAccess().getGroup_4()); 

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
    // InternalElkGraph.g:3442:1: rule__ElkEdgeSection__Group__5 : rule__ElkEdgeSection__Group__5__Impl ;
    public final void rule__ElkEdgeSection__Group__5() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3446:1: ( rule__ElkEdgeSection__Group__5__Impl )
            // InternalElkGraph.g:3447:2: rule__ElkEdgeSection__Group__5__Impl
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
    // InternalElkGraph.g:3453:1: rule__ElkEdgeSection__Group__5__Impl : ( ']' ) ;
    public final void rule__ElkEdgeSection__Group__5__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3457:1: ( ( ']' ) )
            // InternalElkGraph.g:3458:1: ( ']' )
            {
            // InternalElkGraph.g:3458:1: ( ']' )
            // InternalElkGraph.g:3459:2: ']'
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
    // InternalElkGraph.g:3469:1: rule__ElkEdgeSection__Group_2__0 : rule__ElkEdgeSection__Group_2__0__Impl rule__ElkEdgeSection__Group_2__1 ;
    public final void rule__ElkEdgeSection__Group_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3473:1: ( rule__ElkEdgeSection__Group_2__0__Impl rule__ElkEdgeSection__Group_2__1 )
            // InternalElkGraph.g:3474:2: rule__ElkEdgeSection__Group_2__0__Impl rule__ElkEdgeSection__Group_2__1
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
    // InternalElkGraph.g:3481:1: rule__ElkEdgeSection__Group_2__0__Impl : ( '->' ) ;
    public final void rule__ElkEdgeSection__Group_2__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3485:1: ( ( '->' ) )
            // InternalElkGraph.g:3486:1: ( '->' )
            {
            // InternalElkGraph.g:3486:1: ( '->' )
            // InternalElkGraph.g:3487:2: '->'
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
    // InternalElkGraph.g:3496:1: rule__ElkEdgeSection__Group_2__1 : rule__ElkEdgeSection__Group_2__1__Impl rule__ElkEdgeSection__Group_2__2 ;
    public final void rule__ElkEdgeSection__Group_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3500:1: ( rule__ElkEdgeSection__Group_2__1__Impl rule__ElkEdgeSection__Group_2__2 )
            // InternalElkGraph.g:3501:2: rule__ElkEdgeSection__Group_2__1__Impl rule__ElkEdgeSection__Group_2__2
            {
            pushFollow(FOLLOW_18);
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
    // InternalElkGraph.g:3508:1: rule__ElkEdgeSection__Group_2__1__Impl : ( ( rule__ElkEdgeSection__OutgoingSectionsAssignment_2_1 ) ) ;
    public final void rule__ElkEdgeSection__Group_2__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3512:1: ( ( ( rule__ElkEdgeSection__OutgoingSectionsAssignment_2_1 ) ) )
            // InternalElkGraph.g:3513:1: ( ( rule__ElkEdgeSection__OutgoingSectionsAssignment_2_1 ) )
            {
            // InternalElkGraph.g:3513:1: ( ( rule__ElkEdgeSection__OutgoingSectionsAssignment_2_1 ) )
            // InternalElkGraph.g:3514:2: ( rule__ElkEdgeSection__OutgoingSectionsAssignment_2_1 )
            {
             before(grammarAccess.getElkEdgeSectionAccess().getOutgoingSectionsAssignment_2_1()); 
            // InternalElkGraph.g:3515:2: ( rule__ElkEdgeSection__OutgoingSectionsAssignment_2_1 )
            // InternalElkGraph.g:3515:3: rule__ElkEdgeSection__OutgoingSectionsAssignment_2_1
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
    // InternalElkGraph.g:3523:1: rule__ElkEdgeSection__Group_2__2 : rule__ElkEdgeSection__Group_2__2__Impl ;
    public final void rule__ElkEdgeSection__Group_2__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3527:1: ( rule__ElkEdgeSection__Group_2__2__Impl )
            // InternalElkGraph.g:3528:2: rule__ElkEdgeSection__Group_2__2__Impl
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
    // InternalElkGraph.g:3534:1: rule__ElkEdgeSection__Group_2__2__Impl : ( ( rule__ElkEdgeSection__Group_2_2__0 )* ) ;
    public final void rule__ElkEdgeSection__Group_2__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3538:1: ( ( ( rule__ElkEdgeSection__Group_2_2__0 )* ) )
            // InternalElkGraph.g:3539:1: ( ( rule__ElkEdgeSection__Group_2_2__0 )* )
            {
            // InternalElkGraph.g:3539:1: ( ( rule__ElkEdgeSection__Group_2_2__0 )* )
            // InternalElkGraph.g:3540:2: ( rule__ElkEdgeSection__Group_2_2__0 )*
            {
             before(grammarAccess.getElkEdgeSectionAccess().getGroup_2_2()); 
            // InternalElkGraph.g:3541:2: ( rule__ElkEdgeSection__Group_2_2__0 )*
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);

                if ( (LA36_0==26) ) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // InternalElkGraph.g:3541:3: rule__ElkEdgeSection__Group_2_2__0
            	    {
            	    pushFollow(FOLLOW_20);
            	    rule__ElkEdgeSection__Group_2_2__0();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop36;
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
    // InternalElkGraph.g:3550:1: rule__ElkEdgeSection__Group_2_2__0 : rule__ElkEdgeSection__Group_2_2__0__Impl rule__ElkEdgeSection__Group_2_2__1 ;
    public final void rule__ElkEdgeSection__Group_2_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3554:1: ( rule__ElkEdgeSection__Group_2_2__0__Impl rule__ElkEdgeSection__Group_2_2__1 )
            // InternalElkGraph.g:3555:2: rule__ElkEdgeSection__Group_2_2__0__Impl rule__ElkEdgeSection__Group_2_2__1
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
    // InternalElkGraph.g:3562:1: rule__ElkEdgeSection__Group_2_2__0__Impl : ( ',' ) ;
    public final void rule__ElkEdgeSection__Group_2_2__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3566:1: ( ( ',' ) )
            // InternalElkGraph.g:3567:1: ( ',' )
            {
            // InternalElkGraph.g:3567:1: ( ',' )
            // InternalElkGraph.g:3568:2: ','
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
    // InternalElkGraph.g:3577:1: rule__ElkEdgeSection__Group_2_2__1 : rule__ElkEdgeSection__Group_2_2__1__Impl ;
    public final void rule__ElkEdgeSection__Group_2_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3581:1: ( rule__ElkEdgeSection__Group_2_2__1__Impl )
            // InternalElkGraph.g:3582:2: rule__ElkEdgeSection__Group_2_2__1__Impl
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
    // InternalElkGraph.g:3588:1: rule__ElkEdgeSection__Group_2_2__1__Impl : ( ( rule__ElkEdgeSection__OutgoingSectionsAssignment_2_2_1 ) ) ;
    public final void rule__ElkEdgeSection__Group_2_2__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3592:1: ( ( ( rule__ElkEdgeSection__OutgoingSectionsAssignment_2_2_1 ) ) )
            // InternalElkGraph.g:3593:1: ( ( rule__ElkEdgeSection__OutgoingSectionsAssignment_2_2_1 ) )
            {
            // InternalElkGraph.g:3593:1: ( ( rule__ElkEdgeSection__OutgoingSectionsAssignment_2_2_1 ) )
            // InternalElkGraph.g:3594:2: ( rule__ElkEdgeSection__OutgoingSectionsAssignment_2_2_1 )
            {
             before(grammarAccess.getElkEdgeSectionAccess().getOutgoingSectionsAssignment_2_2_1()); 
            // InternalElkGraph.g:3595:2: ( rule__ElkEdgeSection__OutgoingSectionsAssignment_2_2_1 )
            // InternalElkGraph.g:3595:3: rule__ElkEdgeSection__OutgoingSectionsAssignment_2_2_1
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


    // $ANTLR start "rule__ElkEdgeSection__Group_4__0"
    // InternalElkGraph.g:3604:1: rule__ElkEdgeSection__Group_4__0 : rule__ElkEdgeSection__Group_4__0__Impl rule__ElkEdgeSection__Group_4__1 ;
    public final void rule__ElkEdgeSection__Group_4__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3608:1: ( rule__ElkEdgeSection__Group_4__0__Impl rule__ElkEdgeSection__Group_4__1 )
            // InternalElkGraph.g:3609:2: rule__ElkEdgeSection__Group_4__0__Impl rule__ElkEdgeSection__Group_4__1
            {
            pushFollow(FOLLOW_24);
            rule__ElkEdgeSection__Group_4__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4__1();

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4__0"


    // $ANTLR start "rule__ElkEdgeSection__Group_4__0__Impl"
    // InternalElkGraph.g:3616:1: rule__ElkEdgeSection__Group_4__0__Impl : ( ( rule__ElkEdgeSection__UnorderedGroup_4_0 ) ) ;
    public final void rule__ElkEdgeSection__Group_4__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3620:1: ( ( ( rule__ElkEdgeSection__UnorderedGroup_4_0 ) ) )
            // InternalElkGraph.g:3621:1: ( ( rule__ElkEdgeSection__UnorderedGroup_4_0 ) )
            {
            // InternalElkGraph.g:3621:1: ( ( rule__ElkEdgeSection__UnorderedGroup_4_0 ) )
            // InternalElkGraph.g:3622:2: ( rule__ElkEdgeSection__UnorderedGroup_4_0 )
            {
             before(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0()); 
            // InternalElkGraph.g:3623:2: ( rule__ElkEdgeSection__UnorderedGroup_4_0 )
            // InternalElkGraph.g:3623:3: rule__ElkEdgeSection__UnorderedGroup_4_0
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__UnorderedGroup_4_0();

            state._fsp--;


            }

             after(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0()); 

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4__0__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4__1"
    // InternalElkGraph.g:3631:1: rule__ElkEdgeSection__Group_4__1 : rule__ElkEdgeSection__Group_4__1__Impl rule__ElkEdgeSection__Group_4__2 ;
    public final void rule__ElkEdgeSection__Group_4__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3635:1: ( rule__ElkEdgeSection__Group_4__1__Impl rule__ElkEdgeSection__Group_4__2 )
            // InternalElkGraph.g:3636:2: rule__ElkEdgeSection__Group_4__1__Impl rule__ElkEdgeSection__Group_4__2
            {
            pushFollow(FOLLOW_24);
            rule__ElkEdgeSection__Group_4__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4__2();

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4__1"


    // $ANTLR start "rule__ElkEdgeSection__Group_4__1__Impl"
    // InternalElkGraph.g:3643:1: rule__ElkEdgeSection__Group_4__1__Impl : ( ( rule__ElkEdgeSection__Group_4_1__0 )? ) ;
    public final void rule__ElkEdgeSection__Group_4__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3647:1: ( ( ( rule__ElkEdgeSection__Group_4_1__0 )? ) )
            // InternalElkGraph.g:3648:1: ( ( rule__ElkEdgeSection__Group_4_1__0 )? )
            {
            // InternalElkGraph.g:3648:1: ( ( rule__ElkEdgeSection__Group_4_1__0 )? )
            // InternalElkGraph.g:3649:2: ( rule__ElkEdgeSection__Group_4_1__0 )?
            {
             before(grammarAccess.getElkEdgeSectionAccess().getGroup_4_1()); 
            // InternalElkGraph.g:3650:2: ( rule__ElkEdgeSection__Group_4_1__0 )?
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==34) ) {
                alt37=1;
            }
            switch (alt37) {
                case 1 :
                    // InternalElkGraph.g:3650:3: rule__ElkEdgeSection__Group_4_1__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkEdgeSection__Group_4_1__0();

                    state._fsp--;


                    }
                    break;

            }

             after(grammarAccess.getElkEdgeSectionAccess().getGroup_4_1()); 

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4__1__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4__2"
    // InternalElkGraph.g:3658:1: rule__ElkEdgeSection__Group_4__2 : rule__ElkEdgeSection__Group_4__2__Impl ;
    public final void rule__ElkEdgeSection__Group_4__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3662:1: ( rule__ElkEdgeSection__Group_4__2__Impl )
            // InternalElkGraph.g:3663:2: rule__ElkEdgeSection__Group_4__2__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4__2__Impl();

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4__2"


    // $ANTLR start "rule__ElkEdgeSection__Group_4__2__Impl"
    // InternalElkGraph.g:3669:1: rule__ElkEdgeSection__Group_4__2__Impl : ( ( rule__ElkEdgeSection__PropertiesAssignment_4_2 )* ) ;
    public final void rule__ElkEdgeSection__Group_4__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3673:1: ( ( ( rule__ElkEdgeSection__PropertiesAssignment_4_2 )* ) )
            // InternalElkGraph.g:3674:1: ( ( rule__ElkEdgeSection__PropertiesAssignment_4_2 )* )
            {
            // InternalElkGraph.g:3674:1: ( ( rule__ElkEdgeSection__PropertiesAssignment_4_2 )* )
            // InternalElkGraph.g:3675:2: ( rule__ElkEdgeSection__PropertiesAssignment_4_2 )*
            {
             before(grammarAccess.getElkEdgeSectionAccess().getPropertiesAssignment_4_2()); 
            // InternalElkGraph.g:3676:2: ( rule__ElkEdgeSection__PropertiesAssignment_4_2 )*
            loop38:
            do {
                int alt38=2;
                int LA38_0 = input.LA(1);

                if ( (LA38_0==RULE_ID) ) {
                    alt38=1;
                }


                switch (alt38) {
            	case 1 :
            	    // InternalElkGraph.g:3676:3: rule__ElkEdgeSection__PropertiesAssignment_4_2
            	    {
            	    pushFollow(FOLLOW_5);
            	    rule__ElkEdgeSection__PropertiesAssignment_4_2();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop38;
                }
            } while (true);

             after(grammarAccess.getElkEdgeSectionAccess().getPropertiesAssignment_4_2()); 

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4__2__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0_0__0"
    // InternalElkGraph.g:3685:1: rule__ElkEdgeSection__Group_4_0_0__0 : rule__ElkEdgeSection__Group_4_0_0__0__Impl rule__ElkEdgeSection__Group_4_0_0__1 ;
    public final void rule__ElkEdgeSection__Group_4_0_0__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3689:1: ( rule__ElkEdgeSection__Group_4_0_0__0__Impl rule__ElkEdgeSection__Group_4_0_0__1 )
            // InternalElkGraph.g:3690:2: rule__ElkEdgeSection__Group_4_0_0__0__Impl rule__ElkEdgeSection__Group_4_0_0__1
            {
            pushFollow(FOLLOW_11);
            rule__ElkEdgeSection__Group_4_0_0__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_0_0__1();

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0_0__0"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0_0__0__Impl"
    // InternalElkGraph.g:3697:1: rule__ElkEdgeSection__Group_4_0_0__0__Impl : ( 'incoming' ) ;
    public final void rule__ElkEdgeSection__Group_4_0_0__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3701:1: ( ( 'incoming' ) )
            // InternalElkGraph.g:3702:1: ( 'incoming' )
            {
            // InternalElkGraph.g:3702:1: ( 'incoming' )
            // InternalElkGraph.g:3703:2: 'incoming'
            {
             before(grammarAccess.getElkEdgeSectionAccess().getIncomingKeyword_4_0_0_0()); 
            match(input,30,FOLLOW_2); 
             after(grammarAccess.getElkEdgeSectionAccess().getIncomingKeyword_4_0_0_0()); 

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0_0__0__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0_0__1"
    // InternalElkGraph.g:3712:1: rule__ElkEdgeSection__Group_4_0_0__1 : rule__ElkEdgeSection__Group_4_0_0__1__Impl rule__ElkEdgeSection__Group_4_0_0__2 ;
    public final void rule__ElkEdgeSection__Group_4_0_0__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3716:1: ( rule__ElkEdgeSection__Group_4_0_0__1__Impl rule__ElkEdgeSection__Group_4_0_0__2 )
            // InternalElkGraph.g:3717:2: rule__ElkEdgeSection__Group_4_0_0__1__Impl rule__ElkEdgeSection__Group_4_0_0__2
            {
            pushFollow(FOLLOW_7);
            rule__ElkEdgeSection__Group_4_0_0__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_0_0__2();

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0_0__1"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0_0__1__Impl"
    // InternalElkGraph.g:3724:1: rule__ElkEdgeSection__Group_4_0_0__1__Impl : ( ':' ) ;
    public final void rule__ElkEdgeSection__Group_4_0_0__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3728:1: ( ( ':' ) )
            // InternalElkGraph.g:3729:1: ( ':' )
            {
            // InternalElkGraph.g:3729:1: ( ':' )
            // InternalElkGraph.g:3730:2: ':'
            {
             before(grammarAccess.getElkEdgeSectionAccess().getColonKeyword_4_0_0_1()); 
            match(input,20,FOLLOW_2); 
             after(grammarAccess.getElkEdgeSectionAccess().getColonKeyword_4_0_0_1()); 

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0_0__1__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0_0__2"
    // InternalElkGraph.g:3739:1: rule__ElkEdgeSection__Group_4_0_0__2 : rule__ElkEdgeSection__Group_4_0_0__2__Impl ;
    public final void rule__ElkEdgeSection__Group_4_0_0__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3743:1: ( rule__ElkEdgeSection__Group_4_0_0__2__Impl )
            // InternalElkGraph.g:3744:2: rule__ElkEdgeSection__Group_4_0_0__2__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_0_0__2__Impl();

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0_0__2"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0_0__2__Impl"
    // InternalElkGraph.g:3750:1: rule__ElkEdgeSection__Group_4_0_0__2__Impl : ( ( rule__ElkEdgeSection__IncomingShapeAssignment_4_0_0_2 ) ) ;
    public final void rule__ElkEdgeSection__Group_4_0_0__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3754:1: ( ( ( rule__ElkEdgeSection__IncomingShapeAssignment_4_0_0_2 ) ) )
            // InternalElkGraph.g:3755:1: ( ( rule__ElkEdgeSection__IncomingShapeAssignment_4_0_0_2 ) )
            {
            // InternalElkGraph.g:3755:1: ( ( rule__ElkEdgeSection__IncomingShapeAssignment_4_0_0_2 ) )
            // InternalElkGraph.g:3756:2: ( rule__ElkEdgeSection__IncomingShapeAssignment_4_0_0_2 )
            {
             before(grammarAccess.getElkEdgeSectionAccess().getIncomingShapeAssignment_4_0_0_2()); 
            // InternalElkGraph.g:3757:2: ( rule__ElkEdgeSection__IncomingShapeAssignment_4_0_0_2 )
            // InternalElkGraph.g:3757:3: rule__ElkEdgeSection__IncomingShapeAssignment_4_0_0_2
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__IncomingShapeAssignment_4_0_0_2();

            state._fsp--;


            }

             after(grammarAccess.getElkEdgeSectionAccess().getIncomingShapeAssignment_4_0_0_2()); 

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0_0__2__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0_1__0"
    // InternalElkGraph.g:3766:1: rule__ElkEdgeSection__Group_4_0_1__0 : rule__ElkEdgeSection__Group_4_0_1__0__Impl rule__ElkEdgeSection__Group_4_0_1__1 ;
    public final void rule__ElkEdgeSection__Group_4_0_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3770:1: ( rule__ElkEdgeSection__Group_4_0_1__0__Impl rule__ElkEdgeSection__Group_4_0_1__1 )
            // InternalElkGraph.g:3771:2: rule__ElkEdgeSection__Group_4_0_1__0__Impl rule__ElkEdgeSection__Group_4_0_1__1
            {
            pushFollow(FOLLOW_11);
            rule__ElkEdgeSection__Group_4_0_1__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_0_1__1();

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0_1__0"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0_1__0__Impl"
    // InternalElkGraph.g:3778:1: rule__ElkEdgeSection__Group_4_0_1__0__Impl : ( 'outgoing' ) ;
    public final void rule__ElkEdgeSection__Group_4_0_1__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3782:1: ( ( 'outgoing' ) )
            // InternalElkGraph.g:3783:1: ( 'outgoing' )
            {
            // InternalElkGraph.g:3783:1: ( 'outgoing' )
            // InternalElkGraph.g:3784:2: 'outgoing'
            {
             before(grammarAccess.getElkEdgeSectionAccess().getOutgoingKeyword_4_0_1_0()); 
            match(input,31,FOLLOW_2); 
             after(grammarAccess.getElkEdgeSectionAccess().getOutgoingKeyword_4_0_1_0()); 

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0_1__0__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0_1__1"
    // InternalElkGraph.g:3793:1: rule__ElkEdgeSection__Group_4_0_1__1 : rule__ElkEdgeSection__Group_4_0_1__1__Impl rule__ElkEdgeSection__Group_4_0_1__2 ;
    public final void rule__ElkEdgeSection__Group_4_0_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3797:1: ( rule__ElkEdgeSection__Group_4_0_1__1__Impl rule__ElkEdgeSection__Group_4_0_1__2 )
            // InternalElkGraph.g:3798:2: rule__ElkEdgeSection__Group_4_0_1__1__Impl rule__ElkEdgeSection__Group_4_0_1__2
            {
            pushFollow(FOLLOW_7);
            rule__ElkEdgeSection__Group_4_0_1__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_0_1__2();

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0_1__1"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0_1__1__Impl"
    // InternalElkGraph.g:3805:1: rule__ElkEdgeSection__Group_4_0_1__1__Impl : ( ':' ) ;
    public final void rule__ElkEdgeSection__Group_4_0_1__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3809:1: ( ( ':' ) )
            // InternalElkGraph.g:3810:1: ( ':' )
            {
            // InternalElkGraph.g:3810:1: ( ':' )
            // InternalElkGraph.g:3811:2: ':'
            {
             before(grammarAccess.getElkEdgeSectionAccess().getColonKeyword_4_0_1_1()); 
            match(input,20,FOLLOW_2); 
             after(grammarAccess.getElkEdgeSectionAccess().getColonKeyword_4_0_1_1()); 

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0_1__1__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0_1__2"
    // InternalElkGraph.g:3820:1: rule__ElkEdgeSection__Group_4_0_1__2 : rule__ElkEdgeSection__Group_4_0_1__2__Impl ;
    public final void rule__ElkEdgeSection__Group_4_0_1__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3824:1: ( rule__ElkEdgeSection__Group_4_0_1__2__Impl )
            // InternalElkGraph.g:3825:2: rule__ElkEdgeSection__Group_4_0_1__2__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_0_1__2__Impl();

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0_1__2"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0_1__2__Impl"
    // InternalElkGraph.g:3831:1: rule__ElkEdgeSection__Group_4_0_1__2__Impl : ( ( rule__ElkEdgeSection__OutgoingShapeAssignment_4_0_1_2 ) ) ;
    public final void rule__ElkEdgeSection__Group_4_0_1__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3835:1: ( ( ( rule__ElkEdgeSection__OutgoingShapeAssignment_4_0_1_2 ) ) )
            // InternalElkGraph.g:3836:1: ( ( rule__ElkEdgeSection__OutgoingShapeAssignment_4_0_1_2 ) )
            {
            // InternalElkGraph.g:3836:1: ( ( rule__ElkEdgeSection__OutgoingShapeAssignment_4_0_1_2 ) )
            // InternalElkGraph.g:3837:2: ( rule__ElkEdgeSection__OutgoingShapeAssignment_4_0_1_2 )
            {
             before(grammarAccess.getElkEdgeSectionAccess().getOutgoingShapeAssignment_4_0_1_2()); 
            // InternalElkGraph.g:3838:2: ( rule__ElkEdgeSection__OutgoingShapeAssignment_4_0_1_2 )
            // InternalElkGraph.g:3838:3: rule__ElkEdgeSection__OutgoingShapeAssignment_4_0_1_2
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__OutgoingShapeAssignment_4_0_1_2();

            state._fsp--;


            }

             after(grammarAccess.getElkEdgeSectionAccess().getOutgoingShapeAssignment_4_0_1_2()); 

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0_1__2__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0_2__0"
    // InternalElkGraph.g:3847:1: rule__ElkEdgeSection__Group_4_0_2__0 : rule__ElkEdgeSection__Group_4_0_2__0__Impl rule__ElkEdgeSection__Group_4_0_2__1 ;
    public final void rule__ElkEdgeSection__Group_4_0_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3851:1: ( rule__ElkEdgeSection__Group_4_0_2__0__Impl rule__ElkEdgeSection__Group_4_0_2__1 )
            // InternalElkGraph.g:3852:2: rule__ElkEdgeSection__Group_4_0_2__0__Impl rule__ElkEdgeSection__Group_4_0_2__1
            {
            pushFollow(FOLLOW_11);
            rule__ElkEdgeSection__Group_4_0_2__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_0_2__1();

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0_2__0"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0_2__0__Impl"
    // InternalElkGraph.g:3859:1: rule__ElkEdgeSection__Group_4_0_2__0__Impl : ( 'start' ) ;
    public final void rule__ElkEdgeSection__Group_4_0_2__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3863:1: ( ( 'start' ) )
            // InternalElkGraph.g:3864:1: ( 'start' )
            {
            // InternalElkGraph.g:3864:1: ( 'start' )
            // InternalElkGraph.g:3865:2: 'start'
            {
             before(grammarAccess.getElkEdgeSectionAccess().getStartKeyword_4_0_2_0()); 
            match(input,32,FOLLOW_2); 
             after(grammarAccess.getElkEdgeSectionAccess().getStartKeyword_4_0_2_0()); 

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0_2__0__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0_2__1"
    // InternalElkGraph.g:3874:1: rule__ElkEdgeSection__Group_4_0_2__1 : rule__ElkEdgeSection__Group_4_0_2__1__Impl rule__ElkEdgeSection__Group_4_0_2__2 ;
    public final void rule__ElkEdgeSection__Group_4_0_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3878:1: ( rule__ElkEdgeSection__Group_4_0_2__1__Impl rule__ElkEdgeSection__Group_4_0_2__2 )
            // InternalElkGraph.g:3879:2: rule__ElkEdgeSection__Group_4_0_2__1__Impl rule__ElkEdgeSection__Group_4_0_2__2
            {
            pushFollow(FOLLOW_17);
            rule__ElkEdgeSection__Group_4_0_2__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_0_2__2();

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0_2__1"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0_2__1__Impl"
    // InternalElkGraph.g:3886:1: rule__ElkEdgeSection__Group_4_0_2__1__Impl : ( ':' ) ;
    public final void rule__ElkEdgeSection__Group_4_0_2__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3890:1: ( ( ':' ) )
            // InternalElkGraph.g:3891:1: ( ':' )
            {
            // InternalElkGraph.g:3891:1: ( ':' )
            // InternalElkGraph.g:3892:2: ':'
            {
             before(grammarAccess.getElkEdgeSectionAccess().getColonKeyword_4_0_2_1()); 
            match(input,20,FOLLOW_2); 
             after(grammarAccess.getElkEdgeSectionAccess().getColonKeyword_4_0_2_1()); 

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0_2__1__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0_2__2"
    // InternalElkGraph.g:3901:1: rule__ElkEdgeSection__Group_4_0_2__2 : rule__ElkEdgeSection__Group_4_0_2__2__Impl rule__ElkEdgeSection__Group_4_0_2__3 ;
    public final void rule__ElkEdgeSection__Group_4_0_2__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3905:1: ( rule__ElkEdgeSection__Group_4_0_2__2__Impl rule__ElkEdgeSection__Group_4_0_2__3 )
            // InternalElkGraph.g:3906:2: rule__ElkEdgeSection__Group_4_0_2__2__Impl rule__ElkEdgeSection__Group_4_0_2__3
            {
            pushFollow(FOLLOW_18);
            rule__ElkEdgeSection__Group_4_0_2__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_0_2__3();

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0_2__2"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0_2__2__Impl"
    // InternalElkGraph.g:3913:1: rule__ElkEdgeSection__Group_4_0_2__2__Impl : ( ( rule__ElkEdgeSection__StartXAssignment_4_0_2_2 ) ) ;
    public final void rule__ElkEdgeSection__Group_4_0_2__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3917:1: ( ( ( rule__ElkEdgeSection__StartXAssignment_4_0_2_2 ) ) )
            // InternalElkGraph.g:3918:1: ( ( rule__ElkEdgeSection__StartXAssignment_4_0_2_2 ) )
            {
            // InternalElkGraph.g:3918:1: ( ( rule__ElkEdgeSection__StartXAssignment_4_0_2_2 ) )
            // InternalElkGraph.g:3919:2: ( rule__ElkEdgeSection__StartXAssignment_4_0_2_2 )
            {
             before(grammarAccess.getElkEdgeSectionAccess().getStartXAssignment_4_0_2_2()); 
            // InternalElkGraph.g:3920:2: ( rule__ElkEdgeSection__StartXAssignment_4_0_2_2 )
            // InternalElkGraph.g:3920:3: rule__ElkEdgeSection__StartXAssignment_4_0_2_2
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__StartXAssignment_4_0_2_2();

            state._fsp--;


            }

             after(grammarAccess.getElkEdgeSectionAccess().getStartXAssignment_4_0_2_2()); 

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0_2__2__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0_2__3"
    // InternalElkGraph.g:3928:1: rule__ElkEdgeSection__Group_4_0_2__3 : rule__ElkEdgeSection__Group_4_0_2__3__Impl rule__ElkEdgeSection__Group_4_0_2__4 ;
    public final void rule__ElkEdgeSection__Group_4_0_2__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3932:1: ( rule__ElkEdgeSection__Group_4_0_2__3__Impl rule__ElkEdgeSection__Group_4_0_2__4 )
            // InternalElkGraph.g:3933:2: rule__ElkEdgeSection__Group_4_0_2__3__Impl rule__ElkEdgeSection__Group_4_0_2__4
            {
            pushFollow(FOLLOW_17);
            rule__ElkEdgeSection__Group_4_0_2__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_0_2__4();

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0_2__3"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0_2__3__Impl"
    // InternalElkGraph.g:3940:1: rule__ElkEdgeSection__Group_4_0_2__3__Impl : ( ',' ) ;
    public final void rule__ElkEdgeSection__Group_4_0_2__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3944:1: ( ( ',' ) )
            // InternalElkGraph.g:3945:1: ( ',' )
            {
            // InternalElkGraph.g:3945:1: ( ',' )
            // InternalElkGraph.g:3946:2: ','
            {
             before(grammarAccess.getElkEdgeSectionAccess().getCommaKeyword_4_0_2_3()); 
            match(input,26,FOLLOW_2); 
             after(grammarAccess.getElkEdgeSectionAccess().getCommaKeyword_4_0_2_3()); 

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0_2__3__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0_2__4"
    // InternalElkGraph.g:3955:1: rule__ElkEdgeSection__Group_4_0_2__4 : rule__ElkEdgeSection__Group_4_0_2__4__Impl ;
    public final void rule__ElkEdgeSection__Group_4_0_2__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3959:1: ( rule__ElkEdgeSection__Group_4_0_2__4__Impl )
            // InternalElkGraph.g:3960:2: rule__ElkEdgeSection__Group_4_0_2__4__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_0_2__4__Impl();

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0_2__4"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0_2__4__Impl"
    // InternalElkGraph.g:3966:1: rule__ElkEdgeSection__Group_4_0_2__4__Impl : ( ( rule__ElkEdgeSection__StartYAssignment_4_0_2_4 ) ) ;
    public final void rule__ElkEdgeSection__Group_4_0_2__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3970:1: ( ( ( rule__ElkEdgeSection__StartYAssignment_4_0_2_4 ) ) )
            // InternalElkGraph.g:3971:1: ( ( rule__ElkEdgeSection__StartYAssignment_4_0_2_4 ) )
            {
            // InternalElkGraph.g:3971:1: ( ( rule__ElkEdgeSection__StartYAssignment_4_0_2_4 ) )
            // InternalElkGraph.g:3972:2: ( rule__ElkEdgeSection__StartYAssignment_4_0_2_4 )
            {
             before(grammarAccess.getElkEdgeSectionAccess().getStartYAssignment_4_0_2_4()); 
            // InternalElkGraph.g:3973:2: ( rule__ElkEdgeSection__StartYAssignment_4_0_2_4 )
            // InternalElkGraph.g:3973:3: rule__ElkEdgeSection__StartYAssignment_4_0_2_4
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__StartYAssignment_4_0_2_4();

            state._fsp--;


            }

             after(grammarAccess.getElkEdgeSectionAccess().getStartYAssignment_4_0_2_4()); 

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0_2__4__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0_3__0"
    // InternalElkGraph.g:3982:1: rule__ElkEdgeSection__Group_4_0_3__0 : rule__ElkEdgeSection__Group_4_0_3__0__Impl rule__ElkEdgeSection__Group_4_0_3__1 ;
    public final void rule__ElkEdgeSection__Group_4_0_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3986:1: ( rule__ElkEdgeSection__Group_4_0_3__0__Impl rule__ElkEdgeSection__Group_4_0_3__1 )
            // InternalElkGraph.g:3987:2: rule__ElkEdgeSection__Group_4_0_3__0__Impl rule__ElkEdgeSection__Group_4_0_3__1
            {
            pushFollow(FOLLOW_11);
            rule__ElkEdgeSection__Group_4_0_3__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_0_3__1();

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0_3__0"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0_3__0__Impl"
    // InternalElkGraph.g:3994:1: rule__ElkEdgeSection__Group_4_0_3__0__Impl : ( 'end' ) ;
    public final void rule__ElkEdgeSection__Group_4_0_3__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:3998:1: ( ( 'end' ) )
            // InternalElkGraph.g:3999:1: ( 'end' )
            {
            // InternalElkGraph.g:3999:1: ( 'end' )
            // InternalElkGraph.g:4000:2: 'end'
            {
             before(grammarAccess.getElkEdgeSectionAccess().getEndKeyword_4_0_3_0()); 
            match(input,33,FOLLOW_2); 
             after(grammarAccess.getElkEdgeSectionAccess().getEndKeyword_4_0_3_0()); 

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0_3__0__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0_3__1"
    // InternalElkGraph.g:4009:1: rule__ElkEdgeSection__Group_4_0_3__1 : rule__ElkEdgeSection__Group_4_0_3__1__Impl rule__ElkEdgeSection__Group_4_0_3__2 ;
    public final void rule__ElkEdgeSection__Group_4_0_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4013:1: ( rule__ElkEdgeSection__Group_4_0_3__1__Impl rule__ElkEdgeSection__Group_4_0_3__2 )
            // InternalElkGraph.g:4014:2: rule__ElkEdgeSection__Group_4_0_3__1__Impl rule__ElkEdgeSection__Group_4_0_3__2
            {
            pushFollow(FOLLOW_17);
            rule__ElkEdgeSection__Group_4_0_3__1__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_0_3__2();

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0_3__1"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0_3__1__Impl"
    // InternalElkGraph.g:4021:1: rule__ElkEdgeSection__Group_4_0_3__1__Impl : ( ':' ) ;
    public final void rule__ElkEdgeSection__Group_4_0_3__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4025:1: ( ( ':' ) )
            // InternalElkGraph.g:4026:1: ( ':' )
            {
            // InternalElkGraph.g:4026:1: ( ':' )
            // InternalElkGraph.g:4027:2: ':'
            {
             before(grammarAccess.getElkEdgeSectionAccess().getColonKeyword_4_0_3_1()); 
            match(input,20,FOLLOW_2); 
             after(grammarAccess.getElkEdgeSectionAccess().getColonKeyword_4_0_3_1()); 

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0_3__1__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0_3__2"
    // InternalElkGraph.g:4036:1: rule__ElkEdgeSection__Group_4_0_3__2 : rule__ElkEdgeSection__Group_4_0_3__2__Impl rule__ElkEdgeSection__Group_4_0_3__3 ;
    public final void rule__ElkEdgeSection__Group_4_0_3__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4040:1: ( rule__ElkEdgeSection__Group_4_0_3__2__Impl rule__ElkEdgeSection__Group_4_0_3__3 )
            // InternalElkGraph.g:4041:2: rule__ElkEdgeSection__Group_4_0_3__2__Impl rule__ElkEdgeSection__Group_4_0_3__3
            {
            pushFollow(FOLLOW_18);
            rule__ElkEdgeSection__Group_4_0_3__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_0_3__3();

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0_3__2"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0_3__2__Impl"
    // InternalElkGraph.g:4048:1: rule__ElkEdgeSection__Group_4_0_3__2__Impl : ( ( rule__ElkEdgeSection__EndXAssignment_4_0_3_2 ) ) ;
    public final void rule__ElkEdgeSection__Group_4_0_3__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4052:1: ( ( ( rule__ElkEdgeSection__EndXAssignment_4_0_3_2 ) ) )
            // InternalElkGraph.g:4053:1: ( ( rule__ElkEdgeSection__EndXAssignment_4_0_3_2 ) )
            {
            // InternalElkGraph.g:4053:1: ( ( rule__ElkEdgeSection__EndXAssignment_4_0_3_2 ) )
            // InternalElkGraph.g:4054:2: ( rule__ElkEdgeSection__EndXAssignment_4_0_3_2 )
            {
             before(grammarAccess.getElkEdgeSectionAccess().getEndXAssignment_4_0_3_2()); 
            // InternalElkGraph.g:4055:2: ( rule__ElkEdgeSection__EndXAssignment_4_0_3_2 )
            // InternalElkGraph.g:4055:3: rule__ElkEdgeSection__EndXAssignment_4_0_3_2
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__EndXAssignment_4_0_3_2();

            state._fsp--;


            }

             after(grammarAccess.getElkEdgeSectionAccess().getEndXAssignment_4_0_3_2()); 

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0_3__2__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0_3__3"
    // InternalElkGraph.g:4063:1: rule__ElkEdgeSection__Group_4_0_3__3 : rule__ElkEdgeSection__Group_4_0_3__3__Impl rule__ElkEdgeSection__Group_4_0_3__4 ;
    public final void rule__ElkEdgeSection__Group_4_0_3__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4067:1: ( rule__ElkEdgeSection__Group_4_0_3__3__Impl rule__ElkEdgeSection__Group_4_0_3__4 )
            // InternalElkGraph.g:4068:2: rule__ElkEdgeSection__Group_4_0_3__3__Impl rule__ElkEdgeSection__Group_4_0_3__4
            {
            pushFollow(FOLLOW_17);
            rule__ElkEdgeSection__Group_4_0_3__3__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_0_3__4();

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0_3__3"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0_3__3__Impl"
    // InternalElkGraph.g:4075:1: rule__ElkEdgeSection__Group_4_0_3__3__Impl : ( ',' ) ;
    public final void rule__ElkEdgeSection__Group_4_0_3__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4079:1: ( ( ',' ) )
            // InternalElkGraph.g:4080:1: ( ',' )
            {
            // InternalElkGraph.g:4080:1: ( ',' )
            // InternalElkGraph.g:4081:2: ','
            {
             before(grammarAccess.getElkEdgeSectionAccess().getCommaKeyword_4_0_3_3()); 
            match(input,26,FOLLOW_2); 
             after(grammarAccess.getElkEdgeSectionAccess().getCommaKeyword_4_0_3_3()); 

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0_3__3__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0_3__4"
    // InternalElkGraph.g:4090:1: rule__ElkEdgeSection__Group_4_0_3__4 : rule__ElkEdgeSection__Group_4_0_3__4__Impl ;
    public final void rule__ElkEdgeSection__Group_4_0_3__4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4094:1: ( rule__ElkEdgeSection__Group_4_0_3__4__Impl )
            // InternalElkGraph.g:4095:2: rule__ElkEdgeSection__Group_4_0_3__4__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_0_3__4__Impl();

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0_3__4"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_0_3__4__Impl"
    // InternalElkGraph.g:4101:1: rule__ElkEdgeSection__Group_4_0_3__4__Impl : ( ( rule__ElkEdgeSection__EndYAssignment_4_0_3_4 ) ) ;
    public final void rule__ElkEdgeSection__Group_4_0_3__4__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4105:1: ( ( ( rule__ElkEdgeSection__EndYAssignment_4_0_3_4 ) ) )
            // InternalElkGraph.g:4106:1: ( ( rule__ElkEdgeSection__EndYAssignment_4_0_3_4 ) )
            {
            // InternalElkGraph.g:4106:1: ( ( rule__ElkEdgeSection__EndYAssignment_4_0_3_4 ) )
            // InternalElkGraph.g:4107:2: ( rule__ElkEdgeSection__EndYAssignment_4_0_3_4 )
            {
             before(grammarAccess.getElkEdgeSectionAccess().getEndYAssignment_4_0_3_4()); 
            // InternalElkGraph.g:4108:2: ( rule__ElkEdgeSection__EndYAssignment_4_0_3_4 )
            // InternalElkGraph.g:4108:3: rule__ElkEdgeSection__EndYAssignment_4_0_3_4
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__EndYAssignment_4_0_3_4();

            state._fsp--;


            }

             after(grammarAccess.getElkEdgeSectionAccess().getEndYAssignment_4_0_3_4()); 

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_0_3__4__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_1__0"
    // InternalElkGraph.g:4117:1: rule__ElkEdgeSection__Group_4_1__0 : rule__ElkEdgeSection__Group_4_1__0__Impl rule__ElkEdgeSection__Group_4_1__1 ;
    public final void rule__ElkEdgeSection__Group_4_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4121:1: ( rule__ElkEdgeSection__Group_4_1__0__Impl rule__ElkEdgeSection__Group_4_1__1 )
            // InternalElkGraph.g:4122:2: rule__ElkEdgeSection__Group_4_1__0__Impl rule__ElkEdgeSection__Group_4_1__1
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
    // InternalElkGraph.g:4129:1: rule__ElkEdgeSection__Group_4_1__0__Impl : ( 'bends' ) ;
    public final void rule__ElkEdgeSection__Group_4_1__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4133:1: ( ( 'bends' ) )
            // InternalElkGraph.g:4134:1: ( 'bends' )
            {
            // InternalElkGraph.g:4134:1: ( 'bends' )
            // InternalElkGraph.g:4135:2: 'bends'
            {
             before(grammarAccess.getElkEdgeSectionAccess().getBendsKeyword_4_1_0()); 
            match(input,34,FOLLOW_2); 
             after(grammarAccess.getElkEdgeSectionAccess().getBendsKeyword_4_1_0()); 

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
    // InternalElkGraph.g:4144:1: rule__ElkEdgeSection__Group_4_1__1 : rule__ElkEdgeSection__Group_4_1__1__Impl rule__ElkEdgeSection__Group_4_1__2 ;
    public final void rule__ElkEdgeSection__Group_4_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4148:1: ( rule__ElkEdgeSection__Group_4_1__1__Impl rule__ElkEdgeSection__Group_4_1__2 )
            // InternalElkGraph.g:4149:2: rule__ElkEdgeSection__Group_4_1__1__Impl rule__ElkEdgeSection__Group_4_1__2
            {
            pushFollow(FOLLOW_17);
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
    // InternalElkGraph.g:4156:1: rule__ElkEdgeSection__Group_4_1__1__Impl : ( ':' ) ;
    public final void rule__ElkEdgeSection__Group_4_1__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4160:1: ( ( ':' ) )
            // InternalElkGraph.g:4161:1: ( ':' )
            {
            // InternalElkGraph.g:4161:1: ( ':' )
            // InternalElkGraph.g:4162:2: ':'
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
    // InternalElkGraph.g:4171:1: rule__ElkEdgeSection__Group_4_1__2 : rule__ElkEdgeSection__Group_4_1__2__Impl rule__ElkEdgeSection__Group_4_1__3 ;
    public final void rule__ElkEdgeSection__Group_4_1__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4175:1: ( rule__ElkEdgeSection__Group_4_1__2__Impl rule__ElkEdgeSection__Group_4_1__3 )
            // InternalElkGraph.g:4176:2: rule__ElkEdgeSection__Group_4_1__2__Impl rule__ElkEdgeSection__Group_4_1__3
            {
            pushFollow(FOLLOW_25);
            rule__ElkEdgeSection__Group_4_1__2__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_1__3();

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
    // InternalElkGraph.g:4183:1: rule__ElkEdgeSection__Group_4_1__2__Impl : ( ( rule__ElkEdgeSection__BendPointsAssignment_4_1_2 ) ) ;
    public final void rule__ElkEdgeSection__Group_4_1__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4187:1: ( ( ( rule__ElkEdgeSection__BendPointsAssignment_4_1_2 ) ) )
            // InternalElkGraph.g:4188:1: ( ( rule__ElkEdgeSection__BendPointsAssignment_4_1_2 ) )
            {
            // InternalElkGraph.g:4188:1: ( ( rule__ElkEdgeSection__BendPointsAssignment_4_1_2 ) )
            // InternalElkGraph.g:4189:2: ( rule__ElkEdgeSection__BendPointsAssignment_4_1_2 )
            {
             before(grammarAccess.getElkEdgeSectionAccess().getBendPointsAssignment_4_1_2()); 
            // InternalElkGraph.g:4190:2: ( rule__ElkEdgeSection__BendPointsAssignment_4_1_2 )
            // InternalElkGraph.g:4190:3: rule__ElkEdgeSection__BendPointsAssignment_4_1_2
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__BendPointsAssignment_4_1_2();

            state._fsp--;


            }

             after(grammarAccess.getElkEdgeSectionAccess().getBendPointsAssignment_4_1_2()); 

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


    // $ANTLR start "rule__ElkEdgeSection__Group_4_1__3"
    // InternalElkGraph.g:4198:1: rule__ElkEdgeSection__Group_4_1__3 : rule__ElkEdgeSection__Group_4_1__3__Impl ;
    public final void rule__ElkEdgeSection__Group_4_1__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4202:1: ( rule__ElkEdgeSection__Group_4_1__3__Impl )
            // InternalElkGraph.g:4203:2: rule__ElkEdgeSection__Group_4_1__3__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_1__3__Impl();

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_1__3"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_1__3__Impl"
    // InternalElkGraph.g:4209:1: rule__ElkEdgeSection__Group_4_1__3__Impl : ( ( rule__ElkEdgeSection__Group_4_1_3__0 )* ) ;
    public final void rule__ElkEdgeSection__Group_4_1__3__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4213:1: ( ( ( rule__ElkEdgeSection__Group_4_1_3__0 )* ) )
            // InternalElkGraph.g:4214:1: ( ( rule__ElkEdgeSection__Group_4_1_3__0 )* )
            {
            // InternalElkGraph.g:4214:1: ( ( rule__ElkEdgeSection__Group_4_1_3__0 )* )
            // InternalElkGraph.g:4215:2: ( rule__ElkEdgeSection__Group_4_1_3__0 )*
            {
             before(grammarAccess.getElkEdgeSectionAccess().getGroup_4_1_3()); 
            // InternalElkGraph.g:4216:2: ( rule__ElkEdgeSection__Group_4_1_3__0 )*
            loop39:
            do {
                int alt39=2;
                int LA39_0 = input.LA(1);

                if ( (LA39_0==35) ) {
                    alt39=1;
                }


                switch (alt39) {
            	case 1 :
            	    // InternalElkGraph.g:4216:3: rule__ElkEdgeSection__Group_4_1_3__0
            	    {
            	    pushFollow(FOLLOW_26);
            	    rule__ElkEdgeSection__Group_4_1_3__0();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop39;
                }
            } while (true);

             after(grammarAccess.getElkEdgeSectionAccess().getGroup_4_1_3()); 

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_1__3__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_1_3__0"
    // InternalElkGraph.g:4225:1: rule__ElkEdgeSection__Group_4_1_3__0 : rule__ElkEdgeSection__Group_4_1_3__0__Impl rule__ElkEdgeSection__Group_4_1_3__1 ;
    public final void rule__ElkEdgeSection__Group_4_1_3__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4229:1: ( rule__ElkEdgeSection__Group_4_1_3__0__Impl rule__ElkEdgeSection__Group_4_1_3__1 )
            // InternalElkGraph.g:4230:2: rule__ElkEdgeSection__Group_4_1_3__0__Impl rule__ElkEdgeSection__Group_4_1_3__1
            {
            pushFollow(FOLLOW_17);
            rule__ElkEdgeSection__Group_4_1_3__0__Impl();

            state._fsp--;

            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_1_3__1();

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_1_3__0"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_1_3__0__Impl"
    // InternalElkGraph.g:4237:1: rule__ElkEdgeSection__Group_4_1_3__0__Impl : ( '|' ) ;
    public final void rule__ElkEdgeSection__Group_4_1_3__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4241:1: ( ( '|' ) )
            // InternalElkGraph.g:4242:1: ( '|' )
            {
            // InternalElkGraph.g:4242:1: ( '|' )
            // InternalElkGraph.g:4243:2: '|'
            {
             before(grammarAccess.getElkEdgeSectionAccess().getVerticalLineKeyword_4_1_3_0()); 
            match(input,35,FOLLOW_2); 
             after(grammarAccess.getElkEdgeSectionAccess().getVerticalLineKeyword_4_1_3_0()); 

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_1_3__0__Impl"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_1_3__1"
    // InternalElkGraph.g:4252:1: rule__ElkEdgeSection__Group_4_1_3__1 : rule__ElkEdgeSection__Group_4_1_3__1__Impl ;
    public final void rule__ElkEdgeSection__Group_4_1_3__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4256:1: ( rule__ElkEdgeSection__Group_4_1_3__1__Impl )
            // InternalElkGraph.g:4257:2: rule__ElkEdgeSection__Group_4_1_3__1__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__Group_4_1_3__1__Impl();

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_1_3__1"


    // $ANTLR start "rule__ElkEdgeSection__Group_4_1_3__1__Impl"
    // InternalElkGraph.g:4263:1: rule__ElkEdgeSection__Group_4_1_3__1__Impl : ( ( rule__ElkEdgeSection__BendPointsAssignment_4_1_3_1 ) ) ;
    public final void rule__ElkEdgeSection__Group_4_1_3__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4267:1: ( ( ( rule__ElkEdgeSection__BendPointsAssignment_4_1_3_1 ) ) )
            // InternalElkGraph.g:4268:1: ( ( rule__ElkEdgeSection__BendPointsAssignment_4_1_3_1 ) )
            {
            // InternalElkGraph.g:4268:1: ( ( rule__ElkEdgeSection__BendPointsAssignment_4_1_3_1 ) )
            // InternalElkGraph.g:4269:2: ( rule__ElkEdgeSection__BendPointsAssignment_4_1_3_1 )
            {
             before(grammarAccess.getElkEdgeSectionAccess().getBendPointsAssignment_4_1_3_1()); 
            // InternalElkGraph.g:4270:2: ( rule__ElkEdgeSection__BendPointsAssignment_4_1_3_1 )
            // InternalElkGraph.g:4270:3: rule__ElkEdgeSection__BendPointsAssignment_4_1_3_1
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__BendPointsAssignment_4_1_3_1();

            state._fsp--;


            }

             after(grammarAccess.getElkEdgeSectionAccess().getBendPointsAssignment_4_1_3_1()); 

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
    // $ANTLR end "rule__ElkEdgeSection__Group_4_1_3__1__Impl"


    // $ANTLR start "rule__ElkBendPoint__Group__0"
    // InternalElkGraph.g:4279:1: rule__ElkBendPoint__Group__0 : rule__ElkBendPoint__Group__0__Impl rule__ElkBendPoint__Group__1 ;
    public final void rule__ElkBendPoint__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4283:1: ( rule__ElkBendPoint__Group__0__Impl rule__ElkBendPoint__Group__1 )
            // InternalElkGraph.g:4284:2: rule__ElkBendPoint__Group__0__Impl rule__ElkBendPoint__Group__1
            {
            pushFollow(FOLLOW_18);
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
    // InternalElkGraph.g:4291:1: rule__ElkBendPoint__Group__0__Impl : ( ( rule__ElkBendPoint__XAssignment_0 ) ) ;
    public final void rule__ElkBendPoint__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4295:1: ( ( ( rule__ElkBendPoint__XAssignment_0 ) ) )
            // InternalElkGraph.g:4296:1: ( ( rule__ElkBendPoint__XAssignment_0 ) )
            {
            // InternalElkGraph.g:4296:1: ( ( rule__ElkBendPoint__XAssignment_0 ) )
            // InternalElkGraph.g:4297:2: ( rule__ElkBendPoint__XAssignment_0 )
            {
             before(grammarAccess.getElkBendPointAccess().getXAssignment_0()); 
            // InternalElkGraph.g:4298:2: ( rule__ElkBendPoint__XAssignment_0 )
            // InternalElkGraph.g:4298:3: rule__ElkBendPoint__XAssignment_0
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
    // InternalElkGraph.g:4306:1: rule__ElkBendPoint__Group__1 : rule__ElkBendPoint__Group__1__Impl rule__ElkBendPoint__Group__2 ;
    public final void rule__ElkBendPoint__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4310:1: ( rule__ElkBendPoint__Group__1__Impl rule__ElkBendPoint__Group__2 )
            // InternalElkGraph.g:4311:2: rule__ElkBendPoint__Group__1__Impl rule__ElkBendPoint__Group__2
            {
            pushFollow(FOLLOW_17);
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
    // InternalElkGraph.g:4318:1: rule__ElkBendPoint__Group__1__Impl : ( ',' ) ;
    public final void rule__ElkBendPoint__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4322:1: ( ( ',' ) )
            // InternalElkGraph.g:4323:1: ( ',' )
            {
            // InternalElkGraph.g:4323:1: ( ',' )
            // InternalElkGraph.g:4324:2: ','
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
    // InternalElkGraph.g:4333:1: rule__ElkBendPoint__Group__2 : rule__ElkBendPoint__Group__2__Impl ;
    public final void rule__ElkBendPoint__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4337:1: ( rule__ElkBendPoint__Group__2__Impl )
            // InternalElkGraph.g:4338:2: rule__ElkBendPoint__Group__2__Impl
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
    // InternalElkGraph.g:4344:1: rule__ElkBendPoint__Group__2__Impl : ( ( rule__ElkBendPoint__YAssignment_2 ) ) ;
    public final void rule__ElkBendPoint__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4348:1: ( ( ( rule__ElkBendPoint__YAssignment_2 ) ) )
            // InternalElkGraph.g:4349:1: ( ( rule__ElkBendPoint__YAssignment_2 ) )
            {
            // InternalElkGraph.g:4349:1: ( ( rule__ElkBendPoint__YAssignment_2 ) )
            // InternalElkGraph.g:4350:2: ( rule__ElkBendPoint__YAssignment_2 )
            {
             before(grammarAccess.getElkBendPointAccess().getYAssignment_2()); 
            // InternalElkGraph.g:4351:2: ( rule__ElkBendPoint__YAssignment_2 )
            // InternalElkGraph.g:4351:3: rule__ElkBendPoint__YAssignment_2
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


    // $ANTLR start "rule__QualifiedId__Group__0"
    // InternalElkGraph.g:4360:1: rule__QualifiedId__Group__0 : rule__QualifiedId__Group__0__Impl rule__QualifiedId__Group__1 ;
    public final void rule__QualifiedId__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4364:1: ( rule__QualifiedId__Group__0__Impl rule__QualifiedId__Group__1 )
            // InternalElkGraph.g:4365:2: rule__QualifiedId__Group__0__Impl rule__QualifiedId__Group__1
            {
            pushFollow(FOLLOW_28);
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
    // InternalElkGraph.g:4372:1: rule__QualifiedId__Group__0__Impl : ( RULE_ID ) ;
    public final void rule__QualifiedId__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4376:1: ( ( RULE_ID ) )
            // InternalElkGraph.g:4377:1: ( RULE_ID )
            {
            // InternalElkGraph.g:4377:1: ( RULE_ID )
            // InternalElkGraph.g:4378:2: RULE_ID
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
    // InternalElkGraph.g:4387:1: rule__QualifiedId__Group__1 : rule__QualifiedId__Group__1__Impl ;
    public final void rule__QualifiedId__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4391:1: ( rule__QualifiedId__Group__1__Impl )
            // InternalElkGraph.g:4392:2: rule__QualifiedId__Group__1__Impl
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
    // InternalElkGraph.g:4398:1: rule__QualifiedId__Group__1__Impl : ( ( rule__QualifiedId__Group_1__0 )* ) ;
    public final void rule__QualifiedId__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4402:1: ( ( ( rule__QualifiedId__Group_1__0 )* ) )
            // InternalElkGraph.g:4403:1: ( ( rule__QualifiedId__Group_1__0 )* )
            {
            // InternalElkGraph.g:4403:1: ( ( rule__QualifiedId__Group_1__0 )* )
            // InternalElkGraph.g:4404:2: ( rule__QualifiedId__Group_1__0 )*
            {
             before(grammarAccess.getQualifiedIdAccess().getGroup_1()); 
            // InternalElkGraph.g:4405:2: ( rule__QualifiedId__Group_1__0 )*
            loop40:
            do {
                int alt40=2;
                int LA40_0 = input.LA(1);

                if ( (LA40_0==37) ) {
                    alt40=1;
                }


                switch (alt40) {
            	case 1 :
            	    // InternalElkGraph.g:4405:3: rule__QualifiedId__Group_1__0
            	    {
            	    pushFollow(FOLLOW_29);
            	    rule__QualifiedId__Group_1__0();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop40;
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
    // InternalElkGraph.g:4414:1: rule__QualifiedId__Group_1__0 : rule__QualifiedId__Group_1__0__Impl rule__QualifiedId__Group_1__1 ;
    public final void rule__QualifiedId__Group_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4418:1: ( rule__QualifiedId__Group_1__0__Impl rule__QualifiedId__Group_1__1 )
            // InternalElkGraph.g:4419:2: rule__QualifiedId__Group_1__0__Impl rule__QualifiedId__Group_1__1
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
    // InternalElkGraph.g:4426:1: rule__QualifiedId__Group_1__0__Impl : ( '.' ) ;
    public final void rule__QualifiedId__Group_1__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4430:1: ( ( '.' ) )
            // InternalElkGraph.g:4431:1: ( '.' )
            {
            // InternalElkGraph.g:4431:1: ( '.' )
            // InternalElkGraph.g:4432:2: '.'
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
    // InternalElkGraph.g:4441:1: rule__QualifiedId__Group_1__1 : rule__QualifiedId__Group_1__1__Impl ;
    public final void rule__QualifiedId__Group_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4445:1: ( rule__QualifiedId__Group_1__1__Impl )
            // InternalElkGraph.g:4446:2: rule__QualifiedId__Group_1__1__Impl
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
    // InternalElkGraph.g:4452:1: rule__QualifiedId__Group_1__1__Impl : ( RULE_ID ) ;
    public final void rule__QualifiedId__Group_1__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4456:1: ( ( RULE_ID ) )
            // InternalElkGraph.g:4457:1: ( RULE_ID )
            {
            // InternalElkGraph.g:4457:1: ( RULE_ID )
            // InternalElkGraph.g:4458:2: RULE_ID
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


    // $ANTLR start "rule__Property__Group__0"
    // InternalElkGraph.g:4468:1: rule__Property__Group__0 : rule__Property__Group__0__Impl rule__Property__Group__1 ;
    public final void rule__Property__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4472:1: ( rule__Property__Group__0__Impl rule__Property__Group__1 )
            // InternalElkGraph.g:4473:2: rule__Property__Group__0__Impl rule__Property__Group__1
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
    // InternalElkGraph.g:4480:1: rule__Property__Group__0__Impl : ( ( rule__Property__KeyAssignment_0 ) ) ;
    public final void rule__Property__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4484:1: ( ( ( rule__Property__KeyAssignment_0 ) ) )
            // InternalElkGraph.g:4485:1: ( ( rule__Property__KeyAssignment_0 ) )
            {
            // InternalElkGraph.g:4485:1: ( ( rule__Property__KeyAssignment_0 ) )
            // InternalElkGraph.g:4486:2: ( rule__Property__KeyAssignment_0 )
            {
             before(grammarAccess.getPropertyAccess().getKeyAssignment_0()); 
            // InternalElkGraph.g:4487:2: ( rule__Property__KeyAssignment_0 )
            // InternalElkGraph.g:4487:3: rule__Property__KeyAssignment_0
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
    // InternalElkGraph.g:4495:1: rule__Property__Group__1 : rule__Property__Group__1__Impl rule__Property__Group__2 ;
    public final void rule__Property__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4499:1: ( rule__Property__Group__1__Impl rule__Property__Group__2 )
            // InternalElkGraph.g:4500:2: rule__Property__Group__1__Impl rule__Property__Group__2
            {
            pushFollow(FOLLOW_30);
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
    // InternalElkGraph.g:4507:1: rule__Property__Group__1__Impl : ( ':' ) ;
    public final void rule__Property__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4511:1: ( ( ':' ) )
            // InternalElkGraph.g:4512:1: ( ':' )
            {
            // InternalElkGraph.g:4512:1: ( ':' )
            // InternalElkGraph.g:4513:2: ':'
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
    // InternalElkGraph.g:4522:1: rule__Property__Group__2 : rule__Property__Group__2__Impl ;
    public final void rule__Property__Group__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4526:1: ( rule__Property__Group__2__Impl )
            // InternalElkGraph.g:4527:2: rule__Property__Group__2__Impl
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
    // InternalElkGraph.g:4533:1: rule__Property__Group__2__Impl : ( ( rule__Property__Alternatives_2 ) ) ;
    public final void rule__Property__Group__2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4537:1: ( ( ( rule__Property__Alternatives_2 ) ) )
            // InternalElkGraph.g:4538:1: ( ( rule__Property__Alternatives_2 ) )
            {
            // InternalElkGraph.g:4538:1: ( ( rule__Property__Alternatives_2 ) )
            // InternalElkGraph.g:4539:2: ( rule__Property__Alternatives_2 )
            {
             before(grammarAccess.getPropertyAccess().getAlternatives_2()); 
            // InternalElkGraph.g:4540:2: ( rule__Property__Alternatives_2 )
            // InternalElkGraph.g:4540:3: rule__Property__Alternatives_2
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


    // $ANTLR start "rule__PropertyKey__Group__0"
    // InternalElkGraph.g:4549:1: rule__PropertyKey__Group__0 : rule__PropertyKey__Group__0__Impl rule__PropertyKey__Group__1 ;
    public final void rule__PropertyKey__Group__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4553:1: ( rule__PropertyKey__Group__0__Impl rule__PropertyKey__Group__1 )
            // InternalElkGraph.g:4554:2: rule__PropertyKey__Group__0__Impl rule__PropertyKey__Group__1
            {
            pushFollow(FOLLOW_28);
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
    // InternalElkGraph.g:4561:1: rule__PropertyKey__Group__0__Impl : ( RULE_ID ) ;
    public final void rule__PropertyKey__Group__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4565:1: ( ( RULE_ID ) )
            // InternalElkGraph.g:4566:1: ( RULE_ID )
            {
            // InternalElkGraph.g:4566:1: ( RULE_ID )
            // InternalElkGraph.g:4567:2: RULE_ID
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
    // InternalElkGraph.g:4576:1: rule__PropertyKey__Group__1 : rule__PropertyKey__Group__1__Impl ;
    public final void rule__PropertyKey__Group__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4580:1: ( rule__PropertyKey__Group__1__Impl )
            // InternalElkGraph.g:4581:2: rule__PropertyKey__Group__1__Impl
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
    // InternalElkGraph.g:4587:1: rule__PropertyKey__Group__1__Impl : ( ( rule__PropertyKey__Group_1__0 )* ) ;
    public final void rule__PropertyKey__Group__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4591:1: ( ( ( rule__PropertyKey__Group_1__0 )* ) )
            // InternalElkGraph.g:4592:1: ( ( rule__PropertyKey__Group_1__0 )* )
            {
            // InternalElkGraph.g:4592:1: ( ( rule__PropertyKey__Group_1__0 )* )
            // InternalElkGraph.g:4593:2: ( rule__PropertyKey__Group_1__0 )*
            {
             before(grammarAccess.getPropertyKeyAccess().getGroup_1()); 
            // InternalElkGraph.g:4594:2: ( rule__PropertyKey__Group_1__0 )*
            loop41:
            do {
                int alt41=2;
                int LA41_0 = input.LA(1);

                if ( (LA41_0==37) ) {
                    alt41=1;
                }


                switch (alt41) {
            	case 1 :
            	    // InternalElkGraph.g:4594:3: rule__PropertyKey__Group_1__0
            	    {
            	    pushFollow(FOLLOW_29);
            	    rule__PropertyKey__Group_1__0();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop41;
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
    // InternalElkGraph.g:4603:1: rule__PropertyKey__Group_1__0 : rule__PropertyKey__Group_1__0__Impl rule__PropertyKey__Group_1__1 ;
    public final void rule__PropertyKey__Group_1__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4607:1: ( rule__PropertyKey__Group_1__0__Impl rule__PropertyKey__Group_1__1 )
            // InternalElkGraph.g:4608:2: rule__PropertyKey__Group_1__0__Impl rule__PropertyKey__Group_1__1
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
    // InternalElkGraph.g:4615:1: rule__PropertyKey__Group_1__0__Impl : ( '.' ) ;
    public final void rule__PropertyKey__Group_1__0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4619:1: ( ( '.' ) )
            // InternalElkGraph.g:4620:1: ( '.' )
            {
            // InternalElkGraph.g:4620:1: ( '.' )
            // InternalElkGraph.g:4621:2: '.'
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
    // InternalElkGraph.g:4630:1: rule__PropertyKey__Group_1__1 : rule__PropertyKey__Group_1__1__Impl ;
    public final void rule__PropertyKey__Group_1__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4634:1: ( rule__PropertyKey__Group_1__1__Impl )
            // InternalElkGraph.g:4635:2: rule__PropertyKey__Group_1__1__Impl
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
    // InternalElkGraph.g:4641:1: rule__PropertyKey__Group_1__1__Impl : ( RULE_ID ) ;
    public final void rule__PropertyKey__Group_1__1__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4645:1: ( ( RULE_ID ) )
            // InternalElkGraph.g:4646:1: ( RULE_ID )
            {
            // InternalElkGraph.g:4646:1: ( RULE_ID )
            // InternalElkGraph.g:4647:2: RULE_ID
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
    // InternalElkGraph.g:4657:1: rule__ShapeLayout__UnorderedGroup_2 : ( rule__ShapeLayout__UnorderedGroup_2__0 )? ;
    public final void rule__ShapeLayout__UnorderedGroup_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        		getUnorderedGroupHelper().enter(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2());
        	
        try {
            // InternalElkGraph.g:4662:1: ( ( rule__ShapeLayout__UnorderedGroup_2__0 )? )
            // InternalElkGraph.g:4663:2: ( rule__ShapeLayout__UnorderedGroup_2__0 )?
            {
            // InternalElkGraph.g:4663:2: ( rule__ShapeLayout__UnorderedGroup_2__0 )?
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( LA42_0 == 25 && getUnorderedGroupHelper().canSelect(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2(), 0) ) {
                alt42=1;
            }
            else if ( LA42_0 == 27 && getUnorderedGroupHelper().canSelect(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2(), 1) ) {
                alt42=1;
            }
            switch (alt42) {
                case 1 :
                    // InternalElkGraph.g:4663:2: rule__ShapeLayout__UnorderedGroup_2__0
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
    // InternalElkGraph.g:4671:1: rule__ShapeLayout__UnorderedGroup_2__Impl : ( ({...}? => ( ( ( rule__ShapeLayout__Group_2_0__0 ) ) ) ) | ({...}? => ( ( ( rule__ShapeLayout__Group_2_1__0 ) ) ) ) ) ;
    public final void rule__ShapeLayout__UnorderedGroup_2__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        		boolean selected = false;
        	
        try {
            // InternalElkGraph.g:4676:1: ( ( ({...}? => ( ( ( rule__ShapeLayout__Group_2_0__0 ) ) ) ) | ({...}? => ( ( ( rule__ShapeLayout__Group_2_1__0 ) ) ) ) ) )
            // InternalElkGraph.g:4677:3: ( ({...}? => ( ( ( rule__ShapeLayout__Group_2_0__0 ) ) ) ) | ({...}? => ( ( ( rule__ShapeLayout__Group_2_1__0 ) ) ) ) )
            {
            // InternalElkGraph.g:4677:3: ( ({...}? => ( ( ( rule__ShapeLayout__Group_2_0__0 ) ) ) ) | ({...}? => ( ( ( rule__ShapeLayout__Group_2_1__0 ) ) ) ) )
            int alt43=2;
            int LA43_0 = input.LA(1);

            if ( LA43_0 == 25 && getUnorderedGroupHelper().canSelect(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2(), 0) ) {
                alt43=1;
            }
            else if ( LA43_0 == 27 && getUnorderedGroupHelper().canSelect(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2(), 1) ) {
                alt43=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 43, 0, input);

                throw nvae;
            }
            switch (alt43) {
                case 1 :
                    // InternalElkGraph.g:4678:3: ({...}? => ( ( ( rule__ShapeLayout__Group_2_0__0 ) ) ) )
                    {
                    // InternalElkGraph.g:4678:3: ({...}? => ( ( ( rule__ShapeLayout__Group_2_0__0 ) ) ) )
                    // InternalElkGraph.g:4679:4: {...}? => ( ( ( rule__ShapeLayout__Group_2_0__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2(), 0) ) {
                        throw new FailedPredicateException(input, "rule__ShapeLayout__UnorderedGroup_2__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2(), 0)");
                    }
                    // InternalElkGraph.g:4679:107: ( ( ( rule__ShapeLayout__Group_2_0__0 ) ) )
                    // InternalElkGraph.g:4680:5: ( ( rule__ShapeLayout__Group_2_0__0 ) )
                    {

                    					getUnorderedGroupHelper().select(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2(), 0);
                    				

                    					selected = true;
                    				
                    // InternalElkGraph.g:4686:5: ( ( rule__ShapeLayout__Group_2_0__0 ) )
                    // InternalElkGraph.g:4687:6: ( rule__ShapeLayout__Group_2_0__0 )
                    {
                     before(grammarAccess.getShapeLayoutAccess().getGroup_2_0()); 
                    // InternalElkGraph.g:4688:6: ( rule__ShapeLayout__Group_2_0__0 )
                    // InternalElkGraph.g:4688:7: rule__ShapeLayout__Group_2_0__0
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
                    // InternalElkGraph.g:4693:3: ({...}? => ( ( ( rule__ShapeLayout__Group_2_1__0 ) ) ) )
                    {
                    // InternalElkGraph.g:4693:3: ({...}? => ( ( ( rule__ShapeLayout__Group_2_1__0 ) ) ) )
                    // InternalElkGraph.g:4694:4: {...}? => ( ( ( rule__ShapeLayout__Group_2_1__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2(), 1) ) {
                        throw new FailedPredicateException(input, "rule__ShapeLayout__UnorderedGroup_2__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2(), 1)");
                    }
                    // InternalElkGraph.g:4694:107: ( ( ( rule__ShapeLayout__Group_2_1__0 ) ) )
                    // InternalElkGraph.g:4695:5: ( ( rule__ShapeLayout__Group_2_1__0 ) )
                    {

                    					getUnorderedGroupHelper().select(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2(), 1);
                    				

                    					selected = true;
                    				
                    // InternalElkGraph.g:4701:5: ( ( rule__ShapeLayout__Group_2_1__0 ) )
                    // InternalElkGraph.g:4702:6: ( rule__ShapeLayout__Group_2_1__0 )
                    {
                     before(grammarAccess.getShapeLayoutAccess().getGroup_2_1()); 
                    // InternalElkGraph.g:4703:6: ( rule__ShapeLayout__Group_2_1__0 )
                    // InternalElkGraph.g:4703:7: rule__ShapeLayout__Group_2_1__0
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
    // InternalElkGraph.g:4716:1: rule__ShapeLayout__UnorderedGroup_2__0 : rule__ShapeLayout__UnorderedGroup_2__Impl ( rule__ShapeLayout__UnorderedGroup_2__1 )? ;
    public final void rule__ShapeLayout__UnorderedGroup_2__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4720:1: ( rule__ShapeLayout__UnorderedGroup_2__Impl ( rule__ShapeLayout__UnorderedGroup_2__1 )? )
            // InternalElkGraph.g:4721:2: rule__ShapeLayout__UnorderedGroup_2__Impl ( rule__ShapeLayout__UnorderedGroup_2__1 )?
            {
            pushFollow(FOLLOW_31);
            rule__ShapeLayout__UnorderedGroup_2__Impl();

            state._fsp--;

            // InternalElkGraph.g:4722:2: ( rule__ShapeLayout__UnorderedGroup_2__1 )?
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( LA44_0 == 25 && getUnorderedGroupHelper().canSelect(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2(), 0) ) {
                alt44=1;
            }
            else if ( LA44_0 == 27 && getUnorderedGroupHelper().canSelect(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2(), 1) ) {
                alt44=1;
            }
            switch (alt44) {
                case 1 :
                    // InternalElkGraph.g:4722:2: rule__ShapeLayout__UnorderedGroup_2__1
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
    // InternalElkGraph.g:4728:1: rule__ShapeLayout__UnorderedGroup_2__1 : rule__ShapeLayout__UnorderedGroup_2__Impl ;
    public final void rule__ShapeLayout__UnorderedGroup_2__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4732:1: ( rule__ShapeLayout__UnorderedGroup_2__Impl )
            // InternalElkGraph.g:4733:2: rule__ShapeLayout__UnorderedGroup_2__Impl
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


    // $ANTLR start "rule__ElkSingleEdgeSection__UnorderedGroup_1_0"
    // InternalElkGraph.g:4740:1: rule__ElkSingleEdgeSection__UnorderedGroup_1_0 : ( rule__ElkSingleEdgeSection__UnorderedGroup_1_0__0 )? ;
    public final void rule__ElkSingleEdgeSection__UnorderedGroup_1_0() throws RecognitionException {

        		int stackSize = keepStackSize();
        		getUnorderedGroupHelper().enter(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0());
        	
        try {
            // InternalElkGraph.g:4745:1: ( ( rule__ElkSingleEdgeSection__UnorderedGroup_1_0__0 )? )
            // InternalElkGraph.g:4746:2: ( rule__ElkSingleEdgeSection__UnorderedGroup_1_0__0 )?
            {
            // InternalElkGraph.g:4746:2: ( rule__ElkSingleEdgeSection__UnorderedGroup_1_0__0 )?
            int alt45=2;
            int LA45_0 = input.LA(1);

            if ( LA45_0 == 30 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 0) ) {
                alt45=1;
            }
            else if ( LA45_0 == 31 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 1) ) {
                alt45=1;
            }
            else if ( LA45_0 == 32 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 2) ) {
                alt45=1;
            }
            else if ( LA45_0 == 33 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 3) ) {
                alt45=1;
            }
            switch (alt45) {
                case 1 :
                    // InternalElkGraph.g:4746:2: rule__ElkSingleEdgeSection__UnorderedGroup_1_0__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkSingleEdgeSection__UnorderedGroup_1_0__0();

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

            	getUnorderedGroupHelper().leave(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0());
            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__UnorderedGroup_1_0"


    // $ANTLR start "rule__ElkSingleEdgeSection__UnorderedGroup_1_0__Impl"
    // InternalElkGraph.g:4754:1: rule__ElkSingleEdgeSection__UnorderedGroup_1_0__Impl : ( ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0_0__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0_1__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0_2__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0_3__0 ) ) ) ) ) ;
    public final void rule__ElkSingleEdgeSection__UnorderedGroup_1_0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        		boolean selected = false;
        	
        try {
            // InternalElkGraph.g:4759:1: ( ( ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0_0__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0_1__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0_2__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0_3__0 ) ) ) ) ) )
            // InternalElkGraph.g:4760:3: ( ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0_0__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0_1__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0_2__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0_3__0 ) ) ) ) )
            {
            // InternalElkGraph.g:4760:3: ( ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0_0__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0_1__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0_2__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0_3__0 ) ) ) ) )
            int alt46=4;
            int LA46_0 = input.LA(1);

            if ( LA46_0 == 30 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 0) ) {
                alt46=1;
            }
            else if ( LA46_0 == 31 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 1) ) {
                alt46=2;
            }
            else if ( LA46_0 == 32 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 2) ) {
                alt46=3;
            }
            else if ( LA46_0 == 33 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 3) ) {
                alt46=4;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 46, 0, input);

                throw nvae;
            }
            switch (alt46) {
                case 1 :
                    // InternalElkGraph.g:4761:3: ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0_0__0 ) ) ) )
                    {
                    // InternalElkGraph.g:4761:3: ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0_0__0 ) ) ) )
                    // InternalElkGraph.g:4762:4: {...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0_0__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 0) ) {
                        throw new FailedPredicateException(input, "rule__ElkSingleEdgeSection__UnorderedGroup_1_0__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 0)");
                    }
                    // InternalElkGraph.g:4762:118: ( ( ( rule__ElkSingleEdgeSection__Group_1_0_0__0 ) ) )
                    // InternalElkGraph.g:4763:5: ( ( rule__ElkSingleEdgeSection__Group_1_0_0__0 ) )
                    {

                    					getUnorderedGroupHelper().select(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 0);
                    				

                    					selected = true;
                    				
                    // InternalElkGraph.g:4769:5: ( ( rule__ElkSingleEdgeSection__Group_1_0_0__0 ) )
                    // InternalElkGraph.g:4770:6: ( rule__ElkSingleEdgeSection__Group_1_0_0__0 )
                    {
                     before(grammarAccess.getElkSingleEdgeSectionAccess().getGroup_1_0_0()); 
                    // InternalElkGraph.g:4771:6: ( rule__ElkSingleEdgeSection__Group_1_0_0__0 )
                    // InternalElkGraph.g:4771:7: rule__ElkSingleEdgeSection__Group_1_0_0__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkSingleEdgeSection__Group_1_0_0__0();

                    state._fsp--;


                    }

                     after(grammarAccess.getElkSingleEdgeSectionAccess().getGroup_1_0_0()); 

                    }


                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalElkGraph.g:4776:3: ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0_1__0 ) ) ) )
                    {
                    // InternalElkGraph.g:4776:3: ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0_1__0 ) ) ) )
                    // InternalElkGraph.g:4777:4: {...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0_1__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 1) ) {
                        throw new FailedPredicateException(input, "rule__ElkSingleEdgeSection__UnorderedGroup_1_0__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 1)");
                    }
                    // InternalElkGraph.g:4777:118: ( ( ( rule__ElkSingleEdgeSection__Group_1_0_1__0 ) ) )
                    // InternalElkGraph.g:4778:5: ( ( rule__ElkSingleEdgeSection__Group_1_0_1__0 ) )
                    {

                    					getUnorderedGroupHelper().select(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 1);
                    				

                    					selected = true;
                    				
                    // InternalElkGraph.g:4784:5: ( ( rule__ElkSingleEdgeSection__Group_1_0_1__0 ) )
                    // InternalElkGraph.g:4785:6: ( rule__ElkSingleEdgeSection__Group_1_0_1__0 )
                    {
                     before(grammarAccess.getElkSingleEdgeSectionAccess().getGroup_1_0_1()); 
                    // InternalElkGraph.g:4786:6: ( rule__ElkSingleEdgeSection__Group_1_0_1__0 )
                    // InternalElkGraph.g:4786:7: rule__ElkSingleEdgeSection__Group_1_0_1__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkSingleEdgeSection__Group_1_0_1__0();

                    state._fsp--;


                    }

                     after(grammarAccess.getElkSingleEdgeSectionAccess().getGroup_1_0_1()); 

                    }


                    }


                    }


                    }
                    break;
                case 3 :
                    // InternalElkGraph.g:4791:3: ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0_2__0 ) ) ) )
                    {
                    // InternalElkGraph.g:4791:3: ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0_2__0 ) ) ) )
                    // InternalElkGraph.g:4792:4: {...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0_2__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 2) ) {
                        throw new FailedPredicateException(input, "rule__ElkSingleEdgeSection__UnorderedGroup_1_0__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 2)");
                    }
                    // InternalElkGraph.g:4792:118: ( ( ( rule__ElkSingleEdgeSection__Group_1_0_2__0 ) ) )
                    // InternalElkGraph.g:4793:5: ( ( rule__ElkSingleEdgeSection__Group_1_0_2__0 ) )
                    {

                    					getUnorderedGroupHelper().select(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 2);
                    				

                    					selected = true;
                    				
                    // InternalElkGraph.g:4799:5: ( ( rule__ElkSingleEdgeSection__Group_1_0_2__0 ) )
                    // InternalElkGraph.g:4800:6: ( rule__ElkSingleEdgeSection__Group_1_0_2__0 )
                    {
                     before(grammarAccess.getElkSingleEdgeSectionAccess().getGroup_1_0_2()); 
                    // InternalElkGraph.g:4801:6: ( rule__ElkSingleEdgeSection__Group_1_0_2__0 )
                    // InternalElkGraph.g:4801:7: rule__ElkSingleEdgeSection__Group_1_0_2__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkSingleEdgeSection__Group_1_0_2__0();

                    state._fsp--;


                    }

                     after(grammarAccess.getElkSingleEdgeSectionAccess().getGroup_1_0_2()); 

                    }


                    }


                    }


                    }
                    break;
                case 4 :
                    // InternalElkGraph.g:4806:3: ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0_3__0 ) ) ) )
                    {
                    // InternalElkGraph.g:4806:3: ({...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0_3__0 ) ) ) )
                    // InternalElkGraph.g:4807:4: {...}? => ( ( ( rule__ElkSingleEdgeSection__Group_1_0_3__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 3) ) {
                        throw new FailedPredicateException(input, "rule__ElkSingleEdgeSection__UnorderedGroup_1_0__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 3)");
                    }
                    // InternalElkGraph.g:4807:118: ( ( ( rule__ElkSingleEdgeSection__Group_1_0_3__0 ) ) )
                    // InternalElkGraph.g:4808:5: ( ( rule__ElkSingleEdgeSection__Group_1_0_3__0 ) )
                    {

                    					getUnorderedGroupHelper().select(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 3);
                    				

                    					selected = true;
                    				
                    // InternalElkGraph.g:4814:5: ( ( rule__ElkSingleEdgeSection__Group_1_0_3__0 ) )
                    // InternalElkGraph.g:4815:6: ( rule__ElkSingleEdgeSection__Group_1_0_3__0 )
                    {
                     before(grammarAccess.getElkSingleEdgeSectionAccess().getGroup_1_0_3()); 
                    // InternalElkGraph.g:4816:6: ( rule__ElkSingleEdgeSection__Group_1_0_3__0 )
                    // InternalElkGraph.g:4816:7: rule__ElkSingleEdgeSection__Group_1_0_3__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkSingleEdgeSection__Group_1_0_3__0();

                    state._fsp--;


                    }

                     after(grammarAccess.getElkSingleEdgeSectionAccess().getGroup_1_0_3()); 

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
            		getUnorderedGroupHelper().returnFromSelection(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0());
            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkSingleEdgeSection__UnorderedGroup_1_0__Impl"


    // $ANTLR start "rule__ElkSingleEdgeSection__UnorderedGroup_1_0__0"
    // InternalElkGraph.g:4829:1: rule__ElkSingleEdgeSection__UnorderedGroup_1_0__0 : rule__ElkSingleEdgeSection__UnorderedGroup_1_0__Impl ( rule__ElkSingleEdgeSection__UnorderedGroup_1_0__1 )? ;
    public final void rule__ElkSingleEdgeSection__UnorderedGroup_1_0__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4833:1: ( rule__ElkSingleEdgeSection__UnorderedGroup_1_0__Impl ( rule__ElkSingleEdgeSection__UnorderedGroup_1_0__1 )? )
            // InternalElkGraph.g:4834:2: rule__ElkSingleEdgeSection__UnorderedGroup_1_0__Impl ( rule__ElkSingleEdgeSection__UnorderedGroup_1_0__1 )?
            {
            pushFollow(FOLLOW_32);
            rule__ElkSingleEdgeSection__UnorderedGroup_1_0__Impl();

            state._fsp--;

            // InternalElkGraph.g:4835:2: ( rule__ElkSingleEdgeSection__UnorderedGroup_1_0__1 )?
            int alt47=2;
            int LA47_0 = input.LA(1);

            if ( LA47_0 == 30 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 0) ) {
                alt47=1;
            }
            else if ( LA47_0 == 31 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 1) ) {
                alt47=1;
            }
            else if ( LA47_0 == 32 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 2) ) {
                alt47=1;
            }
            else if ( LA47_0 == 33 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 3) ) {
                alt47=1;
            }
            switch (alt47) {
                case 1 :
                    // InternalElkGraph.g:4835:2: rule__ElkSingleEdgeSection__UnorderedGroup_1_0__1
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkSingleEdgeSection__UnorderedGroup_1_0__1();

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
    // $ANTLR end "rule__ElkSingleEdgeSection__UnorderedGroup_1_0__0"


    // $ANTLR start "rule__ElkSingleEdgeSection__UnorderedGroup_1_0__1"
    // InternalElkGraph.g:4841:1: rule__ElkSingleEdgeSection__UnorderedGroup_1_0__1 : rule__ElkSingleEdgeSection__UnorderedGroup_1_0__Impl ( rule__ElkSingleEdgeSection__UnorderedGroup_1_0__2 )? ;
    public final void rule__ElkSingleEdgeSection__UnorderedGroup_1_0__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4845:1: ( rule__ElkSingleEdgeSection__UnorderedGroup_1_0__Impl ( rule__ElkSingleEdgeSection__UnorderedGroup_1_0__2 )? )
            // InternalElkGraph.g:4846:2: rule__ElkSingleEdgeSection__UnorderedGroup_1_0__Impl ( rule__ElkSingleEdgeSection__UnorderedGroup_1_0__2 )?
            {
            pushFollow(FOLLOW_32);
            rule__ElkSingleEdgeSection__UnorderedGroup_1_0__Impl();

            state._fsp--;

            // InternalElkGraph.g:4847:2: ( rule__ElkSingleEdgeSection__UnorderedGroup_1_0__2 )?
            int alt48=2;
            int LA48_0 = input.LA(1);

            if ( LA48_0 == 30 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 0) ) {
                alt48=1;
            }
            else if ( LA48_0 == 31 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 1) ) {
                alt48=1;
            }
            else if ( LA48_0 == 32 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 2) ) {
                alt48=1;
            }
            else if ( LA48_0 == 33 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 3) ) {
                alt48=1;
            }
            switch (alt48) {
                case 1 :
                    // InternalElkGraph.g:4847:2: rule__ElkSingleEdgeSection__UnorderedGroup_1_0__2
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkSingleEdgeSection__UnorderedGroup_1_0__2();

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
    // $ANTLR end "rule__ElkSingleEdgeSection__UnorderedGroup_1_0__1"


    // $ANTLR start "rule__ElkSingleEdgeSection__UnorderedGroup_1_0__2"
    // InternalElkGraph.g:4853:1: rule__ElkSingleEdgeSection__UnorderedGroup_1_0__2 : rule__ElkSingleEdgeSection__UnorderedGroup_1_0__Impl ( rule__ElkSingleEdgeSection__UnorderedGroup_1_0__3 )? ;
    public final void rule__ElkSingleEdgeSection__UnorderedGroup_1_0__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4857:1: ( rule__ElkSingleEdgeSection__UnorderedGroup_1_0__Impl ( rule__ElkSingleEdgeSection__UnorderedGroup_1_0__3 )? )
            // InternalElkGraph.g:4858:2: rule__ElkSingleEdgeSection__UnorderedGroup_1_0__Impl ( rule__ElkSingleEdgeSection__UnorderedGroup_1_0__3 )?
            {
            pushFollow(FOLLOW_32);
            rule__ElkSingleEdgeSection__UnorderedGroup_1_0__Impl();

            state._fsp--;

            // InternalElkGraph.g:4859:2: ( rule__ElkSingleEdgeSection__UnorderedGroup_1_0__3 )?
            int alt49=2;
            int LA49_0 = input.LA(1);

            if ( LA49_0 == 30 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 0) ) {
                alt49=1;
            }
            else if ( LA49_0 == 31 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 1) ) {
                alt49=1;
            }
            else if ( LA49_0 == 32 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 2) ) {
                alt49=1;
            }
            else if ( LA49_0 == 33 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 3) ) {
                alt49=1;
            }
            switch (alt49) {
                case 1 :
                    // InternalElkGraph.g:4859:2: rule__ElkSingleEdgeSection__UnorderedGroup_1_0__3
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkSingleEdgeSection__UnorderedGroup_1_0__3();

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
    // $ANTLR end "rule__ElkSingleEdgeSection__UnorderedGroup_1_0__2"


    // $ANTLR start "rule__ElkSingleEdgeSection__UnorderedGroup_1_0__3"
    // InternalElkGraph.g:4865:1: rule__ElkSingleEdgeSection__UnorderedGroup_1_0__3 : rule__ElkSingleEdgeSection__UnorderedGroup_1_0__Impl ;
    public final void rule__ElkSingleEdgeSection__UnorderedGroup_1_0__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4869:1: ( rule__ElkSingleEdgeSection__UnorderedGroup_1_0__Impl )
            // InternalElkGraph.g:4870:2: rule__ElkSingleEdgeSection__UnorderedGroup_1_0__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkSingleEdgeSection__UnorderedGroup_1_0__Impl();

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
    // $ANTLR end "rule__ElkSingleEdgeSection__UnorderedGroup_1_0__3"


    // $ANTLR start "rule__ElkEdgeSection__UnorderedGroup_4_0"
    // InternalElkGraph.g:4877:1: rule__ElkEdgeSection__UnorderedGroup_4_0 : ( rule__ElkEdgeSection__UnorderedGroup_4_0__0 )? ;
    public final void rule__ElkEdgeSection__UnorderedGroup_4_0() throws RecognitionException {

        		int stackSize = keepStackSize();
        		getUnorderedGroupHelper().enter(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0());
        	
        try {
            // InternalElkGraph.g:4882:1: ( ( rule__ElkEdgeSection__UnorderedGroup_4_0__0 )? )
            // InternalElkGraph.g:4883:2: ( rule__ElkEdgeSection__UnorderedGroup_4_0__0 )?
            {
            // InternalElkGraph.g:4883:2: ( rule__ElkEdgeSection__UnorderedGroup_4_0__0 )?
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( LA50_0 == 30 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 0) ) {
                alt50=1;
            }
            else if ( LA50_0 == 31 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 1) ) {
                alt50=1;
            }
            else if ( LA50_0 == 32 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 2) ) {
                alt50=1;
            }
            else if ( LA50_0 == 33 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 3) ) {
                alt50=1;
            }
            switch (alt50) {
                case 1 :
                    // InternalElkGraph.g:4883:2: rule__ElkEdgeSection__UnorderedGroup_4_0__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkEdgeSection__UnorderedGroup_4_0__0();

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

            	getUnorderedGroupHelper().leave(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0());
            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__UnorderedGroup_4_0"


    // $ANTLR start "rule__ElkEdgeSection__UnorderedGroup_4_0__Impl"
    // InternalElkGraph.g:4891:1: rule__ElkEdgeSection__UnorderedGroup_4_0__Impl : ( ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_0_0__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_0_1__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_0_2__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_0_3__0 ) ) ) ) ) ;
    public final void rule__ElkEdgeSection__UnorderedGroup_4_0__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        		boolean selected = false;
        	
        try {
            // InternalElkGraph.g:4896:1: ( ( ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_0_0__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_0_1__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_0_2__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_0_3__0 ) ) ) ) ) )
            // InternalElkGraph.g:4897:3: ( ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_0_0__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_0_1__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_0_2__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_0_3__0 ) ) ) ) )
            {
            // InternalElkGraph.g:4897:3: ( ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_0_0__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_0_1__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_0_2__0 ) ) ) ) | ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_0_3__0 ) ) ) ) )
            int alt51=4;
            int LA51_0 = input.LA(1);

            if ( LA51_0 == 30 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 0) ) {
                alt51=1;
            }
            else if ( LA51_0 == 31 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 1) ) {
                alt51=2;
            }
            else if ( LA51_0 == 32 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 2) ) {
                alt51=3;
            }
            else if ( LA51_0 == 33 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 3) ) {
                alt51=4;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 51, 0, input);

                throw nvae;
            }
            switch (alt51) {
                case 1 :
                    // InternalElkGraph.g:4898:3: ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_0_0__0 ) ) ) )
                    {
                    // InternalElkGraph.g:4898:3: ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_0_0__0 ) ) ) )
                    // InternalElkGraph.g:4899:4: {...}? => ( ( ( rule__ElkEdgeSection__Group_4_0_0__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 0) ) {
                        throw new FailedPredicateException(input, "rule__ElkEdgeSection__UnorderedGroup_4_0__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 0)");
                    }
                    // InternalElkGraph.g:4899:112: ( ( ( rule__ElkEdgeSection__Group_4_0_0__0 ) ) )
                    // InternalElkGraph.g:4900:5: ( ( rule__ElkEdgeSection__Group_4_0_0__0 ) )
                    {

                    					getUnorderedGroupHelper().select(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 0);
                    				

                    					selected = true;
                    				
                    // InternalElkGraph.g:4906:5: ( ( rule__ElkEdgeSection__Group_4_0_0__0 ) )
                    // InternalElkGraph.g:4907:6: ( rule__ElkEdgeSection__Group_4_0_0__0 )
                    {
                     before(grammarAccess.getElkEdgeSectionAccess().getGroup_4_0_0()); 
                    // InternalElkGraph.g:4908:6: ( rule__ElkEdgeSection__Group_4_0_0__0 )
                    // InternalElkGraph.g:4908:7: rule__ElkEdgeSection__Group_4_0_0__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkEdgeSection__Group_4_0_0__0();

                    state._fsp--;


                    }

                     after(grammarAccess.getElkEdgeSectionAccess().getGroup_4_0_0()); 

                    }


                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalElkGraph.g:4913:3: ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_0_1__0 ) ) ) )
                    {
                    // InternalElkGraph.g:4913:3: ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_0_1__0 ) ) ) )
                    // InternalElkGraph.g:4914:4: {...}? => ( ( ( rule__ElkEdgeSection__Group_4_0_1__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 1) ) {
                        throw new FailedPredicateException(input, "rule__ElkEdgeSection__UnorderedGroup_4_0__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 1)");
                    }
                    // InternalElkGraph.g:4914:112: ( ( ( rule__ElkEdgeSection__Group_4_0_1__0 ) ) )
                    // InternalElkGraph.g:4915:5: ( ( rule__ElkEdgeSection__Group_4_0_1__0 ) )
                    {

                    					getUnorderedGroupHelper().select(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 1);
                    				

                    					selected = true;
                    				
                    // InternalElkGraph.g:4921:5: ( ( rule__ElkEdgeSection__Group_4_0_1__0 ) )
                    // InternalElkGraph.g:4922:6: ( rule__ElkEdgeSection__Group_4_0_1__0 )
                    {
                     before(grammarAccess.getElkEdgeSectionAccess().getGroup_4_0_1()); 
                    // InternalElkGraph.g:4923:6: ( rule__ElkEdgeSection__Group_4_0_1__0 )
                    // InternalElkGraph.g:4923:7: rule__ElkEdgeSection__Group_4_0_1__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkEdgeSection__Group_4_0_1__0();

                    state._fsp--;


                    }

                     after(grammarAccess.getElkEdgeSectionAccess().getGroup_4_0_1()); 

                    }


                    }


                    }


                    }
                    break;
                case 3 :
                    // InternalElkGraph.g:4928:3: ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_0_2__0 ) ) ) )
                    {
                    // InternalElkGraph.g:4928:3: ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_0_2__0 ) ) ) )
                    // InternalElkGraph.g:4929:4: {...}? => ( ( ( rule__ElkEdgeSection__Group_4_0_2__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 2) ) {
                        throw new FailedPredicateException(input, "rule__ElkEdgeSection__UnorderedGroup_4_0__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 2)");
                    }
                    // InternalElkGraph.g:4929:112: ( ( ( rule__ElkEdgeSection__Group_4_0_2__0 ) ) )
                    // InternalElkGraph.g:4930:5: ( ( rule__ElkEdgeSection__Group_4_0_2__0 ) )
                    {

                    					getUnorderedGroupHelper().select(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 2);
                    				

                    					selected = true;
                    				
                    // InternalElkGraph.g:4936:5: ( ( rule__ElkEdgeSection__Group_4_0_2__0 ) )
                    // InternalElkGraph.g:4937:6: ( rule__ElkEdgeSection__Group_4_0_2__0 )
                    {
                     before(grammarAccess.getElkEdgeSectionAccess().getGroup_4_0_2()); 
                    // InternalElkGraph.g:4938:6: ( rule__ElkEdgeSection__Group_4_0_2__0 )
                    // InternalElkGraph.g:4938:7: rule__ElkEdgeSection__Group_4_0_2__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkEdgeSection__Group_4_0_2__0();

                    state._fsp--;


                    }

                     after(grammarAccess.getElkEdgeSectionAccess().getGroup_4_0_2()); 

                    }


                    }


                    }


                    }
                    break;
                case 4 :
                    // InternalElkGraph.g:4943:3: ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_0_3__0 ) ) ) )
                    {
                    // InternalElkGraph.g:4943:3: ({...}? => ( ( ( rule__ElkEdgeSection__Group_4_0_3__0 ) ) ) )
                    // InternalElkGraph.g:4944:4: {...}? => ( ( ( rule__ElkEdgeSection__Group_4_0_3__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 3) ) {
                        throw new FailedPredicateException(input, "rule__ElkEdgeSection__UnorderedGroup_4_0__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 3)");
                    }
                    // InternalElkGraph.g:4944:112: ( ( ( rule__ElkEdgeSection__Group_4_0_3__0 ) ) )
                    // InternalElkGraph.g:4945:5: ( ( rule__ElkEdgeSection__Group_4_0_3__0 ) )
                    {

                    					getUnorderedGroupHelper().select(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 3);
                    				

                    					selected = true;
                    				
                    // InternalElkGraph.g:4951:5: ( ( rule__ElkEdgeSection__Group_4_0_3__0 ) )
                    // InternalElkGraph.g:4952:6: ( rule__ElkEdgeSection__Group_4_0_3__0 )
                    {
                     before(grammarAccess.getElkEdgeSectionAccess().getGroup_4_0_3()); 
                    // InternalElkGraph.g:4953:6: ( rule__ElkEdgeSection__Group_4_0_3__0 )
                    // InternalElkGraph.g:4953:7: rule__ElkEdgeSection__Group_4_0_3__0
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkEdgeSection__Group_4_0_3__0();

                    state._fsp--;


                    }

                     after(grammarAccess.getElkEdgeSectionAccess().getGroup_4_0_3()); 

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
            		getUnorderedGroupHelper().returnFromSelection(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0());
            	restoreStackSize(stackSize);

        }
        return ;
    }
    // $ANTLR end "rule__ElkEdgeSection__UnorderedGroup_4_0__Impl"


    // $ANTLR start "rule__ElkEdgeSection__UnorderedGroup_4_0__0"
    // InternalElkGraph.g:4966:1: rule__ElkEdgeSection__UnorderedGroup_4_0__0 : rule__ElkEdgeSection__UnorderedGroup_4_0__Impl ( rule__ElkEdgeSection__UnorderedGroup_4_0__1 )? ;
    public final void rule__ElkEdgeSection__UnorderedGroup_4_0__0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4970:1: ( rule__ElkEdgeSection__UnorderedGroup_4_0__Impl ( rule__ElkEdgeSection__UnorderedGroup_4_0__1 )? )
            // InternalElkGraph.g:4971:2: rule__ElkEdgeSection__UnorderedGroup_4_0__Impl ( rule__ElkEdgeSection__UnorderedGroup_4_0__1 )?
            {
            pushFollow(FOLLOW_32);
            rule__ElkEdgeSection__UnorderedGroup_4_0__Impl();

            state._fsp--;

            // InternalElkGraph.g:4972:2: ( rule__ElkEdgeSection__UnorderedGroup_4_0__1 )?
            int alt52=2;
            int LA52_0 = input.LA(1);

            if ( LA52_0 == 30 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 0) ) {
                alt52=1;
            }
            else if ( LA52_0 == 31 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 1) ) {
                alt52=1;
            }
            else if ( LA52_0 == 32 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 2) ) {
                alt52=1;
            }
            else if ( LA52_0 == 33 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 3) ) {
                alt52=1;
            }
            switch (alt52) {
                case 1 :
                    // InternalElkGraph.g:4972:2: rule__ElkEdgeSection__UnorderedGroup_4_0__1
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkEdgeSection__UnorderedGroup_4_0__1();

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
    // $ANTLR end "rule__ElkEdgeSection__UnorderedGroup_4_0__0"


    // $ANTLR start "rule__ElkEdgeSection__UnorderedGroup_4_0__1"
    // InternalElkGraph.g:4978:1: rule__ElkEdgeSection__UnorderedGroup_4_0__1 : rule__ElkEdgeSection__UnorderedGroup_4_0__Impl ( rule__ElkEdgeSection__UnorderedGroup_4_0__2 )? ;
    public final void rule__ElkEdgeSection__UnorderedGroup_4_0__1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4982:1: ( rule__ElkEdgeSection__UnorderedGroup_4_0__Impl ( rule__ElkEdgeSection__UnorderedGroup_4_0__2 )? )
            // InternalElkGraph.g:4983:2: rule__ElkEdgeSection__UnorderedGroup_4_0__Impl ( rule__ElkEdgeSection__UnorderedGroup_4_0__2 )?
            {
            pushFollow(FOLLOW_32);
            rule__ElkEdgeSection__UnorderedGroup_4_0__Impl();

            state._fsp--;

            // InternalElkGraph.g:4984:2: ( rule__ElkEdgeSection__UnorderedGroup_4_0__2 )?
            int alt53=2;
            int LA53_0 = input.LA(1);

            if ( LA53_0 == 30 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 0) ) {
                alt53=1;
            }
            else if ( LA53_0 == 31 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 1) ) {
                alt53=1;
            }
            else if ( LA53_0 == 32 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 2) ) {
                alt53=1;
            }
            else if ( LA53_0 == 33 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 3) ) {
                alt53=1;
            }
            switch (alt53) {
                case 1 :
                    // InternalElkGraph.g:4984:2: rule__ElkEdgeSection__UnorderedGroup_4_0__2
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkEdgeSection__UnorderedGroup_4_0__2();

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
    // $ANTLR end "rule__ElkEdgeSection__UnorderedGroup_4_0__1"


    // $ANTLR start "rule__ElkEdgeSection__UnorderedGroup_4_0__2"
    // InternalElkGraph.g:4990:1: rule__ElkEdgeSection__UnorderedGroup_4_0__2 : rule__ElkEdgeSection__UnorderedGroup_4_0__Impl ( rule__ElkEdgeSection__UnorderedGroup_4_0__3 )? ;
    public final void rule__ElkEdgeSection__UnorderedGroup_4_0__2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:4994:1: ( rule__ElkEdgeSection__UnorderedGroup_4_0__Impl ( rule__ElkEdgeSection__UnorderedGroup_4_0__3 )? )
            // InternalElkGraph.g:4995:2: rule__ElkEdgeSection__UnorderedGroup_4_0__Impl ( rule__ElkEdgeSection__UnorderedGroup_4_0__3 )?
            {
            pushFollow(FOLLOW_32);
            rule__ElkEdgeSection__UnorderedGroup_4_0__Impl();

            state._fsp--;

            // InternalElkGraph.g:4996:2: ( rule__ElkEdgeSection__UnorderedGroup_4_0__3 )?
            int alt54=2;
            int LA54_0 = input.LA(1);

            if ( LA54_0 == 30 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 0) ) {
                alt54=1;
            }
            else if ( LA54_0 == 31 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 1) ) {
                alt54=1;
            }
            else if ( LA54_0 == 32 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 2) ) {
                alt54=1;
            }
            else if ( LA54_0 == 33 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 3) ) {
                alt54=1;
            }
            switch (alt54) {
                case 1 :
                    // InternalElkGraph.g:4996:2: rule__ElkEdgeSection__UnorderedGroup_4_0__3
                    {
                    pushFollow(FOLLOW_2);
                    rule__ElkEdgeSection__UnorderedGroup_4_0__3();

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
    // $ANTLR end "rule__ElkEdgeSection__UnorderedGroup_4_0__2"


    // $ANTLR start "rule__ElkEdgeSection__UnorderedGroup_4_0__3"
    // InternalElkGraph.g:5002:1: rule__ElkEdgeSection__UnorderedGroup_4_0__3 : rule__ElkEdgeSection__UnorderedGroup_4_0__Impl ;
    public final void rule__ElkEdgeSection__UnorderedGroup_4_0__3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5006:1: ( rule__ElkEdgeSection__UnorderedGroup_4_0__Impl )
            // InternalElkGraph.g:5007:2: rule__ElkEdgeSection__UnorderedGroup_4_0__Impl
            {
            pushFollow(FOLLOW_2);
            rule__ElkEdgeSection__UnorderedGroup_4_0__Impl();

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
    // $ANTLR end "rule__ElkEdgeSection__UnorderedGroup_4_0__3"


    // $ANTLR start "rule__RootNode__IdentifierAssignment_1_1"
    // InternalElkGraph.g:5014:1: rule__RootNode__IdentifierAssignment_1_1 : ( RULE_ID ) ;
    public final void rule__RootNode__IdentifierAssignment_1_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5018:1: ( ( RULE_ID ) )
            // InternalElkGraph.g:5019:2: ( RULE_ID )
            {
            // InternalElkGraph.g:5019:2: ( RULE_ID )
            // InternalElkGraph.g:5020:3: RULE_ID
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
    // InternalElkGraph.g:5029:1: rule__RootNode__PropertiesAssignment_2 : ( ruleProperty ) ;
    public final void rule__RootNode__PropertiesAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5033:1: ( ( ruleProperty ) )
            // InternalElkGraph.g:5034:2: ( ruleProperty )
            {
            // InternalElkGraph.g:5034:2: ( ruleProperty )
            // InternalElkGraph.g:5035:3: ruleProperty
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


    // $ANTLR start "rule__RootNode__LabelsAssignment_3_0"
    // InternalElkGraph.g:5044:1: rule__RootNode__LabelsAssignment_3_0 : ( ruleElkLabel ) ;
    public final void rule__RootNode__LabelsAssignment_3_0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5048:1: ( ( ruleElkLabel ) )
            // InternalElkGraph.g:5049:2: ( ruleElkLabel )
            {
            // InternalElkGraph.g:5049:2: ( ruleElkLabel )
            // InternalElkGraph.g:5050:3: ruleElkLabel
            {
             before(grammarAccess.getRootNodeAccess().getLabelsElkLabelParserRuleCall_3_0_0()); 
            pushFollow(FOLLOW_2);
            ruleElkLabel();

            state._fsp--;

             after(grammarAccess.getRootNodeAccess().getLabelsElkLabelParserRuleCall_3_0_0()); 

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
    // $ANTLR end "rule__RootNode__LabelsAssignment_3_0"


    // $ANTLR start "rule__RootNode__PortsAssignment_3_1"
    // InternalElkGraph.g:5059:1: rule__RootNode__PortsAssignment_3_1 : ( ruleElkPort ) ;
    public final void rule__RootNode__PortsAssignment_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5063:1: ( ( ruleElkPort ) )
            // InternalElkGraph.g:5064:2: ( ruleElkPort )
            {
            // InternalElkGraph.g:5064:2: ( ruleElkPort )
            // InternalElkGraph.g:5065:3: ruleElkPort
            {
             before(grammarAccess.getRootNodeAccess().getPortsElkPortParserRuleCall_3_1_0()); 
            pushFollow(FOLLOW_2);
            ruleElkPort();

            state._fsp--;

             after(grammarAccess.getRootNodeAccess().getPortsElkPortParserRuleCall_3_1_0()); 

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
    // $ANTLR end "rule__RootNode__PortsAssignment_3_1"


    // $ANTLR start "rule__RootNode__ChildrenAssignment_3_2"
    // InternalElkGraph.g:5074:1: rule__RootNode__ChildrenAssignment_3_2 : ( ruleElkNode ) ;
    public final void rule__RootNode__ChildrenAssignment_3_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5078:1: ( ( ruleElkNode ) )
            // InternalElkGraph.g:5079:2: ( ruleElkNode )
            {
            // InternalElkGraph.g:5079:2: ( ruleElkNode )
            // InternalElkGraph.g:5080:3: ruleElkNode
            {
             before(grammarAccess.getRootNodeAccess().getChildrenElkNodeParserRuleCall_3_2_0()); 
            pushFollow(FOLLOW_2);
            ruleElkNode();

            state._fsp--;

             after(grammarAccess.getRootNodeAccess().getChildrenElkNodeParserRuleCall_3_2_0()); 

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
    // $ANTLR end "rule__RootNode__ChildrenAssignment_3_2"


    // $ANTLR start "rule__RootNode__ContainedEdgesAssignment_3_3"
    // InternalElkGraph.g:5089:1: rule__RootNode__ContainedEdgesAssignment_3_3 : ( ruleElkEdge ) ;
    public final void rule__RootNode__ContainedEdgesAssignment_3_3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5093:1: ( ( ruleElkEdge ) )
            // InternalElkGraph.g:5094:2: ( ruleElkEdge )
            {
            // InternalElkGraph.g:5094:2: ( ruleElkEdge )
            // InternalElkGraph.g:5095:3: ruleElkEdge
            {
             before(grammarAccess.getRootNodeAccess().getContainedEdgesElkEdgeParserRuleCall_3_3_0()); 
            pushFollow(FOLLOW_2);
            ruleElkEdge();

            state._fsp--;

             after(grammarAccess.getRootNodeAccess().getContainedEdgesElkEdgeParserRuleCall_3_3_0()); 

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
    // $ANTLR end "rule__RootNode__ContainedEdgesAssignment_3_3"


    // $ANTLR start "rule__ElkNode__IdentifierAssignment_1"
    // InternalElkGraph.g:5104:1: rule__ElkNode__IdentifierAssignment_1 : ( RULE_ID ) ;
    public final void rule__ElkNode__IdentifierAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5108:1: ( ( RULE_ID ) )
            // InternalElkGraph.g:5109:2: ( RULE_ID )
            {
            // InternalElkGraph.g:5109:2: ( RULE_ID )
            // InternalElkGraph.g:5110:3: RULE_ID
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
    // InternalElkGraph.g:5119:1: rule__ElkNode__PropertiesAssignment_2_2 : ( ruleProperty ) ;
    public final void rule__ElkNode__PropertiesAssignment_2_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5123:1: ( ( ruleProperty ) )
            // InternalElkGraph.g:5124:2: ( ruleProperty )
            {
            // InternalElkGraph.g:5124:2: ( ruleProperty )
            // InternalElkGraph.g:5125:3: ruleProperty
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


    // $ANTLR start "rule__ElkNode__LabelsAssignment_2_3_0"
    // InternalElkGraph.g:5134:1: rule__ElkNode__LabelsAssignment_2_3_0 : ( ruleElkLabel ) ;
    public final void rule__ElkNode__LabelsAssignment_2_3_0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5138:1: ( ( ruleElkLabel ) )
            // InternalElkGraph.g:5139:2: ( ruleElkLabel )
            {
            // InternalElkGraph.g:5139:2: ( ruleElkLabel )
            // InternalElkGraph.g:5140:3: ruleElkLabel
            {
             before(grammarAccess.getElkNodeAccess().getLabelsElkLabelParserRuleCall_2_3_0_0()); 
            pushFollow(FOLLOW_2);
            ruleElkLabel();

            state._fsp--;

             after(grammarAccess.getElkNodeAccess().getLabelsElkLabelParserRuleCall_2_3_0_0()); 

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
    // $ANTLR end "rule__ElkNode__LabelsAssignment_2_3_0"


    // $ANTLR start "rule__ElkNode__PortsAssignment_2_3_1"
    // InternalElkGraph.g:5149:1: rule__ElkNode__PortsAssignment_2_3_1 : ( ruleElkPort ) ;
    public final void rule__ElkNode__PortsAssignment_2_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5153:1: ( ( ruleElkPort ) )
            // InternalElkGraph.g:5154:2: ( ruleElkPort )
            {
            // InternalElkGraph.g:5154:2: ( ruleElkPort )
            // InternalElkGraph.g:5155:3: ruleElkPort
            {
             before(grammarAccess.getElkNodeAccess().getPortsElkPortParserRuleCall_2_3_1_0()); 
            pushFollow(FOLLOW_2);
            ruleElkPort();

            state._fsp--;

             after(grammarAccess.getElkNodeAccess().getPortsElkPortParserRuleCall_2_3_1_0()); 

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
    // $ANTLR end "rule__ElkNode__PortsAssignment_2_3_1"


    // $ANTLR start "rule__ElkNode__ChildrenAssignment_2_3_2"
    // InternalElkGraph.g:5164:1: rule__ElkNode__ChildrenAssignment_2_3_2 : ( ruleElkNode ) ;
    public final void rule__ElkNode__ChildrenAssignment_2_3_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5168:1: ( ( ruleElkNode ) )
            // InternalElkGraph.g:5169:2: ( ruleElkNode )
            {
            // InternalElkGraph.g:5169:2: ( ruleElkNode )
            // InternalElkGraph.g:5170:3: ruleElkNode
            {
             before(grammarAccess.getElkNodeAccess().getChildrenElkNodeParserRuleCall_2_3_2_0()); 
            pushFollow(FOLLOW_2);
            ruleElkNode();

            state._fsp--;

             after(grammarAccess.getElkNodeAccess().getChildrenElkNodeParserRuleCall_2_3_2_0()); 

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
    // $ANTLR end "rule__ElkNode__ChildrenAssignment_2_3_2"


    // $ANTLR start "rule__ElkNode__ContainedEdgesAssignment_2_3_3"
    // InternalElkGraph.g:5179:1: rule__ElkNode__ContainedEdgesAssignment_2_3_3 : ( ruleElkEdge ) ;
    public final void rule__ElkNode__ContainedEdgesAssignment_2_3_3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5183:1: ( ( ruleElkEdge ) )
            // InternalElkGraph.g:5184:2: ( ruleElkEdge )
            {
            // InternalElkGraph.g:5184:2: ( ruleElkEdge )
            // InternalElkGraph.g:5185:3: ruleElkEdge
            {
             before(grammarAccess.getElkNodeAccess().getContainedEdgesElkEdgeParserRuleCall_2_3_3_0()); 
            pushFollow(FOLLOW_2);
            ruleElkEdge();

            state._fsp--;

             after(grammarAccess.getElkNodeAccess().getContainedEdgesElkEdgeParserRuleCall_2_3_3_0()); 

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
    // $ANTLR end "rule__ElkNode__ContainedEdgesAssignment_2_3_3"


    // $ANTLR start "rule__ElkLabel__IdentifierAssignment_1_0"
    // InternalElkGraph.g:5194:1: rule__ElkLabel__IdentifierAssignment_1_0 : ( RULE_ID ) ;
    public final void rule__ElkLabel__IdentifierAssignment_1_0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5198:1: ( ( RULE_ID ) )
            // InternalElkGraph.g:5199:2: ( RULE_ID )
            {
            // InternalElkGraph.g:5199:2: ( RULE_ID )
            // InternalElkGraph.g:5200:3: RULE_ID
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
    // InternalElkGraph.g:5209:1: rule__ElkLabel__TextAssignment_2 : ( RULE_STRING ) ;
    public final void rule__ElkLabel__TextAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5213:1: ( ( RULE_STRING ) )
            // InternalElkGraph.g:5214:2: ( RULE_STRING )
            {
            // InternalElkGraph.g:5214:2: ( RULE_STRING )
            // InternalElkGraph.g:5215:3: RULE_STRING
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
    // InternalElkGraph.g:5224:1: rule__ElkLabel__PropertiesAssignment_3_2 : ( ruleProperty ) ;
    public final void rule__ElkLabel__PropertiesAssignment_3_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5228:1: ( ( ruleProperty ) )
            // InternalElkGraph.g:5229:2: ( ruleProperty )
            {
            // InternalElkGraph.g:5229:2: ( ruleProperty )
            // InternalElkGraph.g:5230:3: ruleProperty
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
    // InternalElkGraph.g:5239:1: rule__ElkLabel__LabelsAssignment_3_3 : ( ruleElkLabel ) ;
    public final void rule__ElkLabel__LabelsAssignment_3_3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5243:1: ( ( ruleElkLabel ) )
            // InternalElkGraph.g:5244:2: ( ruleElkLabel )
            {
            // InternalElkGraph.g:5244:2: ( ruleElkLabel )
            // InternalElkGraph.g:5245:3: ruleElkLabel
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
    // InternalElkGraph.g:5254:1: rule__ElkPort__IdentifierAssignment_1 : ( RULE_ID ) ;
    public final void rule__ElkPort__IdentifierAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5258:1: ( ( RULE_ID ) )
            // InternalElkGraph.g:5259:2: ( RULE_ID )
            {
            // InternalElkGraph.g:5259:2: ( RULE_ID )
            // InternalElkGraph.g:5260:3: RULE_ID
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
    // InternalElkGraph.g:5269:1: rule__ElkPort__PropertiesAssignment_2_2 : ( ruleProperty ) ;
    public final void rule__ElkPort__PropertiesAssignment_2_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5273:1: ( ( ruleProperty ) )
            // InternalElkGraph.g:5274:2: ( ruleProperty )
            {
            // InternalElkGraph.g:5274:2: ( ruleProperty )
            // InternalElkGraph.g:5275:3: ruleProperty
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
    // InternalElkGraph.g:5284:1: rule__ElkPort__LabelsAssignment_2_3 : ( ruleElkLabel ) ;
    public final void rule__ElkPort__LabelsAssignment_2_3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5288:1: ( ( ruleElkLabel ) )
            // InternalElkGraph.g:5289:2: ( ruleElkLabel )
            {
            // InternalElkGraph.g:5289:2: ( ruleElkLabel )
            // InternalElkGraph.g:5290:3: ruleElkLabel
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
    // InternalElkGraph.g:5299:1: rule__ShapeLayout__XAssignment_2_0_2 : ( ruleNumber ) ;
    public final void rule__ShapeLayout__XAssignment_2_0_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5303:1: ( ( ruleNumber ) )
            // InternalElkGraph.g:5304:2: ( ruleNumber )
            {
            // InternalElkGraph.g:5304:2: ( ruleNumber )
            // InternalElkGraph.g:5305:3: ruleNumber
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
    // InternalElkGraph.g:5314:1: rule__ShapeLayout__YAssignment_2_0_4 : ( ruleNumber ) ;
    public final void rule__ShapeLayout__YAssignment_2_0_4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5318:1: ( ( ruleNumber ) )
            // InternalElkGraph.g:5319:2: ( ruleNumber )
            {
            // InternalElkGraph.g:5319:2: ( ruleNumber )
            // InternalElkGraph.g:5320:3: ruleNumber
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
    // InternalElkGraph.g:5329:1: rule__ShapeLayout__WidthAssignment_2_1_2 : ( ruleNumber ) ;
    public final void rule__ShapeLayout__WidthAssignment_2_1_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5333:1: ( ( ruleNumber ) )
            // InternalElkGraph.g:5334:2: ( ruleNumber )
            {
            // InternalElkGraph.g:5334:2: ( ruleNumber )
            // InternalElkGraph.g:5335:3: ruleNumber
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
    // InternalElkGraph.g:5344:1: rule__ShapeLayout__HeightAssignment_2_1_4 : ( ruleNumber ) ;
    public final void rule__ShapeLayout__HeightAssignment_2_1_4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5348:1: ( ( ruleNumber ) )
            // InternalElkGraph.g:5349:2: ( ruleNumber )
            {
            // InternalElkGraph.g:5349:2: ( ruleNumber )
            // InternalElkGraph.g:5350:3: ruleNumber
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
    // InternalElkGraph.g:5359:1: rule__ElkEdge__IdentifierAssignment_1_0 : ( RULE_ID ) ;
    public final void rule__ElkEdge__IdentifierAssignment_1_0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5363:1: ( ( RULE_ID ) )
            // InternalElkGraph.g:5364:2: ( RULE_ID )
            {
            // InternalElkGraph.g:5364:2: ( RULE_ID )
            // InternalElkGraph.g:5365:3: RULE_ID
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
    // InternalElkGraph.g:5374:1: rule__ElkEdge__SourcesAssignment_2 : ( ( ruleQualifiedId ) ) ;
    public final void rule__ElkEdge__SourcesAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5378:1: ( ( ( ruleQualifiedId ) ) )
            // InternalElkGraph.g:5379:2: ( ( ruleQualifiedId ) )
            {
            // InternalElkGraph.g:5379:2: ( ( ruleQualifiedId ) )
            // InternalElkGraph.g:5380:3: ( ruleQualifiedId )
            {
             before(grammarAccess.getElkEdgeAccess().getSourcesElkConnectableShapeCrossReference_2_0()); 
            // InternalElkGraph.g:5381:3: ( ruleQualifiedId )
            // InternalElkGraph.g:5382:4: ruleQualifiedId
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
    // InternalElkGraph.g:5393:1: rule__ElkEdge__SourcesAssignment_3_1 : ( ( ruleQualifiedId ) ) ;
    public final void rule__ElkEdge__SourcesAssignment_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5397:1: ( ( ( ruleQualifiedId ) ) )
            // InternalElkGraph.g:5398:2: ( ( ruleQualifiedId ) )
            {
            // InternalElkGraph.g:5398:2: ( ( ruleQualifiedId ) )
            // InternalElkGraph.g:5399:3: ( ruleQualifiedId )
            {
             before(grammarAccess.getElkEdgeAccess().getSourcesElkConnectableShapeCrossReference_3_1_0()); 
            // InternalElkGraph.g:5400:3: ( ruleQualifiedId )
            // InternalElkGraph.g:5401:4: ruleQualifiedId
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
    // InternalElkGraph.g:5412:1: rule__ElkEdge__TargetsAssignment_5 : ( ( ruleQualifiedId ) ) ;
    public final void rule__ElkEdge__TargetsAssignment_5() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5416:1: ( ( ( ruleQualifiedId ) ) )
            // InternalElkGraph.g:5417:2: ( ( ruleQualifiedId ) )
            {
            // InternalElkGraph.g:5417:2: ( ( ruleQualifiedId ) )
            // InternalElkGraph.g:5418:3: ( ruleQualifiedId )
            {
             before(grammarAccess.getElkEdgeAccess().getTargetsElkConnectableShapeCrossReference_5_0()); 
            // InternalElkGraph.g:5419:3: ( ruleQualifiedId )
            // InternalElkGraph.g:5420:4: ruleQualifiedId
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
    // InternalElkGraph.g:5431:1: rule__ElkEdge__TargetsAssignment_6_1 : ( ( ruleQualifiedId ) ) ;
    public final void rule__ElkEdge__TargetsAssignment_6_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5435:1: ( ( ( ruleQualifiedId ) ) )
            // InternalElkGraph.g:5436:2: ( ( ruleQualifiedId ) )
            {
            // InternalElkGraph.g:5436:2: ( ( ruleQualifiedId ) )
            // InternalElkGraph.g:5437:3: ( ruleQualifiedId )
            {
             before(grammarAccess.getElkEdgeAccess().getTargetsElkConnectableShapeCrossReference_6_1_0()); 
            // InternalElkGraph.g:5438:3: ( ruleQualifiedId )
            // InternalElkGraph.g:5439:4: ruleQualifiedId
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
    // InternalElkGraph.g:5450:1: rule__ElkEdge__PropertiesAssignment_7_2 : ( ruleProperty ) ;
    public final void rule__ElkEdge__PropertiesAssignment_7_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5454:1: ( ( ruleProperty ) )
            // InternalElkGraph.g:5455:2: ( ruleProperty )
            {
            // InternalElkGraph.g:5455:2: ( ruleProperty )
            // InternalElkGraph.g:5456:3: ruleProperty
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
    // InternalElkGraph.g:5465:1: rule__ElkEdge__LabelsAssignment_7_3 : ( ruleElkLabel ) ;
    public final void rule__ElkEdge__LabelsAssignment_7_3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5469:1: ( ( ruleElkLabel ) )
            // InternalElkGraph.g:5470:2: ( ruleElkLabel )
            {
            // InternalElkGraph.g:5470:2: ( ruleElkLabel )
            // InternalElkGraph.g:5471:3: ruleElkLabel
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
    // InternalElkGraph.g:5480:1: rule__EdgeLayout__SectionsAssignment_2_0 : ( ruleElkSingleEdgeSection ) ;
    public final void rule__EdgeLayout__SectionsAssignment_2_0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5484:1: ( ( ruleElkSingleEdgeSection ) )
            // InternalElkGraph.g:5485:2: ( ruleElkSingleEdgeSection )
            {
            // InternalElkGraph.g:5485:2: ( ruleElkSingleEdgeSection )
            // InternalElkGraph.g:5486:3: ruleElkSingleEdgeSection
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
    // InternalElkGraph.g:5495:1: rule__EdgeLayout__SectionsAssignment_2_1 : ( ruleElkEdgeSection ) ;
    public final void rule__EdgeLayout__SectionsAssignment_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5499:1: ( ( ruleElkEdgeSection ) )
            // InternalElkGraph.g:5500:2: ( ruleElkEdgeSection )
            {
            // InternalElkGraph.g:5500:2: ( ruleElkEdgeSection )
            // InternalElkGraph.g:5501:3: ruleElkEdgeSection
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


    // $ANTLR start "rule__ElkSingleEdgeSection__IncomingShapeAssignment_1_0_0_2"
    // InternalElkGraph.g:5510:1: rule__ElkSingleEdgeSection__IncomingShapeAssignment_1_0_0_2 : ( ( ruleQualifiedId ) ) ;
    public final void rule__ElkSingleEdgeSection__IncomingShapeAssignment_1_0_0_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5514:1: ( ( ( ruleQualifiedId ) ) )
            // InternalElkGraph.g:5515:2: ( ( ruleQualifiedId ) )
            {
            // InternalElkGraph.g:5515:2: ( ( ruleQualifiedId ) )
            // InternalElkGraph.g:5516:3: ( ruleQualifiedId )
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getIncomingShapeElkConnectableShapeCrossReference_1_0_0_2_0()); 
            // InternalElkGraph.g:5517:3: ( ruleQualifiedId )
            // InternalElkGraph.g:5518:4: ruleQualifiedId
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getIncomingShapeElkConnectableShapeQualifiedIdParserRuleCall_1_0_0_2_0_1()); 
            pushFollow(FOLLOW_2);
            ruleQualifiedId();

            state._fsp--;

             after(grammarAccess.getElkSingleEdgeSectionAccess().getIncomingShapeElkConnectableShapeQualifiedIdParserRuleCall_1_0_0_2_0_1()); 

            }

             after(grammarAccess.getElkSingleEdgeSectionAccess().getIncomingShapeElkConnectableShapeCrossReference_1_0_0_2_0()); 

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
    // $ANTLR end "rule__ElkSingleEdgeSection__IncomingShapeAssignment_1_0_0_2"


    // $ANTLR start "rule__ElkSingleEdgeSection__OutgoingShapeAssignment_1_0_1_2"
    // InternalElkGraph.g:5529:1: rule__ElkSingleEdgeSection__OutgoingShapeAssignment_1_0_1_2 : ( ( ruleQualifiedId ) ) ;
    public final void rule__ElkSingleEdgeSection__OutgoingShapeAssignment_1_0_1_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5533:1: ( ( ( ruleQualifiedId ) ) )
            // InternalElkGraph.g:5534:2: ( ( ruleQualifiedId ) )
            {
            // InternalElkGraph.g:5534:2: ( ( ruleQualifiedId ) )
            // InternalElkGraph.g:5535:3: ( ruleQualifiedId )
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getOutgoingShapeElkConnectableShapeCrossReference_1_0_1_2_0()); 
            // InternalElkGraph.g:5536:3: ( ruleQualifiedId )
            // InternalElkGraph.g:5537:4: ruleQualifiedId
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getOutgoingShapeElkConnectableShapeQualifiedIdParserRuleCall_1_0_1_2_0_1()); 
            pushFollow(FOLLOW_2);
            ruleQualifiedId();

            state._fsp--;

             after(grammarAccess.getElkSingleEdgeSectionAccess().getOutgoingShapeElkConnectableShapeQualifiedIdParserRuleCall_1_0_1_2_0_1()); 

            }

             after(grammarAccess.getElkSingleEdgeSectionAccess().getOutgoingShapeElkConnectableShapeCrossReference_1_0_1_2_0()); 

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
    // $ANTLR end "rule__ElkSingleEdgeSection__OutgoingShapeAssignment_1_0_1_2"


    // $ANTLR start "rule__ElkSingleEdgeSection__StartXAssignment_1_0_2_2"
    // InternalElkGraph.g:5548:1: rule__ElkSingleEdgeSection__StartXAssignment_1_0_2_2 : ( ruleNumber ) ;
    public final void rule__ElkSingleEdgeSection__StartXAssignment_1_0_2_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5552:1: ( ( ruleNumber ) )
            // InternalElkGraph.g:5553:2: ( ruleNumber )
            {
            // InternalElkGraph.g:5553:2: ( ruleNumber )
            // InternalElkGraph.g:5554:3: ruleNumber
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getStartXNumberParserRuleCall_1_0_2_2_0()); 
            pushFollow(FOLLOW_2);
            ruleNumber();

            state._fsp--;

             after(grammarAccess.getElkSingleEdgeSectionAccess().getStartXNumberParserRuleCall_1_0_2_2_0()); 

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
    // $ANTLR end "rule__ElkSingleEdgeSection__StartXAssignment_1_0_2_2"


    // $ANTLR start "rule__ElkSingleEdgeSection__StartYAssignment_1_0_2_4"
    // InternalElkGraph.g:5563:1: rule__ElkSingleEdgeSection__StartYAssignment_1_0_2_4 : ( ruleNumber ) ;
    public final void rule__ElkSingleEdgeSection__StartYAssignment_1_0_2_4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5567:1: ( ( ruleNumber ) )
            // InternalElkGraph.g:5568:2: ( ruleNumber )
            {
            // InternalElkGraph.g:5568:2: ( ruleNumber )
            // InternalElkGraph.g:5569:3: ruleNumber
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getStartYNumberParserRuleCall_1_0_2_4_0()); 
            pushFollow(FOLLOW_2);
            ruleNumber();

            state._fsp--;

             after(grammarAccess.getElkSingleEdgeSectionAccess().getStartYNumberParserRuleCall_1_0_2_4_0()); 

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
    // $ANTLR end "rule__ElkSingleEdgeSection__StartYAssignment_1_0_2_4"


    // $ANTLR start "rule__ElkSingleEdgeSection__EndXAssignment_1_0_3_2"
    // InternalElkGraph.g:5578:1: rule__ElkSingleEdgeSection__EndXAssignment_1_0_3_2 : ( ruleNumber ) ;
    public final void rule__ElkSingleEdgeSection__EndXAssignment_1_0_3_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5582:1: ( ( ruleNumber ) )
            // InternalElkGraph.g:5583:2: ( ruleNumber )
            {
            // InternalElkGraph.g:5583:2: ( ruleNumber )
            // InternalElkGraph.g:5584:3: ruleNumber
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getEndXNumberParserRuleCall_1_0_3_2_0()); 
            pushFollow(FOLLOW_2);
            ruleNumber();

            state._fsp--;

             after(grammarAccess.getElkSingleEdgeSectionAccess().getEndXNumberParserRuleCall_1_0_3_2_0()); 

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
    // $ANTLR end "rule__ElkSingleEdgeSection__EndXAssignment_1_0_3_2"


    // $ANTLR start "rule__ElkSingleEdgeSection__EndYAssignment_1_0_3_4"
    // InternalElkGraph.g:5593:1: rule__ElkSingleEdgeSection__EndYAssignment_1_0_3_4 : ( ruleNumber ) ;
    public final void rule__ElkSingleEdgeSection__EndYAssignment_1_0_3_4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5597:1: ( ( ruleNumber ) )
            // InternalElkGraph.g:5598:2: ( ruleNumber )
            {
            // InternalElkGraph.g:5598:2: ( ruleNumber )
            // InternalElkGraph.g:5599:3: ruleNumber
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getEndYNumberParserRuleCall_1_0_3_4_0()); 
            pushFollow(FOLLOW_2);
            ruleNumber();

            state._fsp--;

             after(grammarAccess.getElkSingleEdgeSectionAccess().getEndYNumberParserRuleCall_1_0_3_4_0()); 

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
    // $ANTLR end "rule__ElkSingleEdgeSection__EndYAssignment_1_0_3_4"


    // $ANTLR start "rule__ElkSingleEdgeSection__BendPointsAssignment_1_1_2"
    // InternalElkGraph.g:5608:1: rule__ElkSingleEdgeSection__BendPointsAssignment_1_1_2 : ( ruleElkBendPoint ) ;
    public final void rule__ElkSingleEdgeSection__BendPointsAssignment_1_1_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5612:1: ( ( ruleElkBendPoint ) )
            // InternalElkGraph.g:5613:2: ( ruleElkBendPoint )
            {
            // InternalElkGraph.g:5613:2: ( ruleElkBendPoint )
            // InternalElkGraph.g:5614:3: ruleElkBendPoint
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getBendPointsElkBendPointParserRuleCall_1_1_2_0()); 
            pushFollow(FOLLOW_2);
            ruleElkBendPoint();

            state._fsp--;

             after(grammarAccess.getElkSingleEdgeSectionAccess().getBendPointsElkBendPointParserRuleCall_1_1_2_0()); 

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
    // $ANTLR end "rule__ElkSingleEdgeSection__BendPointsAssignment_1_1_2"


    // $ANTLR start "rule__ElkSingleEdgeSection__BendPointsAssignment_1_1_3_1"
    // InternalElkGraph.g:5623:1: rule__ElkSingleEdgeSection__BendPointsAssignment_1_1_3_1 : ( ruleElkBendPoint ) ;
    public final void rule__ElkSingleEdgeSection__BendPointsAssignment_1_1_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5627:1: ( ( ruleElkBendPoint ) )
            // InternalElkGraph.g:5628:2: ( ruleElkBendPoint )
            {
            // InternalElkGraph.g:5628:2: ( ruleElkBendPoint )
            // InternalElkGraph.g:5629:3: ruleElkBendPoint
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getBendPointsElkBendPointParserRuleCall_1_1_3_1_0()); 
            pushFollow(FOLLOW_2);
            ruleElkBendPoint();

            state._fsp--;

             after(grammarAccess.getElkSingleEdgeSectionAccess().getBendPointsElkBendPointParserRuleCall_1_1_3_1_0()); 

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
    // $ANTLR end "rule__ElkSingleEdgeSection__BendPointsAssignment_1_1_3_1"


    // $ANTLR start "rule__ElkSingleEdgeSection__PropertiesAssignment_1_2"
    // InternalElkGraph.g:5638:1: rule__ElkSingleEdgeSection__PropertiesAssignment_1_2 : ( ruleProperty ) ;
    public final void rule__ElkSingleEdgeSection__PropertiesAssignment_1_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5642:1: ( ( ruleProperty ) )
            // InternalElkGraph.g:5643:2: ( ruleProperty )
            {
            // InternalElkGraph.g:5643:2: ( ruleProperty )
            // InternalElkGraph.g:5644:3: ruleProperty
            {
             before(grammarAccess.getElkSingleEdgeSectionAccess().getPropertiesPropertyParserRuleCall_1_2_0()); 
            pushFollow(FOLLOW_2);
            ruleProperty();

            state._fsp--;

             after(grammarAccess.getElkSingleEdgeSectionAccess().getPropertiesPropertyParserRuleCall_1_2_0()); 

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
    // $ANTLR end "rule__ElkSingleEdgeSection__PropertiesAssignment_1_2"


    // $ANTLR start "rule__ElkEdgeSection__IdentifierAssignment_1"
    // InternalElkGraph.g:5653:1: rule__ElkEdgeSection__IdentifierAssignment_1 : ( RULE_ID ) ;
    public final void rule__ElkEdgeSection__IdentifierAssignment_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5657:1: ( ( RULE_ID ) )
            // InternalElkGraph.g:5658:2: ( RULE_ID )
            {
            // InternalElkGraph.g:5658:2: ( RULE_ID )
            // InternalElkGraph.g:5659:3: RULE_ID
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
    // InternalElkGraph.g:5668:1: rule__ElkEdgeSection__OutgoingSectionsAssignment_2_1 : ( ( RULE_ID ) ) ;
    public final void rule__ElkEdgeSection__OutgoingSectionsAssignment_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5672:1: ( ( ( RULE_ID ) ) )
            // InternalElkGraph.g:5673:2: ( ( RULE_ID ) )
            {
            // InternalElkGraph.g:5673:2: ( ( RULE_ID ) )
            // InternalElkGraph.g:5674:3: ( RULE_ID )
            {
             before(grammarAccess.getElkEdgeSectionAccess().getOutgoingSectionsElkEdgeSectionCrossReference_2_1_0()); 
            // InternalElkGraph.g:5675:3: ( RULE_ID )
            // InternalElkGraph.g:5676:4: RULE_ID
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
    // InternalElkGraph.g:5687:1: rule__ElkEdgeSection__OutgoingSectionsAssignment_2_2_1 : ( ( RULE_ID ) ) ;
    public final void rule__ElkEdgeSection__OutgoingSectionsAssignment_2_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5691:1: ( ( ( RULE_ID ) ) )
            // InternalElkGraph.g:5692:2: ( ( RULE_ID ) )
            {
            // InternalElkGraph.g:5692:2: ( ( RULE_ID ) )
            // InternalElkGraph.g:5693:3: ( RULE_ID )
            {
             before(grammarAccess.getElkEdgeSectionAccess().getOutgoingSectionsElkEdgeSectionCrossReference_2_2_1_0()); 
            // InternalElkGraph.g:5694:3: ( RULE_ID )
            // InternalElkGraph.g:5695:4: RULE_ID
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


    // $ANTLR start "rule__ElkEdgeSection__IncomingShapeAssignment_4_0_0_2"
    // InternalElkGraph.g:5706:1: rule__ElkEdgeSection__IncomingShapeAssignment_4_0_0_2 : ( ( ruleQualifiedId ) ) ;
    public final void rule__ElkEdgeSection__IncomingShapeAssignment_4_0_0_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5710:1: ( ( ( ruleQualifiedId ) ) )
            // InternalElkGraph.g:5711:2: ( ( ruleQualifiedId ) )
            {
            // InternalElkGraph.g:5711:2: ( ( ruleQualifiedId ) )
            // InternalElkGraph.g:5712:3: ( ruleQualifiedId )
            {
             before(grammarAccess.getElkEdgeSectionAccess().getIncomingShapeElkConnectableShapeCrossReference_4_0_0_2_0()); 
            // InternalElkGraph.g:5713:3: ( ruleQualifiedId )
            // InternalElkGraph.g:5714:4: ruleQualifiedId
            {
             before(grammarAccess.getElkEdgeSectionAccess().getIncomingShapeElkConnectableShapeQualifiedIdParserRuleCall_4_0_0_2_0_1()); 
            pushFollow(FOLLOW_2);
            ruleQualifiedId();

            state._fsp--;

             after(grammarAccess.getElkEdgeSectionAccess().getIncomingShapeElkConnectableShapeQualifiedIdParserRuleCall_4_0_0_2_0_1()); 

            }

             after(grammarAccess.getElkEdgeSectionAccess().getIncomingShapeElkConnectableShapeCrossReference_4_0_0_2_0()); 

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
    // $ANTLR end "rule__ElkEdgeSection__IncomingShapeAssignment_4_0_0_2"


    // $ANTLR start "rule__ElkEdgeSection__OutgoingShapeAssignment_4_0_1_2"
    // InternalElkGraph.g:5725:1: rule__ElkEdgeSection__OutgoingShapeAssignment_4_0_1_2 : ( ( ruleQualifiedId ) ) ;
    public final void rule__ElkEdgeSection__OutgoingShapeAssignment_4_0_1_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5729:1: ( ( ( ruleQualifiedId ) ) )
            // InternalElkGraph.g:5730:2: ( ( ruleQualifiedId ) )
            {
            // InternalElkGraph.g:5730:2: ( ( ruleQualifiedId ) )
            // InternalElkGraph.g:5731:3: ( ruleQualifiedId )
            {
             before(grammarAccess.getElkEdgeSectionAccess().getOutgoingShapeElkConnectableShapeCrossReference_4_0_1_2_0()); 
            // InternalElkGraph.g:5732:3: ( ruleQualifiedId )
            // InternalElkGraph.g:5733:4: ruleQualifiedId
            {
             before(grammarAccess.getElkEdgeSectionAccess().getOutgoingShapeElkConnectableShapeQualifiedIdParserRuleCall_4_0_1_2_0_1()); 
            pushFollow(FOLLOW_2);
            ruleQualifiedId();

            state._fsp--;

             after(grammarAccess.getElkEdgeSectionAccess().getOutgoingShapeElkConnectableShapeQualifiedIdParserRuleCall_4_0_1_2_0_1()); 

            }

             after(grammarAccess.getElkEdgeSectionAccess().getOutgoingShapeElkConnectableShapeCrossReference_4_0_1_2_0()); 

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
    // $ANTLR end "rule__ElkEdgeSection__OutgoingShapeAssignment_4_0_1_2"


    // $ANTLR start "rule__ElkEdgeSection__StartXAssignment_4_0_2_2"
    // InternalElkGraph.g:5744:1: rule__ElkEdgeSection__StartXAssignment_4_0_2_2 : ( ruleNumber ) ;
    public final void rule__ElkEdgeSection__StartXAssignment_4_0_2_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5748:1: ( ( ruleNumber ) )
            // InternalElkGraph.g:5749:2: ( ruleNumber )
            {
            // InternalElkGraph.g:5749:2: ( ruleNumber )
            // InternalElkGraph.g:5750:3: ruleNumber
            {
             before(grammarAccess.getElkEdgeSectionAccess().getStartXNumberParserRuleCall_4_0_2_2_0()); 
            pushFollow(FOLLOW_2);
            ruleNumber();

            state._fsp--;

             after(grammarAccess.getElkEdgeSectionAccess().getStartXNumberParserRuleCall_4_0_2_2_0()); 

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
    // $ANTLR end "rule__ElkEdgeSection__StartXAssignment_4_0_2_2"


    // $ANTLR start "rule__ElkEdgeSection__StartYAssignment_4_0_2_4"
    // InternalElkGraph.g:5759:1: rule__ElkEdgeSection__StartYAssignment_4_0_2_4 : ( ruleNumber ) ;
    public final void rule__ElkEdgeSection__StartYAssignment_4_0_2_4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5763:1: ( ( ruleNumber ) )
            // InternalElkGraph.g:5764:2: ( ruleNumber )
            {
            // InternalElkGraph.g:5764:2: ( ruleNumber )
            // InternalElkGraph.g:5765:3: ruleNumber
            {
             before(grammarAccess.getElkEdgeSectionAccess().getStartYNumberParserRuleCall_4_0_2_4_0()); 
            pushFollow(FOLLOW_2);
            ruleNumber();

            state._fsp--;

             after(grammarAccess.getElkEdgeSectionAccess().getStartYNumberParserRuleCall_4_0_2_4_0()); 

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
    // $ANTLR end "rule__ElkEdgeSection__StartYAssignment_4_0_2_4"


    // $ANTLR start "rule__ElkEdgeSection__EndXAssignment_4_0_3_2"
    // InternalElkGraph.g:5774:1: rule__ElkEdgeSection__EndXAssignment_4_0_3_2 : ( ruleNumber ) ;
    public final void rule__ElkEdgeSection__EndXAssignment_4_0_3_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5778:1: ( ( ruleNumber ) )
            // InternalElkGraph.g:5779:2: ( ruleNumber )
            {
            // InternalElkGraph.g:5779:2: ( ruleNumber )
            // InternalElkGraph.g:5780:3: ruleNumber
            {
             before(grammarAccess.getElkEdgeSectionAccess().getEndXNumberParserRuleCall_4_0_3_2_0()); 
            pushFollow(FOLLOW_2);
            ruleNumber();

            state._fsp--;

             after(grammarAccess.getElkEdgeSectionAccess().getEndXNumberParserRuleCall_4_0_3_2_0()); 

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
    // $ANTLR end "rule__ElkEdgeSection__EndXAssignment_4_0_3_2"


    // $ANTLR start "rule__ElkEdgeSection__EndYAssignment_4_0_3_4"
    // InternalElkGraph.g:5789:1: rule__ElkEdgeSection__EndYAssignment_4_0_3_4 : ( ruleNumber ) ;
    public final void rule__ElkEdgeSection__EndYAssignment_4_0_3_4() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5793:1: ( ( ruleNumber ) )
            // InternalElkGraph.g:5794:2: ( ruleNumber )
            {
            // InternalElkGraph.g:5794:2: ( ruleNumber )
            // InternalElkGraph.g:5795:3: ruleNumber
            {
             before(grammarAccess.getElkEdgeSectionAccess().getEndYNumberParserRuleCall_4_0_3_4_0()); 
            pushFollow(FOLLOW_2);
            ruleNumber();

            state._fsp--;

             after(grammarAccess.getElkEdgeSectionAccess().getEndYNumberParserRuleCall_4_0_3_4_0()); 

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
    // $ANTLR end "rule__ElkEdgeSection__EndYAssignment_4_0_3_4"


    // $ANTLR start "rule__ElkEdgeSection__BendPointsAssignment_4_1_2"
    // InternalElkGraph.g:5804:1: rule__ElkEdgeSection__BendPointsAssignment_4_1_2 : ( ruleElkBendPoint ) ;
    public final void rule__ElkEdgeSection__BendPointsAssignment_4_1_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5808:1: ( ( ruleElkBendPoint ) )
            // InternalElkGraph.g:5809:2: ( ruleElkBendPoint )
            {
            // InternalElkGraph.g:5809:2: ( ruleElkBendPoint )
            // InternalElkGraph.g:5810:3: ruleElkBendPoint
            {
             before(grammarAccess.getElkEdgeSectionAccess().getBendPointsElkBendPointParserRuleCall_4_1_2_0()); 
            pushFollow(FOLLOW_2);
            ruleElkBendPoint();

            state._fsp--;

             after(grammarAccess.getElkEdgeSectionAccess().getBendPointsElkBendPointParserRuleCall_4_1_2_0()); 

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
    // $ANTLR end "rule__ElkEdgeSection__BendPointsAssignment_4_1_2"


    // $ANTLR start "rule__ElkEdgeSection__BendPointsAssignment_4_1_3_1"
    // InternalElkGraph.g:5819:1: rule__ElkEdgeSection__BendPointsAssignment_4_1_3_1 : ( ruleElkBendPoint ) ;
    public final void rule__ElkEdgeSection__BendPointsAssignment_4_1_3_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5823:1: ( ( ruleElkBendPoint ) )
            // InternalElkGraph.g:5824:2: ( ruleElkBendPoint )
            {
            // InternalElkGraph.g:5824:2: ( ruleElkBendPoint )
            // InternalElkGraph.g:5825:3: ruleElkBendPoint
            {
             before(grammarAccess.getElkEdgeSectionAccess().getBendPointsElkBendPointParserRuleCall_4_1_3_1_0()); 
            pushFollow(FOLLOW_2);
            ruleElkBendPoint();

            state._fsp--;

             after(grammarAccess.getElkEdgeSectionAccess().getBendPointsElkBendPointParserRuleCall_4_1_3_1_0()); 

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
    // $ANTLR end "rule__ElkEdgeSection__BendPointsAssignment_4_1_3_1"


    // $ANTLR start "rule__ElkEdgeSection__PropertiesAssignment_4_2"
    // InternalElkGraph.g:5834:1: rule__ElkEdgeSection__PropertiesAssignment_4_2 : ( ruleProperty ) ;
    public final void rule__ElkEdgeSection__PropertiesAssignment_4_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5838:1: ( ( ruleProperty ) )
            // InternalElkGraph.g:5839:2: ( ruleProperty )
            {
            // InternalElkGraph.g:5839:2: ( ruleProperty )
            // InternalElkGraph.g:5840:3: ruleProperty
            {
             before(grammarAccess.getElkEdgeSectionAccess().getPropertiesPropertyParserRuleCall_4_2_0()); 
            pushFollow(FOLLOW_2);
            ruleProperty();

            state._fsp--;

             after(grammarAccess.getElkEdgeSectionAccess().getPropertiesPropertyParserRuleCall_4_2_0()); 

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
    // $ANTLR end "rule__ElkEdgeSection__PropertiesAssignment_4_2"


    // $ANTLR start "rule__ElkBendPoint__XAssignment_0"
    // InternalElkGraph.g:5849:1: rule__ElkBendPoint__XAssignment_0 : ( ruleNumber ) ;
    public final void rule__ElkBendPoint__XAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5853:1: ( ( ruleNumber ) )
            // InternalElkGraph.g:5854:2: ( ruleNumber )
            {
            // InternalElkGraph.g:5854:2: ( ruleNumber )
            // InternalElkGraph.g:5855:3: ruleNumber
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
    // InternalElkGraph.g:5864:1: rule__ElkBendPoint__YAssignment_2 : ( ruleNumber ) ;
    public final void rule__ElkBendPoint__YAssignment_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5868:1: ( ( ruleNumber ) )
            // InternalElkGraph.g:5869:2: ( ruleNumber )
            {
            // InternalElkGraph.g:5869:2: ( ruleNumber )
            // InternalElkGraph.g:5870:3: ruleNumber
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
    // InternalElkGraph.g:5879:1: rule__Property__KeyAssignment_0 : ( rulePropertyKey ) ;
    public final void rule__Property__KeyAssignment_0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5883:1: ( ( rulePropertyKey ) )
            // InternalElkGraph.g:5884:2: ( rulePropertyKey )
            {
            // InternalElkGraph.g:5884:2: ( rulePropertyKey )
            // InternalElkGraph.g:5885:3: rulePropertyKey
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
    // InternalElkGraph.g:5894:1: rule__Property__ValueAssignment_2_0 : ( ruleStringValue ) ;
    public final void rule__Property__ValueAssignment_2_0() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5898:1: ( ( ruleStringValue ) )
            // InternalElkGraph.g:5899:2: ( ruleStringValue )
            {
            // InternalElkGraph.g:5899:2: ( ruleStringValue )
            // InternalElkGraph.g:5900:3: ruleStringValue
            {
             before(grammarAccess.getPropertyAccess().getValueStringValueParserRuleCall_2_0_0()); 
            pushFollow(FOLLOW_2);
            ruleStringValue();

            state._fsp--;

             after(grammarAccess.getPropertyAccess().getValueStringValueParserRuleCall_2_0_0()); 

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
    // InternalElkGraph.g:5909:1: rule__Property__ValueAssignment_2_1 : ( ruleQualifiedIdValue ) ;
    public final void rule__Property__ValueAssignment_2_1() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5913:1: ( ( ruleQualifiedIdValue ) )
            // InternalElkGraph.g:5914:2: ( ruleQualifiedIdValue )
            {
            // InternalElkGraph.g:5914:2: ( ruleQualifiedIdValue )
            // InternalElkGraph.g:5915:3: ruleQualifiedIdValue
            {
             before(grammarAccess.getPropertyAccess().getValueQualifiedIdValueParserRuleCall_2_1_0()); 
            pushFollow(FOLLOW_2);
            ruleQualifiedIdValue();

            state._fsp--;

             after(grammarAccess.getPropertyAccess().getValueQualifiedIdValueParserRuleCall_2_1_0()); 

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
    // InternalElkGraph.g:5924:1: rule__Property__ValueAssignment_2_2 : ( ruleNumberValue ) ;
    public final void rule__Property__ValueAssignment_2_2() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5928:1: ( ( ruleNumberValue ) )
            // InternalElkGraph.g:5929:2: ( ruleNumberValue )
            {
            // InternalElkGraph.g:5929:2: ( ruleNumberValue )
            // InternalElkGraph.g:5930:3: ruleNumberValue
            {
             before(grammarAccess.getPropertyAccess().getValueNumberValueParserRuleCall_2_2_0()); 
            pushFollow(FOLLOW_2);
            ruleNumberValue();

            state._fsp--;

             after(grammarAccess.getPropertyAccess().getValueNumberValueParserRuleCall_2_2_0()); 

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
    // InternalElkGraph.g:5939:1: rule__Property__ValueAssignment_2_3 : ( ruleBooleanValue ) ;
    public final void rule__Property__ValueAssignment_2_3() throws RecognitionException {

        		int stackSize = keepStackSize();
        	
        try {
            // InternalElkGraph.g:5943:1: ( ( ruleBooleanValue ) )
            // InternalElkGraph.g:5944:2: ( ruleBooleanValue )
            {
            // InternalElkGraph.g:5944:2: ( ruleBooleanValue )
            // InternalElkGraph.g:5945:3: ruleBooleanValue
            {
             before(grammarAccess.getPropertyAccess().getValueBooleanValueParserRuleCall_2_3_0()); 
            pushFollow(FOLLOW_2);
            ruleBooleanValue();

            state._fsp--;

             after(grammarAccess.getPropertyAccess().getValueBooleanValueParserRuleCall_2_3_0()); 

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

    // Delegated rules


 

    public static final BitSet FOLLOW_1 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_2 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_3 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_4 = new BitSet(new long[]{0x0000000010298080L});
    public static final BitSet FOLLOW_5 = new BitSet(new long[]{0x0000000000000082L});
    public static final BitSet FOLLOW_6 = new BitSet(new long[]{0x0000000010290002L});
    public static final BitSet FOLLOW_7 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_8 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_9 = new BitSet(new long[]{0x00000000106D0080L});
    public static final BitSet FOLLOW_10 = new BitSet(new long[]{0x0000000000000090L});
    public static final BitSet FOLLOW_11 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_12 = new BitSet(new long[]{0x00000000004C0080L});
    public static final BitSet FOLLOW_13 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_14 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_15 = new BitSet(new long[]{0x000000000A000000L});
    public static final BitSet FOLLOW_16 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_17 = new BitSet(new long[]{0x0000000000000060L});
    public static final BitSet FOLLOW_18 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_19 = new BitSet(new long[]{0x0000000024000000L});
    public static final BitSet FOLLOW_20 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_21 = new BitSet(new long[]{0x0000000004020000L});
    public static final BitSet FOLLOW_22 = new BitSet(new long[]{0x00000013C0000000L});
    public static final BitSet FOLLOW_23 = new BitSet(new long[]{0x00000003C0000000L});
    public static final BitSet FOLLOW_24 = new BitSet(new long[]{0x0000000400000080L});
    public static final BitSet FOLLOW_25 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_26 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_27 = new BitSet(new long[]{0x0000000020800000L});
    public static final BitSet FOLLOW_28 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_29 = new BitSet(new long[]{0x0000002000000002L});
    public static final BitSet FOLLOW_30 = new BitSet(new long[]{0x00000000000060F0L});
    public static final BitSet FOLLOW_31 = new BitSet(new long[]{0x000000000A000002L});
    public static final BitSet FOLLOW_32 = new BitSet(new long[]{0x00000003C0000002L});

}
