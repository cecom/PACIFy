package com.geewhiz.pacify.mavenplugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import com.geewhiz.pacify.mavenplugin.GenerateFinalPropertyFile;

import java.util.Properties;

/**
 * User: sop
 * Date: 24.08.11
 * Time: 14:42
 */
public class GenerateFinalPropertyFileMojoTest extends AbstractMojoTestCase {

    Properties propertiesShouldLookLike = new Properties();

    protected void setUp() throws Exception {
        super.setUp();

        propertiesShouldLookLike.put("env.name", "ChildOfChildEnv");
        propertiesShouldLookLike.put("SomeBaseProperty", "SomeBasePropertyValue");
        propertiesShouldLookLike.put("SomeChild1Property", "SomeChild1PropertyValue");
        propertiesShouldLookLike.put("SomeChild2Property", "SomeChild2PropertyValue");
        propertiesShouldLookLike.put("SomeChildOfChildProperty", "SomeChildOfChildPropertyValue");
    }


    public void testPseudo() {

    }

    public void doesNotWorkGeneratePropertyFile() throws Exception {
        GenerateFinalPropertyFile mavenMojo = (GenerateFinalPropertyFile) lookupMojo("generateFinalPropertyFile", "target/test-classes/GenerateFinalPropertyFile.pom");

        assertNotNull(mavenMojo);

        try {
            mavenMojo.execute();
//            MavenProject project = (MavenProject) getVariableValueFromObject(mavenMojo, "project");
//            assertEquals(propertiesShouldLookLike, project.getProperties());
        } catch (MojoExecutionException e) {
            fail(e.getMessage());
        }
    }
}
