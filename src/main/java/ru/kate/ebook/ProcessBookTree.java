package ru.kate.ebook;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ProcessBookTree {
    public static void processBookTree(TreeView<TreeItemBook> treeView, String html) {

        Document document = Jsoup.parse(html);
        Elements elements = document.getElementsByTag("a");
        if (elements.size() <= 0) {
            return;
        }

        TreeItem<TreeItemBook> root = new TreeItem<>(new TreeItemBook("Book", "", ""));
        root.setExpanded(true);
        treeView.setRoot(root);

        Map<String, TreeItem> items = new HashMap<>();
        List<TreeItem> orderedItems = new ArrayList<>();
        elements.forEach(element -> {
            TreeItem<TreeItemBook> item = new TreeItem<>(new TreeItemBook(element.text(), element.attr("id"), element.attr("parent")));
            items.put(element.attr("id"), item);
            orderedItems.add(item);
        });

        for (TreeItem<TreeItemBook> orderedItem : orderedItems) {
            String parent = orderedItem.getValue().getParent();
            if (parent == null || parent.isEmpty()) {
                root.getChildren().add(orderedItem);
            } else {
                TreeItem<TreeItemBook> parentItem = items.get(parent);
                if (parentItem != null) {
                    parentItem.getChildren().add(orderedItem);
                }
            }
        }
    }
}
