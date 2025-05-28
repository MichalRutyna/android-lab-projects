package pl.pollub.android.rm31;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ProgressEvent implements Parcelable {
    //statusy pobierania
    public static final int OK = 0;
    public static final int IN_PROGRESS = 1;
    public static final int ERROR = 2;
    public int progress;
    public int total;
    public int result;

    //inne potrzebne konstruktory...
    //interfejs Parcelable
    protected ProgressEvent(Parcel in) {
        progress = in.readInt();
        total = in.readInt();
        result = in.readInt();
    }

    public ProgressEvent(int progress, int total, int result) {
        this.progress = progress;
        this.total = total;
        this.result = result;
    }

    public static final Creator<ProgressEvent> CREATOR = new Creator<ProgressEvent>() {
        @Override
        public ProgressEvent createFromParcel(Parcel in) {
            return new ProgressEvent(in);
        }

        @Override
        public ProgressEvent[] newArray(int size) {
            return new ProgressEvent[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(progress);
        dest.writeInt(total);
        dest.writeInt(result);
    }
}