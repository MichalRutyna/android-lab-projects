package pl.pollub.android.rmbazy;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "telefon")
public class Element {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @NonNull
    @ColumnInfo(name = "marka")
    private String marka;

    @NonNull
    @ColumnInfo(name = "model")
    private String model;

    @NonNull
    @ColumnInfo(name = "android_version")
    private String android_version;

    @NonNull
    @ColumnInfo(name = "website")
    private String website;

    public Element(@NonNull String marka, @NonNull String model, @NonNull String android_version, @NonNull String website) {
        this.marka = marka;
        this.model = model;
        this.android_version = android_version;
        this.website = website;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getMarka() {
        return marka;
    }

    public void setMarka(@NonNull String marka) {
        this.marka = marka;
    }

    @NonNull
    public String getModel() {
        return model;
    }

    public void setModel(@NonNull String model) {
        this.model = model;
    }

    @NonNull
    public String getAndroid_version() {
        return android_version;
    }

    public void setAndroid_version(@NonNull String android_version) {
        this.android_version = android_version;
    }

    @NonNull
    public String getWebsite() {
        return website;
    }

    public void setWebsite(@NonNull String website) {
        this.website = website;
    }
}