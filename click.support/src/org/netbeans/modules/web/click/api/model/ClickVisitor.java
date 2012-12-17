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

        public void accept(ClickApp component) {
            visitChild();
        }

        public void accept(Headers component) {
            visitChild();
        }

        public void accept(Header component) {
            visitChild();
        }

        public void accept(Pages component) {
            visitChild();
        }

        public void accept(Page component) {
            visitChild();
        }

        public void accept(Controls component) {
            visitChild();
        }

        public void accept(ControlSet component) {
            visitChild();
        }

        public void accept(Control component) {
            visitChild();
        }

        public void accept(Mode component) {
            visitChild();
        }

        public void accept(Format component) {
            visitChild();
        }

        public void accept(FileUploadService component) {
            visitChild();
        }

        public void accept(LogService component) {
            visitChild();
        }

        public void accept(TemplateService component) {
            visitChild();
        }

        public void accept(Property component) {
            visitChild();
        }

        public void accept(Excludes component) {
            visitChild();
        }

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
