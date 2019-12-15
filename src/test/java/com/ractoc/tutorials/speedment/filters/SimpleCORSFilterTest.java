package com.ractoc.tutorials.speedment.filters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.verify;

@DisplayName("Test the CategoryHandler")
@ExtendWith(MockitoExtension.class)
class SimpleCORSFilterTest {

    private SimpleCORSFilter filter = new SimpleCORSFilter();

    @Mock
    private HttpServletRequest mockedHttpServletRequest;
    @Mock
    private HttpServletResponse mockedHttpServletResponse;
    @Mock
    private FilterChain mockedFilterChain;

    @DisplayName("Test if all the required headers are set")
    @Test
    void doFilter() throws IOException, ServletException {
        // Given

        // When
        filter.doFilter(mockedHttpServletRequest, mockedHttpServletResponse, mockedFilterChain);

        // Then
        verify(mockedHttpServletResponse).setHeader("Access-Control-Allow-Origin", "*");
        verify(mockedHttpServletResponse).setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE, PATCH");
        verify(mockedHttpServletResponse).setHeader("Access-Control-Max-Age", "3600");
        verify(mockedHttpServletResponse).setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
        verify(mockedHttpServletResponse).setHeader("Access-Control-Expose-Headers", "Location");
        verify(mockedFilterChain).doFilter(mockedHttpServletRequest, mockedHttpServletResponse);
    }
}
