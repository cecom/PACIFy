package com.geewhiz.pacify.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

public class ListAppender extends AbstractAppender {

    private static final long  serialVersionUID = 1L;

    private final List<String> logMessages      = new ArrayList<String>();

    public ListAppender() {
        super("Test", null, null);
        setStarted();
    }

    public void append(LogEvent logEvent) {
        logMessages.add(logEvent.getMessage().getFormattedMessage());
    }

    public List<String> getLogMessages() {
        return logMessages;
    }

}
