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
package com.arkea.satd.sonar.xml.checks;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.sonar.check.Rule;
import org.sonarsource.analyzer.commons.xml.XmlFile;
import org.sonarsource.analyzer.commons.xml.checks.SonarXmlCheck;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


/**
 * Definition : Reserved words as variables - ServiceCallout Response
 * Code : PO020
 * @author Nicolas Tisserand
 */
@Rule(key = "ServiceCalloutResponseVariableNameCheck")
public class ServiceCalloutResponseVariableNameCheck extends SonarXmlCheck {

	@Override
	public void scanFile(XmlFile xmlFile) {
		
	    Document document = xmlFile.getDocument();
	    if (document.getDocumentElement() != null) {
    	
		    XPathFactory xPathfactory = XPathFactory.newInstance();
		    XPath xpath = xPathfactory.newXPath();
		    
		    try {
		    	// Select in one shot the Response which have variable equals to 'response'
			    Node responseNode = (Node)xpath.evaluate("/ServiceCallout/Response[text() = 'response']", document, XPathConstants.NODE);
		    	if(responseNode!=null) {
		    		reportIssue(responseNode, "Using response for the Response name causes unexepected side effects.");
		    	}
			    
			} catch (XPathExpressionException e) {
				// Nothing to do
			}
	    }
		    
	}

}
