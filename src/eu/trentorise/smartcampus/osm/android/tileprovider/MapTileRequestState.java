package eu.trentorise.smartcampus.osm.android.tileprovider;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

import eu.trentorise.smartcampus.osm.android.tileprovider.IMapTileProviderCallback;

import eu.trentorise.smartcampus.osm.android.tileprovider.modules.MapTileModuleProviderBase;

public class MapTileRequestState {

	private final Queue<MapTileModuleProviderBase> mProviderQueue;
	private final MapTile mMapTile;
	private final IMapTileProviderCallback mCallback;
	private MapTileModuleProviderBase mCurrentProvider;

	public MapTileRequestState(final MapTile mapTile,
			final MapTileModuleProviderBase[] providers,
			final IMapTileProviderCallback callback) {
		mProviderQueue = new LinkedList<MapTileModuleProviderBase>();
		Collections.addAll(mProviderQueue, providers);
		mMapTile = mapTile;
		mCallback = callback;
	}

	public MapTile getMapTile() {
		return mMapTile;
	}

	public IMapTileProviderCallback getCallback() {
		return mCallback;
	}

	public boolean isEmpty() {
		return mProviderQueue.isEmpty();
	}

	public MapTileModuleProviderBase getNextProvider() {
		mCurrentProvider = mProviderQueue.poll();
		return mCurrentProvider;
	}

	public MapTileModuleProviderBase getCurrentProvider() {
		return mCurrentProvider;
	}
}
