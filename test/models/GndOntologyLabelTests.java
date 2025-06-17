package models;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import modules.IndexTest;

@RunWith(Parameterized.class)
public class GndOntologyLabelTests extends IndexTest {

	@Parameters(name = "{0} -> {1}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { //
				{ "CollectiveManuscript", "Sammelhandschrift" }, //
				{ "https://d-nb.info/standards/elementset/gnd#CollectiveManuscript", "Sammelhandschrift" }, //
				{ "broaderTermPartitive", "Oberbegriff partitiv" }, //
				{ "preferredName", "Bevorzugter Name" }, //
				{ "gndIdentifier", "GND-Nummer" }, //
				{ "ZZ", "Land unbekannt" }, //
				{ "https://d-nb.info/standards/vocab/gnd/gnd-sc#ZZ", "Land unbekannt" }, //
				{ "36", "Basteln, Handarbeiten, Heimwerken" }, //
				{ "https://d-nb.info/standards/vocab/gnd/geographic-area-code#36", "Basteln, Handarbeiten, Heimwerken" }, //
				{ "notKnown", "Unbekannt" }, //
				{ "https://d-nb.info/standards/vocab/gnd/gender#notKnown", "Unbekannt" }, //
				{ "hasChild", "Kind" }, //
				{ "hasSpouse", "Ehepartner" }, //
				{ "hasAncestor", "Vorfahr" }, //
				{ "hasParent", "Elternteil" }, //
				{ "professionalRelationship", "Berufliche Beziehung" }, //
				{ "broadMatch", "Oberbegriff" }, //
				{ "exactMatch", "Entspricht" }, //
				{ "relatedMatch", "Verwandter Begriff" }, //
				{ "SubjectHeadingSensoStricto", "Schlagwort sensu stricto" }, //
				{ "https://d-nb.info/gnd/118820591", "Weizenbaum, Joseph" }, //
				{ "118820591", "Weizenbaum, Joseph" }, //
				{ "https://d-nb.info/gnd/7855044-0", "Lamassu" }, //
				{ "7855044-0", "Lamassu" }, //
				{ "7855044n0", "Lamassu" } });
	}

	private String id;
	private String label;

	public GndOntologyLabelTests(String id, String label) {
		this.id = id;
		this.label = label;
	}

	@Test
	public void testLabel() throws FileNotFoundException {
		assertThat(GndOntology.label(id), equalTo(label));
	}

}
