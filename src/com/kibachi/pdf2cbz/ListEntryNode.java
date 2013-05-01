package com.kibachi.pdf2cbz;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: kengelke
 * Date: 01.05.13
 * Time: 19:41
 */
public class ListEntryNode extends BorderPane {
    private File file;
    private String numberOfPages;
    private boolean locked;
    private boolean isOk;

    public ListEntryNode() {
        init();
    }

    public ListEntryNode(File file, String numberOfPages, boolean locked) {
        this.file = file;
        this.numberOfPages = numberOfPages;
        this.locked = locked;
        init();
    }

    private void init() {
        Image imagePages = new Image(getClass().getResourceAsStream("resources/copy_16x16.png"));
        Image imageLocked = new Image(getClass().getResourceAsStream("resources/lock_16x16.png"));

        Label pathLabel = new Label(file == null ? "(unknown)" : file.getName());

        Label pageNumberLabel = new Label(numberOfPages);
        HBox pageNumberBox = HBoxBuilder.create().spacing(5).children(new ImageView(imagePages), pageNumberLabel).build();

        setTop(pathLabel);
        setLeft(pageNumberBox);

        if (locked) {
            setRight(new ImageView(imageLocked));
        }

        setPadding(new Insets(3));
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(String numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isOk() {
        return isOk;
    }

    public void setOk(boolean ok) {
        isOk = ok;
        if (!isOk) {
            setStyle("-fx-background-color: #ff816b; -fx-text-fill: white;");
        } else {
            setStyle("-fx-background-color: #43b04c; -fx-text-fill: white;");
        }
    }
}
