package vn.edu.fpt.be.service.impl;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.dto.response.DebtPaymentResponse;
import vn.edu.fpt.be.model.Customer;
import vn.edu.fpt.be.model.SaleInvoice;
import vn.edu.fpt.be.model.SaleInvoiceDetail;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.model.enums.RecordType;
import vn.edu.fpt.be.repository.CustomerRepository;
import vn.edu.fpt.be.repository.DebtPaymentHistoryRepository;
import vn.edu.fpt.be.repository.SaleInvoiceDetailRepository;
import vn.edu.fpt.be.repository.SaleInvoiceRepository;
import vn.edu.fpt.be.service.DebtPaymentHistoryService;
import vn.edu.fpt.be.service.PDFService;
import vn.edu.fpt.be.service.UserService;
import com.itextpdf.layout.Document;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PdfServiceImpl implements PDFService {
    private final SaleInvoiceRepository saleInvoiceRepository;
    private final SaleInvoiceDetailRepository saleInvoiceDetailRepository;
    private final CustomerRepository customerRepository;
    private final DebtPaymentHistoryService debtPaymentHistoryService;
    private final UserService userService;
    @Value("${mss.app.fonts-url}")
    private String fontsURL;

    @Override
    public ByteArrayInputStream generateInvoicePdf(Long saleInvoiceId) throws Exception {


        PdfFont font = PdfFontFactory.createFont(fontsURL, PdfEncodings.IDENTITY_H, true);

        User currentUser = userService.getCurrentUser();
        Optional<SaleInvoice> saleInvoice = saleInvoiceRepository.findById(saleInvoiceId);
        if (saleInvoice.isEmpty()) {
            throw new RuntimeException("Not found any invoice with id " + saleInvoiceId);
        }
        if (saleInvoice.get().getStore() != currentUser.getStore()) {
            throw new RuntimeException("This invoice not belong to current store");
        }
        List<SaleInvoiceDetail> saleInvoiceDetailList = saleInvoiceDetailRepository.findBySaleInvoiceId(saleInvoiceId);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(out);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {
            document.setFont(font);

            float[] columnWidths = {1, 1};
            Table headerTable = new Table(columnWidths);
            headerTable.useAllAvailableWidth();
            Cell leftCell = new Cell().add(new Paragraph(saleInvoice.get().getStore().getStoreName())
                            .setBold().setFontSize(20).setFixedLeading(25).setMarginTop(5))
                    .add(new Paragraph("Địa chỉ: " + saleInvoice.get().getStore().getAddress()).setFixedLeading(23))
                    .add(new Paragraph("Điện thoại: " + saleInvoice.get().getStore().getPhoneNumber()).setFixedLeading(23));
            leftCell.setBorder(Border.NO_BORDER);
            Cell rightCell = new Cell().add(new Paragraph("Chuyên cung cấp các loại gạo ngon, chất lượng cao").setTextAlignment(TextAlignment.RIGHT))
                    .add(new Paragraph("Phân phối gạo sạch toàn quốc").setTextAlignment(TextAlignment.RIGHT))
                    .add(new Paragraph("Gạo thơm ngon từ các vùng miền Việt Nam").setTextAlignment(TextAlignment.RIGHT))
                    .add(new Paragraph("Đảm bảo 100% gạo sạch, không chất bảo quản").setTextAlignment(TextAlignment.RIGHT));
            rightCell.setBorder(Border.NO_BORDER);
            headerTable.addCell(leftCell);
            headerTable.addCell(rightCell);
            document.add(headerTable);

            document.add(new Paragraph("HÓA ĐƠN BÁN HÀNG").setTextAlignment(TextAlignment.CENTER).setBold().setFontSize(20).setFixedLeading(18));
            document.add(new Paragraph("Ngày " + saleInvoice.get().getCreatedAt()).setTextAlignment(TextAlignment.CENTER).setFixedLeading(18));

            // Add customer information
            document.add(new Paragraph("Khách hàng: " + saleInvoice.get().getCustomer().getCustomerName())
                    .setTextAlignment(TextAlignment.LEFT).setFixedLeading(18).setBold());
            document.add(new Paragraph("Điện thoại: " + saleInvoice.get().getCustomer().getPhoneNumber())
                    .setTextAlignment(TextAlignment.LEFT).setFixedLeading(18));
            document.add(new Paragraph("Địa chỉ: " + saleInvoice.get().getCustomer().getAddress())
                    .setTextAlignment(TextAlignment.LEFT).setFixedLeading(18));

            Table table = new Table(new float[]{1, 3, 1, 1, 1, 1});
            table.setWidth(530);

            // Thêm tiêu đề cột
            table.addHeaderCell("TT").setTextAlignment(TextAlignment.CENTER).setBold();
            table.addHeaderCell("Tên hàng");
            table.addHeaderCell("Đvt");
            table.addHeaderCell("Số lượng");
            table.addHeaderCell("Đơn giá");
            table.addHeaderCell("Thành tiền");

            int count = 1;
            for (SaleInvoiceDetail detail : saleInvoiceDetailList) {
                table.addCell(new Cell().add(new Paragraph(String.valueOf(count++))));
                table.addCell(new Cell().add(new Paragraph(detail.getProduct().getProductName())));
                table.addCell(new Cell().add(new Paragraph("Kg"))); // Đơn vị tính
                table.addCell(new Cell().add(new Paragraph(String.valueOf(detail.getQuantity()))));
                table.addCell(new Cell().add(new Paragraph(String.format("%,.2f",detail.getUnitPrice()))));
                table.addCell(new Cell().add(new Paragraph(String.format("%,.2f",detail.getTotalPrice()))));
            }
            document.add(table);

            LineSeparator separator = new LineSeparator(new SolidLine(1f)).setMarginRight(22);
            Div separatorDiv = new Div()
                    .add(separator)
                    .setWidth(200) // Đặt chiều rộng cố định cho Div
                    .setHorizontalAlignment(HorizontalAlignment.RIGHT); // Căn lề Div sang phải

            float[] footerColumnWidths = {1, 1, 1}; // Căn đều ba cột
            Table footerTable = new Table(footerColumnWidths);
            footerTable.useAllAvailableWidth();
            Cell customerSignatureCell = new Cell()
                    .add(new Paragraph("Khách hàng").setMarginTop(20).setTextAlignment(TextAlignment.CENTER))
                    .add(new Paragraph("(Ký, họ tên)").setTextAlignment(TextAlignment.CENTER).setFixedLeading(10).setItalic());
            customerSignatureCell.setBorder(Border.NO_BORDER);
            Cell issuerSignatureCell = new Cell()
                    .add(new Paragraph("Người lập phiếu").setMarginTop(20).setTextAlignment(TextAlignment.CENTER))
                    .add(new Paragraph("(Ký, họ tên)").setTextAlignment(TextAlignment.CENTER).setFixedLeading(10).setItalic());
            issuerSignatureCell.setBorder(Border.NO_BORDER);
            Cell summaryCell = new Cell()
                    .add(new Paragraph("Tổng cộng tiền hàng: " + String.format("%,.2f", saleInvoice.get().getTotalPrice()))
                            .setTextAlignment(TextAlignment.RIGHT).setMarginTop(10).setMarginRight(22).setBold())
                    .add(new Paragraph("Thành tiền: " + String.format("%,.2f", saleInvoice.get().getTotalPrice()))
                            .setTextAlignment(TextAlignment.RIGHT).setFixedLeading(25).setMarginRight(22))
                    .add(separatorDiv).setMarginRight(22)
                    .add(new Paragraph("Nợ cũ: " + String.format("%,.2f", saleInvoice.get().getOldDebt()))
                            .setTextAlignment(TextAlignment.RIGHT).setFixedLeading(25).setMarginRight(22))
                    .add(new Paragraph("Đã thanh toán: " + String.format("%,.2f", saleInvoice.get().getPricePaid()))
                            .setTextAlignment(TextAlignment.RIGHT).setFixedLeading(25).setMarginRight(22).setBold())
                    .add(separatorDiv).setMarginRight(22)
                    .add(new Paragraph("Còn lại: " + String.format("%,.2f", saleInvoice.get().getNewDebt()))
                            .setTextAlignment(TextAlignment.RIGHT).setFixedLeading(25).setMarginRight(22));
            summaryCell.setBorder(Border.NO_BORDER);
            footerTable.addCell(customerSignatureCell);
            footerTable.addCell(issuerSignatureCell);
            footerTable.addCell(summaryCell);
            document.add(footerTable);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public ByteArrayInputStream generateTransactionPdf(Long customerId, LocalDateTime startDate, LocalDateTime endDate) throws Exception {
        User currentUser = userService.getCurrentUser();
        Optional<Customer> customer = customerRepository.findById(customerId);
        PdfFont font = PdfFontFactory.createFont(fontsURL, PdfEncodings.IDENTITY_H, true);
        List<DebtPaymentResponse> transactions = debtPaymentHistoryService.getAllTransactionHistoryByCustomerAndDateRange(customerId, startDate, endDate);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(out);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            document.setFont(font);

            float[] columnWidths = {1, 1};
            Table headerTable = new Table(columnWidths);
            headerTable.useAllAvailableWidth();
            Cell leftCell = new Cell().add(new Paragraph(currentUser.getStore().getStoreName())
                            .setBold().setFontSize(20).setFixedLeading(25).setMarginTop(5))
                    .add(new Paragraph("Địa chỉ: " + currentUser.getStore().getAddress()).setFixedLeading(23))
                    .add(new Paragraph("Điện thoại: " + currentUser.getStore().getPhoneNumber()).setFixedLeading(23));
            leftCell.setBorder(Border.NO_BORDER);
            Cell rightCell = new Cell().add(new Paragraph("Chuyên cung cấp các loại gạo ngon, chất lượng cao").setTextAlignment(TextAlignment.RIGHT))
                    .add(new Paragraph("Phân phối gạo sạch toàn quốc").setTextAlignment(TextAlignment.RIGHT))
                    .add(new Paragraph("Gạo thơm ngon từ các vùng miền Việt Nam").setTextAlignment(TextAlignment.RIGHT))
                    .add(new Paragraph("Đảm bảo 100% gạo sạch, không chất bảo quản").setTextAlignment(TextAlignment.RIGHT));
            rightCell.setBorder(Border.NO_BORDER);
            headerTable.addCell(leftCell);
            headerTable.addCell(rightCell);
            document.add(headerTable);

            document.add(new Paragraph("LỊCH SỬ SAO KÊ").setTextAlignment(TextAlignment.CENTER).setBold().setFontSize(20).setFixedLeading(18).setMarginTop(10));

            // Add customer information
            document.add(new Paragraph("Kính gửi quý khách hàng: " + customer.get().getCustomerName())
                    .setTextAlignment(TextAlignment.LEFT).setFixedLeading(18).setBold());
            document.add(new Paragraph("Điện thoại: " + customer.get().getPhoneNumber())
                    .setTextAlignment(TextAlignment.LEFT).setFixedLeading(18));
            document.add(new Paragraph("Địa chỉ: " + customer.get().getAddress())
                    .setTextAlignment(TextAlignment.LEFT).setFixedLeading(18));
            document.add(new Paragraph("Cửa hàng xin trân trọng thông báo Sao kê giao dịch của khách hàng như sau: ")
                    .setTextAlignment(TextAlignment.LEFT).setFixedLeading(18).setItalic());
            document.add(new Paragraph("Từ ngày: " + startDate + " đến ngày: " + endDate)
                    .setTextAlignment(TextAlignment.RIGHT).setFixedLeading(18).setItalic().setBold());

            float[] transactionColumnWidths = {1, 3, 3, 2, 3};
            Table transactionTable = new Table(transactionColumnWidths);
            transactionTable.useAllAvailableWidth();

            // Add headers to the transaction table
            transactionTable.addHeaderCell(new Cell().add(new Paragraph("TT")).setBold());
            transactionTable.addHeaderCell(new Cell().add(new Paragraph("Số lượng")).setBold());
            transactionTable.addHeaderCell(new Cell().add(new Paragraph("Ngày lập phiếu")).setBold());
            transactionTable.addHeaderCell(new Cell().add(new Paragraph("Loại")).setBold());
            transactionTable.addHeaderCell(new Cell().add(new Paragraph("Ghi chú")).setBold());

            int count = 1;
            for (DebtPaymentResponse transaction : transactions) {
                String displayText;
                com.itextpdf.kernel.colors.Color textColor;

                if (transaction.getType() == RecordType.SALE_INVOICE) {
                    displayText = "NỢ";
                    textColor = ColorConstants.RED;
                } else { // Assume the only other type is PAYMENT
                    displayText = "TRẢ";
                    textColor = ColorConstants.BLUE;
                }
                Text typeText = new Text(displayText).setFontColor(textColor);

                transactionTable.addCell(new Cell().add(new Paragraph(String.valueOf(count++))));
                transactionTable.addCell(new Cell().add(new Paragraph(String.format("%,.2f", transaction.getAmount()))));
                transactionTable.addCell(new Cell().add(new Paragraph(transaction.getRecordDate().toString())));
                transactionTable.addCell(new Cell().add(new Paragraph(typeText)));
                transactionTable.addCell(new Cell().add(new Paragraph(transaction.getNote())));
            }

            document.add(transactionTable);
            document.add(new Paragraph("Cảm ơn quý khách và hẹn gặp lại!")
                    .setTextAlignment(TextAlignment.CENTER).setMarginTop(15).setFixedLeading(18).setItalic());
            document.close();
        } catch (Exception e) {

        }
        return new ByteArrayInputStream(out.toByteArray());
    }
}
