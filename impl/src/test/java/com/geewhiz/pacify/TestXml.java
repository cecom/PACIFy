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

import org.junit.Assert;
import org.junit.Test;

import com.geewhiz.pacify.managers.EntityManager;
import com.geewhiz.pacify.model.PMarker;

public class TestXml {

    @Test
    public void testAll() {
        File source = new File("target/test-classes/testXml/package");

        EntityManager entityManager = new EntityManager(source);
        entityManager.initialize();

        PMarker pMarker = entityManager.getPMarkers().get(0);

        Assert.assertEquals(3, pMarker.getPFiles().size());

        Assert.assertEquals("someConf.conf", pMarker.getPFiles().get(0).getRelativePath());
        Assert.assertEquals("subfolder/someOtherConf.conf", pMarker.getPFiles().get(1).getRelativePath());
        Assert.assertEquals("someParentConf.conf", pMarker.getPFiles().get(2).getRelativePath());

        Assert.assertEquals(1, pMarker.getPFiles().get(0).getPProperties().size());
        Assert.assertEquals(1, pMarker.getPFiles().get(1).getPProperties().size());
        Assert.assertEquals(1, pMarker.getPFiles().get(2).getPProperties().size());

        Assert.assertEquals("foobar1", pMarker.getPFiles().get(0).getPProperties().get(0).getName());
        Assert.assertEquals("foobar1", pMarker.getPFiles().get(1).getPProperties().get(0).getName());
        Assert.assertEquals("foobar2", pMarker.getPFiles().get(2).getPProperties().get(0).getName());

    }
}
