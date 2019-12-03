package restful.api.metric.analyzer.cli.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import restful.api.metric.analyzer.cli.util.PathOperationUtils;

class PathOperationUtilsTest {

	@Test
	void rootPathName() {
		assertEquals(PathOperationUtils.EMPTY_ROOT_NAME, PathOperationUtils.getRootFromPath(""));
		assertEquals(PathOperationUtils.EMPTY_ROOT_NAME, PathOperationUtils.getRootFromPath("/"));
		assertEquals(PathOperationUtils.EMPTY_ROOT_NAME, PathOperationUtils.getRootFromPath("//"));
		assertEquals(PathOperationUtils.EMPTY_ROOT_NAME, PathOperationUtils.getRootFromPath("//"));

		assertEquals("/{", PathOperationUtils.getRootFromPath("/{"));
		assertEquals("/{ab", PathOperationUtils.getRootFromPath("/{ab"));

		assertEquals(PathOperationUtils.EMPTY_ROOT_NAME, PathOperationUtils.getRootFromPath("/{name}"));
		assertEquals(PathOperationUtils.EMPTY_ROOT_NAME, PathOperationUtils.getRootFromPath("/{name}/"));
		assertEquals(PathOperationUtils.EMPTY_ROOT_NAME, PathOperationUtils.getRootFromPath("/{name}/ab"));
		assertEquals(PathOperationUtils.EMPTY_ROOT_NAME, PathOperationUtils.getRootFromPath("/{name}/{ea}"));
		assertEquals(PathOperationUtils.EMPTY_ROOT_NAME, PathOperationUtils.getRootFromPath("/"));
		assertEquals(PathOperationUtils.EMPTY_ROOT_NAME, PathOperationUtils.getRootFromPath("{name}"));
		assertEquals(PathOperationUtils.EMPTY_ROOT_NAME, PathOperationUtils.getRootFromPath("{name}/ab"));
		assertEquals(PathOperationUtils.EMPTY_ROOT_NAME, PathOperationUtils.getRootFromPath("{name}/"));


		assertEquals("/pet", PathOperationUtils.getRootFromPath("/pet"));
		assertEquals("/pet", PathOperationUtils.getRootFromPath("/pet/"));
		assertEquals("/pet", PathOperationUtils.getRootFromPath("/pet/{name}"));
		assertEquals("/pet", PathOperationUtils.getRootFromPath("/pet/{name}//"));

		assertEquals("/pet{", PathOperationUtils.getRootFromPath("/pet{"));
		assertEquals("/pet{", PathOperationUtils.getRootFromPath("/pet{/"));
		assertEquals("/pet{", PathOperationUtils.getRootFromPath("/pet{//"));
		assertEquals("/pet{", PathOperationUtils.getRootFromPath("/pet{/}/"));

		assertEquals(PathOperationUtils.EMPTY_ROOT_NAME, PathOperationUtils.getRootFromPath("/pet{}"));
		assertEquals(PathOperationUtils.EMPTY_ROOT_NAME, PathOperationUtils.getRootFromPath("/pet{}/"));
		assertEquals(PathOperationUtils.EMPTY_ROOT_NAME, PathOperationUtils.getRootFromPath("/pet{}/{name}"));

		assertEquals("/123", PathOperationUtils.getRootFromPath("123/"));
		assertEquals("/123{", PathOperationUtils.getRootFromPath("123{/"));
		assertEquals(PathOperationUtils.EMPTY_ROOT_NAME, PathOperationUtils.getRootFromPath("123{}/"));
		assertEquals("/users", PathOperationUtils.getRootFromPath("users/{user_id}/favorites/{track_id}.{format}"));
		assertEquals(PathOperationUtils.EMPTY_ROOT_NAME, PathOperationUtils.getRootFromPath("users.{format}"));
		assertEquals(PathOperationUtils.EMPTY_ROOT_NAME, PathOperationUtils.getRootFromPath("users.{format}///"));
		assertEquals(PathOperationUtils.EMPTY_ROOT_NAME, PathOperationUtils.getRootFromPath("users.{format}/"));
		assertEquals(PathOperationUtils.EMPTY_ROOT_NAME, PathOperationUtils.getRootFromPath("{users}.{format}"));
		assertEquals(PathOperationUtils.EMPTY_ROOT_NAME, PathOperationUtils.getRootFromPath("/{users}.{format}"));
		assertEquals("/}{", PathOperationUtils.getRootFromPath("/}{/a"));

	}
}