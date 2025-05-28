package pl.pollub.android.rm31;

import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.net.ssl.HttpsURLConnection;

public class ShortTask {
    private static final String TAG = ShortTask.class.getSimpleName();

    public static class FileInfo {
        public int length;
        public String type;

        public FileInfo(int length, String type) {
            this.length = length;
            this.type = type;
        }

        @Override
        public String toString() {
            return "FileInfo{" +
                    "length=" + length +
                    ", type='" + type + '\'' +
                    '}';
        }
    }

    private final ExecutorService executorService;
    private final Handler mainThreadHandler;
    private Future<FileInfo> future;

    public ShortTask() {
        // utworzenie puli 2 wątków
        executorService = Executors.newFixedThreadPool(2);
        // utworzenie Handlera do wysyłania zadań do wątku UI
        mainThreadHandler = new Handler(Looper.getMainLooper());
    }

    // wywołanie zwrotne do przekazywania wyników
    public interface ResultCallback {
        void onSuccess(FileInfo result);

        void onError(Throwable throwable);
        // można dodać więcej metod np. do przekazywania informacji o postępie
    }

    // do metody trzeba przekazać wywołanie zwrotne do odbierania wyników i wymagane
    // parametry
    public Future<FileInfo> executeTask(ResultCallback callback, String param) {
        // anulowanie bieżącego zadania
        if (future != null && !future.isDone()) {
            future.cancel(true);
        }
        // tworzenie zadania, które odczyta wynik zadania wykonanego w tle i
        // przekaże go do głównego wątku
        Runnable completionTask = () -> {
            try {
                // czekanie na wynik (jeżeli wyniku nie ma blokuje wątek
                // wywołujący)
                FileInfo result = future.get();
                // przekazanie wyniku
                callback.onSuccess(result);
            } catch (CancellationException e) {
                System.out.println("canceled");
            } catch (Exception e) {
                System.out.println("error in task: " + e);
                callback.onError(e);
            }
        };
        // tworzenie zadania wykonywanego w tle (zadanie zwraca wynik)
        Callable<FileInfo> asyncTask = new Callable<FileInfo>() {
            @Override // wykonywane zadania mogą powodować wyjątki
            public FileInfo call() throws Exception {
                URLConnection connection = null;
                FileInfo fileInfo = null;
                try {
                    URL url = new URL(param);
                    connection = (URLConnection) url.openConnection();
//                    connection.setRequestMethod("GET");
                    System.out.println("Connection: " + connection);
                    // custom FileInfo class
                    fileInfo = new FileInfo(connection.getContentLength(),
                            connection.getContentType());
                } catch (Exception e) {
                    System.out.println("error insiiide task: " + e);
                }
                // wynik jest gotowy – wysyłamy do wykonania zadanie przekazujące wynik
                mainThreadHandler.post(completionTask);
                // zakończenie przyszłości (poprzez ustawienie wyniku)
                System.out.println("Task finished");
                return fileInfo;
            }
        };
        // wysłanie zadania do wykonania przez pulę wątków
        future = executorService.submit(asyncTask);
        // zwrócenie przyszłości (może być użyta do anulowania zadania)
        return future;
    }
    // tą metodę wywołać gdy aktywność lub fragment są niszczone
    public void shutdown() {
        // anulowanie wykonywanego zadania
        if (future != null && !future.isDone()) {
            future.cancel(true); // true – przerwanie zadania
        }
        // zamknięcie puli wątków
        executorService.shutdown();
    }
}