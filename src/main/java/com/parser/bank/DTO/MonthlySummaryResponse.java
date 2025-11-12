package com.parser.bank.DTO;

import java.util.Map;

			public class MonthlySummaryResponse {
				Double totalWithdrawal;
				Double totalDeposit;
				Long numberOfTransactions;
				Double netFlow;
			//	Map<String, Double> categorySpendMap;

	public MonthlySummaryResponse() {
//		super();
		totalWithdrawal = 0d;
		totalDeposit = 0d;
		numberOfTransactions = 0l;
		netFlow = 0d;
	}

	public MonthlySummaryResponse(Double totalWithdrawal, Double totalDeposit, Long numberOfTransactions, Double netFlow,
			Map<String, Double> categorySpendMap) {
		super();
		this.totalWithdrawal = totalWithdrawal;
		this.totalDeposit = totalDeposit;
		this.numberOfTransactions = numberOfTransactions;
		this.netFlow = netFlow;
//		this.categorySpendMap = categorySpendMap;
	}

	public Double getTotalWithdrawal() {
		return totalWithdrawal;
	}

	public void setTotalWithdrawal(Double totalWithdrawal) {
		this.totalWithdrawal = totalWithdrawal;
	}

	public Double getTotalDeposit() {
		return totalDeposit;
	}

	public void setTotalDeposit(Double totalDeposit) {
		this.totalDeposit = totalDeposit;
	}

	public Long getNumberOfTransactions() {
		return numberOfTransactions;
	}

	public void setNumberOfTransactions(Long numberOfTransactions) {
		this.numberOfTransactions = numberOfTransactions;
	}

	public Double getNetFlow() {
		return netFlow;
	}

	public void setNetFlow(Double netFlow) {
		this.netFlow = netFlow;
	}

//	public Map<String, Double> getCategorySpendMap() {
//		return categorySpendMap;
//	}

//	public void setCategorySpendMap(Map<String, Double> categorySpendMap) {
//		this.categorySpendMap = categorySpendMap;
//	}

}
