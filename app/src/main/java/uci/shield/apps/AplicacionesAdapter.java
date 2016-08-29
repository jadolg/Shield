package uci.shield.apps;

import android.app.Application;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uci.shield.R;

/**
 * Created by akiel on 12/27/15.
 */
public class AplicacionesAdapter extends ArrayAdapter<Aplicacion> {

    private final Context context;
    private ArrayList<Aplicacion> values;
    private ArrayList<Aplicacion> original;
    private Filter filter;

    public AplicacionesAdapter(Context context, ArrayList<Aplicacion> values) {
        super(context, R.layout.apps_list_layout, values);
        this.context = context;
        this.values = new ArrayList<>(values);
        this.original = new ArrayList<>(values);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.apps_list_layout, parent, false);
        RelativeLayout header = (RelativeLayout) rowView.findViewById(R.id.header);
//
//        if (values[position].isVisible()){
//            rowView.setVisibility(View.VISIBLE);
//        } else {
//            rowView.setVisibility(View.GONE);
//        }

        TextView titulo = (TextView) rowView.findViewById(R.id.uid);
        TextView texto = (TextView) rowView.findViewById(R.id.label);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);
        titulo.setText(Html.fromHtml(String.valueOf(values.get(position).getUid())));
        texto.setText(Html.fromHtml(values.get(position).getLabel()));
        RelativeLayout checked = (RelativeLayout) rowView.findViewById(R.id.checked_flag);

        if (values.get(position).isChecked()){
            checked.setVisibility(View.VISIBLE);
        } else {
            checked.setVisibility(View.INVISIBLE);
        }

//        imageView.setImageBitmap(BitmapFactory.decodeByteArray(values[position].getLogo(), 0, values[position].getLogo().length));
        imageView.setImageDrawable(values.get(position).getIcon());

        return rowView;
    }

    @Override
    public Filter getFilter()
    {
        if (filter == null)
            filter = new PkmnNameFilter();

        return filter;
    }

    private class PkmnNameFilter extends Filter
    {
        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
            FilterResults results = new FilterResults();
            String prefix = constraint.toString().toLowerCase();

            if (prefix == null || prefix.length() == 0)
            {
                ArrayList<Aplicacion> list = new ArrayList<Aplicacion>(original);
                results.values = list;
                results.count = list.size();
            }
            else
            {
                final ArrayList<Aplicacion> list = new ArrayList<Aplicacion>(original);
                final ArrayList<Aplicacion> nlist = new ArrayList<Aplicacion>();
                int count = list.size();

                for (int i=0; i<count; i++)
                {
                    final Aplicacion pkmn = list.get(i);
                    final String value = pkmn.getLabel().toLowerCase();

                    if (value.contains(prefix.toLowerCase()))
                    {
                        nlist.add(pkmn);
                    }
                }
                results.values = nlist;
                results.count = nlist.size();
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            values = (ArrayList<Aplicacion>)results.values;

            clear();
            int count = values.size();
            for (int i=0; i<count; i++)
            {
                Aplicacion pkmn = (Aplicacion)values.get(i);
                add(pkmn);
            }
        }

    }
    public ArrayList<Aplicacion> getOrig(){
        return original;
    }


}
