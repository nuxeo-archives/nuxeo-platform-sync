package org.nuxeo.ecm.platform.sync.api.util;

public class MonitorProvider {

    private static SynchronizeMonitor monitor;

    public static SynchronizeMonitor getMonitor() {
        if (monitor == null) {
            monitor = new AbstractSynchronizeMonitor();
        }
        return monitor;
    }

    public synchronized static void setMonitor(SynchronizeMonitor monitor) {
        if (MonitorProvider.monitor != null) {
            throw new IllegalStateException("");
        }
        MonitorProvider.monitor = monitor;
    }

    public synchronized static void disposeMonitor() {
        MonitorProvider.monitor = null;
    }
}
