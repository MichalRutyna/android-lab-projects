package pl.pollub.android.rm31;


import static android.Manifest.permission.POST_NOTIFICATIONS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    ShortTask mTaskService;

    int file_size;
    String file_type;

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

        mTaskService = new ShortTask();
    }

    public void onGetInfoClick(View v) {
        ShortTask.ResultCallback callback = new ShortTask.ResultCallback() {
            @Override
            public void onSuccess(ShortTask.FileInfo result) {
                displayResponse(result);
                System.out.println("showing result");
            }

            @Override
            public void onError(Throwable throwable) {
                System.err.println("Error in get info, " + throwable.toString() + Arrays.toString(throwable.getStackTrace()));
                Toast.makeText(MainActivity.this, "Get info error", Toast.LENGTH_SHORT).show();
            }
        };
        mTaskService.executeTask(callback, getURLText());
    }

    public void onDownloadClick(View v) {
        System.out.println("download");
        callDownloadWithPerms();

    }

    private void callDownloadWithPerms() {
        boolean has_perms = true;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    WRITE_EXTERNAL_STORAGE)) {
                //musimy wyjaśnić użytkownikowi po co nam uprawnienie
                System.out.println("Wyjasnienie");
            has_perms = false;
            } else {
                //nie mamy uprawnień - prosimy o uprawnienie
                ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE},
                        1);
                has_perms = false;
            }
        }
        if (ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED) {
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                POST_NOTIFICATIONS)) {
            //musimy wyjaśnić użytkownikowi po co nam uprawnienie
            System.out.println("Wyjasnienie");
            has_perms = false;
        } else {
            //nie mamy uprawnień - prosimy o uprawnienie
            ActivityCompat.requestPermissions(this, new String[]{POST_NOTIFICATIONS},
                    1);
            has_perms = false;
        }
        if (has_perms) {
            download();
        }
    }

    private void download() {
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("download_url", getURLText());
        intent.putExtra("file_size", file_size);
        intent.putExtra("file_type", file_type);

        startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        String requiredPermission = getRequiredPermission();
        if (permissions.length > 0 && permissions[0].equals(requiredPermission) &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            callDownloadWithPerms();
        } else
            System.out.println("Nie przyznano uprawnienia");
    }

    private void displayResponse(ShortTask.FileInfo info) {
        TextView fileSize = findViewById(R.id.fileSizeValue);
        TextView fileType = findViewById(R.id.fileTypeValue);
        TextView bytesDownloaded = findViewById(R.id.bytesDownloadedValue);

        file_size = info.length;
        file_type = info.type;

        System.out.println("Setting value");
        fileSize.setText(String.valueOf(info.length));
        fileType.setText(info.type);
    }

    private String getURLText() {
        try {
            String val = ((EditText) findViewById(R.id.urlInput)).getText().toString();
            if (!val.startsWith("https://")) {
                return null;
            }
            return val;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        mTaskService.shutdown();
        super.onDestroy();
    }

    private static String getRequiredPermission() {
        String requiredPermission = "";
        //Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requiredPermission = POST_NOTIFICATIONS;
        }
        //Android wcześniejszy niż 10 (API 29)
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            requiredPermission = WRITE_EXTERNAL_STORAGE;
        }
        return requiredPermission;
    }
}