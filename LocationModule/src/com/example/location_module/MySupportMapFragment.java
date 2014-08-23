package com.example.location_module;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.SupportMapFragment;

public class MySupportMapFragment extends SupportMapFragment {
	public View mOriginalContentView;
	public TouchableWrapper mTouchView;
	Communicator communicator;
	boolean checkMove = true;
	int d = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		mOriginalContentView = super.onCreateView(inflater, parent,
				savedInstanceState);
		mTouchView = new TouchableWrapper(getActivity());
		mTouchView.addView(mOriginalContentView);
		communicator = (Communicator) getActivity();

		return mTouchView;
	}

	@Override
	public View getView() {
		return mOriginalContentView;
	}

	private class TouchableWrapper extends FrameLayout {

		public TouchableWrapper(Context context) {
			super(context);
		}

		@Override
		public boolean dispatchTouchEvent(MotionEvent event) {
			d++;
			Log.d("myLog", String.valueOf(d));
			switch (event.getAction()) {

			case MotionEvent.ACTION_UP:

				if (checkMove) {
					Log.d("myLog", "ACTION_UP");
					int x = (int) event.getX();
					int y = (int) event.getY();
					Point clickedPoint = new Point(x, y);
					communicator.respond(clickedPoint);
				}
				break;
			case MotionEvent.ACTION_CANCEL:
				Log.d("myLog", "ACTION_CANCEL");

				/*
				 * case MotionEvent.ACTION_DOWN: Log.d("myLog", "ACTION_DOWN");
				 */
				break;
			case MotionEvent.ACTION_MOVE:

				Log.d("myLog", "ACTION_MOVE");
				checkMove = false;
				break;
			case MotionEvent.ACTION_HOVER_ENTER:
				Log.d("myLog", "ACTION_HOVER_ENTER");
				break;
			case MotionEvent.ACTION_HOVER_EXIT:
				Log.d("myLog", "ACTION_HOVER_EXIT");
				break;
			case MotionEvent.ACTION_HOVER_MOVE:
				Log.d("myLog", "ACTION_HOVER_MOVE");
				break;
			case MotionEvent.ACTION_MASK:
				Log.d("myLog", "ACTION_MASK");
				break;
			case MotionEvent.ACTION_OUTSIDE:
				Log.d("myLog", "ACTION_OUTSIDE");
				break;
			case MotionEvent.ACTION_SCROLL:
				Log.d("myLog", "ACTION_SCROLL");

				break;
			case MotionEvent.ACTION_DOWN:
				Log.d("myLog", "ACTION_DOWN");
				checkMove = true;

				break;
			}
			Log.d("myLog", String.valueOf(checkMove));
			return super.dispatchTouchEvent(event);
		}
	}

}