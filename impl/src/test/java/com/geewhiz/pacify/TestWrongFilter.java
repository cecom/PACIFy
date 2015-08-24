package com.geewhiz.pacify;

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

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.geewhiz.pacify.checks.impl.CheckCorrectPacifyFilter;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.FilterNotFoundDefect;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.test.TestUtil;

public class TestWrongFilter {

    @Test
    public void test() throws Exception {
        File source = new File("target/test-classes/testWrongFilter/package/example-CMFile.pacify");

        PMarker pMarker = TestUtil.readPMarker(source);

        CheckCorrectPacifyFilter check = new CheckCorrectPacifyFilter();
        List<Defect> defects = check.checkForErrors(pMarker);

        Assert.assertEquals(2, defects.size());
        Assert.assertEquals(FilterNotFoundDefect.class.getName(), defects.get(0).getClass().getName());
        Assert.assertEquals(FilterNotFoundDefect.class.getName(), defects.get(1).getClass().getName());
    }
}
