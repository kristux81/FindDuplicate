package utils;

public class ProcessFile {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String inputFile = null;
		if (args.length >= 1) {
				inputFile = args[0];

		} else {
			System.err
					.println("Syntax : java -jar <jarname> <input File path>");
			System.exit(1);
		}

		Utils processor = new Utils(inputFile);

		// Build processing Map, Duplicate, Not-Processed File lists
		processor.buildMap();
		
		// Write signature association file
		processor.writeOutputFile();

       //  Write deletion file
		processor.writeFile(Utils.DELETION_LIST);
		
		// Write Not Processed File ( failed to process files entries )
		if (processor.isExistNPList())
			processor.writeFile(Utils.ERROR_LIST);
	}

}
