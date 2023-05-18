package ru.raiffeisen.rmcp.id.service.digitalcard.utils;

import com.github.javafaker.Faker;
import reactor.core.publisher.Flux;
import ru.raiffeisen.rmcp.id.domain.customer.Customer;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class CustomerUserTypeMockTestUtils {

    public Flux<Customer> buildCustomerFluxEntity(
            Faker faker,
            String phoneNumberExpected,
            String emailExpected) {

        int startValue = 1111;
        int finishValue = 999999;
        long customerId = faker
                .number()
                .numberBetween(startValue, finishValue);

        String firstName = faker.address().firstName();
        String lastName = faker.address().lastName();
        String middleName = faker.name().firstName();
        Date birthday = faker.date().birthday();

        LocalDate birthdayActual = Instant
                .ofEpochMilli(birthday.getTime())
                .atZone(ZoneId.systemDefault()).toLocalDate();

        Integer categoryId = 70;

        Integer employeeOrNo = 0;

        int enableCustomerOrNo = 1;

        Customer customer = new Customer(
                customerId,
                firstName,
                lastName,
                middleName,
                birthdayActual,
                emailExpected,
                phoneNumberExpected,
                updatedTimeLogin(),
                createTimeCreatedLogin(),
                categoryId,
                setClientEndDate(),
                enableCustomerOrNo,
                employeeOrNo
        );

        return Flux.just(customer);
    }

    private LocalDate setClientEndDate(){

        int days = 5000;

        Instant loginEndDate = Instant
                .now()
                .plus(days, ChronoUnit.DAYS);

        return LocalDateTime.ofInstant(loginEndDate, ZoneId.systemDefault()).toLocalDate();
    }

    private LocalDateTime updatedTimeLogin() {

        int minutes = 2;

        Instant loginUpdated = Instant
                .now()
                .plus(minutes, ChronoUnit.MINUTES);
        return LocalDateTime.ofInstant(loginUpdated, ZoneId.systemDefault());
    }

    private LocalDateTime createTimeCreatedLogin() {

        Instant loginCreated = Instant
                .now();
        return LocalDateTime.ofInstant(loginCreated, ZoneId.systemDefault());
    }
}
