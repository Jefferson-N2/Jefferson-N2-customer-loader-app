package com.corporate.payroll.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "clients", indexes = {
    @Index(name = "idx_client_code", columnList = "client_code"),
    @Index(name = "idx_id_number", columnList = "id_number")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "client_code", unique = true, nullable = false)
    private String clientCode;
    
    @Column(name = "id_type", length = 1)
    private String idType;
    
    @Column(name = "id_number", length = 50, nullable = false)
    private String idNumber;
    
    @Column(name = "first_names", nullable = false, length = 100)
    private String firstNames;
    
    @Column(name = "last_names", nullable = false, length = 100)
    private String lastNames;
    
    @Column(name = "birth_date")
    private LocalDate birthDate;
    
    @Column(name = "join_date")
    private LocalDate joinDate;
    
    @Column(name = "email", length = 100)
    private String email;
    
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "process_id", length = 36)
    private String processId;

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<AccountEntity> accounts;

}
