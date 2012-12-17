/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package org.netbeans.modules.web.click.editor.completion;

import java.util.HashMap;
import java.util.Map;
import org.netbeans.modules.web.click.api.model.impl.ClickAttributes;
import org.netbeans.modules.web.click.api.model.impl.ClickQNames;
import org.netbeans.modules.web.click.api.model.impl.MenuAttributes;
import org.netbeans.modules.web.click.api.model.impl.MenuQNames;
import org.netbeans.modules.web.click.editor.StringUtils;

/**
 * Copied from struts2 module.
 * @author Rohan Ranade (Rohan.Ranade@Sun.COM)
 */
public final class CompletorRegistry {

    private static Map<String, CompletorFactory> completorFactories = new HashMap<String, CompletorFactory>();

    private CompletorRegistry() {
        setupCompletors();
    }

    private void setupCompletors() {

        GenericCompletorFactory javaClassCompletorFactory = new GenericCompletorFactory(JavaClassCompletor.class);
        registerCompletorFactory(ClickQNames.PAGE, ClickAttributes.CLASSNAME, javaClassCompletorFactory);
        registerCompletorFactory(ClickQNames.FILE_UPLOAD_SERVICE, ClickAttributes.CLASSNAME, javaClassCompletorFactory);
        registerCompletorFactory(ClickQNames.LOG_SERVICE, ClickAttributes.CLASSNAME, javaClassCompletorFactory);
        registerCompletorFactory(ClickQNames.TEMPLATE_SERVICE, ClickAttributes.CLASSNAME, javaClassCompletorFactory);
        registerCompletorFactory(ClickQNames.FORMAT, ClickAttributes.CLASSNAME, javaClassCompletorFactory);

        GenericCompletorFactory javaPackageCompletorFactory = new GenericCompletorFactory(JavaPackageCompletor.class);
        registerCompletorFactory(ClickQNames.PAGES, ClickAttributes.PACKAGE, javaPackageCompletorFactory);

        GenericCompletorFactory fileCompletorFactory = new GenericCompletorFactory(ResourceCompletor.class);
        registerCompletorFactory(ClickQNames.PAGE, ClickAttributes.PATH, fileCompletorFactory);
        registerCompletorFactory(MenuQNames.MENU.getLocalName(), MenuAttributes.PATH.getName(), fileCompletorFactory);


//        AttributeValueCompletorFactory autoBindingCompletorFactory = new AttributeValueCompletorFactory(
//                new String[]{
//                    "annotation", "Enable autobinding on the field with Bindable annotation",
//                    "public", "Enable autobinding on the field with public modifier",
//                    "none", "Disable autobinding",
//                    "true", "Deprecated, use 'public' or 'annotation' instead. ",
//                    "false", "Deprecated, use 'none' instead. "
//                });
//        registerCompletorFactory(ClickQNames.PAGES, ClickAttributes.AUTOBINDING, autoBindingCompletorFactory);


    }
    private static CompletorRegistry INSTANCE = new CompletorRegistry();

    public static CompletorRegistry getDefault() {
        return INSTANCE;
    }

    public Completor getCompletor(CompletionContext context) {
        return getAttributeValueCompletor(context);
    }

    private Completor getAttributeValueCompletor(CompletionContext context) {
        String tagName = context.getTagName();
        String attrName = context.getAttrName();
        CompletorFactory completorFactory = locateCompletorFactory(tagName, attrName);
        if (completorFactory != null) {
            Completor completor = completorFactory.createCompletor(context.getCaretOffset());
            return completor;
        }

        return null;
    }

    private void registerCompletorFactory(String tagName, String attribName,
            CompletorFactory completorFactory) {
        completorFactories.put(createRegisteredName(tagName, attribName), completorFactory);
    }

    private void registerCompletorFactory(ClickQNames tagName, ClickAttributes attribName,
            CompletorFactory completorFactory) {
        completorFactories.put(createRegisteredName(tagName.getLocalName(), attribName.getName()), completorFactory);
    }

    private static String createRegisteredName(String nodeName, String attributeName) {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.hasText(nodeName)) {
            builder.append("/nodeName=");  // NOI18N
            builder.append(nodeName);
        } else {
            builder.append("/nodeName=");  // NOI18N
            builder.append("*");  // NOI18N
        }

        if (StringUtils.hasText(attributeName)) {
            builder.append("/attribute="); // NOI18N
            builder.append(attributeName);
        }

        return builder.toString();
    }

    private CompletorFactory locateCompletorFactory(String nodeName, String attributeName) {
        String key = createRegisteredName(nodeName, attributeName);
        if (completorFactories.containsKey(key)) {
            return completorFactories.get(key);
        }

        key = createRegisteredName(nodeName, null);
        if (completorFactories.containsKey(key)) {
            return completorFactories.get(key);
        }

        key = createRegisteredName("*", attributeName); // NOI18N
        if (completorFactories.containsKey(key)) {
            return completorFactories.get(key);
        }

        return null;
    }
}
