package com.example.guest999.firebasenotification.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.guest999.firebasenotification.Config;
import com.example.guest999.firebasenotification.R;
import com.example.guest999.firebasenotification.utilis.CheckConnection;
import com.example.guest999.firebasenotification.utilis.SearchGetSet;
import com.example.guest999.firebasenotification.utilis.SqlHandler;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Harshad on 26-10-2016.
 */

public class Search extends AppCompatActivity {
	public EditText et_search;
	public ArrayList<SearchGetSet> User_info_search = new ArrayList<>();
	SearchAdapter myAdapter;
	String TAG = getClass().getName();
	//for Internet
	CheckConnection cd;
	IntentFilter filter;
	private RecyclerView rv;
	SqlHandler sqlHandler;
	// converting arraylist to string array intializing size
	private String[] full_name = new String[UserList.Array_user_list.size()];
	private String[] full_no = new String[UserList.Array_user_list.size()];
	private String[] pro_pic = new String[UserList.Array_user_list.size()];
	private String[] counter = new String[UserList.Array_user_list.size()];
	// array list
	private String Login_User, image_external_Url, file_extenal_Url;
	String usname_local = null, count_local = null, phon_local = null;
	String tv_getname, tv_getphone, tv_getcount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search);
		Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		//for Internet
		cd = new CheckConnection();
		filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

		Intent i = getIntent();
		image_external_Url = i.getStringExtra("IMG_URL_SEARCH");
		file_extenal_Url = i.getStringExtra("FILE_URL_SEARCH");
		Log.e(TAG, "onCreate Search: " + image_external_Url);
		Log.e(TAG, "onCreate Search: " + file_extenal_Url);

		LoadUielements();
		LoadUILisners();

		sqlHandler = new SqlHandler(Search.this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//for Internet
		registerReceiver(cd, filter);
	}

	@Override
	protected void onPause() {
		unregisterReceiver(cd);
		super.onPause();
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void LoadUielements() {
		rv = (RecyclerView) findViewById(R.id.recyclerview);
		et_search = (EditText) findViewById(R.id.et_search);

		Log.e("Search", String.valueOf(User_info_search));
		for (int i = 0; i < UserList.Array_user_list.size(); i++) {
			try {
				full_name[i] = UserList.Array_user_list.get(i).get(Config.KEY_USERNAME);
				full_no[i] = UserList.Array_user_list.get(i).get(Config.KEY_PHONE);
				pro_pic[i] = UserList.Array_user_list.get(i).get(Config.KEY_PROFILE_PATH);
				counter[i] = UserList.Array_user_list.get(i).get("count");

				Log.e("Userlist", full_name[i]);
				Log.e("Userlist", full_no[i]);
				Log.e("Userlist", pro_pic[i] + "");
				Log.e("Userlist", counter[i] + "");

				SearchGetSet searchGetSet = new SearchGetSet(full_name[i], full_no[i], pro_pic[i], counter[i]);
				User_info_search.add(searchGetSet);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		rv.setHasFixedSize(true);

		LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
		rv.setLayoutManager(mLayoutManager);

		Log.e("LoadUielements: ", User_info_search + "");
		myAdapter = new SearchAdapter(Search.this, User_info_search);
		rv.setAdapter(myAdapter);
	}

	private void LoadUILisners() {

		et_search.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				String text = et_search.getText().toString().toLowerCase(Locale.getDefault());
				Log.e("afterTextChanged: ", text);
				myAdapter.filter(text);
			}
		});

	}

	class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

		Context context;
		private String User_Click_Phone;
		private List<SearchGetSet> searchlist = null;
		private ArrayList<SearchGetSet> mDataset;

		SearchAdapter(Search search, ArrayList<SearchGetSet> user_info_search) {
			this.context = search;
			this.searchlist = user_info_search;
			this.mDataset = new ArrayList<>();
			this.mDataset.addAll(searchlist);
			Log.e("MyAdapter: mDataset ", mDataset + "");
			Log.e("MyAdapter: world ", searchlist + "");
		}

		@Override
		public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			// create a new view
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_search, parent, false);
			// set the view's size, margins, paddings and layout parameters
			return new SearchAdapter.ViewHolder(v);
		}

		@Override
		public void onBindViewHolder(final SearchAdapter.ViewHolder holder, final int position) {
			Log.e("onBindViewHolder: ", searchlist.get(position).getSearchName());
			Log.e("onBindViewHolder: ", searchlist.get(position).getSearchNo());
			Log.e("onBindViewHolder: ", searchlist.get(position).getThumbnail());
			Log.e("onBindViewHolder: ", searchlist.get(position).getCounter());

			final String phone = searchlist.get(position).getSearchNo();
			final String countFromUrl = searchlist.get(position).getCounter();


			int tvtotal_final = 0;

			final String count_show = String.valueOf(Integer.parseInt(countFromUrl) / 2);

			holder.tv_name.setText(searchlist.get(position).getSearchName());
			holder.tv_phone.setText(searchlist.get(position).getSearchNo());
			//holder.tv_counter.setText(searchlist.get(position).getCounter());


			holder.setIsRecyclable(false);

			String query = "SELECT * FROM USER_LIST WHERE PHON='" + phone + "'";
			Cursor c = sqlHandler.selectQuery(query);
			c.moveToFirst();
			int a = c.getCount();
			Log.e("before onclick user is Harshad:-", a + "");
			HashMap<String, String> map_ = new HashMap<>();
			if (a > 0) {

				do {
					usname_local = c.getString(c.getColumnIndex("NAME"));
					phon_local = c.getString(c.getColumnIndex("PHON"));
					count_local = c.getString(c.getColumnIndex("COUNT"));

					map_.put("NAME", usname_local);
					map_.put("PHON", phon_local);
					map_.put("COUNT", count_local);

					tvtotal_final = Integer.parseInt(count_show) - Integer.parseInt(count_local);
					Log.e(TAG, "just testing substraction: " + tvtotal_final);

				} while (c.moveToNext());
				c.close();

			}

			if (map_.isEmpty()) {
				Log.e(TAG, "outSide onClick Harshad: " + map_);

				if (count_show.equals("0")) {
					holder.tv_counter.setVisibility(View.GONE);
				}
				if (!count_show.equals(count_local)) {
					holder.tv_counter.setText(count_show);
				} else if (count_show.equals(count_local)) {
					holder.tv_counter.setVisibility(View.INVISIBLE);
				}
			} else {
				if (count_show.equals("0")) {
					holder.tv_counter.setVisibility(View.GONE);
				}
				if (tvtotal_final < 0) {
					if (!count_show.equals(count_local)) {
						holder.tv_counter.setText(count_show + "");
					} else if (count_show.equals(count_local)) {
						holder.tv_counter.setVisibility(View.INVISIBLE);
					}
					if (String.valueOf(tvtotal_final).equals("0")) {
						holder.tv_counter.setVisibility(View.GONE);
					}
				} else {
					if (!count_show.equals(count_local)) {
						holder.tv_counter.setText(tvtotal_final + "");
					} else if (count_show.equals(count_local)) {
						holder.tv_counter.setVisibility(View.INVISIBLE);
					}
					if (String.valueOf(tvtotal_final).equals("0")) {
						holder.tv_counter.setVisibility(View.GONE);
					}
				}
			}

			String path = searchlist.get(position).getThumbnail();

			if (!path.isEmpty()) {
				Picasso.with(context)
						.load(path)
						.fit()
						.placeholder(R.drawable.profile)
						.error(R.drawable.placeholder)
						.into(holder.profile_search);

			} else {
				Picasso.with(context)
						.load(R.drawable.profile)
						.placeholder(R.drawable.profile)
						.error(R.drawable.placeholder)
						.into(holder.profile_search);

			}

			holder.main_rel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					User_Click_Phone = searchlist.get(position).getSearchNo();
					Intent i = new Intent(context, Data_Sharing.class);
					Bundle extras = new Bundle();
					if (image_external_Url != null) {
						extras.putString("IMG_URL", image_external_Url);
						Log.e("onClick: ", image_external_Url);
					} else if (file_extenal_Url != null) {
						extras.putString("FILE_URL", file_extenal_Url);
						Log.e("onClick: ", file_extenal_Url);
					}
					image_external_Url = null;
					file_extenal_Url = null;
					extras.putString("Click_Phone", User_Click_Phone);
					extras.putString(Config.KEY_USERNAME, searchlist.get(position).getSearchName());
					i.putExtras(extras);
					Log.e("onClick: ", User_Click_Phone + searchlist.get(position).getSearchName());
					finish();
					context.startActivity(i);


					tv_getname = holder.tv_name.getText().toString();
					tv_getphone = holder.tv_phone.getText().toString();

					if (holder.tv_counter.getVisibility() == View.VISIBLE) {
						tv_getcount = holder.tv_counter.getText().toString();
					}

					for (int ii = 0; ii < mDataset.size(); ii++) {
						if (ii == position) {

							String query = "SELECT * FROM USER_LIST WHERE PHON='"
									+ User_Click_Phone + "'";
							Cursor c1 = sqlHandler.selectQuery(query);
							c1.moveToFirst();

							int t = c1.getCount();
							Log.e("Count is Array_user_listo world:-", t + "");

							c1.close();
							if (t == 0) {

								String q = "INSERT INTO USER_LIST(NAME,PHON,COUNT) values ('"
										+ tv_getname
										+ "','"
										+ tv_getphone
										+ "','"
										+ count_show
										+ "')";
								sqlHandler.executeQuery(q);
								Log.e(TAG, "onClick: " + "data insert");
							} else {
								String u = "UPDATE USER_LIST SET COUNT='"
										+ count_show + "' WHERE PHON='" + tv_getphone
										+ "'";
								sqlHandler.executeQuery(u);
								Log.e(TAG, "onClick: " + "data update");
							}
						}

					}

					Log.e(TAG, "onClick count from url is: " + countFromUrl);


				}
			});
		}

		void filter(String charText) {
			charText = charText.toLowerCase(Locale.getDefault());
			searchlist.clear();
			Log.e("filter:1 ", searchlist + "");
			if (charText.length() == 0) {
				searchlist.addAll(mDataset);
				Log.e("filter:2 ", searchlist + "");
				Log.e("filter:3 ", mDataset + "");
			} else {
				for (SearchGetSet wp : mDataset) {
					if (wp.getSearchName().toLowerCase(Locale.getDefault()).contains(charText) || wp.getSearchNo().toLowerCase(Locale.getDefault()).contains(charText)) {
						Log.e("filter:4 ", wp + "");
						searchlist.add(wp);
						Log.e("filter:5 ", searchlist + "");
					}
				}
			}
			notifyDataSetChanged();
		}

		@Override
		public int getItemCount() {
			return searchlist.size();
		}

		// Provide a reference to the views for each data item
		// Complex data items may need more than one view per item, and
		// you provide access to all the views for a data item in a view holder
		class ViewHolder extends RecyclerView.ViewHolder {
			LinearLayout main_rel;
			CircleImageView profile_search;
			// each data item is just a string in this case
			private TextView tv_name, tv_phone, tv_counter;

			@SuppressLint("WrongViewCast")
			ViewHolder(View itemView) {
				super(itemView);
				tv_name = (TextView) itemView.findViewById(R.id.m_name);
				tv_phone = (TextView) itemView.findViewById(R.id.phone_number);
				main_rel = (LinearLayout) itemView.findViewById(R.id.main_rel);
				tv_counter = (TextView) itemView.findViewById(R.id.count_read_unread);
				profile_search = (CircleImageView) itemView.findViewById(R.id.profile_image_search);
			}
		}
	}
}



