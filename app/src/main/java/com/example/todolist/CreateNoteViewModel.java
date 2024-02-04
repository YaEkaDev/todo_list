package com.example.todolist;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CreateNoteViewModel extends AndroidViewModel {
    private NotesDao notesDao;
    private MutableLiveData<Boolean> shoudCloseScreen = new MutableLiveData<>();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();//коллекция всех подписок

    public LiveData<Boolean> getShoudCloseScreen() {
        return shoudCloseScreen;
    }

    public CreateNoteViewModel(@NonNull Application application) {
        super(application);
        notesDao = NoteDatabase.getInstance(application).notesDao();

    }

    public void saveNote(Note note) {
        //подписка на объект Complitable(смотри в Dao) при добавление заметки в базу
        //когда завершит работу add, будет вызван метод run, в котором описано,
        // что делать после успешного завершения
        Disposable disposable = notesDao.add(note) //создание объекта, у которой можно управлять ЖЦ подписки
                .subscribeOn(Schedulers.io()) //выполнение метода add будет проходить в фоновом потоке
                .observeOn(AndroidSchedulers.mainThread()) //выполнение действий ниже будет проходить в главном потоке
                .subscribe(new Action() {//подписка
                    @Override
                    public void run() throws Throwable {
                        shoudCloseScreen.setValue(true);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        Log.d("MyLog", "Заметка не сохранилась");
                    }
                });
        compositeDisposable.add(disposable);

    }

    @Override
    protected void onCleared() { //метод VM, вызываемый при уничтожении VM
        super.onCleared();
        compositeDisposable.dispose(); //отмена всех подписок
    }
}
