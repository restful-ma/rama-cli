package restful.api.metric.analyzer.cli.util;

public class PathOperationUtils {

	public static final String EMPTY_ROOT_NAME = "/emptyRootName";
	
	 private PathOperationUtils() {
		    throw new IllegalStateException("Utility class");
		  }

	/**
	 * Get Root from a path
	 *
	 * @param pathName the path as String
	 * @return rootName or a predefined EMPTY_ROOT_NAME
	 */
	public static String getRootFromPath(String pathName) {
		// add "/" as first char
		pathName = (pathName.length() > 0 && !pathName.startsWith("/")) ? "/" + pathName : pathName;

		if (pathName.equals("/"))
			return EMPTY_ROOT_NAME;

		if (pathName.startsWith("//"))
			return EMPTY_ROOT_NAME;

		// checks for /{name}/ starting path
		if (pathName.startsWith("/{") && (pathName.indexOf('/', 1) > pathName.indexOf('}')))
			return EMPTY_ROOT_NAME;

		// checks for /{name} starting path
		if (pathName.startsWith("/{") && (pathName.contains("}")) && (pathName.lastIndexOf('/') == 0))
			return EMPTY_ROOT_NAME;

		// checks for /abc.{name}/ starting path
		if (pathName.indexOf('{', 1) < pathName.indexOf('}')  && (pathName.indexOf('/', 1) > pathName.indexOf('}')))
			return EMPTY_ROOT_NAME;

		// checks for /abc.{name} starting path
		if (pathName.indexOf('{', 1) < pathName.indexOf('}') && (pathName.contains("}")) && (pathName.lastIndexOf('/') == 0))
			return EMPTY_ROOT_NAME;

		// root is start of string till first "/" sign
		// if no "/" sign occurs the return pathName
		if (pathName.length() > 0) {
			return pathName.lastIndexOf('/') > 0 ? pathName.substring(0, pathName.indexOf('/', 1)) : pathName;
		}

		return EMPTY_ROOT_NAME;
	}
}