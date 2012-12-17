/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.editor.completion;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.beans.BeanInfo;
import java.io.IOException;
import java.util.Iterator;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.SimpleElementVisitor6;
import javax.lang.model.util.SimpleTypeVisitor6;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.SourceUtils;
import org.netbeans.api.java.source.Task;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.web.click.editor.JavaUtils;
import org.netbeans.spi.editor.completion.CompletionDocumentation;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.xml.XMLUtil;

/**
 * copied from spring.beans module.
 * small modifications are included in this module.
 * @author hantsy
 */
public abstract class ClickCompletionItem implements CompletionItem {

    public static ClickCompletionItem createTypeItem(int substitutionOffset, TypeElement elem, ElementHandle<TypeElement> elemHandle,
            boolean deprecated, boolean smartItem) {
        return new ClassItem(substitutionOffset, elem, elemHandle, deprecated, smartItem);
    }

    public static ClickCompletionItem createPackageItem(int substitutionOffset, String packageName,
            boolean deprecated) {
        return new PackageItem(substitutionOffset, packageName, deprecated);
    }

    public static ClickCompletionItem createFolderItem(int substitutionOffset, FileObject folder) {
        return new FolderItem(substitutionOffset, folder);
    }

    public static ClickCompletionItem createFileItem(int substitutionOffset, FileObject file, String filePath) {
        return new FileItem(substitutionOffset, file, filePath);
    }

    public static ClickCompletionItem createAttribValueItem(int substitutionOffset, String displayText, String docText) {
        return new AttribValueItem(substitutionOffset, displayText, docText);
    }
    protected int substitutionOffset;

    protected ClickCompletionItem(int substitutionOffset) {
        this.substitutionOffset = substitutionOffset;
    }

    @Override
    public void defaultAction(JTextComponent component) {
        if (component != null) {
            Completion.get().hideDocumentation();
            Completion.get().hideCompletion();
            int caretOffset = component.getSelectionEnd();
            substituteText(component, substitutionOffset, caretOffset - substitutionOffset, null);
        }
    }

    protected void substituteText(JTextComponent c, final int offset, final int len, String toAdd) {
        final BaseDocument doc = (BaseDocument) c.getDocument();
        CharSequence prefix = getSubstitutionText();
        String text = prefix.toString();
        if (toAdd != null) {
            text += toAdd;
        }

        final String sText = text;
        doc.runAtomic(new Runnable() {

            public void run() {
                try {
                    Position position = doc.createPosition(offset);
                    doc.remove(offset, len);
                    doc.insertString(position.getOffset(), sText.toString(), null);
                } catch (BadLocationException ble) {
                    // nothing can be done to update
                }
            }
        });
    }

    protected CharSequence getSubstitutionText() {
        return getInsertPrefix();
    }

    public void processKeyEvent(KeyEvent evt) {
    }

    public int getPreferredWidth(Graphics g, Font defaultFont) {
        return CompletionUtilities.getPreferredWidth(getLeftHtmlText(),
                getRightHtmlText(), g, defaultFont);
    }

    public void render(Graphics g, Font defaultFont, Color defaultColor,
            Color backgroundColor, int width, int height, boolean selected) {
        CompletionUtilities.renderHtml(getIcon(), getLeftHtmlText(),
                getRightHtmlText(), g, defaultFont, defaultColor, width, height, selected);
    }

    public CompletionTask createDocumentationTask() {
        return null;
    }

    public CompletionTask createToolTipTask() {
        return null;
    }

    public boolean instantSubstitution(JTextComponent component) {
        defaultAction(component);
        return true;
    }

    protected String getLeftHtmlText() {
        return null;
    }

    protected String getRightHtmlText() {
        return null;
    }

    protected ImageIcon getIcon() {
        return null;
    }
    public static final String COLOR_END = "</font>"; //NOI18N
    public static final String STRIKE = "<s>"; //NOI18N
    public static final String STRIKE_END = "</s>"; //NOI18N
    public static final String BOLD = "<b>"; //NOI18N
    public static final String BOLD_END = "</b>"; //NOI18N

    /**
     * Represents a class in the completion popup.
     *
     * Heavily derived from Java Editor module's JavaCompletionItem class
     *
     */
    private static class ClassItem extends ClickCompletionItem {

        private static final String CLASS = "org/netbeans/modules/editor/resources/completion/class_16.png"; //NOI18N
        private static final String CLASS_COLOR = "<font color=#560000>"; //NOI18N
        private static final String PKG_COLOR = "<font color=#808080>"; //NOI18N
        private ElementHandle<TypeElement> elemHandle;
        private boolean deprecated;
        private String displayName;
        private String enclName;
        private String sortText;
        private String leftText;
        private boolean smartItem;

        public ClassItem(int substitutionOffset, TypeElement elem, ElementHandle<TypeElement> elemHandle,
                boolean deprecated, boolean smartItem) {
            super(substitutionOffset);
            this.elemHandle = elemHandle;
            this.deprecated = deprecated;
            this.displayName = smartItem ? elem.getSimpleName().toString() : getRelativeName(elem);
            this.enclName = getElementName(elem.getEnclosingElement(), true).toString();
            this.sortText = this.displayName + getImportanceLevel(this.enclName) + "#" + this.enclName; //NOI18N
            this.smartItem = smartItem;
        }

        private String getRelativeName(TypeElement elem) {
            StringBuilder sb = new StringBuilder();
            sb.append(elem.getSimpleName().toString());
            Element parent = elem.getEnclosingElement();
            while (parent.getKind() != ElementKind.PACKAGE) {
                sb.insert(0, parent.getSimpleName().toString() + "$"); // NOI18N
                parent = parent.getEnclosingElement();
            }

            return sb.toString();
        }

        public int getSortPriority() {
            return 200;
        }

        public CharSequence getSortText() {
            return sortText;
        }

        public CharSequence getInsertPrefix() {
            return smartItem ? "" : elemHandle.getBinaryName(); // NOI18N
        }

        @Override
        protected CharSequence getSubstitutionText() {
            return elemHandle.getBinaryName();
        }

        @Override
        public boolean instantSubstitution(JTextComponent component) {
            return false;
        }

        @Override
        protected String getLeftHtmlText() {
            if (leftText == null) {
                StringBuilder sb = new StringBuilder();
                sb.append(getColor());
                if (deprecated) {
                    sb.append(STRIKE);
                }
                sb.append(displayName);
                if (deprecated) {
                    sb.append(STRIKE_END);
                }
                if (smartItem && enclName != null && enclName.length() > 0) {
                    sb.append(COLOR_END);
                    sb.append(PKG_COLOR);
                    sb.append(" ("); //NOI18N
                    sb.append(enclName);
                    sb.append(")"); //NOI18N
                }
                sb.append(COLOR_END);
                leftText = sb.toString();
            }
            return leftText;
        }

        protected String getColor() {
            return CLASS_COLOR;
        }

        @Override
        protected ImageIcon getIcon() {
            return ImageUtilities.loadImageIcon(CLASS, false);
        }

        @Override
        public CompletionTask createDocumentationTask() {
            return new AsyncCompletionTask(new JavaElementDocQuery(elemHandle), EditorRegistry.lastFocusedComponent());
        }
    }

    private static class FolderItem extends ClickCompletionItem {

        private FileObject folder;

        public FolderItem(int substitutionOffset, FileObject folder) {
            super(substitutionOffset);
            this.folder = folder;
        }

        @Override
        public void processKeyEvent(KeyEvent evt) {
            if (evt.getID() == KeyEvent.KEY_TYPED) {
                if (evt.getKeyChar() == '/') { // NOI18N
                    Completion.get().hideDocumentation();
                    JTextComponent component = (JTextComponent) evt.getSource();
                    int caretOffset = component.getSelectionEnd();
                    substituteText(component, substitutionOffset, caretOffset - substitutionOffset, Character.toString(evt.getKeyChar()));
                    Completion.get().showCompletion();
                    evt.consume();
                }
            }
        }

        public int getSortPriority() {
            return 300;
        }

        public CharSequence getSortText() {
            return folder.getName();
        }

        public CharSequence getInsertPrefix() {
            return folder.getName();
        }

        @Override
        public boolean instantSubstitution(JTextComponent component) {
            return false;
        }

        @Override
        protected ImageIcon getIcon() {
            return new ImageIcon(getTreeFolderIcon());
        }

        @Override
        protected String getLeftHtmlText() {
            return folder.getName();
        }
        private static final String ICON_KEY_UIMANAGER = "Tree.closedIcon"; // NOI18N
        private static final String ICON_KEY_UIMANAGER_NB = "Nb.Explorer.Folder.icon"; // NOI18N

        /**
         * Returns default folder icon as {@link java.awt.Image}. Never returns
         * <code>null</code>.Adapted from J2SELogicalViewProvider
         */
        private static Image getTreeFolderIcon() {
            Image base = null;
            Icon baseIcon = UIManager.getIcon(ICON_KEY_UIMANAGER); // #70263
            if (baseIcon != null) {
                base = ImageUtilities.icon2Image(baseIcon);
            } else {
                base = (Image) UIManager.get(ICON_KEY_UIMANAGER_NB); // #70263
                if (base == null) { // fallback to our owns                
                    final Node n = DataFolder.findFolder(FileUtil.getConfigRoot()).getNodeDelegate();
                    base = n.getIcon(BeanInfo.ICON_COLOR_16x16);
                }
            }
            assert base != null;
            return base;
        }
    }

    private static class FileItem extends ClickCompletionItem {

        private FileObject file;
        private static final String PATH_COLOR = "<font color=#808080>"; //NOI18N
        private String filePath;
        private String pagePathName;

        public FileItem(int substitutionOffset, FileObject file, String filePath) {
            super(substitutionOffset);
            this.file = file;
            this.filePath = filePath;
            this.pagePathName = file.getNameExt();
            if (pagePathName.endsWith(".jsp")) {
                this.pagePathName = pagePathName.substring(0, pagePathName.lastIndexOf(".jsp")) + ".htm";
            }
        }

        public int getSortPriority() {
            return 100;
        }

        public CharSequence getSortText() {
            return file.getNameExt();
        }

        public CharSequence getInsertPrefix() {
            return pagePathName;
        }

        @Override
        protected ImageIcon getIcon() {
            return ImageUtilities.loadImageIcon("org/netbeans/modules/html/editor/resources/html.png", false); // NOI18N
        }

        @Override
        protected String getLeftHtmlText() {
            return pagePathName + PATH_COLOR + "(" + filePath + ")" + COLOR_END;
        }
    }

    private static class PackageItem extends ClickCompletionItem {

        private static final String PACKAGE = "org/netbeans/modules/java/editor/resources/package.gif"; // NOI18N
        private static final String PACKAGE_COLOR = "<font color=#005600>"; //NOI18N
        private static ImageIcon icon;
        private boolean deprecated;
        private String simpleName;
        private String sortText;
        private String leftText;

        public PackageItem(int substitutionOffset, String packageFQN, boolean deprecated) {
            super(substitutionOffset);
            int idx = packageFQN.lastIndexOf('.'); // NOI18N
            this.simpleName = idx < 0 ? packageFQN : packageFQN.substring(idx + 1);
            this.deprecated = deprecated;
            this.sortText = this.simpleName + "#" + packageFQN; //NOI18N
        }

        public int getSortPriority() {
            return 50;
        }

        public CharSequence getSortText() {
            return sortText;
        }

        public CharSequence getInsertPrefix() {
            return simpleName;
        }

        @Override
        public void processKeyEvent(KeyEvent evt) {
            if (evt.getID() == KeyEvent.KEY_TYPED) {
                if (evt.getKeyChar() == '.') { // NOI18N
                    Completion.get().hideDocumentation();
                    JTextComponent component = (JTextComponent) evt.getSource();
                    int caretOffset = component.getSelectionEnd();
                    substituteText(component, substitutionOffset, caretOffset - substitutionOffset, Character.toString(evt.getKeyChar()));
                    Completion.get().showCompletion();
                    evt.consume();
                }
            }
        }

        @Override
        protected ImageIcon getIcon() {
            if (icon == null) {
                icon = ImageUtilities.loadImageIcon(PACKAGE, false);
            }
            return icon;
        }

        @Override
        protected String getLeftHtmlText() {
            if (leftText == null) {
                StringBuilder sb = new StringBuilder();
                sb.append(PACKAGE_COLOR);
                if (deprecated) {
                    sb.append(STRIKE);
                }
                sb.append(simpleName);
                if (deprecated) {
                    sb.append(STRIKE_END);
                }
                sb.append(COLOR_END);
                leftText = sb.toString();
            }
            return leftText;
        }
    }

    private static class AttribValueItem extends ClickCompletionItem {

        private String displayText;
        private String docText;

        public AttribValueItem(int substitutionOffset, String displayText, String docText) {
            super(substitutionOffset);
            this.displayText = displayText;
            this.docText = docText;
        }

        public int getSortPriority() {
            return 50;
        }

        public CharSequence getSortText() {
            return displayText;
        }

        public CharSequence getInsertPrefix() {
            return displayText;
        }

        @Override
        protected String getLeftHtmlText() {
            return displayText;
        }

        @Override
        public CompletionTask createDocumentationTask() {
            return new AsyncCompletionTask(new AsyncCompletionQuery() {

                @Override
                protected void query(CompletionResultSet resultSet, Document doc, int caretOffset) {
                    if (docText != null) {
                        CompletionDocumentation documentation = ClickClassnameCompletionDoc.getAttribValueDoc(docText);
                        resultSet.setDocumentation(documentation);
                    }
                    resultSet.finish();
                }
            });
        }
    }

    public static CharSequence getElementName(Element el, boolean fqn) {
        if (el == null || el.asType().getKind() == TypeKind.NONE) {
            return ""; //NOI18N
        }
        return new ElementNameVisitor().visit(el, fqn);
    }

    private static class ElementNameVisitor extends SimpleElementVisitor6<StringBuilder, Boolean> {

        private ElementNameVisitor() {
            super(new StringBuilder());
        }

        @Override
        public StringBuilder visitPackage(PackageElement e, Boolean p) {
            return DEFAULT_VALUE.append((p ? e.getQualifiedName() : e.getSimpleName()).toString());
        }

        @Override
        public StringBuilder visitType(TypeElement e, Boolean p) {
            return DEFAULT_VALUE.append((p ? e.getQualifiedName() : e.getSimpleName()).toString());
        }
    }

    private static String escape(String s) {
        if (s != null) {
            try {
                return XMLUtil.toAttributeValue(s);
            } catch (Exception ex) {
            }
        }
        return s;
    }

    private static class JavaElementDocQuery extends AsyncCompletionQuery {

        private ElementHandle<?> elemHandle;

        public JavaElementDocQuery(ElementHandle<?> elemHandle) {
            this.elemHandle = elemHandle;
        }

        @Override
        protected void query(final CompletionResultSet resultSet, Document doc, int caretOffset) {
            try {
                JavaSource js = JavaUtils.getJavaSource(doc);
                if (js == null) {
                    return;
                }

                js.runUserActionTask(new Task<CompilationController>() {

                    public void run(CompilationController cc) throws Exception {
                        cc.toPhase(Phase.RESOLVED);
                        Element element = elemHandle.resolve(cc);
                        if (element == null) {
                            return;
                        }
                        ClickClassnameCompletionDoc doc = ClickClassnameCompletionDoc.createJavaDoc(cc, element);
                        resultSet.setDocumentation(doc);
                    }
                }, false);
                resultSet.finish();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    public static int getImportanceLevel(String fqn) {
        int weight = 50;
        if (fqn.startsWith("java.lang") || fqn.startsWith("java.util")) // NOI18N
        {
            weight -= 10;
        } else if (fqn.startsWith("org.omg") || fqn.startsWith("org.apache")) // NOI18N
        {
            weight += 10;
        } else if (fqn.startsWith("com.sun") || fqn.startsWith("com.ibm") || fqn.startsWith("com.apple")) // NOI18N
        {
            weight += 20;
        } else if (fqn.startsWith("sun") || fqn.startsWith("sunw") || fqn.startsWith("netscape")) // NOI18N
        {
            weight += 30;
        }
        return weight;
    }

    public static CharSequence getTypeName(TypeMirror type, boolean fqn) {
        return getTypeName(type, fqn, false);
    }

    public static CharSequence getTypeName(TypeMirror type, boolean fqn, boolean varArg) {
        if (type == null) {
            return ""; //NOI18N
        }
        return new TypeNameVisitor(varArg).visit(type, fqn);
    }
    private static final String UNKNOWN = "<unknown>"; //NOI18N
    private static final String CAPTURED_WILDCARD = "<captured wildcard>"; //NOI18N

    private static class TypeNameVisitor extends SimpleTypeVisitor6<StringBuilder, Boolean> {

        private boolean varArg;

        private TypeNameVisitor(boolean varArg) {
            super(new StringBuilder());
            this.varArg = varArg;
        }

        @Override
        public StringBuilder defaultAction(TypeMirror t, Boolean p) {
            return DEFAULT_VALUE.append(t);
        }

        @Override
        public StringBuilder visitDeclared(DeclaredType t, Boolean p) {
            Element e = t.asElement();
            if (e instanceof TypeElement) {
                TypeElement te = (TypeElement) e;
                DEFAULT_VALUE.append((p ? te.getQualifiedName() : te.getSimpleName()).toString());
                Iterator<? extends TypeMirror> it = t.getTypeArguments().iterator();
                if (it.hasNext()) {
                    DEFAULT_VALUE.append("<"); //NOI18N
                    while (it.hasNext()) {
                        visit(it.next(), p);
                        if (it.hasNext()) {
                            DEFAULT_VALUE.append(", "); //NOI18N
                        }
                    }
                    DEFAULT_VALUE.append(">"); //NOI18N
                }
                return DEFAULT_VALUE;
            } else {
                return DEFAULT_VALUE.append(UNKNOWN); //NOI18N
            }
        }

        @Override
        public StringBuilder visitArray(ArrayType t, Boolean p) {
            boolean isVarArg = varArg;
            varArg = false;
            visit(t.getComponentType(), p);
            return DEFAULT_VALUE.append(isVarArg ? "..." : "[]"); //NOI18N
        }

        @Override
        public StringBuilder visitTypeVariable(TypeVariable t, Boolean p) {
            Element e = t.asElement();
            if (e != null) {
                String name = e.getSimpleName().toString();
                if (!CAPTURED_WILDCARD.equals(name)) {
                    return DEFAULT_VALUE.append(name);
                }
            }
            DEFAULT_VALUE.append("?"); //NOI18N
            TypeMirror bound = t.getLowerBound();
            if (bound != null && bound.getKind() != TypeKind.NULL) {
                DEFAULT_VALUE.append(" super "); //NOI18N
                visit(bound, p);
            } else {
                bound = t.getUpperBound();
                if (bound != null && bound.getKind() != TypeKind.NULL) {
                    DEFAULT_VALUE.append(" extends "); //NOI18N
                    if (bound.getKind() == TypeKind.TYPEVAR) {
                        bound = ((TypeVariable) bound).getLowerBound();
                    }
                    visit(bound, p);
                }
            }
            return DEFAULT_VALUE;
        }

        @Override
        public StringBuilder visitWildcard(WildcardType t, Boolean p) {
            DEFAULT_VALUE.append("?"); //NOI18N
            TypeMirror bound = t.getSuperBound();
            if (bound == null) {
                bound = t.getExtendsBound();
                if (bound != null) {
                    DEFAULT_VALUE.append(" extends "); //NOI18N
                    if (bound.getKind() == TypeKind.WILDCARD) {
                        bound = ((WildcardType) bound).getSuperBound();
                    }
                    visit(bound, p);
                } else {
                    bound = SourceUtils.getBound(t);
                    if (bound != null && (bound.getKind() != TypeKind.DECLARED || !((TypeElement) ((DeclaredType) bound).asElement()).getQualifiedName().contentEquals("java.lang.Object"))) { //NOI18N
                        DEFAULT_VALUE.append(" extends "); //NOI18N
                        visit(bound, p);
                    }
                }
            } else {
                DEFAULT_VALUE.append(" super "); //NOI18N
                visit(bound, p);
            }
            return DEFAULT_VALUE;
        }

        @Override
        public StringBuilder visitError(ErrorType t, Boolean p) {
            Element e = t.asElement();
            if (e instanceof TypeElement) {
                TypeElement te = (TypeElement) e;
                return DEFAULT_VALUE.append((p ? te.getQualifiedName() : te.getSimpleName()).toString());
            }
            return DEFAULT_VALUE;
        }
    }
}
