package bard.util;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProgressWriter {

	private static final Logger log = LoggerFactory.getLogger(ProgressWriter.class);

	private AtomicInteger counter = new AtomicInteger();
	private int minOrder = 100;
	private int maxOrder = 10000;
	private long sum = 0;
	private long start = System.currentTimeMillis();
	private String message = "Progress";

	public ProgressWriter(String message) {
		this(message, 100, 10000);
	}
	
	public ProgressWriter(String message, int minOrder, int maxOrder) {
		this.message = message;
		this.minOrder = minOrder;
		this.maxOrder = maxOrder;
	}

	public void increment() {
		long end = System.currentTimeMillis();
		sum += end - start;
		start = end;
		int count = counter.incrementAndGet();
		if (count % minOrder == 0) {
			notifyProgress(count);
			handleOrder(count);
		}
	}
	
	protected void handleOrder(int count) {
		int len = String.valueOf(count).length();
		int minLen = String.valueOf(minOrder).length();
		int maxLen = String.valueOf(maxOrder).length();
		if ( len > minLen && len <= maxLen)
			minOrder *= 10;
	}

	public void notifyProgress(int progress) {
		log.debug(String.format("[%s] Count: %s, Total time: %s, Average time: %.3f(s)", message, counter, hms(sum / 1000), getAverageSeconds()));
	}

	protected double getAverageSeconds() {
		return ((double) sum / 1000D) / (double) counter.get();
	}

	public int getCount() {
		return counter.get();
	}

	static String hms(Number secs) {
		long hours = secs.intValue() / 3600;
		long remainder = secs.intValue() % 3600;
		long minutes = remainder / 60;
		long seconds = remainder % 60;
		return ((hours < 10 ? "0" : "") + hours + ":" + (minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds);
	}
}