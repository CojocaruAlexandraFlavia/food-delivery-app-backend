package com.example.fooddelivery.repository;

import com.example.fooddelivery.model.History;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRepository  extends JpaRepository<History, Long> {

    @Modifying(clearAutomatically = true)
    @Query("DELETE from History q WHERE q.id = ?1")
    void deleteById(@NotNull Long id);
}
