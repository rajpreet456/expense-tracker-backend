package com.example.expense_tracker.repository;

import com.example.expense_tracker.entity.Expense;
import com.example.expense_tracker.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Page<Expense> findByUser(User user, Pageable pageable);
    Page<Expense> findByUserAndCategory(User user, String category, Pageable pageable);
    Page<Expense> findByUserUsername(String username, Pageable pageable);
    Page<Expense> findByUserUsernameAndCategory(String username, String category, Pageable pageable);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user = :user")
    Double getTotalExpensesByUser(User user);

    @Query("SELECT e.category, SUM(e.amount) FROM Expense e WHERE e.user = :user GROUP BY e.category")
    List<Object[]> getCategoryWiseExpenses(User user);

    @Query("SELECT YEAR(e.date), MONTH(e.date), SUM(e.amount) " +
            "FROM Expense e WHERE e.user = :user " +
            "GROUP BY YEAR(e.date), MONTH(e.date) " +
            "ORDER BY YEAR(e.date) DESC, MONTH(e.date) DESC")
    List<Object[]> getMonthlyExpenses(User user);
}