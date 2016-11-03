package com.geewhiz.pacify.defect;

import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.model.PProperty;

public class PropertyHasCycleDefect extends DefectException {

    private static final long serialVersionUID = 1L;

    private String            property;
    private String            cycle;

    public PropertyHasCycleDefect(PMarker pMarker, String property, String cycle) {
        super(pMarker);
        this.property = property;
        this.cycle = cycle;
    }

    public PropertyHasCycleDefect(PProperty pProperty, String property, String cycle) {
        super(pProperty);
        this.property = property;
        this.cycle = cycle;
    }

    @Override
    public String getDefectMessage() {
        return super.getDefectMessage() + String.format("\n\t[Property=%s]\n\t[cycle=%s]", getProperty(), getCycle());
    }

    public String getCycle() {
        return cycle;
    }

    public String getProperty() {
        return property;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((cycle == null) ? 0 : cycle.hashCode());
        result = prime * result + ((property == null) ? 0 : property.hashCode());
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
        PropertyHasCycleDefect other = (PropertyHasCycleDefect) obj;
        if (cycle == null) {
            if (other.cycle != null) {
                return false;
            }
        } else if (!cycle.equals(other.cycle)) {
            return false;
        }
        if (property == null) {
            if (other.property != null) {
                return false;
            }
        } else if (!property.equals(other.property)) {
            return false;
        }
        return true;
    }

}
