package com.example.test2222222;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class CrudController implements Initializable {

    @FXML
    private TableView<Book> bookTable;

    @FXML
    private TableColumn<Book, Integer> bookIdColumn;

    @FXML
    private TableColumn<Book, String> titleColumn;

    @FXML
    private TableColumn<Book, String> authorColumn;

    @FXML
    private TableColumn<Book, String> genreColumn;

    @FXML
    private TextField bookId;

    @FXML
    private TextField title;

    @FXML
    private TextField author;

    @FXML
    private TextField genre;

    private ObservableList<Book> bookList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        bookTable.setItems(bookList);
        fetchData();
    }

    @FXML
    protected void insertData(ActionEvent event) {
        executeUpdate("INSERT INTO library_books (Title, Author, Genre) VALUES (?, ?, ?)", title.getText(), author.getText(), genre.getText());
    }

    @FXML
    protected void updateData(ActionEvent event) {
        executeUpdate("UPDATE library_books SET Title = ?, Author = ?, Genre = ? WHERE BookID = ?", title.getText(), author.getText(), genre.getText(), bookId.getText());
    }

    @FXML
    protected void deleteData(ActionEvent event) {
        executeUpdate("DELETE FROM library_books WHERE BookID = ?", bookId.getText());
    }

    @FXML
    protected void loadData(ActionEvent event) {
        bookList.clear();
        executeQuery("SELECT * FROM library_books WHERE BookID = ?", bookId.getText(), resultSet -> {
            try {
                if (resultSet.next()) {
                    title.setText(resultSet.getString("Title"));
                    author.setText(resultSet.getString("Author"));
                    genre.setText(resultSet.getString("Genre"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void fetchData() {
        bookList.clear();
        executeQuery("SELECT * FROM library_books", resultSet -> {
            try {
                while (resultSet.next()) {
                    bookList.add(new Book(resultSet.getInt("BookID"), resultSet.getString("Title"), resultSet.getString("Author"), resultSet.getString("Genre")));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void executeUpdate(String query, String... params) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/crudddtest2", "root", "")) {
            PreparedStatement statement = connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                statement.setString(i + 1, params[i]);
            }
            statement.executeUpdate();
            fetchData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void executeQuery(String query, QueryHandler handler) {
        executeQuery(query, null, handler);
    }

    private void executeQuery(String query, String param, QueryHandler handler) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/crudddtest2", "root", "")) {
            PreparedStatement statement = connection.prepareStatement(query);
            if (param != null) {
                statement.setString(1, param);
            }
            ResultSet resultSet = statement.executeQuery();
            handler.handle(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FunctionalInterface
    interface QueryHandler {
        void handle(ResultSet resultSet) throws SQLException;
    }
}
