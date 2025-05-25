package pl.pollub.android.rmlab1;

import android.os.Parcel;
import android.os.Parcelable;

public class ModelOceny implements Parcelable {
    String nazwa;
    float ocena;

    ModelOceny(String nazwa, int ocena)
    {
        this.nazwa = nazwa;
        this.ocena = ocena;
    }

    protected ModelOceny(Parcel in) {
        nazwa = in.readString();
        ocena = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nazwa);
        dest.writeFloat(ocena);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ModelOceny> CREATOR = new Creator<ModelOceny>() {
        @Override
        public ModelOceny createFromParcel(Parcel in) {
            return new ModelOceny(in);
        }

        @Override
        public ModelOceny[] newArray(int size) {
            return new ModelOceny[size];
        }
    };
}
