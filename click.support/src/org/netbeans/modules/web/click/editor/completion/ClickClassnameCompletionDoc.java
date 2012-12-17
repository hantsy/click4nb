/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.editor.completion;

import java.net.URL;
import javax.lang.model.element.Element;
import javax.swing.Action;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.ui.ElementJavadoc;
import org.netbeans.spi.editor.completion.CompletionDocumentation;

/**
 *
 * @author hantsy
 */
public abstract class ClickClassnameCompletionDoc implements CompletionDocumentation {

    static ClickClassnameCompletionDoc createJavaDoc(CompilationController cc, Element element) {
        return new JavaElementDoc(ElementJavadoc.create(cc, element));
    }


    public static ClickClassnameCompletionDoc getAttribValueDoc(String text) {
        return new AttribValueDoc(text);
    }
    

    public URL getURL() {
        return null;
    }

    public CompletionDocumentation resolveLink(String link) {
        return null;
    }

    public Action getGotoSourceAction() {
        return null;
    }

    private static class AttribValueDoc extends ClickClassnameCompletionDoc {

        private String text;

        public AttribValueDoc(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    private static class JavaElementDoc extends ClickClassnameCompletionDoc {

        private ElementJavadoc elementJavadoc;

        public JavaElementDoc(ElementJavadoc elementJavadoc) {
            this.elementJavadoc = elementJavadoc;
        }

        @Override
        public JavaElementDoc resolveLink(String link) {
            ElementJavadoc doc = elementJavadoc.resolveLink(link);
            return doc != null ? new JavaElementDoc(doc) : null;
        }

        @Override
        public URL getURL() {
            return elementJavadoc.getURL();
        }

        public String getText() {
            return elementJavadoc.getText();
        }

        @Override
        public Action getGotoSourceAction() {
            return elementJavadoc.getGotoSourceAction();
        }
    }
}
