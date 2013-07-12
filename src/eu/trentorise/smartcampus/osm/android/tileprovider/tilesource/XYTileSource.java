package eu.trentorise.smartcampus.osm.android.tileprovider.tilesource;


import eu.trentorise.smartcampus.osm.android.ResourceProxy.string;
import eu.trentorise.smartcampus.osm.android.tileprovider.MapTile;

public class XYTileSource extends OnlineTileSourceBase {

	public XYTileSource(final String aName, final string aResourceId, final int aZoomMinLevel,
			final int aZoomMaxLevel, final int aTileSizePixels, final String aImageFilenameEnding,
			final String... aBaseUrl) {
		super(aName, aResourceId, aZoomMinLevel, aZoomMaxLevel, aTileSizePixels,
				aImageFilenameEnding, aBaseUrl);
	}

	@Override
	public String getTileURLString(final MapTile aTile) {
		return getBaseUrl() + aTile.getZoomLevel() + "/" + aTile.getX() + "/" + aTile.getY()
				+ mImageFilenameEnding;
	}
}