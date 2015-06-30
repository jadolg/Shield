package uci.shield;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class Aplicaciones extends ActionBarActivity {

    LinearLayout layout;
    private ArrayList<CheckBox> checkBoxes = new ArrayList<>();
    private ArrayList<String> checkedApps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aplicaciones);
        loadCheckedApps();

        layout = (LinearLayout) findViewById(R.id.layout);
        PackageManager pm = getPackageManager();
        for (ApplicationInfo i : pm.getInstalledApplications(0)) {
            CheckBox ch = new CheckBox(this);
            checkBoxes.add(ch);
            ch.setText(String.valueOf(i.uid) + ":" + i.loadLabel(pm));
            if (checkedApps.contains(ch.getText())){
                ch.setChecked(true);
            }
            ImageView image = new ImageView(this);
            image.setImageDrawable(i.loadIcon(pm));
            image.setMinimumWidth(50);
            image.setMinimumHeight(50);
            image.setMaxWidth(50);
            image.setMaxHeight(50);
            image.setAdjustViewBounds(true);
            image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            LinearLayout linea = new LinearLayout(this);
            linea.addView(image);
            linea.addView(ch);
            linea.setGravity(Gravity.CENTER_VERTICAL);
            layout.addView(linea);
//            Log.e("app info " + i.loadLabel(pm), String.valueOf(i.uid));
        }
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
                for (CheckBox c : checkBoxes) {
                    if (c.isChecked()) {
                        fo.write(((String) c.getText()+"\n").getBytes());
                        Log.i("checked", (String) c.getText());
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


}
