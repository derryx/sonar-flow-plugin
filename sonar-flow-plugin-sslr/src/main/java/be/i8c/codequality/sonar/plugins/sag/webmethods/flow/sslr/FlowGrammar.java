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
package be.i8c.codequality.sonar.plugins.sag.webmethods.flow.sslr;

import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.grammar.LexerfulGrammarBuilder;

import com.sonar.sslr.api.Grammar;

import static be.i8c.codequality.sonar.plugins.sag.webmethods.flow.sslr.FlowLexer.FlowAttTypes.*;
import static be.i8c.codequality.sonar.plugins.sag.webmethods.flow.sslr.FlowLexer.FlowTypes.*;
import static com.sonar.sslr.api.GenericTokenType.*;

public enum FlowGrammar implements GrammarRuleKey{
	
	FLOW,
	SEQUENCE,
	INVOKE,
	MAP,
	LOOP,
	BRANCH,
	EXIT,
	RETRY,
	
	CONTENT,
	COMMENT,
	
	MAPSOURCE,
	MAPTARGET,
	MAPDELETE,
	MAPPING,
	MAPSET,
	MAPCOPY,
	MAPINVOKE,
	
	DATA,
	VALUES,
	RECORD,
	VALUE,
	NUMBER,
	ARRAY,
	
	UNDEF_ATT,
	ATTRIBUTES;
	
	public static Grammar create() {
		
	    LexerfulGrammarBuilder b = LexerfulGrammarBuilder.create();
	    
	    b.rule(FLOW).is(START_FLOW, ATTRIBUTES, b.optional(COMMENT), CONTENT, STOP_FLOW, EOF);
	    b.rule(SEQUENCE).is(START_SEQUENCE,ATTRIBUTES,b.optional(COMMENT), CONTENT, STOP_SEQUENCE);
	    b.rule(INVOKE).is(START_INVOKE,ATTRIBUTES, b.optional(COMMENT), b.zeroOrMore(MAP), STOP_INVOKE);
	    b.rule(BRANCH).is(START_BRANCH,ATTRIBUTES, b.optional(COMMENT), CONTENT, STOP_BRANCH);
	    b.rule(MAP).is(START_MAP,ATTRIBUTES, b.optional(COMMENT), MAPPING , STOP_MAP);
	    b.rule(LOOP).is(START_LOOP,ATTRIBUTES, b.optional(COMMENT), CONTENT, STOP_LOOP);
	    b.rule(EXIT).is(START_EXIT,ATTRIBUTES, b.optional(COMMENT), STOP_EXIT);
	    b.rule(RETRY).is(START_RETRY,ATTRIBUTES, b.optional(COMMENT), CONTENT, STOP_RETRY);
	    
	    
	    b.rule(COMMENT).is(START_COMMENT, b.zeroOrMore(ELEMENT_VALUE), STOP_COMMENT);
	    b.rule(CONTENT).is(b.zeroOrMore(b.firstOf(SEQUENCE,INVOKE,MAP, BRANCH, LOOP, EXIT, RETRY)));
	    
	    b.rule(MAPPING).is(b.optional(MAPTARGET),b.optional(MAPSOURCE), b.zeroOrMore(b.firstOf(MAPINVOKE, MAPDELETE,MAPSET,MAPCOPY)));
	    b.rule(MAPTARGET).is(START_MAPTARGET,VALUES,STOP_MAPTARGET);
	    b.rule(MAPSOURCE).is(START_MAPSOURCE,VALUES,STOP_MAPSOURCE);
	    b.rule(MAPDELETE).is(START_MAPDELETE,ATTRIBUTES,b.optional(COMMENT),STOP_MAPDELETE);
	    b.rule(MAPSET).is(START_MAPSET,ATTRIBUTES,b.optional(COMMENT),b.optional(DATA),STOP_MAPSET);
	    b.rule(MAPCOPY).is(START_MAPCOPY,ATTRIBUTES,b.optional(COMMENT), STOP_MAPCOPY);
	    b.rule(MAPINVOKE).is(START_MAPINVOKE,ATTRIBUTES,b.optional(COMMENT), b.oneOrMore(MAP), STOP_MAPINVOKE);
	    
	    b.rule(DATA).is(START_DATA,ATTRIBUTES,b.optional(VALUES),STOP_DATA);
	    b.rule(VALUES).is(START_VALUES,ATTRIBUTES,b.zeroOrMore(b.firstOf(VALUE,ARRAY,RECORD,NUMBER)),STOP_VALUES);
	    b.rule(RECORD).is(START_RECORD,ATTRIBUTES,b.zeroOrMore(b.firstOf(VALUE,ARRAY,RECORD,NUMBER)),STOP_RECORD);
	    b.rule(VALUE).is(START_VALUE,ATTRIBUTES,b.zeroOrMore(ELEMENT_VALUE),STOP_VALUE);
	    b.rule(NUMBER).is(START_NUMBER,ATTRIBUTES,b.zeroOrMore(ELEMENT_VALUE),STOP_NUMBER);
	    b.rule(ARRAY).is(START_ARRAY,ATTRIBUTES,b.zeroOrMore(b.firstOf(VALUE,ARRAY,RECORD)),STOP_ARRAY);
	    
	    b.rule(UNDEF_ATT).is(b.sequence(IDENTIFIER, LITERAL));
	    b.rule(ATTRIBUTES).is(b.zeroOrMore(b.firstOf(SERVICE,EXITON,MODE,NAME,DISABLED,FROM,FAILUREMESSAGE,SIGNAL,SWITCH,LABELEXPRESSIONS,UNDEF_ATT,COUNT,BACKOFF,LOOPON,TIMEOUT)));

	    b.setRootRule(FLOW);
	    
	    return b.build();
	}
}
