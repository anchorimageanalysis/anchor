package org.anchoranalysis.core.log;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.core.log.error.ErrorReporterIntoLog;

/*
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

/**
 * A logging-mechanism for both messages and errors.
 *
 * <p>Messages are written to a {@link MessageLogger} whereas errors are written to a {@link
 * ErrorReporter} (which may or may not report the errors back into the message-logger).
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
@Accessors(fluent = true)
public class Logger {

    /** Where messages are logged to. */
    @Getter private final MessageLogger messageLogger;

    /** Where errors are reported to. */
    @Getter private final ErrorReporter errorReporter;

    /**
     * Constructs with an error-reporter that writes into the message logger.
     *
     * @param messageLogger where messages are logged to.
     */
    public Logger(MessageLogger messageLogger) {
        this.messageLogger = messageLogger;
        this.errorReporter = new ErrorReporterIntoLog(messageLogger);
    }
}
