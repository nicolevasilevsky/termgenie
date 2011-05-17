package org.bbop.termgenie.ontology;

import static org.bbop.termgenie.core.rules.DefaultTermTemplates.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bbop.termgenie.core.OntologyAware.Ontology;
import org.bbop.termgenie.core.rules.DefaultTermTemplates.DefaultOntology;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.IRIDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.UnknownOWLOntologyException;

import owltools.graph.OWLGraphWrapper;

public class DefaultOntologyLoader {
	
	private final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	private final Map<String, OWLGraphWrapper> ontologies = new HashMap<String, OWLGraphWrapper>();

	public DefaultOntologyLoader() {
		super();
		manager.addIRIMapper(new LocalFileIRIMapper());
	}

	public List<Ontology> getOntologies() {
		List<Ontology> result = new ArrayList<Ontology>(defaultOntologies);
		for (DefaultOntology ontology : defaultOntologies) {
			OWLGraphWrapper realInstance = loadOntology(ontology);
			ontology.setRealInstance(realInstance);
		}
		return result;
	}
	
	
	private OWLGraphWrapper loadOntology(DefaultOntology ontology) {
		String uniqueName = ontology.getUniqueName();
		if (ontologies.containsKey(uniqueName)) {
			return ontologies.get(uniqueName);
		}
		try {
			OWLGraphWrapper w = getResource(ontology);
			ontologies.put(uniqueName, w);
			return w;
		} catch (UnknownOWLOntologyException exception) {
			throw new RuntimeException(exception);
		} catch (OWLOntologyCreationException exception) {
			throw new RuntimeException(exception);
		}
	}


	protected OWLGraphWrapper getResource(DefaultOntology ontology) throws OWLOntologyCreationException {
		if (equals(ontology, GENE_ONTOLOGY)) {
			OWLGraphWrapper w = load("http://www.geneontology.org/ontology/obo_format_1_2/gene_ontology_ext.obo");
			if (w == null) {
				return null;
			}
			// load additional cross products
			w.addSupportOntology(loadOWL("http://www.geneontology.org/scratch/xps/biological_process_xp_self.obo"));
			w.addSupportOntology(loadOWL("http://www.geneontology.org/scratch/xps/biological_process_xp_cellular_component.obo"));
			w.addSupportOntology(loadOWL("http://www.geneontology.org/scratch/xps/molecular_function_xp_protein.obo"));
//			w.addSupportOntology(loadOWL("http://www.geneontology.org/scratch/xps/biological_process_xp_uber_anatomy.obo"));
			w.addSupportOntology(loadOWL("http://www.geneontology.org/scratch/xps/biological_process_xp_plant_anatomy.obo"));
			w.addSupportOntology(loadOWL("http://www.geneontology.org/scratch/xps/cellular_component_xp_protein.obo"));
			return w;
		} 
		else if (equals(ontology, PROTEIN_ONTOLOGY)) {
			return load("ftp://ftp.pir.georgetown.edu/databases/ontology/pro_obo/pro.obo");
		}
		else if (equals(ontology, UBERON_ONTOLOGY)) {
			return load("http://github.com/cmungall/uberon/raw/master/uberon.obo");
		}
		else if (equals(ontology, HP_ONTOLOGY)) {
			OWLGraphWrapper w = load("http://compbio.charite.de/svn/hpo/trunk/src/ontology/human-phenotype-ontology.obo");
			if (w != null) {
				w.addSupportOntology(loadOWL("http://compbio.charite.de/svn/hpo/trunk/src/ontology/human-phenotype-ontology_xp.obo"));
			}
			return w;
		}
		else if (equals(ontology, FMA_ONTOLOGY)) {
			
		}
		else if (equals(ontology, PATO)) {
			
		}
		else if (equals(ontology, OMP)) {
			
		}
		else if (equals(ontology, CELL_ONTOLOGY)) {
			
		}
		else if (equals(ontology, PLANT_ONTOLOGY)) {
			
		}
		return null;
	}
	
	OWLGraphWrapper load(String url) throws OWLOntologyCreationException {
		OWLOntology owlOntology = loadOWL(url);
		if (owlOntology == null) {
			return null;
		}
		return new OWLGraphWrapper(owlOntology);
	}

	protected OWLOntology loadOWL(String url) throws OWLOntologyCreationException {
		OWLOntology owlOntology = manager.loadOntology(IRI.create(url));
		return owlOntology;
	}
	
	private boolean equals(Ontology o1, Ontology o2) {
		if (o1 == null) {
			return o2 == null;
		}
		if (o2 == null) {
			return false;
		}
		return o1.getUniqueName().equals(o2.getUniqueName());
	}

	public static void main(String[] args) throws Exception {
		DefaultOntologyLoader instance = new DefaultOntologyLoader();
		List<Ontology> ontologies = instance.getOntologies();
		
		OWLGraphWrapper ontology = ontologies.get(0).getRealInstance();
		OWLObject owlObject = ontology.getOWLObjectByIdentifier("GO:0003674");
		System.out.println(owlObject);
	}
}