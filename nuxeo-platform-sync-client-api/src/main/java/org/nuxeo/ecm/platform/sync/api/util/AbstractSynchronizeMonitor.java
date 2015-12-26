/*
 * (C) Copyright 2010 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Thierry Martins
 */
package org.nuxeo.ecm.platform.sync.api.util;

public class AbstractSynchronizeMonitor implements SynchronizeMonitor {

    public void beginTask(String name, int totalWork) {

    }

    public void done() {

    }

    public boolean isCanceled() {
        return false;
    }

    public void setCanceled(boolean value) {

    }

    public void setTaskName(String value) {

    }

    public void subTask(String value) {

    }

    public void worked(int work) {

    }

}
