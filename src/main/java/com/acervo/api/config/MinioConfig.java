package com.acervo.api.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Config básica para MinIO (criar bucket e política de RO)
// só cria se n existir

@Configuration
@Slf4j
public class MinioConfig {

    private final String endpoint;
    private final String accessKey;
    private final String secretKey;
    private final String bucketName;
    private final MinioClient minioClient;

    public MinioConfig(
            @Value("${minio.endpoint}") String endpoint,
            @Value("${minio.access-key}") String accessKey,
            @Value("${minio.secret-key}") String secretKey,
            @Value("${minio.bucket-name}") String bucketName) {

        this.endpoint = endpoint;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.bucketName = bucketName;

        // Criar MinioClient no construtor para evitar referência circular
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    @Bean
    public MinioClient minioClient() {
        return minioClient;
    }

    @PostConstruct
    public void initBucket() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                log.info("Bucket '{}' não encontrado. Criando...", bucketName);
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());

                // Criando politica de RO
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

                minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                        .bucket(bucketName)
                        .config(policy)
                        .build());
                log.info("Bucket '{}' criado.", bucketName);
            } else {
                log.info("Bucket '{}' existe.", bucketName);
            }
        } catch (Exception e) {
            log.error("Erro ao inicializar MinIO: {}", e.getMessage(), e);
            // Falha não bloqueante mas o upload pode falhar. isso aqui é placebo!!!
        }
    }
}
