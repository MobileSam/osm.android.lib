package eu.trentorise.smartcampus.osm.android.util;

import java.io.File;

import android.os.Environment;
/**
 * A class for handling the deletion of the osmdroid cache which would be saved in "/sdcard/osmdroid"
 * NO need to be instanciated
 * @author Michele Armellini
 *
 */
public class DeleteCache {

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
		File f = new File(Environment.getExternalStorageDirectory().getPath()+"/osmdroid");
		deleteRecursive(f);
	}

}
