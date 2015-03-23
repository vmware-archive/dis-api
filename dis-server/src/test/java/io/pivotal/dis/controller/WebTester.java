package io.pivotal.dis.controller;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import io.pivotal.dis.Application;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import java.io.IOException;
import java.net.URL;

public class WebTester implements AutoCloseable {

    public static WebTester build(URL serverUrl) throws Exception {
        Application application = new Application(0, serverUrl);
        application.start();
        return new WebTester(application);
    }

    private final Application application;

    public WebTester(Application application) {
        this.application = application;
    }

    public String get(String path) throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setPathInfo(path);
        MockHttpServletResponse response = new MockHttpServletResponse();

        Handler handler = application.getHandler();
        Request baseRequest = new Request(null, null);
        handler.handle(path, baseRequest, request, response);

        return response.getOutputStreamContent();
    }

    @Override
    public void close() throws Exception {
        application.close();
    }

}
