package de.oppermann.maven.pflist;

import de.oppermann.maven.pflist.xml.PFList;
import org.junit.Assert;
import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class TestXml {

    @Test
    public void testAll() {
        Serializer serializer = new Persister();
        File source = new File("target/test-classes/testXml/example-PFList.xml");

        PFList pfList = null;
        try {
            pfList = serializer.read(PFList.class, source);
        } catch (Exception e) {
            throw new RuntimeException("Couldnt read xml file.", e);
        }

        Assert.assertEquals(pfList.getPfListProperties().size(), 2);

        Assert.assertEquals("foobar1", pfList.getPfListProperties().get(0).getId());
        Assert.assertEquals("foobar2", pfList.getPfListProperties().get(1).getId());

        Assert.assertEquals("someConf.conf", pfList.getPfListProperties().get(0).getPFFiles().get(0).getRelativePath());
        Assert.assertEquals("subfolder/someOtherConf.conf", pfList.getPfListProperties().get(0).getPFFiles().get(1).getRelativePath());
        Assert.assertEquals("someParentConf.conf", pfList.getPfListProperties().get(1).getPFFiles().get(0).getRelativePath());

    }

//    public void writeExampleFile() {
//        Serializer serializer = new Persister();
//        File targetFile = new File("target/example-PFList.xml");
//
//        ArrayList<PFProperty> array = new ArrayList<PFProperty>();
//        array.add(new PFProperty("foobar1"));
//        array.add(new PFProperty("foobar2"));
//
//        PFList pfList = new PFList(array);
//
//        try {
//            serializer.write(pfList, targetFile);
//        } catch (Exception e) {
//            throw new RuntimeException("Exception.", e);
//        }
//
//    }
}
