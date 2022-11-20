package com.aws.s3.service;

import java.io.File;
import java.io.IOException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.aws.s3.constants.CommonConstants;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;


public class EntryPoint {

//	public static void main(String[] args) throws IOException {
	public static void SaveToCloud(String bucketName, String FolderName, String filePath) {

		CommonService commonService = new CommonService();
		// credentials object identifying user for authentication
		// user must have AWSConnector and AmazonS3FullAccess for
		// this example to work
		AWSCredentials credentials = new BasicAWSCredentials(CommonConstants.ACCESS_KEY_ID,CommonConstants.ACCESS_SEC_KEY);

		// create a client connection based on credentials
		//AmazonS3 s3client = new AmazonS3Client(credentials);

		AmazonS3 s3client = AmazonS3ClientBuilder
				  .standard()
				  .withCredentials(new AWSStaticCredentialsProvider(credentials))
				  .withRegion(Regions.AP_SOUTH_1)
				  .build();

		// create bucket - name must be unique for all S3 users
//		String bucketName = CommonConstants.BUCKET_NAME;
		String cbucketName = bucketName;

//		s3client.createBucket(bucketName);

		// create folder into bucket
		String cfolderName = FolderName;
//		CommonService.createFolder(bucketName, folderName, s3client,CommonConstants.SUFFIX);

		// upload file to folder and set it to public
//		String fileName = folderName + CommonConstants.SUFFIX + CommonConstants.FILE_NAME;
		String cfileName = cfolderName + CommonConstants.SUFFIX + CommonConstants.FILE_NAME;

		s3client.putObject(
//				new PutObjectRequest(bucketName, fileName, new File(CommonConstants.FILE_PATH))
				new PutObjectRequest(cbucketName, cfileName, new File(filePath))

				.withCannedAcl(CannedAccessControlList.PublicRead));

		System.out.println("Execution Completed");

		commonService.getObj(s3client);

//		CommonService.deleteFolder(bucketName, folderName, s3client);

		// deletes bucket
//		s3client.deleteBucket(bucketName);
	}

	static AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.AP_SOUTH_1).build();

	public static boolean downloadFile(String bucketName, String objectName, String dirname)
	{
		CommonService commonService = new CommonService();
		// credentials object identifying user for authentication
		// user must have AWSConnector and AmazonS3FullAccess for
		// this example to work
		AWSCredentials credentials = new BasicAWSCredentials(CommonConstants.ACCESS_KEY_ID,CommonConstants.ACCESS_SEC_KEY);

		// create a client connection based on credentials
		//AmazonS3 s3client = new AmazonS3Client(credentials);

		AmazonS3 s3 = AmazonS3ClientBuilder
				.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(Regions.AP_SOUTH_1)
				.build();

//		String dbucketName = CommonConstants.BUCKET_NAME;
		String dbucketName = bucketName;

//		String dfolderName = CommonConstants.FOLDER_NAME;

//		String dfileName = dfolderName + CommonConstants.SUFFIX + CommonConstants.FILE_NAME;

		String dfileName = objectName;

//		String dlocalFileName = CommonConstants.LOCAL_DOWNLOAD_PATH + CommonConstants.FILE_NAME;
		String dlocalFileName = dirname + CommonConstants.SUFFIX + CommonConstants.FILE_NAME;

		S3Object object;
		InputStream objectData;
		InputStream reader = null;
		OutputStream writer = null;
		try
		{
			object = s3.getObject(new GetObjectRequest(dbucketName, dfileName));
			objectData = object.getObjectContent();
		}
		catch(Exception e)
		{
			System.out.println("Error001 downloading file "+dbucketName+"/"+dfileName+" to "+dlocalFileName);
			return false;
		}
		try
		{
			File file = new File(dlocalFileName);
			try
			{
				reader = new BufferedInputStream(object.getObjectContent());
				writer = new BufferedOutputStream(new FileOutputStream(file));
				int read = -1;
				while ( ( read = reader.read() ) != -1 )
				{
					writer.write(read);
				}
				System.out.println("file from "+dbucketName+"/"+dfileName+" "+" downloaded to "+dlocalFileName + " successfully");
				return true;
			}
			catch(IOException e)
			{
				System.out.println("Error downloading file "+dbucketName+"/"+dfileName+" to "+dlocalFileName);
				return false;
			}
			finally
			{
				object.close();
				writer.flush();
				writer.close();
				reader.close();
			}

		}
		catch(IOException e)
		{
			System.out.println("Error opening local file "+dlocalFileName+" for writing ");
			return false;
		}
	}

	public static boolean CheckFile(String BucketName, String Objectname) {
		AWSCredentials credentials = new BasicAWSCredentials(CommonConstants.ACCESS_KEY_ID, CommonConstants.ACCESS_SEC_KEY);

		AmazonS3 s3 = AmazonS3ClientBuilder
				.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(Regions.AP_SOUTH_1)
				.build();

		boolean doesItExists = false;
		try {
			doesItExists = s3.doesObjectExist(BucketName, Objectname);
			System.out.println("Does the object exist in the given bucket " + doesItExists);
		} catch (Exception error) {
			System.out.println("this is the error " + error);
		}
		return doesItExists;
	}

	public static boolean checkFolder(String bucketName, String key) {
		AWSCredentials credentials = new BasicAWSCredentials(CommonConstants.ACCESS_KEY_ID, CommonConstants.ACCESS_SEC_KEY);

		AmazonS3 s3 = AmazonS3ClientBuilder
				.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(Regions.AP_SOUTH_1)
				.build();

		try {
			ListObjectsV2Result result = s3.listObjectsV2(bucketName, key);
			return result.getKeyCount() > 0;
		} catch (Exception error) {
			System.out.println("this is the error " + error);
		}
		return false;
	}
}