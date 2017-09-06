package com.swimHelper.component.calories;

import com.swimHelper.model.Style;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Marcin Szalek on 04.09.17.
 */
public class CaloryBurnFactorFactoryTest {

    private final CaloryBurnFactorFactory sut = new CaloryBurnFactorFactory();

    @Test
    public void getBackstrokeFactor_returns8() throws Exception {
        //given
        //when
        double factor = sut.getFactor(Style.BACKSTROKE);
        //then
        assertThat(factor).isEqualTo(8);
    }

    @Test
    public void getFreestyleFactor_returns9() throws Exception {
        //given
        //when
        double factor = sut.getFactor(Style.FREESTYLE);
        //then
        assertThat(factor).isEqualTo(9);
    }

    @Test
    public void getBreaststrokeFactor_returns10() throws Exception {
        //given
        //when
        double factor = sut.getFactor(Style.BREASTSTROKE);
        //then
        assertThat(factor).isEqualTo(10);
    }

    @Test
    public void getButterflyFactor_returns11() throws Exception {
        //given
        //when
        double factor = sut.getFactor(Style.BUTTERFLY);
        //then
        assertThat(factor).isEqualTo(11);
    }
}