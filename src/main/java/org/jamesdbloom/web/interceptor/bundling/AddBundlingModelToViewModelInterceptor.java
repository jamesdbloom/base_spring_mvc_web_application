package org.jamesdbloom.web.interceptor.bundling;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import ro.isdc.wro.model.WroModel;
import ro.isdc.wro.model.group.Group;
import ro.isdc.wro.model.resource.Resource;
import ro.isdc.wro.model.resource.ResourceType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author jamesdbloom
 */
public class AddBundlingModelToViewModelInterceptor extends HandlerInterceptorAdapter {

    public static final String JS_RESOURCES = "jsResources";
    public static final String CSS_RESOURCES = "cssResources";
    private static final Function<Resource, String> RESOURCE_TO_URI = new Function<Resource, String>() {
        @Override
        public String apply(Resource resource) {
            return resource.getUri();
        }
    };
    private final WroModelHolder wroModelHolder;
    private final String bundlingEnabled;

    public AddBundlingModelToViewModelInterceptor(WroModelHolder wroModelHolder, String bundlingEnabled) {
        this.wroModelHolder = wroModelHolder;
        this.bundlingEnabled = bundlingEnabled;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        modelAndView.addObject(JS_RESOURCES, getListOfUnbundledResources(ResourceType.JS, wroModelHolder.getWroModel(), request));
        modelAndView.addObject(CSS_RESOURCES, getListOfUnbundledResources(ResourceType.CSS, wroModelHolder.getWroModel(), request));
    }

    private Map<String, List<String>> getListOfUnbundledResources(ResourceType resourceType, WroModel wroModel, HttpServletRequest request) {
        Map<String, List<String>> resources = new HashMap<>();
        if (wroModel != null) {
            for (Group group : wroModel.getGroups()) {
                if (Strings.isNullOrEmpty(request.getParameter("bundle")) ? Boolean.parseBoolean(bundlingEnabled) : Boolean.parseBoolean(request.getParameter("bundle"))) {
                    resources.put(group.getName(), Arrays.asList("/bundle/" + group.getName() + "." + resourceType.name().toLowerCase() + (request.getQueryString().contains("minimize=false") ? "?minimize=false" : "")));
                } else {
                    resources.put(group.getName(), Lists.transform(wroModel.getGroupByName(group.getName()).collectResourcesOfType(resourceType).getResources(), RESOURCE_TO_URI));
                }
            }
        } else {
            resources.put("all", new ArrayList<String>());
        }
        return resources;
    }
}
