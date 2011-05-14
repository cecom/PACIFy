package de.oppermann.maven.pflist;

import de.oppermann.maven.pflist.property.PropertyFileProperties;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * User: sop
 * Date: 12.05.11
 * Time: 15:55
 */
public class PropertyFileLoaderTest {

    Properties allPropertiesShouldLookLike = new Properties();
    Properties child1PropertiesShouldLookLike = new Properties();
    Properties child2PropertiesShouldLookLike = new Properties();
    Properties childOfChildPropertiesShouldLookLike = new Properties();
    Properties basePropertiesShouldLookLike = new Properties();

    @Before
    public void setUp() throws Exception {
        basePropertiesShouldLookLike.put("env.name", "baseEnvName");
        basePropertiesShouldLookLike.put("SomeBaseProperty", "SomeBasePropertyValue");

        child1PropertiesShouldLookLike.put("env.name", "child1EnvName");
        child1PropertiesShouldLookLike.put("SomeChild1Property", "SomeChild1PropertyValue");

        child2PropertiesShouldLookLike.put("env.name", "child2EnvName");
        child2PropertiesShouldLookLike.put("SomeChild2Property", "SomeChild2PropertyValue");

        childOfChildPropertiesShouldLookLike.put("env.name", "ChildOfChildEnv");
        childOfChildPropertiesShouldLookLike.put("SomeChildOfChildProperty", "SomeChildOfChildPropertyValue");

        allPropertiesShouldLookLike.putAll(basePropertiesShouldLookLike);
        allPropertiesShouldLookLike.putAll(child1PropertiesShouldLookLike);
        allPropertiesShouldLookLike.putAll(child2PropertiesShouldLookLike);
        allPropertiesShouldLookLike.putAll(childOfChildPropertiesShouldLookLike);
    }

    @Test
    public void testWithClasspath() {
        URL url = this.getClass().getClassLoader().getResource("properties/subfolder/ChildOfChilds.properties");

        PropertyFileProperties propertyFileProperties = new PropertyFileProperties(url);

        assertEquals(allPropertiesShouldLookLike, propertyFileProperties.getProperties());
        assertEquals(childOfChildPropertiesShouldLookLike, propertyFileProperties.getLocalProperties());
        assertEquals(child1PropertiesShouldLookLike, propertyFileProperties.getParentPropertyFilePropertieses().get(0).getLocalProperties());
        assertEquals(child2PropertiesShouldLookLike, propertyFileProperties.getParentPropertyFilePropertieses().get(1).getLocalProperties());
        assertEquals(basePropertiesShouldLookLike, propertyFileProperties.getParentPropertyFilePropertieses().get(0).getParentPropertyFilePropertieses().get(0).getLocalProperties());
    }
}
