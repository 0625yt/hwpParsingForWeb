package com.parse.document;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;

import com.parse.document.DataExtractContext;
import com.parse.document.common.Const;

public abstract class FileDataExtractor {

	public static File download(IProgressMonitor monitor, DataExtractContext context, String title, String[] paths) {
		return context.getFile(monitor, title, paths);
	}

	public static File[] downloads(IProgressMonitor monitor, DataExtractContext context, String title) {
		String directoryPath = Const.DEFAULT_PATH;

		File directory = new File(directoryPath);

		if (directory.exists() && directory.isDirectory()) {
			File subfolder = new File(directory, title);

			if (subfolder.exists() && subfolder.isDirectory()) {
				File[] filesInSubfolder = subfolder.listFiles();

				if (filesInSubfolder != null) {
					return filesInSubfolder;
				} else {
					System.out.println("No files found in the folder: " + subfolder.getAbsolutePath());
					return new File[0]; // Return empty array if no files are found in the folder
				}
			} else {
				System.out.println("Folder with the name '" + title + "' does not exist or is not a valid directory.");
				return new File[0]; // Return empty array if no such folder exists
			}
		} else {
			System.out.println("Directory does not exist or is not a valid directory.");
			return new File[0]; // Return empty array if directory is invalid
		}
	}
}
