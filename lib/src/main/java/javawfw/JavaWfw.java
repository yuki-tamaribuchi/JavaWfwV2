package javawfw;

import java.util.List;
import java.util.Map;

import javawfw.core.server.Server;
import javawfw.core.components.ComponentScanner;
import javawfw.core.paths.PathRegister;
import javawfw.core.paths.PathResolver;
import javawfw.core.paths.Path;


public class JavaWfw {
	private Server server;

	private JavaWfw(String ipAddr, int port, Class<?> clazz) {

		Map<Class<?>,Object> components = ComponentScanner.scan(clazz.getPackageName());
		System.out.println("Registered Components => " + components);
		List<Path> paths = PathRegister.register(components);
		PathResolver pathResolver = new PathResolver(paths);

		System.out.println("Server was started at " + ipAddr + ":" + port);
		server = new Server(ipAddr, port, components, pathResolver);
	}
		
}
