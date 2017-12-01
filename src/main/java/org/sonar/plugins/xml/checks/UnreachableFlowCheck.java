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
package org.sonar.plugins.xml.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.xml.checks.XmlSourceCode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Flow without a condition should be last.
 * Code : FL501
 * @author Nicolas Tisserand
 */
@Rule(key = "UnreachableFlowCheck")
public class UnreachableFlowCheck extends AbstractXmlCheck {

	@Override
	public void validate(XmlSourceCode xmlSourceCode) {
	    setWebSourceCode(xmlSourceCode);

	    Document document = getWebSourceCode().getDocument(false);
	    if (document.getDocumentElement() != null) {

	    	// Search for last Flow of an ProxyEndpoint document
	    	if("ProxyEndpoint".equals(document.getDocumentElement().getNodeName())) {

		    	NodeList flowNodeList = document.getDocumentElement().getElementsByTagName("Flow");
		    	
		    	if(flowNodeList!=null) {
		    		for(int i=0; i<flowNodeList.getLength(); i++) {
		    			Node flowNode = flowNodeList.item(i);

		    			// Search Condition value
		    			NodeList flowChilds = flowNode.getChildNodes();
		    			boolean hasNoCondition = true;
		    			for(int j = 0; j < flowChilds.getLength() && hasNoCondition; j++) {
		    				Node currentChild = flowChilds.item(j);
		    				if("Condition".equals(currentChild.getNodeName())) {
		    					String cond = currentChild.getTextContent();
		    					if(!"true".equalsIgnoreCase(cond) && cond.length()>0) {
		    						hasNoCondition = false;
		    					}
		    				}
		    			}
		    			
	    				// Create a violation if flow node is not the last one
		    			if(hasNoCondition && i < flowNodeList.getLength()-1) {
			    			createViolation(getWebSourceCode().getLineForNode(flowNode), "Flow without a condition should be last.");
		    			}
		    		}
		    	}
	    	}
	    }
	}

}
