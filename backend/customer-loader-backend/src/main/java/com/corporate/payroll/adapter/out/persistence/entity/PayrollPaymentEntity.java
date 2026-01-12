package com.corporate.payroll.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.LocalDate;
import java.math.BigDecimal;

@Entity
@Table(name = "payroll_payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayrollPaymentEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "amount", precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "status", length = 50)
    private String status;

 }
