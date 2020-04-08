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
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.sonar.check.Rule;
import org.sonarsource.analyzer.commons.xml.XmlFile;
import org.sonarsource.analyzer.commons.xml.checks.SonarXmlCheck;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Definition : Empty steps clutter a bundle. Performance is not degraded.
 * Codes : ST001
 * @author Nicolas Tisserand
 */
@Rule(key = "EmptyStepCheck")
public class EmptyStepCheck extends SonarXmlCheck {

	@Override
	public void scanFile(XmlFile xmlFile) {
		
	    Document document = xmlFile.getDocument();
	    
	    XPathFactory xPathfactory = XPathFactory.newInstance();
	    XPath xpath = xPathfactory.newXPath();
	    
	    try {
	    	// Select in one shot the Step nodes which are empty
		    XPathExpression exprDisplayName = xpath.compile("//*[name() = 'ProxyEndpoint' or name() = 'SharedFlow']//Step[not(normalize-space())]");
		    NodeList descriptionNodeList = (NodeList)exprDisplayName.evaluate(document, XPathConstants.NODESET);

	    	if(descriptionNodeList!=null) {
	    		for(int i=0 ; i < descriptionNodeList.getLength(); i++) {
	    	    	Node stepNode = descriptionNodeList.item(i);
    				reportIssue(stepNode, "Empty steps clutter a bundle. Performance is not degraded.");
	    		}
	    	}
		} catch (XPathExpressionException e) {
			// Nothing to do
		}

	}

}
