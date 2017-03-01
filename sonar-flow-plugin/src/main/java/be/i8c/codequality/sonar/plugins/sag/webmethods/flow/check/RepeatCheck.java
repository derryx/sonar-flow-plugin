/*
 * i8c
 * Copyright (C) 2016 i8c NV
 * mailto:contact AT i8c DOT be
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package be.i8c.codequality.sonar.plugins.sag.webmethods.flow.check;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;
import org.sonar.squidbridge.checks.SquidCheck;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;

import be.i8c.codequality.sonar.plugins.sag.webmethods.flow.sslr.FlowGrammar;
import be.i8c.codequality.sonar.plugins.sag.webmethods.flow.sslr.FlowLexer.FlowAttTypes;

@Rule(key="S00006",name = "In the REPEAT step, the \"Count\" property must be defined", 
priority = Priority.MAJOR, tags = {Tags.BUG})
@ActivatedByDefault
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.LOGIC_RELIABILITY)
@SqaleConstantRemediation("2min")
public class RepeatCheck extends SquidCheck<Grammar>{

	final static Logger logger = LoggerFactory.getLogger(RepeatCheck.class);
	
	@Override
	public void init() {
		logger.debug("++ Initializing {} ++", this.getClass().getName());
		subscribeTo(FlowGrammar.RETRY);
	}

	@Override
	public void visitNode(AstNode astNode) {
		AstNode repeatNode = astNode.getFirstChild(FlowGrammar.ATTRIBUTES);
		if (repeatNode != null){
			logger.debug("++ Repeat interface element found. ++");
			String count = getCount(repeatNode);
			if (count == null || count.trim().equals("")) {
				logger.debug("++ \"Count\" property found to be empty! ++");
				getContext().createLineViolation(this, "The \"Count\" "
				+ "property must be defined for the step type 'REPEAT'", repeatNode);
			}
		}
	}

	private String getCount(AstNode repeatNode) {
		if (repeatNode != null) {
			AstNode countAtt = repeatNode.getFirstChild(FlowAttTypes.COUNT);
			if (countAtt != null) {
				String countType = countAtt.getToken().getOriginalValue();
				logger.debug("++ Count field found! ++");
				if ( countType != null) {
					return countType;
				}
			}
		}
		return null;
	}
}