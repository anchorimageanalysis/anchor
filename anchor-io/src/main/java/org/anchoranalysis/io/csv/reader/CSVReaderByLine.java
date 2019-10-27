package org.anchoranalysis.io.csv.reader;

/*-
 * #%L
 * anchor-io
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import java.io.IOException;
import java.nio.file.Path;

import org.anchoranalysis.core.error.OperationFailedException;

/**
 * Reads a CSV file line-by-line
 * 
 * This is a helpful class that quickly processes a CSV file without having to directly
 * interact with CSVReader
 * 
 * @author FEEHANO
 *
 */
public class CSVReaderByLine {

	@FunctionalInterface
	public static interface ProcessCSVLine {
		void processLine( String[] line, boolean firstLine ) throws OperationFailedException;
	}
	
	public static interface ReadByLine extends AutoCloseable {
		
		/**
		 * The headers of the CSV file
		 * 
		 * @return a string or NULL if the headers don't exist
		 */
		String[] headers() throws IOException;
		
		/**
		 * Reads a CSV-file iterating through each row and passing it to lineProcessor
		 * 
		 * This will automatically close any opened-files
		 * 
		 * @param lineProcessor called one for each row incrementally
		 * @return the number of lines read
		 * @throws IOException
		 */
		int read( ProcessCSVLine lineProcessor ) throws IOException;
		
		/**
		 * Closes any opened-files
		 * 
		 * @throws IOException
		 */
		void close() throws IOException;
	}
	
	private static class ReadByLineImpl implements ReadByLine {
		
		private CSVReader csvReader;
		private Path filePath;
		
		private CSVReader.OpenedCSVFile openedFile = null;
		
		public ReadByLineImpl(Path filePath, CSVReader csvReader) {
			super();
			this.filePath = filePath;
			this.csvReader = csvReader;
		}

		@Override
		public String[] headers() throws IOException {
			openIfNecessary();
			return openedFile.getHeaders();
		}
		
		@Override
		public int read( ProcessCSVLine lineProcessor ) throws IOException {

			try {
				openIfNecessary();
				
				String[] line;
				boolean firstLine = true; 
				
				int cnt = 0;
				
				while ( (line= openedFile.readLine())!=null ) {
					lineProcessor.processLine(line, firstLine);
					firstLine = false;
					cnt++;
				}
				
				return cnt;
				
			} catch (IOException | OperationFailedException e) {
				throw new IOException(e);
			} finally {
				close();
			}
		
		}
		
		
		private void openIfNecessary() throws IOException {
			if (openedFile==null) {
				openedFile = csvReader.read( filePath );
			}
		}

		/** Closes any opened-files */
		@Override
		public void close() throws IOException {
			if (openedFile!=null) {
				openedFile.close();
				openedFile = null;
			}
		}
		
	}
		
	/** Default reader, using comma and first-line headers */
	public static ReadByLine open( Path filePath ) {
		return new ReadByLineImpl( filePath, new CSVReader( ",", true ) );
	}
	
	/** Default reader, using comma and first-line headers */
	public static ReadByLine open( Path filePath, String regExSeperator, boolean firstLineHeaders ) {
		return new ReadByLineImpl(
			filePath,
			new CSVReader( regExSeperator, firstLineHeaders )
		);
	}
	
	/** Default reader, using comma and first-line headers */
	public static ReadByLine open( Path filePath, String regExSeperator, boolean firstLineHeaders, boolean quotedStrings ) {
		return new ReadByLineImpl(
			filePath,
			new CSVReader( regExSeperator, firstLineHeaders, quotedStrings )
		);
	}
}
