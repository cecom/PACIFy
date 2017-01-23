/*-
 * ========================LICENSE_START=================================
 * com.geewhiz.pacify.resolver.file-resolver
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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.NotReplacedPropertyDefect;
import com.geewhiz.pacify.defect.PropertyNotDefinedInResolverDefect;

public class TestMissingProperty extends FileResolverTestBase  {

    @Test
    public void checkForNotCorrect() {
        String testFolder = "testMissingProperty";

        LinkedHashSet<Defect> result = createPrepareValidateAndReplace(testFolder, createPropertyResolveManager(testFolder));
        List<Defect> defects = new ArrayList<Defect>(result);

        Assert.assertEquals(PropertyNotDefinedInResolverDefect.class, defects.get(0).getClass());
        Assert.assertEquals(2, defects.size());
        Assert.assertEquals("foobar5", ((PropertyNotDefinedInResolverDefect) defects.get(0)).getPProperty().getName());

        Assert.assertEquals(NotReplacedPropertyDefect.class, defects.get(1).getClass());
        Assert.assertEquals("foobar5", ((NotReplacedPropertyDefect) defects.get(1)).getPropertyId());
    }

    
}
