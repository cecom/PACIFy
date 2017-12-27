/*-
 * ========================LICENSE_START=================================
 * com.geewhiz.pacify.impl
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

package com.geewhiz.pacify.exceptions;



public class ResolverRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String            resolver;
    private String            property;
    private String            message;

    public ResolverRuntimeException(String resolver, String property, String message) {
        super("We got a resolver exception \n\t[resolver=" + resolver + "]\n\t[property=" + property + "]\n\t[message=\""
                + message + "\"].");
        this.resolver = resolver;
        this.property = property;
        this.message = message;
    }

    public String getResolver() {
        return resolver;
    }

    public String getProperty() {
        return property;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
