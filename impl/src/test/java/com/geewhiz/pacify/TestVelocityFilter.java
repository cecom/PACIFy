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

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.WrongTokenDefinedDefect;
import com.geewhiz.pacify.utils.LoggingUtils;

public class TestVelocityFilter extends TestBase {

    Map<String, String> propertiesToUseWhileResolving = new HashMap<String, String>();

    @Before
    public void before() {
        Logger logger = LogManager.getLogger();
        LoggingUtils.setLogLevel(logger, Level.ERROR);

        propertiesToUseWhileResolving.put("foobar1", "foobar1Value");
        propertiesToUseWhileResolving.put("foobar2", "foobar2Value");
        propertiesToUseWhileResolving.put("jdbc.host.url", "123.123.123.133");
        propertiesToUseWhileResolving.put("jdbc.host.port", "1234");
    }

    @Test
    public void testWrongToken() throws Exception {
        String testFolder = "testVelocityFilter/wrong/wrongToken";

        LinkedHashSet<Defect> defects = createPrepareValidateAndReplace(testFolder, createPropertyResolveManager(propertiesToUseWhileResolving));

        Assert.assertEquals(1, defects.size());
        Assert.assertEquals(WrongTokenDefinedDefect.class.getName(), defects.iterator().next().getClass().getName());
    }

    // velocity checks not implemented yet
    // @Test
    // public void testNotReplacedProperty() throws Exception {
    // String testFolder = "testVelocityFilter/wrong/notReplacedProperty";
    //
    // LinkedHashSet<Defect> defects = createPrepareValidateAndReplace(testFolder, createPropertyResolveManager(propertiesToUseWhileResolving));
    //
    // Assert.assertEquals(1, defects.size());
    // Assert.assertEquals("com.geewhiz.pacify.defect.NotReplacedPropertyDefect", defects.iterator().next().getClass().getName());
    // }

    @Test
    public void testSimpleReplacement() throws Exception {
        String testFolder = "testVelocityFilter/correct/simple";

        LinkedHashSet<Defect> defects = createPrepareValidateAndReplace(testFolder, createPropertyResolveManager(propertiesToUseWhileResolving));

        Assert.assertEquals(0, defects.size());

        checkIfResultIsAsExpected(testFolder);
    }

    @Test
    public void testWithIfReplacement() throws Exception {
        String testFolder = "testVelocityFilter/correct/ifCondition";

        Map<String, String> useProperties = new HashMap<String, String>(propertiesToUseWhileResolving);
        useProperties.put("use.jdbc", "true");

        LinkedHashSet<Defect> defects = createPrepareValidateAndReplace(testFolder, createPropertyResolveManager(useProperties));

        Assert.assertEquals(0, defects.size());

        checkIfResultIsAsExpected(testFolder);
    }

    @Test
    public void testWithIfElseReplacement() throws Exception {
        String testFolder = "testVelocityFilter/correct/ifElseCondition";

        Map<String, String> useProperties = new HashMap<String, String>(propertiesToUseWhileResolving);
        useProperties.put("use.jdbc", "false");

        LinkedHashSet<Defect> defects = createPrepareValidateAndReplace(testFolder, createPropertyResolveManager(useProperties));

        Assert.assertEquals(0, defects.size());

        checkIfResultIsAsExpected(testFolder);
    }

    @Test
    public void testForEach() throws Exception {
        String testFolder = "testVelocityFilter/correct/forEachCondition";

        Map<String, String> useProperties = new HashMap<String, String>(propertiesToUseWhileResolving);
        useProperties.put("a.list", "1,2,3,foo,bar");

        LinkedHashSet<Defect> defects = createPrepareValidateAndReplace(testFolder, createPropertyResolveManager(useProperties));

        Assert.assertEquals(0, defects.size());

        checkIfResultIsAsExpected(testFolder);
    }
}
