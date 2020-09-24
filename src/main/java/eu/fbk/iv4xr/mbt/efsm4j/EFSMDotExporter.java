package eu.fbk.iv4xr.mbt.efsm4j;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.function.Function;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.IntegerComponentNameProvider;

/** @author Manuel Benz created on 24.02.18 */
public class EFSMDotExporter<
	State extends EFSMState, 
	Transition extends eu.fbk.iv4xr.mbt.efsm4j.Transition<State, ?, ?>> {
	
  private final EFSM<State, ?, ?, Transition> efsm;
  private final Function<State, String> stateLabeler;
  private final Function<Transition, String> edgeLabeler;

  public EFSMDotExporter(EFSM<State, ?, ?, Transition> efsm) {
    this(efsm, Object::toString, Object::toString);
  }

  public EFSMDotExporter(
      EFSM<State, ?, ?,Transition> efsm,
      Function<State, String> stateLabeler,
      Function<Transition, String> edgeLabeler) {
    this.efsm = efsm;
    this.stateLabeler = stateLabeler;
    this.edgeLabeler = edgeLabeler;
  }

  public void writeOut(Path outFile) throws IOException {
    DOTExporter exporter =
        new DOTExporter<State, Transition>(
            new IntegerComponentNameProvider<>(),
            s -> stateLabeler.apply(s),
            t -> edgeLabeler.apply(t));

    try (Writer writer =
        new BufferedWriter(
            new OutputStreamWriter(
                new FileOutputStream(outFile.toFile()), StandardCharsets.UTF_8))) {
      exporter.exportGraph(efsm.getBaseGraph(), writer);
    }
  }
}
