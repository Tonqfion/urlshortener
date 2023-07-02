package fr.centralpay.urlshortener.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class ScheduleTaskServiceTest {

    @InjectMocks
    ScheduleTaskService tested;

    @Mock
    UuidPoolService uuidPoolService;

    @Mock
    UrlService urlService;

    int givenUrlDaysTTL;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tested, "urlDaysTtl", givenUrlDaysTTL);
    }

    @Test
    void test_scheduledUuidPoolUpdate() {
        // Then
        Assertions.assertDoesNotThrow(() -> tested.scheduledUuidPoolUpdate());
        Mockito.verify(uuidPoolService, Mockito.times(1)).checkAndFeedRandomStringPool();
    }

    @Test
    void test_scheduledOldUrlEntitiesDeletion() {
        // Then
        Assertions.assertDoesNotThrow(() -> tested.scheduledOldUrlEntitiesDeletion());
        Mockito.verify(urlService, Mockito.times(1)).removeOldUrlEntity(givenUrlDaysTTL);
    }


}
