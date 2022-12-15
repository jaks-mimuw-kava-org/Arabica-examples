package org.arabicaexamples;

import org.kava.arabica.ServletContainer;

public class ServletContainerExample {
    public static void main(String[] args) throws Exception {
        var container = new ServletContainer(4040);
        container.registerServlet(LibraryServlet.class);
        container.registerServlet(HelloWorld.class);
        container.registerServlet(KavaExplained.class);
        container.registerIcon("static/favicon.ico");
        container.start();
    }
}
