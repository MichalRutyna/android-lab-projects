package pl.pollub.android.rmbazy;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Element.class}, version = 4, exportSchema = false)
public abstract class ElementRoomDatabase extends RoomDatabase {
    public abstract ElementDao elementDao();

    private static volatile ElementRoomDatabase INSTANCE;
    static ElementRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ElementRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    ElementRoomDatabase.class, "nazwa_bazy")
                            .addCallback(sRoomDatabaseCallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                ElementDao dao = INSTANCE.elementDao();
                dao.insert(new Element("Google", "Pixel 9", "32.0", "www.pixel9.com"));
                dao.insert(new Element("Google", "Pixel 9 Pro", "33.0f", "www.pixel9p.com"));
                dao.insert(new Element("Google", "Pixel 9 Pro XL", "34.0f", "www.pixel9px.com"));
                dao.insert(new Element("Google", "Pixel 9 Pro XL Super", "35.0f", "www.pixel9pxs.com"));
                dao.insert(new Element("Google", "Pixel 9 Pro XL Super Mega", "36.0f", "www.pixel9pxsm.com"));
                dao.insert(new Element("Google", "Pixel 9a", "33.5f", "www.pixel9a.com"));
            });
        }
    };
}