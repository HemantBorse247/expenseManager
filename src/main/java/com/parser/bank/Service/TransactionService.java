package com.parser.bank.Service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//import com.example.banktransactionanalyzer.model.Transaction;
//import com.example.banktransactionanalyzer.repository.TransactionRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.parser.bank.DTO.MonthlySummaryResponse;
import com.parser.bank.DTO.UploadResponse;
import com.parser.bank.Entity.Transaction;
import com.parser.bank.Repository.TransactionRepository;

@Service
public class TransactionService {

	private final TransactionRepository transactionRepository;

	final List<String> categoryList = Arrays.asList("bills", "entertainment", "food", "utility", "travel", "snacks",
			"groceries", "fruits", "rent", "auto", "bus", "tea", "dmart", "haircut", "swiggy", "zepto", "zomato",
			"blinkit", "instamart", "shopping");

	public TransactionService(TransactionRepository transactionRepository) {
		this.transactionRepository = transactionRepository;
	}

	public List<Transaction> processExcelFile(MultipartFile file) throws IOException {
		List<Transaction> transactions = new ArrayList<>();
		try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {

			Sheet sheet = workbook.getSheetAt(0); // Assuming data is on the first sheet
			Iterator<Row> rows = sheet.iterator();

			// Assuming the first row is header, skip it
			if (rows.hasNext()) {
				rows.next();
			}

			while (rows.hasNext()) {
				Row currentRow = rows.next();
				// Skip empty rows
				if (isRowEmpty(currentRow)) {
					continue;
				}

				Transaction transaction = new Transaction();
				// date
				try {
					Cell dateCell = currentRow.getCell(0);
					if (dateCell != null && dateCell.getCellType() == CellType.NUMERIC
							&& DateUtil.isCellDateFormatted(dateCell)) {
						transaction.setTransactionDate(
								dateCell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
					} else if (dateCell != null && dateCell.getCellType() == CellType.STRING) {
						String dateString = dateCell.getStringCellValue();
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uu")
								.withResolverStyle(ResolverStyle.STRICT);
						LocalDate localDate = LocalDate.parse(dateString, formatter);
						transaction.setTransactionDate(localDate);
					}

					// Withdrawal Amount
					Cell withdrawalCell = currentRow.getCell(4);
					if (withdrawalCell != null) {
						if (withdrawalCell.getCellType() == CellType.NUMERIC) {
							transaction.setWithdrawalAmount(withdrawalCell.getNumericCellValue());
						} else if (withdrawalCell.getCellType() == CellType.STRING) {
							try {
								transaction
										.setWithdrawalAmount(Double.parseDouble(withdrawalCell.getStringCellValue()));
							} catch (NumberFormatException e) {
								transaction.setWithdrawalAmount(0.0); // Or handle error
							}
						}
					} else {
						transaction.setWithdrawalAmount(0.0);
					}

					// Deposit Amount
					Cell depositCell = currentRow.getCell(5);
					if (depositCell != null) {
						if (depositCell.getCellType() == CellType.NUMERIC) {
							transaction.setDepositAmount(depositCell.getNumericCellValue());
						} else if (depositCell.getCellType() == CellType.STRING) {
							try {
								transaction.setDepositAmount(Double.parseDouble(depositCell.getStringCellValue()));
							} catch (NumberFormatException e) {
								transaction.setDepositAmount(0.0); // Or handle error
							}
						}
					} else {
						transaction.setDepositAmount(0.0);
					}

					// Closing Balance
					Cell closingBalanceCell = currentRow.getCell(6);
					if (closingBalanceCell != null) {
						if (closingBalanceCell.getCellType() == CellType.NUMERIC) {
							transaction.setClosingBalance(closingBalanceCell.getNumericCellValue());
						} else if (closingBalanceCell.getCellType() == CellType.STRING) {
							try {
								transaction
										.setClosingBalance(Double.parseDouble(closingBalanceCell.getStringCellValue()));
							} catch (NumberFormatException e) {
								transaction.setClosingBalance(0.0); // Or handle error
							}
						}
					} else {
						transaction.setClosingBalance(0.0);
					}

					Cell refNoCell = currentRow.getCell(2);
					if (refNoCell != null) {
						if (refNoCell.getCellType() == CellType.NUMERIC) {
							transaction.setId(String.valueOf(refNoCell.getNumericCellValue()));
						} else if (refNoCell.getCellType() == CellType.STRING) {
							try {
								transaction.setId(refNoCell.getStringCellValue());
							} catch (NumberFormatException e) {
								System.out.println(e);
//								transaction.setClosingBalance(0.0); // Or handle error
							}
						}
					} else {
						continue;
					}

					// Description (assuming column 4)
					Cell descriptionCell = currentRow.getCell(1);
					if (descriptionCell != null) {

						transaction.setDescription(descriptionCell.getStringCellValue());
						transaction.setCategory(this.setCategory(descriptionCell.getStringCellValue()));
					}

					transactions.add(transaction);
				} catch (Exception e) {
					// Log the error and potentially skip the row or throw a custom exception
					System.err
							.println("Error processing row: " + currentRow.getRowNum() + ". Error: " + e.getMessage());
				}
			}
		}

		return transactionRepository.saveAll(transactions);
	}

	private String setCategory(String description) {
		String[] descArray = description.split("-");
		String category = descArray[descArray.length - 1];
		if (categoryList.contains(category.toLowerCase())) {
			return category;
		} else
			return "miscellaneous";

	}

	private boolean isRowEmpty(Row row) {
		if (row == null) {
			return true;
		}
		for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
			Cell cell = row.getCell(cellNum);
			if (cell != null && cell.getCellType() != CellType.BLANK && cell.getRichStringCellValue().length() > 0) {
				return false;
			}
		}
		return true;
	}

	public Map<String, Double> calculateMonthlySpend() {
		List<Transaction> allTransactions = transactionRepository.findAll();

		return allTransactions.stream().filter(t -> t.getWithdrawalAmount() != null && t.getWithdrawalAmount() > 0)
				.collect(Collectors.groupingBy(transaction -> {
					LocalDate date = transaction.getTransactionDate();
					return date.getYear() + "-" + String.format("%02d", date.getMonthValue());
				}, Collectors.summingDouble(Transaction::getWithdrawalAmount)));
	}

	public List<Transaction> getAllTransactions() {
		return transactionRepository.findAll();
	}

	public ResponseEntity<UploadResponse> setCategoryForTransactions() {
		List<Transaction> allTransactions = transactionRepository.findAll();
		allTransactions.forEach(t -> {
			t.setCategory(setCategory(t.getDescription()));
			transactionRepository.save(t);
		});
		UploadResponse response = new UploadResponse("File uploaded and processed successfully.",
				allTransactions.size());
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	public MonthlySummaryResponse getCustomPeriodSummary(Date startDate, Date endDate) {
		if(startDate == null || endDate == null) {
			return new MonthlySummaryResponse();
		}
		Map<String, Double> categorySpendMap = new HashMap<>();

		List<Transaction> monthlyTransactions = transactionRepository.findByTransactionDateBetween(startDate.toLocalDate(), endDate.toLocalDate());
		double totalWithdrawal = monthlyTransactions.stream().filter(t -> t.getWithdrawalAmount() != null)
				.mapToDouble(Transaction::getWithdrawalAmount).sum();

		monthlyTransactions.stream().forEach(t -> {
			if (t.getWithdrawalAmount() != null && t.getWithdrawalAmount() > 0) {
				String category = t.getCategory();
				if (!categorySpendMap.containsKey(category)) {
					categorySpendMap.put(category, t.getWithdrawalAmount());
				} else {
					Double spend = categorySpendMap.get(category);
					categorySpendMap.put(category, t.getWithdrawalAmount() + spend);
				}
			}
		});

		double totalDeposit = monthlyTransactions.stream().filter(t -> t.getDepositAmount() != null)
				.mapToDouble(Transaction::getDepositAmount).sum();

		long numberOfTransactions = monthlyTransactions.size();

		// Calculate net flow (deposit - withdrawal)
		double netFlow = totalDeposit - totalWithdrawal;
		
		return new MonthlySummaryResponse(totalWithdrawal, totalDeposit, numberOfTransactions, netFlow, categorySpendMap);
	}

//	public MonthlySummaryResponse getCustomPeriodSummary(Date startDate, Date endDate) {
//
//
//		return new MonthlySummaryResponse();
//	}

	public Map<String, Double> getYearlyFinancialSummary(int year) {
		YearMonth yearMonth = YearMonth.of(year, 1);
		YearMonth yearLastMonth = YearMonth.of(year, 12);
		LocalDate startDate = yearMonth.atDay(1);
		LocalDate endDate = yearLastMonth.atEndOfMonth();

		List<Transaction> monthlyTransactions = transactionRepository.findByTransactionDateBetween(startDate, endDate);
//		List<Transaction> top10Transactions = monthlyTransactions.stream().forEach();

		double totalWithdrawal = monthlyTransactions.stream().filter(t -> t.getWithdrawalAmount() != null)
				.mapToDouble(Transaction::getWithdrawalAmount).sum();

		double totalDeposit = monthlyTransactions.stream().filter(t -> t.getDepositAmount() != null)
				.mapToDouble(Transaction::getDepositAmount).sum();

		long numberOfTransactions = monthlyTransactions.size();

		// Calculate net flow (deposit - withdrawal)
		double netFlow = totalDeposit - totalWithdrawal;

		Map<String, Double> summary = new HashMap<>();
		summary.put("total_withdrawal", totalWithdrawal);
		summary.put("total_deposit", totalDeposit);
		summary.put("num_transactions", (double) numberOfTransactions); // Convert long to Double
		summary.put("net_flow", netFlow);

		return summary;
	}
}