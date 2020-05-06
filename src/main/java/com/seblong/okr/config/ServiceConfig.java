package com.seblong.okr.config;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import com.seblong.okr.utils.SnowflakeIdWorker;

@Configuration
@EnableScheduling
@EnableAsync
@ComponentScan(basePackages = { "com.seblong.okr.services" })
public class ServiceConfig extends BaseServiceConfig{
	
	@Bean(destroyMethod = "shutdown")
	public ThreadPoolTaskExecutor executor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(20);
		executor.setMaxPoolSize(50);
		executor.setQueueCapacity(512);
		executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				try {
					TimeUnit.SECONDS.sleep(1500);
					executor.execute(r);
				} catch (InterruptedException e) {
				}
			}
		});
		executor.setWaitForTasksToCompleteOnShutdown(true);
		return executor;
	}
	
	@Bean
	public RestTemplate RestTemplate(RestTemplateBuilder restTemplateBuilder) throws NoSuchAlgorithmException,
			KeyStoreException, KeyManagementException {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setConnectionRequestTimeout(10000);
		requestFactory.setConnectTimeout(10000);
		requestFactory.setReadTimeout(10000);

		SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null,
				(X509Certificate[] certificates, String type) -> true).build();
		SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext,
				NoopHostnameVerifier.INSTANCE);

		Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
				.register("http", new PlainConnectionSocketFactory()).register("https", socketFactory).build();

		PoolingHttpClientConnectionManager poolHttpClienManger = new PoolingHttpClientConnectionManager(registry);
		poolHttpClienManger.setMaxTotal(200);
		poolHttpClienManger.setDefaultMaxPerRoute(50);
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory)
				.setConnectionManager(poolHttpClienManger).setConnectionManagerShared(true).build();
		requestFactory.setHttpClient(httpClient);

		RestTemplate restTemplate = new RestTemplate(requestFactory);
		return restTemplate;
	}
	
	public ServiceConfig(@Value("${snail.okr.datacenterId}") int datacenterId, @Value("${snail.okr.workerId}") int workerId) {
		snowflakeId = new SnowflakeIdWorker(workerId, datacenterId);
	}
}
