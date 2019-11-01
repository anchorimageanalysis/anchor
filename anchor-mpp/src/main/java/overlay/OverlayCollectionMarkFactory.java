package overlay;

import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.color.RGBColor;

import ch.ethz.biol.cell.mpp.cfg.Cfg;
import ch.ethz.biol.cell.mpp.gui.videostats.internalframe.markredraw.ColoredCfg;
import ch.ethz.biol.cell.mpp.mark.regionmap.RegionMembershipWithFlags;

/**
 * Two-way factory.
 * 
 * Creation of OverlayCollection from marks
 * Retrieval of marks back from OverlayCollections
 * 
 * @author Owen Feehan
 *
 */
public class OverlayCollectionMarkFactory {
	
	public static OverlayCollection createWithoutColor( Cfg cfg, RegionMembershipWithFlags regionMembership ) {
		OverlayCollection out = new OverlayCollection();
		
		for(int i=0; i<cfg.size(); i++) {
			Mark m = cfg.get(i);
			out.add( new OverlayMark(m, regionMembership) );
		}
		
		return out;
	}

	public static ColoredOverlayCollection createColor( ColoredCfg cfg, RegionMembershipWithFlags regionMembership ) {
		return createColor( cfg.getCfg(), cfg.getColorList(), regionMembership );
	}
	
	
	private static ColoredOverlayCollection createColor(
		Cfg cfg,
		ColorIndex colorIndex,
		RegionMembershipWithFlags regionMembership
	) {
		
		ColoredOverlayCollection out = new ColoredOverlayCollection();
		
		for(int i=0; i<cfg.size(); i++) {
			Mark m = cfg.get(i);
			RGBColor color = colorIndex.get(i);
			
			// TODO should we duplicate color?
			out.add( new OverlayMark(m, regionMembership), color );
		}
		
		return out;
	}

	// Creates a cfg from whatever Overlays are found in the collection
	public static Cfg cfgFromOverlays( OverlayCollection overlays ) {
		Cfg out = new Cfg();
		
		for(int i=0; i<overlays.size(); i++) {
			Overlay overlay = overlays.get(i);
			
			if (overlay instanceof OverlayMark) {
				OverlayMark overlayMark = (OverlayMark) overlay;
				out.add( overlayMark.getMark() );
			}
		}
		
		return out;
	}
	
	

	// Creates a cfg from whatever Overlays are found in the collection
	public static ColoredCfg cfgFromOverlays( ColoredOverlayCollection overlays ) {
		ColoredCfg out = new ColoredCfg();
		
		for(int i=0; i<overlays.size(); i++) {
			Overlay overlay = overlays.get(i);
			
			RGBColor col = overlays.getColor(i);
			
			if (overlay instanceof OverlayMark) {
				OverlayMark overlayMark = (OverlayMark) overlay;
				out.add( overlayMark.getMark(), col );
			}
		}
		
		return out;
	}


	
	

}
