package com.example.tasktwo.Service;

import com.example.tasktwo.Entity.*;
import com.example.tasktwo.Enums.*;
import com.example.tasktwo.Exception.EmptyDataException;
import com.example.tasktwo.Repository.*;
import com.example.tasktwo.customclasses.Validations;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import jakarta.transaction.Transactional;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;

@Service
public class TransactionService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RewardRepository rewardRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private Validations validations;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private SqsTemplate sqsTemplate;

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @SqsListener("MyTransactionQueue")
    @Transactional
    public void listeningFromSqs(String message) {

        try {
            // Creating an instance of ObjectMapper (for converting the JSON data type to object data type)
            ObjectMapper objectMapper = new ObjectMapper();

            // Deserialize the JSON string back into a TransactionRequest object
            TransactionRequest transactionRequest = objectMapper.readValue(message, TransactionRequest.class);

            // Validating transaction after listening from the SQS
            validateTransaction(transactionRequest);

            // Fetching Merchant from the database
            Merchant merchant = merchantRepository.findById(transactionRequest.getMerchantId())
                    .orElseThrow(() -> new IllegalArgumentException("Merchant ID does not exist in the database: "
                            + transactionRequest.getMerchantId()));

            // Fetching User from the database
            User user = userRepository.findById(transactionRequest.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User ID does not exist in the database: "
                            + transactionRequest.getUserId()));

            // Now you can save the transaction or perform other operations
            Transaction transaction = new Transaction();
            transaction.setUser(user);
            transaction.setCreditDebitIndicator(transactionRequest.getCreditDebitIndicator());
            transaction.setTransactionType(transactionRequest.getTransactionType());
            transaction.setTime(Instant.now().getEpochSecond());
            transaction.setAmount(transactionRequest.getAmount());
            transaction.setCurrencyCode(transactionRequest.getCurrencyCode());
            transaction.setMerchant(merchant);

            // Calculating the rewards if the creditDebitIndicator is DEBIT
            if (transactionRequest.getCreditDebitIndicator() == CreditDebitIndicator.DEBIT) {
                transactionRepository.save(transaction);
                calculatingRewards(transaction);
            }

            // Calculate the refund and deduct rewards if the creditDebitIndicator is CREDIT
            if (transactionRequest.getCreditDebitIndicator() == CreditDebitIndicator.CREDIT) {
                grantRefund(transaction);
                logger.info("in grant refund");
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("In catch of listener: " + e.getMessage());
        }
    }

    // Validating Transaction after listening from the SQS
    @Transactional
    private void validateTransaction(TransactionRequest transactionRequest) {

        if (transactionRequest == null) {
            throw new EmptyDataException("Transaction Failed: Transaction cannot be null");
        }

        // Validating User ID
        userRepository.findById(transactionRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User ID is invalid or does not exist in the database: "
                        + transactionRequest.getUserId()));

        // Validating credit debit indicator type
        Optional.ofNullable(transactionRequest.getCreditDebitIndicator())
                .map(indicator -> CreditDebitIndicator.valueOf(indicator.toString().toUpperCase()))
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid credit debit indicator type: "
                                + transactionRequest.getCreditDebitIndicator()));

        //  Validating transaction type (Transaction type must be "ATM" or "POS")
        Optional.ofNullable(transactionRequest.getTransactionType())
                .map(transactionType -> TransactionType.valueOf(transactionType.toString().toUpperCase()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid transaction type: " +
                        transactionRequest.getTransactionType() + " (e.g., POS, ATM"));

        // Amount should be greater than zero
        if (transactionRequest.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        // Validating Currency Code
        Optional.ofNullable(transactionRequest.getCurrencyCode())
                .map(code -> CurrencyCode.valueOf(code.toString().toUpperCase()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid currency code: " +
                        transactionRequest.getCurrencyCode() + " (e.g., USD, EUR, INR)"));

        // Fetching Merchant from database
        Merchant merchant = merchantRepository.findById(transactionRequest.getMerchantId())
                .orElseThrow(() -> new IllegalArgumentException("Merchant ID does not exist in the database: "
                        + transactionRequest.getMerchantId()));

        // Validating merchant ID, category code, and name in a single decision statement
        if (transactionRequest.getMerchantId() == null ||
                String.valueOf(transactionRequest.getMerchantId()).length() != 3 ||
                transactionRequest.getMerchantName() == null ||
                transactionRequest.getMerchantName().trim().isEmpty() ||
                !merchant.getMerchantName().equals(transactionRequest.getMerchantName())) {
            throw new IllegalArgumentException("Invalid Merchant Details");
        }

        // Validate Merchant Category Code
        if (!validateMerchantCategoryCode(merchant.getMerchantCategoryCode())) {
            throw new IllegalArgumentException("Invalid Merchant Category Code: " + merchant.getMerchantCategoryCode());
        }
    }

    // Method to validate the Merchant Category Code
    public boolean validateMerchantCategoryCode(MerchantCategoryCode mcc) {
        return mcc == MerchantCategoryCode.EATING_PLACES ||
                mcc == MerchantCategoryCode.GROCERY_STORES ||
                mcc == MerchantCategoryCode.FAST_FOOD_RESTAURANTS ||
                mcc == MerchantCategoryCode.DEPARTMENT_STORES ||
                mcc == MerchantCategoryCode.GAS_STATIONS ||
                mcc == MerchantCategoryCode.HOTELS ||
                mcc == MerchantCategoryCode.TRAVEL_AGENCIES;
    }

    @Transactional
    private void calculatingRewards(Transaction transaction) {
        double rewardAmount = transaction.getAmount() * 0.10; // 10% reward for debit transactions

        // Create a new Rewards object with the correct parameters
        Rewards reward = new Rewards();
        reward.setUser(transaction.getUser());
        reward.setTransaction(transaction);
        reward.setRewards(rewardAmount);
        rewardRepository.save(reward);
    }

    @Transactional
    public String grantRefund(Transaction transactionRequest) {

        List<Transaction> transactions = transactionRepository.findRecentTransactionsByUserIdAndMerchantId(
                transactionRequest.getUser().getId(), transactionRequest.getMerchant().getMerchantId());

        if (transactions.isEmpty()) {
            logger.info("in no previous transactions");
            throw new NoSuchElementException("No previous transactions found for this user and merchant.");
        }

        // Validate the refund amount
        double refundAmount = transactionRequest.getAmount();
        if (refundAmount <= 0) {
            throw new IllegalArgumentException("Refund amount must be greater than zero.");
        }

        if (refundAmount > transactionRequest.getAmount()) {
            throw new IllegalArgumentException("Refund amount exceeds the original transaction amount.");
        }

        Optional<Transaction> matchingTransaction = transactions.stream()
                .filter(transaction -> refundAmount <= transaction.getAmount())
                .findFirst();

        if (matchingTransaction.isPresent()) {
            processRefund(matchingTransaction.get(), refundAmount);
            return "Refund granted successfully and rewards updated.";
        } else {
            throw new IllegalArgumentException("Insufficient transaction amounts to cover the requested refund.");
        }
    }

    private void processRefund(Transaction transaction, double refundAmount) {
        logger.info("Processing refund for transaction ID: " + transaction.getId() + " with refund amount: " + refundAmount);

        // Fetch the rewards based on the transaction id
        Rewards reward = rewardRepository.findByTransaction(transaction);
        if (reward == null) {
            logger.error("No rewards found for transaction ID: " + transaction.getId());
            throw new NoSuchElementException("No rewards found for the previous transaction.");
        }

        double rewardToDeduct = refundAmount * 0.10;
        if (reward.getRewards() < rewardToDeduct) {
            throw new IllegalArgumentException("Insufficient rewards to process the refund.");
        }

        // Adjust the reward based on the refund amount
        reward.setRewards(reward.getRewards() - rewardToDeduct);
        rewardRepository.save(reward);

        // Check if the refund amount equals the transaction amount
        if (refundAmount == transaction.getAmount()) {
            logger.info("Refund amount equals transaction amount. Deleting transaction and associated reward.");
            rewardRepository.delete(reward);  // Delete the associated reward
            transactionRepository.delete(transaction);  // Delete the transaction
            return;  // Exit the method since the transaction and reward have been removed
        }

        // Update the transaction amount after processing the refund
        transaction.setAmount(transaction.getAmount() - refundAmount);

        // Save the updated transaction
        transactionRepository.save(transaction);
        logger.info("Refund processed successfully for transaction ID: " + transaction.getId());
    }

    @Transactional
    private void sendToSqs(TransactionRequest transactionRequest) {

        try {
            TransactionRequest transactionObject = new TransactionRequest(
                    transactionRequest.getUserId(),
                    transactionRequest.getCreditDebitIndicator(),
                    transactionRequest.getTransactionType(),
                    transactionRequest.getAmount(),
                    transactionRequest.getCurrencyCode(),
                    transactionRequest.getMerchantId(),
                    transactionRequest.getMerchantName(),
                    transactionRequest.getMerchantCategoryCode());

            ObjectMapper convertingTransactionRequestObjectToJson = new ObjectMapper();
            String convertedObjectInJson = convertingTransactionRequestObjectToJson.writeValueAsString(transactionObject);
            sqsTemplate.send("https://sqs.ap-south-1.amazonaws.com/992382815412/MyTransactionQueue",
                    convertedObjectInJson);
        } catch (Exception e) {
            logger.info("Got error while converting the object to Json  error: " + e.getMessage());
        }
    }

    @Transactional
    public String makeTransaction(TransactionRequest transactionRequest) {
        sendToSqs(transactionRequest);
        return "Transaction successful";
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

}



