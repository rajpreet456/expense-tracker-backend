package com.example.expense_tracker.controller;

import com.example.expense_tracker.dto.ExpenseDTO;
import com.example.expense_tracker.dto.ExpenseRequestDTO;
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
import java.util.Map;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    public ExpenseRepository expenseRepository;

    //Add Expense (WITH validation)
    @PostMapping
    public ExpenseDTO addExpense(
            @Valid @RequestBody ExpenseRequestDTO dto,
            Principal principal) {

        return expenseService.addExpense(dto, principal.getName());
    }

    // Get Expenses
    @GetMapping
    public Page<ExpenseDTO> getExpenses(
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
public ExpenseDTO updateExpense(
        @PathVariable Long id,
        @Valid @RequestBody ExpenseRequestDTO dto,
        Principal principal) {

    return expenseService.updateExpense(id, dto, principal.getName());
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
    @GetMapping("/total")
    public Map<String, Double> getTotalExpenses(Principal principal) {

        Double total = expenseService.getTotalExpenses(principal.getName());

        return Map.of("total", total);
    }

    @GetMapping("/category-summary")
    public List<Map<String, Object>> getCategorySummary(Principal principal) {

        return expenseService.getCategoryWiseExpenses(principal.getName());
    }
    @GetMapping("/monthly-summary")
    public List<Map<String, Object>> getMonthlySummary(Principal principal) {

        return expenseService.getMonthlyExpenses(principal.getName());
    }
}