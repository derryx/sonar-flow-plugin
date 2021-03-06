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

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class CheckList {
	final static Logger logger = LoggerFactory.getLogger(CheckList.class);
	
	public static final String REPOSITORY_KEY = "flow";

	public static final String I8C_PROFILE = "i8c Quality Profile";

	private CheckList() {
	}

	public static List<Class> getChecks() {
		Builder<Class> builder = new ImmutableList.Builder<Class>();
		builder.addAll(getNodeChecks());
		builder.addAll(getTopLevelChecks());
		builder.addAll(getOtherChecks());
		
		if (logger.isDebugEnabled()) {
			logger.debug("getChecks(): classes="+toString(builder.build()));
		}
		
		return builder.build();
	}

	/**
	 * @param ignoreTopLevel
	 * @param getNodeRules
	 * @return list of checks
	 */
	public static List<Class> getChecks(boolean ignoreTopLevel, boolean getNodeRules) {
		Builder<Class> builder = new ImmutableList.Builder<Class>();
		if (getNodeRules)
			builder.addAll(getNodeChecks());
		else {
			if (!ignoreTopLevel)
				builder.addAll(getTopLevelChecks());
			builder.addAll(getOtherChecks());
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("getChecks(boolean ignoreTopLevel, boolean getNodeRules): ignoreTopLevel="+ignoreTopLevel+", getNodeRules="+getNodeRules+" classes="+toString(builder.build()));
		}
		
		return builder.build();
	}

	private static List<Class> getNodeChecks() {
		return ImmutableList.<Class> of(
				InterfaceCommentsCheck.class,
				PipelineDebugCheck.class,
				DocTypeQualifiedNameCheck.class,
				DocTypeReferenceCheck.class,
				OnlyLocalPublishableCheck.class);
	}

	private static List<Class> getTopLevelChecks() {
		return ImmutableList.<Class> of(
				TryCatchCheck.class,
				TryCatchGetLastErrorCheck.class
				);
	}

	private static List<Class> getOtherChecks() {
		return ImmutableList.<Class> of(
				QualifiedNameCheck.class,
				SavePipelineCheck.class,
				DisabledCheck.class,
				ExitCheck.class,
				EmptyMapCheck.class,
				BranchPropertiesCheck.class,
				EmptyFlowCheck.class,
				ClearPipelineCheck.class,
				BranchDepthCheck.class,
				DepthCheck.class,
				LoopDepthCheck.class,
				RepeatCheck.class);
	}
	
	private static String toString(List<Class> classes) {
		return classes.stream().map(c -> c.toString()).collect(Collectors.joining(", "));
	}
}
