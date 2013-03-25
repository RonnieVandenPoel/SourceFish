package com.sourcefish.projectmanagement;

import java.util.List;

import com.fedorvlasov.lazylist.ImageLoader;
import com.sourcefish.tools.User;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AddUserAdapter extends ArrayAdapter<User> {

	private List<User> users;
	private Context context;
	private ImageLoader loader;
	
	public AddUserAdapter(Context context, int textViewResourceId,
			List<User> objects) {
		super(context, textViewResourceId, objects);
		this.context=context;
		this.users=users;
		loader=new ImageLoader(context);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView textView = (TextView) super.getView(position,convertView,parent);
		User u=users.get(position);
		
		
		return textView;
	}
}
