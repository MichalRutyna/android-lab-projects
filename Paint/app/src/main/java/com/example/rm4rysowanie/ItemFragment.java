package com.example.rm4rysowanie;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rm4rysowanie.databinding.FragmentImageBinding;
import com.example.rm4rysowanie.databinding.FragmentItemBinding;
import com.example.rm4rysowanie.databinding.FragmentItemListBinding;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 */
public class ItemFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";

    private static RecyclerView mRecyclerView;
    private static MyItemRecyclerViewAdapter mRecyclerAdapter;

    private static ArrayList<Image> mImageList;
    private int mColumnCount = 1;
    private @NonNull FragmentItemListBinding binding;

    public ItemClickedListener reciever;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    public static ItemFragment newInstance(int columnCount) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        mImageList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentItemListBinding.inflate(inflater, container, false);
        RecyclerView view = binding.getRoot();

        reciever = (ItemClickedListener) getActivity();

        // Set the adapter
        Context context = view.getContext();
        mRecyclerView = view;
        if (mColumnCount <= 1) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        System.out.println("initttt");
        mRecyclerAdapter = new MyItemRecyclerViewAdapter(new ArrayList<>(), (image) -> {
            System.out.println("Clicked in fragment");
            reciever.itemClicked(image);
        });
        mRecyclerView.setAdapter(mRecyclerAdapter);

        getFileList();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        //konfiguracja istniejącego widoku np. wyświetlenie danych, konfiguracja
        //obsługi zdarzeń…
        //view to główny element layoutu XML
    }

    private void getFileList() {
        Uri collection;
        String[] projection; //kolumny do pobrania
        String selection; //warunek wybierający wiersze
        String[] selectionArgs; //argumenty do warunku wstawiane w miejsce "?"
        //budowanie zapytania do MediaStore (sposób zależy od wersji Androida)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection =
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
            projection = new String[]{
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.OWNER_PACKAGE_NAME
            };
            //Od Androida 10 dostęp bez uprawnień możliwy jest tylko do plików, które
            //aplikacja zapisała samodzielnie (nazwa pakietu zapisywana jest
            //automatycznie)
            selection = MediaStore.Images.Media.OWNER_PACKAGE_NAME + " = ?";
            selectionArgs = new String[]{getContext().getPackageName()};
        } else {
            collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            projection = new String[]{
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
            };
            selection = null;
            selectionArgs = null;
        }
        String sortOrder = MediaStore.Images.Media.DISPLAY_NAME + " ASC";
        //
        try (Cursor cursor = getContext().getContentResolver().query(
                collection,
                projection,
                selection,
                selectionArgs,
                sortOrder
        )) {
            //zapamiętujemy znalezione na podstawie nazw indeksy kolumn
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            int packageColumn = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                packageColumn =
                        cursor.getColumnIndexOrThrow(
                                MediaStore.Images.Media.OWNER_PACKAGE_NAME);
            }
            //czyścimy listę, w której zapiszemy dane o rysunkach
            mImageList.clear();
            //przeglądamy zwrócony kursor
            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                String packageName = cursor.getString(packageColumn);
                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                Image image = new Image(id, name);
                mImageList.add(image);
            }
            //ustawiamy listę w adapterze (i w ten sposób wyświetlamy listę)
            System.out.println(mImageList);
            mRecyclerAdapter.setImageList(mImageList);
        }
    }

    public interface ItemClickedListener {
        public void itemClicked(Image item);
    }
}