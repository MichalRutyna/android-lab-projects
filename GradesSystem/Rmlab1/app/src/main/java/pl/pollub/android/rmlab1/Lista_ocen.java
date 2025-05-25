package pl.pollub.android.rmlab1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Lista_ocen extends AppCompatActivity {

    protected int liczba_ocen = 0;
    ArrayList<ModelOceny> mDane;

    AdapterOcen adapter;
    RecyclerView recyclerView;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("oceny", mDane);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mDane = savedInstanceState.getParcelableArrayList("oceny");
        System.out.println(mDane.get(0).ocena);
        AdapterOcen adap = new
                AdapterOcen(this, mDane);

        recyclerView.setAdapter(adap);
        recyclerView.invalidate();
        System.out.println("Restored");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_ocen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            liczba_ocen = extras.getInt("liczba_ocen");
        }

        mDane = new ArrayList<ModelOceny>();
        String[] nazwyPrzedmiotow =
                getResources().getStringArray(R.array.nazwy_przedmiotow);

        for (int i = 0; i < liczba_ocen; i++) {
            mDane.add(new ModelOceny(nazwyPrzedmiotow[i], 2));
        }

        adapter = new
                AdapterOcen(this, mDane);

        recyclerView = findViewById(R.id.oceny_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        System.out.println("Created");


    }

    float liczSrednia() {
        int suma = 0;
        for (ModelOceny ocena : this.mDane) {
            suma += ocena.ocena;
        }
        return (float) suma / liczba_ocen;
    }

    public void sredniaOnClick(View v) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("mean", liczSrednia());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}