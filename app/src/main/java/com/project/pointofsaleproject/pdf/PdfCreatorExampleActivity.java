package com.project.pointofsaleproject.pdf;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.text.HtmlCompat;

import com.project.pointofsaleproject.R;
import com.project.pointofsaleproject.model.Order;
import com.project.pointofsaleproject.model.Produk;
import com.tejpratapsingh.pdfcreator.activity.PDFCreatorActivity;
import com.tejpratapsingh.pdfcreator.utils.PDFUtil;
import com.tejpratapsingh.pdfcreator.views.PDFBody;
import com.tejpratapsingh.pdfcreator.views.PDFFooterView;
import com.tejpratapsingh.pdfcreator.views.PDFHeaderView;
import com.tejpratapsingh.pdfcreator.views.PDFTableView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFHorizontalView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFLineSeparatorView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFTextView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFVerticalView;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class PdfCreatorExampleActivity extends PDFCreatorActivity {
    Order dataOrder;
    ArrayList<Produk> dataProduk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataOrder= getIntent().getParcelableExtra("data_order");
        dataProduk = getIntent().getParcelableArrayListExtra("order_detail");

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        createPDF("point_of_sales", new PDFUtil.PDFUtilListener() {
            @Override
            public void pdfGenerationSuccess(File savedPDFFile) {
                Toast.makeText(PdfCreatorExampleActivity.this, "PDF Created", Toast.LENGTH_SHORT).show();
                Uri pdfUri = Uri.fromFile(savedPDFFile);
                Intent intentPdfViewer = new Intent(PdfCreatorExampleActivity.this, PdfViewerExampleActivity.class);
                intentPdfViewer.putExtra(PdfViewerExampleActivity.PDF_FILE_URI, pdfUri);
                finish();
                startActivity(intentPdfViewer);
            }

            @Override
            public void pdfGenerationFailure(Exception exception) {
                Toast.makeText(PdfCreatorExampleActivity.this, "PDF NOT Created", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected PDFHeaderView getHeaderView(int pageIndex) {
        PDFHeaderView headerView = new PDFHeaderView(getApplicationContext());

        PDFHorizontalView horizontalView = new PDFHorizontalView(getApplicationContext());

        PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.HEADER);
        SpannableString word = new SpannableString("Agen Ikan Jakabaring");
        word.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        pdfTextView.setText(word);
        pdfTextView.getView().setGravity(Gravity.CENTER);
        pdfTextView.getView().setTypeface(pdfTextView.getView().getTypeface(), Typeface.BOLD);

        horizontalView.addView(pdfTextView);

        headerView.addView(horizontalView);
        PDFLineSeparatorView lineSeparatorView1 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.WHITE);
        PDFLineSeparatorView lineSeparatorView2 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(R.color.overlay_dark_40);
        headerView.addView(lineSeparatorView1);
        headerView.addView(lineSeparatorView2);

        return headerView;
    }

    @Override
    protected PDFBody getBodyViews() {
        PDFBody pdfBody = new PDFBody();

        PDFLineSeparatorView lineSeparatorView = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.WHITE);

        PDFHorizontalView horizontalView = new PDFHorizontalView(getApplicationContext());

        PDFVerticalView verticalView1 = new PDFVerticalView(getApplicationContext());
        verticalView1.setLayout(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        
        PDFTextView vInvoice = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.H1);
        vInvoice.setText("INVOICE");
        vInvoice.setLayout(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        vInvoice.getView().setTypeface(vInvoice.getView().getTypeface(), Typeface.BOLD);
        verticalView1.addView(vInvoice);

        verticalView1.addView(pdfText(dataOrder.getKode()));

        horizontalView.addView(verticalView1);

        PDFVerticalView verticalView2 = new PDFVerticalView(getApplicationContext());
        verticalView2.setLayout(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        verticalView2.getView().setHorizontalGravity(Gravity.END);
        
        verticalView2.addView(pdfText(dataOrder.getJam() + " " + dataOrder.getTanggal()));
        verticalView2.addView(pdfText("Met. Kirim : " + dataOrder.getMetode_kirim()));
        verticalView2.addView(pdfText("Met. Bayar : " + dataOrder.getMetode_bayar()));
        
        horizontalView.addView(verticalView2);
        horizontalView.setPadding(0, 15, 0, 20);

        PDFTextView vCustomer = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.H1);
        vCustomer.setText(dataOrder.getCustomer());
        vCustomer.setLayout(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        vCustomer.getView().setTypeface(vCustomer.getView().getTypeface(), Typeface.BOLD);

        PDFVerticalView verticalView3 = new PDFVerticalView(getApplicationContext());
        verticalView3.setLayout(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        verticalView3.getView().setHorizontalGravity(Gravity.CENTER);
        verticalView3.setPadding(0, 10, 0, 10);
        verticalView3.addView(pdfTextWidthGravity("Pesanan",  PDFTextView.PDF_TEXT_SIZE.H2));

//
        int[] widthPercent = {10, 30, 20, 20, 20}; // Sum should be equal to 100%
        String[] textInTable = {"1", "2", "3", "4", "5"};
        String[] headerInTable = {"No", "Nama Produk", "Satuan/kg", "Banyak", "Total Harga"};
        PDFTableView.PDFTableRowView tableHeader = new PDFTableView.PDFTableRowView(getApplicationContext());
        for (String s : headerInTable) {
            PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            pdfTextView.setText(s);
            tableHeader.addToRow(pdfTextView);
        }

        tableHeader.setPadding(0, 10, 0, 10);

        PDFTableView.PDFTableRowView tableRowView1 = new PDFTableView.PDFTableRowView(getApplicationContext());
        PDFTableView tableView = new PDFTableView(getApplicationContext(), tableHeader, tableRowView1);

        for (int i = 0; i < dataProduk.size(); i++) {
            // Create 10 rows
            PDFTableView.PDFTableRowView tableRowView = new PDFTableView.PDFTableRowView(getApplicationContext());
            int harga = Integer.parseInt(dataProduk.get(i).getHarga());
            int qty = dataProduk.get(i).getQty();
            int sub_total = harga * qty;
            String[] dataInTable = {i+1+"", dataProduk.get(i).getNama(), "Rp. " + formatMoney(harga), "x" + qty, "Rp. " + formatMoney(sub_total)};
            for (String s : dataInTable) {
                PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
                pdfTextView.setText(s);
                tableRowView.addToRow(pdfTextView);
            }
            tableRowView.setPadding(0, 3, 0, 3);
            tableView.addRow(tableRowView);
        }
        tableView.setColumnWidth(widthPercent);

        PDFVerticalView verticalView4 = new PDFVerticalView(getApplicationContext());
        verticalView4.setLayout(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        verticalView4.getView().setHorizontalGravity(Gravity.END);
        verticalView4.setPadding(0, 6, 50, 6);
        verticalView4.addView(pdfTextWidthGravity("Total Bayar : Rp. " + formatMoney(Integer.parseInt(dataOrder.getHarga_total())),  PDFTextView.PDF_TEXT_SIZE.H3));

        PDFVerticalView verticalView5 = new PDFVerticalView(getApplicationContext());
        verticalView5.setLayout(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        verticalView5.getView().setHorizontalGravity(Gravity.CENTER);
        verticalView5.setPadding(0, 10, 0, 5);
        verticalView5.addView(pdfTextWidthGravity("Agen Ikan Jakabaring",  PDFTextView.PDF_TEXT_SIZE.P));

        PDFVerticalView verticalView6 = new PDFVerticalView(getApplicationContext());
        verticalView6.setLayout(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        verticalView6.getView().setHorizontalGravity(Gravity.CENTER);
        verticalView6.setPadding(0, 3, 0, 5);
        verticalView6.addView(pdfTextWidthGravity("Jl. Pangeran Ratu, 15 Ulu, Kecamatan Seberang Ulu I, Sumatera Selatan",  PDFTextView.PDF_TEXT_SIZE.P));


        PDFLineSeparatorView lineSeparatorViewL1 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.BLACK);
        PDFLineSeparatorView lineSeparatorViewL2 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.BLACK);
        PDFLineSeparatorView lineSeparatorViewL3 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.BLACK);
        PDFLineSeparatorView lineSeparatorViewL4 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.BLACK);

        pdfBody.addView(lineSeparatorViewL1);
        pdfBody.addView(horizontalView);
        pdfBody.addView(lineSeparatorView);
        pdfBody.addView(vCustomer);
        pdfBody.addView(verticalView3);
        pdfBody.addView(lineSeparatorViewL2);
        pdfBody.addView(tableView);
        pdfBody.addView(lineSeparatorViewL3);
        pdfBody.addView(verticalView4);
        pdfBody.addView(lineSeparatorViewL4);
        pdfBody.addView(verticalView5);
        pdfBody.addView(verticalView6);
        return pdfBody;
    }

    private PDFTextView pdfTextWidthGravity(String s, PDFTextView.PDF_TEXT_SIZE size) {
        PDFTextView vText = new PDFTextView(getApplicationContext(), size);
        vText.setLayout(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        vText.setText(s);
        vText.getView().setTypeface(vText.getView().getTypeface(), Typeface.BOLD);
        return vText;
    }


    public PDFTextView pdfText(String s){
        PDFTextView vText = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
        vText.setLayout(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        vText.setText(s);
        vText.getView().setTypeface(vText.getView().getTypeface(), Typeface.BOLD);
        return vText;
    }

    @Override
    protected PDFFooterView getFooterView(int pageIndex) {
        PDFFooterView footerView = new PDFFooterView(getApplicationContext());

        PDFTextView pdfTextViewPage = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
        pdfTextViewPage.setText(String.format(Locale.getDefault(), "Page: %d", pageIndex + 1));
        pdfTextViewPage.setLayout(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 0));
        pdfTextViewPage.getView().setGravity(Gravity.CENTER_HORIZONTAL);

        footerView.addView(pdfTextViewPage);

        return footerView;
    }

//    @Nullable
//    @Override
//    protected PDFImageView getWatermarkView(int forPage) {
//        PDFImageView pdfImageView = new PDFImageView(getApplicationContext());
//        FrameLayout.LayoutParams childLayoutParams = new FrameLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                200, Gravity.CENTER);
//        pdfImageView.setLayout(childLayoutParams);
//
//        pdfImageView.setImageResource(R.drawable.ic_pdf);
//        pdfImageView.setImageScale(ImageView.ScaleType.FIT_CENTER);
//        pdfImageView.getView().setAlpha(0.3F);
//
//        return pdfImageView;
//    }

    @Override
    protected void onNextClicked(final File savedPDFFile) {
        Uri pdfUri = Uri.fromFile(savedPDFFile);
        Intent intentPdfViewer = new Intent(PdfCreatorExampleActivity.this, PdfViewerExampleActivity.class);
        intentPdfViewer.putExtra(PdfViewerExampleActivity.PDF_FILE_URI, pdfUri);

        startActivity(intentPdfViewer);
    }

    public String formatMoney(long s){
        NumberFormat formatter = new DecimalFormat("#,###,###");
        return formatter.format(s);
    }
}
