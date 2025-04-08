package no.brreg.regnskap.utils.stubs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ProcessStub extends Process {
    public OutputStream outputStream;
    public InputStream inputStream;
    public InputStream errorStream;

    public ProcessStub(boolean errors) {
        outputStream = new ByteArrayOutputStream();
        inputStream = new ByteArrayInputStream(errors ? new byte[0] : "testdata".getBytes());
        errorStream = new ByteArrayInputStream(errors ? "Error message".getBytes() : new byte[0]);
    }

    @Override
    public OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public InputStream getErrorStream() {
        return errorStream;
    }

    @Override
    public int waitFor() throws InterruptedException {
        return 0;
    }

    @Override
    public int exitValue() {
        return 0;
    }

    @Override
    public void destroy() {

    }
}
