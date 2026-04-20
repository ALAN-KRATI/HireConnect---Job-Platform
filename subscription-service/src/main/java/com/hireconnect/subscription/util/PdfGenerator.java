package com.hireconnect.subscription.util;

import com.hireconnect.subscription.dto.InvoiceResponseDTO;
import com.hireconnect.subscription.entity.Invoice;
import com.hireconnect.subscription.entity.Subscription;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Component
public class PdfGenerator {

    public byte[] generateInvoicePdf(Invoice invoice, Subscription subscription, String recruiterName, String recruiterEmail) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            
            String html = buildInvoiceHtml(invoice, subscription, recruiterName, recruiterEmail);
            
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, "file:///");
            builder.toStream(outputStream);
            builder.run();
            
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF invoice", e);
        }
    }

    private String buildInvoiceHtml(Invoice invoice, Subscription subscription, String recruiterName, String recruiterEmail) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm");
        String invoiceDate = invoice.getPaymentDate() != null ? 
            invoice.getPaymentDate().format(formatter) : "Pending";
        
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 40px; color: #333; }
                    .header { border-bottom: 3px solid #2563eb; padding-bottom: 20px; margin-bottom: 30px; }
                    .company-name { font-size: 28px; font-weight: bold; color: #2563eb; }
                    .invoice-title { font-size: 24px; margin-top: 10px; color: #666; }
                    .section { margin-bottom: 25px; }
                    .section-title { font-size: 16px; font-weight: bold; color: #2563eb; margin-bottom: 10px; }
                    .info-row { display: flex; justify-content: space-between; margin: 5px 0; }
                    .label { color: #666; }
                    .value { font-weight: 500; }
                    .table { width: 100%%; border-collapse: collapse; margin: 20px 0; }
                    .table th { background-color: #2563eb; color: white; padding: 12px; text-align: left; }
                    .table td { padding: 12px; border-bottom: 1px solid #ddd; }
                    .total-section { background-color: #f3f4f6; padding: 20px; border-radius: 8px; margin-top: 30px; }
                    .total-row { display: flex; justify-content: space-between; font-size: 18px; font-weight: bold; }
                    .status-paid { color: #059669; font-weight: bold; }
                    .status-pending { color: #d97706; font-weight: bold; }
                    .footer { margin-top: 40px; padding-top: 20px; border-top: 1px solid #ddd; text-align: center; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="header">
                    <div class="company-name">HireConnect</div>
                    <div class="invoice-title">INVOICE</div>
                </div>
                
                <div class="section">
                    <div class="section-title">Invoice Details</div>
                    <div class="info-row">
                        <span class="label">Invoice Number:</span>
                        <span class="value">INV-%d</span>
                    </div>
                    <div class="info-row">
                        <span class="label">Transaction ID:</span>
                        <span class="value">%s</span>
                    </div>
                    <div class="info-row">
                        <span class="label">Payment Date:</span>
                        <span class="value">%s</span>
                    </div>
                    <div class="info-row">
                        <span class="label">Payment Status:</span>
                        <span class="value %s">%s</span>
                    </div>
                </div>
                
                <div class="section">
                    <div class="section-title">Bill To</div>
                    <div class="value">%s</div>
                    <div class="value">%s</div>
                </div>
                
                <table class="table">
                    <thead>
                        <tr>
                            <th>Description</th>
                            <th>Plan</th>
                            <th>Period</th>
                            <th>Amount (INR)</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>Subscription Fee</td>
                            <td>%s</td>
                            <td>%s to %s</td>
                            <td>₹%.2f</td>
                        </tr>
                    </tbody>
                </table>
                
                <div class="total-section">
                    <div class="total-row">
                        <span>Total Amount Paid:</span>
                        <span>₹%.2f</span>
                    </div>
                </div>
                
                <div class="footer">
                    <p>Thank you for choosing HireConnect!</p>
                    <p>For any queries, please contact support@hireconnect.com</p>
                    <p>This is a computer generated invoice and does not require signature.</p>
                </div>
            </body>
            </html>
            """.formatted(
                invoice.getInvoiceId(),
                invoice.getTransactionId() != null ? invoice.getTransactionId() : "N/A",
                invoiceDate,
                invoice.getPaymentStatus() != null && invoice.getPaymentStatus().toString().equals("COMPLETED") ? "status-paid" : "status-pending",
                invoice.getPaymentStatus() != null ? invoice.getPaymentStatus() : "PENDING",
                recruiterName != null ? recruiterName : "Recruiter",
                recruiterEmail != null ? recruiterEmail : "N/A",
                subscription.getPlan() != null ? subscription.getPlan().name() : "N/A",
                subscription.getStartDate() != null ? subscription.getStartDate() : "N/A",
                subscription.getEndDate() != null ? subscription.getEndDate() : "N/A",
                invoice.getAmount(),
                invoice.getAmount()
            );
    }
}
