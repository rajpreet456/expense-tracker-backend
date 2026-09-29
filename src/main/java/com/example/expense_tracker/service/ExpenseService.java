package com.example.expense_tracker.service;

import com.example.expense_tracker.dto.ExpenseDTO;
import com.example.expense_tracker.dto.ExpenseRequestDTO;
import com.example.expense_tracker.entity.Expense;
import com.example.expense_tracker.entity.User;
import com.example.expense_tracker.exception.ResourceNotFoundException;
import com.example.expense_tracker.exception.UnauthorizedException;
import com.example.expense_tracker.repository.ExpenseRepository;
import com.example.expense_tracker.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.*;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    // ENTITY -> DTO
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
    public ExpenseDTO addExpense(ExpenseRequestDTO dto, String username) {

        Expense expense = new Expense();

        expense.setTitle(dto.getTitle());
        expense.setAmount(dto.getAmount());
        expense.setCategory(dto.getCategory());
        expense.setDate(LocalDate.parse(dto.getDate()));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        expense.setUser(user);

        return convertToDTO(expenseRepository.save(expense));
    }

    public ExpenseDTO getExpenseById(Long id, String username) {

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        if (!expense.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("You are not allowed to view this expense");
        }

        return convertToDTO(expense);
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

        return expenses.map(this::convertToDTO);
    }

    // UPDATE EXPENSE
    public ExpenseDTO updateExpense(Long id, ExpenseRequestDTO dto, String username) {

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        if (!expense.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("You are not allowed to update this expense");
        }

        expense.setTitle(dto.getTitle());
        expense.setAmount(dto.getAmount());
        expense.setCategory(dto.getCategory());
        expense.setDate(LocalDate.parse(dto.getDate()));

        return convertToDTO(expenseRepository.save(expense));
    }

    // DELETE EXPENSE
    public void deleteExpense(Long id, String username) {

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        if (!expense.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("You are not allowed to delete this expense");
        }

        expenseRepository.delete(expense);
    }

    // TOTAL EXPENSE
    public Double getTotalExpenses(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Double total = expenseRepository.getTotalExpensesByUser(user);

        return total != null ? total : 0.0;
    }

    // CATEGORY WISE
    public List<Map<String, Object>> getCategoryWiseExpenses(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

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

    // MONTHLY
    public List<Map<String, Object>> getMonthlyExpenses(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Object[]> results = expenseRepository.getMonthlyExpenses(user);
        List<Map<String, Object>> response = new ArrayList<>();

        for (Object[] row : results) {
            Integer year = (Integer) row[0];
            Integer month = (Integer) row[1];
            Double total = (Double) row[2];

            // Format to "YYYY-MM" (e.g., 2026-09)
            String formattedMonth = String.format("%d-%02d", year, month);

            Map<String, Object> map = new HashMap<>();
            map.put("month", formattedMonth);
            map.put("total", total);
            response.add(map);
        }

        return response;
    }
}