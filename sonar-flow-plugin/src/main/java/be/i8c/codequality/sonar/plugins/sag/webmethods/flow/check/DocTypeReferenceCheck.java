package be.i8c.codequality.sonar.plugins.sag.webmethods.flow.check;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

import com.sonar.sslr.api.AstNode;

import be.i8c.codequality.sonar.plugins.sag.webmethods.flow.check.type.NodeCheck;
import be.i8c.codequality.sonar.plugins.sag.webmethods.flow.sslr.NodeGrammar;
import be.i8c.codequality.sonar.plugins.sag.webmethods.flow.sslr.FlowLexer.FlowTypes;

@Rule(key="S00018",name = "Checks if nested documents are used instead of doctype references", 
priority = Priority.MINOR, tags = {Tags.BAD_PRACTICE})
@ActivatedByDefault
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.LOGIC_CHANGEABILITY)
@SqaleConstantRemediation("10min")
public class DocTypeReferenceCheck extends NodeCheck {
	final static Logger logger = LoggerFactory.getLogger(DocTypeReferenceCheck.class);
	
	@Override
	public void init() {
		logger.debug("++ Initializing " + this.getClass().getName() + " ++");
		
		subscribeTo(NodeGrammar.RECORD);
	}
	
	@Override
	public void visitNode(AstNode astNode) {
		// We are only interested in Record definitions
		if (!"RECORD".equalsIgnoreCase(getNodeType(astNode))) {
			return;
		}
		
		// We are only interested in document definitions
		if (!"RECORD".equalsIgnoreCase(getFieldType(astNode))) {
			return;
		}
		
		if (getFieldName(astNode)!=null && getFieldReference(astNode)==null) {
			getContext().createLineViolation(this, "Field \"" + getFieldName(astNode) + "\" should be a doctype reference", astNode);
		}
	}
	
	private String getValue(AstNode v, String valueName) {
		for (AstNode n : v.getChildren(NodeGrammar.VALUE)) {
			for (AstNode attr : n.getChildren(NodeGrammar.ATTRIBUTES)) {
				if (attr.getTokenValue().equalsIgnoreCase(valueName)) {
					AstNode value=n.getFirstChild(FlowTypes.ELEMENT_VALUE);
					if (value!=null) {
						return value.getTokenValue().trim();
					}
				}
			}
		}
		
		return null;
	}
	
	private String getNodeType(AstNode n) {
		return getValue(n,"NODE_TYPE");
	}
	
	private String getName(AstNode n) {
		return getValue(n,"NODE_NSNAME");
	}
	
	private String getFieldName(AstNode n) {
		return getValue(n,"FIELD_NAME");
	}
	
	private String getFieldType(AstNode n) {
		return getValue(n, "FIELD_TYPE");
	}
	
	private String getFieldReference(AstNode n) {
		return getValue(n, "REC_REF");
	}
	
	private String getRecordName(AstNode n) {
		return n.getFirstChild(NodeGrammar.ATTRIBUTES).getTokenValue().trim();
	}
}
