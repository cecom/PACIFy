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

package com.geewhiz.pacify;



import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.managers.EntityManager;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.PMarker;

public class TestXml {

    @Test
    public void testAll() {
        File source = new File("target/test-classes/testXml/package");

        EntityManager entityManager = new EntityManager(source);
        entityManager.initialize();

        PMarker pMarker = entityManager.getPMarkers().get(0);

        List<PFile> pFiles = entityManager.getPFilesFrom(pMarker);

        Assert.assertEquals(3, pFiles.size());

        Assert.assertEquals("someConf.conf", pFiles.get(0).getRelativePath());
        Assert.assertEquals("subfolder/someOtherConf.conf", pFiles.get(1).getRelativePath());
        Assert.assertEquals("someParentConf.conf", pFiles.get(2).getRelativePath());

        Assert.assertEquals(1, pFiles.get(0).getPProperties().size());
        Assert.assertEquals(1, pFiles.get(1).getPProperties().size());
        Assert.assertEquals(1, pFiles.get(2).getPProperties().size());

        Assert.assertEquals("foobar1", pFiles.get(0).getPProperties().get(0).getName());
        Assert.assertEquals("foobar1", pFiles.get(1).getPProperties().get(0).getName());
        Assert.assertEquals("foobar2", pFiles.get(2).getPProperties().get(0).getName());

    }

    @Test
    public void wrongXml() {
        File source = new File("target/test-classes/testWrongXmlFormat/package");

        EntityManager entityManager = new EntityManager(source);
        LinkedHashSet<Defect> result = entityManager.initialize();

        List<Defect> defects = new ArrayList<Defect>(result);

        Assert.assertEquals(1, defects.size());
        Assert.assertEquals("com.geewhiz.pacify.defect.XMLValidationDefect", defects.get(0).getClass().getName());

    }

}
