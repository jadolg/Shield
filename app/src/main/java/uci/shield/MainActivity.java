package uci.shield;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

//    private TextView textOutput;
    ToggleButton button;
    ToggleButton button2;
    ToggleButton button3;
    ToggleButton button4;
    private String default_addrs = "addons.mozilla.\n" +
            "fbstatic\n" +
            "cdn.\n" +
            "apis.google.com\n" +
            "fsdn.";

    private ArrayList<String> addrs;
    private ArrayList<String> checkedApps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (ToggleButton) findViewById(R.id.toggleButton);
        button2 = (ToggleButton) findViewById(R.id.toggleButton2);
        button3 = (ToggleButton) findViewById(R.id.toggleButton3);
        button4 = (ToggleButton) findViewById(R.id.toggleButton4);
        createFile();
        readFile();

        confButtons();

    }

    @Override
    protected void onResume() {
        //used to configure the form when it is restarted
        //if closed by the system
        super.onResume();
        readFile();
        confButtons();
    }

    private ArrayList<String> getDomainS(){
        Class<?> SystemProperties = null;
        try {
            SystemProperties = Class.forName("android.os.SystemProperties");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        assert SystemProperties != null;
        Method method = null;
        try {
            method = SystemProperties.getMethod("get", new Class[] { String.class });
            ArrayList<String> servers = new ArrayList<String>();
//            for (String name : new String[] { "net.dns1", "net.dns2", "net.dns3", "net.dns4", }) {
            for (String name : new String[] { "net.dns1", "net.dns2", }) {
                String value = (String) method.invoke(null, name);
                if (value != null && !"".equals(value) && !servers.contains(value)){
                    Log.e("DNS",value);
                    servers.add(value);}
            }
            return servers;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private void loadCheckedApps(){
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard, "apps_shield.txt");
        checkedApps.clear();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                checkedApps.add(line);
                Log.w("checked app",line);
            }
            br.close();
//            this.textOutput.setText(text);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), getString(R.string.error_reading_file),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void confButtons(){
        if (isOn()) {
            button.setChecked(true);
        } else {
            button.setChecked(false);
        }

        if (isNautaOn()){
            button2.setChecked(true);
        } else {
            button2.setChecked(false);
        }

        if (isDatablockON()){
            button3.setChecked(true);
        } else {
            button3.setChecked(false);
        }

        if (isNautaNDNSOn()){
            button4.setChecked(true);
        } else {
            button4.setChecked(false);
        }

    }



    private void readFile() {
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard, "addresses_shield.txt");

        StringBuilder text = new StringBuilder();
        addrs = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                addrs.add(line);
                text.append(line);
                text.append('\n');
            }
            br.close();
//            this.textOutput.setText(text);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), getString(R.string.error_reading_file),
                    Toast.LENGTH_SHORT).show();
        }

    }


    private void createFile() {
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard, "addresses_shield.txt");
        if (!file.exists()) {
            try {
                FileOutputStream fo = new FileOutputStream(file);
                fo.write(default_addrs.getBytes());
                fo.close();
            } catch (FileNotFoundException e) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_creating_file),
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.editstrings) {
            File sdcard = Environment.getExternalStorageDirectory();
            File file = new File(sdcard, "addresses_shield.txt");
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "text/plain");

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        } if (id == R.id.nautaapps){
            Intent myIntent = new Intent(MainActivity.this, Aplicaciones.class);
            startActivityForResult(myIntent, 239);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 239){
            if (isNautaOn()){
                nautaUnprot();
                nautaProt();
            }

            if (isNautaNDNSOn()){
                nautaUnprotNDNS();
                nautaProtNDNS();
            }
        }
    }

    public void sudo(ArrayList<String> strings) {
        try {
            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

            for (String s : strings) {
                outputStream.writeBytes(s + "\n");
                outputStream.flush();
            }

            outputStream.writeBytes("exit\n");
            outputStream.flush();
            try {
                su.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            outputStream.close();

        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), getString(R.string.cant_get_root),
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private String suexec(String cmd) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            DataOutputStream outputStream = new DataOutputStream(process.getOutputStream());
            outputStream.writeBytes(cmd + "\n");
            outputStream.flush();
            outputStream.writeBytes("exit\n");
            outputStream.flush();
            outputStream.close();
            String line = bufferedReader.readLine();
            StringBuilder res = new StringBuilder();
            while (line != null) {
                res.append(line + "\n");
                line = bufferedReader.readLine();
            }
            return res.toString();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), getString(R.string.cant_get_root),
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return "error";
    }

    private boolean isOn() {
        String iptables = suexec("iptables -nL");
        for (String string : addrs) {
            if (!iptables.contains("STRING match  \""+string+"\" ALGO name kmp TO 65535 reject-with tcp-reset")) {
                return false;
            }
        }
        return true;
    }

    private boolean isNautaOn() {
        String iptables = suexec("iptables -S");
        if (iptables.contains("-N NAUTA")){
            return true;
        }
        else return false;
    }

    private boolean isNautaNDNSOn() {
        String iptables = suexec("iptables -S");
        if (iptables.contains("-N NDNSNAUTA")){
            return true;
        }
        else return false;
    }

    private boolean isDatablockON(){
        String iptables = suexec("iptables -L OUTPUT");
        String string = "REJECT     all  --  anywhere             anywhere             reject-with icmp-port-unreachable";
        if (iptables.contains(string)){
            return true;
        } else {
            return false;
        }
    }

    private void nautaProt(){
        loadCheckedApps();
        ArrayList<String> a = new ArrayList<>();
//        a.add("iptables -A OUTPUT -o rmnet0 -j REJECT");
        a.add("iptables -N NAUTA");
        for (String app : checkedApps) {
//            String tapp = app.substring(0, app.indexOf(":"));
//            Log.e("ch", app.substring(0, app.indexOf(":")));

            for (String dnsServer : getDomainS()) {
                a.add("iptables -A NAUTA -o rmnet0 -d "+dnsServer+" -p tcp -m tcp --dport 53 -m owner --uid-owner "+app+" -j ACCEPT");
                a.add("iptables -A NAUTA -o rmnet0 -d "+dnsServer+" -p udp -m udp --dport 53 -m owner --uid-owner "+app+" -j ACCEPT");
            }

            a.add("iptables -A NAUTA -o rmnet0 -d 181.225.231.0/24 -p tcp --dport 25 -m owner --uid-owner "+app+" -j ACCEPT");
            a.add("iptables -A NAUTA -o rmnet0 -d 181.225.231.0/24 -p tcp --dport 110 -m owner --uid-owner "+app+" -j ACCEPT");
            a.add("iptables -A NAUTA -o rmnet0 -d 181.225.231.0/24 -p tcp --dport 143 -m owner --uid-owner "+app+" -j ACCEPT");
            a.add("iptables -A NAUTA -o rmnet0 -d 181.225.231.0/24 -p tcp --dport 80 -m owner --uid-owner "+app+" -j ACCEPT");
            a.add("iptables -A NAUTA -o rmnet0 -d 181.225.231.0/24 -p tcp --dport 443 -m owner --uid-owner "+app+" -j ACCEPT");
        }

        a.add("iptables -A NAUTA -o rmnet0 -p tcp -j REJECT --reject-with tcp-reset");
        a.add("iptables -A NAUTA -o rmnet0 -p udp -j REJECT");
        a.add("iptables -A OUTPUT -j NAUTA");

        sudo(a);
    }

    private void nautaUnprot(){
        ArrayList<String> a = new ArrayList<>();
        a.add("iptables -D OUTPUT -j NAUTA");
        a.add("iptables -F NAUTA");
        a.add("iptables -X NAUTA");
        sudo(a);
    }

    private void nautaProtNDNS(){
        loadCheckedApps();
        ArrayList<String> a = new ArrayList<>();
//        a.add("iptables -A OUTPUT -o rmnet0 -j REJECT");
        a.add("iptables -N NDNSNAUTA");
        for (String app : checkedApps) {
//            String tapp = app.substring(0, app.indexOf(":"));
//            Log.e("ch", app.substring(0, app.indexOf(":")));

            a.add("iptables -A NDNSNAUTA -o rmnet0 -d 181.225.231.0/24 -p tcp --dport 25 -m owner --uid-owner "+app+" -j ACCEPT");
            a.add("iptables -A NDNSNAUTA -o rmnet0 -d 181.225.231.0/24 -p tcp --dport 110 -m owner --uid-owner "+app+" -j ACCEPT");
            a.add("iptables -A NDNSNAUTA -o rmnet0 -d 181.225.231.0/24 -p tcp --dport 143 -m owner --uid-owner "+app+" -j ACCEPT");
//            a.add("iptables -A NDNSNAUTA -o rmnet0 -d 181.225.231.0/24 -p tcp --dport 80 -m owner --uid-owner "+tapp+" -j ACCEPT");
//            a.add("iptables -A NDNSNAUTA -o rmnet0 -d 181.225.231.0/24 -p tcp --dport 443 -m owner --uid-owner "+tapp+" -j ACCEPT");
        }

        a.add("iptables -A NDNSNAUTA -o rmnet0 -p tcp -j REJECT --reject-with tcp-reset");
        a.add("iptables -A NDNSNAUTA -o rmnet0 -p udp -j REJECT");
        a.add("iptables -A OUTPUT -j NDNSNAUTA");

        sudo(a);
    }

    private void nautaUnprotNDNS(){
        ArrayList<String> a = new ArrayList<>();
        a.add("iptables -D OUTPUT -j NDNSNAUTA");
        a.add("iptables -F NDNSNAUTA");
        a.add("iptables -X NDNSNAUTA");
        sudo(a);
    }

    private void blockData(){
        if (isNautaNDNSOn()){
            nautaUnprotNDNS();
        }
        if (isNautaOn()){
            nautaUnprot();
        }

        button2.setChecked(false);
        ArrayList<String> a = new ArrayList<>();
        a.add("iptables -A OUTPUT -o rmnet0 -j REJECT");
        sudo(a);
        //x
    }

    private void unBlockData(){
        button2.setChecked(true);
        ArrayList<String> a = new ArrayList<>();
        a.add("iptables -D OUTPUT -o rmnet0 -j REJECT");
        sudo(a);
    }

    public void clicked(View view) {
        ArrayList<String> a = new ArrayList<>();
        String iptables = suexec("iptables -nL");
        if (button.isChecked()) {
            for (String string : addrs) {
                if (!iptables.contains("STRING match  \""+string+"\" ALGO name kmp TO 65535 reject-with tcp-reset")) {
                    a.add("iptables -A OUTPUT -p tcp -m string --string \"" + string + "\" --algo kmp -j REJECT --reject-with tcp-reset");
                }
            }
        } else {
            for (String string : addrs) {
                a.add("iptables -D OUTPUT -p tcp -m string --string \"" + string + "\" --algo kmp -j REJECT --reject-with tcp-reset");
            }
        }
        sudo(a);
        confButtons();
//        Log.w("iptables -nL", suexec("iptables -nL"));

    }

    public void clickedBlock(View view) {
        if (button3.isChecked()){
            blockData();
        } else {
            unBlockData();
        }
        confButtons();
    }

    public void clickedNauta(View view) {
        if (button2.isChecked()) {
            Log.w("setting nauta","");
            if (isNautaNDNSOn()){
                nautaUnprotNDNS();
            }

            if (isDatablockON()){
                unBlockData();
            }
            nautaProt();
        } else {
            nautaUnprot();
            Log.w("unsetting nauta","");
         }
        confButtons();
       }

    public void clickedNautaNDNS(View view) {
        if (button4.isChecked()) {
            Log.w("setting nauta","");
            if (isNautaOn()){
                nautaUnprot();
            }
            if (isDatablockON()){
                unBlockData();
            }
            nautaProtNDNS();
        } else {
            nautaUnprotNDNS();
            Log.w("unsetting nauta","");
        }
        confButtons();
    }

}
