package com.kibachi.pdf2cbz;

import com.itextpdf.text.exceptions.BadPasswordException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Main extends Application {

    private final ObservableList<ListEntryNode> fileList = FXCollections.<ListEntryNode>observableArrayList();
    private File lastDirectory;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage stage) throws Exception {
        this.lastDirectory = new File(System.getProperty("user.home"));

        Image imageOpen = new Image(getClass().getResourceAsStream("resources/open_24x24.png"));
        Image imageExport = new Image(getClass().getResourceAsStream("resources/export_24x24.png"));

        Label step1Label = new Label("Step 1:");
        step1Label.setStyle("-fx-font-size: 2em");

        Button openButton = new Button("Open files", new ImageView(imageOpen));
        openButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                openFilesAction(stage);
            }
        });

        HBox step1Box = HBoxBuilder.create().spacing(10).children(step1Label, openButton).build();
        step1Box.setPadding(new Insets(3));

        ListView<ListEntryNode> listView = new ListView<>();
        listView.setItems(fileList);

        Label step2Label = new Label("Step 2:");
        step2Label.setStyle("-fx-font-size: 2em");

        Button exportButton = new Button("Export files", new ImageView(imageExport));
        exportButton.setAlignment(Pos.CENTER_LEFT);
        exportButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                exportFilesAction(stage);
            }
        });

        HBox step2Box = HBoxBuilder.create().spacing(10).children(step2Label, exportButton).build();
        step2Box.setPadding(new Insets(3));

        BorderPane mainPane = new BorderPane();

        mainPane.setTop(step1Box);
        mainPane.setCenter(listView);
        mainPane.setBottom(step2Box);

        stage.setTitle("PDF2CBZ");
        stage.setScene(new Scene(mainPane));
        stage.show();
    }

    private void openFilesAction(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(lastDirectory);
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);

        List<File> files = fileChooser.showOpenMultipleDialog(stage);

        if (files != null && !files.isEmpty()) {
            fileList.clear();
            lastDirectory = files.get(0).getParentFile();
            for (File file : files) {
                fileList.add(initListEntryNode(file));
            }
        }
    }

    private void exportFilesAction(Stage stage) {
        for (ListEntryNode entryNode : fileList) {
            File source = entryNode.getFile();
            File target = new File(source.getParent(), getNameWithoutExtension(source) + ".cbz");
            entryNode.setOk(convert(source, target));
        }
    }

    private boolean convert(File source, File target) {
        try {
            ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(target));

            PdfReader reader = new PdfReader(source.getAbsolutePath());
            PdfReaderContentParser parser = new PdfReaderContentParser(reader);
            ImageRenderListener strategy;
            String imgExtension;

            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                strategy = parser.processContent(i, new ImageRenderListener());
                if (strategy.getFileType() != null && !strategy.getFileType().trim().isEmpty()) {
                    imgExtension = strategy.getFileType().toLowerCase();
                } else {
                    imgExtension = "jpg";
                }
                zip.putNextEntry(new ZipEntry(String.format("%1$05d.%2$s", i, imgExtension)));
                zip.write(strategy.getImage(), 0, strategy.getImage().length);
                zip.closeEntry();
            }
            zip.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private String getNameWithoutExtension(File file) {
        return file.getName().substring(0, file.getName().lastIndexOf("."));
    }

    private ListEntryNode initListEntryNode(File file) {
        String numberOfPages = "-/-";
        boolean locked = false;
        try {
            PdfReader pdfReader = new PdfReader(file.getAbsolutePath());
            numberOfPages = String.valueOf(pdfReader.getNumberOfPages());
            locked = false;
        } catch (BadPasswordException e) {
            locked = true;
        } catch (IOException e) {
            ListEntryNode listEntryNode = new ListEntryNode(file, numberOfPages, locked);
            listEntryNode.setOk(false);
            return listEntryNode;
        }
        return new ListEntryNode(file, numberOfPages, locked);
    }
}
