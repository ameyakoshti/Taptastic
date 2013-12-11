package com.csci580.taptastic;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.tjerkw.slideexpandable.library.ActionSlideExpandableListView;

public class AnnouncmentsFragments extends Fragment implements AsyncResponse {
	View rootView;

	public AnnouncmentsFragments() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.single_expandable_list, container, false);

		ServletCalls Announcments = new ServletCalls();
		Announcments.delegate = AnnouncmentsFragments.this;
		Announcments.execute("3", "9790886604", "csci588");

		return rootView;
	}

	public class AnnoucementstItemAdapter extends ArrayAdapter {
		private ArrayList tweets;
		private ArrayList types;
		private int inflateId;

		public AnnoucementstItemAdapter(Context context, int textViewResourceId, int text, ArrayList tweets, ArrayList types) {
			super(context, textViewResourceId, text, tweets);
			this.tweets = tweets;
			this.types = types;
			this.inflateId = textViewResourceId;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(inflateId, null);
			}

			String tweet = tweets.get(position).toString();
			if (tweet != null) {

				ImageView image = (ImageView) v.findViewById(R.id.imageView1);
				TextView tview2 = (TextView) v.findViewById(R.id.text);
				tview2.setText(tweet);
				TextView tview = (TextView) v.findViewById(R.id.textView1);
				TextView tview1 = (TextView) v.findViewById(R.id.textView2);
				if (image != null) {
					if (types.get(position).toString().equals("588"))
						image.setImageResource(R.drawable.ic_588);
					else if (types.get(position).toString().equals("580"))
						image.setImageResource(R.drawable.ic_580);

				}
				if (tweet.equals("sample")) {
					tview.setText("\nfirst sample");
					tview1.setText("Time Posted: 12/10/2013 6:00pm");
				}
				if (tweet.equals("one more sample")) {
					tview.setText("second sample");
					tview1.setText("Time Posted: 12/10/2013 6:00am");
				}
				if (tweet.equals("what is")) {
					tview.setText("Holidays has been extended until further notice");
					tview1.setText("Time Posted: 12/8/2013 6:00pm");
				}
			}
			return v;
		}
	}

	public ListAdapter buildDummyData(String servletResponse) {

		ArrayList<String> values = new ArrayList<String>();
		ArrayList<String> type = new ArrayList<String>();
		JSONObject jObject;
		JSONArray posts;

		try {
			jObject = new JSONObject(servletResponse);
			try {
				posts = jObject.getJSONArray("posts");
				for (int i = 0; i < posts.length(); i++) {
					if(i%2==0)
					type.add("588");
					else type.add("580");
					values.add(posts.getJSONObject(i).get("post").toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return new AnnoucementstItemAdapter(getActivity(), R.layout.expandable_list_annoucements, R.id.text, values, type);
	}

	@Override
	public void processFinish(String output) {
		ActionSlideExpandableListView list = (ActionSlideExpandableListView) rootView.findViewById(R.id.list);
		list.setAdapter(buildDummyData(output));
	}
}
