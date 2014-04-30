package com.jest.onyx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class MetricContent {

	/**
	 * An array of metrics.
	 */
	public static List<MetricItem> ITEMS = new ArrayList<MetricItem>();

	/**
	 * A map of sample (dummy) items, by ID.
	 */
	public static Map<String, MetricItem> ITEM_MAP = new HashMap<String, MetricItem>();

	static {
		// Add 3 sample items.
		addItem(new MetricItem("1", "Release Speed"));
		addItem(new MetricItem("2", "Release Angle"));
		addItem(new MetricItem("3", "Elbow Angle"));
	}

	private static void addItem(MetricItem item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}

	/**
	 * A dummy item representing a piece of content.
	 */
	public static class MetricItem {
		public String id;
		public String content;

		public MetricItem(String id, String content) {
			this.id = id;
			this.content = content;
		}

		@Override
		public String toString() {
			return content;
		}
	}
}
