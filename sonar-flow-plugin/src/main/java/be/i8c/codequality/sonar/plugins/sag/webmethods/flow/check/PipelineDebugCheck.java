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
import be.i8c.codequality.sonar.plugins.sag.webmethods.flow.sslr.FlowLexer.FlowTypes;
import be.i8c.codequality.sonar.plugins.sag.webmethods.flow.sslr.NodeGrammar;

@Rule(key = "S00010", name = "Property \"Pipeline debug\" should be set to \"None\"", priority = Priority.MAJOR, tags = {
		Tags.DEBUG_CODE, Tags.BAD_PRACTICE })
@ActivatedByDefault
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.LOGIC_RELIABILITY)
@SqaleConstantRemediation("2min")
public class PipelineDebugCheck extends NodeCheck {
	final static Logger logger = LoggerFactory.getLogger(PipelineDebugCheck.class);
	
	@Override
	public void init() {
		logger.debug("++ Initializing " + this.getClass().getName() + " ++");
		subscribeTo(NodeGrammar.VALUE);
	}
	
	@Override
	public void visitNode(AstNode astNode) {
		for(AstNode attr:astNode.getChildren(NodeGrammar.ATTRIBUTES)){
			if("PIPELINE_OPTION".equalsIgnoreCase(attr.getTokenValue())) {
				logger.debug("++ Pipeline option found ++");
				List<AstNode> pipOptions=astNode.getChildren(FlowTypes.ELEMENT_VALUE);
				for (AstNode pip : pipOptions) {
					// 0=None,1=Save,2=Restore(Override),3=Restore(Merge)
					// so everything else than 0 has a bad taste
					if(pip.getTokenValue()!=null && !pip.getTokenValue().contains("0")) {
						logger.debug("++ Pipeline option VIOLATION found ++");
						getContext().createLineViolation(this, "Set property \"Pipeline debug\" to \"None\"", astNode);
					}					
				}
			}
		}
	}	
}
