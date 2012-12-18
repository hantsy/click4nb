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
@NbBundle.Messages({"CLICK_RESOVLVER_NAME=Click Config File"})
@MIMEResolver.Registration(
   displayName = "#CLICK_RESOVLVER_NAME",
resource = "ClickResolver.xml")
public class ClickMimeResolverRegistration {
}
