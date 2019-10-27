package org.anchoranalysis.io.csv.comparer;

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
import java.io.PrintStream;
import java.nio.file.Path;

import org.anchoranalysis.io.csv.reader.CSVReader;
import org.anchoranalysis.io.csv.reader.CSVReader.OpenedCSVFile;
import org.apache.commons.lang.ArrayUtils;

public class CSVComparer {

	/**
	 * Are two CSV files equal?
	 * 
	 * @param file1 first file to compare, without any lines having been already read
	 * @param file2 second file to compare, without any lines having been already read
	 * @param ignoreFirstNumColumns ignore the first number of columns (left most) when measuring equality
 	 * @param sortLines if true, all lines in the CSV file are sorted before comparison. if false, the order remains unchanged.
 	 * @param rejectZeroRows throws an exception if the CSV file has zero rows
 	 * @param messageStream if non-equal, additional explanation messages are printed here 
	 * @throws IOException if something goes wrong
	 */
	public static boolean areCsvFilesEqual(
		Path path1,
		Path path2,
		String regExSeperator,
		boolean firstLineHeaders,		
		int ignoreFirstNumColumns,
		boolean sortLines,
		boolean rejectZeroRows,
		PrintStream messageStream
	) throws IOException {
		
		OpenedCSVFile file1 = openCsvFromFilePath(path1, regExSeperator, firstLineHeaders);
		OpenedCSVFile file2 = openCsvFromFilePath(path2, regExSeperator, firstLineHeaders);
		
		if (firstLineHeaders && !checkHeadersIdentical(file1, file2, messageStream)) {
			return false;
		}
		
		if (sortLines) {
			CompareSorted compareWithSorting = new CompareSorted( ignoreFirstNumColumns, rejectZeroRows );
			return compareWithSorting.compare( file1, file2, System.out );
		} else {
			CompareUnsorted compareUnsorted = new CompareUnsorted();
			return compareUnsorted.compareCsvFilesWithoutSorting( file1, file2, ignoreFirstNumColumns, rejectZeroRows );
		}
	}
	
	private static boolean checkHeadersIdentical(OpenedCSVFile file1, OpenedCSVFile file2, PrintStream messageStream) {
		String[] headers1 = file1.getHeaders();
		String[] headers2 = file2.getHeaders();
		if (ArrayUtils.isEquals(headers1, headers2)) {
			return true;
		} else {
			messageStream.println("Headers are not identical:");
			CompareUtilities.printTwoLines(messageStream, headers1, headers2);
			return false;
		}		
	}
	
	
	/**
	 * Opens a CSV file from an absolute filePath
	 * 
	 * @param filePath absolute file-path
	 * @param regExSeperator the seperator for the csv file (a regular expression for split function)
	 * @param firstLineHeaders are their headers?
	 * @return a CSVFile object, but without any rows having been read
	 * @throws TestDataLoadException
	 */
	private static OpenedCSVFile openCsvFromFilePath( Path filePath, String regExSeperator, boolean firstLineHeaders ) throws IOException {
	
		CSVReader csvReader = new CSVReader(regExSeperator, firstLineHeaders);
		return csvReader.read(filePath);
	}

}
