package Service;

import View.ThongKeController;
import Model.StatRow;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PDF Export Service
 * ✅ Separated concern - handles all PDF generation
 * ✅ Better maintainability and testability
 * ✅ Reusable across different controllers
 */
public class PdfExportService {

    private static final Logger LOGGER = Logger.getLogger(PdfExportService.class.getName());
    private static final Locale VI = new Locale("vi", "VN");
    private static final NumberFormat NF = NumberFormat.getIntegerInstance(VI);

    // PDF Layout Constants
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    private static final float MARGIN = 40f;
    private static final float LINE_HEIGHT = 16f;
    private static final float TABLE_ROW_HEIGHT = 18f;

    // Positions
    private static final float TITLE_SIZE = 18f;
    private static final float HEADING_SIZE = 12f;
    private static final float TEXT_SIZE = 10f;
    private static final float SMALL_TEXT_SIZE = 9f;

    // Colors
    private static final float[] COLOR_GREEN = {0.2f, 0.7f, 0.4f};
    private static final float[] COLOR_LIGHT_GREEN = {0.86f, 0.99f, 0.91f};

    /**
     * Generate PDF report
     */
    public File generatePDF(ThongKeController.Filter filter,
                           List<StatRow> data,
                           ThongKeController.StatSummary summary) throws IOException {
        
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                float y = PAGE_HEIGHT - MARGIN;

                y = drawTitle(cs, filter, y);
                y = drawDateInfo(cs, y);
                y = drawDivider(cs, y);
                y = drawSummarySection(cs, summary, y);
                y = drawDetailsSection(cs, data, summary, y);
                y = drawFooter(cs);
            }

            return saveFile(doc, filter);
        }
    }

    private float drawTitle(PDPageContentStream cs, ThongKeController.Filter filter, float y) throws IOException {
        cs.setFont(PDType1Font.HELVETICA_BOLD, TITLE_SIZE);
        cs.beginText();
        cs.newLineAtOffset(MARGIN, y);
        cs.showText("BAO CAO THONG KE - " + sa(filter.getLabel()).toUpperCase());
        cs.endText();
        return y - (TITLE_SIZE + 4);
    }

    private float drawDateInfo(PDPageContentStream cs, float y) throws IOException {
        cs.setFont(PDType1Font.HELVETICA, TEXT_SIZE);
        cs.beginText();
        cs.newLineAtOffset(MARGIN, y);
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        cs.showText("He thong quan ly san cau long | Ngay: " + dateStr);
        cs.endText();
        return y - (TEXT_SIZE + 4);
    }

    private float drawDivider(PDPageContentStream cs, float y) throws IOException {
        cs.setStrokingColor(COLOR_GREEN[0], COLOR_GREEN[1], COLOR_GREEN[2]);
        cs.setLineWidth(1.5f);
        cs.moveTo(MARGIN, y);
        cs.lineTo(PAGE_WIDTH - MARGIN, y);
        cs.stroke();
        return y - 16f;
    }

    private float drawSummarySection(PDPageContentStream cs, 
                                     ThongKeController.StatSummary summary, 
                                     float y) throws IOException {
        // Header
        cs.setFont(PDType1Font.HELVETICA_BOLD, HEADING_SIZE);
        cs.beginText();
        cs.newLineAtOffset(MARGIN, y);
        cs.showText("TONG QUAN");
        cs.endText();
        y -= LINE_HEIGHT;

        cs.setFont(PDType1Font.HELVETICA, TEXT_SIZE);
        String[] summaryLines = {
                "Tong doanh thu  : " + NF.format(summary.totalRevenue()) + " VND",
                "Luot dat san    : " + summary.totalBookings(),
                "Khach hang      : " + summary.totalCustomers(),
                "Trung binh/luot : " + NF.format(summary.avgRevenue()) + " VND"
        };

        for (String line : summaryLines) {
            cs.beginText();
            cs.newLineAtOffset(MARGIN + 10, y);
            cs.showText(line);
            cs.endText();
            y -= LINE_HEIGHT;
        }

        return y - 10f;
    }

    private float drawDetailsSection(PDPageContentStream cs, 
                                    List<StatRow> data,
                                    ThongKeController.StatSummary summary, 
                                    float y) throws IOException {
        // Header
        cs.setFont(PDType1Font.HELVETICA_BOLD, HEADING_SIZE);
        cs.beginText();
        cs.newLineAtOffset(MARGIN, y);
        cs.showText("CHI TIET");
        cs.endText();
        y -= LINE_HEIGHT;

        float[] colWidths = {150f, 120f, 70f, 90f, 110f};
        String[] headers = {"Giai doan", "Doanh thu", "Luot dat", "Khach hang", "TB/luot"};

        // Draw header row
        cs.setFont(PDType1Font.HELVETICA_BOLD, SMALL_TEXT_SIZE);
        drawTableRow(cs, MARGIN, y, colWidths, headers);
        y -= TABLE_ROW_HEIGHT;

        // Draw data rows
        cs.setFont(PDType1Font.HELVETICA, SMALL_TEXT_SIZE);
        for (StatRow row : data) {
            String[] rowData = {
                    row.name(),
                    NF.format(row.revenue()) + " đ",
                    String.valueOf(row.bookings()),
                    String.valueOf(row.customers()),
                    formatAverage(row)
            };
            drawTableRow(cs, MARGIN, y, colWidths, rowData);
            y -= TABLE_ROW_HEIGHT;
        }

        // Draw total row
        cs.setFont(PDType1Font.HELVETICA_BOLD, SMALL_TEXT_SIZE);
        String[] totalRow = {
                "TONG CONG",
                NF.format(summary.totalRevenue()) + " d",
                String.valueOf(summary.totalBookings()),
                String.valueOf(summary.totalCustomers()),
                NF.format(summary.avgRevenue()) + " d"
        };
        drawTableRow(cs, MARGIN, y, colWidths, totalRow);

        return y - 24f;
    }

    private float drawFooter(PDPageContentStream cs) throws IOException {
        cs.setFont(PDType1Font.HELVETICA, 8);
        cs.beginText();
        cs.newLineAtOffset(MARGIN, 30);
        cs.showText("Trang 1/1 | Quản lý sân cầu lông");
        cs.endText();
        return 30f;
    }

    private void drawTableRow(PDPageContentStream cs, float x, float y,
                             float[] colWidths, String[] cells) throws IOException {
        float cx = x;
        for (int i = 0; i < cells.length; i++) {
            cs.beginText();
            cs.newLineAtOffset(cx + 4, y);
            cs.showText(sa(truncateText(cells[i], 20)));  // strip accents!
            cs.endText();
            cx += colWidths[i];
        }
    }

    /**
     * Truncate text if too long for column
     */
    private String truncateText(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }

    /**
     * Format average revenue
     */
    private String formatAverage(StatRow row) {
        long bookings = Math.max(1, row.bookings());
        return NF.format(row.revenue() / bookings) + " đ";
    }

    /**
     * Save PDF file to Downloads folder
     */
    private File saveFile(PDDocument doc, ThongKeController.Filter filter) throws IOException {
        String fileName = "bao-cao-" + filter.name().toLowerCase()
                + "-" + System.currentTimeMillis() + ".pdf";
        
        File downloadDir = new File(System.getProperty("user.home"), "Downloads");
        if (!downloadDir.exists() && !downloadDir.mkdirs()) {
            LOGGER.warning("Failed to create Downloads directory");
        }
        
        File outputFile = new File(downloadDir, fileName);
        doc.save(outputFile);
        
        LOGGER.info("PDF saved to: " + outputFile.getAbsolutePath());

        if (java.awt.Desktop.isDesktopSupported()) {
            try { java.awt.Desktop.getDesktop().open(outputFile); }
            catch (Exception e) { LOGGER.log(java.util.logging.Level.WARNING, "Cannot open file", e); }
        }
        return outputFile;
    }

    /** Xóa dấu tiếng Việt → ASCII (PDType1Font chỉ hỗ trợ Latin) */
    private String sa(String s) {
        if (s == null) return "";
        String n = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD);
        return n.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replace('\u0111', 'd').replace('\u0110', 'D')
                .replace('\u01b0', 'u').replace('\u01af', 'U')
                .replace('\u01a1', 'o').replace('\u01a0', 'O')
                .replaceAll("[^\\x00-\\x7F]", "?");
    }
}