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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.NotReplacedPropertyDefect;

public class TestNotReplacedProperty extends FileResolverTestBase {

    @Test
    public void checkForNotCorrect() {
        String testFolder = "notReplacedPropertyTest";

        LinkedHashSet<Defect> result = createPrepareValidateAndReplace(testFolder, createPropertyResolveManager(testFolder));
        List<Defect> defects = new ArrayList<Defect>(result);

        Assert.assertThat(defects, hasSize(1));

        Assert.assertThat(defects.toArray()[0], IsInstanceOf.instanceOf(NotReplacedPropertyDefect.class));

        Assert.assertThat(((NotReplacedPropertyDefect) defects.iterator().next()).getPropertyId(), is("aReference"));
    }

}
