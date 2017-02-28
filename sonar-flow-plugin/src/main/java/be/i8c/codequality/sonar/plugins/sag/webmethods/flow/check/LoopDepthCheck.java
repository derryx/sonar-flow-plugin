package be.i8c.codequality.sonar.plugins.sag.webmethods.flow.check;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;
import org.sonar.squidbridge.checks.SquidCheck;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;

import be.i8c.codequality.sonar.plugins.sag.webmethods.flow.sslr.FlowGrammar;

@Rule(key="S00015",name = "Checks how many nested LOOPs a flow service has", 
priority = Priority.MINOR, tags = {Tags.BAD_PRACTICE,Tags.BRAIN_OVERLOAD,Tags.PERFORMANCE})
@ActivatedByDefault
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.LOGIC_CHANGEABILITY)
@SqaleConstantRemediation("60min")
public class LoopDepthCheck extends SquidCheck<Grammar> {	
	final static Logger logger = LoggerFactory.getLogger(LoopDepthCheck.class);
	
	private static final String DEFAULT_NESTING="2";
	
	@RuleProperty(
			key = "flow.loop.nesting",
			description = "How many nested LOOP steps are allowed",
			defaultValue = "" + DEFAULT_NESTING)
	private String nestingAllowed;
	private int nestingAllowedInt;
	
	@Override
	public void init() {
		logger.debug("++ Initializing ", this.getClass().getName() + " ++");
		if (nestingAllowed==null) {
			nestingAllowed=DEFAULT_NESTING;
		}
		nestingAllowedInt=Integer.parseInt(nestingAllowed);
		
		subscribeTo(FlowGrammar.LOOP);
	}
	
	@Override
	public void visitNode(AstNode astNode) {
		int branchLevel=1+calcLoopLevel(astNode);
		
		if (branchLevel>nestingAllowedInt) {
			getContext().createLineViolation(this, "Too many LOOP levels ("+branchLevel+")", astNode);
		}
	}

	private int calcLoopLevel(AstNode astNode) {
		List<AstNode> children=getContent(astNode)==null?new ArrayList<AstNode>():getContent(astNode).getChildren();
		int result=0;
		for (AstNode child : children) {
			int newResult=0;
			// recurse through all children
			if (child.getType().equals(FlowGrammar.LOOP)) {
				// child is a LOOP -> add 1 to level
				newResult=1+calcLoopLevel(child);
			} else {
				// child is something else -> no additional penalty
				newResult=calcLoopLevel(child);
			}
			
			// save only the highest "badness"
			if (newResult>result) {
				result=newResult;
			}
		}
		return result;
	}

	public AstNode getContent(AstNode sequenceNode) {
		if (sequenceNode != null) {
			return sequenceNode.getFirstChild(FlowGrammar.CONTENT);
		}
		return null;
	}
}
