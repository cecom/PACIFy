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



public class CycleDetectRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String            property;
    private String            cycle;

    public CycleDetectRuntimeException() {
        super();
    }

    public CycleDetectRuntimeException(String property, String cycle) {
        super("You have a cycle reference in property [" + property + "] ["
                + cycle + "].");
        this.setProperty(property);
        this.setCycle(cycle);
    }

    public CycleDetectRuntimeException(String message) {
        super(message);
    }

    public CycleDetectRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CycleDetectRuntimeException(Throwable cause) {
        super(cause);
    }

    public String getProperty() {
        return property;
    }

    private void setProperty(String property) {
        this.property = property;
    }

    public String getCycle() {
        return cycle;
    }

    private void setCycle(String cycle) {
        this.cycle = cycle;
    }

}
