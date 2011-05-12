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
public class LoadPropertyFileMojoTest extends AbstractMojoTestCase {

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
        LoadPropertyFileMojo mojo = (LoadPropertyFileMojo) lookupMojo("loadPropertyFile", "target/test-classes/LoadPropertyFile.pom");

        assertNotNull(mojo);

        try {
            mojo.execute();
            MavenProject project = (MavenProject) getVariableValueFromObject(mojo, "project");
            assertEquals(propertiesShouldLookLike, project.getProperties());
        } catch (MojoExecutionException e) {
            fail(e.getMessage());
        }
    }
}