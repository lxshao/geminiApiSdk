package com.example.genaisdk.service;

import com.example.genaisdk.utils.RagUtiils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IngestionService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(IngestionService.class);

    private final VectorStore vectorStore;

    @Value("classpath:docs/Flexora_FAQ.pdf")
    private Resource faqPdf;

    @Value("${ingestion.enabled:false}")
    private boolean ingestionEnabled;

    public IngestionService(@Qualifier(value = "qaVectorStore") PgVectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void run(String... args) throws Exception {
        //ingestPDFDocs("page", faqPdf);
    }

    private void ingestPDFDocs(String ingestType, Resource pdfResource) {
        //if (ingestionEnabled) {
            //var docs = new PagePdfDocumentReader(faqResource).get();
            //log.info("PDF Docs Content: {}, size: {}", docs, docs.size());
            log.info("Ingesting PDF docs from resource: {} ", pdfResource.getFilename());
            var docs = getPDFDocument(ingestType,pdfResource);
            vectorStore.add(docs);
            log.info("Ingested {} documents into the vector store.", docs.size());
        //}
    }

    private static List<Document> getPDFDocument(String ingestType, Resource pdfResource) {
        try {
            return switch (ingestType) {
                case "page" -> new PagePdfDocumentReader(pdfResource).get();
                case "paragraph" -> new ParagraphPdfDocumentReader(pdfResource).get();
                default -> throw new IllegalArgumentException("Unsupported ingestType: " + ingestType);
            };
        } catch (Exception e) {
            log.error("Error reading PDF document", e);
            throw new RuntimeException("Error while reading PDF document", e);
        }

    }

    public void ingest(byte[] fileContent, String filename, String ingestType) {
        log.info("IngestionService.ingest called with fileName: {}, ingestType: {}", filename, ingestType);
        Resource docSource = new ByteArrayResource(fileContent) {
            @Override
            public String getFilename() {
                return filename;
            }
        };

        var fileExtension = RagUtiils.getFileExtension(filename);
        switch (fileExtension) {
            case "pdf" -> {
                ingestPDFDocs(ingestType, docSource);
            }
            case "docx" -> {
                ingestWordDocs(ingestType, docSource);
            }
            case "txt" -> {
                log.info("Ingesting text documents from file: {}", filename);
                ingestTextDocs(ingestType, docSource);
            }
            default -> throw new IllegalArgumentException("Unsupported file type: " + fileExtension);
        };
        //ingestPDFDocs(ingestType, docSource);
        log.info("Ingestion Completed Successfully.");
    }

    private void ingestWordDocs(String ingestType, Resource docResource) {
        log.info("Ingesting Word docs from file ");
        var docs = getWordDocuments(docResource, ingestType);
        vectorStore.add(docs);
        log.info("Ingested {} documents from Word file:", docs.size());
    }

    private static List<Document> getWordDocuments(Resource pdfResource, String ingestType) {

        var docs = new TikaDocumentReader(pdfResource).get();
        return switch (ingestType) {
            case "token" -> {
                TokenTextSplitter splitter = new TokenTextSplitter();
                //                TokenTextSplitter splitter = new TokenTextSplitter(1000, 400, 10, 5000, true);

                yield splitter.apply(docs);
            }
            default -> docs;
        };

    }

    private void ingestTextDocs(String ingestType, Resource docSource) {
        var textReader = new TextReader(docSource);
        textReader.getCustomMetadata().put("filename", docSource.getFilename());
        var docs = textReader.read();
        vectorStore.add(docs);
        log.info("Ingested {} documents from Text file: {}", docs.size());
    }
}
