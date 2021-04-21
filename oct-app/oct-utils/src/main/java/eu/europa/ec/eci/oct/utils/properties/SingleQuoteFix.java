package eu.europa.ec.eci.oct.utils.properties;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.regex.Pattern;

public class SingleQuoteFix {

	private final static Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\d+\\}");
	
	private final static Pattern SINGLE_QUOTE_PATTERN = Pattern.compile("(?<=^|[^'])'(?=[^']|$)");

	public SingleQuoteFix(File srcFile, File destFile) throws IOException {		
		copyFile(srcFile, destFile);
	}

	private String fixLine(String line) {
		if (PLACEHOLDER_PATTERN.matcher(line).find() && SINGLE_QUOTE_PATTERN.matcher(line).find()) {
			return SINGLE_QUOTE_PATTERN.matcher(line).replaceAll("''");
		}
		return line;
	}
	
	
	public void copyFile(File fromFile, File toFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fromFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(toFile));

        //... Loop as long as there are input lines.
        String line = null;
        while ((line=reader.readLine()) != null) {
            writer.write(fixLine(line));
            writer.newLine();   // Write system dependent end of line.
        }

        reader.close();  // Close to unlock.
        writer.close();  // Close to unlock and flush to disk.
    }
	
	
	
	public static void main(String[] args) throws IOException{
		
		String srcFolderName = "";
		String destFolderName = "";
		if(args.length>0){
			srcFolderName = args[0];
		}
		if(args.length>1){
			destFolderName = args[1];
		}
		
		File destFolder = new File(destFolderName);
		File srcFolder = new File(srcFolderName);
		
		System.out.println("src folder: " + srcFolderName + ", dest folder: " + destFolderName);
		
		if(srcFolder.isDirectory()){
			
			File[] files = srcFolder.listFiles(new FilenameFilter() {				
				@Override
				public boolean accept(File dir, String name) {
					// TODO Auto-generated method stub
					return name.endsWith(".properties");
				}
			});
			
			for (File file : files) {
				File destFile = new File(destFolder, file.getName());
				System.out.println("rewriting file " + file.getAbsolutePath() + ", to file " + destFile.getAbsolutePath());
				new SingleQuoteFix(file, destFile);
				
			}
			System.out.println("finished");
		} else {
			System.out.println("no source directory!");
		}
		
		
	}

}
