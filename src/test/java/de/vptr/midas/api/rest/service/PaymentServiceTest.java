package de.vptr.midas.api.rest.service;

import static de.vptr.midas.api.util.ServiceTestDataBuilder.createUniqueAccountEntity;
import static de.vptr.midas.api.util.ServiceTestUtil.assertServiceNotNull;
import static de.vptr.midas.api.util.ServiceTestUtil.setupTestUser;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.vptr.midas.api.rest.entity.AccountEntity;
import de.vptr.midas.api.rest.entity.UserEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@QuarkusTest
class PaymentServiceTest {
    @Inject
    PaymentService paymentService;

    @Inject
    UserService userService;

    private UserEntity testUser;
    private AccountEntity testSourceAccount;
    private AccountEntity testTargetAccount;

    @BeforeEach
    @Transactional
    void setUp() {
        // Create test user using utility
        this.testUser = setupTestUser(this.userService);

        // Create test accounts using utility
        this.testSourceAccount = createUniqueAccountEntity();
        this.testSourceAccount.name = "Source Account";
        this.testSourceAccount.persist();

        this.testTargetAccount = createUniqueAccountEntity();
        this.testTargetAccount.name = "Target Account";
        this.testTargetAccount.persist();
    }

    @Test
    void testServiceNotNull() {
        assertServiceNotNull(this.paymentService);
    }

    @Test
    void testGetAllPayments() {
        final var payments = this.paymentService.getAllPayments();
        assertNotNull(payments);
    }

    @Test
    @Transactional
    void testCreatePayment() {
        final var newPaymentDto = de.vptr.midas.api.util.ServiceTestDataBuilder.createPaymentDto(
                this.testSourceAccount.id,
                this.testTargetAccount.id,
                this.testUser.id,
                "Test payment",
                LocalDate.now(),
                new BigDecimal("100.50"));

        final var createdPayment = this.paymentService.createPayment(newPaymentDto);

        assertNotNull(createdPayment);
        assertNotNull(createdPayment.id);
        assertEquals(this.testTargetAccount.id, createdPayment.targetAccountId);
        assertEquals(this.testSourceAccount.id, createdPayment.sourceAccountId);
        assertEquals(this.testUser.id, createdPayment.userId);
        assertEquals("Test payment", createdPayment.comment);
        assertEquals(LocalDate.now(), createdPayment.date);
        assertEquals(new BigDecimal("100.50"), createdPayment.amount);
        assertNotNull(createdPayment.created);
    }

    @Test
    @Transactional
    void testUpdatePayment() {
        // First create a payment
        final var newPaymentDto = de.vptr.midas.api.util.ServiceTestDataBuilder.createPaymentDto(
                this.testSourceAccount.id,
                this.testTargetAccount.id,
                this.testUser.id,
                "Original comment",
                LocalDate.now(),
                new BigDecimal("50.00"));
        final var createdPayment = this.paymentService.createPayment(newPaymentDto);

        // Update the payment
        final var updateDto = de.vptr.midas.api.util.ServiceTestDataBuilder.createPaymentDto(
                this.testSourceAccount.id,
                this.testTargetAccount.id,
                this.testUser.id,
                "Updated comment",
                LocalDate.now(),
                new BigDecimal("75.00"));

        final var updatedPayment = this.paymentService.updatePayment(createdPayment.id, updateDto);

        assertNotNull(updatedPayment);
        assertEquals("Updated comment", updatedPayment.comment);
        assertEquals(new BigDecimal("75.00"), updatedPayment.amount);
        assertNotNull(updatedPayment.lastEdit);
    }

    @Test
    @Transactional
    void testDeletePayment() {
        // First create a payment
        final var newPaymentDto = de.vptr.midas.api.util.ServiceTestDataBuilder.createPaymentDto(
                this.testSourceAccount.id,
                this.testTargetAccount.id,
                this.testUser.id,
                "Delete test payment",
                LocalDate.now(),
                new BigDecimal("25.00"));
        final var createdPayment = this.paymentService.createPayment(newPaymentDto);

        final Long paymentId = createdPayment.id;

        final var deleted = this.paymentService.deletePayment(paymentId);

        assertTrue(deleted);
        final var deletedPayment = this.paymentService.findById(paymentId);
        assertTrue(deletedPayment.isEmpty());
    }

    @Test
    void testDeleteNonExistentPayment() {
        final var deleted = this.paymentService.deletePayment(999999L);
        assertFalse(deleted);
    }

    @Test
    @Transactional
    void testFindById() {
        // First create a payment
        final var newPaymentDto = de.vptr.midas.api.util.ServiceTestDataBuilder.createPaymentDto(
                this.testSourceAccount.id,
                this.testTargetAccount.id,
                this.testUser.id,
                "Find test payment",
                LocalDate.now(),
                new BigDecimal("200.00"));
        final var createdPayment = this.paymentService.createPayment(newPaymentDto);

        final var foundPayment = this.paymentService.findById(createdPayment.id);

        assertTrue(foundPayment.isPresent());
        assertEquals(createdPayment.id, foundPayment.get().id);
        assertEquals("Find test payment", foundPayment.get().comment);
        assertEquals(new BigDecimal("200.00"), foundPayment.get().amount);
    }

    @Test
    void testFindByIdNonExistent() {
        final var foundPayment = this.paymentService.findById(999999L);
        assertTrue(foundPayment.isEmpty());
    }

    @Test
    @Transactional
    void testFindByUserId() {
        // First create a payment
        final var newPaymentDto = de.vptr.midas.api.util.ServiceTestDataBuilder.createPaymentDto(
                this.testSourceAccount.id,
                this.testTargetAccount.id,
                this.testUser.id,
                "User payment test",
                LocalDate.now(),
                new BigDecimal("150.00"));
        this.paymentService.createPayment(newPaymentDto);

        final var userPayments = this.paymentService.findByUserId(this.testUser.id);

        assertNotNull(userPayments);
        assertFalse(userPayments.isEmpty());

        // Verify all payments belong to the user
        for (final var payment : userPayments) {
            assertEquals(this.testUser.id, payment.userId);
        }
    }

    @Test
    @Transactional
    void testFindByDateRange() {
        final LocalDate startDate = LocalDate.now().minusDays(1);
        final LocalDate endDate = LocalDate.now().plusDays(1);

        // First create a payment within the date range
        final var newPaymentDto = de.vptr.midas.api.util.ServiceTestDataBuilder.createPaymentDto(
                this.testSourceAccount.id,
                this.testTargetAccount.id,
                this.testUser.id,
                "Date range test payment",
                LocalDate.now(),
                new BigDecimal("100.00"));
        this.paymentService.createPayment(newPaymentDto);

        final var paymentsInRange = this.paymentService.findByDateRange(startDate, endDate);

        assertNotNull(paymentsInRange);
        assertFalse(paymentsInRange.isEmpty());

        // Verify all payments are within the date range
        for (final var payment : paymentsInRange) {
            assertFalse(payment.date.isBefore(startDate));
            assertFalse(payment.date.isAfter(endDate));
        }
    }
}
