package fands.support;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.concurrent.atomic.AtomicInteger;

@WebListener
public class OnlineListener implements HttpSessionListener {

    private AtomicInteger online = new AtomicInteger(0);

    public void sessionCreated(HttpSessionEvent e) {
        ServletContext servletContext = e.getSession().getServletContext();
        if (servletContext.getAttribute("online") == null) {
            servletContext.setAttribute("online", online);
        }
        online.incrementAndGet();
    }

    public void sessionDestroyed(HttpSessionEvent e) {
        ServletContext servletContext = e.getSession().getServletContext();
        if (servletContext.getAttribute("online") == null) {
            servletContext.setAttribute("online", online);
        }
        online.decrementAndGet();
    }

}
