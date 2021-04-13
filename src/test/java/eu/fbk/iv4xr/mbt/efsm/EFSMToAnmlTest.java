package eu.fbk.iv4xr.mbt.efsm;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import eu.fbk.iv4xr.mbt.MBTProperties.ModelCriterion;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.ButtonDoors1;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.LabRecruitsRandomEFSM;

class EFSMToAnmlTest {

	LabRecruitsRandomEFSM lrRandom;
	@BeforeEach
	void setUp() throws Exception {
		lrRandom = new LabRecruitsRandomEFSM();
	}

	@Test
	void testToDot() throws IOException {
		String dot = lrRandom.getEFMS().toString();
		String fn = "model.dot";
		lrRandom.saveEFSMtoDot(fn);
		System.out.println(dot);
	}
	
	@Test
	void testAnmlInstance() {
		ModelCriterion criterion = ModelCriterion.STATE;
		String anmlInstance = lrRandom.getAnmlInstanceMultiGoal(criterion);
		System.out.println(anmlInstance);
	}
	
	@Test
	void testAnml () {
		String anml = lrRandom.getAnml();
		System.out.println(anml);
	}

}
