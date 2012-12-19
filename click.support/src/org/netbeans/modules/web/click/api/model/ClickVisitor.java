/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.api.model;

/**
 *
 * @author hantsy
 */
public interface ClickVisitor {

    void accept(ClickApp component);

    void accept(Headers component);

    void accept(Header component);

    void accept(Pages component);

    void accept(Page component);

    void accept(Excludes component);

    void accept(Controls component);

    void accept(ControlSet component);

    void accept(Control component);

    void accept(Mode component);

    void accept(Format component);

    void accept(FileUploadService component);

    void accept(LogService component);

    void accept(TemplateService component);

    void accept(Property component);

    void accept(PageInterceptor component);

    public static class Default implements ClickVisitor {

        protected void visitChild() {
        }

        @Override
        public void accept(ClickApp component) {
            visitChild();
        }

        @Override
        public void accept(Headers component) {
            visitChild();
        }

        @Override
        public void accept(Header component) {
            visitChild();
        }

        @Override
        public void accept(Pages component) {
            visitChild();
        }

        @Override
        public void accept(Page component) {
            visitChild();
        }

        @Override
        public void accept(Controls component) {
            visitChild();
        }

        @Override
        public void accept(ControlSet component) {
            visitChild();
        }

        @Override
        public void accept(Control component) {
            visitChild();
        }

        @Override
        public void accept(Mode component) {
            visitChild();
        }

        @Override
        public void accept(Format component) {
            visitChild();
        }

        @Override
        public void accept(FileUploadService component) {
            visitChild();
        }

        @Override
        public void accept(LogService component) {
            visitChild();
        }

        @Override
        public void accept(TemplateService component) {
            visitChild();
        }

        @Override
        public void accept(Property component) {
            visitChild();
        }

        @Override
        public void accept(Excludes component) {
            visitChild();
        }

        @Override
        public void accept(PageInterceptor component) {
            visitChild();
        }
    }

     /**
     * Deep visitor
     */
    public static class Deep extends Default {

        protected void visitChild(ClickComponent component) {
            for (ClickComponent child : component.getChildren()) {
                child.accept(this);
            }
        }
    }
}
