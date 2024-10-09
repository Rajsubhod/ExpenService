package com.rajsubhod.expenseservice.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rajsubhod.expenseservice.dto.ExpenseDto;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.Map;

public class ExpenseDeserializer implements Deserializer<ExpenseDto> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public ExpenseDto deserialize(String s, byte[] bytes) {
        ObjectMapper mapper = new ObjectMapper();
        ExpenseDto expenseDto = null;
        try{
            expenseDto = mapper.readValue(bytes, ExpenseDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return expenseDto;
    }

    @Override
    public void close() {

    }
}
