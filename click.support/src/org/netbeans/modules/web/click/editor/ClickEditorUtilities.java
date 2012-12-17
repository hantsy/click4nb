/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.editor;

import org.openide.cookies.EditCookie;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;

/**
 *
 * @author hantsy
 */
public class ClickEditorUtilities {

    public static void openInEditor(FileObject fileToOpen) {
        DataObject fileDO = null;
        try {
            fileDO = DataObject.find(fileToOpen);
            if (fileDO != null) {
                EditCookie editCookie = fileDO.getCookie(EditCookie.class);
                if (editCookie != null) {
                    editCookie.edit();
                } else {
                    OpenCookie openCookie = fileDO.getCookie(OpenCookie.class);
                    if (openCookie != null) {
                        openCookie.open();
                    }
                }
            }
        } catch (DataObjectNotFoundException e) {
            Exceptions.printStackTrace(e);
            return;
        }

    }

    public static FileObject findPageByPath(FileObject webRoot, String path) {
        FileObject targetFO = webRoot.getFileObject(path);

        if (targetFO == null && path.lastIndexOf(".") != -1) {
            path = path.substring(0, path.lastIndexOf(".")) + ".jsp";
            targetFO = webRoot.getFileObject(path);
        }

        return targetFO;
    }


    public static String convertClassNameToPathName(String simpleClassName){
         StringBuffer pageNameBuffer = new StringBuffer();
        boolean lastDigit = false;
        for (int i = 0; i < simpleClassName.length(); i++) {
            char c = simpleClassName.charAt(i);
            if (i == 0) {
                pageNameBuffer = pageNameBuffer.append(Character.toLowerCase(c));
                if (Character.isDigit(c)) {
                    lastDigit = true;
                }
            } else {
                if (Character.isDigit(c)) {
                    if (lastDigit) {
                        pageNameBuffer = pageNameBuffer.append(c);
                    } else {
                        pageNameBuffer = pageNameBuffer.append("-").append(c);
                    }
                    lastDigit = true;
                } else {
                    if (Character.isUpperCase(c)) {
                        pageNameBuffer = pageNameBuffer.append("-").append(Character.toLowerCase(c));
                    } else {
                        pageNameBuffer = pageNameBuffer.append(c);
                    }
                    lastDigit = false;
                }
            }
        }
        return pageNameBuffer.toString();
    }
}
