package eu.trentorise.smartcampus.osm.android.util;

import java.io.File;
import eu.trentorise.smartcampus.osm.android.tileprovider.constants.OSMapTileProviderConstants;
/**
 * A class for handling the deletion of the osmdroid cache which would be saved in "/Android/data/eu/trentorise/smartcampus/osm/android"
 * NO need to be instanciated
 * @author Michele Armellini
 *
 */
public class DeleteCache implements OSMapTileProviderConstants{

	private static void deleteRecursive(File f) {
		if (f.isDirectory())
			for (File child : f.listFiles())
				deleteRecursive(child);

		f.delete();
	}
	/**
	 * Call this method to delete all the files in the osmdroid's cache directory.
	 */
	public static void deleteCache()
	{
		deleteRecursive(OSMDROID_PATH);
	}

}
