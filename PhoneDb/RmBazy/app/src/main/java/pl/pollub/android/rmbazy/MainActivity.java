package pl.pollub.android.rmbazy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.Objects;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerViewBinding;
    private ElementViewModel mElementViewModel;
    private ElementListAdapter mAdapter;
    private ElementViewModel mPhoneViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.menuBar);
        toolbar.setTitle("Phone DB");
//        setSupportActionBar(toolbar);
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mRecyclerViewBinding = findViewById(R.id.telefonyRecycler);

        //ustawienie adaptera na liście, ustawienie layoutu elementów listy
        mAdapter = new ElementListAdapter(this, this::onRowClick);
        mRecyclerViewBinding.setAdapter(mAdapter);
        mRecyclerViewBinding.setLayoutManager(new LinearLayoutManager(this));
        //mBinding.addItemDecoration(new DividerItemDecoration(this,
        //        DividerItemDecoration.VERTICAL));

        //odczytanie modelu widoku z dostawcy
        mPhoneViewModel = new ViewModelProvider(this).get(ElementViewModel.class);

        //gdy zmienią się dane w obiekcie live data w modelu widoku zostanie
        //wywołana metoda ustawiająca zmienioną listę elementów w adapterze
        mPhoneViewModel.getAllElements().observe(this, elements -> {
            mAdapter.setElementList(elements);
        });

        ((MaterialToolbar) findViewById(R.id.menuBar)).setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.skasuj_all) {
                System.out.println("skasuj");
                mPhoneViewModel.deleteAll();
                return true;
            }
            return false;
        });

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int adapterPosition = viewHolder.getAdapterPosition();
                //kasowanie „przeciągniętego” elementu
                mPhoneViewModel.delete(mPhoneViewModel.getAllElements().getValue().get(adapterPosition));
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRecyclerViewBinding);
    }

    public void onClickAdd(View view) {
        Intent intencja = new Intent(this,
                Formularz.class);
        startActivityForResult(intencja, 1);
    }

    public void onRowClick(Element element) {
        Intent intencja = new Intent(this,
                Formularz.class);
        intencja.putExtra("id", element.getId());
        intencja.putExtra("manufacturer", element.getMarka());
        intencja.putExtra("model", element.getModel());
        intencja.putExtra("version", element.getAndroid_version());
        intencja.putExtra("website", element.getWebsite());
        System.out.println(element);
        startActivityForResult(intencja, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                int id = data.getIntExtra("id", -1);
                String manufacturer = data.getStringExtra("manufacturer");
                String model = data.getStringExtra("model");
                String website = data.getStringExtra("website");
                String version = data.getStringExtra("version");
                Element new_element = new Element(manufacturer, model, version, website);
                mPhoneViewModel.insert(new_element);
            }
        }

        else if (requestCode == 2) {
            if(resultCode == Activity.RESULT_OK){
                long id = data.getLongExtra("id", -1);
                String manufacturer = data.getStringExtra("manufacturer");
                String model = data.getStringExtra("model");
                String website = data.getStringExtra("website");
                String version = data.getStringExtra("version");
                System.out.println(id);
                Element updated_element = Objects.requireNonNull(mPhoneViewModel.getAllElements().getValue()).stream()
                        .filter(a -> Objects.equals(a.getId(), id))
                        .collect(Collectors.toList()).get(0);
                updated_element.setMarka(manufacturer);
                updated_element.setModel(model);
                updated_element.setWebsite(website);
                updated_element.setAndroid_version(version);
                mPhoneViewModel.update(updated_element);
                System.out.println("onresult: " + updated_element.getMarka());
            }
        }
    }
}