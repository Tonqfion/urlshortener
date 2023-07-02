package fr.centralpay.urlshortener.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import fr.centralpay.urlshortener.exception.GeneralTechnicalException;
import fr.centralpay.urlshortener.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class QRCodeService {

    public String getQrImageAsByteArray(String url) {
        int imageSize = 320;
        try (var bos = new ByteArrayOutputStream()) {
            BitMatrix matrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, imageSize, imageSize);
            MatrixToImageWriter.writeToStream(matrix, "JPG", bos);
            return Utils.convertByteArrayToBase64EncodedString(bos.toByteArray());
        } catch (WriterException | IOException e) {
            log.error(e.getMessage());
            throw new GeneralTechnicalException();
        }
    }
}
