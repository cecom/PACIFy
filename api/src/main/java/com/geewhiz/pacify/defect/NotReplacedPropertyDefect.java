package com.geewhiz.pacify.defect;

import com.geewhiz.pacify.model.PFile;

public class NotReplacedPropertyDefect extends DefectException {

    private static final long serialVersionUID = 1L;

    private String            propertyId;

    public NotReplacedPropertyDefect(PFile pFile, String propertyId) {
        super(pFile);
        this.propertyId = propertyId;
    }

    @Override
    public String getDefectMessage() {
        return super.getDefectMessage() + String.format("\n\t[Property=%s]", getPropertyId());
    }

    public String getPropertyId() {
        return propertyId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((propertyId == null) ? 0 : propertyId.hashCode());
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
        NotReplacedPropertyDefect other = (NotReplacedPropertyDefect) obj;
        if (propertyId == null) {
            if (other.propertyId != null) {
                return false;
            }
        } else if (!propertyId.equals(other.propertyId)) {
            return false;
        }
        return true;
    }

}