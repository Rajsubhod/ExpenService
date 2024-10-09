package com.rajsubhod.expenseservice.util;


import com.rajsubhod.expenseservice.dto.ExpenseDto;
import com.rajsubhod.expenseservice.dto.response.ExpenseResponse;
import com.rajsubhod.expenseservice.entity.Expense;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.UUID;

@Component
public class DtoConverter {

    public Expense convertToExpense(ExpenseDto expenseDto){
        return Expense.builder()
                .Id(UUID.randomUUID().toString())
                .userId(expenseDto.getUserId())
                .amount(expenseDto.getAmount())
                .sender(expenseDto.getSender())
                .receiver(expenseDto.getReceiver())
                .date(expenseDto.getDate())
                .build();
    }

    public ExpenseResponse convertToResponse(ExpenseDto expenseDto){
        return ExpenseResponse.builder()
                .Id(UUID.randomUUID().toString())
                .amount(expenseDto.getAmount())
                .sender(expenseDto.getSender())
                .receiver(expenseDto.getReceiver())
                .date(parseDate(expenseDto.getDate()))
                .build();
    }

    private String parseDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date).toString();
        } catch (ParseException e) {
            return "";
        }
    }
}
