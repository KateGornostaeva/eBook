package ru.kate.ebook.etb;

import javafx.scene.control.TreeItem;
import lombok.Data;
import lombok.NonNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import ru.kate.ebook.TreeItemBook;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class Ebook {

    private final Statement statement;
    private String html;
    private List<Test> tests = new ArrayList<>();

    public Ebook(@NonNull File file) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
        statement = connection.createStatement();

        StringBuilder builder = new StringBuilder();
        ResultSet rs = statement.executeQuery("select * from content where name='head';");
        while (rs.next()) {
            builder.append(rs.getString("value"));
        }
        rs.close();

        rs = statement.executeQuery("select * from content where name='script';");
        while (rs.next()) {
            builder.append(rs.getString("value"));
        }
        rs.close();

        rs = statement.executeQuery("select * from content where name='style';");
        while (rs.next()) {
            builder.append(rs.getString("value"));
        }
        rs.close();
        builder.append("</head>");

        rs = statement.executeQuery("select * from content where name='body';");
        while (rs.next()) {
            builder.append(rs.getString("value"));
        }
        rs.close();

        builder.append("</html>");
        html = builder.toString();

        extractTests();
    }

    public TreeItem<TreeItemBook> getTreeRoot() {

        Document document = Jsoup.parse(html);
        Elements elements = document.getElementsByTag("a");

        TreeItem<TreeItemBook> root = new TreeItem<>(new TreeItemBook("Book", "", ""));
        root.setExpanded(true);

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
        return root;
    }

    private void extractTests() throws SQLException {
        ResultSet rs = statement.executeQuery("select * from test");
        while (rs.next()) {
            Test test = new Test();
            test.setId(UUID.fromString(rs.getString("id")));
            test.setName(rs.getString("name"));
            test.setDescription(rs.getString("description"));
            tests.add(test);
        }
        rs.close();

        for (Test test : tests) {
            rs = statement.executeQuery("select * from test_section where test_id = '" + test.getId() + "'");
            while (rs.next()) {
                TestSection testSection = new TestSection();
                testSection.setId(rs.getInt("id"));
                testSection.setTestId(test.getId());
                testSection.setQuestion(rs.getString("question"));
                testSection.setMinValue(rs.getInt("min_value"));
                String correctResponses = rs.getString("correct_responses");
                List<Integer> responses = Arrays.stream(correctResponses.split(",")).map(Integer::valueOf).collect(Collectors.toList());
                testSection.setCorrectResponses(responses);
                test.getSections().add(testSection);

                List<Answer> answers = new ArrayList<>();
                ResultSet rs2 = statement.executeQuery("select * from answer where test_section_id = '" + testSection.getId() + "'");
                while (rs2.next()) {
                    Answer answer = new Answer();
                    answer.setId(rs2.getInt("id"));
                    answer.setTestSectionId(testSection.getId());
                    answer.setAnswer(rs2.getString("answer"));
                    answer.setWeight(rs2.getInt("weight"));
                    answers.add(answer);
                }
                testSection.setAnswers(answers);
                rs2.close();
            }
            rs.close();
        }

    }
}
