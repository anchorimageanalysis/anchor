/* (C)2020 */
package org.anchoranalysis.experiment.bean.io;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.OptionHandler;

class Log4JFileAppender implements Appender, OptionHandler {

    // Bean interface
    private FileAppender delegate = new FileAppender();
    private String outputName = null;
    private String fallbackFile;
    // End bean interface

    private BoundOutputManagerRouteErrors outputManager = null;

    public void init(BoundOutputManagerRouteErrors outputManager) {
        this.outputManager = outputManager;
    }

    @Override
    public void activateOptions() {

        if (outputManager != null) {

            Path filePath = outputManager.outFilePath(outputName + "." + "txt");
            delegate.setFile(filePath.toString());

        } else {
            delegate.setFile(fallbackFile);
        }

        delegate.activateOptions();
    }

    public void append(LoggingEvent event) {
        delegate.append(event);
    }

    @Override
    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    public boolean getAppend() {
        return delegate.getAppend();
    }

    public int getBufferSize() {
        return delegate.getBufferSize();
    }

    public boolean getBufferedIO() {
        return delegate.getBufferedIO();
    }

    public String getEncoding() {
        return delegate.getEncoding();
    }

    public String getFile() {
        return delegate.getFile();
    }

    public final Filter getFirstFilter() {
        return delegate.getFirstFilter();
    }

    public boolean getImmediateFlush() {
        return delegate.getImmediateFlush();
    }

    public Priority getThreshold() {
        return delegate.getThreshold();
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    public boolean isAsSevereAsThreshold(Priority priority) {
        return delegate.isAsSevereAsThreshold(priority);
    }

    public void setAppend(boolean flag) {
        delegate.setAppend(flag);
    }

    public void setBufferSize(int bufferSize) {
        delegate.setBufferSize(bufferSize);
    }

    public void setBufferedIO(boolean bufferedIO) {
        delegate.setBufferedIO(bufferedIO);
    }

    public void setEncoding(String value) {
        delegate.setEncoding(value);
    }

    public void setFile(String arg0, boolean arg1, boolean arg2, int arg3) throws IOException {
        delegate.setFile(arg0, arg1, arg2, arg3);
    }

    public void setFile(String file) {
        delegate.setFile(file);
    }

    public void setImmediateFlush(boolean value) {
        delegate.setImmediateFlush(value);
    }

    public void setThreshold(Priority threshold) {
        delegate.setThreshold(threshold);
    }

    public void setWriter(Writer writer) {
        delegate.setWriter(writer);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    @Override
    public void addFilter(Filter newFilter) {
        delegate.addFilter(newFilter);
    }

    @Override
    public void clearFilters() {
        delegate.clearFilters();
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public void doAppend(LoggingEvent event) {
        delegate.doAppend(event);
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return delegate.getErrorHandler();
    }

    @Override
    public Filter getFilter() {
        return delegate.getFilter();
    }

    @Override
    public Layout getLayout() {
        return delegate.getLayout();
    }

    @Override
    public final String getName() {
        return delegate.getName();
    }

    @Override
    public boolean requiresLayout() {
        return delegate.requiresLayout();
    }

    @Override
    public void setErrorHandler(ErrorHandler eh) {
        delegate.setErrorHandler(eh);
    }

    @Override
    public void setLayout(Layout layout) {
        delegate.setLayout(layout);
    }

    @Override
    public void setName(String name) {
        delegate.setName(name);
    }

    public String getOutputName() {
        return outputName;
    }

    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }

    public String getFallbackFile() {
        return fallbackFile;
    }

    public void setFallbackFile(String fallbackFile) {
        this.fallbackFile = fallbackFile;
    }
}
