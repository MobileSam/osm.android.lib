package eu.trentorise.smartcampus.osm.android.tileprovider.modules;


import java.io.InputStream;

import eu.trentorise.smartcampus.osm.android.tileprovider.MapTile;
import eu.trentorise.smartcampus.osm.android.tileprovider.tilesource.ITileSource;

/**
 * Represents a write-only interface into a file system cache.
 * 
 * @author Marc Kurtz
 * 
 */
public interface IFilesystemCache {
	/**
	 * Save an InputStream as the specified tile in the file system cache for the specified tile
	 * source.
	 * 
	 * @param pTileSourceInfo
	 *            a tile source
	 * @param pTile
	 *            a tile
	 * @param pStream
	 *            an InputStream
	 * @return
	 */
	boolean saveFile(final ITileSource pTileSourceInfo, MapTile pTile,
			final InputStream pStream);
}