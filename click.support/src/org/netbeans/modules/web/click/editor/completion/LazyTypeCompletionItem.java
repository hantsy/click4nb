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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.IOException;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import javax.swing.text.JTextComponent;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.Task;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.LazyCompletionItem;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.util.Exceptions;

/**
 * Based on LazyTypeCompletionItem from Java Editor module
 * 
 * @author Rohan Ranade (Rohan.Ranade@Sun.COM)
 */
public class LazyTypeCompletionItem extends ClickCompletionItem implements LazyCompletionItem {

    public static LazyTypeCompletionItem create(int substitutionOffset, ElementHandle<TypeElement> eh, JavaSource javaSource) {
        return new LazyTypeCompletionItem(substitutionOffset, eh, javaSource);
    }
    
    private ElementHandle<TypeElement> eh;
    private JavaSource javaSource;
    private String name;
    private ClickCompletionItem delegate;
    private String simpleName;
    private String pkgName;
    private int prefWidth = -1;
    private String sortText;
    
    private LazyTypeCompletionItem(int substitutionOffset, ElementHandle<TypeElement> eh, JavaSource javaSource) {
        super(substitutionOffset);
        this.eh = eh;
        this.javaSource = javaSource;
        this.name = eh.getQualifiedName();
        int idx = name.lastIndexOf('.'); //NOI18N
        this.simpleName = idx > -1 ? name.substring(idx + 1) : name;
        this.pkgName = idx > -1 ? name.substring(0, idx) : ""; //NOI18N
        this.sortText = this.simpleName + ClickCompletionItem.getImportanceLevel(this.pkgName) + "#" + this.pkgName; // NOI18N
    }

    public boolean accept() {
        if(eh != null) {
            try {
                javaSource.runUserActionTask(new Task<CompilationController>() {
                    public void run(CompilationController cc) throws Exception {
                        cc.toPhase(Phase.ELEMENTS_RESOLVED);
                        TypeElement e = eh.resolve(cc);
                        if (e != null && isAccessibleClass(e)) {
                            delegate = ClickCompletionItem.createTypeItem(substitutionOffset,
                                        e, eh, cc.getElements().isDeprecated(e), true);
                        }
                        eh = null;
                    }
                }, true);
            } catch (IOException ioe) {
                Exceptions.printStackTrace(ioe);
            }
        }
        
        return delegate != null;
    }
    
    private static boolean isAccessibleClass(TypeElement te) {
        NestingKind nestingKind = te.getNestingKind();
        return (nestingKind == NestingKind.TOP_LEVEL) || (nestingKind == NestingKind.MEMBER && te.getModifiers().contains(Modifier.STATIC));
    }
    
    public int getSortPriority() {
        return 200;
    }

    public CharSequence getSortText() {
        return sortText;
    }

    public CharSequence getInsertPrefix() {
        return simpleName;
    }

    @Override
    public CompletionTask createDocumentationTask() {
        if(delegate != null) {
            return delegate.createDocumentationTask();
        }
        
        return null;
    }

    @Override
    public CompletionTask createToolTipTask() {
        if(delegate != null) {
            return delegate.createToolTipTask();
        }
        
        return null;
    }

    @Override
    public void defaultAction(JTextComponent component) {
        if (delegate != null)
            delegate.defaultAction(component);
    }

    @Override
    public void processKeyEvent(KeyEvent evt) {
        if (delegate != null)
            delegate.processKeyEvent(evt);
    }

    @Override
    public int getPreferredWidth(Graphics g, Font defaultFont) {
        if (prefWidth < 0)
            prefWidth = CompletionUtilities.getPreferredWidth(simpleName + " (" + pkgName + ")", null, g, defaultFont); //NOI18N
        return prefWidth;
    }

    @Override
    public void render(Graphics g, Font defaultFont, Color defaultColor, Color backgroundColor, int width, int height, boolean selected) {
        if (delegate != null)
            delegate.render(g, defaultFont, defaultColor, backgroundColor, width, height, selected);
    }
}
