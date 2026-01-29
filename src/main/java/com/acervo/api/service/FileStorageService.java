package com.acervo.api.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

  private final MinioClient minioClient;

  @Value("${minio.bucket-name}")
  private String bucketName;

  @Value("${minio.endpoint}")
  private String endpoint;

  public String upload(MultipartFile file, String folder) {
    try {
      String filename = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
      // evitar erros de barra dupla
      String objectName = folder + "/" + filename;

      // caso exista
      boolean found = minioClient.bucketExists(io.minio.BucketExistsArgs.builder().bucket(bucketName).build());
      if (!found) {
        minioClient.makeBucket(io.minio.MakeBucketArgs.builder().bucket(bucketName).build());

        // R public
        String policy = """
            {
              "Version": "2012-10-17",
              "Statement": [
                {
                  "Effect": "Allow",
                  "Principal": {"AWS": ["*"]},
                  "Action": ["s3:GetObject"],
                  "Resource": ["arn:aws:s3:::%s/*"]
                }
              ]
            }
            """.formatted(bucketName);
        minioClient.setBucketPolicy(
            io.minio.SetBucketPolicyArgs.builder().bucket(bucketName).config(policy).build());
      }

      try (InputStream inputStream = file.getInputStream()) {
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(inputStream, file.getSize(), -1)
                .contentType(file.getContentType())
                .build());
      }
      // Retorna URL pública acessível pelo navegador (localhost)
      // em prod deve mudar a var de ambiente
      return endpoint + bucketName + "/" + objectName;
    } catch (Exception e) {
      e.printStackTrace(); // Log do stack trace para depuração
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Erro ao fazer upload da imagem: " + e.getMessage(), e);
    }
  }
}
