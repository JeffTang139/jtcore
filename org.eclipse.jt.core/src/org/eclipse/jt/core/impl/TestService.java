package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.None;
import org.eclipse.jt.core.RemoteLoginInfo;
import org.eclipse.jt.core.TreeNode;
import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.invoke.AsyncHandle;
import org.eclipse.jt.core.invoke.AsyncState;
import org.eclipse.jt.core.invoke.AsyncTask;
import org.eclipse.jt.core.invoke.SimpleTask;
import org.eclipse.jt.core.invoke.Task;
import org.eclipse.jt.core.service.AsyncInfo;
import org.eclipse.jt.core.service.Publish;
import org.eclipse.jt.core.service.ServiceInvoker;
import org.eclipse.jt.core.service.Publish.Mode;


final class TestService extends ServiceBase<ContextImpl<?, ?, ?>> {
	protected TestService() {
		super("core test service");
	}

	@Override
	protected void init(Context context) throws Throwable {
		super.init(context);
		if (Boolean.getBoolean("lrj.test.remote")) {
			this.testRemote(context);
		}
		if (Boolean.getBoolean("core.test.repeat")) {
			System.out.println("repeat test begin:");
			context.asyncHandle(new RepeatTesterTask(), new AsyncInfo(2000,
					2000));
		}
		if (Boolean.getBoolean("test.nnet")) {
			this.testNRemote(ContextImpl.toContext(context));
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// ********************* Test Remote Invocation ****************************

	@Publish(Mode.SITE_PUBLIC)
	final class RemoteQueryTestNoKeyProvider extends
			ResultProvider<RemoteQueryAnswer> {
		@Override
		protected RemoteQueryAnswer provide(ContextImpl<?, ?, ?> context)
				throws Throwable {
			return new RemoteQueryAnswer();
		}
	}

	@Publish(Mode.SITE_PUBLIC)
	final class RemoteQueryTestOneKeyProvider extends
			OneKeyResultProvider<RemoteQueryAnswer, String> {
		@Override
		protected RemoteQueryAnswer provide(ContextImpl<?, ?, ?> context,
				String key1) throws Throwable {
			return new RemoteQueryAnswer(key1);
		}
	}

	@Publish(Mode.SITE_PUBLIC)
	final class RemoteQueryTestTwoKeyProvider extends
			TwoKeyResultProvider<RemoteQueryAnswer, String, String> {
		@Override
		protected RemoteQueryAnswer provide(ContextImpl<?, ?, ?> context,
				String key1, String key2) throws Throwable {
			return new RemoteQueryAnswer(key1, key2);
		}
	}

	@Publish(Mode.SITE_PUBLIC)
	final class RemoteQueryTestThreeKeyProvider extends
			ThreeKeyResultProvider<RemoteQueryAnswer, String, String, String> {
		@Override
		protected RemoteQueryAnswer provide(ContextImpl<?, ?, ?> context,
				String key1, String key2, String key3) throws Throwable {
			return new RemoteQueryAnswer(key1, key2, key3);
		}
	}

	@Publish(Mode.SITE_PUBLIC)
	final class RemoteListQueryTestNoKeyProvider extends
			ResultListProvider<RemoteQueryAnswer> {
		@Override
		protected void provide(ContextImpl<?, ?, ?> context,
				List<RemoteQueryAnswer> resultList) throws Throwable {
			RemoteListQueryAnswer.convert(resultList);
		}
	}

	@Publish(Mode.SITE_PUBLIC)
	final class RemoteListQueryTestOneKeyProvider extends
			OneKeyResultListProvider<RemoteQueryAnswer, String> {
		@Override
		protected void provide(ContextImpl<?, ?, ?> context, String key,
				List<RemoteQueryAnswer> resultList) throws Throwable {
			RemoteListQueryAnswer.convert(resultList, key);
		}
	}

	@Publish(Mode.SITE_PUBLIC)
	final class RemoteListQueryTestTwoKeyProvider extends
			TwoKeyResultListProvider<RemoteQueryAnswer, String, String> {
		@Override
		protected void provide(ContextImpl<?, ?, ?> context, String key1,
				String key2, List<RemoteQueryAnswer> resultList)
				throws Throwable {
			RemoteListQueryAnswer.convert(resultList, key1, key2);
		}
	}

	@Publish(Mode.SITE_PUBLIC)
	final class RemoteListQueryTestThreeKeyProvider
			extends
			ThreeKeyResultListProvider<RemoteQueryAnswer, String, String, String> {
		@Override
		protected void provide(ContextImpl<?, ?, ?> context, String key1,
				String key2, String key3, List<RemoteQueryAnswer> resultList)
				throws Throwable {
			RemoteListQueryAnswer.convert(resultList, key1, key2, key3);
		}
	}

	@Publish(Mode.SITE_PUBLIC)
	final class RemoteTreeNodeQueryTestNoKeyProvider extends
			TreeNodeProvider<RemoteQueryAnswer> {
		@Override
		protected int provide(ContextImpl<?, ?, ?> context,
				TreeNode<RemoteQueryAnswer> resultTreeNode) throws Throwable {
			resultTreeNode.setElement(new RemoteTreeNodeQueryAnswer());
			return 0;
		}
	}

	@Publish(Mode.SITE_PUBLIC)
	final class RemoteTreeNodeQueryTestOneKeyProvider extends
			OneKeyTreeNodeProvider<RemoteQueryAnswer, String> {
		@Override
		protected int provide(ContextImpl<?, ?, ?> context, String key,
				TreeNode<RemoteQueryAnswer> resultTreeNode) throws Throwable {
			resultTreeNode.setElement(new RemoteTreeNodeQueryAnswer(key));
			return 1;
		}
	}

	@Publish(Mode.SITE_PUBLIC)
	final class RemoteTreeNodeQueryTestTwoKeyProvider extends
			TwoKeyTreeNodeProvider<RemoteQueryAnswer, String, String> {
		@Override
		protected int provide(ContextImpl<?, ?, ?> context, String key1,
				String key2, TreeNode<RemoteQueryAnswer> resultTreeNode)
				throws Throwable {
			resultTreeNode.setElement(new RemoteTreeNodeQueryAnswer(key1));
			resultTreeNode.append(new RemoteTreeNodeQueryAnswer(key2));
			return 1;
		}
	}

	@Publish(Mode.SITE_PUBLIC)
	final class RemoteTreeNodeQueryTestThreeKeyProvider extends
			ThreeKeyTreeNodeProvider<RemoteQueryAnswer, String, String, String> {
		@Override
		protected int provide(ContextImpl<?, ?, ?> context, String key1,
				String key2, String key3,
				TreeNode<RemoteQueryAnswer> resultTreeNode) throws Throwable {
			resultTreeNode.setElement(new RemoteTreeNodeQueryAnswer(key1));
			resultTreeNode.append(new RemoteTreeNodeQueryAnswer(key2));
			resultTreeNode.append(new RemoteTreeNodeQueryAnswer(key3));
			return 1;
		}
	}

	@Publish(Mode.SITE_PUBLIC)
	final class RemoteSimpleTaskHandler extends
			TaskMethodHandler<SimpleTaskForRemoteTest, None> {
		protected RemoteSimpleTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected void handle(ContextImpl<?, ?, ?> context,
				SimpleTaskForRemoteTest task) throws Throwable {
			task.setResult("Simple Task Return");
		}
	}

	@Publish(Mode.SITE_PUBLIC)
	final class RemoteTaskHandler extends
			TaskMethodHandler<TaskForRemoteTest, TaskForRemoteTest.Method> {
		protected RemoteTaskHandler() {
			super(TaskForRemoteTest.Method.REMOTE, null);
		}

		@Override
		protected void handle(ContextImpl<?, ?, ?> context,
				TaskForRemoteTest task) throws Throwable {
			task.setResult(task.condition
					.substring(task.condition.length() / 2));
		}
	}

	static final void printTree(TreeNode<?> node) {
		if (print) {
			System.out.print("TREE {");
			if (node == null) {
				System.out.print("null");
			} else {
				int c = 0;
				Iterator<?> i = node.iterator();
				while (i.hasNext()) {
					if (c > 0) {
						System.out.print(", ");
					}
					c++;
					System.out.print(i.next());
				}
			}
			System.out.println("}");
		}
	}

	private static final void PAUSE() {
		System.out.println("Press any key to continue ...");
		try {
			System.in.skip(System.in.available());
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static boolean print = true;

	static void print(Object obj) {
		if (print) {
			System.out.print(obj);
		}
	}

	static void println(Object obj) {
		if (print) {
			System.out.println(obj);
		}
	}

	private final void testNRemote(ContextImpl<?, ?, ?> context) {
		try {
			final NetSessionImpl ns = this.site.application.getNetNode(
					new URL("http://localhost:9797/dna_core/ncl")).newSession();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 100000; i++) {
				sb.append('a');
			}
			String s = sb.toString();
			AsyncTask<TaskForRemoteTest, TaskForRemoteTest.Method> taskHandle = ns
					.newRequest(new TaskForRemoteTest(s),
							TaskForRemoteTest.Method.REMOTE);
			context.waitFor(taskHandle);
			if (taskHandle.getState() == AsyncState.ERROR
					|| taskHandle.getTask().result.length() != s.length() / 2) {
				throw new Exception("任务执行出错");
			}
			long dt = System.currentTimeMillis();
			System.out.println("testNRemote start");
			for (int j = 0; j < 10; j++) {
				Thread.sleep(5000);
				System.out.println("round " + j);
				ArrayList<AsyncHandle> handles = new ArrayList<AsyncHandle>();
				for (int i = 0; i < 10; i++) {
					handles.add(ns.newRequest(new TaskForRemoteTest(s),
							TaskForRemoteTest.Method.REMOTE));
				}
				context
						.waitFor(taskHandle, handles
								.toArray(new AsyncHandle[0]));
			}
			dt = System.currentTimeMillis() - dt;
			System.out.println("testNRemote done:" + dt);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final void testRemote(Context context) {
		System.out.println("To test remote invokation");
		PAUSE();
		String host = System.getProperty("lrj.test.remote.host", "localhost");
		String prt = System.getProperty("lrj.test.remote.port");
		int port = RIUtil.DEFAULT_PORT;
		if (prt != null) {
			port = Integer.parseInt(prt);
		}
		RemoteLoginInfo rli = context.allocRemoteLoginInfo(host, port);
		ServiceInvoker invoker = context.usingRemoteInvoker(rli);

		System.err.print("SERVICE-OBJECT: ");
		System.err.println(invoker);

		RITestHelper.clear();

		long start0 = System.currentTimeMillis();
		// print = false;
		println(invoker.find(RemoteQueryAnswer.class));
		println(invoker.find(RemoteQueryAnswer.class, "one key query"));
		println(invoker.find(RemoteQueryAnswer.class, "two keys", "query"));
		println(invoker.find(RemoteQueryAnswer.class, "three", "keys", "query"));
		println(invoker.find(RemoteQueryAnswer.class, "lots", "of", "keys",
				"query"));
		println(invoker.getList(RemoteQueryAnswer.class));
		println(invoker.getList(RemoteQueryAnswer.class, "one key query"));
		println(invoker.getList(RemoteQueryAnswer.class, "two keys", "query"));
		println(invoker.getList(RemoteQueryAnswer.class, "three", "keys",
				"query"));
		try {
			println(invoker.getList(RemoteQueryAnswer.class, "lots", "of",
					"keys", "query"));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		printTree(invoker.getTreeNode(RemoteQueryAnswer.class));
		printTree(invoker.getTreeNode(RemoteQueryAnswer.class, "one key query"));
		printTree(invoker.getTreeNode(RemoteQueryAnswer.class, "two keys",
				"query"));
		printTree(invoker.getTreeNode(RemoteQueryAnswer.class, "three", "keys",
				"query"));
		try {
			printTree(invoker.getTreeNode(RemoteQueryAnswer.class, "lots",
					"of", "keys", "query"));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		SimpleTaskForRemoteTest stask = new SimpleTaskForRemoteTest(
				"simple handle");
		println(stask);
		invoker.handle(stask);
		println(stask);
		TaskForRemoteTest task = new TaskForRemoteTest("handle");
		print(TaskForRemoteTest.Method.REMOTE);
		print('\t');
		println(task);
		invoker.handle(task, TaskForRemoteTest.Method.REMOTE);
		print(TaskForRemoteTest.Method.REMOTE);
		print('\t');
		println(task);
		long end1 = System.currentTimeMillis();

		print = false;
		for (int i = 0; i < 100; i++) {
			println(invoker.find(RemoteQueryAnswer.class));
			println(invoker.find(RemoteQueryAnswer.class, "one key query"));
			println(invoker.find(RemoteQueryAnswer.class, "two keys", "query"));
			println(invoker.find(RemoteQueryAnswer.class, "three", "keys",
					"query"));
			println(invoker.find(RemoteQueryAnswer.class, "lots", "of", "keys",
					"query"));
			println(invoker.getList(RemoteQueryAnswer.class));
			println(invoker.getList(RemoteQueryAnswer.class, "one key query"));
			println(invoker.getList(RemoteQueryAnswer.class, "two keys",
					"query"));
			println(invoker.getList(RemoteQueryAnswer.class, "three", "keys",
					"query"));
			// try {
			// println(invoker.getList(RemoteQueryAnswer.class,
			// "lots", "of", "keys", "query"));
			// } catch (Throwable e) {
			// e.printStackTrace();
			// }
			printTree(invoker.getTreeNode(RemoteQueryAnswer.class));
			printTree(invoker.getTreeNode(RemoteQueryAnswer.class,
					"one key query"));
			printTree(invoker.getTreeNode(RemoteQueryAnswer.class, "two keys",
					"query"));
			printTree(invoker.getTreeNode(RemoteQueryAnswer.class, "three",
					"keys", "query"));
			// try {
			// printTree(invoker.getTreeNode(RemoteQueryAnswer.class, "lots",
			// "of", "keys", "query"));
			// } catch (Throwable e) {
			// e.printStackTrace();
			// }
			stask = new SimpleTaskForRemoteTest("simple handle");
			println(stask);
			invoker.handle(stask);
			println(stask);
			task = new TaskForRemoteTest("handle");
			print(TaskForRemoteTest.Method.REMOTE);
			print('\t');
			println(task);
			invoker.handle(task, TaskForRemoteTest.Method.REMOTE);
			print(TaskForRemoteTest.Method.REMOTE);
			print('\t');
			println(task);
		}

		long endx = System.currentTimeMillis();

		System.out.println("------------------------------------------------");
		System.out.format("%s%n%s%n%s%n%s\t%s%n", start0, end1, endx, end1
				- start0, endx - end1);
		System.out.println("------------------------------------------------");
		RITestHelper.printAll();
	}

	@StructClass
	static class SimpleTaskForRemoteTest extends SimpleTask {
		final String condition;
		String result;

		SimpleTaskForRemoteTest(String condition) {
			this.condition = condition;
		}

		void setResult(String result) {
			this.result = result;
		}

		@Override
		public String toString() {
			return String.format("SIMPLE-TASK(C:%s, R:%s)", this.condition,
					this.result);
		}
	}

	@StructClass
	static class TaskForRemoteTest extends Task<TaskForRemoteTest.Method> {
		final String condition;
		String result;

		TaskForRemoteTest(String condition) {
			this.condition = condition;
		}

		void setResult(String result) {
			this.result = result;
		}

		@Override
		public String toString() {
			return String.format("TASK(C:%s, R:%s)", this.condition,
					this.result);
		}

		static enum Method {
			REMOTE
		}
	}

	@StructClass
	static class RemoteQueryAnswer {
		final byte[] bs = new byte[102400];
		final Object[] fields;

		RemoteQueryAnswer(Object... args) {
			this.fields = args;
		}

		@Override
		public String toString() {
			if (this.fields == null || this.fields.length == 0) {
				return this.getClass().getSimpleName();
			} else {
				StringBuilder str = new StringBuilder(this.getClass()
						.getSimpleName());
				str.append('(');
				int i = 0;
				for (int len = this.fields.length - 1; i < len; i++) {
					str.append(this.fields[i]);
					str.append(',');
				}
				str.append(this.fields[i]);
				str.append(')');
				return str.toString();
			}
		}
	}

	static final class RemoteTreeNodeQueryAnswer extends RemoteQueryAnswer {
		RemoteTreeNodeQueryAnswer(Object... args) {
			super(args);
		}
	}

	static final class RemoteListQueryAnswer {
		static void convert(List<RemoteQueryAnswer> resultList, Object... args) {
			if (args == null || args.length == 0) {
				return;
			}
			for (int i = 0, len = args.length; i < len; i++) {
				resultList.add(new RemoteQueryAnswer(args[i]));
			}
		}
	}

	// ********************* Test Remote Invocation ****************************
	// /////////////////////////////////////////////////////////////////////////

	// ///////////////// repeat test //////////////////////////////////
	static class RepeatTesterTask extends SimpleTask {
		public int count;
	}

	@Publish()
	final class RepeatTesterTaskHandler extends
			TaskMethodHandler<RepeatTesterTask, None> {
		protected RepeatTesterTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected void handle(ContextImpl<?, ?, ?> context,
				RepeatTesterTask task) throws Throwable {
			if (task.count++ > 5) {
				context.abort();
			}
			System.out.println("repeat: " + task.count + " - " + new Date());
		}
	}
	// ///////////////// repeat test //////////////////////////////////
}
