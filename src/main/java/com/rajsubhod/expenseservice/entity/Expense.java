package com.rajsubhod.expenseservice.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.sql.Timestamp;


@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "expense")
public class Expense {

    @Id
    private String Id;

    private String userId;

    private BigDecimal amount;

    private String sender;

    private String receiver;

    private String date;

    @CreatedDate
    @Column(name = "created")
    private Timestamp createdDate;
}
