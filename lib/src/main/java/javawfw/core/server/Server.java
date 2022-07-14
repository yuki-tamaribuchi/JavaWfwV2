package javawfw.core.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.stream.Stream;
import java.io.OutputStream;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.lang.reflect.Method;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.Headers;

import javawfw.core.paths.PathResolver;
import javawfw.http.Status;
import javawfw.http.response.IResponse;
import javawfw.annotations.PathVariable;
import javawfw.annotations.RequestParam;
import javawfw.core.paths.Path;
import javawfw.http.response.StatusResponse;
import javawfw.core.exceptions.paths.NullPathVariableMapException;


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


			Path path = pathResolver.resolve(finalUrl, getMethod(t.getRequestMethod()));
			if (path==null) {
				IResponse response = new StatusResponse(Status.NOT_FOUND);
				sendResponse(response, t);
			}
			Method method = path.getMethod();
			List<Object> invokeParameterList = createInvokeParameterList(method, path, finalUrl, getReqeustBody(t));
		}
	}

	private static String normalizeUrl(String url) {
		String normalizedUrl = url.substring(1);
		if (normalizedUrl.endsWith("/")) {
				normalizedUrl = normalizedUrl.substring(0, normalizedUrl.length()-1);
			}
		return normalizedUrl;
	}

	private static List<Object> createInvokeParameterList(Method method, Path path, String url, String requestBody) throws NullPathVariableMapException {
		List<Object> invokeParameterList = new ArrayList<>();

		List<Parameter> parameters = Arrays.asList(method.getParameters());
		Stream<Parameter> parametersSteam = parameters.stream();
		parametersSteam
		.forEach(param->{
			if (param.isAnnotationPresent(PathVariable.class)) {
				Map<String, String> pathVariableMap = path.getPathVariableMap(url.split("\\?")[0]);
				if (pathVariableMap == null) {
					throw new NullPathVariableMapException();
				}

				PathVariable pathVariable = param.getAnnotation(PathVariable.class);
				String pathVariableName = pathVariable.value();
				String pathVariableValue = pathVariableMap.get(pathVariableName);
				invokeParameterList.add(pathVariableValue);
			} else if (param.isAnnotationPresent(RequestParam.class)) {
				Map<String, String> requestParamMap = path.getRequestParamMap(url);

				RequestParam requestParam = param.getAnnotation(RequestParam.class);
				String requestParamName = requestParam.value();
				String requestParamValue = requestParamMap.get(requestParamName);
				invokeParameterList.add(requestParamValue);
			} else if (param.isAnnotationPresent(RequestBody.class)) {
				invokeParameterList.add(requestBody);
			}
		});
		return invokeParameterList;
	}

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
