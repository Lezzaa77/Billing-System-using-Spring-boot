package com.billing.util;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Very small PDF writer (A4, monospaced text, automatic page breaks).
 * No external library needed - enough for simple text reports.
 */
public final class SimplePdf {

    private static final float PAGE_W = 595f;
    private static final float PAGE_H = 842f;
    private static final float MARGIN = 40f;

    private final List<StringBuilder> pages = new ArrayList<>();
    private StringBuilder current;
    private float y;

    public SimplePdf() {
        newPage();
    }

    private void newPage() {
        current = new StringBuilder();
        pages.add(current);
        y = PAGE_H - MARGIN;
    }

    /** Adds one line of text. Courier is used so columns line up (6 pt per character at size 10). */
    public void line(String text, boolean bold, float size) {
        float lead = size * 1.35f;
        if (y - lead < MARGIN) newPage();
        y -= lead;
        current.append("BT /").append(bold ? "F2" : "F1").append(' ').append(num(size)).append(" Tf ")
                .append(num(MARGIN)).append(' ').append(num(y)).append(" Td (")
                .append(escape(text)).append(") Tj ET\n");
    }

    public void line(String text) {
        line(text, false, 10f);
    }

    public void blank() {
        y -= 10f * 1.35f;
        if (y < MARGIN) newPage();
    }

    /** Builds the finished PDF file as bytes. */
    public byte[] toBytes() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        List<Integer> offsets = new ArrayList<>();
        int n = pages.size();

        write(out, "%PDF-1.4\n");

        offsets.add(out.size());
        write(out, "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");

        StringBuilder kids = new StringBuilder();
        for (int i = 0; i < n; i++) kids.append(5 + 2 * i).append(" 0 R ");
        offsets.add(out.size());
        write(out, "2 0 obj\n<< /Type /Pages /Kids [" + kids + "] /Count " + n + " >>\nendobj\n");

        offsets.add(out.size());
        write(out, "3 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Courier /Encoding /WinAnsiEncoding >>\nendobj\n");
        offsets.add(out.size());
        write(out, "4 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Courier-Bold /Encoding /WinAnsiEncoding >>\nendobj\n");

        for (int i = 0; i < n; i++) {
            int pageObj = 5 + 2 * i;
            int contentObj = pageObj + 1;
            offsets.add(out.size());
            write(out, pageObj + " 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] "
                    + "/Resources << /Font << /F1 3 0 R /F2 4 0 R >> >> /Contents " + contentObj + " 0 R >>\nendobj\n");

            byte[] content = pages.get(i).toString().getBytes(StandardCharsets.ISO_8859_1);
            offsets.add(out.size());
            write(out, contentObj + " 0 obj\n<< /Length " + content.length + " >>\nstream\n");
            out.writeBytes(content);
            write(out, "\nendstream\nendobj\n");
        }

        int xref = out.size();
        int total = offsets.size() + 1;
        write(out, "xref\n0 " + total + "\n0000000000 65535 f \n");
        for (int off : offsets) {
            write(out, String.format(Locale.US, "%010d 00000 n \n", off));
        }
        write(out, "trailer\n<< /Size " + total + " /Root 1 0 R >>\nstartxref\n" + xref + "\n%%EOF\n");

        return out.toByteArray();
    }

    private static void write(ByteArrayOutputStream out, String s) {
        out.writeBytes(s.getBytes(StandardCharsets.ISO_8859_1));
    }

    private static String num(float f) {
        return String.format(Locale.US, "%.2f", f);
    }

    /** Escapes PDF string characters; anything outside Latin-1 becomes '?'. */
    private static String escape(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c == '\\' || c == '(' || c == ')') sb.append('\\').append(c);
            else if (c < 32) sb.append(' ');
            else if (c > 255) sb.append('?');
            else sb.append(c);
        }
        return sb.toString();
    }
}
