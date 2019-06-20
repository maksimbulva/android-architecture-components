package com.example.android.persistence.db;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.android.persistence.db.entity.ProductEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;

public class FakeDataSource {

    public LiveData<List<ProductEntity>> loadAllProducts() {
        return new ProductEntries();
    }

    private class ProductEntries extends LiveData<List<ProductEntity>> {

        private Disposable ongoingLoad = null;

        @Override
        protected void onActive() {
            super.onActive();
            if (ongoingLoad == null) {
                ongoingLoad = fakeLongLoad().subscribe(this::postValue);
            }
        }

        @Override
        protected void onInactive() {
            if (ongoingLoad != null) {
                ongoingLoad.dispose();
                ongoingLoad = null;
            }
            super.onInactive();
        }

        private Flowable<List<ProductEntity>> fakeLongLoad() {
            return Flowable.interval(10, TimeUnit.SECONDS)
                    .take(1)
                    .map(tick -> {
                        final ProductEntity e = new ProductEntity(
                                0,
                                "My test name",
                                "My test description",
                                100);
                        final List<ProductEntity> result = new ArrayList<>();
                        result.add(e);
                        return result;
                    })
                    .doOnSubscribe(s -> Log.i("MyTest", "Begin data loading"))
                    .doOnTerminate(() -> Log.i("MyTest", "Terminated data loading"));
        }
    }
}
