/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.web.click.editor.completion;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import org.netbeans.api.java.source.ClassIndex;
import org.netbeans.api.java.source.ClassIndex.SearchScope;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.Task;
import org.netbeans.modules.web.click.editor.JavaUtils;
import org.netbeans.spi.editor.completion.CompletionProvider;

/**
 *
 * @author Rohan Ranade (Rohan.Ranade@Sun.COM)
 */
public class JavaPackageCompletor extends Completor {

    private static final Set<SearchScope> ALL = EnumSet.allOf(SearchScope.class);
    private static final Set<SearchScope> LOCAL = EnumSet.of(SearchScope.SOURCE);
   
    public JavaPackageCompletor(int invocationOffset) {
        super(invocationOffset);
    }

    @Override
    protected int initAnchorOffset(CompletionContext context) {
        int idx = context.getTokenOffset() + 1;
        String typedChars = context.getTypedPrefix();
        if (typedChars.contains(".") || typedChars.equals("")) {
            int dotIndex = typedChars.lastIndexOf(".");
            idx += dotIndex + 1;
        }

        return idx;
    }

    @Override
    protected void compute(CompletionContext context) throws IOException {
        final String typedChars = context.getTypedPrefix();

        JavaSource js = JavaUtils.getJavaSource(context.getFileObject());
        if (js == null) {
            return;
        }


        if (typedChars.contains(".") || typedChars.equals("")) { // Switch to normal completion
            doNormalJavaCompletion(js, typedChars, context.getTokenOffset() + 1);
        } else { // Switch to smart class path completion
            doSmartJavaCompletion(js, typedChars, context.getTokenOffset() + 1, context.getQueryType());
        }
    }

    private void doNormalJavaCompletion(JavaSource js, final String typedPrefix, final int substitutionOffset) throws IOException {
        js.runUserActionTask(new Task<CompilationController>() {

            public void run(CompilationController cc) throws Exception {
                if (isCancelled()) {
                    return;
                }

                cc.toPhase(Phase.ELEMENTS_RESOLVED);
                ClassIndex ci = cc.getClasspathInfo().getClassIndex();
                int index = substitutionOffset;
                String packName = typedPrefix;
                int dotIndex = typedPrefix.lastIndexOf('.'); // NOI18N

                if (dotIndex != -1) {
                    index += (dotIndex + 1);  // NOI18N

                    packName = typedPrefix.substring(0, dotIndex);
                }
                addPackages(ci, typedPrefix, index, CompletionProvider.COMPLETION_ALL_QUERY_TYPE);

                if (isCancelled()) {
                    return;
                }
            }
        }, true);
    }

    private void doSmartJavaCompletion(final JavaSource js, final String typedPrefix, final int substitutionOffset, final int queryType) throws IOException {
        js.runUserActionTask(new Task<CompilationController>() {

            public void run(CompilationController cc) throws Exception {
                if (isCancelled()) {
                    return;
                }

                cc.toPhase(Phase.ELEMENTS_RESOLVED);

                ClassIndex ci = cc.getClasspathInfo().getClassIndex();
                // add packages
                addPackages(ci, typedPrefix, substitutionOffset, queryType);
                if (isCancelled()) {
                    return;
                }
            }
        }, true);
    }

    @Override
    protected void setAdditionalItems(boolean additionalItems) {
        super.setAdditionalItems(false);
    }

  

    private void addPackages(ClassIndex ci, String typedPrefix, int substitutionOffset, int queryType) {
        Set<SearchScope> scope = (queryType == CompletionProvider.COMPLETION_ALL_QUERY_TYPE) ? ALL : LOCAL;
        Set<String> packages = ci.getPackageNames(typedPrefix, true, scope);
        for (String pkg : packages) {
            if (pkg.length() > 0) {
                ClickCompletionItem item = ClickCompletionItem.createPackageItem(substitutionOffset, pkg, false);
                addCacheItem(item);
            }
        }
    }

}
