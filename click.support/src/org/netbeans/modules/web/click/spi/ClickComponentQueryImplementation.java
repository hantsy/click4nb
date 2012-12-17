package org.netbeans.modules.web.click.spi;

import org.netbeans.modules.web.click.api.ClickFileType;
import org.openide.filesystems.FileObject;

public interface ClickComponentQueryImplementation {

    public FileObject[] find(FileObject activeFileObject, ClickFileType clickFileType);

}
