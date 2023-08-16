/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.annotation_tool_for_custom_dataset;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Ali Haider
 */
public class Annotataion_Window extends javax.swing.JFrame implements TextReturnedListener {

    /**
     * Creates new form Annotataion_Window
     */
    JSONObject config = ConfigurationLoader.loadConfigurations();
    JSONObject state = ConfigurationLoader.loadState();
    ArrayList<JCheckBox> aspects_checkboxes;
    int total_rows = 0;
    static int previous_counter = 0;
    CSVReader reader_for_non_annotated_data;
    String data_to_annotate = "";
    String annotated_data = "";
    boolean opinion_terms;

    public Annotataion_Window() throws FileNotFoundException, IOException, CsvValidationException {
        initComponents();
        aspects_checkboxes = new ArrayList<JCheckBox>();
        if (!state.getBoolean("Data_configured")) {
            Dataconfigure();
        }
        String domain = config.getString("domain");
        super.setTitle(domain.toUpperCase());
        jLabel1.setText(domain.toUpperCase());
        JSONObject Sentiment_Analysis_or_Text_Annotataion_under_classes = config.getJSONObject("Sentiment_Analysis_or_Text_Annotataion_under_classes");
        JSONObject Aspect_based_sentiment_Analysis = config.getJSONObject("Aspect_based_sentiment_Analysis");

        opinion_terms = config.getBoolean("opinion_term");
        holder_panel.setLayout(new GridLayout(0, 1));

        if (Sentiment_Analysis_or_Text_Annotataion_under_classes.getBoolean("Simple_text_Categorical_Annotation")) {
            for (int k = 0; k < Sentiment_Analysis_or_Text_Annotataion_under_classes.getJSONArray("Classes(Sentiments_or_aspects)").length(); k++) {
                String aspect_or_class = Sentiment_Analysis_or_Text_Annotataion_under_classes.getJSONArray("Classes(Sentiments_or_aspects)").getString(k);
                JCheckBox checkbox = new JCheckBox(aspect_or_class);
                checkbox.setName(aspect_or_class);
                if (opinion_terms) {
                    checkbox.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (checkbox.isSelected()) {
                                openSecondaryFrame(checkbox.getName().toString());
                                checkbox.setSelected(false); // Unselect the checkbox after the click
                            }
                        }
                    });
                }

                aspects_checkboxes.add(checkbox);
                holder_panel.add(checkbox);
            }
        } else if (Aspect_based_sentiment_Analysis.getBoolean("Aspect_based_Sentiment_Analysis")) {
            JSONArray sentiments = Aspect_based_sentiment_Analysis.getJSONArray("sentiments");
            if (Aspect_based_sentiment_Analysis.getBoolean("wrap_aspects_in_categories")) {

                JSONObject Categories_wrapper = Aspect_based_sentiment_Analysis.getJSONObject("Categories_wrapper");
                JSONArray categories = Categories_wrapper.getJSONArray("categories");
                JSONObject aspects = Categories_wrapper.getJSONObject("aspects");

                for (int i = 0; i < categories.length(); i++) {
                    String category = categories.getString(i);
                    JPanel catgeoires_wrapper = new JPanel();
                    TitledBorder border = BorderFactory.createTitledBorder(category);
                    catgeoires_wrapper.setBorder(border);
                    catgeoires_wrapper.setLayout(new GridLayout(0, 3));
                    Font borderFont = new Font("Arial", Font.BOLD, 18); // You can replace "Arial" with your desired font family
                    border.setTitleFont(borderFont);
                    catgeoires_wrapper.setBackground(Color.WHITE); // Set the background color to white

                    holder_panel.add(catgeoires_wrapper);
                    for (int j = 0; j < sentiments.length(); j++) {
                        String sentiment = sentiments.getString(j);
                        JPanel sentiment_wrapper = new JPanel();
                        TitledBorder border1 = BorderFactory.createTitledBorder(sentiment);
                        sentiment_wrapper.setBorder(border1);
                        sentiment_wrapper.setLayout(new GridLayout(0, 3));
                        sentiment_wrapper.setBackground(Color.WHITE); // Set the background color to white
                        borderFont = new Font("Arial", Font.BOLD, 15);
                        border1.setTitleFont(borderFont);
                        JSONArray category_aspects = aspects.getJSONArray(category);
                        catgeoires_wrapper.add(sentiment_wrapper);

                        for (int k = 0; k < category_aspects.length(); k++) {
                            String aspect = category_aspects.getString(k);
                            JCheckBox checkbox = new JCheckBox(aspect);
                            String name = category + "-" + aspect + "-" + sentiment;
                            checkbox.setName(name);
                            if (opinion_terms) {
                                checkbox.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        if (checkbox.isSelected()) {
                                            openSecondaryFrame(checkbox.getName().toString());
                                            checkbox.setSelected(false); // Unselect the checkbox after the click
                                        }
                                    }
                                });
                            }

                            aspects_checkboxes.add(checkbox);
                            sentiment_wrapper.add(checkbox);
                        }

                    }

                }
            } else {
                for (int j = 0; j < sentiments.length(); j++) {
                    String sentiment = sentiments.getString(j);
                    JPanel sentiment_wrapper = new JPanel();
                    TitledBorder border1 = BorderFactory.createTitledBorder(sentiment);
                    sentiment_wrapper.setBorder(border1);
                    sentiment_wrapper.setLayout(new GridLayout(0, 3));
                    sentiment_wrapper.setBackground(Color.WHITE); // Set the background color to white
                    Font borderFont = new Font("Arial", Font.BOLD, 15);
                    border1.setTitleFont(borderFont);
                    JSONArray category_aspects = Aspect_based_sentiment_Analysis.getJSONArray("Classes(aspects)");
                    holder_panel.add(sentiment_wrapper);

                    for (int k = 0; k < category_aspects.length(); k++) {
                        String aspect = category_aspects.getString(k);
                        JCheckBox checkbox = new JCheckBox(aspect);
                        String name = aspect + "-" + sentiment;
                        checkbox.setName(name);
                        if (opinion_terms) {
                            checkbox.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    if (checkbox.isSelected()) {
                                        openSecondaryFrame(checkbox.getName().toString());
                                        checkbox.setSelected(false); // Unselect the checkbox after the click
                                    }
                                }
                            });
                        }

                        aspects_checkboxes.add(checkbox);
                        sentiment_wrapper.add(checkbox);
                    }

                }

            }

        }

        data_to_annotate = state.getString("input_file_path");
        annotated_data = state.getString("annotated_data_file");
        total_rows = row_count();
        previous_counter = state.getInt("counter");
        read_and_display(previous_counter);

    }

    @Override
    public void onTextReturned(String text, String CheckboxName) {

        selectCheckboxByName(text, CheckboxName); // Update the text field with returned text
    }

    private void selectCheckboxByName(String aspects_opinion_terms, String aspect_name) {
        Container contentPane = getContentPane();
        Component[] components = contentPane.getComponents();

        for (JCheckBox checkbox : aspects_checkboxes) {

            if (aspect_name.equals(checkbox.getName())) {
                checkbox.setSelected(true);
                checkbox.putClientProperty("opinion_terms_list", aspects_opinion_terms);
                break; // Exit the loop after finding the matching checkbox
            }
        }

    }

    private void openSecondaryFrame(String CheckboxName) {
        opinion_term frame = new opinion_term(jTextArea1.getText(), CheckboxName);
        frame.setTextReturnedListener(this);
        frame.setVisible(true);

    }

    public void reset_checks() {
        for (JCheckBox checkbox : aspects_checkboxes) {
            checkbox.setSelected(false);
        }

    }

    public int row_count() throws FileNotFoundException, IOException, CsvValidationException {
        this.reader_for_non_annotated_data = new CSVReader(new FileReader(data_to_annotate));
        int rowCount = 0;
        while (reader_for_non_annotated_data.readNext() != null) {
            rowCount++;
        }
        return rowCount;
    }

    public void move_next() {
        previous_counter++;
        try {
            read_and_display(previous_counter);
            // TODO add your handling code here:
        } catch (IOException ex) {
        } catch (CsvValidationException ex) {
        }
    }

    public void move_previous() {
        previous_counter--;
        if (previous_counter <= 0) {
            JOptionPane.showMessageDialog(this, "No More Data to Display.", "Information", JOptionPane.ERROR_MESSAGE);

        } else {

            try {
                read_and_display(previous_counter);
                // TODO add your handling code here:
            } catch (IOException ex) {
            } catch (CsvValidationException ex) {
            }
        }
    }

    public void save_data() {
        String Sentence = jTextArea1.getText();
        String id = review_id.getText();
        String category_sentiment_aspect = "";
        String aspects_opinion_terms = "";
        for (JCheckBox checkbox : aspects_checkboxes) {
            if (checkbox.isSelected()) {
                category_sentiment_aspect += checkbox.getName().toString();
                category_sentiment_aspect += ", ";
                if (opinion_terms) {
                    aspects_opinion_terms += (String) checkbox.getClientProperty("opinion_terms_list");
                    aspects_opinion_terms += ", ";
                }

            }
        }

        if (category_sentiment_aspect.length() > 0) {

            category_sentiment_aspect = category_sentiment_aspect.substring(0, category_sentiment_aspect.length() - 2);

            if (opinion_terms) {
                aspects_opinion_terms = aspects_opinion_terms.substring(0, aspects_opinion_terms.length() - 2);
            }

            String[] row;
            if (opinion_terms) {
                row = new String[]{id, Sentence, category_sentiment_aspect, aspects_opinion_terms};
            } else {
                row = new String[]{id, Sentence, category_sentiment_aspect};
            }
            System.out.println(id + Sentence + category_sentiment_aspect + aspects_opinion_terms);
            try ( CSVWriter writer = new CSVWriter(new FileWriter(annotated_data, true))) {
                writer.writeNext(row);
            } catch (IOException e) {
                // Handle the exception
            }
            move_next();
        } else {
            JOptionPane.showMessageDialog(this, "Please Mark Any of the Atrribute (Missing Aspects Values)", "Information", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void read_and_display(int index) throws IOException, CsvValidationException {
        reset_checks();
        jLabel3.setText("Count ( " + previous_counter + "/ " + total_rows + " ) ");

        String[] nextLine;
        int lineIndex = 0;
        this.reader_for_non_annotated_data = new CSVReader(new FileReader(data_to_annotate));

        while ((nextLine = reader_for_non_annotated_data.readNext()) != null) {
            if (lineIndex == index) {
                review_id.setText(nextLine[0]);
                jTextArea1.setText(nextLine[1]);
                chars_count.setText("Characters Count ( " + String.valueOf(nextLine[1].length()) + " )");
                break; // Exit the loop after processing the line
            }
            lineIndex++;
        }
    }

    public void Dataconfigure() throws IOException {
        if (config != null) {
            String inputDataPath = config.getString("input_data_path");
            boolean splitting = config.getBoolean("split_Reviews_to_sentences");
            File csvFile = new File(inputDataPath);
            Path path = Paths.get(inputDataPath);
            Path directoryPath = path.getParent();

            if (inputDataPath.toLowerCase().endsWith(".csv") && csvFile.exists()) {
                String outputFilePath = directoryPath + "/Data_to_Annotate.csv";
                String annotatedDataPath = directoryPath + "/Annotated_Data.csv";
                BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath));
                ArrayList<String> outputLines = new ArrayList<>();
                BufferedReader br = new BufferedReader(new FileReader(csvFile));

                if (splitting) {

                    try {
                        String line;
                        while ((line = br.readLine()) != null) {
                            String[] columns = line.split(",", 2); // Split into 2 parts: id and review text
                            if (columns.length >= 2) {
                                String id = columns[0].trim();
                                String reviewText = columns[1].trim();

                                // Split review text into sentences
                                String[] sentences = splitIntoSentences(reviewText);
                                // Append sentences with corresponding id to outputLines
                                for (String sentence : sentences) {
                                    sentence = preprocess_text(sentence);
                                    outputLines.add(id + ",\"" + sentence + "\"");
                                }
                            }
                        }
                        br.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        String line;
                        while ((line = br.readLine()) != null) {
                            String[] columns = line.split(",", 2); // Split into 2 parts: id and review text
                            if (columns.length >= 2) {
                                String id = columns[0].trim();
                                String reviewText = columns[1].trim();
                                reviewText = preprocess_text(reviewText);
                                outputLines.add(id + ",\"" + reviewText + "\"");

                            }
                        }
                        br.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                for (String outputLine : outputLines) {
                    bw.write(outputLine);
                    bw.newLine();
                }
                bw.close();

                // Update configuration values
                state.put("Data_configured", true);
                state.put("input_file_path", outputFilePath);
                state.put("annotated_data_file", annotatedDataPath);
                saveConfigurations(state);
                System.out.println("COnfiguration Successful ...");

            } else {
                JOptionPane.showMessageDialog(this, "Input file is not valid file please ensure the csv file is present at the Path.", "Information", JOptionPane.ERROR_MESSAGE);

            }
        }
    }

    public static void saveConfigurations(JSONObject config) {
        String currentDir = System.getProperty("user.dir");  // Get the current working directory

        try {
            // Write the updated configuration back to the file
            OutputStream outputStream = new FileOutputStream(currentDir + "/state.json");
            outputStream.write(config.toString(4).getBytes());

            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String removeCustomStopwords(String text, JSONArray customStopwords) {
        String[] words = text.split("\\s+");
        StringBuilder cleanedText = new StringBuilder();

        for (String word : words) {
            if (!containsIgnoreCase(customStopwords, word)) {
                cleanedText.append(word).append(" ");
            }
        }

        return cleanedText.toString().trim();
    }
    // Helper method to check if a JSONArray contains a given string (case-insensitive)

    private boolean containsIgnoreCase(JSONArray jsonArray, String searchTerm) {
        for (int i = 0; i < jsonArray.length(); i++) {
            if (jsonArray.getString(i).equalsIgnoreCase(searchTerm)) {
                return true;
            }
        }
        return false;
    }

    public String preprocess_text(String input) {
        JSONObject textPreprocessing = config.getJSONObject("text_preprocessing");
        boolean removeHtml = textPreprocessing.getBoolean("remove_html");
        boolean removeUrls = textPreprocessing.getBoolean("remove_urls");
        boolean removeEmails = textPreprocessing.getBoolean("remove_emails");
        boolean removeMentions = textPreprocessing.getBoolean("remove_mentions");
        boolean removePunctuations = textPreprocessing.getBoolean("remove_punctuations");
        JSONArray removeStopwords = textPreprocessing.getJSONArray("custom_stopwords_to_remove");
        boolean removeExtraSpaces = textPreprocessing.getBoolean("remove_extra_spaces");

        // Remove HTML tags
        if (removeHtml) {
            input = input.replaceAll("<.*?>", "");
        }

        // Remove URLs
        if (removeUrls) {
            input = input.replaceAll("https?://\\S+\\s?", "");
        }

        // Remove email addresses
        if (removeEmails) {
            input = input.replaceAll("\\S+@\\S+\\s?", "");
        }

        // Remove mentions (@username)
        if (removeMentions) {
            input = input.replaceAll("@\\S+\\s?", "");
        }

        // Remove punctuations
        if (removePunctuations) {
            input = input.replaceAll("[\\p{Punct}&&[^@_]]+", "");
        }

        // Remove extra spaces
        if (removeExtraSpaces) {
            input = input.replaceAll("\\s+", " ");
        }

        if (removeStopwords.length() > 0) {
            input = removeCustomStopwords(input, removeStopwords);
        }
        return input;
    }

    private static String[] splitIntoSentences(String text) {
        // This regex assumes that sentences end with ".", "!", or "?"
        String regex = "[^.?]+[.?]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        ArrayList<String> sentences = new ArrayList<>();
        while (matcher.find()) {
            sentences.add(matcher.group().trim());
        }
        if (sentences.size() <= 0) {
            sentences.add(text.trim());
        }
        return sentences.toArray(new String[0]);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        holder_panel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        review_id = new javax.swing.JLabel();
        chars_count = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("CUSTOM ANNOTATION TOOL");

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setText("Reviews Counter");

        jButton1.setBackground(new java.awt.Color(0, 102, 102));
        jButton1.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Save and Move Next");
        jButton1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(0, 102, 102));
        jButton4.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("Skip and Jump");
        jButton4.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        holder_panel.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane2.setViewportView(holder_panel);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jLabel4.setText("Review_Identification ( ");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText(")");

        review_id.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        review_id.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        review_id.setText("ID");

        chars_count.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        chars_count.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        chars_count.setText("Characters Count (         )");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(review_id, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 589, Short.MAX_VALUE)
                                .addComponent(jLabel3))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(chars_count, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(40, 40, 40))))
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(review_id)
                        .addComponent(jLabel5)
                        .addComponent(jLabel4))
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(chars_count)
                .addGap(36, 36, 36)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE))
                .addGap(17, 17, 17))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButton1, jButton4});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        save_data();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        move_next();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Annotataion_Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Annotataion_Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Annotataion_Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Annotataion_Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new Annotataion_Window().setVisible(true);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Annotataion_Window.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Annotataion_Window.class.getName()).log(Level.SEVERE, null, ex);
                } catch (CsvValidationException ex) {
                    Logger.getLogger(Annotataion_Window.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        // Register a shutdown hook to perform some function before the program is closed
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                JSONObject state = ConfigurationLoader.loadState();

                state.put("counter", previous_counter);
                saveConfigurations(state);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel chars_count;
    private javax.swing.JPanel holder_panel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel review_id;
    // End of variables declaration//GEN-END:variables

}
