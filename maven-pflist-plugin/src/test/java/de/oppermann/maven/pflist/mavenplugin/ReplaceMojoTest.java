package de.oppermann.maven.pflist.mavenplugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;

import java.util.Properties;

/**
 * User: sop
 * Date: 14.05.11
 * Time: 10:05
 */
public class ReplaceMojoTest extends AbstractMojoTestCase {

    Properties propertiesShouldLookLike = new Properties();

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testLoadPropertyFile() throws Exception {
        ReplaceMojo replaceMojo = (ReplaceMojo) lookupMojo("replace", "target/test-classes/Replace.pom");

        assertNotNull(replaceMojo);

        try {
            replaceMojo.execute();
        } catch (MojoExecutionException e) {
            fail(e.getMessage());
        }
    }
}
