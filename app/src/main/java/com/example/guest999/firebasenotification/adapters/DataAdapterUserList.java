package com.example.guest999.firebasenotification.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.guest999.firebasenotification.Config;
import com.example.guest999.firebasenotification.R;
import com.example.guest999.firebasenotification.activities.Data_Sharing;
import com.example.guest999.firebasenotification.utilis.SharedPreferenceManager;
import com.example.guest999.firebasenotification.utilis.SqlHandler;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by HIREN AMALIYAR on 15-11-2016.
 */

public class DataAdapterUserList extends RecyclerView.Adapter<DataAdapterUserList.ViewHolder> {
	SqlHandler sqlHandler;
	String tv_getname, tv_getphone, tv_getcount;
	String usname_local = null, count_local = null, phon_local = null;
	String Login_User, User_Click_Phone, admin_type;
	private ArrayList<HashMap<String, String>> mDataset;
	private Context context;
	private int lastposition = -1;
	Uri imageUri, filePath;

	public DataAdapterUserList(Context context, ArrayList<HashMap<String, String>> userList, SqlHandler sqlHandler, Uri imageUri, Uri filePath) {
		this.context = context;
		this.mDataset = userList;
		this.sqlHandler = sqlHandler;
		this.imageUri = imageUri;
		this.filePath = filePath;
	}

	@Override
	public void onAttachedToRecyclerView(RecyclerView recyclerView) {
		super.onAttachedToRecyclerView(recyclerView);
	}

	@Override
	public void onViewDetachedFromWindow(ViewHolder holder) {
		super.onViewDetachedFromWindow(holder);
		holder.itemView.clearAnimation();
	}

	@Override
	public DataAdapterUserList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_userlist, parent, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(final DataAdapterUserList.ViewHolder holder, final int position) {
		holder.setIsRecyclable(false);
		final String username = mDataset.get(position).get(Config.KEY_USERNAME);
		final String phone = mDataset.get(position).get(Config.KEY_PHONE);
		final String path = mDataset.get(position).get(Config.KEY_PROFILE_PATH);
		holder.name.setText(username);
		holder.phon.setText(phone);

		final String total_admin = mDataset.get(position).get("total");
		final String countFromUrl = mDataset.get(position).get("count");

		int tvtotal_final = 0;

		Log.e("onBindViewHolder:possition", position + "");
		Log.e("onBindViewHoldercount: ", countFromUrl + "");

		final String count_show = String.valueOf(Integer.parseInt(countFromUrl) / Integer.parseInt(total_admin));
		holder.setIsRecyclable(false);

		holder.name.setText(username);
		holder.phon.setText(phone);

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
				Log.e("just testing substraction: ", tvtotal_final + "");

			} while (c.moveToNext());
			c.close();

		}

		if (map_.isEmpty()) {
			Log.e("outSide onClick Harshad: ", map_ + "");

			if (count_show.equals("0")) {
				holder.count_read_unread.setVisibility(View.GONE);
			}
			if (!count_show.equals(count_local)) {
				holder.count_read_unread.setText(count_show);
			} else if (count_show.equals(count_local)) {
				holder.count_read_unread.setVisibility(View.INVISIBLE);
			}
		} else {
			if (count_show.equals("0")) {
				holder.count_read_unread.setVisibility(View.GONE);
			}
			if (tvtotal_final < 0) {
				if (!count_show.equals(count_local)) {
					holder.count_read_unread.setText(count_show + "");
				} else if (count_show.equals(count_local)) {
					holder.count_read_unread.setVisibility(View.INVISIBLE);
				}
				if (String.valueOf(tvtotal_final).equals("0")) {
					holder.count_read_unread.setVisibility(View.GONE);
				}
			} else {
				if (!count_show.equals(count_local)) {
					holder.count_read_unread.setText(tvtotal_final + "");
				} else if (count_show.equals(count_local)) {
					holder.count_read_unread.setVisibility(View.INVISIBLE);
				}
				if (String.valueOf(tvtotal_final).equals("0")) {
					holder.count_read_unread.setVisibility(View.GONE);
				}
			}
		}

		if (!path.isEmpty()) {
			Picasso.with(context)
					.load(path)
					.fit()
					.placeholder(R.drawable.profile)
					.error(R.drawable.placeholder)
					.into(holder.imageView);

		} else {
			Picasso.with(context)
					.load(R.drawable.profile)
					.placeholder(R.drawable.profile)
					.error(R.drawable.placeholder)
					.into(holder.imageView);

		}




	/*	Animation animation = AnimationUtils.loadAnimation(context,
				(position > lastposition) ? R.anim.up_from_bottom
						: R.anim.down_from_top);
		holder.itemView.startAnimation(animation);
		lastposition = position;*/


		holder.main_lin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Login_User = SharedPreferenceManager.getDefaults("phone", context);
				admin_type = SharedPreferenceManager.getDefaults("type", context);

				User_Click_Phone = mDataset.get(position).get(Config.KEY_PHONE);
				Intent i = new Intent(context, Data_Sharing.class);
				Bundle extras = new Bundle();
				extras.putString("Click_Phone", User_Click_Phone);
				extras.putInt("Click_Position", position);
				extras.putString(Config.KEY_USERNAME, mDataset.get(position).get(Config.KEY_USERNAME));
				i.putExtras(extras);
				if (admin_type.contains("admin")) {
					if (imageUri != null) {
						i.putExtra("IMG_URL", imageUri + "");
						Log.e("onClick admin: ", imageUri + "");
					} else if (filePath != null) {
						i.putExtra("FILE_URL", filePath + "");
						Log.e("onClick admin: ", filePath + "");
					}
				}
				//i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				context.startActivity(i);

				imageUri = null;
				filePath = null;

				// i have two thing ,first one is user click phone and 2nd is count
				//when i click phone insert it and its count value in to local db.
				//so here is all for this

				//holder.count_read_unread.setVisibility(View.GONE);
				tv_getname = holder.name.getText().toString();
				tv_getphone = holder.phon.getText().toString();

				if (holder.count_read_unread.getVisibility() == View.VISIBLE) {
					tv_getcount = holder.count_read_unread.getText().toString();
				}

				for (int ii = 0; ii < mDataset.size(); ii++) {
					if (ii == position) {

						String query = "SELECT * FROM USER_LIST WHERE PHON='"
								+ User_Click_Phone + "'";
						Cursor c1 = sqlHandler.selectQuery(query);
						c1.moveToFirst();

						int t = c1.getCount();
						Log.e("Count is helloo world:-", t + "");

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
							Log.e("onClick: ", "data insert");
						} else {
							String u = "UPDATE USER_LIST SET COUNT='"
									+ count_show + "' WHERE PHON='" + tv_getphone
									+ "'";
							sqlHandler.executeQuery(u);
							Log.e("onClick: ", "data update");
						}
					}

				}

				Log.e("onClick count from url is: ", countFromUrl);


			}
		});


	}

	@Override
	public int getItemCount() {
		return mDataset.size();
	}


	// Provide a reference to the views for each data item
	// Complex data items may need more than one view per item, and
	// you provide access to all the views for a data item in a view holder
	class ViewHolder extends RecyclerView.ViewHolder {
		private TextView name, phon;
		TextView count_read_unread;
		private LinearLayout main_lin;
		private ImageView imageView;

		ViewHolder(View itemView) {
			super(itemView);
			count_read_unread = (TextView) itemView.findViewById(R.id.count_read_unread);
			name = (TextView) itemView.findViewById(R.id.m_name);
			phon = (TextView) itemView.findViewById(R.id.phone_number);
			main_lin = (LinearLayout) itemView.findViewById(R.id.main_lin);
			imageView = (ImageView) itemView.findViewById(R.id.profile_image_list);
		}
	}

}
