package com.android.lightcontrol;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class Controlling extends Activity {
    private static final String TAG = "BlueTest5-Controlling";
    private int mMaxChars = 50000;//Default//change this to string..........
    private UUID mDeviceUUID;
    private BluetoothSocket mBTSocket;
    private ReadInput mReadThread = null;


    private boolean mIsUserInitiatedDisconnect = false;
    private boolean mIsBluetoothConnected = false;


    private Button mBtnDisconnect;
    private BluetoothDevice mDevice;

//    final static String on="92";//on
//    final static String off="79";//off

    final static int delay_time = 20;


    private ProgressDialog progressDialog;
//    Button btnOn,btnOff;

    // multitouch stuff
    private static String TAG_MULTIPLE_TOUCH = "MULTIPLE_TOUCH";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controlling);

        ActivityHelper.initialize(this);
        // mBtnDisconnect = (Button) findViewById(R.id.btnDisconnect);
//        btnOn=(Button)findViewById(R.id.on);
//        btnOff=(Button)findViewById(R.id.off);

        final TextView pointerOneStatusTextView = (TextView)findViewById(R.id.multi_touch_pointer1);
        final TextView pointerTwoStatusTextView = (TextView)findViewById(R.id.multi_touch_pointer2);
        LinearLayout layout = (LinearLayout)findViewById(R.id.multi_touch_layout);


        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        mDevice = b.getParcelable(MainActivity.DEVICE_EXTRA);
        mDeviceUUID = UUID.fromString(b.getString(MainActivity.DEVICE_UUID));
        mMaxChars = b.getInt(MainActivity.BUFFER_SIZE);

        Log.d(TAG, "Ready");


        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                // Get total pointer number.
                int totalPointerCount = motionEvent.getPointerCount();
                StringBuffer btString1 = new StringBuffer();
                StringBuffer btString2 = new StringBuffer();
                StringBuffer value = new StringBuffer();
                StringBuffer digit = new StringBuffer();
//                float x1, y1, x2, y2, xc, yc;
                float x1 = 0;
                float y1 = 0;

//                btString.setLength(0);

                // Loop for each pointer.
                for(int i=0;i<totalPointerCount;i++)
                {
                    StringBuffer pointerStatusBuf = new StringBuffer();


                    // Get pointer id.
                    int pointerId = motionEvent.getPointerId(i);
                    pointerStatusBuf.append("Id: ");
                    pointerStatusBuf.append(pointerId);
                    pointerStatusBuf.append(" . ");

                    // Get pointer action.
                    pointerStatusBuf.append("Action : ");

                    int action = motionEvent.getAction();

                    if(action == MotionEvent.ACTION_DOWN)
                    {
                        pointerStatusBuf.append("Down. ");
                    }else if(action == MotionEvent.ACTION_UP)
                    {
                        pointerStatusBuf.append("Up. ");
                    }else if(action == MotionEvent.ACTION_POINTER_DOWN)
                    {
                        pointerStatusBuf.append("Pointer Down. ");
                    }else if(action == MotionEvent.ACTION_POINTER_UP)
                    {
                        pointerStatusBuf.append("Pointer Up. ");
                    }else if(action == MotionEvent.ACTION_MOVE)
                    {
                        pointerStatusBuf.append("Move. ");
                    }else if(action == MotionEvent.ACTION_HOVER_ENTER)
                    {
                        pointerStatusBuf.append("Hover Enter. ");
                    }else if(action == MotionEvent.ACTION_HOVER_MOVE)
                    {
                        pointerStatusBuf.append("Hover Move. ");
                    }else if(action == MotionEvent.ACTION_HOVER_EXIT)
                    {
                        pointerStatusBuf.append("Hover Exit. ");
                    }else if(action == MotionEvent.ACTION_POINTER_2_DOWN)
                    {
                        pointerStatusBuf.append("Pointer 2 Down. ");
                    }else if(action == MotionEvent.ACTION_POINTER_2_UP)
                    {
                        pointerStatusBuf.append("Pointer 2 Up. ");
                    }else
                    {
                        pointerStatusBuf.append(action + ". ");
                    }


                    // Get action index.
//                    int actionIndex = motionEvent.getActionIndex();
//                    pointerStatusBuf.append("Action Index : ");
//                    pointerStatusBuf.append(actionIndex);
//                    pointerStatusBuf.append(" . ");

                    // Get pointer X coordinates.
                    float x = motionEvent.getX(i);
                    if (x < 0){ x = 0;}



                    // Get pointer Y coordinates.
                    float y = motionEvent.getY(i);
                    if (y < 0){ y = 0;}
                    
                    pointerStatusBuf.append("X : ");
                    pointerStatusBuf.append(x);
                    pointerStatusBuf.append(" , Y : ");
                    pointerStatusBuf.append(y);

                    // Display the pointer info in logcat console.
                    Log.d(TAG_MULTIPLE_TOUCH, pointerStatusBuf.toString());

//                    String outStream = null;
//                    try {
//                        outStream = mBTSocket.getOutputStream().toString();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    // Write to Bluetooth

//                    btString.append(" ");
//                    btString.append(pointerId);
//                    btString.append(",");
//                    btString.append((int) x);
//                    btString.append(",");
//                    btString.append((int) y);
//                    btString.append(" ");
//                    if (outStream.split(" ").length == 1){
                    if(pointerId == 0)
                    {
                        // First textview display first touch pointer status info.
                        pointerOneStatusTextView.setText(pointerStatusBuf.toString());

//                            // Write to Bluetooth
//                            btString.setLength(0);
//                            btString.append(" ");
//                            btString.append(pointerId);
//                            btString.append(",");
                        btString1.setLength(0);

                        x1 = x;
                        y1 = y;

//                        if (x < 100){
//                            digit.setLength(0);
//                            digit.append("0");
//                            digit.append((int) x/10);
//                            btString1.append(digit);
//                        }
//                        else{
//                            btString1.append((int) x/10);
//                        }
//                        if (y < 100){
//                            digit.setLength(0);
//                            digit.append("0");
//                            digit.append((int) y/10);
//                            btString1.append(digit);
//                        }
//                        else if (y > 1000){
//                            btString1.append(99);
//
//                        }
//                        else{
//                            btString1.append((int) y/10);
//                        }

                    }else if(pointerId == 1)
                    {
                        // Second textview display second touch pointer status info.
                        pointerTwoStatusTextView.setText(pointerStatusBuf.toString());

                        btString2.setLength(0);

                        float x2 = x;
                        float y2 = y;

                        float xc = (x1 + x2) / 2;
                        float yc = (y1 + y2) / 2;

//                         for sending thousands digit
                        if (xc < 1000){
                            digit.setLength(0);
                            digit.append("0");
                            if(xc<100){
                                digit.append("0");
                            }
                            digit.append((int) xc);
                            btString2.append(digit);
                        }
                        else {
                            btString2.append((int) xc);
                        }
                        if (yc < 1000){
                            digit.setLength(0);
                            digit.append("0");
                            if(yc<100){
                                digit.append("0");
                            }
                            digit.append((int) yc);
                            btString2.append(digit);
                        }
                        else{
                            btString2.append((int) yc);
                        }

//                        // for sending less than 1000
//                        if(xc<100){
//                            digit.setLength(0);
//                            digit.append("0");
//                            digit.append((int) xc);
//                            btString2.append(digit);
//                        }
//                        else {
//                            btString2.append((int) xc);
//                        }
//                        if(yc<100){
//                            digit.setLength(0);
//                            digit.append("0");
//                            digit.append((int) yc);
//                            btString2.append(digit);
//                        }
//                        else if (y > 1000) {
//                            btString2.append(999);
//                         }
//                        else{
//                            btString2.append((int) yc);
//                        }
//
//                        if (btString2.length() != 0) {
//                            btString1.append(btString2);
                            try {
                                // turn to hex code
//                                int str_int = Integer.parseInt(btString1.toString());
//                                for (int counter = 0; counter < btString1.length(); counter+= 2){
//                                    char c = btString1.charAt(counter);
//                                    value.setLength(0);
//                                    value.append(btString1.charAt(counter));
//                                    value.append(btString1.charAt(counter+1));
//                                    int asd = Integer.parseInt(value.toString());
//                                    byte[] pair_byte = value.toString().getBytes();
//                                    mBTSocket.getOutputStream().write(asd);
//                                }
//                                mBTSocket.getOutputStream().write(btString1.toString().getBytes());
                                mBTSocket.getOutputStream().write(btString2.toString().getBytes());



                            } catch (IOException e) {
                                e.printStackTrace();
                            }
//                            try {
//                                Thread.sleep(delay_time);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
                    }
//                        btString.append(" ");




                }
//                try {
//                    mBTSocket.getOutputStream().write(btString.toString().getBytes());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

//                try {
//                    Thread.sleep(300);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                // Tell android system event has been consumed by this listener.
                return true;
            }
        });

    }

    private class ReadInput implements Runnable {

        private boolean bStop = false;
        private Thread t;

        public ReadInput() {
            t = new Thread(this, "Input Thread");
            t.start();
        }

        public boolean isRunning() {
            return t.isAlive();
        }

        @Override
        public void run() {
            InputStream inputStream;

            try {
                inputStream = mBTSocket.getInputStream();
                while (!bStop) {
                    byte[] buffer = new byte[256];
                    if (inputStream.available() > 0) {
                        inputStream.read(buffer);
                        int i = 0;
                        /*
                         * This is needed because new String(buffer) is taking the entire buffer i.e. 256 chars on Android 2.3.4 http://stackoverflow.com/a/8843462/1287554
                         */
                        for (i = 0; i < buffer.length && buffer[i] != 0; i++) {
                        }
                        final String strInput = new String(buffer, 0, i);

                        /*
                         * If checked then receive text, better design would probably be to stop thread if unchecked and free resources, but this is a quick fix
                         */



                    }
                    Thread.sleep(500);
                }
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        public void stop() {
            bStop = true;
        }

    }

    private class DisConnectBT extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {//cant inderstand these dotss

            if (mReadThread != null) {
                mReadThread.stop();
                while (mReadThread.isRunning())
                    ; // Wait until it stops
                mReadThread = null;

            }

            try {
                mBTSocket.close();
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mIsBluetoothConnected = false;
            if (mIsUserInitiatedDisconnect) {
                finish();
            }
        }

    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        if (mBTSocket != null && mIsBluetoothConnected) {
            new DisConnectBT().execute();
        }
        Log.d(TAG, "Paused");
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mBTSocket == null || !mIsBluetoothConnected) {
            new ConnectBT().execute();
        }
        Log.d(TAG, "Resumed");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Stopped");
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
// TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean mConnectSuccessful = true;

        @Override
        protected void onPreExecute() {

            progressDialog = ProgressDialog.show(Controlling.this, "Hold on", "Connecting");// http://stackoverflow.com/a/11130220/1287554

        }

        @Override
        protected Void doInBackground(Void... devices) {

            try {
                if (mBTSocket == null || !mIsBluetoothConnected) {
                    mBTSocket = mDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mBTSocket.connect();
                }
            } catch (IOException e) {
// Unable to connect to device`
                // e.printStackTrace();
                mConnectSuccessful = false;



            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!mConnectSuccessful) {
                Toast.makeText(getApplicationContext(), "Could not connect to device.Please turn on your Hardware", Toast.LENGTH_LONG).show();
                finish();
            } else {
                msg("Connected to device");
                mIsBluetoothConnected = true;
                mReadThread = new ReadInput(); // Kick off input reader
            }

            progressDialog.dismiss();
        }

    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}