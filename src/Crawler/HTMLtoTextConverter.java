package Crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;

public class HTMLtoTextConverter {

    private static final String HTML_BASE_DIR = "saved_pages";
    private static final String TEXT_BASE_DIR = "text_pages";

    public static void convertDomain(String domain) {
        System.out.println("Converting HTML files for domain: " + domain);

        File htmlInputFolder = new File(HTML_BASE_DIR);

        File[] htmlFiles = htmlInputFolder.listFiles((dir, name) ->
            name.toLowerCase().endsWith(".html") && name.contains(domain)
        );

        if (htmlFiles == null || htmlFiles.length == 0) {
            System.out.println("No HTML files found for domain: " + domain);
            return;
        }

        // Create domain-specific output folder for text files
        File domainTextDir = new File(TEXT_BASE_DIR, domain);
        if (!domainTextDir.exists()) domainTextDir.mkdirs();

        for (File htmlFile : htmlFiles) {
            try {
                Document doc = Jsoup.parse(htmlFile, "UTF-8");
                String text = doc.text();

                String textFileName = htmlFile.getName().replace(".html", ".txt");
                File textFile = new File(domainTextDir, textFileName);

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(textFile))) {
                    writer.write(text);
                }

                System.out.println("Converted: " + textFile.getAbsolutePath());

            } catch (IOException e) {
                System.err.println("Failed to convert " + htmlFile.getName() + ": " + e.getMessage());
            }
        }
    }
}
