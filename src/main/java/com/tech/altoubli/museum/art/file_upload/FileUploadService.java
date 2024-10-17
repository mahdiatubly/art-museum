package com.tech.altoubli.museum.art.file_upload;

import com.tech.altoubli.museum.art.exception.FileFormatException;
import com.tech.altoubli.museum.art.exception.SavingFileException;
import com.tech.altoubli.museum.art.user.User;
import com.tech.altoubli.museum.art.user.UserRepository;
import com.tech.altoubli.museum.art.user_profile.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.io.File.separator;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileUploadService {
    private final UserRepository userRepository;
    @Value("${application.file.uploads}")
    private String uploadsPath;

    private final UserProfileRepository userProfileRepository;

    public String uploadFile(MultipartFile file, String type, Authentication connectedUser) {
        String fileName = file.getOriginalFilename();
        User user = userRepository.findByEmail(connectedUser.getName())
                .orElseThrow(()-> new UsernameNotFoundException("User Not Found"));
        try {
            String extension = fileName.substring(fileName.lastIndexOf("."));
            String finalFileName = user.getId() + "-"
                    + System.currentTimeMillis() + extension;
            String filePath = uploadsPath + separator + type;
            File fileFolder = new File(filePath);
            if (!fileFolder.exists()) {
                boolean folderCreated = fileFolder.mkdirs();
                if (!folderCreated) {
                    throw new SavingFileException("The file is not saved, please try again.");
                }
            }
            try {
                String fullFilePath = filePath + separator + finalFileName;
                Path targetPath = Paths.get(fullFilePath);
                Files.write(targetPath, file.getBytes());
                log.info("File saved to: " + fullFilePath);
                return fullFilePath;
            } catch (IOException e) {
                throw new SavingFileException("File not saved, please try again.");
            }
        } catch(RuntimeException e) {
            throw new FileFormatException("This file format is  not supported.");
        }

    }
}
