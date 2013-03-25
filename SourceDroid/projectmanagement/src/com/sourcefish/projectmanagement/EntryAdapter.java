package com.sourcefish.projectmanagement;

import java.util.List;

import com.fedorvlasov.lazylist.ImageLoader;
import com.sourcefish.tools.Entry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EntryAdapter extends ArrayAdapter<Entry> {

	private List<Entry> entries;
	private Context context;
	private ImageLoader loader;
	
	public EntryAdapter(Context context, int textViewResourceId,
			List<Entry> entries) {
		super(context, textViewResourceId, entries);
		this.entries=entries;
		this.context=context;
		loader=new ImageLoader(context);
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
	    if (v == null) {
	    	LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.entrieslistview, null);
	    }
	    
	    Entry entry=entries.get(position);
	    
	    if(entry!=null && !entry.isOpen())
	    {
	    	ImageView iv=(ImageView) v.findViewById(R.id.entryProfilePicture);
	    	loader.DisplayImage(entry.u.username, iv);
	    	
	    	TextView tv=(TextView) v.findViewById(R.id.entryTitle);
	    	tv.setText(entry.toString());
	    }
	    
	    return v;
    }

}
