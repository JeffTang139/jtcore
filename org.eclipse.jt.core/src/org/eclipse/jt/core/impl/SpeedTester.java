package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.List;

public final class SpeedTester {
	public interface TestWork {
		public abstract String description();

		public abstract void comparison() throws Throwable;

		public abstract void work() throws Throwable;
	}

	public static abstract class TestWorkBase implements TestWork {
		final String description;

		TestWorkBase(String description) {
			this.description = description;
		}

		public void comparison() throws Throwable {
		}

		public String description() {
			return this.description;
		}

		public abstract void work() throws Throwable;

	}

	private final List<TestWork> works = new ArrayList<TestWork>();

	final void regWorkToBeTest(TestWork work) {
		if (!this.works.contains(work)) {
			this.works.add(work);
		}
	}

	private static class Nanos {
		final TestWork work;
		private long workNano;
		private long comparisonNano;
		private long times;

		final void repeat() throws Throwable {
			TestWork work = this.work;
			long times = this.times / 10;
			long start = System.nanoTime();
			for (long i = 0; i < times; i++) {
				work.comparison();
				work.comparison();
				work.comparison();
				work.comparison();
				work.comparison();
				work.comparison();
				work.comparison();
				work.comparison();
				work.comparison();
				work.comparison();
			}
			this.comparisonNano = System.nanoTime() - start;
			start = System.nanoTime();
			for (long i = 0; i < times; i++) {
				work.work();
				work.work();
				work.work();
				work.work();
				work.work();
				work.work();
				work.work();
				work.work();
				work.work();
				work.work();
			}
			this.workNano = System.nanoTime() - start;
		}

		void testTimes(long minTimes, long maxNano) throws Throwable {
			double b = 1.0;
			for (this.times = minTimes; b >= 1.0; this.times = (long) (this.times * Math
			        .min(Math.max(b, 1.1), 100.0))) {
				this.repeat();
				b = maxNano / (double) (this.comparisonNano + this.workNano);
			}
		}

		final double testTimesPerNano(long maxNano) throws Throwable {
			this.testTimes(1, maxNano / 10);
			this.testTimes(this.times * 10, 0);
			return (double) (this.workNano - this.comparisonNano)
			        / (double) this.times;
		}

		@Override
		public final String toString() {
			return "<" + this.work.description() + "> : \r\n" + "\t测试耗时:["
			        + (double) this.workNano / this.times + "](Nano)\r\n"
			        + "\t对比耗时:[" + (double) this.comparisonNano / this.times
			        + "](Nano)\r\n" + "\t耗时差:["
			        + (double) (this.workNano - this.comparisonNano)
			        / this.times + "](Nano)\r\n\t耗时比:["
			        + (double) (this.workNano - this.comparisonNano)
			        / this.workNano * 100 + "]%";
		}

		Nanos(TestWork work) {
			this.work = work;
		}
	}

	public final void test(int msPerWork) throws Throwable {
		for (TestWork work : this.works) {
			Nanos nanos = new Nanos(work);
			nanos.testTimesPerNano(1000000L * msPerWork);
			System.out.println(nanos);
		}
	}

	public static void test(int msPerWork, TestWork... works) throws Throwable {
		for (TestWork work : works) {
			Nanos nanos = new Nanos(work);
			nanos.testTimesPerNano(1000L * 1000L * msPerWork);
			System.out.println(nanos);
		}
	}

	public static int bitCount(int n) {
		n -= ((n >> 1) & 033333333333) - ((n >> 2) & 011111111111);
		return ((n + (n >> 3)) & 030707070707) % 63;
	}

	public static void main(String[] args) {
		try {
			final String a = "asddddddddddddfasf1";
			final String b = "AsDdddddddddddfAsF2";
			final String c = "asddddddddddddfasf3";
			test(1000, new TestWorkBase("regx") {
				@Override
				public void comparison() throws Throwable {
					a.compareTo(c);
				}

				@Override
				public void work() throws Throwable {
					a.compareToIgnoreCase(b);
				}
			});
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
