package org.nuxeo.ecm.platform;

import java.io.File;
import java.net.URL;

import org.apache.log4j.BasicConfigurator;
import org.nuxeo.ecm.platform.sync.processor.TupleProcessorUpdate;

import junit.framework.TestCase;

public class TestZipProc extends TestCase {
    public TestZipProc() {
        BasicConfigurator.configure();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOpenZip() throws Exception {
        URL archiveUrl = getClass().getClassLoader().getResource(
                "org/nuxeo/test/document.zip");
//        TupleProcessorUpdate.checkZippedDocument(new File(archiveUrl.getFile()));
    }
}
