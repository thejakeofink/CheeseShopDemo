package com.vikingsen.cheesedemo.model.data.cheese;


import android.support.annotation.NonNull;

import com.vikingsen.cheesedemo.model.database.cheese.Cheese;
import com.vikingsen.cheesedemo.model.database.cheese.CheeseManager;
import com.vikingsen.cheesedemo.model.webservice.dto.CheeseDto;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.temporal.ChronoUnit;
import org.threeten.bp.temporal.TemporalUnit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.Maybe;
import io.reactivex.Single;

@Singleton
class CheeseLocalDataSource {

    private static final long CACHE_VALID_AMOUNT = 1L;
    private static final TemporalUnit CACHE_VALID_UNIT = ChronoUnit.HOURS;

    private final CheeseManager cheeseManager;

    @Inject
    CheeseLocalDataSource(CheeseManager cheeseManager) {
        this.cheeseManager = cheeseManager;
    }

    Single<List<Cheese>> getCheeses() {
        return RxJavaInterop.toV2Observable(cheeseManager.findAllCheesesRx()).single(Collections.emptyList());
    }

    Maybe<Cheese> getCheese(Long cheeseId) {
        return Maybe.create(emitter -> {
            try {
                Cheese cheese = cheeseManager.findByCheeseId(cheeseId);
                if (cheese != null) {
                    emitter.onSuccess(cheese);
                } else {
                    emitter.onComplete();
                }
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    boolean isCheeseStale() {
        LocalDateTime cacheExpiration = LocalDateTime.now().minus(CACHE_VALID_AMOUNT, CACHE_VALID_UNIT);

        // GOTCHA - DOUBLE CHECK THAT THE CHECK MATCHES THE METHOD NAME (isBefore vs isAfter)
        return cheeseManager.findOldestCacheDate().isBefore(cacheExpiration);
    }

    boolean isCheeseStale(long cheeseId) {
        LocalDateTime cacheExpiration = LocalDateTime.now().minus(CACHE_VALID_AMOUNT, CACHE_VALID_UNIT);
        return cheeseManager.findCacheDate(cheeseId).isBefore(cacheExpiration);
    }

    List<Cheese> saveCheeses(@NonNull List<CheeseDto> cheeseDtos) {
        LocalDateTime cached = LocalDateTime.now();
        List<Cheese> cheeses = new ArrayList<>(cheeseDtos.size());
        cheeseManager.beginTransaction();
        boolean commit = false;
        try {
            if (!cheeses.isEmpty()) {
                cheeseManager.deleteAll();
            }
            for (CheeseDto dto : cheeseDtos) {
                Cheese cheese = new Cheese();
                cheese.setId(dto.getId());
                cheese.setName(dto.getName());
                cheese.setDescription(dto.getDescription());
                cheese.setImageUrl(dto.getImage());
                cheese.setCached(cached);
                cheeseManager.save(cheese);
                cheeses.add(cheese);
            }
            commit = true;
        }finally {
            cheeseManager.endTransaction(commit);
        }
        return cheeses;
    }

    Cheese saveCheese(@NonNull CheeseDto dto) {
        Cheese cheese = new Cheese();
        cheese.setId(dto.getId());
        cheese.setName(dto.getName());
        cheese.setDescription(dto.getDescription());
        cheese.setImageUrl(dto.getImage());
        cheese.setCached(LocalDateTime.now());
        cheeseManager.save(cheese);
        return cheese;
    }
}
