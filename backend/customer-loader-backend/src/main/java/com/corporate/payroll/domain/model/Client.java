package com.corporate.payroll.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {
    
    private Long id;
    private String clientCode;
    private String idType;
    private String idNumber;
    private String firstNames;
    private String lastNames;
    private LocalDate birthDate;
    private LocalDate joinDate;
    private String email;
    private String phoneNumber;
    private String processId;
    private List<Account> accounts;
    
    public Account getFirstAccount() {
        return accounts != null && !accounts.isEmpty() ? accounts.get(0) : null;
    }
    
    public PayrollPayment getFirstPayment() {
        Account account = getFirstAccount();
        return account != null && account.getPayments() != null && !account.getPayments().isEmpty() 
            ? account.getPayments().get(0) : null;
    }
}