package com.parser.bank.DTO;

public class UploadResponse {
	private String message;
	private int transactionsSaved;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getTransactionsSaved() {
		return transactionsSaved;
	}

	public void setTransactionsSaved(int transactionsSaved) {
		this.transactionsSaved = transactionsSaved;
	}

	public UploadResponse(String message, int transactionsSaved) {
		super();
		this.message = message;
		this.transactionsSaved = transactionsSaved;
	}

	public UploadResponse() {
		super();
	}

}