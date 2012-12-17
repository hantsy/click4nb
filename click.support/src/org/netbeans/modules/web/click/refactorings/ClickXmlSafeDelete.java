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
import java.util.List;
import org.netbeans.modules.refactoring.api.AbstractRefactoring;
import org.netbeans.modules.refactoring.api.Problem;
import org.netbeans.modules.refactoring.api.SafeDeleteRefactoring;
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
import org.netbeans.modules.web.click.api.model.impl.ClickAppImpl;
import org.netbeans.modules.web.click.api.model.impl.ClickComponentImpl;
import org.netbeans.modules.web.click.api.model.impl.ControlImpl;
import org.netbeans.modules.web.click.api.model.impl.ControlsImpl;
import org.netbeans.modules.web.click.api.model.impl.FileUploadServiceImpl;
import org.netbeans.modules.web.click.api.model.impl.FormatImpl;
import org.netbeans.modules.web.click.api.model.impl.LogServiceImpl;
import org.netbeans.modules.web.click.api.model.impl.PageImpl;
import org.netbeans.modules.web.click.api.model.impl.PagesImpl;
import org.netbeans.modules.web.click.api.model.impl.TemplateServiceImpl;
import org.openide.filesystems.FileObject;
import org.openide.text.PositionBounds;
import org.openide.util.NbBundle;

/**
 *  
 * @author hantsy
 */
public class ClickXmlSafeDelete extends ClickXmlRefactoring {

    private final SafeDeleteRefactoring safeDelete;
    private final List<String> classes;
    private final String clazz;

    public ClickXmlSafeDelete(FileObject clickFO, String clazz, SafeDeleteRefactoring safeDelete) {
        super(clickFO);
        this.safeDelete = safeDelete;
        this.clazz = clazz;
        this.classes = RefactoringUtil.getRefactoredClasses(safeDelete);
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

            void scanDeleteClassName(ClickModel clickModel) {
                clickModel.getRootComponent().accept(this);
            }

            @Override
            public void accept(Page component) {
                for (String clz : classes) {
                    if (clz.equals(component.getClassName())) {
                        bag.add(safeDelete, new PageSoftDeleteElement(clickFO, clickModel, "TXT_SoftDeletePageClass", clz, (PageImpl) component));
                    }
                }
            }

            @Override
            public void accept(Control component) {
                for (String clz : classes) {
                    if (clz.equals(component.getClassName())) {
                        bag.add(safeDelete, new ControlSoftDeleteElement(clickFO, clickModel, "TXT_SoftDeleteControlClass", clz, (ControlImpl) component));
                    }
                }
            }

            @Override
            public void accept(Format component) {
                for (String clz : classes) {
                    if (clz.equals(component.getClassName())) {
                        bag.add(safeDelete, new FormatSoftDeleteElement(clickFO, clickModel, "TXT_SoftDeleteFormatClass", clz, (FormatImpl) component));
                    }
                }
            }

            @Override
            public void accept(FileUploadService component) {
                for (String clz : classes) {
                    if (clz.equals(component.getClassName())) {
                        bag.add(safeDelete, new FileUploadServiceSoftDeleteElement(clickFO, clickModel, "TXT_SoftDeleteFileUploadServiceClass", clz, (FileUploadServiceImpl) component));
                    }
                }
            }

            @Override
            public void accept(LogService component) {
                 for (String clz : classes) {
                    if (clz.equals(component.getClassName())) {
                        bag.add(safeDelete, new LogServiceSoftDeleteElement(clickFO, clickModel, "TXT_SoftDeleteLogServiceClass", clz, (LogServiceImpl) component));
                    }
                }
            }

            @Override
            public void accept(TemplateService component) {
                 for (String clz : classes) {
                    if (clz.equals(component.getClassName())) {
                        bag.add(safeDelete, new TemplateServiceSoftDeleteElement(clickFO, clickModel, "TXT_SoftDelete(TemplateServiceClass", clz, (TemplateServiceImpl) component));
                    }
                }
            }
        }.scanDeleteClassName(clickModel);
        return null;
    }

    @Override
    AbstractRefactoring getRefactoring() {
        return this.safeDelete;
    }

    private abstract class SoftDeleteElement extends ClickRefactoringElement {

        ClickComponentImpl component;
        String bundleKey;
        String className;

        public SoftDeleteElement(FileObject clickFO, ClickModel clickApp, String bundleKey, String className, ClickComponentImpl component) {
            super(clickFO, clickApp);
            this.component = component;
            this.bundleKey = bundleKey;
            this.className = className;
        }

        @Override
        public String getDisplayText() {
            Object[] args = new Object[]{className};
            return MessageFormat.format(NbBundle.getMessage(ClickXmlWhereUsed.class, bundleKey), args);
        }

        @Override
        public PositionBounds getPosition() {
            return createClassnamePosistionBounds(component);
        }
    }

    private class PageSoftDeleteElement extends SoftDeleteElement {

        public PageSoftDeleteElement(FileObject clickFO, ClickModel clickApp, String bundleKey, String className, PageImpl component) {
            super(clickFO, clickApp, bundleKey, className, component);
        }

        @Override
        public void performChange() {
            ((PagesImpl) (component.getParent())).removePage((PageImpl) component);
        }

        @Override
        public void undoChange() {
            ((PagesImpl) (component.getParent())).addPage((PageImpl) component);
        }
    }

    private class ControlSoftDeleteElement extends SoftDeleteElement {

        public ControlSoftDeleteElement(FileObject clickFO, ClickModel clickApp, String bundleKey, String className, ControlImpl component) {
            super(clickFO, clickApp, bundleKey, className, component);
        }

        @Override
        public void performChange() {
            ((ControlsImpl) (component.getParent())).removeControl((ControlImpl) component);
        }

        @Override
        public void undoChange() {
            ((ControlsImpl) (component.getParent())).addControl((ControlImpl) component);
        }
    }

    private class FormatSoftDeleteElement extends SoftDeleteElement {

        public FormatSoftDeleteElement(FileObject clickFO, ClickModel clickApp, String bundleKey, String className, FormatImpl component) {
            super(clickFO, clickApp, bundleKey, className, component);
        }

        @Override
        public void performChange() {
            ((ClickAppImpl) (component.getParent())).setFormat(null);
        }

        @Override
        public void undoChange() {
            ((ClickAppImpl) (component.getParent())).setFormat((FormatImpl) component);
        }
    }

    private class FileUploadServiceSoftDeleteElement extends SoftDeleteElement {

        public FileUploadServiceSoftDeleteElement(FileObject clickFO, ClickModel clickApp, String bundleKey, String className, FileUploadServiceImpl component) {
            super(clickFO, clickApp, bundleKey, className, component);
        }

        @Override
        public void performChange() {
            ((ClickAppImpl) (component.getParent())).setFileUploadService(null);
        }

        @Override
        public void undoChange() {
            ((ClickAppImpl) (component.getParent())).setFileUploadService((FileUploadServiceImpl) component);
        }
    }


    private class TemplateServiceSoftDeleteElement extends SoftDeleteElement {

        public TemplateServiceSoftDeleteElement(FileObject clickFO, ClickModel clickApp, String bundleKey, String className, TemplateServiceImpl component) {
            super(clickFO, clickApp, bundleKey, className, component);
        }

        @Override
        public void performChange() {
            ((ClickAppImpl) (component.getParent())).setTemplateService(null);
        }

        @Override
        public void undoChange() {
            ((ClickAppImpl) (component.getParent())).setTemplateService((TemplateServiceImpl) component);
        }
    }


    private class LogServiceSoftDeleteElement extends SoftDeleteElement {

        public LogServiceSoftDeleteElement(FileObject clickFO, ClickModel clickApp, String bundleKey, String className, LogServiceImpl component) {
            super(clickFO, clickApp, bundleKey, className, component);
        }

        @Override
        public void performChange() {
            ((ClickAppImpl) (component.getParent())).setLogService(null);
        }

        @Override
        public void undoChange() {
            ((ClickAppImpl) (component.getParent())).setLogService((LogServiceImpl) component);
        }
    }
}
