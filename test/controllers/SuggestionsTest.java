package controllers;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.GET;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.route;
import static play.test.Helpers.running;

import org.junit.Test;

import modules.IndexTest;
import play.Application;
import play.libs.Json;
import play.mvc.Result;

public class SuggestionsTest extends IndexTest {

	@Test
	public void suggestionsWithoutCallback() {
		Application application = fakeApplication();
		running(application, () -> {
			Result result = route(application, fakeRequest(GET,
					"/search?q=*&filter=type:Person&format=json:preferredName,professionOrOccupation"));
			assertNotNull("We have a result", result);
			assertThat(result.contentType().get(), is(equalTo("application/json")));
			String content = contentAsString(result);
			assertNotNull("We can parse the result as JSON", Json.parse(content));
			assertThat(content,
					allOf(//
							containsString("label"), //
							containsString("id"), //
							containsString("category"), //
							containsString("image")));
			assertTrue("We used both given fields for any of the labels",
					Json.parse(content).findValues("label").stream().anyMatch(label -> label.asText().contains(" | ")));
		});

	}

	@Test
	public void suggestionsWithCallback() {
		Application application = fakeApplication();
		running(application, () -> {
			Result result = route(application,
					fakeRequest(GET, "/search?q=*&filter=type:Person&format=json:preferredName&callback=test"));
			assertNotNull("We have a result", result);
			assertThat(result.contentType().get(), is(equalTo("application/javascript")));
			assertThat(contentAsString(result), allOf(containsString("test("), // callback
					containsString("label"), containsString("id"), containsString("category")));
		});
	}

	@Test
	public void suggestionsCorsHeader() {
		Application application = fakeApplication();
		running(application, () -> {
			Result result = route(application, fakeRequest(GET, "/search?q=*&format=json:preferredName"));
			assertNotNull("We have a result", result);
			assertThat(result.header("Access-Control-Allow-Origin").get(), is(equalTo("*")));
		});

	}

	@Test
	public void suggestionsTemplate() {
		Application application = fakeApplication();
		running(application, () -> {
			String format = "json:preferredName,*_dateOfBirth+in_placeOfBirth";
			Result result = route(application,
					fakeRequest(GET, "/search?q=*&filter=type:Person&format=" + format));
			assertNotNull("We have a result", result);
			assertThat(result.contentType().get(), is(equalTo("application/json")));
			String content = contentAsString(result);
			assertNotNull("We can parse the result as JSON", Json.parse(content));
			assertTrue("We replaced the field names in the template with their values",
					Json.parse(content).findValues("label").stream()
							.anyMatch(label -> label.asText().contains("* 1923 in ")));
		});
	}

	@Test
	public void suggestionsTemplateMultiValues() {
		Application application = fakeApplication();
		running(application, () -> {
			String format = "json:preferredName,gndSubjectCategory,aka_variantName";
			Result result = route(application,
					fakeRequest(GET, "/search?q=Weizenbaum&filter=type:Person&format=" + format));
			assertNotNull("We have a result", result);
			assertThat(result.contentType().get(), is(equalTo("application/json")));
			String content = contentAsString(result);
			assertNotNull("We can parse the result as JSON", Json.parse(content));
			assertThat("Multi-values use consistent delimiter", content,
					allOf(
						containsString("Informatik, Datenverarbeitung; Mathematik"),
						containsString("aka Weizenbaum, Josef; Weizenbaum, J.")));
		});
	}

	@Test
	public void suggestionsArePrettyPrinted() {
		Application application = fakeApplication();
		running(application, () -> {
			Result result = route(application,
					fakeRequest(GET, "/search?q=*&format=json:suggest"));
			assertNotNull(result);
			assertThat(result.contentType().get(), is(equalTo("application/json")));
			assertThat(contentAsString(result), containsString("}, {\n"));
		});
	}

}
