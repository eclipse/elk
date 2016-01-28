package org.eclipse.elk.alg.graphviz.dot.parser.antlr.internal; 

import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.xtext.parser.antlr.AbstractInternalAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.parser.antlr.AntlrDatatypeRuleToken;
import org.eclipse.elk.alg.graphviz.dot.services.GraphvizDotGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class InternalGraphvizDotParser extends AbstractInternalAntlrParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_ID", "RULE_INT", "RULE_FLOAT", "RULE_STRING", "RULE_PREC_LINE", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'strict'", "'{'", "'}'", "';'", "'='", "'['", "','", "']'", "':'", "'subgraph'", "'->'", "'--'", "'graph'", "'digraph'", "'node'", "'edge'"
    };
    public static final int RULE_PREC_LINE=8;
    public static final int RULE_STRING=7;
    public static final int RULE_SL_COMMENT=10;
    public static final int T__19=19;
    public static final int T__15=15;
    public static final int T__16=16;
    public static final int T__17=17;
    public static final int T__18=18;
    public static final int T__13=13;
    public static final int T__14=14;
    public static final int EOF=-1;
    public static final int RULE_ID=4;
    public static final int RULE_WS=11;
    public static final int RULE_ANY_OTHER=12;
    public static final int T__26=26;
    public static final int T__27=27;
    public static final int T__28=28;
    public static final int RULE_INT=5;
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


        public InternalGraphvizDotParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public InternalGraphvizDotParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return InternalGraphvizDotParser.tokenNames; }
    public String getGrammarFileName() { return "../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g"; }



     	private GraphvizDotGrammarAccess grammarAccess;
     	
        public InternalGraphvizDotParser(TokenStream input, GraphvizDotGrammarAccess grammarAccess) {
            this(input);
            this.grammarAccess = grammarAccess;
            registerRules(grammarAccess.getGrammar());
        }
        
        @Override
        protected String getFirstRuleName() {
        	return "GraphvizModel";	
       	}
       	
       	@Override
       	protected GraphvizDotGrammarAccess getGrammarAccess() {
       		return grammarAccess;
       	}



    // $ANTLR start "entryRuleGraphvizModel"
    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:68:1: entryRuleGraphvizModel returns [EObject current=null] : iv_ruleGraphvizModel= ruleGraphvizModel EOF ;
    public final EObject entryRuleGraphvizModel() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleGraphvizModel = null;


        try {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:69:2: (iv_ruleGraphvizModel= ruleGraphvizModel EOF )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:70:2: iv_ruleGraphvizModel= ruleGraphvizModel EOF
            {
             newCompositeNode(grammarAccess.getGraphvizModelRule()); 
            pushFollow(FOLLOW_ruleGraphvizModel_in_entryRuleGraphvizModel75);
            iv_ruleGraphvizModel=ruleGraphvizModel();

            state._fsp--;

             current =iv_ruleGraphvizModel; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleGraphvizModel85); 

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
    // $ANTLR end "entryRuleGraphvizModel"


    // $ANTLR start "ruleGraphvizModel"
    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:77:1: ruleGraphvizModel returns [EObject current=null] : ( (lv_graphs_0_0= ruleGraph ) )* ;
    public final EObject ruleGraphvizModel() throws RecognitionException {
        EObject current = null;

        EObject lv_graphs_0_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:80:28: ( ( (lv_graphs_0_0= ruleGraph ) )* )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:81:1: ( (lv_graphs_0_0= ruleGraph ) )*
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:81:1: ( (lv_graphs_0_0= ruleGraph ) )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==13||(LA1_0>=25 && LA1_0<=26)) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:82:1: (lv_graphs_0_0= ruleGraph )
            	    {
            	    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:82:1: (lv_graphs_0_0= ruleGraph )
            	    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:83:3: lv_graphs_0_0= ruleGraph
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getGraphvizModelAccess().getGraphsGraphParserRuleCall_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleGraph_in_ruleGraphvizModel130);
            	    lv_graphs_0_0=ruleGraph();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getGraphvizModelRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"graphs",
            	            		lv_graphs_0_0, 
            	            		"Graph");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


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
    // $ANTLR end "ruleGraphvizModel"


    // $ANTLR start "entryRuleGraph"
    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:107:1: entryRuleGraph returns [EObject current=null] : iv_ruleGraph= ruleGraph EOF ;
    public final EObject entryRuleGraph() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleGraph = null;


        try {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:108:2: (iv_ruleGraph= ruleGraph EOF )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:109:2: iv_ruleGraph= ruleGraph EOF
            {
             newCompositeNode(grammarAccess.getGraphRule()); 
            pushFollow(FOLLOW_ruleGraph_in_entryRuleGraph166);
            iv_ruleGraph=ruleGraph();

            state._fsp--;

             current =iv_ruleGraph; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleGraph176); 

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
    // $ANTLR end "entryRuleGraph"


    // $ANTLR start "ruleGraph"
    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:116:1: ruleGraph returns [EObject current=null] : ( ( (lv_strict_0_0= 'strict' ) )? ( (lv_type_1_0= ruleGraphType ) ) ( (lv_name_2_0= ruleDotID ) )? otherlv_3= '{' ( (lv_statements_4_0= ruleStatement ) )* otherlv_5= '}' ) ;
    public final EObject ruleGraph() throws RecognitionException {
        EObject current = null;

        Token lv_strict_0_0=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        Enumerator lv_type_1_0 = null;

        AntlrDatatypeRuleToken lv_name_2_0 = null;

        EObject lv_statements_4_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:119:28: ( ( ( (lv_strict_0_0= 'strict' ) )? ( (lv_type_1_0= ruleGraphType ) ) ( (lv_name_2_0= ruleDotID ) )? otherlv_3= '{' ( (lv_statements_4_0= ruleStatement ) )* otherlv_5= '}' ) )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:120:1: ( ( (lv_strict_0_0= 'strict' ) )? ( (lv_type_1_0= ruleGraphType ) ) ( (lv_name_2_0= ruleDotID ) )? otherlv_3= '{' ( (lv_statements_4_0= ruleStatement ) )* otherlv_5= '}' )
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:120:1: ( ( (lv_strict_0_0= 'strict' ) )? ( (lv_type_1_0= ruleGraphType ) ) ( (lv_name_2_0= ruleDotID ) )? otherlv_3= '{' ( (lv_statements_4_0= ruleStatement ) )* otherlv_5= '}' )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:120:2: ( (lv_strict_0_0= 'strict' ) )? ( (lv_type_1_0= ruleGraphType ) ) ( (lv_name_2_0= ruleDotID ) )? otherlv_3= '{' ( (lv_statements_4_0= ruleStatement ) )* otherlv_5= '}'
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:120:2: ( (lv_strict_0_0= 'strict' ) )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==13) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:121:1: (lv_strict_0_0= 'strict' )
                    {
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:121:1: (lv_strict_0_0= 'strict' )
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:122:3: lv_strict_0_0= 'strict'
                    {
                    lv_strict_0_0=(Token)match(input,13,FOLLOW_13_in_ruleGraph219); 

                            newLeafNode(lv_strict_0_0, grammarAccess.getGraphAccess().getStrictStrictKeyword_0_0());
                        

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getGraphRule());
                    	        }
                           		setWithLastConsumed(current, "strict", true, "strict");
                    	    

                    }


                    }
                    break;

            }

            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:135:3: ( (lv_type_1_0= ruleGraphType ) )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:136:1: (lv_type_1_0= ruleGraphType )
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:136:1: (lv_type_1_0= ruleGraphType )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:137:3: lv_type_1_0= ruleGraphType
            {
             
            	        newCompositeNode(grammarAccess.getGraphAccess().getTypeGraphTypeEnumRuleCall_1_0()); 
            	    
            pushFollow(FOLLOW_ruleGraphType_in_ruleGraph254);
            lv_type_1_0=ruleGraphType();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getGraphRule());
            	        }
                   		set(
                   			current, 
                   			"type",
                    		lv_type_1_0, 
                    		"GraphType");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:153:2: ( (lv_name_2_0= ruleDotID ) )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( ((LA3_0>=RULE_ID && LA3_0<=RULE_STRING)) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:154:1: (lv_name_2_0= ruleDotID )
                    {
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:154:1: (lv_name_2_0= ruleDotID )
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:155:3: lv_name_2_0= ruleDotID
                    {
                     
                    	        newCompositeNode(grammarAccess.getGraphAccess().getNameDotIDParserRuleCall_2_0()); 
                    	    
                    pushFollow(FOLLOW_ruleDotID_in_ruleGraph275);
                    lv_name_2_0=ruleDotID();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getGraphRule());
                    	        }
                           		set(
                           			current, 
                           			"name",
                            		lv_name_2_0, 
                            		"DotID");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }
                    break;

            }

            otherlv_3=(Token)match(input,14,FOLLOW_14_in_ruleGraph288); 

                	newLeafNode(otherlv_3, grammarAccess.getGraphAccess().getLeftCurlyBracketKeyword_3());
                
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:175:1: ( (lv_statements_4_0= ruleStatement ) )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( ((LA4_0>=RULE_ID && LA4_0<=RULE_STRING)||LA4_0==14||LA4_0==22||LA4_0==25||(LA4_0>=27 && LA4_0<=28)) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:176:1: (lv_statements_4_0= ruleStatement )
            	    {
            	    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:176:1: (lv_statements_4_0= ruleStatement )
            	    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:177:3: lv_statements_4_0= ruleStatement
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getGraphAccess().getStatementsStatementParserRuleCall_4_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleStatement_in_ruleGraph309);
            	    lv_statements_4_0=ruleStatement();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getGraphRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"statements",
            	            		lv_statements_4_0, 
            	            		"Statement");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);

            otherlv_5=(Token)match(input,15,FOLLOW_15_in_ruleGraph322); 

                	newLeafNode(otherlv_5, grammarAccess.getGraphAccess().getRightCurlyBracketKeyword_5());
                

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
    // $ANTLR end "ruleGraph"


    // $ANTLR start "entryRuleStatement"
    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:205:1: entryRuleStatement returns [EObject current=null] : iv_ruleStatement= ruleStatement EOF ;
    public final EObject entryRuleStatement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleStatement = null;


        try {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:206:2: (iv_ruleStatement= ruleStatement EOF )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:207:2: iv_ruleStatement= ruleStatement EOF
            {
             newCompositeNode(grammarAccess.getStatementRule()); 
            pushFollow(FOLLOW_ruleStatement_in_entryRuleStatement358);
            iv_ruleStatement=ruleStatement();

            state._fsp--;

             current =iv_ruleStatement; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleStatement368); 

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
    // $ANTLR end "entryRuleStatement"


    // $ANTLR start "ruleStatement"
    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:214:1: ruleStatement returns [EObject current=null] : ( (this_NodeStatement_0= ruleNodeStatement | this_EdgeStatement_1= ruleEdgeStatement | this_AttributeStatement_2= ruleAttributeStatement | this_Attribute_3= ruleAttribute | this_Subgraph_4= ruleSubgraph ) (otherlv_5= ';' )? ) ;
    public final EObject ruleStatement() throws RecognitionException {
        EObject current = null;

        Token otherlv_5=null;
        EObject this_NodeStatement_0 = null;

        EObject this_EdgeStatement_1 = null;

        EObject this_AttributeStatement_2 = null;

        EObject this_Attribute_3 = null;

        EObject this_Subgraph_4 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:217:28: ( ( (this_NodeStatement_0= ruleNodeStatement | this_EdgeStatement_1= ruleEdgeStatement | this_AttributeStatement_2= ruleAttributeStatement | this_Attribute_3= ruleAttribute | this_Subgraph_4= ruleSubgraph ) (otherlv_5= ';' )? ) )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:218:1: ( (this_NodeStatement_0= ruleNodeStatement | this_EdgeStatement_1= ruleEdgeStatement | this_AttributeStatement_2= ruleAttributeStatement | this_Attribute_3= ruleAttribute | this_Subgraph_4= ruleSubgraph ) (otherlv_5= ';' )? )
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:218:1: ( (this_NodeStatement_0= ruleNodeStatement | this_EdgeStatement_1= ruleEdgeStatement | this_AttributeStatement_2= ruleAttributeStatement | this_Attribute_3= ruleAttribute | this_Subgraph_4= ruleSubgraph ) (otherlv_5= ';' )? )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:218:2: (this_NodeStatement_0= ruleNodeStatement | this_EdgeStatement_1= ruleEdgeStatement | this_AttributeStatement_2= ruleAttributeStatement | this_Attribute_3= ruleAttribute | this_Subgraph_4= ruleSubgraph ) (otherlv_5= ';' )?
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:218:2: (this_NodeStatement_0= ruleNodeStatement | this_EdgeStatement_1= ruleEdgeStatement | this_AttributeStatement_2= ruleAttributeStatement | this_Attribute_3= ruleAttribute | this_Subgraph_4= ruleSubgraph )
            int alt5=5;
            alt5 = dfa5.predict(input);
            switch (alt5) {
                case 1 :
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:219:5: this_NodeStatement_0= ruleNodeStatement
                    {
                     
                            newCompositeNode(grammarAccess.getStatementAccess().getNodeStatementParserRuleCall_0_0()); 
                        
                    pushFollow(FOLLOW_ruleNodeStatement_in_ruleStatement416);
                    this_NodeStatement_0=ruleNodeStatement();

                    state._fsp--;

                     
                            current = this_NodeStatement_0; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:229:5: this_EdgeStatement_1= ruleEdgeStatement
                    {
                     
                            newCompositeNode(grammarAccess.getStatementAccess().getEdgeStatementParserRuleCall_0_1()); 
                        
                    pushFollow(FOLLOW_ruleEdgeStatement_in_ruleStatement443);
                    this_EdgeStatement_1=ruleEdgeStatement();

                    state._fsp--;

                     
                            current = this_EdgeStatement_1; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:239:5: this_AttributeStatement_2= ruleAttributeStatement
                    {
                     
                            newCompositeNode(grammarAccess.getStatementAccess().getAttributeStatementParserRuleCall_0_2()); 
                        
                    pushFollow(FOLLOW_ruleAttributeStatement_in_ruleStatement470);
                    this_AttributeStatement_2=ruleAttributeStatement();

                    state._fsp--;

                     
                            current = this_AttributeStatement_2; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:249:5: this_Attribute_3= ruleAttribute
                    {
                     
                            newCompositeNode(grammarAccess.getStatementAccess().getAttributeParserRuleCall_0_3()); 
                        
                    pushFollow(FOLLOW_ruleAttribute_in_ruleStatement497);
                    this_Attribute_3=ruleAttribute();

                    state._fsp--;

                     
                            current = this_Attribute_3; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;
                case 5 :
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:259:5: this_Subgraph_4= ruleSubgraph
                    {
                     
                            newCompositeNode(grammarAccess.getStatementAccess().getSubgraphParserRuleCall_0_4()); 
                        
                    pushFollow(FOLLOW_ruleSubgraph_in_ruleStatement524);
                    this_Subgraph_4=ruleSubgraph();

                    state._fsp--;

                     
                            current = this_Subgraph_4; 
                            afterParserOrEnumRuleCall();
                        

                    }
                    break;

            }

            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:267:2: (otherlv_5= ';' )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==16) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:267:4: otherlv_5= ';'
                    {
                    otherlv_5=(Token)match(input,16,FOLLOW_16_in_ruleStatement537); 

                        	newLeafNode(otherlv_5, grammarAccess.getStatementAccess().getSemicolonKeyword_1());
                        

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
    // $ANTLR end "ruleStatement"


    // $ANTLR start "entryRuleAttribute"
    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:279:1: entryRuleAttribute returns [EObject current=null] : iv_ruleAttribute= ruleAttribute EOF ;
    public final EObject entryRuleAttribute() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttribute = null;


        try {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:280:2: (iv_ruleAttribute= ruleAttribute EOF )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:281:2: iv_ruleAttribute= ruleAttribute EOF
            {
             newCompositeNode(grammarAccess.getAttributeRule()); 
            pushFollow(FOLLOW_ruleAttribute_in_entryRuleAttribute575);
            iv_ruleAttribute=ruleAttribute();

            state._fsp--;

             current =iv_ruleAttribute; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttribute585); 

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
    // $ANTLR end "entryRuleAttribute"


    // $ANTLR start "ruleAttribute"
    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:288:1: ruleAttribute returns [EObject current=null] : ( ( (lv_name_0_0= ruleDotID ) ) otherlv_1= '=' ( (lv_value_2_0= ruleDotID ) ) ) ;
    public final EObject ruleAttribute() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        AntlrDatatypeRuleToken lv_name_0_0 = null;

        AntlrDatatypeRuleToken lv_value_2_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:291:28: ( ( ( (lv_name_0_0= ruleDotID ) ) otherlv_1= '=' ( (lv_value_2_0= ruleDotID ) ) ) )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:292:1: ( ( (lv_name_0_0= ruleDotID ) ) otherlv_1= '=' ( (lv_value_2_0= ruleDotID ) ) )
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:292:1: ( ( (lv_name_0_0= ruleDotID ) ) otherlv_1= '=' ( (lv_value_2_0= ruleDotID ) ) )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:292:2: ( (lv_name_0_0= ruleDotID ) ) otherlv_1= '=' ( (lv_value_2_0= ruleDotID ) )
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:292:2: ( (lv_name_0_0= ruleDotID ) )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:293:1: (lv_name_0_0= ruleDotID )
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:293:1: (lv_name_0_0= ruleDotID )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:294:3: lv_name_0_0= ruleDotID
            {
             
            	        newCompositeNode(grammarAccess.getAttributeAccess().getNameDotIDParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleDotID_in_ruleAttribute631);
            lv_name_0_0=ruleDotID();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getAttributeRule());
            	        }
                   		set(
                   			current, 
                   			"name",
                    		lv_name_0_0, 
                    		"DotID");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_1=(Token)match(input,17,FOLLOW_17_in_ruleAttribute643); 

                	newLeafNode(otherlv_1, grammarAccess.getAttributeAccess().getEqualsSignKeyword_1());
                
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:314:1: ( (lv_value_2_0= ruleDotID ) )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:315:1: (lv_value_2_0= ruleDotID )
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:315:1: (lv_value_2_0= ruleDotID )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:316:3: lv_value_2_0= ruleDotID
            {
             
            	        newCompositeNode(grammarAccess.getAttributeAccess().getValueDotIDParserRuleCall_2_0()); 
            	    
            pushFollow(FOLLOW_ruleDotID_in_ruleAttribute664);
            lv_value_2_0=ruleDotID();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getAttributeRule());
            	        }
                   		set(
                   			current, 
                   			"value",
                    		lv_value_2_0, 
                    		"DotID");
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
    // $ANTLR end "ruleAttribute"


    // $ANTLR start "entryRuleNodeStatement"
    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:340:1: entryRuleNodeStatement returns [EObject current=null] : iv_ruleNodeStatement= ruleNodeStatement EOF ;
    public final EObject entryRuleNodeStatement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleNodeStatement = null;


        try {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:341:2: (iv_ruleNodeStatement= ruleNodeStatement EOF )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:342:2: iv_ruleNodeStatement= ruleNodeStatement EOF
            {
             newCompositeNode(grammarAccess.getNodeStatementRule()); 
            pushFollow(FOLLOW_ruleNodeStatement_in_entryRuleNodeStatement700);
            iv_ruleNodeStatement=ruleNodeStatement();

            state._fsp--;

             current =iv_ruleNodeStatement; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleNodeStatement710); 

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
    // $ANTLR end "entryRuleNodeStatement"


    // $ANTLR start "ruleNodeStatement"
    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:349:1: ruleNodeStatement returns [EObject current=null] : ( ( (lv_node_0_0= ruleNode ) ) (otherlv_1= '[' ( ( (lv_attributes_2_0= ruleListAttribute ) ) ( (otherlv_3= ',' )? ( (lv_attributes_4_0= ruleListAttribute ) ) )* )? otherlv_5= ']' )? ) ;
    public final EObject ruleNodeStatement() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        EObject lv_node_0_0 = null;

        EObject lv_attributes_2_0 = null;

        EObject lv_attributes_4_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:352:28: ( ( ( (lv_node_0_0= ruleNode ) ) (otherlv_1= '[' ( ( (lv_attributes_2_0= ruleListAttribute ) ) ( (otherlv_3= ',' )? ( (lv_attributes_4_0= ruleListAttribute ) ) )* )? otherlv_5= ']' )? ) )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:353:1: ( ( (lv_node_0_0= ruleNode ) ) (otherlv_1= '[' ( ( (lv_attributes_2_0= ruleListAttribute ) ) ( (otherlv_3= ',' )? ( (lv_attributes_4_0= ruleListAttribute ) ) )* )? otherlv_5= ']' )? )
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:353:1: ( ( (lv_node_0_0= ruleNode ) ) (otherlv_1= '[' ( ( (lv_attributes_2_0= ruleListAttribute ) ) ( (otherlv_3= ',' )? ( (lv_attributes_4_0= ruleListAttribute ) ) )* )? otherlv_5= ']' )? )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:353:2: ( (lv_node_0_0= ruleNode ) ) (otherlv_1= '[' ( ( (lv_attributes_2_0= ruleListAttribute ) ) ( (otherlv_3= ',' )? ( (lv_attributes_4_0= ruleListAttribute ) ) )* )? otherlv_5= ']' )?
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:353:2: ( (lv_node_0_0= ruleNode ) )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:354:1: (lv_node_0_0= ruleNode )
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:354:1: (lv_node_0_0= ruleNode )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:355:3: lv_node_0_0= ruleNode
            {
             
            	        newCompositeNode(grammarAccess.getNodeStatementAccess().getNodeNodeParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleNode_in_ruleNodeStatement756);
            lv_node_0_0=ruleNode();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getNodeStatementRule());
            	        }
                   		set(
                   			current, 
                   			"node",
                    		lv_node_0_0, 
                    		"Node");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:371:2: (otherlv_1= '[' ( ( (lv_attributes_2_0= ruleListAttribute ) ) ( (otherlv_3= ',' )? ( (lv_attributes_4_0= ruleListAttribute ) ) )* )? otherlv_5= ']' )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==18) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:371:4: otherlv_1= '[' ( ( (lv_attributes_2_0= ruleListAttribute ) ) ( (otherlv_3= ',' )? ( (lv_attributes_4_0= ruleListAttribute ) ) )* )? otherlv_5= ']'
                    {
                    otherlv_1=(Token)match(input,18,FOLLOW_18_in_ruleNodeStatement769); 

                        	newLeafNode(otherlv_1, grammarAccess.getNodeStatementAccess().getLeftSquareBracketKeyword_1_0());
                        
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:375:1: ( ( (lv_attributes_2_0= ruleListAttribute ) ) ( (otherlv_3= ',' )? ( (lv_attributes_4_0= ruleListAttribute ) ) )* )?
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( ((LA9_0>=RULE_ID && LA9_0<=RULE_STRING)) ) {
                        alt9=1;
                    }
                    switch (alt9) {
                        case 1 :
                            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:375:2: ( (lv_attributes_2_0= ruleListAttribute ) ) ( (otherlv_3= ',' )? ( (lv_attributes_4_0= ruleListAttribute ) ) )*
                            {
                            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:375:2: ( (lv_attributes_2_0= ruleListAttribute ) )
                            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:376:1: (lv_attributes_2_0= ruleListAttribute )
                            {
                            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:376:1: (lv_attributes_2_0= ruleListAttribute )
                            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:377:3: lv_attributes_2_0= ruleListAttribute
                            {
                             
                            	        newCompositeNode(grammarAccess.getNodeStatementAccess().getAttributesListAttributeParserRuleCall_1_1_0_0()); 
                            	    
                            pushFollow(FOLLOW_ruleListAttribute_in_ruleNodeStatement791);
                            lv_attributes_2_0=ruleListAttribute();

                            state._fsp--;


                            	        if (current==null) {
                            	            current = createModelElementForParent(grammarAccess.getNodeStatementRule());
                            	        }
                                   		add(
                                   			current, 
                                   			"attributes",
                                    		lv_attributes_2_0, 
                                    		"ListAttribute");
                            	        afterParserOrEnumRuleCall();
                            	    

                            }


                            }

                            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:393:2: ( (otherlv_3= ',' )? ( (lv_attributes_4_0= ruleListAttribute ) ) )*
                            loop8:
                            do {
                                int alt8=2;
                                int LA8_0 = input.LA(1);

                                if ( ((LA8_0>=RULE_ID && LA8_0<=RULE_STRING)||LA8_0==19) ) {
                                    alt8=1;
                                }


                                switch (alt8) {
                            	case 1 :
                            	    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:393:3: (otherlv_3= ',' )? ( (lv_attributes_4_0= ruleListAttribute ) )
                            	    {
                            	    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:393:3: (otherlv_3= ',' )?
                            	    int alt7=2;
                            	    int LA7_0 = input.LA(1);

                            	    if ( (LA7_0==19) ) {
                            	        alt7=1;
                            	    }
                            	    switch (alt7) {
                            	        case 1 :
                            	            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:393:5: otherlv_3= ','
                            	            {
                            	            otherlv_3=(Token)match(input,19,FOLLOW_19_in_ruleNodeStatement805); 

                            	                	newLeafNode(otherlv_3, grammarAccess.getNodeStatementAccess().getCommaKeyword_1_1_1_0());
                            	                

                            	            }
                            	            break;

                            	    }

                            	    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:397:3: ( (lv_attributes_4_0= ruleListAttribute ) )
                            	    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:398:1: (lv_attributes_4_0= ruleListAttribute )
                            	    {
                            	    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:398:1: (lv_attributes_4_0= ruleListAttribute )
                            	    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:399:3: lv_attributes_4_0= ruleListAttribute
                            	    {
                            	     
                            	    	        newCompositeNode(grammarAccess.getNodeStatementAccess().getAttributesListAttributeParserRuleCall_1_1_1_1_0()); 
                            	    	    
                            	    pushFollow(FOLLOW_ruleListAttribute_in_ruleNodeStatement828);
                            	    lv_attributes_4_0=ruleListAttribute();

                            	    state._fsp--;


                            	    	        if (current==null) {
                            	    	            current = createModelElementForParent(grammarAccess.getNodeStatementRule());
                            	    	        }
                            	           		add(
                            	           			current, 
                            	           			"attributes",
                            	            		lv_attributes_4_0, 
                            	            		"ListAttribute");
                            	    	        afterParserOrEnumRuleCall();
                            	    	    

                            	    }


                            	    }


                            	    }
                            	    break;

                            	default :
                            	    break loop8;
                                }
                            } while (true);


                            }
                            break;

                    }

                    otherlv_5=(Token)match(input,20,FOLLOW_20_in_ruleNodeStatement844); 

                        	newLeafNode(otherlv_5, grammarAccess.getNodeStatementAccess().getRightSquareBracketKeyword_1_2());
                        

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
    // $ANTLR end "ruleNodeStatement"


    // $ANTLR start "entryRuleNode"
    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:427:1: entryRuleNode returns [EObject current=null] : iv_ruleNode= ruleNode EOF ;
    public final EObject entryRuleNode() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleNode = null;


        try {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:428:2: (iv_ruleNode= ruleNode EOF )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:429:2: iv_ruleNode= ruleNode EOF
            {
             newCompositeNode(grammarAccess.getNodeRule()); 
            pushFollow(FOLLOW_ruleNode_in_entryRuleNode882);
            iv_ruleNode=ruleNode();

            state._fsp--;

             current =iv_ruleNode; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleNode892); 

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
    // $ANTLR end "entryRuleNode"


    // $ANTLR start "ruleNode"
    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:436:1: ruleNode returns [EObject current=null] : ( ( (lv_name_0_0= ruleDotID ) ) (otherlv_1= ':' ( (lv_port_2_0= rulePort ) ) )? ) ;
    public final EObject ruleNode() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        AntlrDatatypeRuleToken lv_name_0_0 = null;

        EObject lv_port_2_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:439:28: ( ( ( (lv_name_0_0= ruleDotID ) ) (otherlv_1= ':' ( (lv_port_2_0= rulePort ) ) )? ) )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:440:1: ( ( (lv_name_0_0= ruleDotID ) ) (otherlv_1= ':' ( (lv_port_2_0= rulePort ) ) )? )
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:440:1: ( ( (lv_name_0_0= ruleDotID ) ) (otherlv_1= ':' ( (lv_port_2_0= rulePort ) ) )? )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:440:2: ( (lv_name_0_0= ruleDotID ) ) (otherlv_1= ':' ( (lv_port_2_0= rulePort ) ) )?
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:440:2: ( (lv_name_0_0= ruleDotID ) )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:441:1: (lv_name_0_0= ruleDotID )
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:441:1: (lv_name_0_0= ruleDotID )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:442:3: lv_name_0_0= ruleDotID
            {
             
            	        newCompositeNode(grammarAccess.getNodeAccess().getNameDotIDParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleDotID_in_ruleNode938);
            lv_name_0_0=ruleDotID();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getNodeRule());
            	        }
                   		set(
                   			current, 
                   			"name",
                    		lv_name_0_0, 
                    		"DotID");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:458:2: (otherlv_1= ':' ( (lv_port_2_0= rulePort ) ) )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==21) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:458:4: otherlv_1= ':' ( (lv_port_2_0= rulePort ) )
                    {
                    otherlv_1=(Token)match(input,21,FOLLOW_21_in_ruleNode951); 

                        	newLeafNode(otherlv_1, grammarAccess.getNodeAccess().getColonKeyword_1_0());
                        
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:462:1: ( (lv_port_2_0= rulePort ) )
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:463:1: (lv_port_2_0= rulePort )
                    {
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:463:1: (lv_port_2_0= rulePort )
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:464:3: lv_port_2_0= rulePort
                    {
                     
                    	        newCompositeNode(grammarAccess.getNodeAccess().getPortPortParserRuleCall_1_1_0()); 
                    	    
                    pushFollow(FOLLOW_rulePort_in_ruleNode972);
                    lv_port_2_0=rulePort();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getNodeRule());
                    	        }
                           		set(
                           			current, 
                           			"port",
                            		lv_port_2_0, 
                            		"Port");
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
    // $ANTLR end "ruleNode"


    // $ANTLR start "entryRuleEdgeStatement"
    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:488:1: entryRuleEdgeStatement returns [EObject current=null] : iv_ruleEdgeStatement= ruleEdgeStatement EOF ;
    public final EObject entryRuleEdgeStatement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleEdgeStatement = null;


        try {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:489:2: (iv_ruleEdgeStatement= ruleEdgeStatement EOF )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:490:2: iv_ruleEdgeStatement= ruleEdgeStatement EOF
            {
             newCompositeNode(grammarAccess.getEdgeStatementRule()); 
            pushFollow(FOLLOW_ruleEdgeStatement_in_entryRuleEdgeStatement1010);
            iv_ruleEdgeStatement=ruleEdgeStatement();

            state._fsp--;

             current =iv_ruleEdgeStatement; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleEdgeStatement1020); 

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
    // $ANTLR end "entryRuleEdgeStatement"


    // $ANTLR start "ruleEdgeStatement"
    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:497:1: ruleEdgeStatement returns [EObject current=null] : ( ( (lv_sourceNode_0_0= ruleNode ) ) ( (lv_edgeTargets_1_0= ruleEdgeTarget ) )+ (otherlv_2= '[' ( ( (lv_attributes_3_0= ruleListAttribute ) ) ( (otherlv_4= ',' )? ( (lv_attributes_5_0= ruleListAttribute ) ) )* )? otherlv_6= ']' )? ) ;
    public final EObject ruleEdgeStatement() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        Token otherlv_4=null;
        Token otherlv_6=null;
        EObject lv_sourceNode_0_0 = null;

        EObject lv_edgeTargets_1_0 = null;

        EObject lv_attributes_3_0 = null;

        EObject lv_attributes_5_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:500:28: ( ( ( (lv_sourceNode_0_0= ruleNode ) ) ( (lv_edgeTargets_1_0= ruleEdgeTarget ) )+ (otherlv_2= '[' ( ( (lv_attributes_3_0= ruleListAttribute ) ) ( (otherlv_4= ',' )? ( (lv_attributes_5_0= ruleListAttribute ) ) )* )? otherlv_6= ']' )? ) )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:501:1: ( ( (lv_sourceNode_0_0= ruleNode ) ) ( (lv_edgeTargets_1_0= ruleEdgeTarget ) )+ (otherlv_2= '[' ( ( (lv_attributes_3_0= ruleListAttribute ) ) ( (otherlv_4= ',' )? ( (lv_attributes_5_0= ruleListAttribute ) ) )* )? otherlv_6= ']' )? )
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:501:1: ( ( (lv_sourceNode_0_0= ruleNode ) ) ( (lv_edgeTargets_1_0= ruleEdgeTarget ) )+ (otherlv_2= '[' ( ( (lv_attributes_3_0= ruleListAttribute ) ) ( (otherlv_4= ',' )? ( (lv_attributes_5_0= ruleListAttribute ) ) )* )? otherlv_6= ']' )? )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:501:2: ( (lv_sourceNode_0_0= ruleNode ) ) ( (lv_edgeTargets_1_0= ruleEdgeTarget ) )+ (otherlv_2= '[' ( ( (lv_attributes_3_0= ruleListAttribute ) ) ( (otherlv_4= ',' )? ( (lv_attributes_5_0= ruleListAttribute ) ) )* )? otherlv_6= ']' )?
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:501:2: ( (lv_sourceNode_0_0= ruleNode ) )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:502:1: (lv_sourceNode_0_0= ruleNode )
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:502:1: (lv_sourceNode_0_0= ruleNode )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:503:3: lv_sourceNode_0_0= ruleNode
            {
             
            	        newCompositeNode(grammarAccess.getEdgeStatementAccess().getSourceNodeNodeParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleNode_in_ruleEdgeStatement1066);
            lv_sourceNode_0_0=ruleNode();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getEdgeStatementRule());
            	        }
                   		set(
                   			current, 
                   			"sourceNode",
                    		lv_sourceNode_0_0, 
                    		"Node");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:519:2: ( (lv_edgeTargets_1_0= ruleEdgeTarget ) )+
            int cnt12=0;
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( ((LA12_0>=23 && LA12_0<=24)) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:520:1: (lv_edgeTargets_1_0= ruleEdgeTarget )
            	    {
            	    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:520:1: (lv_edgeTargets_1_0= ruleEdgeTarget )
            	    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:521:3: lv_edgeTargets_1_0= ruleEdgeTarget
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getEdgeStatementAccess().getEdgeTargetsEdgeTargetParserRuleCall_1_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleEdgeTarget_in_ruleEdgeStatement1087);
            	    lv_edgeTargets_1_0=ruleEdgeTarget();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getEdgeStatementRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"edgeTargets",
            	            		lv_edgeTargets_1_0, 
            	            		"EdgeTarget");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt12 >= 1 ) break loop12;
                        EarlyExitException eee =
                            new EarlyExitException(12, input);
                        throw eee;
                }
                cnt12++;
            } while (true);

            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:537:3: (otherlv_2= '[' ( ( (lv_attributes_3_0= ruleListAttribute ) ) ( (otherlv_4= ',' )? ( (lv_attributes_5_0= ruleListAttribute ) ) )* )? otherlv_6= ']' )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==18) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:537:5: otherlv_2= '[' ( ( (lv_attributes_3_0= ruleListAttribute ) ) ( (otherlv_4= ',' )? ( (lv_attributes_5_0= ruleListAttribute ) ) )* )? otherlv_6= ']'
                    {
                    otherlv_2=(Token)match(input,18,FOLLOW_18_in_ruleEdgeStatement1101); 

                        	newLeafNode(otherlv_2, grammarAccess.getEdgeStatementAccess().getLeftSquareBracketKeyword_2_0());
                        
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:541:1: ( ( (lv_attributes_3_0= ruleListAttribute ) ) ( (otherlv_4= ',' )? ( (lv_attributes_5_0= ruleListAttribute ) ) )* )?
                    int alt15=2;
                    int LA15_0 = input.LA(1);

                    if ( ((LA15_0>=RULE_ID && LA15_0<=RULE_STRING)) ) {
                        alt15=1;
                    }
                    switch (alt15) {
                        case 1 :
                            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:541:2: ( (lv_attributes_3_0= ruleListAttribute ) ) ( (otherlv_4= ',' )? ( (lv_attributes_5_0= ruleListAttribute ) ) )*
                            {
                            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:541:2: ( (lv_attributes_3_0= ruleListAttribute ) )
                            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:542:1: (lv_attributes_3_0= ruleListAttribute )
                            {
                            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:542:1: (lv_attributes_3_0= ruleListAttribute )
                            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:543:3: lv_attributes_3_0= ruleListAttribute
                            {
                             
                            	        newCompositeNode(grammarAccess.getEdgeStatementAccess().getAttributesListAttributeParserRuleCall_2_1_0_0()); 
                            	    
                            pushFollow(FOLLOW_ruleListAttribute_in_ruleEdgeStatement1123);
                            lv_attributes_3_0=ruleListAttribute();

                            state._fsp--;


                            	        if (current==null) {
                            	            current = createModelElementForParent(grammarAccess.getEdgeStatementRule());
                            	        }
                                   		add(
                                   			current, 
                                   			"attributes",
                                    		lv_attributes_3_0, 
                                    		"ListAttribute");
                            	        afterParserOrEnumRuleCall();
                            	    

                            }


                            }

                            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:559:2: ( (otherlv_4= ',' )? ( (lv_attributes_5_0= ruleListAttribute ) ) )*
                            loop14:
                            do {
                                int alt14=2;
                                int LA14_0 = input.LA(1);

                                if ( ((LA14_0>=RULE_ID && LA14_0<=RULE_STRING)||LA14_0==19) ) {
                                    alt14=1;
                                }


                                switch (alt14) {
                            	case 1 :
                            	    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:559:3: (otherlv_4= ',' )? ( (lv_attributes_5_0= ruleListAttribute ) )
                            	    {
                            	    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:559:3: (otherlv_4= ',' )?
                            	    int alt13=2;
                            	    int LA13_0 = input.LA(1);

                            	    if ( (LA13_0==19) ) {
                            	        alt13=1;
                            	    }
                            	    switch (alt13) {
                            	        case 1 :
                            	            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:559:5: otherlv_4= ','
                            	            {
                            	            otherlv_4=(Token)match(input,19,FOLLOW_19_in_ruleEdgeStatement1137); 

                            	                	newLeafNode(otherlv_4, grammarAccess.getEdgeStatementAccess().getCommaKeyword_2_1_1_0());
                            	                

                            	            }
                            	            break;

                            	    }

                            	    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:563:3: ( (lv_attributes_5_0= ruleListAttribute ) )
                            	    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:564:1: (lv_attributes_5_0= ruleListAttribute )
                            	    {
                            	    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:564:1: (lv_attributes_5_0= ruleListAttribute )
                            	    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:565:3: lv_attributes_5_0= ruleListAttribute
                            	    {
                            	     
                            	    	        newCompositeNode(grammarAccess.getEdgeStatementAccess().getAttributesListAttributeParserRuleCall_2_1_1_1_0()); 
                            	    	    
                            	    pushFollow(FOLLOW_ruleListAttribute_in_ruleEdgeStatement1160);
                            	    lv_attributes_5_0=ruleListAttribute();

                            	    state._fsp--;


                            	    	        if (current==null) {
                            	    	            current = createModelElementForParent(grammarAccess.getEdgeStatementRule());
                            	    	        }
                            	           		add(
                            	           			current, 
                            	           			"attributes",
                            	            		lv_attributes_5_0, 
                            	            		"ListAttribute");
                            	    	        afterParserOrEnumRuleCall();
                            	    	    

                            	    }


                            	    }


                            	    }
                            	    break;

                            	default :
                            	    break loop14;
                                }
                            } while (true);


                            }
                            break;

                    }

                    otherlv_6=(Token)match(input,20,FOLLOW_20_in_ruleEdgeStatement1176); 

                        	newLeafNode(otherlv_6, grammarAccess.getEdgeStatementAccess().getRightSquareBracketKeyword_2_2());
                        

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
    // $ANTLR end "ruleEdgeStatement"


    // $ANTLR start "entryRuleEdgeTarget"
    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:593:1: entryRuleEdgeTarget returns [EObject current=null] : iv_ruleEdgeTarget= ruleEdgeTarget EOF ;
    public final EObject entryRuleEdgeTarget() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleEdgeTarget = null;


        try {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:594:2: (iv_ruleEdgeTarget= ruleEdgeTarget EOF )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:595:2: iv_ruleEdgeTarget= ruleEdgeTarget EOF
            {
             newCompositeNode(grammarAccess.getEdgeTargetRule()); 
            pushFollow(FOLLOW_ruleEdgeTarget_in_entryRuleEdgeTarget1214);
            iv_ruleEdgeTarget=ruleEdgeTarget();

            state._fsp--;

             current =iv_ruleEdgeTarget; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleEdgeTarget1224); 

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
    // $ANTLR end "entryRuleEdgeTarget"


    // $ANTLR start "ruleEdgeTarget"
    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:602:1: ruleEdgeTarget returns [EObject current=null] : ( ( (lv_operator_0_0= ruleEdgeOperator ) ) ( ( (lv_targetSubgraph_1_0= ruleSubgraph ) ) | ( (lv_targetnode_2_0= ruleNode ) ) ) ) ;
    public final EObject ruleEdgeTarget() throws RecognitionException {
        EObject current = null;

        Enumerator lv_operator_0_0 = null;

        EObject lv_targetSubgraph_1_0 = null;

        EObject lv_targetnode_2_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:605:28: ( ( ( (lv_operator_0_0= ruleEdgeOperator ) ) ( ( (lv_targetSubgraph_1_0= ruleSubgraph ) ) | ( (lv_targetnode_2_0= ruleNode ) ) ) ) )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:606:1: ( ( (lv_operator_0_0= ruleEdgeOperator ) ) ( ( (lv_targetSubgraph_1_0= ruleSubgraph ) ) | ( (lv_targetnode_2_0= ruleNode ) ) ) )
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:606:1: ( ( (lv_operator_0_0= ruleEdgeOperator ) ) ( ( (lv_targetSubgraph_1_0= ruleSubgraph ) ) | ( (lv_targetnode_2_0= ruleNode ) ) ) )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:606:2: ( (lv_operator_0_0= ruleEdgeOperator ) ) ( ( (lv_targetSubgraph_1_0= ruleSubgraph ) ) | ( (lv_targetnode_2_0= ruleNode ) ) )
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:606:2: ( (lv_operator_0_0= ruleEdgeOperator ) )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:607:1: (lv_operator_0_0= ruleEdgeOperator )
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:607:1: (lv_operator_0_0= ruleEdgeOperator )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:608:3: lv_operator_0_0= ruleEdgeOperator
            {
             
            	        newCompositeNode(grammarAccess.getEdgeTargetAccess().getOperatorEdgeOperatorEnumRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleEdgeOperator_in_ruleEdgeTarget1270);
            lv_operator_0_0=ruleEdgeOperator();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getEdgeTargetRule());
            	        }
                   		set(
                   			current, 
                   			"operator",
                    		lv_operator_0_0, 
                    		"EdgeOperator");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:624:2: ( ( (lv_targetSubgraph_1_0= ruleSubgraph ) ) | ( (lv_targetnode_2_0= ruleNode ) ) )
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==14||LA17_0==22) ) {
                alt17=1;
            }
            else if ( ((LA17_0>=RULE_ID && LA17_0<=RULE_STRING)) ) {
                alt17=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 17, 0, input);

                throw nvae;
            }
            switch (alt17) {
                case 1 :
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:624:3: ( (lv_targetSubgraph_1_0= ruleSubgraph ) )
                    {
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:624:3: ( (lv_targetSubgraph_1_0= ruleSubgraph ) )
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:625:1: (lv_targetSubgraph_1_0= ruleSubgraph )
                    {
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:625:1: (lv_targetSubgraph_1_0= ruleSubgraph )
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:626:3: lv_targetSubgraph_1_0= ruleSubgraph
                    {
                     
                    	        newCompositeNode(grammarAccess.getEdgeTargetAccess().getTargetSubgraphSubgraphParserRuleCall_1_0_0()); 
                    	    
                    pushFollow(FOLLOW_ruleSubgraph_in_ruleEdgeTarget1292);
                    lv_targetSubgraph_1_0=ruleSubgraph();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getEdgeTargetRule());
                    	        }
                           		set(
                           			current, 
                           			"targetSubgraph",
                            		lv_targetSubgraph_1_0, 
                            		"Subgraph");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:643:6: ( (lv_targetnode_2_0= ruleNode ) )
                    {
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:643:6: ( (lv_targetnode_2_0= ruleNode ) )
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:644:1: (lv_targetnode_2_0= ruleNode )
                    {
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:644:1: (lv_targetnode_2_0= ruleNode )
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:645:3: lv_targetnode_2_0= ruleNode
                    {
                     
                    	        newCompositeNode(grammarAccess.getEdgeTargetAccess().getTargetnodeNodeParserRuleCall_1_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleNode_in_ruleEdgeTarget1319);
                    lv_targetnode_2_0=ruleNode();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getEdgeTargetRule());
                    	        }
                           		set(
                           			current, 
                           			"targetnode",
                            		lv_targetnode_2_0, 
                            		"Node");
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
    // $ANTLR end "ruleEdgeTarget"


    // $ANTLR start "entryRuleAttributeStatement"
    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:669:1: entryRuleAttributeStatement returns [EObject current=null] : iv_ruleAttributeStatement= ruleAttributeStatement EOF ;
    public final EObject entryRuleAttributeStatement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAttributeStatement = null;


        try {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:670:2: (iv_ruleAttributeStatement= ruleAttributeStatement EOF )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:671:2: iv_ruleAttributeStatement= ruleAttributeStatement EOF
            {
             newCompositeNode(grammarAccess.getAttributeStatementRule()); 
            pushFollow(FOLLOW_ruleAttributeStatement_in_entryRuleAttributeStatement1356);
            iv_ruleAttributeStatement=ruleAttributeStatement();

            state._fsp--;

             current =iv_ruleAttributeStatement; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleAttributeStatement1366); 

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
    // $ANTLR end "entryRuleAttributeStatement"


    // $ANTLR start "ruleAttributeStatement"
    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:678:1: ruleAttributeStatement returns [EObject current=null] : ( ( (lv_type_0_0= ruleAttributeType ) ) otherlv_1= '[' ( ( (lv_attributes_2_0= ruleListAttribute ) ) ( (otherlv_3= ',' )? ( (lv_attributes_4_0= ruleListAttribute ) ) )* )? otherlv_5= ']' ) ;
    public final EObject ruleAttributeStatement() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        Enumerator lv_type_0_0 = null;

        EObject lv_attributes_2_0 = null;

        EObject lv_attributes_4_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:681:28: ( ( ( (lv_type_0_0= ruleAttributeType ) ) otherlv_1= '[' ( ( (lv_attributes_2_0= ruleListAttribute ) ) ( (otherlv_3= ',' )? ( (lv_attributes_4_0= ruleListAttribute ) ) )* )? otherlv_5= ']' ) )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:682:1: ( ( (lv_type_0_0= ruleAttributeType ) ) otherlv_1= '[' ( ( (lv_attributes_2_0= ruleListAttribute ) ) ( (otherlv_3= ',' )? ( (lv_attributes_4_0= ruleListAttribute ) ) )* )? otherlv_5= ']' )
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:682:1: ( ( (lv_type_0_0= ruleAttributeType ) ) otherlv_1= '[' ( ( (lv_attributes_2_0= ruleListAttribute ) ) ( (otherlv_3= ',' )? ( (lv_attributes_4_0= ruleListAttribute ) ) )* )? otherlv_5= ']' )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:682:2: ( (lv_type_0_0= ruleAttributeType ) ) otherlv_1= '[' ( ( (lv_attributes_2_0= ruleListAttribute ) ) ( (otherlv_3= ',' )? ( (lv_attributes_4_0= ruleListAttribute ) ) )* )? otherlv_5= ']'
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:682:2: ( (lv_type_0_0= ruleAttributeType ) )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:683:1: (lv_type_0_0= ruleAttributeType )
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:683:1: (lv_type_0_0= ruleAttributeType )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:684:3: lv_type_0_0= ruleAttributeType
            {
             
            	        newCompositeNode(grammarAccess.getAttributeStatementAccess().getTypeAttributeTypeEnumRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleAttributeType_in_ruleAttributeStatement1412);
            lv_type_0_0=ruleAttributeType();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getAttributeStatementRule());
            	        }
                   		set(
                   			current, 
                   			"type",
                    		lv_type_0_0, 
                    		"AttributeType");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            otherlv_1=(Token)match(input,18,FOLLOW_18_in_ruleAttributeStatement1424); 

                	newLeafNode(otherlv_1, grammarAccess.getAttributeStatementAccess().getLeftSquareBracketKeyword_1());
                
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:704:1: ( ( (lv_attributes_2_0= ruleListAttribute ) ) ( (otherlv_3= ',' )? ( (lv_attributes_4_0= ruleListAttribute ) ) )* )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( ((LA20_0>=RULE_ID && LA20_0<=RULE_STRING)) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:704:2: ( (lv_attributes_2_0= ruleListAttribute ) ) ( (otherlv_3= ',' )? ( (lv_attributes_4_0= ruleListAttribute ) ) )*
                    {
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:704:2: ( (lv_attributes_2_0= ruleListAttribute ) )
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:705:1: (lv_attributes_2_0= ruleListAttribute )
                    {
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:705:1: (lv_attributes_2_0= ruleListAttribute )
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:706:3: lv_attributes_2_0= ruleListAttribute
                    {
                     
                    	        newCompositeNode(grammarAccess.getAttributeStatementAccess().getAttributesListAttributeParserRuleCall_2_0_0()); 
                    	    
                    pushFollow(FOLLOW_ruleListAttribute_in_ruleAttributeStatement1446);
                    lv_attributes_2_0=ruleListAttribute();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getAttributeStatementRule());
                    	        }
                           		add(
                           			current, 
                           			"attributes",
                            		lv_attributes_2_0, 
                            		"ListAttribute");
                    	        afterParserOrEnumRuleCall();
                    	    

                    }


                    }

                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:722:2: ( (otherlv_3= ',' )? ( (lv_attributes_4_0= ruleListAttribute ) ) )*
                    loop19:
                    do {
                        int alt19=2;
                        int LA19_0 = input.LA(1);

                        if ( ((LA19_0>=RULE_ID && LA19_0<=RULE_STRING)||LA19_0==19) ) {
                            alt19=1;
                        }


                        switch (alt19) {
                    	case 1 :
                    	    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:722:3: (otherlv_3= ',' )? ( (lv_attributes_4_0= ruleListAttribute ) )
                    	    {
                    	    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:722:3: (otherlv_3= ',' )?
                    	    int alt18=2;
                    	    int LA18_0 = input.LA(1);

                    	    if ( (LA18_0==19) ) {
                    	        alt18=1;
                    	    }
                    	    switch (alt18) {
                    	        case 1 :
                    	            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:722:5: otherlv_3= ','
                    	            {
                    	            otherlv_3=(Token)match(input,19,FOLLOW_19_in_ruleAttributeStatement1460); 

                    	                	newLeafNode(otherlv_3, grammarAccess.getAttributeStatementAccess().getCommaKeyword_2_1_0());
                    	                

                    	            }
                    	            break;

                    	    }

                    	    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:726:3: ( (lv_attributes_4_0= ruleListAttribute ) )
                    	    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:727:1: (lv_attributes_4_0= ruleListAttribute )
                    	    {
                    	    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:727:1: (lv_attributes_4_0= ruleListAttribute )
                    	    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:728:3: lv_attributes_4_0= ruleListAttribute
                    	    {
                    	     
                    	    	        newCompositeNode(grammarAccess.getAttributeStatementAccess().getAttributesListAttributeParserRuleCall_2_1_1_0()); 
                    	    	    
                    	    pushFollow(FOLLOW_ruleListAttribute_in_ruleAttributeStatement1483);
                    	    lv_attributes_4_0=ruleListAttribute();

                    	    state._fsp--;


                    	    	        if (current==null) {
                    	    	            current = createModelElementForParent(grammarAccess.getAttributeStatementRule());
                    	    	        }
                    	           		add(
                    	           			current, 
                    	           			"attributes",
                    	            		lv_attributes_4_0, 
                    	            		"ListAttribute");
                    	    	        afterParserOrEnumRuleCall();
                    	    	    

                    	    }


                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop19;
                        }
                    } while (true);


                    }
                    break;

            }

            otherlv_5=(Token)match(input,20,FOLLOW_20_in_ruleAttributeStatement1499); 

                	newLeafNode(otherlv_5, grammarAccess.getAttributeStatementAccess().getRightSquareBracketKeyword_3());
                

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
    // $ANTLR end "ruleAttributeStatement"


    // $ANTLR start "entryRuleSubgraph"
    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:756:1: entryRuleSubgraph returns [EObject current=null] : iv_ruleSubgraph= ruleSubgraph EOF ;
    public final EObject entryRuleSubgraph() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleSubgraph = null;


        try {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:757:2: (iv_ruleSubgraph= ruleSubgraph EOF )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:758:2: iv_ruleSubgraph= ruleSubgraph EOF
            {
             newCompositeNode(grammarAccess.getSubgraphRule()); 
            pushFollow(FOLLOW_ruleSubgraph_in_entryRuleSubgraph1535);
            iv_ruleSubgraph=ruleSubgraph();

            state._fsp--;

             current =iv_ruleSubgraph; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleSubgraph1545); 

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
    // $ANTLR end "entryRuleSubgraph"


    // $ANTLR start "ruleSubgraph"
    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:765:1: ruleSubgraph returns [EObject current=null] : ( () (otherlv_1= 'subgraph' ( (lv_name_2_0= RULE_ID ) )? )? otherlv_3= '{' ( (lv_statements_4_0= ruleStatement ) )* otherlv_5= '}' ) ;
    public final EObject ruleSubgraph() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token lv_name_2_0=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        EObject lv_statements_4_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:768:28: ( ( () (otherlv_1= 'subgraph' ( (lv_name_2_0= RULE_ID ) )? )? otherlv_3= '{' ( (lv_statements_4_0= ruleStatement ) )* otherlv_5= '}' ) )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:769:1: ( () (otherlv_1= 'subgraph' ( (lv_name_2_0= RULE_ID ) )? )? otherlv_3= '{' ( (lv_statements_4_0= ruleStatement ) )* otherlv_5= '}' )
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:769:1: ( () (otherlv_1= 'subgraph' ( (lv_name_2_0= RULE_ID ) )? )? otherlv_3= '{' ( (lv_statements_4_0= ruleStatement ) )* otherlv_5= '}' )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:769:2: () (otherlv_1= 'subgraph' ( (lv_name_2_0= RULE_ID ) )? )? otherlv_3= '{' ( (lv_statements_4_0= ruleStatement ) )* otherlv_5= '}'
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:769:2: ()
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:770:5: 
            {

                    current = forceCreateModelElement(
                        grammarAccess.getSubgraphAccess().getSubgraphAction_0(),
                        current);
                

            }

            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:775:2: (otherlv_1= 'subgraph' ( (lv_name_2_0= RULE_ID ) )? )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==22) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:775:4: otherlv_1= 'subgraph' ( (lv_name_2_0= RULE_ID ) )?
                    {
                    otherlv_1=(Token)match(input,22,FOLLOW_22_in_ruleSubgraph1592); 

                        	newLeafNode(otherlv_1, grammarAccess.getSubgraphAccess().getSubgraphKeyword_1_0());
                        
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:779:1: ( (lv_name_2_0= RULE_ID ) )?
                    int alt21=2;
                    int LA21_0 = input.LA(1);

                    if ( (LA21_0==RULE_ID) ) {
                        alt21=1;
                    }
                    switch (alt21) {
                        case 1 :
                            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:780:1: (lv_name_2_0= RULE_ID )
                            {
                            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:780:1: (lv_name_2_0= RULE_ID )
                            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:781:3: lv_name_2_0= RULE_ID
                            {
                            lv_name_2_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleSubgraph1609); 

                            			newLeafNode(lv_name_2_0, grammarAccess.getSubgraphAccess().getNameIDTerminalRuleCall_1_1_0()); 
                            		

                            	        if (current==null) {
                            	            current = createModelElement(grammarAccess.getSubgraphRule());
                            	        }
                                   		setWithLastConsumed(
                                   			current, 
                                   			"name",
                                    		lv_name_2_0, 
                                    		"ID");
                            	    

                            }


                            }
                            break;

                    }


                    }
                    break;

            }

            otherlv_3=(Token)match(input,14,FOLLOW_14_in_ruleSubgraph1629); 

                	newLeafNode(otherlv_3, grammarAccess.getSubgraphAccess().getLeftCurlyBracketKeyword_2());
                
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:801:1: ( (lv_statements_4_0= ruleStatement ) )*
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( ((LA23_0>=RULE_ID && LA23_0<=RULE_STRING)||LA23_0==14||LA23_0==22||LA23_0==25||(LA23_0>=27 && LA23_0<=28)) ) {
                    alt23=1;
                }


                switch (alt23) {
            	case 1 :
            	    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:802:1: (lv_statements_4_0= ruleStatement )
            	    {
            	    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:802:1: (lv_statements_4_0= ruleStatement )
            	    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:803:3: lv_statements_4_0= ruleStatement
            	    {
            	     
            	    	        newCompositeNode(grammarAccess.getSubgraphAccess().getStatementsStatementParserRuleCall_3_0()); 
            	    	    
            	    pushFollow(FOLLOW_ruleStatement_in_ruleSubgraph1650);
            	    lv_statements_4_0=ruleStatement();

            	    state._fsp--;


            	    	        if (current==null) {
            	    	            current = createModelElementForParent(grammarAccess.getSubgraphRule());
            	    	        }
            	           		add(
            	           			current, 
            	           			"statements",
            	            		lv_statements_4_0, 
            	            		"Statement");
            	    	        afterParserOrEnumRuleCall();
            	    	    

            	    }


            	    }
            	    break;

            	default :
            	    break loop23;
                }
            } while (true);

            otherlv_5=(Token)match(input,15,FOLLOW_15_in_ruleSubgraph1663); 

                	newLeafNode(otherlv_5, grammarAccess.getSubgraphAccess().getRightCurlyBracketKeyword_4());
                

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
    // $ANTLR end "ruleSubgraph"


    // $ANTLR start "entryRuleListAttribute"
    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:831:1: entryRuleListAttribute returns [EObject current=null] : iv_ruleListAttribute= ruleListAttribute EOF ;
    public final EObject entryRuleListAttribute() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleListAttribute = null;


        try {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:832:2: (iv_ruleListAttribute= ruleListAttribute EOF )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:833:2: iv_ruleListAttribute= ruleListAttribute EOF
            {
             newCompositeNode(grammarAccess.getListAttributeRule()); 
            pushFollow(FOLLOW_ruleListAttribute_in_entryRuleListAttribute1699);
            iv_ruleListAttribute=ruleListAttribute();

            state._fsp--;

             current =iv_ruleListAttribute; 
            match(input,EOF,FOLLOW_EOF_in_entryRuleListAttribute1709); 

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
    // $ANTLR end "entryRuleListAttribute"


    // $ANTLR start "ruleListAttribute"
    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:840:1: ruleListAttribute returns [EObject current=null] : ( ( (lv_name_0_0= ruleDotID ) ) (otherlv_1= '=' ( (lv_value_2_0= ruleDotID ) ) )? ) ;
    public final EObject ruleListAttribute() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        AntlrDatatypeRuleToken lv_name_0_0 = null;

        AntlrDatatypeRuleToken lv_value_2_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:843:28: ( ( ( (lv_name_0_0= ruleDotID ) ) (otherlv_1= '=' ( (lv_value_2_0= ruleDotID ) ) )? ) )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:844:1: ( ( (lv_name_0_0= ruleDotID ) ) (otherlv_1= '=' ( (lv_value_2_0= ruleDotID ) ) )? )
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:844:1: ( ( (lv_name_0_0= ruleDotID ) ) (otherlv_1= '=' ( (lv_value_2_0= ruleDotID ) ) )? )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:844:2: ( (lv_name_0_0= ruleDotID ) ) (otherlv_1= '=' ( (lv_value_2_0= ruleDotID ) ) )?
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:844:2: ( (lv_name_0_0= ruleDotID ) )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:845:1: (lv_name_0_0= ruleDotID )
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:845:1: (lv_name_0_0= ruleDotID )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:846:3: lv_name_0_0= ruleDotID
            {
             
            	        newCompositeNode(grammarAccess.getListAttributeAccess().getNameDotIDParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleDotID_in_ruleListAttribute1755);
            lv_name_0_0=ruleDotID();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getListAttributeRule());
            	        }
                   		set(
                   			current, 
                   			"name",
                    		lv_name_0_0, 
                    		"DotID");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:862:2: (otherlv_1= '=' ( (lv_value_2_0= ruleDotID ) ) )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==17) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:862:4: otherlv_1= '=' ( (lv_value_2_0= ruleDotID ) )
                    {
                    otherlv_1=(Token)match(input,17,FOLLOW_17_in_ruleListAttribute1768); 

                        	newLeafNode(otherlv_1, grammarAccess.getListAttributeAccess().getEqualsSignKeyword_1_0());
                        
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:866:1: ( (lv_value_2_0= ruleDotID ) )
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:867:1: (lv_value_2_0= ruleDotID )
                    {
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:867:1: (lv_value_2_0= ruleDotID )
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:868:3: lv_value_2_0= ruleDotID
                    {
                     
                    	        newCompositeNode(grammarAccess.getListAttributeAccess().getValueDotIDParserRuleCall_1_1_0()); 
                    	    
                    pushFollow(FOLLOW_ruleDotID_in_ruleListAttribute1789);
                    lv_value_2_0=ruleDotID();

                    state._fsp--;


                    	        if (current==null) {
                    	            current = createModelElementForParent(grammarAccess.getListAttributeRule());
                    	        }
                           		set(
                           			current, 
                           			"value",
                            		lv_value_2_0, 
                            		"DotID");
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
    // $ANTLR end "ruleListAttribute"


    // $ANTLR start "entryRulePort"
    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:892:1: entryRulePort returns [EObject current=null] : iv_rulePort= rulePort EOF ;
    public final EObject entryRulePort() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePort = null;


        try {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:893:2: (iv_rulePort= rulePort EOF )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:894:2: iv_rulePort= rulePort EOF
            {
             newCompositeNode(grammarAccess.getPortRule()); 
            pushFollow(FOLLOW_rulePort_in_entryRulePort1827);
            iv_rulePort=rulePort();

            state._fsp--;

             current =iv_rulePort; 
            match(input,EOF,FOLLOW_EOF_in_entryRulePort1837); 

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
    // $ANTLR end "entryRulePort"


    // $ANTLR start "rulePort"
    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:901:1: rulePort returns [EObject current=null] : ( ( (lv_name_0_0= ruleDotID ) ) (otherlv_1= ':' ( (lv_compass_pt_2_0= RULE_ID ) ) )? ) ;
    public final EObject rulePort() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token lv_compass_pt_2_0=null;
        AntlrDatatypeRuleToken lv_name_0_0 = null;


         enterRule(); 
            
        try {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:904:28: ( ( ( (lv_name_0_0= ruleDotID ) ) (otherlv_1= ':' ( (lv_compass_pt_2_0= RULE_ID ) ) )? ) )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:905:1: ( ( (lv_name_0_0= ruleDotID ) ) (otherlv_1= ':' ( (lv_compass_pt_2_0= RULE_ID ) ) )? )
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:905:1: ( ( (lv_name_0_0= ruleDotID ) ) (otherlv_1= ':' ( (lv_compass_pt_2_0= RULE_ID ) ) )? )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:905:2: ( (lv_name_0_0= ruleDotID ) ) (otherlv_1= ':' ( (lv_compass_pt_2_0= RULE_ID ) ) )?
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:905:2: ( (lv_name_0_0= ruleDotID ) )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:906:1: (lv_name_0_0= ruleDotID )
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:906:1: (lv_name_0_0= ruleDotID )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:907:3: lv_name_0_0= ruleDotID
            {
             
            	        newCompositeNode(grammarAccess.getPortAccess().getNameDotIDParserRuleCall_0_0()); 
            	    
            pushFollow(FOLLOW_ruleDotID_in_rulePort1883);
            lv_name_0_0=ruleDotID();

            state._fsp--;


            	        if (current==null) {
            	            current = createModelElementForParent(grammarAccess.getPortRule());
            	        }
                   		set(
                   			current, 
                   			"name",
                    		lv_name_0_0, 
                    		"DotID");
            	        afterParserOrEnumRuleCall();
            	    

            }


            }

            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:923:2: (otherlv_1= ':' ( (lv_compass_pt_2_0= RULE_ID ) ) )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==21) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:923:4: otherlv_1= ':' ( (lv_compass_pt_2_0= RULE_ID ) )
                    {
                    otherlv_1=(Token)match(input,21,FOLLOW_21_in_rulePort1896); 

                        	newLeafNode(otherlv_1, grammarAccess.getPortAccess().getColonKeyword_1_0());
                        
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:927:1: ( (lv_compass_pt_2_0= RULE_ID ) )
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:928:1: (lv_compass_pt_2_0= RULE_ID )
                    {
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:928:1: (lv_compass_pt_2_0= RULE_ID )
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:929:3: lv_compass_pt_2_0= RULE_ID
                    {
                    lv_compass_pt_2_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_rulePort1913); 

                    			newLeafNode(lv_compass_pt_2_0, grammarAccess.getPortAccess().getCompass_ptIDTerminalRuleCall_1_1_0()); 
                    		

                    	        if (current==null) {
                    	            current = createModelElement(grammarAccess.getPortRule());
                    	        }
                           		setWithLastConsumed(
                           			current, 
                           			"compass_pt",
                            		lv_compass_pt_2_0, 
                            		"ID");
                    	    

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
    // $ANTLR end "rulePort"


    // $ANTLR start "entryRuleDotID"
    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:953:1: entryRuleDotID returns [String current=null] : iv_ruleDotID= ruleDotID EOF ;
    public final String entryRuleDotID() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleDotID = null;


        try {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:954:2: (iv_ruleDotID= ruleDotID EOF )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:955:2: iv_ruleDotID= ruleDotID EOF
            {
             newCompositeNode(grammarAccess.getDotIDRule()); 
            pushFollow(FOLLOW_ruleDotID_in_entryRuleDotID1957);
            iv_ruleDotID=ruleDotID();

            state._fsp--;

             current =iv_ruleDotID.getText(); 
            match(input,EOF,FOLLOW_EOF_in_entryRuleDotID1968); 

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
    // $ANTLR end "entryRuleDotID"


    // $ANTLR start "ruleDotID"
    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:962:1: ruleDotID returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_ID_0= RULE_ID | this_INT_1= RULE_INT | this_FLOAT_2= RULE_FLOAT | this_STRING_3= RULE_STRING ) ;
    public final AntlrDatatypeRuleToken ruleDotID() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_ID_0=null;
        Token this_INT_1=null;
        Token this_FLOAT_2=null;
        Token this_STRING_3=null;

         enterRule(); 
            
        try {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:965:28: ( (this_ID_0= RULE_ID | this_INT_1= RULE_INT | this_FLOAT_2= RULE_FLOAT | this_STRING_3= RULE_STRING ) )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:966:1: (this_ID_0= RULE_ID | this_INT_1= RULE_INT | this_FLOAT_2= RULE_FLOAT | this_STRING_3= RULE_STRING )
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:966:1: (this_ID_0= RULE_ID | this_INT_1= RULE_INT | this_FLOAT_2= RULE_FLOAT | this_STRING_3= RULE_STRING )
            int alt26=4;
            switch ( input.LA(1) ) {
            case RULE_ID:
                {
                alt26=1;
                }
                break;
            case RULE_INT:
                {
                alt26=2;
                }
                break;
            case RULE_FLOAT:
                {
                alt26=3;
                }
                break;
            case RULE_STRING:
                {
                alt26=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 26, 0, input);

                throw nvae;
            }

            switch (alt26) {
                case 1 :
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:966:6: this_ID_0= RULE_ID
                    {
                    this_ID_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleDotID2008); 

                    		current.merge(this_ID_0);
                        
                     
                        newLeafNode(this_ID_0, grammarAccess.getDotIDAccess().getIDTerminalRuleCall_0()); 
                        

                    }
                    break;
                case 2 :
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:974:10: this_INT_1= RULE_INT
                    {
                    this_INT_1=(Token)match(input,RULE_INT,FOLLOW_RULE_INT_in_ruleDotID2034); 

                    		current.merge(this_INT_1);
                        
                     
                        newLeafNode(this_INT_1, grammarAccess.getDotIDAccess().getINTTerminalRuleCall_1()); 
                        

                    }
                    break;
                case 3 :
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:982:10: this_FLOAT_2= RULE_FLOAT
                    {
                    this_FLOAT_2=(Token)match(input,RULE_FLOAT,FOLLOW_RULE_FLOAT_in_ruleDotID2060); 

                    		current.merge(this_FLOAT_2);
                        
                     
                        newLeafNode(this_FLOAT_2, grammarAccess.getDotIDAccess().getFLOATTerminalRuleCall_2()); 
                        

                    }
                    break;
                case 4 :
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:990:10: this_STRING_3= RULE_STRING
                    {
                    this_STRING_3=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleDotID2086); 

                    		current.merge(this_STRING_3);
                        
                     
                        newLeafNode(this_STRING_3, grammarAccess.getDotIDAccess().getSTRINGTerminalRuleCall_3()); 
                        

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
    // $ANTLR end "ruleDotID"


    // $ANTLR start "ruleEdgeOperator"
    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:1005:1: ruleEdgeOperator returns [Enumerator current=null] : ( (enumLiteral_0= '->' ) | (enumLiteral_1= '--' ) ) ;
    public final Enumerator ruleEdgeOperator() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;

         enterRule(); 
        try {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:1007:28: ( ( (enumLiteral_0= '->' ) | (enumLiteral_1= '--' ) ) )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:1008:1: ( (enumLiteral_0= '->' ) | (enumLiteral_1= '--' ) )
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:1008:1: ( (enumLiteral_0= '->' ) | (enumLiteral_1= '--' ) )
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==23) ) {
                alt27=1;
            }
            else if ( (LA27_0==24) ) {
                alt27=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 27, 0, input);

                throw nvae;
            }
            switch (alt27) {
                case 1 :
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:1008:2: (enumLiteral_0= '->' )
                    {
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:1008:2: (enumLiteral_0= '->' )
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:1008:4: enumLiteral_0= '->'
                    {
                    enumLiteral_0=(Token)match(input,23,FOLLOW_23_in_ruleEdgeOperator2145); 

                            current = grammarAccess.getEdgeOperatorAccess().getDirectedEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getEdgeOperatorAccess().getDirectedEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:1014:6: (enumLiteral_1= '--' )
                    {
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:1014:6: (enumLiteral_1= '--' )
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:1014:8: enumLiteral_1= '--'
                    {
                    enumLiteral_1=(Token)match(input,24,FOLLOW_24_in_ruleEdgeOperator2162); 

                            current = grammarAccess.getEdgeOperatorAccess().getUndirectedEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getEdgeOperatorAccess().getUndirectedEnumLiteralDeclaration_1()); 
                        

                    }


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
    // $ANTLR end "ruleEdgeOperator"


    // $ANTLR start "ruleGraphType"
    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:1024:1: ruleGraphType returns [Enumerator current=null] : ( (enumLiteral_0= 'graph' ) | (enumLiteral_1= 'digraph' ) ) ;
    public final Enumerator ruleGraphType() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;

         enterRule(); 
        try {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:1026:28: ( ( (enumLiteral_0= 'graph' ) | (enumLiteral_1= 'digraph' ) ) )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:1027:1: ( (enumLiteral_0= 'graph' ) | (enumLiteral_1= 'digraph' ) )
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:1027:1: ( (enumLiteral_0= 'graph' ) | (enumLiteral_1= 'digraph' ) )
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==25) ) {
                alt28=1;
            }
            else if ( (LA28_0==26) ) {
                alt28=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 28, 0, input);

                throw nvae;
            }
            switch (alt28) {
                case 1 :
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:1027:2: (enumLiteral_0= 'graph' )
                    {
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:1027:2: (enumLiteral_0= 'graph' )
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:1027:4: enumLiteral_0= 'graph'
                    {
                    enumLiteral_0=(Token)match(input,25,FOLLOW_25_in_ruleGraphType2207); 

                            current = grammarAccess.getGraphTypeAccess().getGraphEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getGraphTypeAccess().getGraphEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:1033:6: (enumLiteral_1= 'digraph' )
                    {
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:1033:6: (enumLiteral_1= 'digraph' )
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:1033:8: enumLiteral_1= 'digraph'
                    {
                    enumLiteral_1=(Token)match(input,26,FOLLOW_26_in_ruleGraphType2224); 

                            current = grammarAccess.getGraphTypeAccess().getDigraphEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getGraphTypeAccess().getDigraphEnumLiteralDeclaration_1()); 
                        

                    }


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
    // $ANTLR end "ruleGraphType"


    // $ANTLR start "ruleAttributeType"
    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:1043:1: ruleAttributeType returns [Enumerator current=null] : ( (enumLiteral_0= 'graph' ) | (enumLiteral_1= 'node' ) | (enumLiteral_2= 'edge' ) ) ;
    public final Enumerator ruleAttributeType() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;

         enterRule(); 
        try {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:1045:28: ( ( (enumLiteral_0= 'graph' ) | (enumLiteral_1= 'node' ) | (enumLiteral_2= 'edge' ) ) )
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:1046:1: ( (enumLiteral_0= 'graph' ) | (enumLiteral_1= 'node' ) | (enumLiteral_2= 'edge' ) )
            {
            // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:1046:1: ( (enumLiteral_0= 'graph' ) | (enumLiteral_1= 'node' ) | (enumLiteral_2= 'edge' ) )
            int alt29=3;
            switch ( input.LA(1) ) {
            case 25:
                {
                alt29=1;
                }
                break;
            case 27:
                {
                alt29=2;
                }
                break;
            case 28:
                {
                alt29=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 29, 0, input);

                throw nvae;
            }

            switch (alt29) {
                case 1 :
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:1046:2: (enumLiteral_0= 'graph' )
                    {
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:1046:2: (enumLiteral_0= 'graph' )
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:1046:4: enumLiteral_0= 'graph'
                    {
                    enumLiteral_0=(Token)match(input,25,FOLLOW_25_in_ruleAttributeType2269); 

                            current = grammarAccess.getAttributeTypeAccess().getGraphEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_0, grammarAccess.getAttributeTypeAccess().getGraphEnumLiteralDeclaration_0()); 
                        

                    }


                    }
                    break;
                case 2 :
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:1052:6: (enumLiteral_1= 'node' )
                    {
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:1052:6: (enumLiteral_1= 'node' )
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:1052:8: enumLiteral_1= 'node'
                    {
                    enumLiteral_1=(Token)match(input,27,FOLLOW_27_in_ruleAttributeType2286); 

                            current = grammarAccess.getAttributeTypeAccess().getNodeEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_1, grammarAccess.getAttributeTypeAccess().getNodeEnumLiteralDeclaration_1()); 
                        

                    }


                    }
                    break;
                case 3 :
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:1058:6: (enumLiteral_2= 'edge' )
                    {
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:1058:6: (enumLiteral_2= 'edge' )
                    // ../org.eclipse.elk.alg.graphviz.dot/src-gen/org/eclipse/elk/alg/graphviz/dot/parser/antlr/internal/InternalGraphvizDot.g:1058:8: enumLiteral_2= 'edge'
                    {
                    enumLiteral_2=(Token)match(input,28,FOLLOW_28_in_ruleAttributeType2303); 

                            current = grammarAccess.getAttributeTypeAccess().getEdgeEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                            newLeafNode(enumLiteral_2, grammarAccess.getAttributeTypeAccess().getEdgeEnumLiteralDeclaration_2()); 
                        

                    }


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
    // $ANTLR end "ruleAttributeType"

    // Delegated rules


    protected DFA5 dfa5 = new DFA5(this);
    static final String DFA5_eotS =
        "\21\uffff";
    static final String DFA5_eofS =
        "\1\uffff\4\12\6\uffff\4\12\1\uffff\1\12";
    static final String DFA5_minS =
        "\5\4\3\uffff\1\4\2\uffff\6\4";
    static final String DFA5_maxS =
        "\5\34\3\uffff\1\7\2\uffff\4\34\1\4\1\34";
    static final String DFA5_acceptS =
        "\5\uffff\1\3\1\5\1\4\1\uffff\1\2\1\1\6\uffff";
    static final String DFA5_specialS =
        "\21\uffff}>";
    static final String[] DFA5_transitionS = {
            "\1\1\1\2\1\3\1\4\6\uffff\1\6\7\uffff\1\6\2\uffff\1\5\1\uffff\2\5",
            "\4\12\6\uffff\3\12\1\7\1\12\2\uffff\1\10\1\12\2\11\1\12\1\uffff\2\12",
            "\4\12\6\uffff\3\12\1\7\1\12\2\uffff\1\10\1\12\2\11\1\12\1\uffff\2\12",
            "\4\12\6\uffff\3\12\1\7\1\12\2\uffff\1\10\1\12\2\11\1\12\1\uffff\2\12",
            "\4\12\6\uffff\3\12\1\7\1\12\2\uffff\1\10\1\12\2\11\1\12\1\uffff\2\12",
            "",
            "",
            "",
            "\1\13\1\14\1\15\1\16",
            "",
            "",
            "\4\12\6\uffff\3\12\1\uffff\1\12\2\uffff\1\17\1\12\2\11\1\12\1\uffff\2\12",
            "\4\12\6\uffff\3\12\1\uffff\1\12\2\uffff\1\17\1\12\2\11\1\12\1\uffff\2\12",
            "\4\12\6\uffff\3\12\1\uffff\1\12\2\uffff\1\17\1\12\2\11\1\12\1\uffff\2\12",
            "\4\12\6\uffff\3\12\1\uffff\1\12\2\uffff\1\17\1\12\2\11\1\12\1\uffff\2\12",
            "\1\20",
            "\4\12\6\uffff\3\12\1\uffff\1\12\3\uffff\1\12\2\11\1\12\1\uffff\2\12"
    };

    static final short[] DFA5_eot = DFA.unpackEncodedString(DFA5_eotS);
    static final short[] DFA5_eof = DFA.unpackEncodedString(DFA5_eofS);
    static final char[] DFA5_min = DFA.unpackEncodedStringToUnsignedChars(DFA5_minS);
    static final char[] DFA5_max = DFA.unpackEncodedStringToUnsignedChars(DFA5_maxS);
    static final short[] DFA5_accept = DFA.unpackEncodedString(DFA5_acceptS);
    static final short[] DFA5_special = DFA.unpackEncodedString(DFA5_specialS);
    static final short[][] DFA5_transition;

    static {
        int numStates = DFA5_transitionS.length;
        DFA5_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA5_transition[i] = DFA.unpackEncodedString(DFA5_transitionS[i]);
        }
    }

    class DFA5 extends DFA {

        public DFA5(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 5;
            this.eot = DFA5_eot;
            this.eof = DFA5_eof;
            this.min = DFA5_min;
            this.max = DFA5_max;
            this.accept = DFA5_accept;
            this.special = DFA5_special;
            this.transition = DFA5_transition;
        }
        public String getDescription() {
            return "218:2: (this_NodeStatement_0= ruleNodeStatement | this_EdgeStatement_1= ruleEdgeStatement | this_AttributeStatement_2= ruleAttributeStatement | this_Attribute_3= ruleAttribute | this_Subgraph_4= ruleSubgraph )";
        }
    }
 

    public static final BitSet FOLLOW_ruleGraphvizModel_in_entryRuleGraphvizModel75 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleGraphvizModel85 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleGraph_in_ruleGraphvizModel130 = new BitSet(new long[]{0x0000000006002002L});
    public static final BitSet FOLLOW_ruleGraph_in_entryRuleGraph166 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleGraph176 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_13_in_ruleGraph219 = new BitSet(new long[]{0x00000000060060F0L});
    public static final BitSet FOLLOW_ruleGraphType_in_ruleGraph254 = new BitSet(new long[]{0x00000000000040F0L});
    public static final BitSet FOLLOW_ruleDotID_in_ruleGraph275 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_14_in_ruleGraph288 = new BitSet(new long[]{0x000000001A40C0F0L});
    public static final BitSet FOLLOW_ruleStatement_in_ruleGraph309 = new BitSet(new long[]{0x000000001A40C0F0L});
    public static final BitSet FOLLOW_15_in_ruleGraph322 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleStatement_in_entryRuleStatement358 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleStatement368 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNodeStatement_in_ruleStatement416 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_ruleEdgeStatement_in_ruleStatement443 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_ruleAttributeStatement_in_ruleStatement470 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_ruleAttribute_in_ruleStatement497 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_ruleSubgraph_in_ruleStatement524 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_16_in_ruleStatement537 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttribute_in_entryRuleAttribute575 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttribute585 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDotID_in_ruleAttribute631 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_17_in_ruleAttribute643 = new BitSet(new long[]{0x00000000000000F0L});
    public static final BitSet FOLLOW_ruleDotID_in_ruleAttribute664 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNodeStatement_in_entryRuleNodeStatement700 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleNodeStatement710 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNode_in_ruleNodeStatement756 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_18_in_ruleNodeStatement769 = new BitSet(new long[]{0x00000000001000F0L});
    public static final BitSet FOLLOW_ruleListAttribute_in_ruleNodeStatement791 = new BitSet(new long[]{0x00000000001800F0L});
    public static final BitSet FOLLOW_19_in_ruleNodeStatement805 = new BitSet(new long[]{0x00000000000000F0L});
    public static final BitSet FOLLOW_ruleListAttribute_in_ruleNodeStatement828 = new BitSet(new long[]{0x00000000001800F0L});
    public static final BitSet FOLLOW_20_in_ruleNodeStatement844 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNode_in_entryRuleNode882 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleNode892 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDotID_in_ruleNode938 = new BitSet(new long[]{0x0000000000200002L});
    public static final BitSet FOLLOW_21_in_ruleNode951 = new BitSet(new long[]{0x00000000000000F0L});
    public static final BitSet FOLLOW_rulePort_in_ruleNode972 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeStatement_in_entryRuleEdgeStatement1010 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleEdgeStatement1020 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNode_in_ruleEdgeStatement1066 = new BitSet(new long[]{0x0000000001800000L});
    public static final BitSet FOLLOW_ruleEdgeTarget_in_ruleEdgeStatement1087 = new BitSet(new long[]{0x0000000001840002L});
    public static final BitSet FOLLOW_18_in_ruleEdgeStatement1101 = new BitSet(new long[]{0x00000000001000F0L});
    public static final BitSet FOLLOW_ruleListAttribute_in_ruleEdgeStatement1123 = new BitSet(new long[]{0x00000000001800F0L});
    public static final BitSet FOLLOW_19_in_ruleEdgeStatement1137 = new BitSet(new long[]{0x00000000000000F0L});
    public static final BitSet FOLLOW_ruleListAttribute_in_ruleEdgeStatement1160 = new BitSet(new long[]{0x00000000001800F0L});
    public static final BitSet FOLLOW_20_in_ruleEdgeStatement1176 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeTarget_in_entryRuleEdgeTarget1214 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleEdgeTarget1224 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEdgeOperator_in_ruleEdgeTarget1270 = new BitSet(new long[]{0x000000001A4040F0L});
    public static final BitSet FOLLOW_ruleSubgraph_in_ruleEdgeTarget1292 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNode_in_ruleEdgeTarget1319 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeStatement_in_entryRuleAttributeStatement1356 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAttributeStatement1366 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAttributeType_in_ruleAttributeStatement1412 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_ruleAttributeStatement1424 = new BitSet(new long[]{0x00000000001000F0L});
    public static final BitSet FOLLOW_ruleListAttribute_in_ruleAttributeStatement1446 = new BitSet(new long[]{0x00000000001800F0L});
    public static final BitSet FOLLOW_19_in_ruleAttributeStatement1460 = new BitSet(new long[]{0x00000000000000F0L});
    public static final BitSet FOLLOW_ruleListAttribute_in_ruleAttributeStatement1483 = new BitSet(new long[]{0x00000000001800F0L});
    public static final BitSet FOLLOW_20_in_ruleAttributeStatement1499 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleSubgraph_in_entryRuleSubgraph1535 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleSubgraph1545 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_22_in_ruleSubgraph1592 = new BitSet(new long[]{0x0000000000004010L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleSubgraph1609 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_14_in_ruleSubgraph1629 = new BitSet(new long[]{0x000000001A40C0F0L});
    public static final BitSet FOLLOW_ruleStatement_in_ruleSubgraph1650 = new BitSet(new long[]{0x000000001A40C0F0L});
    public static final BitSet FOLLOW_15_in_ruleSubgraph1663 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleListAttribute_in_entryRuleListAttribute1699 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleListAttribute1709 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDotID_in_ruleListAttribute1755 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_17_in_ruleListAttribute1768 = new BitSet(new long[]{0x00000000000000F0L});
    public static final BitSet FOLLOW_ruleDotID_in_ruleListAttribute1789 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePort_in_entryRulePort1827 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRulePort1837 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDotID_in_rulePort1883 = new BitSet(new long[]{0x0000000000200002L});
    public static final BitSet FOLLOW_21_in_rulePort1896 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_ID_in_rulePort1913 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDotID_in_entryRuleDotID1957 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDotID1968 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleDotID2008 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_INT_in_ruleDotID2034 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_FLOAT_in_ruleDotID2060 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleDotID2086 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_ruleEdgeOperator2145 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_24_in_ruleEdgeOperator2162 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_ruleGraphType2207 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_ruleGraphType2224 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_ruleAttributeType2269 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_ruleAttributeType2286 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_ruleAttributeType2303 = new BitSet(new long[]{0x0000000000000002L});

}