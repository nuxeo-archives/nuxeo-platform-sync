package org.nuxeo.ecm.platform;

import org.apache.log4j.BasicConfigurator;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class TestZipProc {

    public TestZipProc() {
        BasicConfigurator.configure();
    }

    @Test
    public void testOpenZip() throws Exception {
        // URL archiveUrl = getClass().getClassLoader().getResource(
        // "org/nuxeo/test/document.zip");
        // TupleProcessorUpdate.checkZippedDocument(new File(archiveUrl.getFile()));
    }
}
