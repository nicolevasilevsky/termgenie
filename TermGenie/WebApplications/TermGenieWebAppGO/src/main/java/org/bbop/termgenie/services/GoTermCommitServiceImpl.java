package org.bbop.termgenie.services;

import java.util.List;

import org.bbop.termgenie.core.Ontology;
import org.bbop.termgenie.core.Ontology.IRelation;
import org.bbop.termgenie.core.Ontology.OntologyTerm;
import org.bbop.termgenie.core.Ontology.Relation;
import org.bbop.termgenie.core.rules.TermGenerationEngine;
import org.bbop.termgenie.ontology.CommitInfo;
import org.bbop.termgenie.ontology.CommitObject;
import org.bbop.termgenie.ontology.Committer;
import org.bbop.termgenie.ontology.OntologyIdManager;
import org.bbop.termgenie.ontology.OntologyLoader;
import org.bbop.termgenie.ontology.OntologyTaskManager;
import org.bbop.termgenie.ontology.OntologyTaskManager.OntologyTask;
import org.bbop.termgenie.ontology.go.GoCommitInfo;
import org.bbop.termgenie.ontology.impl.ConfiguredOntology;
import org.bbop.termgenie.services.permissions.UserPermissions;
import org.bbop.termgenie.services.permissions.UserPermissions.CommitUserData;
import org.bbop.termgenie.tools.OntologyTools;

import owltools.graph.OWLGraphWrapper;
import owltools.graph.OWLGraphWrapper.Synonym;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class GoTermCommitServiceImpl extends AbstractTermCommitServiceImpl {

	private final ConfiguredOntology geneOntology;
	private String tempIdPrefix;

	@Inject
	protected GoTermCommitServiceImpl(OntologyTools ontologyTools,
			InternalSessionHandler sessionHandler,
			Committer committer,
			final @Named("ConfiguredOntologyGeneOntology") ConfiguredOntology source,
			OntologyLoader loader,
			OntologyIdManager idProvider,
			final TermGenerationEngine generationEngine,
			UserPermissions permissions)
	{
		super(ontologyTools, sessionHandler, committer, idProvider, permissions);
		geneOntology = source;
		OntologyTaskManager taskManager = loader.getOntology(source);
		taskManager.runManagedTask(new OntologyTask() {

			@Override
			protected void runCatching(OWLGraphWrapper managed) throws TaskException, Exception {
				tempIdPrefix = generationEngine.getTempIdPrefix(managed);
			}
		});
		
	}

	@Override
	protected Ontology getTargetOntology() {
		return geneOntology;
	}

	@Override
	protected String getTempIdPrefix() {
		return tempIdPrefix;
	}

	@Override
	protected CommitInfo createCommitInfo(List<CommitObject<OntologyTerm<Synonym, IRelation>>> terms,
			List<CommitObject<Relation>> relations,
			String termgenieUser,
			CommitUserData commitUserData) {
		String screenname = commitUserData.getScreenname();
		if (screenname == null) {
			screenname = termgenieUser;
		}
		return new GoCommitInfo(terms, screenname);
	}
}
