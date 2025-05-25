package pl.pollub.android.rmbazy;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ElementDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(Element element);

    @Update
    void update(Element element);

    @Query("DELETE FROM telefon")
    void deleteAll();

    @Delete
    void delete(Element phone);

    @Query("SELECT * FROM telefon ORDER BY model ASC")
    LiveData<List<Element>> getAlphabetizedElements();
}