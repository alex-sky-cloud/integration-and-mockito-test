package ru.raiffeisen.rmcp.id.service.digitalcard.utils;

import ru.raiffeisen.rmcp.id.config.properties.RmcpIdProperties;

import javax.validation.constraints.NotNull;

import static ru.raiffeisen.rmcp.id.service.utils.EmailUtils.getLengthEmail;
import static ru.raiffeisen.rmcp.id.service.utils.EmailUtils.getLengthHiddenSymbolsEmail;

public class EmailUtils {

    /**
     * Перед отправкой response, email будет преобразован к виду
     * email12345@mail.com -> ema*******@mail.com
     *
     * @param email - email клиента, на который будет выслан одноразовый пароль
     */
    public static String maskedEmail(@NotNull String email, RmcpIdProperties rmcpIdProperties) {

        int emailLength = getLengthEmail(email);
        Double emailSecretRate = rmcpIdProperties.getEmailAccessRecovery().getEmailSecretRate();

        long visibleSymbolsNumber = getLengthHiddenSymbolsEmail(emailLength, emailSecretRate);

        if (visibleSymbolsNumber < 1) {
            visibleSymbolsNumber = 1;
        }

        String emailSecretPattern = rmcpIdProperties.getEmailAccessRecovery().getEmailSecretPattern();
        String patternWithOpenSymbols = String.format(emailSecretPattern, visibleSymbolsNumber);

        String startSign = "*";
        return email.replaceAll(patternWithOpenSymbols, startSign);
    }
}
