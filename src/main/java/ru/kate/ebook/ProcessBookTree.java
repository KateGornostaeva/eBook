package ru.kate.ebook;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

@Slf4j
public class ProcessBookTree {
    public static void processBookTree(TreeView<TreeItemBook> treeView, String html) {

        Document document = Jsoup.parse(html);
        Elements elements = document.getElementsByTag("a");
        if (elements.size() <= 0) {
            return;
        }

        TreeItem<TreeItemBook> root = new TreeItem<>(new TreeItemBook("Book", ""));
        root.setExpanded(true);
        treeView.setRoot(root);

        elements.forEach(element -> {
            TreeItem<TreeItemBook> item = new TreeItem<>(new TreeItemBook(element.text(), element.attr("id")));
            root.getChildren().add(item);
        });

    }
}
