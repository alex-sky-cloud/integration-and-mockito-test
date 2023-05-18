package ru.raiffeisen.rmcp.id.service.digitalcard.utils;

import com.github.javafaker.Faker;
import reactor.core.publisher.Mono;
import ru.raiffeisen.rmcp.id.config.properties.RmcpIdAccessViaEmailProperties;
import ru.raiffeisen.rmcp.id.security.authentication.UserAuthentication;
import ru.raiffeisen.rmcp.id.security.mfa.MfaToken;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class MfaTokenMockTestUtils {


    /**
     * создаем первоначальный Mfa token в оболочке Mono
     * */
    public Mono<MfaToken> createMfaTokenMonoMock(String valueToken,
                                                 Faker faker,
                                                 RmcpIdAccessViaEmailProperties accessViaEmailProperties,
                                                 UserAuthentication userAuthentication,
                                                 String numberMobilePhone) {

        String sessionId = faker.internet().uuid();
        UUID otpSessionId = UUID.randomUUID();

        Duration timeToValidity = accessViaEmailProperties.getAccessViaEmail().getMfa().getTimeToValidity();
        Duration additionalTtl = accessViaEmailProperties.getAccessViaEmail().getMfa().getAdditionalTtl();

        Duration extendedMFADuration = timeToValidity.plus(additionalTtl);

        MfaToken mfaToken = new MfaToken(
                valueToken,
                Instant.now(),
                Instant.now().plus(extendedMFADuration),
                sessionId,
                otpSessionId,
                null,
                null,
                numberMobilePhone,
                userAuthentication
        );

        return Mono.just(mfaToken);
    }

    public Mono<MfaToken> createNewMfaTokenMonoMock(String valueToken,
                                                    UserAuthentication userAuthentication,
                                                    String sessionId,
                                                    UUID otpSessionId,
                                                    String numberMobilePhone,
                                                    Duration extendedMFADuration
    ) {


        MfaToken mfaToken = new MfaToken(
                valueToken,
                Instant.now(),
                Instant.now().plus(extendedMFADuration),
                sessionId,
                otpSessionId,
                null,
                null,
                numberMobilePhone,
                userAuthentication
        );

        return Mono.just(mfaToken);
    }


    public MfaToken createMfaTokenMock(String valueToken,
                                       Faker faker,
                                       RmcpIdAccessViaEmailProperties accessViaEmailProperties,
                                       UserAuthentication userAuthentication,
                                       String numberMobilePhone) {

        String sessionId = faker.internet().uuid();
        UUID otpSessionId = UUID.randomUUID();

        Duration timeToValidity = accessViaEmailProperties.getAccessViaEmail().getMfa().getTimeToValidity();
        Duration additionalTtl = accessViaEmailProperties.getAccessViaEmail().getMfa().getAdditionalTtl();

        Duration extendedMFADuration = timeToValidity.plus(additionalTtl);

        return new MfaToken(
                valueToken,
                Instant.now(),
                Instant.now().plus(extendedMFADuration),
                sessionId,
                otpSessionId,
                null,
                null,
                numberMobilePhone,
                userAuthentication
        );
    }
}
