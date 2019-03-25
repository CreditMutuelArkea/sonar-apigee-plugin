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
package com.arkea.satd.sonar.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.internal.google.common.annotations.VisibleForTesting;
import org.sonar.api.measures.FileLinesContextFactory;
import org.sonar.api.rule.RuleKey;
import org.sonar.plugins.xml.Xml;
import org.sonar.plugins.xml.checks.BundleRecorder;
import org.sonar.plugins.xml.checks.ParsingErrorCheck;
import org.sonarsource.analyzer.commons.ProgressReport;
import org.sonarsource.analyzer.commons.xml.XmlFile;
import org.sonarsource.analyzer.commons.xml.checks.SonarXmlCheck;

import com.arkea.satd.sonar.xml.checks.CheckRepository;

/**
 * ApigeeXmlSensor provides analysis of xml files.
 * 
 * @author Matthijs Galesloot
 * @author Nicolas Tisserand
 */
public class ApigeeXmlSensor implements Sensor {

	private final Checks<Object> checks;
	private final FileSystem fileSystem;
	private final FilePredicate mainFilesPredicate;
	private final boolean parsingErrorCheckEnabled;
	private final FileLinesContextFactory fileLinesContextFactory;	
	
	private static final RuleKey PARSING_ERROR_RULE_KEY = RuleKey.of(Xml.REPOSITORY_KEY, ParsingErrorCheck.RULE_KEY);

	
	private static SensorContext staticContext;

	public static void setContext(SensorContext ctx) {
		staticContext = ctx;
	}
	
	public ApigeeXmlSensor(FileSystem fileSystem, CheckFactory checkFactory, FileLinesContextFactory fileLinesContextFactory) {		
		this.fileLinesContextFactory = fileLinesContextFactory;
		this.checks = checkFactory.create(CheckRepository.REPOSITORY_KEY).addAnnotatedChecks((Iterable<?>) CheckRepository.getCheckClasses());
		this.parsingErrorCheckEnabled = this.checks.of(PARSING_ERROR_RULE_KEY) != null;
		this.fileSystem = fileSystem;
		this.mainFilesPredicate = fileSystem.predicates().and(
		fileSystem.predicates().hasType(InputFile.Type.MAIN),
		fileSystem.predicates().hasLanguage(Xml.KEY));
	}

/*
	public void analyse(SensorContext sensorContext) {
		execute(sensorContext);
	}
*/
	

	private void runChecks(SensorContext context, XmlFile newXmlFile) {
	    checks.all().stream()
	      .map(SonarXmlCheck.class::cast)
	      // checks.ruleKey(check) is never null because "check" is part of "checks.all()"
	      .forEach(check -> runCheck(context, check, checks.ruleKey(check), newXmlFile));

	    /*
		XmlSourceCode sourceCode = new XmlSourceCode(xmlFile);

		// Do not execute any XML rule when an XML file is corrupted (SONARXML-13)
		try {
			sourceCode.parseSource();
			for (Object check : checks.all()) {
				
				RuleKey rkey = checks.ruleKey(check); 
				if(rkey != null) {
					((AbstractXmlCheck) check).setRuleKey(rkey);
					((AbstractXmlCheck) check).validate(sourceCode);
				}
			}
			saveIssue(context, sourceCode);
			*/
		/*
	      try {
	          saveSyntaxHighlighting(context, new XMLHighlighting(xmlFile).getHighlightingData(), xmlFile.getInputFile().wrapped());
	        } catch (IOException e) {
	          throw new IllegalStateException("Could not analyze file " + xmlFile.getAbsolutePath(), e);
	        }	*/		
	//	} catch(ParseException e) {
			// Do nothing
	//	}

	}
	
	@VisibleForTesting
	  void runCheck(SensorContext context, SonarXmlCheck check, RuleKey ruleKey, XmlFile newXmlFile) {
	    try {
	      check.scanFile(context, ruleKey, newXmlFile);
	    } catch (Exception e) {
	    	// Do nothing
	    }
	}	
	
	
/*
	public static void saveIssue(SensorContext context, XmlSourceCode sourceCode) {
		if(context!=null) {
			for (XmlIssue xmlIssue : sourceCode.getXmlIssues()) {
				NewIssue newIssue = context.newIssue().forRule(xmlIssue.getRuleKey());
				NewIssueLocation location = newIssue.newLocation().on(sourceCode.getInputFile().wrapped())
						.message(xmlIssue.getMessage());
				if (xmlIssue.getLine() != null) {
					location.at(sourceCode.getInputFile().selectLine(xmlIssue.getLine()));
				}
				newIssue.at(location).save();
			}
		}
	}

	
	private static void saveIssue(SensorContext context, XmlFile xmlFile) {
	
	//createParsingErrorIssue(Exception e, SensorContext context, InputFile inputFile) {
	    NewIssue newIssue = context.newIssue();

		NewIssueLocation location = newIssue.newLocation().on(xmlFile.getInputFile().wrapped())
				.message(xmlIssue.getMessage());
	    
	    
	    NewIssueLocation primaryLocation = newIssue.newLocation()
	      .message("Parse error: " + e.getMessage())
	      .on(inputFile);
	    newIssue
	      .forRule(PARSING_ERROR_RULE_KEY)
	      .at(primaryLocation)
	      .save();
	}	
	*/
	
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	@Override
	public void describe(SensorDescriptor descriptor) {
		descriptor
			.onlyOnLanguage(Xml.KEY)
			.name("Apigee XML Sensor");
	}

	
	
	@Override
	public void execute(SensorContext context) {
		
		
		// Catch the context
//		ApigeeXmlSensor.setContext(context);

	    List<InputFile> inputFiles = new ArrayList<>();
	    fileSystem.inputFiles(mainFilesPredicate).forEach(inputFiles::add);

	    if (inputFiles.isEmpty()) {
	      return;
	    }

//	    boolean isSonarLintContext = context.runtime().getProduct() == SonarProduct.SONARLINT;

	    ProgressReport progressReport = new ProgressReport("Report about progress of Apigee XML analyzer", TimeUnit.SECONDS.toMillis(10));
	    progressReport.start(inputFiles.stream().map(InputFile::toString).collect(Collectors.toList()));

	    boolean cancelled = false;
	    try {
	    	
			// First loop to store ALL files.
			for (InputFile inputFile : inputFiles) {
				XmlFile xmlFile = XmlFile.create(inputFile);
				BundleRecorder.storeFile(xmlFile);
			}
	    	
			// Second loop to checks files one by one.
	      for (InputFile inputFile : inputFiles) {
	        if (context.isCancelled()) {
	          cancelled = true;
	          break;
	        }
	        runChecks(context, XmlFile.create(inputFile));
	    //    scanFile(context, inputFile, isSonarLintContext);
	        progressReport.nextFile();
	      }
	    } catch(IOException e) {
	    } finally {
	      if (!cancelled) {
	        progressReport.stop();
	      } else {
	        progressReport.cancel();
	      }
	}		
		
		
	}


	
	
	public static SensorContext getContext() {
		return staticContext;
	}
}
