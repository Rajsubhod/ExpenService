package com.rajsubhod.expenseservice.repository;

import com.rajsubhod.expenseservice.entity.Expense;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, String> {
    List<Expense> findByUserId(String userId);
    Page<Expense> findByUserId(String userId, Pageable pageable);
}
