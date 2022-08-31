package it.poliba.sisinflab.coap.ldp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.eclipse.californium.core.CoapResource;

import java.util.List;

import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPBasicContainer;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPDirectContainer;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPIndirectContainer;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPNonRDFSource;
import it.poliba.sisinflab.coap.ldp.resources.CoAPLDPRDFSource;

public class ResourceListViewAdapter extends ArrayAdapter<CoapResource> {
    Context context;

    public ResourceListViewAdapter(Context context, int resourceId,
                                   List<CoapResource> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView txtTitle;
        TextView txtDesc;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        CoapResource rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.txtDesc = (TextView) convertView.findViewById(R.id.desc);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
            holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.txtDesc.setText(rowItem.getPath());
        holder.txtTitle.setText(rowItem.getName());

        if(rowItem instanceof CoAPLDPBasicContainer)
            holder.imageView.setImageResource(R.mipmap.ldpbc);
        else if(rowItem instanceof CoAPLDPDirectContainer)
            holder.imageView.setImageResource(R.mipmap.ldpdc);
        else if(rowItem instanceof CoAPLDPIndirectContainer)
            holder.imageView.setImageResource(R.mipmap.ldpic);
        else if(rowItem instanceof CoAPLDPNonRDFSource)
            holder.imageView.setImageResource(R.mipmap.ldpnr);
        else if(rowItem instanceof CoAPLDPRDFSource)
            holder.imageView.setImageResource(R.mipmap.ldpr);
        else
            holder.imageView.setImageResource(R.mipmap.coap);

        return convertView;
    }
}