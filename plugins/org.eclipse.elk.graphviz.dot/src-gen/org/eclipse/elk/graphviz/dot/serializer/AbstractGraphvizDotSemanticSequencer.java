package org.eclipse.elk.graphviz.dot.serializer;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.eclipse.elk.graphviz.dot.dot.Attribute;
import org.eclipse.elk.graphviz.dot.dot.AttributeStatement;
import org.eclipse.elk.graphviz.dot.dot.DotPackage;
import org.eclipse.elk.graphviz.dot.dot.EdgeStatement;
import org.eclipse.elk.graphviz.dot.dot.EdgeTarget;
import org.eclipse.elk.graphviz.dot.dot.Graph;
import org.eclipse.elk.graphviz.dot.dot.GraphvizModel;
import org.eclipse.elk.graphviz.dot.dot.Node;
import org.eclipse.elk.graphviz.dot.dot.NodeStatement;
import org.eclipse.elk.graphviz.dot.dot.Port;
import org.eclipse.elk.graphviz.dot.dot.Subgraph;
import org.eclipse.elk.graphviz.dot.services.GraphvizDotGrammarAccess;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.serializer.acceptor.ISemanticSequenceAcceptor;
import org.eclipse.xtext.serializer.acceptor.SequenceFeeder;
import org.eclipse.xtext.serializer.diagnostic.ISemanticSequencerDiagnosticProvider;
import org.eclipse.xtext.serializer.diagnostic.ISerializationDiagnostic.Acceptor;
import org.eclipse.xtext.serializer.sequencer.AbstractDelegatingSemanticSequencer;
import org.eclipse.xtext.serializer.sequencer.GenericSequencer;
import org.eclipse.xtext.serializer.sequencer.ISemanticNodeProvider.INodesForEObjectProvider;
import org.eclipse.xtext.serializer.sequencer.ISemanticSequencer;
import org.eclipse.xtext.serializer.sequencer.ITransientValueService;
import org.eclipse.xtext.serializer.sequencer.ITransientValueService.ValueTransient;

@SuppressWarnings("all")
public abstract class AbstractGraphvizDotSemanticSequencer extends AbstractDelegatingSemanticSequencer {

	@Inject
	private GraphvizDotGrammarAccess grammarAccess;
	
	public void createSequence(EObject context, EObject semanticObject) {
		if(semanticObject.eClass().getEPackage() == DotPackage.eINSTANCE) switch(semanticObject.eClass().getClassifierID()) {
			case DotPackage.ATTRIBUTE:
				if(context == grammarAccess.getAttributeRule() ||
				   context == grammarAccess.getStatementRule()) {
					sequence_Attribute(context, (Attribute) semanticObject); 
					return; 
				}
				else if(context == grammarAccess.getListAttributeRule()) {
					sequence_ListAttribute(context, (Attribute) semanticObject); 
					return; 
				}
				else break;
			case DotPackage.ATTRIBUTE_STATEMENT:
				if(context == grammarAccess.getAttributeStatementRule() ||
				   context == grammarAccess.getStatementRule()) {
					sequence_AttributeStatement(context, (AttributeStatement) semanticObject); 
					return; 
				}
				else break;
			case DotPackage.EDGE_STATEMENT:
				if(context == grammarAccess.getEdgeStatementRule() ||
				   context == grammarAccess.getStatementRule()) {
					sequence_EdgeStatement(context, (EdgeStatement) semanticObject); 
					return; 
				}
				else break;
			case DotPackage.EDGE_TARGET:
				if(context == grammarAccess.getEdgeTargetRule()) {
					sequence_EdgeTarget(context, (EdgeTarget) semanticObject); 
					return; 
				}
				else break;
			case DotPackage.GRAPH:
				if(context == grammarAccess.getGraphRule()) {
					sequence_Graph(context, (Graph) semanticObject); 
					return; 
				}
				else break;
			case DotPackage.GRAPHVIZ_MODEL:
				if(context == grammarAccess.getGraphvizModelRule()) {
					sequence_GraphvizModel(context, (GraphvizModel) semanticObject); 
					return; 
				}
				else break;
			case DotPackage.NODE:
				if(context == grammarAccess.getNodeRule()) {
					sequence_Node(context, (Node) semanticObject); 
					return; 
				}
				else break;
			case DotPackage.NODE_STATEMENT:
				if(context == grammarAccess.getNodeStatementRule() ||
				   context == grammarAccess.getStatementRule()) {
					sequence_NodeStatement(context, (NodeStatement) semanticObject); 
					return; 
				}
				else break;
			case DotPackage.PORT:
				if(context == grammarAccess.getPortRule()) {
					sequence_Port(context, (Port) semanticObject); 
					return; 
				}
				else break;
			case DotPackage.SUBGRAPH:
				if(context == grammarAccess.getStatementRule() ||
				   context == grammarAccess.getSubgraphRule()) {
					sequence_Subgraph(context, (Subgraph) semanticObject); 
					return; 
				}
				else break;
			}
		if (errorAcceptor != null) errorAcceptor.accept(diagnosticProvider.createInvalidContextOrTypeDiagnostic(semanticObject, context));
	}
	
	/**
	 * Constraint:
	 *     (type=AttributeType (attributes+=ListAttribute attributes+=ListAttribute*)?)
	 */
	protected void sequence_AttributeStatement(EObject context, AttributeStatement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name=DotID value=DotID)
	 */
	protected void sequence_Attribute(EObject context, Attribute semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, DotPackage.Literals.ATTRIBUTE__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, DotPackage.Literals.ATTRIBUTE__NAME));
			if(transientValues.isValueTransient(semanticObject, DotPackage.Literals.ATTRIBUTE__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, DotPackage.Literals.ATTRIBUTE__VALUE));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getAttributeAccess().getNameDotIDParserRuleCall_0_0(), semanticObject.getName());
		feeder.accept(grammarAccess.getAttributeAccess().getValueDotIDParserRuleCall_2_0(), semanticObject.getValue());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (sourceNode=Node edgeTargets+=EdgeTarget+ (attributes+=ListAttribute attributes+=ListAttribute*)?)
	 */
	protected void sequence_EdgeStatement(EObject context, EdgeStatement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (operator=EdgeOperator (targetSubgraph=Subgraph | targetnode=Node))
	 */
	protected void sequence_EdgeTarget(EObject context, EdgeTarget semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (strict?='strict'? type=GraphType name=DotID? statements+=Statement*)
	 */
	protected void sequence_Graph(EObject context, Graph semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     graphs+=Graph*
	 */
	protected void sequence_GraphvizModel(EObject context, GraphvizModel semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name=DotID value=DotID?)
	 */
	protected void sequence_ListAttribute(EObject context, Attribute semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (node=Node (attributes+=ListAttribute attributes+=ListAttribute*)?)
	 */
	protected void sequence_NodeStatement(EObject context, NodeStatement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name=DotID port=Port?)
	 */
	protected void sequence_Node(EObject context, Node semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name=DotID compass_pt=ID?)
	 */
	protected void sequence_Port(EObject context, Port semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name=ID? statements+=Statement*)
	 */
	protected void sequence_Subgraph(EObject context, Subgraph semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
}
