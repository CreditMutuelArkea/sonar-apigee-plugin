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
package com.arkea.satd.sonar.checks;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Test;
import org.sonar.api.batch.sensor.issue.Issue;
import org.sonarsource.analyzer.commons.xml.checks.SonarXmlCheck;

import com.arkea.satd.sonar.xml.checks.ConditionLengthCheck;

public class ConditionLengthCheckTest extends AbstractCheckTester {
	
	private SonarXmlCheck check = new ConditionLengthCheck();
	
	@Test
	public void test_ok() throws Exception {
		
		Collection<Issue> issues = getIssues(check, 
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n" + 
				"<ProxyEndpoint name=\"default\">\r\n" + 
				"    <Description/>\r\n" + 
				"    <PreFlow name=\"PreFlow\">\r\n" + 
				"        <Request>\r\n" + 
				"            <Step>\r\n" + 
				"                <Name>VA-Verify-API-Key-1</Name>\r\n" + 
				"                <Condition>request.verb != \"OPTIONS\"</Condition>\r\n" + 
				"            </Step>\r\n" + 
				"        </Request>\r\n" + 
				"        <Response/>\r\n" + 
				"    </PreFlow>\r\n" + 
				"</ProxyEndpoint>"
			);
		
		assertEquals(0, issues.size());
	}	
	
	@Test
	public void test_too_long() throws Exception {
		
		Collection<Issue> issues = getIssues(check,
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n" + 
			"<ProxyEndpoint name=\"default\">\r\n" + 
			"    <Description/>\r\n" + 
			"    <PreFlow name=\"PreFlow\">\r\n" + 
			"        <Request>\r\n" + 
			"            <Step>\r\n" + 
			"                <Name>validate-content-type</Name>\r\n" + 
			"                <!-- condition with exactly 256 chars -->\r\n" + 
			"                <Condition>(request.header.Content-Type Matches \"application/json*\" and ((request.verb = \"PUT\") or (request.verb = \"POST\") or (request.verb = \"PATCH\")) and (request.header.Content-Length != 0)) or (some other complicated expression with a lot of terms here and more).</Condition>\r\n" +		
			"            </Step>\r\n" + 
			"            <Step>\r\n" + 
			"                <Name>JSON-Threat-Protection-1</Name>\r\n" + 
			"                <!-- condition with more than 256 chars -->\r\n" + 
			"                <Condition>(request.header.Content-Type Matches \"application/json*\" and ((request.verb = \"PUT\") or (request.verb = \"POST\") or (request.verb = \"PATCH\")) and (request.header.Content-Length != 0)) or (some other complicated expression with a lot of terms here and more and more).</Condition>\r\n" +		
			"            </Step>\r\n" + 
			"        </Request>\r\n" + 
			"        <Response/>\r\n" + 
			"    </PreFlow>\r\n" + 
			"</ProxyEndpoint>"
		);
		assertEquals(1, issues.size());
		Issue iss = issues.toArray(new Issue[] {})[0];
		assertEquals(14, iss.primaryLocation().textRange().start().line());
	}	

	
	@Test
	public void test_too_long_2() throws Exception {
		
		Collection<Issue> issues = getIssues(check,
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n" + 
			"<ProxyEndpoint name=\"default\">\r\n" + 
			"    <Description/>\r\n" + 
			"    <PreFlow name=\"PreFlow\">\r\n" + 
			"        <Request>\r\n" + 
			"            <Step>\r\n" + 
			"                <Name>validate-content-type</Name>\r\n" + 
			"                <!-- condition with more than 256 chars -->\r\n" +
			"                <Condition>(request.header.Content-Type Matches \"application/json*\" and ((request.verb = \"PUT\") or (request.verb = \"POST\") or (request.verb = \"PATCH\")) and (request.header.Content-Length != 0)) or (some other complicated expression with a lot of terms here and more and more).</Condition>\r\n" +		
			"            </Step>\r\n" + 
			"            <Step>\r\n" + 
			"                <Name>JSON-Threat-Protection-1</Name>\r\n" + 
			"                <!-- second condition with more than 256 chars -->\r\n" + 
			"                <Condition>(request.header.Content-Type Matches \"application/json*\" and ((request.verb = \"PUT\") or (request.verb = \"POST\") or (request.verb = \"PATCH\")) and (request.header.Content-Length != 0)) or (some other complicated expression with a lot of terms here and more and more).</Condition>\r\n" +		
			"            </Step>\r\n" + 
			"        </Request>\r\n" + 
			"        <Response/>\r\n" + 
			"    </PreFlow>\r\n" + 
			"</ProxyEndpoint>"
		);
		assertEquals(2, issues.size());		
	}		
	
}