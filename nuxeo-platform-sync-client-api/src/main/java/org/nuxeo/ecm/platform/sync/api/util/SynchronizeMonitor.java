package org.nuxeo.ecm.platform.sync.api.util;

public interface SynchronizeMonitor {

    public boolean isCanceled();

    public void setCanceled(boolean value);

    public void beginTask(String name, int totalWork);

    public void setTaskName(String value);

    public void subTask(String value);

    public void worked(int work);

    public void done();

}
