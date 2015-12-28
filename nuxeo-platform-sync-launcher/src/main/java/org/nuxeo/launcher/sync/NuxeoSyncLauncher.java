/*
 * (C) Copyright 2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Sun Seng David TAN <stan@nuxeo.com>, jcarsique
 */
package org.nuxeo.launcher.sync;

import static org.nuxeo.launcher.NuxeoLauncher.createLauncher;
import static org.nuxeo.launcher.NuxeoLauncher.launch;
import static org.nuxeo.launcher.NuxeoLauncher.printLongHelp;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.apache.commons.cli.ParseException;

import org.nuxeo.connect.update.PackageException;
import org.nuxeo.launcher.NuxeoLauncher;
import org.nuxeo.launcher.config.ConfigurationException;

/**
 * Customized nuxeo-launcher Main class to launch the gui with the "sync" tab.
 *
 * @author Sun Seng David TAN <stan@nuxeo.com>
 */
public class NuxeoSyncLauncher {

    public static void main(String[] args) throws ConfigurationException, ParseException, IOException,
            PackageException, GeneralSecurityException {
        if (args.length == 0) {
            printLongHelp();
            return;
        }
        final NuxeoLauncher launcher = createLauncher(args);
        if (launcher.isUsingGui()) {
            launcher.setGUI(new NuxeoLauncherSyncGUI(launcher));
        }
        launch(launcher);
    }

}
