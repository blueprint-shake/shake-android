package com.stevenwood.com.shake;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.Button;
import com.stevenwood.com.shake.Util.Acceleration.Point;
import com.stevenwood.com.shake.Util.Acceleration.Shaker;
import com.stevenwood.com.shake.Util.Contact;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements SensorEventListener {
    static String TAG = "WASD";
    static final int PICK_CONTACT_REQUEST = 1;  // The request code
    Contact currentContact;
    Contact receivedContact;
    Button bSelectContact;
    Button bRemove;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

        bSelectContact = (Button) findViewById(R.id.bSelectContact);
        bRemove = (Button) findViewById(R.id.bRemove);
        bSelectContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
                pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
                startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
            }
        });
        bRemove = (Button) findViewById(R.id.bRemove);
        bRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToContacts();

            }
        });
    }

    public void addToContacts(){
        if(receivedContact != null) {
            Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
            intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
            intent.putExtra(ContactsContract.Intents.Insert.EMAIL, receivedContact.getEmail());
            intent.putExtra(ContactsContract.Intents.Insert.NAME, receivedContact.getName());
            intent.putExtra(ContactsContract.Intents.Insert.COMPANY, receivedContact.getCompany());
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, receivedContact.getphoneNumber());
            intent.putExtra(ContactsContract.Intents.Insert.POSTAL, receivedContact.getAddress());
            intent.putExtra(ContactsContract.Intents.Insert.NAME, receivedContact.getName());

            startActivity(intent);
        }
    }

    @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
                currentContact = new Contact();
                String phoneNo = null ;
                Uri uri = data.getData();
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                cursor.moveToFirst();

                int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                phoneNo = cursor.getString(phoneIndex);
                currentContact.setPhoneNumber(phoneNo);
                //int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

           }
        }
    }

   //DELETE CUZ A TEST
    public void dummyContact(){
        receivedContact = new Contact();
        receivedContact.setCompany("Google");
        receivedContact.setEmail("stevenator21@gmail.com");
        receivedContact.setPhoneNumber("9781231");
        receivedContact.setAddres("12 Old Village Road");
        receivedContact.setName("StevenWood");
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private ArrayList<Point> acc = new ArrayList<Point>();

    private void cleanAcc(){
        if(acc.size() == 0) return;
        long now = System.currentTimeMillis();
        while(now - acc.get(0).getTimestamp() > 1000*2) acc.remove(0);
    }

    ArrayList<Point> peaks;
    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor eventSensor = event.sensor;
        if (eventSensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            long time = System.currentTimeMillis();

            acc.add(new Point(x, y, z, time));
            cleanAcc();

            ArrayList<Point> peaks = Shaker.detect(acc);
            if (peaks != null) {
                new SendAccel().execute();
                acc.clear();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    }
    @Override
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);

    }

    //static String server = "http://10.189.5.152:8083";
    public JSONObject getJSON(String urlParam) throws IOException, JSONException {
        HttpClient httpClient = new DefaultHttpClient();
        StringBuilder url = new StringBuilder(urlParam);

        HttpGet get = new HttpGet(url.toString());
        HttpResponse r = httpClient.execute(get);
        int status = r.getStatusLine().getStatusCode();
        if (status == 200) {
            HttpEntity e = r.getEntity();
            String data = EntityUtils.toString(e);
            JSONArray array = new JSONArray();
            JSONObject last = array.getJSONObject(0);
            return last;
        }
        return null;
    }

    public class SendAccel extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... params) {
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://10.189.5.152:8083/api");

                JSONStringer vm;
                vm = new JSONStringer().object().key("__method__")
                        .value("logger.log").key("string").value(peaks).endObject();
                StringEntity entity = new StringEntity(vm.toString());

                httppost.setEntity(entity);

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                sb.append(reader.readLine() + "\n");
                String line = "0";
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                reader.close();
                String result11 = sb.toString();
                Log.v(TAG, result11);
                return result11;
                // parsing data
               // return new JSONArray(result11);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }


    }
}
