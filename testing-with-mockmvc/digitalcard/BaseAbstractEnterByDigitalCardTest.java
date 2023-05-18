package ru.raiffeisen.rmcp.id.service.digitalcard;

import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.raiffeisen.rmcp.id.RmcpIdApplication;
import ru.raiffeisen.rmcp.id.config.properties.RmcpIdAccessViaEmailProperties;
import ru.raiffeisen.rmcp.id.config.properties.RmcpIdProperties;
import ru.raiffeisen.rmcp.id.domain.customer.Customer;
import ru.raiffeisen.rmcp.id.security.authentication.UserAuthentication;
import ru.raiffeisen.rmcp.id.security.mfa.MfaToken;
import ru.raiffeisen.rmcp.id.service.CustomerService;
import ru.raiffeisen.rmcp.id.service.MfaTokenService;
import ru.raiffeisen.rmcp.id.service.digitalcard.impl.DigitalCardGettingAccessByEmailServiceImpl;
import ru.raiffeisen.rmcp.id.service.digitalcard.utils.AuthUserMockTestUtils;
import ru.raiffeisen.rmcp.id.service.digitalcard.utils.CustomerUserTypeMockTestUtils;
import ru.raiffeisen.rmcp.id.service.digitalcard.utils.MfaTokenMockTestUtils;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static ru.raiffeisen.rmcp.id.service.digitalcard.utils.EmailUtils.maskedEmail;


@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseAbstractEnterByDigitalCardTest {

    @MockBean
    protected MfaTokenService mfaTokenService;

    @MockBean
    protected CustomerService customerService;

    protected static RmcpIdProperties rmcpIdProperties;

    protected DigitalCardGettingAccessByEmailService digitalCardGettingAccessByEmailService;

    public static RmcpIdAccessViaEmailProperties accessViaEmailProperties;

    protected String emailExpected;

    protected String emailMaskedExpected;

    protected String mfaTokenValueExpected;

    protected Faker faker;
    protected FakeValuesService fakeValuesService;

    protected String phoneNumberExpected;

    protected Mono<MfaToken> mfaTokenUserTypeMonoMock;

    protected Mono<MfaToken> newMfaTokenUserTypeMonoMockExpected;

    protected MfaToken mfaTokenUserMock;

    protected Flux<Customer> customerFluxMock;

    protected AuthUserMockTestUtils authUserMockTestUtils;

    protected MfaTokenMockTestUtils mfaTokenMockTestUtils;

    static {

        int timeToValidityForMfaToken = 5;
        Duration timeToValidity = Duration.of(timeToValidityForMfaToken, ChronoUnit.MINUTES);

        accessViaEmailProperties = new RmcpIdAccessViaEmailProperties();
        accessViaEmailProperties
                .getAccessViaEmail()
                .getMfa()
                .setTimeToValidity(timeToValidity);

        int timeToLiveAdditionalToMfaToken = 2;
        Duration timeToLive = Duration.of(timeToLiveAdditionalToMfaToken, ChronoUnit.MINUTES);

        accessViaEmailProperties
                .getAccessViaEmail()
                .getMfa()
                .setAdditionalTtl(timeToLive);

        int amountAttemptsEnterEmail = 5;
        accessViaEmailProperties
                .getAccessViaEmail()
                .getNumberAttempts()
                .setEnterEmail(amountAttemptsEnterEmail);

        rmcpIdProperties = new RmcpIdProperties();

    }

    /**
     * {@link UserAuthentication } <b>userAuthenticationCustomerTypeMock</b> -
     * для пользователя, с типом {@link ru.raiffeisen.rmcp.id.domain.user.UserType#CUSTOMER}
     * используем {@link AuthUserMockTestUtils#buildUserAuthenticationUser(Faker, String, String)}
     * <p>
     * {@link UserAuthentication } <b>userAuthenticationUserTypeMock</b> -
     * для пользователя, с типом {@link ru.raiffeisen.rmcp.id.domain.user.UserType#USER}
     * <p>
     * Cоздаем первоначальный Mfa token в оболочке Mono
     * {@link MfaTokenMockTestUtils#createMfaTokenMonoMock(String, Faker, RmcpIdAccessViaEmailProperties, UserAuthentication, String)}
     * </p>
     * <p>
     * создаем первоначальный Mfa token без оболочки Mono
     * {@link MfaTokenMockTestUtils#createMfaTokenMock(String, Faker, RmcpIdAccessViaEmailProperties, UserAuthentication, String)}
     * </p>
     * <p>
     * Cоздаем entity Customer в оболочке Flux
     * {@link CustomerUserTypeMockTestUtils#buildCustomerFluxEntity(Faker, String, String)}
     * </p>
     * <p>
     *  Подготавливаем ответы, для макетов-сервисов {@link MfaTokenService} и {@link CustomerService},
     *  например, {@link BaseAbstractEnterByDigitalCardTest#buildAnswerAfterCreatedNewMfaToken()} и т.д.
     * </p>
     * <p>
     * Инициализируем компонент {@link DigitalCardGettingAccessByEmailService}, методы которого будет защищаться тестами.
     * Здесь будет использоваться набор mocks (макетов сервисов):
     * </p>
     */
    @BeforeAll
    protected void init() {

        this.authUserMockTestUtils = new AuthUserMockTestUtils();

        this.mfaTokenMockTestUtils = new MfaTokenMockTestUtils();

        CustomerUserTypeMockTestUtils customerUserTypeMockTestUtils = new CustomerUserTypeMockTestUtils();

        this.faker = new Faker();
        this.fakeValuesService = new FakeValuesService(
                new Locale("en-GB"), new RandomService());

        this.mfaTokenValueExpected = this.faker.internet().uuid();

        this.emailExpected = this.fakeValuesService.bothify("????##@mail.com");

        this.emailMaskedExpected = maskedEmail(this.emailExpected, rmcpIdProperties);

        this.phoneNumberExpected = this.faker.numerify("79#########");


      /*  UserAuthentication userAuthenticationCustomerTypeMock =
                authUserMockTestUtils
                        .buildUserAuthenticationCustomer(this.faker, this.phoneNumberExpected, this.emailExpected);*/

        UserAuthentication userAuthenticationUserTypeMock = authUserMockTestUtils
                .buildUserAuthenticationUser(this.faker, this.phoneNumberExpected, this.emailExpected);

        this.mfaTokenUserTypeMonoMock = mfaTokenMockTestUtils
                .createMfaTokenMonoMock(
                        this.mfaTokenValueExpected,
                        this.faker,
                        accessViaEmailProperties,
                        userAuthenticationUserTypeMock,
                        this.phoneNumberExpected);


        this.mfaTokenUserMock = mfaTokenMockTestUtils
                .createMfaTokenMock(
                        this.mfaTokenValueExpected,
                        this.faker,
                        accessViaEmailProperties,
                        userAuthenticationUserTypeMock,
                        this.phoneNumberExpected);

        customerFluxMock = customerUserTypeMockTestUtils
                .buildCustomerFluxEntity(
                        this.faker,
                        this.phoneNumberExpected,
                        this.emailExpected);

        /*Подготавливаем ответы, для макетов-сервисов MfaTokenService и CustomerService*/
        buildAnswerAfterCreateNewMfaTokenMock();
        buildAnswerAfterReadMfaToken();
        buildAnswerAfterCountingIncreaseAttemptEnterEmail();
        buildAnswerAfterFindCustomerByEmail();
        buildAnswerAfterFindCustomerByMobilePhoneAndEmail();
        buildAnswerAfterRemoveMfaToken();
        buildAnswerAfterCreatedNewMfaToken();

        this.digitalCardGettingAccessByEmailService =
                new DigitalCardGettingAccessByEmailServiceImpl(
                        this.mfaTokenService,
                        rmcpIdProperties,
                        accessViaEmailProperties,
                        this.customerService);

        // System.out.println(digitalCardGettingAccessByEmailService);
        /*Mono<MfaToken> mfaTokenCustomerTypeMock = mfaTokenMockTestUtils
                .createMfaTokenMonoMock(
                        this.faker,
                        accessViaEmailProperties,
                        userAuthenticationCustomerTypeMock,
                        this.phoneNumberExpected);


        mfaTokenMockTestUtils
                .createMfaTokenMonoMock(
                        this.faker,
                        accessViaEmailProperties,
                        userAuthenticationUserTypeMock,
                        this.phoneNumberExpected);*/
    }

    private void buildAnswerAfterCreateNewMfaTokenMock() {

        doReturn(this.mfaTokenUserTypeMonoMock)
                .when(this.mfaTokenService)
                .createNewMfaToken(
                        any(UserAuthentication.class),
                        any(String.class),
                        any(UUID.class),
                        any(String.class),
                        any(Duration.class)
                );
    }

    private void buildAnswerAfterReadMfaToken() {

        doReturn(this.mfaTokenUserTypeMonoMock)
                .when(this.mfaTokenService)
                .readMfaToken(
                        any(String.class)
                );
    }

    private void buildAnswerAfterCountingIncreaseAttemptEnterEmail() {
        Mono<Long> amountAttemptEnterByEmail = Mono.just(1L);

        doReturn(amountAttemptEnterByEmail)
                .when(
                        this.mfaTokenService
                ).increaseAttemtpEnterEmail(
                        any(MfaToken.class)
                );
    }


    private void buildAnswerAfterFindCustomerByEmail() {

        doReturn(this.customerFluxMock)
                .when(
                        this.customerService
                )
                .getCustomerByEmail(
                        any(String.class)
                );
    }

    private void buildAnswerAfterFindCustomerByMobilePhoneAndEmail() {

        doReturn(this.customerFluxMock)
                .when(this.customerService)
                .getByMobilePhoneAndEmail(
                        any(String.class), any(String.class)
                );

    }

    private void buildAnswerAfterRemoveMfaToken() {

        doReturn(Mono.empty())
                .when(
                        this.mfaTokenService)
                .removeMfaToken(
                        any(MfaToken.class)
                );
    }

    private void buildAnswerAfterCreatedNewMfaToken() {

        Mono<MfaToken> mfaTokenNewMono = authUserMockTestUtils
                .buildNewMfaToken(this.mfaTokenUserMock, this.emailExpected, this.mfaTokenMockTestUtils);

        doReturn(mfaTokenNewMono)
                .when(this.mfaTokenService)
                .createNewMfaToken(
                        any(UserAuthentication.class),
                        any(String.class),
                        any(UUID.class),
                        any(String.class),
                        any(Duration.class)
                );

    }
}
