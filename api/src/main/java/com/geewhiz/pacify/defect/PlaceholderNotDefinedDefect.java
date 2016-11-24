package com.geewhiz.pacify.defect;

import com.geewhiz.pacify.defect.DefectException;
import com.geewhiz.pacify.model.PFile;

public class PlaceholderNotDefinedDefect extends DefectException {

    private static final long serialVersionUID = 1L;

    private String            placeHolder;

    public PlaceholderNotDefinedDefect(PFile pFile, String placeHolder) {
        super(pFile);
        this.placeHolder = placeHolder;
    }

    public String getPlaceHolder() {
        return placeHolder;
    }

    @Override
    public String getDefectMessage() {
        return super.getDefectMessage() + String.format("\n\t[Property=%s]", placeHolder)
                + String.format("\n\t[Message=Placeholder is not defined in pacify marker file.]");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((placeHolder == null) ? 0 : placeHolder.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        PlaceholderNotDefinedDefect other = (PlaceholderNotDefinedDefect) obj;
        if (placeHolder == null) {
            if (other.placeHolder != null)
                return false;
        } else if (!placeHolder.equals(other.placeHolder))
            return false;
        return true;
    }

}