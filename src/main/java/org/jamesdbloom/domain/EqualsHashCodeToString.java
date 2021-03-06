package org.jamesdbloom.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * @author jamesdbloom
 */
public abstract class EqualsHashCodeToString implements Serializable {

    protected transient Logger logger = LoggerFactory.getLogger(this.getClass());

    static {
        ReflectionToStringBuilder.setDefaultStyle(ToStringStyle.MULTI_LINE_STYLE);
    }

    protected String[] fieldsExcludedFromEqualsAndHashCode() {
        return new String[]{"logger"};
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toStringExclude(this, fieldsExcludedFromEqualsAndHashCode());
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other, fieldsExcludedFromEqualsAndHashCode());
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, fieldsExcludedFromEqualsAndHashCode());
    }
}
