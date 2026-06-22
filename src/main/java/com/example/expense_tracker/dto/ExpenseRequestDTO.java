package com.example.expense_tracker.dto;

import jakarta.validation.constraints.*;

public class ExpenseRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @Min(value = 1, message = "Amount must be greater than 0")
    private double amount;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Date is required")
    private String date;

    // getters and setters
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}