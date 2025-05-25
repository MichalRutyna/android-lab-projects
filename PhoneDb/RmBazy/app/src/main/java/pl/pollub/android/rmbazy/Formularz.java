package pl.pollub.android.rmbazy;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Formularz extends AppCompatActivity {

    boolean edit_mode = false;
    long edit_id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_formularz);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle extras = getIntent().getExtras();
        if (getIntent().hasExtra("id")) {
            edit_mode = true;
            edit_id = extras.getLong("id");
            EditText man = findViewById(R.id.editTextManufacturer);
            EditText model = findViewById(R.id.editTextModel);
            EditText vers = findViewById(R.id.editTextVersion);
            EditText webs = findViewById(R.id.editTextWebsite);
            man.setText(extras.getString("manufacturer"));
            model.setText(extras.getString("model"));
            vers.setText(extras.getString("version"));
            webs.setText(extras.getString("website"));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        if (edit_mode) {
            toolbar.setTitle("Edit a phone");
        }
        else {
            toolbar.setTitle("Add new phone");
        }
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void onClickCancel(View v)
    {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
    public void onClickSave(View v)
    {
        Intent returnIntent = new Intent();
        if (edit_mode) {
            returnIntent.putExtra("id", edit_id);
        }
            boolean error = false;

        EditText view = findViewById(R.id.editTextManufacturer);
        String value = view.getText().toString();
        if (value.isEmpty())
        {
            view.setError("This cannot be empty");
            error = true;
        }
         view = findViewById(R.id.editTextWebsite);
         value = view.getText().toString();
        if (value.isEmpty())
        {
            view.setError("This cannot be empty");
            error = true;
        }
         view = findViewById(R.id.editTextVersion);
         value = view.getText().toString();
        if (value.isEmpty())
        {
            view.setError("This cannot be empty");
            error = true;
        }
         view = findViewById(R.id.editTextModel);
         value = view.getText().toString();
        if (value.isEmpty())
        {
            view.setError("This cannot be empty");
            error = true;
        }

        if (error) {
            return;
        }
        returnIntent.putExtra("manufacturer", ((EditText) findViewById(R.id.editTextManufacturer)).getText().toString());
        returnIntent.putExtra("model", ((EditText) findViewById(R.id.editTextModel)).getText().toString());
        returnIntent.putExtra("version", ((EditText) findViewById(R.id.editTextVersion)).getText().toString());
        returnIntent.putExtra("website", ((EditText) findViewById(R.id.editTextWebsite)).getText().toString());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
    public void onClickWebsite(View v)
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(((EditText) findViewById(R.id.editTextWebsite)).getText().toString()));
        startActivity(browserIntent);
    }
}