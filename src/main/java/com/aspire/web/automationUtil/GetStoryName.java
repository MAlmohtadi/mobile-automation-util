package com.aspire.web.automationUtil;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.model.GivenStories;
import org.jbehave.core.model.Lifecycle;
import org.jbehave.core.model.Meta;
import org.jbehave.core.model.Narrative;
import org.jbehave.core.model.OutcomesTable;
import org.jbehave.core.model.Scenario;
import org.jbehave.core.model.Story;
import org.jbehave.core.model.StoryDuration;
import org.jbehave.core.reporters.FilePrintStreamFactory;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporter;
import org.jbehave.core.reporters.StoryReporterBuilder;

public class GetStoryName implements StoryReporter {
	
	protected  StringBuilder stepsBuilder;
	protected static String currentScenario;


	private static GetStoryName singleton = new GetStoryName();

	public final static Format GetStoryName = new Format("Scenario Name") {
		@Override
		public StoryReporter createStoryReporter(
				FilePrintStreamFactory factory,
				StoryReporterBuilder storyReporterBuilder) {
			return singleton;
		}
	};

	private GetStoryName() {
			stepsBuilder = new StringBuilder();
	}

	public static GetStoryName getInstance() {
		return singleton;
	}

	/// StoryReporter implementation

	public void storyNotAllowed(Story story, String filter) {

	}

	public void storyCancelled(Story story, StoryDuration storyDuration) {

	}

	public void beforeStory(Story story, boolean givenStory) {

	}

	public void afterStory(boolean givenStory) {
	}

	public void narrative(Narrative narrative) {
	}

	public void lifecyle(Lifecycle lifecycle) {
	}

	public void scenarioNotAllowed(Scenario scenario, String filter) {
	}
	
	public void beforeScenario(String scenarioTitle) {
		currentScenario = StringEscapeUtils.escapeHtml4(scenarioTitle);
		stepsBuilder.setLength(0);
	}
	public static String GetSenarioName ()
	{
		
		return currentScenario;
	}
	
	public void scenarioMeta(Meta meta) {

	}

	public void afterScenario() {
	}

	public void givenStories(GivenStories givenStories) {
	}

	public void givenStories(List<String> storyPaths) {
	}

	public void beforeExamples(List<String> steps, ExamplesTable table) {
	}
	
	public void afterExamples() {

	}

	public void successful(String step) {

	}

	public void ignorable(String step) {

	}

	public void pending(String step) {

	}

	public void notPerformed(String step) {
	}

	public void failed(String step, Throwable cause) {
	}

	public void failedOutcomes(String step, OutcomesTable table) {

	}

	public void restarted(String step, Throwable cause) {

	}

	public void dryRun() {

	}

	public void pendingMethods(List<String> methods) {

	}

	public void example(Map<String, String> tableRow) {
		// TODO Auto-generated method stub
	}

	public void beforeStep(String step) {
		// TODO Auto-generated method stub
	}

	//@Override
	public void restartedStory(Story story, Throwable cause) {
		// TODO Auto-generated method stub
		
	}

}