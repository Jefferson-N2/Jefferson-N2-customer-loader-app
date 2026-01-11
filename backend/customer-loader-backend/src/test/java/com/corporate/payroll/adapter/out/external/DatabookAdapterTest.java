package com.corporate.payroll.adapter.out.external;

import com.corporate.payroll.adapter.in.web.dto.DatabookResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests para DatabookAdapter.
 * Verifica que delega correctamente al DatabookService y retorna el Optional esperado.
 */
class DatabookAdapterTest {

    private DatabookService databookService;
    private DatabookAdapter databookAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        databookService = Mockito.mock(DatabookService.class);
        databookAdapter = new DatabookAdapter(databookService);
    }

    @Test
    void testGetClientInfoShouldReturnResponseFromService() {
        String idType = "C";
        String idNumber = "12345678";

        DatabookResponseDto dto = new DatabookResponseDto();
        dto.setIdType(idType);
        dto.setIdNumber(idNumber);
        dto.setFirstNames("Juan Pérez");

        when(databookService.getClientInfo(idType, idNumber))
                .thenReturn(Optional.of(dto));

        Optional<DatabookResponseDto> result = databookAdapter.getClientInfo(idType, idNumber);

        assertTrue(result.isPresent(), "El resultado debe estar presente");
        assertEquals("Juan Pérez", result.get().getFirstNames(), "El nombre debe coincidir");
        verify(databookService, times(1)).getClientInfo(idType, idNumber);
    }

    @Test
    void testGetClientInfoShouldReturnEmptyWhenServiceReturnsEmpty() {
        String idType = "P";
        String idNumber = "A12345";

        when(databookService.getClientInfo(idType, idNumber))
                .thenReturn(Optional.empty());

        Optional<DatabookResponseDto> result = databookAdapter.getClientInfo(idType, idNumber);

        assertFalse(result.isPresent(), "El resultado debe estar vacío");
        verify(databookService, times(1)).getClientInfo(idType, idNumber);
    }
}
