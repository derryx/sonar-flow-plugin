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
package be.i8c.codequality.sonar.plugins.sag.webmethods.flow.squid;

import java.io.File;
import java.util.Set;

import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.squidbridge.api.CheckMessage;
import org.sonar.squidbridge.api.SourceFile;

import be.i8c.codequality.sonar.plugins.sag.webmethods.flow.check.InterfaceCommentsCheck;
import be.i8c.codequality.sonar.plugins.sag.webmethods.flow.check.PipelineDebugCheck;
import be.i8c.codequality.sonar.plugins.sag.webmethods.flow.squid.NodeAstScanner;

public class NodeAstScannerTest {

	final static Logger logger = LoggerFactory.getLogger(NodeAstScannerTest.class);
	
	File nodeFile = new File("src/test/resources/WmPackage/ns/I8cFlowSonarPluginTest/flows/subProcess/node.ndf");
	
	@Test
	  public void scanFile() {
		logger.debug("Scanning file");
		NodeAstScanner.scanSingleFile(nodeFile);
	}
	
	@Test
	  public void pipelineDebugCheck() {
		SourceFile result = NodeAstScanner.scanSingleFile(nodeFile, new PipelineDebugCheck());
		
		Set<CheckMessage> messages = result.getCheckMessages();
		assertTrue(messages.stream().anyMatch(cm -> cm.getCheck() instanceof PipelineDebugCheck));
	}
	
	@Test
	  public void interfaceCommentsCheck() {		
		SourceFile result = NodeAstScanner.scanSingleFile( nodeFile, new InterfaceCommentsCheck());
		
		Set<CheckMessage> messages = result.getCheckMessages();
		assertTrue(messages.stream().anyMatch(cm -> cm.getCheck() instanceof InterfaceCommentsCheck));
	}
}
