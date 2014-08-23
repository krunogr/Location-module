package com.example.location_module;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

@SuppressLint("NewApi")
public class Dialog extends DialogFragment implements OnClickListener {
	Button ok;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog, null);
		ok = (Button) v.findViewById(R.id.buttonDialog);
		ok.setOnClickListener(this);
		getDialog().setTitle("Warning");
		return v;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.buttonDialog) {
			dismiss();
		}

	}

}
