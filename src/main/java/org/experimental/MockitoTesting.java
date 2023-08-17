package org.experimental;

import java.util.UUID;

import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class MockitoTesting {

    @Test
    public void staticMethodTests() {
        UUID defaultUuid = UUID.fromString("8d8b30e3-de52-4f1c-a71c-9905a8043dac");
        try (MockedStatic<UUID> mockedUuid = Mockito.mockStatic(UUID.class)) {
            mockedUuid.when(UUID::randomUUID).thenReturn(defaultUuid);

            UUID testUUID = UUID.randomUUID();
            System.out.println("Mocked UUID=" + testUUID);
        }

    }
}
