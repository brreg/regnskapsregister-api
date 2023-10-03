package no.brreg.regnskap.spring;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.ContentCachingRequestWrapper;

public class CachableDispatcherServlet extends DispatcherServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(CachableDispatcherServlet.class);

    @Override
    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!(request instanceof ContentCachingRequestWrapper)) {
            request = new ContentCachingRequestWrapper(request);
        }
        try {
            super.doDispatch(request, response);
        } catch (Exception e) {
            LOGGER.error("Dispatch failed: ", e);
        }
        
    }

}