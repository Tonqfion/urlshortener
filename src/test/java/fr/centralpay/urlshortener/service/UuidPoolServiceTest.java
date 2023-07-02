package fr.centralpay.urlshortener.service;

import fr.centralpay.urlshortener.exception.ImpossibleToShortenException;
import fr.centralpay.urlshortener.model.entity.UuidPoolEntity;
import fr.centralpay.urlshortener.repository.UrlEntityRepository;
import fr.centralpay.urlshortener.repository.UuidPoolRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UuidPoolServiceTest {

    @InjectMocks
    UuidPoolService tested;

    @Mock
    UuidPoolRepository uuidPoolRepository;

    @Mock
    UrlEntityRepository urlEntityRepository;

    UuidPoolEntity givenEntity;

    String givenRdmString = "randomString";


    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tested, "amountUuidToStore", 5);
        ReflectionTestUtils.setField(tested, "minimumUuidBeforeFilling", 2);
        givenEntity = new UuidPoolEntity();
        givenEntity.setRandomString(givenRdmString);
    }

    @Test
    void test_getRandomString_ok() {
        // Given
        Mockito.when(uuidPoolRepository.findFirstByOrderByRandomStringDesc()).thenReturn(Optional.of(givenEntity));

        // When
        String result = tested.getRandomString();

        // Then
        Assertions.assertEquals(givenRdmString, result);
    }

    @Test
    void test_getRandomString_throw_impossibleToShortenException() {
        // Given
        Mockito.when(uuidPoolRepository.findFirstByOrderByRandomStringDesc()).thenReturn(Optional.empty());

        // Then
        Assertions.assertThrows(ImpossibleToShortenException.class, () -> tested.getRandomString());
    }

    @Test
    void test_checkAndFeedRandomStringPool_refill_needed() {
        // Given
        Mockito.when(uuidPoolRepository.findAll()).thenReturn(new ArrayList<>());

        // When
        tested.checkAndFeedRandomStringPool();

        Mockito.verify(uuidPoolRepository, Mockito.times(1)).saveAll(Mockito.any());
    }

    @Test
    void test_checkAndFeedRandomStringPool_refill_not_needed() {
        // Given
        List<UuidPoolEntity> givenList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            givenList.add(new UuidPoolEntity());
        }
        Mockito.when(uuidPoolRepository.findAll()).thenReturn(givenList);

        // When
        tested.checkAndFeedRandomStringPool();

        Mockito.verify(uuidPoolRepository, Mockito.times(0)).saveAll(Mockito.any());
    }
}
