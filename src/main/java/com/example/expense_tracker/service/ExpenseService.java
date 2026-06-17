package com.example.expense_tracker.service;

import com.example.expense_tracker.entity.Expense;
import com.example.expense_tracker.entity.User;
import com.example.expense_tracker.repository.ExpenseRepository;
import com.example.expense_tracker.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    // ADD EXPENSE
    public Expense addExpense(Expense expense, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        expense.setUser(user);

        return expenseRepository.save(expense);
    }

    // GET EXPENSES WITH PAGINATION
    public Page<Expense> getUserExpenses(
            String username,
            String category,
            int page,
            int size,
            String sortBy,
            String sortDir) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        //Step 1 Create Sort object
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        //Step 2 Combine with pagination
        Pageable pageable = PageRequest.of(page, size, sort);

        // 🔥 Step 3: Apply filtering if needed
        if (category != null) {
            return expenseRepository.findByUserAndCategory(user, category, pageable);
        }

        return expenseRepository.findByUser(user, pageable);
    }
    //UPDATE EXPENSE
    public Expense updateExpense(Long id, Expense updatedExpense, String username) {

        Expense existingExpense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        //  Ownership check
        if (!existingExpense.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You are not allowed to update this expense");
        }

        existingExpense.setTitle(updatedExpense.getTitle());
        existingExpense.setAmount(updatedExpense.getAmount());
        existingExpense.setCategory(updatedExpense.getCategory());
        existingExpense.setDate(updatedExpense.getDate());

        return expenseRepository.save(existingExpense);
    }

    // DELETE EXPENSE
    public void deleteExpense(Long id, String username) {

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        // Ownership check
        if (!expense.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You are not allowed to delete this expense");
        }

        expenseRepository.delete(expense);
    }
}