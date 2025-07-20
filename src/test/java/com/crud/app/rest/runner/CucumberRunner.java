package com.crud.app.rest.runner;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.core.options.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features") // Points to src/test/resources/features
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.crud.app.rest.steps") // Points to your step definition package
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty") // For pretty console output
public class CucumberRunner {
}