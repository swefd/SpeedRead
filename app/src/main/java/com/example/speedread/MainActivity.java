package com.example.speedread;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import static android.os.Environment.getExternalStorageDirectory;


public class MainActivity extends AppCompatActivity {

    private int STORAGE_PERMISSION_CODE = 1;
    private int READ_REQUEST_CODE = 42;

    private TextView txtShow;

    private TextView SpeedView;


    private TextView Txt_Path_Show;

    private Button Read_Btn;
    private Button Restart_Btn;
    private Button Open_File_Btn;

    String path;
    Intent myFileIntent;

    String sdcardPath = getExternalStorageDirectory().toString();

    //String charset = "UTF-8";

    String temp;
    int speed = 500;

    boolean GoBack = false;
    boolean work = false;
    int PauseIterator = 0;

    public MainActivity() {
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri;
            PauseIterator = 0;
            if (data != null) {
                uri = data.getData();
                assert uri != null;
                path = uri.getPath();
                assert path != null;
                path = sdcardPath + "/" + path.substring(path.indexOf(":") + 1);

                //Log.i("LOG", "Uri: " + uri.toString());


                Toast.makeText(this, "" + path, Toast.LENGTH_SHORT).show();
                Txt_Path_Show.setText(path);
            }
        }


        //Plan B :D

//        switch (requestCode) {
//            case 42:
//                Uri FileUri = null;
//                if (resultCode == RESULT_OK) {
//
//                   // FileUri = data.getData();
//                    //Log.i(TAG, "Uri: " + FileUri.toString());
//                    File_Path = data.getData().getPath();
//                    File_Path = File_Path.substring(14, File_Path.length());
//
//                    //File_Path = FileUri.toString();
//
//                   // Log.i(File_Path, "Uri: " + FileUri.toString());
//
//                    //Txt_Path_Show.setText(FileUri.toString());
//                    Txt_Path_Show.setText(File_Path);
//
//
//                }
//        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //edit = (EditText)findViewById(R.id.editText);
        // if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M && checkSelfPermission(Manifest.Permission.READ_E))

        // requestPermissions(new String[{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE);
        txtShow = findViewById(R.id.textView);
        SpeedView = findViewById(R.id.SpeedView);
        SeekBar speedBar = findViewById(R.id.SpeedBar);
        //Відкриття файлу
        Txt_Path_Show = findViewById(R.id.Txt_Path_Show);

        Open_File_Btn = findViewById(R.id.Open_File_Btn);

        Read_Btn = findViewById(R.id.Read);
        Restart_Btn = findViewById(R.id.Restart);

        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress > 10) {
                    speed = progress;
                    SpeedView.setText(progress + " ms");
                }else {
                    speed = 10;
                    SpeedView.setText("10 ms");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        Open_File_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    myFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    myFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    myFileIntent.setType("text/*");
                    startActivityForResult(myFileIntent, READ_REQUEST_CODE);
                } else {
                    requestStoragePermission();
                }
            }
        });

    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this).setTitle("Дозвіл").setMessage("Для читання файлів потрібно надати доступ до сховища пристрою").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                }
            }).setNegativeButton("Відміна", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No no no", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void read(View view) {

        if (!work) {

            try {


                File file = new File(path);
                // StringBuilder test = new StringBuilder();
                // FileInputStream fileInput = openFileInput(Environment.getExternalStorageState(), path);
                //InputStreamReader reader = new InputStreamReader(path);

                // BufferedReader buffer = new BufferedReader(new InputStreamReader(fileInput));

                BufferedReader buffer = new BufferedReader(new FileReader(file));

                StringBuilder strBuffer = new StringBuilder();

                String lines;

                while ((lines = buffer.readLine()) != null) {
//
                    strBuffer.append(lines).append(" ");
                }
                temp = strBuffer.toString();

                // txtShow.setText(strBuffer.toString());

                final String[] words = temp.split("\\W+");


                final Thread ShowWord = new Thread(new Runnable() {

                    @Override
                    public void run() {


                        for (; PauseIterator < words.length; PauseIterator++) {

                            if (!GoBack && work) {
                                try {
                                txtShow.setText(words[PauseIterator]);

                                    Thread.sleep(speed);
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (GoBack && work) {
                                if (PauseIterator < 5) PauseIterator = 0;
                                else PauseIterator -= 5;
                                GoBack = false;
                                try {
                                    Thread.sleep(3000);
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else {
                                break;
                            }

                        }




//                    for(String word : words){
//                        txtShow.setText(word);
//
//                        try {
//                            Thread.sleep(speed);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
                    }
                });


                /*
                if (PauseIterator == words.length){
                    PauseIterator = 0;
                    Read_Btn.setText("START");                                    //PAUSE TEXT
                    Restart_Btn.setEnabled(true);
                    //Open_File_Btn.setEnabled(true);
                }
*/
                //   Read_Btn.setText("PAUSE");







                Read_Btn.setText("PAUSE");                                    //PAUSE TEXT
                Restart_Btn.setEnabled(false);
                //Open_File_Btn.setEnabled(false);
                work = true;                                                    //WORK TRUE

                ShowWord.start();


            }
            catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Error File Read!", Toast.LENGTH_SHORT).show();
                work = false;
            }

        } else {

            Read_Btn.setText("START");                                    //PAUSE TEXT
            Restart_Btn.setEnabled(true);
            work = false;                                                    //WORK

        }
    }

    public void RestatRead(View view) {
        PauseIterator = 0;
        Toast.makeText(MainActivity.this, "Restart!", Toast.LENGTH_SHORT).show();
    }


    @SuppressLint("SetTextI18n")
    public void enableButtons(int start, int restart, int open, int back){

        if(start == 1){
            Read_Btn.setText("START");                                    //PAUSE TEXT
            Restart_Btn.setEnabled(true);
            Open_File_Btn.setEnabled(true);
        }else if(start == 2){
            Read_Btn.setText("PAUSE");                                    //PAUSE TEXT
            Restart_Btn.setEnabled(false);
            Open_File_Btn.setEnabled(false);
        }
    }


    public void Back(View view) {

        if (!GoBack) {
            Toast.makeText(MainActivity.this, "Back", Toast.LENGTH_SHORT).show();
            GoBack = true;
        }
    }
}



