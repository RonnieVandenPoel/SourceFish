package com.sourcefish.projectmanagement;

import java.util.List;

import com.fedorvlasov.lazylist.ImageLoader;
import com.sourcefish.tools.Entry;
import com.sourcefish.tools.User;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class UserAdapter extends ArrayAdapter<User> {

	private Context context;
	private List<User> users;
	private ImageLoader loader;
	
	public UserAdapter(Context context,int textViewResourceId,
			List<User> objects) {
		super(context, textViewResourceId, objects);
		this.context=context;
		this.users=objects;
		loader=new ImageLoader(context);
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
	    if (v == null) {
	    	LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.userlistview, null);
	    }
	    
	    User u=users.get(position);
	    
	    if(u!=null)
	    {
	    	ImageView iv=(ImageView) v.findViewById(R.id.ul_userProfilePicture);
	    	loader.DisplayImage(u.username, iv);
	    	
	    	TextView tv=(TextView) v.findViewById(R.id.ul_userData);
	    	tv.setText(u.username + " access level: " + u.rechten);
	    }
	    
	    return v;
    }


}
