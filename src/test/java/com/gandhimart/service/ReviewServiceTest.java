package com.gandhimart.service;

import com.gandhimart.dao.ReviewDAO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

    private ReviewDAO reviewDAO;
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewDAO = mock(ReviewDAO.class);
        reviewService = new ReviewService(reviewDAO);
    }

    @Test
    void createReviewShouldRejectInvalidRating() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> reviewService.createReview(
                                1L,
                                1L,
                                6,
                                "Test"
                        )
                );

        assertEquals(
                "Rating must be between 1 and 5",
                exception.getMessage()
        );

        verifyNoInteractions(reviewDAO);
    }

    @Test
    void createReviewShouldRejectUnpurchasedProduct()
            throws Exception {

        when(
                reviewDAO.hasCompletedPurchase(
                        1L,
                        2L
                )
        ).thenReturn(false);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> reviewService.createReview(
                                1L,
                                2L,
                                5,
                                "Test"
                        )
                );

        assertEquals(
                "You can review only products from completed paid orders",
                exception.getMessage()
        );

        verify(
                reviewDAO
        ).hasCompletedPurchase(
                1L,
                2L
        );

        verify(
                reviewDAO,
                never()
        ).create(any());
    }
}