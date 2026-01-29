package com.acervo.api.service;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

  private final MinioClient minioClient;

  @Value("${minio.bucket-name}")
  private String bucketName;

  @Value("${minio.endpoint}")
  private String endpoint;

  @Value("${minio.access-key}")
  private String accessKey;

  @Value("${minio.secret-key}")
  private String secretKey;

  private MinioClient signerClient;

  @PostConstruct
  public void initSigner() {
    log.info("Inicializando MinIO - Endpoint: {}", endpoint);
    this.signerClient = MinioClient.builder()
        .endpoint(endpoint)
        .credentials(accessKey, secretKey)
        .region("us-east-1")
        .build();
  }

  /**
   * Faz o upload do arquivo e retorna o nome do objeto (chave) salvo.
   */
  public String upload(MultipartFile file, String folder) {
    try {
      String filename = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
      String objectName = folder + "/" + filename;

      boolean found = minioClient.bucketExists(
          io.minio.BucketExistsArgs.builder().bucket(bucketName).build());

      if (!found) {
        minioClient.makeBucket(
            io.minio.MakeBucketArgs.builder().bucket(bucketName).build());
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

      return objectName;
    } catch (Exception e) {
      log.error("Erro no upload", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Erro ao fazer upload da imagem: " + e.getMessage(), e);
    }
  }

  /**
   * Gera uma URL pré-assinada temporária (30 minutos) para visualização do
   * arquivo.
   */
  public String generatePresignedUrl(String objectName) {
    try {
      if (objectName == null || objectName.isBlank()) {
        return null;
      }

      String url = signerClient.getPresignedObjectUrl(
          GetPresignedObjectUrlArgs.builder()
              .method(Method.GET)
              .bucket(bucketName)
              .object(objectName)
              .expiry(30, TimeUnit.MINUTES)
              .build());

      return url;

    } catch (Exception e) {
      log.error("Erro ao gerar URL para: {}", objectName, e);
      return null;
    }
  }
}
