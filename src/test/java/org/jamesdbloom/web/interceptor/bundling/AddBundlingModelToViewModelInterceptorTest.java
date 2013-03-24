package org.jamesdbloom.web.interceptor.bundling;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import ro.isdc.wro.model.WroModel;
import ro.isdc.wro.model.group.Group;
import ro.isdc.wro.model.resource.Resource;
import ro.isdc.wro.model.resource.ResourceType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * @author jamesdbloom
 */
@RunWith(MockitoJUnitRunner.class)
public class AddBundlingModelToViewModelInterceptorTest {

    @Mock
    private WroModelHolder wroModelHolder;
    private MockHttpServletRequest mockHttpServletRequest;

    @Before
    public void setupFixture() {
        WroModel wroModel = new WroModel();

        Group group_one = new Group("group_one");
        Group group_two = new Group("group_two");
        wroModel.setGroups(Arrays.asList(group_one, group_two));

        group_one.addResource(Resource.create("group_one_css_uri_one", ResourceType.CSS));
        group_one.addResource(Resource.create("group_one_css_uri_two", ResourceType.CSS));
        group_one.addResource(Resource.create("group_one_js_uri_one", ResourceType.JS));
        group_one.addResource(Resource.create("group_one_js_uri_two", ResourceType.JS));

        group_two.addResource(Resource.create("group_two_css_uri_one", ResourceType.CSS));
        group_two.addResource(Resource.create("group_two_css_uri_two", ResourceType.CSS));
        group_two.addResource(Resource.create("group_two_js_uri_one", ResourceType.JS));
        group_two.addResource(Resource.create("group_two_js_uri_two", ResourceType.JS));

        when(wroModelHolder.getWroModel()).thenReturn(wroModel);

        mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setQueryString("");
    }

    @Test
    public void testShouldReturnUnbundledResourcesWhenBundlingDisabled() throws Exception {
        // given - setupFixture and
        ModelAndView modelAndView = new ModelAndView();

        // when
        new AddBundlingModelToViewModelInterceptor(wroModelHolder, "false").postHandle(mockHttpServletRequest, null, null, modelAndView);

        // then
        checkViewModelContainsCorrectUnbundledResources(modelAndView);
    }

    private void checkViewModelContainsCorrectUnbundledResources(ModelAndView modelAndView) {
        assertEquals(Arrays.asList("group_one_js_uri_one", "group_one_js_uri_two"), ((Map<String, List<String>>) modelAndView.getModel().get(AddBundlingModelToViewModelInterceptor.JS_RESOURCES)).get("group_one"));
        assertEquals(Arrays.asList("group_two_js_uri_one", "group_two_js_uri_two"), ((Map<String, List<String>>) modelAndView.getModel().get(AddBundlingModelToViewModelInterceptor.JS_RESOURCES)).get("group_two"));
        assertEquals(Arrays.asList("group_one_css_uri_one", "group_one_css_uri_two"), ((Map<String, List<String>>) modelAndView.getModel().get(AddBundlingModelToViewModelInterceptor.CSS_RESOURCES)).get("group_one"));
        assertEquals(Arrays.asList("group_two_css_uri_one", "group_two_css_uri_two"), ((Map<String, List<String>>) modelAndView.getModel().get(AddBundlingModelToViewModelInterceptor.CSS_RESOURCES)).get("group_two"));
    }

    private void checkViewModelContainsCorrectBundledResources(ModelAndView modelAndView, String extraQueryString) {
        assertEquals(Arrays.asList("/bundle/group_one.js" + extraQueryString), ((Map<String, List<String>>) modelAndView.getModel().get(AddBundlingModelToViewModelInterceptor.JS_RESOURCES)).get("group_one"));
        assertEquals(Arrays.asList("/bundle/group_two.js" + extraQueryString), ((Map<String, List<String>>) modelAndView.getModel().get(AddBundlingModelToViewModelInterceptor.JS_RESOURCES)).get("group_two"));
        assertEquals(Arrays.asList("/bundle/group_one.css" + extraQueryString), ((Map<String, List<String>>) modelAndView.getModel().get(AddBundlingModelToViewModelInterceptor.CSS_RESOURCES)).get("group_one"));
        assertEquals(Arrays.asList("/bundle/group_two.css" + extraQueryString), ((Map<String, List<String>>) modelAndView.getModel().get(AddBundlingModelToViewModelInterceptor.CSS_RESOURCES)).get("group_two"));
    }

    @Test
    public void testShouldReturnBundledResourcesWhenBundlingEnabled() throws Exception {
        // given - setupFixture and
        ModelAndView modelAndView = new ModelAndView();

        // then
        new AddBundlingModelToViewModelInterceptor(wroModelHolder, "true").postHandle(mockHttpServletRequest, null, null, modelAndView);

        // then
        checkViewModelContainsCorrectBundledResources(modelAndView, "");
    }

    @Test
    public void testShouldReturnUnbundledResourcesWhenBundlingDisabledByQueryParameter() throws Exception {
        // given - setupFixture and
        ModelAndView modelAndView = new ModelAndView();
        mockHttpServletRequest.addParameter("bundling", "false");

        // then
        new AddBundlingModelToViewModelInterceptor(wroModelHolder, "true").postHandle(mockHttpServletRequest, null, null, modelAndView);

        // then
        checkViewModelContainsCorrectBundledResources(modelAndView, "");
    }

    @Test
    public void testShouldReturnUnbundledResourcesWhenBundlingEnabledByQueryParameter() throws Exception {
        // given - setupFixture and
        ModelAndView modelAndView = new ModelAndView();
        mockHttpServletRequest.addParameter("bundling", "true");

        // then
        new AddBundlingModelToViewModelInterceptor(wroModelHolder, "false").postHandle(mockHttpServletRequest, null, null, modelAndView);

        // then
        checkViewModelContainsCorrectUnbundledResources(modelAndView);
    }

    @Test
    public void testShouldReturnUnminifiedBundledResourcesWhenMinificationDisabledByQueryParameter() throws Exception {
        // given - setupFixture and
        ModelAndView modelAndView = new ModelAndView();
        mockHttpServletRequest.setQueryString("minimize=false");

        // then
        new AddBundlingModelToViewModelInterceptor(wroModelHolder, "true").postHandle(mockHttpServletRequest, null, null, modelAndView);

        // then
        checkViewModelContainsCorrectBundledResources(modelAndView, "?minimize=false");
    }
}
