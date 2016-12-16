package com.example.guest999.firebasenotification.adapters;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.guest999.firebasenotification.Config;
import com.example.guest999.firebasenotification.R;
import com.example.guest999.firebasenotification.utilis.CheckConnection;
import com.example.guest999.firebasenotification.utilis.Delete_Data;
import com.example.guest999.firebasenotification.utilis.DownloadCallBack;
import com.example.guest999.firebasenotification.utilis.DownloadTaskIMG;
import com.example.guest999.firebasenotification.utilis.DownloadTaskPDF;
import com.example.guest999.firebasenotification.utilis.MarshmallowPermissions;
import com.example.guest999.firebasenotification.utilis.SharedPreferenceManager;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

import static com.example.guest999.firebasenotification.Config.PhoneFromDevice;

/**
 * Created by Harshad and Modified by Joshi Tushar and Hiren
 */

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> implements DownloadCallBack {
	private static final int REQUEST_WRITE_STORAGE = 112;
	private static final int RESULT_LOAD_FILE = 0;
	private static final int RESULT_LOAD_IMAGE = 1;
	private static final int REQUEST_CODE_PICK_CONTACTS = 99;
	private static final int REQUEST_TEXT = 7;
	private static DownloadTaskIMG downloadTask;
	private static DownloadTaskPDF downloadPdf;
	private static ArrayList<HashMap<String, String>> file_paths = new ArrayList<>();
	private SwipyRefreshLayout swipeRefreshLayout;
	private Boolean chkVisibility = false;
	private ImagePick image;
	private File extStore;
	private String ImageFileName, localPaTH, FileName;
	private DownloadCallBack downloadCallBack;
	private File myFile, LocalFile, myFile_;
	private LayoutInflater inflater = null;
	private Context context;
	private MarshmallowPermissions marshmallowPermissions;
	private FilePick file;
	private Delete_Data delete_data;

	public DataAdapter(Context mcontext, ArrayList<HashMap<String, String>> file_paths, SwipyRefreshLayout swipeRefreshLayout) {
		this.context = mcontext;
		inflater = LayoutInflater.from(context);
		DataAdapter.file_paths = file_paths;
		downloadCallBack = this;
		this.swipeRefreshLayout = swipeRefreshLayout;
		marshmallowPermissions = new MarshmallowPermissions((Activity) context);
	}

	@Override
	public void onAttachedToRecyclerView(RecyclerView recyclerView) {
		super.onAttachedToRecyclerView(recyclerView);
	}

	@Override
	public int getItemViewType(int position) {

		// Log.e("getItemViewType:pos ", String.valueOf(position));
		Log.e("IS this PDF ?", String.valueOf(file_paths.get(position).get(Config.TAG_DATA).endsWith(".pdf")));
		if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".pdf") || file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".docx") || file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".doc") || file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".txt") || file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".ppt") || file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".pptx") || file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".zip") || file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".rar") || file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".xls") || file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".xlsx")) {
			return RESULT_LOAD_FILE;
		} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".png") || file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".jpg") || file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".jpeg")) {
			return RESULT_LOAD_IMAGE;
		} else if (file_paths.get(position).get(Config.TAG_DATA).contains("::")) {
			return REQUEST_CODE_PICK_CONTACTS;
		} else {
			return REQUEST_TEXT;
		}
	}


	private void NoApplicationAvailable(final int position) {
		android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
		builder.setTitle("Warning");
		builder.setMessage("No Application available to view this file");
		builder.setIcon(R.drawable.error);
		builder.setPositiveButton("Download",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
						if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".rar")) {
							try {
								context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.rarlab.rar&hl=en" + appPackageName)));
							} catch (android.content.ActivityNotFoundException anfe) {
								context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.rarlab.rar&hl=en" + appPackageName)));
							}
						} else {
							try {
								context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=cn.wps.moffice_eng&hl=en" + appPackageName)));
							} catch (android.content.ActivityNotFoundException anfe) {
								context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=cn.wps.moffice_eng&hl=en" + appPackageName)));
							}
						}
					}
				});
		builder.setNegativeButton("Not Now",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		android.support.v7.app.AlertDialog dialog = builder.create();
		// display dialog
		try {
			dialog.show();
		} catch (Exception ed) {
			ed.printStackTrace();
		}
	}


	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v;

		Log.e("layout code: ", viewType + "");

		if (viewType == RESULT_LOAD_FILE) {
			v = inflater
					.inflate(R.layout.raw_file, parent, false);
			return new FilePick(v);
		} else if (viewType == RESULT_LOAD_IMAGE) {
			v = inflater
					.inflate(R.layout.raw_image, parent, false);
			return new ImagePick(v);
		} else if (viewType == REQUEST_CODE_PICK_CONTACTS) {
			v = inflater
					.inflate(R.layout.raw_contact, parent, false);
			return new ContactPick(v);
		} else {
			v = inflater
					.inflate(R.layout.raw_text, parent, false);
			return new TextSend(v);
		}

	}

	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		holder.setIsRecyclable(false);
//for Internet
		CheckConnection cd;
		IntentFilter filter;


		String ad_date = file_paths.get(position).get(Config.CURRENT_DATE);
		String ad_time = file_paths.get(position).get(Config.CURRENT_TIME);
		String file_size = file_paths.get(position).get(Config.KEY_FILE_SIZE);
		PhoneFromDevice = SharedPreferenceManager.getDefaults("phone", context);

		Log.e("onBindViewHolder:pos ", String.valueOf(position));

		if (holder instanceof FilePick) {
			file = (FilePick) holder;
			final String pdfname = file_paths.get(position).get(Config.TAG_DATA).replace("http://www.laxmisecurity.com/android/uploads/", "");

			FileName = file.temp.getText().toString();
			extStore = Environment.getExternalStorageDirectory();
			myFile_ = new File(extStore.getAbsolutePath() + "/P L Shah/P L Shah File/" + FileName);


			if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".pdf")) {
				file.file_type_image.setBackgroundResource(R.drawable.pdf);
			} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".docx") || file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".doc")) {
				file.file_type_image.setBackgroundResource(R.drawable.word);
			} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".txt")) {
				file.file_type_image.setBackgroundResource(R.drawable.txt);
			} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".ppt")) {
				file.file_type_image.setBackgroundResource(R.drawable.ppt);
			} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".pptx")) {
				file.file_type_image.setBackgroundResource(R.drawable.ppt);
			} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".zip")) {
				file.file_type_image.setBackgroundResource(R.drawable.zip);
			} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".xls") || file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".xlsx")) {
				file.file_type_image.setBackgroundResource(R.drawable.xls);
			} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".rar")) {
				file.file_type_image.setBackgroundResource(R.drawable.rar);
			}

			file.temp.setText(pdfname.substring(10));
			file.textview_time.setText(ad_time);
			file.textview_date.setText(ad_date);
			file.txt_file_size.setText(file_size);

			if (!file_paths.get(position).get(Config.KEY_PHONE).equals(PhoneFromDevice)) {
				//for receive file
				file.status.setText(R.string.received);
				file.txt_file_size.setTextColor(Color.GRAY);
				try {
					file.outgoing_layout_bubble.setGravity(Gravity.START | Gravity.BOTTOM);
					file.linearLayout.setGravity(Gravity.START | Gravity.BOTTOM);
					boolean tabletSize = context.getResources().getBoolean(R.bool.tablet);
					if (tabletSize) {
						// do something
						file.outgoing_layout_bubble.setBackgroundResource(R.drawable.balloon_incomin_normal);
					} else {
						// do something else
						file.outgoing_layout_bubble.setBackgroundResource(R.drawable.balloon_incoming_normal);
					}

					file.outgoing_layout_bubble.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							//for downloading pdf 07-10-2016
							localPaTH = file_paths.get(position).get(Config.KEY_LOCAL_PATH);
							LocalFile = new File(localPaTH);
							FileName = file.temp.getText().toString();
							extStore = Environment.getExternalStorageDirectory();
							myFile_ = new File(extStore.getAbsolutePath() + "/P L Shah/P L Shah File/" + pdfname.substring(10));

//						File myFile = new File(extStore.getAbsolutePath() + "/P L Shah/P L Shah File/" + file.temp.getText().toString());
							if (myFile_.exists()) {
								//	Toast.makeText(context, "File already Exists in " + myFile, Toast.LENGTH_SHORT).show();
								try {
									// File pdfFile = new File(Environment.getExternalStorageDirectory() + "/FileSharing/" + pdfname );  // -> filename
									//file.outgoing_layout_bubble.removeView(file.myButton);
									Uri path = Uri.fromFile(myFile_);
									Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
									if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".pdf")) {
										pdfIntent.setDataAndType(path, "application/pdf");
									} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".docx")) {
										pdfIntent.setDataAndType(path, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
									} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".doc")) {
										pdfIntent.setDataAndType(path, "application/msword");
									} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".txt")) {
										pdfIntent.setDataAndType(path, "text/plain");
									} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".ppt")) {
										pdfIntent.setDataAndType(path, "application/vnd.ms-powerpoint");
									} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".pptx")) {
										pdfIntent.setDataAndType(path, "application/vnd.openxmlformats-officedocument.presentationml.presentation");
									} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".zip")) {
										pdfIntent.setDataAndType(path, "application/zip");
									} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".xls")) {
										pdfIntent.setDataAndType(path, "application/vnd.ms-excel");
									} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".xlsx")) {
										pdfIntent.setDataAndType(path, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
									} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".rar")) {
										pdfIntent.setDataAndType(path, "application/x-rar-compressed");
									} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".rar") || file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".zip")) {
										pdfIntent.setDataAndType(path, "application/octet-stream");
									}

									// file.myButton.setVisibility(INVISIBLE);
									context.startActivity(pdfIntent);
								} catch (ActivityNotFoundException e) {

									NoApplicationAvailable(position);
								}

							} else {

								extStore = Environment.getExternalStorageDirectory();
								//myFile = new File(extStore.getAbsolutePath() + "/P L Shah/P L Shah File/" + ImageFileName);
								myFile_ = new File(extStore.getAbsolutePath() + "/P L Shah/P L Shah File/" + pdfname.substring(10));

								if (!myFile_.exists()) {
									if (marshmallowPermissions.checkIfAlreadyhavePermission()) {
										// execute this when the downloader must be fired
										downloadPdf = new DownloadTaskPDF(context, Config.INTERNAL_IMAGE_PATH_URI, "" + pdfname.substring(10), downloadCallBack, swipeRefreshLayout);
										// downloadTask.execute("http://ia.tranetech.ae:82/upload/uploads/five-point-someone-chetan-bhagat_ebook.pdf", "" + finalHolder.tv_paper_name.getText().toString() + ".pdf");
										downloadPdf.execute(file_paths.get(position).get(Config.TAG_DATA), "" + pdfname.substring(10));
									} else {
										marshmallowPermissions.requestpermissions();
									}
								} else {
									try {
										// File pdfFile = new File(Environment.getExternalStorageDirectory() + "/FileSharing/" + pdfname );  // -> filename
										//file.outgoing_layout_bubble.removeView(file.myButton);
										Uri path = Uri.fromFile(myFile_);
										Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
										if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".pdf")) {
											pdfIntent.setDataAndType(path, "application/pdf");
										} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".docx")) {
											pdfIntent.setDataAndType(path, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
										} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".doc")) {
											pdfIntent.setDataAndType(path, "application/msword");
										} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".txt")) {
											pdfIntent.setDataAndType(path, "text/plain");
										} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".ppt")) {
											pdfIntent.setDataAndType(path, "application/vnd.ms-powerpoint");
										} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".pptx")) {
											pdfIntent.setDataAndType(path, "application/vnd.openxmlformats-officedocument.presentationml.presentation");
										} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".zip")) {
											pdfIntent.setDataAndType(path, "application/zip");
										} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".xls")) {
											pdfIntent.setDataAndType(path, "application/vnd.ms-excel");
										} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".xlsx")) {
											pdfIntent.setDataAndType(path, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
										} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".rar")) {
											pdfIntent.setDataAndType(path, "application/x-rar-compressed");
										} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".rar") || file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".zip")) {
											pdfIntent.setDataAndType(path, "application/octet-stream");
										}
										// file.myButton.setVisibility(INVISIBLE);
										context.startActivity(pdfIntent);
									} catch (ActivityNotFoundException e) {
										Toast.makeText(context, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
										NoApplicationAvailable(position);
									}
								}
							}
						}
					});

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				//for sent files
				file.status.setText(R.string.sent);
				try {
					file.outgoing_layout_bubble.setGravity(Gravity.END | Gravity.BOTTOM);
					file.linearLayout.setGravity(Gravity.END | Gravity.BOTTOM);
					boolean tabletSize = context.getResources().getBoolean(R.bool.tablet);
					if (tabletSize) {
						// do something
						file.outgoing_layout_bubble.setBackgroundResource(R.drawable.balloon_outgoin_normal);
					} else {
						// do something else
						file.outgoing_layout_bubble.setBackgroundResource(R.drawable.balloon_outgoing_normal);
					}
					file.temp.setTextColor(Color.WHITE);
					file.txt_file_size.setTextColor(Color.WHITE);
					file.textview_time.setTextColor(Color.WHITE);
					file.textview_date.setTextColor(Color.WHITE);
					file.status.setTextColor(Color.WHITE);

				} catch (Exception e) {
					e.printStackTrace();
				}

				//for image download on click.
				file.outgoing_layout_bubble.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						localPaTH = file_paths.get(position).get(Config.KEY_LOCAL_PATH);
						LocalFile = new File(localPaTH);

						FileName = file.temp.getText().toString();
						extStore = Environment.getExternalStorageDirectory();
						//myFile = new File(extStore.getAbsolutePath() + "/P L Shah/P L Shah File/" + ImageFileName);
						myFile_ = new File(extStore.getAbsolutePath() + "/P L Shah/P L Shah File/" + pdfname.substring(10));


//						File myFile = new File(extStore.getAbsolutePath() + "/P L Shah/P L Shah File/" + file.temp.getText().toString());
						if (LocalFile.exists()) {
							//	Toast.makeText(context, "File already Exists in " + myFile, Toast.LENGTH_SHORT).show();
							try {
								// File pdfFile = new File(Environment.getExternalStorageDirectory() + "/FileSharing/" + pdfname );  // -> filename
								//file.outgoing_layout_bubble.removeView(file.myButton);
								Uri path = Uri.fromFile(LocalFile);
								Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
								if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".pdf")) {
									pdfIntent.setDataAndType(path, "application/pdf");
								} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".docx")) {
									pdfIntent.setDataAndType(path, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
								} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".doc")) {
									pdfIntent.setDataAndType(path, "application/msword");
								} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".txt")) {
									pdfIntent.setDataAndType(path, "text/plain");
								} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".ppt")) {
									pdfIntent.setDataAndType(path, "application/vnd.ms-powerpoint");
								} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".pptx")) {
									pdfIntent.setDataAndType(path, "application/vnd.openxmlformats-officedocument.presentationml.presentation");
								} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".zip")) {
									pdfIntent.setDataAndType(path, "application/zip");
								} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".xls")) {
									pdfIntent.setDataAndType(path, "application/vnd.ms-excel");
								} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".xlsx")) {
									pdfIntent.setDataAndType(path, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
								} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".rar")) {
									pdfIntent.setDataAndType(path, "application/x-rar-compressed");
								} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".rar") || file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".zip")) {
									pdfIntent.setDataAndType(path, "application/octet-stream");
								}

								// file.myButton.setVisibility(INVISIBLE);

								context.startActivity(pdfIntent);
							} catch (ActivityNotFoundException e) {
								Toast.makeText(context, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
								NoApplicationAvailable(position);
							}
						} else {


							if (!myFile_.exists()) {
								if (marshmallowPermissions.checkIfAlreadyhavePermission()) {
									// execute this when the downloader must be fired
									downloadPdf = new DownloadTaskPDF(context, Config.INTERNAL_IMAGE_PATH_URI, "" + pdfname.substring(10), downloadCallBack, swipeRefreshLayout);
									// downloadTask.execute("http://ia.tranetech.ae:82/upload/uploads/five-point-someone-chetan-bhagat_ebook.pdf", "" + finalHolder.tv_paper_name.getText().toString() + ".pdf");
									downloadPdf.execute(file_paths.get(position).get(Config.TAG_DATA), "" + pdfname.substring(10));
								} else {
									marshmallowPermissions.requestpermissions();
								}
							} else {
								try {
									// File pdfFile = new File(Environment.getExternalStorageDirectory() + "/FileSharing/" + pdfname );  // -> filename
									//file.outgoing_layout_bubble.removeView(file.myButton);
									Uri path = Uri.fromFile(myFile_);
									Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
									if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".pdf")) {
										pdfIntent.setDataAndType(path, "application/pdf");
									} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".docx")) {
										pdfIntent.setDataAndType(path, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
									} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".doc")) {
										pdfIntent.setDataAndType(path, "application/msword");
									} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".txt")) {
										pdfIntent.setDataAndType(path, "text/plain");
									} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".ppt")) {
										pdfIntent.setDataAndType(path, "application/vnd.ms-powerpoint");
									} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".pptx")) {
										pdfIntent.setDataAndType(path, "application/vnd.openxmlformats-officedocument.presentationml.presentation");
									} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".zip")) {
										pdfIntent.setDataAndType(path, "application/zip");
									} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".xls")) {
										pdfIntent.setDataAndType(path, "application/vnd.ms-excel");
									} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".xlsx")) {
										pdfIntent.setDataAndType(path, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
									} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".rar")) {
										pdfIntent.setDataAndType(path, "application/x-rar-compressed");
									} else if (file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".rar") || file_paths.get(position).get(Config.TAG_DATA).toLowerCase().endsWith(".zip")) {
										pdfIntent.setDataAndType(path, "application/octet-stream");
									}
									// file.myButton.setVisibility(INVISIBLE);
									context.startActivity(pdfIntent);
								} catch (ActivityNotFoundException e) {
									NoApplicationAvailable(position);
								}
							}
						}
					}
				});
			}
			file.outgoing_layout_bubble.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					file.dialog = new Dialog(context);
					file.dialog.setContentView(R.layout.dialog_del);
					file.dialog.setTitle("Select Action");

					file.dialog.findViewById(R.id.lv_del).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Delet_data(position, file_paths.size());
							file.dialog.dismiss();

						}
					});

					// show dialog on screen
					//contactPick.dialog.getWindow().getAttributes().windowAnimations = animationSource;
					file.dialog.show();
					return false;


				}
			});
		} else if (holder instanceof ImagePick) {
			// below code changed by Hiren please ask if you want to modified.
			try {
				image = (DataAdapter.ImagePick) holder;
				ImageFileName = file_paths.get(position).get(Config.TAG_DATA).replace(Config.INTERNAL_IMAGE_PATH_URI, "");
				extStore = Environment.getExternalStorageDirectory();
				myFile = new File(extStore.getAbsolutePath() + "/P L Shah/P L Shah Images/" + ImageFileName.substring(10));
				localPaTH = file_paths.get(position).get(Config.KEY_LOCAL_PATH);
				LocalFile = new File(localPaTH);

				image.txt_img_size.setText(file_size);
				image.textview_time.setText(ad_time);
				image.textview_date.setText(ad_date);
				PhoneFromDevice = SharedPreferenceManager.getDefaults("phone", context);

				if (!file_paths.get(position).get(Config.KEY_PHONE).equals(PhoneFromDevice)) {
					image.status.setText(R.string.received);
					image.outgoing_layout_bubble.setGravity(Gravity.LEFT | Gravity.BOTTOM);
					image.linearLayout.setGravity(Gravity.LEFT | Gravity.BOTTOM);
					boolean tabletSize = context.getResources().getBoolean(R.bool.tablet);
					if (tabletSize) {
						// do something
						image.outgoing_layout_bubble.setBackgroundResource(R.drawable.balloon_incomin_normal);
					} else {
						// do something else
						image.outgoing_layout_bubble.setBackgroundResource(R.drawable.balloon_incoming_normal);
					}


					// coding for recived items
					// below code created by Hiren please ask if you want to delete it.

					if (!myFile.exists()) {
						chkVisibility = true;
						Change_VisiBility(chkVisibility);
						try {
							Glide.with(context.getApplicationContext())
									.load(file_paths.get(position).get(Config.TAG_DATA))
									.placeholder(R.drawable.placeholder)
									.override(18, 18)
									.error(R.drawable.placeholder)
									.crossFade()
									.centerCrop()
									.into(image.score);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						chkVisibility = false;
						Change_VisiBility(chkVisibility);
						try {
							Glide.with(context.getApplicationContext())
									.load(myFile)
									.placeholder(R.drawable.placeholder)
									.error(R.drawable.placeholder)
									.crossFade()
									.centerCrop()
									.into(image.score);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				} else {
					// coding for sent items

					image.status.setText(R.string.sent);
					image.outgoing_layout_bubble.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
					image.linearLayout.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
					boolean tabletSize = context.getResources().getBoolean(R.bool.tablet);
					if (tabletSize) {
						// do something
						image.outgoing_layout_bubble.setBackgroundResource(R.drawable.balloon_outgoin_normal);
					} else {
						// do something else
						image.outgoing_layout_bubble.setBackgroundResource(R.drawable.balloon_outgoing_normal);
					}

					image.status.setTextColor(Color.WHITE);
					image.textview_time.setTextColor(Color.WHITE);
					image.textview_date.setTextColor(Color.WHITE);

					//below code created by Hiren please ask if you want to delete it.
					localPaTH = file_paths.get(position).get(Config.KEY_LOCAL_PATH);
					LocalFile = new File(localPaTH);


					if (LocalFile.exists()) {
						chkVisibility = false;
						Change_VisiBility(chkVisibility);
						try {
							Glide.with(context.getApplicationContext())
									.load(LocalFile)
									.placeholder(R.drawable.placeholder)
									.error(R.drawable.placeholder)
									.crossFade()
									.centerCrop()
									.into(image.score);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						//below code created by Hiren please ask if you want to delete it.
						ImageFileName = file_paths.get(position).get(Config.TAG_DATA).replace(Config.INTERNAL_IMAGE_PATH_URI, "");
						extStore = Environment.getExternalStorageDirectory();
						myFile = new File(extStore.getAbsolutePath() + "/P L Shah/P L Shah Images/" + ImageFileName.substring(10));

						if (!myFile.exists()) {
							//	image.spinwheel.setVisibility(View.VISIBLE);
							chkVisibility = true;
							Change_VisiBility(chkVisibility);
							try {
								Glide.with(context.getApplicationContext())
										.load(file_paths.get(position).get(Config.TAG_DATA))
										.placeholder(R.drawable.placeholder)
										.override(18, 18)
										.error(R.drawable.placeholder)
										.crossFade()
										.centerCrop()
										.into(image.score);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							//	image.spinwheel.setVisibility(View.INVISIBLE);
							chkVisibility = false;
							Change_VisiBility(chkVisibility);
							try {
								Glide.with(context.getApplicationContext())
										.load(myFile)
										.placeholder(R.drawable.placeholder)
										.error(R.drawable.placeholder)
										.crossFade()
										.centerCrop()
										.into(image.score);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					}


				}
				//for image download on click.
				image.outgoing_layout_bubble.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						//below code created by Hiren please ask if you want to delete it.
						localPaTH = file_paths.get(position).get(Config.KEY_LOCAL_PATH);
						LocalFile = new File(localPaTH);
						//below code created by Hiren please ask if you want to delete it.
						ImageFileName = file_paths.get(position).get(Config.TAG_DATA).replace(Config.INTERNAL_IMAGE_PATH_URI, "");
						extStore = Environment.getExternalStorageDirectory();
						myFile = new File(extStore.getAbsolutePath() + "/P L Shah/P L Shah Images/" + ImageFileName.substring(10));
						if (myFile.exists()) {
							chkVisibility = false;
							Change_VisiBility(chkVisibility);
							Uri path = Uri.fromFile(myFile);
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setDataAndType(path, "image/*");
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
							intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
							try {
								context.startActivity(intent);
							} catch (ActivityNotFoundException e) {
								Toast.makeText(context, "No Application in your device available to view", Toast.LENGTH_SHORT).show();
							}
						} else {

							if (!myFile.exists()) {
								if (marshmallowPermissions.checkIfAlreadyhavePermission()) {
									// execute this when the downloader must be fired
									downloadTask = new DownloadTaskIMG(context, Config.INTERNAL_IMAGE_PATH_URI, "" + ImageFileName, downloadCallBack, swipeRefreshLayout);
									downloadTask.execute();
								} else {
									marshmallowPermissions.requestpermissions();
								}
							} else {
								chkVisibility = false;
								Change_VisiBility(chkVisibility);
								Uri path = Uri.fromFile(myFile);
								Intent intent = new Intent(Intent.ACTION_VIEW);
								intent.setDataAndType(path, "image/*");
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
								intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
								try {
									context.startActivity(intent);
								} catch (ActivityNotFoundException e) {
									Toast.makeText(context, "No Application in your device available to view", Toast.LENGTH_SHORT).show();
								}
							}
						}

					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}


			image.outgoing_layout_bubble.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					image.dialog = new Dialog(context);
					image.dialog.setContentView(R.layout.dialog_del);
					image.dialog.setTitle("Select Action");

					image.dialog.findViewById(R.id.lv_del).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Delet_data(position, file_paths.size());
							image.dialog.dismiss();

						}
					});

					// show dialog on screen
					//contactPick.dialog.getWindow().getAttributes().windowAnimations = animationSource;
					image.dialog.show();
					return false;


				}
			});

		} else if (holder instanceof ContactPick) {
			try {
				final String Contact_name = file_paths.get(position).get(Config.TAG_DATA);
				final ContactPick contactPick = (ContactPick) holder;


				String[] splited = Contact_name.split("::");
				final String phone_no = splited[0];
				final String phone_name = splited[1];
				try {
					Log.e("onBindViewHolder: ", phone_no);
					Log.e("onBindViewHolder: ", phone_name);
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					contactPick.no.setText(phone_no);
					contactPick.name.setText(phone_name);
					contactPick.textview_time.setText(ad_time);
					contactPick.textview_date.setText(ad_date);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (!file_paths.get(position).get(Config.KEY_PHONE).equals(PhoneFromDevice)) {
					contactPick.status.setText(R.string.received);
					contactPick.outgoing_layout_bubble.setGravity(Gravity.START | Gravity.BOTTOM);
					contactPick.linearLayout.setGravity(Gravity.START | Gravity.BOTTOM);
					contactPick.outgoing_layout_bubble.setBackgroundResource(R.drawable.balloon_incoming_normal);

					contactPick.outgoing_layout_bubble.setOnLongClickListener(new View.OnLongClickListener() {
						@Override
						public boolean onLongClick(View v) {
							contactPick.dialog = new Dialog(context);
							contactPick.dialog.setContentView(R.layout.dialog_contact);
							contactPick.dialog.setTitle("Select Action");

							contactPick.dialog.findViewById(R.id.lv_call).setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									Intent callIntent = new Intent(Intent.ACTION_CALL);
									callIntent.setData(Uri.parse("tel:" + phone_no));
									Log.e("onClick: ", phone_no);
									if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
										// TODO: Consider calling
										//    ActivityCompat#requestPermissions
										// here to request the missing permissions, and then overriding
										//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
										//                                          int[] grantResults)
										// to handle the case where the user grants the permission. See the documentation
										// for ActivityCompat#requestPermissions for more details.
										return;
									}
									context.startActivity(callIntent);
									contactPick.dialog.dismiss();

								}
							});
							contactPick.dialog.findViewById(R.id.lv_del).setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									Delet_data(position, file_paths.size());
									contactPick.dialog.dismiss();

								}
							});

							contactPick.dialog.findViewById(R.id.lv_add).setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									Intent intent = new Intent(Intent.ACTION_INSERT,
											ContactsContract.Contacts.CONTENT_URI);
									intent.putExtra(ContactsContract.Intents.Insert.NAME, phone_name);
									intent.putExtra(ContactsContract.Intents.Insert.PHONE, phone_no);
									context.startActivity(intent);
									contactPick.dialog.dismiss();

								}
							});

							// show dialog on screen
							//contactPick.dialog.getWindow().getAttributes().windowAnimations = animationSource;
							contactPick.dialog.show();
							return false;
						}
					});


				} else {
					contactPick.status.setText(R.string.sent);
					contactPick.outgoing_layout_bubble.setGravity(Gravity.END | Gravity.BOTTOM);
					contactPick.linearLayout.setGravity(Gravity.END | Gravity.BOTTOM);
					contactPick.outgoing_layout_bubble.setBackgroundResource(R.drawable.balloon_outgoing_normal);
					contactPick.status.setTextColor(Color.WHITE);
					contactPick.no.setTextColor(Color.WHITE);
					contactPick.name.setTextColor(Color.WHITE);
					contactPick.textview_time.setTextColor(Color.WHITE);
					contactPick.textview_date.setTextColor(Color.WHITE);

					contactPick.outgoing_layout_bubble.setOnLongClickListener(new View.OnLongClickListener() {
						@Override
						public boolean onLongClick(View v) {
							contactPick.dialog = new Dialog(context);
							contactPick.dialog.setContentView(R.layout.dialog_contact);
							contactPick.dialog.setTitle("Select Action");

							contactPick.dialog.findViewById(R.id.lv_call).setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									Intent callIntent = new Intent(Intent.ACTION_CALL);
									callIntent.setData(Uri.parse("tel:" + phone_no));
									Log.e("onClick: ", phone_no);
									if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
										// TODO: Consider calling
										//    ActivityCompat#requestPermissions
										// here to request the missing permissions, and then overriding
										//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
										//                                          int[] grantResults)
										// to handle the case where the user grants the permission. See the documentation
										// for ActivityCompat#requestPermissions for more details.
										return;
									}
									context.startActivity(callIntent);
									contactPick.dialog.dismiss();

								}
							});
							contactPick.dialog.findViewById(R.id.lv_del).setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									Delet_data(position, file_paths.size());
									contactPick.dialog.dismiss();

								}
							});

							contactPick.dialog.findViewById(R.id.lv_add).setVisibility(View.GONE);
							contactPick.dialog.findViewById(R.id.contact_divider).setVisibility(View.GONE);

							// show dialog on screen
							//contactPick.dialog.getWindow().getAttributes().windowAnimations = animationSource;
							contactPick.dialog.show();
							return false;
						}
					});

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (holder instanceof TextSend) {
			try {
				final String Contact_name = file_paths.get(position).get(Config.TAG_DATA);
				final TextSend textSend = (TextSend) holder;

				try {
					textSend.msg.setText(Contact_name);
					textSend.textview_time.setText(ad_time);
					textSend.textview_date.setText(ad_date);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (!file_paths.get(position).get(Config.KEY_PHONE).equals(PhoneFromDevice)) {
					textSend.status.setText(R.string.received);
					textSend.outgoing_layout_bubble.setGravity(Gravity.START | Gravity.BOTTOM);
					textSend.linearLayout.setGravity(Gravity.START | Gravity.BOTTOM);
					boolean tabletSize = context.getResources().getBoolean(R.bool.tablet);
					if (tabletSize) {
						// do something
						textSend.outgoing_layout_bubble.setBackgroundResource(R.drawable.balloon_incomin_normal);
					} else {
						// do something else
						textSend.outgoing_layout_bubble.setBackgroundResource(R.drawable.balloon_incoming_normal);
					}


				} else {
					textSend.status.setText(R.string.sent);
					textSend.outgoing_layout_bubble.setGravity(Gravity.END | Gravity.BOTTOM);
					textSend.linearLayout.setGravity(Gravity.END | Gravity.BOTTOM);
					boolean tabletSize = context.getResources().getBoolean(R.bool.tablet);
					if (tabletSize) {
						// do something
						textSend.outgoing_layout_bubble.setBackgroundResource(R.drawable.balloon_outgoin_normal);
					} else {
						// do something else
						textSend.outgoing_layout_bubble.setBackgroundResource(R.drawable.balloon_outgoing_normal);
					}
					textSend.status.setTextColor(Color.WHITE);
					textSend.msg.setTextColor(Color.WHITE);
					textSend.textview_time.setTextColor(Color.WHITE);
					textSend.textview_date.setTextColor(Color.WHITE);

				}
				textSend.outgoing_layout_bubble.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						textSend.dialog = new Dialog(context);
						textSend.dialog.setContentView(R.layout.dialog_del);
						textSend.dialog.setTitle("Select Action");

						textSend.dialog.findViewById(R.id.lv_del).setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								Delet_data(position, file_paths.size());
								textSend.dialog.dismiss();

							}
						});

						// show dialog on screen
						//contactPick.dialog.getWindow().getAttributes().windowAnimations = animationSource;
						textSend.dialog.show();
						return false;

					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void Delet_data(int position, int size) {

		if (CheckConnection.ni != null) {
			String adid = file_paths.get(position).get(Config.ad_id);
			delete_data = new Delete_Data(context, adid, swipeRefreshLayout);
			file_paths.remove(position);
			notifyItemRemoved(position);
			notifyItemRangeChanged(position, size);
			notifyDataSetChanged();
		} else {
			Toast.makeText(context, "No Internet Available", Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public int getItemCount() {
		return file_paths.size();
	}

	@Override
	public void onDownloadComplete() {
		notifyDataSetChanged();
	}

	private void Change_VisiBility(Boolean chkVisibility) {
		if (chkVisibility) {
			image.txt_img_size.setVisibility(View.VISIBLE);
			image.rl_img_size.setVisibility(View.VISIBLE);
			image.rpb_img_size.setVisibility(View.VISIBLE);
			image.ib_img_size.setVisibility(View.VISIBLE);
		} else {
			image.txt_img_size.setVisibility(View.INVISIBLE);
			image.rl_img_size.setVisibility(View.INVISIBLE);
			image.rpb_img_size.setVisibility(View.INVISIBLE);
			image.ib_img_size.setVisibility(View.INVISIBLE);
		}
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		ViewHolder(View itemView) {
			super(itemView);
		}

	}

	private class FilePick extends ViewHolder {
		TextView temp, txt_file_size;
		TextView status, textview_time, textview_date;
		LinearLayout outgoing_layout_bubble;
		LinearLayout linearLayout;
		ImageView file_type_image;
		private Dialog dialog;

		FilePick(View v) {
			super(v);
			this.temp = (TextView) v.findViewById(R.id.file_name);
			this.status = (TextView) v.findViewById(R.id.send_receive);
			this.txt_file_size = (TextView) v.findViewById(R.id.txt_file_size);
			this.textview_time = (TextView) v.findViewById(R.id.textview_time);
			this.textview_date = (TextView) v.findViewById(R.id.textview_date);
			this.outgoing_layout_bubble = (LinearLayout) v.findViewById(R.id.outgoing_layout_bubble);
			this.linearLayout = (LinearLayout) v.findViewById(R.id.mainlinearlayout);
			this.file_type_image = (ImageView) v.findViewById(R.id.file_image);
		}
	}

	private class ImagePick extends ViewHolder {
		private RingProgressBar rpb_img_size;
		private ImageButton ib_img_size;
		//	private ProgressBar spinwheel;
		private RelativeLayout rl_img_size;
		private ImageView score;
		private Dialog dialog;
		private TextView status, textview_time, textview_date, txt_img_size;
		private LinearLayout outgoing_layout_bubble;
		private LinearLayout linearLayout;

		ImagePick(View v) {
			super(v);
			this.txt_img_size = (TextView) v.findViewById(R.id.txt_img_size);
			this.ib_img_size = (ImageButton) v.findViewById(R.id.ib_img_size);
			this.rpb_img_size = (RingProgressBar) v.findViewById(R.id.rpb_img_size);
			this.rl_img_size = (RelativeLayout) v.findViewById(R.id.rl_img_size);
			this.score = (ImageView) v.findViewById(R.id.image_list);
			this.status = (TextView) v.findViewById(R.id.send_receive);
			this.textview_time = (TextView) v.findViewById(R.id.textview_time);
			this.textview_date = (TextView) v.findViewById(R.id.textview_date);
			this.outgoing_layout_bubble = (LinearLayout) v.findViewById(R.id.outgoing_layout_bubble);
			this.linearLayout = (LinearLayout) v.findViewById(R.id.mainlinearlayout);

		}
	}

	private class ContactPick extends ViewHolder {
		TextView status, textview_time, textview_date, no, name;
		LinearLayout outgoing_layout_bubble;
		LinearLayout linearLayout;
		private Dialog dialog;

		ContactPick(View v) {
			super(v);
			this.no = (TextView) v.findViewById(R.id.contact_no);
			this.name = (TextView) v.findViewById(R.id.contact_name);
			this.status = (TextView) v.findViewById(R.id.send_receive);
			this.textview_time = (TextView) v.findViewById(R.id.textview_time);
			this.textview_date = (TextView) v.findViewById(R.id.textview_date);
			this.outgoing_layout_bubble = (LinearLayout) v.findViewById(R.id.outgoing_layout_bubble);
			this.linearLayout = (LinearLayout) v.findViewById(R.id.mainlinearlayout);
		}
	}

	private class TextSend extends ViewHolder {
		TextView status, textview_time, textview_date, msg;
		LinearLayout outgoing_layout_bubble;
		LinearLayout linearLayout;
		private Dialog dialog;

		TextSend(View v) {
			super(v);
			this.msg = (TextView) v.findViewById(R.id.client_text);
			this.status = (TextView) v.findViewById(R.id.send_receive);
			this.textview_time = (TextView) v.findViewById(R.id.textview_time);
			this.textview_date = (TextView) v.findViewById(R.id.textview_date);
			this.outgoing_layout_bubble = (LinearLayout) v.findViewById(R.id.outgoing_layout_bubble);
			this.linearLayout = (LinearLayout) v.findViewById(R.id.mainlinearlayout);
		}

	}

}
