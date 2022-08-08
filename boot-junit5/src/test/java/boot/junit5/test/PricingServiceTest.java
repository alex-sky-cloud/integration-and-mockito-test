package boot.junit5.test;

import boot.junit5.service.PriceReporter;
import boot.junit5.service.PricingService;
import boot.junit5.service.ProductVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;



@ExtendWith(MockitoExtension.class) // register the Mockito extension
public class PricingServiceTest {

    @Mock //Instruct Mockito to mock this object
    private ProductVerifier mockedProductVerifier;

    /**
     * метод должен имитировать возврат дешевой цены товара,
     * когда продукт находится на складе конкурента
     */
    @Test
    public void shouldReturnCheapPriceWhenProductIsInStockOfCompetitor() {

        boolean isProductTheAirPods =
                mockedProductVerifier
                        .isCurrentlyInStockOfCompetitor("AirPods");

        when(isProductTheAirPods)
                .thenReturn(true); //Specify what boolean value to return

        PricingService cut = new PricingService(mockedProductVerifier);

        /*hamcrest*/
        assertThat(
                cut.calculatePrice("AirPods"),
                equalTo(new BigDecimal("99.99"))
        );

        BigDecimal expectedValue = new BigDecimal("99.99");

        BigDecimal actualValue = cut.calculatePrice("AirPods");

        assertEquals(expectedValue, actualValue );

    }

    @Mock
    private PriceReporter priceReporter;


    @Test
    public void shouldReturnCheapPriceWhenProductIsInStockOfCompetitorNew() {

        /**на созданной заглушке класса , вызываем метод, который производит проверку входного параметра
        * (мы не вызываем метод который определен в классе, а вызывается метод, сгенерированный
        * Mockito, на основе метаданных класса {@link ProductVerifier}*/
        boolean isAirPods = mockedProductVerifier.isCurrentlyInStockOfCompetitor("AirPods");

        /*Затем определяем статическое поведение обработанного результата. В строчке выше, мы можем задать,
        * любое значение, которое мы хотим проверить, но тогда его же и нужно будет передавать строчкой
        * ниже*/
        when(isAirPods).thenReturn(true);

        /*создаем объект класса, поведение свойств которого будем проверять. Здесь уже создание объекта
        * происходит без Mockito, то есть создается настоящий объект*/
        PricingService cut = new PricingService(mockedProductVerifier, priceReporter);


        BigDecimal expectedValue = new BigDecimal("99.99");

        /** и вот в этой строчке, происходит обращение
         *  к методу {@link PricingService#calculatePriceCheap(String)}  },
         *  но вот метод {@link ProductVerifier#isCurrentlyInStockOfCompetitor(String)},
         *  будет перехвачен Mockito и в этот метод, будет передано поведение,
         *  которое мы определили выше*/
        BigDecimal actualValue = cut.calculatePriceCheap("AirPods");

        assertEquals(expectedValue, actualValue );


        /** и вот в этой строчке, происходит обращение
         *  к методу {@link PricingService#calculatePriceCheap(String)}  } - для него выше определена
         *  заглушка,
         *  но вот метод {@link PriceReporter#notify(String)},
         *  будет перехвачен Mockito и в этот метод, будет передано поведение,
         *  которое мы определили выше*/
        verify(priceReporter).notify("AirPods"); //verify the interaction
    }


}
