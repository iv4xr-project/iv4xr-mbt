package eu.fbk.iv4xr.mbt.efsm;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.function.Function;

import org.evosuite.Properties;
import org.evosuite.ga.stoppingconditions.MaxFitnessEvaluationsStoppingCondition;
import org.evosuite.ga.stoppingconditions.MaxGenerationStoppingCondition;
import org.evosuite.ga.stoppingconditions.MaxStatementsStoppingCondition;
import org.evosuite.ga.stoppingconditions.MaxTestsStoppingCondition;
import org.evosuite.ga.stoppingconditions.MaxTimeStoppingCondition;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.nio.dot.DOTExporter;
import org.jgrapht.nio.graphml.GraphMLExporter;

import eu.fbk.iv4xr.mbt.efsm.labRecruits.levelGenerator.Button;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.levelGenerator.Corridor;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.levelGenerator.Door;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.levelGenerator.Room;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.levelGenerator.RendererToLRLevelDef;
import eu.fbk.iv4xr.mbt.efsm.labRecruits.levelGenerator.Layout;


/** @author Manuel Benz created on 24.02.18 */
public class EFSMExporter {

	private final EFSM efsm;
	private final Function<EFSMState, String> stateLabeler;
	private final Function<EFSMTransition, String> edgeLabeler;

	public EFSMExporter(EFSM efsm) {
		this(efsm, Object::toString, Object::toString);
	}

	public EFSMExporter(EFSM efsm, Function<EFSMState, String> stateLabeler, Function<EFSMTransition, String> edgeLabeler) {
		this.efsm = efsm;
		this.stateLabeler = stateLabeler;
		this.edgeLabeler = edgeLabeler;
	}

	public void writeOut(Path outFile, String mode) throws FileNotFoundException, IOException {
			switch (mode) {
			case "dot":
				writeDOT(outFile);
				break;
			case "gml":
				writeGML(outFile);
				break;
			default:
				throw new RuntimeException("Write mode "+mode+" not supported");
			}
	}

	/*
	 * save efsm graph to dot
	 */
	private void writeDOT(Path outFile) throws FileNotFoundException, IOException {
		DOTExporter exporter = new DOTExporter<>(stateLabeler);
		try (Writer writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(outFile.toFile()), StandardCharsets.UTF_8))) {
			exporter.exportGraph(efsm.getBaseGraph(), writer);
		}
	}
	
	/*
	 * save efsm graph to graphml
	 */
	private void writeGML(Path outFile) throws FileNotFoundException, IOException {
		GraphMLExporter<EFSMState, EFSMTransition> gExporter = new GraphMLExporter<EFSMState, EFSMTransition>();
		gExporter.setExportEdgeWeights(true);
		gExporter.setExportEdgeLabels(true);
		gExporter.setExportVertexLabels(true);
		Writer file = new FileWriter(outFile.toString());
		DirectedPseudograph<EFSMState, EFSMTransition> baseGraph = efsm.getBaseGraph();
		gExporter.exportGraph(baseGraph, file);
	}
	
	
}
