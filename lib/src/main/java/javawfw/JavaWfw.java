package javawfw;

import java.util.Map;

import javawfw.core.components.ComponentScanner;

public class JavaWfw {
	private JavaWfw(String ipAddr, int port, Class<?> clazz) {

		Map<Class<?>,Object> components = ComponentScanner.scan(clazz.getPackageName());
		System.out.println("Registered Components => " + components);
	}
}
