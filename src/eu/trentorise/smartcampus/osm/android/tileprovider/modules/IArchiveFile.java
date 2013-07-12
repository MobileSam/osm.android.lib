package eu.trentorise.smartcampus.osm.android.tileprovider.modules;


import java.io.InputStream;

import eu.trentorise.smartcampus.osm.android.tileprovider.MapTile;
import eu.trentorise.smartcampus.osm.android.tileprovider.tilesource.ITileSource;

public interface IArchiveFile {

	/**
	 * Get the input stream for the requested tile.
	 * @return the input stream, or null if the archive doesn't contain an entry for the requested tile
	 */
	InputStream getInputStream(ITileSource tileSource, MapTile tile);

}