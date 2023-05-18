package ru.raiffeisen.rmcp.id.service.digitalcard;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.raiffeisen.rmcp.id.dto.AccessViaEmailResponseDto;
import ru.raiffeisen.rmcp.id.dto.digitalcard.AccessByEmailForNoUniquePhoneNumberRequestDto;
import ru.raiffeisen.rmcp.id.security.mfa.MfaToken;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DigitalCardGettingAccessByEmailServiceSuccessTest extends BaseAbstractEnterByDigitalCardTest {

    /**
     * !!! Важно, чтобы mocks {@link MfaToken} использовался один и тот же, на протяжении всего времени запроса,
     * а также его значения, которые он сохраняет внутри ('encapsulating').
     * {@link Mockito#reset(Object[])} - необходим, так как нужно будет сформировать новое значение в нужном mock.
     */
    @BeforeAll
    void setUp() {
    }


    @Test
    void getAccessWithUniqEmail() {

        AccessByEmailForNoUniquePhoneNumberRequestDto requestDto =
                AccessByEmailForNoUniquePhoneNumberRequestDto
                        .builder()
                        .email(emailExpected)
                        .mfaToken(mfaTokenValueExpected)
                        .build();

   /*     Mono<AccessViaEmailResponseDto> accessWithUniqEmail =
                digitalCardGettingAccessByEmailService.getAccessWithUniqEmail(requestDto);*/

        StepVerifier.create(
                        super.digitalCardGettingAccessByEmailService.getAccessWithUniqEmail(requestDto)
                )
                .assertNext(accessViaEmailResponseDto -> {

                            String emailMaskedActual = accessViaEmailResponseDto.getEmail();
                            String mfaTokenValueActual = accessViaEmailResponseDto.getMfaToken();

                            Assertions.assertEquals(super.emailMaskedExpected, emailMaskedActual);
                            Assertions.assertEquals(super.mfaTokenValueExpected, mfaTokenValueActual);
                        }
                )
                .verifyComplete();

      /*  accessWithUniqEmail
                .as(StepVerifier::create)
                .assertNext(accessViaEmailResponseDto -> {
                            assertThat(accessViaEmailResponseDto.getEmail().equals(emailExpected));
                        }
                )
                .verifyComplete();*/
/*
        AccessViaEmailResponseDto block = accessWithUniqEmail.block();
        String mfaToken = block.getMfaToken();*/
    }

    @Test
    void checkPossibilityUseEmail() {
    }

    @Test
    void createNewUserAuthenticationForMfaFlowTypeAccessViaEmail() {
    }

    @Test
    void calculateExtendedMFADuration() {
    }

    @Test
    @DisplayName("Test : to check the method that masks the email.")
    void getMaskedEmailGivenByUserTest() {

        String email = "email12345@mail.com";
        String maskedEmailActual = digitalCardGettingAccessByEmailService.getMaskedEmailGivenByUser(email);

        String maskedEmailExpected = "ema*******@mail.com";
        Assertions.assertEquals(maskedEmailExpected, maskedEmailActual);
    }

    @Test
    @DisplayName("Test : validate mfa token.")
    void isEmptyMfaTokenTest() {

        boolean isEmptyMfaToken = digitalCardGettingAccessByEmailService.isEmptyMfaToken(mfaTokenValueExpected);
        Assertions.assertTrue(isEmptyMfaToken);

        String emptyValue = "";
        isEmptyMfaToken = digitalCardGettingAccessByEmailService.isEmptyMfaToken(emptyValue);
        Assertions.assertFalse(isEmptyMfaToken);

        boolean isNullMfaToken = digitalCardGettingAccessByEmailService.isEmptyMfaToken(null);
        Assertions.assertFalse(isNullMfaToken);
    }
}