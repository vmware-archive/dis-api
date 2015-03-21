package io.pivotal.dis;

import io.pivotal.dis.config.ApplicationConfiguration;
import io.pivotal.dis.controller.TflProxyController;
import io.pivotal.dis.provider.TflUrlProvider;
import io.pivotal.dis.provider.TimeProvider;
import io.pivotal.dis.provider.TimeProviderImpl;
import io.pivotal.dis.service.DisruptedLinesService;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;

import org.eclipse.jetty.server.Server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class Application {
    //public static void main(String[] args) {
    //    SpringApplication.run(ApplicationConfiguration.class, args);
    //}

    public static void main(String[] args) throws Exception {
        new Application();
    }

    public Application() throws Exception{
        Properties properties = new Properties();
        String propertyFileName = "application.properties";
        String tflApiId = "";
        String tflApiKey = "";

        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertyFileName);
            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propertyFileName + "' not found in the classpath");
            }
            tflApiId = properties.getProperty("tfl.appId");
            tflApiKey = properties.getProperty("tfl.appKey");
        } catch (Exception ex){
            ex.printStackTrace();
        }

        TimeProvider clock = new TimeProviderImpl();
        TflUrlProvider tflUrlProvider = new TflUrlProvider(tflApiId, tflApiKey);
        TflProxyController controller = new TflProxyController();
        DisruptedLinesService disruptedLinesService = new DisruptedLinesService(clock, tflUrlProvider);
        controller.setDisruptedLinesService(disruptedLinesService);

        int port = Integer.parseInt(System.getenv("PORT"));
        Server server = new Server(port);
        server.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                response.setContentType("application/json;charset=utf-8");
                response.setStatus(HttpServletResponse.SC_OK);
                baseRequest.setHandled(true);
                Map disruptions = controller.lineDisruptions();
                JSONObject json = new JSONObject(disruptions);
                response.getWriter().println(json.toString());
            }
        });
        server.start();
        server.join();
    }

}
