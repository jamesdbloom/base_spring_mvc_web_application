package org.jamesdbloom.web.interceptor.bundling;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import ro.isdc.wro.http.support.ServletContextAttributeHelper;
import ro.isdc.wro.model.WroModel;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

/**
 * @author jamesdbloom
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class WroModelHolder {

    @Resource
    private ServletContext servletContext;
    private WroModel wroModel;

    public WroModel getWroModel() {
        if (wroModel == null) {
            ServletContextAttributeHelper helper = new ServletContextAttributeHelper(servletContext);
            if (helper.getManagerFactory() != null) {
                wroModel = helper.getManagerFactory().create().getModelFactory().create();
            }
        }
        return wroModel;
    }

}
