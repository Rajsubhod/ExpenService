package com.rajsubhod.expenseservice.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rajsubhod.expenseservice.dto.ExpenseDto;
import com.rajsubhod.expenseservice.dto.response.ExpenseResponse;
import com.rajsubhod.expenseservice.entity.Expense;
import com.rajsubhod.expenseservice.repository.ExpenseRepository;
import com.rajsubhod.expenseservice.util.DtoConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final DtoConverter dtoConverter;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ExpenseService(ExpenseRepository expenseRepository, DtoConverter dtoConverter) {
        this.expenseRepository = expenseRepository;
        this.dtoConverter = dtoConverter;
    }

    public void saveExpense(ExpenseDto expenseDto) {
        Expense expense = dtoConverter.convertToExpense(expenseDto);
        expenseRepository.save(expense);
    }

    public ExpenseDto getExpenseByID(String expenseId) {
        Expense expense = expenseRepository.findById(expenseId).orElseThrow(() -> new RuntimeException("Expense not found"));
        return new ExpenseDto(
                expense.getUserId(),
                expense.getAmount(),
                expense.getSender(),
                expense.getReceiver(),
                expense.getDate()
        );
    }

    public List<ExpenseResponse> getExpenses(String userId) {
        List<Expense> expenselist = expenseRepository.findByUserId(userId);
        return objectMapper.convertValue(expenselist, new TypeReference<List<ExpenseResponse>>() {});
    }

    public Page<ExpenseResponse> getExpenses(String userId, Pageable pageable) {
        Page<Expense> expensesPage = expenseRepository.findByUserId(userId, pageable);
        return expensesPage.map(expense -> objectMapper.convertValue(expense, ExpenseResponse.class));
    }


    public boolean createExpense(ExpenseDto expenseDto) {
        try {
            expenseRepository.save(objectMapper.convertValue(expenseDto, Expense.class));
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean updateExpense(ExpenseDto expenseDto) {
        return true;
    }
}

