package com.anu.upload.service;

import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.anu.upload.domain.ContactDetails;
import com.anu.upload.domain.PersonalInfo;
import com.anu.upload.domain.UserFile;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.anu.upload.util.UploadUtil;



@Service
public class UploadService {
	
	private final UploadUtil uploadUtil;
	
	Workbook workbook;
	
	public UploadService(UploadUtil uploadUtil) {
		this.uploadUtil = uploadUtil;
	}

	public UserFile upload(MultipartFile file) throws Exception {

		String[] sheetsToRead = {"personalInfo","contact"};
		
		Path tempDir = Files.createTempDirectory("");

		File tempFile = tempDir.resolve(file.getOriginalFilename()).toFile();
		
		file.transferTo(tempFile);

		workbook = WorkbookFactory.create(tempFile);
		
		UserFile userFile = new UserFile();
		for(String sheet:sheetsToRead) {
			List<Map<String, String>> data = readSheet(sheet);
			
			if(data!=null && data.size()>0){
				switch (sheet){
					case "personalInfo":
						mapPersonalInfo(userFile, data);
						break;
					case "contact":
						mapContacts(userFile, data);
				}
			}
			
		}
		return userFile;
	}
	
	public void mapPersonalInfo(UserFile userFile, List<Map<String, String>> data){
		for(Map<String, String> map: data){
			PersonalInfo personalInfo = new PersonalInfo();
			personalInfo.setFirstName(map.get("firstName"));
			personalInfo.setLastName(map.get("lastName"));
			userFile.addPersonalInfo(personalInfo);
		}
	}
	
	public void mapContacts(UserFile userFile, List<Map<String, String>> data){
		for(Map<String, String> map: data){
			ContactDetails contactDetails = new ContactDetails();
			contactDetails.setFirstName(map.get("firstName"));
			contactDetails.setAddress(map.get("city"));
			userFile.addContactDetailsList(contactDetails);
		}
	}
	
	public List<Map<String, String>> readSheet(String sheetName){
		Sheet sheet = workbook.getSheet(sheetName);
		
		Supplier<Stream<Row>> rowStreamSupplier = uploadUtil.getRowStreamSupplier(sheet);
		
		Row headerRow = rowStreamSupplier.get().findFirst().get();
		
		List<String> headerCells = uploadUtil.getStream(headerRow)
				.map(Cell::getStringCellValue)
				.collect(Collectors.toList());
		
		int colCount = headerCells.size();
		
		return rowStreamSupplier.get()
				.skip(1)
				.map(row -> {
					
					List<String> cellList = uploadUtil.getStream(row)
							.map(Cell::getStringCellValue)
							.collect(Collectors.toList());
					
					return uploadUtil.cellIteratorSupplier(colCount)
							.get()
							.collect(toMap(headerCells::get, cellList::get));
				})
				.collect(Collectors.toList());
	}

}
