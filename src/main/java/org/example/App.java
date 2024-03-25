package org.example;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.PageExtractor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;


public class App {

  public static void main(String[] args) {

    generateDocument();

//    String buyersGuide = "/Users/tonylangworthy/Documents/car-dealer-forms/test/buyers-guide-custom.pdf";
//    String noticeOfSale = "/Users/tonylangworthy/Documents/car-dealer-forms/test/notice-of-sale.pdf";
//    String mergedDoc = "/Users/tonylangworthy/Documents/car-dealer-forms/test/merged.pdf";
//
//
//    List<File> files = new ArrayList<>();
//    files.add(new File(buyersGuide));
//    files.add(new File(noticeOfSale));
//
//    mergePages(files, new File(mergedDoc));
  }

  public static void generateDocument() {

    String formTemplate = "/Users/tonylangworthy/Documents/car-dealer-forms/test/title-application.pdf";

    try {

      PDDocument pdfDocument = PDDocument.load(new File(formTemplate));

      // Get the document catalog
      PDAcroForm acroForm = pdfDocument.getDocumentCatalog().getAcroForm();

      // Get all the PDF fields
      Iterator fieldIterator = acroForm.getFieldIterator();

      while(fieldIterator.hasNext()) {
        Object fieldObject = (PDField) fieldIterator.next();
        String fieldType = ((PDField) fieldObject).getFieldType();
        String fieldName = ((PDField) fieldObject).getFullyQualifiedName();

        System.out.println("Field Type: " + fieldType);
        System.out.println("Field Name: " + fieldName);
        System.out.println(fieldObject);


        if(fieldType == "Tx") {
          PDTextField field = (PDTextField) acroForm.getField(fieldName);
          // If there is a JavaScript action for value formatting, set it to null
          // to avoid PdfBox creating an appearance
          field.setActions(null);
          field.setValue(fieldName);

        }
      }
      System.out.println();

      //FDFDocument fdfDocument = (FDFDocument) acroForm.exportFDF();

      //fdfDocument.save("title-app.fdf");
      pdfDocument.save("/Users/tonylangworthy/Documents/car-dealer-forms/test/title-application-filled.pdf");
      pdfDocument.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  public static void extractPages() {

    String buyersGuide = "/Users/tonylangworthy/Documents/car-dealer-forms/buyers-guide/buyers-guide-custom-prd.pdf";

    try {
      PDDocument pdfDocument = PDDocument.load(new File(buyersGuide));
      PageExtractor pageExtractor = new PageExtractor(pdfDocument);
      pageExtractor.setStartPage(2);
      pageExtractor.setEndPage(3);
      PDDocument extractedDocument = pageExtractor.extract();
      extractedDocument.save("/Users/tonylangworthy/Documents/car-dealer-forms/buyers-guide/custom-buyers-guide.pdf");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }


  }

  public static void mergePages(List<File> fileList, File outputFile) {

    PDFMergerUtility mergerUtility = new PDFMergerUtility();
    mergerUtility.setAcroFormMergeMode(PDFMergerUtility.AcroFormMergeMode.JOIN_FORM_FIELDS_MODE);
//            mergerUtility.setDocumentMergeMode(PDFMergerUtility.DocumentMergeMode.OPTIMIZE_RESOURCES_MODE);
    fileList.forEach(file -> {
      try {
        System.out.println("Adding file to merge: " + file.getAbsolutePath());
        mergerUtility.addSource(file.getAbsolutePath());
      } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
      }
    });
    mergerUtility.setDestinationFileName(outputFile.getAbsolutePath());
    try {
      mergerUtility.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
