package com.rajsubhod.expenseservice.controller;

import com.rajsubhod.expenseservice.dto.ExpenseDto;
import com.rajsubhod.expenseservice.dto.response.ExpenseResponse;
import com.rajsubhod.expenseservice.services.ExpenseService;
import com.rajsubhod.expenseservice.services.SseEmitterService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class ExpenseController {

    private static final Logger logger = LoggerFactory.getLogger(ExpenseController.class);

    private final ExpenseService expenseService;
    private final SseEmitterService sseEmitterService;

    @GetMapping("/v1/")
    public ResponseEntity<?> getExpenses(@RequestParam(value = "user_Id") @NonNull String userId) {
        try {
            logger.info("Fetching all expenses");
            List<ExpenseResponse> expenseResponseList = expenseService.getExpenses(userId);
            return new ResponseEntity<>(expenseResponseList, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Transactions Not Found");
            return new ResponseEntity<>("User Transaction Not Found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/v1/id")
    public ResponseEntity<?> getExpensesId(@RequestHeader(value = "X-User-Id") @NonNull String userId) {
        try {
            logger.info("Fetching all expenses");
            List<ExpenseResponse> expenseResponseList = expenseService.getExpenses(userId);
            return new ResponseEntity<>(expenseResponseList, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Transactions Not Found");
            return new ResponseEntity<>("User Transaction Not Found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/v1/{expenseId}")
    public ResponseEntity<?> getExpense(@PathVariable String expenseId) {
        try {
            logger.info("Fetching expense by expenseId {}", expenseId);
            ExpenseDto expenseDto = expenseService.getExpenseByID(expenseId);
            return new ResponseEntity<>(expenseDto, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Transaction Not Found");
            return new ResponseEntity<>("User Transaction Not Found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/v1/page")
    public ResponseEntity<?> getExpensePage(
            @RequestParam(value = "user_id") @NonNull String userId,
            @PageableDefault(page = 0, size = 30) Pageable pageable
    ) {
        try {
            logger.info("Fetching expenses by batch of 30");
            Page<ExpenseResponse> expenseResponseList = expenseService.getExpenses(userId, pageable);
            return new ResponseEntity<>(expenseResponseList, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("User Transactions Not Found");
            return new ResponseEntity<>("User Transaction Not Found", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(path = "/v1/")
    public ResponseEntity<?> addExpenses(@RequestHeader(value = "X-User-Id") @NonNull String userId, @RequestBody ExpenseDto expenseDto) {
        try {
            logger.info("Adding expense for user {}", userId);
            expenseDto.setUserId(userId);
            return new ResponseEntity<>(expenseService.createExpense(expenseDto), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Request Failed");
            return new ResponseEntity<>("Request Not Accepted", HttpStatus.BAD_REQUEST);
        }
    }

//    TODO: Implement the updateExpense method and deleteExpense method

    @GetMapping("/v1/connect")
    public SseEmitter subscribe(@RequestHeader(value = "X-User-Id") @NonNull String userId) {
        try {
            logger.info("Subscribing to sse user: {}", userId);
            return sseEmitterService.createEmitter(userId);
        } catch (Exception ex) {
            logger.error("Subscription Failed");
            throw new RuntimeException("User not found");
        }
    }
}
