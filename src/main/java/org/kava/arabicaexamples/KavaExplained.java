package org.kava.arabicaexamples;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.kava.arabica.utils.StaticReader;

import java.io.IOException;

@WebServlet("/info")
public class KavaExplained extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var body = StaticReader.readFileFromResources("static/welcome.html");
        resp.getOutputStream().write(body.getBytes());
        resp.setContentLength(body.length());
        resp.setContentType("text/html");
        resp.setStatus(200);
    }
}
