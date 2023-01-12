package eu.fbk.iv4xr.mbt.execution.on_sut.impl.se;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import environments.SeEnvironment;
import spaceEngineers.controller.ContextControllerWrapper;

/**
 * Utility methods to interact with Space Engineers
 * 
 * @param worldId
 * @return
 */
public class SpaceEngineersUtils {

	// Basic empty map to create SE levels from LabRecruits csv
	private static final String emptyMapName = "empty-world-creative";

	// Basic map to perform grind and weldge tests
	private static final String grindMapName = "simple-place-grind-torch";

	// Basic map to perform grind and weldge tests
	private static final String lrRandomMediumMapName = "LR_random_medium";

	
	/**
	 * Load an empty level from the resources
	 * 
	 * @param controllerWrapper
	 * @return
	 */
	public static SeEnvironment createSeEnvWithEmptyMap(ContextControllerWrapper controllerWrapper) {
		// load path to se saves
		String seGameSavesFolder = getSeGameSavesFolder();
		// create evn
		SeEnvironment theEnv = new SeEnvironment(emptyMapName, controllerWrapper, seGameSavesFolder);
		return theEnv;
	}
	
	/**
	 * Load map for grind and weldge tests
	 * 
	 * @param controllerWrapper
	 * @return
	 */
	public static SeEnvironment createSeEnvWithSimpleMap(ContextControllerWrapper controllerWrapper) {
		// load path to se saves
		String seGameSavesFolder = getSeGameSavesFolder();
		// create evn
		SeEnvironment theEnv = new SeEnvironment(grindMapName, controllerWrapper, seGameSavesFolder);
		return theEnv;
	}
	
	/**
	 * Load Lab Recruits random medium map
	 * 
	 * @param controllerWrapper
	 * @return
	 */
	public static SeEnvironment createSeEnvWithLrRanommediumMap(ContextControllerWrapper controllerWrapper) {
		// load path to se saves
		String seGameSavesFolder = getSeGameSavesFolder();
		// create evn
		SeEnvironment theEnv = new SeEnvironment(lrRandomMediumMapName, controllerWrapper, seGameSavesFolder);
		return theEnv;
	}
	

	/**
	 * Get folder with game saves
	 * 
	 * @return
	 */
	private static String getSeGameSavesFolder() {

		URI jarResources = null;
		try {
			jarResources = SpaceEngineersUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		java.nio.file.Path se_saves_path = Paths.get(jarResources);
		se_saves_path = Paths.get(se_saves_path.toString(), "se_game_saves");
		String game_saves_folder = se_saves_path.toString() + File.separator;

		return game_saves_folder;
	}

}
