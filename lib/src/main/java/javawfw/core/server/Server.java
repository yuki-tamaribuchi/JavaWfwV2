package javawfw.core.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.Headers;

import javawfw.core.paths.PathResolver;


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
		}
	}
}
