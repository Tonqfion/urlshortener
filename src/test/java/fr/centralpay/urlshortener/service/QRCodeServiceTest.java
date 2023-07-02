package fr.centralpay.urlshortener.service;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import fr.centralpay.urlshortener.exception.GeneralTechnicalException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
public class QRCodeServiceTest {

    @InjectMocks
    QRCodeService tested;

    private final String givenUrl = "url";

    @Test
    void testGetQrImageAsByteArray_ok() {
        // When
        Assertions.assertDoesNotThrow(() -> tested.getQrImageAsByteArray(givenUrl));
    }

    @Test
    void testGetQrImageAsByteArray_catch_IOException_throws_GeneralTechnicalException() {
        // Given
        MockedStatic<MatrixToImageWriter> mockedMatrixToImgWriter = Mockito.mockStatic(MatrixToImageWriter.class);
        mockedMatrixToImgWriter.when(() -> MatrixToImageWriter.writeToStream(Mockito.any(), Mockito.anyString(), Mockito.any())).thenThrow(IOException.class);

        // Then
        Assertions.assertThrows(GeneralTechnicalException.class, () -> tested.getQrImageAsByteArray(givenUrl));
    }
}
