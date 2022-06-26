package javawfw.core.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.Headers;


public class Server {
	HttpServer server;

	public Server(String ipAddr, int port) {
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
		}
	}
}
