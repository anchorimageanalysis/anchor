package org.anchoranalysis.test.image.rasterwriter;

import java.io.IOException;
import java.util.Optional;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.bean.rasterwriter.RasterWriter;
import org.junit.Test;

/**
 * For testing all {@link RasterWriter}s that create TIFFs.
 * 
 * @author Owen Feehan
 *
 */
public abstract class TiffTestBase extends RasterWriterTestBase {
    
    public TiffTestBase() {
        super("tif", true, true, Optional.of("ome.xml") );
    }

    @Test
    public void testSingleChannel() throws RasterIOException, IOException {
        tester.testSingleChannel();
    }
        
    @Test(expected=RasterIOException.class)
    public void testSingleChannelRGB() throws RasterIOException, IOException {
        tester.testSingleChannelRGB();
    }
    
    @Test(expected=RasterIOException.class)
    public void testTwoChannels() throws RasterIOException, IOException {
        tester.testTwoChannels();
    }
    
    @Test
    public void testThreeChannelsSeparate() throws RasterIOException, IOException {
        tester.testThreeChannelsSeparate();
    }
    
    @Test
    public void testThreeChannelsRGB() throws RasterIOException, IOException {
        tester.testThreeChannelsRGB();
    }
    
    @Test(expected=RasterIOException.class)
    public void testFourChannels() throws RasterIOException, IOException {
        tester.testFourChannels();
    }
}
