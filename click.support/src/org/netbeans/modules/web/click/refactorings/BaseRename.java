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

import java.util.List;
import org.netbeans.modules.refactoring.api.Problem;
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
import org.netbeans.modules.web.click.api.model.impl.ControlImpl;
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
 * Base class for refactorings that handle moving and renaming
 * packages / classes.
 *
 * @author Erno Mononen
 */
public abstract class BaseRename extends ClickXmlRefactoring {

    public BaseRename(FileObject clickFO) {
        super(clickFO);
    }

    /**
     * @return a list of <code>RenameItem</code>s representing the new and old 
     * names of the classes that are affected by this refactoring.
     */
    abstract List<RenameItem> getRenameItems();

    abstract List<RenameItem> getPackageRenameItems();

    @Override
    Problem prepare(final RefactoringElementsBag bag) {

        Problem problem = null;
        Problem current = null;
        for (RenameItem item : getRenameItems()) {
            /*
             * Additional fix for IZ#153294 - Error message when renaming a package that contains a dash character
             */
            if (item.getProblem() != null) {
                if (problem == null) {
                    problem = item.getProblem();
                    current = problem;
                } else {
                    current.setNext(item.getProblem());
                    current = item.getProblem();
                }
                continue;
            }

        }

        for (RenameItem item : getPackageRenameItems()) {
            /*
             * Additional fix for IZ#153294 - Error message when renaming a package that contains a dash character
             */
            if (item.getProblem() != null) {
                if (problem == null) {
                    problem = item.getProblem();
                    current = problem;
                } else {
                    current.setNext(item.getProblem());
                    current = item.getProblem();
                }
                continue;
            }

        }

        new ClickVisitor.Deep() {

            void scan(ClickApp root) {
                root.accept(this);
            }

            @Override
            public void accept(ClickApp component) {
                visitChild(component);
            }

            @Override
            public void accept(Controls component) {
                visitChild(component);
            }

            @Override
            public void accept(Pages component) {
                PositionBounds bounds = createClassnamePosistionBounds((PagesImpl) component);
                for (RenameItem item : getPackageRenameItems()) {
                    String newName = item.getNewName();
                    String oldFqn = item.getOldName();
                    if (oldFqn.equals(component.getPackage())) {
                        bag.add(getRefactoring(), new PagesPackageRenameElement(clickFO, clickModel, oldFqn, newName, bounds, component));
                    }
                }
                visitChild(component);
            }

            @Override
            public void accept(Page component) {

                PositionBounds bounds = createClassnamePosistionBounds((PageImpl) component);
                for (RenameItem item : getRenameItems()) {
                    String newName = item.getNewName();
                    String oldFqn = item.getOldName();
                    if (oldFqn.equals(component.getClassName())) {
                        bag.add(getRefactoring(), new PageClassRenameElement(clickFO, clickModel, oldFqn, newName, bounds, component));
                    }
                }
            }

            @Override
            public void accept(Control component) {

                PositionBounds bounds = createClassnamePosistionBounds((ControlImpl) component);
                for (RenameItem item : getRenameItems()) {
                    String newName = item.getNewName();
                    String oldFqn = item.getOldName();
                    if (oldFqn.equals(component.getClassName())) {
                        bag.add(getRefactoring(), new ControlClassRenameElement(clickFO, clickModel, oldFqn, newName, bounds, component));
                    }
                }
            }

            @Override
            public void accept(Format component) {
                PositionBounds bounds = createClassnamePosistionBounds((FormatImpl) component);
                for (RenameItem item : getRenameItems()) {
                    String newName = item.getNewName();
                    String oldFqn = item.getOldName();
                    if (oldFqn.equals(component.getClassName())) {
                        bag.add(getRefactoring(), new FormatClassRenameElement(clickFO, clickModel, oldFqn, newName, bounds, component));
                    }
                }
            }

            @Override
            public void accept(FileUploadService component) {
                PositionBounds bounds = createClassnamePosistionBounds((FileUploadServiceImpl) component);
                for (RenameItem item : getRenameItems()) {
                    String newName = item.getNewName();
                    String oldFqn = item.getOldName();
                    if (oldFqn.equals(component.getClassName())) {
                        bag.add(getRefactoring(), new FileUploadServiceClassRenameElement(clickFO, clickModel, oldFqn, newName, bounds, component));
                    }
                }
            }

            @Override
            public void accept(LogService component) {
                PositionBounds bounds = createClassnamePosistionBounds((LogServiceImpl) component);
                for (RenameItem item : getRenameItems()) {
                    String newName = item.getNewName();
                    String oldFqn = item.getOldName();
                    if (oldFqn.equals(component.getClassName())) {
                        bag.add(getRefactoring(), new LogServiceClassRenameElement(clickFO, clickModel, oldFqn, newName, bounds, component));
                    }
                }
            }

            @Override
            public void accept(TemplateService component) {
                PositionBounds bounds = createClassnamePosistionBounds((TemplateServiceImpl) component);
                for (RenameItem item : getRenameItems()) {
                    String newName = item.getNewName();
                    String oldFqn = item.getOldName();
                    if (oldFqn.equals(component.getClassName())) {
                        bag.add(getRefactoring(), new TemplateServiceClassRenameElement(clickFO, clickModel, oldFqn, newName, bounds, component));
                    }
                }
            }
        }.scan(clickModel.getRootComponent());
        return problem;
    }

    private abstract static class RenameElement extends ClickRefactoringElement {

        protected String oldName;
        protected String newName;
        protected PositionBounds positionBounds;
        //protected RenameRefactoring rename;

        public RenameElement(FileObject clickFO, ClickModel clickMdoel, String oldName, String newName, PositionBounds bounds) {
            super(clickFO, clickMdoel);
            this.newName = newName;
            this.oldName = oldName;
            this.positionBounds = bounds;
        }

        protected String getName() {
            return oldName;
        }

        @Override
        public PositionBounds getPosition() {
            return this.positionBounds;
        }
    }

    private class PageClassRenameElement extends RenameElement {

        private Page page;

        public PageClassRenameElement(FileObject clickFO, ClickModel clickMdoel, String oldName, String newName, PositionBounds bounds, Page page) {
            super(clickFO, clickMdoel, oldName, newName, bounds);
            this.page = page;
        }

        @Override
        public void performChange() {
            page.setClassName(newName);
        }

        @Override
        public void undoChange() {
            page.setClassName(oldName);
        }

        @Override
        public String getDisplayText() {
            return NbBundle.getMessage(BaseRename.class, "TXT_RenamePageClass", oldName, newName);
        }
    }

    private class ControlClassRenameElement extends RenameElement {

        private Control component;

        public ControlClassRenameElement(FileObject clickFO, ClickModel clickMdoel, String oldName, String newName, PositionBounds bounds, Control component) {
            super(clickFO, clickMdoel, oldName, newName, bounds);
            this.component = component;
        }

        @Override
        public void performChange() {
            component.setClassName(newName);
        }

        @Override
        public void undoChange() {
            component.setClassName(oldName);
        }

        @Override
        public String getDisplayText() {
            return NbBundle.getMessage(BaseRename.class, "TXT_RenameControlClass", oldName, newName);
        }
    }

    private class FormatClassRenameElement extends RenameElement {

        private Format component;

        public FormatClassRenameElement(FileObject clickFO, ClickModel clickMdoel, String oldName, String newName, PositionBounds bounds, Format component) {
            super(clickFO, clickMdoel, oldName, newName, bounds);
            this.component = component;
        }

        @Override
        public void performChange() {
            component.setClassName(newName);
        }

        @Override
        public void undoChange() {
            component.setClassName(oldName);
        }

        @Override
        public String getDisplayText() {
            return NbBundle.getMessage(BaseRename.class, "TXT_RenameFormatServiceClass", oldName, newName);
        }
    }

    private class FileUploadServiceClassRenameElement extends RenameElement {

        private FileUploadService component;

        public FileUploadServiceClassRenameElement(FileObject clickFO, ClickModel clickMdoel, String oldName, String newName, PositionBounds bounds, FileUploadService component) {
            super(clickFO, clickMdoel, oldName, newName, bounds);
            this.component = component;
        }

        @Override
        public void performChange() {
            component.setClassName(newName);
        }

        @Override
        public void undoChange() {
            component.setClassName(oldName);
        }

        @Override
        public String getDisplayText() {
            return NbBundle.getMessage(BaseRename.class, "TXT_RenameFileUploadServiceClass", oldName, newName);
        }
    }

    private class LogServiceClassRenameElement extends RenameElement {

        private LogService component;

        public LogServiceClassRenameElement(FileObject clickFO, ClickModel clickMdoel, String oldName, String newName, PositionBounds bounds, LogService component) {
            super(clickFO, clickMdoel, oldName, newName, bounds);
            this.component = component;
        }

        @Override
        public void performChange() {
            component.setClassName(newName);
        }

        @Override
        public void undoChange() {
            component.setClassName(oldName);
        }

        @Override
        public String getDisplayText() {
            return NbBundle.getMessage(BaseRename.class, "TXT_RenameLogServiceClass", oldName, newName);
        }
    }

    private class TemplateServiceClassRenameElement extends RenameElement {

        private TemplateService component;

        public TemplateServiceClassRenameElement(FileObject clickFO, ClickModel clickMdoel, String oldName, String newName, PositionBounds bounds, TemplateService component) {
            super(clickFO, clickMdoel, oldName, newName, bounds);
            this.component = component;
        }

        @Override
        public void performChange() {
            component.setClassName(newName);
        }

        @Override
        public void undoChange() {
            component.setClassName(oldName);
        }

        @Override
        public String getDisplayText() {
            return NbBundle.getMessage(BaseRename.class, "TXT_RenameTemplateServiceClass", oldName, newName);
        }
    }

    private class PagesPackageRenameElement extends RenameElement {

        private Pages component;

        public PagesPackageRenameElement(FileObject clickFO, ClickModel clickMdoel, String oldName, String newName, PositionBounds bounds, Pages component) {
            super(clickFO, clickMdoel, oldName, newName, bounds);
            this.component = component;
        }

        @Override
        public void performChange() {
            component.setPackage(newName);
        }

        @Override
        public void undoChange() {
            component.setPackage(oldName);
        }

        @Override
        public String getDisplayText() {
            return NbBundle.getMessage(BaseRename.class, "TXT_RenamePagesPackage", oldName, newName);
        }
    }
}
