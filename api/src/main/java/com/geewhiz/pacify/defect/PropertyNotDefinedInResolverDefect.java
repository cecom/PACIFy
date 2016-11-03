package com.geewhiz.pacify.defect;

import com.geewhiz.pacify.model.PProperty;

public class PropertyNotDefinedInResolverDefect extends DefectException {

    private static final long serialVersionUID = 1L;

    private String            resolvers;

    public PropertyNotDefinedInResolverDefect(PProperty pProperty, String resolvers) {
        super(pProperty);
        this.resolvers = resolvers;
    }

    @Override
    public String getDefectMessage() {
        return super.getDefectMessage() + String.format("\n\t[resolvers=%s]", resolvers);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((resolvers == null) ? 0 : resolvers.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PropertyNotDefinedInResolverDefect other = (PropertyNotDefinedInResolverDefect) obj;
        if (resolvers == null) {
            if (other.resolvers != null) {
                return false;
            }
        } else if (!resolvers.equals(other.resolvers)) {
            return false;
        }
        return true;
    }

}
