package org.bbop.termgenie.ontology.impl;

import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.bbop.termgenie.ontology.IRIMapper;
import org.bbop.termgenie.ontology.OntologyLoader;

/**
 * Ontology Module, which priodically reloads ontologies from the source.
 */
public class ReloadingOntologyModule extends DefaultOntologyModule {

	@Override
	protected void configure() {
		super.configure();
		bind(OntologyLoader.class).to(ReloadingOntologyLoader.class);
		bind("ReloadingOntologyLoaderPeriod", new Long(6L));
		bind("ReloadingOntologyLoaderTimeUnit", TimeUnit.HOURS);
		
		
		bind(IRIMapper.class).to(FileCachingIRIMapper.class);
		bind("FileCachingIRIMapperLocalCache", FileUtils.getTempDirectory().getAbsolutePath());
		bind("FileCachingIRIMapperPeriod", new Long(6L));
		bind("FileCachingIRIMapperTimeUnit", TimeUnit.HOURS);
	}

	
	
}
