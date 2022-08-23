package com.example.springbootteststartexample.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PricingServiceTest {

    /**
     * Аннотация {@link Mock} инструктирует Mockito, что нужно сделать заглушку объекта
     * {@link ProductVerifier}
     */
    @Mock
    private ProductVerifier productVerifier;

    /**
     * Аннотация {@link Mock} инструктирует Mockito, что нужно сделать заглушку объекта
     * {@link ProductReporter}
     */
    @Mock
    private ProductReporter productReporter;

    /**
     * Данный тест будет проверять контракт метода,
     * который возвращать прайс с ценами дешевле,
     * чем такие же продукты у конкурента, после проверки, что данный товар
     * есть также у конкурентов
     */
    @Test
    void shouldReturnCheapPriceWhenProductIsInStockOfCompetitor() {

        String productNameMocked = "AirPods";

        /**Существует ли продукт у конкурентов. Так как сюда должно прийти булево
         * значение после проверки, но ввиду того, что мы
         * используем заглушку класса ProductVerifier, поэтому будем использовать метод
         * {@link  org.mockito.stubbing.OngoingStubbing#thenReturn(Object)} */
        boolean existsProductInCompetitor = this.productVerifier
                .isCurrentlyInStockOfCompetitor(productNameMocked);


        /** В методе {@link  org.mockito.stubbing.OngoingStubbing#thenReturn(Object)},
         * мы определили значение, которое должно вернуться в переменную 'existsProductInCompetitor'*/
        when(existsProductInCompetitor)
                .thenReturn(true);

        /*Когда зависимости-заглушки готовы, можно начинать тестировать основной класс*/
        PricingService classUnderTest = new PricingService(this.productVerifier, this.productReporter);

        BigDecimal priceExpected = new BigDecimal("99.99");
        BigDecimal priceActual = classUnderTest.calculatePrice(productNameMocked);

        assertEquals(priceExpected, priceActual);
    }

    @Test
    void shouldReturnCheapPriceAndReportWhenProductIsInStockOfCompetitor() {

        String productNameMocked = "AirPods";

        /**Существует ли продукт у конкурентов. Так как сюда должно прийти булево
         * значение после проверки, но ввиду того, что мы
         * используем заглушку класса ProductVerifier, поэтому будем использовать метод
         * {@link  org.mockito.stubbing.OngoingStubbing#thenReturn(Object)} */
        boolean existsProductInCompetitor = this.productVerifier
                .isCurrentlyInStockOfCompetitor(productNameMocked);


        /** В методе {@link  org.mockito.stubbing.OngoingStubbing#thenReturn(Object)},
         * мы определили значение, которое должно вернуться в переменную 'existsProductInCompetitor'*/
        when(existsProductInCompetitor)
                .thenReturn(true);

        /*Когда зависимости-заглушки готовы, можно начинать тестировать основной класс*/
        PricingService classUnderTest = new PricingService(this.productVerifier, this.productReporter);

        BigDecimal priceExpected = new BigDecimal("99.99");
        BigDecimal priceActual = classUnderTest.calculatePrice(productNameMocked);

        assertEquals(priceExpected, priceActual);

        /** Проверяем, что метод {@link ProductReporter#notify(String)} получает то же значение аргумента,
         * которое мы задали выше для метода, основного тестируемого класса:
         * {@link PricingService#calculatePrice(String)}. Если значения которое мы указали здесь productNameMocked,
         * и значение, которое передали в {@link PricingService#calculatePrice(String)},
         * не совпадают, значит есть нарушение контракта, то есть тестируемый метод ,
         * {@link ProductReporter#notify(String)}, работает неправильно*/
        verify(this.productReporter).notify(productNameMocked);
    }


    @Test
    void shouldReturnExpensivePriceWhenProductIsNotInStockOfCompetitor() {


        String productNameMocked = "MacBook";

        /**Существует ли продукт у конкурентов. Так как сюда должно прийти булево
         * значение после проверки, но ввиду того, что мы
         * используем заглушку класса ProductVerifier, поэтому будем использовать метод
         * {@link  org.mockito.stubbing.OngoingStubbing#thenReturn(Object)} */
        boolean existsProductInCompetitor = this.productVerifier
                .isCurrentlyInStockOfCompetitor(productNameMocked);


        /** В методе {@link  org.mockito.stubbing.OngoingStubbing#thenReturn(Object)},
         * мы определили значение, которое должно вернуться в переменную 'existsProductInCompetitor'*/
        when(existsProductInCompetitor)
                .thenReturn(false);

        /*Когда зависимости-заглушки готовы, можно начинать тестировать основной класс*/
        PricingService classUnderTest = new PricingService(this.productVerifier, this.productReporter);

        BigDecimal priceExpected = new BigDecimal("149.99");
        BigDecimal priceActual = classUnderTest.calculatePrice(productNameMocked);

        assertEquals(priceExpected, priceActual);

        /** Проверяем, что при тестировании основного класса и его контракта,
         * {@link PricingService#calculatePrice(String)} не будет вызываться метод из класса-заглушки
         * {@link ProductReporter#notify(String)}, так как в данной ситуации он и не должен быть вызван.*/
        verifyNoMoreInteractions(this.productReporter);
    }
}