package eu.fbk.iv4xr.mbt.execution.on_sut.impl.se;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import environments.SeEnvironment;
import spaceEngineers.controller.ContextControllerWrapper;
import spaceEngineers.controller.ExtendedSpaceEngineers;
import spaceEngineers.controller.SpaceEngineersJvmExtensionsKt;
import spaceEngineers.iv4xr.navigation.Iv4XRAStarPathFinder;
import spaceEngineers.model.DefinitionId;
import spaceEngineers.model.Vec3F;
import spaceEngineers.model.Vec3I;
import spaceEngineers.util.generator.map.DataBlockPlacementInformation;
import spaceEngineers.util.generator.map.MapPlacer;
import spaceEngineers.util.generator.map.Orientations;
import spaceEngineers.util.generator.map.labrecruits.LabRecruitsMap;
import spaceEngineers.util.generator.map.labrecruits.LabRecruitsMapBuilder;

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

	// delay time to allow communication with SE
	public static Long longSleepTime = 2000l;
	public static Long shortSleepTime = 500l;
	
	
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
		// String seGameSavesFolder = getSeGameSavesFolder();
		String seGameSavesFolder = "target\\classes\\se_game_saves\\";
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

	
	
	/**
	 * Create a Space Engineers lavel from a csv that specify a Lab Recruits level
	 * 
	 * @param controllerWrapper
	 * @param LrMapUri The URI 
	 * @return
	 */
	public static SeEnvironment createSpaceEngineersMapFromLabRecruitsCsv(ContextControllerWrapper controllerWrapper, URI LrMapUri) {
		
		//TODO Check if the URI is valid
		
		// Load the map as a string
		String mapLines = null;
		try {
			mapLines = Files.readString(Paths.get(LrMapUri));
		} catch (IOException e) {
			System.out.println("Problem in loading map "+LrMapUri.toString());
			e.printStackTrace();
		}
		
		// Load ExtendedSpaceEngineers
		SeEnvironment theEnv = SpaceEngineersUtils.createSeEnvWithEmptyMap(controllerWrapper);
		Iv4XRAStarPathFinder pathFinder = new Iv4XRAStarPathFinder();
		ExtendedSpaceEngineers seExt = SpaceEngineersJvmExtensionsKt.extend(theEnv.getController().getSpaceEngineers() , pathFinder);

		// create map
		LabRecruitsMap lrMap = LabRecruitsMap.Companion.fromString(mapLines);
		
		Orientations or = new Orientations(Vec3I.Companion.getFORWARD(), Vec3I.Companion.getUP());
		List<Orientations> orientations = new ArrayList<Orientations>();
		orientations.add(or);
		DataBlockPlacementInformation dbPlacerInfo = new DataBlockPlacementInformation(
				DefinitionId.Companion.cubeBlock("LargeHeavyBlockArmorBlock"), null, null, Vec3I.Companion.getZERO(),
				orientations);
		MapPlacer mapPlacer = new MapPlacer(lrMap, seExt, dbPlacerInfo, null);

	
		Vec3F teleportPosition = new Vec3F(10f, 10f, 10f);
		
		LabRecruitsMapBuilder mapBuilder = new LabRecruitsMapBuilder(lrMap, seExt, mapPlacer);
		
		// Load the level
		theEnv.loadWorld();

		sleep(longSleepTime);
		
		// generate the map
		mapBuilder.generate();
		
//		SeEnvironment theEnv = SpaceEngineersUtils.createSeEnvWithEmptyMap(controllerWrapper);
//		theEnv.loadWorld();
//
//		// wait the map is loaded
//		sleep(longSleepTime);
//
//		// load LR level by lines
//		String mapLines = null; 
//		try {
//			ClassLoader classLoader = getClass().getClassLoader();
//			URI uri = classLoader.getResource(lr_map_path).toURI();
//			mapLines = Files.readString(Paths.get(uri));
//		} catch (URISyntaxException | IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		// Load ExtendedSpaceEngineers
//		Iv4XRAStarPathFinder pathFinder = new Iv4XRAStarPathFinder();
//		ExtendedSpaceEngineers seExt = SpaceEngineersJvmExtensionsKt.extend(se, pathFinder);
//
//		// create map
//		LabRecruitsMap lrMap = LabRecruitsMap.Companion.fromString(mapLines);
//
//		Orientations or = new Orientations(Vec3I.Companion.getFORWARD(), Vec3I.Companion.getUP());
//		List<Orientations> orientations = new ArrayList<Orientations>();
//		orientations.add(or);
//		DataBlockPlacementInformation dbPlacerInfo = new DataBlockPlacementInformation(
//				DefinitionId.Companion.cubeBlock("LargeHeavyBlockArmorBlock"), null, null, Vec3I.Companion.getZERO(),
//				orientations);
//		MapPlacer mapPlacer = new MapPlacer(lrMap, seExt, dbPlacerInfo, null);
//
//		sleep(longSleepTime);
//
//		Vec3F teleportPosition = new Vec3F(10f, 10f, 10f);
//
//		
////		lrMap.placeGenerator();
////		lrMap.placeGravityGenerator();
////		String generate = mapPlacer.generate(Vec3F.Companion.getZERO(), teleportPosition);
//		
//		LabRecruitsMapBuilder mapBuilder = new LabRecruitsMapBuilder(lrMap, seExt, mapPlacer);
//		
//		mapBuilder.generate();
//		
		
		return null;
	}
	
	
	/**
	 * Suspend the execution to allow Space Engineers processing information
	 * @param i
	 */
	private static void sleep(long i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
