/*
 * Copyright 2017 Credit Mutuel Arkea
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package com.arkea.devops.sonar.xml.checks;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.sonar.plugins.xml.checks.EmptyStepCheck;
import org.sonar.plugins.xml.checks.XmlIssue;
import org.sonar.plugins.xml.checks.XmlSourceCode;

public class EmptyStepCheckTest extends AbstractCheckTester {

	private List<XmlIssue> getIssues(String content) throws IOException {
		XmlSourceCode sourceCode = parseAndCheck(createTempFile(content), new EmptyStepCheck());
		return sourceCode.getXmlIssues();
	}
	
	@Test
	public void test_ok() throws Exception {
		List<XmlIssue> issues = getIssues("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n" + 
				"<ProxyEndpoint name=\"default\">\r\n" + 
				"    <PreFlow name=\"PreFlow\">\r\n" + 
				"        <Request>\r\n" + 
				"            <Step>\r\n" + 
				"                <Name>Verify-API-Key-1</Name>\r\n" + 
				"                <Condition>request.verb != \"OPTIONS\"</Condition>\r\n" + 
				"            </Step>\r\n" + 
				"        </Request>\r\n" + 
				"        <Response/>\r\n" + 
				"    </PreFlow>\r\n" + 
				"    <Flows>\r\n" + 
				"        <Flow name=\"GET_check\">\r\n" + 
				"            <Condition>(proxy.pathsuffix MatchesPath \"/check\") and (request.verb = \"GET\")</Condition>\r\n" + 
				"            <Description>Check</Description>\r\n" + 
				"   	     <Request>\r\n" + 
				"           	 <Step>\r\n" + 
				"     	           <Name>Verify-API-Key-1</Name>\r\n" + 
				"      	          <Condition>request.verb != \"OPTIONS\"</Condition>\r\n" + 
				"      	      </Step>\r\n" + 
				"       	 </Request>\r\n" + 
				"            <Response></Response>\r\n" + 
				"        </Flow>\r\n" + 
				"    </Flows>\r\n" + 
				"</ProxyEndpoint>"

		);
		assertEquals(0, issues.size());
	}
	
	@Test
	public void test_ko1() throws Exception {
		List<XmlIssue> issues = getIssues("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n" + 
				"<ProxyEndpoint name=\"default\">\r\n" + 
				"    <PreFlow name=\"PreFlow\">\r\n" + 
				"        <Request>\r\n" + 
				"            <Step>\r\n" + 
				"                <Name>Verify-API-Key-1</Name>\r\n" + 
				"                <Condition>request.verb != \"OPTIONS\"</Condition>\r\n" + 
				"            </Step>\r\n" + 
				"            <Step/>\r\n" + 
				"        </Request>\r\n" + 
				"        <Response/>\r\n" + 
				"    </PreFlow>\r\n" + 
				"    <Flows>\r\n" + 
				"        <Flow name=\"GET_check\">\r\n" + 
				"            <Condition>(proxy.pathsuffix MatchesPath \"/check\") and (request.verb = \"GET\")</Condition>\r\n" + 
				"            <Description>Check</Description>\r\n" + 
				"   	     <Request>\r\n" + 
				"           	 <Step>\r\n" + 
				"     	           <Name>Verify-API-Key-1</Name>\r\n" + 
				"      	          <Condition>request.verb != \"OPTIONS\"</Condition>\r\n" + 
				"      	      </Step>\r\n" + 
				"       	 </Request>\r\n" + 
				"            <Response></Response>\r\n" + 
				"        </Flow>\r\n" + 
				"    </Flows>\r\n" + 
				"</ProxyEndpoint>"

		);
		assertEquals(1, issues.size());
	}	
	
	
	
	@Test
	public void test_ko3() throws Exception {
		List<XmlIssue> issues = getIssues("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n" + 
				"<ProxyEndpoint name=\"default\">\r\n" + 
				"    <PreFlow name=\"PreFlow\">\r\n" + 
				"        <Request>\r\n" + 
				"            <Step attr=\"1\">\r\n" + 
				"                <Name>Verify-API-Key-1</Name>\r\n" + 
				"                <Condition>request.verb != \"OPTIONS\"</Condition>\r\n" + 
				"            </Step>\r\n" + 
				"            <Step/>\r\n" + 
				"        </Request>\r\n" + 
				"        <Response/>\r\n" + 
				"    </PreFlow>\r\n" + 
				"    <Flows>\r\n" + 
				"        <Flow name=\"GET_check\">\r\n" + 
				"            <Condition>(proxy.pathsuffix MatchesPath \"/check\") and (request.verb = \"GET\")</Condition>\r\n" + 
				"            <Description>Check</Description>\r\n" + 
				"   	     <Request>\r\n" + 
				"           	 <Step>\r\n" + 
				"     	           <Name>Verify-API-Key-1</Name>\r\n" + 
				"      	          <Condition>request.verb != \"OPTIONS\"</Condition>\r\n" + 
				"      	      	</Step>\r\n" + 
				"            	<Step attr=\"2\">\r\n" + 
				"            	</Step>\r\n" + 
				"            	<Step attr=\"3\">\r\n" + 
				"            	<!-- comment -->\r\n" + 
				"            	</Step>\r\n" + 
				"       	 </Request>\r\n" + 
				"            <Response></Response>\r\n" + 
				"        </Flow>\r\n" + 
				"    </Flows>\r\n" + 
				"</ProxyEndpoint>"

		);
		assertEquals(3, issues.size());
	}		

}