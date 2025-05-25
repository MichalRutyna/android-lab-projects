package pl.pollub.android.rmlab1;

import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    boolean imieCorr = false;
    boolean nazwCorr = false;
    boolean liczbCorr = false;
    boolean zaliczenie = false;
    float mean = 0f;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Button bt = findViewById(R.id.oceny_button);
        outState.putInt("przycisk", bt.getVisibility());
        outState.putBoolean("imie", imieCorr);
        outState.putBoolean("nazw", nazwCorr);
        outState.putBoolean("liczb", liczbCorr);
        outState.putBoolean("zaliczenie", zaliczenie);
        outState.putFloat("mean", mean);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Button bt = findViewById(R.id.oceny_button);
        bt.setVisibility(savedInstanceState.getInt("przycisk"));
        imieCorr = savedInstanceState.getBoolean("imie");
        nazwCorr = savedInstanceState.getBoolean("nazw");
        liczbCorr = savedInstanceState.getBoolean("liczb");
        zaliczenie = savedInstanceState.getBoolean("zaliczenie");
        mean = savedInstanceState.getFloat("mean");
        showMean(mean);
    }

    public void onClickOceny(View v) {
        Intent intencja = new Intent(this,
                Lista_ocen.class);
        EditText liczbaEdit = findViewById(R.id.editTextLiczbaocen);
        intencja.putExtra("liczba_ocen", Integer.parseInt(liczbaEdit.getText().toString()));
        startActivityForResult(intencja, 1);
    }

    public void onClickSuper(View v) {
        if (zaliczenie) {
            Toast.makeText(MainActivity.this,
                            "Gratulacje! Otrzymujesz zaliczenie!",
                            Toast.LENGTH_SHORT)
                    .show();
        }
        else {
            Toast.makeText(MainActivity.this,
                            "Wysyłam podanie o zaliczenie warunkowe",
                            Toast.LENGTH_SHORT)
                    .show();
        }
        this.finishAffinity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                Float result=data.getFloatExtra("mean", 0);
                showMean(result);
                mean = result;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.lab12);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        EditText imieEdit = findViewById(R.id.editTextImie);
        imieEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (imieEdit.getText().toString().isEmpty())
                    {
                        Toast.makeText(MainActivity.this,
                                        "Imie nie moze byc puste",
                                        Toast.LENGTH_SHORT)
                                .show();
                        imieEdit.setError("Pole nie moze byc puste");
                        imieCorr = false;
                        MainActivity.this.checkData();
                    }
                    else {
                        imieCorr = true;
                        MainActivity.this.checkData();
                    }
                }
            }
        });

        EditText nazwEdit = findViewById(R.id.editTextNazwisko);
        nazwEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (nazwEdit.getText().toString().isEmpty())
                    {
                        Toast.makeText(MainActivity.this,
                                        "Nazwisko nie moze byc puste",
                                        Toast.LENGTH_SHORT)
                                .show();
                        nazwEdit.setError("Pole nie moze byc puste");
                        nazwCorr = false;
                        MainActivity.this.checkData();
                    }
                    else {
                        nazwCorr = true;
                        MainActivity.this.checkData();
                    }
                }

            }
        });

        EditText liczbaEdit = findViewById(R.id.editTextLiczbaocen);
        liczbaEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b)
                {
                    int val;
                    try
                    {
                        val = Integer.parseInt(liczbaEdit.getText().toString());
                    }
                    catch (Exception e) {
                        Toast.makeText(MainActivity.this,
                                        "Nieprawidlowa wartosc",
                                        Toast.LENGTH_SHORT)
                                .show();
                        liczbaEdit.setError("Nieprawidlowa wartosc");
                        liczbCorr = false;
                        MainActivity.this.checkData();
                        return;
                    }
                    if (val < 5 || val > 15) {
                        Toast.makeText(MainActivity.this,
                                        "Liczba ocen musi byc w przedziale 5-15",
                                        Toast.LENGTH_SHORT)
                                .show();
                        liczbaEdit.setError("Nieprawidlowy przedzial");
                        liczbCorr = false;
                        MainActivity.this.checkData();
                    }
                    else {
                        liczbCorr = true;
                         MainActivity.this.checkData();
                    }
                }

            }
        });
    }

    protected void checkData() {
        Button oceny = findViewById(R.id.oceny_button);
        if (imieCorr && nazwCorr && liczbCorr) {
            oceny.setVisibility(View.VISIBLE);
            Toast.makeText(MainActivity.this,
                            "Ale super!",
                            Toast.LENGTH_SHORT)
                    .show();
        }
        else {
            oceny.setVisibility(View.INVISIBLE);
        }
    }

    protected void showMean(Float mean) {
        Button przycisk = findViewById(R.id.przycisk_srednia);
        TextView tekst = findViewById(R.id.sredniaText);

        tekst.setText(getString(R.string.your_mean) + (Math.round(mean * 100) / 100f));
        if (mean - 3 < 0) {
            // slabo
            przycisk.setText(getString(R.string.next_time));
        }
        else {
            // fajnie
            przycisk.setText(getString(R.string.nice));
        }

        przycisk.setVisibility(View.VISIBLE);
        tekst.setVisibility(View.VISIBLE);
    }


}