/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.web.click.refactorings;

import java.text.MessageFormat;
import org.netbeans.modules.refactoring.api.AbstractRefactoring;
import org.netbeans.modules.refactoring.api.Problem;
import org.netbeans.modules.refactoring.api.WhereUsedQuery;
import org.netbeans.modules.refactoring.spi.RefactoringElementsBag;
import org.netbeans.modules.web.click.api.model.ClickApp;
import org.netbeans.modules.web.click.api.model.ClickModel;
import org.netbeans.modules.web.click.api.model.ClickVisitor;
import org.netbeans.modules.web.click.api.model.Control;
import org.netbeans.modules.web.click.api.model.Controls;
import org.netbeans.modules.web.click.api.model.FileUploadService;
import org.netbeans.modules.web.click.api.model.Format;
import org.netbeans.modules.web.click.api.model.LogService;
import org.netbeans.modules.web.click.api.model.Page;
import org.netbeans.modules.web.click.api.model.Pages;
import org.netbeans.modules.web.click.api.model.TemplateService;
import org.netbeans.modules.web.click.api.model.impl.ClickComponentImpl;
import org.netbeans.modules.web.click.api.model.impl.ControlImpl;
import org.netbeans.modules.web.click.api.model.impl.FileUploadServiceImpl;
import org.netbeans.modules.web.click.api.model.impl.FormatImpl;
import org.netbeans.modules.web.click.api.model.impl.LogServiceImpl;
import org.netbeans.modules.web.click.api.model.impl.PageImpl;
import org.netbeans.modules.web.click.api.model.impl.TemplateServiceImpl;
import org.openide.filesystems.FileObject;
import org.openide.text.PositionBounds;
import org.openide.util.NbBundle;

/**
 *
 * @author Erno Mononen
 */
public class ClickXmlWhereUsed extends ClickXmlRefactoring {

    private final WhereUsedQuery whereUsedQuery;
    private final String clazzFqn;

    public ClickXmlWhereUsed(FileObject clickFO, String clazzFqn, WhereUsedQuery whereUsedQuery) {
        super(clickFO);
        this.clazzFqn = clazzFqn;
        this.whereUsedQuery = whereUsedQuery;
    }

    public Problem prepare(final RefactoringElementsBag bag) {
        new ClickVisitor.Deep() {

            @Override
            public void accept(ClickApp component) {
                visitChild(component);
            }

            @Override
            public void accept(Pages component) {
                visitChild(component);
            }

            @Override
            public void accept(Controls component) {
               visitChild(component);
            }


            private void scanWhereUsedClassName(ClickModel clickModel) {
                clickModel.getRootComponent().accept(this);
            }

            @Override
            public void accept(Page component) {
                if (clazzFqn.equals(component.getClassName())) {
                    bag.add(whereUsedQuery, new WhereUsedElement(clickFO, clickModel, "TXT_WhereUsedInPage", clazzFqn, (PageImpl) component));
                }
            }

            @Override
            public void accept(Control component) {
                if (clazzFqn.equals(component.getClassName())) {
                    bag.add(whereUsedQuery, new WhereUsedElement(clickFO, clickModel, "TXT_WhereUsedInControl", clazzFqn, (ControlImpl) component));
                }
            }

            @Override
            public void accept(Format component) {
                if (clazzFqn.equals(component.getClassName())) {
                    bag.add(whereUsedQuery, new WhereUsedElement(clickFO, clickModel, "TXT_WhereUsedInFormat", clazzFqn, (FormatImpl) component));
                }
            }

            @Override
            public void accept(FileUploadService component) {
                if (clazzFqn.equals(component.getClassName())) {
                    bag.add(whereUsedQuery, new WhereUsedElement(clickFO, clickModel, "TXT_WhereUsedInFileUploadService", clazzFqn, (FileUploadServiceImpl) component));
                }
            }

            @Override
            public void accept(LogService component) {
                if (clazzFqn.equals(component.getClassName())) {
                    bag.add(whereUsedQuery, new WhereUsedElement(clickFO, clickModel, "TXT_WhereUsedInLogService", clazzFqn, (LogServiceImpl) component));
                }
            }

            @Override
            public void accept(TemplateService component) {
                if (clazzFqn.equals(component.getClassName())) {
                    bag.add(whereUsedQuery, new WhereUsedElement(clickFO, clickModel, "TXT_WhereUsedInTemplateService", clazzFqn, (TemplateServiceImpl) component));
                }
            }
        }.scanWhereUsedClassName(clickModel);
        return null;
    }

    @Override
    AbstractRefactoring getRefactoring() {
        return this.whereUsedQuery;
    }



    private class WhereUsedElement extends ClickRefactoringElement {

        private final String clazz;
        private final String bundleKey;
        private final ClickComponentImpl component;

        public WhereUsedElement(FileObject clickFO, ClickModel clickModel, String bundleKey, String clazz, ClickComponentImpl component) {
            super(clickFO, clickModel);
            this.bundleKey = bundleKey;
            this.clazz = clazz;
            this.component = component;
        }

        public String getDisplayText() {
            Object[] args = new Object[]{clazz};
            return MessageFormat.format(NbBundle.getMessage(ClickXmlWhereUsed.class, bundleKey), args);
        }

        protected String getName() {
            return clazz;
        }

        @Override
        public PositionBounds getPosition() {
            return createClassnamePosistionBounds(component);
        }
    }
}
