package org.nuxeo.ecm.platform;

import java.io.File;
import java.net.URL;

import org.junit.Test;
import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.nuxeo.ecm.platform.sync.processor.TupleProcessorUpdate;

public class TestZipProc {

    public TestZipProc() {
        BasicConfigurator.configure();
    }

    @Test
    public void testOpenZip() throws Exception {
        URL archiveUrl = getClass().getClassLoader().getResource(
                "org/nuxeo/test/document.zip");
//        TupleProcessorUpdate.checkZippedDocument(new File(archiveUrl.getFile()));
    }
}
