package uci.shield;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import uci.shield.apps.Aplicacion;
import uci.shield.apps.AplicacionesAdapter;


public class Aplicaciones extends ActionBarActivity implements AdapterView.OnItemClickListener{

    LinearLayout layout;
    private ArrayList<String> checkedApps = new ArrayList<>();
    private ArrayList<Aplicacion> aplicaciones;


    private AplicacionesAdapter appsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aplicaciones);

        ListView container = (ListView) findViewById(R.id.apps_placeholder);

        aplicaciones = getApps();

        appsAdapter = new AplicacionesAdapter(getApplicationContext(),aplicaciones);
        container.setAdapter(appsAdapter);
        container.setOnItemClickListener(this);

        EditText search_text = (EditText) findViewById(R.id.search_text);
        search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(String.valueOf(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    private void filter(String filter){
//        for (Aplicacion a : aplicaciones){
//            if (!filter.isEmpty()){
//                if (!a.getLabel().toLowerCase().contains(filter.toLowerCase())){
//                    a.setVisible(false);
//                } else {
//                    a.setVisible(true);
//                }
//            } else a.setVisible(true);
//        }
        appsAdapter.getFilter().filter(filter);
//        appsAdapter.notifyDataSetChanged();
    }

    private ArrayList<Aplicacion> getApps(){
        loadCheckedApps();

//        layout = (LinearLayout) findViewById(R.id.layout);
        PackageManager pm = getPackageManager();
        ArrayList<ApplicationInfo> installedapps = (ArrayList<ApplicationInfo>) pm.getInstalledApplications(0);
        ArrayList<Aplicacion> result = new ArrayList<>();



        for (ApplicationInfo i : installedapps) {
            boolean checked = false;

            if (checkedApps.contains(String.valueOf(i.uid))){
                checked = true;
            }
            result.add(new Aplicacion(i.uid,String.valueOf(i.loadLabel(pm)),i.loadIcon(pm),checked,true));

        }

        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_aplicaciones, menu);
        return true;
    }

    private void loadCheckedApps(){
        checkedApps.clear();
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard, "apps_shield.txt");

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                checkedApps.add(line);
            }
            br.close();
//            this.textOutput.setText(text);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), getString(R.string.error_reading_file),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.save) {
            File sdcard = Environment.getExternalStorageDirectory();
            File file = new File(sdcard, "apps_shield.txt");
            try {
                FileOutputStream fo = new FileOutputStream(file);
                for (Aplicacion c : aplicaciones) {
                    if (c.isChecked()) {
                        fo.write((String.valueOf(c.getUid()) + "\n").getBytes());
                        Log.i("checked", (String.valueOf(c.getUid())));
                    }
                }
                fo.close();


                if (getParent() == null) {
                    setResult(239);
                } else {
                    getParent().setResult(239);
                }
                finish();
            } catch (FileNotFoundException e) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_creating_file),
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        aplicaciones.get(position).setChecked(!aplicaciones.get(position).isChecked());
        String text = aplicaciones.get(position).getLabel()+" set to "+String.valueOf(aplicaciones.get(position).isChecked());
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show();
        appsAdapter.notifyDataSetChanged();
    }
}
