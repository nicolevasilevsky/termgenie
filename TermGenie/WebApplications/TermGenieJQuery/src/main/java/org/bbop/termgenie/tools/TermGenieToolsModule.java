package org.bbop.termgenie.tools;

import org.bbop.termgenie.core.ioc.IOCModule;
import org.bbop.termgenie.core.rules.TermGenerationEngine;
import org.bbop.termgenie.ontology.OntologyConfiguration;
import org.bbop.termgenie.ontology.OntologyLoader;

import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * Module for commons tools in the TermGenie web application.
 */
public class TermGenieToolsModule extends IOCModule {

	@Override
	protected void configure() {
		// intentionally empty
	}

	@Provides
	@Singleton
	OntologyTools provideOntologyTools(TermGenerationEngine engine,
			OntologyLoader loader,
			OntologyConfiguration configuration)
	{
		return new OntologyTools(engine, loader, configuration);
	}

	@Provides
	@Singleton
	UserCredentialValidatorTools provideUserCredentialValidatorTools() {
		return new UserCredentialValidatorTools();
	}
}
