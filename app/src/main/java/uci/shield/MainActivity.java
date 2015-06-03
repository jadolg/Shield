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
import android.widget.TextView;
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
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;


public class MainActivity extends ActionBarActivity {

//    private TextView textOutput;
    ToggleButton button;
    ToggleButton button2;
    ToggleButton button3;
    private String default_addrs = "addons.mozilla.\n" +
            "fbstatic\n" +
            "cdn.\n" +
            "apis.google.com\n" +
            "fsdn.";

    private ArrayList<String> addrs;
    ArrayList<String> nauta_rules = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        textOutput = (TextView) findViewById(R.id.editText);
        button = (ToggleButton) findViewById(R.id.toggleButton);
        button2 = (ToggleButton) findViewById(R.id.toggleButton2);
        button3 = (ToggleButton) findViewById(R.id.toggleButton3);
        createFile();
        readFile();

        nauta_rules.add("ACCEPT     tcp  --  0.0.0.0/0            200.55.128.3         tcp dpt:53");
        nauta_rules.add("ACCEPT     udp  --  0.0.0.0/0            200.55.128.3         udp dpt:53");
        nauta_rules.add("ACCEPT     tcp  --  0.0.0.0/0            190.6.81.0/24        tcp dpt:25");
        nauta_rules.add("ACCEPT     tcp  --  0.0.0.0/0            190.6.81.0/24        tcp dpt:110");
        nauta_rules.add("ACCEPT     tcp  --  0.0.0.0/0            190.6.81.0/24        tcp dpt:143");
        nauta_rules.add("ACCEPT     tcp  --  0.0.0.0/0            190.6.81.0/24        tcp dpt:80");
        nauta_rules.add("ACCEPT     tcp  --  0.0.0.0/0            190.6.81.0/24        tcp dpt:443");
        nauta_rules.add("REJECT     tcp  --  0.0.0.0/0            0.0.0.0/0            reject-with tcp-reset");
        nauta_rules.add("REJECT     udp  --  0.0.0.0/0            0.0.0.0/0            reject-with icmp-port-unreachable");

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
        }

        return super.onOptionsItemSelected(item);
    }

    public void getIfs(){
        try {
            Log.w("getting interfaces","here");
            for (Enumeration<NetworkInterface> en =
                         NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
            {
                NetworkInterface intf = en.nextElement();
                // Iterate over all IP addresses in each network interface.
                for (Enumeration<InetAddress> enumIPAddr =
                             intf.getInetAddresses(); enumIPAddr.hasMoreElements();)
                {
                    InetAddress iNetAddress = enumIPAddr.nextElement();
                    // Loop back address (127.0.0.1) doesn't count as an in-use
                    // IP address.
                    if (!iNetAddress.isLoopbackAddress())
                    {

//                        this.textOutput.append(intf.getName()+"\n");
    //                    sLocalIP = iNetAddress.getHostAddress().toString();
    //                    sInterfaceName = intf.getName();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
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
        String iptables = suexec("iptables -nL");
        for (String string : nauta_rules) {
            if (!iptables.contains(string)) {
                return false;
            }
        }
        return true;
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

    private void dataProt(){
        ArrayList<String> a = new ArrayList<>();
//        a.add("iptables -A OUTPUT -o rmnet0 -j REJECT");
        a.add("iptables -A OUTPUT -o rmnet0 -d 200.55.128.3 -p tcp -m tcp --dport 53 -j ACCEPT");
        a.add("iptables -A OUTPUT -o rmnet0 -d 200.55.128.3 -p udp -m udp --dport 53 -j ACCEPT");
        a.add("iptables -A OUTPUT -o rmnet0 -d 190.6.81.0/24 -p tcp --dport 25 -j ACCEPT");
        a.add("iptables -A OUTPUT -o rmnet0 -d 190.6.81.0/24 -p tcp --dport 110 -j ACCEPT");
        a.add("iptables -A OUTPUT -o rmnet0 -d 190.6.81.0/24 -p tcp --dport 143 -j ACCEPT");
        a.add("iptables -A OUTPUT -o rmnet0 -d 190.6.81.0/24 -p tcp --dport 80 -j ACCEPT");
        a.add("iptables -A OUTPUT -o rmnet0 -d 190.6.81.0/24 -p tcp --dport 443 -j ACCEPT");
        a.add("iptables -A OUTPUT -o rmnet0 -p tcp -j REJECT --reject-with tcp-reset");
        a.add("iptables -A OUTPUT -o rmnet0 -p udp -j REJECT");
        sudo(a);
    }

    private void dataUnprot(){
        ArrayList<String> a = new ArrayList<>();
//        a.add("iptables -D OUTPUT -o rmnet0 -j REJECT");
        a.add("iptables -D OUTPUT -o rmnet0 -d 200.55.128.3 -p tcp -m tcp --dport 53 -j ACCEPT");
        a.add("iptables -D OUTPUT -o rmnet0 -d 200.55.128.3 -p udp -m udp --dport 53 -j ACCEPT");
        a.add("iptables -D OUTPUT -o rmnet0 -d 190.6.81.0/24 -p tcp --dport 25 -j ACCEPT");
        a.add("iptables -D OUTPUT -o rmnet0 -d 190.6.81.0/24 -p tcp --dport 110 -j ACCEPT");
        a.add("iptables -D OUTPUT -o rmnet0 -d 190.6.81.0/24 -p tcp --dport 143 -j ACCEPT");
        a.add("iptables -D OUTPUT -o rmnet0 -d 190.6.81.0/24 -p tcp --dport 80 -j ACCEPT");
        a.add("iptables -D OUTPUT -o rmnet0 -d 190.6.81.0/24 -p tcp --dport 443 -j ACCEPT");
        a.add("iptables -D OUTPUT -o rmnet0 -p tcp -j REJECT --reject-with tcp-reset");
        a.add("iptables -D OUTPUT -o rmnet0 -p udp -j REJECT");
        sudo(a);
    }

    private void blockData(){
        dataUnprot();
        button2.setChecked(false);
        ArrayList<String> a = new ArrayList<>();
        a.add("iptables -A OUTPUT -o rmnet0 -j REJECT");
        sudo(a);
    }

    private void unBlockData(){
        dataProt();
        button2.setChecked(true);
        ArrayList<String> a = new ArrayList<>();
        a.add("iptables -D OUTPUT -o rmnet0 -j REJECT");
        sudo(a);
    }

    public void clicked(View view) {
        getIfs();
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
            dataProt();
        } else {
            dataUnprot();
            Log.w("unsetting nauta","");
         }
        confButtons();
       }

}
