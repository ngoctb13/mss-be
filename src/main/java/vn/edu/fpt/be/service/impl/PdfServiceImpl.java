package vn.edu.fpt.be.service.impl;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.model.SaleInvoice;
import vn.edu.fpt.be.model.SaleInvoiceDetail;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.repository.SaleInvoiceDetailRepository;
import vn.edu.fpt.be.repository.SaleInvoiceRepository;
import vn.edu.fpt.be.service.PDFService;
import vn.edu.fpt.be.service.UserService;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PdfServiceImpl implements PDFService {
    private final SaleInvoiceRepository saleInvoiceRepository;
    private final SaleInvoiceDetailRepository saleInvoiceDetailRepository;
    private final UserService userService;

    private static final String FONT_FILE = "src/main/resources/fonts/Trirong-Regular.ttf";
    @Override
    public ByteArrayInputStream generateInvoicePdf(Long saleInvoiceId) throws Exception {

        PdfFont font = PdfFontFactory.createFont(FONT_FILE, PdfEncodings.IDENTITY_H, true);

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

            document.add(new Paragraph(saleInvoice.get().getStore().getStoreName()).setTextAlignment(TextAlignment.LEFT).setBold());
            document.add(new Paragraph("Địa chỉ: " + saleInvoice.get().getStore().getAddress()).setTextAlignment(TextAlignment.LEFT));
            document.add(new Paragraph("Điện thoại: " + saleInvoice.get().getStore().getPhoneNumber()).setTextAlignment(TextAlignment.LEFT));
            document.add(new Paragraph("HÓA ĐƠN BÁN HÀNG").setTextAlignment(TextAlignment.CENTER).setBold());
            document.add(new Paragraph("Ngày " + saleInvoice.get().getCreatedAt()).setTextAlignment(TextAlignment.CENTER));

            // Add customer information
            document.add(new Paragraph("Khách hàng: " + saleInvoice.get().getCustomer().getCustomerName())
                    .setTextAlignment(TextAlignment.LEFT));
            document.add(new Paragraph("Điện thoại: " + saleInvoice.get().getCustomer().getPhoneNumber())
                    .setTextAlignment(TextAlignment.LEFT));
            document.add(new Paragraph("Địa chỉ: " + saleInvoice.get().getCustomer().getAddress())
                    .setTextAlignment(TextAlignment.LEFT));

            Table table = new Table(new float[]{1, 3, 1, 1, 1, 1});
            table.setWidth(530);

            // Thêm tiêu đề cột
            table.addHeaderCell("TT");
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
                table.addCell(new Cell().add(new Paragraph(String.valueOf(detail.getUnitPrice()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(detail.getTotalPrice()))));
            }
            document.add(table);

            Div totalsDiv = new Div();
            totalsDiv.setPaddingRight(30);
            // Add paragraphs with totals to the Div
            totalsDiv.add(new Paragraph("Tổng cộng tiền hàng: " + String.format("%,.2f", saleInvoice.get().getTotalPrice()))
                    .setTextAlignment(TextAlignment.RIGHT));
            totalsDiv.add(new Paragraph("Thành tiền: " + String.format("%,.2f", saleInvoice.get().getTotalPrice()))
                    .setTextAlignment(TextAlignment.RIGHT));
            totalsDiv.add(new Paragraph("_____________________________")
                    .setTextAlignment(TextAlignment.RIGHT));
            totalsDiv.add(new Paragraph("Nợ cũ: " + String.format("%,.2f", saleInvoice.get().getOldDebt()))
                    .setTextAlignment(TextAlignment.RIGHT));
            totalsDiv.add(new Paragraph("Đã thanh toán: " + String.format("%,.2f", saleInvoice.get().getPricePaid()))
                    .setTextAlignment(TextAlignment.RIGHT));
            totalsDiv.add(new Paragraph("Còn lại: " + String.format("%,.2f", saleInvoice.get().getNewDebt()))
                    .setTextAlignment(TextAlignment.RIGHT));

// Add the Div to the document
            document.add(totalsDiv);

            document.add(new Paragraph("Khách hàng (Ký, họ tên)")
                    .setTextAlignment(TextAlignment.LEFT));


            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
