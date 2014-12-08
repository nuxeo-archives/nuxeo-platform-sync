/*
 * (C) Copyright 2011 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Sun Seng David TAN <stan@nuxeo.com>, jcarsique
 */
package org.nuxeo.launcher.sync;

import static org.nuxeo.launcher.NuxeoLauncher.createLauncher;
import static org.nuxeo.launcher.NuxeoLauncher.launch;
import static org.nuxeo.launcher.NuxeoLauncher.printLongHelp;

import java.io.IOException;

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

    public static void main(String[] args) throws ConfigurationException, ParseException, IOException, PackageException {
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
