package eu.fbk.iv4xr.mbt.space_engineers;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import environments.SeAgentState;
import environments.SeEnvironment;
import eu.fbk.iv4xr.mbt.execution.on_sut.impl.se.SpaceEngineersUtils;
import eu.iv4xr.framework.mainConcepts.TestAgent;
import eu.iv4xr.framework.mainConcepts.TestDataCollector;
import spaceEngineers.controller.ContextControllerWrapper;
import spaceEngineers.controller.ExtendedSpaceEngineers;
import spaceEngineers.controller.SpaceEngineers;
import spaceEngineers.controller.SpaceEngineersExtensions;
import spaceEngineers.controller.SpaceEngineersJavaProxyBuilder;
import spaceEngineers.controller.SpaceEngineersJvmExtensionsKt;
import spaceEngineers.controller.SpaceEngineersTestContext;
import spaceEngineers.iv4xr.navigation.Iv4XRAStarPathFinder;
//import spaceEngineers.iv4xr.navigation.Iv4XRAStarPathFinder;
import spaceEngineers.model.DefinitionId;
import spaceEngineers.model.ToolbarLocation;
import spaceEngineers.model.Vec3F;
import spaceEngineers.model.Vec3I;
//import spaceEngineers.util.generator.map.labrecruits.LabRecruitsMapBuilder;
import spaceEngineers.util.generator.map.BlockPlacementInformation;
import spaceEngineers.util.generator.map.DataBlockPlacementInformation;
import spaceEngineers.util.generator.map.MapLayer;
import spaceEngineers.util.generator.map.MapPlacer;
import spaceEngineers.util.generator.map.Orientations;
import spaceEngineers.util.generator.map.labrecruits.LabRecruitsMap;
import spaceEngineers.util.generator.map.labrecruits.LabRecruitsMapBuilder;

/**
 * Load Lab Recruits level labrecruits.random_medium.csv and creates corresponding
 * level in Space Engineers
 * @author Davide Prandi
 *
 */
public class LoadLrLevelInSeTest {

	Long longSleepTime = 2000l;
	Long shortSleepTime = 500l;
	
	String agentId = SpaceEngineers.Companion.DEFAULT_AGENT_ID;
	String lr_map_path = "lab_recruits_levels/labrecruits.random_medium.csv";

	// "C:\\gitRepo\\iv4XR\\iv4xr-mbt\\target\\test-classes\\levels\\labrecruits.random_medium.csv"

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
	public void LoadLevelTest() {

		// load empty SE map
		SpaceEngineersTestContext context = new SpaceEngineersTestContext();
		SpaceEngineersJavaProxyBuilder proxyBuilder = new SpaceEngineersJavaProxyBuilder();
		SpaceEngineers se = proxyBuilder.localhost(agentId);
		ContextControllerWrapper controllerWrapper = new ContextControllerWrapper(se, context);
		
		
		ClassLoader classLoader = getClass().getClassLoader();
		URI mapUri = null;
		try {
			mapUri = classLoader.getResource(lr_map_path).toURI();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (mapUri != null) {
			SpaceEngineersUtils.createSpaceEngineersMapFromLabRecruitsCsv(controllerWrapper, mapUri);	
		}else {
			fail();
		}
		
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
		
		
		// MapPlacer mapPlacer = new MapPlacer(null, seExt, null, null)

//		Iv4XRAStarPathFinder pathFinder = new Iv4XRAStarPathFinder();
//		
//		ExtendedSpaceEngineers seExt = SpaceEngineersJvmExtensionsKt.extend(se, pathFinder);
//
//		//
//		List<String> mapLines = null;
//		try {
//			mapLines = Files.readAllLines(
//					Paths.get("C:\\gitRepo\\iv4xr\\iv4xrDemo\\src\\test\\resources\\levels\\button1_opens_door1.csv"));
//			System.out.println();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		LabRecruitsMap lrMap = LabRecruitsMap.Companion.fromLines(mapLines);
//
////		LabRecruitsMapBuilder lrMapBuilder = new LabRecruitsMapBuilder(lrMap, seExt);
////
////		try {
////			lrMapBuilder.generate();	
////		}catch (Exception e) {
////			e.printStackTrace();
////		}
//		
//		lrMap.placeGenerator();
//		sleep(longSleepTime);
//        lrMap.placeGravityGenerator();
//        sleep(longSleepTime);
//        //spaceEngineers.removeAllBlocks()
//        
//        DefinitionId blockType = DefinitionId.Companion.cubeBlock("LargeHeavyBlockArmorBlock");
//        
//        DataBlockPlacementInformation blockInfo = 
//        		new DataBlockPlacementInformation(
//        				blockType, 
//        				Vec3I.Companion.getFORWARD(), 
//        				Vec3I.Companion.getUP(), 
//        				null, 
//        				agentId, 
//        				Vec3I.Companion.getZERO());
//        
//        MapPlacer labRecruitsMapPlacer = new MapPlacer(lrMap, seExt, blockInfo);
//        sleep(longSleepTime);
//        
//        Vec3F teleportPosition = new Vec3F(10f, 10f, 10f);
//        
//        labRecruitsMapPlacer.generate(Vec3F.Companion.getZERO(),teleportPosition);
////        createGroups(gridId, map)
////        mapButtons(map)
////        sleep(50)
////        closeAllDoors(map)
//		
//		theEnv.close();

	}

}
