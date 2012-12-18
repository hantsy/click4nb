/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click;

import org.openide.filesystems.MIMEResolver;
import org.openide.util.NbBundle;

/**
 *
 * @author hantsy
 */
@NbBundle.Messages({
    "CLICKMENU_RESOVLVER_NAME=Click Menu Config"})
@MIMEResolver.Registration(
   displayName = "#CLICKMENU_RESOVLVER_NAME",
resource = "MenuResolver.xml")
public class MemuMimeResolverRegistration {
}
