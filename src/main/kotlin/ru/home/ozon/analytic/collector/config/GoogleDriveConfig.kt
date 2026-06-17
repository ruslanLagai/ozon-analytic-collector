package ru.home.ozon.analytic.collector.config

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.apache.v2.ApacheHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.auth.Credentials
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.home.ozon.analytic.collector.config.properties.GoogleDriveProperties
import java.io.FileInputStream


@Configuration
@EnableConfigurationProperties(GoogleDriveProperties::class)
class GoogleDriveConfig {

    @Bean
    fun googleHttpTransport(): HttpTransport = ApacheHttpTransport()

    @Bean
    fun googleJsonFactory(): JsonFactory = GsonFactory.getDefaultInstance()

    @Bean
    @ConditionalOnMissingBean(Credential::class)
    fun googleDriveCredential(
        props: GoogleDriveProperties
    ): HttpCredentialsAdapter {
        return HttpCredentialsAdapter(
            GoogleCredentials
                .fromStream(this.javaClass.classLoader.getResourceAsStream((props.keyFile)))
                .createScoped(listOf(DriveScopes.DRIVE))
        )
    }

    @Bean
    fun googleDriveClient(
        transport: HttpTransport,
        jsonFactory: JsonFactory,
        googleDriveCredential: HttpCredentialsAdapter,
    ): Drive = Drive.Builder(
            transport,
            jsonFactory,
        googleDriveCredential,
        )
        .setApplicationName("ozon-analytic-collector")
        .build()
}