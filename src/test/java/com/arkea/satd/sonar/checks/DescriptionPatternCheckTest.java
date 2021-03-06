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

import com.arkea.satd.sonar.xml.checks.DescriptionPatternCheck;

public class DescriptionPatternCheckTest extends AbstractCheckTester {

	private DescriptionPatternCheck check = new DescriptionPatternCheck();
	
	@Test
	public void test_ok1() throws Exception {
		Collection<Issue> issues = getIssues(check,
			"<APIProxy revision=\"1\" name=\"XXX\">\r\n" + 
			"    <Description>Text of description</Description>\r\n" + 
			"</APIProxy>"
		);
		assertEquals(0, issues.size());
	}
	
	@Test
	public void test_ok2() throws Exception {
		check.setRegexPattern(".*\\(code=([A-Z0-9]{4})\\).*");
		Collection<Issue> issues = getIssues(check,
			"<APIProxy revision=\"1\" name=\"XXX\">\r\n" + 
			"    <Description>Text of description (code=AB12)</Description>\r\n" + 
			"</APIProxy>"
		);
		assertEquals(0, issues.size());
	}
	
	@Test
	public void test_ko() throws Exception {
		
		check.setRegexPattern("[a-zA-Z]*");
		
		Collection<Issue> issues = getIssues(check,
			"<APIProxy revision=\"1\" name=\"XXX\">\r\n" + 
			"    <Description>bad desc : 123456</Description>\r\n" + 
			"</APIProxy>"
		);
		assertEquals(1, issues.size());
	}	

}