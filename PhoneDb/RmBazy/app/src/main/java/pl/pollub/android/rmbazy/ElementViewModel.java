package pl.pollub.android.rmbazy;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ElementViewModel extends AndroidViewModel {
    private final ElementRepository mRepository;
    private final LiveData<List<Element>> mAllElements;

    public ElementViewModel(@NonNull Application application) {
        super(application);
        mRepository = new ElementRepository(application);
        mAllElements = mRepository.getAllElements();
    }

    public LiveData<List<Element>> getAllElements() {
        return mAllElements;
    }
    public void update(Element element) {
        mRepository.update(element);
        System.out.println("in viewmodel: " + element.getMarka());
    }
    public void deleteAll() {
        mRepository.deleteAll();
    }
    public void delete(Element element) {
        mRepository.delete(element);
    }
    public void insert(Element element) {
        mRepository.insert(element);
    }
}