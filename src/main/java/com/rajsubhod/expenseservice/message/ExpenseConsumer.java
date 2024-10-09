package com.rajsubhod.expenseservice.message;

import com.rajsubhod.expenseservice.dto.ExpenseDto;
import com.rajsubhod.expenseservice.dto.response.ExpenseResponse;
import com.rajsubhod.expenseservice.services.ExpenseService;
import com.rajsubhod.expenseservice.services.SseEmitterService;
import com.rajsubhod.expenseservice.util.DtoConverter;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ExpenseConsumer {

    private final ExpenseService expenseService;
    private final SseEmitterService sseEmitterService;
    private final DtoConverter dtoConverter;

    public ExpenseConsumer(ExpenseService expenseService, SseEmitterService sseEmitterService, DtoConverter dtoConverter) {
        this.expenseService = expenseService;
        this.sseEmitterService = sseEmitterService;
        this.dtoConverter = dtoConverter;
    }

    @KafkaListener(topics = "${spring.kafka.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void receiveEventFromQueue(ExpenseDto expenseDto){
        try{
            expenseService.saveExpense(expenseDto);
            ExpenseResponse expenseResponse = dtoConverter.convertToResponse(expenseDto);
            sseEmitterService.sendToUser(expenseDto.getUserId(), expenseResponse);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

}
