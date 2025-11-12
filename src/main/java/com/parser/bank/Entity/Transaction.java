package com.parser.bank.Entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "transactions")

public class Transaction {
	@Id
	private String id;
	private LocalDate transactionDate;
	private Double withdrawalAmount;
	private Double depositAmount;
	private Double closingBalance;
	private String description; // narration
	private String category;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public LocalDate getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(LocalDate transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Double getWithdrawalAmount() {
		return withdrawalAmount;
	}

	public void setWithdrawalAmount(Double withdrawalAmount) {
		this.withdrawalAmount = withdrawalAmount;
	}

	public Double getDepositAmount() {
		return depositAmount;
	}

	public void setDepositAmount(Double depositAmount) {
		this.depositAmount = depositAmount;
	}

	public Double getClosingBalance() {
		return closingBalance;
	}

	public void setClosingBalance(Double closingBalance) {
		this.closingBalance = closingBalance;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Transaction(String id, LocalDate transactionDate, Double withdrawalAmount, Double depositAmount,
			Double closingBalance, String description, String category) {
		super();
		this.id = id;
		this.transactionDate = transactionDate;
		this.withdrawalAmount = withdrawalAmount;
		this.depositAmount = depositAmount;
		this.closingBalance = closingBalance;
		this.description = description;
		this.category = category;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Transaction() {
		super();
		// TODO Auto-generated constructor stub
	}

}
