/*-
 * #%L
 * anchor-io
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

package org.anchoranalysis.core.serialize;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Path;
import org.anchoranalysis.core.time.OperationContext;

/**
 * Deserializes using Java's Native serialization framework.
 *
 * <p>See <a
 * href="https://docs.oracle.com/javase/8/docs/platform/serialization/spec/serialTOC.html">documentation
 * on Java's native serialization</a>.
 *
 * @author Owen Feehan
 * @param <T> type of object to deserialize.
 */
public class ObjectInputStreamDeserializer<T> implements Deserializer<T> {

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(Path filePath, OperationContext context)
            throws DeserializationFailedException {

        if (!filePath.toFile().exists()) {
            throw new DeserializationFailedException(
                    String.format("File '%s' does not exist", filePath));
        }

        try {
            // Deserialize from a file
            try (ObjectInputStream stream = createInputStream(filePath)) {

                Object uncast = stream.readObject();

                if (uncast == null) {
                    throw new DeserializationFailedException(
                            "Deserialization failed for unknown reasons");
                }

                return (T) uncast;
            }

        } catch (ClassNotFoundException | IOException e) {
            throw new DeserializationFailedException(e);
        }
    }

    private static ObjectInputStream createInputStream(Path filePath) throws IOException {
        return new ObjectInputStream(new FileInputStream(filePath.toFile()));
    }
}
