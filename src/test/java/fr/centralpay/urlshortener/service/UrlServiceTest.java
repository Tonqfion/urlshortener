package fr.centralpay.urlshortener.service;

import fr.centralpay.urlshortener.exception.ElementNotFoundException;
import fr.centralpay.urlshortener.model.entity.UrlEntity;
import fr.centralpay.urlshortener.repository.UrlEntityRepository;
import fr.centralpay.urlshortener.util.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {
    @InjectMocks
    UrlService tested;

    @Mock
    UrlEntityRepository urlEntityRepository;

    @Mock
    UuidPoolService uuidPoolService;

    @Mock
    QRCodeService qrCodeService;

    String givenLongUrl = "longUrl";

    MessageDigest givenMD5Digest;

    MessageDigest givenSHA256Digest;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        givenMD5Digest = MessageDigest.getInstance("MD5");
        givenSHA256Digest = MessageDigest.getInstance("SHA-256");
    }

    @Test
    void test_generateShortUrl_no_MD5_hash_present() {
        // Given
        String expectedHash = Utils.hashAndShortenString(givenMD5Digest, givenLongUrl);
        String expectedShortUrl = Utils.getShortUrlForCurrentServerFromHash(expectedHash);

        // When
        var result = tested.generateShortUrl(givenLongUrl);

        // Then
        Assertions.assertEquals(expectedShortUrl, result.shortUrl());
    }

    @Test
    void test_generateShortUrl_MD5_hash_present_no_SHA256_hash_present() {
        // Given
        String givenOtherLongUrl = "givenOtherLongUrl";
        String expectedMd5Hash = Utils.hashAndShortenString(givenMD5Digest, givenLongUrl);
        String expectedMd256Hash = Utils.hashAndShortenString(givenSHA256Digest, givenLongUrl);
        String expectedMd256ShortUrl = Utils.getShortUrlForCurrentServerFromHash(expectedMd256Hash);
        UrlEntity givenUrlEntity = new UrlEntity();
        givenUrlEntity.setHashId(expectedMd5Hash);
        givenUrlEntity.setCompleteUrl(givenOtherLongUrl);
        Mockito.when(urlEntityRepository.findById(expectedMd5Hash)).thenReturn(Optional.of(givenUrlEntity));

        // When
        var result = tested.generateShortUrl(givenLongUrl);

        // Then
        Assertions.assertEquals(expectedMd256ShortUrl, result.shortUrl());
    }

    @Test
    void test_generateShortUrl_MD5_hash_present_SHA256_hash_present_find_rdm_string_from_db() {
        // Given
        String givenRdmString = "givenRdmString";
        String givenShortUrl = Utils.getShortUrlForCurrentServerFromHash(givenRdmString);
        String givenOtherLongUrl = "givenOtherLongUrl";
        String expectedMd5Hash = Utils.hashAndShortenString(givenMD5Digest, givenLongUrl);
        String expectedMd256Hash = Utils.hashAndShortenString(givenSHA256Digest, givenLongUrl);
        UrlEntity givenUrlEntity = new UrlEntity();
        givenUrlEntity.setHashId(expectedMd5Hash);
        givenUrlEntity.setCompleteUrl(givenOtherLongUrl);
        Mockito.when(urlEntityRepository.findById(expectedMd5Hash)).thenReturn(Optional.of(givenUrlEntity));
        Mockito.when(urlEntityRepository.findById(expectedMd256Hash)).thenReturn(Optional.of(givenUrlEntity));
        Mockito.when(uuidPoolService.getRandomString()).thenReturn(givenRdmString);

        // When
        var result = tested.generateShortUrl(givenLongUrl);

        // Then
        Assertions.assertEquals(givenShortUrl, result.shortUrl());
    }

    @Test
    void test_retrieveOriginalUrl_ok() {
        // Given
        String hashId = "hashId";
        UrlEntity givenUrlEntity = new UrlEntity();
        givenUrlEntity.setHashId(hashId);
        givenUrlEntity.setCompleteUrl(givenLongUrl);
        Mockito.when(urlEntityRepository.findById(hashId)).thenReturn(Optional.of(givenUrlEntity));

        // When
        var result = tested.retrieveOriginalUrl(hashId);

        // Then
        Assertions.assertEquals(givenLongUrl, result);
    }

    @Test
    void test_retrieveOriginalUrl_throw_elementNotFoundException() {
        // Given
        String hashId = "hashId";
        Mockito.when(urlEntityRepository.findById(hashId)).thenReturn(Optional.empty());

        // Then
        Assertions.assertThrows(ElementNotFoundException.class, () -> tested.retrieveOriginalUrl(hashId));
    }

    @Test
    void test_removeOldUrlEntity_ok() {
        // Given
        tested.removeOldUrlEntity(3);

        // Then
        Mockito.verify(urlEntityRepository, Mockito.times(1)).deleteByDateCreatedBefore(Mockito.any());
    }


}
