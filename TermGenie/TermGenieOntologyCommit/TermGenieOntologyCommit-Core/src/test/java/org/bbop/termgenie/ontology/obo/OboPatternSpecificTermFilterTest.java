package org.bbop.termgenie.ontology.obo;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bbop.termgenie.ontology.entities.CommitHistoryItem;
import org.bbop.termgenie.ontology.entities.CommitedOntologyTerm;
import org.junit.BeforeClass;
import org.junit.Test;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.parser.OBOFormatParser;


public class OboPatternSpecificTermFilterTest  {

	private static OboPatternSpecificTermFilter filter;
	private static OBODoc main;
	private static OBODoc xp;
	private static List<OBODoc> allOntologies;
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		Map<String, Integer> specialPatterns = Collections.singletonMap("special", Integer.valueOf(1));
		filter = new OboPatternSpecificTermFilter(specialPatterns );
		
		OBOFormatParser p = new OBOFormatParser();
		main = p.parse(new File("src/test/resources/svn-test-main.obo"));
		xp = p.parse(new File("src/test/resources/svn-test-extension.obo"));
		
		allOntologies = Arrays.asList(main, xp);
	}

	@Test
	public void testFilterTerms1a() {
		// prepare
		CommitHistoryItem item = new CommitHistoryItem();
		List<CommitedOntologyTerm> terms = new ArrayList<CommitedOntologyTerm>();
		terms.add(createTerm("FOO:4000", "default", 
				"[Term]\nid: FOO:4000\nname: foo-4000\nis_a: FOO:0001\nintersection_of: FOO:0001\nintersection_of: part_of FOO:0002\nrelationship: part_of FOO:0002\n"));
		item.setTerms(terms);
		
		// filter for main
		List<CommitedOntologyTerm> filtered = filter.filterTerms(item, main, allOntologies, 0);
		final CommitedOntologyTerm originalTerm = terms.get(0);
		CommitedOntologyTerm filteredTerm = filtered.get(0);
		assertEquals("expected no changes to term", originalTerm, filteredTerm);
		
		// filter for xp file
		assertNull("expected no content for xp file", filter.filterTerms(item, xp, allOntologies, 1));
	}
	
	@Test
	public void testFilterTerms1b() {
		// prepare
		CommitHistoryItem item = new CommitHistoryItem();
		List<CommitedOntologyTerm> terms = new ArrayList<CommitedOntologyTerm>();
		terms.add(createTerm("FOO:4000", "special", 
				"[Term]\nid: FOO:4000\nname: foo-4000\nis_a: FOO:0001\nintersection_of: FOO:0001\nintersection_of: part_of FOO:0002\nrelationship: part_of FOO:0002\n"));
		item.setTerms(terms);
		
		// filter for main
		List<CommitedOntologyTerm> filtered0 = filter.filterTerms(item, main, allOntologies, 0);
		final CommitedOntologyTerm originalTerm = terms.get(0);
		CommitedOntologyTerm filteredTerm = filtered0.get(0);
		assertEquals("expected no changes to term", originalTerm, filteredTerm);
		
		// filter for xp file
		List<CommitedOntologyTerm> filtered1 = filter.filterTerms(item, xp, allOntologies, 1);
		assertTrue("expected no content for xp file", filtered1.isEmpty());
	}
	
	@Test
	public void testFilterTerms2() {
		// prepare
		CommitHistoryItem item = new CommitHistoryItem();
		List<CommitedOntologyTerm> terms = new ArrayList<CommitedOntologyTerm>();
		terms.add(createTerm("FOO:4000", "special", 
				"[Term]\nid: FOO:4000\nname: foo-4000\nis_a: FOO:0001\nintersection_of: FOO:0001\nintersection_of: has_participant FOO:0002\nrelationship: has_participant FOO:0002\n"));
		item.setTerms(terms);
		
		// filter for main
		List<CommitedOntologyTerm> filtered0 = filter.filterTerms(item, main, allOntologies, 0);
		assertEquals("[Term]\nid: FOO:4000\nname: foo-4000\nis_a: FOO:0001\n\n", 
				filtered0.get(0).getObo());
		
		// filter for xp file
		List<CommitedOntologyTerm> filtered1 = filter.filterTerms(item, xp, allOntologies, 1);
		assertEquals("[Term]\nid: FOO:4000\nintersection_of: FOO:0001\nintersection_of: has_participant FOO:0002\n\n", 
				filtered1.get(0).getObo());
	}
	
	
	@Test
	public void testFilterTerms3() {
		// prepare
		CommitHistoryItem item = new CommitHistoryItem();
		List<CommitedOntologyTerm> terms = new ArrayList<CommitedOntologyTerm>();
		terms.add(createTerm("FOO:4000", "special", 
				"[Term]\nid: FOO:4000\nname: foo-4000\nis_a: FOO:0001\nintersection_of: FOO:0001\nintersection_of: part_of GOO:0002\nrelationship: part_of GOO:0002\n"));
		item.setTerms(terms);
		
		// filter for main
		List<CommitedOntologyTerm> filtered0 = filter.filterTerms(item, main, allOntologies, 0);
		assertEquals("[Term]\nid: FOO:4000\nname: foo-4000\nis_a: FOO:0001\n\n", 
				filtered0.get(0).getObo());
		
		// filter for xp file
		List<CommitedOntologyTerm> filtered1 = filter.filterTerms(item, xp, allOntologies, 1);
		assertEquals("[Term]\nid: FOO:4000\nintersection_of: FOO:0001\nintersection_of: part_of GOO:0002\n\n", 
				filtered1.get(0).getObo());
	}
	
	private CommitedOntologyTerm createTerm(String id, String pattern, String obo) {
		CommitedOntologyTerm term = new CommitedOntologyTerm();
		
		term.setId(id);
		term.setPattern(pattern);
		term.setObo(obo);
		
		return term;
	}

}