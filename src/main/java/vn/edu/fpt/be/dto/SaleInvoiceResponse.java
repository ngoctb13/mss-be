package vn.edu.fpt.be.dto;

import vn.edu.fpt.be.model.Customer;

public class SaleInvoiceResponse {
    private Long id;
    private Double totalPrice;
    private Double oldDebt;
    private Double totalPayment;
    private Double pricePaid;
    private Double newDebt;
    private Customer customer;
}
