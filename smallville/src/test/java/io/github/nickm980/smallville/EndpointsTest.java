package io.github.nickm980.smallville;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.github.nickm980.smallville.analytics.Analytics;
import io.github.nickm980.smallville.api.SmallvilleServer;
import io.github.nickm980.smallville.llm.ChatGPT;
import io.javalin.Javalin;
import io.javalin.community.routing.annotations.Get;
import io.javalin.testtools.JavalinTest;
import okhttp3.Response;

public class EndpointsTest {
    private Javalin app;

    @BeforeEach
    public void setUp() {
	ChatGPT llm = Mockito.mock(ChatGPT.class);
	Mockito.when(llm.sendChat(Mockito.any(), Mockito.anyInt())).thenReturn("result");
	app = new SmallvilleServer(new Analytics(), llm, new World()).server();
    }

    @Test
    public void GET_endpoint_not_found() {
	JavalinTest.test(app, (server, client) -> {
	    assertEquals(client.get("/thisendpointdoesnotexist").code(), 404);
	});
    }

    @Test
    public void GET_to_ping_returns_pong() {
	JavalinTest.test(app, (server, client) -> {
	    Response response = client.get("/ping");
	    JSONObject body = new JSONObject(response.body().string());

	    assertEquals("could not ping the server", body.get("ping"), "pong");
	    assertEquals(body.get("success"), true);
	});
    }

    @Test
    public void POST_to_memory_stream_saves_memory() {
	// /memories/stream/{uuid}
	JavalinTest.test(app, (server, client) -> {
	    Response response = client.post("/memories/stream");
	    JSONObject body = new JSONObject(response.body().string());

	    assertEquals(200, response.code());
	    assertEquals(true, body.get("success"));
	});
    }

    @Test
    public void GET_info_returns_analytics_and_simulation_information() {
	// GET /info
	JavalinTest.test(app, (server, client) -> {
	    Response response = client.get("/info");
	    JSONObject body = new JSONObject(response.body().string());

	    assertEquals(response.code(), 200);
	    assertEquals(body.get("step"), 1);
	    assertNotNull(body.get("step"));
	    assertNotNull(body.get("locationVisits"));
	    assertNotNull(body.get("prompts"));
	});
    }

    @Test
    public void GET_agents_returns_list_of_agents() {
	// GET /agents
	JavalinTest.test(app, (server, client) -> {
	    Response response = client.get("/agents");
	    JSONObject body = new JSONObject(response.body().string());

	    assertEquals(response.code(), 200);
	    assertNotNull(body.get("agents"));
	});
    }

    @Test
    @Get("/agents/{name}")
    public void GET_agent_by_name_returns_successfully() {
	// GET /agents/{name}
	JavalinTest.test(app, (server, client) -> {
	    Response response = client.get("/agents/nonexistant");

	    assertEquals(500, response.code());
	});
    }

    @Test
    public void POST_to_locations_creates_new_location() {
	// POST /locations
	JavalinTest.test(app, (server, client) -> {
	    Response response = client.post("/locations", Map.of("name", "Red House: Kitchen"));
	    JSONObject body = new JSONObject(response.body().string());

	    assertEquals(response.code(), 200);
	    assertEquals(body.get("success"), true);
	});
    }

    @Test
    public void GET_locations_returns_list_of_locations() {
	// GET /locations
	JavalinTest.test(app, (server, client) -> {
	    Response response = client.get("/locations");
	    JSONObject body = new JSONObject(response.body().string());

	    assertEquals(response.code(), 200);
	});
    }

    @Test
    public void GET_to_state_returns_current_state() {
	// GET /state
	JavalinTest.test(app, (server, client) -> {
	    Response response = client.get("/state");
	    JSONObject body = new JSONObject(response.body().string());

	    assertEquals(response.code(), 200);
	    assertNotNull(body.get("location_states"));
	    assertNotNull(body.get("agents"));
	    assertNotNull(body.get("conversations"));
	});
    }
}
