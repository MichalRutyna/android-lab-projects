package com.example.rm4rysowanie;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rm4rysowanie.databinding.FragmentImageBinding;


public class ImageFragment extends Fragment {
    private static final String TAG = ItemFragment.class.getSimpleName();
    private FragmentImageBinding binding;
    public ImageFragment() {
        //publiczny bezparametrowy konstruktor jest wymagany
    }

    public void setImage(Image image) {
        Uri imageUri = Uri.withAppendedPath(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Long.toString(image.id));
        binding.imageView.setImageURI(imageUri);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //tworzenie widoków (komponentów) na podstawie XMLa
        binding = FragmentImageBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
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
}
