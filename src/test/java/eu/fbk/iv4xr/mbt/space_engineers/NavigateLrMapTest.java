package eu.fbk.iv4xr.mbt.space_engineers;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import environments.SeEnvironment;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import spaceEngineers.controller.Character;
import spaceEngineers.controller.ContextControllerWrapper;
import spaceEngineers.controller.ExtendedSpaceEngineers;
import spaceEngineers.controller.SpaceEngineers;
import spaceEngineers.controller.SpaceEngineersJavaProxyBuilder;
import spaceEngineers.controller.SpaceEngineersJvmExtensionsKt;
import spaceEngineers.controller.SpaceEngineersTestContext;
import spaceEngineers.iv4xr.navigation.Iv4XRAStarPathFinder;
import spaceEngineers.labrecruits.LabRecruitsController;
import spaceEngineers.labrecruits.LabRecruitsController.SimpleLabRecruitsObservation;
import spaceEngineers.model.TerminalBlock;

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
		
		SimpleLabRecruitsObservation observeActiveObjects = lrController.observeActiveObjects();
		List<TerminalBlock> buttons = observeActiveObjects.getButtons();
		TerminalBlock b0 = lrController.buttonBlockById("b0");

		lrController.goToButton("b0", null);
		
		Character character = lrController.getSpaceEngineers().getCharacter();
		
		
		System.out.println();
		
	}
	
}
