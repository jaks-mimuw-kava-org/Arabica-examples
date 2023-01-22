package org.kava.arabicaexamples;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.kava.arabica.utils.StaticReader;
import org.kava.lungo.Logger;
import org.kava.lungo.LoggerFactory;
import sun.misc.Signal;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@WebServlet("/library")
public class LibraryServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(LibraryServlet.class);

    private List<Book> books = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(200);

        String allBooks = books.stream()
                .map(book -> format("<li>[%s] <b>%s</b></li>", book.author(), book.getTruncatedName()))
                .collect(Collectors.joining());

        String form = StaticReader.readFileFromResources("static/book_form.html");
        String body = format("<h2>Books:</h2><ul>%s</ul>%s", allBooks, form);

        resp.setContentType("text/html");
        resp.setContentLength(body.length());
        resp.getOutputStream().write(body.getBytes());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            var rawBody = req.getInputStream().readAllBytes();
            var body = new String(rawBody);
            String[] kvs = body.split("[&=]");
            StringBuilder json = new StringBuilder("{");
            for (int i = 0; i < kvs.length; i += 2) {
                json.append(Book.quote(kvs[i])).append(":").append(Book.quote(kvs[i + 1]));
                if (i + 2 < kvs.length) {
                    json.append(",");
                }
            }
            json.append("}");

            var book = Book.fromJSON(json.toString());
            books.add(book);

            var outBody = "<h1>Added!</h1><a href=\"/library\">Go back!</a>";

            resp.setStatus(200);
            resp.setContentType("text/plain");
            resp.setContentLength(outBody.length());
            resp.getOutputStream().write(outBody.getBytes());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public LibraryServlet() {
        loadFromMemory();
        Signal.handle(new Signal("INT"), signal -> {
            saveToMemory();
            System.exit(0);
        });
    }

    private static final String FILE_NAME = "books.by";

    public void saveToMemory() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(FILE_NAME);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(books);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadFromMemory() {
        try {
            File file = new File(FILE_NAME);
            if (file.exists()) {
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                books = (List<Book>) in.readObject();
                logger.info("Loaded books from memory! Number of books: %d", books.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
