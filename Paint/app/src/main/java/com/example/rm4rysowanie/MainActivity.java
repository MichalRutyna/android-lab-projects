package com.example.rm4rysowanie;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Mainnn";
    private static DrawingSurface surface;

    private static boolean permissions_granted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        surface = findViewById(R.id.drawing_surface);

        findViewById(R.id.button3).setOnClickListener(v -> surface.mFarba.setColor(Color.RED));
        findViewById(R.id.button4).setOnClickListener(v -> surface.mFarba.setColor(Color.YELLOW));
        findViewById(R.id.button5).setOnClickListener(v -> surface.mFarba.setColor(Color.GREEN));
        findViewById(R.id.button6).setOnClickListener(v -> surface.mFarba.setColor(Color.BLUE));
        findViewById(R.id.button7).setOnClickListener(v -> surface.clearScreen());

        ((MaterialToolbar) findViewById(R.id.materialMenu)).setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.save) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    //o uprawnienia prosimy tylko gdy musimy (sprawdzić wersję Androida)
                    if (ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED) {
                        saveImage();
                    } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            WRITE_EXTERNAL_STORAGE)) {
                        //musimy wyjaśnić użytkownikowi po co nam uprawnienie
                        //...
                        System.out.println("Wyjasnienie");
                    } else {
                        //nie mamy uprawnień - prosimy o uprawnienie
                        //...
                        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE},
                                1);
                    }
                }
                else {
                    saveImage();
                }
                return true;
            }
            if (id == R.id.browse) {
                Intent intent = new Intent(this, BrowseActivity.class);
                startActivity(intent);
            }
            return false;
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        String requiredPermission = getRequiredPermission();
        switch (requestCode) {
            case 1:
                if (permissions.length > 0 && permissions[0].equals(requiredPermission) &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        saveImage();
                } else
                    System.out.println("Nie przyznano uprawnienia");
                    break;
            default:
                System.out.println("Nieznane uprawnienie");
                break;
        }
    }

    private static String getRequiredPermission() {
        String requiredPermission = "";
        //Android wcześniejszy niż 10 (API 29)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            requiredPermission = WRITE_EXTERNAL_STORAGE;
        }
        return requiredPermission;
    }



    public void saveImage() {

        Log.d(TAG, "save image");
        //Informacje o obrazku będziemy zapisywać do bazy MediaStore
        ContentResolver resolver = getApplicationContext().getContentResolver();
        //Odczytanie URI do kolekcji obrazów (zależy od wersji Androida)
        Uri imageCollection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageCollection = MediaStore.Images.Media
                    .getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        //Utworzenie rekordu opisującego zapisywany obraz
        ContentValues imageDetails = new ContentValues();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "IMG_" + timeStamp + ".png";
        imageDetails.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //w nowszych wersjach Androida możemy oznaczyć plik jako zapisywany (tzn. że
            //zapisywanie trwa)
            imageDetails.put(MediaStore.Images.Media.IS_PENDING, 1);
        }
        //zapisanie rekordu w kolekcji obrazów
        Uri imageUri = resolver.insert(imageCollection, imageDetails);
        //zapisanie bitmapy do pliku, używamy deskryptora utworzonego na podstawie URI do
        //pliku
        try (ParcelFileDescriptor pfd =
                     resolver.openFileDescriptor(imageUri, "w", null);
             FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor())) {
            surface.getBitmap()
                    .compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //w nowszych wersjach Androida możemy oznaczyć plik jako zapisywany (tzn. że
        //zapisywanie się zakończyło)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageDetails.clear();
            imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0);
            resolver.update(imageUri, imageDetails, null, null);
        }
    }

}