package com.example.shu.homeautomation2;

/**
 * Created by Shu on 14/04/2016.
 */

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cardiomood.android.speedometer.SpeedometerView;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;






public class ledControl extends AppCompatActivity {

    Button btnOn, btnOff, b1, b2, b3, b4, buz1, buz2, btnDis;

    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    android.os.Handler customHandler = new android.os.Handler();
    int i = 50;
    int a;
    private SpeedometerView speedometer;
    private SpeedometerView speedometer2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS); //receive the address of the bluetooth device

        //view of the ledControl
        setContentView(R.layout.activity_led_control);

        //call the widgtes
        btnOn = (Button) findViewById(R.id.button2);
        btnOff = (Button) findViewById(R.id.button3);
        btnDis = (Button) findViewById(R.id.button4);
        b1 = (Button) findViewById(R.id.b1);
        b2 = (Button) findViewById(R.id.b2);
        b3 = (Button) findViewById(R.id.b3);
        b4 = (Button) findViewById(R.id.b4);
        buz1 = (Button) findViewById(R.id.buz1);
        buz2 = (Button) findViewById(R.id.buz2);


        new ConnectBT().execute();

        customHandler.postDelayed(updateTimerThread, 0);
        // Customize SpeedometerGauge
        speedometer = (SpeedometerView)findViewById(R.id.speedometer);

        // Add label converter
        speedometer.setLabelConverter(new SpeedometerView.LabelConverter() {
            @Override
            public String getLabelFor(double progress, double maxProgress) {
                return String.valueOf((int) Math.round(progress));
            }
        });

        // configure value range and ticks
        speedometer.setMaxSpeed(100);
        speedometer.setMajorTickStep(10);
        speedometer.setMinorTicks(4);

        // Configure value range colors

        speedometer.addColoredRange(10, 40, Color.GREEN);
        speedometer.addColoredRange(40, 60, Color.YELLOW);
        speedometer.addColoredRange(60, 100, Color.RED);

        // Customize SpeedometerGauge
        speedometer2 = (SpeedometerView)findViewById(R.id.speedometer2);

        // Add label converter
        speedometer2.setLabelConverter(new SpeedometerView.LabelConverter() {
            @Override
            public String getLabelFor(double progress, double maxProgress) {
                return String.valueOf((int) Math.round(progress));
            }
        });

        // configure value range and ticks
        speedometer2.setMaxSpeed(100);
        speedometer2.setMajorTickStep(10);
        speedometer2.setMinorTicks(4);

        // Configure value range colors

        speedometer2.addColoredRange(10, 40, Color.GREEN);
        speedometer2.addColoredRange(40, 60, Color.YELLOW);
        speedometer2.addColoredRange(60, 100, Color.RED);


        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                turnOnLed();
                RadioButton rbu1 = (RadioButton) findViewById(R.id.r1);
                rbu1.setChecked(true);
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                turnOffLed();
                RadioButton rbu1 = (RadioButton) findViewById(R.id.r1);
                rbu1.setChecked(false);
            }
        });

        btnDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Disconnect();
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOnFan();
                RadioButton rbu1 = (RadioButton) findViewById(R.id.r2);
                rbu1.setChecked(true);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOffFan();
                RadioButton rbu1 = (RadioButton) findViewById(R.id.r2);
                rbu1.setChecked(false);
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOnAC();
                RadioButton rbu1 = (RadioButton) findViewById(R.id.r3);
                rbu1.setChecked(true);
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOffAC();
                RadioButton rbu1 = (RadioButton) findViewById(R.id.r3);
                rbu1.setChecked(false);
            }
        });

        buz1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOnLamp();
                RadioButton rbu1 = (RadioButton) findViewById(R.id.r4);
                rbu1.setChecked(true);
            }
        });

        buz2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOffLamp();
                RadioButton rbu1 = (RadioButton) findViewById(R.id.r4);
                rbu1.setChecked(false);
            }
        });


    }


    private void Disconnect() {
        if (btSocket != null) {
            try {
                btSocket.close();
            } catch (IOException e) {
                msg("Error");
            }
        }
        finish();
    }

    private void turnOffLed() {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write('0');
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void turnOnLed() {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write('1');
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void turnOnLamp() {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write('7');
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void turnOffLamp() {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write('8');
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void turnOnAC() {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write('5');
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void turnOffAC() {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write('6');
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void turnOnFan() {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write('3');
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void turnOffFan() {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write('4');
            } catch (IOException e) {
                msg("Error");
            }
        }
    }


    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_led_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;


        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(ledControl.this, "Connecting...", "Please wait!!!");
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if ((!ConnectSuccess) && (i > 0)) {
                i--;
                new ConnectBT().execute();


            } else if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            } else

            {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            //write here whaterver you want to repeat

            String str = "0";


            TextView speed = (TextView) findViewById(R.id.speed);
            TextView speed2 = (TextView) findViewById(R.id.speed2);


            try {
                InputStream mmInputStream;
                final byte delimiter = 10;
                mmInputStream = btSocket.getInputStream();
                byte[] readBuffer;
                int readBufferPosition;
                readBufferPosition = 0;
                readBuffer = new byte[1024];

                int bytesAvailable = mmInputStream.available();


                Log.d("value", String.valueOf(bytesAvailable));
                if (bytesAvailable > 0) {
                    byte[] packetBytes = new byte[bytesAvailable];
                    mmInputStream.read(packetBytes);
                    for (int i = 0; i < bytesAvailable; i++) {
                        byte bs = packetBytes[i];

                        str = new String(packetBytes, "UTF-8");
                        Log.e("A is", str);
                        try {
                            a = Integer.parseInt(str.replaceAll("[\\D]", ""));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (a > 1000 && a < 2000) {
                            int b = a - 1000;
                            speed.setText(String.valueOf(b));
                            speedometer.setSpeed(b);
                        } else if (a > 2000 && a < 3000) {
                            int c = a - 2000;
                            speed2.setText(String.valueOf(c));
                            speedometer2.setSpeed(c);
                        }

                        Log.d("Hello", str);
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            customHandler.postDelayed(this, 100);
        }
    };
}

