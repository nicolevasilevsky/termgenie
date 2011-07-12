package org.bbop.termgenie.ontology;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.Set;

import org.apache.tools.ant.filters.StringInputStream;
import org.bbop.termgenie.ontology.DefaultOntologyCleaner.CleanerConfig;
import org.junit.BeforeClass;
import org.junit.Test;

public class DefaultOntologyCleanerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testLoadCleanerConfig() {
		String source = "[Ontology]\n name: ProteinOntology \n[Term]\nts [ts1,ts2] true \n [Typdef] \n td [td1] \n\n!tx\n\n [Instance]\n in1\n in2\n"+
		"[Ontology]\n name: Uberon \n[Term]\nts [ts1,ts2] \n [Typdef] \n td [td1] \n\n!tx\n\n [Instance]\n in1 true \n in2";
		Map<String, CleanerConfig> settings = CleanerConfig.loadSettings(new StringInputStream(source));
		assertEquals(2, settings.size());
		CleanerConfig cleanerConfig1 = settings.get("ProteinOntology");
		assertNotNull(cleanerConfig1);
		assertEquals(1, cleanerConfig1.retain_term_clauses.size());
		assertSetEquals(new String[]{"ts1","ts2"}, cleanerConfig1.retain_term_clauses.get("ts").types);
		assertEquals(true, cleanerConfig1.retain_term_clauses.get("ts").clearQualifiers);
		
		assertEquals(1, cleanerConfig1.retain_typedef_clauses.size());
		assertSetEquals(new String[]{"td1"}, cleanerConfig1.retain_typedef_clauses.get("td").types);
		assertEquals(false, cleanerConfig1.retain_typedef_clauses.get("td").clearQualifiers);
		
		assertEquals(2, cleanerConfig1.retain_instance_clauses.size());
		assertSetEquals(new String[]{}, cleanerConfig1.retain_instance_clauses.get("in1").types);
		assertEquals(false, cleanerConfig1.retain_instance_clauses.get("in1").clearQualifiers);
		assertSetEquals(new String[]{}, cleanerConfig1.retain_instance_clauses.get("in2").types);
		assertEquals(false, cleanerConfig1.retain_instance_clauses.get("in2").clearQualifiers);
		
		CleanerConfig cleanerConfig2 = settings.get("Uberon");
		assertNotNull(cleanerConfig2);
		assertNotNull(cleanerConfig2);
		assertEquals(1, cleanerConfig2.retain_term_clauses.size());
		assertSetEquals(new String[]{"ts1","ts2"}, cleanerConfig2.retain_term_clauses.get("ts").types);
		assertEquals(false, cleanerConfig2.retain_term_clauses.get("ts").clearQualifiers);
		
		assertEquals(1, cleanerConfig2.retain_typedef_clauses.size());
		assertSetEquals(new String[]{"td1"}, cleanerConfig2.retain_typedef_clauses.get("td").types);
		assertEquals(false, cleanerConfig2.retain_typedef_clauses.get("td").clearQualifiers);
		
		assertEquals(2, cleanerConfig2.retain_instance_clauses.size());
		assertSetEquals(new String[]{}, cleanerConfig2.retain_instance_clauses.get("in1").types);
		assertEquals(true, cleanerConfig2.retain_instance_clauses.get("in1").clearQualifiers);
		assertSetEquals(new String[]{}, cleanerConfig2.retain_instance_clauses.get("in2").types);
		assertEquals(false, cleanerConfig2.retain_instance_clauses.get("in2").clearQualifiers);
	}
	
	private static void assertSetEquals(String[] set1, Set<String> set2) {
		assertEquals(set1.length, set2.size());
		for (String string : set1) {
			assertTrue(set2.contains(string));
		}
	}

}
