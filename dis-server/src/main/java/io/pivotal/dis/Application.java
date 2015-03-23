package io.pivotal.dis;

import io.pivotal.dis.controller.TflProxyController;
import io.pivotal.dis.provider.TimeProvider;
import io.pivotal.dis.provider.TimeProviderImpl;
import io.pivotal.dis.service.DisruptedLinesService;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

public class Application implements AutoCloseable {

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getenv("PORT"));
        Properties properties = loadProperties();
        URL tflUrl = constructTflUrl(properties);
        Application application = new Application(port, tflUrl);
        application.run();
    }

    private static URL constructTflUrl(Properties properties) throws MalformedURLException {
        return new URL("http://api.tfl.gov.uk/Line/Mode/%7Bmodes%7D/Status?modes=tube&detail=False&app_id=" + properties.getProperty("tfl.appId") + "&app_key=" + properties.getProperty("tfl.appKey"));
    }

    private static Properties loadProperties() throws IOException {
        try (InputStream inputStream = openResource("application.properties")) {
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        }
    }

    private static InputStream openResource(String name) throws FileNotFoundException {
        InputStream inputStream = Application.class.getResourceAsStream("/" + name);
        if (inputStream == null) {
            throw new FileNotFoundException("resource '" + name + "' not found in the classpath");
        }
        return inputStream;
    }

    private final TimeProvider clock;
    private final TflProxyController controller;
    private final DisruptedLinesService disruptedLinesService;
    private final Handler handler;
    private final Server server;

    public Application(int port, URL tflUrl) {
        clock = new TimeProviderImpl();
        disruptedLinesService = new DisruptedLinesService(clock, tflUrl);
        controller = new TflProxyController(disruptedLinesService);

        server = new Server(port);
        handler = new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                response.setContentType("application/json;charset=utf-8");
                response.setStatus(HttpServletResponse.SC_OK);
                baseRequest.setHandled(true);
                Map disruptions = controller.lineDisruptions();
                JSONObject json = new JSONObject(disruptions);
                PrintWriter writer = response.getWriter();
                writer.println(json.toString());
                writer.close();
            }
        };
        server.setHandler(handler);
    }

    public void start() throws Exception {
        server.start();
    }

    public void run() throws Exception {
        start();
        server.join();
    }

    public Handler getHandler() {
        return handler;
    }

    @Override
    public void close() throws Exception {
        server.stop();
        server.join();
    }

}
