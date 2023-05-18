package ru.raiffeisen.rmcp.id.service.digitalcard.utils;

import com.github.javafaker.Faker;
import reactor.core.publisher.Mono;
import ru.raiffeisen.rmcp.id.domain.user.UserType;
import ru.raiffeisen.rmcp.id.security.authentication.AuthUser;
import ru.raiffeisen.rmcp.id.security.authentication.UserAuthentication;
import ru.raiffeisen.rmcp.id.security.mfa.MfaFlowType;
import ru.raiffeisen.rmcp.id.security.mfa.MfaState;
import ru.raiffeisen.rmcp.id.security.mfa.MfaToken;
import ru.raiffeisen.rmcp.id.service.digitalcard.BaseAbstractEnterByDigitalCardTest;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

public class AuthUserMockTestUtils {

    public UserAuthentication buildUserAuthenticationCustomer(Faker faker,
                                                              String phoneNumberExpected,
                                                              String emailExpected) {

        AuthUser.ResourceOwner resourceOwnerDataCustomer = createResourceOwnerDataCustomer(
                faker,
                phoneNumberExpected,
                emailExpected);

        AuthUser principal = buildAuthUser(resourceOwnerDataCustomer, faker, UserType.CUSTOMER);

        return new UserAuthentication(
                principal,
                MfaState.ACCESS_VIA_EMAIL_OTP_REQUIRED,
                MfaFlowType.ACCESS_VIA_EMAIL);
    }

    public UserAuthentication buildUserAuthenticationUser(Faker faker,
                                                          String phoneNumberExpected,
                                                          String emailExpected) {

        AuthUser.ResourceOwner resourceOwnerDataCustomer = createResourceOwnerDataCustomer(
                faker,
                phoneNumberExpected,
                emailExpected);

        AuthUser principal = buildAuthUser(resourceOwnerDataCustomer, faker, UserType.USER);

        return new UserAuthentication(
                principal,
                MfaState.ACCESS_VIA_EMAIL_OTP_REQUIRED,
                MfaFlowType.ACCESS_VIA_EMAIL);
    }

    private AuthUser buildAuthUser(AuthUser.ResourceOwner resourceOwnerNewData, Faker faker, UserType userType) {

        int startValue = 1111;
        int finishValue = 999999;
        long customerId = faker
                .number()
                .numberBetween(startValue, finishValue);

        String userId = faker.internet().uuid();
        String username = faker.name().username();
        String password = faker.internet().password();

        int oneDay = 1;
        Instant expireLogin = Instant
                .now()
                .plus(oneDay, ChronoUnit.DAYS);
        LocalDateTime expireLoginAt = LocalDateTime.ofInstant(expireLogin, ZoneId.systemDefault());

        return AuthUser
                .builder()
                .userId(userId)
                .userType(userType)
                .customerId(customerId)
                .username(username)
                .password(password)
                .resourceOwner(resourceOwnerNewData)
                .accountNonLocked(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .expiredAt(expireLoginAt)
                .ronlineEnabled(false)
                .build();
    }


    public AuthUser.ResourceOwner createResourceOwnerDataCustomer(
            Faker faker,
            String phoneNumberExpected,
            String emailExpected) {

        String firstName = faker.address().firstName();
        String lastName = faker.address().lastName();
        String middleName = faker.name().firstName();
        Date birthday = faker.date().birthday();

        LocalDate birthdayActual = Instant
                .ofEpochMilli(birthday.getTime())
                .atZone(ZoneId.systemDefault()).toLocalDate();

        Integer categoryId = 70;

        return AuthUser.ResourceOwner.builder()
                .firstName(firstName)
                .lastName(lastName)
                .middleName(middleName)
                .birthDate(birthdayActual)
                .email(emailExpected)
                .mobilePhone(phoneNumberExpected)
                .isBiometric(false)
                .categoryId(categoryId)
                .employee(false)
                .build();
    }


    public Mono<MfaToken> buildNewMfaToken(
            MfaToken mfaToken,
            String actualEmail,
            MfaTokenMockTestUtils mfaTokenMockTestUtils) {

        String sessionId = mfaToken.getSessionId();
        UUID otpSessionId = mfaToken.getOtpSessionId();
        String numberMobilePhone = retrieveMobilePhoneFromMfaToken(mfaToken);
        Duration extendedMFADuration = calculateExtendedMFATime();

        String tokenValue = mfaToken.getTokenValue();

        UserAuthentication userAuthenticationNew = buildUserAuthenticationWithEmail(mfaToken, actualEmail);

        assert otpSessionId != null;

        return mfaTokenMockTestUtils
                .createNewMfaTokenMonoMock(
                        tokenValue,
                        userAuthenticationNew,
                        sessionId,
                        otpSessionId,
                        numberMobilePhone,
                        extendedMFADuration);

    }

    public Duration calculateExtendedMFATime() {

        Duration timeToValidity = BaseAbstractEnterByDigitalCardTest
                .accessViaEmailProperties
                .getAccessViaEmail()
                .getMfa()
                .getTimeToValidity();

        Duration additionalTtl = BaseAbstractEnterByDigitalCardTest
                .accessViaEmailProperties
                .getAccessViaEmail()
                .getMfa()
                .getAdditionalTtl();

        return timeToValidity.plus(additionalTtl);
    }

    private String retrieveMobilePhoneFromMfaToken(MfaToken mfaToken) {
        UserAuthentication userAuthentication = mfaToken.getUserAuthentication();

        assert userAuthentication != null;
        AuthUser userDetails = userAuthentication.getUserDetails();

        return userDetails.getResourceOwner().getMobilePhone();
    }


    /**
     * Для формирования правдоподобной имитации, данный код повторяет рабочий,
     * чтобы
     * {@link ru.raiffeisen.rmcp.id.service.MfaTokenService#createNewMfaToken(UserAuthentication, String, UUID, String, Duration)},
     * вернул правильный ответ, в виде ожидаемого значения {@link ru.raiffeisen.rmcp.id.security.mfa.MfaToken}
     */
    private UserAuthentication buildUserAuthenticationWithEmail(MfaToken mfaToken, String actualEmail) {

        UserAuthentication userAuthenticationOld = mfaToken.getUserAuthentication();

        assert userAuthenticationOld != null;
        AuthUser principal = userAuthenticationOld.getUserDetails();
        MfaState mfaState = userAuthenticationOld.getMfaState();

        AuthUser.ResourceOwner resourceOwnerNewData = changeResourceOwnerData(principal, actualEmail);
        AuthUser principalNew = changeAuthUser(principal, resourceOwnerNewData);


        return new UserAuthentication(
                principalNew,
                mfaState,
                MfaFlowType.ACCESS_VIA_EMAIL);
    }

    /**
     * Для формирования правдоподобной имитации, данный код повторяет рабочий,
     * чтобы
     * {@link ru.raiffeisen.rmcp.id.service.MfaTokenService#createNewMfaToken(UserAuthentication, String, UUID, String, Duration)},
     * вернул правильный ответ, в виде ожидаемого значения {@link ru.raiffeisen.rmcp.id.security.mfa.MfaToken}
     */
    private AuthUser changeAuthUser(AuthUser principal, AuthUser.ResourceOwner resourceOwnerNewData) {

        return AuthUser
                .builder()
                .userId(principal.getUserId())
                .userType(UserType.USER)
                .customerId(principal.getCustomerId())
                .username(principal.getUsername())
                .password(principal.getPassword())
                .resourceOwner(resourceOwnerNewData)
                .accountNonLocked(principal.isAccountNonLocked())
                .accountNonExpired(principal.isAccountNonExpired())
                .credentialsNonExpired(principal.isCredentialsNonExpired())
                .enabled(principal.isEnabled())
                .expiredAt(principal.getExpiredAt())
                .ronlineEnabled(principal.isRonlineEnabled())
                .build();
    }

    /**
     * Для формирования правдоподобной имитации, данный код повторяет рабочий,
     * чтобы
     * {@link ru.raiffeisen.rmcp.id.service.MfaTokenService#createNewMfaToken(UserAuthentication, String, UUID, String, Duration)},
     * вернул правильный ответ, в виде ожидаемого значения {@link ru.raiffeisen.rmcp.id.security.mfa.MfaToken}
     */
    private AuthUser.ResourceOwner changeResourceOwnerData(AuthUser principal, String actualEmail) {

        AuthUser.ResourceOwner resourceOwner = principal.getResourceOwner();
        return AuthUser.ResourceOwner.builder()
                .firstName(resourceOwner.getFirstName())
                .lastName(resourceOwner.getLastName())
                .middleName(resourceOwner.getMiddleName())
                .birthDate(resourceOwner.getBirthDate())
                .email(actualEmail)
                .mobilePhone(resourceOwner.getMobilePhone())
                .isBiometric(resourceOwner.getIsBiometric())
                .categoryId(resourceOwner.getCategoryId())
                .employee(resourceOwner.isEmployee())
                .build();

    }
}
