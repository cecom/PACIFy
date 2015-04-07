package de.oppermann.maven.pflist.mavenplugin.stubs;

import org.apache.maven.plugin.testing.stubs.MavenProjectStub;

import java.util.Properties;

/**
 * User: sop
 * Date: 12.05.11
 * Time: 11:21
 */
public class PFListMavenMojoProjectStub extends MavenProjectStub {

    Properties properties = new Properties();

    /**
     * {@inheritDoc}
     */
    public Properties getProperties() {
        return properties;
    }

}