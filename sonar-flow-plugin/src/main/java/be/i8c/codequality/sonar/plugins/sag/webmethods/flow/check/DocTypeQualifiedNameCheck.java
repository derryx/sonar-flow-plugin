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

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

import com.sonar.sslr.api.AstNode;

import be.i8c.codequality.sonar.plugins.sag.webmethods.flow.check.type.NodeCheck;
import be.i8c.codequality.sonar.plugins.sag.webmethods.flow.sslr.NodeGrammar;
import be.i8c.codequality.sonar.plugins.sag.webmethods.flow.sslr.FlowLexer.FlowTypes;

@Rule(key = "S00017", name = "DocType assets should follow the predefined naming convention", priority = Priority.MAJOR, tags = {
		Tags.BAD_PRACTICE })
@ActivatedByDefault
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.READABILITY)
@SqaleConstantRemediation("1min")
public class DocTypeQualifiedNameCheck extends NodeCheck {

	final static Logger logger = LoggerFactory.getLogger(DocTypeQualifiedNameCheck.class);
	
	private static final String DEFAULT_NC = "[A-Z][a-z0-9]*[A-Z0-9][a-z0-9]+[A-Z.a-z0-9]*:[A-Z]+[a-z0-9]+[A-Za-z0-9]*";
	private static final String DEFAULT_FIELD_NC = "[A-Z][a-z0-9]+[A-Za-z0-9]*";
	private static final String DEFAULT_SIG_CHECK = "true";

	@RuleProperty(
			key = "flow.doctype.naming",
			description = "Regular expression defining the naming convention of doctype assets",
			defaultValue = "" + DEFAULT_NC)
	private String namingConvention = DEFAULT_NC;

	@RuleProperty(
			key = "flow.doctype.field.naming",
			description = "Regular expression defining the naming convention of doctype assets",
			defaultValue = "" + DEFAULT_FIELD_NC)
	private String fieldNamingConvention = DEFAULT_FIELD_NC;
	
	@RuleProperty(
			key = "flow.doctype.naming.svcsig",
			description = "Switch (true/false) to enable or disable checking of service signatures",
			defaultValue = ""+DEFAULT_SIG_CHECK)
	private String enableServiceSignatureCheck;
	private Boolean enableServiceSignatureCheckBool;
	
	private Pattern p;
	private Pattern pField;
	
	@Override
	public void init() {
		logger.debug("++ Initializing " + this.getClass().getName() + " ++");
		p = Pattern.compile(namingConvention);
		pField = Pattern.compile(fieldNamingConvention);
		
		if (enableServiceSignatureCheck==null) {
			enableServiceSignatureCheck=DEFAULT_SIG_CHECK;
		}
		enableServiceSignatureCheckBool=Boolean.parseBoolean(enableServiceSignatureCheck);
		
		subscribeTo(NodeGrammar.RECORD);
	}

	@Override
	public void visitNode(AstNode astNode) {
		// is it a service signature?
		if (!enableServiceSignatureCheckBool && "SVC_SIG".equalsIgnoreCase(getRecordName(astNode))) {
			// we ignore service signatures if switch is off
			return;
		}
		
		// We are only interested in DocType definitions
		if (!"RECORD".equalsIgnoreCase(getNodeType(astNode))) {
			return;
		}
		
		// If we get a name we check it
		String docName=getName(astNode);
		if (docName!=null && !p.matcher(docName).matches()) {
			getContext().createLineViolation(this, "DocType name \"" + docName + "\" does not conform to the naming convention", astNode);
		}
		
		// If we get a field name we check it
		String fieldName=getFieldName(astNode);
		if (fieldName!=null && !pField.matcher(fieldName).matches()) {
			getContext().createLineViolation(this, "DocType field \"" + fieldName + "\" does not conform to the naming convention", astNode);
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
	
	private String getRecordName(AstNode n) {
		return n.getFirstChild(NodeGrammar.ATTRIBUTES).getTokenValue().trim();
	}
}