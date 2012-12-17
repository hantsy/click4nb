/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.web.click.api.model;

/**
 *
 * @author hantsy
 */
public interface  MenuVisitor {
   void accept(MenuComponent component);

   public static class Default implements MenuVisitor{

        public void accept(MenuComponent component) {
            visitChild();
        }

        protected void visitChild() {
        }
   }

    /**
     * Deep visitor
     */
    public static class Deep extends Default {

        protected void visitChild(MenuComponent component) {
            for (MenuComponent child : component.getChildren()) {
                child.accept(this);
            }
        }
    }
}
