package javawfw.core.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.Headers;

import javawfw.core.paths.PathResolver;
import javawfw.http.response.IResponse;


public class Server {
	HttpServer server;
	private static Map<Class<?>, Object> components;
	private static PathResolver pathResolver;

	public Server(String ipAddr, int port, Map<Class<?>, Object> components, PathResolver pathResolver) {
		Server.pathResolver = pathResolver;
		Server.components = components;

		try {
			server = HttpServer.create(new InetSocketAddress(ipAddr, port), 0);
			server.createContext("/", new Handler());
			server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		

	private static class Handler implements HttpHandler {
		public void handle(HttpExchange t) {
			final String finalUrl = normalizeUrl(t.getRequestURI().toString());
		}
	}

	private static String normalizeUrl(String url) {
		String normalizedUrl = url.substring(1);
		if (normalizedUrl.endsWith("/")) {
				normalizedUrl = normalizedUrl.substring(0, normalizedUrl.length()-1);
			}
		return normalizedUrl;

	private static void sendResponse(IResponse response, HttpExchange t) {
		int status = response.getStatus();
		long contentLength = response.getContentLength();
		Map<String, String> options = response.getOptions();
		Headers headers = t.getResponseHeaders();
		options.forEach((String key, String value)->{
			headers.set(key, value);
		});

		try {
			t.sendResponseHeaders(status, contentLength);
			OutputStream os = t.getResponseBody();
			os.write(response.getBody().getBytes());
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
