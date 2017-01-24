package org.eclipse.elk.graph.text.parser.antlr.internal;

import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parser.antlr.AbstractInternalAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.parser.antlr.AntlrDatatypeRuleToken;
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
public class InternalElkGraphParser extends AbstractInternalAntlrParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_ID", "RULE_STRING", "RULE_SIGNED_INT", "RULE_FLOAT", "RULE_INT", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'graph'", "'node'", "'{'", "'}'", "'label'", "':'", "'port'", "'layout'", "'['", "'position'", "','", "'size'", "']'", "'edge'", "'->'", "'incoming'", "'outgoing'", "'start'", "'end'", "'bends'", "'|'", "'section'", "'.'", "'true'", "'false'"
    };
    public static final int RULE_STRING=5;
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
    public static final int RULE_ID=4;
    public static final int RULE_WS=11;
    public static final int RULE_SIGNED_INT=6;
    public static final int RULE_ANY_OTHER=12;
    public static final int T__26=26;
    public static final int T__27=27;
    public static final int T__28=28;
    public static final int RULE_INT=8;
    public static final int T__29=29;
    public static final int T__22=22;
    public static final int RULE_ML_COMMENT=9;
    public static final int T__23=23;
    public static final int RULE_FLOAT=7;
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

        public InternalElkGraphParser(TokenStream input, ElkGraphGrammarAccess grammarAccess) {
            this(input);
            this.grammarAccess = grammarAccess;
            registerRules(grammarAccess.getGrammar());
        }

        @Override
        protected String getFirstRuleName() {
        	return "RootNode";
       	}

       	@Override
       	protected ElkGraphGrammarAccess getGrammarAccess() {
       		return grammarAccess;
       	}




    // $ANTLR start "entryRuleRootNode"
    // InternalElkGraph.g:68:1: entryRuleRootNode returns [EObject current=null] : iv_ruleRootNode= ruleRootNode EOF ;
    public final EObject entryRuleRootNode() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRootNode = null;


        try {
            // InternalElkGraph.g:68:49: (iv_ruleRootNode= ruleRootNode EOF )
            // InternalElkGraph.g:69:2: iv_ruleRootNode= ruleRootNode EOF
            {
             newCompositeNode(grammarAccess.getRootNodeRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleRootNode=ruleRootNode();

            state._fsp--;

             current =iv_ruleRootNode; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleRootNode"


    // $ANTLR start "ruleRootNode"
    // InternalElkGraph.g:75:1: ruleRootNode returns [EObject current=null] : ( () (otherlv_1= 'graph' ( (lv_identifier_2_0= RULE_ID ) ) )? ( (lv_properties_3_0= ruleProperty ) )* ( ( (lv_labels_4_0= ruleElkLabel ) ) | ( (lv_ports_5_0= ruleElkPort ) ) | ( (lv_children_6_0= ruleElkNode ) ) | ( (lv_containedEdges_7_0= ruleElkEdge ) ) )* ) ;
    public final EObject ruleRootNode() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token lv_identifier_2_0=null;
        EObject lv_properties_3_0 = null;

        EObject lv_labels_4_0 = null;

        EObject lv_ports_5_0 = null;

        EObject lv_children_6_0 = null;

        EObject lv_containedEdges_7_0 = null;



        	enterRule();

        try {
            // InternalElkGraph.g:81:2: ( ( () (otherlv_1= 'graph' ( (lv_identifier_2_0= RULE_ID ) ) )? ( (lv_properties_3_0= ruleProperty ) )* ( ( (lv_labels_4_0= ruleElkLabel ) ) | ( (lv_ports_5_0= ruleElkPort ) ) | ( (lv_children_6_0= ruleElkNode ) ) | ( (lv_containedEdges_7_0= ruleElkEdge ) ) )* ) )
            // InternalElkGraph.g:82:2: ( () (otherlv_1= 'graph' ( (lv_identifier_2_0= RULE_ID ) ) )? ( (lv_properties_3_0= ruleProperty ) )* ( ( (lv_labels_4_0= ruleElkLabel ) ) | ( (lv_ports_5_0= ruleElkPort ) ) | ( (lv_children_6_0= ruleElkNode ) ) | ( (lv_containedEdges_7_0= ruleElkEdge ) ) )* )
            {
            // InternalElkGraph.g:82:2: ( () (otherlv_1= 'graph' ( (lv_identifier_2_0= RULE_ID ) ) )? ( (lv_properties_3_0= ruleProperty ) )* ( ( (lv_labels_4_0= ruleElkLabel ) ) | ( (lv_ports_5_0= ruleElkPort ) ) | ( (lv_children_6_0= ruleElkNode ) ) | ( (lv_containedEdges_7_0= ruleElkEdge ) ) )* )
            // InternalElkGraph.g:83:3: () (otherlv_1= 'graph' ( (lv_identifier_2_0= RULE_ID ) ) )? ( (lv_properties_3_0= ruleProperty ) )* ( ( (lv_labels_4_0= ruleElkLabel ) ) | ( (lv_ports_5_0= ruleElkPort ) ) | ( (lv_children_6_0= ruleElkNode ) ) | ( (lv_containedEdges_7_0= ruleElkEdge ) ) )*
            {
            // InternalElkGraph.g:83:3: ()
            // InternalElkGraph.g:84:4: 
            {

            				current = forceCreateModelElement(
            					grammarAccess.getRootNodeAccess().getElkNodeAction_0(),
            					current);
            			

            }

            // InternalElkGraph.g:90:3: (otherlv_1= 'graph' ( (lv_identifier_2_0= RULE_ID ) ) )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==13) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // InternalElkGraph.g:91:4: otherlv_1= 'graph' ( (lv_identifier_2_0= RULE_ID ) )
                    {
                    otherlv_1=(Token)match(input,13,FOLLOW_3); 

                    				newLeafNode(otherlv_1, grammarAccess.getRootNodeAccess().getGraphKeyword_1_0());
                    			
                    // InternalElkGraph.g:95:4: ( (lv_identifier_2_0= RULE_ID ) )
                    // InternalElkGraph.g:96:5: (lv_identifier_2_0= RULE_ID )
                    {
                    // InternalElkGraph.g:96:5: (lv_identifier_2_0= RULE_ID )
                    // InternalElkGraph.g:97:6: lv_identifier_2_0= RULE_ID
                    {
                    lv_identifier_2_0=(Token)match(input,RULE_ID,FOLLOW_4); 

                    						newLeafNode(lv_identifier_2_0, grammarAccess.getRootNodeAccess().getIdentifierIDTerminalRuleCall_1_1_0());
                    					

                    						if (current==null) {
                    							current = createModelElement(grammarAccess.getRootNodeRule());
                    						}
                    						setWithLastConsumed(
                    							current,
                    							"identifier",
                    							lv_identifier_2_0,
                    							"org.eclipse.xtext.common.Terminals.ID");
                    					

                    }


                    }


                    }
                    break;

            }

            // InternalElkGraph.g:114:3: ( (lv_properties_3_0= ruleProperty ) )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==RULE_ID) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // InternalElkGraph.g:115:4: (lv_properties_3_0= ruleProperty )
            	    {
            	    // InternalElkGraph.g:115:4: (lv_properties_3_0= ruleProperty )
            	    // InternalElkGraph.g:116:5: lv_properties_3_0= ruleProperty
            	    {

            	    					newCompositeNode(grammarAccess.getRootNodeAccess().getPropertiesPropertyParserRuleCall_2_0());
            	    				
            	    pushFollow(FOLLOW_4);
            	    lv_properties_3_0=ruleProperty();

            	    state._fsp--;


            	    					if (current==null) {
            	    						current = createModelElementForParent(grammarAccess.getRootNodeRule());
            	    					}
            	    					add(
            	    						current,
            	    						"properties",
            	    						lv_properties_3_0,
            	    						"org.eclipse.elk.graph.text.ElkGraph.Property");
            	    					afterParserOrEnumRuleCall();
            	    				

            	    }


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            // InternalElkGraph.g:133:3: ( ( (lv_labels_4_0= ruleElkLabel ) ) | ( (lv_ports_5_0= ruleElkPort ) ) | ( (lv_children_6_0= ruleElkNode ) ) | ( (lv_containedEdges_7_0= ruleElkEdge ) ) )*
            loop3:
            do {
                int alt3=5;
                switch ( input.LA(1) ) {
                case 17:
                    {
                    alt3=1;
                    }
                    break;
                case 19:
                    {
                    alt3=2;
                    }
                    break;
                case 14:
                    {
                    alt3=3;
                    }
                    break;
                case 26:
                    {
                    alt3=4;
                    }
                    break;

                }

                switch (alt3) {
            	case 1 :
            	    // InternalElkGraph.g:134:4: ( (lv_labels_4_0= ruleElkLabel ) )
            	    {
            	    // InternalElkGraph.g:134:4: ( (lv_labels_4_0= ruleElkLabel ) )
            	    // InternalElkGraph.g:135:5: (lv_labels_4_0= ruleElkLabel )
            	    {
            	    // InternalElkGraph.g:135:5: (lv_labels_4_0= ruleElkLabel )
            	    // InternalElkGraph.g:136:6: lv_labels_4_0= ruleElkLabel
            	    {

            	    						newCompositeNode(grammarAccess.getRootNodeAccess().getLabelsElkLabelParserRuleCall_3_0_0());
            	    					
            	    pushFollow(FOLLOW_5);
            	    lv_labels_4_0=ruleElkLabel();

            	    state._fsp--;


            	    						if (current==null) {
            	    							current = createModelElementForParent(grammarAccess.getRootNodeRule());
            	    						}
            	    						add(
            	    							current,
            	    							"labels",
            	    							lv_labels_4_0,
            	    							"org.eclipse.elk.graph.text.ElkGraph.ElkLabel");
            	    						afterParserOrEnumRuleCall();
            	    					

            	    }


            	    }


            	    }
            	    break;
            	case 2 :
            	    // InternalElkGraph.g:154:4: ( (lv_ports_5_0= ruleElkPort ) )
            	    {
            	    // InternalElkGraph.g:154:4: ( (lv_ports_5_0= ruleElkPort ) )
            	    // InternalElkGraph.g:155:5: (lv_ports_5_0= ruleElkPort )
            	    {
            	    // InternalElkGraph.g:155:5: (lv_ports_5_0= ruleElkPort )
            	    // InternalElkGraph.g:156:6: lv_ports_5_0= ruleElkPort
            	    {

            	    						newCompositeNode(grammarAccess.getRootNodeAccess().getPortsElkPortParserRuleCall_3_1_0());
            	    					
            	    pushFollow(FOLLOW_5);
            	    lv_ports_5_0=ruleElkPort();

            	    state._fsp--;


            	    						if (current==null) {
            	    							current = createModelElementForParent(grammarAccess.getRootNodeRule());
            	    						}
            	    						add(
            	    							current,
            	    							"ports",
            	    							lv_ports_5_0,
            	    							"org.eclipse.elk.graph.text.ElkGraph.ElkPort");
            	    						afterParserOrEnumRuleCall();
            	    					

            	    }


            	    }


            	    }
            	    break;
            	case 3 :
            	    // InternalElkGraph.g:174:4: ( (lv_children_6_0= ruleElkNode ) )
            	    {
            	    // InternalElkGraph.g:174:4: ( (lv_children_6_0= ruleElkNode ) )
            	    // InternalElkGraph.g:175:5: (lv_children_6_0= ruleElkNode )
            	    {
            	    // InternalElkGraph.g:175:5: (lv_children_6_0= ruleElkNode )
            	    // InternalElkGraph.g:176:6: lv_children_6_0= ruleElkNode
            	    {

            	    						newCompositeNode(grammarAccess.getRootNodeAccess().getChildrenElkNodeParserRuleCall_3_2_0());
            	    					
            	    pushFollow(FOLLOW_5);
            	    lv_children_6_0=ruleElkNode();

            	    state._fsp--;


            	    						if (current==null) {
            	    							current = createModelElementForParent(grammarAccess.getRootNodeRule());
            	    						}
            	    						add(
            	    							current,
            	    							"children",
            	    							lv_children_6_0,
            	    							"org.eclipse.elk.graph.text.ElkGraph.ElkNode");
            	    						afterParserOrEnumRuleCall();
            	    					

            	    }


            	    }


            	    }
            	    break;
            	case 4 :
            	    // InternalElkGraph.g:194:4: ( (lv_containedEdges_7_0= ruleElkEdge ) )
            	    {
            	    // InternalElkGraph.g:194:4: ( (lv_containedEdges_7_0= ruleElkEdge ) )
            	    // InternalElkGraph.g:195:5: (lv_containedEdges_7_0= ruleElkEdge )
            	    {
            	    // InternalElkGraph.g:195:5: (lv_containedEdges_7_0= ruleElkEdge )
            	    // InternalElkGraph.g:196:6: lv_containedEdges_7_0= ruleElkEdge
            	    {

            	    						newCompositeNode(grammarAccess.getRootNodeAccess().getContainedEdgesElkEdgeParserRuleCall_3_3_0());
            	    					
            	    pushFollow(FOLLOW_5);
            	    lv_containedEdges_7_0=ruleElkEdge();

            	    state._fsp--;


            	    						if (current==null) {
            	    							current = createModelElementForParent(grammarAccess.getRootNodeRule());
            	    						}
            	    						add(
            	    							current,
            	    							"containedEdges",
            	    							lv_containedEdges_7_0,
            	    							"org.eclipse.elk.graph.text.ElkGraph.ElkEdge");
            	    						afterParserOrEnumRuleCall();
            	    					

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleRootNode"


    // $ANTLR start "entryRuleElkNode"
    // InternalElkGraph.g:218:1: entryRuleElkNode returns [EObject current=null] : iv_ruleElkNode= ruleElkNode EOF ;
    public final EObject entryRuleElkNode() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleElkNode = null;


        try {
            // InternalElkGraph.g:218:48: (iv_ruleElkNode= ruleElkNode EOF )
            // InternalElkGraph.g:219:2: iv_ruleElkNode= ruleElkNode EOF
            {
             newCompositeNode(grammarAccess.getElkNodeRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleElkNode=ruleElkNode();

            state._fsp--;

             current =iv_ruleElkNode; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleElkNode"


    // $ANTLR start "ruleElkNode"
    // InternalElkGraph.g:225:1: ruleElkNode returns [EObject current=null] : (otherlv_0= 'node' ( (lv_identifier_1_0= RULE_ID ) ) (otherlv_2= '{' (this_ShapeLayout_3= ruleShapeLayout[$current] )? ( (lv_properties_4_0= ruleProperty ) )* ( ( (lv_labels_5_0= ruleElkLabel ) ) | ( (lv_ports_6_0= ruleElkPort ) ) | ( (lv_children_7_0= ruleElkNode ) ) | ( (lv_containedEdges_8_0= ruleElkEdge ) ) )* otherlv_9= '}' )? ) ;
    public final EObject ruleElkNode() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_identifier_1_0=null;
        Token otherlv_2=null;
        Token otherlv_9=null;
        EObject this_ShapeLayout_3 = null;

        EObject lv_properties_4_0 = null;

        EObject lv_labels_5_0 = null;

        EObject lv_ports_6_0 = null;

        EObject lv_children_7_0 = null;

        EObject lv_containedEdges_8_0 = null;



        	enterRule();

        try {
            // InternalElkGraph.g:231:2: ( (otherlv_0= 'node' ( (lv_identifier_1_0= RULE_ID ) ) (otherlv_2= '{' (this_ShapeLayout_3= ruleShapeLayout[$current] )? ( (lv_properties_4_0= ruleProperty ) )* ( ( (lv_labels_5_0= ruleElkLabel ) ) | ( (lv_ports_6_0= ruleElkPort ) ) | ( (lv_children_7_0= ruleElkNode ) ) | ( (lv_containedEdges_8_0= ruleElkEdge ) ) )* otherlv_9= '}' )? ) )
            // InternalElkGraph.g:232:2: (otherlv_0= 'node' ( (lv_identifier_1_0= RULE_ID ) ) (otherlv_2= '{' (this_ShapeLayout_3= ruleShapeLayout[$current] )? ( (lv_properties_4_0= ruleProperty ) )* ( ( (lv_labels_5_0= ruleElkLabel ) ) | ( (lv_ports_6_0= ruleElkPort ) ) | ( (lv_children_7_0= ruleElkNode ) ) | ( (lv_containedEdges_8_0= ruleElkEdge ) ) )* otherlv_9= '}' )? )
            {
            // InternalElkGraph.g:232:2: (otherlv_0= 'node' ( (lv_identifier_1_0= RULE_ID ) ) (otherlv_2= '{' (this_ShapeLayout_3= ruleShapeLayout[$current] )? ( (lv_properties_4_0= ruleProperty ) )* ( ( (lv_labels_5_0= ruleElkLabel ) ) | ( (lv_ports_6_0= ruleElkPort ) ) | ( (lv_children_7_0= ruleElkNode ) ) | ( (lv_containedEdges_8_0= ruleElkEdge ) ) )* otherlv_9= '}' )? )
            // InternalElkGraph.g:233:3: otherlv_0= 'node' ( (lv_identifier_1_0= RULE_ID ) ) (otherlv_2= '{' (this_ShapeLayout_3= ruleShapeLayout[$current] )? ( (lv_properties_4_0= ruleProperty ) )* ( ( (lv_labels_5_0= ruleElkLabel ) ) | ( (lv_ports_6_0= ruleElkPort ) ) | ( (lv_children_7_0= ruleElkNode ) ) | ( (lv_containedEdges_8_0= ruleElkEdge ) ) )* otherlv_9= '}' )?
            {
            otherlv_0=(Token)match(input,14,FOLLOW_3); 

            			newLeafNode(otherlv_0, grammarAccess.getElkNodeAccess().getNodeKeyword_0());
            		
            // InternalElkGraph.g:237:3: ( (lv_identifier_1_0= RULE_ID ) )
            // InternalElkGraph.g:238:4: (lv_identifier_1_0= RULE_ID )
            {
            // InternalElkGraph.g:238:4: (lv_identifier_1_0= RULE_ID )
            // InternalElkGraph.g:239:5: lv_identifier_1_0= RULE_ID
            {
            lv_identifier_1_0=(Token)match(input,RULE_ID,FOLLOW_6); 

            					newLeafNode(lv_identifier_1_0, grammarAccess.getElkNodeAccess().getIdentifierIDTerminalRuleCall_1_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getElkNodeRule());
            					}
            					setWithLastConsumed(
            						current,
            						"identifier",
            						lv_identifier_1_0,
            						"org.eclipse.xtext.common.Terminals.ID");
            				

            }


            }

            // InternalElkGraph.g:255:3: (otherlv_2= '{' (this_ShapeLayout_3= ruleShapeLayout[$current] )? ( (lv_properties_4_0= ruleProperty ) )* ( ( (lv_labels_5_0= ruleElkLabel ) ) | ( (lv_ports_6_0= ruleElkPort ) ) | ( (lv_children_7_0= ruleElkNode ) ) | ( (lv_containedEdges_8_0= ruleElkEdge ) ) )* otherlv_9= '}' )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==15) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // InternalElkGraph.g:256:4: otherlv_2= '{' (this_ShapeLayout_3= ruleShapeLayout[$current] )? ( (lv_properties_4_0= ruleProperty ) )* ( ( (lv_labels_5_0= ruleElkLabel ) ) | ( (lv_ports_6_0= ruleElkPort ) ) | ( (lv_children_7_0= ruleElkNode ) ) | ( (lv_containedEdges_8_0= ruleElkEdge ) ) )* otherlv_9= '}'
                    {
                    otherlv_2=(Token)match(input,15,FOLLOW_7); 

                    				newLeafNode(otherlv_2, grammarAccess.getElkNodeAccess().getLeftCurlyBracketKeyword_2_0());
                    			
                    // InternalElkGraph.g:260:4: (this_ShapeLayout_3= ruleShapeLayout[$current] )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==20) ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // InternalElkGraph.g:261:5: this_ShapeLayout_3= ruleShapeLayout[$current]
                            {

                            					if (current==null) {
                            						current = createModelElement(grammarAccess.getElkNodeRule());
                            					}
                            					newCompositeNode(grammarAccess.getElkNodeAccess().getShapeLayoutParserRuleCall_2_1());
                            				
                            pushFollow(FOLLOW_8);
                            this_ShapeLayout_3=ruleShapeLayout(current);

                            state._fsp--;


                            					current = this_ShapeLayout_3;
                            					afterParserOrEnumRuleCall();
                            				

                            }
                            break;

                    }

                    // InternalElkGraph.g:273:4: ( (lv_properties_4_0= ruleProperty ) )*
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( (LA5_0==RULE_ID) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // InternalElkGraph.g:274:5: (lv_properties_4_0= ruleProperty )
                    	    {
                    	    // InternalElkGraph.g:274:5: (lv_properties_4_0= ruleProperty )
                    	    // InternalElkGraph.g:275:6: lv_properties_4_0= ruleProperty
                    	    {

                    	    						newCompositeNode(grammarAccess.getElkNodeAccess().getPropertiesPropertyParserRuleCall_2_2_0());
                    	    					
                    	    pushFollow(FOLLOW_8);
                    	    lv_properties_4_0=ruleProperty();

                    	    state._fsp--;


                    	    						if (current==null) {
                    	    							current = createModelElementForParent(grammarAccess.getElkNodeRule());
                    	    						}
                    	    						add(
                    	    							current,
                    	    							"properties",
                    	    							lv_properties_4_0,
                    	    							"org.eclipse.elk.graph.text.ElkGraph.Property");
                    	    						afterParserOrEnumRuleCall();
                    	    					

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop5;
                        }
                    } while (true);

                    // InternalElkGraph.g:292:4: ( ( (lv_labels_5_0= ruleElkLabel ) ) | ( (lv_ports_6_0= ruleElkPort ) ) | ( (lv_children_7_0= ruleElkNode ) ) | ( (lv_containedEdges_8_0= ruleElkEdge ) ) )*
                    loop6:
                    do {
                        int alt6=5;
                        switch ( input.LA(1) ) {
                        case 17:
                            {
                            alt6=1;
                            }
                            break;
                        case 19:
                            {
                            alt6=2;
                            }
                            break;
                        case 14:
                            {
                            alt6=3;
                            }
                            break;
                        case 26:
                            {
                            alt6=4;
                            }
                            break;

                        }

                        switch (alt6) {
                    	case 1 :
                    	    // InternalElkGraph.g:293:5: ( (lv_labels_5_0= ruleElkLabel ) )
                    	    {
                    	    // InternalElkGraph.g:293:5: ( (lv_labels_5_0= ruleElkLabel ) )
                    	    // InternalElkGraph.g:294:6: (lv_labels_5_0= ruleElkLabel )
                    	    {
                    	    // InternalElkGraph.g:294:6: (lv_labels_5_0= ruleElkLabel )
                    	    // InternalElkGraph.g:295:7: lv_labels_5_0= ruleElkLabel
                    	    {

                    	    							newCompositeNode(grammarAccess.getElkNodeAccess().getLabelsElkLabelParserRuleCall_2_3_0_0());
                    	    						
                    	    pushFollow(FOLLOW_9);
                    	    lv_labels_5_0=ruleElkLabel();

                    	    state._fsp--;


                    	    							if (current==null) {
                    	    								current = createModelElementForParent(grammarAccess.getElkNodeRule());
                    	    							}
                    	    							add(
                    	    								current,
                    	    								"labels",
                    	    								lv_labels_5_0,
                    	    								"org.eclipse.elk.graph.text.ElkGraph.ElkLabel");
                    	    							afterParserOrEnumRuleCall();
                    	    						

                    	    }


                    	    }


                    	    }
                    	    break;
                    	case 2 :
                    	    // InternalElkGraph.g:313:5: ( (lv_ports_6_0= ruleElkPort ) )
                    	    {
                    	    // InternalElkGraph.g:313:5: ( (lv_ports_6_0= ruleElkPort ) )
                    	    // InternalElkGraph.g:314:6: (lv_ports_6_0= ruleElkPort )
                    	    {
                    	    // InternalElkGraph.g:314:6: (lv_ports_6_0= ruleElkPort )
                    	    // InternalElkGraph.g:315:7: lv_ports_6_0= ruleElkPort
                    	    {

                    	    							newCompositeNode(grammarAccess.getElkNodeAccess().getPortsElkPortParserRuleCall_2_3_1_0());
                    	    						
                    	    pushFollow(FOLLOW_9);
                    	    lv_ports_6_0=ruleElkPort();

                    	    state._fsp--;


                    	    							if (current==null) {
                    	    								current = createModelElementForParent(grammarAccess.getElkNodeRule());
                    	    							}
                    	    							add(
                    	    								current,
                    	    								"ports",
                    	    								lv_ports_6_0,
                    	    								"org.eclipse.elk.graph.text.ElkGraph.ElkPort");
                    	    							afterParserOrEnumRuleCall();
                    	    						

                    	    }


                    	    }


                    	    }
                    	    break;
                    	case 3 :
                    	    // InternalElkGraph.g:333:5: ( (lv_children_7_0= ruleElkNode ) )
                    	    {
                    	    // InternalElkGraph.g:333:5: ( (lv_children_7_0= ruleElkNode ) )
                    	    // InternalElkGraph.g:334:6: (lv_children_7_0= ruleElkNode )
                    	    {
                    	    // InternalElkGraph.g:334:6: (lv_children_7_0= ruleElkNode )
                    	    // InternalElkGraph.g:335:7: lv_children_7_0= ruleElkNode
                    	    {

                    	    							newCompositeNode(grammarAccess.getElkNodeAccess().getChildrenElkNodeParserRuleCall_2_3_2_0());
                    	    						
                    	    pushFollow(FOLLOW_9);
                    	    lv_children_7_0=ruleElkNode();

                    	    state._fsp--;


                    	    							if (current==null) {
                    	    								current = createModelElementForParent(grammarAccess.getElkNodeRule());
                    	    							}
                    	    							add(
                    	    								current,
                    	    								"children",
                    	    								lv_children_7_0,
                    	    								"org.eclipse.elk.graph.text.ElkGraph.ElkNode");
                    	    							afterParserOrEnumRuleCall();
                    	    						

                    	    }


                    	    }


                    	    }
                    	    break;
                    	case 4 :
                    	    // InternalElkGraph.g:353:5: ( (lv_containedEdges_8_0= ruleElkEdge ) )
                    	    {
                    	    // InternalElkGraph.g:353:5: ( (lv_containedEdges_8_0= ruleElkEdge ) )
                    	    // InternalElkGraph.g:354:6: (lv_containedEdges_8_0= ruleElkEdge )
                    	    {
                    	    // InternalElkGraph.g:354:6: (lv_containedEdges_8_0= ruleElkEdge )
                    	    // InternalElkGraph.g:355:7: lv_containedEdges_8_0= ruleElkEdge
                    	    {

                    	    							newCompositeNode(grammarAccess.getElkNodeAccess().getContainedEdgesElkEdgeParserRuleCall_2_3_3_0());
                    	    						
                    	    pushFollow(FOLLOW_9);
                    	    lv_containedEdges_8_0=ruleElkEdge();

                    	    state._fsp--;


                    	    							if (current==null) {
                    	    								current = createModelElementForParent(grammarAccess.getElkNodeRule());
                    	    							}
                    	    							add(
                    	    								current,
                    	    								"containedEdges",
                    	    								lv_containedEdges_8_0,
                    	    								"org.eclipse.elk.graph.text.ElkGraph.ElkEdge");
                    	    							afterParserOrEnumRuleCall();
                    	    						

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);

                    otherlv_9=(Token)match(input,16,FOLLOW_2); 

                    				newLeafNode(otherlv_9, grammarAccess.getElkNodeAccess().getRightCurlyBracketKeyword_2_4());
                    			

                    }
                    break;

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleElkNode"


    // $ANTLR start "entryRuleElkLabel"
    // InternalElkGraph.g:382:1: entryRuleElkLabel returns [EObject current=null] : iv_ruleElkLabel= ruleElkLabel EOF ;
    public final EObject entryRuleElkLabel() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleElkLabel = null;


        try {
            // InternalElkGraph.g:382:49: (iv_ruleElkLabel= ruleElkLabel EOF )
            // InternalElkGraph.g:383:2: iv_ruleElkLabel= ruleElkLabel EOF
            {
             newCompositeNode(grammarAccess.getElkLabelRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleElkLabel=ruleElkLabel();

            state._fsp--;

             current =iv_ruleElkLabel; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleElkLabel"


    // $ANTLR start "ruleElkLabel"
    // InternalElkGraph.g:389:1: ruleElkLabel returns [EObject current=null] : (otherlv_0= 'label' ( ( (lv_identifier_1_0= RULE_ID ) ) otherlv_2= ':' )? ( (lv_text_3_0= RULE_STRING ) ) (otherlv_4= '{' (this_ShapeLayout_5= ruleShapeLayout[$current] )? ( (lv_properties_6_0= ruleProperty ) )* ( (lv_labels_7_0= ruleElkLabel ) )* otherlv_8= '}' )? ) ;
    public final EObject ruleElkLabel() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_identifier_1_0=null;
        Token otherlv_2=null;
        Token lv_text_3_0=null;
        Token otherlv_4=null;
        Token otherlv_8=null;
        EObject this_ShapeLayout_5 = null;

        EObject lv_properties_6_0 = null;

        EObject lv_labels_7_0 = null;



        	enterRule();

        try {
            // InternalElkGraph.g:395:2: ( (otherlv_0= 'label' ( ( (lv_identifier_1_0= RULE_ID ) ) otherlv_2= ':' )? ( (lv_text_3_0= RULE_STRING ) ) (otherlv_4= '{' (this_ShapeLayout_5= ruleShapeLayout[$current] )? ( (lv_properties_6_0= ruleProperty ) )* ( (lv_labels_7_0= ruleElkLabel ) )* otherlv_8= '}' )? ) )
            // InternalElkGraph.g:396:2: (otherlv_0= 'label' ( ( (lv_identifier_1_0= RULE_ID ) ) otherlv_2= ':' )? ( (lv_text_3_0= RULE_STRING ) ) (otherlv_4= '{' (this_ShapeLayout_5= ruleShapeLayout[$current] )? ( (lv_properties_6_0= ruleProperty ) )* ( (lv_labels_7_0= ruleElkLabel ) )* otherlv_8= '}' )? )
            {
            // InternalElkGraph.g:396:2: (otherlv_0= 'label' ( ( (lv_identifier_1_0= RULE_ID ) ) otherlv_2= ':' )? ( (lv_text_3_0= RULE_STRING ) ) (otherlv_4= '{' (this_ShapeLayout_5= ruleShapeLayout[$current] )? ( (lv_properties_6_0= ruleProperty ) )* ( (lv_labels_7_0= ruleElkLabel ) )* otherlv_8= '}' )? )
            // InternalElkGraph.g:397:3: otherlv_0= 'label' ( ( (lv_identifier_1_0= RULE_ID ) ) otherlv_2= ':' )? ( (lv_text_3_0= RULE_STRING ) ) (otherlv_4= '{' (this_ShapeLayout_5= ruleShapeLayout[$current] )? ( (lv_properties_6_0= ruleProperty ) )* ( (lv_labels_7_0= ruleElkLabel ) )* otherlv_8= '}' )?
            {
            otherlv_0=(Token)match(input,17,FOLLOW_10); 

            			newLeafNode(otherlv_0, grammarAccess.getElkLabelAccess().getLabelKeyword_0());
            		
            // InternalElkGraph.g:401:3: ( ( (lv_identifier_1_0= RULE_ID ) ) otherlv_2= ':' )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==RULE_ID) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // InternalElkGraph.g:402:4: ( (lv_identifier_1_0= RULE_ID ) ) otherlv_2= ':'
                    {
                    // InternalElkGraph.g:402:4: ( (lv_identifier_1_0= RULE_ID ) )
                    // InternalElkGraph.g:403:5: (lv_identifier_1_0= RULE_ID )
                    {
                    // InternalElkGraph.g:403:5: (lv_identifier_1_0= RULE_ID )
                    // InternalElkGraph.g:404:6: lv_identifier_1_0= RULE_ID
                    {
                    lv_identifier_1_0=(Token)match(input,RULE_ID,FOLLOW_11); 

                    						newLeafNode(lv_identifier_1_0, grammarAccess.getElkLabelAccess().getIdentifierIDTerminalRuleCall_1_0_0());
                    					

                    						if (current==null) {
                    							current = createModelElement(grammarAccess.getElkLabelRule());
                    						}
                    						setWithLastConsumed(
                    							current,
                    							"identifier",
                    							lv_identifier_1_0,
                    							"org.eclipse.xtext.common.Terminals.ID");
                    					

                    }


                    }

                    otherlv_2=(Token)match(input,18,FOLLOW_12); 

                    				newLeafNode(otherlv_2, grammarAccess.getElkLabelAccess().getColonKeyword_1_1());
                    			

                    }
                    break;

            }

            // InternalElkGraph.g:425:3: ( (lv_text_3_0= RULE_STRING ) )
            // InternalElkGraph.g:426:4: (lv_text_3_0= RULE_STRING )
            {
            // InternalElkGraph.g:426:4: (lv_text_3_0= RULE_STRING )
            // InternalElkGraph.g:427:5: lv_text_3_0= RULE_STRING
            {
            lv_text_3_0=(Token)match(input,RULE_STRING,FOLLOW_6); 

            					newLeafNode(lv_text_3_0, grammarAccess.getElkLabelAccess().getTextSTRINGTerminalRuleCall_2_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getElkLabelRule());
            					}
            					setWithLastConsumed(
            						current,
            						"text",
            						lv_text_3_0,
            						"org.eclipse.xtext.common.Terminals.STRING");
            				

            }


            }

            // InternalElkGraph.g:443:3: (otherlv_4= '{' (this_ShapeLayout_5= ruleShapeLayout[$current] )? ( (lv_properties_6_0= ruleProperty ) )* ( (lv_labels_7_0= ruleElkLabel ) )* otherlv_8= '}' )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==15) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // InternalElkGraph.g:444:4: otherlv_4= '{' (this_ShapeLayout_5= ruleShapeLayout[$current] )? ( (lv_properties_6_0= ruleProperty ) )* ( (lv_labels_7_0= ruleElkLabel ) )* otherlv_8= '}'
                    {
                    otherlv_4=(Token)match(input,15,FOLLOW_13); 

                    				newLeafNode(otherlv_4, grammarAccess.getElkLabelAccess().getLeftCurlyBracketKeyword_3_0());
                    			
                    // InternalElkGraph.g:448:4: (this_ShapeLayout_5= ruleShapeLayout[$current] )?
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==20) ) {
                        alt9=1;
                    }
                    switch (alt9) {
                        case 1 :
                            // InternalElkGraph.g:449:5: this_ShapeLayout_5= ruleShapeLayout[$current]
                            {

                            					if (current==null) {
                            						current = createModelElement(grammarAccess.getElkLabelRule());
                            					}
                            					newCompositeNode(grammarAccess.getElkLabelAccess().getShapeLayoutParserRuleCall_3_1());
                            				
                            pushFollow(FOLLOW_14);
                            this_ShapeLayout_5=ruleShapeLayout(current);

                            state._fsp--;


                            					current = this_ShapeLayout_5;
                            					afterParserOrEnumRuleCall();
                            				

                            }
                            break;

                    }

                    // InternalElkGraph.g:461:4: ( (lv_properties_6_0= ruleProperty ) )*
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( (LA10_0==RULE_ID) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // InternalElkGraph.g:462:5: (lv_properties_6_0= ruleProperty )
                    	    {
                    	    // InternalElkGraph.g:462:5: (lv_properties_6_0= ruleProperty )
                    	    // InternalElkGraph.g:463:6: lv_properties_6_0= ruleProperty
                    	    {

                    	    						newCompositeNode(grammarAccess.getElkLabelAccess().getPropertiesPropertyParserRuleCall_3_2_0());
                    	    					
                    	    pushFollow(FOLLOW_14);
                    	    lv_properties_6_0=ruleProperty();

                    	    state._fsp--;


                    	    						if (current==null) {
                    	    							current = createModelElementForParent(grammarAccess.getElkLabelRule());
                    	    						}
                    	    						add(
                    	    							current,
                    	    							"properties",
                    	    							lv_properties_6_0,
                    	    							"org.eclipse.elk.graph.text.ElkGraph.Property");
                    	    						afterParserOrEnumRuleCall();
                    	    					

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop10;
                        }
                    } while (true);

                    // InternalElkGraph.g:480:4: ( (lv_labels_7_0= ruleElkLabel ) )*
                    loop11:
                    do {
                        int alt11=2;
                        int LA11_0 = input.LA(1);

                        if ( (LA11_0==17) ) {
                            alt11=1;
                        }


                        switch (alt11) {
                    	case 1 :
                    	    // InternalElkGraph.g:481:5: (lv_labels_7_0= ruleElkLabel )
                    	    {
                    	    // InternalElkGraph.g:481:5: (lv_labels_7_0= ruleElkLabel )
                    	    // InternalElkGraph.g:482:6: lv_labels_7_0= ruleElkLabel
                    	    {

                    	    						newCompositeNode(grammarAccess.getElkLabelAccess().getLabelsElkLabelParserRuleCall_3_3_0());
                    	    					
                    	    pushFollow(FOLLOW_15);
                    	    lv_labels_7_0=ruleElkLabel();

                    	    state._fsp--;


                    	    						if (current==null) {
                    	    							current = createModelElementForParent(grammarAccess.getElkLabelRule());
                    	    						}
                    	    						add(
                    	    							current,
                    	    							"labels",
                    	    							lv_labels_7_0,
                    	    							"org.eclipse.elk.graph.text.ElkGraph.ElkLabel");
                    	    						afterParserOrEnumRuleCall();
                    	    					

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop11;
                        }
                    } while (true);

                    otherlv_8=(Token)match(input,16,FOLLOW_2); 

                    				newLeafNode(otherlv_8, grammarAccess.getElkLabelAccess().getRightCurlyBracketKeyword_3_4());
                    			

                    }
                    break;

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleElkLabel"


    // $ANTLR start "entryRuleElkPort"
    // InternalElkGraph.g:508:1: entryRuleElkPort returns [EObject current=null] : iv_ruleElkPort= ruleElkPort EOF ;
    public final EObject entryRuleElkPort() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleElkPort = null;


        try {
            // InternalElkGraph.g:508:48: (iv_ruleElkPort= ruleElkPort EOF )
            // InternalElkGraph.g:509:2: iv_ruleElkPort= ruleElkPort EOF
            {
             newCompositeNode(grammarAccess.getElkPortRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleElkPort=ruleElkPort();

            state._fsp--;

             current =iv_ruleElkPort; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleElkPort"


    // $ANTLR start "ruleElkPort"
    // InternalElkGraph.g:515:1: ruleElkPort returns [EObject current=null] : (otherlv_0= 'port' ( (lv_identifier_1_0= RULE_ID ) ) (otherlv_2= '{' (this_ShapeLayout_3= ruleShapeLayout[$current] )? ( (lv_properties_4_0= ruleProperty ) )* ( (lv_labels_5_0= ruleElkLabel ) )* otherlv_6= '}' )? ) ;
    public final EObject ruleElkPort() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_identifier_1_0=null;
        Token otherlv_2=null;
        Token otherlv_6=null;
        EObject this_ShapeLayout_3 = null;

        EObject lv_properties_4_0 = null;

        EObject lv_labels_5_0 = null;



        	enterRule();

        try {
            // InternalElkGraph.g:521:2: ( (otherlv_0= 'port' ( (lv_identifier_1_0= RULE_ID ) ) (otherlv_2= '{' (this_ShapeLayout_3= ruleShapeLayout[$current] )? ( (lv_properties_4_0= ruleProperty ) )* ( (lv_labels_5_0= ruleElkLabel ) )* otherlv_6= '}' )? ) )
            // InternalElkGraph.g:522:2: (otherlv_0= 'port' ( (lv_identifier_1_0= RULE_ID ) ) (otherlv_2= '{' (this_ShapeLayout_3= ruleShapeLayout[$current] )? ( (lv_properties_4_0= ruleProperty ) )* ( (lv_labels_5_0= ruleElkLabel ) )* otherlv_6= '}' )? )
            {
            // InternalElkGraph.g:522:2: (otherlv_0= 'port' ( (lv_identifier_1_0= RULE_ID ) ) (otherlv_2= '{' (this_ShapeLayout_3= ruleShapeLayout[$current] )? ( (lv_properties_4_0= ruleProperty ) )* ( (lv_labels_5_0= ruleElkLabel ) )* otherlv_6= '}' )? )
            // InternalElkGraph.g:523:3: otherlv_0= 'port' ( (lv_identifier_1_0= RULE_ID ) ) (otherlv_2= '{' (this_ShapeLayout_3= ruleShapeLayout[$current] )? ( (lv_properties_4_0= ruleProperty ) )* ( (lv_labels_5_0= ruleElkLabel ) )* otherlv_6= '}' )?
            {
            otherlv_0=(Token)match(input,19,FOLLOW_3); 

            			newLeafNode(otherlv_0, grammarAccess.getElkPortAccess().getPortKeyword_0());
            		
            // InternalElkGraph.g:527:3: ( (lv_identifier_1_0= RULE_ID ) )
            // InternalElkGraph.g:528:4: (lv_identifier_1_0= RULE_ID )
            {
            // InternalElkGraph.g:528:4: (lv_identifier_1_0= RULE_ID )
            // InternalElkGraph.g:529:5: lv_identifier_1_0= RULE_ID
            {
            lv_identifier_1_0=(Token)match(input,RULE_ID,FOLLOW_6); 

            					newLeafNode(lv_identifier_1_0, grammarAccess.getElkPortAccess().getIdentifierIDTerminalRuleCall_1_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getElkPortRule());
            					}
            					setWithLastConsumed(
            						current,
            						"identifier",
            						lv_identifier_1_0,
            						"org.eclipse.xtext.common.Terminals.ID");
            				

            }


            }

            // InternalElkGraph.g:545:3: (otherlv_2= '{' (this_ShapeLayout_3= ruleShapeLayout[$current] )? ( (lv_properties_4_0= ruleProperty ) )* ( (lv_labels_5_0= ruleElkLabel ) )* otherlv_6= '}' )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==15) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // InternalElkGraph.g:546:4: otherlv_2= '{' (this_ShapeLayout_3= ruleShapeLayout[$current] )? ( (lv_properties_4_0= ruleProperty ) )* ( (lv_labels_5_0= ruleElkLabel ) )* otherlv_6= '}'
                    {
                    otherlv_2=(Token)match(input,15,FOLLOW_13); 

                    				newLeafNode(otherlv_2, grammarAccess.getElkPortAccess().getLeftCurlyBracketKeyword_2_0());
                    			
                    // InternalElkGraph.g:550:4: (this_ShapeLayout_3= ruleShapeLayout[$current] )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0==20) ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // InternalElkGraph.g:551:5: this_ShapeLayout_3= ruleShapeLayout[$current]
                            {

                            					if (current==null) {
                            						current = createModelElement(grammarAccess.getElkPortRule());
                            					}
                            					newCompositeNode(grammarAccess.getElkPortAccess().getShapeLayoutParserRuleCall_2_1());
                            				
                            pushFollow(FOLLOW_14);
                            this_ShapeLayout_3=ruleShapeLayout(current);

                            state._fsp--;


                            					current = this_ShapeLayout_3;
                            					afterParserOrEnumRuleCall();
                            				

                            }
                            break;

                    }

                    // InternalElkGraph.g:563:4: ( (lv_properties_4_0= ruleProperty ) )*
                    loop14:
                    do {
                        int alt14=2;
                        int LA14_0 = input.LA(1);

                        if ( (LA14_0==RULE_ID) ) {
                            alt14=1;
                        }


                        switch (alt14) {
                    	case 1 :
                    	    // InternalElkGraph.g:564:5: (lv_properties_4_0= ruleProperty )
                    	    {
                    	    // InternalElkGraph.g:564:5: (lv_properties_4_0= ruleProperty )
                    	    // InternalElkGraph.g:565:6: lv_properties_4_0= ruleProperty
                    	    {

                    	    						newCompositeNode(grammarAccess.getElkPortAccess().getPropertiesPropertyParserRuleCall_2_2_0());
                    	    					
                    	    pushFollow(FOLLOW_14);
                    	    lv_properties_4_0=ruleProperty();

                    	    state._fsp--;


                    	    						if (current==null) {
                    	    							current = createModelElementForParent(grammarAccess.getElkPortRule());
                    	    						}
                    	    						add(
                    	    							current,
                    	    							"properties",
                    	    							lv_properties_4_0,
                    	    							"org.eclipse.elk.graph.text.ElkGraph.Property");
                    	    						afterParserOrEnumRuleCall();
                    	    					

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop14;
                        }
                    } while (true);

                    // InternalElkGraph.g:582:4: ( (lv_labels_5_0= ruleElkLabel ) )*
                    loop15:
                    do {
                        int alt15=2;
                        int LA15_0 = input.LA(1);

                        if ( (LA15_0==17) ) {
                            alt15=1;
                        }


                        switch (alt15) {
                    	case 1 :
                    	    // InternalElkGraph.g:583:5: (lv_labels_5_0= ruleElkLabel )
                    	    {
                    	    // InternalElkGraph.g:583:5: (lv_labels_5_0= ruleElkLabel )
                    	    // InternalElkGraph.g:584:6: lv_labels_5_0= ruleElkLabel
                    	    {

                    	    						newCompositeNode(grammarAccess.getElkPortAccess().getLabelsElkLabelParserRuleCall_2_3_0());
                    	    					
                    	    pushFollow(FOLLOW_15);
                    	    lv_labels_5_0=ruleElkLabel();

                    	    state._fsp--;


                    	    						if (current==null) {
                    	    							current = createModelElementForParent(grammarAccess.getElkPortRule());
                    	    						}
                    	    						add(
                    	    							current,
                    	    							"labels",
                    	    							lv_labels_5_0,
                    	    							"org.eclipse.elk.graph.text.ElkGraph.ElkLabel");
                    	    						afterParserOrEnumRuleCall();
                    	    					

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop15;
                        }
                    } while (true);

                    otherlv_6=(Token)match(input,16,FOLLOW_2); 

                    				newLeafNode(otherlv_6, grammarAccess.getElkPortAccess().getRightCurlyBracketKeyword_2_4());
                    			

                    }
                    break;

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleElkPort"


    // $ANTLR start "ruleShapeLayout"
    // InternalElkGraph.g:611:1: ruleShapeLayout[EObject in_current] returns [EObject current=in_current] : (otherlv_0= 'layout' otherlv_1= '[' ( ( ( ( ({...}? => ( ({...}? => (otherlv_3= 'position' otherlv_4= ':' ( (lv_x_5_0= ruleNumber ) ) otherlv_6= ',' ( (lv_y_7_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= 'size' otherlv_9= ':' ( (lv_width_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_height_12_0= ruleNumber ) ) ) ) ) ) )* ) ) ) otherlv_13= ']' ) ;
    public final EObject ruleShapeLayout(EObject in_current) throws RecognitionException {
        EObject current = in_current;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Token otherlv_6=null;
        Token otherlv_8=null;
        Token otherlv_9=null;
        Token otherlv_11=null;
        Token otherlv_13=null;
        AntlrDatatypeRuleToken lv_x_5_0 = null;

        AntlrDatatypeRuleToken lv_y_7_0 = null;

        AntlrDatatypeRuleToken lv_width_10_0 = null;

        AntlrDatatypeRuleToken lv_height_12_0 = null;



        	enterRule();

        try {
            // InternalElkGraph.g:617:2: ( (otherlv_0= 'layout' otherlv_1= '[' ( ( ( ( ({...}? => ( ({...}? => (otherlv_3= 'position' otherlv_4= ':' ( (lv_x_5_0= ruleNumber ) ) otherlv_6= ',' ( (lv_y_7_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= 'size' otherlv_9= ':' ( (lv_width_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_height_12_0= ruleNumber ) ) ) ) ) ) )* ) ) ) otherlv_13= ']' ) )
            // InternalElkGraph.g:618:2: (otherlv_0= 'layout' otherlv_1= '[' ( ( ( ( ({...}? => ( ({...}? => (otherlv_3= 'position' otherlv_4= ':' ( (lv_x_5_0= ruleNumber ) ) otherlv_6= ',' ( (lv_y_7_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= 'size' otherlv_9= ':' ( (lv_width_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_height_12_0= ruleNumber ) ) ) ) ) ) )* ) ) ) otherlv_13= ']' )
            {
            // InternalElkGraph.g:618:2: (otherlv_0= 'layout' otherlv_1= '[' ( ( ( ( ({...}? => ( ({...}? => (otherlv_3= 'position' otherlv_4= ':' ( (lv_x_5_0= ruleNumber ) ) otherlv_6= ',' ( (lv_y_7_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= 'size' otherlv_9= ':' ( (lv_width_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_height_12_0= ruleNumber ) ) ) ) ) ) )* ) ) ) otherlv_13= ']' )
            // InternalElkGraph.g:619:3: otherlv_0= 'layout' otherlv_1= '[' ( ( ( ( ({...}? => ( ({...}? => (otherlv_3= 'position' otherlv_4= ':' ( (lv_x_5_0= ruleNumber ) ) otherlv_6= ',' ( (lv_y_7_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= 'size' otherlv_9= ':' ( (lv_width_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_height_12_0= ruleNumber ) ) ) ) ) ) )* ) ) ) otherlv_13= ']'
            {
            otherlv_0=(Token)match(input,20,FOLLOW_16); 

            			newLeafNode(otherlv_0, grammarAccess.getShapeLayoutAccess().getLayoutKeyword_0());
            		
            otherlv_1=(Token)match(input,21,FOLLOW_17); 

            			newLeafNode(otherlv_1, grammarAccess.getShapeLayoutAccess().getLeftSquareBracketKeyword_1());
            		
            // InternalElkGraph.g:627:3: ( ( ( ( ({...}? => ( ({...}? => (otherlv_3= 'position' otherlv_4= ':' ( (lv_x_5_0= ruleNumber ) ) otherlv_6= ',' ( (lv_y_7_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= 'size' otherlv_9= ':' ( (lv_width_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_height_12_0= ruleNumber ) ) ) ) ) ) )* ) ) )
            // InternalElkGraph.g:628:4: ( ( ( ({...}? => ( ({...}? => (otherlv_3= 'position' otherlv_4= ':' ( (lv_x_5_0= ruleNumber ) ) otherlv_6= ',' ( (lv_y_7_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= 'size' otherlv_9= ':' ( (lv_width_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_height_12_0= ruleNumber ) ) ) ) ) ) )* ) )
            {
            // InternalElkGraph.g:628:4: ( ( ( ({...}? => ( ({...}? => (otherlv_3= 'position' otherlv_4= ':' ( (lv_x_5_0= ruleNumber ) ) otherlv_6= ',' ( (lv_y_7_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= 'size' otherlv_9= ':' ( (lv_width_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_height_12_0= ruleNumber ) ) ) ) ) ) )* ) )
            // InternalElkGraph.g:629:5: ( ( ({...}? => ( ({...}? => (otherlv_3= 'position' otherlv_4= ':' ( (lv_x_5_0= ruleNumber ) ) otherlv_6= ',' ( (lv_y_7_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= 'size' otherlv_9= ':' ( (lv_width_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_height_12_0= ruleNumber ) ) ) ) ) ) )* )
            {
             
            				  getUnorderedGroupHelper().enter(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2());
            				
            // InternalElkGraph.g:632:5: ( ( ({...}? => ( ({...}? => (otherlv_3= 'position' otherlv_4= ':' ( (lv_x_5_0= ruleNumber ) ) otherlv_6= ',' ( (lv_y_7_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= 'size' otherlv_9= ':' ( (lv_width_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_height_12_0= ruleNumber ) ) ) ) ) ) )* )
            // InternalElkGraph.g:633:6: ( ({...}? => ( ({...}? => (otherlv_3= 'position' otherlv_4= ':' ( (lv_x_5_0= ruleNumber ) ) otherlv_6= ',' ( (lv_y_7_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= 'size' otherlv_9= ':' ( (lv_width_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_height_12_0= ruleNumber ) ) ) ) ) ) )*
            {
            // InternalElkGraph.g:633:6: ( ({...}? => ( ({...}? => (otherlv_3= 'position' otherlv_4= ':' ( (lv_x_5_0= ruleNumber ) ) otherlv_6= ',' ( (lv_y_7_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= 'size' otherlv_9= ':' ( (lv_width_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_height_12_0= ruleNumber ) ) ) ) ) ) )*
            loop17:
            do {
                int alt17=3;
                int LA17_0 = input.LA(1);

                if ( LA17_0 == 22 && getUnorderedGroupHelper().canSelect(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2(), 0) ) {
                    alt17=1;
                }
                else if ( LA17_0 == 24 && getUnorderedGroupHelper().canSelect(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2(), 1) ) {
                    alt17=2;
                }


                switch (alt17) {
            	case 1 :
            	    // InternalElkGraph.g:634:4: ({...}? => ( ({...}? => (otherlv_3= 'position' otherlv_4= ':' ( (lv_x_5_0= ruleNumber ) ) otherlv_6= ',' ( (lv_y_7_0= ruleNumber ) ) ) ) ) )
            	    {
            	    // InternalElkGraph.g:634:4: ({...}? => ( ({...}? => (otherlv_3= 'position' otherlv_4= ':' ( (lv_x_5_0= ruleNumber ) ) otherlv_6= ',' ( (lv_y_7_0= ruleNumber ) ) ) ) ) )
            	    // InternalElkGraph.g:635:5: {...}? => ( ({...}? => (otherlv_3= 'position' otherlv_4= ':' ( (lv_x_5_0= ruleNumber ) ) otherlv_6= ',' ( (lv_y_7_0= ruleNumber ) ) ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2(), 0) ) {
            	        throw new FailedPredicateException(input, "ruleShapeLayout", "getUnorderedGroupHelper().canSelect(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2(), 0)");
            	    }
            	    // InternalElkGraph.g:635:108: ( ({...}? => (otherlv_3= 'position' otherlv_4= ':' ( (lv_x_5_0= ruleNumber ) ) otherlv_6= ',' ( (lv_y_7_0= ruleNumber ) ) ) ) )
            	    // InternalElkGraph.g:636:6: ({...}? => (otherlv_3= 'position' otherlv_4= ':' ( (lv_x_5_0= ruleNumber ) ) otherlv_6= ',' ( (lv_y_7_0= ruleNumber ) ) ) )
            	    {

            	    						getUnorderedGroupHelper().select(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2(), 0);
            	    					
            	    // InternalElkGraph.g:639:9: ({...}? => (otherlv_3= 'position' otherlv_4= ':' ( (lv_x_5_0= ruleNumber ) ) otherlv_6= ',' ( (lv_y_7_0= ruleNumber ) ) ) )
            	    // InternalElkGraph.g:639:10: {...}? => (otherlv_3= 'position' otherlv_4= ':' ( (lv_x_5_0= ruleNumber ) ) otherlv_6= ',' ( (lv_y_7_0= ruleNumber ) ) )
            	    {
            	    if ( !((true)) ) {
            	        throw new FailedPredicateException(input, "ruleShapeLayout", "true");
            	    }
            	    // InternalElkGraph.g:639:19: (otherlv_3= 'position' otherlv_4= ':' ( (lv_x_5_0= ruleNumber ) ) otherlv_6= ',' ( (lv_y_7_0= ruleNumber ) ) )
            	    // InternalElkGraph.g:639:20: otherlv_3= 'position' otherlv_4= ':' ( (lv_x_5_0= ruleNumber ) ) otherlv_6= ',' ( (lv_y_7_0= ruleNumber ) )
            	    {
            	    otherlv_3=(Token)match(input,22,FOLLOW_11); 

            	    									newLeafNode(otherlv_3, grammarAccess.getShapeLayoutAccess().getPositionKeyword_2_0_0());
            	    								
            	    otherlv_4=(Token)match(input,18,FOLLOW_18); 

            	    									newLeafNode(otherlv_4, grammarAccess.getShapeLayoutAccess().getColonKeyword_2_0_1());
            	    								
            	    // InternalElkGraph.g:647:9: ( (lv_x_5_0= ruleNumber ) )
            	    // InternalElkGraph.g:648:10: (lv_x_5_0= ruleNumber )
            	    {
            	    // InternalElkGraph.g:648:10: (lv_x_5_0= ruleNumber )
            	    // InternalElkGraph.g:649:11: lv_x_5_0= ruleNumber
            	    {

            	    											newCompositeNode(grammarAccess.getShapeLayoutAccess().getXNumberParserRuleCall_2_0_2_0());
            	    										
            	    pushFollow(FOLLOW_19);
            	    lv_x_5_0=ruleNumber();

            	    state._fsp--;


            	    											if (current==null) {
            	    												current = createModelElementForParent(grammarAccess.getShapeLayoutRule());
            	    											}
            	    											set(
            	    												current,
            	    												"x",
            	    												lv_x_5_0,
            	    												"org.eclipse.elk.graph.text.ElkGraph.Number");
            	    											afterParserOrEnumRuleCall();
            	    										

            	    }


            	    }

            	    otherlv_6=(Token)match(input,23,FOLLOW_18); 

            	    									newLeafNode(otherlv_6, grammarAccess.getShapeLayoutAccess().getCommaKeyword_2_0_3());
            	    								
            	    // InternalElkGraph.g:670:9: ( (lv_y_7_0= ruleNumber ) )
            	    // InternalElkGraph.g:671:10: (lv_y_7_0= ruleNumber )
            	    {
            	    // InternalElkGraph.g:671:10: (lv_y_7_0= ruleNumber )
            	    // InternalElkGraph.g:672:11: lv_y_7_0= ruleNumber
            	    {

            	    											newCompositeNode(grammarAccess.getShapeLayoutAccess().getYNumberParserRuleCall_2_0_4_0());
            	    										
            	    pushFollow(FOLLOW_17);
            	    lv_y_7_0=ruleNumber();

            	    state._fsp--;


            	    											if (current==null) {
            	    												current = createModelElementForParent(grammarAccess.getShapeLayoutRule());
            	    											}
            	    											set(
            	    												current,
            	    												"y",
            	    												lv_y_7_0,
            	    												"org.eclipse.elk.graph.text.ElkGraph.Number");
            	    											afterParserOrEnumRuleCall();
            	    										

            	    }


            	    }


            	    }


            	    }

            	     
            	    						getUnorderedGroupHelper().returnFromSelection(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2());
            	    					

            	    }


            	    }


            	    }
            	    break;
            	case 2 :
            	    // InternalElkGraph.g:695:4: ({...}? => ( ({...}? => (otherlv_8= 'size' otherlv_9= ':' ( (lv_width_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_height_12_0= ruleNumber ) ) ) ) ) )
            	    {
            	    // InternalElkGraph.g:695:4: ({...}? => ( ({...}? => (otherlv_8= 'size' otherlv_9= ':' ( (lv_width_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_height_12_0= ruleNumber ) ) ) ) ) )
            	    // InternalElkGraph.g:696:5: {...}? => ( ({...}? => (otherlv_8= 'size' otherlv_9= ':' ( (lv_width_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_height_12_0= ruleNumber ) ) ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2(), 1) ) {
            	        throw new FailedPredicateException(input, "ruleShapeLayout", "getUnorderedGroupHelper().canSelect(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2(), 1)");
            	    }
            	    // InternalElkGraph.g:696:108: ( ({...}? => (otherlv_8= 'size' otherlv_9= ':' ( (lv_width_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_height_12_0= ruleNumber ) ) ) ) )
            	    // InternalElkGraph.g:697:6: ({...}? => (otherlv_8= 'size' otherlv_9= ':' ( (lv_width_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_height_12_0= ruleNumber ) ) ) )
            	    {

            	    						getUnorderedGroupHelper().select(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2(), 1);
            	    					
            	    // InternalElkGraph.g:700:9: ({...}? => (otherlv_8= 'size' otherlv_9= ':' ( (lv_width_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_height_12_0= ruleNumber ) ) ) )
            	    // InternalElkGraph.g:700:10: {...}? => (otherlv_8= 'size' otherlv_9= ':' ( (lv_width_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_height_12_0= ruleNumber ) ) )
            	    {
            	    if ( !((true)) ) {
            	        throw new FailedPredicateException(input, "ruleShapeLayout", "true");
            	    }
            	    // InternalElkGraph.g:700:19: (otherlv_8= 'size' otherlv_9= ':' ( (lv_width_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_height_12_0= ruleNumber ) ) )
            	    // InternalElkGraph.g:700:20: otherlv_8= 'size' otherlv_9= ':' ( (lv_width_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_height_12_0= ruleNumber ) )
            	    {
            	    otherlv_8=(Token)match(input,24,FOLLOW_11); 

            	    									newLeafNode(otherlv_8, grammarAccess.getShapeLayoutAccess().getSizeKeyword_2_1_0());
            	    								
            	    otherlv_9=(Token)match(input,18,FOLLOW_18); 

            	    									newLeafNode(otherlv_9, grammarAccess.getShapeLayoutAccess().getColonKeyword_2_1_1());
            	    								
            	    // InternalElkGraph.g:708:9: ( (lv_width_10_0= ruleNumber ) )
            	    // InternalElkGraph.g:709:10: (lv_width_10_0= ruleNumber )
            	    {
            	    // InternalElkGraph.g:709:10: (lv_width_10_0= ruleNumber )
            	    // InternalElkGraph.g:710:11: lv_width_10_0= ruleNumber
            	    {

            	    											newCompositeNode(grammarAccess.getShapeLayoutAccess().getWidthNumberParserRuleCall_2_1_2_0());
            	    										
            	    pushFollow(FOLLOW_19);
            	    lv_width_10_0=ruleNumber();

            	    state._fsp--;


            	    											if (current==null) {
            	    												current = createModelElementForParent(grammarAccess.getShapeLayoutRule());
            	    											}
            	    											set(
            	    												current,
            	    												"width",
            	    												lv_width_10_0,
            	    												"org.eclipse.elk.graph.text.ElkGraph.Number");
            	    											afterParserOrEnumRuleCall();
            	    										

            	    }


            	    }

            	    otherlv_11=(Token)match(input,23,FOLLOW_18); 

            	    									newLeafNode(otherlv_11, grammarAccess.getShapeLayoutAccess().getCommaKeyword_2_1_3());
            	    								
            	    // InternalElkGraph.g:731:9: ( (lv_height_12_0= ruleNumber ) )
            	    // InternalElkGraph.g:732:10: (lv_height_12_0= ruleNumber )
            	    {
            	    // InternalElkGraph.g:732:10: (lv_height_12_0= ruleNumber )
            	    // InternalElkGraph.g:733:11: lv_height_12_0= ruleNumber
            	    {

            	    											newCompositeNode(grammarAccess.getShapeLayoutAccess().getHeightNumberParserRuleCall_2_1_4_0());
            	    										
            	    pushFollow(FOLLOW_17);
            	    lv_height_12_0=ruleNumber();

            	    state._fsp--;


            	    											if (current==null) {
            	    												current = createModelElementForParent(grammarAccess.getShapeLayoutRule());
            	    											}
            	    											set(
            	    												current,
            	    												"height",
            	    												lv_height_12_0,
            	    												"org.eclipse.elk.graph.text.ElkGraph.Number");
            	    											afterParserOrEnumRuleCall();
            	    										

            	    }


            	    }


            	    }


            	    }

            	     
            	    						getUnorderedGroupHelper().returnFromSelection(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2());
            	    					

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop17;
                }
            } while (true);


            }


            }

             
            				  getUnorderedGroupHelper().leave(grammarAccess.getShapeLayoutAccess().getUnorderedGroup_2());
            				

            }

            otherlv_13=(Token)match(input,25,FOLLOW_2); 

            			newLeafNode(otherlv_13, grammarAccess.getShapeLayoutAccess().getRightSquareBracketKeyword_3());
            		

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleShapeLayout"


    // $ANTLR start "entryRuleElkEdge"
    // InternalElkGraph.g:771:1: entryRuleElkEdge returns [EObject current=null] : iv_ruleElkEdge= ruleElkEdge EOF ;
    public final EObject entryRuleElkEdge() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleElkEdge = null;


        try {
            // InternalElkGraph.g:771:48: (iv_ruleElkEdge= ruleElkEdge EOF )
            // InternalElkGraph.g:772:2: iv_ruleElkEdge= ruleElkEdge EOF
            {
             newCompositeNode(grammarAccess.getElkEdgeRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleElkEdge=ruleElkEdge();

            state._fsp--;

             current =iv_ruleElkEdge; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleElkEdge"


    // $ANTLR start "ruleElkEdge"
    // InternalElkGraph.g:778:1: ruleElkEdge returns [EObject current=null] : (otherlv_0= 'edge' ( ( (lv_identifier_1_0= RULE_ID ) ) otherlv_2= ':' )? ( ( ruleQualifiedId ) ) (otherlv_4= ',' ( ( ruleQualifiedId ) ) )* otherlv_6= '->' ( ( ruleQualifiedId ) ) (otherlv_8= ',' ( ( ruleQualifiedId ) ) )* (otherlv_10= '{' (this_EdgeLayout_11= ruleEdgeLayout[$current] )? ( (lv_properties_12_0= ruleProperty ) )* ( (lv_labels_13_0= ruleElkLabel ) )* otherlv_14= '}' )? ) ;
    public final EObject ruleElkEdge() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_identifier_1_0=null;
        Token otherlv_2=null;
        Token otherlv_4=null;
        Token otherlv_6=null;
        Token otherlv_8=null;
        Token otherlv_10=null;
        Token otherlv_14=null;
        EObject this_EdgeLayout_11 = null;

        EObject lv_properties_12_0 = null;

        EObject lv_labels_13_0 = null;



        	enterRule();

        try {
            // InternalElkGraph.g:784:2: ( (otherlv_0= 'edge' ( ( (lv_identifier_1_0= RULE_ID ) ) otherlv_2= ':' )? ( ( ruleQualifiedId ) ) (otherlv_4= ',' ( ( ruleQualifiedId ) ) )* otherlv_6= '->' ( ( ruleQualifiedId ) ) (otherlv_8= ',' ( ( ruleQualifiedId ) ) )* (otherlv_10= '{' (this_EdgeLayout_11= ruleEdgeLayout[$current] )? ( (lv_properties_12_0= ruleProperty ) )* ( (lv_labels_13_0= ruleElkLabel ) )* otherlv_14= '}' )? ) )
            // InternalElkGraph.g:785:2: (otherlv_0= 'edge' ( ( (lv_identifier_1_0= RULE_ID ) ) otherlv_2= ':' )? ( ( ruleQualifiedId ) ) (otherlv_4= ',' ( ( ruleQualifiedId ) ) )* otherlv_6= '->' ( ( ruleQualifiedId ) ) (otherlv_8= ',' ( ( ruleQualifiedId ) ) )* (otherlv_10= '{' (this_EdgeLayout_11= ruleEdgeLayout[$current] )? ( (lv_properties_12_0= ruleProperty ) )* ( (lv_labels_13_0= ruleElkLabel ) )* otherlv_14= '}' )? )
            {
            // InternalElkGraph.g:785:2: (otherlv_0= 'edge' ( ( (lv_identifier_1_0= RULE_ID ) ) otherlv_2= ':' )? ( ( ruleQualifiedId ) ) (otherlv_4= ',' ( ( ruleQualifiedId ) ) )* otherlv_6= '->' ( ( ruleQualifiedId ) ) (otherlv_8= ',' ( ( ruleQualifiedId ) ) )* (otherlv_10= '{' (this_EdgeLayout_11= ruleEdgeLayout[$current] )? ( (lv_properties_12_0= ruleProperty ) )* ( (lv_labels_13_0= ruleElkLabel ) )* otherlv_14= '}' )? )
            // InternalElkGraph.g:786:3: otherlv_0= 'edge' ( ( (lv_identifier_1_0= RULE_ID ) ) otherlv_2= ':' )? ( ( ruleQualifiedId ) ) (otherlv_4= ',' ( ( ruleQualifiedId ) ) )* otherlv_6= '->' ( ( ruleQualifiedId ) ) (otherlv_8= ',' ( ( ruleQualifiedId ) ) )* (otherlv_10= '{' (this_EdgeLayout_11= ruleEdgeLayout[$current] )? ( (lv_properties_12_0= ruleProperty ) )* ( (lv_labels_13_0= ruleElkLabel ) )* otherlv_14= '}' )?
            {
            otherlv_0=(Token)match(input,26,FOLLOW_3); 

            			newLeafNode(otherlv_0, grammarAccess.getElkEdgeAccess().getEdgeKeyword_0());
            		
            // InternalElkGraph.g:790:3: ( ( (lv_identifier_1_0= RULE_ID ) ) otherlv_2= ':' )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==RULE_ID) ) {
                int LA18_1 = input.LA(2);

                if ( (LA18_1==18) ) {
                    alt18=1;
                }
            }
            switch (alt18) {
                case 1 :
                    // InternalElkGraph.g:791:4: ( (lv_identifier_1_0= RULE_ID ) ) otherlv_2= ':'
                    {
                    // InternalElkGraph.g:791:4: ( (lv_identifier_1_0= RULE_ID ) )
                    // InternalElkGraph.g:792:5: (lv_identifier_1_0= RULE_ID )
                    {
                    // InternalElkGraph.g:792:5: (lv_identifier_1_0= RULE_ID )
                    // InternalElkGraph.g:793:6: lv_identifier_1_0= RULE_ID
                    {
                    lv_identifier_1_0=(Token)match(input,RULE_ID,FOLLOW_11); 

                    						newLeafNode(lv_identifier_1_0, grammarAccess.getElkEdgeAccess().getIdentifierIDTerminalRuleCall_1_0_0());
                    					

                    						if (current==null) {
                    							current = createModelElement(grammarAccess.getElkEdgeRule());
                    						}
                    						setWithLastConsumed(
                    							current,
                    							"identifier",
                    							lv_identifier_1_0,
                    							"org.eclipse.xtext.common.Terminals.ID");
                    					

                    }


                    }

                    otherlv_2=(Token)match(input,18,FOLLOW_3); 

                    				newLeafNode(otherlv_2, grammarAccess.getElkEdgeAccess().getColonKeyword_1_1());
                    			

                    }
                    break;

            }

            // InternalElkGraph.g:814:3: ( ( ruleQualifiedId ) )
            // InternalElkGraph.g:815:4: ( ruleQualifiedId )
            {
            // InternalElkGraph.g:815:4: ( ruleQualifiedId )
            // InternalElkGraph.g:816:5: ruleQualifiedId
            {

            					if (current==null) {
            						current = createModelElement(grammarAccess.getElkEdgeRule());
            					}
            				

            					newCompositeNode(grammarAccess.getElkEdgeAccess().getSourcesElkConnectableShapeCrossReference_2_0());
            				
            pushFollow(FOLLOW_20);
            ruleQualifiedId();

            state._fsp--;


            					afterParserOrEnumRuleCall();
            				

            }


            }

            // InternalElkGraph.g:830:3: (otherlv_4= ',' ( ( ruleQualifiedId ) ) )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==23) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // InternalElkGraph.g:831:4: otherlv_4= ',' ( ( ruleQualifiedId ) )
            	    {
            	    otherlv_4=(Token)match(input,23,FOLLOW_3); 

            	    				newLeafNode(otherlv_4, grammarAccess.getElkEdgeAccess().getCommaKeyword_3_0());
            	    			
            	    // InternalElkGraph.g:835:4: ( ( ruleQualifiedId ) )
            	    // InternalElkGraph.g:836:5: ( ruleQualifiedId )
            	    {
            	    // InternalElkGraph.g:836:5: ( ruleQualifiedId )
            	    // InternalElkGraph.g:837:6: ruleQualifiedId
            	    {

            	    						if (current==null) {
            	    							current = createModelElement(grammarAccess.getElkEdgeRule());
            	    						}
            	    					

            	    						newCompositeNode(grammarAccess.getElkEdgeAccess().getSourcesElkConnectableShapeCrossReference_3_1_0());
            	    					
            	    pushFollow(FOLLOW_20);
            	    ruleQualifiedId();

            	    state._fsp--;


            	    						afterParserOrEnumRuleCall();
            	    					

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop19;
                }
            } while (true);

            otherlv_6=(Token)match(input,27,FOLLOW_3); 

            			newLeafNode(otherlv_6, grammarAccess.getElkEdgeAccess().getHyphenMinusGreaterThanSignKeyword_4());
            		
            // InternalElkGraph.g:856:3: ( ( ruleQualifiedId ) )
            // InternalElkGraph.g:857:4: ( ruleQualifiedId )
            {
            // InternalElkGraph.g:857:4: ( ruleQualifiedId )
            // InternalElkGraph.g:858:5: ruleQualifiedId
            {

            					if (current==null) {
            						current = createModelElement(grammarAccess.getElkEdgeRule());
            					}
            				

            					newCompositeNode(grammarAccess.getElkEdgeAccess().getTargetsElkConnectableShapeCrossReference_5_0());
            				
            pushFollow(FOLLOW_21);
            ruleQualifiedId();

            state._fsp--;


            					afterParserOrEnumRuleCall();
            				

            }


            }

            // InternalElkGraph.g:872:3: (otherlv_8= ',' ( ( ruleQualifiedId ) ) )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==23) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // InternalElkGraph.g:873:4: otherlv_8= ',' ( ( ruleQualifiedId ) )
            	    {
            	    otherlv_8=(Token)match(input,23,FOLLOW_3); 

            	    				newLeafNode(otherlv_8, grammarAccess.getElkEdgeAccess().getCommaKeyword_6_0());
            	    			
            	    // InternalElkGraph.g:877:4: ( ( ruleQualifiedId ) )
            	    // InternalElkGraph.g:878:5: ( ruleQualifiedId )
            	    {
            	    // InternalElkGraph.g:878:5: ( ruleQualifiedId )
            	    // InternalElkGraph.g:879:6: ruleQualifiedId
            	    {

            	    						if (current==null) {
            	    							current = createModelElement(grammarAccess.getElkEdgeRule());
            	    						}
            	    					

            	    						newCompositeNode(grammarAccess.getElkEdgeAccess().getTargetsElkConnectableShapeCrossReference_6_1_0());
            	    					
            	    pushFollow(FOLLOW_21);
            	    ruleQualifiedId();

            	    state._fsp--;


            	    						afterParserOrEnumRuleCall();
            	    					

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop20;
                }
            } while (true);

            // InternalElkGraph.g:894:3: (otherlv_10= '{' (this_EdgeLayout_11= ruleEdgeLayout[$current] )? ( (lv_properties_12_0= ruleProperty ) )* ( (lv_labels_13_0= ruleElkLabel ) )* otherlv_14= '}' )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==15) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // InternalElkGraph.g:895:4: otherlv_10= '{' (this_EdgeLayout_11= ruleEdgeLayout[$current] )? ( (lv_properties_12_0= ruleProperty ) )* ( (lv_labels_13_0= ruleElkLabel ) )* otherlv_14= '}'
                    {
                    otherlv_10=(Token)match(input,15,FOLLOW_13); 

                    				newLeafNode(otherlv_10, grammarAccess.getElkEdgeAccess().getLeftCurlyBracketKeyword_7_0());
                    			
                    // InternalElkGraph.g:899:4: (this_EdgeLayout_11= ruleEdgeLayout[$current] )?
                    int alt21=2;
                    int LA21_0 = input.LA(1);

                    if ( (LA21_0==20) ) {
                        alt21=1;
                    }
                    switch (alt21) {
                        case 1 :
                            // InternalElkGraph.g:900:5: this_EdgeLayout_11= ruleEdgeLayout[$current]
                            {

                            					if (current==null) {
                            						current = createModelElement(grammarAccess.getElkEdgeRule());
                            					}
                            					newCompositeNode(grammarAccess.getElkEdgeAccess().getEdgeLayoutParserRuleCall_7_1());
                            				
                            pushFollow(FOLLOW_14);
                            this_EdgeLayout_11=ruleEdgeLayout(current);

                            state._fsp--;


                            					current = this_EdgeLayout_11;
                            					afterParserOrEnumRuleCall();
                            				

                            }
                            break;

                    }

                    // InternalElkGraph.g:912:4: ( (lv_properties_12_0= ruleProperty ) )*
                    loop22:
                    do {
                        int alt22=2;
                        int LA22_0 = input.LA(1);

                        if ( (LA22_0==RULE_ID) ) {
                            alt22=1;
                        }


                        switch (alt22) {
                    	case 1 :
                    	    // InternalElkGraph.g:913:5: (lv_properties_12_0= ruleProperty )
                    	    {
                    	    // InternalElkGraph.g:913:5: (lv_properties_12_0= ruleProperty )
                    	    // InternalElkGraph.g:914:6: lv_properties_12_0= ruleProperty
                    	    {

                    	    						newCompositeNode(grammarAccess.getElkEdgeAccess().getPropertiesPropertyParserRuleCall_7_2_0());
                    	    					
                    	    pushFollow(FOLLOW_14);
                    	    lv_properties_12_0=ruleProperty();

                    	    state._fsp--;


                    	    						if (current==null) {
                    	    							current = createModelElementForParent(grammarAccess.getElkEdgeRule());
                    	    						}
                    	    						add(
                    	    							current,
                    	    							"properties",
                    	    							lv_properties_12_0,
                    	    							"org.eclipse.elk.graph.text.ElkGraph.Property");
                    	    						afterParserOrEnumRuleCall();
                    	    					

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop22;
                        }
                    } while (true);

                    // InternalElkGraph.g:931:4: ( (lv_labels_13_0= ruleElkLabel ) )*
                    loop23:
                    do {
                        int alt23=2;
                        int LA23_0 = input.LA(1);

                        if ( (LA23_0==17) ) {
                            alt23=1;
                        }


                        switch (alt23) {
                    	case 1 :
                    	    // InternalElkGraph.g:932:5: (lv_labels_13_0= ruleElkLabel )
                    	    {
                    	    // InternalElkGraph.g:932:5: (lv_labels_13_0= ruleElkLabel )
                    	    // InternalElkGraph.g:933:6: lv_labels_13_0= ruleElkLabel
                    	    {

                    	    						newCompositeNode(grammarAccess.getElkEdgeAccess().getLabelsElkLabelParserRuleCall_7_3_0());
                    	    					
                    	    pushFollow(FOLLOW_15);
                    	    lv_labels_13_0=ruleElkLabel();

                    	    state._fsp--;


                    	    						if (current==null) {
                    	    							current = createModelElementForParent(grammarAccess.getElkEdgeRule());
                    	    						}
                    	    						add(
                    	    							current,
                    	    							"labels",
                    	    							lv_labels_13_0,
                    	    							"org.eclipse.elk.graph.text.ElkGraph.ElkLabel");
                    	    						afterParserOrEnumRuleCall();
                    	    					

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop23;
                        }
                    } while (true);

                    otherlv_14=(Token)match(input,16,FOLLOW_2); 

                    				newLeafNode(otherlv_14, grammarAccess.getElkEdgeAccess().getRightCurlyBracketKeyword_7_4());
                    			

                    }
                    break;

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleElkEdge"


    // $ANTLR start "ruleEdgeLayout"
    // InternalElkGraph.g:960:1: ruleEdgeLayout[EObject in_current] returns [EObject current=in_current] : (otherlv_0= 'layout' otherlv_1= '[' ( ( (lv_sections_2_0= ruleElkSingleEdgeSection ) ) | ( (lv_sections_3_0= ruleElkEdgeSection ) )+ ) otherlv_4= ']' ) ;
    public final EObject ruleEdgeLayout(EObject in_current) throws RecognitionException {
        EObject current = in_current;

        Token otherlv_0=null;
        Token otherlv_1=null;
        Token otherlv_4=null;
        EObject lv_sections_2_0 = null;

        EObject lv_sections_3_0 = null;



        	enterRule();

        try {
            // InternalElkGraph.g:966:2: ( (otherlv_0= 'layout' otherlv_1= '[' ( ( (lv_sections_2_0= ruleElkSingleEdgeSection ) ) | ( (lv_sections_3_0= ruleElkEdgeSection ) )+ ) otherlv_4= ']' ) )
            // InternalElkGraph.g:967:2: (otherlv_0= 'layout' otherlv_1= '[' ( ( (lv_sections_2_0= ruleElkSingleEdgeSection ) ) | ( (lv_sections_3_0= ruleElkEdgeSection ) )+ ) otherlv_4= ']' )
            {
            // InternalElkGraph.g:967:2: (otherlv_0= 'layout' otherlv_1= '[' ( ( (lv_sections_2_0= ruleElkSingleEdgeSection ) ) | ( (lv_sections_3_0= ruleElkEdgeSection ) )+ ) otherlv_4= ']' )
            // InternalElkGraph.g:968:3: otherlv_0= 'layout' otherlv_1= '[' ( ( (lv_sections_2_0= ruleElkSingleEdgeSection ) ) | ( (lv_sections_3_0= ruleElkEdgeSection ) )+ ) otherlv_4= ']'
            {
            otherlv_0=(Token)match(input,20,FOLLOW_16); 

            			newLeafNode(otherlv_0, grammarAccess.getEdgeLayoutAccess().getLayoutKeyword_0());
            		
            otherlv_1=(Token)match(input,21,FOLLOW_22); 

            			newLeafNode(otherlv_1, grammarAccess.getEdgeLayoutAccess().getLeftSquareBracketKeyword_1());
            		
            // InternalElkGraph.g:976:3: ( ( (lv_sections_2_0= ruleElkSingleEdgeSection ) ) | ( (lv_sections_3_0= ruleElkEdgeSection ) )+ )
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==RULE_ID||LA26_0==25||(LA26_0>=28 && LA26_0<=32)) ) {
                alt26=1;
            }
            else if ( (LA26_0==34) ) {
                alt26=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 26, 0, input);

                throw nvae;
            }
            switch (alt26) {
                case 1 :
                    // InternalElkGraph.g:977:4: ( (lv_sections_2_0= ruleElkSingleEdgeSection ) )
                    {
                    // InternalElkGraph.g:977:4: ( (lv_sections_2_0= ruleElkSingleEdgeSection ) )
                    // InternalElkGraph.g:978:5: (lv_sections_2_0= ruleElkSingleEdgeSection )
                    {
                    // InternalElkGraph.g:978:5: (lv_sections_2_0= ruleElkSingleEdgeSection )
                    // InternalElkGraph.g:979:6: lv_sections_2_0= ruleElkSingleEdgeSection
                    {

                    						newCompositeNode(grammarAccess.getEdgeLayoutAccess().getSectionsElkSingleEdgeSectionParserRuleCall_2_0_0());
                    					
                    pushFollow(FOLLOW_23);
                    lv_sections_2_0=ruleElkSingleEdgeSection();

                    state._fsp--;


                    						if (current==null) {
                    							current = createModelElementForParent(grammarAccess.getEdgeLayoutRule());
                    						}
                    						add(
                    							current,
                    							"sections",
                    							lv_sections_2_0,
                    							"org.eclipse.elk.graph.text.ElkGraph.ElkSingleEdgeSection");
                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalElkGraph.g:997:4: ( (lv_sections_3_0= ruleElkEdgeSection ) )+
                    {
                    // InternalElkGraph.g:997:4: ( (lv_sections_3_0= ruleElkEdgeSection ) )+
                    int cnt25=0;
                    loop25:
                    do {
                        int alt25=2;
                        int LA25_0 = input.LA(1);

                        if ( (LA25_0==34) ) {
                            alt25=1;
                        }


                        switch (alt25) {
                    	case 1 :
                    	    // InternalElkGraph.g:998:5: (lv_sections_3_0= ruleElkEdgeSection )
                    	    {
                    	    // InternalElkGraph.g:998:5: (lv_sections_3_0= ruleElkEdgeSection )
                    	    // InternalElkGraph.g:999:6: lv_sections_3_0= ruleElkEdgeSection
                    	    {

                    	    						newCompositeNode(grammarAccess.getEdgeLayoutAccess().getSectionsElkEdgeSectionParserRuleCall_2_1_0());
                    	    					
                    	    pushFollow(FOLLOW_22);
                    	    lv_sections_3_0=ruleElkEdgeSection();

                    	    state._fsp--;


                    	    						if (current==null) {
                    	    							current = createModelElementForParent(grammarAccess.getEdgeLayoutRule());
                    	    						}
                    	    						add(
                    	    							current,
                    	    							"sections",
                    	    							lv_sections_3_0,
                    	    							"org.eclipse.elk.graph.text.ElkGraph.ElkEdgeSection");
                    	    						afterParserOrEnumRuleCall();
                    	    					

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt25 >= 1 ) break loop25;
                                EarlyExitException eee =
                                    new EarlyExitException(25, input);
                                throw eee;
                        }
                        cnt25++;
                    } while (true);


                    }
                    break;

            }

            otherlv_4=(Token)match(input,25,FOLLOW_2); 

            			newLeafNode(otherlv_4, grammarAccess.getEdgeLayoutAccess().getRightSquareBracketKeyword_3());
            		

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleEdgeLayout"


    // $ANTLR start "entryRuleElkSingleEdgeSection"
    // InternalElkGraph.g:1025:1: entryRuleElkSingleEdgeSection returns [EObject current=null] : iv_ruleElkSingleEdgeSection= ruleElkSingleEdgeSection EOF ;
    public final EObject entryRuleElkSingleEdgeSection() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleElkSingleEdgeSection = null;


        try {
            // InternalElkGraph.g:1025:61: (iv_ruleElkSingleEdgeSection= ruleElkSingleEdgeSection EOF )
            // InternalElkGraph.g:1026:2: iv_ruleElkSingleEdgeSection= ruleElkSingleEdgeSection EOF
            {
             newCompositeNode(grammarAccess.getElkSingleEdgeSectionRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleElkSingleEdgeSection=ruleElkSingleEdgeSection();

            state._fsp--;

             current =iv_ruleElkSingleEdgeSection; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleElkSingleEdgeSection"


    // $ANTLR start "ruleElkSingleEdgeSection"
    // InternalElkGraph.g:1032:1: ruleElkSingleEdgeSection returns [EObject current=null] : ( () ( ( ( ( ( ({...}? => ( ({...}? => (otherlv_2= 'incoming' otherlv_3= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_5= 'outgoing' otherlv_6= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= 'start' otherlv_9= ':' ( (lv_startX_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_startY_12_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_13= 'end' otherlv_14= ':' ( (lv_endX_15_0= ruleNumber ) ) otherlv_16= ',' ( (lv_endY_17_0= ruleNumber ) ) ) ) ) ) )* ) ) ) (otherlv_18= 'bends' otherlv_19= ':' ( (lv_bendPoints_20_0= ruleElkBendPoint ) ) (otherlv_21= '|' ( (lv_bendPoints_22_0= ruleElkBendPoint ) ) )* )? ( (lv_properties_23_0= ruleProperty ) )* ) ) ;
    public final EObject ruleElkSingleEdgeSection() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        Token otherlv_6=null;
        Token otherlv_8=null;
        Token otherlv_9=null;
        Token otherlv_11=null;
        Token otherlv_13=null;
        Token otherlv_14=null;
        Token otherlv_16=null;
        Token otherlv_18=null;
        Token otherlv_19=null;
        Token otherlv_21=null;
        AntlrDatatypeRuleToken lv_startX_10_0 = null;

        AntlrDatatypeRuleToken lv_startY_12_0 = null;

        AntlrDatatypeRuleToken lv_endX_15_0 = null;

        AntlrDatatypeRuleToken lv_endY_17_0 = null;

        EObject lv_bendPoints_20_0 = null;

        EObject lv_bendPoints_22_0 = null;

        EObject lv_properties_23_0 = null;



        	enterRule();

        try {
            // InternalElkGraph.g:1038:2: ( ( () ( ( ( ( ( ({...}? => ( ({...}? => (otherlv_2= 'incoming' otherlv_3= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_5= 'outgoing' otherlv_6= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= 'start' otherlv_9= ':' ( (lv_startX_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_startY_12_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_13= 'end' otherlv_14= ':' ( (lv_endX_15_0= ruleNumber ) ) otherlv_16= ',' ( (lv_endY_17_0= ruleNumber ) ) ) ) ) ) )* ) ) ) (otherlv_18= 'bends' otherlv_19= ':' ( (lv_bendPoints_20_0= ruleElkBendPoint ) ) (otherlv_21= '|' ( (lv_bendPoints_22_0= ruleElkBendPoint ) ) )* )? ( (lv_properties_23_0= ruleProperty ) )* ) ) )
            // InternalElkGraph.g:1039:2: ( () ( ( ( ( ( ({...}? => ( ({...}? => (otherlv_2= 'incoming' otherlv_3= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_5= 'outgoing' otherlv_6= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= 'start' otherlv_9= ':' ( (lv_startX_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_startY_12_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_13= 'end' otherlv_14= ':' ( (lv_endX_15_0= ruleNumber ) ) otherlv_16= ',' ( (lv_endY_17_0= ruleNumber ) ) ) ) ) ) )* ) ) ) (otherlv_18= 'bends' otherlv_19= ':' ( (lv_bendPoints_20_0= ruleElkBendPoint ) ) (otherlv_21= '|' ( (lv_bendPoints_22_0= ruleElkBendPoint ) ) )* )? ( (lv_properties_23_0= ruleProperty ) )* ) )
            {
            // InternalElkGraph.g:1039:2: ( () ( ( ( ( ( ({...}? => ( ({...}? => (otherlv_2= 'incoming' otherlv_3= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_5= 'outgoing' otherlv_6= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= 'start' otherlv_9= ':' ( (lv_startX_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_startY_12_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_13= 'end' otherlv_14= ':' ( (lv_endX_15_0= ruleNumber ) ) otherlv_16= ',' ( (lv_endY_17_0= ruleNumber ) ) ) ) ) ) )* ) ) ) (otherlv_18= 'bends' otherlv_19= ':' ( (lv_bendPoints_20_0= ruleElkBendPoint ) ) (otherlv_21= '|' ( (lv_bendPoints_22_0= ruleElkBendPoint ) ) )* )? ( (lv_properties_23_0= ruleProperty ) )* ) )
            // InternalElkGraph.g:1040:3: () ( ( ( ( ( ({...}? => ( ({...}? => (otherlv_2= 'incoming' otherlv_3= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_5= 'outgoing' otherlv_6= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= 'start' otherlv_9= ':' ( (lv_startX_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_startY_12_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_13= 'end' otherlv_14= ':' ( (lv_endX_15_0= ruleNumber ) ) otherlv_16= ',' ( (lv_endY_17_0= ruleNumber ) ) ) ) ) ) )* ) ) ) (otherlv_18= 'bends' otherlv_19= ':' ( (lv_bendPoints_20_0= ruleElkBendPoint ) ) (otherlv_21= '|' ( (lv_bendPoints_22_0= ruleElkBendPoint ) ) )* )? ( (lv_properties_23_0= ruleProperty ) )* )
            {
            // InternalElkGraph.g:1040:3: ()
            // InternalElkGraph.g:1041:4: 
            {

            				current = forceCreateModelElement(
            					grammarAccess.getElkSingleEdgeSectionAccess().getElkEdgeSectionAction_0(),
            					current);
            			

            }

            // InternalElkGraph.g:1047:3: ( ( ( ( ( ({...}? => ( ({...}? => (otherlv_2= 'incoming' otherlv_3= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_5= 'outgoing' otherlv_6= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= 'start' otherlv_9= ':' ( (lv_startX_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_startY_12_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_13= 'end' otherlv_14= ':' ( (lv_endX_15_0= ruleNumber ) ) otherlv_16= ',' ( (lv_endY_17_0= ruleNumber ) ) ) ) ) ) )* ) ) ) (otherlv_18= 'bends' otherlv_19= ':' ( (lv_bendPoints_20_0= ruleElkBendPoint ) ) (otherlv_21= '|' ( (lv_bendPoints_22_0= ruleElkBendPoint ) ) )* )? ( (lv_properties_23_0= ruleProperty ) )* )
            // InternalElkGraph.g:1048:4: ( ( ( ( ({...}? => ( ({...}? => (otherlv_2= 'incoming' otherlv_3= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_5= 'outgoing' otherlv_6= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= 'start' otherlv_9= ':' ( (lv_startX_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_startY_12_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_13= 'end' otherlv_14= ':' ( (lv_endX_15_0= ruleNumber ) ) otherlv_16= ',' ( (lv_endY_17_0= ruleNumber ) ) ) ) ) ) )* ) ) ) (otherlv_18= 'bends' otherlv_19= ':' ( (lv_bendPoints_20_0= ruleElkBendPoint ) ) (otherlv_21= '|' ( (lv_bendPoints_22_0= ruleElkBendPoint ) ) )* )? ( (lv_properties_23_0= ruleProperty ) )*
            {
            // InternalElkGraph.g:1048:4: ( ( ( ( ({...}? => ( ({...}? => (otherlv_2= 'incoming' otherlv_3= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_5= 'outgoing' otherlv_6= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= 'start' otherlv_9= ':' ( (lv_startX_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_startY_12_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_13= 'end' otherlv_14= ':' ( (lv_endX_15_0= ruleNumber ) ) otherlv_16= ',' ( (lv_endY_17_0= ruleNumber ) ) ) ) ) ) )* ) ) )
            // InternalElkGraph.g:1049:5: ( ( ( ({...}? => ( ({...}? => (otherlv_2= 'incoming' otherlv_3= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_5= 'outgoing' otherlv_6= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= 'start' otherlv_9= ':' ( (lv_startX_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_startY_12_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_13= 'end' otherlv_14= ':' ( (lv_endX_15_0= ruleNumber ) ) otherlv_16= ',' ( (lv_endY_17_0= ruleNumber ) ) ) ) ) ) )* ) )
            {
            // InternalElkGraph.g:1049:5: ( ( ( ({...}? => ( ({...}? => (otherlv_2= 'incoming' otherlv_3= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_5= 'outgoing' otherlv_6= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= 'start' otherlv_9= ':' ( (lv_startX_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_startY_12_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_13= 'end' otherlv_14= ':' ( (lv_endX_15_0= ruleNumber ) ) otherlv_16= ',' ( (lv_endY_17_0= ruleNumber ) ) ) ) ) ) )* ) )
            // InternalElkGraph.g:1050:6: ( ( ({...}? => ( ({...}? => (otherlv_2= 'incoming' otherlv_3= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_5= 'outgoing' otherlv_6= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= 'start' otherlv_9= ':' ( (lv_startX_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_startY_12_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_13= 'end' otherlv_14= ':' ( (lv_endX_15_0= ruleNumber ) ) otherlv_16= ',' ( (lv_endY_17_0= ruleNumber ) ) ) ) ) ) )* )
            {
             
            					  getUnorderedGroupHelper().enter(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0());
            					
            // InternalElkGraph.g:1053:6: ( ( ({...}? => ( ({...}? => (otherlv_2= 'incoming' otherlv_3= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_5= 'outgoing' otherlv_6= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= 'start' otherlv_9= ':' ( (lv_startX_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_startY_12_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_13= 'end' otherlv_14= ':' ( (lv_endX_15_0= ruleNumber ) ) otherlv_16= ',' ( (lv_endY_17_0= ruleNumber ) ) ) ) ) ) )* )
            // InternalElkGraph.g:1054:7: ( ({...}? => ( ({...}? => (otherlv_2= 'incoming' otherlv_3= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_5= 'outgoing' otherlv_6= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= 'start' otherlv_9= ':' ( (lv_startX_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_startY_12_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_13= 'end' otherlv_14= ':' ( (lv_endX_15_0= ruleNumber ) ) otherlv_16= ',' ( (lv_endY_17_0= ruleNumber ) ) ) ) ) ) )*
            {
            // InternalElkGraph.g:1054:7: ( ({...}? => ( ({...}? => (otherlv_2= 'incoming' otherlv_3= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_5= 'outgoing' otherlv_6= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_8= 'start' otherlv_9= ':' ( (lv_startX_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_startY_12_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_13= 'end' otherlv_14= ':' ( (lv_endX_15_0= ruleNumber ) ) otherlv_16= ',' ( (lv_endY_17_0= ruleNumber ) ) ) ) ) ) )*
            loop27:
            do {
                int alt27=5;
                int LA27_0 = input.LA(1);

                if ( LA27_0 == 28 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 0) ) {
                    alt27=1;
                }
                else if ( LA27_0 == 29 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 1) ) {
                    alt27=2;
                }
                else if ( LA27_0 == 30 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 2) ) {
                    alt27=3;
                }
                else if ( LA27_0 == 31 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 3) ) {
                    alt27=4;
                }


                switch (alt27) {
            	case 1 :
            	    // InternalElkGraph.g:1055:5: ({...}? => ( ({...}? => (otherlv_2= 'incoming' otherlv_3= ':' ( ( ruleQualifiedId ) ) ) ) ) )
            	    {
            	    // InternalElkGraph.g:1055:5: ({...}? => ( ({...}? => (otherlv_2= 'incoming' otherlv_3= ':' ( ( ruleQualifiedId ) ) ) ) ) )
            	    // InternalElkGraph.g:1056:6: {...}? => ( ({...}? => (otherlv_2= 'incoming' otherlv_3= ':' ( ( ruleQualifiedId ) ) ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 0) ) {
            	        throw new FailedPredicateException(input, "ruleElkSingleEdgeSection", "getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 0)");
            	    }
            	    // InternalElkGraph.g:1056:120: ( ({...}? => (otherlv_2= 'incoming' otherlv_3= ':' ( ( ruleQualifiedId ) ) ) ) )
            	    // InternalElkGraph.g:1057:7: ({...}? => (otherlv_2= 'incoming' otherlv_3= ':' ( ( ruleQualifiedId ) ) ) )
            	    {

            	    							getUnorderedGroupHelper().select(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 0);
            	    						
            	    // InternalElkGraph.g:1060:10: ({...}? => (otherlv_2= 'incoming' otherlv_3= ':' ( ( ruleQualifiedId ) ) ) )
            	    // InternalElkGraph.g:1060:11: {...}? => (otherlv_2= 'incoming' otherlv_3= ':' ( ( ruleQualifiedId ) ) )
            	    {
            	    if ( !((true)) ) {
            	        throw new FailedPredicateException(input, "ruleElkSingleEdgeSection", "true");
            	    }
            	    // InternalElkGraph.g:1060:20: (otherlv_2= 'incoming' otherlv_3= ':' ( ( ruleQualifiedId ) ) )
            	    // InternalElkGraph.g:1060:21: otherlv_2= 'incoming' otherlv_3= ':' ( ( ruleQualifiedId ) )
            	    {
            	    otherlv_2=(Token)match(input,28,FOLLOW_11); 

            	    										newLeafNode(otherlv_2, grammarAccess.getElkSingleEdgeSectionAccess().getIncomingKeyword_1_0_0_0());
            	    									
            	    otherlv_3=(Token)match(input,18,FOLLOW_3); 

            	    										newLeafNode(otherlv_3, grammarAccess.getElkSingleEdgeSectionAccess().getColonKeyword_1_0_0_1());
            	    									
            	    // InternalElkGraph.g:1068:10: ( ( ruleQualifiedId ) )
            	    // InternalElkGraph.g:1069:11: ( ruleQualifiedId )
            	    {
            	    // InternalElkGraph.g:1069:11: ( ruleQualifiedId )
            	    // InternalElkGraph.g:1070:12: ruleQualifiedId
            	    {

            	    												if (current==null) {
            	    													current = createModelElement(grammarAccess.getElkSingleEdgeSectionRule());
            	    												}
            	    											

            	    												newCompositeNode(grammarAccess.getElkSingleEdgeSectionAccess().getIncomingShapeElkConnectableShapeCrossReference_1_0_0_2_0());
            	    											
            	    pushFollow(FOLLOW_24);
            	    ruleQualifiedId();

            	    state._fsp--;


            	    												afterParserOrEnumRuleCall();
            	    											

            	    }


            	    }


            	    }


            	    }

            	     
            	    							getUnorderedGroupHelper().returnFromSelection(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0());
            	    						

            	    }


            	    }


            	    }
            	    break;
            	case 2 :
            	    // InternalElkGraph.g:1090:5: ({...}? => ( ({...}? => (otherlv_5= 'outgoing' otherlv_6= ':' ( ( ruleQualifiedId ) ) ) ) ) )
            	    {
            	    // InternalElkGraph.g:1090:5: ({...}? => ( ({...}? => (otherlv_5= 'outgoing' otherlv_6= ':' ( ( ruleQualifiedId ) ) ) ) ) )
            	    // InternalElkGraph.g:1091:6: {...}? => ( ({...}? => (otherlv_5= 'outgoing' otherlv_6= ':' ( ( ruleQualifiedId ) ) ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 1) ) {
            	        throw new FailedPredicateException(input, "ruleElkSingleEdgeSection", "getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 1)");
            	    }
            	    // InternalElkGraph.g:1091:120: ( ({...}? => (otherlv_5= 'outgoing' otherlv_6= ':' ( ( ruleQualifiedId ) ) ) ) )
            	    // InternalElkGraph.g:1092:7: ({...}? => (otherlv_5= 'outgoing' otherlv_6= ':' ( ( ruleQualifiedId ) ) ) )
            	    {

            	    							getUnorderedGroupHelper().select(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 1);
            	    						
            	    // InternalElkGraph.g:1095:10: ({...}? => (otherlv_5= 'outgoing' otherlv_6= ':' ( ( ruleQualifiedId ) ) ) )
            	    // InternalElkGraph.g:1095:11: {...}? => (otherlv_5= 'outgoing' otherlv_6= ':' ( ( ruleQualifiedId ) ) )
            	    {
            	    if ( !((true)) ) {
            	        throw new FailedPredicateException(input, "ruleElkSingleEdgeSection", "true");
            	    }
            	    // InternalElkGraph.g:1095:20: (otherlv_5= 'outgoing' otherlv_6= ':' ( ( ruleQualifiedId ) ) )
            	    // InternalElkGraph.g:1095:21: otherlv_5= 'outgoing' otherlv_6= ':' ( ( ruleQualifiedId ) )
            	    {
            	    otherlv_5=(Token)match(input,29,FOLLOW_11); 

            	    										newLeafNode(otherlv_5, grammarAccess.getElkSingleEdgeSectionAccess().getOutgoingKeyword_1_0_1_0());
            	    									
            	    otherlv_6=(Token)match(input,18,FOLLOW_3); 

            	    										newLeafNode(otherlv_6, grammarAccess.getElkSingleEdgeSectionAccess().getColonKeyword_1_0_1_1());
            	    									
            	    // InternalElkGraph.g:1103:10: ( ( ruleQualifiedId ) )
            	    // InternalElkGraph.g:1104:11: ( ruleQualifiedId )
            	    {
            	    // InternalElkGraph.g:1104:11: ( ruleQualifiedId )
            	    // InternalElkGraph.g:1105:12: ruleQualifiedId
            	    {

            	    												if (current==null) {
            	    													current = createModelElement(grammarAccess.getElkSingleEdgeSectionRule());
            	    												}
            	    											

            	    												newCompositeNode(grammarAccess.getElkSingleEdgeSectionAccess().getOutgoingShapeElkConnectableShapeCrossReference_1_0_1_2_0());
            	    											
            	    pushFollow(FOLLOW_24);
            	    ruleQualifiedId();

            	    state._fsp--;


            	    												afterParserOrEnumRuleCall();
            	    											

            	    }


            	    }


            	    }


            	    }

            	     
            	    							getUnorderedGroupHelper().returnFromSelection(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0());
            	    						

            	    }


            	    }


            	    }
            	    break;
            	case 3 :
            	    // InternalElkGraph.g:1125:5: ({...}? => ( ({...}? => (otherlv_8= 'start' otherlv_9= ':' ( (lv_startX_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_startY_12_0= ruleNumber ) ) ) ) ) )
            	    {
            	    // InternalElkGraph.g:1125:5: ({...}? => ( ({...}? => (otherlv_8= 'start' otherlv_9= ':' ( (lv_startX_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_startY_12_0= ruleNumber ) ) ) ) ) )
            	    // InternalElkGraph.g:1126:6: {...}? => ( ({...}? => (otherlv_8= 'start' otherlv_9= ':' ( (lv_startX_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_startY_12_0= ruleNumber ) ) ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 2) ) {
            	        throw new FailedPredicateException(input, "ruleElkSingleEdgeSection", "getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 2)");
            	    }
            	    // InternalElkGraph.g:1126:120: ( ({...}? => (otherlv_8= 'start' otherlv_9= ':' ( (lv_startX_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_startY_12_0= ruleNumber ) ) ) ) )
            	    // InternalElkGraph.g:1127:7: ({...}? => (otherlv_8= 'start' otherlv_9= ':' ( (lv_startX_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_startY_12_0= ruleNumber ) ) ) )
            	    {

            	    							getUnorderedGroupHelper().select(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 2);
            	    						
            	    // InternalElkGraph.g:1130:10: ({...}? => (otherlv_8= 'start' otherlv_9= ':' ( (lv_startX_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_startY_12_0= ruleNumber ) ) ) )
            	    // InternalElkGraph.g:1130:11: {...}? => (otherlv_8= 'start' otherlv_9= ':' ( (lv_startX_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_startY_12_0= ruleNumber ) ) )
            	    {
            	    if ( !((true)) ) {
            	        throw new FailedPredicateException(input, "ruleElkSingleEdgeSection", "true");
            	    }
            	    // InternalElkGraph.g:1130:20: (otherlv_8= 'start' otherlv_9= ':' ( (lv_startX_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_startY_12_0= ruleNumber ) ) )
            	    // InternalElkGraph.g:1130:21: otherlv_8= 'start' otherlv_9= ':' ( (lv_startX_10_0= ruleNumber ) ) otherlv_11= ',' ( (lv_startY_12_0= ruleNumber ) )
            	    {
            	    otherlv_8=(Token)match(input,30,FOLLOW_11); 

            	    										newLeafNode(otherlv_8, grammarAccess.getElkSingleEdgeSectionAccess().getStartKeyword_1_0_2_0());
            	    									
            	    otherlv_9=(Token)match(input,18,FOLLOW_18); 

            	    										newLeafNode(otherlv_9, grammarAccess.getElkSingleEdgeSectionAccess().getColonKeyword_1_0_2_1());
            	    									
            	    // InternalElkGraph.g:1138:10: ( (lv_startX_10_0= ruleNumber ) )
            	    // InternalElkGraph.g:1139:11: (lv_startX_10_0= ruleNumber )
            	    {
            	    // InternalElkGraph.g:1139:11: (lv_startX_10_0= ruleNumber )
            	    // InternalElkGraph.g:1140:12: lv_startX_10_0= ruleNumber
            	    {

            	    												newCompositeNode(grammarAccess.getElkSingleEdgeSectionAccess().getStartXNumberParserRuleCall_1_0_2_2_0());
            	    											
            	    pushFollow(FOLLOW_19);
            	    lv_startX_10_0=ruleNumber();

            	    state._fsp--;


            	    												if (current==null) {
            	    													current = createModelElementForParent(grammarAccess.getElkSingleEdgeSectionRule());
            	    												}
            	    												set(
            	    													current,
            	    													"startX",
            	    													lv_startX_10_0,
            	    													"org.eclipse.elk.graph.text.ElkGraph.Number");
            	    												afterParserOrEnumRuleCall();
            	    											

            	    }


            	    }

            	    otherlv_11=(Token)match(input,23,FOLLOW_18); 

            	    										newLeafNode(otherlv_11, grammarAccess.getElkSingleEdgeSectionAccess().getCommaKeyword_1_0_2_3());
            	    									
            	    // InternalElkGraph.g:1161:10: ( (lv_startY_12_0= ruleNumber ) )
            	    // InternalElkGraph.g:1162:11: (lv_startY_12_0= ruleNumber )
            	    {
            	    // InternalElkGraph.g:1162:11: (lv_startY_12_0= ruleNumber )
            	    // InternalElkGraph.g:1163:12: lv_startY_12_0= ruleNumber
            	    {

            	    												newCompositeNode(grammarAccess.getElkSingleEdgeSectionAccess().getStartYNumberParserRuleCall_1_0_2_4_0());
            	    											
            	    pushFollow(FOLLOW_24);
            	    lv_startY_12_0=ruleNumber();

            	    state._fsp--;


            	    												if (current==null) {
            	    													current = createModelElementForParent(grammarAccess.getElkSingleEdgeSectionRule());
            	    												}
            	    												set(
            	    													current,
            	    													"startY",
            	    													lv_startY_12_0,
            	    													"org.eclipse.elk.graph.text.ElkGraph.Number");
            	    												afterParserOrEnumRuleCall();
            	    											

            	    }


            	    }


            	    }


            	    }

            	     
            	    							getUnorderedGroupHelper().returnFromSelection(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0());
            	    						

            	    }


            	    }


            	    }
            	    break;
            	case 4 :
            	    // InternalElkGraph.g:1186:5: ({...}? => ( ({...}? => (otherlv_13= 'end' otherlv_14= ':' ( (lv_endX_15_0= ruleNumber ) ) otherlv_16= ',' ( (lv_endY_17_0= ruleNumber ) ) ) ) ) )
            	    {
            	    // InternalElkGraph.g:1186:5: ({...}? => ( ({...}? => (otherlv_13= 'end' otherlv_14= ':' ( (lv_endX_15_0= ruleNumber ) ) otherlv_16= ',' ( (lv_endY_17_0= ruleNumber ) ) ) ) ) )
            	    // InternalElkGraph.g:1187:6: {...}? => ( ({...}? => (otherlv_13= 'end' otherlv_14= ':' ( (lv_endX_15_0= ruleNumber ) ) otherlv_16= ',' ( (lv_endY_17_0= ruleNumber ) ) ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 3) ) {
            	        throw new FailedPredicateException(input, "ruleElkSingleEdgeSection", "getUnorderedGroupHelper().canSelect(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 3)");
            	    }
            	    // InternalElkGraph.g:1187:120: ( ({...}? => (otherlv_13= 'end' otherlv_14= ':' ( (lv_endX_15_0= ruleNumber ) ) otherlv_16= ',' ( (lv_endY_17_0= ruleNumber ) ) ) ) )
            	    // InternalElkGraph.g:1188:7: ({...}? => (otherlv_13= 'end' otherlv_14= ':' ( (lv_endX_15_0= ruleNumber ) ) otherlv_16= ',' ( (lv_endY_17_0= ruleNumber ) ) ) )
            	    {

            	    							getUnorderedGroupHelper().select(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0(), 3);
            	    						
            	    // InternalElkGraph.g:1191:10: ({...}? => (otherlv_13= 'end' otherlv_14= ':' ( (lv_endX_15_0= ruleNumber ) ) otherlv_16= ',' ( (lv_endY_17_0= ruleNumber ) ) ) )
            	    // InternalElkGraph.g:1191:11: {...}? => (otherlv_13= 'end' otherlv_14= ':' ( (lv_endX_15_0= ruleNumber ) ) otherlv_16= ',' ( (lv_endY_17_0= ruleNumber ) ) )
            	    {
            	    if ( !((true)) ) {
            	        throw new FailedPredicateException(input, "ruleElkSingleEdgeSection", "true");
            	    }
            	    // InternalElkGraph.g:1191:20: (otherlv_13= 'end' otherlv_14= ':' ( (lv_endX_15_0= ruleNumber ) ) otherlv_16= ',' ( (lv_endY_17_0= ruleNumber ) ) )
            	    // InternalElkGraph.g:1191:21: otherlv_13= 'end' otherlv_14= ':' ( (lv_endX_15_0= ruleNumber ) ) otherlv_16= ',' ( (lv_endY_17_0= ruleNumber ) )
            	    {
            	    otherlv_13=(Token)match(input,31,FOLLOW_11); 

            	    										newLeafNode(otherlv_13, grammarAccess.getElkSingleEdgeSectionAccess().getEndKeyword_1_0_3_0());
            	    									
            	    otherlv_14=(Token)match(input,18,FOLLOW_18); 

            	    										newLeafNode(otherlv_14, grammarAccess.getElkSingleEdgeSectionAccess().getColonKeyword_1_0_3_1());
            	    									
            	    // InternalElkGraph.g:1199:10: ( (lv_endX_15_0= ruleNumber ) )
            	    // InternalElkGraph.g:1200:11: (lv_endX_15_0= ruleNumber )
            	    {
            	    // InternalElkGraph.g:1200:11: (lv_endX_15_0= ruleNumber )
            	    // InternalElkGraph.g:1201:12: lv_endX_15_0= ruleNumber
            	    {

            	    												newCompositeNode(grammarAccess.getElkSingleEdgeSectionAccess().getEndXNumberParserRuleCall_1_0_3_2_0());
            	    											
            	    pushFollow(FOLLOW_19);
            	    lv_endX_15_0=ruleNumber();

            	    state._fsp--;


            	    												if (current==null) {
            	    													current = createModelElementForParent(grammarAccess.getElkSingleEdgeSectionRule());
            	    												}
            	    												set(
            	    													current,
            	    													"endX",
            	    													lv_endX_15_0,
            	    													"org.eclipse.elk.graph.text.ElkGraph.Number");
            	    												afterParserOrEnumRuleCall();
            	    											

            	    }


            	    }

            	    otherlv_16=(Token)match(input,23,FOLLOW_18); 

            	    										newLeafNode(otherlv_16, grammarAccess.getElkSingleEdgeSectionAccess().getCommaKeyword_1_0_3_3());
            	    									
            	    // InternalElkGraph.g:1222:10: ( (lv_endY_17_0= ruleNumber ) )
            	    // InternalElkGraph.g:1223:11: (lv_endY_17_0= ruleNumber )
            	    {
            	    // InternalElkGraph.g:1223:11: (lv_endY_17_0= ruleNumber )
            	    // InternalElkGraph.g:1224:12: lv_endY_17_0= ruleNumber
            	    {

            	    												newCompositeNode(grammarAccess.getElkSingleEdgeSectionAccess().getEndYNumberParserRuleCall_1_0_3_4_0());
            	    											
            	    pushFollow(FOLLOW_24);
            	    lv_endY_17_0=ruleNumber();

            	    state._fsp--;


            	    												if (current==null) {
            	    													current = createModelElementForParent(grammarAccess.getElkSingleEdgeSectionRule());
            	    												}
            	    												set(
            	    													current,
            	    													"endY",
            	    													lv_endY_17_0,
            	    													"org.eclipse.elk.graph.text.ElkGraph.Number");
            	    												afterParserOrEnumRuleCall();
            	    											

            	    }


            	    }


            	    }


            	    }

            	     
            	    							getUnorderedGroupHelper().returnFromSelection(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0());
            	    						

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop27;
                }
            } while (true);


            }


            }

             
            					  getUnorderedGroupHelper().leave(grammarAccess.getElkSingleEdgeSectionAccess().getUnorderedGroup_1_0());
            					

            }

            // InternalElkGraph.g:1254:4: (otherlv_18= 'bends' otherlv_19= ':' ( (lv_bendPoints_20_0= ruleElkBendPoint ) ) (otherlv_21= '|' ( (lv_bendPoints_22_0= ruleElkBendPoint ) ) )* )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==32) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // InternalElkGraph.g:1255:5: otherlv_18= 'bends' otherlv_19= ':' ( (lv_bendPoints_20_0= ruleElkBendPoint ) ) (otherlv_21= '|' ( (lv_bendPoints_22_0= ruleElkBendPoint ) ) )*
                    {
                    otherlv_18=(Token)match(input,32,FOLLOW_11); 

                    					newLeafNode(otherlv_18, grammarAccess.getElkSingleEdgeSectionAccess().getBendsKeyword_1_1_0());
                    				
                    otherlv_19=(Token)match(input,18,FOLLOW_18); 

                    					newLeafNode(otherlv_19, grammarAccess.getElkSingleEdgeSectionAccess().getColonKeyword_1_1_1());
                    				
                    // InternalElkGraph.g:1263:5: ( (lv_bendPoints_20_0= ruleElkBendPoint ) )
                    // InternalElkGraph.g:1264:6: (lv_bendPoints_20_0= ruleElkBendPoint )
                    {
                    // InternalElkGraph.g:1264:6: (lv_bendPoints_20_0= ruleElkBendPoint )
                    // InternalElkGraph.g:1265:7: lv_bendPoints_20_0= ruleElkBendPoint
                    {

                    							newCompositeNode(grammarAccess.getElkSingleEdgeSectionAccess().getBendPointsElkBendPointParserRuleCall_1_1_2_0());
                    						
                    pushFollow(FOLLOW_25);
                    lv_bendPoints_20_0=ruleElkBendPoint();

                    state._fsp--;


                    							if (current==null) {
                    								current = createModelElementForParent(grammarAccess.getElkSingleEdgeSectionRule());
                    							}
                    							add(
                    								current,
                    								"bendPoints",
                    								lv_bendPoints_20_0,
                    								"org.eclipse.elk.graph.text.ElkGraph.ElkBendPoint");
                    							afterParserOrEnumRuleCall();
                    						

                    }


                    }

                    // InternalElkGraph.g:1282:5: (otherlv_21= '|' ( (lv_bendPoints_22_0= ruleElkBendPoint ) ) )*
                    loop28:
                    do {
                        int alt28=2;
                        int LA28_0 = input.LA(1);

                        if ( (LA28_0==33) ) {
                            alt28=1;
                        }


                        switch (alt28) {
                    	case 1 :
                    	    // InternalElkGraph.g:1283:6: otherlv_21= '|' ( (lv_bendPoints_22_0= ruleElkBendPoint ) )
                    	    {
                    	    otherlv_21=(Token)match(input,33,FOLLOW_18); 

                    	    						newLeafNode(otherlv_21, grammarAccess.getElkSingleEdgeSectionAccess().getVerticalLineKeyword_1_1_3_0());
                    	    					
                    	    // InternalElkGraph.g:1287:6: ( (lv_bendPoints_22_0= ruleElkBendPoint ) )
                    	    // InternalElkGraph.g:1288:7: (lv_bendPoints_22_0= ruleElkBendPoint )
                    	    {
                    	    // InternalElkGraph.g:1288:7: (lv_bendPoints_22_0= ruleElkBendPoint )
                    	    // InternalElkGraph.g:1289:8: lv_bendPoints_22_0= ruleElkBendPoint
                    	    {

                    	    								newCompositeNode(grammarAccess.getElkSingleEdgeSectionAccess().getBendPointsElkBendPointParserRuleCall_1_1_3_1_0());
                    	    							
                    	    pushFollow(FOLLOW_25);
                    	    lv_bendPoints_22_0=ruleElkBendPoint();

                    	    state._fsp--;


                    	    								if (current==null) {
                    	    									current = createModelElementForParent(grammarAccess.getElkSingleEdgeSectionRule());
                    	    								}
                    	    								add(
                    	    									current,
                    	    									"bendPoints",
                    	    									lv_bendPoints_22_0,
                    	    									"org.eclipse.elk.graph.text.ElkGraph.ElkBendPoint");
                    	    								afterParserOrEnumRuleCall();
                    	    							

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop28;
                        }
                    } while (true);


                    }
                    break;

            }

            // InternalElkGraph.g:1308:4: ( (lv_properties_23_0= ruleProperty ) )*
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( (LA30_0==RULE_ID) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // InternalElkGraph.g:1309:5: (lv_properties_23_0= ruleProperty )
            	    {
            	    // InternalElkGraph.g:1309:5: (lv_properties_23_0= ruleProperty )
            	    // InternalElkGraph.g:1310:6: lv_properties_23_0= ruleProperty
            	    {

            	    						newCompositeNode(grammarAccess.getElkSingleEdgeSectionAccess().getPropertiesPropertyParserRuleCall_1_2_0());
            	    					
            	    pushFollow(FOLLOW_26);
            	    lv_properties_23_0=ruleProperty();

            	    state._fsp--;


            	    						if (current==null) {
            	    							current = createModelElementForParent(grammarAccess.getElkSingleEdgeSectionRule());
            	    						}
            	    						add(
            	    							current,
            	    							"properties",
            	    							lv_properties_23_0,
            	    							"org.eclipse.elk.graph.text.ElkGraph.Property");
            	    						afterParserOrEnumRuleCall();
            	    					

            	    }


            	    }
            	    break;

            	default :
            	    break loop30;
                }
            } while (true);


            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleElkSingleEdgeSection"


    // $ANTLR start "entryRuleElkEdgeSection"
    // InternalElkGraph.g:1332:1: entryRuleElkEdgeSection returns [EObject current=null] : iv_ruleElkEdgeSection= ruleElkEdgeSection EOF ;
    public final EObject entryRuleElkEdgeSection() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleElkEdgeSection = null;


        try {
            // InternalElkGraph.g:1332:55: (iv_ruleElkEdgeSection= ruleElkEdgeSection EOF )
            // InternalElkGraph.g:1333:2: iv_ruleElkEdgeSection= ruleElkEdgeSection EOF
            {
             newCompositeNode(grammarAccess.getElkEdgeSectionRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleElkEdgeSection=ruleElkEdgeSection();

            state._fsp--;

             current =iv_ruleElkEdgeSection; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleElkEdgeSection"


    // $ANTLR start "ruleElkEdgeSection"
    // InternalElkGraph.g:1339:1: ruleElkEdgeSection returns [EObject current=null] : (otherlv_0= 'section' ( (lv_identifier_1_0= RULE_ID ) ) (otherlv_2= '->' ( (otherlv_3= RULE_ID ) ) (otherlv_4= ',' ( (otherlv_5= RULE_ID ) ) )* )? otherlv_6= '[' ( ( ( ( ( ({...}? => ( ({...}? => (otherlv_8= 'incoming' otherlv_9= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_11= 'outgoing' otherlv_12= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_14= 'start' otherlv_15= ':' ( (lv_startX_16_0= ruleNumber ) ) otherlv_17= ',' ( (lv_startY_18_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'end' otherlv_20= ':' ( (lv_endX_21_0= ruleNumber ) ) otherlv_22= ',' ( (lv_endY_23_0= ruleNumber ) ) ) ) ) ) )* ) ) ) (otherlv_24= 'bends' otherlv_25= ':' ( (lv_bendPoints_26_0= ruleElkBendPoint ) ) (otherlv_27= '|' ( (lv_bendPoints_28_0= ruleElkBendPoint ) ) )* )? ( (lv_properties_29_0= ruleProperty ) )* ) otherlv_30= ']' ) ;
    public final EObject ruleElkEdgeSection() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_identifier_1_0=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Token otherlv_5=null;
        Token otherlv_6=null;
        Token otherlv_8=null;
        Token otherlv_9=null;
        Token otherlv_11=null;
        Token otherlv_12=null;
        Token otherlv_14=null;
        Token otherlv_15=null;
        Token otherlv_17=null;
        Token otherlv_19=null;
        Token otherlv_20=null;
        Token otherlv_22=null;
        Token otherlv_24=null;
        Token otherlv_25=null;
        Token otherlv_27=null;
        Token otherlv_30=null;
        AntlrDatatypeRuleToken lv_startX_16_0 = null;

        AntlrDatatypeRuleToken lv_startY_18_0 = null;

        AntlrDatatypeRuleToken lv_endX_21_0 = null;

        AntlrDatatypeRuleToken lv_endY_23_0 = null;

        EObject lv_bendPoints_26_0 = null;

        EObject lv_bendPoints_28_0 = null;

        EObject lv_properties_29_0 = null;



        	enterRule();

        try {
            // InternalElkGraph.g:1345:2: ( (otherlv_0= 'section' ( (lv_identifier_1_0= RULE_ID ) ) (otherlv_2= '->' ( (otherlv_3= RULE_ID ) ) (otherlv_4= ',' ( (otherlv_5= RULE_ID ) ) )* )? otherlv_6= '[' ( ( ( ( ( ({...}? => ( ({...}? => (otherlv_8= 'incoming' otherlv_9= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_11= 'outgoing' otherlv_12= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_14= 'start' otherlv_15= ':' ( (lv_startX_16_0= ruleNumber ) ) otherlv_17= ',' ( (lv_startY_18_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'end' otherlv_20= ':' ( (lv_endX_21_0= ruleNumber ) ) otherlv_22= ',' ( (lv_endY_23_0= ruleNumber ) ) ) ) ) ) )* ) ) ) (otherlv_24= 'bends' otherlv_25= ':' ( (lv_bendPoints_26_0= ruleElkBendPoint ) ) (otherlv_27= '|' ( (lv_bendPoints_28_0= ruleElkBendPoint ) ) )* )? ( (lv_properties_29_0= ruleProperty ) )* ) otherlv_30= ']' ) )
            // InternalElkGraph.g:1346:2: (otherlv_0= 'section' ( (lv_identifier_1_0= RULE_ID ) ) (otherlv_2= '->' ( (otherlv_3= RULE_ID ) ) (otherlv_4= ',' ( (otherlv_5= RULE_ID ) ) )* )? otherlv_6= '[' ( ( ( ( ( ({...}? => ( ({...}? => (otherlv_8= 'incoming' otherlv_9= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_11= 'outgoing' otherlv_12= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_14= 'start' otherlv_15= ':' ( (lv_startX_16_0= ruleNumber ) ) otherlv_17= ',' ( (lv_startY_18_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'end' otherlv_20= ':' ( (lv_endX_21_0= ruleNumber ) ) otherlv_22= ',' ( (lv_endY_23_0= ruleNumber ) ) ) ) ) ) )* ) ) ) (otherlv_24= 'bends' otherlv_25= ':' ( (lv_bendPoints_26_0= ruleElkBendPoint ) ) (otherlv_27= '|' ( (lv_bendPoints_28_0= ruleElkBendPoint ) ) )* )? ( (lv_properties_29_0= ruleProperty ) )* ) otherlv_30= ']' )
            {
            // InternalElkGraph.g:1346:2: (otherlv_0= 'section' ( (lv_identifier_1_0= RULE_ID ) ) (otherlv_2= '->' ( (otherlv_3= RULE_ID ) ) (otherlv_4= ',' ( (otherlv_5= RULE_ID ) ) )* )? otherlv_6= '[' ( ( ( ( ( ({...}? => ( ({...}? => (otherlv_8= 'incoming' otherlv_9= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_11= 'outgoing' otherlv_12= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_14= 'start' otherlv_15= ':' ( (lv_startX_16_0= ruleNumber ) ) otherlv_17= ',' ( (lv_startY_18_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'end' otherlv_20= ':' ( (lv_endX_21_0= ruleNumber ) ) otherlv_22= ',' ( (lv_endY_23_0= ruleNumber ) ) ) ) ) ) )* ) ) ) (otherlv_24= 'bends' otherlv_25= ':' ( (lv_bendPoints_26_0= ruleElkBendPoint ) ) (otherlv_27= '|' ( (lv_bendPoints_28_0= ruleElkBendPoint ) ) )* )? ( (lv_properties_29_0= ruleProperty ) )* ) otherlv_30= ']' )
            // InternalElkGraph.g:1347:3: otherlv_0= 'section' ( (lv_identifier_1_0= RULE_ID ) ) (otherlv_2= '->' ( (otherlv_3= RULE_ID ) ) (otherlv_4= ',' ( (otherlv_5= RULE_ID ) ) )* )? otherlv_6= '[' ( ( ( ( ( ({...}? => ( ({...}? => (otherlv_8= 'incoming' otherlv_9= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_11= 'outgoing' otherlv_12= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_14= 'start' otherlv_15= ':' ( (lv_startX_16_0= ruleNumber ) ) otherlv_17= ',' ( (lv_startY_18_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'end' otherlv_20= ':' ( (lv_endX_21_0= ruleNumber ) ) otherlv_22= ',' ( (lv_endY_23_0= ruleNumber ) ) ) ) ) ) )* ) ) ) (otherlv_24= 'bends' otherlv_25= ':' ( (lv_bendPoints_26_0= ruleElkBendPoint ) ) (otherlv_27= '|' ( (lv_bendPoints_28_0= ruleElkBendPoint ) ) )* )? ( (lv_properties_29_0= ruleProperty ) )* ) otherlv_30= ']'
            {
            otherlv_0=(Token)match(input,34,FOLLOW_3); 

            			newLeafNode(otherlv_0, grammarAccess.getElkEdgeSectionAccess().getSectionKeyword_0());
            		
            // InternalElkGraph.g:1351:3: ( (lv_identifier_1_0= RULE_ID ) )
            // InternalElkGraph.g:1352:4: (lv_identifier_1_0= RULE_ID )
            {
            // InternalElkGraph.g:1352:4: (lv_identifier_1_0= RULE_ID )
            // InternalElkGraph.g:1353:5: lv_identifier_1_0= RULE_ID
            {
            lv_identifier_1_0=(Token)match(input,RULE_ID,FOLLOW_27); 

            					newLeafNode(lv_identifier_1_0, grammarAccess.getElkEdgeSectionAccess().getIdentifierIDTerminalRuleCall_1_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getElkEdgeSectionRule());
            					}
            					setWithLastConsumed(
            						current,
            						"identifier",
            						lv_identifier_1_0,
            						"org.eclipse.xtext.common.Terminals.ID");
            				

            }


            }

            // InternalElkGraph.g:1369:3: (otherlv_2= '->' ( (otherlv_3= RULE_ID ) ) (otherlv_4= ',' ( (otherlv_5= RULE_ID ) ) )* )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==27) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // InternalElkGraph.g:1370:4: otherlv_2= '->' ( (otherlv_3= RULE_ID ) ) (otherlv_4= ',' ( (otherlv_5= RULE_ID ) ) )*
                    {
                    otherlv_2=(Token)match(input,27,FOLLOW_3); 

                    				newLeafNode(otherlv_2, grammarAccess.getElkEdgeSectionAccess().getHyphenMinusGreaterThanSignKeyword_2_0());
                    			
                    // InternalElkGraph.g:1374:4: ( (otherlv_3= RULE_ID ) )
                    // InternalElkGraph.g:1375:5: (otherlv_3= RULE_ID )
                    {
                    // InternalElkGraph.g:1375:5: (otherlv_3= RULE_ID )
                    // InternalElkGraph.g:1376:6: otherlv_3= RULE_ID
                    {

                    						if (current==null) {
                    							current = createModelElement(grammarAccess.getElkEdgeSectionRule());
                    						}
                    					
                    otherlv_3=(Token)match(input,RULE_ID,FOLLOW_28); 

                    						newLeafNode(otherlv_3, grammarAccess.getElkEdgeSectionAccess().getOutgoingSectionsElkEdgeSectionCrossReference_2_1_0());
                    					

                    }


                    }

                    // InternalElkGraph.g:1387:4: (otherlv_4= ',' ( (otherlv_5= RULE_ID ) ) )*
                    loop31:
                    do {
                        int alt31=2;
                        int LA31_0 = input.LA(1);

                        if ( (LA31_0==23) ) {
                            alt31=1;
                        }


                        switch (alt31) {
                    	case 1 :
                    	    // InternalElkGraph.g:1388:5: otherlv_4= ',' ( (otherlv_5= RULE_ID ) )
                    	    {
                    	    otherlv_4=(Token)match(input,23,FOLLOW_3); 

                    	    					newLeafNode(otherlv_4, grammarAccess.getElkEdgeSectionAccess().getCommaKeyword_2_2_0());
                    	    				
                    	    // InternalElkGraph.g:1392:5: ( (otherlv_5= RULE_ID ) )
                    	    // InternalElkGraph.g:1393:6: (otherlv_5= RULE_ID )
                    	    {
                    	    // InternalElkGraph.g:1393:6: (otherlv_5= RULE_ID )
                    	    // InternalElkGraph.g:1394:7: otherlv_5= RULE_ID
                    	    {

                    	    							if (current==null) {
                    	    								current = createModelElement(grammarAccess.getElkEdgeSectionRule());
                    	    							}
                    	    						
                    	    otherlv_5=(Token)match(input,RULE_ID,FOLLOW_28); 

                    	    							newLeafNode(otherlv_5, grammarAccess.getElkEdgeSectionAccess().getOutgoingSectionsElkEdgeSectionCrossReference_2_2_1_0());
                    	    						

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop31;
                        }
                    } while (true);


                    }
                    break;

            }

            otherlv_6=(Token)match(input,21,FOLLOW_29); 

            			newLeafNode(otherlv_6, grammarAccess.getElkEdgeSectionAccess().getLeftSquareBracketKeyword_3());
            		
            // InternalElkGraph.g:1411:3: ( ( ( ( ( ({...}? => ( ({...}? => (otherlv_8= 'incoming' otherlv_9= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_11= 'outgoing' otherlv_12= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_14= 'start' otherlv_15= ':' ( (lv_startX_16_0= ruleNumber ) ) otherlv_17= ',' ( (lv_startY_18_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'end' otherlv_20= ':' ( (lv_endX_21_0= ruleNumber ) ) otherlv_22= ',' ( (lv_endY_23_0= ruleNumber ) ) ) ) ) ) )* ) ) ) (otherlv_24= 'bends' otherlv_25= ':' ( (lv_bendPoints_26_0= ruleElkBendPoint ) ) (otherlv_27= '|' ( (lv_bendPoints_28_0= ruleElkBendPoint ) ) )* )? ( (lv_properties_29_0= ruleProperty ) )* )
            // InternalElkGraph.g:1412:4: ( ( ( ( ({...}? => ( ({...}? => (otherlv_8= 'incoming' otherlv_9= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_11= 'outgoing' otherlv_12= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_14= 'start' otherlv_15= ':' ( (lv_startX_16_0= ruleNumber ) ) otherlv_17= ',' ( (lv_startY_18_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'end' otherlv_20= ':' ( (lv_endX_21_0= ruleNumber ) ) otherlv_22= ',' ( (lv_endY_23_0= ruleNumber ) ) ) ) ) ) )* ) ) ) (otherlv_24= 'bends' otherlv_25= ':' ( (lv_bendPoints_26_0= ruleElkBendPoint ) ) (otherlv_27= '|' ( (lv_bendPoints_28_0= ruleElkBendPoint ) ) )* )? ( (lv_properties_29_0= ruleProperty ) )*
            {
            // InternalElkGraph.g:1412:4: ( ( ( ( ({...}? => ( ({...}? => (otherlv_8= 'incoming' otherlv_9= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_11= 'outgoing' otherlv_12= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_14= 'start' otherlv_15= ':' ( (lv_startX_16_0= ruleNumber ) ) otherlv_17= ',' ( (lv_startY_18_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'end' otherlv_20= ':' ( (lv_endX_21_0= ruleNumber ) ) otherlv_22= ',' ( (lv_endY_23_0= ruleNumber ) ) ) ) ) ) )* ) ) )
            // InternalElkGraph.g:1413:5: ( ( ( ({...}? => ( ({...}? => (otherlv_8= 'incoming' otherlv_9= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_11= 'outgoing' otherlv_12= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_14= 'start' otherlv_15= ':' ( (lv_startX_16_0= ruleNumber ) ) otherlv_17= ',' ( (lv_startY_18_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'end' otherlv_20= ':' ( (lv_endX_21_0= ruleNumber ) ) otherlv_22= ',' ( (lv_endY_23_0= ruleNumber ) ) ) ) ) ) )* ) )
            {
            // InternalElkGraph.g:1413:5: ( ( ( ({...}? => ( ({...}? => (otherlv_8= 'incoming' otherlv_9= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_11= 'outgoing' otherlv_12= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_14= 'start' otherlv_15= ':' ( (lv_startX_16_0= ruleNumber ) ) otherlv_17= ',' ( (lv_startY_18_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'end' otherlv_20= ':' ( (lv_endX_21_0= ruleNumber ) ) otherlv_22= ',' ( (lv_endY_23_0= ruleNumber ) ) ) ) ) ) )* ) )
            // InternalElkGraph.g:1414:6: ( ( ({...}? => ( ({...}? => (otherlv_8= 'incoming' otherlv_9= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_11= 'outgoing' otherlv_12= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_14= 'start' otherlv_15= ':' ( (lv_startX_16_0= ruleNumber ) ) otherlv_17= ',' ( (lv_startY_18_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'end' otherlv_20= ':' ( (lv_endX_21_0= ruleNumber ) ) otherlv_22= ',' ( (lv_endY_23_0= ruleNumber ) ) ) ) ) ) )* )
            {
             
            					  getUnorderedGroupHelper().enter(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0());
            					
            // InternalElkGraph.g:1417:6: ( ( ({...}? => ( ({...}? => (otherlv_8= 'incoming' otherlv_9= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_11= 'outgoing' otherlv_12= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_14= 'start' otherlv_15= ':' ( (lv_startX_16_0= ruleNumber ) ) otherlv_17= ',' ( (lv_startY_18_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'end' otherlv_20= ':' ( (lv_endX_21_0= ruleNumber ) ) otherlv_22= ',' ( (lv_endY_23_0= ruleNumber ) ) ) ) ) ) )* )
            // InternalElkGraph.g:1418:7: ( ({...}? => ( ({...}? => (otherlv_8= 'incoming' otherlv_9= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_11= 'outgoing' otherlv_12= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_14= 'start' otherlv_15= ':' ( (lv_startX_16_0= ruleNumber ) ) otherlv_17= ',' ( (lv_startY_18_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'end' otherlv_20= ':' ( (lv_endX_21_0= ruleNumber ) ) otherlv_22= ',' ( (lv_endY_23_0= ruleNumber ) ) ) ) ) ) )*
            {
            // InternalElkGraph.g:1418:7: ( ({...}? => ( ({...}? => (otherlv_8= 'incoming' otherlv_9= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_11= 'outgoing' otherlv_12= ':' ( ( ruleQualifiedId ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_14= 'start' otherlv_15= ':' ( (lv_startX_16_0= ruleNumber ) ) otherlv_17= ',' ( (lv_startY_18_0= ruleNumber ) ) ) ) ) ) | ({...}? => ( ({...}? => (otherlv_19= 'end' otherlv_20= ':' ( (lv_endX_21_0= ruleNumber ) ) otherlv_22= ',' ( (lv_endY_23_0= ruleNumber ) ) ) ) ) ) )*
            loop33:
            do {
                int alt33=5;
                int LA33_0 = input.LA(1);

                if ( LA33_0 == 28 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 0) ) {
                    alt33=1;
                }
                else if ( LA33_0 == 29 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 1) ) {
                    alt33=2;
                }
                else if ( LA33_0 == 30 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 2) ) {
                    alt33=3;
                }
                else if ( LA33_0 == 31 && getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 3) ) {
                    alt33=4;
                }


                switch (alt33) {
            	case 1 :
            	    // InternalElkGraph.g:1419:5: ({...}? => ( ({...}? => (otherlv_8= 'incoming' otherlv_9= ':' ( ( ruleQualifiedId ) ) ) ) ) )
            	    {
            	    // InternalElkGraph.g:1419:5: ({...}? => ( ({...}? => (otherlv_8= 'incoming' otherlv_9= ':' ( ( ruleQualifiedId ) ) ) ) ) )
            	    // InternalElkGraph.g:1420:6: {...}? => ( ({...}? => (otherlv_8= 'incoming' otherlv_9= ':' ( ( ruleQualifiedId ) ) ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 0) ) {
            	        throw new FailedPredicateException(input, "ruleElkEdgeSection", "getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 0)");
            	    }
            	    // InternalElkGraph.g:1420:114: ( ({...}? => (otherlv_8= 'incoming' otherlv_9= ':' ( ( ruleQualifiedId ) ) ) ) )
            	    // InternalElkGraph.g:1421:7: ({...}? => (otherlv_8= 'incoming' otherlv_9= ':' ( ( ruleQualifiedId ) ) ) )
            	    {

            	    							getUnorderedGroupHelper().select(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 0);
            	    						
            	    // InternalElkGraph.g:1424:10: ({...}? => (otherlv_8= 'incoming' otherlv_9= ':' ( ( ruleQualifiedId ) ) ) )
            	    // InternalElkGraph.g:1424:11: {...}? => (otherlv_8= 'incoming' otherlv_9= ':' ( ( ruleQualifiedId ) ) )
            	    {
            	    if ( !((true)) ) {
            	        throw new FailedPredicateException(input, "ruleElkEdgeSection", "true");
            	    }
            	    // InternalElkGraph.g:1424:20: (otherlv_8= 'incoming' otherlv_9= ':' ( ( ruleQualifiedId ) ) )
            	    // InternalElkGraph.g:1424:21: otherlv_8= 'incoming' otherlv_9= ':' ( ( ruleQualifiedId ) )
            	    {
            	    otherlv_8=(Token)match(input,28,FOLLOW_11); 

            	    										newLeafNode(otherlv_8, grammarAccess.getElkEdgeSectionAccess().getIncomingKeyword_4_0_0_0());
            	    									
            	    otherlv_9=(Token)match(input,18,FOLLOW_3); 

            	    										newLeafNode(otherlv_9, grammarAccess.getElkEdgeSectionAccess().getColonKeyword_4_0_0_1());
            	    									
            	    // InternalElkGraph.g:1432:10: ( ( ruleQualifiedId ) )
            	    // InternalElkGraph.g:1433:11: ( ruleQualifiedId )
            	    {
            	    // InternalElkGraph.g:1433:11: ( ruleQualifiedId )
            	    // InternalElkGraph.g:1434:12: ruleQualifiedId
            	    {

            	    												if (current==null) {
            	    													current = createModelElement(grammarAccess.getElkEdgeSectionRule());
            	    												}
            	    											

            	    												newCompositeNode(grammarAccess.getElkEdgeSectionAccess().getIncomingShapeElkConnectableShapeCrossReference_4_0_0_2_0());
            	    											
            	    pushFollow(FOLLOW_29);
            	    ruleQualifiedId();

            	    state._fsp--;


            	    												afterParserOrEnumRuleCall();
            	    											

            	    }


            	    }


            	    }


            	    }

            	     
            	    							getUnorderedGroupHelper().returnFromSelection(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0());
            	    						

            	    }


            	    }


            	    }
            	    break;
            	case 2 :
            	    // InternalElkGraph.g:1454:5: ({...}? => ( ({...}? => (otherlv_11= 'outgoing' otherlv_12= ':' ( ( ruleQualifiedId ) ) ) ) ) )
            	    {
            	    // InternalElkGraph.g:1454:5: ({...}? => ( ({...}? => (otherlv_11= 'outgoing' otherlv_12= ':' ( ( ruleQualifiedId ) ) ) ) ) )
            	    // InternalElkGraph.g:1455:6: {...}? => ( ({...}? => (otherlv_11= 'outgoing' otherlv_12= ':' ( ( ruleQualifiedId ) ) ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 1) ) {
            	        throw new FailedPredicateException(input, "ruleElkEdgeSection", "getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 1)");
            	    }
            	    // InternalElkGraph.g:1455:114: ( ({...}? => (otherlv_11= 'outgoing' otherlv_12= ':' ( ( ruleQualifiedId ) ) ) ) )
            	    // InternalElkGraph.g:1456:7: ({...}? => (otherlv_11= 'outgoing' otherlv_12= ':' ( ( ruleQualifiedId ) ) ) )
            	    {

            	    							getUnorderedGroupHelper().select(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 1);
            	    						
            	    // InternalElkGraph.g:1459:10: ({...}? => (otherlv_11= 'outgoing' otherlv_12= ':' ( ( ruleQualifiedId ) ) ) )
            	    // InternalElkGraph.g:1459:11: {...}? => (otherlv_11= 'outgoing' otherlv_12= ':' ( ( ruleQualifiedId ) ) )
            	    {
            	    if ( !((true)) ) {
            	        throw new FailedPredicateException(input, "ruleElkEdgeSection", "true");
            	    }
            	    // InternalElkGraph.g:1459:20: (otherlv_11= 'outgoing' otherlv_12= ':' ( ( ruleQualifiedId ) ) )
            	    // InternalElkGraph.g:1459:21: otherlv_11= 'outgoing' otherlv_12= ':' ( ( ruleQualifiedId ) )
            	    {
            	    otherlv_11=(Token)match(input,29,FOLLOW_11); 

            	    										newLeafNode(otherlv_11, grammarAccess.getElkEdgeSectionAccess().getOutgoingKeyword_4_0_1_0());
            	    									
            	    otherlv_12=(Token)match(input,18,FOLLOW_3); 

            	    										newLeafNode(otherlv_12, grammarAccess.getElkEdgeSectionAccess().getColonKeyword_4_0_1_1());
            	    									
            	    // InternalElkGraph.g:1467:10: ( ( ruleQualifiedId ) )
            	    // InternalElkGraph.g:1468:11: ( ruleQualifiedId )
            	    {
            	    // InternalElkGraph.g:1468:11: ( ruleQualifiedId )
            	    // InternalElkGraph.g:1469:12: ruleQualifiedId
            	    {

            	    												if (current==null) {
            	    													current = createModelElement(grammarAccess.getElkEdgeSectionRule());
            	    												}
            	    											

            	    												newCompositeNode(grammarAccess.getElkEdgeSectionAccess().getOutgoingShapeElkConnectableShapeCrossReference_4_0_1_2_0());
            	    											
            	    pushFollow(FOLLOW_29);
            	    ruleQualifiedId();

            	    state._fsp--;


            	    												afterParserOrEnumRuleCall();
            	    											

            	    }


            	    }


            	    }


            	    }

            	     
            	    							getUnorderedGroupHelper().returnFromSelection(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0());
            	    						

            	    }


            	    }


            	    }
            	    break;
            	case 3 :
            	    // InternalElkGraph.g:1489:5: ({...}? => ( ({...}? => (otherlv_14= 'start' otherlv_15= ':' ( (lv_startX_16_0= ruleNumber ) ) otherlv_17= ',' ( (lv_startY_18_0= ruleNumber ) ) ) ) ) )
            	    {
            	    // InternalElkGraph.g:1489:5: ({...}? => ( ({...}? => (otherlv_14= 'start' otherlv_15= ':' ( (lv_startX_16_0= ruleNumber ) ) otherlv_17= ',' ( (lv_startY_18_0= ruleNumber ) ) ) ) ) )
            	    // InternalElkGraph.g:1490:6: {...}? => ( ({...}? => (otherlv_14= 'start' otherlv_15= ':' ( (lv_startX_16_0= ruleNumber ) ) otherlv_17= ',' ( (lv_startY_18_0= ruleNumber ) ) ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 2) ) {
            	        throw new FailedPredicateException(input, "ruleElkEdgeSection", "getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 2)");
            	    }
            	    // InternalElkGraph.g:1490:114: ( ({...}? => (otherlv_14= 'start' otherlv_15= ':' ( (lv_startX_16_0= ruleNumber ) ) otherlv_17= ',' ( (lv_startY_18_0= ruleNumber ) ) ) ) )
            	    // InternalElkGraph.g:1491:7: ({...}? => (otherlv_14= 'start' otherlv_15= ':' ( (lv_startX_16_0= ruleNumber ) ) otherlv_17= ',' ( (lv_startY_18_0= ruleNumber ) ) ) )
            	    {

            	    							getUnorderedGroupHelper().select(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 2);
            	    						
            	    // InternalElkGraph.g:1494:10: ({...}? => (otherlv_14= 'start' otherlv_15= ':' ( (lv_startX_16_0= ruleNumber ) ) otherlv_17= ',' ( (lv_startY_18_0= ruleNumber ) ) ) )
            	    // InternalElkGraph.g:1494:11: {...}? => (otherlv_14= 'start' otherlv_15= ':' ( (lv_startX_16_0= ruleNumber ) ) otherlv_17= ',' ( (lv_startY_18_0= ruleNumber ) ) )
            	    {
            	    if ( !((true)) ) {
            	        throw new FailedPredicateException(input, "ruleElkEdgeSection", "true");
            	    }
            	    // InternalElkGraph.g:1494:20: (otherlv_14= 'start' otherlv_15= ':' ( (lv_startX_16_0= ruleNumber ) ) otherlv_17= ',' ( (lv_startY_18_0= ruleNumber ) ) )
            	    // InternalElkGraph.g:1494:21: otherlv_14= 'start' otherlv_15= ':' ( (lv_startX_16_0= ruleNumber ) ) otherlv_17= ',' ( (lv_startY_18_0= ruleNumber ) )
            	    {
            	    otherlv_14=(Token)match(input,30,FOLLOW_11); 

            	    										newLeafNode(otherlv_14, grammarAccess.getElkEdgeSectionAccess().getStartKeyword_4_0_2_0());
            	    									
            	    otherlv_15=(Token)match(input,18,FOLLOW_18); 

            	    										newLeafNode(otherlv_15, grammarAccess.getElkEdgeSectionAccess().getColonKeyword_4_0_2_1());
            	    									
            	    // InternalElkGraph.g:1502:10: ( (lv_startX_16_0= ruleNumber ) )
            	    // InternalElkGraph.g:1503:11: (lv_startX_16_0= ruleNumber )
            	    {
            	    // InternalElkGraph.g:1503:11: (lv_startX_16_0= ruleNumber )
            	    // InternalElkGraph.g:1504:12: lv_startX_16_0= ruleNumber
            	    {

            	    												newCompositeNode(grammarAccess.getElkEdgeSectionAccess().getStartXNumberParserRuleCall_4_0_2_2_0());
            	    											
            	    pushFollow(FOLLOW_19);
            	    lv_startX_16_0=ruleNumber();

            	    state._fsp--;


            	    												if (current==null) {
            	    													current = createModelElementForParent(grammarAccess.getElkEdgeSectionRule());
            	    												}
            	    												set(
            	    													current,
            	    													"startX",
            	    													lv_startX_16_0,
            	    													"org.eclipse.elk.graph.text.ElkGraph.Number");
            	    												afterParserOrEnumRuleCall();
            	    											

            	    }


            	    }

            	    otherlv_17=(Token)match(input,23,FOLLOW_18); 

            	    										newLeafNode(otherlv_17, grammarAccess.getElkEdgeSectionAccess().getCommaKeyword_4_0_2_3());
            	    									
            	    // InternalElkGraph.g:1525:10: ( (lv_startY_18_0= ruleNumber ) )
            	    // InternalElkGraph.g:1526:11: (lv_startY_18_0= ruleNumber )
            	    {
            	    // InternalElkGraph.g:1526:11: (lv_startY_18_0= ruleNumber )
            	    // InternalElkGraph.g:1527:12: lv_startY_18_0= ruleNumber
            	    {

            	    												newCompositeNode(grammarAccess.getElkEdgeSectionAccess().getStartYNumberParserRuleCall_4_0_2_4_0());
            	    											
            	    pushFollow(FOLLOW_29);
            	    lv_startY_18_0=ruleNumber();

            	    state._fsp--;


            	    												if (current==null) {
            	    													current = createModelElementForParent(grammarAccess.getElkEdgeSectionRule());
            	    												}
            	    												set(
            	    													current,
            	    													"startY",
            	    													lv_startY_18_0,
            	    													"org.eclipse.elk.graph.text.ElkGraph.Number");
            	    												afterParserOrEnumRuleCall();
            	    											

            	    }


            	    }


            	    }


            	    }

            	     
            	    							getUnorderedGroupHelper().returnFromSelection(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0());
            	    						

            	    }


            	    }


            	    }
            	    break;
            	case 4 :
            	    // InternalElkGraph.g:1550:5: ({...}? => ( ({...}? => (otherlv_19= 'end' otherlv_20= ':' ( (lv_endX_21_0= ruleNumber ) ) otherlv_22= ',' ( (lv_endY_23_0= ruleNumber ) ) ) ) ) )
            	    {
            	    // InternalElkGraph.g:1550:5: ({...}? => ( ({...}? => (otherlv_19= 'end' otherlv_20= ':' ( (lv_endX_21_0= ruleNumber ) ) otherlv_22= ',' ( (lv_endY_23_0= ruleNumber ) ) ) ) ) )
            	    // InternalElkGraph.g:1551:6: {...}? => ( ({...}? => (otherlv_19= 'end' otherlv_20= ':' ( (lv_endX_21_0= ruleNumber ) ) otherlv_22= ',' ( (lv_endY_23_0= ruleNumber ) ) ) ) )
            	    {
            	    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 3) ) {
            	        throw new FailedPredicateException(input, "ruleElkEdgeSection", "getUnorderedGroupHelper().canSelect(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 3)");
            	    }
            	    // InternalElkGraph.g:1551:114: ( ({...}? => (otherlv_19= 'end' otherlv_20= ':' ( (lv_endX_21_0= ruleNumber ) ) otherlv_22= ',' ( (lv_endY_23_0= ruleNumber ) ) ) ) )
            	    // InternalElkGraph.g:1552:7: ({...}? => (otherlv_19= 'end' otherlv_20= ':' ( (lv_endX_21_0= ruleNumber ) ) otherlv_22= ',' ( (lv_endY_23_0= ruleNumber ) ) ) )
            	    {

            	    							getUnorderedGroupHelper().select(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0(), 3);
            	    						
            	    // InternalElkGraph.g:1555:10: ({...}? => (otherlv_19= 'end' otherlv_20= ':' ( (lv_endX_21_0= ruleNumber ) ) otherlv_22= ',' ( (lv_endY_23_0= ruleNumber ) ) ) )
            	    // InternalElkGraph.g:1555:11: {...}? => (otherlv_19= 'end' otherlv_20= ':' ( (lv_endX_21_0= ruleNumber ) ) otherlv_22= ',' ( (lv_endY_23_0= ruleNumber ) ) )
            	    {
            	    if ( !((true)) ) {
            	        throw new FailedPredicateException(input, "ruleElkEdgeSection", "true");
            	    }
            	    // InternalElkGraph.g:1555:20: (otherlv_19= 'end' otherlv_20= ':' ( (lv_endX_21_0= ruleNumber ) ) otherlv_22= ',' ( (lv_endY_23_0= ruleNumber ) ) )
            	    // InternalElkGraph.g:1555:21: otherlv_19= 'end' otherlv_20= ':' ( (lv_endX_21_0= ruleNumber ) ) otherlv_22= ',' ( (lv_endY_23_0= ruleNumber ) )
            	    {
            	    otherlv_19=(Token)match(input,31,FOLLOW_11); 

            	    										newLeafNode(otherlv_19, grammarAccess.getElkEdgeSectionAccess().getEndKeyword_4_0_3_0());
            	    									
            	    otherlv_20=(Token)match(input,18,FOLLOW_18); 

            	    										newLeafNode(otherlv_20, grammarAccess.getElkEdgeSectionAccess().getColonKeyword_4_0_3_1());
            	    									
            	    // InternalElkGraph.g:1563:10: ( (lv_endX_21_0= ruleNumber ) )
            	    // InternalElkGraph.g:1564:11: (lv_endX_21_0= ruleNumber )
            	    {
            	    // InternalElkGraph.g:1564:11: (lv_endX_21_0= ruleNumber )
            	    // InternalElkGraph.g:1565:12: lv_endX_21_0= ruleNumber
            	    {

            	    												newCompositeNode(grammarAccess.getElkEdgeSectionAccess().getEndXNumberParserRuleCall_4_0_3_2_0());
            	    											
            	    pushFollow(FOLLOW_19);
            	    lv_endX_21_0=ruleNumber();

            	    state._fsp--;


            	    												if (current==null) {
            	    													current = createModelElementForParent(grammarAccess.getElkEdgeSectionRule());
            	    												}
            	    												set(
            	    													current,
            	    													"endX",
            	    													lv_endX_21_0,
            	    													"org.eclipse.elk.graph.text.ElkGraph.Number");
            	    												afterParserOrEnumRuleCall();
            	    											

            	    }


            	    }

            	    otherlv_22=(Token)match(input,23,FOLLOW_18); 

            	    										newLeafNode(otherlv_22, grammarAccess.getElkEdgeSectionAccess().getCommaKeyword_4_0_3_3());
            	    									
            	    // InternalElkGraph.g:1586:10: ( (lv_endY_23_0= ruleNumber ) )
            	    // InternalElkGraph.g:1587:11: (lv_endY_23_0= ruleNumber )
            	    {
            	    // InternalElkGraph.g:1587:11: (lv_endY_23_0= ruleNumber )
            	    // InternalElkGraph.g:1588:12: lv_endY_23_0= ruleNumber
            	    {

            	    												newCompositeNode(grammarAccess.getElkEdgeSectionAccess().getEndYNumberParserRuleCall_4_0_3_4_0());
            	    											
            	    pushFollow(FOLLOW_29);
            	    lv_endY_23_0=ruleNumber();

            	    state._fsp--;


            	    												if (current==null) {
            	    													current = createModelElementForParent(grammarAccess.getElkEdgeSectionRule());
            	    												}
            	    												set(
            	    													current,
            	    													"endY",
            	    													lv_endY_23_0,
            	    													"org.eclipse.elk.graph.text.ElkGraph.Number");
            	    												afterParserOrEnumRuleCall();
            	    											

            	    }


            	    }


            	    }


            	    }

            	     
            	    							getUnorderedGroupHelper().returnFromSelection(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0());
            	    						

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop33;
                }
            } while (true);


            }


            }

             
            					  getUnorderedGroupHelper().leave(grammarAccess.getElkEdgeSectionAccess().getUnorderedGroup_4_0());
            					

            }

            // InternalElkGraph.g:1618:4: (otherlv_24= 'bends' otherlv_25= ':' ( (lv_bendPoints_26_0= ruleElkBendPoint ) ) (otherlv_27= '|' ( (lv_bendPoints_28_0= ruleElkBendPoint ) ) )* )?
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==32) ) {
                alt35=1;
            }
            switch (alt35) {
                case 1 :
                    // InternalElkGraph.g:1619:5: otherlv_24= 'bends' otherlv_25= ':' ( (lv_bendPoints_26_0= ruleElkBendPoint ) ) (otherlv_27= '|' ( (lv_bendPoints_28_0= ruleElkBendPoint ) ) )*
                    {
                    otherlv_24=(Token)match(input,32,FOLLOW_11); 

                    					newLeafNode(otherlv_24, grammarAccess.getElkEdgeSectionAccess().getBendsKeyword_4_1_0());
                    				
                    otherlv_25=(Token)match(input,18,FOLLOW_18); 

                    					newLeafNode(otherlv_25, grammarAccess.getElkEdgeSectionAccess().getColonKeyword_4_1_1());
                    				
                    // InternalElkGraph.g:1627:5: ( (lv_bendPoints_26_0= ruleElkBendPoint ) )
                    // InternalElkGraph.g:1628:6: (lv_bendPoints_26_0= ruleElkBendPoint )
                    {
                    // InternalElkGraph.g:1628:6: (lv_bendPoints_26_0= ruleElkBendPoint )
                    // InternalElkGraph.g:1629:7: lv_bendPoints_26_0= ruleElkBendPoint
                    {

                    							newCompositeNode(grammarAccess.getElkEdgeSectionAccess().getBendPointsElkBendPointParserRuleCall_4_1_2_0());
                    						
                    pushFollow(FOLLOW_30);
                    lv_bendPoints_26_0=ruleElkBendPoint();

                    state._fsp--;


                    							if (current==null) {
                    								current = createModelElementForParent(grammarAccess.getElkEdgeSectionRule());
                    							}
                    							add(
                    								current,
                    								"bendPoints",
                    								lv_bendPoints_26_0,
                    								"org.eclipse.elk.graph.text.ElkGraph.ElkBendPoint");
                    							afterParserOrEnumRuleCall();
                    						

                    }


                    }

                    // InternalElkGraph.g:1646:5: (otherlv_27= '|' ( (lv_bendPoints_28_0= ruleElkBendPoint ) ) )*
                    loop34:
                    do {
                        int alt34=2;
                        int LA34_0 = input.LA(1);

                        if ( (LA34_0==33) ) {
                            alt34=1;
                        }


                        switch (alt34) {
                    	case 1 :
                    	    // InternalElkGraph.g:1647:6: otherlv_27= '|' ( (lv_bendPoints_28_0= ruleElkBendPoint ) )
                    	    {
                    	    otherlv_27=(Token)match(input,33,FOLLOW_18); 

                    	    						newLeafNode(otherlv_27, grammarAccess.getElkEdgeSectionAccess().getVerticalLineKeyword_4_1_3_0());
                    	    					
                    	    // InternalElkGraph.g:1651:6: ( (lv_bendPoints_28_0= ruleElkBendPoint ) )
                    	    // InternalElkGraph.g:1652:7: (lv_bendPoints_28_0= ruleElkBendPoint )
                    	    {
                    	    // InternalElkGraph.g:1652:7: (lv_bendPoints_28_0= ruleElkBendPoint )
                    	    // InternalElkGraph.g:1653:8: lv_bendPoints_28_0= ruleElkBendPoint
                    	    {

                    	    								newCompositeNode(grammarAccess.getElkEdgeSectionAccess().getBendPointsElkBendPointParserRuleCall_4_1_3_1_0());
                    	    							
                    	    pushFollow(FOLLOW_30);
                    	    lv_bendPoints_28_0=ruleElkBendPoint();

                    	    state._fsp--;


                    	    								if (current==null) {
                    	    									current = createModelElementForParent(grammarAccess.getElkEdgeSectionRule());
                    	    								}
                    	    								add(
                    	    									current,
                    	    									"bendPoints",
                    	    									lv_bendPoints_28_0,
                    	    									"org.eclipse.elk.graph.text.ElkGraph.ElkBendPoint");
                    	    								afterParserOrEnumRuleCall();
                    	    							

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop34;
                        }
                    } while (true);


                    }
                    break;

            }

            // InternalElkGraph.g:1672:4: ( (lv_properties_29_0= ruleProperty ) )*
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);

                if ( (LA36_0==RULE_ID) ) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // InternalElkGraph.g:1673:5: (lv_properties_29_0= ruleProperty )
            	    {
            	    // InternalElkGraph.g:1673:5: (lv_properties_29_0= ruleProperty )
            	    // InternalElkGraph.g:1674:6: lv_properties_29_0= ruleProperty
            	    {

            	    						newCompositeNode(grammarAccess.getElkEdgeSectionAccess().getPropertiesPropertyParserRuleCall_4_2_0());
            	    					
            	    pushFollow(FOLLOW_31);
            	    lv_properties_29_0=ruleProperty();

            	    state._fsp--;


            	    						if (current==null) {
            	    							current = createModelElementForParent(grammarAccess.getElkEdgeSectionRule());
            	    						}
            	    						add(
            	    							current,
            	    							"properties",
            	    							lv_properties_29_0,
            	    							"org.eclipse.elk.graph.text.ElkGraph.Property");
            	    						afterParserOrEnumRuleCall();
            	    					

            	    }


            	    }
            	    break;

            	default :
            	    break loop36;
                }
            } while (true);


            }

            otherlv_30=(Token)match(input,25,FOLLOW_2); 

            			newLeafNode(otherlv_30, grammarAccess.getElkEdgeSectionAccess().getRightSquareBracketKeyword_5());
            		

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleElkEdgeSection"


    // $ANTLR start "entryRuleElkBendPoint"
    // InternalElkGraph.g:1700:1: entryRuleElkBendPoint returns [EObject current=null] : iv_ruleElkBendPoint= ruleElkBendPoint EOF ;
    public final EObject entryRuleElkBendPoint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleElkBendPoint = null;


        try {
            // InternalElkGraph.g:1700:53: (iv_ruleElkBendPoint= ruleElkBendPoint EOF )
            // InternalElkGraph.g:1701:2: iv_ruleElkBendPoint= ruleElkBendPoint EOF
            {
             newCompositeNode(grammarAccess.getElkBendPointRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleElkBendPoint=ruleElkBendPoint();

            state._fsp--;

             current =iv_ruleElkBendPoint; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleElkBendPoint"


    // $ANTLR start "ruleElkBendPoint"
    // InternalElkGraph.g:1707:1: ruleElkBendPoint returns [EObject current=null] : ( ( (lv_x_0_0= ruleNumber ) ) otherlv_1= ',' ( (lv_y_2_0= ruleNumber ) ) ) ;
    public final EObject ruleElkBendPoint() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        AntlrDatatypeRuleToken lv_x_0_0 = null;

        AntlrDatatypeRuleToken lv_y_2_0 = null;



        	enterRule();

        try {
            // InternalElkGraph.g:1713:2: ( ( ( (lv_x_0_0= ruleNumber ) ) otherlv_1= ',' ( (lv_y_2_0= ruleNumber ) ) ) )
            // InternalElkGraph.g:1714:2: ( ( (lv_x_0_0= ruleNumber ) ) otherlv_1= ',' ( (lv_y_2_0= ruleNumber ) ) )
            {
            // InternalElkGraph.g:1714:2: ( ( (lv_x_0_0= ruleNumber ) ) otherlv_1= ',' ( (lv_y_2_0= ruleNumber ) ) )
            // InternalElkGraph.g:1715:3: ( (lv_x_0_0= ruleNumber ) ) otherlv_1= ',' ( (lv_y_2_0= ruleNumber ) )
            {
            // InternalElkGraph.g:1715:3: ( (lv_x_0_0= ruleNumber ) )
            // InternalElkGraph.g:1716:4: (lv_x_0_0= ruleNumber )
            {
            // InternalElkGraph.g:1716:4: (lv_x_0_0= ruleNumber )
            // InternalElkGraph.g:1717:5: lv_x_0_0= ruleNumber
            {

            					newCompositeNode(grammarAccess.getElkBendPointAccess().getXNumberParserRuleCall_0_0());
            				
            pushFollow(FOLLOW_19);
            lv_x_0_0=ruleNumber();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getElkBendPointRule());
            					}
            					set(
            						current,
            						"x",
            						lv_x_0_0,
            						"org.eclipse.elk.graph.text.ElkGraph.Number");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            otherlv_1=(Token)match(input,23,FOLLOW_18); 

            			newLeafNode(otherlv_1, grammarAccess.getElkBendPointAccess().getCommaKeyword_1());
            		
            // InternalElkGraph.g:1738:3: ( (lv_y_2_0= ruleNumber ) )
            // InternalElkGraph.g:1739:4: (lv_y_2_0= ruleNumber )
            {
            // InternalElkGraph.g:1739:4: (lv_y_2_0= ruleNumber )
            // InternalElkGraph.g:1740:5: lv_y_2_0= ruleNumber
            {

            					newCompositeNode(grammarAccess.getElkBendPointAccess().getYNumberParserRuleCall_2_0());
            				
            pushFollow(FOLLOW_2);
            lv_y_2_0=ruleNumber();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getElkBendPointRule());
            					}
            					set(
            						current,
            						"y",
            						lv_y_2_0,
            						"org.eclipse.elk.graph.text.ElkGraph.Number");
            					afterParserOrEnumRuleCall();
            				

            }


            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleElkBendPoint"


    // $ANTLR start "entryRuleQualifiedId"
    // InternalElkGraph.g:1761:1: entryRuleQualifiedId returns [String current=null] : iv_ruleQualifiedId= ruleQualifiedId EOF ;
    public final String entryRuleQualifiedId() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleQualifiedId = null;


        try {
            // InternalElkGraph.g:1761:51: (iv_ruleQualifiedId= ruleQualifiedId EOF )
            // InternalElkGraph.g:1762:2: iv_ruleQualifiedId= ruleQualifiedId EOF
            {
             newCompositeNode(grammarAccess.getQualifiedIdRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleQualifiedId=ruleQualifiedId();

            state._fsp--;

             current =iv_ruleQualifiedId.getText(); 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleQualifiedId"


    // $ANTLR start "ruleQualifiedId"
    // InternalElkGraph.g:1768:1: ruleQualifiedId returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* ) ;
    public final AntlrDatatypeRuleToken ruleQualifiedId() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_ID_0=null;
        Token kw=null;
        Token this_ID_2=null;


        	enterRule();

        try {
            // InternalElkGraph.g:1774:2: ( (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* ) )
            // InternalElkGraph.g:1775:2: (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* )
            {
            // InternalElkGraph.g:1775:2: (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* )
            // InternalElkGraph.g:1776:3: this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )*
            {
            this_ID_0=(Token)match(input,RULE_ID,FOLLOW_32); 

            			current.merge(this_ID_0);
            		

            			newLeafNode(this_ID_0, grammarAccess.getQualifiedIdAccess().getIDTerminalRuleCall_0());
            		
            // InternalElkGraph.g:1783:3: (kw= '.' this_ID_2= RULE_ID )*
            loop37:
            do {
                int alt37=2;
                int LA37_0 = input.LA(1);

                if ( (LA37_0==35) ) {
                    alt37=1;
                }


                switch (alt37) {
            	case 1 :
            	    // InternalElkGraph.g:1784:4: kw= '.' this_ID_2= RULE_ID
            	    {
            	    kw=(Token)match(input,35,FOLLOW_3); 

            	    				current.merge(kw);
            	    				newLeafNode(kw, grammarAccess.getQualifiedIdAccess().getFullStopKeyword_1_0());
            	    			
            	    this_ID_2=(Token)match(input,RULE_ID,FOLLOW_32); 

            	    				current.merge(this_ID_2);
            	    			

            	    				newLeafNode(this_ID_2, grammarAccess.getQualifiedIdAccess().getIDTerminalRuleCall_1_1());
            	    			

            	    }
            	    break;

            	default :
            	    break loop37;
                }
            } while (true);


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleQualifiedId"


    // $ANTLR start "entryRuleNumber"
    // InternalElkGraph.g:1801:1: entryRuleNumber returns [String current=null] : iv_ruleNumber= ruleNumber EOF ;
    public final String entryRuleNumber() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleNumber = null;


        try {
            // InternalElkGraph.g:1801:46: (iv_ruleNumber= ruleNumber EOF )
            // InternalElkGraph.g:1802:2: iv_ruleNumber= ruleNumber EOF
            {
             newCompositeNode(grammarAccess.getNumberRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleNumber=ruleNumber();

            state._fsp--;

             current =iv_ruleNumber.getText(); 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleNumber"


    // $ANTLR start "ruleNumber"
    // InternalElkGraph.g:1808:1: ruleNumber returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_SIGNED_INT_0= RULE_SIGNED_INT | this_FLOAT_1= RULE_FLOAT ) ;
    public final AntlrDatatypeRuleToken ruleNumber() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_SIGNED_INT_0=null;
        Token this_FLOAT_1=null;


        	enterRule();

        try {
            // InternalElkGraph.g:1814:2: ( (this_SIGNED_INT_0= RULE_SIGNED_INT | this_FLOAT_1= RULE_FLOAT ) )
            // InternalElkGraph.g:1815:2: (this_SIGNED_INT_0= RULE_SIGNED_INT | this_FLOAT_1= RULE_FLOAT )
            {
            // InternalElkGraph.g:1815:2: (this_SIGNED_INT_0= RULE_SIGNED_INT | this_FLOAT_1= RULE_FLOAT )
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==RULE_SIGNED_INT) ) {
                alt38=1;
            }
            else if ( (LA38_0==RULE_FLOAT) ) {
                alt38=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 38, 0, input);

                throw nvae;
            }
            switch (alt38) {
                case 1 :
                    // InternalElkGraph.g:1816:3: this_SIGNED_INT_0= RULE_SIGNED_INT
                    {
                    this_SIGNED_INT_0=(Token)match(input,RULE_SIGNED_INT,FOLLOW_2); 

                    			current.merge(this_SIGNED_INT_0);
                    		

                    			newLeafNode(this_SIGNED_INT_0, grammarAccess.getNumberAccess().getSIGNED_INTTerminalRuleCall_0());
                    		

                    }
                    break;
                case 2 :
                    // InternalElkGraph.g:1824:3: this_FLOAT_1= RULE_FLOAT
                    {
                    this_FLOAT_1=(Token)match(input,RULE_FLOAT,FOLLOW_2); 

                    			current.merge(this_FLOAT_1);
                    		

                    			newLeafNode(this_FLOAT_1, grammarAccess.getNumberAccess().getFLOATTerminalRuleCall_1());
                    		

                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleNumber"


    // $ANTLR start "entryRuleProperty"
    // InternalElkGraph.g:1835:1: entryRuleProperty returns [EObject current=null] : iv_ruleProperty= ruleProperty EOF ;
    public final EObject entryRuleProperty() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleProperty = null;


        try {
            // InternalElkGraph.g:1835:49: (iv_ruleProperty= ruleProperty EOF )
            // InternalElkGraph.g:1836:2: iv_ruleProperty= ruleProperty EOF
            {
             newCompositeNode(grammarAccess.getPropertyRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleProperty=ruleProperty();

            state._fsp--;

             current =iv_ruleProperty; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleProperty"


    // $ANTLR start "ruleProperty"
    // InternalElkGraph.g:1842:1: ruleProperty returns [EObject current=null] : ( ( (lv_key_0_0= rulePropertyKey ) ) otherlv_1= ':' ( ( (lv_value_2_0= ruleStringValue ) ) | ( (lv_value_3_0= ruleQualifiedIdValue ) ) | ( (lv_value_4_0= ruleNumberValue ) ) | ( (lv_value_5_0= ruleBooleanValue ) ) ) ) ;
    public final EObject ruleProperty() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        AntlrDatatypeRuleToken lv_key_0_0 = null;

        AntlrDatatypeRuleToken lv_value_2_0 = null;

        AntlrDatatypeRuleToken lv_value_3_0 = null;

        AntlrDatatypeRuleToken lv_value_4_0 = null;

        AntlrDatatypeRuleToken lv_value_5_0 = null;



        	enterRule();

        try {
            // InternalElkGraph.g:1848:2: ( ( ( (lv_key_0_0= rulePropertyKey ) ) otherlv_1= ':' ( ( (lv_value_2_0= ruleStringValue ) ) | ( (lv_value_3_0= ruleQualifiedIdValue ) ) | ( (lv_value_4_0= ruleNumberValue ) ) | ( (lv_value_5_0= ruleBooleanValue ) ) ) ) )
            // InternalElkGraph.g:1849:2: ( ( (lv_key_0_0= rulePropertyKey ) ) otherlv_1= ':' ( ( (lv_value_2_0= ruleStringValue ) ) | ( (lv_value_3_0= ruleQualifiedIdValue ) ) | ( (lv_value_4_0= ruleNumberValue ) ) | ( (lv_value_5_0= ruleBooleanValue ) ) ) )
            {
            // InternalElkGraph.g:1849:2: ( ( (lv_key_0_0= rulePropertyKey ) ) otherlv_1= ':' ( ( (lv_value_2_0= ruleStringValue ) ) | ( (lv_value_3_0= ruleQualifiedIdValue ) ) | ( (lv_value_4_0= ruleNumberValue ) ) | ( (lv_value_5_0= ruleBooleanValue ) ) ) )
            // InternalElkGraph.g:1850:3: ( (lv_key_0_0= rulePropertyKey ) ) otherlv_1= ':' ( ( (lv_value_2_0= ruleStringValue ) ) | ( (lv_value_3_0= ruleQualifiedIdValue ) ) | ( (lv_value_4_0= ruleNumberValue ) ) | ( (lv_value_5_0= ruleBooleanValue ) ) )
            {
            // InternalElkGraph.g:1850:3: ( (lv_key_0_0= rulePropertyKey ) )
            // InternalElkGraph.g:1851:4: (lv_key_0_0= rulePropertyKey )
            {
            // InternalElkGraph.g:1851:4: (lv_key_0_0= rulePropertyKey )
            // InternalElkGraph.g:1852:5: lv_key_0_0= rulePropertyKey
            {

            					newCompositeNode(grammarAccess.getPropertyAccess().getKeyPropertyKeyParserRuleCall_0_0());
            				
            pushFollow(FOLLOW_11);
            lv_key_0_0=rulePropertyKey();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getPropertyRule());
            					}
            					set(
            						current,
            						"key",
            						lv_key_0_0,
            						"org.eclipse.elk.graph.text.ElkGraph.PropertyKey");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            otherlv_1=(Token)match(input,18,FOLLOW_33); 

            			newLeafNode(otherlv_1, grammarAccess.getPropertyAccess().getColonKeyword_1());
            		
            // InternalElkGraph.g:1873:3: ( ( (lv_value_2_0= ruleStringValue ) ) | ( (lv_value_3_0= ruleQualifiedIdValue ) ) | ( (lv_value_4_0= ruleNumberValue ) ) | ( (lv_value_5_0= ruleBooleanValue ) ) )
            int alt39=4;
            switch ( input.LA(1) ) {
            case RULE_STRING:
                {
                alt39=1;
                }
                break;
            case RULE_ID:
                {
                alt39=2;
                }
                break;
            case RULE_SIGNED_INT:
            case RULE_FLOAT:
                {
                alt39=3;
                }
                break;
            case 36:
            case 37:
                {
                alt39=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 39, 0, input);

                throw nvae;
            }

            switch (alt39) {
                case 1 :
                    // InternalElkGraph.g:1874:4: ( (lv_value_2_0= ruleStringValue ) )
                    {
                    // InternalElkGraph.g:1874:4: ( (lv_value_2_0= ruleStringValue ) )
                    // InternalElkGraph.g:1875:5: (lv_value_2_0= ruleStringValue )
                    {
                    // InternalElkGraph.g:1875:5: (lv_value_2_0= ruleStringValue )
                    // InternalElkGraph.g:1876:6: lv_value_2_0= ruleStringValue
                    {

                    						newCompositeNode(grammarAccess.getPropertyAccess().getValueStringValueParserRuleCall_2_0_0());
                    					
                    pushFollow(FOLLOW_2);
                    lv_value_2_0=ruleStringValue();

                    state._fsp--;


                    						if (current==null) {
                    							current = createModelElementForParent(grammarAccess.getPropertyRule());
                    						}
                    						set(
                    							current,
                    							"value",
                    							lv_value_2_0,
                    							"org.eclipse.elk.graph.text.ElkGraph.StringValue");
                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalElkGraph.g:1894:4: ( (lv_value_3_0= ruleQualifiedIdValue ) )
                    {
                    // InternalElkGraph.g:1894:4: ( (lv_value_3_0= ruleQualifiedIdValue ) )
                    // InternalElkGraph.g:1895:5: (lv_value_3_0= ruleQualifiedIdValue )
                    {
                    // InternalElkGraph.g:1895:5: (lv_value_3_0= ruleQualifiedIdValue )
                    // InternalElkGraph.g:1896:6: lv_value_3_0= ruleQualifiedIdValue
                    {

                    						newCompositeNode(grammarAccess.getPropertyAccess().getValueQualifiedIdValueParserRuleCall_2_1_0());
                    					
                    pushFollow(FOLLOW_2);
                    lv_value_3_0=ruleQualifiedIdValue();

                    state._fsp--;


                    						if (current==null) {
                    							current = createModelElementForParent(grammarAccess.getPropertyRule());
                    						}
                    						set(
                    							current,
                    							"value",
                    							lv_value_3_0,
                    							"org.eclipse.elk.graph.text.ElkGraph.QualifiedIdValue");
                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }


                    }
                    break;
                case 3 :
                    // InternalElkGraph.g:1914:4: ( (lv_value_4_0= ruleNumberValue ) )
                    {
                    // InternalElkGraph.g:1914:4: ( (lv_value_4_0= ruleNumberValue ) )
                    // InternalElkGraph.g:1915:5: (lv_value_4_0= ruleNumberValue )
                    {
                    // InternalElkGraph.g:1915:5: (lv_value_4_0= ruleNumberValue )
                    // InternalElkGraph.g:1916:6: lv_value_4_0= ruleNumberValue
                    {

                    						newCompositeNode(grammarAccess.getPropertyAccess().getValueNumberValueParserRuleCall_2_2_0());
                    					
                    pushFollow(FOLLOW_2);
                    lv_value_4_0=ruleNumberValue();

                    state._fsp--;


                    						if (current==null) {
                    							current = createModelElementForParent(grammarAccess.getPropertyRule());
                    						}
                    						set(
                    							current,
                    							"value",
                    							lv_value_4_0,
                    							"org.eclipse.elk.graph.text.ElkGraph.NumberValue");
                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }


                    }
                    break;
                case 4 :
                    // InternalElkGraph.g:1934:4: ( (lv_value_5_0= ruleBooleanValue ) )
                    {
                    // InternalElkGraph.g:1934:4: ( (lv_value_5_0= ruleBooleanValue ) )
                    // InternalElkGraph.g:1935:5: (lv_value_5_0= ruleBooleanValue )
                    {
                    // InternalElkGraph.g:1935:5: (lv_value_5_0= ruleBooleanValue )
                    // InternalElkGraph.g:1936:6: lv_value_5_0= ruleBooleanValue
                    {

                    						newCompositeNode(grammarAccess.getPropertyAccess().getValueBooleanValueParserRuleCall_2_3_0());
                    					
                    pushFollow(FOLLOW_2);
                    lv_value_5_0=ruleBooleanValue();

                    state._fsp--;


                    						if (current==null) {
                    							current = createModelElementForParent(grammarAccess.getPropertyRule());
                    						}
                    						set(
                    							current,
                    							"value",
                    							lv_value_5_0,
                    							"org.eclipse.elk.graph.text.ElkGraph.BooleanValue");
                    						afterParserOrEnumRuleCall();
                    					

                    }


                    }


                    }
                    break;

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleProperty"


    // $ANTLR start "entryRulePropertyKey"
    // InternalElkGraph.g:1958:1: entryRulePropertyKey returns [String current=null] : iv_rulePropertyKey= rulePropertyKey EOF ;
    public final String entryRulePropertyKey() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_rulePropertyKey = null;



        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalElkGraph.g:1960:2: (iv_rulePropertyKey= rulePropertyKey EOF )
            // InternalElkGraph.g:1961:2: iv_rulePropertyKey= rulePropertyKey EOF
            {
             newCompositeNode(grammarAccess.getPropertyKeyRule()); 
            pushFollow(FOLLOW_1);
            iv_rulePropertyKey=rulePropertyKey();

            state._fsp--;

             current =iv_rulePropertyKey.getText(); 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {

            	myHiddenTokenState.restore();

        }
        return current;
    }
    // $ANTLR end "entryRulePropertyKey"


    // $ANTLR start "rulePropertyKey"
    // InternalElkGraph.g:1970:1: rulePropertyKey returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* ) ;
    public final AntlrDatatypeRuleToken rulePropertyKey() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_ID_0=null;
        Token kw=null;
        Token this_ID_2=null;


        	enterRule();
        	HiddenTokens myHiddenTokenState = ((XtextTokenStream)input).setHiddenTokens();

        try {
            // InternalElkGraph.g:1977:2: ( (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* ) )
            // InternalElkGraph.g:1978:2: (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* )
            {
            // InternalElkGraph.g:1978:2: (this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )* )
            // InternalElkGraph.g:1979:3: this_ID_0= RULE_ID (kw= '.' this_ID_2= RULE_ID )*
            {
            this_ID_0=(Token)match(input,RULE_ID,FOLLOW_32); 

            			current.merge(this_ID_0);
            		

            			newLeafNode(this_ID_0, grammarAccess.getPropertyKeyAccess().getIDTerminalRuleCall_0());
            		
            // InternalElkGraph.g:1986:3: (kw= '.' this_ID_2= RULE_ID )*
            loop40:
            do {
                int alt40=2;
                int LA40_0 = input.LA(1);

                if ( (LA40_0==35) ) {
                    alt40=1;
                }


                switch (alt40) {
            	case 1 :
            	    // InternalElkGraph.g:1987:4: kw= '.' this_ID_2= RULE_ID
            	    {
            	    kw=(Token)match(input,35,FOLLOW_3); 

            	    				current.merge(kw);
            	    				newLeafNode(kw, grammarAccess.getPropertyKeyAccess().getFullStopKeyword_1_0());
            	    			
            	    this_ID_2=(Token)match(input,RULE_ID,FOLLOW_32); 

            	    				current.merge(this_ID_2);
            	    			

            	    				newLeafNode(this_ID_2, grammarAccess.getPropertyKeyAccess().getIDTerminalRuleCall_1_1());
            	    			

            	    }
            	    break;

            	default :
            	    break loop40;
                }
            } while (true);


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {

            	myHiddenTokenState.restore();

        }
        return current;
    }
    // $ANTLR end "rulePropertyKey"


    // $ANTLR start "entryRuleStringValue"
    // InternalElkGraph.g:2007:1: entryRuleStringValue returns [String current=null] : iv_ruleStringValue= ruleStringValue EOF ;
    public final String entryRuleStringValue() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleStringValue = null;


        try {
            // InternalElkGraph.g:2007:51: (iv_ruleStringValue= ruleStringValue EOF )
            // InternalElkGraph.g:2008:2: iv_ruleStringValue= ruleStringValue EOF
            {
             newCompositeNode(grammarAccess.getStringValueRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleStringValue=ruleStringValue();

            state._fsp--;

             current =iv_ruleStringValue.getText(); 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleStringValue"


    // $ANTLR start "ruleStringValue"
    // InternalElkGraph.g:2014:1: ruleStringValue returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_STRING_0= RULE_STRING ;
    public final AntlrDatatypeRuleToken ruleStringValue() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_STRING_0=null;


        	enterRule();

        try {
            // InternalElkGraph.g:2020:2: (this_STRING_0= RULE_STRING )
            // InternalElkGraph.g:2021:2: this_STRING_0= RULE_STRING
            {
            this_STRING_0=(Token)match(input,RULE_STRING,FOLLOW_2); 

            		current.merge(this_STRING_0);
            	

            		newLeafNode(this_STRING_0, grammarAccess.getStringValueAccess().getSTRINGTerminalRuleCall());
            	

            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleStringValue"


    // $ANTLR start "entryRuleQualifiedIdValue"
    // InternalElkGraph.g:2031:1: entryRuleQualifiedIdValue returns [String current=null] : iv_ruleQualifiedIdValue= ruleQualifiedIdValue EOF ;
    public final String entryRuleQualifiedIdValue() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleQualifiedIdValue = null;


        try {
            // InternalElkGraph.g:2031:56: (iv_ruleQualifiedIdValue= ruleQualifiedIdValue EOF )
            // InternalElkGraph.g:2032:2: iv_ruleQualifiedIdValue= ruleQualifiedIdValue EOF
            {
             newCompositeNode(grammarAccess.getQualifiedIdValueRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleQualifiedIdValue=ruleQualifiedIdValue();

            state._fsp--;

             current =iv_ruleQualifiedIdValue.getText(); 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleQualifiedIdValue"


    // $ANTLR start "ruleQualifiedIdValue"
    // InternalElkGraph.g:2038:1: ruleQualifiedIdValue returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : this_QualifiedId_0= ruleQualifiedId ;
    public final AntlrDatatypeRuleToken ruleQualifiedIdValue() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        AntlrDatatypeRuleToken this_QualifiedId_0 = null;



        	enterRule();

        try {
            // InternalElkGraph.g:2044:2: (this_QualifiedId_0= ruleQualifiedId )
            // InternalElkGraph.g:2045:2: this_QualifiedId_0= ruleQualifiedId
            {

            		newCompositeNode(grammarAccess.getQualifiedIdValueAccess().getQualifiedIdParserRuleCall());
            	
            pushFollow(FOLLOW_2);
            this_QualifiedId_0=ruleQualifiedId();

            state._fsp--;


            		current.merge(this_QualifiedId_0);
            	

            		afterParserOrEnumRuleCall();
            	

            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleQualifiedIdValue"


    // $ANTLR start "entryRuleNumberValue"
    // InternalElkGraph.g:2058:1: entryRuleNumberValue returns [String current=null] : iv_ruleNumberValue= ruleNumberValue EOF ;
    public final String entryRuleNumberValue() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleNumberValue = null;


        try {
            // InternalElkGraph.g:2058:51: (iv_ruleNumberValue= ruleNumberValue EOF )
            // InternalElkGraph.g:2059:2: iv_ruleNumberValue= ruleNumberValue EOF
            {
             newCompositeNode(grammarAccess.getNumberValueRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleNumberValue=ruleNumberValue();

            state._fsp--;

             current =iv_ruleNumberValue.getText(); 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleNumberValue"


    // $ANTLR start "ruleNumberValue"
    // InternalElkGraph.g:2065:1: ruleNumberValue returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_SIGNED_INT_0= RULE_SIGNED_INT | this_FLOAT_1= RULE_FLOAT ) ;
    public final AntlrDatatypeRuleToken ruleNumberValue() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_SIGNED_INT_0=null;
        Token this_FLOAT_1=null;


        	enterRule();

        try {
            // InternalElkGraph.g:2071:2: ( (this_SIGNED_INT_0= RULE_SIGNED_INT | this_FLOAT_1= RULE_FLOAT ) )
            // InternalElkGraph.g:2072:2: (this_SIGNED_INT_0= RULE_SIGNED_INT | this_FLOAT_1= RULE_FLOAT )
            {
            // InternalElkGraph.g:2072:2: (this_SIGNED_INT_0= RULE_SIGNED_INT | this_FLOAT_1= RULE_FLOAT )
            int alt41=2;
            int LA41_0 = input.LA(1);

            if ( (LA41_0==RULE_SIGNED_INT) ) {
                alt41=1;
            }
            else if ( (LA41_0==RULE_FLOAT) ) {
                alt41=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 41, 0, input);

                throw nvae;
            }
            switch (alt41) {
                case 1 :
                    // InternalElkGraph.g:2073:3: this_SIGNED_INT_0= RULE_SIGNED_INT
                    {
                    this_SIGNED_INT_0=(Token)match(input,RULE_SIGNED_INT,FOLLOW_2); 

                    			current.merge(this_SIGNED_INT_0);
                    		

                    			newLeafNode(this_SIGNED_INT_0, grammarAccess.getNumberValueAccess().getSIGNED_INTTerminalRuleCall_0());
                    		

                    }
                    break;
                case 2 :
                    // InternalElkGraph.g:2081:3: this_FLOAT_1= RULE_FLOAT
                    {
                    this_FLOAT_1=(Token)match(input,RULE_FLOAT,FOLLOW_2); 

                    			current.merge(this_FLOAT_1);
                    		

                    			newLeafNode(this_FLOAT_1, grammarAccess.getNumberValueAccess().getFLOATTerminalRuleCall_1());
                    		

                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleNumberValue"


    // $ANTLR start "entryRuleBooleanValue"
    // InternalElkGraph.g:2092:1: entryRuleBooleanValue returns [String current=null] : iv_ruleBooleanValue= ruleBooleanValue EOF ;
    public final String entryRuleBooleanValue() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleBooleanValue = null;


        try {
            // InternalElkGraph.g:2092:52: (iv_ruleBooleanValue= ruleBooleanValue EOF )
            // InternalElkGraph.g:2093:2: iv_ruleBooleanValue= ruleBooleanValue EOF
            {
             newCompositeNode(grammarAccess.getBooleanValueRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleBooleanValue=ruleBooleanValue();

            state._fsp--;

             current =iv_ruleBooleanValue.getText(); 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleBooleanValue"


    // $ANTLR start "ruleBooleanValue"
    // InternalElkGraph.g:2099:1: ruleBooleanValue returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'true' | kw= 'false' ) ;
    public final AntlrDatatypeRuleToken ruleBooleanValue() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;


        	enterRule();

        try {
            // InternalElkGraph.g:2105:2: ( (kw= 'true' | kw= 'false' ) )
            // InternalElkGraph.g:2106:2: (kw= 'true' | kw= 'false' )
            {
            // InternalElkGraph.g:2106:2: (kw= 'true' | kw= 'false' )
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( (LA42_0==36) ) {
                alt42=1;
            }
            else if ( (LA42_0==37) ) {
                alt42=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 42, 0, input);

                throw nvae;
            }
            switch (alt42) {
                case 1 :
                    // InternalElkGraph.g:2107:3: kw= 'true'
                    {
                    kw=(Token)match(input,36,FOLLOW_2); 

                    			current.merge(kw);
                    			newLeafNode(kw, grammarAccess.getBooleanValueAccess().getTrueKeyword_0());
                    		

                    }
                    break;
                case 2 :
                    // InternalElkGraph.g:2113:3: kw= 'false'
                    {
                    kw=(Token)match(input,37,FOLLOW_2); 

                    			current.merge(kw);
                    			newLeafNode(kw, grammarAccess.getBooleanValueAccess().getFalseKeyword_1());
                    		

                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleBooleanValue"

    // Delegated rules


 

    public static final BitSet FOLLOW_1 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_2 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_3 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_4 = new BitSet(new long[]{0x00000000040A4012L});
    public static final BitSet FOLLOW_5 = new BitSet(new long[]{0x00000000040A4002L});
    public static final BitSet FOLLOW_6 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_7 = new BitSet(new long[]{0x00000000041B4010L});
    public static final BitSet FOLLOW_8 = new BitSet(new long[]{0x00000000040B4010L});
    public static final BitSet FOLLOW_9 = new BitSet(new long[]{0x00000000040B4000L});
    public static final BitSet FOLLOW_10 = new BitSet(new long[]{0x0000000000000030L});
    public static final BitSet FOLLOW_11 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_12 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_13 = new BitSet(new long[]{0x0000000000130010L});
    public static final BitSet FOLLOW_14 = new BitSet(new long[]{0x0000000000030010L});
    public static final BitSet FOLLOW_15 = new BitSet(new long[]{0x0000000000030000L});
    public static final BitSet FOLLOW_16 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_17 = new BitSet(new long[]{0x0000000003400000L});
    public static final BitSet FOLLOW_18 = new BitSet(new long[]{0x00000000000000C0L});
    public static final BitSet FOLLOW_19 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_20 = new BitSet(new long[]{0x0000000008800000L});
    public static final BitSet FOLLOW_21 = new BitSet(new long[]{0x0000000000808002L});
    public static final BitSet FOLLOW_22 = new BitSet(new long[]{0x00000005F2000010L});
    public static final BitSet FOLLOW_23 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_24 = new BitSet(new long[]{0x00000001F0000012L});
    public static final BitSet FOLLOW_25 = new BitSet(new long[]{0x0000000200000012L});
    public static final BitSet FOLLOW_26 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_27 = new BitSet(new long[]{0x0000000008200000L});
    public static final BitSet FOLLOW_28 = new BitSet(new long[]{0x0000000000A00000L});
    public static final BitSet FOLLOW_29 = new BitSet(new long[]{0x00000001F2000010L});
    public static final BitSet FOLLOW_30 = new BitSet(new long[]{0x0000000202000010L});
    public static final BitSet FOLLOW_31 = new BitSet(new long[]{0x0000000002000010L});
    public static final BitSet FOLLOW_32 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_33 = new BitSet(new long[]{0x00000030000000F0L});

}
