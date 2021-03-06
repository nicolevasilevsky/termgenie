package org.bbop.termgenie.rules.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.bbop.termgenie.rules.api.TermGenieScriptFunctionsSynonyms;
import org.bbop.termgenie.tools.Pair;
import org.obolibrary.oboformat.parser.OBOFormatConstants.OboFormatTag;
import org.semanticweb.owlapi.model.OWLObject;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import owltools.graph.OWLGraphWrapper;
import owltools.graph.OWLGraphWrapper.ISynonym;
import owltools.graph.OWLGraphWrapper.Synonym;

public class SynonymGenerationTools implements TermGenieScriptFunctionsSynonyms {

	private boolean filterNonAsciiSynonyms = false;
	
	public SynonymGenerationTools(boolean filterNonAsciiSynonyms) {
		this.filterNonAsciiSynonyms = filterNonAsciiSynonyms;
	}
	
	@Override
	public List<ISynonym> synonyms(String prefix,
			OWLObject x,
			OWLGraphWrapper ontology,
			String suffix,
			String defaultScope,
			String label)
	{
		List<ISynonym> synonyms = getSynonyms(x, ontology, null, false);
		if (synonyms == null || synonyms.isEmpty()) {
			return null;
		}
		List<ISynonym> results = new ArrayList<ISynonym>();
		for (ISynonym synonym : synonyms) {
			StringBuilder sb = new StringBuilder();
			if (prefix != null) {
				sb.append(prefix);
			}
			sb.append(synonym.getLabel());
			if (suffix != null) {
				sb.append(suffix);
			}
			String scope = synonym.getScope();
			if (defaultScope != null) {
				if (defaultScope.equals(OboFormatTag.TAG_BROAD.getTag())) {
					scope = OboFormatTag.TAG_BROAD.getTag();
				}
				else {
					scope = matchScopes(defaultScope, scope).getTwo();
				}
			}
			addSynonym(results, scope, sb.toString(), label);
		}
		return results;
	}

	@Override
	public List<ISynonym> synonyms(String[] prefixes,
			String[] scopes,
			OWLObject x,
			OWLGraphWrapper ontology,
			String[] suffixes,
			String label)
	{
		List<ISynonym> results = new ArrayList<ISynonym>();
		String termLabel = getLabel(x, ontology);
		if (prefixes != null && prefixes.length > 0) {
			for (int i = 0; i < prefixes.length; i++) {
				String prefix = prefixes[i];
				String scope = null;
				if (scopes != null && scopes.length > i) {
					scope = scopes[i];
				}
				if(scope == null) {
					scope = OboFormatTag.TAG_RELATED.getTag();
				}
				if (suffixes != null && suffixes.length > 0) {
					for(String suffix : suffixes) {
						results = addSynonym(label, results, prefix, termLabel, suffix, scope);
					}
				}
				else {
					results = addSynonym(label, results, prefix, termLabel, null, scope);
				}
			}
		}
		else if (suffixes != null) {
			for (int i = 0; i < suffixes.length; i++) {
				String suffix = suffixes[i];
				String scope = null;
				if (scopes != null && scopes.length > i) {
					scope = scopes[i];
				}
				if(scope == null) {
					scope = OboFormatTag.TAG_RELATED.getTag();
				}
				results = addSynonym(label, results, null, termLabel, suffix, scope);
			}
		}
		
		List<ISynonym> synonyms = getSynonyms(x, ontology, null, false);
		if (synonyms != null && !synonyms.isEmpty()) {
			for (ISynonym synonym : synonyms) {
				if (prefixes != null && prefixes.length > 0) {
					for (int i = 0; i < prefixes.length; i++) {
						String prefix = prefixes[i];
						String scope = null;
						if (scopes != null && scopes.length > i) {
							scope = scopes[i];
						}
						Pair<Boolean, String> match = matchScopes(scope, synonym.getScope());
						if (match.getOne()) {
							scope = match.getTwo();
							if (suffixes != null && suffixes.length > 0) {
								for(String suffix : suffixes) {
									addSynonym(label, results, prefix, synonym.getLabel(), suffix, scope);
								}
							}
							else {
								addSynonym(label, results, prefix, synonym.getLabel(), null, scope);
							}
						}
					}
				}
				else if (suffixes != null) {
					for (int i = 0; i < suffixes.length; i++) {
						String suffix = suffixes[i];
						String scope = null;
						if (scopes != null && scopes.length > i) {
							scope = scopes[i];
						}
						Pair<Boolean, String> match = matchScopes(scope, synonym.getScope());
						if (match.getOne()) {
							scope = match.getTwo();
							addSynonym(label, results, null, synonym.getLabel(), suffix, scope);
						}
					}
				}
				
			}
		}
		
		if (results.isEmpty()) {
			results = null;
		}
		return results;
	}

	@Override
	public List<ISynonym> addSynonym(String label,
			List<ISynonym> results,
			String prefix,
			String infix,
			String suffix,
			String scope)
	{
		StringBuilder sb = new StringBuilder();
		if (prefix != null) {
			sb.append(prefix);
		}
		sb.append(infix);
		if (suffix != null) {
			sb.append(suffix);
		}
		return addSynonym(results, scope, sb.toString(), label);
	}

	@Override
	public List<ISynonym> synonyms(String prefix,
			OWLObject x1,
			OWLGraphWrapper ontology1,
			String infix,
			OWLObject x2,
			OWLGraphWrapper ontology2,
			String suffix,
			String defaultScope,
			String label)
	{
		return synonyms(prefix, x1, ontology1, infix, x2, ontology2, suffix, defaultScope, label, null, false);
	}
	
	@Override
	public List<ISynonym> synonyms(String prefix,
			OWLObject x1,
			OWLGraphWrapper ontology1,
			String infix,
			OWLObject x2,
			OWLGraphWrapper ontology2,
			String suffix,
			String defaultScope,
			String label,
			String requiredPrefixLeft,
			boolean ignoreSynonymsRight)
	{
		List<ISynonym> synonyms1 = getSynonyms(x1, ontology1, requiredPrefixLeft, false); 
		List<ISynonym> synonyms2 = getSynonyms(x2, ontology2, null, ignoreSynonymsRight);
		boolean empty1 = synonyms1 == null || synonyms1.isEmpty();
		boolean empty2 = synonyms2 == null || synonyms2.isEmpty();
		if (empty1 && empty2) {
			// do nothing, as both do not have any synonyms
			return null;
		}
		synonyms1 = addLabel(x1, ontology1, synonyms1);
		synonyms2 = addLabel(x2, ontology2, synonyms2);

		List<ISynonym> results = new ArrayList<ISynonym>();
		for (ISynonym synonym1 : synonyms1) {
			for (ISynonym synonym2 : synonyms2) {
				Pair<Boolean, String> match = matchScopes(synonym1, synonym2);
				if (match.getOne()) {
					StringBuilder sb = new StringBuilder();
					if (prefix != null) {
						sb.append(prefix);
					}
					sb.append(synonym1.getLabel());
					if (infix != null) {
						sb.append(infix);
					}
					sb.append(synonym2.getLabel());
					if (suffix != null) {
						sb.append(suffix);
					}
					String scope = match.getTwo();
					if (defaultScope != null 
							&& !OboFormatTag.TAG_RELATED.getTag().equals(defaultScope)
							&& OboFormatTag.TAG_RELATED.getTag().equals(scope)) {
						scope = defaultScope;
					}
					addSynonym(results, scope, sb.toString(), label);
				}
			}
		}
		return results;
	}

	@Override
	public List<ISynonym> synonyms(String prefix,
			OWLObject[] terms,
			String[] defaultScopes,
			OWLGraphWrapper ontology,
			String infix,
			String suffix,
			String label)
	{
		if (terms == null || terms.length == 0) {
			return null;
		}
		final int size = terms.length;
		if (size == 1) {
			String defaultScope = null;
			if (defaultScopes != null && defaultScopes.length >= 1) {
				defaultScope = defaultScopes[0];
			}
			return synonyms(prefix, terms[0], ontology, suffix, defaultScope , label);
		}

		if (size == 2) {
			String defaultScope = null;
			if (defaultScopes != null && defaultScopes.length >= 2) {
				defaultScope = matchScopes(defaultScopes[0], defaultScopes[1]).getTwo();
			}
			return synonyms(prefix, terms[0], ontology, infix, terms[1], ontology, suffix, defaultScope, label);
		}
		List<ISynonym> result = new ArrayList<ISynonym>();
		List<OWLObject> termList = Arrays.asList(terms).subList(1, terms.length);
		OWLObject term = terms[0];
		List<ISynonym> synonyms = getSynonyms(term, ontology, null, false);
		synonyms = addLabel(term, ontology, synonyms);
		for (ISynonym synonym : synonyms) {
			StringBuilder start = new StringBuilder();
			if (prefix != null) {
				start.append(prefix);
			}
			start.append(synonym.getLabel());
			List<Pair<StringBuilder, String>> pairs = concat(start,
					synonym.getScope(),
					termList,
					ontology,
					infix);
			for (Pair<StringBuilder, String> pair : pairs) {
				StringBuilder sb = pair.getOne();
				if (suffix != null) {
					sb.append(suffix);
				}
				addSynonym(result, pair.getTwo(), sb.toString(), label);
			}
		}
		return result;
	}

	private List<Pair<StringBuilder, String>> concat(final CharSequence prefix,
			String scope,
			List<OWLObject> terms,
			OWLGraphWrapper ontology,
			String infix)
	{
		OWLObject term = terms.get(0);
		List<ISynonym> synonyms = getSynonyms(term, ontology, null, false);
		synonyms = addLabel(term, ontology, synonyms);
		List<Pair<StringBuilder, String>> result = new ArrayList<Pair<StringBuilder, String>>();
		for (ISynonym synonym : synonyms) {
			Pair<Boolean, String> pair = matchScopes(scope, synonym.getScope());
			if (pair.getOne()) {
				StringBuilder sb = new StringBuilder(prefix);
				sb.append(infix);
				sb.append(synonym.getLabel());
				if (terms.size() > 1) {
					List<Pair<StringBuilder, String>> concats = concat(sb, pair.getTwo(), terms.subList(1, terms.size()), ontology, infix);
					result.addAll(concats);
				}
				else {
					result.add(Pair.of(sb, pair.getTwo()));
				}
			}
		}
		return result;
	}

	private static final Pair<Boolean, String> MISMATCH = Pair.of(false, null);

	protected Pair<Boolean, String> matchScopes(ISynonym s1, ISynonym s2) {
		return matchScopes(s1.getScope(), s2.getScope());
	}

	protected Pair<Boolean, String> matchScopes(String scope1, String scope2) {
		if (scope1 == null) {
			scope1 = OboFormatTag.TAG_RELATED.getTag();
		}
		if (scope2 == null) {
			scope2 = OboFormatTag.TAG_RELATED.getTag();
		}
		if (scope1.equals(scope2)) {
			return Pair.of(true, scope1);
		}
		if (scope1.equals(OboFormatTag.TAG_BROAD.getTag()) || scope2.equals(OboFormatTag.TAG_BROAD.getTag())) {
			// this is the only case for a miss match
			return MISMATCH;
		}
		if (scope1.equals(OboFormatTag.TAG_EXACT.getTag())) {
			return Pair.of(true, scope2);
		}
		if (scope2.equals(OboFormatTag.TAG_EXACT.getTag())) {
			return Pair.of(true, scope1);
		}
		if (scope1.equals(OboFormatTag.TAG_NARROW.getTag())) {
			return Pair.of(true, scope2);
		}
		if (scope2.equals(OboFormatTag.TAG_NARROW.getTag())) {
			return Pair.of(true, scope1);
		}
		return Pair.of(true, OboFormatTag.TAG_RELATED.getTag());
	}

	private List<ISynonym> addLabel(OWLObject x, OWLGraphWrapper ontology, List<ISynonym> synonyms)
	{
		String label = getLabel(x, ontology);
		if (synonyms == null) {
			synonyms = new ArrayList<ISynonym>(1);
		}
		synonyms.add(new Synonym(label, OboFormatTag.TAG_EXACT.getTag(), null, null));
		return synonyms;
	}

	protected String getLabel(OWLObject x, OWLGraphWrapper ontology) {
		String label = ontology.getLabel(x);
		return label;
	}

	static List<ISynonym> addSynonym(List<ISynonym> results,
			String scope,
			final String newLabel,
			String label)
	{
		if (!newLabel.equals(label)) {
			// if by any chance a synonym has the same label, it is ignored
			// TODO what to to with categories if creating a compound synonym?
			if (scope == null) {
				scope = OboFormatTag.TAG_RELATED.getTag();
			}
			if (results == null) {
				results = new ArrayList<ISynonym>();
			}
			else {
				// check that the current synonym is not already in the list
				Collection<ISynonym> sameLabels = Collections2.filter(results, new Predicate<ISynonym>() {

					@Override
					public boolean apply(ISynonym input) {
						if (input != null) {
							return newLabel.equals(input.getLabel());
						}
						return false;
					}});
				if (sameLabels.isEmpty() == false) {
					// do not make this complicated here
					// make an arbitrary choice by removing the previous ones.
					results.removeAll(sameLabels);
				}
			}
			results.add(new Synonym(newLabel, scope, null, Collections.singleton("GOC:TermGenie")));
		}
		return results;
	}

	protected List<ISynonym> getSynonyms(OWLObject id, OWLGraphWrapper ontology, String requiredPrefix, boolean ignoreSynonyms) {
		if (ignoreSynonyms) {
			return null;
		}
		if (ontology != null) {
			List<ISynonym> oboSynonyms = ontology.getOBOSynonyms(id);
			if (oboSynonyms != null) {
				// defensive copy
				oboSynonyms = new ArrayList<ISynonym>(oboSynonyms);
				if (requiredPrefix != null) {
					Iterator<ISynonym> iterator = oboSynonyms.iterator();
					while (iterator.hasNext()) {
						ISynonym synonym = iterator.next();
						if (!synonym.getLabel().startsWith(requiredPrefix)) {
							iterator.remove();
						}
					}
				}
				if (removeCategories) {
					// remove synonyms which have a synonym category or the wrong one
					Iterator<ISynonym> iterator = oboSynonyms.iterator();
					while (iterator.hasNext()) {
						ISynonym synonym = iterator.next();
						String category = synonym.getCategory();
						if (category != null) {
							if (categories == null || categories.contains(category)) {
								iterator.remove();	
							}
						}
					}
				}
				else if (requireCategories && categories != null) {
					// remove synonyms which do not have a synonym category or the wrong one
					Iterator<ISynonym> iterator = oboSynonyms.iterator();
					while (iterator.hasNext()) {
						ISynonym synonym = iterator.next();
						String category = synonym.getCategory();
						if (category == null || !categories.contains(category)) {
							iterator.remove();
						}
					}
				}
				if (filterNonAsciiSynonyms && oboSynonyms.isEmpty() == false) {
					Iterator<ISynonym> iterator = oboSynonyms.iterator();
					while (iterator.hasNext()) {
						ISynonym synonym = iterator.next();
						String label = synonym.getLabel();
						if (StringUtils.isAsciiPrintable(label) == false) {
							iterator.remove();
						}
					}
				}
			}
			return oboSynonyms;
		}
		return null;
	}
	
	private boolean requireCategories = false;
	private boolean removeCategories = false;
	private Set<String> categories = null;

	@Override
	public void setSynonymFilters(boolean requireCategories,
			boolean removeCategories,
			String[] categories)
	{
		this.requireCategories = requireCategories;
		this.removeCategories = removeCategories;
		this.categories = null;
		if (categories != null && categories.length > 0) {
			this.categories = new HashSet<String>(Arrays.asList(categories));
		}
		
	}

	@Override
	public void resetSynonymFilters() {
		requireCategories = false;
		removeCategories = false;
		categories = null;
	}
}
