package com.corporate.payroll.adapter.out.external;

import com.corporate.payroll.adapter.in.web.dto.DatabookResponseDto;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class DatabookService {

    private static final Map<String, DatabookResponseDto> DATABOOK = new HashMap<>();

    static {
        DATABOOK.put("C|1234567890", DatabookResponseDto.builder()
                .idType("C")
                .idNumber("1234567890")
                .firstNames("Juan Carlos")
                .lastNames("García López")
                .birthDate("1985-03-15")
                .build());

        DATABOOK.put("C|9876543210", DatabookResponseDto.builder()
                .idType("C")
                .idNumber("9876543210")
                .firstNames("María Andrea")
                .lastNames("Rodríguez González")
                .birthDate("1988-07-22")
                .build());

        DATABOOK.put("P|AB123456", DatabookResponseDto.builder()
                .idType("P")
                .idNumber("AB123456")
                .firstNames("Pedro Miguel")
                .lastNames("Martínez Silva")
                .birthDate("1992-05-10")
                .build());

        DATABOOK.put("C|5555555555", DatabookResponseDto.builder()
                .idType("C")
                .idNumber("5555555555")
                .firstNames("Sandra Patricia")
                .lastNames("Hernández Campos")
                .birthDate("1990-11-28")
                .build());

        DATABOOK.put("P|CD789012", DatabookResponseDto.builder()
                .idType("P")
                .idNumber("CD789012")
                .firstNames("Roberto Andrés")
                .lastNames("López Morales")
                .birthDate("1987-09-05")
                .build());

        DATABOOK.put("C|1111111111", DatabookResponseDto.builder()
                .idType("C")
                .idNumber("1111111111")
                .firstNames("Luz Marina")
                .lastNames("Pérez Jiménez")
                .birthDate("1993-02-14")
                .build());

        DATABOOK.put("C|1725364578", DatabookResponseDto.builder()
                .idType("C")
                .idNumber("1725364578")
                .firstNames("Jaime Andrés")
                .lastNames("Sánchez Ruiz")
                .birthDate("1995-08-22")
                .build());

        DATABOOK.put("P|A123", DatabookResponseDto.builder()
                .idType("P")
                .idNumber("A123")
                .firstNames("José María")
                .lastNames("Gutiérrez López")
                .birthDate("1989-11-10")
                .build());
    }

    /**
     * Consulta información del cliente en el servicio databook
     * @param idType Tipo de identificación (C o P)
     * @param idNumber Número de identificación
     * @return Optional con la información del cliente si existe
     */
    public Optional<DatabookResponseDto> getClientInfo(String idType, String idNumber) {
        String key = idType + "|" + idNumber;
        return Optional.ofNullable(DATABOOK.get(key));
    }
}
