package eu.fbk.iv4xr.mbt.space_engineers;




import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import environments.SeEnvironment;

import spaceEngineers.controller.ContextControllerWrapper;
import spaceEngineers.controller.ExtendedSpaceEngineers;
import spaceEngineers.controller.SpaceEngineers;
import spaceEngineers.controller.SpaceEngineersJavaProxyBuilder;
import spaceEngineers.controller.SpaceEngineersJvmExtensionsKt;
import spaceEngineers.controller.SpaceEngineersTestContext;
import spaceEngineers.iv4xr.navigation.Iv4XRAStarPathFinder;
import spaceEngineers.labrecruits.LabRecruitsController;
import spaceEngineers.labrecruits.LabRecruitsCoroutinesFreeController;
import spaceEngineers.labrecruits.SimpleLabRecruitsObservation;
import spaceEngineers.model.DoorBase;


public class NavigateLrMapTest {
	
	Long longSleepTime = 2000l;
	Long shortSleepTime = 500l;
	String worldId = "LR_random_medium";
	String agentId = SpaceEngineers.Companion.DEFAULT_AGENT_ID;

	private void sleep(long i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Disabled("Disabled for building whole project, enable manually by uncommenting.")
	@Test
	public void navigateRandomMediumTest() {
		
		// load empty SE map
		SpaceEngineersTestContext context = new SpaceEngineersTestContext();
		SpaceEngineersJavaProxyBuilder proxyBuilder = new SpaceEngineersJavaProxyBuilder();
		SpaceEngineers se = proxyBuilder.localhost(agentId);
		ContextControllerWrapper controllerWrapper = new ContextControllerWrapper(se, context);
		SeEnvironment theEnv = new SeEnvironment(worldId, controllerWrapper);
		theEnv.loadWorld();
		
		// wait the map is loaded
		sleep(longSleepTime);
		
		// Load ExtendedSpaceEngineers
		Iv4XRAStarPathFinder pathFinder = new Iv4XRAStarPathFinder();
		ExtendedSpaceEngineers seExt = SpaceEngineersJvmExtensionsKt.extend(se, pathFinder);
		
		// Load LabRecruits Controller
		LabRecruitsController lrController = new LabRecruitsController(seExt, 3.5f);
		
		LabRecruitsCoroutinesFreeController lrControllerFree = new LabRecruitsCoroutinesFreeController(lrController);
		
		// b0-{toggle[TOGGLE]; }->b0
		lrControllerFree.goToButton("b0");
		lrControllerFree.pressButton("b0");
		
		// b0-{explore[EXPLORE]; }->d2p
		lrControllerFree.goToDoor("door2");
		
		// d2p-{explore[EXPLORE]; }->d2m
		DoorBase doorBlockById = lrControllerFree.doorBlockById("door2");
		assertTrue(lrControllerFree.doorBlockById("door2").getOpen());
		
		// d2m-{explore[EXPLORE]; }->b3
		lrControllerFree.goToButton("b3");
		
		// b3-{explore[EXPLORE]; }->b4
		lrControllerFree.goToButton("b4");
				
		// b4-{explore[EXPLORE]; }->d2m
		lrControllerFree.goToButton("door2");
		
		// d2m-{explore[EXPLORE]; }->b4
		lrControllerFree.goToButton("b4");
		
		// b4-{explore[EXPLORE]; }->d0p
		lrControllerFree.goToButton("door0");
		
		// d0p-{explore[EXPLORE]; }->b4
		lrControllerFree.goToButton("b4");
		
		// b4-{toggle[TOGGLE]; }->b4
		lrControllerFree.pressButton("b4");
		
		// b4-{toggle[TOGGLE]; }->b4
		lrControllerFree.pressButton("b4");
		
		// b4-{explore[EXPLORE]; }->b3
		lrControllerFree.goToButton("b3");
		
		System.out.println("Test completed");
		
	}
	
}
