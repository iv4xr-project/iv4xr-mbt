package eu.fbk.iv4xr.mbt.model;

import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import eu.fbk.iv4xr.mbt.MBTProperties;
import eu.fbk.iv4xr.mbt.efsm.EFSM;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.LabRecruitsRandomEFSM;

public class LabRecruitsMutantGeneratorTest {

	@Test
	public void mutantGeneratorTest() throws IOException {
		MBTProperties.LR_generation_mode = MBTProperties.LR_random_mode.N_BUTTONS_DEPENDENT;
		MBTProperties.LR_mean_buttons = 0.5;
		MBTProperties.LR_n_doors = 6;
		MBTProperties.LR_n_rooms = 4;

		// out folder
		String outFolder = "data/mutants/";
		// create folder 
		try {
			Path outFolderPath = Paths.get(outFolder);
			Files.createDirectories(outFolderPath);
			
		}catch (IOException e){
			fail();
			System.err.println("Failed to create directory!" + e.getMessage());
		}
				
		// initialize the generator with default parameters
		LabRecruitsRandomEFSM labRecruitsRandomEFSM = new LabRecruitsRandomEFSM();

		// generate and EFSM
		EFSM testEFSM = labRecruitsRandomEFSM.getEFMS();

		String levelId = "Four_rooms_Six_doors";
		// save door graph in graphml formal
		labRecruitsRandomEFSM.saveDoorGraph(outFolder+levelId);
		// save EFSM in dot format
		labRecruitsRandomEFSM.saveEFSMtoDot(outFolder+levelId);
		
		if (labRecruitsRandomEFSM.get_csv() == "") {
			System.out.println("Cannot create a planar graph with these paratemers");
		}else {
			// save the level
			labRecruitsRandomEFSM.saveLabRecruitsLevel(outFolder+levelId);
			// save mutants
			String mutantsFolder = outFolder+"mutants/";
			try {
				Path mutantsFolderPath = Paths.get(mutantsFolder);
				Files.createDirectories(mutantsFolderPath);
				
				// save remove mutations
				List<String> removeMut = labRecruitsRandomEFSM.getRemoveMutations();
				Integer mutId = 1;
				for(String l : removeMut) {
					String scenarioID = levelId+"_removeMut_"+mutId+".csv";
					Path outFile = Paths.get(mutantsFolder+scenarioID);				
					try {
						BufferedWriter writer = new BufferedWriter(new FileWriter(outFile.toString()));
						writer.write(l);
				        writer.close();
				        mutId = mutId + 1;
					} catch(IOException io){
						fail();
						io.printStackTrace();
					}
				}
				
				
				// save add mutations
				List<String> addMut = labRecruitsRandomEFSM.getAddMutations();
				mutId = 1;
				for(String l : addMut) {
					String scenarioID = levelId+"_addMut_"+mutId+".csv";
					Path outFile = Paths.get(mutantsFolder+scenarioID);				
					try {
						BufferedWriter writer = new BufferedWriter(new FileWriter(outFile.toString()));
						writer.write(l);
				        writer.close();
				        mutId = mutId + 1;
					} catch(IOException io){
						fail();
						io.printStackTrace();
					}
				}
				
			}catch (IOException e){
				System.err.println("Failed to create directory!" + e.getMessage());
				fail();
			}

		}
		
		
		
	}

}
