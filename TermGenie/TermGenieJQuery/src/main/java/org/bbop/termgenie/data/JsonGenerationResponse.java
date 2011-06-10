package org.bbop.termgenie.data;

import java.util.Arrays;
import java.util.Collection;

import lib.jsonrpc.JSONObj;
import lib.jsonrpc.JSONProperty;

@JSONObj
public class JsonGenerationResponse {

	@JSONProperty
	private String generalError;
	
	@JSONProperty
	private JsonValidationHint[] errors;
	
	@JSONProperty
	private String[] generatedTerms;
	
	/**
	 * Default constructor.
	 */
	@SuppressWarnings("unused")
	private JsonGenerationResponse() {
		super();
	}
	
	public JsonGenerationResponse(String generalError, Collection<JsonValidationHint> errors, Collection<String> terms) {
		if (errors != null) {
			this.errors = errors.toArray(new JsonValidationHint[errors.size()]);
		}
		if (terms != null) {
			this.generatedTerms = terms.toArray(new String[terms.size()]);
		}
		this.generalError = generalError;
	}
	
	/**
	 * @return the errors
	 */
	public JsonValidationHint[] getErrors() {
		return errors;
	}

	/**
	 * @param errors the errors to set
	 */
	public void setErrors(JsonValidationHint[] errors) {
		this.errors = errors;
	}

	/**
	 * @return the generatedTerms
	 */
	public String[] getGeneratedTerms() {
		return generatedTerms;
	}

	/**
	 * @param generatedTerms the generatedTerms to set
	 */
	public void setGeneratedTerms(String[] generatedTerms) {
		this.generatedTerms = generatedTerms;
	}

	/**
	 * @return the generalError
	 */
	public String getGeneralError() {
		return generalError;
	}

	/**
	 * @param generalError the generalError to set
	 */
	public void setGeneralError(String generalError) {
		this.generalError = generalError;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JsonGenerationResponse:{");
		if (generalError != null) {
			builder.append("generalError:");
			builder.append(generalError);
			builder.append(", ");
		}
		if (errors != null) {
			builder.append("errors:");
			builder.append(Arrays.toString(errors));
			builder.append(", ");
		}
		if (generatedTerms != null) {
			builder.append("generatedTerms:");
			builder.append(Arrays.toString(generatedTerms));
		}
		builder.append("}");
		return builder.toString();
	}
	
	
}
