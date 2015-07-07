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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.Assert;
import org.junit.Test;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.managers.FilterManager;
import com.geewhiz.pacify.model.ObjectFactory;
import com.geewhiz.pacify.model.PMarker;

public class TestWrongFilter {

    @Test
    public void test() throws Exception {
        File source = new File("target/test-classes/testWrongFilter/package/example-CMFile.pacify");

        JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        PMarker pMarker = (PMarker) jaxbUnmarshaller.unmarshal(source);
        pMarker.setFile(source);

        FilterManager manager = new FilterManager(null, pMarker);
        List<Defect> defects = manager.doFilter();

        Assert.assertEquals(1, defects.size());
        Assert.assertEquals("com.geewhiz.pacify.defect.FilterNotFoundDefect", defects.get(0).getClass().getName());
    }
}
