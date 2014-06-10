package com.jest.onyx;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SkillChartAdapter extends com.fima.chartview.LabelAdapter {
	public enum LabelOrientation {
		HORIZONTAL, VERTICAL
	}

	public static final int FLOAT = 0, INT = 1, DATE = 2;
	
	private Context mContext;
	private LabelOrientation mOrientation;
	private int mType;
	DateFormat formatter;

	public SkillChartAdapter(Context context, LabelOrientation orientation, int type) {
		mContext = context;
		mOrientation = orientation;
		mType = type;
		formatter = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView labelTextView;
		if (convertView == null) {
			convertView = new TextView(mContext);
		}

		labelTextView = (TextView) convertView;

		int gravity = Gravity.CENTER;
		if (mOrientation == LabelOrientation.VERTICAL) {
			if (position == 0) {
				gravity = Gravity.BOTTOM | Gravity.RIGHT;
			} else if (position == getCount() - 1) {
				gravity = Gravity.TOP | Gravity.RIGHT;
			} else {
				gravity = Gravity.CENTER | Gravity.RIGHT;
			}
		} else if (mOrientation == LabelOrientation.HORIZONTAL) {
			if (position == 0) {
				gravity = Gravity.CENTER | Gravity.LEFT;
			} else if (position == getCount() - 1) {
				gravity = Gravity.CENTER | Gravity.RIGHT;
			}
		}

		labelTextView.setGravity(gravity);
		labelTextView.setPadding(8, 0, 8, 0);
		
		String text;
		switch (mType) {
			case 2:
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis((long) Math.round(getItem(position)));
				text = formatter.format(cal.getTime());
				break;
			case 1:
				text = String.format(Locale.US,"%.0f", getItem(position)); break;
			case 0: default:
				text = String.format(Locale.US,"%.1f", getItem(position)); break;
		}
		
		labelTextView.setText(text);

		return convertView;
	}

}
