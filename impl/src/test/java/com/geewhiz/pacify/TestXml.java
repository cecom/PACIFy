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

import static org.testng.Assert.assertEquals;

import java.io.File;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.testng.annotations.Test;

import com.geewhiz.pacify.model.PFListEntity;

public class TestXml {

    @Test
    public void testAll() {
        Serializer serializer = new Persister();
        File source = new File("target/test-classes/testXml/example-PFList.xml");

        PFListEntity pfListEntity = null;
        try {
            pfListEntity = serializer.read(PFListEntity.class, source);
        } catch (Exception e) {
            throw new RuntimeException("Couldnt read xml file.", e);
        }

        assertEquals(pfListEntity.getPfPropertyEntities().size(), 2);

        assertEquals("foobar1", pfListEntity.getPfPropertyEntities().get(0).getId());
        assertEquals("foobar2", pfListEntity.getPfPropertyEntities().get(1).getId());

        assertEquals("someConf.conf", pfListEntity.getPfPropertyEntities().get(0).getPFFileEntities().get(0)
                .getRelativePath());
        assertEquals("subfolder/someOtherConf.conf", pfListEntity.getPfPropertyEntities().get(0).getPFFileEntities()
                .get(1).getRelativePath());
        assertEquals("someParentConf.conf", pfListEntity.getPfPropertyEntities().get(1).getPFFileEntities().get(0)
                .getRelativePath());

    }

    // public void writeExampleFile() {
    // Serializer serializer = new Persister();
    // File targetFile = new File("target/example-PFList.xml");
    //
    // ArrayList<PFProperty> array = new ArrayList<PFProperty>();
    // array.add(new PFProperty("foobar1"));
    // array.add(new PFProperty("foobar2"));
    //
    // PFList pfList = new PFList(array);
    //
    // try {
    // serializer.write(pfList, targetFile);
    // } catch (Exception e) {
    // throw new RuntimeException("Exception.", e);
    // }
    //
    // }
}
