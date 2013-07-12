package eu.trentorise.smartcampus.osm.android.tileprovider;

import eu.trentorise.smartcampus.osm.android.tileprovider.modules.INetworkAvailablityCheck;
import eu.trentorise.smartcampus.osm.android.tileprovider.modules.MapTileDownloader;
import eu.trentorise.smartcampus.osm.android.tileprovider.modules.MapTileFileArchiveProvider;
import eu.trentorise.smartcampus.osm.android.tileprovider.modules.MapTileFilesystemProvider;
import eu.trentorise.smartcampus.osm.android.tileprovider.modules.NetworkAvailabliltyCheck;
import eu.trentorise.smartcampus.osm.android.tileprovider.modules.TileWriter;
import eu.trentorise.smartcampus.osm.android.tileprovider.tilesource.ITileSource;
import eu.trentorise.smartcampus.osm.android.tileprovider.tilesource.TileSourceFactory;
import eu.trentorise.smartcampus.osm.android.tileprovider.util.SimpleRegisterReceiver;

import android.content.Context;

/**
 * This top-level tile provider implements a basic tile request chain which includes a
 * {@link MapTileFilesystemProvider} (a file-system cache), a {@link MapTileFileArchiveProvider}
 * (archive provider), and a {@link MapTileDownloader} (downloads map tiles via tile source).
 * 
 * @author Marc Kurtz
 * 
 */
public class MapTileProviderBasic extends MapTileProviderArray implements IMapTileProviderCallback {

	// private static final Logger logger = LoggerFactory.getLogger(MapTileProviderBasic.class);

	/**
	 * Creates a {@link MapTileProviderBasic}.
	 */
	public MapTileProviderBasic(final Context pContext) {
		this(pContext, TileSourceFactory.DEFAULT_TILE_SOURCE);
	}

	/**
	 * Creates a {@link MapTileProviderBasic}.
	 */
	public MapTileProviderBasic(final Context pContext, final ITileSource pTileSource) {
		this(new SimpleRegisterReceiver(pContext), new NetworkAvailabliltyCheck(pContext),
				pTileSource);
	}

	/**
	 * Creates a {@link MapTileProviderBasic}.
	 */
	public MapTileProviderBasic(final IRegisterReceiver pRegisterReceiver,
			final INetworkAvailablityCheck aNetworkAvailablityCheck, final ITileSource pTileSource) {
		super(pTileSource, pRegisterReceiver);

		final TileWriter tileWriter = new TileWriter();

		final MapTileFilesystemProvider fileSystemProvider = new MapTileFilesystemProvider(
				pRegisterReceiver, pTileSource);
		mTileProviderList.add(fileSystemProvider);

		final MapTileFileArchiveProvider archiveProvider = new MapTileFileArchiveProvider(
				pRegisterReceiver, pTileSource);
		mTileProviderList.add(archiveProvider);

		final MapTileDownloader downloaderProvider = new MapTileDownloader(pTileSource, tileWriter,
				aNetworkAvailablityCheck);
		mTileProviderList.add(downloaderProvider);
	}
}