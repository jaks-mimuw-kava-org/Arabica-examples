package org.kava.arabicaexamples;

import org.kava.arabica.http.ArabicaHttpRequest;
import org.kava.arabica.http.ArabicaHttpResponse;
import org.kava.arabica.servlet.ArabicaServlet;
import org.kava.arabica.servlet.ArabicaServletURI;
import org.kava.arabica.utils.StaticReader;
import org.kava.lungo.Logger;
import org.kava.lungo.LoggerFactory;
import sun.misc.Signal;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@ArabicaServletURI("/library")
public class LibraryServlet extends ArabicaServlet {

    private static final Logger logger = LoggerFactory.getLogger(LibraryServlet.class);

    private List<Book> books = new ArrayList<>();

    @Override
    public void doGET(ArabicaHttpRequest request, ArabicaHttpResponse response) {
        response.setStatusCode(200);
        response.setRequest(request);

        String allBooks = books.stream()
                .map(book -> format("<li>[%s] <b>%s</b></li>", book.author(), book.getTruncatedName()))
                .collect(Collectors.joining());

        String form = StaticReader.readFileFromResources("static/book_form.html");

        response.setBody(format("<h2>Books:</h2><ul>%s</ul>%s", allBooks, form));
    }

    @Override
    public void doPOST(ArabicaHttpRequest request, ArabicaHttpResponse response) {
        try {
            var body = request.bodyAsString();
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

            response.setStatusCode(200);
            response.setRequest(request);
            response.setBody("<h1>Added!</h1><a href=\"/library\">Go back!</a>");
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
