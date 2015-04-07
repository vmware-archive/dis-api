package io.pivotal.dis;

import io.pivotal.dis.controller.TflProxyController;
import io.pivotal.dis.service.DisruptedLinesService;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class Application implements AutoCloseable {

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getenv("PORT"));
        Application application = new Application(port, constructRedisUri());
        application.run();
    }

    private static URI constructRedisUri() throws URISyntaxException {
        int redisPort = Integer.parseInt(System.getenv("REDIS_PORT")); // TODO - fix for CF.
        return new URI("http://localhost:" + redisPort);
    }

    private final TflProxyController controller;
    private final DisruptedLinesService disruptedLinesService;
    private final Handler handler;
    private final Server server;

    public Application(int port, URI redisUri) {
        disruptedLinesService = new DisruptedLinesService(redisUri);
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
