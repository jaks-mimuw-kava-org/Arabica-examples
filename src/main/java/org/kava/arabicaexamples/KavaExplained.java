package org.kava.arabicaexamples;

import org.kava.arabica.http.ArabicaHttpRequest;
import org.kava.arabica.http.ArabicaHttpResponse;
import org.kava.arabica.servlet.ArabicaServlet;
import org.kava.arabica.servlet.ArabicaServletURI;
import org.kava.arabica.utils.StaticReader;

@ArabicaServletURI("/info")
public class KavaExplained extends ArabicaServlet {
    @Override
    public void doGET(ArabicaHttpRequest request, ArabicaHttpResponse response) {
        response.setBody(StaticReader.readFileFromResources("static/welcome.html"));
        response.setStatusCode(200);
        response.setRequest(request);
    }

    @Override
    public void doPOST(ArabicaHttpRequest request, ArabicaHttpResponse response) {
        throw new UnsupportedOperationException();
    }
}
