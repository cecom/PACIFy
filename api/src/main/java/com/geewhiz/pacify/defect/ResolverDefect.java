package com.geewhiz.pacify.defect;

import com.geewhiz.pacify.model.PProperty;

public class ResolverDefect extends DefectException {

    private static final long serialVersionUID = 1L;

    private String            message;
    private String            resolver;

    public ResolverDefect(PProperty pProperty, String resolver, String message) {
        super(pProperty);
        this.resolver = resolver;
        this.message = message;
    }

    public ResolverDefect(String resolver, String message) {
        super();
        this.resolver = resolver;
        this.message = message;
    }

    @Override
    public String getDefectMessage() {
        return super.getDefectMessage() + String.format("\n\t[Resolver=%s]\n\t[message=%s]", resolver, message);

    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        result = prime * result + ((resolver == null) ? 0 : resolver.hashCode());
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
        ResolverDefect other = (ResolverDefect) obj;
        if (message == null) {
            if (other.message != null) {
                return false;
            }
        } else if (!message.equals(other.message)) {
            return false;
        }
        if (resolver == null) {
            if (other.resolver != null) {
                return false;
            }
        } else if (!resolver.equals(other.resolver)) {
            return false;
        }
        return true;
    }

}
