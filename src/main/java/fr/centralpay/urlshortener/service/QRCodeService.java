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

/**
 * Service regroupant les méthodes en lien avec les QR Codes
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class QRCodeService {

    /**
     * Méthode permettant de transformer une URL en QRCode au format bytes puis de convertir ces bytes en chaîne de
     * caractère
     *
     * @param url l'URL en entrée
     * @return l'image du QR Code au format String
     */
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
