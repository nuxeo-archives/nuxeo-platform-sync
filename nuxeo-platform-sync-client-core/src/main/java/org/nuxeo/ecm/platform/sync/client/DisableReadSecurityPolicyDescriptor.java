package org.nuxeo.ecm.platform.sync.client;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.common.xmap.annotation.XNodeList;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 */
@XObject("disableReadSecurityPolicy")
public class DisableReadSecurityPolicyDescriptor {

    @XNodeList(value = "path", type = ArrayList.class, componentType = String.class)
    protected List<String> paths = new ArrayList<String>();

    @XNodeList(value = "permission", type = ArrayList.class, componentType = String.class)
    protected List<String> permissions = new ArrayList<String>();

    public List<String> getPaths() {
        return paths;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public boolean shouldDisable(String docPath, String permission) {
        if (permissions.contains(permission)) {
            return true;
        }

        for (String path : paths) {
            if (docPath.startsWith(path)) {
                return true;
            }
        }
        return false;
    }

}
