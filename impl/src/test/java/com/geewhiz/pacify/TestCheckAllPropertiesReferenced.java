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

import com.geewhiz.pacify.checks.impl.CheckPlaceholderExistsInTargetFile;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.PlaceholderNotDefinedDefect;

public class TestCheckAllPropertiesReferenced extends TestBase {
    @Test
    public void checkForNotCorrect() {
        File testStartPath = new File("target/test-classes/checkAllPropertiesReferenced/wrong/package");

        LinkedHashSet<Defect> result = getDefects(new CheckPlaceholderExistsInTargetFile(), testStartPath);
        List<Defect> defects = new ArrayList<Defect>(result);

        Assert.assertEquals("We should get a defect.", 1, defects.size());
        Assert.assertEquals("We expect PlaceholderNotDefinedDefect.", PlaceholderNotDefinedDefect.class, defects.get(0).getClass());
        Assert.assertEquals("We expect placeholder \"foobar3\" to be not defined..", "foobar3",
                ((PlaceholderNotDefinedDefect) defects.get(0)).getPlaceHolder());
    }

    @Test
    public void checkForCorrect() {
        File testStartPath = new File("target/test-classes/checkAllPropertiesReferenced/correct/package");

        LinkedHashSet<Defect> defects = getDefects(new CheckPlaceholderExistsInTargetFile(), testStartPath);

        Assert.assertEquals(0, defects.size());
    }

}
