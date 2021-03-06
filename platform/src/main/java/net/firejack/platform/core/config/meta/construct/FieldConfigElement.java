/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.platform.core.config.meta.construct;

import net.firejack.platform.api.registry.model.FieldType;
import net.firejack.platform.core.config.meta.IFieldElement;
import net.firejack.platform.core.utils.StringUtils;

import java.util.List;


public class FieldConfigElement extends BaseConfigElement implements IFieldElement {

    private FieldType type;
    private String typePath;
    private String customType;
    private Boolean required;
    private Boolean searchable;
    private Boolean autoGenerated;
	private String displayName;
	private String displayDescription;
    private Object defaultValue;
	private String allowValues;
    private Boolean processed;
    private List<Reference> options;

    public FieldConfigElement(String name) {
        super(name);
    }

    @Override
    public FieldType getType() {
        return type;
    }

    public void setType(FieldType type) {
        this.type = type;
    }

    @Override
    public String getTypePath() {
        return typePath;
    }

    public void setTypePath(String typePath) {
        this.typePath = typePath;
    }

    @Override
    public String getCustomType() {
        return customType;
    }

    public void setCustomType(String customType) {
        this.customType = customType;
    }

    @Override
    public boolean isRequired() {
        return required != null && required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

	public boolean isSearchable() {
		return searchable != null && searchable;
	}

	public void setSearchable(Boolean searchable) {
		this.searchable = searchable;
	}

	@Override
    public boolean isAutoGenerated() {
        return autoGenerated != null && autoGenerated;
    }

    public void setAutoGenerated(Boolean autoGenerated) {
        this.autoGenerated = autoGenerated;
    }

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayDescription() {
		return displayDescription;
	}

	public void setDisplayDescription(String displayDescription) {
		this.displayDescription = displayDescription;
	}

	@Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

	public String getAllowValues() {
		return allowValues;
	}

	public void setAllowValues(String allowValues) {
		this.allowValues = allowValues;
	}

    public Boolean getProcessed() {
        return processed;
    }

    public void setProcessed(Boolean processed) {
        this.processed = processed;
    }

    public List<Reference> getOptions() {
        return options;
    }

    public void setOptions(List<Reference> options) {
        this.options = options;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FieldConfigElement)) return false;

        FieldConfigElement fieldConfigElement = (FieldConfigElement) o;
        if (!StringUtils.equals(name, fieldConfigElement.name) || (type != fieldConfigElement.type) ||
                !StringUtils.equals(typePath, fieldConfigElement.typePath) ||
                (defaultValue != null ?
                !defaultValue.equals(fieldConfigElement.defaultValue) : fieldConfigElement.defaultValue != null)) {
            return false;
        }
        boolean thisMandatory = required != null && required;
        boolean thatMandatory = fieldConfigElement.required != null && fieldConfigElement.required;
        return thisMandatory == thatMandatory;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (typePath != null ? typePath.hashCode() : 0);
        result = 31 * result + (required != null ? required.hashCode() : 0);
        result = 31 * result + (defaultValue != null ? defaultValue.hashCode() : 0);
        return result;
    }
}