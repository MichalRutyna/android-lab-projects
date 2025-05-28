package pl.pollub.android.rm31;

import static android.app.Service.START_NOT_STICKY;
import static android.content.Context.NOTIFICATION_SERVICE;
import static android.nfc.tech.MifareClassic.BLOCK_SIZE;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MyService extends Service {
    public final static String TAG = MyService.class.getSimpleName();
    public final static String PACKAGE_NAME = "com.example.lab3_tlo.service";
    public final static String PROGRESS_INFO = PACKAGE_NAME + ".progress_info";
    private static final String CHANNEL_ID = PACKAGE_NAME + ".service_channel";
    private static final int NOTIFICATION_ID_PROGRESS = 1;
    private static final int NOTIFICATION_ID_COMPLETE = 2;
    private NotificationManager notificationManager;
    private HandlerThread handlerThread;
    private Handler handler;
    //metoda odpowiedzialna za zwrócenie Bindera, w przypadku usług niezwiązanych
    //(unbounded) musi zwracać null
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return null;
    }

    String mfileName;
    String mmimeType;
    int mfileSize;

    boolean errored = false;
    int bytesDownloaded = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        //dodatkowy wątek do wykonywania zadań asynchronicznie (usługa działa w głównym
        //wątku aplikacji)
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        handlerThread = new HandlerThread("DownloadService");
        handlerThread.start();
        //utworzenie handlera umożliwiającego wykonanie zadań w osobnym wątku
        handler = new Handler(handlerThread.getLooper());

    }
    //wywoływana gdy usługa zostanie uruchomiona za pomocą startService(intent)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //przygotowanie kanału powiadomień (usługa musi wyświetlać powiadomienie)
        notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        prepareNotificationChannel();

        String url = intent.getStringExtra("download_url");
        String type = intent.getStringExtra("file_type");
        mmimeType = type;
        int size = intent.getIntExtra("file_size", 1);
        mfileSize = size;
        String file = "lastest_file";
        mfileName = file;

        ParamsType params = new ParamsType(url, file, size, type);

        //wykonanie zadania za pomocą handlera
        handler.post(() -> executeTask(params));
        return START_NOT_STICKY;
    }
    //zadanie do wykonania
    protected void executeTask(ParamsType params) {
        //usuwanie starych powiadomień
        notificationManager.cancel(NOTIFICATION_ID_PROGRESS);
        //przejście usługi na pierwszy plan
        //Android 10 (API Level 29)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID_PROGRESS, getNotification(),
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        } else {
            startForeground(NOTIFICATION_ID_PROGRESS, getNotification());
        }
        //wykonanie zadania
        HttpsURLConnection connection = null;
        Uri fileUri = null;
        OutputStream fileOutputStream = null;
        ContentResolver resolver = null;

        try {
            //nawiązanie połączenia
            connection = null;
            URL url = new URL(params.download_url);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            String fileName = params.file_name;
            String mimeType = mmimeType;

            //jeżeli plik istnieje – skasować
            ContentValues values = new ContentValues();
            //Android 10 (API Level 29) - używa scoped storage, mamy dostęp do katalogu Downloads
            //ale trzeba dodać plik do bazy plików
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //jeżeli plik istnieje to go usuwamy
                Uri tmpUri = getFileUri(fileName);
                if (tmpUri != null) {
                    getContentResolver().delete(tmpUri, null, null);
                }
                //tworzymy nowy wpis w bazie plików
                resolver = getContentResolver();
                values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                values.put(MediaStore.Downloads.MIME_TYPE, mimeType);
                values.put(MediaStore.Downloads.IS_PENDING, 1);
                values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                fileUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                fileOutputStream = resolver.openOutputStream(fileUri);
            } else { //w przypadku starszych wersji używamy standardowych ścieżek
                File outFile = new File(Environment.getExternalStorageDirectory() + File.separator
                        + Environment.DIRECTORY_DOWNLOADS + File.separator
                        + fileName);
                if (outFile.exists()) outFile.delete();
                fileOutputStream = new FileOutputStream(outFile);
            }
            //aktualizacja powiadomienia
            sendMessagesAndUpdateNotification();
            //odczytywanie danych ze strumienia sieciowego i zapis do strumienia
            //związanego z plikiem
            DataInputStream reader = new DataInputStream(connection.getInputStream());
            byte buffer[] = new byte[BLOCK_SIZE];
            int downloaded = reader.read(buffer, 0, BLOCK_SIZE);
            while (downloaded != -1) {
                fileOutputStream.write(buffer, 0, downloaded);
                bytesDownloaded += downloaded;
                //aktualizacja powiadomienia
                sendMessagesAndUpdateNotification();
                downloaded = reader.read(buffer, 0, BLOCK_SIZE);
            }
            values.put(MediaStore.Downloads.IS_PENDING, 0);
            getContentResolver().update(fileUri, values, null, null);
            //usługa schodzi z pierwszego planu (aby można było usunąć powiadomienie)
            Log.d(TAG, "downloadFile - going to background");
            stopForeground(STOP_FOREGROUND_REMOVE);
            //aktualizacja powiadomienia
            sendMessagesAndUpdateNotification();
        } catch (Exception e) {
            errored = true;
            e.printStackTrace();
            //jeżeli wystąpił błąd kasujemy plik
            Uri tmpUri = getFileUri(mfileName);
            if (tmpUri != null) {
                getContentResolver().delete(tmpUri, null, null);
            }
            //usługa schodzi z pierwszego planu (aby powiadomienie zniknęło)
            Log.d(TAG, "downloadFile - going to background");
            stopForeground(STOP_FOREGROUND_REMOVE);
            //aktualizacja powiadomień i statusu postępu
            sendMessagesAndUpdateNotification();
        } finally {
            //zamykanie strumieni i zakończenie połączenia
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//            if (networkInputStream != null) {
//                try {
//                    networkInputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
            if (connection != null) connection.disconnect();
        }
    }

    private void prepareNotificationChannel() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //tylko na Androidzie 8.0 i późniejszych
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //tworzymy kanał
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID,
                    getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_LOW);
            mChannel.setDescription(getString(R.string.notification_channel_description));
            notificationManager.createNotificationChannel(mChannel);
        }
    }

    //tworzenie powiadomienia
    private Notification getNotification() {
        //tworzenie powiadomienia
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setContentTitle(getString(R.string.notification_title_downloading))
                .setContentText(getString(R.string.notification_text_downloading))
                .setProgress(100, percentage(), false)
                .setSmallIcon(R.drawable.big_chungus)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_LOW); // dla Android 7.1-
        //w zależności o stanu pobierania ustawiamy różny tytuł
        if (!errored) {
            builder.setContentTitle(getString(R.string.notification_text_finished));
        }
        else {
            builder.setContentTitle(getString(R.string.notification_text_finished_error));
            builder.setAutoCancel(true);
        }
        if (percentage() == 100) {
            //automatyczne
            //zamykanie powiadomienia
            builder.setAutoCancel(true);
        }
        else {
            //jeżeli pobieranie trwa to powiadomienie ma być ustawione jako trwające
            builder.setOngoing(true);
        }
        return builder.build();
    }

    private int percentage() {
        return bytesDownloaded / mfileSize;
    }

    //aktualizacja powiadomień
    void sendMessagesAndUpdateNotification() {
        //WAŻNE!!! nie należy wysyłać powiadomień jeżeli postęp się nie zmienił lub zmienił
        //się nieznacznie. Różne wersje Androida optymalizują wyświetlanie powiadomień
        //(jeżeli wysyłamy powiadomienia za często nie wszystkie zostaną wyświetlone)
        //sprawdzić jaki jest stan pobierania
        if (percentage() < 100) {//wyświetlić powiadomienie
            notificationManager.notify(NOTIFICATION_ID_PROGRESS, getNotification());
        }
        else { //jeżeli pobieranie się zakończyło zalecane jest użycie innego identyfikatora
            //powiadomienia wtedy na pewno się wyświetli
            notificationManager.notify(NOTIFICATION_ID_COMPLETE, getNotification());
        }
    }

    //Android 10 (API Level 29) - pobieranie URI istniejącego pliku z bazy plików
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private Uri getFileUri(String fileName) {
        ContentResolver resolver = getContentResolver();
        String[] projection = {MediaStore.Downloads._ID};
        String selection = MediaStore.Downloads.DISPLAY_NAME + " = ?";
        String[] selectionArgs = {fileName};
        try (Cursor cursor = resolver.query(MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                projection, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID);
                long id = cursor.getLong(idColumn);
                return Uri.withAppendedPath(MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                        String.valueOf(id));
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
}
}