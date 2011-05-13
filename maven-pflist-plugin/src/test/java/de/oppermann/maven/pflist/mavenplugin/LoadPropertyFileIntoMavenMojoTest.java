package de.oppermann.maven.pflist.mavenplugin;


import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;

import java.util.Properties;

/**
 * User: sop
 * Date: 10.05.11
 * Time: 10:08
 */
public class LoadPropertyFileIntoMavenMojoTest extends AbstractMojoTestCase {

    Properties propertiesShouldLookLike = new Properties();

    protected void setUp() throws Exception {
        super.setUp();

        propertiesShouldLookLike.put("env.name", "ChildOfChildEnv");
        propertiesShouldLookLike.put("SomeBaseProperty", "SomeBasePropertyValue");
        propertiesShouldLookLike.put("SomeChild1Property", "SomeChild1PropertyValue");
        propertiesShouldLookLike.put("SomeChild2Property", "SomeChild2PropertyValue");
        propertiesShouldLookLike.put("SomeChildOfChildProperty", "SomeChildOfChildPropertyValue");
    }

    public void testLoadPropertyFile() throws Exception {
        LoadPropertyFileIntoMavenMojo intoMavenMojo = (LoadPropertyFileIntoMavenMojo) lookupMojo("loadPropertyFileIntoMaven", "target/test-classes/LoadPropertyFile.pom");

        assertNotNull(intoMavenMojo);

        try {
            intoMavenMojo.execute();
            MavenProject project = (MavenProject) getVariableValueFromObject(intoMavenMojo, "project");
            assertEquals(propertiesShouldLookLike, project.getProperties());
        } catch (MojoExecutionException e) {
            fail(e.getMessage());
        }
    }
}