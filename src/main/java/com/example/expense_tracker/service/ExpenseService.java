package com.example.expense_tracker.service;

import com.example.expense_tracker.dto.ExpenseDTO;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    //converting entity -> dto
    private ExpenseDTO convertToDTO(Expense expense) {
        ExpenseDTO dto = new ExpenseDTO();
        dto.setId(expense.getId());
        dto.setTitle(expense.getTitle());
        dto.setAmount(expense.getAmount());
        dto.setCategory(expense.getCategory());
        dto.setDate(expense.getDate().toString());
        return dto;
    }

    // ADD EXPENSE
    public Expense addExpense(Expense expense, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        expense.setUser(user);

        return expenseRepository.save(expense);
    }

    // GET EXPENSES WITH PAGINATION
    public Page<ExpenseDTO> getUserExpenses(
            String username,
            String category,
            int page,
            int size,
            String sortBy,
            String sortDir) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                sortDir.equalsIgnoreCase("asc") ?
                        Sort.by(sortBy).ascending() :
                        Sort.by(sortBy).descending()
        );

        Page<Expense> expenses;

        if (category != null) {
            expenses = expenseRepository.findByUserUsernameAndCategory(
                    username, category, pageable);
        } else {
            expenses = expenseRepository.findByUserUsername(
                    username, pageable);
        }

        // 🔥 CONVERT PAGE<Expense> → PAGE<ExpenseDTO>
        return expenses.map(this::convertToDTO);

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

        // TotalExpense
        public Double getTotalExpenses(String username) {

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Double total = expenseRepository.getTotalExpensesByUser(user);

            return total != null ? total : 0.0;
        }
    public List<Map<String, Object>> getCategoryWiseExpenses(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Object[]> results = expenseRepository.getCategoryWiseExpenses(user);

        List<Map<String, Object>> response = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("category", row[0]);
            map.put("total", row[1]);
            response.add(map);
        }

        return response;
    }
    public List<Map<String, Object>> getMonthlyExpenses(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Object[]> results = expenseRepository.getMonthlyExpenses(user);

        List<Map<String, Object>> response = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("month", row[0]);
            map.put("total", row[1]);
            response.add(map);
        }

        return response;
    }
    }