package be.i8c.codequality.sonar.plugins.sag.webmethods.flow.visitors;

import java.util.List;

import org.sonar.squidbridge.SquidAstVisitor;
import org.sonar.squidbridge.measures.MetricDef;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;

import be.i8c.codequality.sonar.plugins.sag.webmethods.flow.sslr.FlowGrammar;
import be.i8c.codequality.sonar.plugins.sag.webmethods.flow.sslr.FlowLexer.FlowTypes;

public class CommentLinesVisitor<GRAMMAR extends Grammar> extends SquidAstVisitor<GRAMMAR> {
	private final MetricDef metric;

	  public CommentLinesVisitor(MetricDef metric) {
	    this.metric = metric;
	  }

	  @Override
	  public void init() {
	    subscribeTo(FlowGrammar.COMMENT);
	  }

	  @Override
	  public void visitNode(AstNode astNode) {
		    AstNode content=getContent(astNode);
		    if (content!=null && content.getTokenValue()!=null && content.getTokenValue().trim().length()>0) {
		    	getContext().peekSourceCode().add(metric, 1);
		    }
	  }
	  
		public AstNode getContent(AstNode sequenceNode) {
			if (sequenceNode != null) {
				return sequenceNode.getFirstChild(FlowTypes.ELEMENT_VALUE);
			}
			return null;
		}
}
