/*-
 * ========================LICENSE_START=================================
 * com.geewhiz.pacify.api
 * %%
 * Copyright (C) 2011 - 2017 Sven Oppermann
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

package com.geewhiz.pacify.defect;

import com.geewhiz.pacify.defect.DefectException;
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
