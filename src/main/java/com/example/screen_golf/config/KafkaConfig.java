package com.example.screen_golf.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.example.screen_golf.reservation.dto.ReservationInfo;

@Configuration
@EnableKafka
public class KafkaConfig {

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	/**
	 * Kafka Admin 설정 - 토픽 생성 등에 필요
	 */
	@Bean
	public KafkaAdmin kafkaAdmin() {
		Map<String, Object> configs = new HashMap<>();
		configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		configs.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 5000);
		configs.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, 5000);
		return new KafkaAdmin(configs);
	}

	/**
	 * 예약 요청용 토픽 자동 생성 설정
	 */
	@Bean
	public NewTopic reservationTopic() {
		return new NewTopic("reservation-requests", 1, (short)1);
	}

	/**
	 * Kafka Producer 설정
	 * - acks: 메시지 유실 방지를 위해 'all'로 설정
	 * - compression: LZ4 압축으로 성능 향상
	 * - linger.ms & batch.size: 배치 전송 최적화
	 * - JsonSerializer: DTO 객체 전송을 위한 JSON 직렬화
	 */
	@Bean
	public ProducerFactory<String, ReservationInfo.ReservationRequest> producerFactory() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		configProps.put(ProducerConfig.ACKS_CONFIG, "all");
		configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");
		configProps.put(ProducerConfig.LINGER_MS_CONFIG, 5);
		configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 16_384);
		configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
		configProps.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);
		configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

		return new DefaultKafkaProducerFactory<>(configProps);
	}

	/**
	 * KafkaTemplate 생성 (DTO 전송용)
	 */
	@Bean
	public KafkaTemplate<String, ReservationInfo.ReservationRequest> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}
} 