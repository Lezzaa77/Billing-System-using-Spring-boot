package com.billing.repository;

import com.billing.model.Item;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemRepository extends JpaRepository<Item, Long> {

    boolean existsByItemCode(String itemCode);

    boolean existsByItemCodeAndIdNot(String itemCode, Long id);

    /** Items that can still be sold. */
    List<Item> findByQuantityGreaterThanOrderByNameAsc(int quantity);

    /** Loads the item and locks its row, so two sales cannot oversell the same stock. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Item i where i.id = :id")
    Optional<Item> findByIdForUpdate(@Param("id") Long id);

    /** Existing category names (for the suggestion list on the add / update form). */
    @Query("select distinct i.category from Item i where i.category is not null and i.category <> '' order by i.category")
    List<String> findCategoryNames();
}
