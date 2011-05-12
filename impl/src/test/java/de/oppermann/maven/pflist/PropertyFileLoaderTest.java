package de.oppermann.maven.pflist;

import de.oppermann.maven.pflist.property.PropertyFileLoader;
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

    Properties propertiesShouldLookLike = new Properties();

    @Before
    public void setUp() throws Exception {
        propertiesShouldLookLike.put("env.name", "ChildOfChildEnv");
        propertiesShouldLookLike.put("SomeBaseProperty", "SomeBasePropertyValue");
        propertiesShouldLookLike.put("SomeChild1Property", "SomeChild1PropertyValue");
        propertiesShouldLookLike.put("SomeChild2Property", "SomeChild2PropertyValue");
        propertiesShouldLookLike.put("SomeChildOfChildProperty", "SomeChildOfChildPropertyValue");
    }

    @Test
    public void testWithClasspath() {
        URL url = this.getClass().getClassLoader().getResource("properties/subfolder/ChildOfChilds.properties");

        PropertyFileLoader pfl = new PropertyFileLoader();
        Properties properties = pfl.loadProperties(url);

        assertEquals(propertiesShouldLookLike, properties);
    }
}
