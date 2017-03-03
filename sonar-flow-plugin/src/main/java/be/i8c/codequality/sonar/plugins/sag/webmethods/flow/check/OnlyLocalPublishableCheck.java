package be.i8c.codequality.sonar.plugins.sag.webmethods.flow.check;

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

@Rule(key = "S00019", name = "Publishable DocTypes should not be only publishable locally", priority = Priority.MAJOR, 
    tags = {Tags.BUG,Tags.BAD_PRACTICE })
@ActivatedByDefault
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.ARCHITECTURE_RELIABILITY)
@SqaleConstantRemediation("1min")
public class OnlyLocalPublishableCheck extends NodeCheck {
	final static Logger logger = LoggerFactory.getLogger(OnlyLocalPublishableCheck.class);
	
	@Override
	public void init() {
		logger.debug("++ Initializing " + this.getClass().getName() + " ++");
		
		subscribeTo(NodeGrammar.VALUE);
	}
	
	@Override
	public void visitNode(AstNode astNode) {
		for (AstNode attr : astNode.getChildren(NodeGrammar.ATTRIBUTES)) {
			if (attr.getTokenValue().equalsIgnoreCase("BROKEREVENTTYPENAME")) {
				AstNode value=astNode.getFirstChild(FlowTypes.ELEMENT_VALUE);
				if (value!=null && value.getTokenValue()!=null && value.getTokenValue().contains("wm::offline")) {
					getContext().createLineViolation(this, "DocType is only publishable locally", astNode);
				}
			}
		}
	}
}
