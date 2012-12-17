package org.netbeans.modules.web.click.api;

import java.util.Collection;
import org.netbeans.modules.web.click.spi.ClickComponentQueryImplementation;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

public final class ClickComponentQuery {

    private ClickComponentQuery() {
    }

    public static FileObject[] findComponent(FileObject activeFileObject, ClickFileType clickFileType) {
        Collection<? extends ClickComponentQueryImplementation> impls = Lookup.getDefault().lookupAll(ClickComponentQueryImplementation.class);

        FileObject[] result = null;
        for (ClickComponentQueryImplementation impl : impls) {
            result = impl.find(activeFileObject, clickFileType);
            if (result != null) {
                break;
            }
        }
        return result;
    }
}
