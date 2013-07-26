package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Utils {
	// list of unprocessed files
	private List listNP = new ArrayList();

	// Proposed deletion list
	private List listDel = new ArrayList();

	// signature and duplicate file association map
	private HashMap map = null;

	private static String UNKNOWN_ERROR = "Ignore_unkown_error";

	private static final String MAGIC_KEY = " ; ";

	private static final float MAX_SIZE = 102400; // 100 KB

	public static final int DELETION_LIST = 0;

	public static final int ERROR_LIST = 1;

	// File I/O
	private String argFile;

	public Utils(String argFile) {
		super();
		this.argFile = argFile;
	}

	public boolean isExistNPList() {
		return !(listNP.isEmpty());
	}

	private char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private String byteArray2Hex(byte[] array) {
		StringBuffer hex = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			int hbits = (array[i] & 0x000000f0) >> 4;
			int lbits = array[i] & 0x0000000f;
			hex.append("" + hexChars[hbits] + hexChars[lbits] + " ");
		}
		return hex.toString();
	}

	private String calcDigest(String datafile) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(datafile);
		} catch (FileNotFoundException e) {
			System.err.println("No Such File or Directory : " + datafile);
		} catch (Exception e) {
			// if any other exception return control with special string and
			// proceed with other file
			e.printStackTrace();
			return UNKNOWN_ERROR;
		}

		byte[] dataBytes = new byte[1024];
		int nread = 0;
		try {
			nread = fis.read(dataBytes);
		} catch (IOException e) {
			System.err.println("Failed to read File : " + datafile);
		} catch (Exception e) {
			// if any other exception return control with special string and
			// proceed with other file
			e.printStackTrace();
			return UNKNOWN_ERROR;
		}

		while (nread > 0) {
			md.update(dataBytes, 0, nread);
			try {
				nread = fis.read(dataBytes);
			} catch (IOException e) {
				System.err.println("Failed while reading File : " + datafile
						+ " . Last Read Bytes : " + nread);
			} catch (Exception e) {
				// if any other exception return control with special string and
				// proceed with other file
				e.printStackTrace();
				return UNKNOWN_ERROR;
			}
		}

		byte[] mdbytes = md.digest();
		return byteArray2Hex(mdbytes);
	}

	private double getFileLength(String filename) {
		File file = new File(filename);
		return file.length();
	}

	/*
	 * BUILD AND POPULATE DATA STRUCTURES
	 * 
	 * Construct a Key-value Hash Map. ( contains key-value pairs with unique
	 * Keys, Duplicate Keys not allowed while insertion.) from input File.
	 * 
	 * Duplicate entries are appended ( semicolon seperated ) to value for same
	 * signature
	 */
	public void buildMap() {
		BufferedReader filereader = null;
		try {
			filereader = new BufferedReader(new FileReader(argFile));
		} catch (FileNotFoundException e) {
			System.err.println("No Such File or Directory : " + argFile);
			e.printStackTrace();
			System.exit(128);
		}

		BufferedWriter filewriter = null;
		try {
			filewriter = new BufferedWriter(new FileWriter(argFile + ".siz"));
		} catch (IOException e1) {
			System.err.println("Failed to create duplicate File : " + argFile
					+ ".siz");
		}

		if (filereader != null) {
			map = new HashMap();
			try {
				int procCount = 0;
				int dupCount = 0;
				float dupBytes = 0;
				while (filereader.ready()) {
					String filename = filereader.readLine().trim();
					if (filename != null && filename.length() > 1) {
						String key = calcDigest(filename);

						// System.out.println(key+ " "+filename);
						if (!key.equalsIgnoreCase(UNKNOWN_ERROR)) {
							try {
								key = key.replaceAll("\\s+", "");
								if (map.containsKey(key)) {

									// add file size in bytes to global counter
									double fLen = getFileLength(filename);

									// add filename to deletion list if greater
									// than MAX_SIZE
									if (fLen > MAX_SIZE)
										listDel.add(filename);

									dupBytes += fLen;

									try {
										Object[] obj = { filename,
												new Float(fLen / 1024) };
										filewriter.write(String.format(
												"%-150s : %16.2f KB\n", obj));
										filewriter.flush();
									} catch (IOException e) {
										System.err
												.println("Failed to write duplicate File : "
														+ argFile + ".siz");
										e.printStackTrace();
									} catch (NullPointerException e) {
										// ignore
									}

									// append filename to same signature
									map.put(key, map.get(key) + MAGIC_KEY
											+ filename);
									dupCount++;
								} else
									map.put(key, filename);
								procCount++;
							} catch (NullPointerException e) {
								// ignore
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else
							listNP.add(filename);
					}
				}
				System.out.println("Total number of Processed files : "
						+ procCount);
				System.out.println("Total number of Duplicate files : "
						+ dupCount);

				Object[] obj = { new Float(dupBytes / (1024 * 1024)) };
				System.out
						.printf(
								"Total Disk Space taken up by Duplicate files : %.2f MB\n",
								obj);
				System.out.println("Duplicate File Size Details added to : "
						+ argFile + ".siz");

			} catch (IOException e) {
				System.err.println("Failed to Read File : " + argFile);
				e.printStackTrace();
				System.exit(126);
			}
		}

		// try {
		// filewriter.flush();
		// } catch (IOException e) {
		// e.printStackTrace();
		// } catch (NullPointerException e) {
		// // ignore
		// }
	}

	/*
	 * WRITE HASHMAP to FILE ( Write file entries that have duplicates; based on
	 * signature association )
	 */
	public void writeOutputFile() {
		BufferedWriter filewriter = null;
		try {
			filewriter = new BufferedWriter(new FileWriter(argFile + ".out"));
			Set set = map.keySet();
			Iterator iter = set.iterator();

			while (iter.hasNext()) {
				String key = (String) iter.next();
				String val = (String) map.get(key);

				// only write the entries that have duplicate files
				if (val.contains(MAGIC_KEY)) {
					filewriter.write(key + "  " + val);
					filewriter.write("\n");
				}
			}
		} catch (IOException e) {
			System.err.println("Failed to write File : " + argFile + ".out");
			e.printStackTrace();
			System.exit(125);
		} finally {
			try {
				if (filewriter != null) {
					filewriter.close();
					System.out
							.println("Duplication File Association added to : "
									+ argFile + ".out");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/*
	 * WRITE ARRAYLIST to FILE ( for unprocessed file entries )
	 */
	public void writeFile(int listType) {
		String Filename = argFile;
		List list = null;
		switch (listType) {
		case DELETION_LIST:
			Filename += ".del";
			list = listDel;
			break;
		case ERROR_LIST:
			Filename += ".nop";
			list = listNP;
			break;
		default:
			System.err.println("INTERNAL ERROR : List Type Not implemented");
			break;
		}

		BufferedWriter filewriter = null;
		try {
			filewriter = new BufferedWriter(new FileWriter(Filename));
			for (int i = 0; i < list.size(); i++) {
				filewriter.write(list.get(i).toString());
				filewriter.write("\n");
			}
		} catch (IOException e) {
			System.err.println("Failed to write File : " + Filename);
			e.printStackTrace();
			System.exit(125);
		} finally {
			try {
				if (filewriter != null) {
					filewriter.close();
					
					switch (listType) {
					case DELETION_LIST:
						System.out
								.println("Proposed Files entries for deletion added to : "
										+ Filename);
						break;
					case ERROR_LIST:
						System.out
						.println("Files entries not processed added to : "
								+ Filename);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
