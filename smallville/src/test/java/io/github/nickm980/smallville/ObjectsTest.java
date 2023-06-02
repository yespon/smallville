package io.github.nickm980.smallville;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.nickm980.smallville.entities.ObjectState;
import io.github.nickm980.smallville.entities.SimulatedLocation;
import io.github.nickm980.smallville.entities.SimulatedObject;

public class ObjectsTest {
    
    @Test
    public void testObjectLoads() {
	World world = new World();
	world.create(new SimulatedLocation("Forest"));
	world
	    .create(new SimulatedObject("Campfire", new ObjectState("on", List.of()),
		    world.getLocation("Forest").orElseThrow()));

	assertTrue(world.getObjectByName("Campfire").getState().equals("on"), "Object state was not loaded");
    }

    @Test
    public void testObjectStateWillChange() {
	World world = new World();
	world.create(new SimulatedLocation("Forest"));
	world
	    .create(new SimulatedObject("Campfire", new ObjectState("on", List.of()),
		    world.getLocation("Forest").orElseThrow()));
	world.setState("Campfire", "off");

	assertTrue(world.getObjectByName("Campfire").getState().equals("off"), "Object state was not changed");
    }
}
