package sensorDataApi;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class getFiles {
	
	public static boolean downloadSingleFile(FTPClient ftpClient,
	        String remoteFilePath, String savePath) throws IOException {
		
	    File downloadFile = new File(savePath);
	  
	     
	    File parentDir = downloadFile.getParentFile();
	    if (!parentDir.exists()) {
	        parentDir.mkdir();
	    }
	    downloadFile.createNewFile();
	         
	    OutputStream outputStream = new BufferedOutputStream(
	            new FileOutputStream(downloadFile));
	    try {
	        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	        return ftpClient.retrieveFile(remoteFilePath, outputStream);
	    } catch (IOException ex) {
	    	ex.printStackTrace();
	        throw ex;
	    } finally {
	        if (outputStream != null) {
	            outputStream.close();
	        }
	    }
	}
	
	public static void downloadDirectory(FTPClient ftpClient, String parentDir,
	        String currentDir, String saveDir) throws IOException {
	    String dirToList = parentDir;
	    if (!currentDir.equals("")) {
	        dirToList += "/" + currentDir;
	    }
	 
	    FTPFile[] subFiles = ftpClient.listFiles(dirToList);
	 
	    if (subFiles != null && subFiles.length > 0) {
	        for (FTPFile aFile : subFiles) {
	            String currentFileName = aFile.getName();
	            if (currentFileName.equals(".") || currentFileName.equals("..")) {
	                // skip parent directory and the directory itself
	                continue;
	            }
	            String filePath = parentDir  + currentDir 
	                    + currentFileName;
	            if (currentDir.equals("")) {
	                filePath = parentDir + currentFileName;
	            }
	 
	            String newDirPath = saveDir +  currentFileName;
	            if (currentDir.equals("")) {
	                newDirPath = saveDir
	                          + currentFileName;
	            }
	 
	            if (aFile.isDirectory()) {
	                // create the directory in saveDir
	                File newDir = new File(newDirPath);
	                boolean created = newDir.mkdirs();
	                if (created) {
	                    System.out.println("CREATED the directory: " + newDirPath);
	                } else {
	                    System.out.println("COULD NOT create the directory: " + newDirPath);
	                }
	 
	                // download the sub directory
	                downloadDirectory(ftpClient, dirToList, currentFileName,
	                        saveDir);
	            } else {
	                // download the file
	                boolean success = downloadSingleFile(ftpClient, filePath,
	                        newDirPath);
	                if (success) {
	                    System.out.println("DOWNLOADED the file: " + filePath);
	                } else {
	                    System.out.println("COULD NOT download the file: "
	                            + filePath);
	                }
	            }
	        }
	    }
	}
	
	public static void downloadFromFtp(String ftpIp, String ftpUser, String ftpPass, String artifactPath, String appName, String basePath) {
		
		/*
		 * Get ftp server details
		 * */
		//args[0] = ftp server
		// args[1] = ftp username
		// args[2] = ftp password
		// args[3] = app artifact root
		// args[4] = appName
		
		
		String server = ftpIp;
        int port = 21;
        String user = ftpUser;
        String pass = ftpPass;
        
        
        FTPClient ftpClient = new FTPClient();
 
        try {
            // connect and login to the server
            ftpClient.connect(server, port);
      
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
 
            // use local passive mode to pass firewall
           
 
            System.out.println("Connected");
            
            FTPFile[] subFiles = ftpClient.listFiles(artifactPath);
            System.out.println(subFiles.length);
 
            String remoteDirPath = artifactPath;
            String saveDirPath = basePath + appName + "/";
 
            downloadDirectory(ftpClient, remoteDirPath, "", saveDirPath);
 
            // log out and disconnect from the server
            ftpClient.logout();
            ftpClient.disconnect();
 
            System.out.println("Disconnected");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
	}
}