package de.vptr.midas.api.rest.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.vptr.midas.api.rest.entity.AccountEntity;
import de.vptr.midas.api.rest.entity.PaymentEntity;
import de.vptr.midas.api.rest.entity.UserEntity;
import de.vptr.midas.api.rest.entity.UserRankEntity;
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
    private UserRankEntity testRank;
    private AccountEntity testSourceAccount;
    private AccountEntity testTargetAccount;

    @BeforeEach
    @Transactional
    void setUp() {
        // Create test rank if it doesn't exist
        this.testRank = UserRankEntity.find("name", "Test Rank").firstResult();
        if (this.testRank == null) {
            this.testRank = new UserRankEntity();
            this.testRank.name = "Test Rank";
            this.testRank.userAdd = false;
            this.testRank.userEdit = false;
            this.testRank.userDelete = false;
            this.testRank.persist();
        }

        // Create test user with unique username
        final String uniqueSuffix = String.valueOf(System.currentTimeMillis() + (int) (Math.random() * 10000));
        this.testUser = new UserEntity();
        this.testUser.username = "paymentTestUser_" + uniqueSuffix;
        this.testUser.email = "paymenttest_" + uniqueSuffix + "@example.com";
        this.testUser.password = "password";
        this.testUser = this.userService.createUser(this.testUser);

        // Create test accounts
        this.testSourceAccount = new AccountEntity();
        this.testSourceAccount.name = "Source Account";
        this.testSourceAccount.persist();

        this.testTargetAccount = new AccountEntity();
        this.testTargetAccount.name = "Target Account";
        this.testTargetAccount.persist();
    }

    @Test
    void testServiceNotNull() {
        assertNotNull(this.paymentService);
    }

    @Test
    void testGetAllPayments() {
        final List<PaymentEntity> payments = this.paymentService.getAllPayments();
        assertNotNull(payments);
    }

    @Test
    @Transactional
    void testCreatePayment() {
        final PaymentEntity newPayment = new PaymentEntity();
        newPayment.targetAccount = this.testTargetAccount;
        newPayment.sourceAccount = this.testSourceAccount;
        newPayment.userId = this.testUser;
        newPayment.comment = "Test payment";
        newPayment.date = LocalDate.now();
        newPayment.amount = new BigDecimal("100.50");

        final PaymentEntity createdPayment = this.paymentService.createPayment(newPayment);

        assertNotNull(createdPayment);
        assertNotNull(createdPayment.id);
        assertEquals(this.testTargetAccount.id, createdPayment.targetAccount.id);
        assertEquals(this.testSourceAccount.id, createdPayment.sourceAccount.id);
        assertEquals(this.testUser.id, createdPayment.userId.id);
        assertEquals("Test payment", createdPayment.comment);
        assertEquals(LocalDate.now(), createdPayment.date);
        assertEquals(new BigDecimal("100.50"), createdPayment.amount);
        assertNotNull(createdPayment.created);
    }

    @Test
    @Transactional
    void testUpdatePayment() {
        // First create a payment
        final PaymentEntity newPayment = new PaymentEntity();
        newPayment.targetAccount = this.testTargetAccount;
        newPayment.sourceAccount = this.testSourceAccount;
        newPayment.userId = this.testUser;
        newPayment.comment = "Original comment";
        newPayment.date = LocalDate.now();
        newPayment.amount = new BigDecimal("50.00");
        final PaymentEntity createdPayment = this.paymentService.createPayment(newPayment);

        // Update the payment
        createdPayment.comment = "Updated comment";
        createdPayment.amount = new BigDecimal("75.00");

        final PaymentEntity updatedPayment = this.paymentService.updatePayment(createdPayment);

        assertNotNull(updatedPayment);
        assertEquals("Updated comment", updatedPayment.comment);
        assertEquals(new BigDecimal("75.00"), updatedPayment.amount);
        assertNotNull(updatedPayment.lastEdit);
    }

    @Test
    @Transactional
    void testDeletePayment() {
        // First create a payment
        final PaymentEntity newPayment = new PaymentEntity();
        newPayment.targetAccount = this.testTargetAccount;
        newPayment.sourceAccount = this.testSourceAccount;
        newPayment.userId = this.testUser;
        newPayment.comment = "Delete test payment";
        newPayment.date = LocalDate.now();
        newPayment.amount = new BigDecimal("25.00");
        final PaymentEntity createdPayment = this.paymentService.createPayment(newPayment);

        final Long paymentId = createdPayment.id;

        final boolean deleted = this.paymentService.deletePayment(paymentId);

        assertTrue(deleted);
        final Optional<PaymentEntity> deletedPayment = this.paymentService.findById(paymentId);
        assertTrue(deletedPayment.isEmpty());
    }

    @Test
    void testDeleteNonExistentPayment() {
        final boolean deleted = this.paymentService.deletePayment(999999L);
        assertFalse(deleted);
    }

    @Test
    @Transactional
    void testFindById() {
        // First create a payment
        final PaymentEntity newPayment = new PaymentEntity();
        newPayment.targetAccount = this.testTargetAccount;
        newPayment.sourceAccount = this.testSourceAccount;
        newPayment.userId = this.testUser;
        newPayment.comment = "Find test payment";
        newPayment.date = LocalDate.now();
        newPayment.amount = new BigDecimal("200.00");
        final PaymentEntity createdPayment = this.paymentService.createPayment(newPayment);

        final Optional<PaymentEntity> foundPayment = this.paymentService.findById(createdPayment.id);

        assertTrue(foundPayment.isPresent());
        assertEquals(createdPayment.id, foundPayment.get().id);
        assertEquals("Find test payment", foundPayment.get().comment);
        assertEquals(new BigDecimal("200.00"), foundPayment.get().amount);
    }

    @Test
    void testFindByIdNonExistent() {
        final Optional<PaymentEntity> foundPayment = this.paymentService.findById(999999L);
        assertTrue(foundPayment.isEmpty());
    }

    @Test
    @Transactional
    void testFindByUserId() {
        // First create a payment
        final PaymentEntity newPayment = new PaymentEntity();
        newPayment.targetAccount = this.testTargetAccount;
        newPayment.sourceAccount = this.testSourceAccount;
        newPayment.userId = this.testUser;
        newPayment.comment = "User payment test";
        newPayment.date = LocalDate.now();
        newPayment.amount = new BigDecimal("150.00");
        this.paymentService.createPayment(newPayment);

        final List<PaymentEntity> userPayments = this.paymentService.findByUserId(this.testUser.id);

        assertNotNull(userPayments);
        assertFalse(userPayments.isEmpty());

        // Verify all payments belong to the user
        for (final PaymentEntity payment : userPayments) {
            assertEquals(this.testUser.id, payment.userId.id);
        }
    }

    @Test
    @Transactional
    void testFindByDateRange() {
        final LocalDate startDate = LocalDate.now().minusDays(1);
        final LocalDate endDate = LocalDate.now().plusDays(1);

        // First create a payment within the date range
        final PaymentEntity newPayment = new PaymentEntity();
        newPayment.targetAccount = this.testTargetAccount;
        newPayment.sourceAccount = this.testSourceAccount;
        newPayment.userId = this.testUser;
        newPayment.comment = "Date range test payment";
        newPayment.date = LocalDate.now();
        newPayment.amount = new BigDecimal("100.00");
        this.paymentService.createPayment(newPayment);

        final List<PaymentEntity> paymentsInRange = this.paymentService.findByDateRange(startDate, endDate);

        assertNotNull(paymentsInRange);
        assertFalse(paymentsInRange.isEmpty());

        // Verify all payments are within the date range
        for (final PaymentEntity payment : paymentsInRange) {
            assertFalse(payment.date.isBefore(startDate));
            assertFalse(payment.date.isAfter(endDate));
        }
    }
}
