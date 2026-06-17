package com.example.expense_tracker.controller;

import com.example.expense_tracker.entity.Expense;
import com.example.expense_tracker.repository.ExpenseRepository;
import com.example.expense_tracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    public ExpenseRepository expenseRepository;

    //Add Expense (WITH validation)
    @PostMapping
    public Expense addExpense(@RequestBody Expense expense) {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return expenseService.addExpense(expense, username);
    }

    // Get Expenses
    @GetMapping
    public Page<Expense> getExpenses(
            @RequestParam(required = false) String category,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Principal principal) {

        return expenseService.getUserExpenses(
                principal.getName(),
                category,
                page,
                size,
                sortBy,
                sortDir
        );
    }

//Update Expense
@PutMapping("/{id}")
public Expense updateExpense(@PathVariable Long id,
                             @RequestBody Expense expense) {

    String username = SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getName();

    return expenseService.updateExpense(id, expense, username);
}
@DeleteMapping("/{id}")
public String deleteExpense(@PathVariable Long id) {

    String username = SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getName();

    expenseService.deleteExpense(id, username);

    return "Expense deleted successfully";
}
}