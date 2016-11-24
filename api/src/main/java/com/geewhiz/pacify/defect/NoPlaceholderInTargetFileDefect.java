package com.geewhiz.pacify.defect;

import com.geewhiz.pacify.defect.DefectException;
import com.geewhiz.pacify.model.PProperty;

public class NoPlaceholderInTargetFileDefect extends DefectException {

    private static final long serialVersionUID = 1L;

    public NoPlaceholderInTargetFileDefect(PProperty pProperty) {
        super(pProperty);
    }

}