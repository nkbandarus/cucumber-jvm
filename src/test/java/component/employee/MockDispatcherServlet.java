package component.employee;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import lombok.AllArgsConstructor;

import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

@Service
public class MockDispatcherServlet {

    private DispatcherServlet servlet;

    private static final Map<String, String> defaultHeaders;
    static {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        defaultHeaders = Collections.unmodifiableMap(headers);
    }

    public MockDispatcherServlet() {

         String contextConfigLocation = "component-applicationContext.xml";
         System.out.println("spring config location: " + contextConfigLocation);

         servlet = new DispatcherServlet();
         MockServletConfig config = new MockServletConfig("resources");
         config.addInitParameter("contextConfigLocation", contextConfigLocation);
         try {
             servlet.init(config);
         } catch (ServletException e) {
             throw new RuntimeException(e);
         }

    }

    public MockHttpServletRepsonseToStringWrapper send(String method, String url) {
        return service(method, url, "", new HashMap<String, String>());
    }

    public MockHttpServletRepsonseToStringWrapper get(String url) {
        return get(url, new HashMap<String, String>());
    }

    public MockHttpServletRepsonseToStringWrapper get(String url, Map<String, String> headers) {
        return service("GET", url, "", headers);
    }

    public MockHttpServletRepsonseToStringWrapper post(String url) {
        return post(url, "");
    }

    public MockHttpServletRepsonseToStringWrapper post(String url, String body) {
        return post(url, body, new HashMap<String, String>());
    }

    public MockHttpServletRepsonseToStringWrapper post(String url, String body, Map<String, String> headers) {
        return service("POST", url, body, headers);
    }

    public MockHttpServletRepsonseToStringWrapper put(String url) {
        return put(url, "");
    }

    public MockHttpServletRepsonseToStringWrapper put(String url, String body){
        return service("PUT", url, body, new HashMap<String, String>());
    }

    public MockHttpServletRepsonseToStringWrapper put(String url, String body, Map<String, String> headers){
        return service("PUT", url, body, headers);
    }

    public MockHttpServletRepsonseToStringWrapper delete(String url) {
        return service("DELETE", url, "", new HashMap<String,String>(), "");
    }

    private MockHttpServletRepsonseToStringWrapper service(String method, String urlString, String body,
                                                           Map<String, String> headers, final String username) {

        LocalURI localURI = new LocalURI(urlString);
        MockHttpServletRequest request = new MockHttpServletRequest(method, localURI.getPath());
        // Setting paths explicitly in order for spring security to apply URL rules
        request.setServletPath("/resources");
        request.setPathInfo(localURI.getPath());

        for(NameValuePair pair : localURI.params()){
            request.addParameter(pair.getName(), pair.getValue() == null ? "" : pair.getValue());
        }

        if(username != null){
            request.setUserPrincipal(new Principal() {
                public String getName() {
                    return username;
                }
            });
        }

        request.setContent(body.getBytes());
        MockHttpServletRepsonseToStringWrapper response = new MockHttpServletRepsonseToStringWrapper();
        addHeaders(request, headers);
        try {
            servlet.service(request, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    public WebApplicationContext context(){
        return servlet.getWebApplicationContext();
    }

    private MockHttpServletRepsonseToStringWrapper service(String method, String url, String body, Map<String, String> headers) {
        return service(method, url, body, headers, null);
    }

    private MockHttpServletRepsonseToStringWrapper service(String url, String fileName, InputStream fileStream) {
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        request.setRequestURI(url);
        try {
            request.addFile(new MockMultipartFile(fileName, IOUtils.toByteArray(fileStream)));
        } catch (IOException e1) {
            throw new RuntimeException(e1);
        }
        MockHttpServletRepsonseToStringWrapper response = new MockHttpServletRepsonseToStringWrapper();
        try {
            servlet.service(request, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    private void addHeaders(MockHttpServletRequest request,
                            Map<String, String> headers) {
        Map<String, String> allHeaders = new HashMap<String, String>();
        allHeaders.putAll(defaultHeaders);
        allHeaders.putAll(headers);

        for (Map.Entry<String, String> entry : allHeaders.entrySet()) {
            request.addHeader(entry.getKey(), entry.getValue());
        }
    }

    public MockHttpServletRepsonseToStringWrapper postFile(String url, String fileName, InputStream fileStream) {
        return service(url, fileName, fileStream);
    }

    public static class MockHttpServletRepsonseToStringWrapper extends MockHttpServletResponse {
        public String toString() {
            String msg = String.format("Status[%s] Msg[%s]", getStatus(), getErrorMessage()) ;
            try {
                msg += "\t" + getContentAsString();
            } catch (UnsupportedEncodingException e) { /* WTF Mate? */ }
            return msg;
        }
    }

    public <T> T getBean(Class<T> type)
    {
        return servlet.getWebApplicationContext().getBean(type);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(String beanName) {
        return (T) servlet.getWebApplicationContext().getBean(beanName);
    }

    @AllArgsConstructor
    public static class LocalURI{
        private String path;

        public List<NameValuePair> params(){
            return URLEncodedUtils.parse(url(), "UTF8");
        }

        public String getPath(){
            return url().getPath();
        }

        private URI url() {
            URI url = null;
            try {
                url = new URI("http://localhost" + path);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            } 
            return url;
    }
    }
}

