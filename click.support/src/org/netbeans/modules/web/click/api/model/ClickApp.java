/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.web.click.api.model;

import java.util.List;

/**
 *
 * @author hantsy
 */
public interface ClickApp extends ReferenceableClickComponent {
    //Attributes
    public static final String PROP_CHARSET = "charset";
    public static final String PROP_LOCALE = "locale";

    String getCharset();
    void setCharse(String charset);

    String getLocale();
    void setLocale(String locale);
    
    //Elements
    public static final String PROP_HEADERS = "headers";
    public static final String PROP_PAGES = "pages";
    public static final String PROP_CONTROLS = "controls";
    public static final String PROP_FORMAT = "format";
    public static final String PROP_MODE = "mode";
    public static final String PROP_LOG_SERVICE = "log-servie";
    public static final String PROP_FILE_UPLOAD_SERVICE = "file-upload-service";
    public static final String PROP_TEMPLATE_SERVICE = "template-service";
    public static final String PROP_PAGE_INTERCEPTOR="page-interceptor";

    Headers getHeaders();

    void setHeaders(Headers headers);

    List<Pages> getPagesList();

    void addPages(Pages pages);

    void removePages(Pages pages);

    Format getFormat();

    void setFormat(Format format);

    Mode getMode();

    void setMode(Mode mode);

    Controls getControls();

    void setControls(Controls controls);

    FileUploadService getFileUploadService();

    void setFileUploadService(FileUploadService service);

    LogService getLogService();

    void setLogService(LogService service);

    TemplateService getTemplateService();

    void setTemplateService(TemplateService service);

    List<PageInterceptor> getPageInterceptorList();

    void addPageInterceptor(PageInterceptor pi);
    void removePageInterceptor(PageInterceptor pi);
}
