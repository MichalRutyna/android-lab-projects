package pl.pollub.android.rmbazy;

import android.app.Application;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ElementRepository {
    private ElementDao mElementDao;
    private LiveData<List<Element>> mAllElements;

    ElementRepository(Application application) {
        ElementRoomDatabase elementRoomDatabase =
                ElementRoomDatabase.getDatabase(application);

        mElementDao = elementRoomDatabase.elementDao();
        mAllElements = mElementDao.getAlphabetizedElements();
    }
    LiveData<List<Element>> getAllElements() {
        return mAllElements;
    }

    void deleteAll() {
        ElementRoomDatabase.databaseWriteExecutor.execute(() -> {
            mElementDao.deleteAll();
        });
    }

    void delete(Element element) {
        ElementRoomDatabase.databaseWriteExecutor.execute(() -> {
            mElementDao.delete(element);
        });
    }

    void insert(Element element) {
        ElementRoomDatabase.databaseWriteExecutor.execute(() -> {
            mElementDao.insert(element);
        });
    }

    void update(Element element) {
        ElementRoomDatabase.databaseWriteExecutor.execute(() -> {
            mElementDao.update(element);
        });
    }

}